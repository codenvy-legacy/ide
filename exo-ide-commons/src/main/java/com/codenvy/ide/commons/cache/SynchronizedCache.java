/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
