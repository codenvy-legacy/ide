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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.ListBox;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSError;
import org.exoplatform.ide.extension.aws.client.s3.events.BucketCreatedEvent;
import org.exoplatform.ide.extension.aws.shared.s3.S3Region;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: CreateBucketPresenter.java Sep 24, 2012 vetal $
 *
 */
public class CreateBucketPresenter implements ViewClosedHandler
{
   interface Display extends IsView
   {
      TextFieldItem getBucketName();

      ListBox getRegion();

      HasClickHandlers getCreateButton();

      HasClickHandlers getCancelButton();

      void enableCreateButton(boolean enable);

      void focusInName();

   }

   private Display display;

   public CreateBucketPresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getCreateButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doCreate();

         }
      });

      display.getBucketName().addKeyUpHandler(new KeyUpHandler()
      {

         @Override
         public void onKeyUp(KeyUpEvent event)
         {
            if (event.getNativeKeyCode() == 13 && isFieldsFullFilled())
            {
               doCreate();
            }
         }
      });

      display.getRegion().addKeyUpHandler(new KeyUpHandler()
      {

         @Override
         public void onKeyUp(KeyUpEvent event)
         {
            if (event.getNativeKeyCode() == 13 && isFieldsFullFilled())
            {
               doCreate();
            }
         }
      });

      display.getBucketName().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            display.enableCreateButton(isFieldsFullFilled());
         }
      });

      setRegions();
      display.enableCreateButton(false);

   }

   private void setRegions()
   {
      S3Region[] regions = S3Region.values();
      for (int i = 0; i < regions.length; i++)
      {
         S3Region s3Region = regions[i];
         display.getRegion().addItem(s3Region.toString());
      }

   }

   /**
    * @return {@link Boolean} <code>true</code> if fields are full filled
    */
   private boolean isFieldsFullFilled()
   {
      return (display.getBucketName().getValue() != null && !display.getBucketName().getValue().isEmpty());
   }

   /**
    * Perform login operation.
    */
   public void doCreate()
   {
      try
      {
         S3Service.getInstance()
            .createBucket(
               new AsyncRequestCallback<String>()
               {

                  @Override
                  protected void onSuccess(String result)
                  {
                     IDE.getInstance().closeView(display.asView().getId());
                     IDE.fireEvent(new BucketCreatedEvent());
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     IDE.getInstance().closeView(display.asView().getId());
                     AWSError awsError = new AWSError(exception.getMessage());
                     if (awsError.getAwsErrorMessage() != null)
                     {
                        Dialogs.getInstance().showError(
                           awsError.getAwsService() + " (" + awsError.getStatusCode() + ")",
                           awsError.getAwsErrorCode() + " : " + awsError.getAwsErrorMessage());
                     }
                     else
                     {
                        Dialogs.getInstance().showError("Amazon S3 Service", awsError.getAwsErrorMessage());
                     }
                  }

               }, display.getBucketName().getValue(),
               display.getRegion().getItemText(display.getRegion().getSelectedIndex()));
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
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

   public void onCreateBucket()
   {

      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
      }

   }
}