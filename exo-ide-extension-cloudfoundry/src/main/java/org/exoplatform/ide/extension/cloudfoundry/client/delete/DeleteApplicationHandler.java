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
package org.exoplatform.ide.extension.cloudfoundry.client.delete;


import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link DeleteApplicationEvent} event.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeleteApplicationHandler.java Jul 15, 2011 10:39:48 AM vereshchaka $
 */
public interface DeleteApplicationHandler extends EventHandler {
    /**
     * Perform actions, when user tries to delete application.
     *
     * @param event
     */
    void onDeleteApplication(DeleteApplicationEvent event);

}
