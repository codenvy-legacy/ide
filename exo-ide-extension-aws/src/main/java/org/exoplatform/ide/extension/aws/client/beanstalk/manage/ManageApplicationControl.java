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
package org.exoplatform.ide.extension.aws.client.beanstalk.manage;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.extension.aws.client.AWSClientBundle;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Control for managing application on AWS.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 19, 2012 10:37:05 AM anya $
 * 
 */
public class ManageApplicationControl extends SimpleControl implements IDEControl, ProjectOpenedHandler,
   ProjectClosedHandler
   , FolderRefreshedHandler
//   , ActiveProjectChangedHandler
{
   private static final String ID = AWSExtension.LOCALIZATION_CONSTANT.manageApplicationControlId();

   private static final String TITLE = AWSExtension.LOCALIZATION_CONSTANT.manageApplicationControlTitle();

   private static final String PROMPT = AWSExtension.LOCALIZATION_CONSTANT.manageApplicationControlPrompt();

   public ManageApplicationControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(AWSClientBundle.INSTANCE.manageApplication(), AWSClientBundle.INSTANCE.manageApplicationDisabled());
      setEvent(new ManageApplicationEvent());
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
//      IDE.addHandler(ActiveProjectChangedEvent.TYPE, this);

      setVisible(true);
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
      setEnabled(event.getProject() != null && AWSExtension.isAWSApplication(event.getProject()));
   }
   
//   @Override
//   public void onActiveProjectChanged(ActiveProjectChangedEvent event)
//   {
//      setEnabled(event.getProject() != null && AWSExtension.isAWSApplication(event.getProject()));
//   }

   @Override
   public void onFolderRefreshed(FolderRefreshedEvent event)
   {
      ProjectModel project = null;
      if (event.getFolder() instanceof ProjectModel)
      {
         project = (ProjectModel)event.getFolder();
      }
      else
      {
         project = event.getFolder().getProject();
      }
      
      if (project == null)
      {
         setEnabled(false);
      }
      else
      {         
         setEnabled(AWSExtension.isAWSApplication(project));
      }
      
//      if (event.getFolder() != null && event.getFolder() instanceof ProjectModel)
//      {
//         setEnabled(AWSExtension.isAWSApplication((ProjectModel)event.getFolder()));
//      }
   }
   
}
