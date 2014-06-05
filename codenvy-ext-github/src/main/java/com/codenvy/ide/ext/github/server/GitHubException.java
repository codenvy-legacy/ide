/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.github.server;

/**
 * If GitHub returns unexpected or error status for request.
 *
 * @author Oksana Vereshchaka
 */
@SuppressWarnings("serial")
public class GitHubException extends Exception {
    /** HTTP status of response from GitHub server. */
    private final int    responseStatus;

    /** Content type of response from GitHub server. */
    private final String contentType;

    /**
     * @param responseStatus HTTP status of response from GitHub server
     * @param message text message
     * @param contentType content type of response from GitHub server
     */
    public GitHubException(int responseStatus, String message, String contentType) {
        super(message);
        this.responseStatus = responseStatus;
        this.contentType = contentType;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public String getContentType() {
        return contentType;
    }
}
