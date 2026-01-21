package qa.dboyko.rococo.extensions;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.data.domain.Page;
import qa.dboyko.rococo.apiservice.grpc.ArtistGrpcClient;
import qa.dboyko.rococo.apiservice.grpc.PaintingGrpcClient;
import qa.dboyko.rococo.extensions.annotations.Artist;
import qa.dboyko.rococo.extensions.annotations.TestArtist;
import qa.dboyko.rococo.model.ArtistJson;

import java.util.Objects;

import static qa.dboyko.rococo.extensions.TestMethodContextExtension.context;
import static qa.dboyko.rococo.utils.ConversionUtils.jpegToString;
import static qa.dboyko.rococo.utils.RandomDataUtils.*;

public class ArtistExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);

    private final ArtistGrpcClient artistGrpcClient = new ArtistGrpcClient();
    private final PaintingGrpcClient paintingGrpcClient = new PaintingGrpcClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Artist.class)
                .ifPresent(artistAnno -> {
                            Page<ArtistJson> allArtists = Page.empty();
                            ArtistJson resultArtist = null;
                            if (!artistAnno.createNew()) {
                                allArtists = artistGrpcClient.allArtists(null, null);
                                if (allArtists.getSize() > 0) {
                                    ArtistJson selectedArtist = allArtists.get().findFirst().get();
                                    resultArtist = new ArtistJson(
                                            selectedArtist.id(),
                                            selectedArtist.name(),
                                            selectedArtist.biography(),
                                            selectedArtist.photo(),
                                            paintingGrpcClient.getPaintingsForArtist(null, selectedArtist.id()).toList()
                                    );
                                }
                            }
                            if (allArtists.isEmpty()) {
                                String artistName = "".equals(artistAnno.name())
                                        ? generateArtistName()
                                        : artistAnno.name();
                                String biography = "".equals(artistAnno.biography())
                                        ? generateDescription()
                                        : artistAnno.biography();

                                ArtistJson artist = new ArtistJson(
                                        "",
                                        artistName,
                                        biography,
                                        jpegToString(Objects.requireNonNull(getRandomArtistFile()).getPath()),
                                null);
                                resultArtist = artistGrpcClient.createArtist(artist);
                            }

                            context.getStore(NAMESPACE).put(
                                    context.getUniqueId(),
                                    resultArtist
                            );
                        }
                );

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
            ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(ArtistJson.class)
                && parameterContext.isAnnotated(TestArtist.class);
    }

    @Override
    public ArtistJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
            ParameterResolutionException {
        return createdArtist();
    }

    public static ArtistJson createdArtist() {
        final ExtensionContext methodContext = context();
        return methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), ArtistJson.class);
    }

}
