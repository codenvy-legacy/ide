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
package com.codenvy.ide.ext.openshift.client.wizard;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link OpenShiftPageView}.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface OpenShiftPageView extends View<OpenShiftPageView.ActionDelegate> {
    /** Needs for delegate some function into OpenShift view. */
    public interface ActionDelegate {
        /** Perform any actions on application name field changed value. */
        public void onApplicationNameChanged();
    }

    /**
     * Get application name to be created on OpenShift.
     *
     * @return application name
     */
    public String getName();

    /**
     * Set application name.
     *
     * @param applicationName
     *         application name
     */
    public void setName(String applicationName);

    /**
     * Indicate that user wants to use autoscaling after creating application.
     *
     * @return true - if use autoscaling, otherwise false
     */
    public boolean getScalingValue();

    /**
     * Get application type which will be created on OpenShift.
     *
     * @return one of possibles application types that support by OpenShift
     */
    public String getApplicationType();

    /**
     * Set application types that supports by OpenShift.
     *
     * @param applicationTypes
     *         json array of application types
     */
    public void setApplicationTypes(JsonArray<String> applicationTypes);
}
