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
 * Handler for {@link RunApplicationEvent} event.
 * 
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: RunApplicationHandler.java Apr 18, 2013 4:51:43 PM vsvydenko $
 *
 */
public interface RunApplicationHandler extends EventHandler {
    /**
     * Perform actions, when user tries to run Node.js application.
     *
     * @param event
     */
    void onRunApplication(RunApplicationEvent event);
}
