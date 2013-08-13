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
package com.codenvy.ide.ext.aws.client.ec2;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.aws.shared.ec2.InstanceInfo;
import com.codenvy.ide.json.JsonArray;

/**
 * The view for {@link EC2ManagerPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface EC2ManagerView extends View<EC2ManagerView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    public interface ActionDelegate {
        /**
         * Perform terminate selected instance.
         *
         * @param instanceInfo
         *         selected instance.
         */
        void onTerminateClicked(InstanceInfo instanceInfo);

        /**
         * Perform reboot selected instance.
         *
         * @param instanceInfo
         *         selected instance.
         */
        void onRebootClicked(InstanceInfo instanceInfo);

        /**
         * Perform start selected instance.
         *
         * @param instanceInfo
         *         selected instance.
         */
        void onStartClicked(InstanceInfo instanceInfo);

        /**
         * Perform stop selected instance.
         *
         * @param instanceInfo
         *         selected instance.
         */
        void onStopClicked(InstanceInfo instanceInfo);

        /** Perform close window. */
        void onCloseClicked();
    }

    /**
     * Set array of instances.
     *
     * @param instances
     *         array of instances.
     */
    public void setEC2Instances(JsonArray<InstanceInfo> instances);

    /**
     * Set array of properties for the selected EC2 instance.
     *
     * @param tags
     *         array of tags and their values.
     */
    public void setEC2Tags(JsonArray<Ec2Tag> tags);

    /**
     * Enable or disable state controlling buttons.
     *
     * @param enable
     *         true if enable.
     */
    public void setAllButtonsEnableState(boolean enable);

    /**
     * Return shown state for current window.
     *
     * @return true if shown, otherwise false.
     */
    public boolean isShown();

    /** Shows current dialog. */
    public void showDialog();

    /** Close current dialog. */
    public void close();
}
