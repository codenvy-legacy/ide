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
package org.exoplatform.ide;

import java.util.ArrayList;
import java.util.List;

/**
 * Folder(project) template data.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: FolderTemplate.java Jul 26, 2011 5:38:07 PM vereshchaka $
 */
public class FolderTemplate extends Template {
    private List<Template> children;

    public FolderTemplate() {
        super("folder");
    }

    /** @return the children */
    public List<Template> getChildren() {
        if (children == null)
            children = new ArrayList<Template>();
        return children;
    }

}
