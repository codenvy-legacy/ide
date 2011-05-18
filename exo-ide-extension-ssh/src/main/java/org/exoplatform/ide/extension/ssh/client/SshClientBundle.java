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

import com.google.gwt.core.client.GWT;

import com.google.gwt.resources.client.ImageResource;

import com.google.gwt.resources.client.ClientBundle;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshClientBundle May 18, 2011 9:23:17 AM evgen $
 *
 */
public interface SshClientBundle extends ClientBundle
{

   SshClientBundle INSTANCE = GWT.create(SshClientBundle.class);

   @Source("org/exoplatform/ide/extension/ssh/client/images/ssh-key-manager_Disabled.png")
   ImageResource sshKeyManagerDisabled();

   @Source("org/exoplatform/ide/extension/ssh/client/images/ssh-key-manager.png")
   ImageResource sshKeyManager();

}
