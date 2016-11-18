package org.glassfish.jersey.media.multipart;

import javax.ws.rs.core.MediaType;

/**
 * This is a direct extension of BodyPart, and only adds Partial support.
 *
 * @author wayneng
 *
 */
public class BodyPartPartial extends BodyPart implements Partial {

    /**
     * boolean to indicate if BodyPart is partial.
     */
    private boolean isPartial = false;

    /**
     * Calls super.
     */
    public BodyPartPartial() {
        super();
    }

    /**
     * Calls super BodyPart with mediaType.
     *
     * @param mediaType set the MediaType.
     */
    public BodyPartPartial(final MediaType mediaType) {
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
