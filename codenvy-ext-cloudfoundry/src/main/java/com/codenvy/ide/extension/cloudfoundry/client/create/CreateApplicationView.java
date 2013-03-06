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
package com.codenvy.ide.extension.cloudfoundry.client.create;

import com.codenvy.ide.view.View;

import com.codenvy.ide.extension.cloudfoundry.client.create.CreateApplicationView;

import java.util.List;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface CreateApplicationView extends View<CreateApplicationView.ActionDelegate>
{
   public interface ActionDelegate
   {
      public void doCreate();

      public void doCancel();

      public void onAutoDetectTypeChanged();

      public void onCustomUrlChanged();

      public void onApplicationNameChanged();

      public void onTypeChanged();

      public void serverChanged();
   }

   public String getType();

   public boolean isAutodetectType();

   public void setAutodetectType(boolean autodetected);

   public String getName();

   public void setName(String name);

   public String getUrl();

   public void setUrl(String url);

   public boolean isCustomUrl();

   public String getInstances();

   public void setInstances(String instances);

   public String getMemory();

   public void setMemory(String memory);

   public String getServer();

   public void setServer(String server);

   public boolean isStartAfterCreation();

   public void setStartAfterCreation(boolean start);

   public void enableCreateButton(boolean enable);

   public void focusInNameField();

   public void setTypeValues(List<String> types);

   public void enableTypeField(boolean enable);

   public void enableUrlField(boolean enable);

   public void enableMemoryField(boolean enable);

   public void setSelectedIndexForTypeSelectItem(int index);

   public void focusInUrlField();

   public void enableAutodetectTypeCheckItem(boolean enable);

   /**
    * Set the list of servers to ServerSelectField.
    * 
    * @param servers
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