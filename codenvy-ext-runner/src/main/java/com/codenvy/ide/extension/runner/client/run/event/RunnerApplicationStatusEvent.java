/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.extension.runner.client.run.event;

import com.codenvy.api.runner.ApplicationStatus;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.api.app.AppContext;
import com.google.web.bindery.event.shared.Event;

/**
 * Client event sent by the runner extension when running application status is updated.
 */
public class RunnerApplicationStatusEvent extends Event<RunnerApplicationStatusEventHandler> {

    protected ApplicationProcessDescriptor applicationProcessDescriptor;
    protected AppContext                   appContext;
    protected ApplicationStatus            applicationStatus;

    public RunnerApplicationStatusEvent(ApplicationProcessDescriptor applicationProcessDescriptor,
                                        AppContext appContext,
                                        ApplicationStatus applicationStatus) {
        this.applicationProcessDescriptor = applicationProcessDescriptor;
        this.appContext = appContext;
        this.applicationStatus = applicationStatus;
    }

    public static Type<RunnerApplicationStatusEventHandler> TYPE = new Type<>();

    @Override
    public com.google.web.bindery.event.shared.Event.Type<RunnerApplicationStatusEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RunnerApplicationStatusEventHandler handler) {
        switch (applicationStatus) {
            case RUNNING:
                handler.onRunnerAppRunning(applicationProcessDescriptor, appContext);
                break;
            case STOPPED:
                handler.onRunnerAppStopped(applicationProcessDescriptor, appContext);
                break;
            case FAILED:
                handler.onRunnerAppFailed(applicationProcessDescriptor, appContext);
                break;
            case CANCELLED:
                handler.onRunnerCancelled(applicationProcessDescriptor, appContext);
                break;
            case NEW:
                handler.onRunnerAppNew(applicationProcessDescriptor, appContext);
                break;
            default:
                break;
        }
    }
}
