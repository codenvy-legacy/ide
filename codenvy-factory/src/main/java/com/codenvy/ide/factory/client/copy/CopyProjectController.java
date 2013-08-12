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
package com.codenvy.ide.factory.client.copy;

import com.codenvy.ide.client.util.logging.Log;
import com.codenvy.ide.factory.client.FactoryExtension;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.workspaceinfo.WorkspaceInfo;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Link;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CopyProjectController implements CopyProjectHandler, EditorFileOpenedHandler, EditorFileClosedHandler {

    private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

    public CopyProjectController() {
        IDE.addHandler(CopyProjectEvent.TYPE, this);
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onCopyProject(CopyProjectEvent event) {
        if (isUnsavedFilesExist()) {
            Dialogs.getInstance().showInfo(FactoryExtension.LOCALIZATION_CONSTANTS.saveAllChangesBeforeCopying());
            return;
        }

        try {
            VirtualFileSystem.getInstance()
                 .getChildren(VirtualFileSystem.getInstance().getInfo().getRoot(),
                              ItemType.PROJECT,
                              new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>())) {
                                  @Override
                                  protected void onSuccess(List<Item> result) {
                                      List<String> projectIds = new ArrayList<String>();
                                      for (Item project : result) {
                                          projectIds.add(project.getId() + ':' + project.getName());
                                      }
                                      if (!projectIds.isEmpty()) {
                                          Item firstItem = result.get(0);
                                          String projectsDownloadUrl = firstItem.getLinkByRelation(Link.REL_DOWNLOAD_ZIP).getHref();
                                          projectsDownloadUrl = projectsDownloadUrl.substring(0, projectsDownloadUrl.length() - firstItem.getId().length());
                                          doCopy(projectsDownloadUrl, projectIds);
                                      }
                                  }

                                  @Override
                                  protected void onFailure(Throwable exception) {
                                      Window.alert(exception.getMessage());
                                  }
                              });
        } catch (RequestException e) {
            Window.alert(e.getMessage());
        }
    }

    private void doCopy(String projectsDownloadUrl, List<String> projectIdList) {
        try {
            List<WorkspaceInfo> workspaces = IDE.user.getWorkspaces();
            String url;
            if (workspaces.size() > 1) {
                UrlBuilder builder = new UrlBuilder();
                url = builder.setProtocol(Window.Location.getProtocol()).setHost(Window.Location.getHost())
                             .setPath("/private/select-tenant").buildString();
            }
            else {
                url = workspaces.get(0).getUrl();
            }
            String projectIds = "";
            for (String projectId : projectIdList) {
                projectIds += projectId + ";";
            }
            url += "?" + CopySpec10.DOWNLOAD_URL + "=" + projectsDownloadUrl + "&" + CopySpec10.PROJECT_ID + "=" + projectIds;
            Window.Location.replace(url);
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            Log.error(getClass(), e);
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
     */
    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
     */
    @Override
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    private boolean isUnsavedFilesExist() {
        for (FileModel file : openedFiles.values()) {
            if (file.isContentChanged()) {
                return true;
            }
        }
        return false;
    }
}
