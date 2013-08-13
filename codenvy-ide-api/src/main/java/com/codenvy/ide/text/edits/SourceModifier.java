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

/**
 * A source modifier can be used to modify the source of a move or copy edit before it gets inserted at the target position. This
 * is useful if the text to be copied has to be modified before it is inserted without changing the original source.
 */
public interface SourceModifier {
    /**
     * Returns the modification to be done to the passed string in form of replace edits. The set of returned replace edits must
     * modify disjoint text regions. Violating this requirement will result in a <code>
     * BadLocationException</code> while executing the associated move or copy edit.
     * <p/>
     * The caller of this method is responsible to apply the returned edits to the passed source.
     *
     * @param source
     *         the source to be copied or moved
     * @return an array of <code>ReplaceEdits</code> describing the modifications.
     */
    public ReplaceEdit[] getModifications(String source);

    /**
     * Creates a copy of this source modifier object. The copy will be used in a different text edit object. So it should be
     * created in a way that is doesn't conflict with other text edits referring to this source modifier.
     *
     * @return the copy of the source modifier
     */
    public SourceModifier copy();
}
