package org.jvnet.mimepull;

import java.nio.ByteBuffer;

/**
 * Customized class for partial message enabling. Nothing else.
 *
 * @author wayneng
 *
 */
abstract class MIMEEventPartial extends MIMEEvent {
    @Override
    abstract EVENT_TYPE getEventType();


    /**
     * Customized class for partial message enabling. Nothing else.
     *
     * @author wayneng
     *
     */
    static final class ContentPartial extends MIMEEventPartial {
        /** No change from base class. */
        private final ByteBuffer bufPartial;
        /** For partial message enabling. */
        private final boolean isPartial;

        /**
         * No change from base class except partial enabling.
         *
         * @param buf ByteBuffer.
         */
        ContentPartial(final ByteBuffer buf) {
            this(buf, false);
        }

        /**
         * No change from base class except partial enabling.
         *
         * @param buf ByteBuffer.
         * @param isPartial boolean.
         */
        ContentPartial(final ByteBuffer buf, final boolean isPartial) {
            this.bufPartial = buf;
            this.isPartial = isPartial;
        }

        /**
         * No change from base class.
         */
        @Override
        EVENT_TYPE getEventType() {
            return EVENT_TYPE.CONTENT;
        }

        /**
         * No change from base class.
         *
         * @return ByteBuffer.
         */
        ByteBuffer getData() {
            return bufPartial;
        }

        /**
         * getter for isPartial.
         *
         * @return boolean.
         */
        boolean getIsPartial() {
            return isPartial;
        }
    }

    /**
     * Customized class for partial message enabling.
     *
     * @author wayneng
     *
     */
    static final class HeadersPartial extends MIMEEvent {
        /** No change from base class. */
        private final InternetHeaders ih;
        /** For partial message enabling. */
        private final boolean isPartial;

        /**
         * No change from base class.
         *
         * @param ih InternetHeaders.
         */
        HeadersPartial(final InternetHeaders ih) {
            this(ih, false);
        }

        /**
         * Constructor for partial Header.
         *
         * @param ih InternetHeaders.
         * @param isPartial boolean.
         */
        HeadersPartial(final InternetHeaders ih, final boolean isPartial) {
            this.ih = ih;
            this.isPartial = isPartial;
        }

        /**
         * No change from base class.
         */
        @Override
        EVENT_TYPE getEventType() {
            return EVENT_TYPE.HEADERS;
        }

        /**
         * No change from base class.
         *
         * @return InternetHeaders.
         */
        InternetHeaders getHeaders() {
            return ih;
        }

        /**
         * getter for isPartial.
         *
         * @return boolean.
         */
        boolean getIsPartial() {
            return isPartial;
        }
    }

}
