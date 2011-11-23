/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.operation.geturl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class GetItemURLView extends ViewImpl implements
   org.exoplatform.ide.client.operation.geturl.GetItemURLPresenter.Display
{

   private static final String ID = "ideGetItemURLForm";

   private static final int DEFAULT_WIDTH = 500;

   private static final int DEFAULT_HEIGHT = 160;

   public static final String URL_FIELD = "ideGetItemURLFormURLField";

   public static final String ID_OK_BUTTON = "ideGetItemURLFormOkButton";

   @UiField
   TextBox urlField;

   @UiField
   ImageButton okButton;

   private static final String TITLE = IDE.NAVIGATION_CONSTANT.getItemUrlTitle();

   interface GetItemURLViewUiBinder extends UiBinder<Widget, GetItemURLView>
   {
   }

   private static GetItemURLViewUiBinder uiBinder = GWT.create(GetItemURLViewUiBinder.class);

   public GetItemURLView()
   {
      super(ID, ViewType.POPUP, TITLE, new Image(IDEImageBundle.INSTANCE.url()), DEFAULT_WIDTH, DEFAULT_HEIGHT);
      setCloseOnEscape(true);

      add(uiBinder.createAndBindUi(this));
      okButton.setButtonId(ID_OK_BUTTON);
      urlField.setName(URL_FIELD);

      new Timer()
      {

         @Override
         public void run()
         {
            urlField.selectAll();
            urlField.setFocus(true);
         }

      }.schedule(500);
   }

   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   @Override
   public HasValue<String> getURLField()
   {
      return urlField;
   }

}
