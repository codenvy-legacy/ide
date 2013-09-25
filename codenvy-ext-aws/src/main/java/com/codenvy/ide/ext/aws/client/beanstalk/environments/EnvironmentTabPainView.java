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
package com.codenvy.ide.ext.aws.client.beanstalk.environments;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentInfo;

import java.util.List;

/**
 * The view for {@link EnvironmentTabPainPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface EnvironmentTabPainView extends View<EnvironmentTabPainView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    interface ActionDelegate {
        /**
         * Perform show edit configuration window for specified environment.
         *
         * @param environment
         *         selected environment.
         */
        void onEditConfigurationButtonClicked(EnvironmentInfo environment);

        /**
         * Perform restart selected environment.
         *
         * @param environment
         *         selected environment.
         */
        void onRestartButtonClicked(EnvironmentInfo environment);

        /**
         * Perform rebuild selected environment.
         *
         * @param environment
         *         selected environment.
         */
        void onRebuildButtonClicked(EnvironmentInfo environment);

        /**
         * Perform terminate selected environment.
         *
         * @param environment
         *         selected environment.
         */
        void onTerminateButtonClicked(EnvironmentInfo environment);

        /**
         * Perform get logs for selected environment.
         *
         * @param environment
         *         selected environment.
         */
        void onGetLogsButtonCLicked(EnvironmentInfo environment);
    }

    /**
     * Set list of environments.
     *
     * @param environments
     *         list of environments.
     */
    void setEnvironments(List<EnvironmentInfo> environments);
}
