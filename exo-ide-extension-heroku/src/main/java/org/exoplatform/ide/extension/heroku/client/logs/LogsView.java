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
package org.exoplatform.ide.extension.heroku.client.logs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.IconButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.heroku.client.HerokuClientBundle;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;


/**
 * View for displaying application's logs.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Sep 21, 2011 10:09:19 AM anya $
 *
 */
public class LogsView extends ViewImpl implements LogsPresenter.Display
{

   private static LogsViewUiBinder uiBinder = GWT.create(LogsViewUiBinder.class);

   interface LogsViewUiBinder extends UiBinder<Widget, LogsView>
   {
   }

   private static final String ID = "ideLogsView";

   private static final String GET_LOGS_BUTTON_ID = "ideLogsViewGetLogsButton";

   private static final String LOG_LINES_FIELD_ID = "ideLogsViewLogLinesField";

   @UiField
   ScrollPanel scrollPanel;

   @UiField
   Element content;

   @UiField
   Toolbar toolbar;

   @UiField
   FlowPanel basePanel;

   /**
    * Field for max number of logs to be shown.
    */
   @UiField
   TextField logLinesField;

   /**
    * Get logs button.
    */
   private IconButton getLogButton;

   public LogsView()
   {
      super(ID, ViewType.OPERATION, HerokuExtension.LOCALIZATION_CONSTANT.logsViewTitle(), new Image(
         HerokuClientBundle.INSTANCE.logs()));
      add(uiBinder.createAndBindUi(this));

      getLogButton =
         new IconButton(new Image(HerokuClientBundle.INSTANCE.getLogs()), new Image(
            HerokuClientBundle.INSTANCE.getLogsDisabled()));
      getLogButton.setTitle(HerokuExtension.LOCALIZATION_CONSTANT.logsViewGetLogsButton());
      toolbar.addItem(getLogButton);
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
    * @see org.exoplatform.ide.extension.heroku.client.logs.LogsPresenter.Display#getShowLogButton()
    */
   @Override
   public HasClickHandlers getShowLogButton()
   {
      return getLogButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.logs.LogsPresenter.Display#getLogLinesCount()
    */
   @Override
   public TextFieldItem getLogLinesCount()
   {
      return logLinesField;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.logs.LogsPresenter.Display#enableShowLogButton(boolean)
    */
   @Override
   public void enableShowLogButton(boolean enable)
   {
      getLogButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.logs.LogsPresenter.Display#focusInLogLinesField()
    */
   @Override
   public void focusInLogLinesField()
   {
      logLinesField.focusInItem();
   }
}
