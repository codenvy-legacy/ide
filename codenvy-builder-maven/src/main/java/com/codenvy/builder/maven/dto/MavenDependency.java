/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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

