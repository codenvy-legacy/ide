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
package com.codenvy.ide.ext.openshift.shared;

import com.codenvy.ide.json.JsonStringMap;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface OpenShiftEmbeddableCartridge {
    /**
     * Get information about cartridge name.
     *
     * @return cartridge name
     */
    String getName();

    /**
     * Get url to control cartridge.
     *
     * @return url link
     */
    String getUrl();

    /**
     * Contains info which should be displayed to the user. It contains important info, e.g. url, username, password for
     * database.
     *
     * @return information about failed creation cartridge.
     */
    String getCreationLog();

    /**
     * Get information about cartridge properties such as login, password, url etc.
     *
     * @return json array with properties
     */
    JsonStringMap<String> getProperties();
}
