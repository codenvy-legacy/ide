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
package org.eclipse.jdt.client.refactoring;


/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class Action {

    public static final String MOVE = "move";

    public static final String UPDATE_CONTENT = "update-content";

    private String action;

    private String resource;

    private String destination;

    public Action(String action, String resource, String destination) {
        this.action = action;
        this.resource = "" + resource;
        this.destination = "" + destination;
    }

    public Action(String action, String resource) {
        this(action, resource, null);
    }

    public String getAction() {
        return action;
    }

    public String getResource() {
        return resource;
    }

    public String getDestination() {
        return destination;
    }

}
