package qa.dboyko.rococo.enums;

import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;

public enum Country {
    Russia("Russia"),
    France("France"),
    Spain("Spain"),
    Estonia("Estonia"),
    VaticanCity("Vatican City");

    @Getter
    private String name;

    Country(String name) {
        this.name = name;
    }


    public static Country getRandomCountry() {
        return Country.values()[ThreadLocalRandom.current().nextInt(Country.values().length)];
    }

}
