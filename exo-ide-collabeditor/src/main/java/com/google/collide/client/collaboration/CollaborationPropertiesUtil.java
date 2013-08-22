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
package com.google.collide.client.collaboration;

import com.codenvy.ide.client.util.logging.Log;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyImpl;

import java.util.Collections;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public final class CollaborationPropertiesUtil {

    public static final String COLLABORATION_MODE = "codenvyCollaborationMode";

    public static boolean isCollaborationEnabled(ProjectModel project) {
        String value = project.getPropertyValue(COLLABORATION_MODE);
        if (value == null) {
            //by default collaboration disabled
            return false;
        }


        return Boolean.valueOf(value);
    }

    public static void updateCollaboration(ProjectModel project, boolean isEnabled){
        Property property = project.getProperty(COLLABORATION_MODE);
        if(property == null){
            property = new PropertyImpl(COLLABORATION_MODE, "");
            project.getProperties().add(property);
        }

        property.setValue(Collections.singletonList(String.valueOf(isEnabled)));
        try {
            VirtualFileSystem.getInstance().updateItem(project, null, new AsyncRequestCallback<ItemWrapper>() {
                @Override
                protected void onSuccess(ItemWrapper result) {
                    //ignore
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Log.debug(CollaborationPropertiesUtil.class, exception);
                }
            });
        } catch (RequestException e) {
            Log.debug(CollaborationPropertiesUtil.class, e);
        }
    }
}
