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
package org.exoplatform.ide.extension.googleappengine.client.deploy;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.googleappengine.client.GAEClientBundle;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;

/**
 * Control for deploying application to Google App Engine.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 16, 2012 4:24:30 PM anya $
 * 
 */
public class DeployApplicationControl extends SimpleControl implements IDEControl, ProjectOpenedHandler,
   ProjectClosedHandler
{
   private static final String ID = "PaaS/Google App Engine/Deploy";

   private static final String TITLE = GoogleAppEngineExtension.GAE_LOCALIZATION.deployApplicationControlTitle();

   private static final String PROMPT = GoogleAppEngineExtension.GAE_LOCALIZATION.deployApplicationControlPrompt();

   public DeployApplicationControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setEvent(new DeployApplicationEvent());
      setImages(GAEClientBundle.INSTANCE.updateApplication(), GAEClientBundle.INSTANCE.updateApplicationDisabled());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);

      setVisible(true);
      setEnabled(false);
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
      boolean enabled =
         ProjectType.GAE_JAVA.value().equals(event.getProject().getProjectType())
            || ProjectType.GAE_PYTHON.value().equals(event.getProject().getProjectType())
            || ProjectResolver.APP_ENGINE_JAVA.equals(event.getProject().getProjectType())
            || ProjectResolver.APP_ENGINE_PYTHON.equals(event.getProject().getProjectType());
      setEnabled(enabled);
   }
}
