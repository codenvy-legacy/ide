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
package com.codenvy.ide.ext.openshift.client;

import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.websocket.MessageBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Git client service for invoking rest service to allow openshift create/update application.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class OpenShiftGitClientService {
    private static final String BASE_URL = "";

    private String                        restServiceContext;
    private Loader                        loader;
    private MessageBus                    wsMessageBus;
    private EventBus                      eventBus;
    private OpenShiftLocalizationConstant constant;

    @Inject
    protected OpenShiftGitClientService(@Named("restContext") String restServiceContext, Loader loader, MessageBus wsMessageBus,
                                        EventBus eventBus, OpenShiftLocalizationConstant constant) {
        this.restServiceContext = restServiceContext;
        this.loader = loader;
        this.wsMessageBus = wsMessageBus;
        this.eventBus = eventBus;
        this.constant = constant;
    }

    //Git command invoke
}
