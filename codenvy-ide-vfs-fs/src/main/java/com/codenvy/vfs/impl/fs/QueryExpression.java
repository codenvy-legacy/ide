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
package com.codenvy.vfs.impl.fs;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
public class QueryExpression {
    private String name;
    private String path;
    private String mediaType;
    private String text;

    public String getPath() {
        return path;
    }

    public QueryExpression setPath(String path) {
        this.path = path;
        return this;
    }

    public String getName() {
        return name;
    }

    public QueryExpression setName(String name) {
        this.name = name;
        return this;
    }

    public String getMediaType() {
        return mediaType;
    }

    public QueryExpression setMediaType(String mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public String getText() {
        return text;
    }

    public QueryExpression setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public String toString() {
        return "QueryExpression{" +
               "name='" + name + '\'' +
               ", path='" + path + '\'' +
               ", mediaType='" + mediaType + '\'' +
               ", text='" + text + '\'' +
               '}';
    }
}
