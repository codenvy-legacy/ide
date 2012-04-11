/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.jdi.client;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStartedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStartedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStopedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStopedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.StopAppEvent;


public class StopAppControl extends SimpleControl implements IDEControl, AppStartedHandler, AppStopedHandler
{
   public static final String ID = "Project/Stop";

   private static final String TITLE = "Stop Application";

   private static final String PROMPT = "Stop Application";

   public StopAppControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(DebuggerClientBundle.INSTANCE.stopApp(), DebuggerClientBundle.INSTANCE.stopAppDisabled());
      setEvent(new StopAppEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      setVisible(true);
      setEnabled(false);

      IDE.addHandler(AppStartedEvent.TYPE, this);
      IDE.addHandler(AppStopedEvent.TYPE, this);
   }

   @Override
   public void onAppStarted(AppStartedEvent event)
   {
      setEnabled(true);
   }

   @Override
   public void onAppStoped(AppStopedEvent appStopedEvent)
   {
      setEnabled(false);
   }
}
