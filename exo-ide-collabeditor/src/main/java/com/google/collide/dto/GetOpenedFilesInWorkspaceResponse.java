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
package com.google.collide.dto;

import com.codenvy.ide.dtogen.shared.RoutingType;
import com.codenvy.ide.dtogen.shared.ServerToClientDto;
import com.codenvy.ide.json.shared.JsonArray;
import com.codenvy.ide.json.shared.JsonStringMap;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RoutingType(type = RoutingTypes.GETOPENEDFILESINWORKSPACERESPONSE)
public interface GetOpenedFilesInWorkspaceResponse extends ServerToClientDto {
    /**
     * Key is file path, value array of users that open this file
     *
     * @return the JsonStringMap
     */
    JsonStringMap<JsonArray<ParticipantUserDetails>> getOpenedFiles();


    /**
     * Key is file path, value is EditSessionId
     *
     * @return the JsonStringMap
     */
    JsonStringMap<String> getFileEditSessions();
}
