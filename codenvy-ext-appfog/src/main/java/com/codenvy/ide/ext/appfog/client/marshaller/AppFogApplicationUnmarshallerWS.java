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
package com.codenvy.ide.ext.appfog.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.appfog.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.rest.Unmarshallable;

/**
 * Unmarshaller for AppFog application.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class AppFogApplicationUnmarshallerWS implements Unmarshallable<AppfogApplication> {
    private DtoClientImpls.AppfogApplicationImpl application;

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Message response) throws UnmarshallerException {
        String text = response.getBody();

        if (text == null || text.isEmpty()) {
            return;
        }

        application = DtoClientImpls.AppfogApplicationImpl.deserialize(text);
    }

    /** {@inheritDoc} */
    @Override
    public AppfogApplication getPayload() {
        return application;
    }
}