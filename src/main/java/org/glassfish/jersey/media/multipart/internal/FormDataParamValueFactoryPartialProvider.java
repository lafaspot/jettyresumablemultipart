package org.glassfish.jersey.media.multipart.internal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.glassfish.jersey.media.multipart.BodyPartEntityPartial;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.message.MessageUtils;
import org.glassfish.jersey.message.internal.Utils;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ParamException;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractor;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;
import org.jvnet.mimepull.MIMEParsingException;

import jersey.repackaged.com.google.common.base.Function;
import jersey.repackaged.com.google.common.collect.Lists;

/**
 * Value factory provider supporting the {@link FormDataParam} injection annotation and entity ({@link FormDataMultiPart})
 * injection.
 *
 * @author Craig McClanahan
 * @author Paul Sandoz
 * @author Michal Gajdos (michal.gajdos at oracle.com)
 */
final class FormDataParamValueFactoryPartialProvider extends AbstractValueFactoryProvider {

    /** No change from base class. */
    private static final Logger LOGGER = Logger.getLogger(FormDataParamValueFactoryPartialProvider.class.getName());

    /** No change from base class. */
    private static final class FormDataParamException extends ParamException {

        /**
         * Exception static class. No change from base class.
         *
         * @param cause Throwable.
         * @param name String.
         * @param defaultStringValue String.
         */
        protected FormDataParamException(final Throwable cause, final String name, final String defaultStringValue) {
            super(cause, Response.Status.BAD_REQUEST, FormDataParam.class, name, defaultStringValue);
        }
    }

    /**
     * {@link FormDataParam} injection resolver.
     */
    static final class InjectionResolver extends ParamInjectionResolver<FormDataParam> {

        /**
         * Create new {@link FormDataParam} injection resolver.
         */
        public InjectionResolver() {
            super(FormDataParamValueFactoryPartialProvider.class);
        }
    }

    /** No change from base class. */
    private abstract class ValueFactory<T> extends AbstractContainerRequestValueFactory<T> {

        /**
         * Returns a {@code FormDataMultiPart} entity from the request and stores it in the request context properties.
         *
         * @return a form data multi part entity.
         */
        FormDataMultiPart getEntity() {
            final ContainerRequest request = getContainerRequest();
            final String requestPropertyName = FormDataMultiPart.class.getName();

            if (request.getProperty(requestPropertyName) == null) {
                request.setProperty(requestPropertyName, request.readEntity(FormDataMultiPart.class));
            }

            return (FormDataMultiPart) request.getProperty(requestPropertyName);
        }
    }

    /**
     * Provider factory for entity of {@code FormDataMultiPart} type.
     */
    private final class FormDataMultiPartFactory extends ValueFactory<FormDataMultiPart> {

        @Override
        public FormDataMultiPart provide() {
            return getEntity();
        }
    }

    /**
     * Provider factory for list of {@link org.glassfish.jersey.media.multipart.FormDataBodyPart} types injected via
     * {@link FormDataParam} annotation.
     */
    private final class ListFormDataBodyPartValueFactory extends ValueFactory<List<FormDataBodyPart>> {
        /** No change from base class. */
        private final String name;

        /**
         * Constructor.
         *
         * @param name String.
         */
        public ListFormDataBodyPartValueFactory(final String name) {
            this.name = name;
        }

        @Override
        public List<FormDataBodyPart> provide() {
            return getEntity().getFields(name);
        }
    }

    /**
     * Provider factory for list of {@link org.glassfish.jersey.media.multipart.FormDataContentDisposition} types injected via
     * {@link FormDataParam} annotation.
     */
    private final class ListFormDataContentDispositionFactory extends ValueFactory<List<FormDataContentDisposition>> {
        /** No change from base class. */
        private final String name;

        /**
         * Constructor.
         *
         * @param name String.
         */
        public ListFormDataContentDispositionFactory(final String name) {
            this.name = name;
        }

        /** No change from base class. */
        @Override
        public List<FormDataContentDisposition> provide() {
            final List<FormDataBodyPart> parts = getEntity().getFields(name);

            return parts == null ? null : Lists.transform(parts, new Function<FormDataBodyPart, FormDataContentDisposition>() {
                @Override
                public FormDataContentDisposition apply(final FormDataBodyPart part) {
                    return part.getFormDataContentDisposition();
                }
            });
        }
    }

    /**
     * Provider factory for {@link org.glassfish.jersey.media.multipart.FormDataBodyPart} types injected via
     * {@link FormDataParam} annotation.
     */
    private final class FormDataBodyPartFactory extends ValueFactory<FormDataBodyPart> {

        /** No change from base class. */
        private final String name;

        /**
         * Constructor. No change from base class.
         *
         * @param name String.
         */
        public FormDataBodyPartFactory(final String name) {
            this.name = name;
        }

        /** No change from base class. */
        @Override
        public FormDataBodyPart provide() {
            return getEntity().getField(name);
        }
    }

    /**
     * Provider factory for {@link org.glassfish.jersey.media.multipart.FormDataContentDisposition} types injected via
     * {@link FormDataParam} annotation.
     */
    private final class FormDataContentDispositionFactory extends ValueFactory<FormDataContentDisposition> {

        /** No change from base class. */
        private final String name;

        /**
         * Constructor.
         *
         * @param name String.
         */
        public FormDataContentDispositionFactory(final String name) {
            this.name = name;
        }

        /** No change from base class. */
        @Override
        public FormDataContentDisposition provide() {
            final FormDataBodyPart part = getEntity().getField(name);

            return part == null ? null : part.getFormDataContentDisposition();
        }
    }

    /**
     * Provider factory for {@link java.io.File} types injected via {@link FormDataParam} annotation.
     */
    private final class FileFactory extends ValueFactory<File> {

        /** No change from base class. */
        private final String name;

        /**
         * Constructor.
         *
         * @param name String.
         */
        public FileFactory(final String name) {
            this.name = name;
        }

        /** No change from base class. */
        @Override
        public File provide() {
            final FormDataBodyPart part = getEntity().getField(name);
            final BodyPartEntityPartial entity = part != null ? part.getEntityAs(BodyPartEntityPartial.class) : null;

            if (entity != null) {
                try {
                    // Create a temporary file.
                    final File file = Utils.createTempFile();

                    // Move the part (represented either via stream or file) to the specific temporary file.
                    entity.moveTo(file);

                    return file;
                } catch (final IOException | MIMEParsingException cannotMove) {
                    // Unable to create a temporary file or move the file.
                    LOGGER.log(Level.WARNING, LocalizationMessages.CANNOT_INJECT_FILE(), cannotMove);
                }
            }

            return null;
        }
    }

    /**
     * Provider factory for generic types injected via {@link FormDataParam} annotation.
     */
    private final class FormDataParamValueFactory extends ValueFactory<Object> {
        /** No change from base class. */
        private final MultivaluedParameterExtractor<?> extractor;
        /** No change from base class. */
        private final Parameter parameter;

        /**
         * Constructor. No change from base class.
         *
         * @param parameter Parameter.
         * @param extractor MultivaluedParameterExtractor.
         */
        public FormDataParamValueFactory(final Parameter parameter, final MultivaluedParameterExtractor<?> extractor) {
            this.parameter = parameter;
            this.extractor = extractor;
        }

        /**
         * @returns Object object.
         */
        @Override
        public Object provide() {
            // Return the field value for the field specified by the sourceName property.
            final List<FormDataBodyPart> parts = getEntity().getFields(parameter.getSourceName());


            final FormDataBodyPart part = parts != null ? parts.get(0) : null;
            final MediaType mediaType = part != null ? part.getMediaType() : MediaType.TEXT_PLAIN_TYPE;

            final ContainerRequest request = getContainerRequest();
            final MessageBodyWorkers messageBodyWorkers = request.getWorkers();

            MessageBodyReader reader = messageBodyWorkers.getMessageBodyReader(
                    parameter.getRawType(),
                    parameter.getType(),
                    parameter.getAnnotations(),
                    mediaType);

            // Transform non-primitive part entity into an instance.
            if (reader != null
                    && !isPrimitiveType(parameter.getRawType())) {

                // Get input stream of the body part.
                final InputStream stream;
                if (part == null) {
                    if (parameter.getDefaultValue() != null) {
                        // Convert default value to bytes.
                        stream = new ByteArrayInputStream(parameter.getDefaultValue()
                                .getBytes(MessageUtils.getCharset(mediaType)));
                    } else {
                        return null;
                    }
                } else {
                    stream = part.getEntityAs(BodyPartEntityPartial.class).getInputStream();
                }

                // Transform input stream into instance of desired Java type.
                try {
                    //noinspection unchecked
                    return reader.readFrom(
                            parameter.getRawType(),
                            parameter.getType(),
                            parameter.getAnnotations(),
                            mediaType,
                            request.getHeaders(),
                            stream);
                } catch (final IOException e) {
                    throw new FormDataParamException(e, parameter.getSourceName(), parameter.getDefaultValue());
                }
            }

            // If no reader was found or a primitive type is being transformed use extractor instead.
            if (extractor != null) {
                final MultivaluedMap<String, String> map = new MultivaluedStringMap();
                try {
                    if (part != null) {
                        for (final FormDataBodyPart p : parts) {
                            reader = messageBodyWorkers.getMessageBodyReader(
                                    String.class,
                                    String.class,
                                    parameter.getAnnotations(),
                                    p.getMediaType());

                            @SuppressWarnings("unchecked") final String value = (String) reader.readFrom(
                                    String.class,
                                    String.class,
                                    parameter.getAnnotations(),
                                    mediaType,
                                    request.getHeaders(),
                                    ((BodyPartEntityPartial) p.getEntity()).getInputStream());

                            map.add(parameter.getSourceName(), value);
                        }
                    }
                    return extractor.extract(map);
                } catch (final IOException | ExtractorException ex) {
                    throw new FormDataParamException(ex, extractor.getName(), extractor.getDefaultValueString());
                }
            }

            return null;
        }
    }

    /** No change from base class. */
    private static final Set<Class<?>> TYPES = initializeTypes();

    /**
     * No change from base class.
     *
     * @return Set of type.
     */
    private static Set<Class<?>> initializeTypes() {
        final Set<Class<?>> newSet = new HashSet<>();
        newSet.add(Byte.class);
        newSet.add(byte.class);
        newSet.add(Short.class);
        newSet.add(short.class);
        newSet.add(Integer.class);
        newSet.add(int.class);
        newSet.add(Long.class);
        newSet.add(long.class);
        newSet.add(Float.class);
        newSet.add(float.class);
        newSet.add(Double.class);
        newSet.add(double.class);
        newSet.add(Boolean.class);
        newSet.add(boolean.class);
        newSet.add(Character.class);
        newSet.add(char.class);
        return newSet;
    }

    /**
     * isPrimitiveType.
     *
     * @param type type.
     * @return boolean.
     */
    private static boolean isPrimitiveType(final Class<?> type) {
        return TYPES.contains(type);
    }

    /**
     * Injection constructor.
     *
     * @param extractorProvider    multi-valued map parameter extractor provider.
     * @param locator HK2 service locator.
     */
    @Inject
    public FormDataParamValueFactoryPartialProvider(final MultivaluedParameterExtractorProvider extractorProvider,
                                             final ServiceLocator locator) {
        super(extractorProvider, locator, Parameter.Source.ENTITY, Parameter.Source.UNKNOWN);
    }

    @Override
    protected Factory<?> createValueFactory(final Parameter parameter) {
        final Class<?> rawType = parameter.getRawType();

        if (Parameter.Source.ENTITY == parameter.getSource()) {
            if (FormDataMultiPart.class.isAssignableFrom(rawType)) {
                return new FormDataMultiPartFactory();
            } else {
                return null;
            }
        } else if (parameter.getSourceAnnotation().annotationType() == FormDataParam.class) {
            final String paramName = parameter.getSourceName();
            if (paramName == null || paramName.isEmpty()) {
                // Invalid query parameter name
                return null;
            }

            if (Collection.class == rawType || List.class == rawType) {
                final Class clazz = ReflectionHelper.getGenericTypeArgumentClasses(parameter.getType()).get(0);

                if (FormDataBodyPart.class == clazz) {
                    // Return a collection of form data body part.
                    return new ListFormDataBodyPartValueFactory(paramName);
                } else if (FormDataContentDisposition.class == clazz) {
                    // Return a collection of form data content disposition.
                    return new ListFormDataContentDispositionFactory(paramName);
                } else {
                    // Return a collection of specific type.
                    return new FormDataParamValueFactory(parameter, get(parameter));
                }
            } else if (FormDataBodyPart.class == rawType) {
                return new FormDataBodyPartFactory(paramName);
            } else if (FormDataContentDisposition.class == rawType) {
                return new FormDataContentDispositionFactory(paramName);
            } else if (File.class == rawType) {
                return new FileFactory(paramName);
            } else {
                return new FormDataParamValueFactory(parameter, get(parameter));
            }
        }

        return null;
    }

    @Override
    public PriorityType getPriority() {
        return Priority.HIGH;
    }

}
