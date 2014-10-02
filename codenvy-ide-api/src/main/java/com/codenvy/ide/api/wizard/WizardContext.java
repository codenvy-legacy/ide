/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.wizard;

import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * The container of information which need to move between different pages into the same wizard.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class WizardContext {
    /**
     * The key of wizard data. It identifies content.
     *
     * @param <T>
     *         type that this key provide
     */
    public static class Key<T> {
        private String name;

        /**
         * Create key.
         *
         * @param name
         *         key name
         */
        public Key(@Nonnull String name) {
            this.name = name;
        }

        /** @return key name */
        @Nonnull
        public String getName() {
            return name;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;

            Key key = (Key)o;
            return name.equals(key.name);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    private Map<Key<Object>, Object> dates;

    /** Create wizard context. */
    @Inject
    public WizardContext() {
        this.dates = new HashMap<>();
    }

    /**
     * Put data to context.
     *
     * @param key
     *         key that identifies value
     * @param value
     *         value that need to add
     * @param <T>
     *         type of value
     */
    public <T> void putData(@Nonnull Key<T> key, @Nonnull T value) {
        dates.put((Key<Object>)key, value);
    }

    /**
     * Get data from context.
     *
     * @param key
     *         key that identifies value
     * @param <T>
     *         type of value
     * @return value that is mapped to this key or <code>null</code> if no value is not mapped
     */
    @Nullable
    public <T> T getData(@Nonnull Key<T> key) {
        return (T)dates.get(key);
    }

    /**
     * Remove data from context.
     *
     * @param key
     *         key that identifies value
     * @param <T>
     *         type of value
     */
    public <T> void removeData(@Nonnull Key<T> key) {
        dates.remove(key);
    }

    /** Remove all keys and their values from context. */
    public void clear() {
        dates.clear();
    }
}