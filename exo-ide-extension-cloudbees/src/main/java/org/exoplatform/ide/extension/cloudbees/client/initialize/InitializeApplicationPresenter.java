/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudbees.client.initialize;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.cloudbees.client.initialize.event.DeployApplicationEvent;
import org.exoplatform.ide.extension.java.client.JavaClientService;
import org.exoplatform.ide.extension.java.shared.MavenResponse;

import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: InitializeApplicationPresenter.java Jun 23, 2011 12:49:09 PM vereshchaka $
 *
 */
public class InitializeApplicationPresenter implements InitializeApplicationHandler, ItemsSelectedHandler
{
   
   /**
    * Events handler.
    */
   private HandlerManager eventBus;
   
   /**
    * Selected items in navigation tree.
    */
   private List<Item> selectedItems;
   
   public InitializeApplicationPresenter(HandlerManager eventbus)
   {
      this.eventBus = eventbus;
      
      eventBus.addHandler(InitializeApplicationEvent.TYPE, this);
      eventbus.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.initialize.InitializeApplicationHandler#onInitializeApplication(org.exoplatform.ide.extension.cloudbees.client.initialize.InitializeApplicationEvent)
    */
   @Override
   public void onInitializeApplication(InitializeApplicationEvent event)
   {
      Item item = selectedItems.get(0);
      String workDir = item.getHref();
      if (item instanceof File)
      {
         workDir = workDir.substring(0, workDir.lastIndexOf("/") + 1);
      }
      final String applicationDir = workDir;
      JavaClientService.getInstance().packageProject(workDir, new AsyncRequestCallback<MavenResponse>()
      {
         @Override
         protected void onSuccess(MavenResponse result)
         {
            String output = result.getOutput();
            output = output.replace("\n", "<br>");
            eventBus.fireEvent(new OutputEvent(output, Type.INFO));
            
            String warUrl = (String)result.getResult().get("war");
            eventBus.fireEvent(new DeployApplicationEvent(applicationDir, warUrl));
         }
      });
   }
   
   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

}
