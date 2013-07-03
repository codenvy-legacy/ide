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
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface EC2ManagerView extends View<EC2ManagerView.ActionDelegate> {
    public interface ActionDelegate {
        void onTerminateClicked(InstanceInfo instanceInfo);

        void onRebootClicked(InstanceInfo instanceInfo);

        void onStartClicked(InstanceInfo instanceInfo);

        void onStopClicked(InstanceInfo instanceInfo);

        void onCloseClicked();
    }

    public void setEC2Instances(JsonArray<InstanceInfo> instances);

    public void setEC2Tags(JsonArray<Ec2Tag> tags);

    public void setAllButtonsEnableState(boolean enable);

    public boolean isShown();

    public void showDialog();

    public void close();
}
