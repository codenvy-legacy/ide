/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.project;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.ide.client.framework.control.ControlsFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ProjectMenuItemFormatter.java Nov 21, 2011 1:01:01 PM vereshchaka $
 */
public class ProjectMenuItemFormatter implements ControlsFormatter
{
   private List<String> controlIdsOrder;
   
   private void initControlsOrder()
   {
      controlIdsOrder = new ArrayList<String>();

      controlIdsOrder.add("Project/New...");
      controlIdsOrder.add("Project/Open...");
      controlIdsOrder.add("Project/Close");
      controlIdsOrder.add("Project/PaaS");
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.ControlsFormatter#format(java.util.List)
    */
   @Override
   public void format(List<Control> controls)
   {
      initControlsOrder();
      Collections.sort(controls, controlComparator);
   }
   
   private Comparator<Control> controlComparator = new Comparator<Control>()
   {
      public int compare(Control control1, Control control2)
      {
         Integer index1 = controlIdsOrder.indexOf(control1.getId());
         Integer index2 = controlIdsOrder.indexOf(control2.getId());
         
         if (index1 == -1 || index2 == -1)
            return 0;

         return index1.compareTo(index2);
      }
   };

}
