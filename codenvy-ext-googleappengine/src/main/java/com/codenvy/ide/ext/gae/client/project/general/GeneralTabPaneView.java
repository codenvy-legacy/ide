package com.codenvy.ide.ext.gae.client.project.general;

import com.codenvy.ide.api.mvp.View;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
public interface GeneralTabPaneView extends View<GeneralTabPaneView.ActionDelegate> {
    interface ActionDelegate {
        void onUpdateApplicationClicked();

        void onRollBackApplicationClicked();

        void onGetApplicationLogsClicked();

        void onUpdateIndexesClicked();

        void onVacuumIndexesClicked();

        void onUpdatePageSpeedClicked();

        void onUpdateQueuesClicked();

        void onUpdateDoSClicked();
    }
}
