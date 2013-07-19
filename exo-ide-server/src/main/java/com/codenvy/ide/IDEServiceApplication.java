/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package com.codenvy.ide;

import org.exoplatform.ide.IDESessionService;
import org.exoplatform.ide.googlecontacts.GoogleContactsRestService;
import org.exoplatform.ide.project.ProjectPrepareExceptionMapper;
import org.exoplatform.ide.project.ProjectPrepareService;
import org.exoplatform.ide.template.TemplatesRestService;
import org.exoplatform.ide.upload.LoopbackContentService;
import org.exoplatform.ide.upload.UploadServiceExceptionMapper;
import org.exoplatform.ide.vfs.server.RequestContextResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Jan 12, 2011 5:24:37 PM evgen $
 */
public class IDEServiceApplication extends Application {

    private final Set<Class<?>> classes = new HashSet<Class<?>>();

    private final Set<Object> objects = new HashSet<Object>();

    public IDEServiceApplication(VirtualFileSystemRegistry vfsRegistry, EventListenerList eventListenerList) {
        objects.add(new UploadServiceExceptionMapper());
        objects.add(new ProjectPrepareExceptionMapper());

        classes.add(TemplatesRestService.class);
        classes.add(LoopbackContentService.class);
        classes.add(IDEConfigurationService.class);
        classes.add(RequestContextResolver.class);
        classes.add(GoogleContactsRestService.class);
        classes.add(ProjectPrepareService.class);
        classes.add(IDESessionService.class);
    }

    /** @see javax.ws.rs.core.Application#getClasses() */
    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    /** @see javax.ws.rs.core.Application#getSingletons() */
    @Override
    public Set<Object> getSingletons() {
        return objects;
    }
}
