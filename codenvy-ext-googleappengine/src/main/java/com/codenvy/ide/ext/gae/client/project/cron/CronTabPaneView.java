package com.codenvy.ide.ext.gae.client.project.cron;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.gae.shared.CronEntry;
import com.codenvy.ide.json.JsonArray;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
public interface CronTabPaneView extends View<CronTabPaneView.ActionDelegate> {
    interface ActionDelegate {
        void onUpdateButtonClicked();
    }

    void setCronEntryData(JsonArray<CronEntry> entries);
}
