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
package com.codenvy.builder.maven;

import com.codenvy.api.builder.internal.DependencyCollector;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
public class MavenDependency extends DependencyCollector.Dependency {
    private String groupId;
    private String artifactId;
    private String type;
    private String version;
    private String classifier;
    private String scope;

    public MavenDependency(String groupId, String artifactId, String type, String version, String classifier, String scope) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.type = type;
        this.version = version;
        this.classifier = classifier;
        this.scope = scope;

        setFullName(groupId + ':' + artifactId + ':' + version + ':' + type);
    }

    public MavenDependency() {
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "MavenDependency{" +
               "artifactId='" + artifactId + '\'' +
               ", groupId='" + groupId + '\'' +
               ", type='" + type + '\'' +
               ", version='" + version + '\'' +
               ", classifier='" + classifier + '\'' +
               ", scope='" + scope + '\'' +
               '}';
    }
}

