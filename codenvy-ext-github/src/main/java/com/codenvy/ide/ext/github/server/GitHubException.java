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
