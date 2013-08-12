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

import com.codenvy.ide.dto.shared.ClientToServerDto;
import com.codenvy.ide.json.JsonStringMap;

/**
 * Represent information about attributes what need to update.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
// TODO IDEX-94
//@RoutingType(type = RoutingTypes.UPDATE_USER_ATTRIBUTES)
public interface UpdateUserAttributes extends ClientToServerDto {
    /**
     * Returns user's attributes with new value what need to update.
     *
     * @return user's attributes
     */
    JsonStringMap<String> getAttributes();
}