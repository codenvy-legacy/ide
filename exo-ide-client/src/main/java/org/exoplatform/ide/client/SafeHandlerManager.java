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
package org.exoplatform.ide.client;

import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SafeHandlerManager extends HandlerManager
{

   public SafeHandlerManager()
   {
      super(null);
   }

   private int depthIndex = 0;

   private String tab;

   @Override
   public void fireEvent(GwtEvent<?> event)
   {
      String name = event.getClass().getName();
      if (name.indexOf(".") >= 0)
      {
         name = name.substring(name.lastIndexOf(".") + 1, name.length());
      }

      refreshTab();

      System.out.println(">>> " + tab + "[" + depthIndex + "] " + name + "     - event");
      logEventParams(event);
      depthIndex++;
      refreshTab();

      try
      {
         super.fireEvent(event);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      System.out.println();

      depthIndex--;
      refreshTab();
      //System.out.println(">>> " + tab + "[" + depthIndex + "] " + name + "     - done");

      printSpacesTimer.cancel();
      printSpacesTimer.schedule(3000);
   }

   private void refreshTab()
   {
      tab = "    ";
      for (int i = 0; i < depthIndex; i++)
      {
         tab += "        ";
      }
   }

   private Timer printSpacesTimer = new Timer()
   {
      @Override
      public void run()
      {
         System.out.println("\r\n\r\n\r\n\r\n");
      }
   };

   @SuppressWarnings("unused")
   private void logEventParams(GwtEvent<?> event)
   {
      if (event instanceof SaveApplicationSettingsEvent)
      {
         SaveApplicationSettingsEvent e = (SaveApplicationSettingsEvent)event;
         System.out.println("        " + tab + "    - save type: " + e.getSaveType());
      }
   }

}
