/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.selection;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;

/**
 * This class contains a single selected object or the bulk of selected objects.
 * Selection can contain any type of Objects and any number of them.
 * <br/>
 * Single selection can be created using {@link Selection#Selection(Object)} constructor
 * that accept one Object.
 * <br/>
 * Multiselection can be created with the help of {@link Selection#Selection(com.codenvy.ide.collections.Array)}.
 *
 * @author Nikolay Zamosenchuk
 */
public class Selection<T> {
    Array<T> elements;

    /** Creates an empty selection */
    public Selection() {
        this(Collections.<T>createArray());
    }

    /**
     * Creates SingleSelection, with only one item in it.
     *
     * @param item
     *         actual Selected object
     */
    public Selection(T item) {
        this();
        elements.add(item);
    }

    /**
     * Creates a MultiSelection, with the list of objects. <br/>
     * Please note, if list contains zero elements, Selection is considered as empty,
     * if single element placed in the list, the Selection is considered as SingleSelection.
     *
     * @param list
     */
    public Selection(Array<T> list) {
        elements = list;
    }

    /**
     * @return The first element in MultiSelection, the only element in SingleSelection
     *         and null otherwise.
     */
    public T getFirstElement() {
        return elements.isEmpty() ? null : elements.get(0);
    }

    /** @return <code>true</code> if Selection is empty. */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /** @return <code>true</code> if Selection contains only one element. */
    public boolean isSingleSelection() {
        return elements.size() == 1;
    }

    /** @return <code>true</code> if Selection contains multiple elements. */
    public boolean isMultiSelection() {
        return elements.size() > 1;
    }

    /** @return the copy of Selection. */
    public Array<T> getAll() {
        Array<T> copy = Collections.createArray();
        copy.addAll(elements);
        return copy;
    }

}
