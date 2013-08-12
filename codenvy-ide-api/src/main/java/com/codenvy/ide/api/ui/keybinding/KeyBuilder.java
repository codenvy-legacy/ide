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
package com.codenvy.ide.api.ui.keybinding;

import com.codenvy.ide.util.input.CharCodeWithModifiers;
import com.codenvy.ide.util.input.ModifierKeys;

/**
 * A builder for {@link CharCodeWithModifiers}. It's simplify creating CharCodeWithModifiers object.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class KeyBuilder {
    private int modifiers;

    private int charCode;

    public KeyBuilder() {
    }

    /**
     * Add ACTION modifier.
     * Action is abstraction for the primary modifier used for chording shortcuts
     * in IDE. To stay consistent with native OS shortcuts, this will be set
     * if CTRL is pressed on Linux or Windows, or if CMD is pressed on Mac.
     *
     * @return the KeyBuilder with action modifier added
     */
    public KeyBuilder action() {
        modifiers |= ModifierKeys.ACTION;
        return this;
    }

    /**
     * Add ALT modifier
     *
     * @return the KeyBuilder with ALT modifier added
     */
    public KeyBuilder alt() {
        modifiers |= ModifierKeys.ALT;
        return this;
    }

    /**
     * Add CTRL modifier.
     * <b>
     * This will only be set on Mac. (On Windows and Linux, the
     * {@link com.codenvy.ide.api.ui.keybinding.KeyBuilder#action()} will be set instead.)
     * </b>
     *
     * @return the KeyBuilder with CTRL modifier added
     */
    public KeyBuilder control() {
        modifiers |= ModifierKeys.CTRL;
        return this;
    }

    /**
     * Add SHIFT modifier.
     *
     * @return the KeyBuilder with SHIFT modifier added
     */
    public KeyBuilder shift() {
        modifiers |= ModifierKeys.SHIFT;
        return this;
    }

    /**
     * Key binding has no modifier keys.
     *
     * @return the KeyBuilder with NONE modifier added
     */
    public KeyBuilder none() {
        modifiers = ModifierKeys.NONE;
        return this;
    }


    /**
     * Set char code
     *
     * @param charCode
     *         the code of the character.
     * @return the KeyBuilder with char code added
     */
    public KeyBuilder charCode(int charCode) {
        this.charCode = charCode;
        return this;
    }

    /**
     * Build CharCodeWithModifiers object.
     *
     * @return new CharCodeWithModifiers object.
     */
    public CharCodeWithModifiers build() {
        return new CharCodeWithModifiers(modifiers, charCode);
    }

}
