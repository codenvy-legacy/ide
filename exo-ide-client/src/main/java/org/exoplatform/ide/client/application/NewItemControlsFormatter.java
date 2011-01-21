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
package org.exoplatform.ide.client.application;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.ControlsFormatter;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.module.navigation.control.newitem.CreateFileFromTemplateControl;
import org.exoplatform.ide.client.module.navigation.control.newitem.CreateFolderControl;
import org.exoplatform.ide.client.module.navigation.control.newitem.CreateProjectFromTemplateControl;
import org.exoplatform.ide.client.module.navigation.control.newitem.CreateProjectTemplateControl;
import org.exoplatform.ide.client.module.navigation.control.newitem.NewFileCommand;
import org.exoplatform.ide.client.module.navigation.control.newitem.NewFilePopupMenuControl;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateNewFileEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class NewItemControlsFormatter implements ControlsFormatter
{
   private List<String> controlIdsOrder;
   
   /**
    * Initialize the order of the controls in menu "New".
    */
   private void initControlsOrder()
   {
      controlIdsOrder = new ArrayList<String>();
      controlIdsOrder.add(CreateProjectTemplateControl.ID);
      controlIdsOrder.add(CreateFolderControl.ID);

      controlIdsOrder.add("File/New/New Google Gadget");
      controlIdsOrder.add("File/New/New REST Service");
      controlIdsOrder.add("File/New/New POGO");
      controlIdsOrder.add("File/New/New Data Object");

      controlIdsOrder.add("File/New/New HTML");
      controlIdsOrder.add("File/New/New Java Script");
      controlIdsOrder.add("File/New/New CSS");
      controlIdsOrder.add("File/New/New Template");

      controlIdsOrder.add("File/New/New XML");
      controlIdsOrder.add("File/New/New TEXT");

      controlIdsOrder.add(CreateProjectFromTemplateControl.ID);
      controlIdsOrder.add(CreateFileFromTemplateControl.ID);
   }
   
   /**
    * @param eventBus
    */
   public NewItemControlsFormatter()
   {
      initControlsOrder();
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.ControlsFormatter#format(java.util.List)
    */
   public void format(List<Control> controls)
   {
      List<Control> newItemControls = getNewItemsControls(controls);
      controls.removeAll(newItemControls);
      controls.addAll(newItemControls);
      createNewItemGroup(controls);
      fillNewItemPopupControl(controls);
      
   }

   private List<Control> getNewItemsControls(List<Control> controls)
   {
      List<Control> newItemControls = new ArrayList<Control>();
      for (Control control : controls)
      {
         if (control.getId().startsWith("File/New/") && control instanceof SimpleControl)
         {
            newItemControls.add(control);
         }
      }
      Collections.sort(newItemControls, controlComparator);
      return newItemControls;
   }
   
   /**
    * Fill new item popup control with sub controls.
    * 
    * @param controls
    */
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

   /**
    * Comparator for items order.
    */
   private Comparator<Control> controlComparator = new Comparator<Control>()
   {
      public int compare(Control control1, Control control2)
      {
         if (!control1.getId().startsWith("File/New/") || !control2.getId().startsWith("File/New/"))
         {
            return 0;
         }
         
         Integer index1 = controlIdsOrder.indexOf(control1.getId());
         Integer index2 = controlIdsOrder.indexOf(control2.getId());
         
         //If item is not found in order list, then put it at the end
         if (index2 == -1) return -1;
         
         
         return index1.compareTo(index2);
      }
   };

   /**
    * @param controls
    */
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
               new NewFileCommand(control.getId(), control.getTitle(), control.getPrompt(),
                  control.getIcon(), control.getEvent());
         }
         else
         {
            command =
               new NewFileCommand(control.getId(), control.getTitle(), control.getPrompt(),
                  control.getIcon(), new CreateNewFileEvent(control.getMimeType()));
         }
         command.setDelimiterBefore(control.hasDelimiterBefore());
         
         controls.set(position, command);
      }
   }

   /**
    * @param controls
    * @return
    */
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
