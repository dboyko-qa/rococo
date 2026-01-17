package qa.dboyko.rococo.config;


import jakarta.annotation.Nonnull;

enum LocalConfig implements Config {
    INSTANCE;

    @Nonnull
    @Override
    public String registrationPassword() {
        return System.getProperty("registration.password");
    }

    @Nonnull
    @Override
    public String frontUrl() {
        return "http://127.0.0.1:3000";
    }

    @Override
    public @Nonnull String authUrl() {
        return "http://127.0.0.1:9000";
    }

    @Nonnull
    @Override
    public String authJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/rococo-auth";
    }

    @Nonnull
    @Override
    public String gatewayUrl() {
        return "http://127.0.0.1:8080";
    }

    @Nonnull
    @Override
    public String userdataUrl() {
        return "http://127.0.0.1:8072";
    }

    @Nonnull
    @Override
    public String userdataJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/rococo-userdata";
    }

    @Nonnull
    @Override
    public String artistUrl() {
        return "http://127.0.0.1:8093";
    }

    @Nonnull
    @Override
    public String artistJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/rococo-artist";
    }

    @Nonnull
    @Override
    public String geoUrl() {
        return "http://127.0.0.1:8076";
    }

    @Nonnull
    @Override
    public String geoJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/rococo-geo";
    }

    @Nonnull
    @Override
    public String museumUrl() {
        return "http://127.0.0.1:8078";
    }

    @Nonnull
    @Override
    public String museumJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/rococo-museum";
    }

    @Nonnull
    @Override
    public String paintingUrl() {
        return "http://127.0.0.1:8070";
    }

    @Nonnull
    @Override
    public String paintingJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/rococo-painting";
    }

    @Nonnull
    @Override
    public String screenshotBaseDir() {
        return "screenshots/local";
    }

    @Nonnull
    @Override
    public String allureDockerUrl() {
        return "http://allure:5050";
    }
}
