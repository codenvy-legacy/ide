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
package com.codenvy.ide.ext.java.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.java.client.editor.JavaParserWorker;
import com.codenvy.ide.ext.java.client.editor.JavaParserWorkerImpl;
import com.codenvy.ide.ext.java.client.projecttemplate.ant.CreateAntProjectClientService;
import com.codenvy.ide.ext.java.client.projecttemplate.ant.CreateAntProjectClientServiceImpl;
import com.codenvy.ide.ext.java.client.projecttemplate.maven.CreateMavenProjectClientService;
import com.codenvy.ide.ext.java.client.projecttemplate.maven.CreateMavenProjectClientService;
import com.codenvy.ide.ext.java.client.projecttemplate.maven.CreateMavenProjectClientServiceImpl;
import com.codenvy.ide.ext.java.client.projecttemplate.maven.CreateMavenProjectClientServiceImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@ExtensionGinModule
public class JavaGinModule extends AbstractGinModule {

    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(JavaParserWorker.class).to(JavaParserWorkerImpl.class).in(Singleton.class);
        bind(CreateMavenProjectClientService.class).to(CreateMavenProjectClientServiceImpl.class).in(Singleton.class);
        bind(CreateAntProjectClientService.class).to(CreateAntProjectClientServiceImpl.class).in(Singleton.class);
    }
}