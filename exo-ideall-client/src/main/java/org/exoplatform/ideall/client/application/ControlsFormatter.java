/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.application;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.component.command.Control;
import org.exoplatform.gwtframework.ui.client.component.command.SimpleControl;
import org.exoplatform.ideall.client.framework.control.NewItemControl;
import org.exoplatform.ideall.client.module.navigation.control.newitem.CreateFileFromTemplateControl;
import org.exoplatform.ideall.client.module.navigation.control.newitem.CreateFolderControl;
import org.exoplatform.ideall.client.module.navigation.control.newitem.NewFileCommand;
import org.exoplatform.ideall.client.module.navigation.control.newitem.NewFilePopupMenuControl;
import org.exoplatform.ideall.client.module.navigation.event.newitem.CreateNewFileEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class ControlsFormatter
{

   private HandlerManager eventBus;

   public ControlsFormatter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   public void format(List<Control> controls)
   {
      createNewItemGroup(controls);
      Collections.sort(controls, controlComparator);
      fillNewItemPopupControl(controls);
   }

   private void fillNewItemPopupControl(List<Control> controls)
   {
      NewFilePopupMenuControl popup = null;
      for (Control control : controls)
      {
         if (NewFilePopupMenuControl.ID.equals(control.getId()))
         {
            popup = (NewFilePopupMenuControl)control;
         }
      }

      if (popup == null)
      {
         return;
      }

      for (Control control : controls)
      {
         if (control.getId().startsWith("File/New/") && control instanceof SimpleControl)
         {
            popup.getCommands().add((SimpleControl)control);
         }
      }
   }

   private Comparator<Control> controlComparator = new Comparator<Control>()
   {
      public int compare(Control control1, Control control2)
      {
         if (!control1.getId().startsWith("File/New/"))
         {
            return 0;
         }

         if (!control2.getId().startsWith("File/New/"))
         {
            return 0;
         }

         //         System.out.println("control1 " + control1.getId());
         //         System.out.println("control2 " + control2.getId());

         if (control1 instanceof CreateFileFromTemplateControl && control2 instanceof CreateFolderControl)
         {
            return -1;
         }
         else if (control1 instanceof CreateFolderControl && control2 instanceof CreateFileFromTemplateControl)
         {
            return 1;
         }
         else if (control1 instanceof CreateFolderControl && !(control2 instanceof CreateFolderControl))
         {
            return 1;
         }
         else if (control1 instanceof CreateFileFromTemplateControl
            && !(control2 instanceof CreateFileFromTemplateControl))
         {
            return 1;
         }

         return 0;
      }
   };

   private void createNewItemGroup(List<Control> controls)
   {
      while (true)
      {
         NewItemControl control = getNewItemControl(controls);
         if (control == null)
         {
            break;
         }

         int position = controls.indexOf(control);

         NewFileCommand command = null;
         if (control.getMimeType() == null)
         {
            command =
               new NewFileCommand(control.getId(), eventBus, control.getTitle(), control.getPrompt(),
                  control.getIcon(), control.getEvent());
         }
         else
         {
            command =
               new NewFileCommand(control.getId(), eventBus, control.getTitle(), control.getPrompt(),
                  control.getIcon(), new CreateNewFileEvent(control.getMimeType()));
         }

         controls.set(position, command);
      }
   }

   private NewItemControl getNewItemControl(List<Control> controls)
   {
      for (Control control : controls)
      {
         if (control instanceof NewItemControl)
         {
            return (NewItemControl)control;
         }
      }

      return null;
   }

}
