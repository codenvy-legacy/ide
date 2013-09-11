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
package org.exoplatform.ide.codeassistant.storage.lucene;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Artifact {

    private String path;

    private String artifactID;

    private String groupID;

    private String version;

    private String type;

    /**
     *
     */
    public Artifact() {
    }

    /** @return the path */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     *         the path to set
     */
    public void setPath(String path) {
        this.path = path;
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

    public String getArtifactString() {
        return groupID + ":" + artifactID + ":" + version + ":" + type;
    }

}
