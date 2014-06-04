/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdi.shared;

import com.codenvy.dto.shared.DTO;

import java.util.List;

/** @author andrew00x */
@DTO
public interface Value {
    List<Variable> getVariables();

    void setVariables(List<Variable> variables);

    Value withVariables(List<Variable> variables);

    String getValue();

    void setValue(String value);

    Value withValue(String value);
}