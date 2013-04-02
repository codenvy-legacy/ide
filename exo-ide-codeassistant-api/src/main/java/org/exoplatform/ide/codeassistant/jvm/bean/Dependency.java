/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.codeassistant.jvm.bean;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Dependency {
    private String groupID;

    private String artifactID;

    private String type;

    private String version;

    private String classifier;

    /**
     *
     */
    public Dependency() {
    }

    /**
     * @param groupID
     * @param artifactID
     * @param type
     * @param version
     */
    public Dependency(String groupID, String artifactID, String type, String version, String classifier) {
        super();
        this.groupID = groupID;
        this.artifactID = artifactID;
        this.type = type;
        this.version = version;
        this.classifier = classifier;
    }

    /** @return the groupID */
    public String getGroupID() {
        return groupID;
    }

    /**
     * @param groupID
     *         the groupID to set
     */
    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    /** @return the artifactID */
    public String getArtifactID() {
        return artifactID;
    }

    /**
     * @param artifactID
     *         the artifactID to set
     */
    public void setArtifactID(String artifactID) {
        this.artifactID = artifactID;
    }

    /** @return the type */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *         the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /** @return the version */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     *         the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString() {
        return groupID + ":" + artifactID + ":" + version + ":" + type;
    }

    /** @return the classifier */
    public String getClassifier() {
        return classifier;
    }

    /**
     * @param classifier
     *         the classifier to set
     */
    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

}
