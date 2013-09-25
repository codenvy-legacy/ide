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
package com.codenvy.ide.util.input;


import com.codenvy.ide.util.StringUtils;
import com.codenvy.ide.util.browser.UserAgent;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class KeyMapUtil {
    public static String getShortcutText(CharCodeWithModifiers shortcut) {
        String s = null;
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
        final int code = accelerator.getCharCode();
        String keyText = String.valueOf((char)accelerator.getCharCode());
        if (StringUtils.isUpperCase((char)accelerator.getCharCode())) {
            modifiers |= ModifierKeys.SHIFT;
        }
        keyText = keyText.toUpperCase();
        if (modifiers > 0) {
            acceleratorText = getModifiersText(modifiers);
        }


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
