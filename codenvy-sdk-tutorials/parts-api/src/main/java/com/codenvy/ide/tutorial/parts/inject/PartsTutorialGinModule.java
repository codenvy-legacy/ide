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
package com.codenvy.ide.tutorial.parts.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.tutorial.parts.howto.TutorialHowToView;
import com.codenvy.ide.tutorial.parts.howto.TutorialHowToViewImpl;
import com.codenvy.ide.tutorial.parts.part.MyPartFactory;
import com.codenvy.ide.tutorial.parts.part.MyPartView;
import com.codenvy.ide.tutorial.parts.part.MyPartViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;

/**
 * GIN module for 'Parts Tutorial' extension.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@ExtensionGinModule
public class PartsTutorialGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(TutorialHowToView.class).to(TutorialHowToViewImpl.class);
        bind(MyPartView.class).to(MyPartViewImpl.class);
        install(new GinFactoryModuleBuilder().build(MyPartFactory.class));
    }
}