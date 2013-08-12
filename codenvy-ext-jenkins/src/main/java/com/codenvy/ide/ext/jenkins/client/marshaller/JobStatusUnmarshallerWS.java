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