package qa.dboyko.rococo.extensions;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import qa.dboyko.rococo.apiservice.UserClient;
import qa.dboyko.rococo.extensions.annotations.User;
import qa.dboyko.rococo.model.TestData;
import qa.dboyko.rococo.model.UserdataJson;
import qa.dboyko.rococo.utils.RandomDataUtils;

import static qa.dboyko.rococo.extensions.TestMethodContextExtension.context;

@NonNullByDefault
public class UserExtension implements
        BeforeEachCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    public static final String DEFAULT_PASSWORD = System.getProperty("registration.password");
    @Nullable
    public static UserdataJson defaultUser;

    private final UserClient usersClient = new UserClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if (userAnno.newUser()) {
                        if ("".equals(userAnno.username())) {
                            setUser(createRandomUser());
                        }
                        else {
                            setUser(usersClient.createUser(userAnno.username(), DEFAULT_PASSWORD)
                                    .addTestData(new TestData(DEFAULT_PASSWORD)));
                        }
                    } else {
                        if (!"".equals(userAnno.username())) {
                            setUser(new UserdataJson(userAnno.username())
                                    .addTestData(new TestData(DEFAULT_PASSWORD)));
                        } else {
                            if (defaultUser == null) {
                                defaultUser = createRandomUser();
                                setUser(defaultUser);
                            } else {
                                setUser(defaultUser);
                            }
                        }
                    }

                });
    }

    private UserdataJson createRandomUser() {
        final String username = RandomDataUtils.randomUsername();
        final UserdataJson created = usersClient.createUser(username, DEFAULT_PASSWORD);

        TestData testData = new TestData(
                DEFAULT_PASSWORD
        );

        return created.addTestData(testData);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
            ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserdataJson.class);
    }

    @Override
    public UserdataJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
            ParameterResolutionException {
        return createdUser();
    }

    public static void setUser(UserdataJson testUser) {
        final ExtensionContext context = context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                testUser
        );
    }

    public static UserdataJson createdUser() {
        final ExtensionContext methodContext = context();
        return methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), UserdataJson.class);
    }
}

