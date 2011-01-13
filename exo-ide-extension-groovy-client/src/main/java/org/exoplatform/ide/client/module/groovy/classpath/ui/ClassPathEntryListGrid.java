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
package org.exoplatform.ide.client.module.groovy.classpath.ui;

import com.google.gwt.user.client.ui.Image;

import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.ListGrid;
import org.exoplatform.ide.client.module.groovy.ImageUtil;
import org.exoplatform.ide.client.module.groovy.Images;
import org.exoplatform.ide.client.module.groovy.classpath.EnumSourceType;
import org.exoplatform.ide.client.module.groovy.classpath.GroovyClassPathEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Grid to display classpath sources.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jan 10, 2011 $
 *
 */
public class ClassPathEntryListGrid extends ListGrid<GroovyClassPathEntry>
{
   private final String PATH = "path";
   
   private final String EMPTY_MESSAGE = "No sources. Click \"Add...\" button to add source directory or file.";

   private final String GROOVY_CLASSPATH_ENTRY = "GroovyClassPathEntry";

   public ClassPathEntryListGrid()
   {
      setCanSort(false);
      setSelectionType(SelectionStyle.MULTIPLE);
      setCanFreezeFields(false);
      setShowHeader(false);
      setFixedFieldWidths(false);
      setEmptyMessage(EMPTY_MESSAGE);
      setImageSize(16);

      ListGridField pathField = new ListGridField(PATH);

      setFields(pathField);
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.smartgwt.component.ListGrid#setRecordFields(com.smartgwt.client.widgets.grid.ListGridRecord, java.lang.Object)
    */
   @Override
   protected void setRecordFields(ListGridRecord record, GroovyClassPathEntry item)
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
      String imageHTML = ImageUtil.getHTML(image);
      record.setAttribute(PATH, "<span>" + imageHTML + "&nbsp;&nbsp;" + item.getPath() + "</span>");
      record.setAttribute(GROOVY_CLASSPATH_ENTRY, item);
   }

   /**
    * Get selected items in the grid.
    * 
    * @return {@link List}
    */
   public List<GroovyClassPathEntry> getSelectedItems()
   {
      List<GroovyClassPathEntry> selectedItems = new ArrayList<GroovyClassPathEntry>();
      for (ListGridRecord record : getSelection())
      {
         selectedItems.add((GroovyClassPathEntry)record.getAttributeAsObject(GROOVY_CLASSPATH_ENTRY));
      }
      return selectedItems;
   }
}
