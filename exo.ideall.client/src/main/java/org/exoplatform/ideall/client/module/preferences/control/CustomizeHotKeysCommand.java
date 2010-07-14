/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.module.preferences.control;

import org.exoplatform.ideall.client.IDEImageBundle;
import org.exoplatform.ideall.client.framework.control.IDEControl;
import org.exoplatform.ideall.client.hotkeys.event.CustomizeHotKeysEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CustomizeHotKeysCommand extends IDEControl
{

   public static final String ID = "Window/HotKeys...";

   public static final String TITLE = "Customize Hotkeys...";

   public CustomizeHotKeysCommand()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(TITLE);
      setEnabled(true);
      setVisible(true);
      setImages(IDEImageBundle.INSTANCE.customizeHotKeys(), IDEImageBundle.INSTANCE.customizeHotKeysDisabled());
      setEvent(new CustomizeHotKeysEvent());
   }

}
