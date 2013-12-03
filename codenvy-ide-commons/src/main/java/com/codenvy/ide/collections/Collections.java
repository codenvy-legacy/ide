// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.codenvy.ide.collections;

import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.collections.java.JsonIntegerMapAdapter;
import com.codenvy.ide.collections.js.Jso;
import com.codenvy.ide.collections.js.JsoStringSet;
import com.codenvy.ide.collections.java.JsonArrayListAdapter;
import com.codenvy.ide.collections.java.JsonStringMapAdapter;
import com.codenvy.ide.collections.java.JsonStringSetAdapter;
import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.collections.js.JsoIntegerMap;
import com.codenvy.ide.collections.js.JsoStringMap;
import com.google.gwt.core.client.GWT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/** A set of static factory methods for lightweight collections. */
public final class Collections {

    public interface Implementation {
        <T> Array<T> createArray();

        <T> JsonStringMap<T> createStringMap();

        <T> IntegerMap<T> createIntegerMap();

        JsonStringSet createStringSet();
    }

    // If running in pure java (server code or tests) or in dev mode, use the pure java impl
    private static Implementation implementation = GWT.isClient() || !GWT.isScript() ? new PureJavaImplementation()
                                                                                     : new NativeImplementation();

    public static <T> Array<T> createArray() {
        return implementation.createArray();
    }

    public static <T> JsonStringMap<T> createStringMap() {
        return implementation.createStringMap();
    }

    public static <T> IntegerMap<T> createIntegerMap() {
        return implementation.createIntegerMap();
    }

    public static <T> Array<T> createArray(T... items) {
        Array<T> array = createArray();
        for (int i = 0, n = items.length; i < n; i++) {
            array.add(items[i]);
        }

        return array;
    }

    public static <T> Array<T> createArray(Iterable<T> items) {
        Array<T> array = createArray();
        for (Iterator<T> it = items.iterator(); it.hasNext(); ) {
            array.add(it.next());
        }

        return array;
    }

    public static JsonStringSet createStringSet() {
        return implementation.createStringSet();
    }

    public static JsonStringSet createStringSet(String... items) {
        JsonStringSet set = createStringSet();
        for (int i = 0, n = items.length; i < n; i++) {
            set.add(items[i]);
        }
        return set;
    }

    public static JsonStringSet createStringSet(Iterator<String> iterator) {
        JsonStringSet set = createStringSet();
        while (iterator.hasNext()) {
            set.add(iterator.next());
        }
        return set;
    }

    // TODO: Is it used?
    public static <T> void addAllMissing(Array<T> self, Array<T> b) {

        if (b == null || self == b) {
            return;
        }

        Array<T> addList = createArray();
        for (int i = 0, n = b.size(); i < n; i++) {
            T addCandidate = b.get(i);
            if (!self.contains(addCandidate)) {
                addList.add(addCandidate);
            }
        }
        self.addAll(addList);
    }

    private static class PureJavaImplementation implements Implementation {
        @Override
        public <T> Array<T> createArray() {
            return new JsonArrayListAdapter<T>(new ArrayList<T>());
        }

        @Override
        public <T> JsonStringMap<T> createStringMap() {
            return new JsonStringMapAdapter<T>(new HashMap<String, T>());
        }

        @Override
        public JsonStringSet createStringSet() {
            return new JsonStringSetAdapter(new HashSet<String>());
        }

        @Override
        public <T> IntegerMap<T> createIntegerMap() {
            return new JsonIntegerMapAdapter<T>(new HashMap<Integer, T>());
        }
    }

    private static class NativeImplementation implements Implementation {
        @Override
        public <T> JsonStringMap<T> createStringMap() {
            return JsoStringMap.create();
        }

        @Override
        public JsonStringSet createStringSet() {
            return JsoStringSet.create();
        }

        @Override
        public <T> Array<T> createArray() {
            return Jso.createArray().<JsoArray<T>>cast();
        }

        @Override
        public <T> IntegerMap<T> createIntegerMap() {
            return JsoIntegerMap.create();
        }
    }

    /**
     * Check if two lists are equal. The lists are equal if they are both the same
     * size, and the items at every index are equal according to the provided
     * equator. Returns true if both lists are null.
     *
     * @param <T>
     *         the data type of the arrays
     */
    public static <T> boolean equals(
            Array<T> a, Array<T> b) {
        if (a == b) {
            // Same list or both null.
            return true;
        } else if (a == null || b == null) {
            // One list is null, the other is not.
            return false;
        } else if (a.size() != b.size()) {
            // Different sizes.
            return false;
        } else {
            // Check the elements in the array.
            for (int i = 0; i < a.size(); i++) {
                T itemA = a.get(i);
                T itemB = b.get(i);
                // if the equator is null we just the equals method and some null checking
                if (!equal(itemA, itemB)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Determines whether two possibly-null objects are equal. Returns:
     * <p/>
     * <ul>
     * <li>{@code true} if {@code a} and {@code b} are both null.
     * <li>{@code true} if {@code a} and {@code b} are both non-null and they are
     * equal according to {@link Object#equals(Object)}.
     * <li>{@code false} in all other situations.
     * </ul>
     * <p/>
     * <p>This assumes that any non-null objects passed to this function conform
     * to the {@code equals()} contract.
     */
    public static boolean equal(@Nullable Object a, @Nullable Object b) {
        return a == b || (a != null && a.equals(b));
    }

}
