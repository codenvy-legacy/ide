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
package com.codenvy.ide.ext.aws.client.beanstalk.wizard;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link BeanstalkPagePresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface BeanstalkPageView extends View<BeanstalkPageView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    interface ActionDelegate {
        /** Perform action when application name changed. */
        void onApplicationNameChange();

        /** Perform action when environment name changed. */
        void onEnvironmentNameChange();
    }

    /**
     * Set application name.
     *
     * @param applicationName
     *         application name.
     */
    void setApplicationName(String applicationName);

    /**
     * Get application name.
     *
     * @return application name which user select as Elastic Beanstalk Application.
     */
    String getApplicationName();

    /**
     * Set environment name.
     *
     * @param environmentName
     *         environment name.
     */
    void setEnvironmentName(String environmentName);

    /**
     * Get environment name.
     *
     * @return environment name which user select to create new environment.
     */
    String getEnvironmentName();

    /**
     * Set list of solution stack technologies.
     *
     * @param stack
     *         array of solution stack.
     */
    void setSolutionStack(JsonArray<String> stack);

    /**
     * Get solution stack technology which user will be used.
     *
     * @return name of solution stack technology.
     */
    String getSolutionStack();
}
