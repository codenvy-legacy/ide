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
package com.codenvy.ide.ext.java.jdi.client.marshaller;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.java.jdi.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;

/**
 * Unmarshaller for debugger info.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class DebuggerInfoUnmarshaller implements Unmarshallable<DebuggerInfo> {
    private DtoClientImpls.DebuggerInfoImpl debuggerInfo;

    /**
     * Create unmarshaller.
     *
     * @param debuggerInfo
     */
    public DebuggerInfoUnmarshaller(@NotNull DtoClientImpls.DebuggerInfoImpl debuggerInfo) {
        this.debuggerInfo = debuggerInfo;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        DtoClientImpls.DebuggerInfoImpl debuggerInfo = DtoClientImpls.DebuggerInfoImpl.deserialize(text);
        this.debuggerInfo.setId(debuggerInfo.getId());
        this.debuggerInfo.setHost(debuggerInfo.getHost());
        if (debuggerInfo.hasPort()) {
            this.debuggerInfo.setPort(debuggerInfo.getPort());
        }
        this.debuggerInfo.setVmName(debuggerInfo.getVmName());
        this.debuggerInfo.setVmVersion(debuggerInfo.getVmVersion());
    }

    /** {@inheritDoc} */
    @Override
    public DebuggerInfo getPayload() {
        return debuggerInfo;
    }
}