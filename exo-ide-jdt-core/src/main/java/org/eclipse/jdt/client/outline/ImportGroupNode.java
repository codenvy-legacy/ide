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
package org.eclipse.jdt.client.outline;

import org.eclipse.jdt.client.core.dom.ImportDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Node is used to group all import declarations.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 8, 2012 5:19:10 PM anya $
 */
public class ImportGroupNode {
    /** Name of the node in Outline. */
    private String name;

    /** The list of imports, contains {@link ImportDeclaration} nodes. */
    private List<Object> imports;

    /**
     * @param name
     *         display name of the node in Outline
     * @param imports
     *         imports
     */
    public ImportGroupNode(String name, List<Object> imports) {
        this.name = name;
        this.imports = imports;
    }

    /** @return the display node name */
    public String getName() {
        return name;
    }

    /** @return {@link List} the list of imports, which contains {@link ImportDeclaration} nodes. */
    public List<Object> getImports() {
        if (imports == null) {
            imports = new ArrayList<Object>();
        }
        return imports;
    }

}
