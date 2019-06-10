package poc.fwk.logger.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for enable or disable automatic loggin on classes or methods.
 *
 * @author ruben.asensio
 *
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Logger {

	/**
	 * Enable or disable automatic loggin
	 */
	boolean enabled() default false;
}
