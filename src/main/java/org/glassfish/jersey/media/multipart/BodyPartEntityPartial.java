package org.glassfish.jersey.media.multipart;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jvnet.mimepull.MIMEPart;
import org.jvnet.mimepull.MIMEPartPartial;


/**
 * Proxy class representing the entity of a {@link BodyPart} when a {@link MultiPart} entity is received and parsed.
 * 
 * Its primary purpose is to provide an input stream to retrieve the actual data. However, it also transparently deals with storing the data in a
 * temporary disk file, if it is larger than a configurable size; otherwise, the data is stored in memory for faster processing.
 *
 * This is a direct extension of BodyPartEntity, except the MIMEPart is changed to MIMEPartPartial. All methods are overridden because of private
 * variables.
 *
 * @author wayneng
 */
public class BodyPartEntityPartial extends BodyPartEntity implements Closeable {

    /**
     * Partial MIMEPart.
     */
    private final MIMEPartPartial mimePart;

    /**
     * Constructs a new {@code BodyPartEntity} with a {@link MIMEPart}.
     *
     * @param mimePart MIMEPart containing the input stream of this body part entity.
     */
    public BodyPartEntityPartial(final MIMEPartPartial mimePart) {
        super(null);
        this.mimePart = mimePart;
    }

    /**
     * Gets the input stream of the raw bytes of this body part entity.
     *
     * @return the input stream of the body part entity.
     */
    @Override
    public InputStream getInputStream() {
        return mimePart.read();
    }

    /**
     * Cleans up temporary file(s), if any were utilized.
     */
    @Override
    public void cleanup() {
        mimePart.close();
    }

    /**
     * Defers to {@link #cleanup}.
     */
    @Override
    public void close() throws IOException {
        cleanup();
    }

    /**
     * Move the contents of the underlying {@link java.io.InputStream} or {@link java.io.File} to the given file.
     *
     * @param file destination file.
     */
    @Override
    public void moveTo(final File file) {
        mimePart.moveTo(file);
    }
}
