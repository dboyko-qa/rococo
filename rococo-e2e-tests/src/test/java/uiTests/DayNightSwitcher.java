package uiTests;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.extensions.annotations.meta.WebTest;
import qa.dboyko.rococo.pages.MainPage;
import qa.dboyko.rococo.pages.constants.LightDarkMode;

@WebTest
@DisplayName("UI tests: light-dark mode switcher")
public class DayNightSwitcher {

    @Test
    @DisplayName("Verify that light mode can be turned on")
    void verifyLightMode() {
        Selenide.open(MainPage.url, MainPage.class)
                .getMainMenu()
                .switchDayNightMode(LightDarkMode.LIGHT)
                .verifyLightMode();
    }

    @Test
    @DisplayName("Verify that dark mode can be turned on")
    void verifyDarkMode() {
        Selenide.open(MainPage.url, MainPage.class)
                .getMainMenu()
                .switchDayNightMode(LightDarkMode.DARK)
                .verifyDarkMode();
    }
}
