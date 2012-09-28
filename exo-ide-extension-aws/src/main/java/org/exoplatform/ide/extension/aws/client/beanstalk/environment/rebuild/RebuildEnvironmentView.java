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
package org.exoplatform.ide.extension.aws.client.beanstalk.environment.rebuild;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSExtension;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: RebuildEnvironmentView.java Sep 28, 2012 4:34:01 PM azatsarynnyy $
 *
 */
public class RebuildEnvironmentView extends ViewImpl implements RebuildEnvironmentPresenter.Display
{
   private static final String ID = "ideStopEnvironmentView";

   private static final int WIDTH = 460;

   private static final int HEIGHT = 170;

   private static final String DELETE_BUTTON_ID = "ideDeleteVersionViewDeleteButton";

   private static final String CANCEL_BUTTON_ID = "ideDeleteVersionViewCancelButton";

//   private static final String DELETE_S3_BUNDLE_ID = "ideDeleteVersionViewDeleteS3Bundle";

   @UiField
   Label questionLabel;

//   @UiField
//   CheckBox deleteS3BundleField;

   @UiField
   ImageButton rebuildButton;

   @UiField
   ImageButton cancelButton;

   private static StopEnvironmentViewUiBinder uiBinder = GWT.create(StopEnvironmentViewUiBinder.class);

   interface StopEnvironmentViewUiBinder extends UiBinder<Widget, RebuildEnvironmentView>
   {
   }

   public RebuildEnvironmentView()
   {
      super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.deleteVersionViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      questionLabel.setIsHTML(true);
      rebuildButton.setButtonId(DELETE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
//      deleteS3BundleField.setName(DELETE_S3_BUNDLE_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.versions.delete.DeleteVersionPresenter.Display#getRebuildButton()
    */
   @Override
   public HasClickHandlers getRebuildButton()
   {
      return rebuildButton;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.versions.delete.DeleteVersionPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.versions.delete.DeleteVersionPresenter.Display#getRebuildQuestion()
    */
   @Override
   public HasValue<String> getRebuildQuestion()
   {
      return questionLabel;
   }

}
