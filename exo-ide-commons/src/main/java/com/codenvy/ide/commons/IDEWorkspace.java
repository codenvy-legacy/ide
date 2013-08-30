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
package com.codenvy.ide.commons;

/**
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: $
 */
public class IDEWorkspace {
    private String  url;
    private boolean temporary;
    private String  name;
    private String  id;

    public IDEWorkspace() {

    }

    public IDEWorkspace(String url, String name, String id, boolean temporary) {
        this.url = url;
        this.name = name;
        this.id = id;
        this.temporary = temporary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"url\":\"").append(url).append("\",\"name\":\"").append(name).append("\",\"id\":\"").append(id)
               .append("\",\"temporary\":\"").append(temporary).append("\"}");
        return builder.toString();
    }

}
