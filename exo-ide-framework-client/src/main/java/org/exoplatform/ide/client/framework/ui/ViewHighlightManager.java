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
package org.exoplatform.ide.client.framework.ui;


import org.exoplatform.ide.client.framework.ui.event.ViewActivatedEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 3, 2010 $
 *
 */
public class ViewHighlightManager
{

   private static ViewHighlightManager instance;

   private View currentActiveView;

   private HandlerManager eventBus;

   /**
    * @param eventBus
    */
   public ViewHighlightManager(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      instance = this;
   }

   public void selectView(View view)
   {
      if (currentActiveView == view)
         return;

      if (currentActiveView != null)
      {
         //         if (!currentActiveView.getDestroyed())
         try
         {
            if (!currentActiveView.getDestroying())
               currentActiveView.removeFocus();
         }
         catch (Exception e)
         {
            currentActiveView.removeFocus();
         }
      }
      view.highlightView();
      currentActiveView = view;
      eventBus.fireEvent(new ViewActivatedEvent(view.getViewId()));
   }

   /**
    * @return the instance
    */
   public static ViewHighlightManager getInstance()
   {
      return instance;
   }

}
