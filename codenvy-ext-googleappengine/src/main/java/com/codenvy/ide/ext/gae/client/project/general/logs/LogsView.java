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

package com.codenvy.ide.ext.gae.client.project.general.logs;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;

/**
 * The view of {@link LogsPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 06.08.13 vlad $
 */
public interface LogsView extends View<LogsView.ActionDelegate> {
    /** Needs for delegate some function into Applications view. */
    interface ActionDelegate extends BaseActionDelegate {
        /** Perform action when get logs button clicked. */
        void onGetLogsButtonClicked();
    }

    /**
     * Set logs content into panel.
     *
     * @param content
     *         logs content.
     */
    void setLogsContent(String content);

    /**
     * Get number of days from which logs should be retrieved.
     *
     * @return count of days.
     */
    int getLogsDaysCount();

    /**
     * Get logs severity. One of few values ERROR, WARNING, INFO, DEBUG, CRITICAL or NULL to show all severities.
     *
     * @return severity status.
     */
    String getLogsSeverity();

    /**
     * Set title of the based tab.
     *
     * @param title
     *         title of the tab.
     */
    void setTitle(String title);
}
