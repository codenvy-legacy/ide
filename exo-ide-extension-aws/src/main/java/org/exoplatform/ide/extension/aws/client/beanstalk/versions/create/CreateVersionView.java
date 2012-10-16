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
package org.exoplatform.ide.extension.aws.client.beanstalk.versions.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSExtension;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 21, 2012 12:34:46 PM anya $
 * 
 */
public class CreateVersionView extends ViewImpl implements CreateVersionPresenter.Display
{
   private static final String ID = "ideCreateVersionView";

   private static final int WIDTH = 580;

   private static final int HEIGHT = 310;

   private static final String VERSION_LABEL_FIELD_ID = "ideCreateApplicationViewVersionLabelField";

   private static final String DESCRIPTION_FIELD_ID = "ideCreateVersionViewDescriptionField";

   private static final String S3_BUCKET_FIELD_ID = "ideCreateVersionViewS3BucketField";

   private static final String S3_KEY_FIELD_ID = "ideCreateVersionViewS3KeyField";

   private static final String CREATE_BUTTON_ID = "ideCreateVersionViewCreateButton";

   private static final String CANCEL_BUTTON_ID = "ideCreateVersionViewCancelButton";

   private static CreateVersionViewUiBinder uiBinder = GWT.create(CreateVersionViewUiBinder.class);

   interface CreateVersionViewUiBinder extends UiBinder<Widget, CreateVersionView>
   {
   }

   @UiField
   TextInput versionLabelField;

   @UiField
   TextInput descriptionField;

   @UiField
   TextInput s3BucketField;

   @UiField
   TextInput s3KeyField;

   @UiField
   ImageButton createButton;

   @UiField
   ImageButton cancelButton;

   public CreateVersionView()
   {
      super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.createVersionViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      versionLabelField.setName(VERSION_LABEL_FIELD_ID);
      descriptionField.setName(DESCRIPTION_FIELD_ID);
      s3BucketField.setName(S3_BUCKET_FIELD_ID);
      s3KeyField.setName(S3_KEY_FIELD_ID);

      createButton.setButtonId(CREATE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#getVersionLabelField()
    */
   @Override
   public TextFieldItem getVersionLabelField()
   {
      return versionLabelField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#getDescriptionField()
    */
   @Override
   public TextFieldItem getDescriptionField()
   {
      return descriptionField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#getS3BucketField()
    */
   @Override
   public TextFieldItem getS3BucketField()
   {
      return s3BucketField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#getS3KeyField()
    */
   @Override
   public TextFieldItem getS3KeyField()
   {
      return s3KeyField;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#getCreateButton()
    */
   @Override
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#enableCreateButton(boolean)
    */
   @Override
   public void enableCreateButton(boolean enabled)
   {
      createButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#focusInVersionLabelField()
    */
   @Override
   public void focusInVersionLabelField()
   {
      versionLabelField.setFocus(true);
   }

}
