/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.extruntime.client.marshaller;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.extruntime.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.extruntime.shared.ApplicationInstance;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.rest.Unmarshallable;

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

        this.applicationInstance.setName(applicationInstance.getName());
        this.applicationInstance.setHost(applicationInstance.getHost());
        this.applicationInstance.setPort(applicationInstance.getPort());
    }

    /** {@inheritDoc} */
    @Override
    public ApplicationInstance getPayload() {
        return applicationInstance;
    }
}
