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
package org.exoplatform.ide.client.versioning;

import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.grid.ListGridField;

import com.smartgwt.client.widgets.grid.ListGridRecord;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.ListGrid;
import org.exoplatform.ide.client.module.vfs.api.Version;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 27, 2010 $
 *
 */
public class VersionsGrid extends ListGrid<Version>
{

   private final String NAME = "Name";
   
   private final String DATE = "Creation date";
   
   private final String LENGTH = "Content lenght";
   
   
   public VersionsGrid(){
      setCanSort(false);
      setCanGroupBy(false);
      setCanFocus(false);
      setSelectionType(SelectionStyle.SINGLE);
      setCanFreezeFields(false);

      ListGridField fieldName = new ListGridField(NAME, NAME);
      fieldName.setWidth("40%");

      ListGridField fieldDate = new ListGridField(DATE, DATE);
      fieldDate.setWidth("40%");

      ListGridField fieldLenght = new ListGridField(LENGTH, LENGTH);
      fieldLenght.setWidth("30%");

      setFields(fieldName, fieldDate, fieldLenght);
   }
   
   /**
    * @see org.exoplatform.gwtframework.ui.client.smartgwt.component.ListGrid#setRecordFields(com.smartgwt.client.widgets.grid.ListGridRecord, java.lang.Object)
    */
   @Override
   protected void setRecordFields(ListGridRecord record, Version item)
   {
      record.setAttribute(NAME, item.getDisplayName());
      record.setAttribute(DATE, item.getCreationDate());
      record.setAttribute(LENGTH, item.getContentLength());
   }

}
