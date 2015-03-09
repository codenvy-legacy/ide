/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.projectimport.wizard;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.project.wizard.ImportProjectNotificationSubscriber;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.commons.exception.UnmarshallerException;
import org.eclipse.che.ide.projectimport.wizard.presenter.ImportProjectWizardPresenter;
import org.eclipse.che.ide.util.loging.Log;
import org.eclipse.che.ide.websocket.Message;
import org.eclipse.che.ide.websocket.MessageBus;
import org.eclipse.che.ide.websocket.WebSocketException;
import org.eclipse.che.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ImportProjectNotificationSubscriberImpl implements ImportProjectNotificationSubscriber {
    private CoreLocalizationConstant    locale;
    private NotificationManager         notificationManager;
    private String                      workspaceId;
    private MessageBus                  messageBus;
    private String                      wsChannel;
    private Notification                notification;
    private SubscriptionHandler<String> importProjectOutputWShandler;

    @Inject
    public ImportProjectNotificationSubscriberImpl(CoreLocalizationConstant locale,
                                                   NotificationManager notificationManager,
                                                   @Named("workspaceId") String workspaceId,
                                                   MessageBus messageBus) {
        this.locale = locale;
        this.notificationManager = notificationManager;
        this.workspaceId = workspaceId;
        this.messageBus = messageBus;

    }

    @Override
    public void subscribe(String projectName) {
        notification = new Notification(locale.importingProject(), Notification.Status.PROGRESS, true);
        subscribe(projectName, notification);
        notificationManager.showNotification(notification);
    }

    @Override
    public void subscribe(String projectName, Notification existingNotification) {
        notification = existingNotification;
        wsChannel = "importProject:output:" + workspaceId + ":" + projectName;
        importProjectOutputWShandler = new SubscriptionHandler<String>(new LineUnmarshaller()) {

            @Override
            protected void onMessageReceived(String result) {
                notification.setMessage(locale.importingProject() + " " + result);
            }

            @Override
            protected void onErrorReceived(Throwable throwable) {
                try {
                    messageBus.unsubscribe(wsChannel, this);
                    notification.setType(Notification.Type.ERROR);
                    notification.setImportant(true);
                    notification.setMessage(locale.importProjectMessageFailure() + " " + throwable.getMessage());
                    Log.error(getClass(), throwable);
                } catch (WebSocketException e) {
                    Log.error(getClass(), e);
                }
            }
        };

        try {
            messageBus.subscribe(wsChannel, importProjectOutputWShandler);
        } catch (WebSocketException e1) {
            Log.error(ImportProjectWizardPresenter.class, e1);
        }
    }

    @Override
    public void onSuccess() {
        try {
            messageBus.unsubscribe(wsChannel, importProjectOutputWShandler);
        } catch (WebSocketException e) {
            Log.error(getClass(), e);
        }
        notification.setStatus(Notification.Status.FINISHED);
        notification.setMessage(locale.importProjectMessageSuccess());
    }

    @Override
    public void onFailure(String errorMessage) {
        try {
            messageBus.unsubscribe(wsChannel, importProjectOutputWShandler);
        } catch (WebSocketException e) {
            Log.error(getClass(), e);
        }
        notification.setStatus(Notification.Status.FINISHED);
        notification.setType(Notification.Type.ERROR);
        notification.setImportant(true);
        notification.setMessage(errorMessage);
    }

    static class LineUnmarshaller implements org.eclipse.che.ide.websocket.rest.Unmarshallable<String> {
        private String line;

        @Override
        public void unmarshal(Message response) throws UnmarshallerException {
            JSONObject jsonObject = JSONParser.parseStrict(response.getBody()).isObject();
            if (jsonObject == null) {
                return;
            }
            if (jsonObject.containsKey("line")) {
                line = jsonObject.get("line").isString().stringValue();
            }
        }

        @Override
        public String getPayload() {
            return line;
        }
    }

}
