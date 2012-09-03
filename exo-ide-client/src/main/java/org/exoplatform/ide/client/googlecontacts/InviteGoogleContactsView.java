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
package org.exoplatform.ide.client.googlecontacts;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.invite.GoogleContact;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

import java.util.ArrayList;
import java.util.List;

/**
 * View for inviting Google Contacts.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: InviteGoogleContactsView.java Aug 23, 2012 12:18:26 PM azatsarynnyy $
 *
 */
public class InviteGoogleContactsView extends ViewImpl implements InviteGoogleContactsPresenter.Display
{
   public static final int HEIGHT = 550;

   public static final int WIDTH = 1350;

   public static final String ID = "ideInviteGoogleContactsView";

   private static final String TITLE = IDE.PREFERENCES_CONSTANT.inviteGoogleContactsTitle();

   private static final String INVITE_BUTTON_ID = "ideInviteGoogleContactsViewInviteButton";

   private static final String CLSOE_BUTTON_ID = "ideInviteGoogleContactsViewCloseButton";

   private List<GoogleContact> contactsToInvite = new ArrayList<GoogleContact>();

   @UiField
   Grid contactsGrid;

   @UiField
   ImageButton inviteButton;

   @UiField
   ImageButton closeButton;

   interface InviteGoogleContactsViewUiBinder extends UiBinder<Widget, InviteGoogleContactsView>
   {
   }

   private static InviteGoogleContactsViewUiBinder uiBinder = GWT.create(InviteGoogleContactsViewUiBinder.class);

   public InviteGoogleContactsView()
   {
      super(ID, ViewType.MODAL, TITLE, null, WIDTH, HEIGHT);
      setCloseOnEscape(true);
      add(uiBinder.createAndBindUi(this));

      inviteButton.setButtonId(INVITE_BUTTON_ID);
      closeButton.setButtonId(CLSOE_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.client.googlecontacts.InviteGoogleContactsPresenter.Display#getInviteButton()
    */
   @Override
   public HasClickHandlers getInviteButton()
   {
      return inviteButton;
   }

   /**
    * @see org.exoplatform.ide.client.googlecontacts.InviteGoogleContactsPresenter.Display#getCloseButton()
    */
   @Override
   public HasClickHandlers getCloseButton()
   {
      return closeButton;
   }

   /**
    * @see org.exoplatform.ide.client.googlecontacts.InviteGoogleContactsPresenter.Display#getContactsForInvite()
    */
   @Override
   public List<GoogleContact> getContactsForInvite()
   {
      return contactsToInvite;
   }

   /**
    * @see org.exoplatform.ide.client.googlecontacts.InviteGoogleContactsPresenter.Display#setContacts(java.util.List)
    */
   @Override
   public void setContacts(List<GoogleContact> contacts)
   {
      contactsGrid.setSize("100%", "100%");

      int columnCount = 4;
      int rowCount = (int)Math.ceil((double)contacts.size() / columnCount);
      contactsGrid.resize(rowCount, columnCount);

      int contactNum = 0;
      for (int rowNum = 0; rowNum < rowCount; rowNum++)
      {
         if (contactNum == contacts.size())
         {
            break;
         }

         for (int colNum = 0; colNum < columnCount; colNum++)
         {
            if (contactNum == contacts.size())
            {
               break;
            }

            DockPanel dock = new DockPanel();
            dock.setSpacing(4);
            dock.setHorizontalAlignment(DockPanel.ALIGN_LEFT);
            final GoogleContact googleContact = contacts.get(contactNum++);

            String photo = googleContact.getPhotoBase64();
            if (photo != null)
            {
               String photoTag = "<IMG src='data:image/jpg;base64," + photo + "' />'";
               dock.add(new HTML(photoTag), DockPanel.WEST);
            }
            else
            {
               Image noPhotoImage = new Image(IDEImageBundle.INSTANCE.noPhoto());
               dock.add(noPhotoImage, DockPanel.WEST);
            }

            dock.add(new HTML(googleContact.getName()), DockPanel.NORTH);
            dock.add(new HTML(googleContact.getEmailAddresses().toString()), DockPanel.NORTH);

            CheckBox inviteCheckBox = new CheckBox("Invite");
            inviteCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>()
            {
               @Override
               public void onValueChange(ValueChangeEvent<Boolean> event)
               {
                  if (event.getValue())
                  {
                     contactsToInvite.add(googleContact);
                  }
                  else
                  {
                     contactsToInvite.remove(googleContact);
                  }
                  setInviteButtonEnable(!contactsToInvite.isEmpty());
               }
            });
            dock.add(inviteCheckBox, DockPanel.SOUTH);
            contactsGrid.setWidget(rowNum, colNum, dock);
         }
      }
   }

   /**
    * @see org.exoplatform.ide.client.googlecontacts.InviteGoogleContactsPresenter.Display#setInviteButtonEnable(boolean)
    */
   @Override
   public void setInviteButtonEnable(boolean isEnable)
   {
      inviteButton.setEnabled(isEnable);
   }
}
