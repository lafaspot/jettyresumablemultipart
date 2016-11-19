package org.jvnet.mimepull;

import java.io.File;

/**
 * Configuration for MIME message parsing and storing.
 *
 * This is a customized extension for constructor overload that passes in sizing info. Everything else is overridden because of private vars.
 * 
 * @author wayneng
 */
public class MIMEConfigYM extends MIMEConfig {

    /** DEFAULT CHUNK SIZE for buffer sizing. No change from base class. */
    private static final int DEFAULT_CHUNK_SIZE = 8192;
    /** DEFAULT_MEMORY_THRESHOLD for max buffer sizing. No change from base class. */
    private static final long DEFAULT_MEMORY_THRESHOLD = 1048576L;
    /** DEFAULT_FILE_PREFIX for MIME. No change from base class. */
    private static final String DEFAULT_FILE_PREFIX = "MIME";
    /** DEFAULT_MAX_MIME_SIZE for max size. */
    private static final int DEFAULT_MAX_MIME_SIZE = 45 * 1024 * 1024;

    // Parses the entire message eagerly
    /** No change from base class. */
    boolean parseEagerly;

    /** New field to support partial message. */
    boolean enablePartial;

    // Approximate Chunk size
    /** No change from base class. */
    int chunkSize;

    // Maximum in-memory data per attachment
    /** No change from base class. */
    long memoryThreshold;

    // temp Dir to store large files
    /** No change from base class. */
    File tempDir;
    /** No change from base class. */
    String prefix;
    /** No change from base class. */
    String suffix;

    int maxMIMESize;

    /**
     * Private Constructor. Added only enablePartial, and no other change from base class.
     *
     * @param parseEagerly boolean.
     * @param chunkSize int.
     * @param inMemoryThreshold long.
     * @param dir String.
     * @param prefix String.
     * @param suffix String.
     */
    private MIMEConfigYM(boolean parseEagerly, int chunkSize, long inMemoryThreshold, String dir, String prefix, String suffix,
            final boolean enablePartial, final int maxMIMESize) {
        this.parseEagerly = parseEagerly;
        this.chunkSize = chunkSize;
        this.memoryThreshold = inMemoryThreshold;
        this.prefix = prefix;
        this.suffix = suffix;
        this.enablePartial = enablePartial;
        this.maxMIMESize = maxMIMESize;
        setDirYM(dir);
    }

    /**
     * Constructor overload for YM. This is different from base class. Used in MIMEParserPartial.
     *
     * @param parseEagerly boolean.
     * @param chunkSize int.
     * @param inMemoryThreshold long.
     * @param enablePartial boolean.
     */
    public MIMEConfigYM(final boolean parseEagerly, final int chunkSize, final long inMemoryThreshold, final boolean enablePartial) {
        this(parseEagerly, chunkSize, inMemoryThreshold, null, DEFAULT_FILE_PREFIX, null, enablePartial, DEFAULT_MAX_MIME_SIZE);
    }

    /**
     * Default constructor. Added only enablePartial, and no other change from base class. Used in customized Jersey MultiPartPartial.
     */
    public MIMEConfigYM() {
        this(false, DEFAULT_CHUNK_SIZE, DEFAULT_MEMORY_THRESHOLD, null, DEFAULT_FILE_PREFIX, null, true, DEFAULT_MAX_MIME_SIZE);
    }

    /**
     * No change from base class.
     *
     * @return boolean.
     */
    @Override
    boolean isParseEagerly() {
        return this.parseEagerly;
    }

    /**
     * No change from base class.
     *
     * @param parseEagerly boolean.
     */
    @Override
    public void setParseEagerly(boolean parseEagerly) {
        this.parseEagerly = parseEagerly;
    }

    /**
     * No change from base class.
     *
     * @param int.
     */
    @Override
    int getChunkSize() {
        return this.chunkSize;
    }

    /**
     * No change from base class.
     *
     * @param chunkSize int.
     */
    @Override
    void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    /**
     * No change from base class.
     *
     * @return long.
     */
    @Override
    long getMemoryThreshold() {
        return this.memoryThreshold;
    }

    /**
     * If the attachment is greater than the threshold, it is written to the disk. No change from base class.
     *
     * @param memoryThreshold no of bytes per attachment if -1, then the whole attachment is kept in memory
     */
    @Override
    public void setMemoryThreshold(long memoryThreshold) {
        this.memoryThreshold = memoryThreshold;
    }

    /**
     * isOnlyMemory. No change from base class.
     *
     * @return boolean.
     */
    @Override
    boolean isOnlyMemory() {
        return this.memoryThreshold == -1L;
    }

    /**
     * getTempDir. No change from base class.
     *
     * @return File tempDir.
     */
    @Override
    File getTempDir() {
        return this.tempDir;
    }

    /**
     * getTempFilePrefix. No change from base class.
     *
     * @return String for prefix.
     */
    @Override
    String getTempFilePrefix() {
        return this.prefix;
    }

    /**
     * getTempFileSuffix. No change from base class.
     *
     * @return String for suffix.
     */
    @Override
    String getTempFileSuffix() {
        return this.suffix;
    }

    /**
     * setDirYM is for new File. This just overrides base class setDir, no new code.
     *
     * @param dir String.
     */
    public void setDirYM(String dir) {
        if (this.tempDir == null && dir != null && !dir.equals("")) {
            this.tempDir = new File(dir);
        }
    }

    /**
     * Validates if it can create temporary files. Otherwise, it stores attachment contents in memory.
     */
    @Override
    public void validate() {
        if (!isOnlyMemory()) {
            try {
                File tempFile = (tempDir == null) ? File.createTempFile(prefix, suffix) : File.createTempFile(prefix, suffix, tempDir);
                boolean deleted = tempFile.delete();
                if (!deleted) {
                    // logging goes here.
                }
            } catch (Exception ioe) {
                memoryThreshold = -1L; // whole attachment will be in-memory
            }
        }
    }

    /**
     * Get boolean enablePartial.
     *
     * @return boolean.
     */
    public boolean isEnablePartial() {
        return this.enablePartial;
    }

    /**
     * Set boolean enablePartial.
     *
     * @param enablePartial boolean.
     */
    public void setEnablePartial(final boolean enablePartial) {
        this.enablePartial = enablePartial;
    }

    /**
     * Get max MIME size.
     *
     * @return int.
     */
    public int getMaxMIMESize() {
        return this.maxMIMESize;
    }

    /**
     * Set max MIME size.
     *
     * @param maxMIMESize int.
     */
    public void setMaxMIMESize(final int maxMIMESize) {
        this.maxMIMESize = maxMIMESize;
    }
}
