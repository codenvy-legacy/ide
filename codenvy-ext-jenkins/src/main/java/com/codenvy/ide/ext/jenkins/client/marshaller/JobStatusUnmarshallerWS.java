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
package com.codenvy.ide.ext.jenkins.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.jenkins.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.jenkins.shared.JobStatus;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.rest.Unmarshallable;

/**
 * Unmarshaller for job status.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class JobStatusUnmarshallerWS implements Unmarshallable<JobStatus> {
    private DtoClientImpls.JobStatusImpl jobStatus;

    /**
     * Create unmarshaller.
     *
     * @param jobStatus
     */
    public JobStatusUnmarshallerWS(DtoClientImpls.JobStatusImpl jobStatus) {
        this.jobStatus = jobStatus;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Message response) throws UnmarshallerException {
        String text = response.getBody();

        if (text == null || text.isEmpty()) {
            return;
        }

        DtoClientImpls.JobStatusImpl jobStatus = DtoClientImpls.JobStatusImpl.deserialize(text);

        this.jobStatus.setName(jobStatus.getName());
        this.jobStatus.setStatus(jobStatus.getStatus());
        this.jobStatus.setLastBuildResult(jobStatus.getLastBuildResult());
        this.jobStatus.setArtifactUrl(jobStatus.getArtifactUrl());
    }

    /** {@inheritDoc} */
    @Override
    public JobStatus getPayload() {
        return jobStatus;
    }
}