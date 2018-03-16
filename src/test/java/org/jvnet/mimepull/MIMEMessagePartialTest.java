package org.jvnet.mimepull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * MIMEMessageTest tests the jvnet partial package.
 *
 * @author wayneng
 *
 */
public class MIMEMessagePartialTest {

    /**
     * class TestKeys is used in this IT class for map put and get values.
     *
     * @author wayneng
     *
     */
    final class TestKeys {
        /** private constructor. */
        private TestKeys() {
        }

        /** KEY_ATTACHMENT at. */
        public static final String KEY_ATTACHMENT = "at";
        /** KEY_BASE64. */
        public static final String KEY_BASE64 = "b64";
        /** KEY_BASE64_CHUNKED. */
        public static final String KEY_BASE64_CHUNKED = "b64_chunked";
        /** KEY_SIZE sz. */
        public static final String KEY_SIZE = "sz";
        /** KEY_HEADER hd. */
        public static final String KEY_HEADER = "hd";
        /** KEY_CONTENT_ID ci. */
        public static final String KEY_CONTENT_ID = "ci";
        /** KEY_CONTENT_NAME cn. */
        public static final String KEY_CONTENT_NAME = "cn";
        /** KEY_DISPOSITION disposition. */
        public static final String KEY_DISPOSITION = "disposition";
        /** KEY_CRC32 crc. */
        public static final String KEY_CRC32 = "crc";
        /** KEY_TYPE type. */
        public static final String KEY_TYPE = "type";
        /** KEY_SUBTYPE subtype. */
        public static final String KEY_SUBTYPE = "subtype";
        /** KEY_MID mid. */
        public static final String KEY_MID = "mid";
        /** KEY_PID pid. */
        public static final String KEY_PID = "pid";
        /** KEY_DOWNLOAD_URL refDownloadUrl. */
        public static final String KEY_DOWNLOAD_URL = "refDownloadUrl";
        /** KEY_SZ_RESPONSE szresponse. */
        public static final String KEY_SZ_RESPONSE = "szresponse";
        /** KEY_OFFSET offset. */
        public static final String KEY_OFFSET = "offset";
        /** KEY_ISPARTIAL isPartial. */
        public static final String KEY_ISPARTIAL = "isPartial";
        /** KEY_BIN_ATTACHMENT binAttachment. */
        public static final String KEY_BIN_ATTACHMENT = "binAttachment";
        /** KEY_XFER_ENCODING transferEncoding. */
        public static final String KEY_XFER_ENCODING = "xferEncoding";
        /** TRUE. */
        public static final String TRUE = "true";
        /** FALSE. */
        public static final String FALSE = "false";
    }

    /**
     *
     * @author wayneng
     *
     */
    class ContainerAttachment {
        /** keyBinAttachment. */
        private ByteArrayOutputStream keyBinAttachment = null;
        /** ba byte array. */
        private byte[] ba = null;
        /** map HashMap. */
        private HashMap<String, String> map = new HashMap<>();

        /**
         *
         * @param key String.
         * @param value Object.
         */
        public void put(final String key, final Object value) {
            switch (key) {
            case TestKeys.KEY_BIN_ATTACHMENT:
                if (value == null || value instanceof ByteArrayOutputStream) {
                    keyBinAttachment = (ByteArrayOutputStream) value;
                    if (keyBinAttachment != null) {
                        ba = keyBinAttachment.toByteArray();
                    }
                }
                break;
            default:
                if (value == null || value instanceof String) {
                    map.put(key, (String) value);
                }
                break;
            }
        }

        /**
         *
         * @param key String.
         * @param byteArray byte array.
         */
        public void put(final String key, final byte[] byteArray) {
            if (TestKeys.KEY_BIN_ATTACHMENT.equals(key)) {
                if (byteArray == null) {
                    keyBinAttachment = null;
                    ba = null;
                } else {
                    keyBinAttachment = new ByteArrayOutputStream();
                    keyBinAttachment.write(byteArray, 0, byteArray.length);
                    ba = keyBinAttachment.toByteArray();
                }
            }
        }

        /**
         *
         * @return boolean.
         */
        public boolean getIsBinary() {
            return keyBinAttachment != null;
        }

        /**
         *
         * @param key String.
         * @return String.
         */
        public String get(final String key) {
            return map.get(key);
        }

        /**
         *
         * @param key String.
         * @return byte array.
         */
        public byte[] getBinAttachment(final String key) {
            switch (key) {
            case TestKeys.KEY_BIN_ATTACHMENT:
                return ba;
            default:
                return null;
            }
        }
    }

    /**
     * generateGenericJSONStringWithAttachmentMap uses JsonGenerator to return JSON String.
     *
     * @param csid CSID String.
     * @param subject String.
     * @param htmlBody html String.
     * @param map Map of filename, ContainerAttachment.
     * @param email String.
     * @return String.
     * @throws IOException JsonGenerator error.
     */
    private String generateGenericJSONStringWithAttachmentMap(final String csid, final String subject, final String htmlBody, final String email,
            final Map<String, ContainerAttachment> map) throws IOException {
        // construct the json and html message
        final ByteArrayOutputStream baosJson = new ByteArrayOutputStream();
        final JsonFactory jsonFactory = new JsonFactory();
        final JsonGenerator jsonGenerator = jsonFactory.createGenerator(baosJson, JsonEncoding.UTF8);
        {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectFieldStart("actions");
            jsonGenerator.writeObjectField("responseMessage", true);
            jsonGenerator.writeObjectField("responseMessageV2", true);
            jsonGenerator.writeEndObject();
            jsonGenerator.writeObjectFieldStart("message");
            jsonGenerator.writeObjectField("csid", csid);
            jsonGenerator.writeObjectFieldStart("flags");
            jsonGenerator.writeObjectField("spam", false);
            jsonGenerator.writeObjectField("read", true);
            jsonGenerator.writeEndObject();
            jsonGenerator.writeObjectFieldStart("headers");
            jsonGenerator.writeObjectField("subject", "test partial message");
            jsonGenerator.writeArrayFieldStart("to");
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("fail", false);
            jsonGenerator.writeObjectField("email", email);
            jsonGenerator.writeObjectField("name", email);
            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndArray();
            jsonGenerator.writeArrayFieldStart("from");
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("email", email);
            jsonGenerator.writeObjectField("name", email);
            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
            jsonGenerator.writeObjectFieldStart("folder");
            jsonGenerator.writeObjectField("id", "3");
            jsonGenerator.writeEndObject();
            jsonGenerator.writeObjectField("newMessage", true);
            jsonGenerator.writeEndObject();
            jsonGenerator.writeObjectFieldStart("simpleBody");
            jsonGenerator.writeObjectField("html", htmlBody);
        }
        {
            // write the attachments from map
            jsonGenerator.writeArrayFieldStart("attachments");

            for (Map.Entry<String, ContainerAttachment> kv : map.entrySet()) {
                ContainerAttachment v = kv.getValue();
                final String filename = kv.getKey();
                final String contentId = v.get(TestKeys.KEY_CONTENT_ID);
                final String disposition = v.get(TestKeys.KEY_DISPOSITION);

                jsonGenerator.writeStartObject();
                jsonGenerator.writeObjectField("disposition", disposition);
                jsonGenerator.writeObjectField("multipartName", "multipart://" + filename);
                jsonGenerator.writeObjectField("contentId", contentId);
                jsonGenerator.writeEndObject();

            }
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndObject();
        jsonGenerator.close();
        final String jsonBody = baosJson.toString();

        return jsonBody;
    }

    /**
     * Get attachment header for MIMEMessage.
     *
     * @param filename String.
     * @param name String.
     * @param contentType String.
     * @param encoding String.
     * @return string String.
     */
    private String getAttachmentHeader(final String filename, final String name, final String contentType, final String encoding) {
        StringBuilder sb = new StringBuilder();
        String sContentType = "application/octet-stream";
        if (contentType != null) {
            sContentType = contentType;
        }
        sb.append("Content-Type: " + sContentType + "\r\n");
        if (encoding != null) {
            sb.append("Content-Transfer-Encoding: " + encoding + "\r\n");
        }
        sb.append(String.format("Content-Disposition: form-data; filename=\"%s\"; name=\"%s\"\r\n\r\n", filename, name));
        return sb.toString();
    }

    /**
     * Gets progressive numbering file attachment up to size bytes.
     *
     * @param szFile size of file.
     * @param filename name of file.
     * @return String that has attachment header and random string attachment.
     */
    private String textNumberFileAttachment(final int szFile, final String filename) {
        int szPerNumber = 7;
        int szNumbersPerLine = 10;
        int maxNumberCount = szFile / (szPerNumber + 1);
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (int i = 0; i < maxNumberCount; i++) {
            sb.append(String.format("%07d", i));
            counter++;
            if ((counter % szNumbersPerLine) == 0) {
                sb.append("\n");
            } else {
                sb.append(" ");
            }
        }
        final String returnVal = sb.toString();
        return returnVal;
    }

    /**
     * returns randByteArrayOutputStream.
     *
     * @param numBytes numBytes for outputStream.
     * @return ByteArrayOutputStream.
     */
    private ByteArrayOutputStream randByteArrayOutputStream(final int numBytes) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < numBytes; i++) {
            baos.write(i);
        }
        return baos;
    }

    /**
     * generateContainerAttachments creates list of attachments based on Containers.
     *
     * @param lhMap LinkedHashMap<String, ContainerAttachment>.
     * @param numAttachments num attachments.
     * @param szPerAttachment sz per attachment.
     * @param isBinary boolean.
     * @param transferEncoding String for Content-Transfer-Encoding.
     *
     * @return List<String> of attachments.
     */
    private List<String> generateContainerAttachments(final LinkedHashMap<String, ContainerAttachment> lhMap, final int numAttachments,
            final int szPerAttachment, final boolean isBinary, final String transferEncoding) {
        if (lhMap == null) {
            return null;
        }
        final String attachmentNamePrefix = "basename";
        final String disposition = "attachment";
        List<String> list = new ArrayList<>();
        final byte[] lineSeparator = { '\n' };
        Base64.Encoder base64 = Base64.getMimeEncoder(76, lineSeparator);
        // this a base64 encoder using only LF, no CR. default is LFCR.
        for (int i = 0; i < numAttachments; i++) {
            String filename = isBinary ? String.format("%s_%02d.bin", attachmentNamePrefix, i)
                    : String.format("%s_%02d.txt", attachmentNamePrefix, i);
            String header = getAttachmentHeader(filename, filename, null, transferEncoding);
            String contentName = String.format("ContentId%s@yahoo.com", filename);
            String contentId = String.format("<%s>", contentName);
            list.add(filename);
            CRC32 crc32 = new CRC32();
            lhMap.put(filename, new ContainerAttachment());
            lhMap.get(filename).put(TestKeys.KEY_DISPOSITION, disposition);
            lhMap.get(filename).put(TestKeys.KEY_CONTENT_NAME, contentName);
            lhMap.get(filename).put(TestKeys.KEY_HEADER, header);
            lhMap.get(filename).put(TestKeys.KEY_CONTENT_ID, contentId);
            if (transferEncoding != null) {
                lhMap.get(filename).put(TestKeys.KEY_XFER_ENCODING, transferEncoding);
            }
            if (isBinary) {
                ByteArrayOutputStream baos = randByteArrayOutputStream(szPerAttachment);
                final byte[] ba = baos.toByteArray();
                final byte[] ba64 = java.util.Base64.getEncoder().encode(ba);
                final byte[] ba64chunked = base64.encode(ba); // Base64.encodeBase64(ba, true);
                final String ba64String = new String(ba64, StandardCharsets.UTF_8);
                final String ba64ChunkedString = new String(ba64chunked, StandardCharsets.UTF_8);
                final int length = ba.length;
                lhMap.get(filename).put(TestKeys.KEY_SIZE, Integer.toString(length));
                lhMap.get(filename).put(TestKeys.KEY_BIN_ATTACHMENT, baos);
                lhMap.get(filename).put(TestKeys.KEY_BASE64, ba64String);
                lhMap.get(filename).put(TestKeys.KEY_BASE64_CHUNKED, ba64ChunkedString);
                crc32.update(ba);
                lhMap.get(filename).put(TestKeys.KEY_CRC32, Long.toString(crc32.getValue()));
            } else {
                String attachment = textNumberFileAttachment(szPerAttachment, filename);
                final byte[] ba = attachment.getBytes(StandardCharsets.UTF_8);
                final byte[] ba64 = java.util.Base64.getEncoder().encode(ba);
                final byte[] ba64chunked = base64.encode(ba);
                final String ba64String = new String(ba64, StandardCharsets.UTF_8);
                final String ba64ChunkedString = new String(ba64chunked, StandardCharsets.UTF_8);
                final int length = ba.length;
                lhMap.get(filename).put(TestKeys.KEY_SIZE, Integer.toString(length));
                lhMap.get(filename).put(TestKeys.KEY_ATTACHMENT, attachment);
                lhMap.get(filename).put(TestKeys.KEY_BASE64, ba64String);
                lhMap.get(filename).put(TestKeys.KEY_BASE64_CHUNKED, ba64ChunkedString);
                crc32.update(ba);
                lhMap.get(filename).put(TestKeys.KEY_CRC32, Long.toString(crc32.getValue()));
            }
        }
        return list;
    }

    /**
     * testPartialBase64 helper method.
     *
     * @param numAttachments int.
     * @param szPerAttachment int.
     * @param payloadBytesMissing int.
     * @param boundary String.
     * @param baosReference reference bytes sent to MIMEMessage for debugging.
     * @param base64Mode boolean.
     * @param isBinary boolean.
     * @return MIMEMessagePartial.
     * @throws IOException error.
     */
    private MIMEMessagePartial createMIMEMessage(final int numAttachments, final int szPerAttachment, final int payloadBytesMissing,
            final String boundary, final ByteArrayOutputStream baosReference, final boolean base64Mode, final boolean isBinary) throws IOException {
        LinkedHashMap<String, ContainerAttachment> map = new LinkedHashMap<>();
        final byte[] ba;
        {
            final String transferEncoding;
            final String jsonBody;
            final MultipartEntityBuilder multiPartEntity = MultipartEntityBuilder.create();
            if (base64Mode) {
                transferEncoding = "base64";
            } else {
                transferEncoding = null;
            }
            {
                generateContainerAttachments(map, numAttachments, szPerAttachment, isBinary, transferEncoding);
                multiPartEntity.setBoundary(boundary);
                final String csid = "1000000000";
                final String email = "test@yahoo.com";
                final String subject = "subject partial test";
                final String htmlBody = "<html><head></head><body>" + "hello this is a body" + "</body></html>";
                jsonBody = generateGenericJSONStringWithAttachmentMap(csid, subject, htmlBody, email, map);
            }
            {
                ContentType contentTypeJSON = ContentType.create("application/json");
                final ContentBody contentBodyJSON = new ByteArrayBody(jsonBody.getBytes(StandardCharsets.UTF_8), contentTypeJSON, null);
                multiPartEntity.addPart("jsonString", contentBodyJSON);
            }
            {
                for (Map.Entry<String, ContainerAttachment> kv : map.entrySet()) {
                    final ContainerAttachment container = kv.getValue();
                    final String filename = kv.getKey();
                    final String attachment = container.get(TestKeys.KEY_ATTACHMENT);
                    final String attachmentBase64 = container.get(TestKeys.KEY_BASE64);

                    final String contentDisposition = String.format("form-data; name=\"%s\"; filename=\"%s\"", filename, filename);
                    final ContentType contentType = ContentType.create("application/octet-stream");
                    final FormBodyPartBuilder formBodyPartBuilder = FormBodyPartBuilder.create();
                    final FormBodyPart formBodyPart;
                    if (base64Mode) {
                        final byte[] byteArrayAttachment = attachmentBase64.getBytes(StandardCharsets.UTF_8);
                        final ContentBody contentBody = new ByteArrayBody(byteArrayAttachment, contentType, filename);
                        formBodyPart = formBodyPartBuilder.setName(filename).addField("Content-Disposition", contentDisposition)
                                .addField("Content-Transfer-Encoding", "base64").setBody(contentBody).build();
                    } else {
                        final byte[] byteArrayAttachment = attachment.getBytes(StandardCharsets.UTF_8);
                        final ContentBody contentBody = new ByteArrayBody(byteArrayAttachment, contentType, filename);
                        formBodyPart = formBodyPartBuilder.setName(filename).addField("Content-Disposition", contentDisposition).setBody(contentBody)
                                .build();
                    }
                    multiPartEntity.addPart(formBodyPart);
                }
            }
            final HttpEntity httpEntity = multiPartEntity.build();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            httpEntity.writeTo(baos);
            final HttpEntity httpEntity2 = new ByteArrayEntity(baos.toByteArray(), ContentType.MULTIPART_FORM_DATA);
            final ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            httpEntity2.writeTo(baos2);
            ba = baos2.toByteArray();
        }
        baosReference.write(ba);
        final int sizePayload = ba.length;
        final int sizePayloadPartial = sizePayload - payloadBytesMissing;
        final byte[] baParsing = Arrays.copyOfRange(ba, 0, sizePayloadPartial);
        final String strMIMEPartial = new String(baParsing, StandardCharsets.UTF_8);
        Assert.assertNotNull(strMIMEPartial, "MIME input is null");
        final ByteArrayInputStream bais = new ByteArrayInputStream(baParsing);
        final MIMEConfigPartial mimeConfig = new MIMEConfigPartial();
        final MIMEMessagePartial mimeMessage = new MIMEMessagePartial(bais, boundary, mimeConfig);
        return mimeMessage;
    }

    /**
     * Test to see how JsonGenerator works.
     *
     * @throws Exception from partial package.
     */
    @Test
    public void testJSONObjectMapperSimpleJSON() throws Exception {
        /*
         * Do string representation JSON of:
         *
         * {
         *
         * a : { aa0:aa00, aa1:aa01 },
         *
         * b : [
         *
         * { bb0:bb00, bb1:bb01 },
         *
         * { bc0:bc01, bc1:bc01 }
         *
         * ],
         *
         * cc0:[]
         *
         * }
         */
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(baos, JsonEncoding.UTF8);
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectFieldStart("a");
        jsonGenerator.writeObjectField("aa0", false);
        jsonGenerator.writeObjectField("aa1", "aa01");
        jsonGenerator.writeEndObject();
        jsonGenerator.writeArrayFieldStart("b");
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("bb0", "bb00");
        jsonGenerator.writeStringField("bb1", "bb01");
        jsonGenerator.writeEndObject();
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("bc0", "bc00");
        jsonGenerator.writeStringField("bc1", "bc01");
        jsonGenerator.writeEndObject();
        jsonGenerator.writeEndArray();
        jsonGenerator.writeArrayFieldStart("c");
        jsonGenerator.writeEndArray();
        jsonGenerator.close();
        String stringJson = new String(baos.toByteArray(), "UTF-8");
        String stringRef = "{\"a\":{\"aa0\":false,\"aa1\":\"aa01\"},"
                + "\"b\":[{\"bb0\":\"bb00\",\"bb1\":\"bb01\"},{\"bc0\":\"bc00\",\"bc1\":\"bc01\"}]," + "\"c\":[]}";
        Assert.assertNotNull(stringJson);
        boolean res = stringRef.equals(stringJson);
        Assert.assertTrue(res, "stringJson mismatch: " + stringJson);
    }

    /**
     * Another test to see how JsonGenerator works.
     *
     * @throws Exception from partial package.
     */
    @Test
    public void testJSONObjectMapperJSONSimpleTest2() throws Exception {
        /*
         * Do string representation JSON of:
         *
         * {
         *
         * a : { aa0:aa00, aa1:aa01 },
         *
         * b : [
         *
         * { bb0:bb00, bb1:bb01 },
         *
         * { bc0:bc01, bc1:bc01 }
         *
         * ],
         *
         * cc0:[]
         *
         * }
         */
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(baos, JsonEncoding.UTF8);
        {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectFieldStart("a");
            jsonGenerator.writeObjectField("aa0", false);
            jsonGenerator.writeObjectField("aa1", "aa01");
            jsonGenerator.writeEndObject();
        }
        {
            // do not use writeArrayFieldStart and writeStartObject for the same thing, use one
            jsonGenerator.writeArrayFieldStart("b");
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("bb0", "bb00");
            jsonGenerator.writeStringField("bb1", "bb01");
            jsonGenerator.writeEndObject();
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("bc0", "bc00");
            jsonGenerator.writeStringField("bc1", "bc01");
            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndArray();
        }
        {
            jsonGenerator.writeArrayFieldStart("c");
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.close();
        String stringJson = new String(baos.toByteArray(), "UTF-8");
        String stringRef = "{\"a\":{\"aa0\":false,\"aa1\":\"aa01\"},"
                + "\"b\":[{\"bb0\":\"bb00\",\"bb1\":\"bb01\"},{\"bc0\":\"bc00\",\"bc1\":\"bc01\"}]," + "\"c\":[]}";
        Assert.assertNotNull(stringJson);
        boolean res = stringRef.equals(stringJson);
        Assert.assertTrue(res, "stringJson mismatch: " + stringJson);
    }

    /**
     *
     * @throws Exception from partial package.
     */
    @Test
    public void testMIMEGoodFiles() throws Exception {
        // testMIMEGoodFiles(1);
        testMIMEGoodFiles(2);
        // testMIMEGoodFiles(10);
    }

    /**
     *
     * @param numFiles number of files to create.
     * @throws Exception from partial package.
     */
    private void testMIMEGoodFiles(final int numFiles) throws Exception {
        /*
         * test MIMEParser with all good attachments
         */
        MIMETestUtils mimeTestUtils = new MIMETestUtils();

        int numCharsPerWord = 9;
        int numWordsPerLine = 8;
        int numLines = 10;
        String delimiter = " ";

        List<String> listHeader = new ArrayList<>();
        List<String> listBody = new ArrayList<>();

        String s;

        String strBoundary = "Boundary_123_test";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<String> listMsgReferences = new ArrayList<>();
        List<String> listAttachmentFilenames = new ArrayList<>();
        // write to array and use for reference later
        int idxFile = 0;
        String filename;
        for (int i = 0; i < numFiles; i++) {
            filename = String.format("file_%d", idxFile++);
            listAttachmentFilenames.add(filename);
        }
        for (int i = 0; i < numFiles; i++) {
            filename = String.format("file_%d", idxFile++);
            listMsgReferences.add(String.format("file_%d", idxFile++));
        }
        s = mimeTestUtils.getBoundaryString(strBoundary, false);
        baos.write(s.getBytes(StandardCharsets.UTF_8));
        baos.write("Content-Type: application/json\r\n".getBytes(StandardCharsets.UTF_8));
        baos.write("Content-Disposition: form-data; name=\"jsonString\"\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        s = mimeTestUtils.getJSONBeanString(listMsgReferences, listAttachmentFilenames);
        baos.write(s.getBytes(StandardCharsets.UTF_8));

        for (int i = 0; i < numFiles; i++) {
            filename = String.format("file_%d", i);
            s = mimeTestUtils.getBoundaryString(strBoundary, false);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            s = mimeTestUtils.getAttachmentHeader(filename);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            listHeader.add(s);
            s = mimeTestUtils.randStringLines(numCharsPerWord, numWordsPerLine, numLines, delimiter);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            listBody.add(s);
        }
        s = mimeTestUtils.getBoundaryString(strBoundary, true);
        baos.write(s.getBytes(StandardCharsets.UTF_8));

        MIMEConfigPartial mimeCfg = new MIMEConfigPartial();
        InputStream is = null;
        MIMEMessagePartial mimeMsg = null;
        boolean res;
        try {
            final String strReference = baos.toString();
            Assert.assertNotNull(strReference);
            is = new ByteArrayInputStream(baos.toByteArray());
            mimeMsg = new MIMEMessagePartial(is, strBoundary, mimeCfg);
            List<MIMEPartPartial> l = mimeMsg.getAttachmentsPartial();
            Assert.assertNotNull(l, "List MIMEPart is null");
            int idx = 0;
            for (int i = 0; i < l.size(); i++) {
                MIMEPartPartial mimePart = l.get(i);
                String contentId = mimePart.getContentId();
                String contentType = mimePart.getContentType();
                res = !mimePart.getIsPartial();
                Assert.assertTrue(res, "mimePart isPartial should be false");
                List<String> lContentDisposition = mimePart.getHeader("Content-Disposition");
                Assert.assertNotNull(lContentDisposition, "contentDisposition null");
                res = lContentDisposition.size() == 1;
                Assert.assertTrue(res, "contentDisposition size != 1");
                Assert.assertNotNull(contentId);
                Assert.assertNotNull(contentType);
                InputStream isPart = mimePart.read();
                ByteArrayOutputStream baosCmp = new ByteArrayOutputStream();
                int v;
                while ((v = isPart.read()) != -1) {
                    baosCmp.write(v);
                }
                baosCmp.write("\n".getBytes(StandardCharsets.UTF_8));
                String mimeStreamVal = new String(baosCmp.toByteArray(), "UTF-8");
                Assert.assertNotNull(mimeStreamVal);
                String mimeReference = listBody.get(idx);
                res = mimeReference.equals(mimeStreamVal);
                if (contentType.equals("application/json")) {
                    continue;
                }
                if (!res) {
                    int szRef = mimeReference.length();
                    int szMsg = mimeStreamVal.length();
                    res = szRef == szMsg;
                    Assert.assertTrue(res, "content length mismatch");
                }
                Assert.assertTrue(res, "string compare mismatch for " + listHeader.get(idx));
                idx++;
            }
            res = l.size() == (listHeader.size() + 1); // +1 for jsonString
            Assert.assertTrue(res, "attachment list size mismatch " + l.size());
            mimeMsg.close();
            is.close();
        } catch (IOException e) {
            throw e;
        } finally {
            mimeMsg.close();
            is.close();
        }

    }

    /**
     *
     * @throws Exception from partial package.
     */
    @Test
    public void testMIMETwoFilesPartialJSONBean() throws Exception {
        /*
         * Test MIMEParser that breaks at JSON Bean, the first part of attachments. This should fail and throw exception. We cannot process if the
         * JSON section not available.
         */
        MIMETestUtils mimeTestUtils = new MIMETestUtils();

        String s;
        String strBoundary = "Boundary_123_test";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<String> listMsgReferences = new ArrayList<>();
        List<String> listAttachmentFilenames = new ArrayList<>();
        // write to array and use for reference later
        int idxFile = 0;
        int numFiles = 2;
        String filename;
        for (int i = 0; i < numFiles; i++) {
            filename = String.format("file_%d", idxFile++);
            listAttachmentFilenames.add(filename);
        }
        for (int i = 0; i < numFiles; i++) {
            filename = String.format("file_%d", idxFile++);
            listMsgReferences.add(String.format("file_%d", idxFile++));
        }
        s = mimeTestUtils.getBoundaryString(strBoundary, false);
        baos.write(s.getBytes(StandardCharsets.UTF_8));
        s = mimeTestUtils.getAttachmentHeader(MIMETestUtils.ContentType.JSON);
        baos.write(s.getBytes(StandardCharsets.UTF_8));
        int szJsonHeader = s.length();
        String stringJson = mimeTestUtils.getJSONBeanString(listMsgReferences, listAttachmentFilenames);
        baos.write(stringJson.getBytes(StandardCharsets.UTF_8));
        s = mimeTestUtils.getBoundaryString(strBoundary, true);
        baos.write(s.getBytes(StandardCharsets.UTF_8));

        MIMEConfigPartial mimeCfg = new MIMEConfigPartial();
        InputStream is = null;
        MIMEMessagePartial mimeMsg = null;
        boolean res;

        // calculate the boundary of the Content-Disposition header of second attachment
        int szBoundary = strBoundary.length() + "--\r\n".length();
        int szFirstHeader = szBoundary + szJsonHeader;
        int szJsonBody = stringJson.length();
        int breakPoint = szFirstHeader + szJsonBody / 2;
        int cntPartial = 0;
        try {
            is = new ByteArrayInputStream(baos.toByteArray(), 0, breakPoint);
            mimeMsg = new MIMEMessagePartial(is, strBoundary, mimeCfg);
            List<MIMEPartPartial> l = mimeMsg.getAttachmentsPartial();
            Assert.assertNotNull(l, "List MIMEPart is null");
            for (int i = 0; i < l.size(); i++) {
                MIMEPartPartial mimePart = l.get(i);
                String contentId = mimePart.getContentId();
                String contentType = mimePart.getContentType();
                res = mimePart.getIsPartial();
                if (res) {
                    cntPartial++;
                }
                List<String> lContentDisposition = mimePart.getHeader("Content-Disposition");
                Assert.assertNotNull(lContentDisposition, "contentDisposition null");
                res = lContentDisposition.size() == 1;
                Assert.assertTrue(res, "contentDisposition size != 1");
                Assert.assertNotNull(contentId);
                Assert.assertNotNull(contentType);
                InputStream isPart = mimePart.read();
                ByteArrayOutputStream baosCmp = new ByteArrayOutputStream();
                int v;
                while ((v = isPart.read()) != -1) {
                    baosCmp.write(v);
                }
                baosCmp.write("\n".getBytes(StandardCharsets.UTF_8));
                String mimeStreamVal = new String(baosCmp.toByteArray(), "UTF-8");
                Assert.assertNotNull(mimeStreamVal);
            }
            res = l.size() == 1; // for jsonString
            Assert.assertTrue(res, "attachment list size mismatch " + l.size());
            res = cntPartial == 1;
            Assert.assertTrue(res, String.format("cntPartial should be 1, is %d", cntPartial));
            mimeMsg.close();
            is.close();
        } catch (IOException e) {
            throw e;
        } finally {
            mimeMsg.close();
            is.close();
        }

    }

    /**
     *
     * @throws Exception from partial package.
     */
    @Test
    public void testMIMETwoFilesPartialJSONBeanFilesFirst() throws Exception {
        /*
         * Test MIMEParser that breaks at JSON Bean, the last part of attachments. This should fail and throw exception. We cannot process if the JSON
         * section not available.
         */
        MIMETestUtils mimeTestUtils = new MIMETestUtils();

        int numFiles = 2;
        int numCharsPerWord = 9;
        int numWordsPerLine = 8;
        int numLines = 10;
        String delimiter = " ";

        List<String> listHeader = new ArrayList<>();
        List<String> listBody = new ArrayList<>();

        String s;

        String strBoundary = "Boundary_123_test";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<String> listMsgReferences = new ArrayList<>();
        List<String> listAttachmentFilenames = new ArrayList<>();
        // write to array and use for reference later
        String filename;
        for (int i = 0; i < numFiles; i++) {
            filename = String.format("file_%d", i);
            listAttachmentFilenames.add(filename);
        }

        for (int i = 0; i < numFiles; i++) {
            filename = String.format("file_%d", i);
            s = mimeTestUtils.getBoundaryString(strBoundary, false);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            s = mimeTestUtils.getAttachmentHeader(filename);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            listHeader.add(s);
            s = mimeTestUtils.randStringLines(numCharsPerWord, numWordsPerLine, numLines, delimiter);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            listBody.add(s);
        }

        s = mimeTestUtils.getBoundaryString(strBoundary, false);
        baos.write(s.getBytes(StandardCharsets.UTF_8));
        s = mimeTestUtils.getAttachmentHeader(MIMETestUtils.ContentType.JSON);
        baos.write(s.getBytes(StandardCharsets.UTF_8));
        int szJsonHeader = s.length();

        s = mimeTestUtils.getJSONBeanString(listMsgReferences, listAttachmentFilenames);
        baos.write(s.getBytes(StandardCharsets.UTF_8));
        int szJsonBody = s.length();

        s = mimeTestUtils.getBoundaryString(strBoundary, true);
        baos.write(s.getBytes(StandardCharsets.UTF_8));

        MIMEConfigPartial mimeCfg = new MIMEConfigPartial();
        InputStream is = null;
        MIMEMessagePartial mimeMsg = null;
        boolean res;

        int szBoundary = strBoundary.length() + "--\r\n".length();
        int szAttachments = szBoundary * 3;
        for (int i = 0; i < numFiles; i++) {
            szAttachments += listHeader.get(i).length();
            szAttachments += listBody.get(i).length();
        }
        int breakPoint = szAttachments + szJsonHeader + szJsonBody / 2;
        int cntPartial = 0;
        try {
            is = new ByteArrayInputStream(baos.toByteArray(), 0, breakPoint);
            mimeMsg = new MIMEMessagePartial(is, strBoundary, mimeCfg);
            List<MIMEPartPartial> l = mimeMsg.getAttachmentsPartial();
            Assert.assertNotNull(l, "List MIMEPart is null");
            // MIMEPart mimePartJSON = mimeMsg.getPart("0");
            int idx = 0;
            for (int i = 0; i < l.size(); i++) {
                MIMEPartPartial mimePart = l.get(i);
                String contentId = mimePart.getContentId();
                String contentType = mimePart.getContentType();
                res = mimePart.getIsPartial();
                if (res) {
                    cntPartial++;
                }
                List<String> lContentDisposition = mimePart.getHeader("Content-Disposition");
                Assert.assertNotNull(lContentDisposition, "contentDisposition null");
                res = lContentDisposition.size() == 1;
                Assert.assertTrue(res, "contentDisposition size != 1");
                // String contentDisposition = lContentDisposition.get(0);
                // parse contentDisposition for filename
                Assert.assertNotNull(contentId);
                Assert.assertNotNull(contentType);
                InputStream isPart = mimePart.read();
                ByteArrayOutputStream baosCmp = new ByteArrayOutputStream();
                int v;
                while ((v = isPart.read()) != -1) {
                    baosCmp.write(v);
                }
                baosCmp.write("\n".getBytes(StandardCharsets.UTF_8));
                String mimeStreamVal = new String(baosCmp.toByteArray(), "UTF-8");
                Assert.assertNotNull(mimeStreamVal);
                if (contentType.equals("application/json")) {
                    res = mimePart.getIsPartial();
                    Assert.assertTrue(res, "application/json should be partial");
                    continue;
                }
                String mimeReference = listBody.get(idx);
                res = mimeReference.equals(mimeStreamVal);
                if (!res) {
                    int szRef = mimeReference.length();
                    int szMsg = mimeStreamVal.length();
                    res = szRef == szMsg;
                    Assert.assertTrue(res, "content length mismatch");
                }
                Assert.assertTrue(res, "string compare mismatch for " + listHeader.get(idx));
                idx++;
            }
            res = l.size() == (listHeader.size() + 1); // +1 for jsonString
            Assert.assertTrue(res, "attachment list size mismatch " + l.size());
            res = cntPartial == 1;
            Assert.assertTrue(res, String.format("cntPartial should be 1, is %d", cntPartial));
            mimeMsg.close();
            is.close();
        } catch (IOException e) {
            throw e;
        } finally {
            mimeMsg.close();
            is.close();
        }

    }

    /**
     * testMIMETwoFilesPartialContentDisposition test MIMEParser with 1 good attachment and 1 partial attachment broken at Content-Disposition header.
     * MIMEMessage.getAttachments() should return 1 full attachment.
     *
     * @throws Exception from partial package.
     */
    @Test
    public void testMIMETwoFilesPartialContentDisposition() throws Exception {
        MIMETestUtils mimeTestUtils = new MIMETestUtils();

        int numCharsPerWord = 9;
        int numWordsPerLine = 8;
        int numLines = 20;
        String delimiter = " ";

        List<String> listHeader = new ArrayList<>();
        List<String> listBody = new ArrayList<>();

        String s;
        int numFiles = 2;

        String strBoundary = "Boundary_123_test";
        int szBoundary = strBoundary.length() + "--\r\n".length();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // write to array and use for reference later
        for (int i = 0; i < numFiles; i++) {
            s = mimeTestUtils.getBoundaryString(strBoundary, false);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            s = mimeTestUtils.getAttachmentHeader(String.format("file_%d", i));
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            listHeader.add(s);
            s = mimeTestUtils.randStringLines(numCharsPerWord, numWordsPerLine, numLines, delimiter);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            baos.write("\r\n".getBytes(StandardCharsets.UTF_8));
            listBody.add(s);
        }
        s = mimeTestUtils.getBoundaryString(strBoundary, true);
        baos.write(s.getBytes(StandardCharsets.UTF_8));
        String stringReference = new String(baos.toByteArray(), "UTF-8");
        Assert.assertNotNull(stringReference, "stringReference is null");

        // calculate the boundary of the Content-Disposition header of second attachment
        int szFirstAttach = szBoundary + listHeader.get(0).length() + listBody.get(0).length() + szBoundary;
        int szSecondHeader = listHeader.get(1).length();
        int breakPoint = szFirstAttach + szSecondHeader / 2;

        MIMEConfigPartial mimeCfg = new MIMEConfigPartial();
        InputStream is = null;
        MIMEMessagePartial mimeMsg = null;
        boolean res;
        int cntPartial = 0;

        try {
            // use only partial byte array
            final byte[] ba = baos.toByteArray();
            final byte[] baPartial = Arrays.copyOfRange(ba, 0, breakPoint);
            final String strPartial = new String(baPartial, StandardCharsets.UTF_8);
            Assert.assertNotNull(strPartial, "partial string should not be null");
            is = new ByteArrayInputStream(baPartial);
            mimeMsg = new MIMEMessagePartial(is, strBoundary, mimeCfg);
            List<MIMEPartPartial> l = mimeMsg.getAttachmentsPartial();
            Assert.assertNotNull(l, "List MIMEPart is null");
            boolean isPartial = mimeMsg.getIsPartial();
            Assert.assertTrue(isPartial, "MIME should be partial");
            for (int i = 0; i < l.size(); i++) {
                MIMEPartPartial mimePart = l.get(i);
                String contentId = mimePart.getContentId();
                String contentType = mimePart.getContentType();
                res = mimePart.getIsPartial();
                if (res) {
                    cntPartial++;
                }
                Assert.assertNotNull(contentId);
                Assert.assertNotNull(contentType);
                InputStream isPart = mimePart.read();
                ByteArrayOutputStream baosCmp = new ByteArrayOutputStream();
                int v;
                while ((v = isPart.read()) != -1) {
                    baosCmp.write(v);
                }

                String mimeStreamVal = new String(baosCmp.toByteArray(), "UTF-8");
                Assert.assertNotNull(mimeStreamVal);
            }
            res = l.size() == 1;
            Assert.assertTrue(res, "attachment list size should be 1 but is " + l.size());
            res = cntPartial == 0;
            Assert.assertTrue(res, String.format("cntPartial should be 0, is %d", cntPartial));

            mimeMsg.close();
            is.close();
        } catch (IOException e) {
            throw e;
        } finally {
            mimeMsg.close();
            is.close();
        }

    }

    /**
     * testMIMEPartialAtBoundaryString tests parsing a MIME multipart that cuts off at the boundary string.
     *
     * The result is that the partial last attachment should have length greater than the actual attachment, and the message itself should be partial.
     *
     * @throws Exception exception.
     */
    @Test
    public void testMIMEPartialAtEndBoundaryString() throws Exception {
        testMIMEPartialAtEndBoundaryString(2);
    }

    /**
     *
     * @param numAttachments int.
     * @throws Exception Exception.
     */
    private void testMIMEPartialAtEndBoundaryString(final int numAttachments) throws Exception {
        MIMETestUtils mimeTestUtils = new MIMETestUtils();
        LinkedHashMap<String, HashMap<String, String>> linkedHashMap = new LinkedHashMap<>();
        final int szPerAttachment = 1000;
        final String attachmentNamePrefix = "attachment";
        final String boundary = "boundaryString123";
        MIMEConfigPartial mimeConfig = new MIMEConfigPartial();
        MIMEMessagePartial mimeMsg = null;
        InputStream is = null;
        mimeTestUtils.generateAttachments(linkedHashMap, numAttachments, szPerAttachment, attachmentNamePrefix);

        /*
         * Create the MIME message. Then calculate the last string boundary length. Break off somewhere at last string boundary.
         */
        try {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, HashMap<String, String>> e : linkedHashMap.entrySet()) {
                HashMap<String, String> v = e.getValue();
                sb.append(mimeTestUtils.getBoundaryString(boundary, false));
                sb.append(v.get(mimeTestUtils.getMapKeyHeader()));
                sb.append(v.get(mimeTestUtils.getMapKeyAttachment()));
            }
            String s = mimeTestUtils.getBoundaryString(boundary, true);
            sb.append(s);
            byte[] ba = sb.toString().getBytes(StandardCharsets.UTF_8);
            int szMsg = ba.length;
            int szBoundaryEnd = s.getBytes(StandardCharsets.UTF_8).length;
            int breakpoint = szMsg - (szBoundaryEnd / 2);
            is = new ByteArrayInputStream(ba, 0, breakpoint);
            mimeMsg = new MIMEMessagePartial(is, boundary, mimeConfig);
            boolean res = false;
            int cntPartial = 0;
            List<MIMEPartPartial> l = mimeMsg.getAttachmentsPartial();
            Assert.assertNotNull(l, "List MIMEPart is null");
            for (int i = 0; i < l.size(); i++) {
                MIMEPartPartial mimePart = l.get(i);
                String contentId = mimePart.getContentId();
                String contentType = mimePart.getContentType();
                Assert.assertNotNull(contentId);
                Assert.assertNotNull(contentType);
                res = mimePart.getIsPartial();
                if (res) {
                    cntPartial++;
                }
                InputStream isPart = mimePart.read();
                ByteArrayOutputStream baosCmp = new ByteArrayOutputStream();
                int v;
                while ((v = isPart.read()) != -1) {
                    baosCmp.write(v);
                }

                String mimeStreamVal = new String(baosCmp.toByteArray(), "UTF-8");
                Assert.assertNotNull(mimeStreamVal);
            }
            res = l.size() == numAttachments;
            Assert.assertTrue(res, "attachment list size should not be " + l.size());
            res = cntPartial == 1;
            Assert.assertTrue(res, String.format("cntPartial should be 1, is %d", cntPartial));

        } finally {
            is.close();
            mimeMsg.close();
        }
    }

    /**
     *
     * @throws Exception exception.
     */
    @Test
    public void testMIMEPartialAtBeginBoundaryString() throws Exception {
        testMIMEPartialAtBeginBoundaryString(3);
    }

    /**
     *
     * @param numAttachments int.
     * @throws Exception exception.
     */
    private void testMIMEPartialAtBeginBoundaryString(final int numAttachments) throws Exception {
        MIMETestUtils mimeTestUtils = new MIMETestUtils();
        LinkedHashMap<String, HashMap<String, String>> linkedHashMap = new LinkedHashMap<>();
        final int szPerAttachment = 1000;
        final String attachmentNamePrefix = "attachment";
        final String boundary = "boundaryString123";
        MIMEConfigPartial mimeConfig = new MIMEConfigPartial();
        MIMEMessagePartial mimeMsg = null;
        InputStream is = null;
        mimeTestUtils.generateAttachments(linkedHashMap, numAttachments, szPerAttachment, attachmentNamePrefix);

        /*
         * Create the MIME message. Then calculate the last string boundary length. Break off somewhere at last string boundary.
         */
        try {
            StringBuilder sb = new StringBuilder();
            String lastFilename = null;
            for (Map.Entry<String, HashMap<String, String>> e : linkedHashMap.entrySet()) {
                lastFilename = e.getKey();
                HashMap<String, String> v = e.getValue();
                sb.append(mimeTestUtils.getBoundaryString(boundary, false));
                sb.append(v.get(mimeTestUtils.getMapKeyHeader()));
                sb.append(v.get(mimeTestUtils.getMapKeyAttachment()));
                sb.append("\r\n");
            }
            String s = mimeTestUtils.getBoundaryString(boundary, true);
            sb.append(s);
            int szBoundaryEnd = s.getBytes(StandardCharsets.UTF_8).length;
            s = mimeTestUtils.getBoundaryString(boundary, false);
            int szBoundaryStart = s.getBytes(StandardCharsets.UTF_8).length;

            final String referenceString = sb.toString();
            byte[] ba = referenceString.getBytes(StandardCharsets.UTF_8);
            int szMsg = ba.length;

            /*
             * Calculate the size of the last attachment (boundary and all).
             */
            HashMap<String, String> hashmap = linkedHashMap.get(lastFilename);

            s = hashmap.get(mimeTestUtils.getMapKeyHeader());
            int szHeader = s.getBytes(StandardCharsets.UTF_8).length;

            s = hashmap.get(mimeTestUtils.getMapKeyAttachment());
            int szAttach = s.getBytes(StandardCharsets.UTF_8).length;

            int szLastAttachment = szBoundaryStart + szHeader + szAttach + szBoundaryEnd;
            int breakpoint = szMsg - szLastAttachment + (szBoundaryStart / 2);

            is = new ByteArrayInputStream(ba, 0, breakpoint);
            mimeMsg = new MIMEMessagePartial(is, boundary, mimeConfig);
            boolean res = false;
            int cntPartial = 0;
            List<MIMEPartPartial> l = mimeMsg.getAttachmentsPartial();
            Assert.assertNotNull(l, "List MIMEPart is null");
            for (int i = 0; i < l.size(); i++) {
                MIMEPartPartial mimePart = l.get(i);
                String contentId = mimePart.getContentId();
                String contentType = mimePart.getContentType();
                Assert.assertNotNull(contentId);
                Assert.assertNotNull(contentType);
                res = mimePart.getIsPartial();
                if (res) {
                    cntPartial++;
                }
                InputStream isPart = mimePart.read();
                ByteArrayOutputStream baosCmp = new ByteArrayOutputStream();
                int v;
                while ((v = isPart.read()) != -1) {
                    baosCmp.write(v);
                }

                String mimeStreamVal = new String(baosCmp.toByteArray(), "UTF-8");
                Assert.assertNotNull(mimeStreamVal);
            }
            res = l.size() == (numAttachments - 1);
            Assert.assertTrue(res, "attachment list size should not be " + l.size());
            res = cntPartial == 1;
            Assert.assertTrue(res, String.format("cntPartial should be 1, is %d", cntPartial));

        } finally {
            is.close();
            mimeMsg.close();
        }
    }

    /**
     * testMIMETwoFilesPartialContentType test MIMEParser with 1 good attachment and 1 partial attachment broken at Content-Disposition header.
     * MIMEMessage.getAttachments() should return 1 full attachment.
     *
     * @throws Exception from partial package.
     */
    @Test
    public void testMIMETwoFilesPartialContentType() throws Exception {
        MIMETestUtils mimeTestUtils = new MIMETestUtils();

        int numCharsPerWord = 9;
        int numWordsPerLine = 8;
        int numLines = 20;
        String delimiter = " ";

        List<String> listHeader = new ArrayList<>();
        List<String> listBody = new ArrayList<>();

        String s;
        int numFiles = 2;

        String strBoundary = "Boundary_123_test";
        int szBoundary = strBoundary.length() + "--\r\n".length();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // write to array and use for reference later
        for (int i = 0; i < numFiles; i++) {
            s = mimeTestUtils.getBoundaryString(strBoundary, false);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            s = mimeTestUtils.getAttachmentHeader(String.format("file_%d", i));
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            listHeader.add(s);
            s = mimeTestUtils.randStringLines(numCharsPerWord, numWordsPerLine, numLines, delimiter);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            baos.write("\r\n".getBytes(StandardCharsets.UTF_8));
            listBody.add(s);
        }
        s = mimeTestUtils.getBoundaryString(strBoundary, true);
        baos.write(s.getBytes(StandardCharsets.UTF_8));
        String stringReference = new String(baos.toByteArray(), "UTF-8");
        Assert.assertNotNull(stringReference, "stringReference is null");

        // calculate the boundary of the Content-Disposition header of second attachment
        int szFirstAttach = szBoundary + listHeader.get(0).length() + listBody.get(0).length() + szBoundary;
        int breakPoint = szFirstAttach + 5;

        MIMEConfigPartial mimeCfg = new MIMEConfigPartial();
        InputStream is = null;
        MIMEMessagePartial mimeMsg = null;
        boolean res;
        int cntPartial = 0;

        try {
            // use only partial byte array
            final byte[] ba = baos.toByteArray();
            final byte[] baPartial = Arrays.copyOfRange(ba, 0, breakPoint);
            is = new ByteArrayInputStream(baPartial);
            final String strPartial = new String(baPartial, StandardCharsets.UTF_8);
            Assert.assertNotNull(strPartial, "string Partial not expected to be null");
            mimeMsg = new MIMEMessagePartial(is, strBoundary, mimeCfg);
            List<MIMEPartPartial> l = mimeMsg.getAttachmentsPartial();
            Assert.assertNotNull(l, "List MIMEPart is null");
            final boolean isPartial = mimeMsg.getIsPartial();
            Assert.assertTrue(isPartial, "MIMEMessage expected to be partial");
            for (int i = 0; i < l.size(); i++) {
                MIMEPartPartial mimePart = l.get(i);
                String contentId = mimePart.getContentId();
                String contentType = mimePart.getContentType();
                res = mimePart.getIsPartial();
                if (res) {
                    cntPartial++;
                }
                Assert.assertNotNull(contentId);
                Assert.assertNotNull(contentType);
                InputStream isPart = mimePart.read();
                ByteArrayOutputStream baosCmp = new ByteArrayOutputStream();
                int v;
                while ((v = isPart.read()) != -1) {
                    baosCmp.write(v);
                }

                String mimeStreamVal = new String(baosCmp.toByteArray(), "UTF-8");
                Assert.assertNotNull(mimeStreamVal);
            }
            res = l.size() == 1;
            Assert.assertTrue(res, "attachment list size should be 1 but is " + l.size());
            res = cntPartial == 0;
            Assert.assertTrue(res, String.format("cntPartial should be 0, is %d", cntPartial));

            mimeMsg.close();
            is.close();
        } catch (IOException e) {
            throw e;
        } finally {
            mimeMsg.close();
            is.close();
        }

    }

    /**
     *
     * @throws Exception from partial package.
     */
    @Test
    public void testMIMETwoFilesPartialBody() throws Exception {
        /*
         * test MIMEParser with 1 good attachment and 1 partial attachment broken at content body. MIMEMessage.getAttachments() should return 1 full
         * and 1 partial attachment.
         */
        MIMETestUtils mimeTestUtils = new MIMETestUtils();

        int numCharsPerWord = 9;
        int numWordsPerLine = 8;
        int numLines = 20;
        String delimiter = " ";

        List<String> listHeader = new ArrayList<>();
        List<String> listBody = new ArrayList<>();

        String s;
        int numFiles = 2;

        String strBoundary = "Boundary_123_test";
        int szBoundary = strBoundary.length() + "--\r\n".length();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // write to array and use for reference later
        for (int i = 0; i < numFiles; i++) {
            s = mimeTestUtils.getBoundaryString(strBoundary, false);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            s = mimeTestUtils.getAttachmentHeader(String.format("file_%d", i));
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            listHeader.add(s);
            s = mimeTestUtils.randStringLines(numCharsPerWord, numWordsPerLine, numLines, delimiter);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            listBody.add(s);
        }
        s = mimeTestUtils.getBoundaryString(strBoundary, true);
        baos.write(s.getBytes(StandardCharsets.UTF_8));
        String stringReference = new String(baos.toByteArray(), "UTF-8");
        Assert.assertNotNull(stringReference, "stringReference is null");

        // calculate the boundary of the Content-Disposition header of second attachment
        int szFirstAttach = szBoundary + listHeader.get(0).length() + listBody.get(0).length() + szBoundary;
        int szSecondAttach = listHeader.get(1).length() + listBody.get(1).length() + szBoundary;
        int breakPoint = szFirstAttach + szSecondAttach / 2;

        MIMEConfigPartial mimeCfg = new MIMEConfigPartial();
        InputStream is = null;
        MIMEMessagePartial mimeMsg = null;
        int cntPartial = 0;

        boolean res;
        try {
            // use only partial byte array
            is = new ByteArrayInputStream(baos.toByteArray(), 0, breakPoint);
            mimeMsg = new MIMEMessagePartial(is, strBoundary, mimeCfg);
            List<MIMEPartPartial> l = mimeMsg.getAttachmentsPartial();
            Assert.assertNotNull(l, "List MIMEPart is null");
            for (int i = 0; i < l.size(); i++) {
                MIMEPartPartial mimePart = l.get(i);
                String contentId = mimePart.getContentId();
                String contentType = mimePart.getContentType();
                res = mimePart.getIsPartial();
                if (res) {
                    cntPartial++;
                }

                Assert.assertNotNull(contentId);
                Assert.assertNotNull(contentType);
                InputStream isPart = mimePart.read();
                ByteArrayOutputStream baosCmp = new ByteArrayOutputStream();
                int v;
                while ((v = isPart.read()) != -1) {
                    baosCmp.write(v);
                }

                String mimeStreamVal = new String(baosCmp.toByteArray(), "UTF-8");
                Assert.assertNotNull(mimeStreamVal);
            }
            res = l.size() == 2;
            Assert.assertTrue(res, "attachment list size should be 2 but is " + l.size());
            res = cntPartial == 1;
            Assert.assertTrue(res, String.format("cntPartial should be 1, is %d", cntPartial));
            mimeMsg.close();
            is.close();
        } catch (IOException e) {
            throw e;
        } finally {
            mimeMsg.close();
            is.close();
        }

    }

    /**
     * testMIMEPartialContentDispositionNoReturn test MIMEParser with 0 good attachment where the attachment is broken at Content-Disposition header.
     *
     * @throws Exception from partial package.
     */
    @Test(expectedExceptions = MIMEParsingException.class, expectedExceptionsMessageRegExp = "MIMEMessage has no parts")
    public void testMIMEPartialContentDispositionNoReturn() throws Exception {
        MIMETestUtils mimeTestUtils = new MIMETestUtils();

        int numCharsPerWord = 9;
        int numWordsPerLine = 8;
        int numLines = 20;
        String delimiter = " ";

        List<String> listHeader = new ArrayList<>();
        List<String> listBody = new ArrayList<>();

        String s;
        int numFiles = 1;

        String strBoundary = "Boundary_123_test";
        int szBoundary = strBoundary.length() + "--\r\n".length();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // write to array and use for reference later
        for (int i = 0; i < numFiles; i++) {
            s = mimeTestUtils.getBoundaryString(strBoundary, false);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            s = mimeTestUtils.getAttachmentHeader(String.format("file_%d", i));
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            listHeader.add(s);
            s = mimeTestUtils.randStringLines(numCharsPerWord, numWordsPerLine, numLines, delimiter);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            listBody.add(s);
        }
        s = mimeTestUtils.getBoundaryString(strBoundary, true);
        baos.write(s.getBytes(StandardCharsets.UTF_8));
        String stringReference = new String(baos.toByteArray(), "UTF-8");
        Assert.assertNotNull(stringReference, "stringReference is null");

        // calculate the boundary of the Content-Disposition header of second attachment
        int szFirstHeader = szBoundary + listHeader.get(0).length();
        int breakPoint = szFirstHeader / 2;

        MIMEConfigPartial mimeCfg = new MIMEConfigPartial();
        InputStream is = null;
        MIMEMessagePartial mimeMsg = null;
        boolean res;
        try {
            // use only partial byte array
            final byte[] ba = baos.toByteArray();
            final byte[] baPartial = Arrays.copyOfRange(ba, 0, breakPoint);
            is = new ByteArrayInputStream(baPartial);
            final String strPartial = new String(baPartial, StandardCharsets.UTF_8);
            Assert.assertNotNull(strPartial, "strPartial should not be null");
            mimeMsg = new MIMEMessagePartial(is, strBoundary, mimeCfg);
            List<MIMEPartPartial> l = mimeMsg.getAttachmentsPartial();
            Assert.assertNotNull(l, "List MIMEPart is null");
            for (MIMEPart mimePart : l) {
                String contentId = mimePart.getContentId();
                String contentType = mimePart.getContentType();
                Assert.assertNotNull(contentId);
                Assert.assertNotNull(contentType);
                InputStream isPart = mimePart.read();
                ByteArrayOutputStream baosCmp = new ByteArrayOutputStream();
                int v;
                while ((v = isPart.read()) != -1) {
                    baosCmp.write(v);
                }

                String mimeStreamVal = new String(baosCmp.toByteArray(), "UTF-8");
                Assert.assertNotNull(mimeStreamVal);
            }
            res = l.size() == 1;
            Assert.assertTrue(res, "attachment list size should be 1 but is " + l.size());
            mimeMsg.close();
            is.close();
        } catch (IOException e) {
            throw e;
        } finally {
            mimeMsg.close();
            is.close();
        }

    }

    /**
     *
     * @throws Exception from partial package.
     */
    @Test
    public void testMIMEOneFilePartialBody() throws Exception {
        /*
         * test MIMEParser with 1 good partial attachment broken at content body. MIMEMessage.getAttachments() should return 1 partial attachment.
         */
        MIMETestUtils mimeTestUtils = new MIMETestUtils();

        int numCharsPerWord = 9;
        int numWordsPerLine = 8;
        int numLines = 20;
        String delimiter = " ";

        List<String> listHeader = new ArrayList<>();
        List<String> listBody = new ArrayList<>();

        String s;
        int numFiles = 1;

        String strBoundary = "Boundary_123_test";
        int szBoundary = strBoundary.length() + "--\r\n".length();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // write to array and use for reference later
        for (int i = 0; i < numFiles; i++) {
            s = mimeTestUtils.getBoundaryString(strBoundary, false);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            s = mimeTestUtils.getAttachmentHeader(String.format("file_%d", i));
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            listHeader.add(s);
            s = mimeTestUtils.randStringLines(numCharsPerWord, numWordsPerLine, numLines, delimiter);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            listBody.add(s);
        }
        s = mimeTestUtils.getBoundaryString(strBoundary, true);
        baos.write(s.getBytes(StandardCharsets.UTF_8));
        String stringReference = new String(baos.toByteArray(), "UTF-8");
        Assert.assertNotNull(stringReference, "stringReference is null");

        // calculate the boundary of the Content-Disposition header of second attachment
        int szFirstAttach = szBoundary + listHeader.get(0).length() + listBody.get(0).length() + szBoundary;
        int breakPoint = szFirstAttach / 2;

        MIMEConfigPartial mimeCfg = new MIMEConfigPartial();
        InputStream is = null;
        MIMEMessagePartial mimeMsg = null;
        boolean res;
        int cntPartial = 0;
        try {
            // use only partial byte array
            is = new ByteArrayInputStream(baos.toByteArray(), 0, breakPoint);
            mimeMsg = new MIMEMessagePartial(is, strBoundary, mimeCfg);
            List<MIMEPartPartial> l = mimeMsg.getAttachmentsPartial();
            Assert.assertNotNull(l, "List MIMEPart is null");
            for (int i = 0; i < l.size(); i++) {
                MIMEPartPartial mimePart = l.get(i);
                String contentId = mimePart.getContentId();
                String contentType = mimePart.getContentType();
                res = mimePart.getIsPartial();
                if (res) {
                    cntPartial++;
                }
                Assert.assertNotNull(contentId);
                Assert.assertNotNull(contentType);
                InputStream isPart = mimePart.read();
                ByteArrayOutputStream baosCmp = new ByteArrayOutputStream();
                int v;
                while ((v = isPart.read()) != -1) {
                    baosCmp.write(v);
                }

                String mimeStreamVal = new String(baosCmp.toByteArray(), "UTF-8");
                Assert.assertNotNull(mimeStreamVal);
            }
            res = l.size() == 1;
            Assert.assertTrue(res, "attachment list size should be 0 but is " + l.size());
            res = cntPartial == 1;
            Assert.assertTrue(res, String.format("cntPartial should be 1, is %d", cntPartial));
            mimeMsg.close();
            is.close();
        } catch (IOException e) {
            throw e;
        } finally {
            mimeMsg.close();
            is.close();
        }

    }

    /**
     *
     * @throws Exception from partial package.
     */
    @Test
    public void testMIMEOneFilePartialBodyAtBoundary() throws Exception {
        /*
         * test MIMEParser with 1 good partial attachment broken at end of boundary. MIMEMessage.getAttachments() should return 1 partial attachment.
         */
        MIMETestUtils mimeTestUtils = new MIMETestUtils();

        int numCharsPerWord = 9;
        int numWordsPerLine = 8;
        int numLines = 20;
        String delimiter = " ";

        List<String> listHeader = new ArrayList<>();
        List<String> listBody = new ArrayList<>();

        String s;
        int numFiles = 1;

        String strBoundary = "Boundary_123_test";
        int szBoundary = strBoundary.length() + "--\r\n".length();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // write to array and use for reference later
        for (int i = 0; i < numFiles; i++) {
            s = mimeTestUtils.getBoundaryString(strBoundary, false);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            s = mimeTestUtils.getAttachmentHeader(String.format("file_%d", i));
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            listHeader.add(s);
            s = mimeTestUtils.randStringLines(numCharsPerWord, numWordsPerLine, numLines, delimiter);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            listBody.add(s);
        }
        s = mimeTestUtils.getBoundaryString(strBoundary, true);
        baos.write(s.getBytes(StandardCharsets.UTF_8));
        String stringReference = new String(baos.toByteArray(), "UTF-8");
        Assert.assertNotNull(stringReference, "stringReference is null");

        // calculate the boundary of the Content-Disposition header of second attachment
        int szFirstAttach = szBoundary + listHeader.get(0).length() + listBody.get(0).length();
        int breakPoint = szFirstAttach + szBoundary / 2;

        MIMEConfigPartial mimeCfg = new MIMEConfigPartial();
        InputStream is = null;
        MIMEMessagePartial mimeMsg = null;
        int cntPartial = 0;
        boolean res;
        try {
            // use only partial byte array
            {
                InputStream isParser = new ByteArrayInputStream(baos.toByteArray());
                MIMEParserPartial mimeParser = new MIMEParserPartial(isParser, strBoundary, new MIMEConfigPartial());
                Assert.assertNotNull(mimeParser);
            }
            is = new ByteArrayInputStream(baos.toByteArray(), 0, breakPoint);
            mimeMsg = new MIMEMessagePartial(is, strBoundary, mimeCfg);
            List<MIMEPartPartial> l = mimeMsg.getAttachmentsPartial();
            Assert.assertNotNull(l, "List MIMEPart is null");
            for (int i = 0; i < l.size(); i++) {
                MIMEPartPartial mimePart = l.get(i);
                String contentId = mimePart.getContentId();
                String contentType = mimePart.getContentType();
                Assert.assertNotNull(contentId);
                Assert.assertNotNull(contentType);
                res = mimePart.getIsPartial();
                if (res) {
                    cntPartial++;
                }
                InputStream isPart = mimePart.read();
                ByteArrayOutputStream baosCmp = new ByteArrayOutputStream();
                int v;
                while ((v = isPart.read()) != -1) {
                    baosCmp.write(v);
                }

                String mimeStreamVal = new String(baosCmp.toByteArray(), "UTF-8");
                Assert.assertNotNull(mimeStreamVal);
            }
            res = l.size() == 1;
            Assert.assertTrue(res, "attachment list size should be 0 but is " + l.size());
            res = cntPartial == 1;
            Assert.assertTrue(res, String.format("cntPartial should be 1, is %d", cntPartial));
            mimeMsg.close();
            is.close();
        } catch (IOException e) {
            throw e;
        } finally {
            mimeMsg.close();
            is.close();
        }
    }

    /**
     * testSimpleBase64FormatOddBoundary uses MIMEMessagePartial and Base64 encoding, truncating MIME Base64 content at o partial binary byte. The
     * mime parser should truncate at mod 4 boundary.
     *
     * @throws Exception from partial package.
     */
    @Test
    public void testSimpleBase64FormatOddStringBoundary() throws Exception {
        final MIMETestUtils mimeTestUtils = new MIMETestUtils();
        final String strSet = "abcdefABCDEF0123456789";
        mimeTestUtils.loadStringSet(strSet);
        final int numChars = 20000;
        final int fileBreakpoint = 9333;
        final String sRef = mimeTestUtils.randString(numChars);
        final String subStringRef = sRef.substring(0, fileBreakpoint);
        final String strBoundary = "boundary123_test";
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final String filename = "file1";
        String s;
        // create the reference
        {
            // JSON portion
            s = mimeTestUtils.getBoundaryString(strBoundary, false);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            s = mimeTestUtils.getAttachmentHeader(MIMETestUtils.ContentType.JSON);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            final List<String> listMsgReferences = new java.util.ArrayList<>();
            final List<String> listAttachmentFilenames = new java.util.ArrayList<>();
            listAttachmentFilenames.add(filename);
            listMsgReferences.add(filename);
            String stringJson = mimeTestUtils.getJSONBeanString(listMsgReferences, listAttachmentFilenames);
            baos.write(stringJson.getBytes(StandardCharsets.UTF_8));
            s = mimeTestUtils.getBoundaryString(strBoundary, false);
            s = "\n" + s;
            baos.write(s.getBytes(StandardCharsets.UTF_8));

            // ATTACHMENT portion
            s = "Content-Type: application/octet-stream\n";
            s += "Content-Transfer-Encoding: Base64\n";
            s += "Content-Disposition: form-data; filename=\"" + filename + "\"; name=\"" + filename + "\"\n\n";
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            baos.write(subStringRef.getBytes(StandardCharsets.UTF_8));
        }

        final MIMEConfigPartial mimeCfg = new MIMEConfigPartial();
        InputStream is = null;
        MIMEMessagePartial mimeMsg = null;
        boolean res;
        try {
            final String sMIMEMsg = baos.toString();
            Assert.assertNotNull(sMIMEMsg);
            is = new ByteArrayInputStream(baos.toByteArray());
            mimeMsg = new MIMEMessagePartial(is, strBoundary, mimeCfg);
            List<MIMEPartPartial> l = mimeMsg.getAttachmentsPartial();
            Assert.assertNotNull(l, "List MIMEPart is null");
            int cntPartial = 0;
            for (int i = 0; i < l.size(); i++) {
                MIMEPartPartial mimePart = l.get(i);
                String contentId = mimePart.getContentId();
                String contentType = mimePart.getContentType();
                res = mimePart.getIsPartial();
                if (!res) {
                    continue;
                }
                cntPartial++;
                List<String> lContentDisposition = mimePart.getHeader("Content-Disposition");
                Assert.assertNotNull(lContentDisposition, "contentDisposition null");
                res = lContentDisposition.size() == 1;
                Assert.assertTrue(res, "contentDisposition size != 1");
                Assert.assertNotNull(contentId);
                Assert.assertNotNull(contentType);
                InputStream isPart = mimePart.read();
                ByteArrayOutputStream baosCmp = new ByteArrayOutputStream();
                int v;
                while ((v = isPart.read()) != -1) {
                    baosCmp.write(v);
                }
                byte[] baOut = Base64.getEncoder().encode(baosCmp.toByteArray());
                String mimeStreamVal = new String(baOut, "UTF-8");
                Assert.assertNotNull(mimeStreamVal);
                final int szRead = mimeStreamVal.length();
                Assert.assertNotEquals(szRead, 0);
                final int substringSz = subStringRef.length();
                final int substringMod4 = substringSz % 4;
                final int substringTruncateSz = substringSz - substringMod4;
                final String substringTruncate = subStringRef.substring(0, substringTruncateSz);
                final int cmpInt = mimeStreamVal.compareTo(substringTruncate);
                Assert.assertEquals(cmpInt, 0);
            }
            Assert.assertEquals(cntPartial, 1);
        } finally {
            mimeMsg.close();
            is.close();
        }
    }

    /**
     * testSimpleBase64FormatPartial just converts binary bytes to Base64, and truncates MIME at fileBreakpoint in bytes. be mod 4, so no truncation
     * is needed. Then the comparison just reads the partial MIME attachment content in binary reference values.
     *
     * @throws Exception exception.
     */
    @Test
    public void testSimpleBase64FormatPartial() throws Exception {
        int numBytes = 20000;
        int fileBreakpoint = 9333;
        testSimpleBase64FormatPartial(numBytes, fileBreakpoint);
        numBytes = 2000;
        fileBreakpoint = 333;
        testSimpleBase64FormatPartial(numBytes, fileBreakpoint);
    }

    /**
     * testSimpleBase64FormatPartial just converts binary bytes to Base64, and truncates MIME at fileBreakpoint in bytes. be mod 4, so no truncation
     * is needed. Then the comparison just reads the partial MIME attachment content in binary reference values.
     *
     * @param numBytes int.
     * @param fileBreakpoint int.
     * @throws Exception exception.
     */
    private void testSimpleBase64FormatPartial(final int numBytes, final int fileBreakpoint) throws Exception {
        final MIMETestUtils mimeTestUtils = new MIMETestUtils();
        final String strSet = "abcdefABCDEF0123456789";
        mimeTestUtils.loadStringSet(strSet);
        final byte[] baRef = mimeTestUtils.randBytes(numBytes);
        final byte[] baSubArrayRef = java.util.Arrays.copyOfRange(baRef, 0, fileBreakpoint);
        final String strSubArrayRef = new String(Base64.getEncoder().encode(baSubArrayRef), "UTF-8");
        final String strBoundary = "boundary123_test";
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final String filename = "file1";
        String s;

        // create the reference
        {
            // JSON portion
            s = mimeTestUtils.getBoundaryString(strBoundary, false);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            s = mimeTestUtils.getAttachmentHeader(MIMETestUtils.ContentType.JSON);
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            final List<String> listMsgReferences = new java.util.ArrayList<>();
            final List<String> listAttachmentFilenames = new java.util.ArrayList<>();
            listAttachmentFilenames.add(filename);
            listMsgReferences.add(filename);
            String stringJson = mimeTestUtils.getJSONBeanString(listMsgReferences, listAttachmentFilenames);
            baos.write(stringJson.getBytes(StandardCharsets.UTF_8));
            s = mimeTestUtils.getBoundaryString(strBoundary, false);
            s = "\n" + s;
            baos.write(s.getBytes(StandardCharsets.UTF_8));

            // ATTACHMENT portion
            s = "Content-Type: application/octet-stream\n";
            s += "Content-Transfer-Encoding: Base64\n";
            s += "Content-Disposition: form-data; filename=\"" + filename + "\"; name=\"" + filename + "\"\n\n";
            baos.write(s.getBytes(StandardCharsets.UTF_8));
            baos.write(strSubArrayRef.getBytes(StandardCharsets.UTF_8));
        }
        final MIMEConfigPartial mimeCfg = new MIMEConfigPartial();
        InputStream is = null;
        MIMEMessagePartial mimeMsg = null;
        boolean res;
        try {
            final String sMIMEMsg = baos.toString();
            Assert.assertNotNull(sMIMEMsg);
            is = new ByteArrayInputStream(baos.toByteArray());
            mimeMsg = new MIMEMessagePartial(is, strBoundary, mimeCfg);
            List<MIMEPartPartial> l = mimeMsg.getAttachmentsPartial();
            Assert.assertNotNull(l, "List MIMEPart is null");
            int cntPartial = 0;

            // for each MIMEPart, should only be JSON and Attachments
            for (int i = 0; i < l.size(); i++) {
                MIMEPartPartial mimePart = l.get(i);
                String contentId = mimePart.getContentId();
                String contentType = mimePart.getContentType();
                res = mimePart.getIsPartial();
                if (!res) {
                    continue;
                }
                cntPartial++;
                List<String> lContentDisposition = mimePart.getHeader("Content-Disposition");
                Assert.assertNotNull(lContentDisposition, "contentDisposition null");
                res = lContentDisposition.size() == 1;
                Assert.assertTrue(res, "contentDisposition size != 1");
                Assert.assertNotNull(contentId);
                Assert.assertNotNull(contentType);
                InputStream isPart = mimePart.read();
                ByteArrayOutputStream baosCmp = new ByteArrayOutputStream();
                int v;
                while ((v = isPart.read()) != -1) {
                    baosCmp.write(v);
                }
                // compare byte array first
                final byte[] baCmp = baosCmp.toByteArray();
                Assert.assertNotNull(baCmp);
                final int szBaCmp = baCmp.length;
                Assert.assertNotEquals(szBaCmp, 0);
                res = java.util.Arrays.equals(baSubArrayRef, baCmp);
                Assert.assertEquals(res, true);

                // compare Base64 String value next
                String mimeStreamVal = new String(Base64.getEncoder().encode(baCmp), "UTF-8");
                Assert.assertNotNull(mimeStreamVal);
                final int szBase64Str = mimeStreamVal.length();
                Assert.assertNotEquals(szBase64Str, 0);
                final int cmpInt = strSubArrayRef.compareTo(mimeStreamVal);
                Assert.assertEquals(cmpInt, 0);
            }
            Assert.assertEquals(cntPartial, 1);
        } finally {
            mimeMsg.close();
            is.close();
        }
    }

    /**
     * testPartialBase64String tests MIME broken at third attachment Content-Type parameter. Breaking at this boundary should cause an exception. So
     * there should only be 2 full attachments, and the message should be partial.
     *
     * @throws Exception error.
     */
    @Test
    public void testPartialBase64String() throws Exception {
        final StringBuilder sb = new StringBuilder();
        final String strBoundary = "testBoundary123";
        {
            sb.append("--testBoundary123\r\n");
            sb.append("Content-Disposition: form-data; name=\"jsonString\"\r\n");
            sb.append("Content-Type: application/json\r\n");
            sb.append("Content-Transfer-Encoding: binary\r\n");
            sb.append("{\"actions\":{\"responseMessage\":true,\"responseMessageV2\":true},");
            sb.append("\"message\":{\"csid\":\"20001000\",\"flags\":{\"spam\":false,\"read\":true},");
            sb.append("\"headers\":{\"subject\":\"test partial message\",");
            sb.append("\"to\":[{\"fail\":false,\"email\":\"yqa_mail_5007191969827192@demobroadband.com\",");
            sb.append("\"name\":\"yqa_mail_5007191969827192@demobroadband.com\"}],");
            sb.append("\"from\":[{\"email\":\"yqa_mail_5007191969827192@demobroadband.com\",");
            sb.append("\"name\":\"yqa_mail_5007191969827192@demobroadband.com\"}]},");
            sb.append("\"folder\":{\"id\":\"3\"},\"newMessage\":true},");
            sb.append("\"simpleBody\":{\"html\":\"<html><head></head><body>hello this is a body</body></html>\",");
            sb.append("\"attachments\":[{\"disposition\":\"attachment\",");
            sb.append("\"multipartName\":\"multipart://attachment_00.txt\",");
            sb.append("\"contentId\":\"<ContentIdattachment_00.txt@yahoo.com>\"},");
            sb.append("{\"disposition\":\"attachment\",\"multipartName\":");
            sb.append("\"multipart://attachment_01.txt\",\"contentId\":\"<ContentIdattachment_01.txt@yahoo.com>\"},");
            sb.append("{\"disposition\":\"attachment\",\"multipartName\":");
            sb.append("\"multipart://attachment_02.txt\",\"contentId\":\"<ContentIdattachment_02.txt@yahoo.com>\"}]}}\r\n");
            sb.append("--testBoundary123\r\n");
            sb.append("Content-Disposition: form-data; name=\"attachment_00.txt\"; filename=\"attachment_00.txt\"\r\n");
            sb.append("Content-Transfer-Encoding: base64\r\n");
            sb.append("Content-Type: application/octet-stream\r\n");
            sb.append("\r\n");
            sb.append("MDAwMDAwMCAwMDAwMDAxIDAwMDAwMDIgMDAwMDAwMyAwMDAwMDA0IDAwMDAwMDUgMDAwMDAwNiAwMDAwMDA3IDAwMD");
            sb.append("AwMDggMDAwMDAwOQowMDAwMDEwIDAwMDAwMTEgMDAwMDAxMiAwMDAwMDEzIDAwMDAwMTQgMDAwMDAxNSAwMDAwMDE2");
            sb.append("IDAwMDAwMTcgMDAwMDAxOCAwMDAwMDE5CjAwMDAwMjAgMDAwMDAyMSAwMDAwMDIyIDAwMDAwMjMgMDAwMDAyNCAwMD");
            sb.append("AwMDI1IDAwMDAwMjYgMDAwMDAyNyAwMDAwMDI4IDAwMDAwMjkKMDAwMDAzMCAwMDAwMDMxIDAwMDAwMzIgMDAwMDAz");
            sb.append("MyAwMDAwMDM0IDAwMDAwMzUgMDAwMDAzNiAwMDAwMDM3IDAwMDAwMzggMDAwMDAzOQowMDAwMDQwIDAwMDAwNDEgMD");
            sb.append("AwMDA0MiAwMDAwMDQzIDAwMDAwNDQgMDAwMDA0NSAwMDAwMDQ2IDAwMDAwNDcgMDAwMDA0OCAwMDAwMDQ5CjAwMDAw");
            sb.append("NTAgMDAwMDA1MSAwMDAwMDUyIDAwMDAwNTMgMDAwMDA1NCAwMDAwMDU1IDAwMDAwNTYgMDAwMDA1NyAwMDAwMDU4ID");
            sb.append("AwMDAwNTkKMDAwMDA2MCAwMDAwMDYxIA==\r\n");
            sb.append("--testBoundary123\r\n");
            sb.append("Content-Disposition: form-data; name=\"attachment_01.txt\"; filename=\"attachment_01.txt\"\r\n");
            sb.append("Content-Transfer-Encoding: base64\r\n");
            sb.append("Content-Type: application/octet-stream\r\n");
            sb.append("\r\n");
            sb.append("MDAwMDAwMCAwMDAwMDAxIDAwMDAwMDIgMDAwMDAwMyAwMDAwMDA0IDAwMDAwMDUgMDAwMDAwNiAwMDAwMDA3IDAwMD");
            sb.append("AwMDggMDAwMDAwOQowMDAwMDEwIDAwMDAwMTEgMDAwMDAxMiAwMDAwMDEzIDAwMDAwMTQgMDAwMDAxNSAwMDAwMDE2");
            sb.append("IDAwMDAwMTcgMDAwMDAxOCAwMDAwMDE5CjAwMDAwMjAgMDAwMDAyMSAwMDAwMDIyIDAwMDAwMjMgMDAwMDAyNCAwMD");
            sb.append("AwMDI1IDAwMDAwMjYgMDAwMDAyNyAwMDAwMDI4IDAwMDAwMjkKMDAwMDAzMCAwMDAwMDMxIDAwMDAwMzIgMDAwMDAz");
            sb.append("MyAwMDAwMDM0IDAwMDAwMzUgMDAwMDAzNiAwMDAwMDM3IDAwMDAwMzggMDAwMDAzOQowMDAwMDQwIDAwMDAwNDEgMD");
            sb.append("AwMDA0MiAwMDAwMDQzIDAwMDAwNDQgMDAwMDA0NSAwMDAwMDQ2IDAwMDAwNDcgMDAwMDA0OCAwMDAwMDQ5CjAwMDAw");
            sb.append("NTAgMDAwMDA1MSAwMDAwMDUyIDAwMDAwNTMgMDAwMDA1NCAwMDAwMDU1IDAwMDAwNTYgMDAwMDA1NyAwMDAwMDU4ID");
            sb.append("AwMDAwNTkKMDAwMDA2MCAwMDAwMDYxIA==\r\n");
            sb.append("--testBoundary123\r\n");
            sb.append("Content-Disposition: form-data; name=\"attachment_02.txt\"; filename=\"attachment_02.txt\"\r\n");
            sb.append("Content-Transfer-Encoding: base64\r\n");
            sb.append("Content-Type: application/oct");
        }
        final String sPartialMIMEBase64 = sb.toString();
        final InputStream is = new ByteArrayInputStream(sPartialMIMEBase64.getBytes(StandardCharsets.UTF_8));
        final MIMEConfigPartial mimeCfg = new MIMEConfigPartial();
        MIMEMessagePartial mimeMsg = new MIMEMessagePartial(is, strBoundary, mimeCfg);
        List<MIMEPartPartial> l = mimeMsg.getAttachmentsPartial();
        Assert.assertNotNull(l, "List MIMEPart is null");
        mimeMsg.close();
    }

    /**
     * testPartialBase64PartialLastHeader1 tests base64 MIME attachments with partial content at last header boundary, leaving no attachment. This
     * should result in 2 attachments and last attachment is missing, and message is partial.
     *
     * @throws Exception error.
     */
    @Test
    public void testPartialBase64PartialLastHeader1() throws Exception {
        final int szPerAttachment = 500;
        final int szPerAttachmentInBase64 = szPerAttachment * 4 / 3;
        final int numAttachments = 3;
        final String boundary = "testBoundary123";
        final int payloadBytesMissing = szPerAttachmentInBase64 + boundary.length() + 4 + 10; // 60;
        final ByteArrayOutputStream baosReference = new ByteArrayOutputStream();
        final MIMEMessagePartial mimeMessage = createMIMEMessage(numAttachments, szPerAttachment, payloadBytesMissing, boundary, baosReference, true,
                false);
        final String strRefHex;
        final String strReference;
        {
            StringBuilder sb = new StringBuilder();
            final byte[] baReference = baosReference.toByteArray();
            strReference = new String(baReference, StandardCharsets.UTF_8);
            Assert.assertNotNull(strReference, "String reference for MIME is null");
            for (int j = 0; j < baReference.length; j++) {
                if (j % 30 == 0 && j != 0) {
                    sb.append("\n");
                }
                sb.append(String.format("%02x,", baReference[j]));
            }
            strRefHex = sb.toString();
            Assert.assertNotNull(strRefHex, "String Hex is null");
        }
        {
            boolean isPartial = mimeMessage.getIsPartial();
            Assert.assertTrue(isPartial, "MIMEMessage expected to be partial");
        }
        {
            List<MIMEPartPartial> listMIMEParts = mimeMessage.getAttachmentsPartial();
            Assert.assertNotNull(listMIMEParts, "List of MIMEParts is null");
            for (final MIMEPartPartial mimePart : listMIMEParts) {
                StringBuilder sb = new StringBuilder();
                for (final Header header : mimePart.getAllHeaders()) {
                    sb.append(header.getName() + ":" + header.getValue() + "\n");
                }
                boolean isPartial = mimePart.getIsPartial();
                final InputStream is = mimePart.read();
                int read = 0;
                final byte[] buf = new byte[32];
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((read = is.read(buf)) != -1) {
                    baos.write(buf, 0, read);
                }
                final byte[] baRead = baos.toByteArray();
                final String strHeaders = sb.toString();
                final String strRead = new String(baRead, StandardCharsets.UTF_8);
                final String strReadHex;
                {
                    sb = new StringBuilder();
                    for (int j = 0; j < baRead.length; j++) {
                        if (j % 30 == 0 && j != 0) {
                            sb.append("\n");
                        }
                        sb.append(String.format("%02x,", baRead[j]));
                    }
                    strReadHex = sb.toString();
                    Assert.assertNotNull(strReadHex, "String Hex is null");
                }
                Assert.assertNotEquals(strHeaders.length(), 0, "headers is 0 length");
                Assert.assertFalse(isPartial, "attachment not expected to be partial");
                Assert.assertNotNull(strRead, "mimePart is null");
            }
            mimeMessage.close();
            final int actNumAttachments = listMIMEParts.size();
            final int expNumAttachments = numAttachments; // +1 for json body, missing 1 attachment
            Assert.assertEquals(actNumAttachments, expNumAttachments, "NumAttachments mismatch");
        }
    }

    /**
     * testPartialBase64PartialLastHeader1 tests base64 MIME attachments with partial content at last header boundary, leaving no attachment. This
     * should result in 2 attachments and last attachment is missing, and message is partial.
     *
     * @throws Exception error.
     */
    @Test
    public void testPartialBase64PartialSecondHeader() throws Exception {
        final int szPerAttachment = 500;
        final int szPerAttachmentInBase64 = szPerAttachment * 4 / 3;
        final int numAttachments = 3;
        final String boundary = "testBoundary123";
        final int payloadBytesMissing = szPerAttachmentInBase64 + boundary.length() + 4 + 60;
        final ByteArrayOutputStream baosReference = new ByteArrayOutputStream();
        final MIMEMessagePartial mimeMessage = createMIMEMessage(numAttachments, szPerAttachment, payloadBytesMissing, boundary, baosReference, true,
                false);
        final String strRefHex;
        final String strReference;
        {
            StringBuilder sb = new StringBuilder();
            final byte[] baReference = baosReference.toByteArray();
            strReference = new String(baReference, StandardCharsets.UTF_8);
            Assert.assertNotNull(strReference, "String reference for MIME is null");
            for (int j = 0; j < baReference.length; j++) {
                if (j % 30 == 0 && j != 0) {
                    sb.append("\n");
                }
                sb.append(String.format("%02x,", baReference[j]));
            }
            strRefHex = sb.toString();
            Assert.assertNotNull(strRefHex, "String Hex is null");
        }
        {
            boolean isPartial = mimeMessage.getIsPartial();
            Assert.assertTrue(isPartial, "MIMEMessage expected to be partial");
        }
        {
            List<MIMEPartPartial> listMIMEParts = mimeMessage.getAttachmentsPartial();
            Assert.assertNotNull(listMIMEParts, "List of MIMEParts is null");
            for (final MIMEPartPartial mimePart : listMIMEParts) {
                StringBuilder sb = new StringBuilder();
                for (final Header header : mimePart.getAllHeaders()) {
                    sb.append(header.getName() + ":" + header.getValue() + "\n");
                }
                boolean isPartial = mimePart.getIsPartial();
                final InputStream is = mimePart.read();
                int read = 0;
                final byte[] buf = new byte[32];
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((read = is.read(buf)) != -1) {
                    baos.write(buf, 0, read);
                }
                final byte[] baRead = baos.toByteArray();
                final String strHeaders = sb.toString();
                final String strRead = new String(baRead, StandardCharsets.UTF_8);
                final String strReadHex;
                {
                    sb = new StringBuilder();
                    for (int j = 0; j < baRead.length; j++) {
                        if (j % 30 == 0 && j != 0) {
                            sb.append("\n");
                        }
                        sb.append(String.format("%02x,", baRead[j]));
                    }
                    strReadHex = sb.toString();
                    Assert.assertNotNull(strReadHex, "String Hex is null");
                }
                Assert.assertNotEquals(strHeaders.length(), 0, "headers is 0 length");
                Assert.assertFalse(isPartial, "attachment not expected to be partial");
                Assert.assertNotNull(strRead, "mimePart is null");
            }
            mimeMessage.close();
            final int actNumAttachments = listMIMEParts.size();
            final int expNumAttachments = numAttachments; // +1 for json body, missing 1 attachment
            Assert.assertEquals(actNumAttachments, expNumAttachments, "NumAttachments mismatch");
        }
    }

    /**
     * testPartialBase64PartialBody tests base64 MIME attachments with partial content body. This should result in 3 attachments and last attachment
     * is partial, and message is partial.
     *
     * @throws Exception error.
     */
    @Test
    public void testPartialBase64PartialBody() throws Exception {
        final int szPerAttachment = 500;
        final int numAttachments = 3;
        final String boundary = "testBoundary123";
        final int payloadBytesMissing = boundary.length() + 4 + 100;
        final ByteArrayOutputStream baosReference = new ByteArrayOutputStream();
        final MIMEMessagePartial mimeMessage = createMIMEMessage(numAttachments, szPerAttachment, payloadBytesMissing, boundary, baosReference, true,
                false);
        final String strRefHex;
        final String strReference;
        {
            StringBuilder sb = new StringBuilder();
            final byte[] baReference = baosReference.toByteArray();
            strReference = new String(baReference, StandardCharsets.UTF_8);
            Assert.assertNotNull(strReference, "String reference for MIME is null");
            for (int j = 0; j < baReference.length; j++) {
                if (j % 30 == 0 && j != 0) {
                    sb.append("\n");
                }
                sb.append(String.format("%02x,", baReference[j]));
            }
            strRefHex = sb.toString();
            Assert.assertNotNull(strRefHex, "String Hex is null");
        }
        {
            boolean isPartial = mimeMessage.getIsPartial();
            Assert.assertTrue(isPartial, "MIMEMessage expected to be partial");
        }
        {
            List<MIMEPartPartial> listMIMEParts = mimeMessage.getAttachmentsPartial();
            Assert.assertNotNull(listMIMEParts, "List of MIMEParts is null");
            int numPartialAttachments = 0;
            for (final MIMEPartPartial mimePart : listMIMEParts) {
                StringBuilder sb = new StringBuilder();
                for (final Header header : mimePart.getAllHeaders()) {
                    sb.append(header.getName() + ":" + header.getValue() + "\n");
                }
                boolean isPartial = mimePart.getIsPartial();
                if (isPartial) {
                    numPartialAttachments++;
                }
                final InputStream is = mimePart.read();
                int read = 0;
                final byte[] buf = new byte[32];
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((read = is.read(buf)) != -1) {
                    baos.write(buf, 0, read);
                }
                final byte[] baRead = baos.toByteArray();
                final String strHeaders = sb.toString();
                final String strRead = new String(baRead, StandardCharsets.UTF_8);
                final String strReadHex;
                {
                    sb = new StringBuilder();
                    for (int j = 0; j < baRead.length; j++) {
                        if (j % 30 == 0 && j != 0) {
                            sb.append("\n");
                        }
                        sb.append(String.format("%02x,", baRead[j]));
                    }
                    strReadHex = sb.toString();
                    Assert.assertNotNull(strReadHex, "String Hex is null");
                }
                Assert.assertNotEquals(strHeaders.length(), 0, "headers is 0 length");
                Assert.assertNotNull(strRead, "mimePart is null");
            }
            mimeMessage.close();
            final int actNumAttachments = listMIMEParts.size();
            final int expNumAttachments = numAttachments + 1; // +1 for json body
            Assert.assertEquals(actNumAttachments, expNumAttachments, "NumAttachments mismatch");
            Assert.assertEquals(numPartialAttachments, 1, "Expecting number of partial attachments to be 1");
        }
    }

    /**
     * testPartialBase64PartialBodyOddOffset tests base64 MIME attachments with partial content body. This should result in 3 attachments and last
     * attachment is partial, and message is partial. This is base64, so the odd offset should handle removing last base64 bytes that are unaligned,
     * and not throw exception.
     *
     * @throws Exception error.
     */
    @Test
    public void testPartialBase64PartialBodyOddOffset() throws Exception {
        final int szPerAttachment = 500;
        final int numAttachments = 3;
        final String boundary = "testBoundary123";
        final int payloadBytesMissing = boundary.length() + 4 + 103;
        final ByteArrayOutputStream baosReference = new ByteArrayOutputStream();
        final MIMEMessagePartial mimeMessage = createMIMEMessage(numAttachments, szPerAttachment, payloadBytesMissing, boundary, baosReference, true,
                false);
        final String strRefHex;
        final String strReference;
        {
            StringBuilder sb = new StringBuilder();
            final byte[] baReference = baosReference.toByteArray();
            strReference = new String(baReference, StandardCharsets.UTF_8);
            Assert.assertNotNull(strReference, "String reference for MIME is null");
            for (int j = 0; j < baReference.length; j++) {
                if (j % 30 == 0 && j != 0) {
                    sb.append("\n");
                }
                sb.append(String.format("%02x,", baReference[j]));
            }
            strRefHex = sb.toString();
            Assert.assertNotNull(strRefHex, "String Hex is null");
        }
        {
            boolean isPartial = mimeMessage.getIsPartial();
            Assert.assertTrue(isPartial, "MIMEMessage expected to be partial");
        }
        {
            List<MIMEPartPartial> listMIMEParts = mimeMessage.getAttachmentsPartial();
            Assert.assertNotNull(listMIMEParts, "List of MIMEParts is null");
            int numPartialAttachments = 0;
            for (final MIMEPartPartial mimePart : listMIMEParts) {
                StringBuilder sb = new StringBuilder();
                for (final Header header : mimePart.getAllHeaders()) {
                    sb.append(header.getName() + ":" + header.getValue() + "\n");
                }
                boolean isPartial = mimePart.getIsPartial();
                if (isPartial) {
                    numPartialAttachments++;
                }
                final InputStream is = mimePart.read();
                int read = 0;
                final byte[] buf = new byte[32];
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((read = is.read(buf)) != -1) {
                    baos.write(buf, 0, read);
                }
                final byte[] baRead = baos.toByteArray();
                final String strHeaders = sb.toString();
                final String strRead = new String(baRead, StandardCharsets.UTF_8);
                final String strReadHex;
                {
                    sb = new StringBuilder();
                    for (int j = 0; j < baRead.length; j++) {
                        if (j % 30 == 0 && j != 0) {
                            sb.append("\n");
                        }
                        sb.append(String.format("%02x,", baRead[j]));
                    }
                    strReadHex = sb.toString();
                    Assert.assertNotNull(strReadHex, "String Hex is null");
                }
                Assert.assertNotEquals(strHeaders.length(), 0, "headers is 0 length");
                Assert.assertNotNull(strRead, "mimePart is null");
            }
            mimeMessage.close();
            final int actNumAttachments = listMIMEParts.size();
            final int expNumAttachments = numAttachments + 1; // +1 for json body
            Assert.assertEquals(actNumAttachments, expNumAttachments, "NumAttachments mismatch");
            Assert.assertEquals(numPartialAttachments, 1, "Expecting number of partial attachments to be 1");
        }
    }

    /**
     * testPartialBase64Complete creates 3 attachments, fully. There should be no errors.
     *
     * @throws Exception error.
     */
    @Test
    public void testPartialBase64Complete() throws Exception {
        final int szPerAttachment = 500;
        final int numAttachments = 3;
        final String boundary = "testBoundary123";
        final int payloadBytesMissing = 0;
        final ByteArrayOutputStream baosReference = new ByteArrayOutputStream();
        final MIMEMessagePartial mimeMessage = createMIMEMessage(numAttachments, szPerAttachment, payloadBytesMissing, boundary, baosReference, true,
                false);
        {
            boolean isPartial = mimeMessage.getIsPartial();
            Assert.assertFalse(isPartial, "MIMEMessage expected not to be partial");
        }
        {
            List<MIMEPartPartial> listMIMEParts = mimeMessage.getAttachmentsPartial();
            Assert.assertNotNull(listMIMEParts, "List of MIMEParts is null");
            final int actNumAttachments = listMIMEParts.size();
            final int expNumAttachments = numAttachments + 1; // +1 for json body
            Assert.assertEquals(actNumAttachments, expNumAttachments, "NumAttachments mismatch");
            for (final MIMEPartPartial mimePart : listMIMEParts) {
                StringBuilder sb = new StringBuilder();
                for (final Header header : mimePart.getAllHeaders()) {
                    sb.append(header.getName() + ":" + header.getValue() + "\n");
                }
                boolean isPartial = mimePart.getIsPartial();
                final InputStream is = mimePart.read();
                int read = 0;
                final byte[] buf = new byte[32];
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((read = is.read(buf)) != -1) {
                    baos.write(buf, 0, read);
                }
                final byte[] baRead = baos.toByteArray();
                final String strHeaders = sb.toString();
                final String strRead = new String(baRead, StandardCharsets.UTF_8);
                Assert.assertNotEquals(strHeaders.length(), 0, "headers is 0 length");
                Assert.assertFalse(isPartial, "attachment not expected to be partial");
                Assert.assertNotNull(strRead, "mimePart is null");
            }
            mimeMessage.close();
        }

    }
}
