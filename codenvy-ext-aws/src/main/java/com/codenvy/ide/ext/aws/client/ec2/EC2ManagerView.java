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
