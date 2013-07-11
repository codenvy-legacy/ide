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
package com.codenvy.ide.ext.extruntime.client.wizard;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link ExtensionPagePresenter}.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtensionPageView.java Jul 8, 2013 4:07:39 PM azatsarynnyy $
 */
public interface ExtensionPageView extends View<ExtensionPageView.ActionDelegate> {
    /** Needs for delegate some function into {@link ExtensionPageView}. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having changed group id. */
        void onGroupIdChanged();

        /** Performs any actions appropriate in response to the user having changed artifact id. */
        void onArtifactIdChanged();

        /** Performs any actions appropriate in response to the user having changed artifact version. */
        void onVersionChanged();
    }

    /**
     * Returns group id.
     * 
     * @return group id
     */
    String getGroupId();

    /**
     * Set group id.
     * 
     * @param group id
     */
    void setGroupId(String groupId);

    /**
     * Returns artifact id.
     * 
     * @return artifact id
     */
    String getArtifactId();

    /**
     * Set artifact id.
     * 
     * @param artifact id
     */
    void setArtifactId(String artifactId);

    /**
     * Returns artifact version.
     * 
     * @return artifact version
     */
    String getVersion();

    /**
     * Set artifact version.
     * 
     * @param artifact version
     */
    void setVersion(String version);

}
