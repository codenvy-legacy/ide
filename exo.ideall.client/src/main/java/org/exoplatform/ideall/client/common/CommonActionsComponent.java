/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.common;

import org.exoplatform.ideall.client.action.GetItemURLForm;
import org.exoplatform.ideall.client.action.GoToLineForm;
import org.exoplatform.ideall.client.application.component.AbstractApplicationComponent;
import org.exoplatform.ideall.client.autocompletion.AutoCompletionManager;
import org.exoplatform.ideall.client.cookie.CookieManager;
import org.exoplatform.ideall.client.event.edit.GoToLineEvent;
import org.exoplatform.ideall.client.event.edit.GoToLineHandler;
import org.exoplatform.ideall.client.event.file.GetFileURLEvent;
import org.exoplatform.ideall.client.event.file.GetFileURLHandler;
import org.exoplatform.ideall.client.outline.event.ShowOutlineEvent;
import org.exoplatform.ideall.client.outline.event.ShowOutlineHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CommonActionsComponent extends AbstractApplicationComponent implements ShowOutlineHandler,
   GoToLineHandler, GetFileURLHandler
{



   public CommonActionsComponent()
   {
      super(new CommonActionsComponentInitializer());
   }

   @Override
   protected void onInitializeComponent()
   {


      //new JavaScriptAutoCompletionHandler(eventBus, context);
      new AutoCompletionManager(eventBus, context);
   }

   @Override
   protected void registerHandlers()
   {

      addHandler(ShowOutlineEvent.TYPE, this);

      addHandler(GetFileURLEvent.TYPE, this);

      addHandler(GoToLineEvent.TYPE, this);

      /*
       * Initializing Save, Save As, Save All Command Handlers
       */

   }

   public void onGetFileURL(GetFileURLEvent event)
   {
      String url = context.getSelectedItems(context.getSelectedNavigationPanel()).get(0).getHref();
      new GetItemURLForm(eventBus, url);
   }

   /**
    * @see org.exoplatform.ideall.client.event.edit.GoToLineHandler#onGoToLine(org.exoplatform.ideall.client.event.edit.GoToLineEvent)
    */
   public void onGoToLine(GoToLineEvent event)
   {
      if (context.getActiveFile() != null)
      {
         new GoToLineForm(eventBus, context);
      }
   }

   /**
    * @see org.exoplatform.ideall.client.outline.event.ShowOutlineHandler#onShowOutline(org.exoplatform.ideall.client.outline.event.ShowOutlineEvent)
    */
   public void onShowOutline(ShowOutlineEvent event)
   {
      context.setShowOutline(event.isShow());
      CookieManager.getInstance().storeOutline(context);
   }

}
