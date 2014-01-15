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