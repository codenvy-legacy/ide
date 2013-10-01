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

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
public class WizardContext {
    public class Key<T> {
        private String name;

        public Key(@NotNull String name) {
            this.name = name;
        }

        @NotNull
        public String getName() {
            return name;
        }
    }

    private Map<Key<Object>, Object> dates;

    @Inject
    public WizardContext() {
        this.dates = new HashMap<Key<Object>, Object>();
    }

    public <T> void putData(@NotNull Key<T> key, @NotNull T value) {
        dates.put((Key<Object>)key, value);
    }

    @Nullable
    public <T> T getData(@NotNull Key<T> key) {
        return (T)dates.get(key);
    }
}