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
