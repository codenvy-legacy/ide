/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.jdi.server;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.Value;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JdiArrayElementImpl implements JdiArrayElement {
    private final int    index;
    private final Value  value;
    private final String name;

    public JdiArrayElementImpl(int index, Value value) {
        this.index = index;
        this.value = value;
        this.name = "[" + index + "]";
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isArray() {
        return value instanceof ArrayReference;
    }

    @Override
    public boolean isPrimitive() {
        return value instanceof PrimitiveValue;
    }

    @Override
    public JdiValue getValue() {
        if (value == null) {
            return new JdiNullValue();
        }
        return new JdiValueImpl(value);
    }

    @Override
    public String getTypeName() {
        if (value == null) {
            return "null";
        }
        return value.type().name();
    }
}
