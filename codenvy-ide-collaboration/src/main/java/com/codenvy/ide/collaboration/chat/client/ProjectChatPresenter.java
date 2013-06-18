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
package com.codenvy.ide.collaboration.chat.client;

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.KeyboardEvent.KeyCode;

import com.codenvy.ide.client.util.SignalEvent;
import com.codenvy.ide.client.util.SignalEventUtils;
import com.codenvy.ide.client.util.logging.Log;
import com.codenvy.ide.collaboration.dto.ChatCodePointMessage;
import com.codenvy.ide.collaboration.dto.ChatMessage;
import com.codenvy.ide.collaboration.dto.ChatParticipantAdd;
import com.codenvy.ide.collaboration.dto.ChatParticipantRemove;
import com.codenvy.ide.collaboration.dto.ParticipantInfo;
import com.codenvy.ide.collaboration.dto.RoutingTypes;
import com.codenvy.ide.collaboration.dto.client.DtoClientImpls.ChatCodePointMessageImpl;
import com.codenvy.ide.collaboration.dto.client.DtoClientImpls.ChatMessageImpl;
import com.codenvy.ide.collaboration.dto.client.DtoClientImpls.UserDetailsImpl;
import com.codenvy.ide.commons.shared.ListenerManager;
import com.codenvy.ide.commons.shared.StringUtils;
import com.codenvy.ide.json.client.JsoStringMap;
import com.codenvy.ide.json.shared.JsonArray;
import com.codenvy.ide.json.shared.JsonCollections;
import com.codenvy.ide.json.shared.JsonStringMap;
import com.codenvy.ide.json.shared.JsonStringMap.IterationCallback;
import com.google.collide.client.CollabEditor;
import com.google.collide.client.CollabEditorExtension;
import com.google.collide.client.bootstrap.BootstrapSession;
import com.google.collide.client.code.ParticipantModel;
import com.google.collide.client.code.ParticipantModel.Listener;
import com.google.collide.client.collaboration.CollaborationManager.ParticipantsListener;
import com.google.collide.client.collaboration.DocumentCollaborationController;
import com.google.collide.dto.UserDetails;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Timer;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.websocket.MessageFilter;
import org.exoplatform.ide.client.framework.websocket.MessageFilter.MessageRecipient;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.editor.client.api.SelectionRange;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.Date;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ProjectChatPresenter implements ViewClosedHandler, ShowHideChatHandler, EditorActiveFileChangedHandler, SendCodePointHandler {

    public static final int                                   MESSAGE_DELIVER_TIMEOUT = 10000;
    private             ListenerManager<ProjectUsersListener> projectUsersListeners   = ListenerManager.create();
    private             EventListener                         enterListener           = new EventListener() {
        @Override
        public void handleEvent(Event event) {
            SignalEvent signalEvent = SignalEventUtils.create(event);
            if (signalEvent != null && signalEvent.getKeyCode() == KeyCode.ENTER) {
                if (signalEvent.getAltKey() || signalEvent.getCommandKey() ||
                    signalEvent.getCtrlKey() || signalEvent.getMetaKey() || signalEvent.getShiftKey()) {
                    return;
                }
                event.stopPropagation();
                event.preventDefault();
                sendMessage();
            }
        }
    };
    private             ParticipantsListener                  participantsListener    = new ParticipantsListener() {
        /**
         * {@inheritDoc}
         */
        @Override
        public void userOpenFile(final String path, com.google.collide.dto.UserDetails user) {
            if (isShow(path)) {
                display.addNotificationMessage(getName(user) + " opened {0} file.", getName(path), new MessageCallback() {

                    @Override
                    public void messageClicked() {
                        openFile(path);
                        if (viewClosed || !display.asView().isViewVisible()) {
                            control.startBlink();
                        }
                    }
                });
            }
        }

        private String getName(UserDetails user) {
            if (user.getDisplayName().contains("@")) {
                String name = user.getDisplayName();
                return name.substring(0, name.indexOf('@'));
            }
            return user.getDisplayName();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void userCloseFile(final String path, com.google.collide.dto.UserDetails user) {
            if (isShow(path)) {
                display.addNotificationMessage(getName(user) + " closed the " + getName(path) + " file.");
                if (viewClosed || !display.asView().isViewVisible()) {
                    control.startBlink();
                }
            }
        }

        private boolean isShow(String path) {
            if (project == null) {
                return true;
            }

            path = path.substring(1);
            path = path.substring(0, path.indexOf('/'));
            if (path.equals(project.getName())) {
                return true;
            }
            return false;
        }

        private String getName(String path) {
            path = path.substring(path.lastIndexOf('/') + 1);
            return path;
        }
    };
    private             Listener                              listener                = new Listener() {
        @Override
        public void participantAdded(com.google.collide.client.code.Participant participant) {
            display.addEditParticipant(participant.getId(), participant.getColor());
            if (viewClosed || !display.asView().isViewVisible()) {
                control.startBlink();
            }
        }

        @Override
        public void participantRemoved(com.google.collide.client.code.Participant participant) {
            display.removeEditParticipant(participant.getId());
        }
    };
    private ChatApi               chatApi;
    private IDE                   ide;
    private ShowChatControl       control;
    private CollabEditorExtension collabExtension;
    private Display               display;
    private JsonStringMap<Participant>   users         = JsonCollections.createMap();
    private JsonStringMap<MessagesTimer> deliverTimers = JsonCollections.createMap();
    private SendCodePointerControl pointerControl;
    private String                 userId;
    private boolean viewClosed = true;
    private ParticipantModel participantModel;
    private ProjectModel     project;
    private CollabEditor     editor;
    private String           clientId;
    private FileModel        file;

    public ProjectChatPresenter(ChatApi chatApi, MessageFilter messageFilter, IDE ide, ShowChatControl chatControl,
                                SendCodePointerControl pointerControl, final String userId, CollabEditorExtension collabExtension) {
        this.chatApi = chatApi;
        this.ide = ide;
        this.pointerControl = pointerControl;
        this.userId = userId;
        control = chatControl;
        this.collabExtension = collabExtension;
        IDE.eventBus().addHandler(ViewClosedEvent.TYPE, this);
        IDE.eventBus().addHandler(ShowHideChatEvent.TYPE, this);
        IDE.eventBus().addHandler(SendCodePointEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        messageFilter.registerMessageRecipient(RoutingTypes.CHAT_MESSAGE, new MessageRecipient<ChatMessage>() {
            @Override
            public void onMessageReceived(ChatMessage message) {
                messageReceived(message);
            }
        });

        messageFilter.registerMessageRecipient(RoutingTypes.CHAT_PARTISIPANT_ADD,
                                               new MessageRecipient<ChatParticipantAdd>() {
                                                   @Override
                                                   public void onMessageReceived(ChatParticipantAdd message) {
                                                       addParticipant(message.participant());
                                                   }
                                               });

        messageFilter.registerMessageRecipient(RoutingTypes.CHAT_PARTISIPANT_REMOVE,
                                               new MessageRecipient<ChatParticipantRemove>() {
                                                   @Override
                                                   public void onMessageReceived(ChatParticipantRemove message) {
                                                       if (BootstrapSession.getBootstrapSession().getActiveClientId()
                                                                           .equals(message.clientId())) {
                                                           return;
                                                       }
                                                       removeParticipant(message.clientId());
                                                   }
                                               });
        messageFilter.registerMessageRecipient(RoutingTypes.CHAT_CODE_POINT, new MessageRecipient<ChatCodePointMessage>() {
            @Override
            public void onMessageReceived(ChatCodePointMessage message) {
                if (clientId.equals(message.getClientId())) {
                    return;
                }
                handleCodePoint(message);
            }
        });
    }

    public ListenerManager<ProjectUsersListener> getProjectUsersListeners() {
        return projectUsersListeners;
    }

    private String getName(String path) {
        path = path.substring(path.lastIndexOf('/') + 1);
        return path;
    }

    private void handleCodePoint(final ChatCodePointMessage message) {
        Participant participant = users.get(message.getClientId());
        StringBuilder name = new StringBuilder(getName(message.getPath()));
        if (message.getStartLine() == message.getEndLine()) {
            name.append(':').append(message.getStartLine());
        } else {
            name.append(" (").append(message.getStartLine()).append("..").append(message.getEndLine()).append(')');
        }
        display.addMessage(participant, name.toString(), Long.valueOf(message.getDateTime()), new MessageCallback() {
            @Override
            public void messageClicked() {
                if (file != null && file.getPath().equals(message.getPath())) {
                    editor.selectRange(message.getStartLine(), message.getStartChar(), message.getEndLine(),
                                       message.getEndChar());
                    editor.setFocus();
                } else {
                    openFile(message.getPath());
                    IDE.eventBus().addHandler(EditorActiveFileChangedEvent.TYPE, new EditorActiveFileChangedHandler() {
                        @Override
                        public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
                            if (event.getFile().getPath().equals(message.getPath())) {
                                IDE.eventBus().removeHandler(EditorActiveFileChangedEvent.TYPE, this);

                                event.getEditor().selectRange(message.getStartLine(), message.getStartChar(),
                                                              message.getEndLine(), message.getEndChar());
                                event.getEditor().setFocus();
                            }
                        }
                    });
                }
            }
        });
    }

    private void removeParticipant(String clientId) {
        Participant participant = users.remove(clientId);
        dispatchParticipantRemoved();
        if (display != null) {
            display.removeParticipant(participant);
            display.addNotificationMessage(participant.getDisplayName() + " left the " + project.getName() + " project.");
            if (users.size() == 1) {
                //                        control.setEnabled(false);
                //            ide.closeView(Display.ID);
            }
        }
    }

    private void dispatchParticipantRemoved() {
        projectUsersListeners.dispatch(new ListenerManager.Dispatcher<ProjectUsersListener>() {
            @Override
            public void dispatch(ProjectUsersListener listener) {
                listener.onUserCloseProject();
            }
        });
    }

    private void addParticipant(ParticipantInfo user) {
        if (user.getClientId().equals(BootstrapSession.getBootstrapSession().getActiveClientId())) {
            ((UserDetailsImpl)user.getUserDetails()).setIsCurrentUser(true);
            ((UserDetailsImpl)user.getUserDetails()).setDisplayName("me");
        }
        Participant participant = (Participant)user.getUserDetails();
        if (participant.getDisplayName().contains("@")) {
            String name = participant.getDisplayName();
            participant.setDisplayName(name.substring(0, name.indexOf('@')));
        }
        participant.setClientId(user.getClientId());
        users.put(user.getClientId(), participant);
        dispatchParticipantAdded();
        if (display != null && !participant.isCurrentUser()) {
            setParticipantColor(participant);
            display.addParticipant(participant);
            display.addNotificationMessage(
                    user.getUserDetails().getDisplayName() + " has joined the " + project.getName() + " project.");
            if (users.size() > 1) {
                control.setEnabled(true);
            }
            if (viewClosed || !display.asView().isViewVisible()) {
                control.startBlink();
            }
        }
    }

    private void dispatchParticipantAdded() {
        projectUsersListeners.dispatch(new ListenerManager.Dispatcher<ProjectUsersListener>() {
            @Override
            public void dispatch(ProjectUsersListener listener) {
                listener.onUserOpenProject();
            }
        });
    }

    private void setParticipantColor(Participant user) {
        com.google.collide.client.code.Participant participant = collabExtension.getUsersModel().getParticipant(
                user.getUserId());
        user.setColor(participant.getColor());
    }

    private void messageReceived(ChatMessage message) {
        if (message.getClientId().equals(BootstrapSession.getBootstrapSession().getActiveClientId())) {
            MessagesTimer timer = deliverTimers.remove(message.getDateTime());
            if (timer.executed) {
                display.messageDelivered(message.getDateTime());
            } else {
                timer.cancel();
            }
            return;
        }
        display.addMessage(users.get(message.getClientId()), message.getMessage(), Long.valueOf(message.getDateTime()));
        if (viewClosed || !display.asView().isViewVisible()) {
            control.startBlink();
        }
    }

    private void sendMessage() {

        String message = display.getChatMessage();
        if (message.isEmpty()) {
            return;
        }
        if (StringUtils.isNullOrWhitespace(message)) {
            return;
        }
        ChatMessageImpl chatMessage = ChatMessageImpl.make();
        chatMessage.setUserId(userId);
        chatMessage.setProjectId(project.getId());
        String clientId = BootstrapSession.getBootstrapSession().getActiveClientId();
        chatMessage.setClientId(clientId);
        Date d = new Date();
        chatMessage.setDateTime(String.valueOf(d.getTime()));
        MessagesTimer messagesTimer = new MessagesTimer(chatMessage.getDateTime());
        messagesTimer.schedule(MESSAGE_DELIVER_TIMEOUT);
        deliverTimers.put(chatMessage.getDateTime(), messagesTimer);
        SafeHtmlBuilder b = new SafeHtmlBuilder();
        b.appendEscapedLines(message);
        chatMessage.setMessage(b.toSafeHtml().asString());
        display.clearMessage();
        display.addMessage(users.get(clientId), chatMessage.getMessage(), d.getTime());
        try {
            chatApi.SEND_MESSAGE.send(chatMessage);
        } catch (WebSocketException e) {
            Log.debug(ProjectChatPresenter.class, e);
        }

    }

    /** {@inheritDoc} */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView().getId().equals(Display.ID)) {
            control.chatOpened(false);
            viewClosed = true;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onShowHideChat(ShowHideChatEvent event) {
        if (event.isShow()) {
            if (display != null) {
                ide.openView(display.asView());
                viewClosed = false;
                control.chatOpened(true);
                control.stopBlink();
            }
        } else {
            ide.closeView(Display.ID);
        }
    }

    void setChatParticipants(JsonArray<ParticipantInfo> chatParticipants) {
        users = JsonCollections.createMap();
        for (ParticipantInfo info : chatParticipants.asIterable()) {
            addParticipant(info);
        }
    }

    void setProjectId(ProjectModel project) {
        collabExtension.getCollaborationManager().getParticipantsListenerManager().add(participantsListener);
        this.project = project;
        clientId = BootstrapSession.getBootstrapSession().getActiveClientId();
        display = GWT.create(Display.class);
        display.addListener(enterListener);
    }

    void projectClosed() {
        ide.closeView(Display.ID);
        display = null;
    }

    /** {@inheritDoc} */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        if (event.getEditor() != null && event.getEditor() instanceof CollabEditor) {
            if (event.getFile().getMimeType().equals(MimeType.TEXT_HTML)) {
                display.clearEditParticipants();
                return;
            }
            editor = (CollabEditor)event.getEditor();
            DocumentCollaborationController controller = collabExtension.getCollaborationManager().getDocumentCollaborationController(
                    editor.getEditor().getDocument().getId());

            if (participantModel != null) {
                participantModel.removeListener(listener);
            }
            participantModel = controller.getParticipantModel();
            participantModel.addListener(listener);
            JsoStringMap<com.google.collide.client.code.Participant> participants = participantModel.getParticipants();
            participants.iterate(new IterationCallback<com.google.collide.client.code.Participant>() {
                @Override
                public void onIteration(String key, com.google.collide.client.code.Participant value) {
                    display.addEditParticipant(key, value.getColor());
                }
            });
            file = event.getFile();
            pointerControl.setVisible(true);
            pointerControl.setEnabled(true);
        } else {
            display.clearEditParticipants();
            pointerControl.setVisible(false);
            pointerControl.setEnabled(false);
            editor = null;
            file = null;
        }
    }

    private void openFile(String path) {
        try {
            VirtualFileSystem.getInstance().getItemByPath(path,
                                                          new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper())) {
                                                              @Override
                                                              protected void onSuccess(ItemWrapper result) {
                                                                  if (result.getItem() != null && result.getItem() instanceof FileModel) {
                                                                      FileModel file = (FileModel)result.getItem();
                                                                      file.setProject(project);
                                                                      ide.eventBus().fireEvent(new OpenFileEvent(file));
                                                                  }
                                                              }

                                                              @Override
                                                              protected void onFailure(Throwable exception) {
                                                                  Log.error(AsyncRequestCallback.class, exception);
                                                              }
                                                          });
        } catch (RequestException e) {
            Log.error(AsyncRequestCallback.class, e);
        }
    }

    @Override
    public void onSendCodePoint(SendCodePointEvent event) {
        if (editor == null) {
            return;
        }

        SelectionRange selectionRange = editor.getSelectionRange();
        ChatCodePointMessageImpl message = ChatCodePointMessageImpl.make();
        message.setClientId(clientId);
        message.setUserId(userId);
        Date d = new Date();
        message.setDateTime(String.valueOf(d.getTime()));
        message.setProjectId(project.getId());
        message.setPath(file.getPath());
        message.setStartLine(selectionRange.getStartLine());
        message.setStartChar(selectionRange.getStartSymbol());
        message.setEndLine(selectionRange.getEndLine());
        message.setEndChar(selectionRange.getEndSymbol());
        handleCodePoint(message);
        try {
            chatApi.SEND_MESSAGE.send(message);
        } catch (WebSocketException e) {
            Log.debug(ProjectChatPresenter.class, e);
        }
    }

    public JsonStringMap<Participant> getParticipants() {
        return users;
    }

    public interface Display extends IsView {
        String ID = "codenvyIdeChat";

        String getChatMessage();

        void clearMessage();

        void addMessage(Participant participant, String message, long time);

        void addMessage(Participant participant, String message, long time, MessageCallback callback);

        void addListener(EventListener eventListener);

        void messageNotDelivered(String messageId);

        void messageDelivered(String messageId);

        void removeParticipant(Participant participant);

        void addParticipant(Participant participant);

        void removeEditParticipant(String userId);

        void addEditParticipant(String userId, String color);

        void clearEditParticipants();

        void addNotificationMessage(String message);

        void addNotificationMessage(String message, String link, MessageCallback callback);
    }


    public interface MessageCallback {
        void messageClicked();
    }

    private class MessagesTimer extends Timer {

        public boolean executed = false;
        private String messageId;

        private MessagesTimer(String messageId) {
            this.messageId = messageId;
        }

        @Override
        public void run() {
            executed = true;
            if (display != null) {
                display.messageNotDelivered(messageId);
            }
        }
    }

}
