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
package org.exoplatform.ide.extension.googleappengine.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.LinkButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.googleappengine.client.GAEClientBundle;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;

public class CreateApplicationView extends ViewImpl implements CreateApplicationPresenter.Display
{
   private static final String ID = "exoCreateApplicationView";

   private static final String DEPLOY_BUTTON_ID = "exoCreateApplicationViewDeployButton";

   private static final String CANCEL_BUTTON_ID = "exoCreateApplicationViewCancelButton";

   private static final String TITLE = GoogleAppEngineExtension.GAE_LOCALIZATION.createApplicationViewTitle();

   private static final int WIDTH = 545;

   private static final int HEIGHT = 180;

   private static CreateApplicationViewUiBinder uiBinder = GWT.create(CreateApplicationViewUiBinder.class);

   interface CreateApplicationViewUiBinder extends UiBinder<Widget, CreateApplicationView>
   {
   }

   @UiField
   ImageButton deployButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   ImageButton createButton;

   @UiField
   Label instructionLabel;

   public CreateApplicationView()
   {
      super(ID, ViewType.MODAL, TITLE, new Image(GAEClientBundle.INSTANCE.googleAppEngine()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
      deployButton.setButtonId(DEPLOY_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
      createButton.setImage(new Image(GAEClientBundle.INSTANCE.googleAppEngine()));
      createButton.setDisabledImage(new Image(GAEClientBundle.INSTANCE.googleAppEngineDisabled()));
      createButton.setText(GoogleAppEngineExtension.GAE_LOCALIZATION.createButton());
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationPresenter.Display#getDeployButton()
    */
   @Override
   public HasClickHandlers getDeployButton()
   {
      return deployButton;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   @Override
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   @Override
   public void enableDeployButton(boolean enable)
   {
      deployButton.setEnabled(enable);
   }

   @Override
   public void enableCreateButton(boolean enable)
   {
      createButton.setEnabled(enable);
   }

   @Override
   public void setUserInstructions(String instructions)
   {
      instructionLabel.setValue(instructions);
   }
}
