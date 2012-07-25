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
package org.exoplatform.ide.extension.ssh.client.keymanager.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.ssh.client.keymanager.SshKeyManagerPresenter.Display;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;

public class SshKeyManagerView extends ViewImpl implements Display
{

   private static SshKeyManagerViewUiBinder uiBinder = GWT.create(SshKeyManagerViewUiBinder.class);

   interface SshKeyManagerViewUiBinder extends UiBinder<Widget, SshKeyManagerView>
   {
   }

   @UiField
   SshKeysGrid keysGrid;

   @UiField
   ImageButton generateButton;

   @UiField
   ImageButton uploadButton;

   public SshKeyManagerView()
   {
      super(ID, ViewType.MODAL, "Ssh Keys", null, 725, 390, false);
      setCloseOnEscape(true);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.SshKeyManagerPresenter.Display#getKeyItemGrid()
    */
   @Override
   public HasSshGrid<KeyItem> getKeyItemGrid()
   {
      return keysGrid;
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.SshKeyManagerPresenter.Display#getGenerateButton()
    */
   @Override
   public HasClickHandlers getGenerateButton()
   {
      return generateButton;
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.SshKeyManagerPresenter.Display#getUploadButton()
    */
   @Override
   public HasClickHandlers getUploadButton()
   {
      return uploadButton;
   }

}
