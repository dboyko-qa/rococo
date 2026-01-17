package qa.dboyko.rococo.model;

import jakarta.annotation.Nonnull;
import org.eclipse.jdt.annotation.NonNullByDefault;

@NonNullByDefault
public record TestData(
        @Nonnull String password
) {
}
