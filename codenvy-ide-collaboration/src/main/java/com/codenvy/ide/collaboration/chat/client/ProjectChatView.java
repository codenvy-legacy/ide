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

import com.codenvy.ide.client.util.AnimationController;
import com.codenvy.ide.client.util.AnimationController.AnimationStateListener;
import com.codenvy.ide.client.util.AnimationController.State;
import com.codenvy.ide.client.util.Elements;
import com.codenvy.ide.collaboration.chat.client.ChatResources.ChatCss;
import com.codenvy.ide.collaboration.chat.client.ProjectChatPresenter.Display;
import com.codenvy.ide.collaboration.dto.UserDetails;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.dom.client.Node;
import elemental.events.EventListener;
import elemental.html.DivElement;
import elemental.html.Element;
import elemental.html.ImageElement;
import elemental.html.SpanElement;
import elemental.html.TableCellElement;
import elemental.html.TableElement;
import elemental.html.TableRowElement;
import elemental.html.TextAreaElement;

import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.json.client.JsoArray;
import org.exoplatform.ide.json.shared.JsonArray;
import org.exoplatform.ide.json.shared.JsonCollections;
import org.exoplatform.ide.json.shared.JsonStringMap;
import org.exoplatform.ide.json.shared.JsonStringMap.IterationCallback;

import java.util.Date;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ProjectChatView extends ViewImpl implements Display
{
   interface ProjectChatViewUiBinder extends UiBinder<SplitLayoutPanel, ProjectChatView>
   {
   }

   private static ProjectChatViewUiBinder ourUiBinder = GWT.create(ProjectChatViewUiBinder.class);

   private DateTimeFormat timeFormat = DateTimeFormat.getFormat(PredefinedFormat.TIME_MEDIUM);

   @UiField
   ScrollPanel chatPanel;

   @UiField
   com.google.gwt.dom.client.TextAreaElement chatMessageInput;

   @UiField
   ScrollPanel participantsPanel;

   private String lastUserId;

   private Element lastMessageElement;

   private ChatCss css;

   private JsonStringMap<Element> currentUserMessages = JsonCollections.createMap();

   private JsonStringMap<Element> participants = JsonCollections.createMap();

   private JsonStringMap<Element> editParticipants = JsonCollections.createMap();

   AnimationController animationController = AnimationController.FADE_ANIMATION_CONTROLLER;

   private com.google.gwt.user.client.Element editHeader;

   private com.google.gwt.user.client.Element editFooter;

   public ProjectChatView()
   {
      super(ID, ViewType.OPERATION, "Project Chat", new Image(ChatExtension.resources.chat()));
      add(ourUiBinder.createAndBindUi(this));
      css = ChatExtension.resources.chatCss();
      editHeader = createEditHeader("Current file Collaborators", 145);
      participantsPanel.getElement().appendChild((Node)editHeader);
      editFooter = createEditHeader("Current project Collaborators", 167);
      participantsPanel.getElement().appendChild(editFooter);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getChatMessage()
   {
      return chatMessageInput.getValue();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addMessage(UserDetails userDetails, String message, long time)
   {
      Date d = new Date(time);
      DivElement messageElement;
      if (userDetails.getUserId().equals(lastUserId))
      {
         messageElement = getMessageElement(message, "...", d, userDetails.isCurrentUser());
         lastMessageElement.appendChild(messageElement);
      }
      else
      {
         messageElement = getMessageElement(message, userDetails.getDisplayName() + ":", d,
            userDetails.isCurrentUser());
         chatPanel.getElement().appendChild((Node)messageElement);
         lastUserId = userDetails.getUserId();
         lastMessageElement = messageElement;
      }
      if (userDetails.isCurrentUser())
      {
         currentUserMessages.put(String.valueOf(d.getTime()), messageElement);
      }
      chatPanel.scrollToBottom();
   }

   private DivElement getMessageElement(String message, String name, Date d, boolean isCurrentUser)
   {
      DivElement messageElement = Elements.createDivElement();
      DivElement timeElement = Elements.createDivElement(css.chatTime());
      timeElement.setInnerHTML("[" + timeFormat.format(d) + "]");
      messageElement.appendChild(timeElement);

      SpanElement nameElement = Elements.createSpanElement(isCurrentUser ? css.chatCurrentName() : css.chatName());
      nameElement.setInnerHTML(name);
      messageElement.appendChild(nameElement);

      DivElement messageDiv = Elements.createDivElement(css.chatMessage());
      messageDiv.setInnerHTML("&nbsp;" + message);
      messageElement.appendChild(messageDiv);
      return messageElement;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addListener(EventListener eventListener)
   {
      ((TextAreaElement)chatMessageInput).setOnKeyPress(eventListener);
      ((TextAreaElement)chatMessageInput).setOnKeyDown(eventListener);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void messageNotDelivered(String messageId)
   {
      if (currentUserMessages.containsKey(messageId))
      {
         Element messageElement = currentUserMessages.get(messageId);

         DivElement divElement = Elements.createDivElement(css.messageNotDelivered());
         divElement.setTitle("This message is not delivered yet.");
         messageElement.getFirstChildElement().getNextSiblingElement().appendChild(divElement);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void messageDelivered(String messageId)
   {
      if (currentUserMessages.containsKey(messageId))
      {
         Element messageElement = currentUserMessages.get(messageId);

         messageElement.getFirstChildElement().getNextSiblingElement().getFirstChildElement().removeFromParent();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeParticipant(String userId)
   {
      if (participants.containsKey(userId))
      {
         Element element = participants.remove(userId);
         element.removeFromParent();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addParticipant(Participant participant)
   {
      Element element = getParticipantElement(participant);
      participants.put(participant.getUserId(), element);
      participantsPanel.getElement().insertAfter((Node)element, editFooter);
      animationController.show(element);
   }

   private com.google.gwt.user.client.Element createEditHeader(String message, int width)
   {
      DivElement divElement = Elements.createDivElement(css.chatHeader());
      TableElement element = Elements.createTableElement();
      element.setCellPadding("0");
      element.setCellSpacing("0");
      element.setWidth("100%");
      element.setBorder("0");
      TableRowElement trElement = Elements.createTRElement();
      element.appendChild(trElement);

      TableCellElement tdElementLeft = Elements.createTDElement();
      tdElementLeft.appendChild(Elements.createHrElement());
      trElement.appendChild(tdElementLeft);

      TableCellElement tdElementMiddle = Elements.createTDElement(css.chatHeaderText());
      tdElementMiddle.setWidth(width + "px");
      tdElementMiddle.setInnerHTML(message);
      trElement.appendChild(tdElementMiddle);

      TableCellElement tdElementRight = Elements.createTDElement();
      tdElementRight.appendChild(Elements.createHrElement());
      trElement.appendChild(tdElementRight);
      divElement.appendChild(element);
      return (com.google.gwt.user.client.Element)divElement;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeEditParticipant(String userId)
   {
      if (editParticipants.containsKey(userId))
      {
         Element element = editParticipants.remove(userId);
         element.removeFromParent();
         participantsPanel.getElement().appendChild((com.google.gwt.user.client.Element)element);
         animationController.show(element);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addEditParticipant(String userId)
   {
      if (participants.containsKey(userId))
      {
         Element element = participants.get(userId);
         element.removeFromParent();
         participantsPanel.getElement().insertBefore((Node)element, editFooter);
         animationController.show(element);
         editParticipants.put(userId, element);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void clearEditParticipants()
   {
      JsonArray<String> keys = editParticipants.getKeys();
      for (String key : keys.asIterable())
      {
         removeEditParticipant(key);
      }
   }

   private Element getParticipantElement(Participant userDetails)
   {
      DivElement element = Elements.createDivElement(css.chatParticipant());

      ImageElement imageElement = Elements.createImageElement(css.chatParticipantImage());
      imageElement.setSrc(userDetails.getPortraitUrl());
      element.appendChild(imageElement);

      DivElement nameElement = Elements.createDivElement(css.chatParticipantName());
      nameElement.setInnerHTML(userDetails.getDisplayName());
      nameElement.getStyle().setBackgroundColor(userDetails.getColor());
      element.appendChild(nameElement);

      DivElement emailElement = Elements.createDivElement(css.chatParticipantEmail());
      emailElement.setInnerHTML(userDetails.getDisplayEmail());
      element.appendChild(emailElement);
      return element;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void clearMessage()
   {
      chatMessageInput.setValue("");
   }
}