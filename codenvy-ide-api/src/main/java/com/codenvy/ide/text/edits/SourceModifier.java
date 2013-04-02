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
