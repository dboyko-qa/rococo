package qa.dboyko.rococo.extensions;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;
import qa.dboyko.rococo.api.core.ThreadSafeCookieFilter;
import qa.dboyko.rococo.apiservice.AuthClient;
import qa.dboyko.rococo.config.Config;
import qa.dboyko.rococo.extensions.annotations.ApiLogin;
import qa.dboyko.rococo.extensions.annotations.Token;
import qa.dboyko.rococo.model.TestData;
import qa.dboyko.rococo.model.UserdataJson;
import qa.dboyko.rococo.pages.MainPage;

@NonNullByDefault
public class ApiLoginExtension implements BeforeTestExecutionCallback, ParameterResolver {

    private static final Config CFG = Config.getInstance();
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);

    private final AuthClient authClient = new AuthClient();
    private final boolean setupBrowser;

    private ApiLoginExtension(boolean setupBrowser) {
        this.setupBrowser = setupBrowser;
    }

    public ApiLoginExtension() {
        this.setupBrowser = true;
    }

    public static ApiLoginExtension rest() {
        return new ApiLoginExtension(false);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(apiLogin -> {

                    final UserdataJson userToLogin;
                    final UserdataJson userFromUserExtension = UserExtension.createdUser();
                    if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())) {
                        if (userFromUserExtension == null) {
                            throw new IllegalStateException("@User must be present in case that @ApiLogin is empty!");
                        }
                        userToLogin = userFromUserExtension;
                    } else {
                        UserdataJson fakeUser = new UserdataJson(
                                apiLogin.username(),
                                new TestData(
                                        apiLogin.password()
                                )
                        );
                        if (userFromUserExtension != null) {
                            throw new IllegalStateException("@User must not be present in case that @ApiLogin contains username or password!");
                        }
                        UserExtension.setUser(fakeUser);
                        userToLogin = fakeUser;
                    }

                    final String token = authClient.login(
                            userToLogin.username(),
                            userToLogin.testData().password()
                    );
                    setToken(token);

                    if (setupBrowser) {
                        Selenide.open(CFG.frontUrl() + "/hermitage.jpg");
                        Selenide.localStorage().setItem("id_token", getToken());
                        WebDriverRunner.getWebDriver().manage().addCookie(
                                getJsessionIdCookie()
                        );
                        Selenide.open(CFG.frontUrl(), MainPage.class)
                                .getMainMenu()
                                .verifyLoginButton()
                                .login();
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(String.class)
                && parameterContext.isAnnotated(Token.class);
    }

    @Override
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return "Bearer " + getToken();
    }

    public static void setToken(String token) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("token", token);
    }

    public static String getToken() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("token", String.class);
    }

    public static void setCode(String code) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("code", code);
    }

    public static String getCode() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("code", String.class);
    }

    public static Cookie getJsessionIdCookie() {
        return new Cookie(
                "JSESSIONID",
                ThreadSafeCookieFilter.INSTANCE.cookieValue("JSESSIONID")
        );
    }
}
