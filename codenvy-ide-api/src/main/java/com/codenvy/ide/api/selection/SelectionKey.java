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

/**
 * Identifier for data items which can be returned from {@link SelectionAgent#getSelection(SelectionKey)}.
 *
 * @param <T>
 *         type of data item
 * @author Artem Zatsarynnyy
 */
public class SelectionKey<T> {
    private final String name;

    public SelectionKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
