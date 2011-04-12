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
package org.exoplatform.ide.git.client.branch;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.shared.Branch;

import java.util.List;

/**
 * Grid for disaplying branches.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 8, 2011 11:31:09 AM anya $
 *
 */
public class BranchGrid extends ListGrid<Branch>
{
   /**
    * Grid's ID.
    */
   private static final String ID = "ideBranchGrid";

   /**
    * Name column's name.
    */
   private final String NAME = "Name";

   /**
    * Name column.
    */
   Column<Branch, SafeHtml> nameColumn;

   public BranchGrid()
   {
      super();
      setID(ID);
      initColumns();
   }

   /**
    * Initialize the colums of the grid.
    */
   private void initColumns()
   {
      CellTable<Branch> cellTable = getCellTable();

      nameColumn = new Column<Branch, SafeHtml>(new SafeHtmlCell())
      {

         @Override
         public SafeHtml getValue(final Branch branch)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               @Override
               public String asString()
               {
                  if (branch.isActive())
                  {
                     return branch.getDisplayName()  + "&nbsp;" +  new Image(GitClientBundle.INSTANCE.currentBranch());
                  }
                  else
                  {
                     return branch.getDisplayName();
                  }
               }
            };
            return html;
         }

      };

      nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
      cellTable.addColumn(nameColumn, NAME);
      cellTable.setColumnWidth(nameColumn, 35, Unit.PCT);
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.ListGrid#setValue(java.util.List)
    */
   @Override
   public void setValue(List<Branch> value)
   {
      super.setValue(value);
      if (value != null && value.size() > 0)
      {
         selectItem(value.get(0));
      }
   }

   /**
    * Returns selected branch in branches grid.
    * 
    * @return {@link Branch} selected branch
    */
   public Branch getSelectedBranch()
   {
      return super.getSelectedItems().get(0);
   }
}
