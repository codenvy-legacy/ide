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
package com.codenvy.ide.ext.java.server.projecttypes;

import com.codenvy.api.project.server.ProjectGenerator;
import com.codenvy.api.project.server.ValueProviderFactory;
import com.codenvy.inject.DynaModule;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/** @author Artem Zatsarynnyy */
@DynaModule
public class JavaModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ProjectTypeDescriptionsExtension.class);
        bind(MultiModuleProjectTypeDescriptionExtension.class);

        bind(JarProjectTypeExtension.class);
        bind(WarProjectTypeExtension.class);
        bind(SpringProjectTypeExtension.class);
        bind(MultiModuleProjectTypeExtension.class);
        bind(MavenJarProjectTypeExtension.class);
        bind(MavenProjectTypeDescriptionsExtension.class);

        Multibinder<ValueProviderFactory> multiBinder = Multibinder.newSetBinder(binder(), ValueProviderFactory.class);
        multiBinder.addBinding().to(MavenSourceFoldersValueProviderFactory.class);
        multiBinder.addBinding().to(AntSourceFoldersValueProviderFactory.class);

        Multibinder<ProjectGenerator> generatorMultibinder = Multibinder.newSetBinder(binder(), ProjectGenerator.class);
        generatorMultibinder.addBinding().to(MavenSimpleProjectGenerator.class);
    }
}
