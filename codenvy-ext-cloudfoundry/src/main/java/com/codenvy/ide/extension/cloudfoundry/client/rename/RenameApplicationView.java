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
package com.codenvy.ide.extension.cloudfoundry.client.rename;

import com.codenvy.ide.view.View;

import com.codenvy.ide.extension.cloudfoundry.client.rename.RenameApplicationView;

/**
 *
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface RenameApplicationView extends View<RenameApplicationView.ActionDelegate>
{
   public interface ActionDelegate
   {
      public void onNameChanged();

      public void onRenameClicked();

      public void onCancelClicked();
   }

   /**
    * Select value in rename field.
    */
   public void selectValueInRenameField();

   /**
    * Change the enable state of the rename button.
    * 
    * @param isEnabled
    */
   public void enableRenameButton(boolean isEnabled);

   public String getName();

   public void setName(String name);

   public void showDialog();

   public void close();
}