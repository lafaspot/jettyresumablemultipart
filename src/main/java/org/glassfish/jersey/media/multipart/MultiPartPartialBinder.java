package org.glassfish.jersey.media.multipart;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.ws.rs.NameBinding;

/**
 * This Interface will be use to intercept the input stream. More details can be found
 * https://jersey.java.net/documentation/latest/user-guide.html#filters-and-interceptors
 *
 * @author nabanita
 *
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiPartPartialBinder {

}
