package com.codenvy.ide.ext.gae.client.wizard;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link GAEWizardPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface GAEWizardView extends View<GAEWizardView.ActionDelegate> {
    /** Needs for delegate some function into Applications view. */
    interface ActionDelegate {
        /**
         * Perform action when application id text field changed.
         */
        void onApplicationIdChanged();

        /**
         * Perform action when required exist application id checked.
         */
        void onAppIdRequiredClicked();
    }

    /**
     * Get application id if user activated checkbox for existed application on Google App Engine.
     *
     * @return name of the existed application.
     */
    String getApplicationId();

    /**
     * Set name for existed application on Google App Engine.
     *
     * @param applicationId
     *         name of the existed application.
     */
    void setApplicationId(String applicationId);

    /**
     * Get required exist application id checkbox state.
     *
     * @return true if activated, otherwise false.
     */
    boolean getAppIdRequired();

    /**
     * Set state of required exist application id checkbox.
     *
     * @param required
     *         true for activate, otherwise false.
     */
    void setAppIdRequired(boolean required);

    /**
     * Enable exist application id text field.
     *
     * @param enable
     *         true for enable, otherwise false.
     */
    void enableApplicationIdField(boolean enable);
}
