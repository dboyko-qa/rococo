package qa.dboyko.rococo.extensions.annotations.meta;

import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;
import qa.dboyko.rococo.extensions.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({
        BrowserExtension.class,
        AllureJunit5.class,
        UserExtension.class,
        ApiLoginExtension.class,
        MuseumExtension.class,
        ArtistExtension.class,
        PaintingExtension.class,
})
public @interface WebTest {
}

