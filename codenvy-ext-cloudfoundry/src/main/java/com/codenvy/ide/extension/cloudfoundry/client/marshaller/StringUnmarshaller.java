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
package com.codenvy.ide.extension.cloudfoundry.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;

/**
 * Unmarshaller for String object.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 */
public class StringUnmarshaller implements Unmarshallable<StringBuilder> {
    private StringBuilder builder;

    /**
     * Create unmarshaller.
     *
     * @param builder
     */
    public StringUnmarshaller(StringBuilder builder) {
        super();
        this.builder = builder;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        builder.append(response.getText());
    }

    /** {@inheritDoc} */
    @Override
    public StringBuilder getPayload() {
        return builder;
    }
}