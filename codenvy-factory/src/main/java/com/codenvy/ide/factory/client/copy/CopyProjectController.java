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
package com.codenvy.ide.factory.client.copy;

import com.codenvy.ide.factory.client.FactoryExtension;
import com.codenvy.ide.factory.shared.CopySpec10;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
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
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Link;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Perform getting all projects that guest user have and try to copy them into permanent workspace.
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
    public void onCopyProject(final CopyProjectEvent event) {
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
                                              protected void onSuccess(List<Item> projects) {
                                                  List<String> projectsToCopy = new ArrayList<String>();
                                                  for (Item project : projects) {
                                                      projectsToCopy.add(project.getId() + ':' + project.getName());
                                                  }

                                                  if (!projects.isEmpty()) {
                                                      getExportLink(projects.get(0), projectsToCopy, event.isCreateAction());
                                                  }
                                              }

                                              @Override
                                              protected void onFailure(Throwable e) {
                                                  IDE.fireEvent(new ExceptionThrownEvent(e));
                                              }
                                          });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void getExportLink(final Item item, final List<String> projectsToCopy, final boolean createAction) {
        if (item.getLinks().isEmpty()) {
            try {
                VirtualFileSystem.getInstance()
                                 .getItemById(item.getId(),
                                              new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(item))) {
                                                  @Override
                                                  protected void onSuccess(ItemWrapper result) {
                                                      String exportLink = result.getItem().getLinkByRelation(Link.REL_EXPORT).getHref();
                                                      exportLink = exportLink.split(result.getItem().getId())[0];
                                                      doCopy(exportLink, projectsToCopy, createAction);
                                                  }

                                                  @Override
                                                  protected void onFailure(Throwable exception) {
                                                      IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                  }
                                              });
            } catch (RequestException e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            }
        } else {
            String exportLink = item.getLinkByRelation(Link.REL_EXPORT).getHref();
            exportLink = exportLink.split(item.getId())[0];
            doCopy(exportLink, projectsToCopy, createAction);
        }
    }

    private void doCopy(String baseDownloadUrl, List<String> projectsToCopy, boolean createAction) {
        try {
            StringBuilder url = new StringBuilder();

            UrlBuilder builder = new UrlBuilder();
            url.append(builder.setProtocol(Window.Location.getProtocol())
                              .setHost(Window.Location.getHost())
                              .setPath(createAction ? "/site/create-account" : "/site/private/select-tenant")
                              .buildString());

            url.append('?');

            for (String projectToCopy : projectsToCopy) {
                url.append(CopySpec10.PROJECT_ID);
                url.append('=');
                url.append(projectToCopy);
                url.append('&');
            }

            url.append(CopySpec10.DOWNLOAD_URL);
            url.append('=');
            url.append(URL.encode(baseDownloadUrl));

            Window.Location.replace(url.toString());
        } catch (Throwable e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    /** {@inheritDoc} */
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
