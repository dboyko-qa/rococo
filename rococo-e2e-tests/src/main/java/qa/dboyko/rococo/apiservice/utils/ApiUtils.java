package qa.dboyko.rococo.apiservice.utils;

import com.dboyko.rococo.grpc.PageInfo;
import org.springframework.data.domain.Pageable;

public class ApiUtils {
    public static PageInfo toGrpcPageInfo(Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) {
            throw new IllegalArgumentException("Pageable must be paged");
        }

        return PageInfo.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize())
                .build();
    }
}
