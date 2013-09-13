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
package org.exoplatform.ide.extension.cloudbees.client.initialize;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link InitializeApplicationEvent} event.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: InitializeApplicationHandler.java Jun 23, 2011 12:44:56 PM vereshchaka $
 */
public interface InitializeApplicationHandler extends EventHandler {
    /**
     * Perform actions, when user tries to initialize application.
     *
     * @param event
     */
    void onInitializeApplication(InitializeApplicationEvent event);

}
