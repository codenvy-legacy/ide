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
package com.codenvy.ide.factory.server;

import org.exoplatform.ide.vfs.server.RequestContextResolver;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * JAX-RS application for Codenvy Factory feature.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FactoryApplication.java Jun 25, 2013 10:18:14 PM azatsarynnyy $
 */
public class FactoryApplication extends Application {

    @Override
    public Set<Class< ? >> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(FactoryService.class);
        classes.add(CopyProjectService.class);
        classes.add(RequestContextResolver.class);
        return classes;
    }
}
