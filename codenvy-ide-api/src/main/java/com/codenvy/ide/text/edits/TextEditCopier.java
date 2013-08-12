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
