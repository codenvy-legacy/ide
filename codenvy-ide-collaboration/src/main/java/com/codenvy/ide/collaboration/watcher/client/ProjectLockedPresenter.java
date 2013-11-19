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

    /**
     * Creates instance of this {@link ProjectLockedPresenter}
     * 
     * @param project
     * @param operation
     */
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
        view.setUserList(new SafeHtmlBuilder().appendEscaped("This project is opened by other user(s).").toSafeHtml());
        ChatExtension.get().getProjectUserListeners().add(this);
    }

    /**
     * @see com.codenvy.ide.collaboration.ResourceLockedView.ActionDelegate#onClose()
     */
    @Override
    public void onClose() {
        IDE.getInstance().closeView(view.getId());
        ChatExtension.get().getProjectUserListeners().remove(this);
    }

    /**
     * @see com.codenvy.ide.collaboration.ResourceLockedView.ActionDelegate#onNotify()
     */
    @Override
    public void onNotify() {
        DtoClientImpls.ProjectOperationNotificationImpl notification = DtoClientImpls.ProjectOperationNotificationImpl.make();
        notification.setProjectId(project.getId()).setClientId(BootstrapSession.getBootstrapSession().getActiveClientId());
        String username = BootstrapSession.getBootstrapSession().getUsername();
        notification.setMessage(username + " wants to delete " + project.getName() + " and asks you to close this project.");
        VfsWatcherExtension.get().collaborationApi.PROJECT_NOTIFICATION.send(notification);
    }

    /**
     * @see com.codenvy.ide.collaboration.chat.client.ProjectUsersListener#onUserOpenProject()
     */
    @Override
    public void onUserOpenProject() {
    }

    /**
     * @see com.codenvy.ide.collaboration.chat.client.ProjectUsersListener#onUserCloseProject()
     */
    @Override
    public void onUserCloseProject() {
        if (ChatExtension.get().getCurrentProjectParticipants().size() <= 1) {
            Dialogs.getInstance().showInfo("All users have just closed the project, now you may perform the operation.");
            onClose();
        }
    }
}
