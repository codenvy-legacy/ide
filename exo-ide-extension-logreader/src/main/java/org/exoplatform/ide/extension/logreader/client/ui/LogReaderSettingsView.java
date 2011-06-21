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
package org.exoplatform.ide.extension.logreader.client.ui;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.user.client.ui.Label;

import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.user.datepicker.client.DateBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.logreader.client.LogReaderSettingsPresenter.Display;

import java.util.Date;

/**
 * Log Reader Settings View, contains input fields for
 * <ul>
 * <li>Data and Time
 * <li>Limit rows
 * <li>Offset rows
 * </ul>
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class LogReaderSettingsView extends ViewImpl implements Display
{

   private static LogReaderSettingsViewUiBinder uiBinder = GWT.create(LogReaderSettingsViewUiBinder.class);

   interface LogReaderSettingsViewUiBinder extends UiBinder<Widget, LogReaderSettingsView>
   {
   }
   @UiField
   DateBox dateField;
   
   @UiField
   TextField limitField;
   
   @UiField
   TextField offsetField;
   
   @UiField
   ImageButton okButton;
   
   @UiField
   ImageButton cancelButton;
   
   @UiField
   Label messageLabel;

   public LogReaderSettingsView()
   {
      super(ID, ViewType.MODAL, "Log Reader Settings", null, 250, 210, false);
      add(uiBinder.createAndBindUi(this));
      DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT);
      dateField.setFormat(new DateBox.DefaultFormat(dateFormat));
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderSettingsPresenter.Display#getDateField()
    */
   @Override
   public HasValue<Date> getDateField()
   {
      return dateField;
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderSettingsPresenter.Display#getOffsetField()
    */
   @Override
   public HasValue<String> getOffsetField()
   {
      return offsetField;
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderSettingsPresenter.Display#getLimitField()
    */
   @Override
   public HasValue<String> getLimitField()
   {
      return limitField;
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderSettingsPresenter.Display#getOkButton()
    */
   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderSettingsPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderSettingsPresenter.Display#showErrorMessage(java.lang.String)
    */
   @Override
   public void showErrorMessage(String message)
   {
      messageLabel.setText(message);
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderSettingsPresenter.Display#clearErrorMessage()
    */
   @Override
   public void clearErrorMessage()
   {
      messageLabel.setText("");
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderSettingsPresenter.Display#setOkButtonEnabled(boolean)
    */
   @Override
   public void setOkButtonEnabled(boolean enabled)
   {
      okButton.setEnabled(enabled);
   }

}
