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
package org.exoplatform.ide.editor.java.client.ui;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;
import org.exoplatform.ide.editor.java.client.model.ShortTypeInfo;
import org.exoplatform.ide.editor.java.client.model.Types;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Dec 2, 2011 12:42:31 PM evgen $
 *
 */
public class TypeList extends ListGrid<ShortTypeInfo>
{
   
   

   /**
    * @param cell
    */
   public TypeList()
   {
      //Image column
      Column<ShortTypeInfo, ImageResource> iconColumn = new Column<ShortTypeInfo, ImageResource>(new ImageResourceCell())
      {
         @Override
         public ImageResource getValue(ShortTypeInfo item)
         {
            return getImageForType(item.getType());
         }
      };
      
      Column<ShortTypeInfo, SafeHtml> repositoryColumn = new Column<ShortTypeInfo, SafeHtml>(new SafeHtmlCell())
      {
         @Override
         public SafeHtml getValue(final ShortTypeInfo item)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               public String asString()
               {
                  return item.getName() + " - " + item.getQualifiedName().substring(0, item.getQualifiedName().lastIndexOf('.'));
               }
            };
            return html;
         }
      };
      
      getCellTable().addColumn(iconColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
      getCellTable().setColumnWidth(iconColumn, 28, Unit.PX);
      
      getCellTable().addColumn(repositoryColumn);
   }

   private ImageResource getImageForType(Types type)
   {
      switch (type)
      {
         case CLASS :
            return JavaClientBundle.INSTANCE.classItem();
         case INTERFACE :
            return JavaClientBundle.INSTANCE.interfaceItem();
         default :
            return JavaClientBundle.INSTANCE.classItem();
      }
   }
   

}
