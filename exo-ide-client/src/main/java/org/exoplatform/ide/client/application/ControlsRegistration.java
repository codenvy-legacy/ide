/**
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
 *
 */

package org.exoplatform.ide.client.application;

import com.google.gwt.core.client.GWT;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.ui.client.component.command.Control;
import org.exoplatform.gwtframework.ui.client.component.command.StatusTextControl;
import org.exoplatform.ide.client.framework.annotation.ClassAnnotationMap;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.control.event.RegisterControlHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ControlsRegistration implements RegisterControlHandler
{

   private List<Control> registeredControls = new ArrayList<Control>();

   private List<String> toolbarDefaultControls = new ArrayList<String>();

   private List<String> statusBarControls = new ArrayList<String>();

   private HandlerManager eventBus;

   private Handlers handlers;

   public ControlsRegistration(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);

      toolbarDefaultControls.add("");
      handlers.addHandler(RegisterControlEvent.TYPE, this);
   }

   public List<Control> getRegisteredControls()
   {
      return registeredControls;
   }

   public List<String> getToolbarDefaultControls()
   {
      return toolbarDefaultControls;
   }

   public List<String> getStatusBarControls()
   {
      return statusBarControls;
   }

   public void onRegisterControl(RegisterControlEvent event)
   {
      if (!(event.getControl() instanceof IDEControl))
      {
         Dialogs.getInstance().showError("Only IDE controls can be registered! " + event.getControl().getClass());
         return;
      }

      addControl(event.getControl(), event.isDockOnToolbar(), event.isRightDocking());

      if (event.getControl() instanceof StatusTextControl)
      {
         statusBarControls.add(event.getControl().getId());
      }
   }

   protected void addControl(Control control, boolean dockOnToolbar, boolean rightDocking)
   {
      registeredControls.add(control);

      if (!dockOnToolbar)
      {
         return;
      }

      if (rightDocking)
      {
         toolbarDefaultControls.add(control.getId());
      }
      else
      {
         int position = 0;
         for (String curId : toolbarDefaultControls)
         {
            if ("".equals(curId))
            {
               break;
            }
            position++;
         }

         if (control.hasDelimiterBefore())
         {
            toolbarDefaultControls.add(position, "---");
            position++;
         }

         toolbarDefaultControls.add(position, control.getId());
      }
   }

   public void initControls(List<String> userRoles)
   {
      ClassAnnotationMap annotationMap = GWT.create(ClassAnnotationMap.class);
      if (annotationMap.getClassAnnotation() != null && annotationMap.getClassAnnotation().size() > 0)
      {
         List<Control> allowedControls = getAllowedControlsForUser(registeredControls, userRoles, annotationMap);
         registeredControls.retainAll(allowedControls);
         removeNotAllowedControls(registeredControls);
      }

      for (Control control : registeredControls)
      {
         if (control instanceof IDEControl)
         {
            ((IDEControl)control).initialize(eventBus);
         }
      }
   }

   private List<Control> getAllowedControlsForUser(List<Control> controls, List<String> userRoles,
      ClassAnnotationMap annotationMap)
   {
      List<Control> allowedControls = new ArrayList<Control>();
      for (Control control : controls)
      {
         String className = control.getClass().getName();
         List<String> rolesAllowed = annotationMap.getClassAnnotation().get(className);
         if (rolesAllowed == null || checkControlAllowedForUser(userRoles, rolesAllowed))
         {
            allowedControls.add(control);
         }
      }
      return allowedControls;
   }

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

}
