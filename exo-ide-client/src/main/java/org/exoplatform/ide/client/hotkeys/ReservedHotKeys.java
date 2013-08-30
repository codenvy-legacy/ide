/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.client.hotkeys;

import org.exoplatform.ide.client.IDE;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class ReservedHotKeys {

    /**
     * Map with reserved hotkeys.
     * <p/>
     * Key - combination of key for this command.
     * <p/>
     * Value - displayed title in list grid.
     */
    private static Map<String, String> hotkeys = new HashMap<String, String>();

    static {
        hotkeys.put("Ctrl+32", IDE.PREFERENCES_CONSTANT.reservedHotkyesAutocomplete()); // Ctrl+Space
        hotkeys.put("Ctrl+66", IDE.PREFERENCES_CONSTANT.reservedHotkeysBold()); // Ctrl+B
        hotkeys.put("Ctrl+73", IDE.PREFERENCES_CONSTANT.reservedHotkeysItalic()); // Ctrl+I
        hotkeys.put("Ctrl+85", IDE.PREFERENCES_CONSTANT.reservedHotkeysUndeline()); // Ctrl+U
        hotkeys.put("Ctrl+67", IDE.PREFERENCES_CONSTANT.reservedHotkeysCopy()); // Ctrl+C
        hotkeys.put("Ctrl+86", IDE.PREFERENCES_CONSTANT.reservedHotkeysPaste()); // Ctrl+V
        hotkeys.put("Ctrl+88", IDE.PREFERENCES_CONSTANT.reservedHotkeysCut()); // Ctrl+X
        hotkeys.put("Ctrl+90", IDE.PREFERENCES_CONSTANT.reservedHotkeysUndo()); // Ctrl+Z
        hotkeys.put("Ctrl+89", IDE.PREFERENCES_CONSTANT.reservedHotkeysRedo()); // Ctrl+Y
        hotkeys.put("Ctrl+65", IDE.PREFERENCES_CONSTANT.reservedHotkeysSelectAll()); // Ctrl+A
        hotkeys.put("Ctrl+36", IDE.PREFERENCES_CONSTANT.reservedHotkeysGoToStart()); // Ctrl+Home
        hotkeys.put("Ctrl+35", IDE.PREFERENCES_CONSTANT.reservedHotkeysGoToEnd()); // Ctrl+End
    }

    public static Map<String, String> getHotkeys() {
        return hotkeys;
    }

}
