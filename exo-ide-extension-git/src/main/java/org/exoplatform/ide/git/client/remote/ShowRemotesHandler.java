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
package org.exoplatform.ide.git.client.remote;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link ShowRemotesEvent} event.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 18, 2011 10:24:53 AM anya $
 */
public interface ShowRemotesHandler extends EventHandler {
    /**
     * Perform actions on show remote repositories.
     * 
     * @param event event
     */
    void onShowRemotes(ShowRemotesEvent event);
}
