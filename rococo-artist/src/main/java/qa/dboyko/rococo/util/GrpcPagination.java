package qa.dboyko.rococo.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.Nonnull;

public class GrpcPagination {
    private final int page;
    private final int size;

    public GrpcPagination(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public @Nonnull Pageable pageable() {
        return PageRequest.of(
                page,
                size
        );
    }
}