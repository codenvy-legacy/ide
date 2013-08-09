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
