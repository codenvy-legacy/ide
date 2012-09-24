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
package org.exoplatform.ide.extension.aws.client.ec2;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.aws.shared.ec2.ImageInfo;
import org.exoplatform.ide.extension.aws.shared.s3.S3Object;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: EC2ImagesGrid.java Sep 21, 2012 3:07:04 PM azatsarynnyy $
 *
 */
public class EC2ImagesGrid extends ListGrid<ImageInfo>
{
   private static final String ID = "ideEC2ImagesGrid";

   public EC2ImagesGrid()
   {
      setID(ID);
      initColumns();
   }

   /**
    * Initialize columns.
    */
   private void initColumns()
   {
      Column<ImageInfo, String> amiIdCol = new Column<ImageInfo, String>(new TextCell())
      {

         @Override
         public String getValue(ImageInfo ec2Image)
         {
            return ec2Image.getAmiId();
         }
      };

      Column<ImageInfo, String> statusCol = new Column<ImageInfo, String>(new TextCell())
      {
         @Override
         public String getValue(ImageInfo ec2Image)
         {
            return ec2Image.getState().toString();
         }
      };

      Column<ImageInfo, String> sourceCol = new Column<ImageInfo, String>(new TextCell())
      {
         @Override
         public String getValue(ImageInfo ec2Image)
         {
            return ec2Image.getManifest();
         }
      };

      getCellTable().addColumn(amiIdCol, "AMI ID");
      getCellTable().setColumnWidth(amiIdCol, 20, Unit.PCT );
      getCellTable().addColumn(statusCol, "Status");
      getCellTable().setColumnWidth(statusCol, 20, Unit.PCT );
      getCellTable().addColumn(sourceCol, "Source");
      getCellTable().setColumnWidth(sourceCol, 40, Unit.PCT );
   }

//   /**
//    * Handler for deleting applications.
//    * 
//    * @param handler
//    * @return
//    */
//   public HandlerRegistration addDeleteButtonSelectionHandler(final SelectionHandler<S3Object> handler)
//   {
//      deleteAppColumn.setFieldUpdater(new FieldUpdater<S3Object, String>()
//      {
//
//         @Override
//         public void update(int index, S3Object object, String value)
//         {
//            handler.onSelection(new SelectionEventImpl(object));
//         }
//      });
//      return null;
//   }

   /**
    * Implementation of {@link SelectionEvent} event.
    */
   private class SelectionEventImpl extends SelectionEvent<S3Object>
   {
      /**
       * @param selectedItem selected application
       */
      protected SelectionEventImpl(S3Object selectedItem)
      {
         super(selectedItem);
      }

   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.ListGrid#setValue(java.util.List)
    */
   @Override
   public void setValue(List<ImageInfo> value)
   {
      super.setValue(value);
      if (value != null && value.size() > 0)
      {
         selectItem(value.get(0));
      }
      getCellTable().redraw();
   }

   /**
    * Cell for clicking to delete application.
    */
   private class Link extends ClickableTextCell
   {
      /**
       * @see com.google.gwt.cell.client.ClickableTextCell#render(com.google.gwt.cell.client.Cell.Context,
       *      com.google.gwt.safehtml.shared.SafeHtml, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
       */
      @Override
      protected void render(com.google.gwt.cell.client.Cell.Context context, final SafeHtml value, SafeHtmlBuilder sb)
      {
         SafeHtml s = new SafeHtml()
         {
            private static final long serialVersionUID = 1L;

            @Override
            public String asString()
            {
               return "<u style=\"cursor: pointer; color:##555555\">" + value.asString() + "</u>";
            }
         };
         sb.append(s);
      }
   }
}
