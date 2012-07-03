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
package org.exoplatform.ide.extension.java.jdi.client.ui;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerExtension;
import org.exoplatform.ide.extension.java.jdi.client.ReLaunchDebuggerPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class RunDebuggerView extends ViewImpl implements ReLaunchDebuggerPresenter.Display
{
   private static final String ID = "ideRunDebuggerView";

   private static final int WIDTH = 320;

   private static final int HEIGHT = 130;

   private static RunDebuggerViewUiBinder uiBinder = GWT.create(RunDebuggerViewUiBinder.class);

   interface RunDebuggerViewUiBinder extends UiBinder<Widget, RunDebuggerView>
   {
   }

   
   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   public RunDebuggerView()
   {
      super(ID, ViewType.MODAL, DebuggerExtension.LOCALIZATION_CONSTANT.debug(), null, WIDTH, HEIGHT, false);
      
      add(uiBinder.createAndBindUi(this));
      setCanBeClosed(false);
   }

   /**
    * @see org.exoplatform.ide.extension.ReLaunchDebuggerPresenter.client.create.CreateApplicationPresenter.Display#getCloseButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

}
