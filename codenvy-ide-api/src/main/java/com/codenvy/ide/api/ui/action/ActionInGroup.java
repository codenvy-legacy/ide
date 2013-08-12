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
package com.codenvy.ide.api.ui.action;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ActionInGroup {
    private final DefaultActionGroup myGroup;
    private final Action             myAction;

    ActionInGroup(DefaultActionGroup group, Action action) {
        myGroup = group;
        myAction = action;
    }

    public ActionInGroup setAsSecondary(boolean isSecondary) {
        myGroup.setAsPrimary(myAction, !isSecondary);
        return this;
    }

    public ActionGroup getGroup() {
        return myGroup;
    }
}
