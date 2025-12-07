package qa.dboyko.rococo.model;

import jakarta.annotation.Nonnull;

public record ErrorJson(@Nonnull String type,
                        @Nonnull String title,
                        int status,
                        @Nonnull String error,
                        @Nonnull String instance) {

}
