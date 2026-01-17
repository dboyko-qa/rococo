package qa.dboyko.rococo.model;

import org.eclipse.jdt.annotation.NonNull;

import java.util.Date;
import java.util.List;

public record ErrorJson(@NonNull Date timestamp,
                        int status,
                        @NonNull List<String> errors) {
}
