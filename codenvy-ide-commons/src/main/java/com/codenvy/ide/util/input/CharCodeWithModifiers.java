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

import com.codenvy.ide.runtime.Assert;

/**
 * Bean that holds information describing the matching key-press.
 * <p/>
 * <p>NOTE: Do not include {@link ModifierKeys#SHIFT} for upper case characters
 * (A,%,?), only for combinations like SHIFT+TAB.
 */
public class CharCodeWithModifiers {

    private final int modifiers;

    private final int charCode;

    private final int digest;

    public CharCodeWithModifiers(int modifiers, int charCode) {
        Assert.isTrue(!KeyCodeMap.needsShift(charCode) || (modifiers & ModifierKeys.SHIFT) == 0,
                      "Do not include ModifierKeys.SHIFT for EventShortcuts where the " +
                      "key pressed could be modified by pressing shift.");
        this.modifiers = modifiers;
        this.charCode = charCode;
        this.digest = computeKeyDigest(modifiers, charCode);
    }

    public int getModifiers() {
        return modifiers;
    }

    public int getCharCode() {
        return charCode;
    }

    public int getKeyDigest() {
        return digest;
    }

    public static int computeKeyDigest(int modifiers, int charCode) {
        return (modifiers << 16) | (0xFFFF & charCode);
    }

    /**
     * Returns an integer representing the combination of pressed modifier keys
     * and the current text key.
     *
     * @see ModifierKeys#ACTION for details on the action key abstraction
     */
    public static int computeKeyDigest(SignalEvent event) {
        return computeKeyDigest(ModifierKeys.computeModifiers(event), KeyCodeMap.getKeyFromEvent(event));
    }
}
