/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.wadl.component;


import org.exoplatform.gwtframework.commons.wadl.Resource;
import org.exoplatform.gwtframework.ui.smartgwt.component.ListGrid;

import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class WadlResourceListGrid extends ListGrid<Resource>
{

   public WadlResourceListGrid()
   {
      setCanSort(false);
      setCanGroupBy(false);
      setCanFocus(false);
      setSelectionType(SelectionStyle.SINGLE);

      ListGridField toolBarItemsField = new ListGridField("wadlpath", "Path");
      setFields(toolBarItemsField);
   }

   public Resource getResource(int i)
   {
      return items.get(i);
   }

   @Override
   protected void setRecordFields(ListGridRecord record, Resource item)
   {
      record.setAttribute("wadlpath", "<span title=\"" + item.getPath() + "\">" + item.getPath() + "</span>");
   }

}
