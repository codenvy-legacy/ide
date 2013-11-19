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
package com.google.collide.client.collaboration;

import com.codenvy.ide.notification.Notification;
import com.codenvy.ide.notification.NotificationManager;
import com.codenvy.ide.users.UsersModel;
import com.google.collide.client.code.Participant;
import com.google.collide.client.common.BaseResources.Css;
import com.google.collide.dto.FileOperationNotification;
import com.google.collide.dto.FileOperationNotification.Operation;
import com.google.collide.dto.RoutingTypes;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.framework.websocket.MessageFilter;
import org.exoplatform.ide.client.framework.websocket.MessageFilter.MessageRecipient;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class NotificationController {

    private MessageRecipient<FileOperationNotification> fileOperationNotificationRecipient =
            new MessageRecipient<FileOperationNotification>() {
                @Override
                public void onMessageReceived(FileOperationNotification message) {
                    showFileOperationNotification(message);
                }
            };

    private NotificationManager manager;

    private UsersModel usersModel;

    public NotificationController(NotificationManager manager, CollaborationManager collaborationManager,
                                  MessageFilter messageFilter, UsersModel usersModel, HandlerManager eventBus, Css css) {
        this.usersModel = usersModel;
        this.manager = manager;
        messageFilter.registerMessageRecipient(RoutingTypes.FILEOPERATIONNOTIFICATION, fileOperationNotificationRecipient);
    }

    private void showFileOperationNotification(FileOperationNotification notification) {
        Participant user = usersModel.getParticipant(usersModel.getUserIdByClientId(notification.getUserId()));
        String targetPath = notification.getTarget();
        targetPath = targetPath.substring(targetPath.lastIndexOf('/') + 1, targetPath.length());
        String fileName = notification.getFilePath();
        fileName = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.length());
        manager.addNotification(new Notification("User " + user.getDisplayName() + " wants to " + getOperationName(
                notification.getOperation()) + " " + targetPath + " and asks you to close file " + fileName, -1));
    }

    private String getOperationName(Operation operation) {
        switch (operation) {
            case RENAME:
                return "rename";
            case DELETE:
                return "delete";
            case MOVE:
                return "move";
            case REFACTORING:
                return "perform refactoring";

            default:
                return operation.name().toLowerCase();
        }
    }
}
