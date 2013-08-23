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
package org.exoplatform.ide.client.project.explorer;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link ProjectSelectedEvent} event.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Aug 20, 2012 3:15:43 PM anya $
 * @deprecated
 */
public interface ProjectSelectedHandler extends EventHandler {
    /**
     * Perform actions, when project is selected in project list.
     *
     * @param event
     */
    void onProjectSelected(ProjectSelectedEvent event);
}
