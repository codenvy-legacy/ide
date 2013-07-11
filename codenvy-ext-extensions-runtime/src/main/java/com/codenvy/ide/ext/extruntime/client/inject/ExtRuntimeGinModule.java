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
package com.codenvy.ide.ext.extruntime.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.extruntime.client.ExtRuntimeClientService;
import com.codenvy.ide.ext.extruntime.client.ExtRuntimeClientServiceImpl;
import com.codenvy.ide.ext.extruntime.client.wizard.ExtensionPageView;
import com.codenvy.ide.ext.extruntime.client.wizard.ExtensionPageViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/**
 * GIN module for 'Codenvy Extensions Runtime' extension.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtRuntimeGinModule.java Jul 2, 2013 4:44:09 PM azatsarynnyy $
 */
@ExtensionGinModule
public class ExtRuntimeGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(ExtRuntimeClientService.class).to(ExtRuntimeClientServiceImpl.class).in(Singleton.class);
        bind(ExtensionPageView.class).to(ExtensionPageViewImpl.class).in(Singleton.class);
    }
}
