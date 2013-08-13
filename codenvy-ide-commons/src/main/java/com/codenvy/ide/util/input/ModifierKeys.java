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

import elemental.events.KeyboardEvent;

import com.codenvy.ide.util.browser.UserAgent;


/** Modifier key constants, safe to be ORed together. */
public final class ModifierKeys {

    public static final int NONE = 0;

    /**
     * This is an abstraction for the primary modifier used for chording shortcuts
     * in Collide. To stay consistent with native OS shortcuts, this will be set
     * if CTRL is pressed on Linux or Windows, or if CMD is pressed on Mac.
     */
    public static final int ACTION = 1;

    public static final int ALT = 1 << 1;

    public static final int SHIFT = 1 << 2;

    /**
     * This will only be set on Mac. (On Windows and Linux, the
     * {@link ModifierKeys#ACTION} will be set instead.)
     */
    public static final int CTRL = 1 << 3;

    private ModifierKeys() {
        // Do nothing
    }

    /**
     * Like {@link #computeExactModifiers(KeyboardEvent)} except computes the
     * shift bit depending on {@link KeyCodeMap#needsShift(int)}.
     */
    public static int computeModifiers(SignalEvent event) {
        int modifiers = computeModifiersExceptShift(event.getMetaKey(), event.getCtrlKey(), event.getAltKey());

        // Only add shift if it isn't changing the charCode (lower to upper case).
        int keyCode = KeyCodeMap.getKeyFromEvent(event);
        if (event.getShiftKey() && !KeyCodeMap.needsShift(keyCode)) {
            modifiers |= SHIFT;
        }

        return modifiers;
    }

    /**
     * Returns an integer with the modifier bits set based on whether the modifier
     * appears in the given event. Unlike {@link #computeModifiers(SignalEvent)},
     * this does a literal translation of the shift key using
     * {@link KeyboardEvent#isShiftKey()} instead of going through our custom
     * {@link KeyCodeMap}.
     */
    public static int computeExactModifiers(KeyboardEvent event) {
        int modifiers = computeModifiersExceptShift(event.isMetaKey(), event.isCtrlKey(), event.isAltKey());
        if (event.isShiftKey()) {
            modifiers |= SHIFT;
        }

        return modifiers;
    }

    private static int computeModifiersExceptShift(boolean hasMeta, boolean hasCtrl, boolean hasAlt) {
        int modifiers = 0;

        if (hasAlt) {
            modifiers |= ALT;
        }

        if (UserAgent.isMac() && hasCtrl) {
            modifiers |= CTRL;
        }

        if (hasAction(hasCtrl, hasMeta)) {
            modifiers |= ACTION;
        }

        return modifiers;
    }

    private static boolean hasAction(boolean hasCtrl, boolean hasMeta) {
        return UserAgent.isMac() ? hasMeta : hasCtrl;
    }
}
