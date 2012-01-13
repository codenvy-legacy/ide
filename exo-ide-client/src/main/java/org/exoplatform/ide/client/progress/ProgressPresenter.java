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
package org.exoplatform.ide.client.progress;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;

import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.job.JobChangeEvent;
import org.exoplatform.ide.client.framework.job.JobChangeHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.progress.event.ShowProgressEvent;
import org.exoplatform.ide.client.progress.event.ShowProgressHandler;

import java.util.LinkedHashMap;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 16, 2011 evgen $
 * 
 */
public class ProgressPresenter implements JobChangeHandler, ShowProgressHandler, ViewClosedHandler
{

   public interface Display extends IsView
   {

      void updateJobs(LinkedHashMap<String, Job> jobs);

      void updateOrAddJob(Job job);

      HasClickHandlers getRemoveFinishedButton();
   }

   private LinkedHashMap<String, Job> jobs = new LinkedHashMap<String, Job>();

   private ProgressNotificationControl control;

   private Display display;

   /**
    * 
    */
   public ProgressPresenter()
   {
      IDE.addHandler(JobChangeEvent.TYPE, this);
      IDE.addHandler(ShowProgressEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);

      control = new ProgressNotificationControl();
      control.setDelimiterBefore(true);
      IDE.getInstance().addControl(control, Docking.STATUSBAR_RIGHT);
      IDE.getInstance().addControl(new ShowProgressControl());
   }

   private void bind()
   {
      display.getRemoveFinishedButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            LinkedHashMap<String, Job> job = new LinkedHashMap<String, Job>();
            for (String key : jobs.keySet())
            {
               if (jobs.get(key).getStatus() == JobStatus.STARTED)
               {
                  job.put(key, jobs.get(key));
               }
            }
            jobs = job;
            display.updateJobs(jobs);
            if (jobs.isEmpty())
               control.hide();
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.job.JobChangeHandler#onJobChangeHandler(org.exoplatform.ide.client.framework.job.JobChangeEvent)
    */
   @Override
   public void onJobChangeHandler(JobChangeEvent event)
   {
      Job job = event.getJob();
      control.setEnabled(true);

      jobs.put(job.getId(), job);
      if (job.getStatus() != JobStatus.FINISHED)
      {
         control.updateState(job);
         control.show();
      }
      else
      {
         control.hide();
      }
      if (display != null)
      {
         display.updateOrAddJob(job);
      }
   }

   private void showJobs()
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bind();
      }
      else
      {
         display.asView().activate();
      }
      display.updateJobs(jobs);
   }

   /**
    * @see org.exoplatform.ide.client.ShowProgressHandler.event.ShowJobsHandler#onShowProgress(org.exoplatform.ide.client.ShowProgressEvent.event.ShowJobsEvent)
    */
   @Override
   public void onShowProgress(ShowProgressEvent event)
   {
      showJobs();
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

}
