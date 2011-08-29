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
package org.exoplatform.ide.extension.groovy.client.classpath.ui;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.extension.groovy.client.Images;
import org.exoplatform.ide.extension.groovy.client.classpath.EnumSourceType;
import org.exoplatform.ide.extension.groovy.client.classpath.GroovyClassPathEntry;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Image;

/**
 * Grid to display classpath sources.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jan 10, 2011 $
 *
 */
public class ClassPathEntryListGrid extends ListGrid<GroovyClassPathEntry>
{
   private final String ID = "ideClassPathEntryListGrid";
   
   private final String PATH = "path";
   
   private final String EMPTY_MESSAGE = "No sources. Click \"Add...\" button to add source directory or file.";

   private final String GROOVY_CLASSPATH_ENTRY = "GroovyClassPathEntry";
   
   private String currentRepository;

   public ClassPathEntryListGrid()
   {
      super();
      setID(ID);
      //TODO:
      //set multi as selection type
      //hide header and set message for empty table
//      setSelectionType(SelectionStyle.MULTIPLE);
//      setShowHeader(false);
//      setEmptyMessage(EMPTY_MESSAGE);

      SafeHtmlCell htmlCell = new SafeHtmlCell();
      Column<GroovyClassPathEntry, SafeHtml> pathColumn = new Column<GroovyClassPathEntry, SafeHtml>(htmlCell)
      {

         @Override
         public SafeHtml getValue(final GroovyClassPathEntry item)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               @Override
               public String asString()
               {
                  String imageSrc = "";
                  if (EnumSourceType.DIR.getValue().equals(item.getKind()))
                  {
                     imageSrc = Images.ClassPath.SOURCE_FOLDER;
                  }
                  else if (EnumSourceType.FILE.getValue().equals(item.getKind()))
                  {
                     imageSrc = Images.ClassPath.SOURCE_FILE;
                  }
                  Image image = new Image(imageSrc);
                  String imageHTML = ImageHelper.getImageHTML(image);
                  String path = item.getPath();
                  return "<span>" + imageHTML + "&nbsp;&nbsp;" + path + "</span>";
               }
            };
            return html;
         }

      };
      
      getCellTable().addColumn(pathColumn, PATH);
      getCellTable().setColumnWidth(pathColumn, "100%");
      
      removeTableHeader();
      
      //TODO:
      //add attribute to store entry, like it was in smartGWT list grid
//    record.setAttribute(GROOVY_CLASSPATH_ENTRY, item);      
   }

   public void setCurrentRepository(String repository)
   {
      currentRepository = repository;
   }
   
}
