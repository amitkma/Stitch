package amitkma.stitch.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create by Amit Kumar on 20/5/17
 * Email : mr.doc10jl96@gmail.com
 * Company : Dot Wave Ltd.
 */

/**
 * Annotates a method to always execute on new thread.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface CallOnNewThread {

}
