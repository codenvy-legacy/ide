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
package com.codenvy.ide.collaboration;

import com.codenvy.ide.collaboration.ResourceLockedView.ActionDelegate;
import com.codenvy.ide.json.shared.JsonArray;
import com.google.collide.client.CollabEditorExtension;
import com.google.collide.client.bootstrap.BootstrapSession;
import com.google.collide.client.collaboration.CollaborationManager;
import com.google.collide.client.collaboration.CollaborationManager.ParticipantsListener;
import com.google.collide.dto.FileOperationNotification.Operation;
import com.google.collide.dto.ParticipantUserDetails;
import com.google.collide.dto.UserDetails;
import com.google.collide.dto.client.DtoClientImpls.FileOperationNotificationImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ResourceLockedPresenter implements ActionDelegate, ParticipantsListener {

    @DefaultLocale("en")
    public interface ResourceLockedMessages extends Messages {
        @DefaultMessage("This file is opened by {0} users.")
        @AlternateMessage({"one", "This file is opened by other user."})
        String file(@PluralCount int users);

        @DefaultMessage("This folder contains file(s) opened by {0} users.")
        @AlternateMessage({"one", "This folder contains file(s) opened by other user."})
        String folder(@PluralCount int size);
    }


    private static final ResourceLockedMessages MESSAGES = GWT.create(ResourceLockedMessages.class);

    private ResourceLockedView view;

    private CollaborationManager manager;

    private String path;

    private Item item;
    private String targetPaht;

    private Operation operation;

    public ResourceLockedPresenter(SafeHtml message, CollaborationManager manager, String path, Item item,
                                   String targetPath, Operation operation) {
        this.manager = manager;
        this.path = path;
        this.item = item;

        this.targetPaht = targetPath;
        this.operation = operation;
        view = GWT.create(ResourceLockedView.class);
        view.setDelegate(this);
        IDE.getInstance().openView(view);
        manager.getParticipantsListenerManager().add(this);

        view.setMessageText(message);
        updateUserList(path);
    }

    private void updateUserList(String path) {
        JsonArray<ParticipantUserDetails> participants = manager.getParticipantsForFile(path);
        if (participants == null) {
            Dialogs.getInstance().showInfo("All users close file, now you may perform operation.");
            onClose();
            return;
        }

        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        if (item instanceof FileModel) {
            builder.appendHtmlConstant(MESSAGES.file(participants.size()));
        } else  if (item instanceof FolderModel){
            builder.appendHtmlConstant(MESSAGES.folder(participants.size()));
        }
        view.setUserList(builder.toSafeHtml());
    }

    /** {@inheritDoc} */
    @Override
    public void onClose() {
        IDE.getInstance().closeView(view.getId());
        manager.getParticipantsListenerManager().remove(this);
    }

    /** {@inheritDoc} */
    @Override
    public void onNotify() {
        FileOperationNotificationImpl notification = FileOperationNotificationImpl.make();
        notification.setFilePath(path);
        notification.setOperation(operation);
        notification.setTarget(targetPaht);
        notification.setUserId(BootstrapSession.getBootstrapSession().getActiveClientId());
        notification.setEditSessionId(manager.getEditSessionId(path));
        CollabEditorExtension.get().getContext().getFrontendApi().FILE_OPERATION_NOTIFY.send(notification);
        view.setNotifyButtonEnabled(false);
    }

    /** {@inheritDoc} */
    @Override
    public void userOpenFile(String path, UserDetails user) {
        if (path.equals(this.path)) {
            updateUserList(path);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void userCloseFile(String path, UserDetails user) {
        if (path.equals(this.path)) {
            updateUserList(path);
        }
    }
}
