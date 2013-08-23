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
package org.exoplatform.ide.codeassistant.storage.api;

import org.exoplatform.ide.codeassistant.jvm.bean.Dependency;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class WriterTask {

    private Dependency artifact;

    private List<TypeInfo> typesInfo;

    private Set<String> packages;

    private Map<String, String> javaDock;

    /**
     *
     */
    public WriterTask(Dependency artifact, List<TypeInfo> typesInfo, Set<String> packages) {
        this(artifact, typesInfo, packages, null);
    }

    /**
     *
     */
    public WriterTask(Dependency artifact, Map<String, String> javaDock) {
        this(artifact, null, null, javaDock);
    }

    /**
     * @param artifact
     * @param typesInfo
     * @param packages
     * @param javaDock
     */
    public WriterTask(Dependency artifact, List<TypeInfo> typesInfo, Set<String> packages, Map<String, String> javaDock) {
        this.artifact = artifact;
        this.typesInfo = typesInfo;
        this.packages = packages;
        this.javaDock = javaDock;
    }

    /** @return the artifact */
    public Dependency getArtifact() {
        return artifact;
    }

    /**
     * @param artifact
     *         the artifact to set
     */
    public void setArtifact(Dependency artifact) {
        this.artifact = artifact;
    }

    /** @return the typesInfo */
    public List<TypeInfo> getTypesInfo() {
        return typesInfo;
    }

    /**
     * @param typesInfo
     *         the typesInfo to set
     */
    public void setTypesInfo(List<TypeInfo> typesInfo) {
        this.typesInfo = typesInfo;
    }

    /** @return the packages */
    public Set<String> getPackages() {
        return packages;
    }

    /**
     * @param packages
     *         the packages to set
     */
    public void setPackages(Set<String> packages) {
        this.packages = packages;
    }

    /** @return the javaDock */
    public Map<String, String> getJavaDock() {
        return javaDock;
    }

    /**
     * @param javaDock
     *         the javaDock to set
     */
    public void setJavaDock(Map<String, String> javaDock) {
        this.javaDock = javaDock;
    }

}
