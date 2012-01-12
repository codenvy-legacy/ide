/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.hotkeys;

import org.exoplatform.ide.client.IDE;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class ReservedHotKeys
{

   /**
    * Map with reserved hotkeys.<p/>
    * Key - combination of key for this command.<p/>
    * Value - displayed title in list grid.
    */
   private static Map<String, String> hotkeys = new HashMap<String, String>();

   static
   {
      hotkeys.put("Ctrl+32", IDE.PREFERENCES_CONSTANT.reservedHotkyesAutocomplete()); //Ctrl+Space
      hotkeys.put("Ctrl+66", IDE.PREFERENCES_CONSTANT.reservedHotkeysBold()); //Ctrl+B
      hotkeys.put("Ctrl+73", IDE.PREFERENCES_CONSTANT.reservedHotkeysItalic()); //Ctrl+I
      hotkeys.put("Ctrl+85", IDE.PREFERENCES_CONSTANT.reservedHotkeysUndeline()); //Ctrl+U
      hotkeys.put("Ctrl+67", IDE.PREFERENCES_CONSTANT.reservedHotkeysCopy()); //Ctrl+C
      hotkeys.put("Ctrl+86", IDE.PREFERENCES_CONSTANT.reservedHotkeysPaste()); //Ctrl+V
      hotkeys.put("Ctrl+88", IDE.PREFERENCES_CONSTANT.reservedHotkeysCut()); //Ctrl+X
      hotkeys.put("Ctrl+90", IDE.PREFERENCES_CONSTANT.reservedHotkeysUndo()); //Ctrl+Z
      hotkeys.put("Ctrl+89", IDE.PREFERENCES_CONSTANT.reservedHotkeysRedo()); //Ctrl+Y
      hotkeys.put("Ctrl+65", IDE.PREFERENCES_CONSTANT.reservedHotkeysSelectAll()); //Ctrl+A
      hotkeys.put("Ctrl+36", IDE.PREFERENCES_CONSTANT.reservedHotkeysGoToStart()); //Ctrl+Home
      hotkeys.put("Ctrl+35", IDE.PREFERENCES_CONSTANT.reservedHotkeysGoToEnd()); //Ctrl+End      
   }

   public static Map<String, String> getHotkeys()
   {
      return hotkeys;
   }

}
