/*
 * Copyright (C) 2013 eXo Platform SAS.
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

package org.exoplatform.ide.vfs.shared;

/** @author <a href="mailto:aparfonov@codenvy.com">Andrey Parfonov</a> */
public class PrincipalImpl implements Principal {
    private String name;
    private Type   type;

    public PrincipalImpl(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public PrincipalImpl() {
    }

    // Copy constructor
    public PrincipalImpl(Principal other) {
        this.name = other.getName();
        this.type = other.getType();
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
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Principal)) {
            return false;
        }
        final PrincipalImpl other = (PrincipalImpl)o;
        return name.equals(other.name) && type == other.type;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PrincipalImpl{" +
               "name='" + name + '\'' +
               ", type=" + type +
               '}';
    }
}
