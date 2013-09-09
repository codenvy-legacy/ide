/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.git.server.rest;

import org.exoplatform.ide.git.shared.MergeResult;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Provider
@Produces(MediaType.TEXT_PLAIN)
public final class MergeResultWriter implements MessageBodyWriter<MergeResult> {
    /**
     * @see MessageBodyWriter#isWriteable(Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
     */
    @Override
    public boolean isWriteable(Class< ? > type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return MergeResult.class.isAssignableFrom(type);
    }

    /**
     * @see MessageBodyWriter#getSize(Object, Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
     */
    @Override
    public long getSize(MergeResult mergeResult,
                        Class< ? > type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType) {
        return -1;
    }

    /**
     * @see MessageBodyWriter#writeTo(Object, Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType,
     *      javax.ws.rs.core.MultivaluedMap, java.io.OutputStream)
     */
    @Override
    public void writeTo(MergeResult mergeResult,
                        Class< ? > type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        Writer writer = new OutputStreamWriter(entityStream);
        MergeResult.MergeStatus status = mergeResult.getMergeStatus();
        switch (mergeResult.getMergeStatus()) {
            case FAST_FORWARD:
            case ALREADY_UP_TO_DATE:
            case MERGED:
                writer.write(status.toString());
                writer.write('\n');
                break;
            case FAILED:
                writer.write("error: Failed to merge:");
                for (String failed : mergeResult.getFailed()) {
                    writer.write("        ");
                    writer.write(failed);
                    writer.write('\n');
                }
                break;
            case CONFLICTING:
                for (String conflict : mergeResult.getConflicts()) {
                    writer.write("CONFLICT(content): Merge conflict in: " + conflict);
                    writer.write('\n');
                }
                writer.write("Automatic merge failed; fix conflicts and then commit the result");
                writer.write('\n');
                break;
            case NOT_SUPPORTED:
                writer.write("Operation not supported");
                writer.write('\n');
                break;
        }
        writer.flush();
    }
}
