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
package org.exoplatform.ide.extension.nodejs.client.run.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link ApplicationStoppedEvent} event.
 * 
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: ApplicationStoppedHandler.java Apr 18, 2013 4:56:13 PM vsvydenko $
 *
 */
public interface ApplicationStoppedHandler extends EventHandler {
    /**
     * Perform actions, when Node.js application has been stopped.
     *
     * @param event
     */
    void onApplicationStopped(ApplicationStoppedEvent event);
}
