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
package org.exoplatform.ide.client.framework.ui;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Contains hidden component, to clear focus from active component.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ClearFocusForm.java Feb 4, 2011 12:16:35 PM vereshchaka $
 *
 */
public class ClearFocusForm
{
   
   private static ClearFocusForm form;

   private TextBox textBox;
   
   private ClearFocusForm()
   {
      textBox = new TextBox();
      textBox.getElement().getStyle().setPosition(Position.ABSOLUTE);
      textBox.getElement().getStyle().setWidth(10, Unit.PX);
      textBox.getElement().getStyle().setHeight(10, Unit.PX);
      RootPanel.get().add(textBox, -10000, -10000);
   }

   public void clearFocus()
   {
      textBox.setFocus(true);
      textBox.setText(".");
   }
   
   public static ClearFocusForm getInstance()
   {
      if (form == null)
      {
         form  = new ClearFocusForm();
      }
      return form;
   }

}
