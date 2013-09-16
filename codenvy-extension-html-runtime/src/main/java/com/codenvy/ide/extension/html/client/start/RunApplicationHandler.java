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
package com.codenvy.ide.extension.html.client.start;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link RunApplicationEvent} event.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: RunApplicationHandler.java Jun 26, 2013 11:07:51 AM azatsarynnyy $
 */
public interface RunApplicationHandler extends EventHandler {
    /**
     * Perform actions, when user tries to run HTML application.
     * 
     * @param event
     */
    void onRunApplication(RunApplicationEvent event);
}
