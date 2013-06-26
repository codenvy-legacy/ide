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
package com.codenvy.ide.extension.html.server;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: AppRunnerApplication.java Jun 26, 2013 11:13:30 AM azatsarynnyy $
 *
 */
public class AppRunnerApplication extends Application {
    private final Set<Class<?>> classes;
    private final Set<Object>   objects;

    public AppRunnerApplication() {
        classes = new HashSet<Class<?>>(1);
        classes.add(ApplicationRunnerService.class);
        objects = new HashSet<Object>(1);
        objects.add(new ApplicationRunnerExceptionMapper());
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return objects;
    }
}
