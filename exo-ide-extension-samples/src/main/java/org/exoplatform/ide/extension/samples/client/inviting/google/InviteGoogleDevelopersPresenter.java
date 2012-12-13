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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.invite.GoogleContact;
import org.exoplatform.ide.client.framework.invite.GoogleContactsServiceImpl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.samples.client.inviting.InviteClientService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class InviteGoogleDevelopersPresenter implements InviteGoogleDevelopersHandler, ViewClosedHandler, GoogleContactSelectionChangedHandler
{
   
   public interface Display extends IsView
   {
      
      void setDevelopers(List<GoogleContact> developers, GoogleContactSelectionChangedHandler selectionChangedHandler);

      boolean isSelected(GoogleContact contact);

      void setSelected(GoogleContact contact, boolean selected);

      HasValue<Boolean> getSelectAllCheckBox();

      String getInviteMessge();

      HasClickHandlers getInviteButton();

      HasClickHandlers getCloseButton();
      
      void setInviteButtonEnabled(boolean enabled);
      
      void setInviteButtonTitle(String title);
      
      HasValue<String> getEmailsTextField();
      
   }

   private InviteClientService inviteClientService;
   
   private Display display;
   
   private List<GoogleContact> contacts;
   
   private List<String> customEmailsList = new ArrayList<String>();
   
   private List<String> selectedEmailsList = new ArrayList<String>();
   
   private List<String> emailsToInvite = new ArrayList<String>();
   
   private int invitations = 0;
   
   public InviteGoogleDevelopersPresenter(InviteClientService inviteClientService)
   {
      this.inviteClientService = inviteClientService;
      
      IDE.getInstance().addControl(new InviteGoogleDevelopersControl());
      
      IDE.addHandler(InviteGoogleDevelopersEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   @Override
   public void onInviteGoogleDevelopers(InviteGoogleDevelopersEvent event)
   {
      if (InviteClientService.DEBUG_MODE)
      {
         loadStaticGoogleContacts();         
      }
      else
      {
         loadGoogleContacts();         
      }
   }
   
   /**
    * For testing only
    * 
    * @param url
    */
   private void loadStaticGoogleContacts()
   {
      IDELoader.show("Loading contacts...");
      
      new Timer()
      {
         @Override
         public void run()
         {
            try
            {
               String url = "/IDE/google-contacts.json";
               AsyncRequest.build(RequestBuilder.GET, URL.encode(url)).loader(IDELoader.get())
               .send(new AsyncRequestCallback<List<GoogleContact>>(
                        new InviteGoogleContactsUnmarshaller(new ArrayList<GoogleContact>()))
               {
                  @Override
                  protected void onSuccess(List<GoogleContact> result)
                  {
                     IDELoader.hide();
                     googleContactsReceived(result);
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     IDELoader.hide();
                     IDE.fireEvent(new ExceptionThrownEvent(exception));
                     exception.printStackTrace();
                  }
               });         
            }
            catch (RequestException e)
            {
               IDELoader.hide();
               IDE.fireEvent(new ExceptionThrownEvent(e));
               e.printStackTrace();
            }         
         }
      }.schedule(500);
   }   
   
   private void loadGoogleContacts()
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
                  googleContactsReceived(result);
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
      
   private void googleContactsReceived(List<GoogleContact> contacts)
   {
      if (display != null)
      {
         return;
      }
      
      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();
      
      this.contacts = new ArrayList<GoogleContact>();
      for (GoogleContact contact : contacts)
      {
         if (contact.getEmailAddresses() != null && !contact.getEmailAddresses().isEmpty())
         {
            this.contacts.add(contact);
         }
      }
      
      display.setDevelopers(contacts, this);
   }
   
   @Override
   public void onGoogleContactSelectionChanged(GoogleContact contact, boolean selected)
   {
      updateSelectedEmailsList();
      updateInviteButton();
   }   
   
   private void updateSelectedEmailsList()
   {
      selectedEmailsList.clear();
      
      for (GoogleContact contact : contacts)
      {
         if (display.isSelected(contact))
         {
            selectedEmailsList.add(contact.getEmailAddresses().get(0));
         }
      }
   }   
   
   private void bindDisplay()
   {
      display.getCloseButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getInviteButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            emailsToInvite.clear();
            emailsToInvite.addAll(customEmailsList);
            emailsToInvite.addAll(selectedEmailsList);
            invitations = 0;
            sendNextEmail();
         }
      });

      display.getSelectAllCheckBox().addValueChangeHandler(new ValueChangeHandler<Boolean>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<Boolean> event)
         {
            boolean selectAll = event.getValue() == null ? false : event.getValue();
            for (GoogleContact contact : contacts)
            {
               display.setSelected(contact, selectAll);
            }
            
            updateSelectedEmailsList();
            updateInviteButton();
         }
      });
      
      display.getEmailsTextField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            parseCustomEmails(event.getValue());
         }
      });
   }
   
   private void parseCustomEmails(String customEmails)
   {
      customEmailsList.clear();
      
      if (customEmails == null || customEmails.trim().isEmpty())
      {
         updateInviteButton();
         return;
      }
      
      String []mails = customEmails.split(",");
      for (String email : mails)
      {
         email = email.trim();
         String []mailParts = email.split("@");
         if (mailParts.length != 2)
         {
            continue;
         }

         customEmailsList.add(email);
      }
      
      updateInviteButton();
   }
   
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   private void updateInviteButton()
   {
      int emails = selectedEmailsList.size() + customEmailsList.size();      
      display.setInviteButtonEnabled(emails > 0);      
      display.setInviteButtonTitle("Invite" + (emails > 0 ? " " + emails + " " : "") + "developers");
   }
   
   public void sendNextEmail()
   {
      if (emailsToInvite.size() == 0)
      {
         IDELoader.hide();
         IDE.getInstance().closeView(display.asView().getId());
         if (invitations == 1)
         {
            Dialogs.getInstance().showInfo("IDE", "One invitation was sent successfully.");
         }
         else
         {
            Dialogs.getInstance().showInfo("IDE", "" + invitations + " invitations were sent successfully.");
         }
         return;
      }
      
      final String emailToInvite = emailsToInvite.remove(0);
      String inviteMessage = display.getInviteMessge();

      IDELoader.show("Inviting " + emailToInvite);
      try
      {
         inviteClientService.inviteUser(emailToInvite, inviteMessage, new AsyncRequestCallback<String>()
         {
            @Override
            protected void onSuccess(String result)
            {
               invitations++;
               sendNextEmail();
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDELoader.hide();
               IDE.fireEvent(new ExceptionThrownEvent(exception));
               return;
            }
         });
      }
      catch (RequestException e)
      {
         IDELoader.hide();
         IDE.fireEvent(new ExceptionThrownEvent(e));
         e.printStackTrace();
      }      
   }

}
