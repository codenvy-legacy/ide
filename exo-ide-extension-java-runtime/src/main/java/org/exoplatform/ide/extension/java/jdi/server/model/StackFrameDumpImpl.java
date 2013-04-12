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
package org.exoplatform.ide.extension.java.jdi.server.model;

import org.exoplatform.ide.extension.java.jdi.shared.Field;
import org.exoplatform.ide.extension.java.jdi.shared.StackFrameDump;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class StackFrameDumpImpl implements StackFrameDump {
    private List<Field>    fields;
    private List<Variable> localVariables;

    public StackFrameDumpImpl(List<Field> fields, List<Variable> localVariables) {
        this.fields = fields;
        this.localVariables = localVariables;
    }

    public StackFrameDumpImpl() {
    }

    @Override
    public List<Field> getFields() {
        if (fields == null) {
            fields = new ArrayList<Field>();
        }
        return fields;
    }

    @Override
    public List<Variable> getLocalVariables() {
        if (localVariables == null) {
            localVariables = new ArrayList<Variable>();
        }
        return localVariables;
    }

    @Override
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    @Override
    public void setLocalVariables(List<Variable> localVariables) {
        this.localVariables = localVariables;
    }

    @Override
    public String toString() {
        return "StackFrameDumpImpl{\n" +
               "====== FIELDS =====\n" + toString(fields) +
               "\n===== LOCAL_VARIABLES======\n" + toString(localVariables) +
               "\n}";
    }

    private <E extends Variable> String toString(Collection<E> collection) {
        Iterator<E> i;
        if (collection == null || !(i = collection.iterator()).hasNext()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        while (true) {
            E e = i.next();
            sb.append(e == collection ? "(this Collection)" : e);
            if (!i.hasNext()) {
                return sb.toString();
            }
            sb.append(',');
            sb.append('\n');
        }
    }
}
