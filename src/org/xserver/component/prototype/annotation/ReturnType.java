package org.xserver.component.prototype.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The annotation <code>ReturnType</code> used to add before the name of the method or
 * class in the external access interface, if you add the annotation, the result you access
 * the interface will be generated by<code>ReturnType<code> annotation value generated,
 * and the result will be deal with the processor, as 'json' will returned deal with json
 * otherwise in accordance with the system default access rule
 * 
 * @author Idol
 * @since  2016/02/22
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface ReturnType {
	String value() default "JSON";
}