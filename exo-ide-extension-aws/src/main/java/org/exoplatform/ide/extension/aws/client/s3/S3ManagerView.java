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
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.shared.s3.S3Object;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 17, 2012 3:32:44 PM anya $
 * 
 */
public class S3ManagerView extends ViewImpl implements S3Manager.Display
{

   private static final String ID = "ideCreateApplicationView";

   private static final int WIDTH = 560;

   private static final int HEIGHT = 360;

//   private static final String NAME_FIELD_ID = "ideCreateApplicationViewNameField";
//
//   private static final String DESCRIPTION_FIELD_ID = "ideCreateApplicationViewDescriptionField";
//
//   private static final String S3_BUCKET_FIELD_ID = "ideCreateApplicationViewS3BucketField";
//
//   private static final String S3_KEY_FIELD_ID = "ideCreateApplicationViewS3KeyField";
//
//   private static final String NEXT_BUTTON_ID = "ideCreateApplicationViewNextButton";
//
//   private static final String BACK_BUTTON_ID = "ideCreateApplicationViewBackButton";
//
//   private static final String FINISH_BUTTON_ID = "ideCreateApplicationViewFinishButton";
//
//   private static final String CANCEL_BUTTON_ID = "ideCreateApplicationViewCancelButton";

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
   public HasClickHandlers getPropertiesButton()
   {
      // TODO Auto-generated method stub
      return null;
   }


 


}
