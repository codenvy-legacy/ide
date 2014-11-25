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
package com.codenvy.ide.extension.runner.client.run.event;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.api.app.AppContext;

/**
 * Handler to listen to runner extension {@link RunnerApplicationStatusEvent} events.
 */
public interface RunnerApplicationStatusEventHandler {

    public void onRunnerAppRunning(ApplicationProcessDescriptor applicationProcessDescriptor, AppContext appContext);


    public void onRunnerAppFailed(ApplicationProcessDescriptor applicationProcessDescriptor, AppContext appContext);


    public void onRunnerAppStopped(ApplicationProcessDescriptor applicationProcessDescriptor, AppContext appContext);


    public void onRunnerCancelled(ApplicationProcessDescriptor applicationProcessDescriptor, AppContext appContext);


    public void onRunnerAppNew(ApplicationProcessDescriptor applicationProcessDescriptor, AppContext appContext);


}
