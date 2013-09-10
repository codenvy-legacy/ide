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
package com.codenvy.ide.ext.aws.shared.beanstalk;

import com.codenvy.ide.dto.DTO;
import com.codenvy.ide.json.JsonArray;

/**
 * Info about AWS Beanstalk application.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@DTO
public interface ApplicationInfo {
    /**
     * The name of the application.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>1 - 100<br/>
     *
     * @return The name of the application.
     */
    String getName();

    /**
     * User-defined description of the application.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>0 - 200<br/>
     *
     * @return User-defined description of the application.
     */
    String getDescription();

    /**
     * The date when the application was created.
     *
     * @return The date when the application was created.
     */
    double getCreated();

    /**
     * The date when the application was last modified.
     *
     * @return The date when the application was last modified.
     */
    double getUpdated();

    /**
     * The names of the versions for this application.
     *
     * @return The names of the versions for this application.
     */
    JsonArray<String> getVersions();

    /**
     * The names of the configuration templates associated with this
     * application.
     *
     * @return The names of the configuration templates associated with this
     *         application.
     */
    JsonArray<String> getConfigurationTemplates();
}
