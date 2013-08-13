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