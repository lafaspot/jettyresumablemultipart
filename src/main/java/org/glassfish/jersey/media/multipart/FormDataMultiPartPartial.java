package org.glassfish.jersey.media.multipart;

/**
 * Extends FormDataMultiPart and uses Partial interface.
 *
 * @author wayneng
 *
 */
public class FormDataMultiPartPartial extends FormDataMultiPart implements Partial {
    /**
     * boolean to indicate if MultiPart is partial.
     */
    private boolean isPartial = false;

    /**
     * Just calls super.
     */
    public FormDataMultiPartPartial() {
        super();
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
