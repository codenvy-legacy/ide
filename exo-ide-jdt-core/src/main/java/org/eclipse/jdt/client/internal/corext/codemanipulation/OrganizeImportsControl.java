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
package org.eclipse.jdt.client.internal.corext.codemanipulation;

import org.eclipse.jdt.client.JdtClientBundle;
import org.eclipse.jdt.client.event.OrganizeImportsEvent;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RolesAllowed("developer")
public class OrganizeImportsControl extends JavaControl {

    /** @param id */
    public OrganizeImportsControl() {
        super("Edit/Organize Imports");
        setTitle("Organize Imports");
        setPrompt("Organize Imports");
        setEvent(new OrganizeImportsEvent());
        setHotKey("Ctrl+Shift+O");
        setImages(JdtClientBundle.INSTANCE.organizeImports(), JdtClientBundle.INSTANCE.organizeImportsDisabled());
    }

}
