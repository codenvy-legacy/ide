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
