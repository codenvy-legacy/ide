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
package org.exoplatform.gwtframework.ui.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;


/**
 * The resources for the {@link SelectItemOld} component.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 1, 2011 5:21:24 PM anya $
 *
 */
public interface SelectItemResource extends ClientBundle
{
   @Source("eXoStyle/skin/default/SelectItem.css")
   Style css();
   
   @Source("eXoStyle/skin/default/images/select/arrow-down.png")
   ImageResource arrow();

   /**
    * CSS style resources.
    */
   public interface Style extends CssResource
   {
      String selectItem();

      String selectItemDisabled();

      String selectItemTitle();

      String selectItemTitleHidden();
      
      String labelOrientationTop();
      
      String labelOrientationLeft();
      
      String labelOrientationRight();
      
      String labelAlignCenter();
      
      String labelAlignLeft();
      
      String labelAlignRight();
      
      String editableSelect();
      
      String comboboxSelectPanel();
      
      String comboBox();
      
      String comboBoxInput();
      
      @ClassName("combobox-grid")
      String comboBoxGrid();
      
      String comboBoxImageButton();
      
      String comboBoxDocPanel();
      
   }
}
