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
package org.eclipse.jdt.client.packaging.model;

import org.exoplatform.ide.vfs.shared.ItemImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class Dependencies extends ItemImpl {

    private List<Dependency> classpathList = new ArrayList<Dependency>();

    public Dependencies(String name) {
        super(null);
        setId("dependencies-" + name);
        setPath("");
        setName(name);
    }

    public List<Dependency> getClasspathList() {
        return classpathList;
    }

}
