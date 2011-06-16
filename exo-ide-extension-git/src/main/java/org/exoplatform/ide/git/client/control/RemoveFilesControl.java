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
package org.exoplatform.ide.git.client.control;

import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.remove.RemoveFilesEvent;

/**
 * Control is used to remove files from commit (added by add command) and work tree.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 12, 2011 3:33:56 PM anya $
 *
 */
public class RemoveFilesControl extends GitControl
{
   public RemoveFilesControl()
   {
      super(GitExtension.MESSAGES.removeControlId());
      setTitle(GitExtension.MESSAGES.removeControlTitle());
      setPrompt(GitExtension.MESSAGES.removeControlPrompt());
      setImages(GitClientBundle.INSTANCE.removeFiles(), GitClientBundle.INSTANCE.removeFilesDisabled());
      setEvent(new RemoveFilesEvent());
   }
}
