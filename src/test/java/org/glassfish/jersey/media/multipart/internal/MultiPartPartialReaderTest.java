package org.glassfish.jersey.media.multipart.internal;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Provider;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntityPartial;
import org.glassfish.jersey.media.multipart.FormDataBodyPartPartial;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPartPartial;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.Partial;
import org.glassfish.jersey.message.internal.MessageBodyFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * MultiPartPartialReaderTest used to test MultiPartPartial.
 *
 * @author wayneng
 *
 */
public class MultiPartPartialReaderTest {
    /** Provider<MessageBodyWorker> of type MessageBodyFactory mock. */
    @Mock
    private Provider<MessageBodyFactory> provider;
    /** MultiPartPartialReader class to test and mock inject. */
    @InjectMocks
    private MultiPartPartialReader multiPartPartialReader;
    /** MultiPartPartialReader class to test and mock inject with partial enabling. */
    /** MessageBodyFactory mock. */
    @Mock
    private MessageBodyFactory messageBodyFactory;

    /** init mock inject. */
    @BeforeTest
    @SuppressWarnings("unchecked")
    public void init() {
        multiPartPartialReader = new MultiPartPartialReader();
        MockitoAnnotations.initMocks(this);
        when(provider.get()).thenReturn(messageBodyFactory);
        when(messageBodyFactory.getMessageBodyReader(any(Class.class), any(Type.class), any(Annotation[].class), any(MediaType.class)))
                .thenReturn(multiPartPartialReader);
    }

    /**
     * test instance.
     *
     * @throws Exception exception.
     */
    @Test
    public void testBasic() throws Exception {
        MultiPartPartialReader multiPartPartialReader = new MultiPartPartialReader();
        Assert.assertNotNull(multiPartPartialReader);
    }

    /**
     * test MultiPartPartialReader.readFrom for good MIME.
     *
     * @throws Exception exception.
     */
    @Test
    public void testReadFrom() throws Exception {
        final byte[] ba = buildMIMEString(false, 90);
        final HashMap<String, String> map = new HashMap<>();
        map.put("boundary", "boundary");
        final MediaType mediaType = new MediaType("multipart", "form-data", map);
        final MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        final ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        final MultiPart multiPart = multiPartPartialReader.readFrom(null, null, null, mediaType, headers, bais);
        Assert.assertTrue((multiPart instanceof Partial), "multiPart should be MultiPartPartial");
        final Partial partial = (FormDataMultiPartPartial) multiPart;
        final FormDataMultiPartPartial formDataMultiPartPartial = (FormDataMultiPartPartial) multiPart;

        Assert.assertFalse(partial.getIsPartial(), "multiPart should not be partial");
        List<BodyPart> listBodyPartPartial = formDataMultiPartPartial.getBodyParts();
        List<String> listFilename = Arrays.asList("attach.0.filename", "attach.1.filename");
        List<String> listName = Arrays.asList("attach.0", "attach.1");
        Assert.assertEquals(listBodyPartPartial.size(), 2, "listBodyPart is supposed to be 2");
        for (int i = 0; i < listBodyPartPartial.size(); i++) {
            final BodyPart bodyPart = listBodyPartPartial.get(i);
            Assert.assertTrue((bodyPart instanceof FormDataBodyPartPartial), "BodyPart should be FormDataBodyPartPartial");
            FormDataBodyPartPartial bodyPartPartial = (FormDataBodyPartPartial) bodyPart;
            Assert.assertFalse(bodyPartPartial.getIsPartial(), "FormDataBodyPartPartial should not be partial");
            final FormDataContentDisposition contentDisposition = bodyPartPartial.getFormDataContentDisposition();
            final boolean cmpFilename = listFilename.get(i).equals(contentDisposition.getFileName());
            final boolean cmpName = listName.get(i).equals(contentDisposition.getName());
            Assert.assertTrue(cmpFilename, "Filename mismatch, expected: " + listFilename.get(i));
            Assert.assertTrue(cmpName, "Name mismatch, expected: " + listName.get(i));
            final BodyPartEntityPartial bodyPartEntity = (BodyPartEntityPartial) bodyPartPartial.getEntity();
            final InputStream istream = bodyPartEntity.getInputStream();
            Assert.assertNotNull(istream, "InputStream for MIME is null");
            final byte[] baMIME = IOUtils.toByteArray(istream);
            istream.close();
            final int szBaMIME = baMIME.length;
            Assert.assertNotEquals(szBaMIME, 0, "byte array of MIME is 0");
            final String sMIME = new String(baMIME, StandardCharsets.UTF_8);
            Assert.assertNotNull(sMIME, "MIME should have been a String");
        }
    }

    /**
     * test MultiPartPartialReader.readFrom for partial MIME.
     *
     * @throws Exception exception.
     */
    // @Test
    @SuppressWarnings("unchecked")
    public void testReadFromPartial() throws Exception {
        final byte[] ba = buildMIMEString(true, 90);
        final HashMap<String, String> map = new HashMap<>();
        map.put("boundary", "boundary");
        final MediaType mediaType = new MediaType("multipart", "form-data", map);
        final MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        final ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        // final MultiPart multiPart = multiPartPartialReaderEnablePartial.readFrom(null, null, null, mediaType, headers, bais);
        final MultiPart multiPart = multiPartPartialReader.readFrom(null, null, null, mediaType, headers, bais);
        Assert.assertTrue((multiPart instanceof Partial), "multiPart should be MultiPartPartial");
        final Partial partial = (FormDataMultiPartPartial) multiPart;
        final FormDataMultiPartPartial formDataMultiPartPartial = (FormDataMultiPartPartial) multiPart;

        Assert.assertTrue(partial.getIsPartial(), "multiPart should not be partial");
        List<BodyPart> listBodyPartPartial = formDataMultiPartPartial.getBodyParts();
        List<String> listFilename = Arrays.asList("attach.0.filename", "attach.1.filename");
        List<String> listName = Arrays.asList("attach.0", "attach.1");
        Assert.assertEquals(listBodyPartPartial.size(), 2, "listBodyPart is supposed to be 2");
        for (int i = 0; i < listBodyPartPartial.size(); i++) {
            final BodyPart bodyPart = listBodyPartPartial.get(i);
            Assert.assertTrue((bodyPart instanceof FormDataBodyPartPartial), "BodyPart should be FormDataBodyPartPartial");
            FormDataBodyPartPartial bodyPartPartial = (FormDataBodyPartPartial) bodyPart;
            if (i == 0) {
                Assert.assertFalse(bodyPartPartial.getIsPartial(), "FormDataBodyPartPartial should not be partial");
            } else if (i == 1) {
                Assert.assertTrue(bodyPartPartial.getIsPartial(), "FormDataBodyPartPartial should be partial");
            }
            final FormDataContentDisposition contentDisposition = bodyPartPartial.getFormDataContentDisposition();
            final boolean cmpFilename = listFilename.get(i).equals(contentDisposition.getFileName());
            final boolean cmpName = listName.get(i).equals(contentDisposition.getName());
            Assert.assertTrue(cmpFilename, "Filename mismatch, expected: " + listFilename.get(i));
            Assert.assertTrue(cmpName, "Name mismatch, expected: " + listName.get(i));
            final BodyPartEntityPartial bodyPartEntity = (BodyPartEntityPartial) bodyPartPartial.getEntity();
            final InputStream istream = bodyPartEntity.getInputStream();
            Assert.assertNotNull(istream, "InputStream for MIME is null");
            final byte[] baMIME = IOUtils.toByteArray(istream);
            istream.close();
            final int szBaMIME = baMIME.length;
            Assert.assertNotEquals(szBaMIME, 0, "byte array of MIME is 0");
            final String sMIME = new String(baMIME, StandardCharsets.UTF_8);
            Assert.assertNotNull(sMIME, "MIME should have been a String");
        }
    }

    /**
     * testReadFromPartialMIMENotSupported.
     *
     * @throws Exception expecting BadRequestException.
     */
    @Test(expectedExceptions = BadRequestException.class)
    public void testReadFromPartialMIMENotSupported() throws Exception {
        final byte[] ba = buildMIMEString(true, 90);
        final HashMap<String, String> map = new HashMap<>();
        map.put("boundary", "boundary");
        final MediaType mediaType = new MediaType("multipart", "form-data", map);
        final MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        final ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        final MultiPart multiPart = multiPartPartialReader.readFrom(null, null, null, mediaType, headers, bais);
        Assert.assertTrue((multiPart instanceof Partial), "multiPart should be MultiPartPartial");
    }

    /**
     * testReadFrom50MegabyteMIMENotSupported
     *
     * @throws Exception expecting BadRequestException.
     */
    @Test(expectedExceptions = BadRequestException.class, expectedExceptionsMessageRegExp = "HTTP 400 Bad Request")
    public void testReadFrom50MegabyteMIMENotSupported() throws Exception {
        final String sData = "123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789\n";
        final String stringMIME;
        {
            final StringBuilder sb = new StringBuilder();
            sb.append("--boundary\r\n");
            sb.append("Content-Disposition: form-data; name=\"attach.0\"; filename=\"attach.0.filename\"\r\n");
            sb.append("Content-Type: plain/text\r\n");
            sb.append("\r\n");
            final int numLines = 500000;
            for (int i = 0; i < numLines; i++) {
                sb.append(String.format("%010d %s", i, sData));
            }
            sb.append("\r\n");
            sb.append("--boundary--\r\n");
            stringMIME = sb.toString();
        }
        final int szMIME = stringMIME.length();
        // System.out.printf("50MB size is %d\n", szMIME);
        Assert.assertTrue((szMIME > 50000000), "MIME size less than 50MB");
        {
            final byte[] ba = stringMIME.getBytes(StandardCharsets.UTF_8);
            final HashMap<String, String> map = new HashMap<>();
            map.put("boundary", "boundary");
            final MediaType mediaType = new MediaType("multipart", "form-data", map);
            final MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
            final ByteArrayInputStream bais = new ByteArrayInputStream(ba);
            final MultiPart multiPart = multiPartPartialReader.readFrom(null, null, null, mediaType, headers, bais);
            Assert.assertTrue((multiPart instanceof Partial), "multiPart should be MultiPartPartial");
        }
    }

    /**
     * testReadFrom2x25MegabyteMIMENotSupported. This splits into 2x25 MB attachments.
     *
     * @throws Exception expecting BadRequestException.
     */
    @Test(expectedExceptions = BadRequestException.class, expectedExceptionsMessageRegExp = "HTTP 400 Bad Request")
    public void testReadFrom2x25MegabyteMIMENotSupported() throws Exception {
        final String sData = "123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789\n";
        final String stringMIME;
        {
            final StringBuilder sb = new StringBuilder();
            sb.append("--boundary\r\n");
            sb.append("Content-Disposition: form-data; name=\"attach.0\"; filename=\"attach.0.filename\"\r\n");
            sb.append("Content-Type: plain/text\r\n");
            sb.append("\r\n");
            final int numLines = 250000;
            for (int i = 0; i < numLines; i++) {
                sb.append(String.format("%010d %s", i, sData));
            }
            sb.append("\r\n");
            sb.append("--boundary\r\n");
            sb.append("Content-Disposition: form-data; name=\"attach.1\"; filename=\"attach.1.filename\"\r\n");
            sb.append("Content-Type: plain/text\r\n");
            sb.append("\r\n");
            for (int i = 0; i < numLines; i++) {
                sb.append(String.format("%010d %s", i, sData));
            }
            sb.append("\r\n");
            sb.append("--boundary--\r\n");
            stringMIME = sb.toString();
        }
        final int szMIME = stringMIME.length();
        // System.out.printf("50MB size is %d\n", szMIME);
        Assert.assertTrue((szMIME > 50000000), "MIME size less than 50MB");
        {
            final byte[] ba = stringMIME.getBytes(StandardCharsets.UTF_8);
            final HashMap<String, String> map = new HashMap<>();
            map.put("boundary", "boundary");
            final MediaType mediaType = new MediaType("multipart", "form-data", map);
            final MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
            final ByteArrayInputStream bais = new ByteArrayInputStream(ba);
            final MultiPart multiPart = multiPartPartialReader.readFrom(null, null, null, mediaType, headers, bais);
            Assert.assertTrue((multiPart instanceof Partial), "multiPart should be MultiPartPartial");
        }
    }

    /**
     * testReadFrom40MegabyteMIMESupported
     *
     * @throws Exception expecting BadRequestException.
     */
    @Test
    public void testReadFrom40MegabyteMIMESupported() throws Exception {
        final String sData = "123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789\n";
        final String stringMIME;
        {
            final StringBuilder sb = new StringBuilder();
            sb.append("--boundary\r\n");
            sb.append("Content-Disposition: form-data; name=\"attach.0\"; filename=\"attach.0.filename\"\r\n");
            sb.append("Content-Type: plain/text\r\n");
            sb.append("\r\n");
            final int numLines = 390000;
            for (int i = 0; i < numLines; i++) {
                sb.append(String.format("%010d %s", i, sData));
            }
            sb.append("\r\n");
            sb.append("--boundary--\r\n");
            stringMIME = sb.toString();
            final int szMIME = stringMIME.length();
            // System.out.printf("40MB size is %d\n", szMIME);
            Assert.assertTrue((szMIME < 40000000), "MIME size greater than 40MB");
        }
        final byte[] ba = stringMIME.getBytes(StandardCharsets.UTF_8);
        final HashMap<String, String> map = new HashMap<>();
        map.put("boundary", "boundary");
        final MediaType mediaType = new MediaType("multipart", "form-data", map);
        final MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        final ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        final MultiPart multiPart = multiPartPartialReader.readFrom(null, null, null, mediaType, headers, bais);
        Assert.assertTrue((multiPart instanceof Partial), "multiPart should be MultiPartPartial");
        final Partial partial = (FormDataMultiPartPartial) multiPart;
        final FormDataMultiPartPartial formDataMultiPartPartial = (FormDataMultiPartPartial) multiPart;

        Assert.assertFalse(partial.getIsPartial(), "multiPart should not be partial");
        List<BodyPart> listBodyPartPartial = formDataMultiPartPartial.getBodyParts();
        List<String> listFilename = Arrays.asList("attach.0.filename");
        List<String> listName = Arrays.asList("attach.0");
        Assert.assertEquals(listBodyPartPartial.size(), 1, "listBodyPart is supposed to be 1");
        for (int i = 0; i < listBodyPartPartial.size(); i++) {
            final BodyPart bodyPart = listBodyPartPartial.get(i);
            Assert.assertTrue((bodyPart instanceof FormDataBodyPartPartial), "BodyPart should be FormDataBodyPartPartial");
            FormDataBodyPartPartial bodyPartPartial = (FormDataBodyPartPartial) bodyPart;
            Assert.assertFalse(bodyPartPartial.getIsPartial(), "FormDataBodyPartPartial should not be partial");
            final FormDataContentDisposition contentDisposition = bodyPartPartial.getFormDataContentDisposition();
            final boolean cmpFilename = listFilename.get(i).equals(contentDisposition.getFileName());
            final boolean cmpName = listName.get(i).equals(contentDisposition.getName());
            Assert.assertTrue(cmpFilename, "Filename mismatch, expected: " + listFilename.get(i));
            Assert.assertTrue(cmpName, "Name mismatch, expected: " + listName.get(i));
            final BodyPartEntityPartial bodyPartEntity = (BodyPartEntityPartial) bodyPartPartial.getEntity();
            final InputStream istream = bodyPartEntity.getInputStream();
            Assert.assertNotNull(istream, "InputStream for MIME is null");
            final byte[] baMIME = IOUtils.toByteArray(istream);
            istream.close();
            final int szBaMIME = baMIME.length;
            Assert.assertNotEquals(szBaMIME, 0, "byte array of MIME is 0");
            final String sMIME = new String(baMIME, StandardCharsets.UTF_8);
            Assert.assertNotNull(sMIME, "MIME should have been a String");
        }
    }

    /**
     * String helper to build generic MIMEMessage format string.
     *
     * @param isPartial boolean.
     * @param pctTerminate int.
     * @return byte array of string.
     */
    private byte[] buildMIMEString(final boolean isPartial, final int pctTerminate) {
        final StringBuilder sb = new StringBuilder();
        sb.append("--boundary\r\n");
        sb.append("Content-Disposition: form-data; name=\"attach.0\"; filename=\"attach.0.filename\"\r\n");
        sb.append("Content-Type: plain/text\r\n");
        sb.append("\r\n");
        sb.append("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\r\n");
        sb.append("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\r\n");
        sb.append("cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc\r\n");
        sb.append("dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd\r\n");
        sb.append("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee\r\n");
        sb.append("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff\r\n");
        sb.append("\r\n");
        sb.append("--boundary\r\n");
        sb.append("Content-Disposition: form-data; name=\"attach.1\"; filename=\"attach.1.filename\"\r\n");
        sb.append("Content-Type: plain/text\r\n");
        sb.append("\r\n");
        sb.append("000000000000000000000000000000000000000000000000000000000000000000\r\n");
        sb.append("111111111111111111111111111111111111111111111111111111111111111111\r\n");
        sb.append("222222222222222222222222222222222222222222222222222222222222222222\r\n");
        sb.append("333333333333333333333333333333333333333333333333333333333333333333\r\n");
        sb.append("444444444444444444444444444444444444444444444444444444444444444444\r\n");
        sb.append("555555555555555555555555555555555555555555555555555555555555555555\r\n");
        sb.append("666666666666666666666666666666666666666666666666666666666666666666\r\n");
        sb.append("\r\n");
        sb.append("--boundary--\r\n");
        final String s = sb.toString();
        final byte[] ba = s.getBytes(StandardCharsets.UTF_8);
        if (isPartial) {
            if (pctTerminate > 0 && pctTerminate < 100) {
                final int szBa = ba.length;
                final int szPartial = (int) (szBa * (pctTerminate / 100.0));
                final byte[] baPartial = Arrays.copyOf(ba, szPartial);
                return baPartial;
            }
        }
        return ba;
    }
}
