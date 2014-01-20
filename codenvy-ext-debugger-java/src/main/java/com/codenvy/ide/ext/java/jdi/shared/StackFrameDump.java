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
public interface StackFrameDump {
    List<Field> getFields();

    void setFields(List<Field> fields);

    StackFrameDump withFields(List<Field> fields);

    List<Variable> getLocalVariables();

    void setLocalVariables(List<Variable> localVariables);

    StackFrameDump withLocalVariables(List<Variable> localVariables);
}