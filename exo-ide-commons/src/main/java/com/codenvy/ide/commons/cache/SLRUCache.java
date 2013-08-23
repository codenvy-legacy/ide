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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Segmented LRU cache. See for details <a href="http://en.wikipedia.org/wiki/Cache_algorithms#Segmented_LRU">Segmented LRU cache</a>
 * <p/>
 * Implementation is not threadsafe. In need concurrent access use {@link SynchronizedCache}
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SLRUCache<K, V> implements Cache<K, V>, Iterable<Entry<K, V>> {
    private final Map<K, V> protectedSegment;
    private final Map<K, V> probationarySegment;
    private final int       protectedSize;
    private final int       probationarySize;

    private int             misses;
    private int             protectedHits;
    private int             probationaryHits;

    /**
     * @param protectedSize size of protected area.
     * @param probationarySize size of probationary area.
     */
    public SLRUCache(int protectedSize, int probationarySize) {
        this.protectedSize = protectedSize;
        this.probationarySize = probationarySize;
        protectedSegment = new LinkedHashMap<K, V>(SLRUCache.this.protectedSize + 1, 1.1f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                if (size() > SLRUCache.this.protectedSize) {
                    probationarySegment.put(eldest.getKey(), eldest.getValue());
                    return true;
                }
                return false;
            }
        };
        probationarySegment = new LinkedHashMap<K, V>(SLRUCache.this.probationarySize + 1, 1.1f, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > SLRUCache.this.probationarySize;
            }
        };
    }

    @Override
    public V get(K key) {
        V value = protectedSegment.get(key);
        if (value != null) {
            protectedHits++;
            return value;
        }
        value = probationarySegment.remove(key);
        if (value == null) {
            misses++;
            return null;
        }
        probationaryHits++;
        protectedSegment.put(key, value);
        return value;
    }

    @Override
    public V put(K key, V value) {
        V oldValueProtected = protectedSegment.remove(key);
        V oldValueProbationary = probationarySegment.put(key, value);
        V oldValue = oldValueProtected == null ? oldValueProbationary : oldValueProtected;
        if (oldValue != null) {
            evict(key, oldValue);
        }
        return oldValue;
    }

    @Override
    public V remove(K key) {
        V oldValue = protectedSegment.remove(key);
        if (oldValue == null) {
            oldValue = probationarySegment.remove(key);
        }
        if (oldValue != null) {
            evict(key, oldValue);
        }
        return oldValue;
    }

    @Override
    public boolean contains(K key) {
        return probationarySegment.containsKey(key) || protectedSegment.containsKey(key);
    }

    @Override
    public void clear() {
        Set<Map.Entry<K, V>> entries = protectedSegment.entrySet();
        for (Map.Entry<K, V> entry : entries) {
            evict(entry.getKey(), entry.getValue());
        }
        entries = probationarySegment.entrySet();
        for (Map.Entry<K, V> entry : entries) {
            evict(entry.getKey(), entry.getValue());
        }
        protectedSegment.clear();
        probationarySegment.clear();
    }

    /**
     * Should be called when remove value from cache. Typically this method should be called from methods {@link #put(Object, Object)},
     * {@link #remove(Object)} and {@link #clear()}. Example:
     * 
     * <pre>
     *    class MyCache&lt;K, V&gt; implements Cache&lt;K, V&gt; {
     *       ...
     *       public V put(K key, V value) {
     *          V previous = ... // do remove old value
     *          if (previous != null) {
     *             evict(key, previous);
     *          }
     *          return previous;
     *       }
     *       ...
     *    }
     * </pre>
     * 
     * @param key key
     * @param value evicted value
     */
    protected void evict(K key, V value) {
        // nothing by default
    }

    @Override
    public int size() {
        return protectedSegment.size() + probationarySegment.size();
    }

    public void printStats() {
        System.out.println("-------------------------------------------");
        System.out.printf("misses:            %d\n", misses);
        System.out.printf("protected hits:    %d\n", protectedHits);
        System.out.printf("probationary hits: %d\n", probationaryHits);
        System.out.println("-------------------------------------------");
    }

    @SuppressWarnings("unchecked")
    public Iterator<Entry<K, V>> iterator() {
        return new CompoundIterator<Entry<K, V>>(protectedSegment.entrySet().iterator(), probationarySegment.entrySet().iterator());
    }
}
