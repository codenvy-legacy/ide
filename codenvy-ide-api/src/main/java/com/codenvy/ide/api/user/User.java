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
package com.codenvy.ide.api.user;

import com.codenvy.ide.dto.DTO;
import com.codenvy.ide.collections.JsonStringMap;

/**
 * Represent information about User.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@DTO
public interface User {
    /**
     * Returns user's id.
     *
     * @return user's id
     */
    String getUserId();

    /**
     * Returns profile's attributes.
     *
     * @return profile's attributes
     */
    JsonStringMap<String> getProfileAttributes();
}