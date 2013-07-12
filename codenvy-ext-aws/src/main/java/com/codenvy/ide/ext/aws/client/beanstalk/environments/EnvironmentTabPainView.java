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
