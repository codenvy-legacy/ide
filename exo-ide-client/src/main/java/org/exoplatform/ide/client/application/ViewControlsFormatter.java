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

import java.util.ArrayList;

/**
 * Formatter to sort controls from "View" menu.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 22, 2011 3:47:02 PM anya $
 * 
 */
public class ViewControlsFormatter extends ControlFormatterBase
{
   /**
    * Initialize the order of the controls in menu "View".
    */
   @Override
   protected void initControlsOrder()
   {
      controlIdsOrder = new ArrayList<String>();
      controlIdsOrder.add("View/Properties");
      controlIdsOrder.add("View/Permissions");
      controlIdsOrder.add("View/Show \\ Hide Outline");
      controlIdsOrder.add("View/Show \\ Hide Documentation");
      controlIdsOrder.add("View/Go to Folder");
      controlIdsOrder.add("View/Get URL...");
      controlIdsOrder.add("View/Progress");
      controlIdsOrder.add("View/Output");
      controlIdsOrder.add("View/Log");
      controlIdsOrder.add("View/Show \\ Hide Hidden Files");
   }

   /**
    * @see org.exoplatform.ide.client.application.ControlFormatterBase#getMainMenuPrefix()
    */
   @Override
   protected String getMainMenuPrefix()
   {
      return "View/";
   }
}
