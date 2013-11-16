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
package com.codenvy.runner.docker.json;

/** @author <a href="mailto:aparfonov@codenvy.com">Andrey Parfonov</a> */
public class LxcConfParam {
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LxcConfParam{" +
               "key='" + key + '\'' +
               ", value='" + value + '\'' +
               '}';
    }

    // -------------------------

    public LxcConfParam withKey(String key) {
        this.key = key;
        return this;
    }

    public LxcConfParam withValue(String value) {
        this.value = value;
        return this;
    }
}
