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
package org.exoplatform.ide.client.application;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.ide.client.framework.control.ControlsFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jan 20, 2011 2:51:47 PM anya $
 *
 */
public class MainMenuControlsFormatter implements ControlsFormatter
{
   private List<String> controlIdsOrder;

   private void initControlsOrder()
   {
      controlIdsOrder = new ArrayList<String>();

      controlIdsOrder.add("File");
      controlIdsOrder.add("Edit");
      controlIdsOrder.add("View");
      controlIdsOrder.add("Run");
      controlIdsOrder.add("Git");
      controlIdsOrder.add("Heroku");
      controlIdsOrder.add("Ssh");
      controlIdsOrder.add("Window");
      controlIdsOrder.add("Help");
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.ControlsFormatter#format(java.util.List)
    */
   public void format(List<Control> controls)
   {
      initControlsOrder();
      Collections.sort(controls, controlComparator);
   }

   private Comparator<Control> controlComparator = new Comparator<Control>()
   {
      public int compare(Control control1, Control control2)
      {
         String main1 =
            (control1.getId().indexOf("/") > 0) ? control1.getId().substring(0, control1.getId().indexOf("/")) : null;
         String main2 =
            (control2.getId().indexOf("/") > 0) ? control2.getId().substring(0, control2.getId().indexOf("/")) : null;

         if (main1 == null || main2 == null)
            return 0;

         Integer index1 = controlIdsOrder.indexOf(main1);
         Integer index2 = controlIdsOrder.indexOf(main2);

         if (index1 == -1 || index2 == -1)
            return 0;

         return index1.compareTo(index2);
      }
   };

}
