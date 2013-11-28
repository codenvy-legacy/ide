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


import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.ext.java.jdi.shared.VariablePath;

import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class VariableImpl implements Variable {
    private String       name;
    private String       value;
    private String       type;
    private VariablePath variablePath;
    private boolean      primitive;

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    private List<Variable> variables;

    public VariableImpl(String name,
                        String value,
                        String type,
                        VariablePath variablePath,
                        boolean primitive) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.variablePath = variablePath;
        this.primitive = primitive;
    }

    public VariableImpl() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public VariablePath getVariablePath() {
        return variablePath;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void setVariablePath(VariablePath variablePath) {
        this.variablePath = variablePath;
    }

    @Override
    public boolean isPrimitive() {
        return primitive;
    }

    @Override
    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }

    @Override
    public String toString() {
        return "VariableImpl{" +
               "name='" + name + '\'' +
               ", value='" + value + '\'' +
               ", type='" + type + '\'' +
               ", variablePath=" + variablePath +
               ", primitive=" + primitive +
               '}';
    }
}
