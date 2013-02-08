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
package org.exoplatform.ide.extension.cloudfoundry.client.login;

import org.exoplatform.ide.view.View;

import java.util.List;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface LoginView extends View<LoginView.ActionDelegate>
{
   public interface ActionDelegate
   {
      public void doLogIn();

      public void doCancel();

      public void onValueChanged();
   }

   public String getEmail();

   public void setEmail(String email);

   public String getPassword();

   public String getServer();

   public void setServer(String server);

   public void setError(String message);

   /**
    * Change the enable state of the login button.
    * 
    * @param enabled
    */
   void enableLoginButton(boolean enabled);

   /**
    * Give focus to login field.
    */
   void focusInEmailField();

   /**
    * Set the list of available targets.
    * 
    * @param targets
    */
   void setServerValues(List<String> servers);

   /**
    * Close dialog.
    */
   void close();

   /**
    * Show dialog.
    */
   void showDialog();
}