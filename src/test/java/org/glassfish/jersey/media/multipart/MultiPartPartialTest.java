package org.glassfish.jersey.media.multipart;

import java.io.IOException;

import org.jvnet.mimepull.MIMEMessagePartial;
import org.jvnet.mimepull.MIMEPartPartial;
import org.jvnet.mimepull.MIMETestUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test MultiPartPartial.
 *
 * @author wayneng
 *
 */
public class MultiPartPartialTest {
    /**
     * TestUtil helper for creating random data.
     */
    private final MIMETestUtils mimeTestUtils = new MIMETestUtils();

    /**
     * Tests simple getter and setter for partial.
     *
     * @throws IOException from partial package.
     */
    @Test
    public void testFormDataMultiPartPartial() throws IOException {
        final FormDataMultiPartPartial formDataMultiPartPartial = new FormDataMultiPartPartial();
        boolean isPartial;
        boolean res;
        isPartial = formDataMultiPartPartial.getIsPartial();
        res = !isPartial;
        Assert.assertTrue(res, "formDataMultiPartPartial isPartial should be false");
        formDataMultiPartPartial.setIsPartial(false);
        isPartial = formDataMultiPartPartial.getIsPartial();
        res = !isPartial;
        Assert.assertTrue(res, "formDataMultiPartPartial isPartial should be false");
        formDataMultiPartPartial.setIsPartial(true);
        isPartial = formDataMultiPartPartial.getIsPartial();
        res = isPartial;
        Assert.assertTrue(res, "formDataMultiPartPartial isPartial should be true");
        formDataMultiPartPartial.close();
    }

    /**
     * Tests simple getter and setter for partial.
     *
     * @throws IOException from partial package.
     */
    @Test
    public void testFormDataBodyPartPartial() throws IOException {
        final FormDataBodyPartPartial formDataBodyPartPartial = new FormDataBodyPartPartial();
        boolean isPartial;
        boolean res;
        isPartial = formDataBodyPartPartial.getIsPartial();
        res = !isPartial;
        Assert.assertTrue(res, "formDataBodyPartPartial isPartial should be false");
        formDataBodyPartPartial.setIsPartial(false);
        isPartial = formDataBodyPartPartial.getIsPartial();
        res = !isPartial;
        Assert.assertTrue(res, "formDataBodyPartPartial isPartial should be false");
        formDataBodyPartPartial.setIsPartial(true);
        isPartial = formDataBodyPartPartial.getIsPartial();
        res = isPartial;
        Assert.assertTrue(res, "formDataBodyPartPartial isPartial should be true");
    }

    /**
     * Tests simple instanceof Partial and base.
     *
     * @throws IOException from partial package.
     * @throws Exception from MIMETestUtils.
     */
    @Test
    public void testBodyPartEntityPartial() throws IOException, Exception {
        MIMEMessagePartial mimeMessage = mimeTestUtils.createMIMEMessage(5);
        MIMEPartPartial mimePart = mimeTestUtils.createMIMEPart(mimeMessage);
        BodyPartEntityPartial bodyPartEntity = new BodyPartEntityPartial(mimePart);
        boolean res;
        res = bodyPartEntity instanceof BodyPartEntityPartial;
        Assert.assertTrue(res, "bodyPartEntity not instanceof BodyPartEntityPartial");
        res = bodyPartEntity instanceof BodyPartEntity;
        Assert.assertTrue(res, "bodyPartEntity not instanceof jersey BodyPartEntity");
        bodyPartEntity.close();
    }

    /**
     * Tests simple getter and setter for partial.
     *
     * @throws IOException from partial package.
     */
    @Test
    public void testBodyPartPartial() throws IOException {
        final BodyPartPartial bodyPartPartial = new BodyPartPartial();
        boolean isPartial;
        boolean res;
        isPartial = bodyPartPartial.getIsPartial();
        res = !isPartial;
        Assert.assertTrue(res, "bodyPartPartial isPartial should be false");
        bodyPartPartial.setIsPartial(false);
        isPartial = bodyPartPartial.getIsPartial();
        res = !isPartial;
        Assert.assertTrue(res, "bodyPartPartial isPartial should be false");
        bodyPartPartial.setIsPartial(true);
        isPartial = bodyPartPartial.getIsPartial();
        res = isPartial;
        Assert.assertTrue(res, "bodyPartPartial isPartial should be true");
    }

    /**
     * Tests simple getter and setter for partial.
     *
     * @throws IOException from partial package.
     */
    @Test
    public void testMultiPartPartial() throws IOException {
        final MultiPartPartial multiPartPartial = new MultiPartPartial();
        boolean isPartial;
        boolean res;
        isPartial = multiPartPartial.getIsPartial();
        res = !isPartial;
        Assert.assertTrue(res, "multiPartPartial isPartial should be false");
        multiPartPartial.setIsPartial(false);
        isPartial = multiPartPartial.getIsPartial();
        res = !isPartial;
        Assert.assertTrue(res, "multiPartPartial isPartial should be false");
        multiPartPartial.setIsPartial(true);
        isPartial = multiPartPartial.getIsPartial();
        res = isPartial;
        Assert.assertTrue(res, "multiPartPartial isPartial should be true");
        multiPartPartial.close();
    }

    /**
     * Tests Partial interface.
     *
     * @throws IOException from partial package.
     */
    @Test
    public void testPartialInterface() throws IOException {
        final Partial partial = new Partial() {
            private boolean isPartial = false;

            @Override
            public void setIsPartial(final boolean isPartial) {
                this.isPartial = isPartial;
            }

            @Override
            public boolean getIsPartial() {
                return isPartial;
            }
        };
        boolean isPartial;
        boolean res;
        isPartial = partial.getIsPartial();
        res = !isPartial;
        Assert.assertTrue(res, "partial isPartial should be false");
        partial.setIsPartial(false);
        isPartial = partial.getIsPartial();
        res = !isPartial;
        Assert.assertTrue(res, "partial isPartial should be false");
        partial.setIsPartial(true);
        isPartial = partial.getIsPartial();
        res = isPartial;
        Assert.assertTrue(res, "partial isPartial should be true");
    }
}
