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

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Synchronized cache.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 * @see Cache
 */
public final class SynchronizedCache<K, V> implements Cache<K, V> {
    private final Cache<K, V> delegate;

    public SynchronizedCache(Cache<K, V> cache) {
        delegate = cache;
    }

    @Override
    public synchronized V get(K key) {
        return delegate.get(key);
    }

    @Override
    public synchronized V put(K key, V value) {
        return delegate.put(key, value);
    }

    @Override
    public synchronized V remove(K key) {
        return delegate.remove(key);
    }

    @Override
    public synchronized boolean contains(K key) {
        return delegate.contains(key);
    }

    @Override
    public synchronized void clear() {
        delegate.clear();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return delegate.iterator();
    }
}
