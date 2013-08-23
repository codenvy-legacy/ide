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
package com.codenvy.ide.commons.cache;

/**
 * SLRUCache that loads value for key if it is not cached yet.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 * @see SLRUCache
 */
public abstract class LoadingValueSLRUCache<K, V> extends SLRUCache<K, V> {
    /**
     * @param protectedSize
     *         size of protected area.
     * @param probationarySize
     *         size of probationary area.
     */
    public LoadingValueSLRUCache(int protectedSize, int probationarySize) {
        super(protectedSize, probationarySize);
    }

    @Override
    public V get(K key) {
        V value = super.get(key);
        if (value != null) {
            return value;
        }
        value = loadValue(key);
        put(key, value);
        return value;
    }

    /**
     * Load value in implementation specific way.
     *
     * @param key
     *         key
     * @return value
     * @throws RuntimeException
     *         if failed to load value
     */
    protected abstract V loadValue(K key) throws RuntimeException;
}
