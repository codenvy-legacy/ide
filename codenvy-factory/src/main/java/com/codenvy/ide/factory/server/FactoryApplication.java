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
