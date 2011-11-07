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
package org.exoplatform.ide.client.project.fromtemplate;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

/**
 * Control for create project from template command.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"administrators", "developers"})
public class CreateProjectFromTemplateControl extends SimpleControl implements IDEControl, VfsChangedHandler
{

   public static final String ID = "Project/New/From Template...";

   private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.createProjectFromTemplateTitleControl();

   private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.createProjectFromTemplatePromptControl();

   private VirtualFileSystemInfo vfsInfo;

   /**
    * 
    */
   public CreateProjectFromTemplateControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(IDEImageBundle.INSTANCE.createFromTemplate(), IDEImageBundle.INSTANCE.createFromTemplateDisabled());
      setEvent(new CreateProjectFromTemplateEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      setVisible(true);

      IDE.addHandler(VfsChangedEvent.TYPE, this);

      updateEnabling();
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
      updateEnabling();
   }

   /**
    * 
    */
   private void updateEnabling()
   {
      setEnabled(vfsInfo != null);
   }

   //   /**
   //    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
   //    */
   //   public void onItemsSelected(ItemsSelectedEvent event)
   //   {
   //      if (event.getSelectedItems().size() != 1)
   //      {
   //         folderItemSelected = false;
   //         updateEnabling();
   //         return;
   //      }
   //
   //      if(event.getSelectedItems().get(0) instanceof ProjectModel)
   //      {
   //         folderItemSelected = false;
   //         updateEnabling();
   //         return;
   //      }
   //      folderItemSelected = true;
   //      updateEnabling();
   //   }

   //   protected void updateEnabling()
   //   {
   //      if (entryPoint == null)
   //      {
   //         setVisible(false);
   //         setEnabled(false);
   //         return;
   //      }
   //      
   //      setVisible(true);
   //      
   //      if (!browserSelected)
   //      {
   //         setEnabled(false);
   //         return;
   //      }
   //      if (folderItemSelected)
   //      {
   //         setEnabled(true);
   //      }
   //      else
   //      {
   //         setEnabled(false);
   //      }
   //   }

}
