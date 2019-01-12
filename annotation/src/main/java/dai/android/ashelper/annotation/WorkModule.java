package dai.android.ashelper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface WorkModule {
    /**
     * can this module work? default true
     *
     * @return
     */
    boolean canWork() default true;

    /**
     * the name of author
     *
     * @return
     */
    String author() default "";
}
