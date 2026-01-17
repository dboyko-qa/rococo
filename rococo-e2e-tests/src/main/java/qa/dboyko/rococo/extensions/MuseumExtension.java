package qa.dboyko.rococo.extensions;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.data.domain.Page;
import qa.dboyko.rococo.apiservice.GeoClient;
import qa.dboyko.rococo.apiservice.MuseumClient;
import qa.dboyko.rococo.extensions.annotations.Museum;
import qa.dboyko.rococo.model.GeoJson;
import qa.dboyko.rococo.model.MuseumJson;

import java.util.Objects;

import static qa.dboyko.rococo.enums.Country.getRandomCountry;
import static qa.dboyko.rococo.extensions.TestMethodContextExtension.context;
import static qa.dboyko.rococo.utils.ConversionUtils.jpegToString;
import static qa.dboyko.rococo.utils.RandomDataUtils.*;

public class MuseumExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);

    private final MuseumClient museumClient = new MuseumClient();
    private final GeoClient geoClient = new GeoClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Museum.class)
                .ifPresent(museumAnno -> {
                            Page<MuseumJson> allMuseums = Page.empty();
                            MuseumJson resultMuseum = null;
                            if (!museumAnno.createNew()) {
                                allMuseums = museumClient.allMuseums(null, null);
                                if (allMuseums.getSize() > 0) {
                                    resultMuseum = allMuseums.get().findFirst().get();
                                }
                            }
                            if (allMuseums.isEmpty()) {
                                String museumName = "".equals(museumAnno.title())
                                        ? generateMuseumName()
                                        : museumAnno.title();
                                String country = "".equals(museumAnno.country())
                                        ? getRandomCountry().getName()
                                        : museumAnno.country();
                                String city = "".equals(museumAnno.city())
                                        ? randomCity()
                                        : museumAnno.city();
                                String description = "".equals(museumAnno.description())
                                        ? generateDescription()
                                        : museumAnno.description();

                                MuseumJson museum = new MuseumJson(
                                        null,
                                        museumName,
                                        description,
                                        jpegToString(Objects.requireNonNull(this.getClass().getClassLoader().getResource("images/museums/louvre.jpg")).getPath()),
                                        new GeoJson(city, geoClient.getCountryByName(country)));
                                resultMuseum = museumClient.createMuseum(museum);
                            }

                            context.getStore(NAMESPACE).put(
                                    context.getUniqueId(),
                                    resultMuseum
                            );
                        }
                );

    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
            ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(MuseumJson.class);
    }

    @Override
    public MuseumJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
            ParameterResolutionException {
        return createdMuseums();
    }

    public static MuseumJson createdMuseums() {
        final ExtensionContext methodContext = context();
        return methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), MuseumJson.class);
    }

}
