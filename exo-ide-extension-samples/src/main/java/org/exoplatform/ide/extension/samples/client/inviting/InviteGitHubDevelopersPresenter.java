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
package org.exoplatform.ide.extension.samples.client.inviting;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.samples.client.inviting.test.InviteGitHubCollaboratorsControl;
import org.exoplatform.ide.extension.samples.client.inviting.test.InviteGitHubCollaboratorsEvent;
import org.exoplatform.ide.extension.samples.client.inviting.test.InviteGitHubCollaboratorsHandler;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.clone.CloneRepositoryCompleteEvent;
import org.exoplatform.ide.git.client.clone.CloneRepositoryCompleteHandler;
import org.exoplatform.ide.git.client.github.GitHubClientService;
import org.exoplatform.ide.git.shared.Collaborators;
import org.exoplatform.ide.git.shared.GitHubUser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class InviteGitHubDevelopersPresenter implements CloneRepositoryCompleteHandler, ViewClosedHandler,
   InviteGitHubCollaboratorsHandler
{

   public interface Display extends IsView
   {

      void setDevelopers(List<GitHubUser> userList, UserSelectionChangedHandler selectionChangedHandler);

      boolean isSelected(GitHubUser user);

      void setSelected(GitHubUser user, boolean selected);

      HasValue<Boolean> getSelectAllCheckBox();

      HasClickHandlers getInviteButton();

      HasClickHandlers getCloseButton();

      String getInviteMessge();

      void setInviteButtonEnabled(boolean enabled);
   }

   private Display display;

   private InviteClientService inviteClientService;

   public InviteGitHubDevelopersPresenter(InviteClientService inviteClientService)
   {
      this.inviteClientService = inviteClientService;

      IDE.addHandler(CloneRepositoryCompleteEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);

      IDE.getInstance().addControl(new InviteGitHubCollaboratorsControl());
      IDE.getInstance().addHandler(InviteGitHubCollaboratorsEvent.TYPE, this);
   }

   @Override
   public void onInviteGitHubCollaborators(InviteGitHubCollaboratorsEvent event)
   {
      AutoBean<Collaborators> autoBean = GitExtension.AUTO_BEAN_FACTORY.collaborators();
      AutoBeanUnmarshaller<Collaborators> unmarshaller = new AutoBeanUnmarshaller<Collaborators>(autoBean);

      String url = "/IDE/git-collaborators.json";
      try
      {
         AsyncRequest.build(RequestBuilder.GET, URL.encode(url)).loader(IDELoader.get())
            .send(new AsyncRequestCallback<Collaborators>(unmarshaller)
            {
               @Override
               protected void onSuccess(Collaborators result)
               {
                  collaboratorsReceived(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
                  exception.printStackTrace();
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
         e.printStackTrace();
      }
   }

   @Override
   public void onCloneRepositoryComplete(CloneRepositoryCompleteEvent event)
   {
      AutoBean<Collaborators> autoBean = GitExtension.AUTO_BEAN_FACTORY.collaborators();
      AutoBeanUnmarshaller<Collaborators> unmarshaller = new AutoBeanUnmarshaller<Collaborators>(autoBean);
      try
      {
         GitHubClientService.getInstance().getCollaborators(event.getUser(), event.getRepositoryName(),
            new AsyncRequestCallback<Collaborators>(unmarshaller)
            {

               @Override
               protected void onSuccess(Collaborators result)
               {
                  collaboratorsReceived(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
                  exception.printStackTrace();
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
         e.printStackTrace();
      }
   }

   private List<GitHubUser> collaborators;

   private void collaboratorsReceived(Collaborators result)
   {
      if (display != null)
      {
         return;
      }

      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();

      collaborators = new ArrayList<GitHubUser>();
      for (GitHubUser user : result.getCollaborators())
      {
         if (user.getEmail() != null && !user.getEmail().isEmpty())
         {
            collaborators.add(user);
         }
      }

      //collaborators = result.getCollaborators();
      display.setDevelopers(collaborators, selectionChangedHandler);
      display.setInviteButtonEnabled(false);
   }
   
   private boolean hasSelectedUsers()
   {
      for (GitHubUser user : collaborators)
      {
         if (display.isSelected(user))
         {
            return true;
         }
      }
      
      return false;
   }

   private UserSelectionChangedHandler selectionChangedHandler = new UserSelectionChangedHandler()
   {
      @Override
      public void onUserSelectionChanged(GitHubUser user, boolean selected)
      {
         display.setInviteButtonEnabled(hasSelectedUsers());
      }
   };

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
            inviteCollaborators();
         }
      });

      display.getSelectAllCheckBox().addValueChangeHandler(new ValueChangeHandler<Boolean>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<Boolean> event)
         {
            boolean selectAll = event.getValue() == null ? false : event.getValue();
            for (GitHubUser user : collaborators)
            {
               display.setSelected(user, selectAll);
            }
            
            display.setInviteButtonEnabled(hasSelectedUsers());            
         }
      });
   }

   private List<String> emailsToSend = new ArrayList<String>();

   private int invitations = 0;

   private void inviteCollaborators()
   {
      emailsToSend.clear();

      for (GitHubUser user : collaborators)
      {
         if (display.isSelected(user) && user.getEmail() != null && !user.getEmail().isEmpty())
         {
            emailsToSend.add(user.getEmail());
         }
      }

      if (emailsToSend.size() > 0)
      {
         invitations = 0;
         sendNextEmail();
      }
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   private void sendNextEmail()
   {
      if (emailsToSend.size() == 0)
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

      String email = emailsToSend.remove(0);
      String inviteMessage = display.getInviteMessge();

      IDELoader.show("Inviting " + email);
      try
      {
         inviteClientService.inviteUser(email, inviteMessage, new AsyncRequestCallback<String>()
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
