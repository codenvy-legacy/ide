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
package com.codenvy.ide.ext.openshift.client.list;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.openshift.client.info.ApplicationProperty;
import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.ext.openshift.shared.OpenShiftEmbeddableCartridge;

import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface ApplicationListView extends View<ApplicationListView.ActionDelegate> {
    /** Needs for delegate some function into Login view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        public void onCloseClicked();

        public void onChangeDomainNameClicked();

        public void onChangeAccountClicked();

        public void onCreateCartridgeClicked();

        public void onApplicationDeleteClicked(AppInfo application);

//        public void onApplicationSelectClicked(AppInfo application);

        public void onCartridgeStartClicked(OpenShiftEmbeddableCartridge cartridge);

        public void onCartridgeStopClicked(OpenShiftEmbeddableCartridge cartridge);

        public void onCartridgeRestartClicked(OpenShiftEmbeddableCartridge cartridge);

        public void onCartridgeReloadClicked(OpenShiftEmbeddableCartridge cartridge);

        public void onCartridgeDeleteClicked(OpenShiftEmbeddableCartridge cartridge);
    }

    public void setApplications(List<AppInfo> applications);

    public AppInfo getSelectedApplication();

    public void setCartridges(List<OpenShiftEmbeddableCartridge> cartridges);

    public void setApplicationInfo(List<ApplicationProperty> properties);

    public void setUserLogin(String userLogin);

    public void setUserDomain(String userDomain);

    public boolean isShown();

    public void close();

    public void showDialog();
}
