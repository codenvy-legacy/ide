/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extruntime.client.marshaller;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.extruntime.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.extruntime.shared.ApplicationInstance;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.rest.Unmarshallable;
import com.google.gwt.user.client.Window;

/**
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationInstanceUnmarshallerWS.java Jul 31, 2013 5:13:18 PM azatsarynnyy $
 */
public class ApplicationInstanceUnmarshallerWS implements Unmarshallable<ApplicationInstance> {
    private DtoClientImpls.ApplicationInstanceImpl applicationInstance;

    /**
     * Create unmarshaller.
     * 
     * @param applicationInstance
     */
    public ApplicationInstanceUnmarshallerWS(@NotNull DtoClientImpls.ApplicationInstanceImpl applicationInstance) {
        this.applicationInstance = applicationInstance;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Message response) throws UnmarshallerException {
        String text = response.getBody();

        if (text == null || text.isEmpty()) {
            return;
        }

        DtoClientImpls.ApplicationInstanceImpl applicationInstance = DtoClientImpls.ApplicationInstanceImpl.deserialize(text);

        this.applicationInstance.setId(applicationInstance.getId());
        final String host = applicationInstance.getHost();
        if (host == null || host.isEmpty()) {
            this.applicationInstance.setHost(Window.Location.getHostName());
        } else {
            this.applicationInstance.setHost(host);
        }
        this.applicationInstance.setPort(applicationInstance.getPort());
        final String codeServerHost = applicationInstance.getCodeServerHost();
        if (codeServerHost == null || codeServerHost.isEmpty()) {
            this.applicationInstance.setCodeServerHost(Window.Location.getHostName());
        } else {
            this.applicationInstance.setCodeServerHost(codeServerHost);
        }
        this.applicationInstance.setCodeServerPort(applicationInstance.getCodeServerPort());
    }

    /** {@inheritDoc} */
    @Override
    public ApplicationInstance getPayload() {
        return applicationInstance;
    }
}
