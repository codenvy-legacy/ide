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
package org.exoplatform.ide.extension.groovy.client.jar;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.groovy.shared.Attribute;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AttributesListGrid extends ListGrid<Attribute>
{
   
   public AttributesListGrid() {
      
      Column<Attribute, SafeHtml> nameColumn = new Column<Attribute, SafeHtml>(new SafeHtmlCell())
      {
         @Override
         public SafeHtml getValue(final Attribute property)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;
               
               @Override
               public String asString()
               {
                  return "<b>" + property.getName() + "</b>";
               }
            };
            return html;
         }
      };
      getCellTable().addColumn(nameColumn, "Name");
      
      Column<Attribute, SafeHtml> valueColumn = new Column<Attribute, SafeHtml>(new SafeHtmlCell())
      {
         @Override
         public SafeHtml getValue(final Attribute property)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               @Override
               public String asString()
               {
                  return property.getValue() == null ? "" : property.getValue();
               }
            };
            return html;
         }
      };
      getCellTable().addColumn(valueColumn, "Value");      
      
   }

}
