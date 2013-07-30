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
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.workspaceinfo.WorkspaceInfo;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Link;

import java.util.List;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CopyProjectController implements CopyProjectHandler, ProjectOpenedHandler {

    private ProjectModel project;

    public CopyProjectController() {
        IDE.addHandler(CopyProjectEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onCopyProject(CopyProjectEvent event) {
        Window.alert(project + "");
        try{
        if(project == null){
            return;
        }
        List<WorkspaceInfo> workspaces = IDE.user.getWorkspaces();
        String url;
        if(workspaces.size() > 1){
            UrlBuilder builder = new UrlBuilder();
            url = builder.setProtocol(Window.Location.getProtocol()).setHost(Window.Location.getHost())
                              .setPath("/private/select-tenant").buildString();
        }
        else{
            url = workspaces.get(0).getUrl();

        }
        url += "?projecturl=" + project.getLinkByRelation(Link.REL_DOWNLOAD_ZIP).getHref()+"&projectname=" + project.getName();
        Window.alert(url);
        Window.Location.replace(url);
        }
        catch (Throwable e){
            Window.alert(e.getMessage());
            Log.error(getClass(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        project = event.getProject();
    }
}
