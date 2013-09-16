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
package com.codenvy.ide.collaboration.chat.client;

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.AnchorElement;
import elemental.html.DivElement;
import elemental.html.Element;
import elemental.html.SpanElement;
import elemental.html.TextAreaElement;

import com.codenvy.ide.client.util.Elements;
import com.codenvy.ide.collaboration.chat.client.ChatResources.ChatCss;
import com.codenvy.ide.collaboration.chat.client.ParticipantList.View;
import com.codenvy.ide.collaboration.chat.client.ProjectChatPresenter.Display;
import com.codenvy.ide.collaboration.chat.client.ProjectChatPresenter.MessageCallback;
import com.codenvy.ide.json.shared.JsonCollections;
import com.codenvy.ide.json.shared.JsonStringMap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Node;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

import java.util.Date;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ProjectChatView extends ViewImpl implements Display {

    private com.google.gwt.user.client.Element disabledMessage;

    interface ProjectChatViewUiBinder extends UiBinder<Widget, ProjectChatView> {
    }

    private static ProjectChatViewUiBinder ourUiBinder = GWT.create(ProjectChatViewUiBinder.class);

    private DateTimeFormat timeFormat = DateTimeFormat.getFormat(PredefinedFormat.TIME_MEDIUM);

    @UiField
    ScrollPanel chatPanel;

    @UiField
    com.google.gwt.dom.client.TextAreaElement chatMessageInput;

    @UiField
    ScrollPanel participantsPanel;

    private String lastClientId;

    private Element lastMessageElement;

    private ChatCss css;

    private JsonStringMap<Element> currentUserMessages = JsonCollections.createMap();

    private final ParticipantList participantList;

    public ProjectChatView() {
        super(ID, ViewType.INFORMATION, "Collaboration", new Image(ChatExtension.resources.collaborators()));
        add(ourUiBinder.createAndBindUi(this));
        css = ChatExtension.resources.chatCss();
        View view = new View();
        participantList = ParticipantList.create(view);
        participantsPanel.getElement().appendChild((Node)view.getElement());
        participantsPanel.getElement().setId("ideParticipants");
        chatPanel.getElement().setId("ideChatMessages");
        chatMessageInput.setId("ideChatInput");
    }

    /** {@inheritDoc} */
    @Override
    public String getChatMessage() {
        return chatMessageInput.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void addMessage(Participant participant, String message, long time) {
        addMessage(participant, message, time, null);
    }

    @Override
    public void addMessage(Participant participant, String message, long time, MessageCallback callback) {
        Date d = new Date(time);
        DivElement messageElement;
        if (participant.getClientId().equals(lastClientId)) {
            messageElement = getMessageElement(message, "...", d, callback);
            lastMessageElement.appendChild(messageElement);
        } else {
            messageElement = getMessageElement(message, participant.getDisplayName() + ":", d, callback);
            chatPanel.getElement().appendChild((Node)messageElement);
            lastClientId = participant.getClientId();
            lastMessageElement = messageElement;
        }
        if (participant.isCurrentUser()) {
            currentUserMessages.put(String.valueOf(d.getTime()), messageElement);
        }
        chatPanel.scrollToBottom();
    }

    private DivElement getMessageElement(String message, String name, Date d, MessageCallback callback) {
        DivElement messageElement = Elements.createDivElement();
        DivElement timeElement = getTimeElement(d);
        messageElement.appendChild(timeElement);

        SpanElement nameElement = getNameElement(name);
        messageElement.appendChild(nameElement);
        if (callback != null) {
            AnchorElement anchorElement = createAnchorElement(message, callback);
            messageElement.appendChild(anchorElement);
        } else {
            DivElement messageDiv = Elements.createDivElement(css.chatMessage());
            messageDiv.setInnerHTML(message);
            messageElement.appendChild(messageDiv);
        }
        return messageElement;
    }

    private SpanElement getNameElement(String name) {
        SpanElement nameElement = Elements.createSpanElement(css.chatName());
        nameElement.setInnerHTML(name + "&nbsp;");
        return nameElement;
    }

    private DivElement getTimeElement(Date d) {
        DivElement timeElement = Elements.createDivElement(css.chatTime());
        timeElement.setInnerHTML("[" + timeFormat.format(d) + "]&nbsp;");
        return timeElement;
    }

    /** {@inheritDoc} */
    @Override
    public void addListener(EventListener eventListener) {
        ((TextAreaElement)chatMessageInput).setOnKeyPress(eventListener);
        ((TextAreaElement)chatMessageInput).setOnKeyDown(eventListener);
    }

    /** {@inheritDoc} */
    @Override
    public void messageNotDelivered(String messageId) {
        if (currentUserMessages.containsKey(messageId)) {
            Element messageElement = currentUserMessages.get(messageId);

            DivElement divElement = Elements.createDivElement(css.messageNotDelivered());
            divElement.setTitle("This message is not delivered yet.");
            messageElement.getFirstChildElement().getNextSiblingElement().appendChild(divElement);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void messageDelivered(String messageId) {
        if (currentUserMessages.containsKey(messageId)) {
            Element messageElement = currentUserMessages.get(messageId);

            messageElement.getFirstChildElement().getNextSiblingElement().getFirstChildElement().removeFromParent();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeParticipant(Participant participant) {
        participantList.participantRemoved(participant);
    }

    /** {@inheritDoc} */
    @Override
    public void addParticipant(Participant participant) {
        participantList.participantAdded(participant);
    }

    /** {@inheritDoc} */
    @Override
    public void removeEditParticipant(String clientId) {
        participantList.removeEditParticipant(clientId);
    }

    /** {@inheritDoc} */
    @Override
    public void addEditParticipant(String clientId, String color) {
        participantList.setEditParticipant(clientId, color);
    }

    /** {@inheritDoc} */
    @Override
    public void clearEditParticipants() {
        participantList.clearEditParticipants();
    }

    @Override
    public void addNotificationMessage(String message) {
        DivElement messageElement = Elements.createDivElement(css.chatNotification());
        messageElement.appendChild(Elements.createTextNode(message));
        chatPanel.getElement().appendChild((Node)messageElement);
        lastClientId = "";
        chatPanel.scrollToBottom();
    }

    @Override
    public void addNotificationMessage(String message, String link, MessageCallback callback) {
        String[] split = message.split("\\{0\\}");
        DivElement messageElement = Elements.createDivElement(css.chatNotification());
        messageElement.appendChild(Elements.createTextNode(split[0]));
        messageElement.appendChild(createAnchorElement(link, callback));
        if (split.length > 1) {
            messageElement.appendChild(Elements.createTextNode(split[1]));
        }
        lastClientId = "";
        chatPanel.getElement().appendChild((Node)messageElement);
        chatPanel.scrollToBottom();
    }

    /** {@inheritDoc} */
    @Override
    public void showChatDisabled() {
        disabledMessage = DOM.createDiv();
        disabledMessage.setClassName(css.chatDisabled());
        com.google.gwt.user.client.Element span = DOM.createSpan();
        span.setInnerText("Collaboration mode has been disabled for this project");
        span.setClassName(css.chatDissabledMessage());
        disabledMessage.appendChild(span);
        getElement().appendChild(disabledMessage);
    }

    /** {@inheritDoc} */
    @Override
    public void removeDisabledMessage() {
        if(disabledMessage != null){
            disabledMessage.removeFromParent();
        }
    }

    private AnchorElement createAnchorElement(final String message, final MessageCallback callback) {
        AnchorElement anchorElement = Elements.createAnchorElement(css.link());
        anchorElement.setHref("javascript:;");
        anchorElement.setTextContent(message);
        if (callback != null) {
            anchorElement.addEventListener(Event.CLICK, new EventListener() {
                @Override
                public void handleEvent(Event event) {
                    callback.messageClicked();
                }
            }, false);
        }
        return anchorElement;
    }

    /** {@inheritDoc} */
    @Override
    public void clearMessage() {
        chatMessageInput.setValue("");
    }
}