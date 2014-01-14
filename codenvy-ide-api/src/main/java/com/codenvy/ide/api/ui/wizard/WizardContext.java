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
package com.codenvy.ide.api.ui.wizard;

import com.google.inject.Inject;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
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
        public Key(@NotNull String name) {
            this.name = name;
        }

        /** @return key name */
        @NotNull
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
        this.dates = new HashMap<Key<Object>, Object>();
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
    public <T> void putData(@NotNull Key<T> key, @NotNull T value) {
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
    public <T> T getData(@NotNull Key<T> key) {
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
    public <T> void removeData(@NotNull Key<T> key) {
        dates.remove(key);
    }

    /** Remove all keys and their values from context. */
    public void clear() {
        dates.clear();
    }
}