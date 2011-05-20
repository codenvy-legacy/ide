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

import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextAreaItem;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.ssh.client.keymanager.SshPublicKeyPresenter.Display;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: SshPublicKeyView May 19, 2011 12:40:06 PM evgen $
 *
 */
public class SshPublicKeyView extends ViewImpl implements Display
{

   private static SshPublicKeyViewUiBinder uiBinder = GWT.create(SshPublicKeyViewUiBinder.class);

   interface SshPublicKeyViewUiBinder extends UiBinder<Widget, SshPublicKeyView>
   {
   }
   
   @UiField
   TextAreaItem publicSshKeyField;
   
   @UiField
   ImageButton closeButton;

   public SshPublicKeyView()
   {
      super(ID, ViewType.MODAL, "Public Ssh Key: ", null, 400, 300);
      add(uiBinder.createAndBindUi(this));      
      UIHelper.setAsReadOnly("exoSshPublicKeyField");
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.SshPublicKeyPresenter.Display#getCloseButton()
    */
   @Override
   public HasClickHandlers getCloseButton()
   {
      return closeButton;
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.SshPublicKeyPresenter.Display#getKeyField()
    */
   @Override
   public HasValue<String> getKeyField()
   {
      return publicSshKeyField;
   }

}
