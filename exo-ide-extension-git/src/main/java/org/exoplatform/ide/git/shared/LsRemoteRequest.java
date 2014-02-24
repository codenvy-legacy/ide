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
package org.exoplatform.ide.git.shared;

/**
 * Request for calling git ls-remote.
 *
 * @author Alexander Garagatyi
 */
public class LsRemoteRequest extends GitRequest {
    private final String remoteUrl;

    private final boolean useAuthorization;

    /**
     * Create ls-remote request.
     *
     * @param remoteUrl url of remote repository.
     * @param useAuthorization Would request be executed with authorization on not.
     */
    public LsRemoteRequest(String remoteUrl, boolean useAuthorization) {
        this.remoteUrl = remoteUrl;
        this.useAuthorization = useAuthorization;
    }

    /**
     * Create ls-remote request.
     * By default do not use authorization.
     *
     * @param remoteUrl url of remote repository.
     */
    public LsRemoteRequest(String remoteUrl) {
        this(remoteUrl, false);
    }

    /**
     * Get url of remote repository.
     * @return - url or repository
     */
    public String getRemoteUrl() {
        return remoteUrl;
    }

    /**
     * Would request be executed with authorization on not.
     *
     * @return - true if request require authorization, false otherwise
     */
    public boolean isUseAuthorization() {
        return useAuthorization;
    }
}
