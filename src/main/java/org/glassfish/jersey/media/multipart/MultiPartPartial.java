package org.glassfish.jersey.media.multipart;

import java.io.Closeable;

import javax.ws.rs.core.MediaType;

/**
 * Extends MultiPart with Partial interface.
 *
 * @author wayneng
 *
 */
public class MultiPartPartial extends MultiPart implements Closeable, Partial {

    /**
     * boolean to indicate if MultiPart is partial.
     */
    private boolean isPartial = false;

    /**
     * Just calls super.
     */
    public MultiPartPartial() {
        super();
    }

    /**
     * Just calls super.
     *
     * @param mediaType sets mediaType.
     */
    public MultiPartPartial(final MediaType mediaType) {
        super(mediaType);
    }

    /**
     * setIsPartial setter.
     *
     * @param isPartial boolean.
     */
    @Override
    public void setIsPartial(final boolean isPartial) {
        this.isPartial = isPartial;
    }

    /**
     * getIsPartial getter.
     *
     * @return boolean.
     */
    @Override
    public boolean getIsPartial() {
        return isPartial;
    }
}
