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
package com.codenvy.ide.collaboration.dto;

import com.codenvy.ide.dtogen.shared.*;
import com.codenvy.ide.json.shared.JsonArray;


/**
 * Partial copy of {@link org.exoplatform.ide.vfs.shared.Property}
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id: $
 */
@RoutingType(type = RoutableDto.NON_ROUTABLE_TYPE)
public interface Property extends ServerToClientDto, CompactJsonDto {
    @SerializationIndex(1)
    String getName();

    @SerializationIndex(2)
    JsonArray<String> getValue();

}
