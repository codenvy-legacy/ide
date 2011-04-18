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
import org.exoplatform.ide.git.client.commit.CommitEvent;

/**
 * Control for performing commit.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 31, 2011 9:21:04 AM anya $
 *
 */
public class CommitControl extends GitControl
{
   /**
    * Control ID.
    */
   public static final String ID = "Git/Commit";

   /**
    * Control's title.
    */
   public static final String TITLE = "Commit";

   /**
   * Control's prompt, when user hovers the mouse on it.
   */
   public static final String PROMPT = "Commit";

   public CommitControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setEvent(new CommitEvent());
      setImages(GitClientBundle.INSTANCE.commit(), GitClientBundle.INSTANCE.commitDisabled());
   }
}
