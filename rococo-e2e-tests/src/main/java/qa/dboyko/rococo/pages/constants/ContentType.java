package qa.dboyko.rococo.pages.constants;

import lombok.Getter;

import static qa.dboyko.rococo.pages.constants.TextConstants.*;

public enum ContentType {
    MUSEUM(ADD_MUSEUM_TEXT, MUSEUMS_HEADER, "//li//img[contains(@alt,\"%s\")]"),
    ARTIST(ADD_ARTIST_TEXT, ARTISTS_HEADER, "//li//span[text()=\"%s\"]"),
    PAINTINGS(ADD_PAINTING_TEXT, PAINTINGS_HEADER, "//li//div[text()=\"%s\"]");

    @Getter
    private final String textAddEntity;

    @Getter
    private final String textHeader;

    @Getter
            private final String optionLocator;

    ContentType(String textAddEntity,
                String textHeader,
                String optionsLocator) {
        this.textAddEntity = textAddEntity;
        this.textHeader = textHeader;
        this.optionLocator = optionsLocator;
    }
}
