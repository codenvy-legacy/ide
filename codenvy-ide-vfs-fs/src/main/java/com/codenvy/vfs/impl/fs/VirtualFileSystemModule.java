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
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.api.vfs.server.VirtualFileFilter;
import com.codenvy.inject.DynaModule;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/** @author andrew00x */
@DynaModule
public class VirtualFileSystemModule extends AbstractModule {
    @Override
    protected void configure() {
        final Multibinder<VirtualFileFilter> multibinder =
                Multibinder.newSetBinder(binder(), VirtualFileFilter.class, Names.named("vfs.index_filter"));
        multibinder.addBinding().toInstance(new VirtualFileFilter() {
            @Override
            public boolean accept(VirtualFile virtualFile) {
                return !virtualFile.getPath().endsWith("/.codenvy/misc.xml");
            }
        });

    }
}
