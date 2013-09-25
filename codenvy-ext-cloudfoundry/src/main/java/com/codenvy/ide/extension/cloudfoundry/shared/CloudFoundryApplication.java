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
package com.codenvy.ide.extension.cloudfoundry.shared;

import com.codenvy.ide.dto.DTO;
import com.codenvy.ide.json.JsonArray;

/**
 * CloudFoundry application info.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: CloudFoundryApplication.java Mar 16, 2012 12:21:15 AM azatsarynnyy $
 */
@DTO
public interface CloudFoundryApplication {
    String getName();

    JsonArray<String> getUris();

    int getInstances();

    int getRunningInstances();

    String getState();

    JsonArray<String> getServices();

    String getVersion();

    JsonArray<String> getEnv();

    CloudFoundryApplicationResources getResources();

    Staging getStaging();

    String getDebug();

    ApplicationMetaInfo getMeta();
}