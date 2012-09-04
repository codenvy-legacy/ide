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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.invite.GoogleContact;
import org.exoplatform.ide.client.framework.invite.GoogleContactsServiceImpl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for {@link InviteGoogleContactsView}.
 * The view must implement {@link InviteGoogleContactsPresenter.Display} interface and pointed in Views.gwt.xml file.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: InviteGoogleContactsPresenter.java Aug 23, 2012 12:12:19 PM azatsarynnyy $
 *
 */
public class InviteGoogleContactsPresenter implements InviteGoogleContactsHandler, ViewClosedHandler
{

   public interface Display extends IsView
   {
      /**
       * Get invite button handler.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getInviteButton();

      /**
       * Get close button handler.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCloseButton();

      /**
       * Get contacts list for invite.
       * 
       * @return {@link List} of the user's contacts
       */
      List<GoogleContact> getContactsForInvite();

      /**
       * Set contacts list value.
       * 
       * @param contacts {@link List} of the user's contacts
       */
      void setContacts(List<GoogleContact> contacts);

      /**
       * Change the enable state of the invite button.
       * 
       * @param isEnable enabled or not
       */
      void setInviteButtonEnable(boolean isEnable);
   }

   /**
    * The display.
    */
   private Display display;

   /**
    * User's Google Contacts to invite.
    */
   private List<GoogleContact> contacts;

   public InviteGoogleContactsPresenter()
   {
      IDE.getInstance().addControl(new InviteGoogleContactsControl());

      IDE.addHandler(InviteGoogleContactsEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * Bind display (view) with presenter.
    */
   public void bindDisplay()
   {
      display.getInviteButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            invite();
         }
      });

      display.getCloseButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.googlecontacts.InviteGoogleContactsHandler#onShowGoogleContacts(org.exoplatform.ide.client.googlecontacts.InviteGoogleContactsEvent)
    */
   @Override
   public void onShowGoogleContacts(InviteGoogleContactsEvent event)
   {
      try
      {
         GoogleContactsServiceImpl.getInstance().getContacts(
            new AsyncRequestCallback<List<GoogleContact>>(
               new InviteGoogleContactsUnmarshaller(new ArrayList<GoogleContact>()))
            {
               @Override
               protected void onSuccess(List<GoogleContact> result)
               {
                  contacts = result;
                  initDisplay();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Initialize and open display.
    */
   private void initDisplay()
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();

         IDE.getInstance().openView(display.asView());

         display.setContacts(contacts);
         display.setInviteButtonEnable(false);
      }
   }

   /**
    * Invite Google Contacts.
    */
   private void invite()
   {
      StringBuffer contactsString = new StringBuffer("<b>Invited contacts:</b><br>");
      for (GoogleContact contact : display.getContactsForInvite())
      {
         contactsString.append(contact.getName()).append("<br>").append(contact.getEmailAddresses()).append("<br>");
      }
      IDE.fireEvent(new OutputEvent(contactsString.toString()));
      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }
}
