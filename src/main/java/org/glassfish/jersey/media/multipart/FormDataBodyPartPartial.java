package org.glassfish.jersey.media.multipart;

import javax.ws.rs.core.MediaType;

/**
 * Extends FormDataBodyPart with Partial interface. No other change.
 *
 * @author wayneng
 *
 */
public class FormDataBodyPartPartial extends FormDataBodyPart implements Partial {

    /**
     * boolean to indicate if MultiPart is partial.
     */
    private boolean isPartial = false;

    /**
     * Calls super.
     */
    public FormDataBodyPartPartial() {
        super();
    }

    /**
     * Calls super with filename.
     *
     * @param fileNameFix is there a fileNameFix.
     */
    public FormDataBodyPartPartial(final boolean fileNameFix) {
        super(fileNameFix);
    }

    /**
     * Calls super with MediaType.
     *
     * @param mediaType sets MediaType.
     */
    public FormDataBodyPartPartial(final MediaType mediaType) {
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
