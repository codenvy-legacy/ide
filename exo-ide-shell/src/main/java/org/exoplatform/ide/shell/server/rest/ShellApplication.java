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
package org.exoplatform.ide.shell.server.rest;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 1, 2011 evgen $
 */
public class ShellApplication extends Application {
    private Set<Class<?>> classes = new HashSet<Class<?>>(2);


    public ShellApplication() {
        classes.add(CLIResourcesService.class);
        classes.add(ShellConfigurationService.class);
    }

    /** @see javax.ws.rs.core.Application#getClasses() */
    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }


}
