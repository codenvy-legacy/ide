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
import org.exoplatform.ide.git.client.merge.MergeEvent;

/**
 * Control for merging branch with Head commit.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 20, 2011 12:31:07 PM anya $
 *
 */
public class MergeControl extends GitControl
{
   public MergeControl()
   {
      super(GitExtension.MESSAGES.mergeControlId());
      setTitle(GitExtension.MESSAGES.mergeControlTitle());
      setPrompt(GitExtension.MESSAGES.mergeControlPrompt());
      setEvent(new MergeEvent());
      setImages(GitClientBundle.INSTANCE.merge(), GitClientBundle.INSTANCE.mergeDisabled());
   }
}
