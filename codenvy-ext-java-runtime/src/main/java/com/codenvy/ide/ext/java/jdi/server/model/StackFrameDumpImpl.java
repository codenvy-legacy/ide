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
package com.codenvy.ide.ext.java.jdi.server.model;

import com.codenvy.ide.ext.java.jdi.shared.Field;
import com.codenvy.ide.ext.java.jdi.shared.StackFrameDump;
import com.codenvy.ide.ext.java.jdi.shared.Variable;

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
