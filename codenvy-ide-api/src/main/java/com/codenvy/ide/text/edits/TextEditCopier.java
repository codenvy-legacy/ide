/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package com.codenvy.ide.text.edits;

import java.util.*;

/**
 * Copies a tree of text edits. A text edit copier keeps a map between original and new text edits. It can be used to map a copy
 * back to its original edit.
 */
public final class TextEditCopier {

    private TextEdit fEdit;

    private Map fCopies;

    /**
     * Constructs a new <code>TextEditCopier</code> for the given edit. The actual copy is done by calling <code>
     * perform</code>.
     *
     * @param edit
     *         the edit to copy
     * @see #perform()
     */
    public TextEditCopier(TextEdit edit) {
        super();
        // Assert.isNotNull(edit);
        fEdit = edit;
        fCopies = new HashMap();
    }

    /**
     * Performs the actual copying.
     *
     * @return the copy
     */
    public TextEdit perform() {
        TextEdit result = doCopy(fEdit);
        if (result != null) {
            for (Iterator iter = fCopies.keySet().iterator(); iter.hasNext(); ) {
                TextEdit edit = (TextEdit)iter.next();
                edit.postProcessCopy(this);
            }
        }
        return result;
    }

    /**
     * Returns the copy for the original text edit.
     *
     * @param original
     *         the original for which the copy is requested
     * @return the copy of the original edit or <code>null</code> if the original isn't managed by this copier
     */
    public TextEdit getCopy(TextEdit original) {
        // Assert.isNotNull(original);
        return (TextEdit)fCopies.get(original);
    }

    // ---- helper methods --------------------------------------------

    private TextEdit doCopy(TextEdit edit) {
        TextEdit result = edit.doCopy();
        List children = edit.internalGetChildren();
        if (children != null) {
            List newChildren = new ArrayList(children.size());
            for (Iterator iter = children.iterator(); iter.hasNext(); ) {
                TextEdit childCopy = doCopy((TextEdit)iter.next());
                childCopy.internalSetParent(result);
                newChildren.add(childCopy);
            }
            result.internalSetChildren(newChildren);
        }
        addCopy(edit, result);
        return result;
    }

    private void addCopy(TextEdit original, TextEdit copy) {
        fCopies.put(original, copy);
    }
}
