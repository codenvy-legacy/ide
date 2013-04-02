/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package com.codenvy.ide.resources.model;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: Property.java 79579 2012-02-17 13:27:25Z andrew00x $
 */
public class Property {
    protected String name;

    protected JsonArray<String> value;

    /**
     * Create multi-valued Property
     *
     * @param name
     * @param value
     */
    public Property(String name, JsonArray<String> value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Create single-valued Property. Using this constructor is equivalent to:
     * <p/>
     * <pre>
     * String val = ...;
     * JsonArray&lt;String&gt; l = JsonCollections.&lt;String&gt;createArray();
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
        this.value = JsonCollections.<String>createArray();
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
    public JsonArray<String> getValue() {
        return value;
    }

    /**
     * Set new value of the Property
     *
     * @param value
     */
    public void setValue(JsonArray<String> value) {
        this.value = value;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString() {
        return "Property [name=" + name + ", value=" + value + ']';
    }
}
