package qa.dboyko.rococo.config;


import jakarta.annotation.Nonnull;

enum DockerConfig implements Config {
  INSTANCE;

  @Nonnull
  @Override
  public String registrationPassword() {
    return "12345";
  }

  @Nonnull
  @Override
  public String frontUrl() {
    return "http://frontend.niffler.dc/";
  }

  @Nonnull
  @Override
  public String authUrl() {
    return "http://auth.niffler.dc:9000/";
  }

  @Nonnull
  @Override
  public String authJdbcUrl() {
    return "jdbc:postgresql://niffler-all-db:5432/niffler-auth";
  }

  @Nonnull
  @Override
  public String gatewayUrl() {
    return "http://gateway.niffler.dc:8090/";
  }

  @Nonnull
  @Override
  public String userdataUrl() {
    return "http://userdata.niffler.dc:8089/";
  }

  @Nonnull
  @Override
  public String userdataJdbcUrl() {
    return "jdbc:postgresql://niffler-all-db:5432/niffler-userdata";
  }

    @Nonnull
    @Override
    public String artistUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String artistJdbcUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String geoUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String geoJdbcUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String museumUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String museumJdbcUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String paintingUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String paintingJdbcUrl() {
        return "";
    }


    @Nonnull
  @Override
  public String screenshotBaseDir() {
    return "screenshots/selenoid/";
  }

  @Nonnull
  @Override
  public String allureDockerUrl() {
    final String allureDockerApiFromEnv = System.getenv("ALLURE_DOCKER_API");
    return allureDockerApiFromEnv != null
        ? allureDockerApiFromEnv
        : "http://allure:5050/";
  }
}
