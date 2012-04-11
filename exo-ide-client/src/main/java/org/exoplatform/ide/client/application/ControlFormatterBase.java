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
package org.exoplatform.ide.client.application;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.ide.client.framework.control.ControlsFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public abstract class ControlFormatterBase implements ControlsFormatter
{

   protected List<String> controlIdsOrder;

   protected abstract void initControlsOrder();

   protected abstract String getMainMenuPrefix();

   /**
    * Comparator for items order.
    */
   @SuppressWarnings("rawtypes")
   private Comparator<Control> controlComparator = new Comparator<Control>()
   {
      public int compare(Control control1, Control control2)
      {
         Integer index1 = controlIdsOrder.indexOf(control1.getId());
         Integer index2 = controlIdsOrder.indexOf(control2.getId());

         // If item is not found in order list, then put it at the end of the list
         if (index2 == -1)
            return -1;
         if (index1 == -1)
            return 1;

         return index1.compareTo(index2);
      }
   };

   /**
    * @see org.exoplatform.ide.client.framework.control.ControlsFormatter#format(java.util.List)
    */
   @SuppressWarnings("rawtypes")
   public void format(List<Control> controls)
   {
      List<Control> viewControls = sortViewControls(controls);
      controls.removeAll(viewControls);
      controls.addAll(viewControls);
   }

   /**
    * Sort new items controls and return them.
    * 
    * @param controls all controls
    * @return sorted only new item controls
    */
   @SuppressWarnings("rawtypes")
   private List<Control> sortViewControls(List<Control> controls)
   {
      List<Control> viewControls = new ArrayList<Control>();
      String prefix = getMainMenuPrefix();
      for (Control control : controls)
      {
         if (control.getId().startsWith(prefix))
         {
            viewControls.add(control);
         }
      }

      Collections.sort(viewControls, controlComparator);

      return viewControls;
   }

   /**
    * 
    */
   public ControlFormatterBase()
   {
      initControlsOrder();
   }

}