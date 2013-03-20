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

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.view.View;

/**
 * The view of {@link CreateApplicationPresenter}.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface CreateApplicationView extends View<CreateApplicationView.ActionDelegate>
{
   /**
    * Needs for delegate some function into CreateApplication view.
    */
   public interface ActionDelegate
   {
      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Create button.
       */
      public void onCreateClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Cancel button.
       */
      public void onCancelClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having changed auto detect application's type value.
       */
      public void onAutoDetectTypeChanged();

      /**
       * Performs any actions appropriate in response to the user 
       * having changed custom url.
       */
      public void onCustomUrlChanged();

      /**
       * Performs any actions appropriate in response to the user 
       * having changed application's name.
       */
      public void onApplicationNameChanged();

      /**
       * Performs any actions appropriate in response to the user 
       * having changed application's type.
       */
      public void onTypeChanged();

      /**
       * Performs any actions appropriate in response to the user 
       * having changed server.
       */
      public void onServerChanged();
   }

   /**
    * Returns application's type.
    * 
    * @return type's name
    */
   public String getType();

   /**
    * Returns whether need to auto detect type of project.
    * 
    * @return <code>true</code> if need to auto detect type of project, and 
    * <code>false</code> otherwise
    */
   public boolean isAutodetectType();

   /**
    * Sets whether need to auto detect project type.
    * 
    * @param autodetected <code>true</code> need to auto detect project type, <code>false</code>
    * otherwise
    */
   public void setAutodetectType(boolean autodetected);

   /**
    * Returns CloudFoundry application's name.
    * 
    * @return application name
    */
   public String getName();

   /**
    * Sets CloudFoundry application's name.
    * 
    * @param name application's name
    */
   public void setName(String name);

   /**
    * Returns CloudFounry application's url.
    * 
    * @return application's url
    */
   public String getUrl();

   /**
    * Sets CloudFoundry application's url.
    * 
    * @param url application's url
    */
   public void setUrl(String url);

   /**
    * Returns whether use custom url.
    * 
    * @return <code>true</code> if need to use custom url, and 
    * <code>false</code> otherwise
    */
   public boolean isCustomUrl();

   /**
    * Returns amount of instances.
    * 
    * @return instances
    */
   public String getInstances();

   /**
    * Sets amount of instances.
    * 
    * @param instances amount of instances
    */
   public void setInstances(String instances);

   /**
    * Returns amount of memory.
    * 
    * @return memory.
    */
   public String getMemory();

   /**
    * Sets amount of memory.
    * 
    * @param memory amount of memory
    */
   public void setMemory(String memory);

   /**
    * Returns selected server.
    * 
    * @return server
    */
   public String getServer();

   /**
    * Select new server.
    * 
    * @param server
    */
   public void setServer(String server);

   /**
    * Returns whether need to start application after create. 
    * 
    * @return <code>true</code> if need to start application after create, and 
    * <code>false</code> otherwise
    */
   public boolean isStartAfterCreation();

   /**
    * Sets whether need to start application after create.
    * 
    * @param start <code>true</code> need to start, <code>false</code>
    * otherwise
    */
   public void setStartAfterCreation(boolean start);

   /**
    * Sets whether Create button is enabled.
    * 
    * @param enable <code>true</code> to enable the button, <code>false</code>
    * to disable it
    */
   public void setEnableCreateButton(boolean enable);

   /**
    * Sets focus in the name field.
    */
   public void focusInNameField();

   /**
    * Sets available application's types.
    * 
    * @param types available types.
    */
   public void setTypeValues(JsonArray<String> types);

   /**
    * Sets whether Type field is enabled.
    * 
    * @param enable <code>true</code> to enable the field, <code>false</code>
    * to disable it
    */
   public void setEnableTypeField(boolean enable);

   /**
    * Sets whether Url field is enabled.
    * 
    * @param enable <code>true</code> to enable the field, <code>false</code>
    * to disable it
    */
   public void setEnableUrlField(boolean enable);

   /**
    * Sets whether Memory field is enabled.
    * 
    * @param enable <code>true</code> to enable the field, <code>false</code>
    * to disable it
    */
   public void setEnableMemoryField(boolean enable);

   /**
    * Sets selected item into the application type field with index.
    * 
    * @param index the index of the item to be selected
    */
   public void setSelectedIndexForTypeSelectItem(int index);

   /**
    * Sets focus in the url field.
    */
   public void focusInUrlField();

   /**
    * Sets whether Auto detect type checkitem is enabled.
    * 
    * @param enable <code>true</code> to enable the checkitem, <code>false</code>
    * to disable it
    */
   public void setEnableAutodetectTypeCheckItem(boolean enable);

   /**
    * Sets the list of servers.
    * 
    * @param servers
    */
   void setServerValues(JsonArray<String> servers);

   /**
    * Close dialog.
    */
   void close();

   /**
    * Show dialog.
    */
   void showDialog();
}