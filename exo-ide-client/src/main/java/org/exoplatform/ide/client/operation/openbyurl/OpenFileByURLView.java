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
package org.exoplatform.ide.client.operation.openbyurl;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * View for opening file by URL.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OpenFileByURLView extends ViewImpl implements
   org.exoplatform.ide.client.operation.openbyurl.OpenFileByURLPresenter.Display
{

   /**
    * View ID.
    */
   public static final String ID = "ide.openFileByURL.view";

   /**
    * Initial width of this view
    */
   private static final int WIDTH = 550;

   /**
    * Initial height of this view
    */
   private static final int HEIGHT = 170;

   private static OpenFileByURLViewUiBinder uiBinder = GWT.create(OpenFileByURLViewUiBinder.class);

   interface OpenFileByURLViewUiBinder extends UiBinder<Widget, OpenFileByURLView>
   {
   }

   private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.openFileByURLViewTitle();

   /**
    * URL text field.
    */
   @UiField
   TextInput urlField;

   /**
    * Open button.
    */
   @UiField
   ImageButton openButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   /**
    * Creates view instance.
    */
   public OpenFileByURLView()
   {
      super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.url()), WIDTH, HEIGHT);
      setCloseOnEscape(true);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.client.remote.OpenFileByURLPresenter.Display#getURLField()
    */
   @Override
   public TextFieldItem getURLField()
   {
      return urlField;
   }

   /**
    * @see org.exoplatform.ide.client.remote.OpenFileByURLPresenter.Display#getOpenButton()
    */
   @Override
   public HasClickHandlers getOpenButton()
   {
      return openButton;
   }

   /**
    * @see org.exoplatform.ide.client.remote.OpenFileByURLPresenter.Display#setOpenButtonEnabled(boolean)
    */
   @Override
   public void setOpenButtonEnabled(boolean enabled)
   {
      openButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.client.remote.OpenFileByURLPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

}
