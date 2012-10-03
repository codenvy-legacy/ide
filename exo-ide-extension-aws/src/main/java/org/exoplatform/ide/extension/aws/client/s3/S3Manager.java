/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.aws.client.s3;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.event.dom.client.HasClickHandlers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasScrollHandlers;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSError;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.client.s3.events.BucketCreatedEvent;
import org.exoplatform.ide.extension.aws.client.s3.events.BucketCreatedHandler;
import org.exoplatform.ide.extension.aws.client.s3.events.S3ObjectUploadedEvent;
import org.exoplatform.ide.extension.aws.client.s3.events.S3ObjectUploadedHandler;
import org.exoplatform.ide.extension.aws.client.s3.events.ShowS3ManagerEvent;
import org.exoplatform.ide.extension.aws.client.s3.events.ShowS3ManagerHandler;
import org.exoplatform.ide.extension.aws.shared.s3.NewS3Object;
import org.exoplatform.ide.extension.aws.shared.s3.S3Bucket;
import org.exoplatform.ide.extension.aws.shared.s3.S3Object;
import org.exoplatform.ide.extension.aws.shared.s3.S3ObjectsList;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: S3Manager.java Sep 19, 2012 vetal $
 *
 */
public class S3Manager implements ProjectOpenedHandler, ProjectClosedHandler, VfsChangedHandler, ViewClosedHandler,
   ShowS3ManagerHandler, BucketCreatedHandler, S3ObjectUploadedHandler
{
   interface Display extends IsView
   {
      ListGridItem<S3Object> getS3Object();

      void setS3Buckets(List<S3Bucket> bucketsList);

      void setS3ObjectsList(S3ObjectsList s3ObjectsList);

      void addS3ObjectsList(S3ObjectsList s3ObjectsList);

      void setEnableDeleteAction(boolean enabled);

      void setEnableUploadAction(boolean enabled);

      void setEnableUploadProjectAction(boolean enabled);

      void setDeleteAction(ScheduledCommand command);

      void setUploadAction(ScheduledCommand command);

      void setUploadOpenedProjectAction(ScheduledCommand command);

      void setRefreshAction(ScheduledCommand command);

      HasChangeHandlers getBuckets();

      String getSelectedBucketId();

      S3Object getSelectedObject();

      void setDeleteBucketAction(ScheduledCommand command);

      void setCreateBucketAction(ScheduledCommand command);

      HasScrollHandlers getNextObject();

      int getVerticalScrollPosition();

      int getOffsetHeight();

      int getWidgetgetOffsetHeight();
      
      HasClickHandlers getUploadButton();

      HasClickHandlers getRefreshButton();

   }

   String currentBucketId;

   private Display display;

   private ProjectModel openedProject;

   private VirtualFileSystemInfo vfsInfo;

   private S3ObjectsList s3ObjectsList;

   private String curent = null;

   private Stack<String> visit = new Stack<String>();

   private CreateBucketPresenter createBucketPresenter;

   private UploadFilePresenter uploadFilePresenter;

   /**
    * The last scroll position.
    */
   private int lastScrollPos = 0;

   /**
    * The default increment size.
    */
   private static final int DEFAULT_INCREMENT = 20;

   /**
    * The increment size.
    */
   private int incrementSize = DEFAULT_INCREMENT;

   public S3Manager()
   {
      IDE.getInstance().addControl(new S3ManagerControl());
      uploadFilePresenter = new UploadFilePresenter();
      createBucketPresenter = new CreateBucketPresenter();

      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ShowS3ManagerEvent.TYPE, this);
      IDE.addHandler(BucketCreatedEvent.TYPE, this);
      IDE.addHandler(S3ObjectUploadedEvent.TYPE, this);

   }

   public void bindDisplay()
   {
      if (openedProject == null)
         display.setEnableUploadProjectAction(false);
      
      display.getRefreshButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
             refresh();
         }
      });
      
      display.getUploadButton().addClickHandler(new ClickHandler()
      {
         
         @Override
         public void onClick(ClickEvent event)
         {
            uploadFilePresenter.onUploadFile(currentBucketId);   
         }
      });

      display.getNextObject().addScrollHandler(new ScrollHandler()
      {
         @Override
         public void onScroll(ScrollEvent event)
         {
            // If scrolling up, ignore the event.
            int oldScrollPos = lastScrollPos;
            lastScrollPos = display.getVerticalScrollPosition();
            if (oldScrollPos >= lastScrollPos)
            {
               return;
            }
            int maxScrollTop = display.getWidgetgetOffsetHeight() - display.getOffsetHeight();
            if (lastScrollPos >= maxScrollTop && s3ObjectsList.getNextMarker() != null)
            {
               nextObjectsList(currentBucketId, s3ObjectsList.getNextMarker());
            }
         }
      });

      display.setRefreshAction(new ScheduledCommand()
      {

         @Override
         public void execute()
         {
            refresh();
         }
      });

      display.setDeleteAction(new ScheduledCommand()
      {

         @Override
         public void execute()
         {
            try
            {
               S3Service.getInstance().deleteObject(new AsyncRequestCallback<String>()
               {

                  @Override
                  protected void onSuccess(String result)
                  {
                     refresh();
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     showError(exception);

                  }
               }, display.getSelectedBucketId(), display.getSelectedObject().getS3Key());
            }
            catch (RequestException e)
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
      });

      display.setUploadAction(new ScheduledCommand()
      {

         @Override
         public void execute()
         {
            uploadFilePresenter.onUploadFile(currentBucketId);
         }
      });

      display.setUploadOpenedProjectAction(new ScheduledCommand()
      {

         @Override
         public void execute()
         {
            doUploadProject();

         }
      });

      display.getBuckets().addChangeHandler(new ChangeHandler()
      {

         @Override
         public void onChange(ChangeEvent event)
         {
            currentBucketId = display.getSelectedBucketId();
            getObjectsList(display.getSelectedBucketId(), null);
         }
      });

      display.setDeleteBucketAction(new ScheduledCommand()
      {

         @Override
         public void execute()
         {
            doDeleteBucket();
         }

      });

      display.setCreateBucketAction(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            createBucketPresenter.onOpenView();
         }
      });
   }

   protected void doUploadProject()
   {
      AutoBean<NewS3Object> autoBean = AWSExtension.AUTO_BEAN_FACTORY.newS3Object();
      try
      {
         S3Service.getInstance().uploadProject(
            new AsyncRequestCallback<NewS3Object>(new AutoBeanUnmarshaller<NewS3Object>(autoBean))
            {

               @Override
               protected void onSuccess(NewS3Object result)
               {
                  Dialogs.getInstance().showInfo(result.getS3Bucket() + result.getVersionId());

               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  showError(exception);

               }
            }, display.getSelectedBucketId(), openedProject.getName(), vfsInfo.getId(), openedProject.getId());
      }
      catch (RequestException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }

   //   protected void getPrev(final String marker)
   //   {
   //      AutoBean<S3ObjectsList> autoBean = AWSExtension.AUTO_BEAN_FACTORY.s3ObjectsList();
   //      try
   //      {
   //         S3Service.getInstance().getS3ObjectsList(
   //            new AsyncRequestCallback<S3ObjectsList>(new AutoBeanUnmarshaller<S3ObjectsList>(autoBean))
   //            {
   //
   //               @Override
   //               protected void onSuccess(S3ObjectsList result)
   //               {
   //                  curent = marker;
   //                  s3ObjectsList = result;
   //
   //                  display.setS3ObjectsList(result);
   //                  if (result.getNextMarker() == null)
   //                  {
   //                     display.setEnableNextButton(false);
   //                  }
   //                  else
   //                  {
   //                     display.setEnableNextButton(true);
   //                  }
   //                  if (visit.empty())
   //                  {
   //                     display.setEnableBackButton(false);
   //                  }
   //                  else
   //                  {
   //                     display.setEnableBackButton(true);
   //                  }
   //               }
   //
   //               @Override
   //               protected void onFailure(Throwable exception)
   //               {
   //                  showError(exception);
   //
   //               }
   //            }, display.getSelectedBucketId(), marker);
   //      }
   //      catch (RequestException e)
   //      {
   //         // TODO Auto-generated catch block
   //         e.printStackTrace();
   //      }
   //   }
   //
   //   protected void getNext()
   //   {
   //
   //      AutoBean<S3ObjectsList> autoBean = AWSExtension.AUTO_BEAN_FACTORY.s3ObjectsList();
   //      try
   //      {
   //         S3Service.getInstance().getS3ObjectsList(
   //            new AsyncRequestCallback<S3ObjectsList>(new AutoBeanUnmarshaller<S3ObjectsList>(autoBean))
   //            {
   //
   //               @Override
   //               protected void onSuccess(S3ObjectsList result)
   //               {
   //                  visit.push(curent);
   //                  curent = s3ObjectsList.getNextMarker();
   //                  s3ObjectsList = result;
   //                  display.setS3ObjectsList(result);
   //                  if (result.getNextMarker() == null)
   //                  {
   //                     display.setEnableNextButton(false);
   //                  }
   //                  else
   //                  {
   //                     display.setEnableNextButton(true);
   //                  }
   //                  if (visit.empty())
   //                  {
   //                     display.setEnableBackButton(false);
   //                  }
   //                  else
   //                  {
   //                     display.setEnableBackButton(true);
   //                  }
   //               }
   //
   //               @Override
   //               protected void onFailure(Throwable exception)
   //               {
   //                  showError(exception);
   //
   //               }
   //            }, display.getSelectedBucketId(), s3ObjectsList.getNextMarker());
   //      }
   //      catch (RequestException e)
   //      {
   //         // TODO Auto-generated catch block
   //         e.printStackTrace();
   //      }
   //
   //   }

   protected void getObjectsList(String s3Bucket, String nextMarker)
   {

      AutoBean<S3ObjectsList> autoBean = AWSExtension.AUTO_BEAN_FACTORY.s3ObjectsList();
      try
      {
         S3Service.getInstance().getS3ObjectsList(
            new AsyncRequestCallback<S3ObjectsList>(new AutoBeanUnmarshaller<S3ObjectsList>(autoBean))
            {

               @Override
               protected void onSuccess(S3ObjectsList result)
               {
                  if (s3ObjectsList != null)
                     curent = s3ObjectsList.getNextMarker();

                  s3ObjectsList = result;
                  display.setS3ObjectsList(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  showError(exception);

               }
            }, s3Bucket, nextMarker, 25);
      }
      catch (RequestException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   protected void nextObjectsList(String s3Bucket, String nextMarker)
   {

      AutoBean<S3ObjectsList> autoBean = AWSExtension.AUTO_BEAN_FACTORY.s3ObjectsList();
      try
      {
         S3Service.getInstance().getS3ObjectsList(
            new AsyncRequestCallback<S3ObjectsList>(new AutoBeanUnmarshaller<S3ObjectsList>(autoBean))
            {

               @Override
               protected void onSuccess(S3ObjectsList result)
               {
                  if (s3ObjectsList != null)
                     curent = s3ObjectsList.getNextMarker();
                  s3ObjectsList = result;
                  display.addS3ObjectsList(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  showError(exception);

               }
            }, s3Bucket, nextMarker, 10);
      }
      catch (RequestException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfsInfo = event.getVfsInfo();
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      this.openedProject = null;
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      this.openedProject = event.getProject();
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

   @Override
   public void onShowS3Manager(ShowS3ManagerEvent event)
   {

      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
      }
      getBuckets();
   }

   private void getBuckets()
   {
      List<S3Bucket> buckets = new ArrayList<S3Bucket>();
      try
      {
         S3Service.getInstance().getBuckets(
            new AwsAsyncRequestCallback<List<S3Bucket>>(new S3BucketsUnmarshaller(buckets), new LoggedInHandler()
            {

               @Override
               public void onLoggedIn()
               {
                  getBuckets();
               }
            })
            {

               @Override
               protected void onSuccess(List<S3Bucket> result)
               {

                  display.setS3Buckets(result);
               }

               @Override
               protected void processFail(Throwable exception)
               {
                  showError(exception);

               }
            });
      }
      catch (RequestException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }

   private void refresh()
   {
      AutoBean<S3ObjectsList> autoBean = AWSExtension.AUTO_BEAN_FACTORY.s3ObjectsList();
      try
      {
         S3Service.getInstance().getS3ObjectsList(
            new AsyncRequestCallback<S3ObjectsList>(new AutoBeanUnmarshaller<S3ObjectsList>(autoBean))
            {

               @Override
               protected void onSuccess(S3ObjectsList result)
               {
                  s3ObjectsList = result;
                  display.setS3ObjectsList(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  showError(exception);

               }
            }, display.getSelectedBucketId(), null, 25);
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

   @Override
   public void onBucketCreated(BucketCreatedEvent event)
   {
      getBuckets();

   }

   private void showError(Throwable exception)
   {
      AWSError awsError = new AWSError(exception.getMessage());
      if (awsError.getAwsErrorMessage() != null)
      {
         Dialogs.getInstance().showError(awsError.getAwsService() + " (" + awsError.getStatusCode() + ")",
            awsError.getAwsErrorCode() + " : " + awsError.getAwsErrorMessage());
      }
      else
      {
         IDE.fireEvent(new OutputEvent(exception.getMessage(), Type.ERROR));
      }
   }

   @Override
   public void onS3ObjectUploaded(S3ObjectUploadedEvent event)
   {
      getObjectsList(display.getSelectedBucketId(), null);
   }

   /**
    * 
    */
   private void doDeleteBucket()
   {
      Dialogs.getInstance().ask("Delete Bucket", "Are you sure want to delete bucket " + currentBucketId,
         new BooleanValueReceivedHandler()
         {
            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (value)
               {
                  try
                  {
                     S3Service.getInstance().deleteBucket(new AsyncRequestCallback<String>()
                     {

                        @Override
                        protected void onSuccess(String result)
                        {
                           getBuckets();
                        }

                        @Override
                        protected void onFailure(Throwable exception)
                        {
                           showError(exception);
                        }
                     }, display.getSelectedBucketId());
                  }
                  catch (RequestException e)
                  {
                     e.printStackTrace();
                  }
               }
            }
         });
   }
}
