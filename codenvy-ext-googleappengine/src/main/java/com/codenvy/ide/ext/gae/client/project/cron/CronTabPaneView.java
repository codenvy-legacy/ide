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
