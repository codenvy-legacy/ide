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

import java.util.ArrayList;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class EditControlsFormatter extends ControlFormatterBase
{

   /**
    * @see org.exoplatform.ide.client.application.ControlFormatterBase#initControlsOrder()
    */
   @Override
   protected void initControlsOrder()
   {
      controlIdsOrder = new ArrayList<String>();
      controlIdsOrder.add("Edit/Cut Item(s)");
      controlIdsOrder.add("Edit/Copy Item(s)");
      controlIdsOrder.add("Edit/Paste Item(s)");
      controlIdsOrder.add("Edit/Undo Typing");
      controlIdsOrder.add("Edit/Redo Typing");
      controlIdsOrder.add("Edit/Format");
      controlIdsOrder.add("Edit/Organize Imports");
      controlIdsOrder.add("Edit/Add Block Comment");
      controlIdsOrder.add("Edit/Remove Block Comment");
      controlIdsOrder.add("Edit/Find-Replace...");
      controlIdsOrder.add("Edit/Show \\ Hide Line Numbers");
      controlIdsOrder.add("Edit/Delete Current Line");
      controlIdsOrder.add("Edit/Go to Line...");
      controlIdsOrder.add("Edit/Lock \\ Unlock File");
   }

   /**
    * @see org.exoplatform.ide.client.application.ControlFormatterBase#getMainMenuPrefix()
    */
   @Override
   protected String getMainMenuPrefix()
   {
      return "Edit/";
   }

}
