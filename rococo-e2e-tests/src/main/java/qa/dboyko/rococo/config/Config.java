package qa.dboyko.rococo.config;

import jakarta.annotation.Nonnull;

public interface Config {

    @Nonnull
    static Config getInstance() {
        return "docker".equals(System.getProperty("test.env"))
                ? DockerConfig.INSTANCE
                : LocalConfig.INSTANCE;
    }

    String projectId = "rococo";

    default String testDatabaseUsername() {
        return "postgres";
    }

    default String testDatabasePassword() {
        return "secret";
    }

    @Nonnull
    String registrationPassword();

    @Nonnull
    String frontUrl();

    @Nonnull
    String authUrl();

    @Nonnull
    String authJdbcUrl();

    @Nonnull
    String gatewayUrl();

    @Nonnull
    String userdataUrl();

    @Nonnull
    String userdataJdbcUrl();

    @Nonnull
    String artistUrl();

    @Nonnull
    String artistJdbcUrl();

    @Nonnull
    String geoUrl();

    @Nonnull
    String geoJdbcUrl();

    @Nonnull
    String museumUrl();

    @Nonnull
    String museumJdbcUrl();

    @Nonnull
    String paintingUrl();

    @Nonnull
    String paintingJdbcUrl();


    @Nonnull
    default String ghUrl() {
        return "https://api.github.com/";
    }

    @Nonnull
    String screenshotBaseDir();

    @Nonnull
    String allureDockerUrl();
}
