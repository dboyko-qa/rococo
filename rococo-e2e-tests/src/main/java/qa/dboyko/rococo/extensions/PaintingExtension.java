package qa.dboyko.rococo.extensions;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.data.domain.Page;
import qa.dboyko.rococo.apiservice.grpc.ArtistGrpcClient;
import qa.dboyko.rococo.apiservice.grpc.MuseumGrpcClient;
import qa.dboyko.rococo.apiservice.grpc.PaintingGrpcClient;
import qa.dboyko.rococo.extensions.annotations.Painting;
import qa.dboyko.rococo.extensions.annotations.TestPainting;
import qa.dboyko.rococo.model.PaintingJson;

import java.util.Objects;

import static qa.dboyko.rococo.extensions.TestMethodContextExtension.context;
import static qa.dboyko.rococo.utils.ConversionUtils.jpegToString;
import static qa.dboyko.rococo.utils.RandomDataUtils.*;

public class PaintingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PaintingExtension.class);

    private final PaintingGrpcClient paintingGrpcClient = new PaintingGrpcClient();
    private final MuseumGrpcClient museumGrpcClient = new MuseumGrpcClient();
    private final ArtistGrpcClient artistGrpcClient = new ArtistGrpcClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Painting.class)
                .ifPresent(paintingAnno -> {
                            Page<PaintingJson> allPaintings = Page.empty();
                            PaintingJson resultPainting = null;
                            if (!paintingAnno.createNew()) {
                                allPaintings = paintingGrpcClient.allPaintings(null, null);
                                if (allPaintings.getSize() > 0) {
                                    resultPainting = allPaintings.get().findFirst().get();
                                }
                            }
                            if (allPaintings.isEmpty()) {
                                String paintingName = "".equals(paintingAnno.title())
                                        ? generatePaintingName()
                                        : paintingAnno.title();
                                String description = "".equals(paintingAnno.description())
                                        ? generateDescription()
                                        : paintingAnno.description();

                                PaintingJson painting = new PaintingJson(
                                        "",
                                        paintingName,
                                        description,
                                        jpegToString(Objects.requireNonNull(getRandomPaintingFile()).getPath()),
                                        museumGrpcClient.getRandomMuseum(),
                                        artistGrpcClient.getRandomArtist());
                                resultPainting = paintingGrpcClient.createPainting(painting);
                            }

                            context.getStore(NAMESPACE).put(
                                    context.getUniqueId(),
                                    resultPainting
                            );
                        }
                );

    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
            ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(PaintingJson.class)
                && parameterContext.isAnnotated(TestPainting.class);
    }

    @Override
    public PaintingJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
            ParameterResolutionException {
        return createdPainting();
    }

    public static PaintingJson createdPainting() {
        final ExtensionContext methodContext = context();
        return methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), PaintingJson.class);
    }

}
