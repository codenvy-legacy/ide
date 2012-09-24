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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.shared.s3.S3Bucket;
import org.exoplatform.ide.extension.aws.shared.s3.S3Object;
import org.exoplatform.ide.extension.aws.shared.s3.S3ObjectsList;

import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 17, 2012 3:32:44 PM anya $
 * 
 */
public class S3ManagerView extends ViewImpl implements S3Manager.Display
{

   private static final String ID = "ideS3ManagerView";

   private static final int WIDTH = 1200;

   private static final int HEIGHT = 720;

   @UiField
   ImageButton deleteBucketButton;
   
   @UiField
   ImageButton deleteButton;
   
   @UiField
   ImageButton uploadButton;

   @UiField
   ListBox buckets;
   
   @UiField
   ObjectGrid s3ObjectsGrid;
   
   @UiField
   ImageButton nextButton;
   
   @UiField
   ImageButton backButton;
   
   @UiField
   ImageButton refreshButton;

   @UiField
   ImageButton createBucketButton;


   private static S3ManagerViewUiBinder uiBinder = GWT.create(S3ManagerViewUiBinder.class);

   interface S3ManagerViewUiBinder extends UiBinder<Widget, S3ManagerView>
   {
   }

   public S3ManagerView()
   {
      super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.createApplicationViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public ListGridItem<S3Object> getS3Object()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public HasClickHandlers getDeleteButton()
   {
      return deleteButton;
   }

   @Override
   public void setS3Buckets(List<S3Bucket> bucketsList)
   {
      buckets.setVisibleItemCount(bucketsList.size());

      for (S3Bucket s3Bucket : bucketsList)
      {
         buckets.addItem(s3Bucket.getName());
      }
   }

   @Override
   public HasChangeHandlers getBuckets()
   {
      return buckets;
   }
   
   @Override
   public String getSelectedBucketId()
   {
      return buckets.getItemText(buckets.getSelectedIndex());
   }
   
   @Override
   public void setS3ObjectsList(S3ObjectsList s3ObjectsList)
   {
      s3ObjectsGrid.setValue(s3ObjectsList.getObjects());
   }
   
   @Override
   public HasClickHandlers getNextButton()
   {
      return nextButton;
   }
   
   @Override
   public void setEnableNextButton(boolean enable)
   {
      nextButton.setEnabled(enable);      
   }
   
   @Override
   public S3Object getSelectedObject()
   {
      return s3ObjectsGrid.getSelectedItems().get(0);
   }
   
   @Override
   public HasClickHandlers getBackButton()
   {
      return backButton;
   }
   
   @Override
   public HasClickHandlers getRefreshButton()
   {
      return refreshButton;
   }
   
   @Override
   public HasClickHandlers getUploadButton()
   {
      return uploadButton;
   }
   
   @Override
   public void setEnableBackButton(boolean enable)
   {
      backButton.setEnabled(enable);
   }
   
   @Override
   public HasClickHandlers getDeleteBucketButton()
   {
      return deleteBucketButton;
   }
   
   @Override
   public HasClickHandlers getCreateBucketButton()
   {
      return createBucketButton;
   }

}
