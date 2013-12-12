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
package com.codenvy.builder.maven.dto;

import com.codenvy.api.builder.dto.Dependency;
import com.codenvy.dto.shared.DTO;

/**
 * Maven project dependency.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
@DTO
public interface MavenDependency extends Dependency {
    String getArtifactID();

    MavenDependency withArtifactID(String artifactID);

    void setArtifactID(String artifactID);

    String getClassifier();

    MavenDependency withClassifier(String classifier);

    void setClassifier(String classifier);

    String getGroupID();

    MavenDependency withGroupID(String groupID);

    void setGroupID(String groupID);

    String getType();

    MavenDependency withType(String type);

    void setType(String type);

    String getVersion();

    MavenDependency withVersion(String version);

    void setVersion(String version);

    String getScope();

    MavenDependency withScope(String scope);

    void setScope(String scope);
}

