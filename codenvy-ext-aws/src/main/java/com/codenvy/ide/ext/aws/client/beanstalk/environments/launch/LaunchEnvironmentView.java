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
package com.codenvy.ide.ext.aws.client.beanstalk.environments.launch;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

/**
 * The view for {@link LaunchEnvironmentPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface LaunchEnvironmentView extends View<LaunchEnvironmentView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    interface ActionDelegate {
        /** Perform action when launch button clicked. */
        void onLaunchButtonClicked();

        /** Perform action when cancel button clicked. */
        void onCancelButtonClicked();

        /** Perform action when name field value changed. */
        void onNameFieldValueChanged();
    }

    /**
     * Get environment name.
     *
     * @return environment name.
     */
    String getEnvName();

    /**
     * Get environment description.
     *
     * @return environment description.
     */
    String getEnvDescription();

    /**
     * Get solution stack technology.
     *
     * @return solution stack technology.
     */
    String getSolutionStack();

    /**
     * Set list of available solution stack technologies.
     *
     * @param values
     *         list of stack technologies.
     */
    void setSolutionStackValues(JsonArray<String> values);

    /**
     * Get version label.
     *
     * @return version label.
     */
    String getVersionField();

    /**
     * Set version values.
     *
     * @param values
     *         version labels.
     * @param selectedValue
     *         label which must be selected.
     */
    void setVersionValues(JsonArray<String> values, String selectedValue);

    /** @param enabled */
    void enableLaunchButton(boolean enabled);

    /** Set focus in environment field. */
    void focusInEnvNameField();

    /**
     * Return shown state for current window.
     *
     * @return true if shown, otherwise false.
     */
    boolean isShown();

    /** Shows current dialog. */
    void showDialog();

    /** Close current dialog. */
    void close();
}
