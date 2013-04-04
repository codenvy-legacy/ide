/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.api.selection;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;

/**
 * This class contains a single selected object or the bulk of selected objects.
 * Selection can contain any type of Objects and any number of them.
 * <br/>
 * Sinle selection can be created using {@link Selection#Selection(Object)} constructor
 * that accept one Object.
 * <br/>
 * Multiselection can be created with the help of {@link Selection#Selection(JsonArray)}.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class Selection<T> {
    JsonArray<T> elements;

    /** Creates an empty selection */
    public Selection() {
        this(JsonCollections.<T>createArray());
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
     * if sinle element placed in the list, the Selection is considered as SingleSelection.
     *
     * @param list
     */
    public Selection(JsonArray<T> list) {
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
    public JsonArray<T> getAll() {
        JsonArray<T> copy = JsonCollections.createArray();
        copy.addAll(elements);
        return copy;
    }

}
