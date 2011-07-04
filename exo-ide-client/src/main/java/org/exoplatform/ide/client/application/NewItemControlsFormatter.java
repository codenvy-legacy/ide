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
import org.exoplatform.ide.client.navigation.control.newitem.NewFileCommand;
import org.exoplatform.ide.client.navigation.control.newitem.NewFilePopupMenuControl;
import org.exoplatform.ide.client.navigation.event.CreateNewFileEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Formatter to sort controls from "File/New" group.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class NewItemControlsFormatter implements ControlsFormatter
{
   class Group
   {
      private int number;
      
      private List<Control> controls;
      
      public Group(int number)
      {
         this.number = number;
      }
      
      /**
       * @return the number
       */
      public int getNumber()
      {
         return number;
      }
      
      /**
       * @return the controls
       */
      public List<Control> getControls()
      {
         if (controls == null)
            controls = new ArrayList<Control>();
         return controls;
      }
   }
   
   /**
    * Each group will be separated by delimiter in menu.
    */
   private List<Group> groups = new ArrayList<NewItemControlsFormatter.Group>();
   
   /**
    * @param eventBus
    */
   public NewItemControlsFormatter()
   {
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.ControlsFormatter#format(java.util.List)
    */
   public void format(List<Control> controls)
   {
      createGroups(controls);
      Collections.sort(groups, groupComparator);
      
      List<Control> newItemControls = sortNewItemsControls();
      
      //Remove new items controls:
      controls.removeAll(newItemControls);
      //Add sorted items controls:
      controls.addAll(newItemControls);
      
      createNewItemGroup(controls);
      fillNewItemPopupControl(controls);
   }
   
   private List<Control> sortNewItemsControls()
   {
      List<Control> newItemControls = new ArrayList<Control>();
      for (Group group : groups)
      {
         if (newItemControls.size() > 0)
         {
            group.getControls().get(0).setDelimiterBefore(true);
         }
         newItemControls.addAll(group.getControls());
      }
      return newItemControls;
   }
   
   private void createGroups(List<Control> controls)
   {
      for (Control control : controls)
      {
         if (control.getId().startsWith("File/New/"))
         {
            int groupNumber = ((SimpleControl)control).getGroup();
            
            Group group = getGroup(groupNumber);
            if (group == null)
            {
               group = new Group(groupNumber);
               groups.add(group);
            }
            
            group.getControls().add(control);
         }
      }
   }
   
   private Group getGroup(int groupNumber)
   {
      for (Group group : groups)
      {
         if (group.getNumber() == groupNumber)
            return group;
      }
      return null;
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

   private Comparator<Group> groupComparator = new Comparator<NewItemControlsFormatter.Group>()
   {
      @Override
      public int compare(Group group1, Group group2)
      {
         Integer number1 = group1.getNumber();
         Integer number2 = group2.getNumber();
         
         return number1.compareTo(number2);
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
    * @return {@link NewItemControl}
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
