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


import com.codenvy.ide.ext.java.jdi.shared.Value;
import com.codenvy.ide.ext.java.jdi.shared.Variable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ValueImpl implements Value {
    private List<Variable> variables;
    private String         value;

    public ValueImpl(List<Variable> variables, String value) {
        this.variables = variables;
        this.value = value;
    }

    public ValueImpl() {
    }

    @Override
    public List<Variable> getVariables() {
        if (variables == null) {
            variables = new ArrayList<Variable>();
        }
        return variables;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ValueImpl{" +
               "variables=" + variables +
               ", value='" + value + '\'' +
               '}';
    }
}
