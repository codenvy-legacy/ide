/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.java.client.JavaExtension;
import com.codenvy.ide.ext.java.client.editor.JavaFormatter;
import com.codenvy.ide.ext.java.client.editor.JavaParserWorker;
import com.codenvy.ide.ext.java.client.editor.JavaParserWorkerImpl;
import com.codenvy.ide.ext.java.client.newresource.NewJavaResourceView;
import com.codenvy.ide.ext.java.client.newresource.NewJavaResourceViewImpl;
import com.codenvy.ide.texteditor.api.ContentFormatter;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/** @author Evgen Vidolob */
@ExtensionGinModule
public class JavaGinModule extends AbstractGinModule {

    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(JavaParserWorker.class).to(JavaParserWorkerImpl.class).in(Singleton.class);
        bind(ContentFormatter.class).to(JavaFormatter.class);
        bind(NewJavaResourceView.class).to(NewJavaResourceViewImpl.class).in(Singleton.class);
    }

    @Provides
    @Named("javaCA")
    protected String getJavaCAPath(){
        return JavaExtension.getJavaCAPath();
    }
}