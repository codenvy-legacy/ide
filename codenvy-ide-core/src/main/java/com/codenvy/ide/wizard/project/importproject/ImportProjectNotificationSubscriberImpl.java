/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.wizard.project.importproject;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.importproject.ImportProjectNotificationSubscriber;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
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
        subscribe(projectName, new Notification(locale.importingProject(), Notification.Status.PROGRESS));
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

    static class LineUnmarshaller implements com.codenvy.ide.websocket.rest.Unmarshallable<String> {
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
