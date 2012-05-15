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
package org.exoplatform.ide.extension.java.jdi.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextAreaInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.java.jdi.client.ChangeValuePresenter;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerExtension;

/**
 * View for change variable value.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ChangeValueView.java Apr 28, 2012 9:47:19 AM azatsarynnyy $
 *
 */
public class ChangeValueView extends ViewImpl implements ChangeValuePresenter.Display
{

   public static final int HEIGHT = 240;

   public static final int WIDTH = 460;

   public static final String ID = "ideChangeVariableValueView";

   private static final String CHANGE_BUTTON_ID = "ideChangeVariableValueViewChangeButton";

   private static final String CANCEL_BUTTON_ID = "ideChangeVariableValueViewCancelButton";

   private static final String EXPRESSION_FIELD_ID = "ideChangeVariableValueViewExpressionField";

   @UiField
   ImageButton changeButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   TextAreaInput expressionField;

   interface ChangeVariableValueViewUiBinder extends UiBinder<Widget, ChangeValueView>
   {
   }

   private static ChangeVariableValueViewUiBinder uiBinder = GWT.create(ChangeVariableValueViewUiBinder.class);

   public ChangeValueView()
   {
      super(ID, ViewType.MODAL, DebuggerExtension.LOCALIZATION_CONSTANT.changeValueViewTitle(), null, WIDTH, HEIGHT);
      setCloseOnEscape(true);
      add(uiBinder.createAndBindUi(this));

      expressionField.setName(EXPRESSION_FIELD_ID);
      changeButton.setButtonId(CHANGE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.java.jdi.client.ChangeValuePresenter.Display#getChangeButton()
    */
   @Override
   public HasClickHandlers getChangeButton()
   {
      return changeButton;
   }

   /**
    * @see org.exoplatform.ide.extension.java.jdi.client.ChangeValuePresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.java.jdi.client.ChangeValuePresenter.Display#getExpression()
    */
   @Override
   public HasValue<String> getExpression()
   {
      return expressionField;
   }

   /**
    * @see org.exoplatform.ide.extension.java.jdi.client.ChangeValuePresenter.Display#focusInExpressionField()
    */
   @Override
   public void focusInExpressionField()
   {
      expressionField.focus();
   }

   /**
    * @see org.exoplatform.ide.extension.java.jdi.client.ChangeValuePresenter.Display#setChangeButtonEnable(boolean)
    */
   @Override
   public void setChangeButtonEnable(boolean isEnable)
   {
      changeButton.setEnabled(isEnable);
   }

}
