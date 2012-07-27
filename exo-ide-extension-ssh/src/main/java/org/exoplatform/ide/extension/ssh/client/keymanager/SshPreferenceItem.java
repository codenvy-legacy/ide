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
package org.exoplatform.ide.extension.ssh.client.keymanager;

import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.client.framework.preference.AbstractPreferenceItem;
import org.exoplatform.ide.client.framework.preference.PreferencePerformer;
import org.exoplatform.ide.extension.ssh.client.SshClientBundle;
import org.exoplatform.ide.extension.ssh.client.SshKeyExtension;

/**
 * SSH keys preference item.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 23, 2012 10:26:52 AM anya $
 * 
 */
public class SshPreferenceItem extends AbstractPreferenceItem
{
   private static final String NAME = SshKeyExtension.CONSTANTS.sshManagerTitle();

   public SshPreferenceItem(PreferencePerformer performer)
   {
      super(NAME, new Image(SshClientBundle.INSTANCE.sshKeyManager()), performer);
   }
}
