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
package org.exoplatform.ide.client.output;

import org.exoplatform.gwtframework.ui.client.button.IconButton;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OutputViewEx extends ViewImpl implements org.exoplatform.ide.client.output.OutputPresenter.Display
{

   private static final String ID = "ideOutputView";
   
   private static final String CONTENT_ID = "ideOutputContent";

   public static final int WIDTH = 450;

   public static final int HEIGHT = 250;

   private static OutputViewExUiBinder uiBinder = GWT.create(OutputViewExUiBinder.class);
   
   private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.outputTitle();
   
   private static final String CLEAR_OUTPUT = IDE.IDE_LOCALIZATION_CONSTANT.outputClear();

   interface OutputViewExUiBinder extends UiBinder<Widget, OutputViewEx>
   {
   }

   private IconButton clearOutputButton;

   @UiField
   ScrollPanel scrollPanel;

   @UiField
   FlowPanel contentPanel;

   @UiField
   Toolbar toolbar;

   private boolean odd = true;

   public OutputViewEx()
   {
      super(ID, ViewType.OPERATION, TITLE, new Image(IDEImageBundle.INSTANCE.output()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      String normalIcon = ImageHelper.getImageHTML(Images.OutputPanel.CLEAR_OUTPUT);
      String disabledIcon = ImageHelper.getImageHTML(Images.OutputPanel.CLEAR_OUTPUT);
      clearOutputButton = new IconButton(normalIcon, disabledIcon);
      clearOutputButton.setTitle(CLEAR_OUTPUT);

      toolbar.addItem(clearOutputButton, true);
      
      contentPanel.getElement().setId(CONTENT_ID);
   }

   @Override
   public void clearOutput()
   {
      contentPanel.clear();
      scrollPanel.scrollToTop();
   }

   @Override
   public void outMessage(OutputMessage message)
   {
      OutputRecord record = new OutputRecord(message, odd);
      odd = !odd;
      contentPanel.add(record);
      scrollPanel.scrollToBottom();
   }

   @Override
   public HasClickHandlers getClearOutputButton()
   {
      return clearOutputButton;
   }

}
