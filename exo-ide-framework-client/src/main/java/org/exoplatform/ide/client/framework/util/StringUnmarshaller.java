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
package org.exoplatform.ide.client.framework.util;

import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.websocket.rest.ResponseMessage;

/**
 * Unmarshaller for unmarshalling response as {@link String}.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: StringUnmarshaller.java Aug 27, 2012 10:05:35 AM azatsarynnyy $
 */
public class StringUnmarshaller implements Unmarshallable<StringBuilder>,
                                           org.exoplatform.ide.client.framework.websocket.rest.Unmarshallable<StringBuilder> {
    protected StringBuilder builder;

    public StringUnmarshaller(StringBuilder builder) {
        this.builder = builder;
    }

    /** @see org.exoplatform.ide.client.framework.websocket.rest.Unmarshallable#unmarshal(org.exoplatform.ide.client.framework.websocket
     * .rest.ResponseMessage) */
    @Override
    public void unmarshal(ResponseMessage response) throws UnmarshallerException {
        if (response.getResponseCode() != HTTPStatus.NO_CONTENT && response.getBody() != null)
            builder.append(response.getBody());
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#getPayload() */
    @Override
    public StringBuilder getPayload() {
        return builder;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        if (response.getStatusCode() != HTTPStatus.NO_CONTENT && response.getText() != null)
            builder.append(response.getText());
    }

}
