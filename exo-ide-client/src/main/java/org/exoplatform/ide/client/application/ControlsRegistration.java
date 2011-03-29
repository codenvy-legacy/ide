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
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.ide.client.framework.annotation.ClassAnnotationMap;
import org.exoplatform.ide.client.framework.control.ControlsFormatter;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.control.event.AddControlsFormatterEvent;
import org.exoplatform.ide.client.framework.control.event.AddControlsFormatterHandler;
import org.exoplatform.ide.client.framework.control.event.ControlsUpdatedEvent;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.control.event.RegisterControlHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ControlsRegistration implements RegisterControlHandler, AddControlsFormatterHandler
{

   private List<Control> registeredControls = new ArrayList<Control>();

   private List<String> toolbarDefaultControls = new ArrayList<String>();

   private List<String> statusBarControls = new ArrayList<String>();

   private HandlerManager eventBus;

   private List<ControlsFormatter> controlsFormatters = new ArrayList<ControlsFormatter>();

   public ControlsRegistration(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      toolbarDefaultControls.add("");
      statusBarControls.add("");
      eventBus.addHandler(RegisterControlEvent.TYPE, this);
      eventBus.addHandler(AddControlsFormatterEvent.TYPE, this);
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
      
      registeredControls.add(event.getControl());

      if (event.getDockTarget() == RegisterControlEvent.DockTarget.TOOLBAR)
      {
         addControl(event.getControl(), toolbarDefaultControls, event.isRightDocking());
      }
      else if (event.getDockTarget() == RegisterControlEvent.DockTarget.STATUSBAR)
      {
         addControl(event.getControl(), statusBarControls, event.isRightDocking());
      }
   }
   
   public void addControl(Control<?> control, DockTarget dockTarget, boolean  rightDocking)
   {
      if (!(control instanceof IDEControl))
      {
         Dialogs.getInstance().showError("Only IDE controls can be registered! " + control.getClass());
         return;
      }
      
      registeredControls.add(control);
      
      switch (dockTarget)
      {
         case TOOLBAR :
            addControl(control, toolbarDefaultControls, rightDocking);
            break;
            
          case STATUSBAR:
             addControl(control, statusBarControls, rightDocking);
             break;
         default :
            break;
      }
   }

   protected void addControl(Control control, List<String> controls, boolean rightDocking)
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
         List<String> rolesAllowed = annotationMap.getClassAnnotations().get(className);
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

   /**
    * @see org.exoplatform.ide.client.framework.control.event.AddControlsFormatterHandler#onAddControlsFormatter(org.exoplatform.ide.client.framework.control.event.AddControlsFormatterEvent)
    */
   public void onAddControlsFormatter(AddControlsFormatterEvent event)
   {
      controlsFormatters.add(event.getControlsFormatter());
   }

   public void formatControls()
   {
      for (ControlsFormatter formatter : controlsFormatters)
      {
         formatter.format(registeredControls);
      }
      eventBus.fireEvent(new ControlsUpdatedEvent(registeredControls));
   }

}
