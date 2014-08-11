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

import com.codenvy.api.project.shared.dto.ItemReference;
import com.google.inject.Singleton;

/**
 * Returns {@link ItemReference} if item node is selected (in Project Explorer).
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ItemReferenceSelectionProvider implements SelectionProvider {

    @Override
    public <T> T get(Object o, SelectionKey<T> key) {
        if (o instanceof ItemReference) {
            if (CoreSelectionTypes.ITEM_REFERENCE.getName().equals(key.getName())) {
                return (T)o;
            }
        }
        return null;
    }
}
