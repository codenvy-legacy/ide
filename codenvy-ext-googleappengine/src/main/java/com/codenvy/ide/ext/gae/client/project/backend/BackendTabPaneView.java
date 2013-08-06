package com.codenvy.ide.ext.gae.client.project.backend;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.gae.shared.Backend;
import com.codenvy.ide.json.JsonArray;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
public interface BackendTabPaneView extends View<BackendTabPaneView.ActionDelegate> {
    interface ActionDelegate {
        void onConfigureBackendClicked();

        void onDeleteBackendClicked();

        void onUpdateBackendClicked();

        void onRollBackBackendClicked();

        void onUpdateAllBackendsClicked();

        void onRollBackAllBackendsClicked();

        void onUpdateBackendState(String backendName, Backend.State backendState);
    }

    void setBackendsList(JsonArray<Backend> backends);

    void setEnableUpdateButtons(boolean enable);

    Backend getSelectedBackend();
}
