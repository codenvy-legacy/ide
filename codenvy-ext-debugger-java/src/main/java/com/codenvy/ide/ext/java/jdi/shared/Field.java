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

/** @author andrew00x */
@DTO
public interface Field extends Variable {
    boolean isIsFinal();

    void setIsFinal(boolean value);

    Field withIsFinal(boolean value);

    boolean isIsStatic();

    void setIsStatic(boolean value);

    Field withIsStatic(boolean value);

    boolean isIsTransient();

    void setIsTransient(boolean value);

    Field withIsTransient(boolean value);

    boolean isIsVolatile();

    void setIsVolatile(boolean value);

    Field withIsVolatile(boolean value);
}