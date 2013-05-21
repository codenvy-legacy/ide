/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.server;

import org.exoplatform.ide.extension.java.server.datasource.DataSourceConfigurationService;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JavaServiceApplication Mar 30, 2011 10:34:04 AM evgen $
 */
public class JavaServiceApplication extends Application {

    private final Set<Class<?>> classes;

    private final Set<Object> singletons;

    public JavaServiceApplication() {
        classes = new HashSet<Class<?>>(3);
        singletons = new HashSet<Object>(1);
        classes.add(RestCodeAssistantJava.class);
        classes.add(RefactoringService.class);
        classes.add(DataSourceConfigurationService.class);
        singletons.add(new RefactoringExceptionMapper());
    }

    /** @see javax.ws.rs.core.Application#getClasses() */
    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
