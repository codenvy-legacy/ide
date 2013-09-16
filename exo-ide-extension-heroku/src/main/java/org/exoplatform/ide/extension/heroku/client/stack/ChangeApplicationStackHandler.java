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
package org.exoplatform.ide.extension.heroku.client.stack;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link ChangeApplicationStackEvent} event.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 28, 2011 6:00:27 PM anya $
 */
public interface ChangeApplicationStackHandler extends EventHandler {
    /**
     * Perform actions, when user tries to change application's stack.
     *
     * @param event
     */
    void onChangeApplicationStack(ChangeApplicationStackEvent event);
}
