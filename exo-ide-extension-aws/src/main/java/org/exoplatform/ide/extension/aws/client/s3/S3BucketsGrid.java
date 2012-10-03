/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.aws.client.s3;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.aws.shared.s3.S3Bucket;

import java.util.List;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: S3BucketsGrid.java Oct 3, 2012 vetal $
 *
 */
public class S3BucketsGrid extends ListGrid<S3Bucket>
{
   private static final String ID = "ideS3BucketsGrid";

   public S3BucketsGrid()
   {
      setID(ID);
      initColumns();
   }

   /**
    * Initialize columns.
    */
   private void initColumns()
   {
      Column<S3Bucket, String> keyCol = new Column<S3Bucket, String>(new TextCell())
      {

         @Override
         public String getValue(S3Bucket S3Bucket)
         {
            return S3Bucket.getName();
         }
      };
      getCellTable().addColumn(keyCol);
      getCellTable().setColumnWidth(keyCol, "100%");
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.ListGrid#setValue(java.util.List)
    */
   @Override
   public void setValue(List<S3Bucket> value)
   {
      super.setValue(value);
      if (value != null && value.size() > 0)
      {
         selectItem(value.get(0));
      }
      getCellTable().redraw();
   }

   
}
