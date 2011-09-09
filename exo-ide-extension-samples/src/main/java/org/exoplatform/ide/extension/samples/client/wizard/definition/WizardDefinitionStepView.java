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
package org.exoplatform.ide.extension.samples.client.wizard.definition;

import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.event.dom.client.HasClickHandlers;

import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * View to show Wizard for Java project creation.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SourceWizardView.java Sep 7, 2011 2:59:09 PM vereshchaka $
 */
public class WizardDefinitionStepView extends ViewImpl implements WizardDefinitionStepPresenter.Display 
{
   private static final String ID = "WizardProjectDefinitionView";
   
   private static final String TITLE = "Wizard for Java Project";
   
   private static final int HEIGHT = 345;

   private static final int WIDTH = 450;
   
   interface SourceWizardViewUiBinder extends UiBinder<Widget, WizardDefinitionStepView>
   {
   }
   
   /**
    * UIBinder instance
    */
   private static SourceWizardViewUiBinder uiBinder = GWT.create(SourceWizardViewUiBinder.class);
   
   @UiField
   TextField nameProjectField;
   
   @UiField
   SelectItem typeSelectField;
   
   @UiField
   ImageButton cancelButton;
   
   @UiField
   ImageButton nextButton;
   
   @UiField
   ImageButton backButton;
   
   public WizardDefinitionStepView()
   {
      super(ID, ViewType.POPUP, TITLE, null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.source.WizardSourceStepPresenter.Display#getNextButton()
    */
   @Override
   public HasClickHandlers getNextButton()
   {
      return nextButton;
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
    * @see org.exoplatform.ide.extension.samples.client.wizard.source.WizardSourceStepPresenter.Display#getSelectSourceField()
    */
   @Override
   public HasValue<String> getSelectTypeField()
   {
      return typeSelectField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.definition.WizardDefinitionStepPresenter.Display#getNameField()
    */
   @Override
   public HasValue<String> getNameField()
   {
      return nameProjectField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.definition.WizardDefinitionStepPresenter.Display#enableNextButton(boolean)
    */
   @Override
   public void enableNextButton(boolean enabled)
   {
      nextButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.definition.WizardDefinitionStepPresenter.Display#setTypes(java.lang.String[])
    */
   @Override
   public void setTypes(String[] types)
   {
      typeSelectField.setValueMap(types);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.definition.WizardDefinitionStepPresenter.Display#getBackButton()
    */
   @Override
   public HasClickHandlers getBackButton()
   {
      return backButton;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.definition.WizardDefinitionStepPresenter.Display#focusInNameField()
    */
   @Override
   public void focusInNameField()
   {
      nameProjectField.focusInItem();
   }

}
