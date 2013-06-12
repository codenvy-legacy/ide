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
package com.codenvy.ide.collaboration.watcher.client;

import com.codenvy.ide.collaboration.ResourceLockedView;
import com.codenvy.ide.collaboration.chat.client.ChatExtension;
import com.codenvy.ide.collaboration.chat.client.ProjectUsersListener;
import com.codenvy.ide.collaboration.dto.client.DtoClientImpls;
import com.google.collide.client.bootstrap.BootstrapSession;
import com.google.collide.dto.FileOperationNotification;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ProjectLockedPresenter implements ResourceLockedView.ActionDelegate, ProjectUsersListener {
    private final ResourceLockedView                  view;
    private final ProjectModel                        project;
    private final FileOperationNotification.Operation operation;

    public ProjectLockedPresenter(ProjectModel project, FileOperationNotification.Operation operation) {
        this.project = project;
        this.operation = operation;
        view = GWT.create(ResourceLockedView.class);
        view.setDelegate(this);
        IDE.getInstance().openView(view);
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("Can't delete project <b>").appendEscaped(
                project.getName()).appendHtmlConstant("</b>").toSafeHtml();
        view.setMessageText(builder.toSafeHtml());
        view.setUserList(new SafeHtmlBuilder().appendEscaped("This project opened by other user(s).").toSafeHtml());
        ChatExtension.get().getProjectUserListeners().add(this);
    }

    @Override
    public void onClose() {
        IDE.getInstance().closeView(view.getId());
        ChatExtension.get().getProjectUserListeners().remove(this);
    }

    @Override
    public void onNotify() {
        DtoClientImpls.ProjectOperationNotificationImpl notification = DtoClientImpls.ProjectOperationNotificationImpl.make();
        notification.setProjectId(project.getId()).setClientId(BootstrapSession.getBootstrapSession().getActiveClientId());
        String username = BootstrapSession.getBootstrapSession().getUsername();
        notification.setMessage(username + " wont to delete " + project.getName() + " and ask you to close this project.");
        VfsWatcherExtension.get().collaborationApi.PROJECT_NOTOFICATION.send(notification);
    }

    @Override
    public void onUserOpenProject() {
    }

    @Override
    public void onUserCloseProject() {
        if (ChatExtension.get().getCurrentProjectParticipants().size() <= 1) {

            Dialogs.getInstance().showInfo("All users close project, now you may perform operation.");
            onClose();
        }
    }
}
