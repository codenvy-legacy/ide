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
package org.exoplatform.ide.extension.maven.client.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.maven.client.BuilderClientBundle;
import org.exoplatform.ide.extension.maven.client.BuilderExtension;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;

/**
 * Control for build project by maven builder.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildProjectControl.java Feb 17, 2012 3:51:08 PM azatsarynnyy $
 * 
 */
public class BuildProjectControl extends SimpleControl implements IDEControl, ProjectClosedHandler,
   ProjectOpenedHandler
{
   public static final String ID = BuilderExtension.LOCALIZATION_CONSTANT.buildProjectControlId();

   private static final String TITLE = BuilderExtension.LOCALIZATION_CONSTANT.buildProjectControlTitle();

   private static final String PROMPT = BuilderExtension.LOCALIZATION_CONSTANT.buildProjectControlPrompt();

   public BuildProjectControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(BuilderClientBundle.INSTANCE.build(), BuilderClientBundle.INSTANCE.buildDisabled());
      setEvent(new BuildProjectEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      setVisible(true);
      setEnabled(false);

      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      setEnabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      String projectType = event.getProject().getProjectType();
      if (ProjectResolver.APP_ENGINE_JAVA.equals(projectType) || ProjectResolver.SERVLET_JSP.equals(projectType)
         || ProjectResolver.SPRING.equals(projectType) || ProjectType.JAVA.value().equals(projectType)
         || ProjectType.JSP.value().equals(projectType) || ProjectType.AWS.value().equals(projectType) || ProjectType.JAR.value().equals(projectType))
         setEnabled(true);
   }
}
