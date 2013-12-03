/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.codeassistant;

import com.codenvy.ide.ext.java.worker.Preferences;
import com.codenvy.ide.ext.java.jdt.core.Flags;
import com.codenvy.ide.ext.java.jdt.core.IType;

import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.runtime.CoreException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;

import java.util.*;
import java.util.Map.Entry;

/** An LRU cache for code assist. */
public final class ContentAssistHistory {
    /** Persistence implementation. */
    private static final class ReaderWriter {

        private static final String NODE_ROOT = "history"; //$NON-NLS-1$

        //      private static final String NODE_LHS = "lhs"; //$NON-NLS-1$
        //
        //      private static final String NODE_RHS = "rhs"; //$NON-NLS-1$
        //
        //      private static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$

        private static final String ATTRIBUTE_MAX_LHS = "maxLHS"; //$NON-NLS-1$

        private static final String ATTRIBUTE_MAX_RHS = "maxRHS"; //$NON-NLS-1$

        public void store(ContentAssistHistory history, StringBuilder result) {

            JSONObject root = new JSONObject();
            root.put(ATTRIBUTE_MAX_LHS, new JSONString(Integer.toString(history.fMaxLHS)));
            root.put(ATTRIBUTE_MAX_RHS, new JSONString(Integer.toString(history.fMaxRHS)));

            JSONObject historyJs = new JSONObject();

            for (Iterator<String> leftHandSides = history.fLHSCache.keySet().iterator(); leftHandSides.hasNext(); ) {
                String lhs = leftHandSides.next();
                JSONArray arr = new JSONArray();
                MRUSet<String> rightHandSides = history.fLHSCache.get(lhs);
                int i = 0;
                for (Iterator<String> rhsIterator = rightHandSides.iterator(); rhsIterator.hasNext(); ) {
                    String rhs = rhsIterator.next();
                    arr.set(i, new JSONString(rhs));
                    i++;
                }
                historyJs.put(lhs, arr);
            }
            root.put(NODE_ROOT, historyJs);

            result.append(root.toString());

        }

        public ContentAssistHistory load(String source) {

            JSONObject root = JSONParser.parseLenient(source).isObject();

            int maxLHS = parseNaturalInt(root.get(ATTRIBUTE_MAX_LHS).isString().stringValue(), DEFAULT_TRACKED_LHS);
            int maxRHS = parseNaturalInt(root.get(ATTRIBUTE_MAX_RHS).isString().stringValue(), DEFAULT_TRACKED_RHS);

            ContentAssistHistory history = new ContentAssistHistory(maxLHS, maxRHS);

            JSONObject list = root.get(NODE_ROOT).isObject();
            for (String lhs : list.keySet()) {
                Set<String> cache = history.getCache(lhs);
                JSONArray arr = list.get(lhs).isArray();
                for (int i = 0; i < arr.size(); i++) {
                    cache.add(arr.get(i).isString().stringValue());
                }
            }
            return history;
        }

        private int parseNaturalInt(String attribute, int defaultValue) {
            try {
                int integer = Integer.parseInt(attribute);
                if (integer > 0)
                    return integer;
                return defaultValue;
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
    }

    /**
     * Most recently used variant with capped size that only counts {@linkplain #put(Object, Object) put} as access. This is
     * implemented by always removing an element before it gets put back.
     *
     * @since 3.2
     */
    private static final class MRUMap<K, V> extends LinkedHashMap<K, V> {
        private static final long serialVersionUID = 1L;

        private final int fMaxSize;

        /**
         * Creates a new <code>MRUMap</code> with the given size.
         *
         * @param maxSize
         *         the maximum size of the cache, must be &gt; 0
         */
        public MRUMap(int maxSize) {
            Assert.isLegal(maxSize > 0);
            fMaxSize = maxSize;
        }

        /*
         * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
         */
        @Override
        public V put(K key, V value) {
            V object = remove(key);
            super.put(key, value);
            return object;
        }

        /*
         * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
         */
        @Override
        protected boolean removeEldestEntry(Entry<K, V> eldest) {
            return size() > fMaxSize;
        }
    }

    /**
     * Most recently used variant with capped size that orders the elements by addition. This is implemented by always removing an
     * element before it gets added back.
     *
     * @since 3.2
     */
    private static final class MRUSet<E> extends LinkedHashSet<E> {
        private static final long serialVersionUID = 1L;

        private final int fMaxSize;

        /**
         * Creates a new <code>MRUSet</code> with the given size.
         *
         * @param maxSize
         *         the maximum size of the cache, must be &gt; 0
         */
        public MRUSet(int maxSize) {
            Assert.isLegal(maxSize > 0);
            fMaxSize = maxSize;
        }

        /*
         * @see java.util.HashSet#add(java.lang.Object)
         */
        @Override
        public boolean add(E o) {
            if (remove(o)) {
                super.add(o);
                return false;
            }

            if (size() >= fMaxSize)
                remove(this.iterator().next());

            super.add(o);
            return true;
        }
    }

    /**
     * A ranking of the most recently selected types.
     *
     * @since 3.2
     */
    public static final class RHSHistory {
        private final LinkedHashMap<String, Integer> fHistory;

        private List<String> fList;

        RHSHistory(LinkedHashMap<String, Integer> history) {
            fHistory = history;
        }

        /**
         * Returns the rank of a type in the history in [0.0,&nbsp;1.0]. The rank of the most recently selected type is 1.0, the
         * rank of any type that is not remembered is zero.
         *
         * @param type
         *         the fully qualified type name to get the rank for
         * @return the rank of <code>type</code>
         */
        public float getRank(String type) {
            if (fHistory == null)
                return 0.0F;
            Integer integer = fHistory.get(type);
            return integer == null ? 0.0F : integer.floatValue() / fHistory.size();
        }

        /**
         * Returns the size of the history.
         *
         * @return the size of the history
         */
        public int size() {
            return fHistory == null ? 0 : fHistory.size();
        }

        /**
         * Returns the list of remembered types ordered by recency. The first element is the <i>least</i>, the last element the
         * <i>most</i> recently remembered type.
         *
         * @return the list of remembered types as fully qualified type names
         */
        public List<String> getTypes() {
            if (fHistory == null)
                return Collections.emptyList();
            if (fList == null) {
                fList = Collections.unmodifiableList(new ArrayList<String>(fHistory.keySet()));
            }
            return fList;
        }
    }

    private static final RHSHistory EMPTY_HISTORY = new RHSHistory(null);

    private static final int DEFAULT_TRACKED_LHS = 100;

    private static final int DEFAULT_TRACKED_RHS = 10;

    private static final Set<String> UNCACHEABLE;

    static {
        Set<String> uncacheable = new HashSet<String>();
        uncacheable.add("java.lang.Object"); //$NON-NLS-1$
        uncacheable.add("java.lang.Comparable"); //$NON-NLS-1$
        uncacheable.add("java.io.Serializable"); //$NON-NLS-1$
        uncacheable.add("java.io.Externalizable"); //$NON-NLS-1$
        UNCACHEABLE = Collections.unmodifiableSet(uncacheable);
    }

    private final LinkedHashMap<String, MRUSet<String>> fLHSCache;

    private final int fMaxLHS;

    private final int fMaxRHS;

    /**
     * Creates a new history.
     *
     * @param maxLHS
     *         the maximum number of tracked left hand sides (&gt; 0)
     * @param maxRHS
     *         the maximum number of tracked right hand sides per left hand side(&gt; 0)
     */
    public ContentAssistHistory(int maxLHS, int maxRHS) {
        Assert.isLegal(maxLHS > 0);
        Assert.isLegal(maxRHS > 0);
        fMaxLHS = maxLHS;
        fMaxRHS = maxRHS;
        fLHSCache = new MRUMap<String, MRUSet<String>>(fMaxLHS);
    }

    /** Creates a new history, equivalent to <code>ContentAssistHistory(DEFAULT_TRACKED_LHS, DEFAULT_TRACKED_RHS})</code>. */
    public ContentAssistHistory() {
        this(DEFAULT_TRACKED_LHS, DEFAULT_TRACKED_RHS);
    }

    /**
     * Remembers the selection of a right hand side type (proposal type) for a certain left hand side (expected type) in content
     * assist.
     *
     * @param lhs
     *         the left hand side / expected type
     * @param rhs
     *         the selected right hand side
     */
    public void remember(IType lhs, IType rhs) {
        Assert.isLegal(lhs != null);
        Assert.isLegal(rhs != null);

        if (!isCacheableRHS(rhs))
            return;
        //      ITypeHierarchy hierarchy = rhs.newSupertypeHierarchy(getProgressMonitor());
        //      if (hierarchy.contains(lhs))
        //      {
        //         // TODO remember for every member of the LHS hierarchy or not? Yes for now.
        //         IGenericType[] allLHSides = hierarchy.getAllSupertypes(lhs);
        String rhsQualifiedName = rhs.getFullyQualifiedName();
        //         for (int i = 0; i < allLHSides.length; i++)
        //            rememberInternal(allLHSides[i], rhsQualifiedName);
        rememberInternal(lhs, rhsQualifiedName);
        //      }

    }

    /**
     * Returns the {@link RHSHistory history} of the types that have been selected most recently as right hand sides for the given
     * type.
     *
     * @param lhs
     *         the fully qualified type name of an expected type for which right hand sides are requested, or <code>null</code>
     * @return the right hand side history for the given type
     */
    public RHSHistory getHistory(String lhs) {
        MRUSet<String> rhsCache = fLHSCache.get(lhs);
        if (rhsCache != null) {
            int count = rhsCache.size();
            LinkedHashMap<String, Integer> history = new LinkedHashMap<String, Integer>((int)(count / 0.75));
            int rank = 1;
            for (Iterator<String> it = rhsCache.iterator(); it.hasNext(); rank++) {
                String type = it.next();
                history.put(type, new Integer(rank));
            }
            return new RHSHistory(history);
        }
        return EMPTY_HISTORY;
    }

    /**
     * Returns a read-only map from {@link IType} to {@link RHSHistory}, where each value is the history for the key type (see
     * {@link #getHistory(String)}.
     *
     * @return the set of remembered right hand sides ordered by least recent selection
     */
    public Map<String, RHSHistory> getEntireHistory() {
        HashMap<String, RHSHistory> map = new HashMap<String, RHSHistory>((int)(fLHSCache.size() / 0.75));
        for (Iterator<Entry<String, MRUSet<String>>> it = fLHSCache.entrySet().iterator(); it.hasNext(); ) {
            Entry<String, MRUSet<String>> entry = it.next();
            String lhs = entry.getKey();
            map.put(lhs, getHistory(lhs));
        }
        return Collections.unmodifiableMap(map);
    }

    private void rememberInternal(IType lhs, String rhsQualifiedName) {
        String lhsQualifiedName = lhs.getFullyQualifiedName();
        if (isCacheableLHS(lhs, lhsQualifiedName))
            getCache(lhsQualifiedName).add(rhsQualifiedName);
    }

    private boolean isCacheableLHS(IType type, String qualifiedName) {
        return !Flags.isFinal(type.getFlags()) && !UNCACHEABLE.contains(qualifiedName);
    }

    private boolean isCacheableRHS(IType type) {
        return !Flags.isInterface(type.getFlags()) && !Flags.isAbstract(type.getFlags());
    }

    private Set<String> getCache(String lhs) {
        MRUSet<String> rhsCache = fLHSCache.get(lhs);
        if (rhsCache == null) {
            rhsCache = new MRUSet<String>(fMaxRHS);
            fLHSCache.put(lhs, rhsCache);
        }

        return rhsCache;
    }

    /**
     * Stores the history as JSON into the given preferences.
     *
     * @param history
     *         the history to store
     * @param preferences
     *         the preferences to store the history into
     * @param key
     *         the key under which to store the history
     * @throws CoreException
     *         if serialization fails
     * @see #load(Preferences, String) on how to restore a history stored by this method
     */
    public static void store(ContentAssistHistory history, Preferences preferences, String key) {
        StringBuilder result = new StringBuilder();
        new ReaderWriter().store(history, result);
        preferences.setValue(key, result.toString());
    }

    /**
     * Loads a history from an JSON encoded preference value.
     *
     * @param preferences
     *         the preferences to retrieve the history from
     * @param key
     *         the key under which the history is stored
     * @return the deserialized history, or <code>null</code> if there is nothing stored under the given key
     * @throws CoreException
     *         if deserialization fails
     * @see #store(ContentAssistHistory, Preferences, String) on how to store a history such that it can be read by this method
     */
    public static ContentAssistHistory load(Preferences preferences, String key) {
        String value = preferences.getString(key);
        if (value != null && value.length() > 0) {
            return new ReaderWriter().load(value);
        }
        return null;
    }
}
