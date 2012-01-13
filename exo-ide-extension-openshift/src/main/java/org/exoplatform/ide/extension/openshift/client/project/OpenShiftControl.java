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
package org.exoplatform.ide.extension.openshift.client.project;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientBundle;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Control for managing project, deployed on OpenShift.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 5, 2011 9:55:32 AM anya $
 * 
 */
public class OpenShiftControl extends SimpleControl implements IDEControl, ProjectOpenedHandler, ProjectClosedHandler,
   FolderRefreshedHandler
{
   public OpenShiftControl()
   {
      super("Project/PaaS/OpenShift");
      setTitle(OpenShiftExtension.LOCALIZATION_CONSTANT.openShiftControlTitle());
      setPrompt(OpenShiftExtension.LOCALIZATION_CONSTANT.openShiftControlPrompt());
      setImages(OpenShiftClientBundle.INSTANCE.openShiftControl(),
         OpenShiftClientBundle.INSTANCE.openShiftControlDisabled());
      setEvent(new ManageOpenShiftProjectEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(FolderRefreshedEvent.TYPE, this);
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
      update(event.getProject());
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler#onFolderRefreshed(org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent)
    */
   @Override
   public void onFolderRefreshed(FolderRefreshedEvent event)
   {
      if (event.getFolder() instanceof ProjectModel)
      {
         update((ProjectModel)event.getFolder());
      }
   }

   /**
    * @param project
    */
   private void update(ProjectModel project)
   {
      boolean isOpenShiftProject = project.getPropertyValue("openshift-express-application") != null;
      setVisible(isOpenShiftProject);
      setEnabled(isOpenShiftProject);
   }

}
