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
import org.exoplatform.ide.git.client.add.AddFilesEvent;

/**
 * Control for adding changes to index (temporary storage).
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 29, 2011 4:23:20 PM anya $
 *
 */
public class AddFilesControl extends GitControl
{
   public AddFilesControl()
   {
      super(GitExtension.MESSAGES.addControlId());
      setTitle(GitExtension.MESSAGES.addControlTitle());
      setPrompt(GitExtension.MESSAGES.addControlTitle());
      setEvent(new AddFilesEvent());
      setImages(GitClientBundle.INSTANCE.addToIndex(), GitClientBundle.INSTANCE.addToIndexDisabled());
      setDelimiterBefore(true);
   }
}
