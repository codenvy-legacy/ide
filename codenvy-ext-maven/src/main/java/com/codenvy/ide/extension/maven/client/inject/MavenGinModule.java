/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.extension.maven.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.extension.maven.client.BuilderClientService;
import com.codenvy.ide.extension.maven.client.BuilderClientServiceImpl;
import com.codenvy.ide.extension.maven.client.build.BuildProjectView;
import com.codenvy.ide.extension.maven.client.build.BuildProjectViewImpl;
import com.codenvy.ide.extension.maven.client.template.CreateProjectClientService;
import com.codenvy.ide.extension.maven.client.template.CreateProjectClientServiceImpl;
import com.codenvy.ide.extension.maven.client.template.wizard.javaproject.CreateJavaProjectPageView;
import com.codenvy.ide.extension.maven.client.template.wizard.javaproject.CreateJavaProjectPageViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@ExtensionGinModule
public class MavenGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(BuilderClientService.class).to(BuilderClientServiceImpl.class).in(Singleton.class);
        bind(CreateProjectClientService.class).to(CreateProjectClientServiceImpl.class).in(Singleton.class);

        bind(BuildProjectView.class).to(BuildProjectViewImpl.class).in(Singleton.class);
        bind(CreateJavaProjectPageView.class).to(CreateJavaProjectPageViewImpl.class);
    }
}