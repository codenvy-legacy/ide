/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.permissions;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.xml.QName;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.permissions.event.ShowPermissionsEvent;
import org.exoplatform.ide.client.permissions.event.ShowPermissionsHandler;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class handle {@link ShowPermissionsEvent} event,
 * asked permissions for item and create new {@link PermissionsManagerForm} form.
 * Created by The eXo Platform SAS .
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 19, 2010 $
 *
 */
public class ShowPermissionsCommandHandler implements ShowPermissionsHandler, ItemsSelectedHandler,
   ApplicationSettingsReceivedHandler
{

   private HandlerManager eventBus;

   private Item selectedItem;

   private Map<String, String> lockTokens;

   public ShowPermissionsCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      eventBus.addHandler(ShowPermissionsEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.permissions.event.ShowPermissionsHandler#onShowPermissions(org.exoplatform.ide.client.permissions.event.ShowPermissionsEvent)
    */
   public void onShowPermissions(ShowPermissionsEvent event)
   {
      if (selectedItem == null)
         return;

      VirtualFileSystem.getInstance().getPropertiesCallback(selectedItem,
         Arrays.asList(new QName[]{ItemProperty.ACL.ACL, ItemProperty.OWNER}), new ItemPropertiesCallback()
         {
            @Override
            protected void onSuccess(Item result)
            {
               new PermissionsManagerForm(eventBus, result, lockTokens);
            }
         });
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent)
    */
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() == 1)
      {
         selectedItem = event.getSelectedItems().get(0);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent)
    */
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {

      if (event.getApplicationSettings().getValueAsMap("lock-tokens") == null)
      {
         event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      lockTokens = event.getApplicationSettings().getValueAsMap("lock-tokens");
   }

}
