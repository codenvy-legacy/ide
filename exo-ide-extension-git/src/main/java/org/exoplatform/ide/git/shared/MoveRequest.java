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
 * Request to move or rename a file or directory.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MoveRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class MoveRequest extends GitRequest {
    /** Source. */
    private String source;

    /** Target. */
    private String target;

    /**
     * @param source move source
     * @param target move target
     */
    public MoveRequest(String source, String target) {
        this.source = source;
        this.target = target;
    }

    /**
     * "Empty" move request. Corresponding setters used to setup required parameters.
     */
    public MoveRequest() {
    }

    /** @return source */
    public String getSource() {
        return source;
    }

    /**
     * @param source move source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /** @return target */
    public String getTarget() {
        return target;
    }

    /**
     * @param target move target
     */
    public void setTarget(String target) {
        this.target = target;
    }
}