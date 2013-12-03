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

package com.codenvy.ide.collections.java;


import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.JsonStringMap;

import java.util.ArrayList;
import java.util.Map;

/**
 * Server wrapper for a {@link java.util.Map} that implements
 * {@link JsonStringMap}.
 */
public class JsonStringMapAdapter<T> implements JsonStringMap<T> {
    private final Map<String, T> delegate;

    public JsonStringMapAdapter(Map<String, T> delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public T get(String key) {
        return delegate.get(key);
    }

    /** {@inheritDoc} */
    @Override
    public Array<String> getKeys() {
        return new JsonArrayListAdapter<String>(new ArrayList<String>(delegate.keySet()));
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public void iterate(IterationCallback<T> callback) {
        for (String key : delegate.keySet()) {
            callback.onIteration(key, delegate.get(key));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void put(String key, T value) {
        delegate.put(key, value);
    }

    /** {@inheritDoc} */
    @Override
    public void putAll(JsonStringMap<T> otherMap) {
        Array<String> keys = otherMap.getKeys();
        for (int i = 0, n = keys.size(); i < n; i++) {
            String key = keys.get(i);
            put(key, otherMap.get(key));
        }
    }

    /** {@inheritDoc} */
    @Override
    public T remove(String key) {
        return delegate.remove(key);
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsKey(String key) {
        return delegate.containsKey(key);
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return delegate.size();
    }

    /** {@inheritDoc} */
    @Override
    public Array<T> getValues() {
        return new JsonArrayListAdapter<T>(new ArrayList<T>(delegate.values()));
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return delegate.toString();
    }
}
