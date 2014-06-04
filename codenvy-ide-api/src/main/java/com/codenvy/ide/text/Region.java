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
package com.codenvy.ide.text;

/**
 * A region describes a certain range in an indexed text store. Text stores are for example documents or strings. A region is
 * defined by its offset into the text store and its length.
 * <p>
 * A region is considered a value object. Its offset and length do not change over time.
 * <p>
 * Clients may implement this interface or use the standard implementation {@link RegionImpl}.
 * </p>
 */
public interface Region {

    /**
     * Returns the length of the region.
     *
     * @return the length of the region
     */
    int getLength();

    /**
     * Returns the offset of the region.
     *
     * @return the offset of the region
     */
    int getOffset();
}
