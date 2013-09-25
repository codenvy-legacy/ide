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
package com.codenvy.ide.ext.java.client.core.formatter;

import java.util.Map;

public class Profile {
    private final String name;

    private final String id;

    private final Map<String, String> settings;

    /**
     * @param name
     * @param id
     * @param settings
     */
    public Profile(String name, String id, Map<String, String> settings) {
        super();
        this.name = name;
        this.id = id;
        this.settings = settings;
    }

    /** @return the name */
    public String getName() {
        return name;
    }

    /** @return the id */
    public String getId() {
        return id;
    }

    /** @return the settings */
    public Map<String, String> getSettings() {
        return settings;
    }

}