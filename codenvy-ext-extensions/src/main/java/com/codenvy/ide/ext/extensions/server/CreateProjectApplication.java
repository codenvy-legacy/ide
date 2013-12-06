/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extensions.server;

import org.exoplatform.container.xml.InitParams;

import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.Set;

import static com.codenvy.ide.commons.ContainerUtils.readValueParam;

/**
 * JAX-RS application for Codenvy extensions runtime.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: CreateProjectApplication.java Jul 3, 2013 3:26:32 PM azatsarynnyy $
 */
public class CreateProjectApplication extends Application {
    public static String BASE_URL;

    public CreateProjectApplication(InitParams initParams) {
        BASE_URL = readValueParam(initParams, "extension-url", "");
    }

    /** {@inheritDoc} */
    @Override
    public Set<Class<?>> getClasses() {
        return Collections.<Class<?>>singleton(CreateProjectService.class);
    }
}
