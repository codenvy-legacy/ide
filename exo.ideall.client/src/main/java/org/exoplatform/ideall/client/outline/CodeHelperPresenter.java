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
package org.exoplatform.ideall.client.outline;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.outline.event.ShowOutlineEvent;
import org.exoplatform.ideall.client.outline.event.ShowOutlineHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CodeHelperPresenter implements ShowOutlineHandler
{
   interface Display
   {
      void showCodeHelper(boolean isShow);
   }
   
   private HandlerManager eventBus;
   
   private ApplicationContext context;
   
   private Handlers handlers;
   
   private Display display;
   
   public CodeHelperPresenter(HandlerManager bus, ApplicationContext applicationContext)
   {
      eventBus = bus;
      context = applicationContext;
      
      handlers = new Handlers(eventBus);
      handlers.addHandler(ShowOutlineEvent.TYPE, this);
   }
   
   public void bindDispyal(Display d)
   {
      display = d;
   }

   public void onShowOutline(ShowOutlineEvent event)
   {
      display.showCodeHelper(event.isShow());
   }

}