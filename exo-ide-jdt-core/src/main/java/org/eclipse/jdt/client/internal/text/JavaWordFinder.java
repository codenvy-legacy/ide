/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.client.internal.text;

import org.eclipse.jdt.client.core.compiler.CharOperation;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IRegion;
import org.exoplatform.ide.editor.shared.text.Region;

public class JavaWordFinder {

    private static final int SURROGATE_BITMASK = 0xFFFFF800;

    private static final int SURROGATE_BITS = 0xD800;

    public static IRegion findWord(IDocument document, int offset) {

        int start = -2;
        int end = -1;

        try {
            int pos = offset;
            char c;

            while (pos >= 0) {
                c = document.getChar(pos);
                if (!CharOperation.isJavaIdentifierPart(c)) {
                    // Check for surrogates
                    if (isSurrogate(c)) {
                  /*
                   * XXX: Here we should create the code point and test whether
                   * it is a Java identifier part. Currently this is not possible
                   * because java.lang.Character in 1.4 does not support surrogates
                   * and because com.ibm.icu.lang.UCharacter.isJavaIdentifierPart(int)
                   * is not correctly implemented.
                   */
                    } else {
                        break;
                    }
                }
                --pos;
            }
            start = pos;

            pos = offset;
            int length = document.getLength();

            while (pos < length) {
                c = document.getChar(pos);
                if (!CharOperation.isJavaIdentifierPart(c)) {
                    if (isSurrogate(c)) {
                  /*
                   * XXX: Here we should create the code point and test whether
                   * it is a Java identifier part. Currently this is not possible
                   * because java.lang.Character in 1.4 does not support surrogates
                   * and because com.ibm.icu.lang.UCharacter.isJavaIdentifierPart(int)
                   * is not correctly implemented.
                   */
                    } else {
                        break;
                    }

                }
                ++pos;
            }
            end = pos;

        } catch (BadLocationException x) {
        }

        if (start >= -1 && end > -1) {
            if (start == offset && end == offset)
                return new Region(offset, 0);
            else if (start == offset)
                return new Region(start, end - start);
            else
                return new Region(start + 1, end - start - 1);
        }

        return null;
    }

    public static boolean isSurrogate(char char16) {
        return (char16 & SURROGATE_BITMASK) == SURROGATE_BITS;
    }
}
