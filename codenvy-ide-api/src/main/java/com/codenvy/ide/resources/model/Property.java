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
package com.codenvy.ide.resources.model;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;

/** @author Andrey Parfonov */
public class Property {
    protected String        name;
    protected Array<String> value;

    /**
     * Create multi-valued Property
     *
     * @param name
     * @param value
     */
    public Property(String name, Array<String> value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Create single-valued Property. Using this constructor is equivalent to:
     * <p/>
     * <pre>
     * String val = ...;
     * Array&lt;String&gt; l = Collections.&lt;String&gt;createArray();
     * l.add(val);
     * new Property(&quot;MyName&quot;, l);
     * </pre>
     *
     * @param name
     *         the name of property
     * @param value
     *         the value. If <code>value == null</code> it means no value
     */
    public Property(String name, String value) {
        this.name = name;
        this.value = Collections.<String>createArray();
        this.value.add(value);
    }

    /** Default constructor for internal needs */
    public Property() {
    }

    public String getName() {
        return name;
    }

    /**
     * Set new Property Name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get value of the Property
     *
     * @return
     */
    public Array<String> getValue() {
        return value;
    }

    /**
     * Set new value of the Property
     *
     * @param value
     */
    public void setValue(Array<String> value) {
        this.value = value;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString() {
        return "Property [name=" + name + ", value=" + value + ']';
    }
}
