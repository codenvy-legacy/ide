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
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialog;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.invite.GoogleContact;
import org.exoplatform.ide.client.framework.invite.GoogleContactsService;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.JsPopUpOAuthWindow;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.jenkins.client.marshal.StringContentUnmarshaller;
import org.exoplatform.ide.extension.samples.client.inviting.InviteClientService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class InviteGoogleDevelopersPresenter implements InviteGoogleDevelopersHandler, ViewClosedHandler,
   GoogleContactSelectionChangedHandler
{

   //public static final String CONTACTS_FAILED = "Error loading the list of Google contacts.";

   public interface Display extends IsView
   {

      void setDevelopersListVisible(boolean visible);
      
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

      FocusWidget getEmailsFocusWidget();

      void showEmailsHint();

      void hideEmailsHint();

   }

   private Display display;

   private List<GoogleContact> contacts;

   private List<String> customEmailsList = new ArrayList<String>();

   private List<String> selectedEmailsList = new ArrayList<String>();

   private List<String> emailsToInvite = new ArrayList<String>();

   private int invitations = 0;

   /**
    * Comparator for ordering Google contacts list alphabetically, by first e-mail.
    */
   private static Comparator<GoogleContact> googleContactsComparator = new GoogleContactsComparator();

   public InviteGoogleDevelopersPresenter()
   {
      IDE.getInstance().addControl(new InviteGoogleDevelopersControl());

      IDE.addHandler(InviteGoogleDevelopersEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   @Override
   public void onInviteGoogleDevelopers(InviteGoogleDevelopersEvent event)
   {
      if (display != null)
      {
         return;
      }
      customEmailsList.clear();
      selectedEmailsList.clear();
      emailsToInvite.clear();    
      display = GWT.create(Display.class);
      bindDisplay();
      display.showEmailsHint();
      contacts = new ArrayList<GoogleContact>();
      display.setDevelopersListVisible(true);
      //lazyLoadGoogleContacts();
      isAuthenticate();
   }

//   /**
//    * Load list of Google contacts from prepared JSON file.
//    * This method uses only for testing.
//    */
//   private void lazyLoadGoogleContacts()
//   {
//      IDELoader.show("Loading Google contacts...");
//
//      new Timer()
//      {
//         @Override
//         public void run()
//         {
//            try
//            {
//               String url = "/IDE/google-contacts.json";
//               AsyncRequest
//                  .build(RequestBuilder.GET, URL.encode(url))
//                  .loader(IDELoader.get())
//                  .send(
//                     new AsyncRequestCallback<List<GoogleContact>>(new InviteGoogleContactsUnmarshaller(
//                        new ArrayList<GoogleContact>()))
//                     {
//                        @Override
//                        protected void onSuccess(List<GoogleContact> result)
//                        {
//                           IDELoader.hide();
//                           googleContactsReceived(result);
//                        }
//
//                        @Override
//                        protected void onFailure(Throwable exception)
//                        {
//                           IDELoader.hide();
//                           loadContactsFailed();
//                        }
//                     });
//               IDELoader.show("Loading Google contacts...");
//            }
//            catch (RequestException exception)
//            {
//               IDELoader.hide();
//               loadContactsFailed();
//            }
//         }
//      }.schedule(500);
//   }

   private void isAuthenticate()
   {
      try
      {
         StringContentUnmarshaller unmarshaller = new StringContentUnmarshaller(new StringBuilder());
         GoogleContactsService.getInstance().isAuthenticate(new AsyncRequestCallback<StringBuilder>(unmarshaller)
         {
            @Override
            protected void onSuccess(StringBuilder s)
            {
               JSONObject object = JSONParser.parseStrict(s.toString()).isObject();
               String state = object.get("state").isString().stringValue();
               if ("valid".equals(state))
               {
                  loadGoogleContacts();
               }
               else
               {
                  showLoginWindow();
               }
            }

            @Override
            protected void onFailure(Throwable throwable)
            {
               showLoginWindow();
            }
         });
      }
      catch (RequestException exception)
      {
         loadContactsFailed();
      }
   }

   protected void showLoginWindow()
   {
      String message =
         "If you want to invite someone from your Google contact list, <br> "
            + "press Yes button and you will be redirected to Google authorization page.";

      Dialog askDialog = new Dialog("You have to be logged in Google account!", message, Dialog.Type.ASK);

      BooleanValueReceivedHandler handler = new BooleanValueReceivedHandler()
      {
         @Override
         public void booleanValueReceived(Boolean aBoolean)
         {
            if (aBoolean != null && aBoolean)
            {
               String authUrl = Utils.getAuthorizationContext()//
                  + "/ide/oauth/authenticate?oauth_provider=google&mode=federated_login"//
                  + "&scope=https://www.googleapis.com/auth/userinfo.profile"//
                  + "&scope=https://www.googleapis.com/auth/userinfo.email"//
                  + "&scope=https://www.googleapis.com/auth/appengine.admin" //
                  + "&scope=https://www.google.com/m8/feeds"//
                  + "&userId=" + IDE.userId//
                  + "&redirect_after_login="//
                  + Utils.getAuthorizationPageURL();

               JsPopUpOAuthWindow authWindow =
                  new JsPopUpOAuthWindow(authUrl, Utils.getAuthorizationErrorPageURL(), 980, 500);
               authWindow.loginWithOAuth();
               if (display != null)
               {
                  display = null;
               }
            }
            else
            {
               loadContactsFailed();
            }
         }
      };

      askDialog.setBooleanValueReceivedHandler(handler);

      Dialogs.getInstance().showDialog(askDialog);
   }

   /**
    * 
    */
   private void loadGoogleContacts()
   {
      try
      {
         GoogleContactsService.getInstance().getContacts(
            new AsyncRequestCallback<List<GoogleContact>>(new InviteGoogleContactsUnmarshaller(
               new ArrayList<GoogleContact>()))
            {
               @Override
               protected void onSuccess(List<GoogleContact> result)
               {
                  IDE.getInstance().openView(display.asView());
                  googleContactsReceived(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  loadContactsFailed();
               }
            });
         IDELoader.show("Loading Google contacts...");
      }
      catch (RequestException exception)
      {
         loadContactsFailed();
      }
   }
   
   private void loadContactsFailed()
   {
      IDE.getInstance().openView(display.asView());
      display.setDevelopersListVisible(false);
   }

   private void googleContactsReceived(List<GoogleContact> contacts)
   {
      for (GoogleContact contact : contacts)
      {
         if (contact.getEmailAddresses() != null && !contact.getEmailAddresses().isEmpty())
         {
            this.contacts.add(contact);
         }
      }
      Collections.sort(contacts, googleContactsComparator);

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

      display.getEmailsFocusWidget().addFocusHandler(new FocusHandler()
      {
         @Override
         public void onFocus(FocusEvent event)
         {
            display.hideEmailsHint();
         }
      });

      display.getEmailsFocusWidget().addBlurHandler(new BlurHandler()
      {
         @Override
         public void onBlur(BlurEvent event)
         {
            String emails = display.getEmailsTextField().getValue();
            if (emails == null || emails.trim().isEmpty())
            {
               display.showEmailsHint();
            }
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

      String[] mails = customEmails.split(",");
      for (String email : mails)
      {
         email = email.trim();
         String[] mailParts = email.split("@");
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
      if (emails == 1)
         display.setInviteButtonTitle("Invite 1 developer");
      else
         display.setInviteButtonTitle("Invite" + (emails > 0 ? " " + emails + " " : " ") + "developers");

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
         InviteClientService.getInstance().inviteUser(emailToInvite, inviteMessage, new AsyncRequestCallback<String>()
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
