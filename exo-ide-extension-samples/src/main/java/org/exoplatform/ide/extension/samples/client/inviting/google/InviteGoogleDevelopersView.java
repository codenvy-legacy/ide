/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.samples.client.inviting.google;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.invite.GoogleContact;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class InviteGoogleDevelopersView extends ViewImpl implements
org.exoplatform.ide.extension.samples.client.inviting.google.InviteGoogleDevelopersPresenter.Display
{

   private static final String ID = "ide.inviteGitHubDevelopersView";

   private static final String TITLE = "Invite developers";

   private static final int WIDTH = 800;

   private static final int HEIGHT = 550;

   private static InviteGoogleDevelopersViewUiBinder uiBinder = GWT.create(InviteGoogleDevelopersViewUiBinder.class);

   interface InviteGoogleDevelopersViewUiBinder extends UiBinder<Widget, InviteGoogleDevelopersView>
   {
   }

   private class UserListWidget extends Widget
   {
      public UserListWidget(Element e)
      {
         setElement(e);
      }
      
      /**
       * Adds a new child widget
       * 
       * @param w the widget to be added
       */
      public void add(Widget w) {
        add(w, getElement());
      }

      /**
       * Adds a new child widget to the panel, attaching its Element to the
       * specified container Element.
       * 
       * @param child the child widget to be added
       * @param container the element within which the child will be contained
       */
      protected void add(Widget child, Element container) {
        // Detach new child.
        child.removeFromParent();

        // Logical attach.
        getChildren().add(child);

        // Physical attach.
        DOM.appendChild(container, child.getElement());

        // Adopt.
        adopt(child);
      }
      
   }
   
   UserListWidget userListWidget;

   @UiField
   DivElement userListElement;
   
   @UiField
   CheckBox checkAll;
   
   @UiField
   ImageButton inviteButton, cancelButton;
   
   @UiField
   TextAreaElement inviteMessage;
   
   @UiField
   TextInput emailsTextField;
   
   public InviteGoogleDevelopersView()
   {
      super(ID, "modal", TITLE, new Image(SamplesClientBundle.INSTANCE.welcome()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
      setCloseOnEscape(true);
   }
   
   private Map<GoogleContact, UserCard> cards = new HashMap<GoogleContact, UserCard>();
   
   @Override
   public void setDevelopers(List<GoogleContact> contacts, UserSelectionChangedHandler selectionChangedHandler)
   {
      if (userListWidget != null)
      {
         userListWidget.removeFromParent();
      }
      userListWidget = new UserListWidget((Element)userListElement.cast());
      
      cards.clear();
      for (GoogleContact contact : contacts)
      {
         UserCard card = new UserCard(contact);
         card.setSelectionChangedHandler(selectionChangedHandler);
         userListWidget.add(card);
         cards.put(contact, card);
      }
   }

   @Override
   public boolean isSelected(GoogleContact user)
   {
      return cards.get(user).isSelected();
   }

   @Override
   public void setSelected(GoogleContact contact, boolean selected)
   {
      cards.get(contact).setSelected(selected);
   }

   @Override
   public HasValue<Boolean> getSelectAllCheckBox()
   {
      return checkAll;
   }

   @Override
   public HasClickHandlers getInviteButton()
   {
      return inviteButton;
   }

   @Override
   public HasClickHandlers getCloseButton()
   {
      return cancelButton;
   }

   @Override
   public String getInviteMessge()
   {
      return inviteMessage.getValue();
   }

   @Override
   public void setInviteButtonEnabled(boolean enabled)
   {
      inviteButton.setEnabled(enabled);
   }

   @Override
   public void setInviteButtonTitle(String title)
   {
      inviteButton.setText(title);
   }

   @Override
   public HasValue<String> getEmailsTextField()
   {
      return emailsTextField;
   }

}
