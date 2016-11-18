package org.glassfish.jersey.media.multipart;

/**
 * Interface for Partial multiparts.
 *
 * @author wayneng
 *
 */
public interface Partial {
    /**
     * Set if Object is Partial.
     *
     * @param isPartial if part is Partial.
     */
    void setIsPartial(final boolean isPartial);

    /**
     * Gets if Object is Partial.
     *
     * @return boolean.
     */
    boolean getIsPartial();
}
