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
package org.exoplatform.ide.client.restdiscovery;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.commons.wadl.Param;
import org.exoplatform.gwtframework.ui.client.component.ListGrid;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class RestServiceParameterListGrid extends ListGrid<Param>
{

   private static final String NAME = "Name";
   
   private static final String TYPE = "Type";
   
   private static final String DEFAULT = "By default";
   
   private static final String GROUP = "Group";

   public RestServiceParameterListGrid()
   {
//      setEmptyMessage("Method has no parameters.");
      
      initColumns();

   }
   
   private void initColumns()
   {
      //name column
      Column<Param, String> nameColumn = new Column<Param, String>(new TextCell())
      {

         @Override
         public String getValue(final Param item)
         {
            return item.getName();
         }

      };
      getCellTable().addColumn(nameColumn, NAME);
      
      //type column
      Column<Param, String> typeColumn = new Column<Param, String>(new TextCell())
      {

         @Override
         public String getValue(final Param item)
         {
            return item.getType().getLocalName();
         }

      };
      getCellTable().addColumn(typeColumn, TYPE);
      
      //column By default
      Column<Param, String> defaultColumn = new Column<Param, String>(new TextCell())
      {

         @Override
         public String getValue(final Param item)
         {
            return item.getDefault();
         }

      };
      getCellTable().addColumn(defaultColumn, DEFAULT);
      
      //group column (will be removed)
      //TODO: made grouping by group column
//      Column<Param, String> groupColumn = new Column<Param, String>(new TextCell())
//      {
//
//         @Override
//         public String getValue(final Param item)
//         {
//            String paramType = "";
//            switch (item.getStyle())
//            {
//               case HEADER :
//                  paramType = "Header";
//                  break;
//               case QUERY :
//                  paramType = "Query";
//                  break;
//               case PLAIN :
//                   paramType = "Plain";
//                  break;
//               case TEMPLATE :
//                   paramType = "Path";
//                  break;
//               case MATRIX :
//                   paramType = "Matrix";
//                  break;
//            }
//            
//            return paramType + " param";
//         }
//
//      };
//      getCellTable().addColumn(groupColumn, GROUP);
   }

}
