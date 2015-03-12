/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.api.selection;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.collections.Collections;

/**
 * This class contains a single selected object or the bulk of selected objects.
 * Selection can contain any type of Objects and any number of them.
 * <br/>
 * Single selection can be created using {@link Selection#Selection(Object)} constructor
 * that accept one Object.
 * <br/>
 * Multiselection can be created with the help of {@link Selection#Selection(List)}.
 *
 * @author Nikolay Zamosenchuk
 */
public class Selection<T> {
    /**
     * The selection.
     */
    private final List<T> elements;

    /**
     * The head of the selection.
     */
    private final T head;

    /** Creates an empty selection */
    public Selection() {
        this.elements = java.util.Collections.emptyList();
        this.head = null;
    }

    /**
     * Creates SingleSelection, with only one item in it.
     *
     * @param item
     *         actual Selected object
     */
    public Selection(final T item) {
        if (item == null) {
            this.elements = java.util.Collections.emptyList();
            this.head = null;
        } else {
            this.elements = java.util.Collections.singletonList(item);
            this.head = item;
        }
    }

    /**
     * Creates a MultiSelection, with the list of objects. <br/>
     * Please note, if list contains zero elements, Selection is considered as empty,
     * if single element placed in the list, the Selection is considered as SingleSelection.
     *
     * @param list
     */
    @Deprecated
    public Selection(final Array<T> list) {
        if (list == null || list.isEmpty()) {
            this.elements = java.util.Collections.emptyList();
            this.head = null;
        } else {
            this.elements = new ArrayList<>();
            for (final T item : list.asIterable()) {
                this.elements.add(item);
            }
            this.head = this.elements.get(0);
        }
    }

    public Selection(final List<T> list) {
        if (list == null || list.isEmpty()) {
            this.elements = java.util.Collections.emptyList();
            this.head = null;
        } else {
            this.elements = list;
            this.head = this.elements.get(0);
        }
    }

    public Selection(final List<T> list, @Nonnull final T head) {
        this.elements = list;
        this.head = head;
    }

    public T getHeadElement() {
        return this.head;
    }

    /**
     * Returns the first element of the selection.
     * 
     * @return the first element of the selection
     * @deprecated use {@link #getHeadElement()}
     */
    @Deprecated
    public T getFirstElement() {
        return getHeadElement();
    }

    /**
     * Tells if the selection is empty.
     * 
     * @return <code>true</code> if Selection is empty
     */
    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    /** @return <code>true</code> if Selection contains only one element. */
    public boolean isSingleSelection() {
        return this.elements.size() == 1;
    }

    public boolean isMultiSelection() {
        return this.elements.size() > 1;
    }

    /**
     * Returns all the selected elements.
     * 
     * @return all the selected elements.
     * @deprecated use {@link #getAllElements()}
     */
    @Deprecated
    public Array<T> getAll() {
        final Array<T> copy = Collections.createArray();
        for (final T item : this.elements) {
            copy.add(item);
        }
        return copy;
    }

    /**
     * Returns all the selected elements.
     * 
     * @return all the selected elements.
     */
    public List<T> getAllElements() {
        return new ArrayList<>(this.elements);
    }
}
