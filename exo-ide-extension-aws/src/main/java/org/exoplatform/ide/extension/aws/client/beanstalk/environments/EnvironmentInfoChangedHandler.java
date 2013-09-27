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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link EnvironmentInfoChangedEvent} event.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: EnvironmentStatusChangedHandler.java Oct 2, 2012 10:51:39 AM azatsarynnyy $
 */
public interface EnvironmentInfoChangedHandler extends EventHandler {
    /**
     * Perform actions, when AWS application's environment info were changed.
     *
     * @param event
     */
    void onEnvironmentInfoChanged(EnvironmentInfoChangedEvent event);
}
