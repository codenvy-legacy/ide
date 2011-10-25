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
package org.exoplatform.ide.client.project.control;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.navigation.control.newitem.NewFileCommand;
import org.exoplatform.ide.client.project.event.CreateProjectFromTemplateEvent;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Control for create project from template command.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"administrators", "developers"})
public class CreateProjectFromTemplateControl extends NewFileCommand implements ItemsSelectedHandler
{
   private boolean folderItemSelected = true;

   public static final String ID = "File/New/Create Project From Template...";
   
   private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.createProjectFromTemplateTitleControl();
   
   private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.createProjectFromTemplatePromptControl();

   public CreateProjectFromTemplateControl()
   {
      super(ID, TITLE, PROMPT, IDEImageBundle.INSTANCE.createFromTemplate(),
         IDEImageBundle.INSTANCE.createFromTemplateDisabled(), new CreateProjectFromTemplateEvent());
      setGroup(0);
   }
   
   public void initialize(HandlerManager eventBus)
   {
      super.initialize(eventBus);
      
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() != 1)
      {
         folderItemSelected = false;
         updateEnabling();
         return;
      }

      if(event.getSelectedItems().get(0) instanceof ProjectModel)
      {
         folderItemSelected = false;
         updateEnabling();
         return;
      }
      folderItemSelected = true;
      updateEnabling();
   }
   
   protected void updateEnabling()
   {
      if (entryPoint == null)
      {
         setVisible(false);
         setEnabled(false);
         return;
      }
      
      setVisible(true);
      
      if (!browserSelected)
      {
         setEnabled(false);
         return;
      }
      if (folderItemSelected)
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
   }

}
