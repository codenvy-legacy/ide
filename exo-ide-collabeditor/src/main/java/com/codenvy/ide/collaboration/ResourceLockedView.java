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
package com.codenvy.ide.collaboration;

import com.google.gwt.safehtml.shared.SafeHtml;

import org.exoplatform.ide.client.framework.ui.api.View;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface ResourceLockedView extends View {
    public interface ActionDelegate {
        void onClose();

        void onNotify();
    }

    String ID = "ideCollaborationResourceLocked";

    void setDelegate(ActionDelegate delegate);

    void setMessageText(SafeHtml message);

    void setUserList(SafeHtml userList);

    void setNotifyButtonEnabled(boolean enabled);
}
