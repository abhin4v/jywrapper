package net.abhinavsarkar.jywrapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.abhinavsarkar.jywrapper.PyMethodType;

@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PyMethod {
	PyMethodType type();
	String method() default "";
}
