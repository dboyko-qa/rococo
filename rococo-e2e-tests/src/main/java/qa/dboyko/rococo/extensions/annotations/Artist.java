package qa.dboyko.rococo.extensions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Artist {
    String name() default "";
    String biography() default "";
    int paintingsCount() default 0;

    boolean createNew() default false;
}
