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

import org.exoplatform.gwtframework.ui.client.button.IconButton;
import org.exoplatform.gwtframework.ui.client.component.TextButton;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.gwtframework.ui.client.component.VPanel;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.logreader.client.LogReaderClientBundle;
import org.exoplatform.ide.extension.logreader.client.LogReaderExtension;
import org.exoplatform.ide.extension.logreader.client.LogReaderPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * View for Log reader, contains toolbar, and {@link ScrollPanel} with set of {@link LogRecord}
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class LogReaderView extends ViewImpl implements LogReaderPresenter.Display
{

   private static LogReaderViewUiBinder uiBinder = GWT.create(LogReaderViewUiBinder.class);

   interface LogReaderViewUiBinder extends UiBinder<Widget, LogReaderView>
   {
   }

   @UiField
   ScrollPanel scrollPanel;

   @UiField
   FlowPanel contentPanel;

   @UiField
   Toolbar toolbar;

   @UiField
   VPanel basePanel;

   private IconButton clearLogButton;

   private IconButton settingsButton;

   private TextButton logButton;

   private boolean odd = true;

   public LogReaderView()
   {
      super(ID, ViewType.OPERATION, "Log Reader", new Image(LogReaderClientBundle.INSTANCE.logReader()));
      add(uiBinder.createAndBindUi(this));

      settingsButton =
         new IconButton(new Image(LogReaderClientBundle.INSTANCE.logRederSettings()), new Image(
            LogReaderClientBundle.INSTANCE.logRederSettings()));
      settingsButton.setTitle(LogReaderExtension.MESSAGES.getSettingsTitle());
      toolbar.addItem(settingsButton);

      toolbar.addDelimiter();
      
      logButton = new TextButton(LogReaderExtension.MESSAGES.getLogButton());
      toolbar.addItem(logButton);
      toolbar.addDelimiter();

      clearLogButton =
         new IconButton(new Image(LogReaderClientBundle.INSTANCE.clearOutput()), new Image(
            LogReaderClientBundle.INSTANCE.clearOutput()));
      toolbar.addItem(clearLogButton, true);

   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderPresenter.Display#getLogButton()
    */
   @Override
   public HasClickHandlers getLogButton()
   {
      return null; //logButton;
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderPresenter.Display#addLogs(java.lang.String)
    */
   @Override
   public void addLogs(String logs)
   {
      contentPanel.add(new LogRecord(logs, odd));
      scrollPanel.scrollToBottom();
      odd = !odd;
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderPresenter.Display#clearLogs()
    */
   @Override
   public void clearLogs()
   {
      contentPanel.clear();
      scrollPanel.scrollToTop();
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderPresenter.Display#getClearLogButton()
    */
   @Override
   public HasClickHandlers getClearLogButton()
   {
      return clearLogButton;
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderPresenter.Display#getSettingsButton()
    */
   @Override
   public HasClickHandlers getSettingsButton()
   {
      return settingsButton;
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderPresenter.Display#setNextButtonText(java.lang.String)
    */
   @Override
   public void setNextButtonText(String text)
   {
      logButton.setText(text);
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderPresenter.Display#addNextButtonCommand(com.google.gwt.user.client.Command)
    */
   @Override
   public void addNextButtonCommand(Command command)
   {
      logButton.setCommand(command);
   }

}
