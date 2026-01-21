package qa.dboyko.rococo.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static qa.dboyko.rococo.pages.constants.TextConstants.*;

public class MainPage extends BasePage<MainPage> {
    public final static String url = CFG.frontUrl();

    private final SelenideElement header = $("p");
    private final SelenideElement gridElement = $("ul.grid");
    private final SelenideElement paintingBanner = gridElement.$("a[href='/painting']");
    private final SelenideElement artistBanner = gridElement.$("a[href='/artist']");
    private final SelenideElement museumBanner = gridElement.$("a[href='/museum']");
    private final SelenideElement paintingBannerCaption = paintingBanner.$("div");
    private final SelenideElement artistBannerCaption = artistBanner.$("div");
    private final SelenideElement museumBannerCaption = museumBanner.$("div");

    @Step("Verify content of the main page")
    public MainPage verifyContent() {
        header.shouldBe(visible).shouldHave(text(MAIN_PAGE_HEADER));
        paintingBanner.shouldBe(visible, clickable);
        paintingBannerCaption.shouldBe(visible).shouldHave(text(PAINTINGS_HEADER));
        artistBanner.shouldBe(visible, clickable);
        artistBannerCaption.shouldBe(visible).shouldHave(text(ARTISTS_HEADER));
        museumBanner.shouldBe(visible, clickable);
        museumBannerCaption.shouldBe(visible).shouldHave(text(MUSEUMS_HEADER));
        return this;
    }

}
