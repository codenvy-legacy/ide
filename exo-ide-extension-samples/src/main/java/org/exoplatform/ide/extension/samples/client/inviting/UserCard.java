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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.git.shared.GitHubUser;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class UserCard extends Composite
{

   private static UserCardUiBinder uiBinder = GWT.create(UserCardUiBinder.class);

   interface UserCardUiBinder extends UiBinder<Widget, UserCard>
   {
   }

   interface UserCardStyle extends CssResource
   {
      String userFieldBody();

      String userFieldBodySelected();
   }
   
   @UiField UserCardStyle style;

   @UiField
   FlowPanel userFieldBody;
   
   @UiField
   CheckBox checkBox;

   @UiField
   Image avatarImage;

   @UiField
   Label name, company, email;
   
   private GitHubUser gitUser;
   
   private UserSelectionChangedHandler selectionChangedHandler;

   public UserCard(GitHubUser user)
   {
      gitUser = user;
      
      initWidget(uiBinder.createAndBindUi(this));

      avatarImage.setUrl(user.getAvatarUrl());
      name.setText(user.getName());
      company.setText(user.getCompany());
      email.setText(user.getEmail());

      checkBox.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            setSelected(checkBox.getValue());
            if (selectionChangedHandler != null)
            {
               selectionChangedHandler.onUserSelectionChanged(gitUser, checkBox.getValue().booleanValue());
            }
         }
      });

      avatarImage.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            setSelected(!checkBox.getValue());
            if (selectionChangedHandler != null)
            {
               selectionChangedHandler.onUserSelectionChanged(gitUser, checkBox.getValue().booleanValue());
            }
         }
      });
   }
   
   public void setSelectionChangedHandler(UserSelectionChangedHandler selectionChangedHandler)
   {
      this.selectionChangedHandler = selectionChangedHandler;
   }

   public boolean isSelected()
   {
      return checkBox.getValue();
   }

   public void setSelected(boolean selected)
   {
      checkBox.setValue(selected);
      if (selected)
      {
         userFieldBody.setStyleName(style.userFieldBodySelected());
      }
      else
      {
         userFieldBody.setStyleName(style.userFieldBody());
      }
   }
}
