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
package org.exoplatform.ide.git.client.status;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link ShowWorkTreeStatusEvent} event.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 28, 2011 3:05:18 PM anya $
 */
public interface ShowWorkTreeStatusHandler extends EventHandler {
    /**
     * Perform actions, when user makes a try to view work tree status.
     * 
     * @param event
     */
    void onShowWorkTreeStatus(ShowWorkTreeStatusEvent event);
}
