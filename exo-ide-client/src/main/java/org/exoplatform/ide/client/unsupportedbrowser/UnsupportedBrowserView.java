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
package org.exoplatform.ide.client.unsupportedbrowser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * View for showing warning about unsupported browser.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: UnsupportedBrowserView.java Oct 23, 2012 13:47:19 PM azatsarynnyy $
 *
 */
public class UnsupportedBrowserView extends ViewImpl implements UnsupportedBrowserHandler.Display
{

   public static final int HEIGHT = 300;

   public static final int WIDTH = 420;

   public static final String ID = "ideUnsupportedBrowserViewView";

   private static final String CONTINUE_BUTTON_ID = "ideUnsupportedBrowserViewViewContinueButton";

   @UiField
   ImageButton continueButton;

   interface BreakpointPropertiesViewUiBinder extends UiBinder<Widget, UnsupportedBrowserView>
   {
   }

   private static BreakpointPropertiesViewUiBinder uiBinder = GWT.create(BreakpointPropertiesViewUiBinder.class);

   public UnsupportedBrowserView()
   {
      super(ID, ViewType.MODAL, "Unsupported browser", null, WIDTH, HEIGHT, false);
      setCanBeClosed(false);
      add(uiBinder.createAndBindUi(this));

      continueButton.setButtonId(CONTINUE_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.client.unsupportedbrowser.UnsupportedBrowserPresenter.Display#getContinueButton()
    */
   @Override
   public HasClickHandlers getContinueButton()
   {
      return continueButton;
   }

}
