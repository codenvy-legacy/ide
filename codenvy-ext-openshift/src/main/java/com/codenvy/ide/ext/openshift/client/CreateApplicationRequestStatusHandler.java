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

import com.codenvy.ide.rest.RequestStatusHandler;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class CreateApplicationRequestStatusHandler implements RequestStatusHandler {
    private String applicationName;
    private EventBus eventBus;
    private OpenShiftLocalizationConstant constant;

    public CreateApplicationRequestStatusHandler(String applicationName, EventBus eventBus,
                                                 OpenShiftLocalizationConstant constant) {
        this.applicationName = applicationName;
        this.eventBus = eventBus;
        this.constant = constant;
    }

    @Override
    public void requestInProgress(String id) {
        //TODO
    }

    @Override
    public void requestFinished(String id) {
        //TODO
    }

    @Override
    public void requestError(String id, Throwable exception) {
        //TODO
    }
}
