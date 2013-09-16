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
package org.exoplatform.ide.extension.java.jdi.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link ChangeValueEvent} event.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ChangeValueHandler.java Apr 28, 2012 10:03:43 AM azatsarynnyy $
 */
public interface ChangeValueHandler extends EventHandler {
    /**
     * Perform actions, when user tries to change variable value.
     *
     * @param event
     *         {@link ChangeValueEvent}
     */
    void onChangeValue(ChangeValueEvent e);
}
