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
