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
package org.exoplatform.ide.client.module.groovy.handlers;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.module.groovy.event.ConfigureBuildPathEvent;
import org.exoplatform.ide.client.module.groovy.event.ConfigureBuildPathHandler;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jan 6, 2011 $
 *
 */
public class ConfigureBuildPathCommandHandler implements ConfigureBuildPathHandler, ItemsSelectedHandler
{
   
   private HandlerManager eventBus;
   
   /**
    * 
    */
   public ConfigureBuildPathCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      
      eventBus.addHandler(ConfigureBuildPathEvent.TYPE, this);
   }
   
   
   /**
    * @see org.exoplatform.ide.client.module.groovy.event.ConfigureBuildPathHandler#onConfigureBuildPath(org.exoplatform.ide.client.module.groovy.event.ConfigureBuildPathEvent)
    */
   @Override
   public void onConfigureBuildPath(ConfigureBuildPathEvent event)
   {
      
   }


   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      
   }

}
