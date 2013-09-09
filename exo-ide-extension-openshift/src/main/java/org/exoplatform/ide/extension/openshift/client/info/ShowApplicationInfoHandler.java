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
package org.exoplatform.ide.extension.openshift.client.info;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link ShowApplicationInfoEvent} event.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 9, 2011 3:47:47 PM anya $
 */
public interface ShowApplicationInfoHandler extends EventHandler {
    /**
     * Perform actions, when user wants to view application's info.
     *
     * @param event
     */
    void onShowApplicationInfo(ShowApplicationInfoEvent event);
}
