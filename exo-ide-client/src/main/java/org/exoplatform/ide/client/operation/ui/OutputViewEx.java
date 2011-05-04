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
package org.exoplatform.ide.client.operation.ui;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OutputViewEx extends ViewImpl implements org.exoplatform.ide.client.operation.OutputPresenter.Display
{

   public static final int INITIAL_WIDTH = 450;

   public static final int INITIAL_HEIGHT = 250;

   private static OutputViewExUiBinder uiBinder = GWT.create(OutputViewExUiBinder.class);

   interface OutputViewExUiBinder extends UiBinder<Widget, OutputViewEx>
   {
   }

   public OutputViewEx()
   {
      super(ID, ViewType.OPERATION, "Output", new Image(IDEImageBundle.INSTANCE.output()),
         INITIAL_WIDTH, INITIAL_HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public void clearOutput()
   {
   }

   @Override
   public void outMessage(OutputMessage message)
   {
   }

   @Override
   public HasClickHandlers getClearOutputButton()
   {
      return null;
   }

}
