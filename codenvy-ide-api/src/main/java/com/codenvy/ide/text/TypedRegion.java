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
 * Describes a region of an indexed text store such as a document or a string. The region consists of offset, length, and type.
 * The region type is defined as a string.
 * <p>
 * A typed region can, e.g., be used to described document partitions.
 * </p>
 * <p>
 * Clients may implement this interface or use the standard implementation {@link org.eclipse.TypedRegionImpl.text.TypedRegion}.
 * </p>
 */
public interface TypedRegion extends Region {

    /**
     * Returns the content type of the region.
     *
     * @return the content type of the region
     */
    String getType();
}
