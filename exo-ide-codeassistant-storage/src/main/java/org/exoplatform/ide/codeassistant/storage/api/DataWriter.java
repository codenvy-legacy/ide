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

import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface DataWriter {

    /** @param typeInfos */
    void addTypeInfo(List<TypeInfo> typeInfos, String artifact);

    /** @param packages */
    void addPackages(Set<String> packages, String artifact);

    /**
     * @param javaDocs
     * @param artifact
     */
    void addJavaDocs(Map<String, String> javaDocs, String artifact);


    /** @param typeInfos */
    void removeTypeInfo(String artifact);

    /** @param packages */
    void removePackages(String artifact);

    /**
     * @param javaDocs
     * @param artifact
     */
    void removeJavaDocs(String artifact);

}
