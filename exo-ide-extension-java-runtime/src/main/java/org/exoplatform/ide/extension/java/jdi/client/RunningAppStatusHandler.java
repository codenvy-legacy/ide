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
package org.exoplatform.ide.extension.java.jdi.client;

import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.job.JobChangeEvent;
import org.exoplatform.ide.client.framework.job.RequestStatusHandlerBase;
import org.exoplatform.ide.client.framework.module.IDE;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class RunningAppStatusHandler extends RequestStatusHandlerBase {


    public RunningAppStatusHandler(String projectName) {
        super(projectName);
    }

    /** @see org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestStatusHandler.RequestStatusHandler#requestInProgress
     * (java.lang.String) */
    @Override
    public void requestInProgress(String id) {
        Job job = new Job(id, JobStatus.STARTED);
        job.setStartMessage(DebuggerExtension.LOCALIZATION_CONSTANT.starting(projectName));
        IDE.fireEvent(new JobChangeEvent(job));
    }

    /** @see org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestStatusHandler.RequestStatusHandler#requestFinished(java
     * .lang.String) */
    @Override
    public void requestFinished(String id) {
        Job job = new Job(id, JobStatus.FINISHED);
        job.setFinishMessage(DebuggerExtension.LOCALIZATION_CONSTANT.started(projectName));
        IDE.fireEvent(new JobChangeEvent(job));
    }

}
