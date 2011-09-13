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
package org.exoplatform.ide.extension.samples.client.wizard.finish;

import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;

/**
 * View to show Wizard for Java project creation.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SourceWizardView.java Sep 7, 2011 2:59:09 PM vereshchaka $
 */
public class WizardFinishStepView extends ViewImpl implements WizardFinishStepPresenter.Display 
{
   private static final String ID = "WizardProjectFinishView";
   
   private static final String TITLE = SamplesExtension.LOCALIZATION_CONSTANT.wizardFinishDialogTitle();
   
   private static final int HEIGHT = 345;

   private static final int WIDTH = 450;
   
   interface SourceWizardViewUiBinder extends UiBinder<Widget, WizardFinishStepView>
   {
   }
   
   /**
    * UIBinder instance
    */
   private static SourceWizardViewUiBinder uiBinder = GWT.create(SourceWizardViewUiBinder.class);
   
   @UiField
   ImageButton cancelButton;
   
   @UiField
   ImageButton finishButton;
   
   @UiField
   ImageButton backButton;
   
   @UiField
   Label nameLabel;
   
   @UiField
   Label typeLabel;
   
   public WizardFinishStepView()
   {
      super(ID, ViewType.POPUP, TITLE, null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.source.WizardSourceStepPresenter.Display#getNextButton()
    */
   @Override
   public HasClickHandlers getFinishButton()
   {
      return finishButton;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.source.WizardSourceStepPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.finish.WizardFinishStepPresenter.Display#getBackButton()
    */
   @Override
   public HasClickHandlers getBackButton()
   {
      return backButton;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.finish.WizardFinishStepPresenter.Display#getNameLabel()
    */
   @Override
   public HasValue<String> getNameLabel()
   {
      return nameLabel;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.finish.WizardFinishStepPresenter.Display#getTypeLable()
    */
   @Override
   public HasValue<String> getTypeLable()
   {
      return typeLabel;
   }

}
