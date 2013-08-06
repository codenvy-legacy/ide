package com.codenvy.ide.ext.gae.client.wizard;

import com.codenvy.ide.api.mvp.View;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface GAEWizardView extends View<GAEWizardView.ActionDelegate> {
    interface ActionDelegate {
        void onApplicationIdChanged();

        void onAppIdRequiredClicked();
    }

    String getApplicationId();

    void setApplicationId(String applicationId);

    boolean getAppIdRequired();

    void setAppIdRequired(boolean required);

    void enableApplicationIdField(boolean enable);
}
