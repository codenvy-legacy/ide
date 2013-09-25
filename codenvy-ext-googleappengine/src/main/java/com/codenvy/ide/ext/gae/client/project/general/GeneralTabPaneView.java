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

package com.codenvy.ide.ext.gae.client.project.general;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link GeneralTabPanePresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
public interface GeneralTabPaneView extends View<GeneralTabPaneView.ActionDelegate> {
    /** Needs for delegate some function into Applications view. */
    interface ActionDelegate {
        /**
         * Perform action when update application button clicked.
         */
        void onUpdateApplicationClicked();

        /**
         * Perform action when rollback application button clicked.
         */
        void onRollBackApplicationClicked();

        /**
         * Perform action when get application logs button clicked.
         */
        void onGetApplicationLogsClicked();

        /**
         * Perform action when update indexes button clicked.
         */
        void onUpdateIndexesClicked();

        /**
         * Perform action when vacuum indexes button clicked.
         */
        void onVacuumIndexesClicked();

        /**
         * Perform action when update page speed button clicked.
         */
        void onUpdatePageSpeedClicked();

        /**
         * Perform action when update queue button clicked.
         */
        void onUpdateQueuesClicked();

        /**
         * Perform action when update dos button clicked.
         */
        void onUpdateDoSClicked();
    }
}
