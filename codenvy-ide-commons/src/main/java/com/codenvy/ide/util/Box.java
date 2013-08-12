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

package com.codenvy.ide.util;

/**
 * Boxes a value. Mainly useful as a workaround for the finality requirement of
 * values in a java closure's scope.
 *
 * @param <T>
 * @author danilatos@google.com (Daniel Danilatos)
 */
public class Box<T> {

    /** Settable value. */
    public T boxed;

    /** Convenience factory method. */
    public static <T> Box<T> create() {
        return new Box<T>();
    }

    /** Convenience factory method. */
    public static <T> Box<T> create(T initial) {
        return new Box<T>(initial);
    }

    /** No initial value. */
    public Box() {
        this(null);
    }

    /**
     * @param boxed
     *         initial value.
     */
    public Box(T boxed) {
        this.boxed = boxed;
    }

    /** Sets the boxed value to the given new value. */
    public void set(T newVal) {
        this.boxed = newVal;
    }

    /** @return the boxed value. */
    public T get() {
        return this.boxed;
    }

    /** Sets the boxed value to null. */
    public void clear() {
        this.boxed = null;
    }
}
