package qa.dboyko.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.qameta.allure.internal.shadowed.jackson.annotation.JsonIgnore;
import io.qameta.allure.internal.shadowed.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import qa.dboyko.rococo.model.sitedata.UserData;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserdataJson(
        @JsonProperty("id")
        String id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("lastname")
        String lastname,
        @JsonProperty("avatar")
        String avatar,
        @JsonIgnore
        TestData testData) {

    @Nonnull
    public UserdataJson(@Nonnull String username) {
        this("", username, "", "", "", null);
    }

    @Nonnull
    public UserdataJson(@Nonnull String username, @Nonnull TestData testData) {
        this("", username, "", "", "", testData);
    }

    @Nonnull
    public UserdataJson addTestData(@Nonnull TestData testData) {
        return new UserdataJson(
                id,
                username,
                firstname,
                lastname,
                avatar,
                testData
        );
    }

    @Nonnull
    public UserData toUserData() {
        return new UserData(
                this.username,
                this.firstname,
                this.lastname,
                "",
                this.testData
        );
    }
}
