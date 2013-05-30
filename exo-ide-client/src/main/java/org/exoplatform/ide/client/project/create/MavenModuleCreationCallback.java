/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.project.create;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ModuleCreatedEvent;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class MavenModuleCreationCallback implements EditorFileOpenedHandler, EditorFileClosedHandler, VfsChangedHandler {

    private static MavenModuleCreationCallback instance;

    public static MavenModuleCreationCallback getInstance() {
        return instance;
    }

    private VirtualFileSystemInfo vfs;

    private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

    public MavenModuleCreationCallback() {
        instance = this;

        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
    }

    @Override
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    public boolean isPomXMLOpened(ProjectModel project) {
        for (Item child : project.getChildren().getItems()) {
            if ("pom.xml".equals(child.getName())) {
                return openedFiles.containsKey(child.getId());
            }
        }

        return false;
    }

    public void moduleCreated(final ProjectModel parent, final ProjectModel module) {
        // ask server to add module in pom.xml
        try {
            String url =
                    Utils.getRestContext() + Utils.getWorkspaceName() + "/project/addModule" +
                    "?vfsId=" + vfs.getId() +
                    "&projectId=" + parent.getId() +
                    "&moduleName=" + module.getName();

            AsyncRequest.build(RequestBuilder.GET, URL.encode(url), false).loader(IDELoader.get())
                        .send(new AsyncRequestCallback<Void>() {
                            @Override
                            protected void onSuccess(Void result) {
                                IDE.fireEvent(new ModuleCreatedEvent(module));
                                IDE.fireEvent(new RefreshBrowserEvent(parent));
                            }

                            @Override
                            protected void onFailure(Throwable e) {
                                IDE.fireEvent(new ExceptionThrownEvent(e.getMessage()));
                            }
                        });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e.getMessage()));
        }
    }

    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfs = event.getVfsInfo();
    }

}
