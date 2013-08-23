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
package org.exoplatform.ide.editor.api.codeassitant;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 */
public enum Modifier {
    STATIC(0x00000008), FINAL(0x00000010), PRIVATE(0x00000002), PUBLIC(0x00000001), PROTECTED(0x00000004), ABSTRACT(
            0x00000400), STRICTFP(0x00000800), SYNCHRONIZED(0x00000020), THREADSAFE(0), TRANSIENT(0x00000080), VOLATILE(
            0x00000040);
    private final int mod;

    Modifier(int i) {
        this.mod = i;
    }

    public int value() {
        return mod;
    }
}