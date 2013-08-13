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
