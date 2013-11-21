/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.dto;

/**
 * Provides implementation of DTO interface.
 *
 * @param <DTO>
 *         the type of DTO interface which implementation this provider provides
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
public interface DtoProvider<DTO> {
    /** Get type of interface which implementation this provider provides. */
    Class<? extends DTO> getImplClass();

    /** Provides implementation of DTO interface from the specified JSON string. */
    DTO fromJson(String json);

    /** Get new implementation of DTO interface. */
    DTO newInstance();
}
