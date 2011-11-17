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

import com.google.gwt.core.client.GWT;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.annotation.ClassAnnotationMap;
import org.exoplatform.ide.client.framework.control.ControlsFormatter;
import org.exoplatform.ide.client.framework.control.ControlsUpdatedEvent;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.control.IDEControl;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ControlsRegistration
{

   private List<Control> registeredControls = new ArrayList<Control>();

   private List<String> toolbarDefaultControls = new ArrayList<String>();

   private List<String> statusBarControls = new ArrayList<String>();

   private List<ControlsFormatter> controlsFormatters = new ArrayList<ControlsFormatter>();

   /**
    * 
    */
   public ControlsRegistration()
   {
      toolbarDefaultControls.add("");
      statusBarControls.add("");
   }

   /**
    * @return
    */
   public List<Control> getRegisteredControls()
   {
      return registeredControls;
   }

   /**
    * @return
    */
   public List<String> getToolbarDefaultControls()
   {
      return toolbarDefaultControls;
   }

   /**
    * @return
    */
   public List<String> getStatusBarControls()
   {
      return statusBarControls;
   }

   /**
    * @param control
    * @param docking
    */
   public void addControl(Control<?> control, Docking docking)
   {
      if (!(control instanceof IDEControl))
      {
         Dialogs.getInstance().showError(IDE.ERRORS_CONSTANT.controlsRegistration() + " " + control.getClass());
         return;
      }

      registeredControls.add(control);

      switch (docking)
      {
         case TOOLBAR :
            addControl(control, toolbarDefaultControls, false);
            break;

         case TOOLBAR_RIGHT :
            addControl(control, toolbarDefaultControls, true);
            break;

         case STATUSBAR :
            addControl(control, statusBarControls, false);
            break;
            
         case STATUSBAR_RIGHT :
            addControl(control, statusBarControls, true);
            break;
      }
   }

   /**
    * @param control
    * @param controls
    * @param rightDocking
    */
   private void addControl(Control control, List<String> controls, boolean rightDocking)
   {
      if (rightDocking)
      {
         controls.add(control.getId());
      }
      else
      {
         int position = 0;
         for (String curId : controls)
         {
            if ("".equals(curId))
            {
               break;
            }
            position++;
         }

         if (control.hasDelimiterBefore())
         {
            controls.add(position, "---");
            position++;
         }

         controls.add(position, control.getId());
      }
   }

   /**
    * @param userRoles
    */
   public void initControls(List<String> userRoles)
   {
      ClassAnnotationMap annotationMap = GWT.create(ClassAnnotationMap.class);
      if (annotationMap.getClassAnnotations() != null && annotationMap.getClassAnnotations().size() > 0)
      {
         List<Control> allowedControls = getAllowedControlsForUser(registeredControls, userRoles, annotationMap);
         registeredControls.retainAll(allowedControls);
         removeNotAllowedControls(registeredControls);
      }

      for (Control control : registeredControls)
      {
         if (control instanceof IDEControl)
         {
            ((IDEControl)control).initialize();
         }
      }
   }

   /**
    * @param controls
    * @param userRoles
    * @param annotationMap
    * @return
    */
   private List<Control> getAllowedControlsForUser(List<Control> controls, List<String> userRoles,
      ClassAnnotationMap annotationMap)
   {
      List<Control> allowedControls = new ArrayList<Control>();
      for (Control control : controls)
      {
         String className = control.getClass().getName();
         List<String> rolesAllowed = annotationMap.getClassAnnotations().get(className);
         if (rolesAllowed == null || checkControlAllowedForUser(userRoles, rolesAllowed))
         {
            allowedControls.add(control);
         }
      }
      return allowedControls;
   }

   /**
    * @param userRoles
    * @param rolesAllowed
    * @return
    */
   private boolean checkControlAllowedForUser(List<String> userRoles, List<String> rolesAllowed)
   {
      for (String role : rolesAllowed)
      {
         if (userRoles.contains(role))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * @param allowedControls
    */
   private void removeNotAllowedControls(List<Control> allowedControls)
   {
      List<String> allowedIds = new ArrayList<String>();
      for (Control control : allowedControls)
      {
         allowedIds.add(control.getId());
      }
      allowedIds.add("---");
      allowedIds.add("");

      toolbarDefaultControls.retainAll(allowedIds);
      statusBarControls.retainAll(allowedIds);
   }

   /**
    * @param formatter
    */
   public void addControlsFormatter(ControlsFormatter formatter)
   {
      controlsFormatters.add(formatter);
   }

   /**
    * 
    */
   public void formatControls()
   {
      for (ControlsFormatter formatter : controlsFormatters)
      {
         formatter.format(registeredControls);
      }

      IDE.fireEvent(new ControlsUpdatedEvent(registeredControls));
   }

}
