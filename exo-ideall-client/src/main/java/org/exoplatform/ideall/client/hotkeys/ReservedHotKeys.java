/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.hotkeys;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class ReservedHotKeys
{

   private static Map<String, String> hotkeys = new HashMap<String, String>();
   
   static
   {
      hotkeys.put("Ctrl+32", "Autocomplete"); //Ctrl+Space
      hotkeys.put("Ctrl+66", "Bold"); //Ctrl+B
      hotkeys.put("Ctrl+73", "Italic"); //Ctrl+I
      hotkeys.put("Ctrl+85", "Undeline"); //Ctrl+U
      hotkeys.put("Ctrl+67", "Copy"); //Ctrl+C
      hotkeys.put("Ctrl+86", "Paste"); //Ctrl+V
      hotkeys.put("Ctrl+88", "Cut"); //Ctrl+X
      hotkeys.put("Ctrl+90", "Undo"); //Ctrl+Z
      hotkeys.put("Ctrl+89", "Redo"); //Ctrl+Y
      hotkeys.put("Ctrl+65", "Select All"); //Ctrl+A
      hotkeys.put("Ctrl+36", "Go to the start"); //Ctrl+Home
      hotkeys.put("Ctrl+35", "Go to the end"); //Ctrl+End      
   }

   public static Map<String, String> getHotkeys()
   {
      return hotkeys;
   }

}
