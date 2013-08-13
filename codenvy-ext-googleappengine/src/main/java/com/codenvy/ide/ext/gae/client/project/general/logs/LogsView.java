package com.codenvy.ide.ext.gae.client.project.general.logs;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.part.base.BaseActionDelegate;

/**
 * The view of {@link LogsPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 06.08.13 vlad $
 */
public interface LogsView extends View<LogsView.ActionDelegate> {
    /** Needs for delegate some function into Applications view. */
    interface ActionDelegate extends BaseActionDelegate {
        /**
         * Perform action when get logs button clicked.
         */
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
