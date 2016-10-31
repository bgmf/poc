package eu.dzim.shared.schedule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Schedule {
	long delay();
	
	TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
	
	long initialDelay() default 0;
}