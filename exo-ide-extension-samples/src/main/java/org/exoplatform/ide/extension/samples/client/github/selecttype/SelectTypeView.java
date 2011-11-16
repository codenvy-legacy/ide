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
package org.exoplatform.ide.extension.samples.client.github.selecttype;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;

import java.util.Set;

/**
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SelectTypeView.java Nov 3, 2011 5:27:17 PM vereshchaka $
 */
public class SelectTypeView extends ViewImpl implements SelectTypePresenter.Display
{
private static final String ID = "SelectTypeView";
   
   private static final String TITLE = SamplesExtension.LOCALIZATION_CONSTANT.selectTypeTitle();
   
   private static final int HEIGHT = 345;

   private static final int WIDTH = 450;
   
   interface SelectTypeViewUiBinder extends UiBinder<Widget, SelectTypeView>
   {
   }
   
   /**
    * UIBinder instance
    */
   private static SelectTypeViewUiBinder uiBinder = GWT.create(SelectTypeViewUiBinder.class);
   
   /**
    * Project's type field.
    */
   @UiField
   SelectItem projectTypeField;
   
   @UiField
   ImageButton cancelButton;
   
   @UiField
   ImageButton finishButton;
   
   @UiField
   ImageButton backButton;
   
   public SelectTypeView()
   {
      super(ID, ViewType.POPUP, TITLE, null, WIDTH, HEIGHT, false);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.selecttype.SelectTypePresenter.Display#getProjectType()
    */
   @Override
   public HasValue<String> getProjectType()
   {
      return projectTypeField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.selecttype.SelectTypePresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.selecttype.SelectTypePresenter.Display#getFinishButton()
    */
   @Override
   public HasClickHandlers getFinishButton()
   {
      return finishButton;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.selecttype.SelectTypePresenter.Display#getBackButton()
    */
   @Override
   public HasClickHandlers getBackButton()
   {
      return backButton;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.github.selecttype.SelectTypePresenter.Display#setProjectTypes(java.util.Set)
    */
   @Override
   public void setProjectTypes(Set<String> set)
   {
      projectTypeField.setValueMap(set.toArray(new String[set.size()]));
   }
}
