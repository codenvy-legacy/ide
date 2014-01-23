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
package com.codenvy.ide.ext.java.jdi.shared;

import com.codenvy.dto.shared.DTO;

import java.util.List;

/** @author andrew00x */
@DTO
public interface Variable {
    String getName();

    void setName(String name);

    Variable withName(String name);

    String getValue();

    void setValue(String value);

    Variable withValue(String value);

    String getType();

    void setType(String type);

    Variable withType(String type);

    VariablePath getVariablePath();

    void setVariablePath(VariablePath variablePath);

    Variable withVariablePath(VariablePath variablePath);

    boolean isPrimitive();

    void setPrimitive(boolean primitive);

    Variable withPrimitive(boolean primitive);

    List<Variable> getVariables();

    void setVariables(List<Variable> variables);

    Variable withVariables(List<Variable> variables);
}