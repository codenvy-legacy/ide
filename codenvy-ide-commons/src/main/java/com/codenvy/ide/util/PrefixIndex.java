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

package com.codenvy.ide.util;

import com.codenvy.ide.json.JsonArray;

/**
 * Interface of the index structure which supports search by the key prefix.
 *
 * @param <T>
 *         value data type
 */
public interface PrefixIndex<T> {
    /**
     * Searches values by the key prefix.
     *
     * @param prefix
     *         search key prefix
     * @return values having keys prefixed with {@code prefix}
     */
    JsonArray<? extends T> search(String prefix);
}
