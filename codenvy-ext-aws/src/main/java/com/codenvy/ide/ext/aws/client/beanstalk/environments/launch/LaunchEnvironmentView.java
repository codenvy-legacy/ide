/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
