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
package com.codenvy.builder.maven;

import com.codenvy.api.builder.internal.Builder;
import com.codenvy.inject.DynaModule;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import java.util.HashMap;
import java.util.Map;

/** @author andrew00x */
@DynaModule
public class MavenBuilderModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<Builder> multiBinder = Multibinder.newSetBinder(binder(), Builder.class);
        multiBinder.addBinding().to(MavenBuilder.class);

        Map<String, String> pluginPackaging = new HashMap<>();
        pluginPackaging.put("play2", "war");
        pluginPackaging.put("grails-app", "war");
        bind(new TypeLiteral<Map<String, String>>() {
        }).annotatedWith(Names.named("PLUGIN_PACKAGING")).toInstance(pluginPackaging);
    }
}
