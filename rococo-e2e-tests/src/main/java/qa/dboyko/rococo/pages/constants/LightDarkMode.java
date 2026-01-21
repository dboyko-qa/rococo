package qa.dboyko.rococo.pages.constants;

import lombok.Getter;

import static qa.dboyko.rococo.pages.constants.TextConstants.SWITCHER_TOGGLE_DARK;
import static qa.dboyko.rococo.pages.constants.TextConstants.SWITCHER_TOGGLE_LIGHT;

public enum LightDarkMode {
    LIGHT(SWITCHER_TOGGLE_DARK),
    DARK(SWITCHER_TOGGLE_LIGHT);

    @Getter
    private final String switcherTitle;

    LightDarkMode(String switcherTitle) {
        this.switcherTitle = switcherTitle;
    }
}
