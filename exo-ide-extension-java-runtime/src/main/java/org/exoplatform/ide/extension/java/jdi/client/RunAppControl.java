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
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStartedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStartedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStopedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStopedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.RunAppEvent;

public class RunAppControl extends SimpleControl implements IDEControl, ProjectClosedHandler, ProjectOpenedHandler,
   AppStartedHandler, AppStopedHandler
{
   public static final String ID = DebuggerExtension.LOCALIZATION_CONSTANT.runAppControlId();

   private static final String TITLE = "Run Application";

   private static final String PROMPT = "Run Application";

   public RunAppControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(DebuggerClientBundle.INSTANCE.runApp(), DebuggerClientBundle.INSTANCE.runAppDisabled());
      setEvent(new RunAppEvent());
      setGroupName(GroupNames.RUNDEBUG);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      setVisible(false);
      setEnabled(false);

      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(AppStartedEvent.TYPE, this);
      IDE.addHandler(AppStopedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      setVisible(false);
      setEnabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      String projectType = event.getProject().getProjectType();
      boolean isJavaProject =
         ProjectResolver.SPRING.equals(projectType) || ProjectResolver.SERVLET_JSP.equals(projectType)
            || ProjectResolver.APP_ENGINE_JAVA.equals(projectType) || ProjectType.JAVA.value().equals(projectType);
      setVisible(isJavaProject);
      setEnabled(isJavaProject);
      setShowInContextMenu(isJavaProject);
   }

   @Override
   public void onAppStoped(AppStopedEvent appStopedEvent)
   {
      setEnabled(true);
   }

   @Override
   public void onAppStarted(AppStartedEvent event)
   {
      setEnabled(false);
   }
}
