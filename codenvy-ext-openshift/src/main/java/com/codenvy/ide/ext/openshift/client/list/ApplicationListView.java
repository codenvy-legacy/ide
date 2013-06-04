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
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link ApplicationProperty}.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface ApplicationListView extends View<ApplicationListView.ActionDelegate> {
    /** Needs for delegate some function into Applications list view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Close button. */
        public void onCloseClicked();

        /** Performs any actions appropriate to the response to the user having pressed the Domain change button. */
        public void onChangeDomainNameClicked();

        /** Performs any actions appropriate to the response to the user having pressed the Account change button. */
        public void onChangeAccountClicked();

        /** Performs any actions appropriate to the response to the user having pressed the Create cartridge button. */
        public void onCreateCartridgeClicked();

        /** Performs any actions appropriate to the response to the user having pressed the Delete application button. */
        public void onApplicationDeleteClicked(AppInfo application);

        /** Performs any actions appropriate to the response to the user having pressed the Start cartridge button. */
        public void onCartridgeStartClicked(OpenShiftEmbeddableCartridge cartridge);

        /** Performs any actions appropriate to the response to the user having pressed the Stop cartridge button. */
        public void onCartridgeStopClicked(OpenShiftEmbeddableCartridge cartridge);

        /** Performs any actions appropriate to the response to the user having pressed the Delete cartridge button. */
        public void onCartridgeRestartClicked(OpenShiftEmbeddableCartridge cartridge);

        /** Performs any actions appropriate to the response to the user having pressed the Reload cartridge button. */
        public void onCartridgeReloadClicked(OpenShiftEmbeddableCartridge cartridge);

        /** Performs any actions appropriate to the response to the user having pressed the Delete cartridge button. */
        public void onCartridgeDeleteClicked(OpenShiftEmbeddableCartridge cartridge);
    }

    /**
     * Set applications list.
     *
     * @param applications
     *         json array list with application
     */
    public void setApplications(JsonArray<AppInfo> applications);

    /**
     * Get selected application in applications list to view properties and cartridges list.
     *
     * @return object of current selected application
     */
    public AppInfo getSelectedApplication();

    /**
     * Set cartridge list into the view.
     *
     * @param cartridges
     *         list of cartridges
     */
    public void setCartridges(JsonArray<OpenShiftEmbeddableCartridge> cartridges);

    /**
     * Set application's info.
     *
     * @param properties
     *         list of properties from the selected application
     */
    public void setApplicationInfo(JsonArray<ApplicationProperty> properties);

    /**
     * Set user login.
     *
     * @param userLogin
     *         user's email on OpenShift
     */
    public void setUserLogin(String userLogin);

    /**
     * Set user domain(namespace).
     *
     * @param userDomain
     *         user's namespace on OpenShift
     */
    public void setUserDomain(String userDomain);

    /**
     * Is current windows showed.
     *
     * @return true - if window showed, otherwise - false
     */
    public boolean isShown();

    /** Close current window. */
    public void close();

    /** Show window. */
    public void showDialog();
}
