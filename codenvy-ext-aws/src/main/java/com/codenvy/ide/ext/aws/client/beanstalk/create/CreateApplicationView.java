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
package com.codenvy.ide.ext.aws.client.beanstalk.create;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

/**
 * The view for {@link CreateApplicationPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface CreateApplicationView extends View<CreateApplicationView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    interface ActionDelegate {
        /** Perform action when next button clicked. */
        void onNextButtonClicked();

        /** Perform action when back button clicked. */
        void onBackButtonClicked();

        /** Perform action when finish button clicked. */
        void onFinishButtonClicked();

        /** Perform action when cancel button clicked. */
        void onCancelButtonClicked();

        /**
         * Perform action when launch environment button clicked.
         *
         * @param enabled
         *         true if enable to launch new environment.
         */
        void onLaunchEnvironmentClicked(boolean enabled);
    }

    /**
     * Enable or disable create environment step.
     *
     * @param enabled
     *         true if enable.
     */
    void enableCreateEnvironmentStep(boolean enabled);

    /** Show create application step. */
    void showCreateApplicationStep();

    /** Show create environment step. */
    void showCreateEnvironmentStep();

    /**
     * Get application name which will be created.
     *
     * @return name of application.
     */
    String getApplicationName();

    /**
     * Get application description.
     *
     * @return description for created application.
     */
    String getDescription();

    /**
     * Get S3 Bucket id in which application will be created.
     *
     * @return S3 Bucket id.
     */
    String getS3Bucket();

    /**
     * Get S3 Object id for application to create.
     *
     * @return S3 Object id.
     */
    String getS3Key();

    /**
     * Get environment name for new application.
     *
     * @return environment name.
     */
    String getEnvironmentName();

    /**
     * Get environment description for new application.
     *
     * @return description for the application
     */
    String getEnvironmentDescription();

    /**
     * Get solution stack technology.
     *
     * @return solution stack technology.
     */
    String getSolutionStack();

    /**
     * Set list of solution stack technologies.
     *
     * @param stack
     *         list of stack technologies.
     */
    void setSolutionStacks(JsonArray<String> stack);

    /**
     * Launch new environment or not.
     *
     * @return true if launch.
     */
    boolean launchNewEnvironment();

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
