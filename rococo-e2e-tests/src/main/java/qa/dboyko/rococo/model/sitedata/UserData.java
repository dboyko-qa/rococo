package qa.dboyko.rococo.model.sitedata;

import qa.dboyko.rococo.model.TestData;
import qa.dboyko.rococo.model.UserdataJson;

import static qa.dboyko.rococo.utils.ConversionUtils.jpegToString;
import static qa.dboyko.rococo.utils.RandomDataUtils.*;

public record UserData(String username,
                       String firstname,
                       String lastname,
                       String avatar,
                       TestData testData
) {

    public UserData update() {
        return new UserData(
                this.username,
                getRandomFirstname(),
                getrandomLastname(),
                getRandomUserFile().getPath(),
                this.testData
        );
    }

    public UserdataJson toUserdataJson() {
        return new UserdataJson(
                "",
                this.username,
                this.firstname,
                this.lastname,
                jpegToString(this.avatar),
                this.testData
        );
    }
}
