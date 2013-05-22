/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.util.input;


import com.codenvy.ide.util.browser.UserAgent;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class KeyMapUtil {
    public static String getShortcutText(CharCodeWithModifiers shortcut) {
        String s = "";
        String acceleratorText = getKeystrokeText(shortcut);
        if (!acceleratorText.isEmpty()) {
            s = acceleratorText;
        }
        return s;
    }

    public static String getKeystrokeText(CharCodeWithModifiers accelerator) {
        if (accelerator == null) return "";
        String acceleratorText = "";
        int modifiers = accelerator.getModifiers();
        if (modifiers > 0) {
            acceleratorText = getModifiersText(modifiers);
        }

        final int code = accelerator.getCharCode();
        String keyText = String.valueOf((char)accelerator.getCharCode());


        acceleratorText += keyText;
        return acceleratorText.trim();
    }

    private static String getModifiersText(int modifiers) {
        final String keyModifiersText = getKeyModifiersText(modifiers);
        if (keyModifiersText.isEmpty()) {
            return keyModifiersText;
        } else {
            return keyModifiersText + "+";
        }
    }

    /**
     * Returns a <code>String</code> describing the modifier key(s),
     * such as "Shift", or "Ctrl+Shift".
     */
    public static String getKeyModifiersText(int modifiers) {
        StringBuilder buf = new StringBuilder();

        if ((modifiers & ModifierKeys.ACTION) != 0) {
            if (UserAgent.isMac()) {
                buf.append("Cmd");
            } else {
                buf.append("Ctrl");
            }
            buf.append("+");
        }
        if ((modifiers & ModifierKeys.CTRL) != 0) {
            buf.append("Control");
            buf.append("+");
        }
        if ((modifiers & ModifierKeys.ALT) != 0) {
            buf.append("Alt");
            buf.append("+");
        }
        if ((modifiers & ModifierKeys.SHIFT) != 0) {
            buf.append("Shift");
            buf.append("+");
        }
        if (buf.length() > 0) {
            buf.setLength(buf.length() - 1); // remove trailing '+'
        }
        return buf.toString();
    }
}
