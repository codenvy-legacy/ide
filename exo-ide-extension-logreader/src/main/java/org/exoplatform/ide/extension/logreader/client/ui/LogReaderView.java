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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.button.IconButton;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.gwtframework.ui.client.component.VPanel;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.logreader.client.LogReaderClientBundle;
import org.exoplatform.ide.extension.logreader.client.LogReaderExtension;
import org.exoplatform.ide.extension.logreader.client.LogReaderPresenter;

import java.util.logging.LogRecord;

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
   Element content;

   @UiField
   Toolbar toolbar;

   @UiField
   VPanel basePanel;

   private IconButton nextLogButton;

   private IconButton prevLogButton;

   private IconButton refreshLogButton;

   public LogReaderView()
   {
      super(ID, ViewType.OPERATION, "Log Reader", new Image(LogReaderClientBundle.INSTANCE.logReader()));
      add(uiBinder.createAndBindUi(this));

      prevLogButton =
         new IconButton(new Image(LogReaderClientBundle.INSTANCE.prev()), new Image(
            LogReaderClientBundle.INSTANCE.prev_Disabled()));

      prevLogButton.setTitle(LogReaderExtension.MESSAGES.getPrevLogButton());

      toolbar.addItem(prevLogButton);
      toolbar.addDelimiter();

      refreshLogButton =
         new IconButton(new Image(LogReaderClientBundle.INSTANCE.refresh()), new Image(
            LogReaderClientBundle.INSTANCE.refresh_Disabled()));

      refreshLogButton.setTitle(LogReaderExtension.MESSAGES.getRefreshLogButton());
      toolbar.addItem(refreshLogButton);
      toolbar.addDelimiter();

      nextLogButton =
         new IconButton(new Image(LogReaderClientBundle.INSTANCE.next()), new Image(
            LogReaderClientBundle.INSTANCE.next_Disabled()));

      nextLogButton.setTitle(LogReaderExtension.MESSAGES.getNextLogButton());
      toolbar.addItem(nextLogButton);

   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderPresenter.Display#addLog(java.lang.String)
    */
   @Override
   public void addLog(String logContent)
   {
      content.setInnerText(logContent);
      scrollPanel.scrollToTop();
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderPresenter.Display#setPrevLogButtonEnabled(boolean)
    */
   @Override
   public void setPrevLogButtonEnabled(boolean enabled)
   {
      prevLogButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderPresenter.Display#getNexLogButton()
    */
   @Override
   public HasClickHandlers getNexLogButton()
   {
      return nextLogButton;
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderPresenter.Display#getPrevLogButton()
    */
   @Override
   public HasClickHandlers getPrevLogButton()
   {
      return prevLogButton;
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderPresenter.Display#getRefreshLogButton()
    */
   @Override
   public HasClickHandlers getRefreshLogButton()
   {
      return refreshLogButton;
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.LogReaderPresenter.Display#setNextLogButtonEnabled(boolean)
    */
   @Override
   public void setNextLogButtonEnabled(boolean enabled)
   {
      nextLogButton.setEnabled(enabled);
   }

}
