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
package com.codenvy.ide.api.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Resource API fires ProjectAction Events when project any kind of operations that
 * changes the project invoked. Those are opening, closing, changing the description.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface ProjectActionHandler extends EventHandler {
    /**
     * Project opened
     *
     * @param event
     */
    void onProjectOpened(ProjectActionEvent event);

    /**
     * Project opened
     *
     * @param event
     */
    void onProjectClosed(ProjectActionEvent event);

    /**
     * Project Description Changed
     *
     * @param event
     */
    void onProjectDescriptionChanged(ProjectActionEvent event);
}
