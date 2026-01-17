package qa.dboyko.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaintingJson(
    @JsonProperty("id")
    String id,
    @JsonProperty("title")
    String title,
    @JsonProperty("description")
    String description,
    @JsonProperty("content")
    String content,
    @JsonProperty("museum")
    MuseumJson museum,
    @JsonProperty("artist")
    ArtistJson artist
) {

}
