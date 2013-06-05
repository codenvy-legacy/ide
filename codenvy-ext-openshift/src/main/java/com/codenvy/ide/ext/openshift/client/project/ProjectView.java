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
package com.codenvy.ide.ext.openshift.client.project;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.openshift.shared.AppInfo;

/**
 * The view of {@link ProjectPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface ProjectView extends View<ProjectView.ActionDelegate> {
    /** Needs for delegate some function into Project view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Close button. */
        public void onCloseClicked();

        /** Performs any actions appropriate in response to the user having pressed the Start application button. */
        public void onStartApplicationClicked(AppInfo application);

        /** Performs any actions appropriate in response to the user having pressed the Stop application button. */
        public void onStopApplicationClicked(AppInfo application);

        /** Performs any actions appropriate in response to the user having pressed the Restart application button. */
        public void onRestartApplicationClicked(AppInfo application);

        /** Performs any actions appropriate in response to the user having pressed the Show application properties button. */
        public void onShowApplicationPropertiesClicked(AppInfo application);

        /** Performs any actions appropriate in response to the user having pressed the Delete application button. */
        public void onDeleteApplicationDeleted(AppInfo application);
    }

    /**
     * Set application health.
     *
     * @param health
     *         two possibility values - STARTED/STOPPED
     */
    public void setApplicationHealth(String health);

    /**
     * Is current windows showed.
     *
     * @return true - if window showed, otherwise - false
     */
    public boolean isShown();

    /** Close current window. */
    public void close();

    /** Show window. */
    public void showDialog(AppInfo application);
}
