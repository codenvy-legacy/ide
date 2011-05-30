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
package org.exoplatform.ide.extension.ssh.client;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.ssh.client.keymanager.SshKeyManagerControl;
import org.exoplatform.ide.extension.ssh.client.keymanager.SshKeyManagerPresenter;
import org.exoplatform.ide.extension.ssh.client.keymanager.SshPublicKeyPresenter;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowPublicSshKeyEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowPublicSshKeyHandler;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshExtension May 17, 2011 5:00:33 PM evgen $
 *
 */
public class SshKeyExtension extends Extension implements InitializeServicesHandler, ShowPublicSshKeyHandler
{

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.getInstance().addControl(new SshKeyManagerControl(), DockTarget.NONE, false);
      new SshKeyManagerPresenter();
      IDE.EVENT_BUS.addHandler(InitializeServicesEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ShowPublicSshKeyEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      new SshKeyService(event.getApplicationConfiguration().getContext(), event.getApplicationConfiguration().getHttpsPort(), event.getLoader());
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowPublicSshKeyHandler#onShowPublicSshKey(org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowPublicSshKeyEvent)
    */
   @Override
   public void onShowPublicSshKey(ShowPublicSshKeyEvent event)
   {
      new SshPublicKeyPresenter(event.getKeyItem());
   }

}
