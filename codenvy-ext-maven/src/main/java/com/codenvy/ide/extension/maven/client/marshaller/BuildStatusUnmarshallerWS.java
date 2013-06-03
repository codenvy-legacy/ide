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
package com.codenvy.ide.extension.maven.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.extension.maven.dto.client.DtoClientImpls;
import com.codenvy.ide.extension.maven.shared.BuildStatus;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.rest.Unmarshallable;

/**
 * Unmarshaller for build status.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class BuildStatusUnmarshallerWS implements Unmarshallable<BuildStatus> {
    private DtoClientImpls.BuildStatusImpl buildStatus;

    /**
     * Create unmarshaller.
     *
     * @param buildStatus
     */
    public BuildStatusUnmarshallerWS(DtoClientImpls.BuildStatusImpl buildStatus) {
        this.buildStatus = buildStatus;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Message response) throws UnmarshallerException {
        String text = response.getBody();

        if (text == null || text.isEmpty()) {
            return;
        }

        DtoClientImpls.BuildStatusImpl buildStatus = DtoClientImpls.BuildStatusImpl.deserialize(text);

        this.buildStatus.setStatus(buildStatus.getStatus());
        if (this.buildStatus.hasExitCode()) {
            this.buildStatus.setExitCode(buildStatus.getExitCode());
        }
        this.buildStatus.setError(buildStatus.getError());
        this.buildStatus.setDownloadUrl(buildStatus.getDownloadUrl());
        this.buildStatus.setTime(buildStatus.getTime());
    }

    /** {@inheritDoc} */
    @Override
    public BuildStatus getPayload() {
        return buildStatus;
    }
}