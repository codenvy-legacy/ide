package com.codenvy.ide.ext.gae.client.project.cron;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.gae.shared.CronEntry;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link CronTabPanePresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
public interface CronTabPaneView extends View<CronTabPaneView.ActionDelegate> {
    /** Needs for delegate some function into Applications view. */
    interface ActionDelegate {
        /**
         * Perform action when update cron button clicked.
         */
        void onUpdateButtonClicked();
    }

    /**
     * Set crons list.
     *
     * @param entries
     *         list of {@link com.codenvy.ide.ext.gae.shared.CronEntry}.
     */
    void setCronEntryData(JsonArray<CronEntry> entries);
}
