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
package org.exoplatform.ide.vfs.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: Property.java 79579 2012-02-17 13:27:25Z andrew00x $
 */
public class PropertyImpl implements Property {
    protected String       name;
    protected List<String> value;

    public PropertyImpl(String name, List<String> value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Create single-valued Property. Using this constructor is equivalent to:
     * <p/>
     * <pre>
     * V val = ...;
     * List&lt;String&gt; l = new ArrayList&lt;String&gt;(1);
     * l.add(val);
     * new Property(&quot;MyName&quot;, l) {
     * };
     * </pre>
     *
     * @param name
     *         the name of property
     * @param value
     *         the value. If <code>value == null</code> it means no value
     */
    public PropertyImpl(String name, String value) {
        this.name = name;
        if (value != null) {
            this.value = new ArrayList<String>(1);
            this.value.add(value);
        }
    }

    public PropertyImpl() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<String> getValue() {
        return value;
    }

    @Override
    public void setValue(List<String> value) {
        this.value = value;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString() {
        return "Property [name=" + name + ", value=" + value + ']';
    }
}
