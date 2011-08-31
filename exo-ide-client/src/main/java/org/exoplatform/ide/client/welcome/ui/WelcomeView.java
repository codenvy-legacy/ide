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
package org.exoplatform.ide.client.welcome.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.welcome.WelcomePresenter;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: WelcomeView.java Aug 25, 2011 12:33:32 PM vereshchaka $
 *
 */
public class WelcomeView extends ViewImpl implements WelcomePresenter.Display
{
   
   private static final String ID = "WelcomeViewId";
   
   private static final String TITLE = "Welcome";
   
   private static WelcomeViewUiBinder uiBinder = GWT.create(WelcomeViewUiBinder.class);

   interface WelcomeViewUiBinder extends UiBinder<Widget, WelcomeView>
   {
   }
   
   @UiField
   Anchor docLink;
   
   @UiField
   Image docImage;
   
   @UiField
   Button samplesLink;
   
   @UiField
   Image samplesImage;
   
   public WelcomeView()
   {
      super(ID, "information", TITLE);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.client.welcome.WelcomePresenter.Display#getSamplesLink()
    */
   @Override
   public HasClickHandlers getSamplesLink()
   {
      return samplesLink;
   }

}
