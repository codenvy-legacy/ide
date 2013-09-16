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
package org.exoplatform.ide.extension.ssh.server;

import org.everrest.core.ApplicationContext;
import org.everrest.core.impl.ApplicationContextImpl;
import org.everrest.core.impl.provider.JsonEntityProvider;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
@Provider
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class JsonpEntityProvider extends JsonEntityProvider<Object> {
    private static final String CALLBACK = "callback";

    @Override
    public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        ApplicationContext applicationContext = ApplicationContextImpl.getCurrent();
        MultivaluedMap<String, String> queryParameters = applicationContext.getQueryParameters();
        String callback = queryParameters.getFirst(CALLBACK);
        if (callback != null) {
            entityStream.write(callback.getBytes(Charset.forName("UTF-8")));
            entityStream.write('(');
            entityStream.flush();
            super.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
            entityStream.write(')');
            entityStream.write(';');
            entityStream.flush();
        } else {
            super.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
        }
    }
}
