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
package org.exoplatform.ide.git.shared;

/**
 * Request to get list of remotes. If {@link #remote} is specified then info about this remote only given.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RemoteListRequest.java 68015 2011-04-06 09:21:31Z anya $
 */
public class RemoteListRequest extends GitRequest {
    /**
     * If <code>true</code> show remote url and name otherwise show remote name only.
     */
    private boolean verbose;

    /**
     * Remote name. May be <code>null</code> if need to get info about all remotes.
     */
    private String  remote;

    /**
     * "Empty" remote list request. Corresponding setters used to setup required parameters.
     */
    public RemoteListRequest() {

    }

    /**
     * @param remote remote name. May be <code>null</code> if need to get info about all remotes
     * @param verbose if <code>true</code> show remote url and name otherwise show remote name only
     */
    public RemoteListRequest(String remote, boolean verbose) {
        this.remote = remote;
        this.verbose = verbose;
    }

    /**
     * @return if <code>true</code> show remote url and name otherwise show remote name only
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * @param verbose if <code>true</code> show remote url and name otherwise show remote name only
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * @return remote name
     * @see #remote
     */
    public String getRemote() {
        return remote;
    }

    /**
     * @param remote remote name
     * @see #remote
     */
    public void setRemote(String remote) {
        this.remote = remote;
    }
}
