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
package org.exoplatform.ide.client.progress.ui;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.job.Job;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Sep 19, 2011 evgen $
 *
 */
public class JobWidget extends Composite
{
   private static final String EVEN_BACKGROUND = "#FFFFFF";

   private static final String ODD_BACKGROUND = "#f8f8f8";

   private static final String OVER_BACKGROUND = "#D9E8FF";

   private Grid body;

   private String backgroundColor;

   private Image image = new Image();

   private Label message = new Label();

   /**
    * 
    */
   protected JobWidget(boolean odd)
   {
      body = new Grid(1, 2);
      body.setWidget(0, 0, image);
      body.setWidget(0, 1, message);
      body.getCellFormatter().setWidth(0, 0, "16px");
      body.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
      body.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
      
      initWidget(body);

      if (odd)
      {
         backgroundColor = ODD_BACKGROUND;
      }
      else
      {
         backgroundColor = EVEN_BACKGROUND;
      }

      setBackgroundColor(backgroundColor);
      setWidth("100%");
      body.addDomHandler(new MouseOutHandler()
      {

         @Override
         public void onMouseOut(MouseOutEvent event)
         {
            setBackgroundColor(backgroundColor);
         }
      }, MouseOutEvent.getType());

      body.addDomHandler(new MouseOverHandler()
      {

         @Override
         public void onMouseOver(MouseOverEvent event)
         {
            setBackgroundColor(OVER_BACKGROUND);
         }
      }, MouseOverEvent.getType());
   }

   /**
    * 
    */
   public JobWidget(Job job, boolean odd)
   {
      this(odd);
      updateJob(job);
   }

   /**
    * @param job
    */
   public void updateJob(Job job)
   {
      switch (job.getStatus())
      {
         case STARTED :
            image.setResource(IDEImageBundle.INSTANCE.asyncRequest());
            message.getElement().setInnerHTML(job.getStartMessage());
            break;

         case FINISHED :
            image.setResource(IDEImageBundle.INSTANCE.ok());
            message.getElement().setInnerHTML(job.getFinishMessage());
            break;
         case ERROR :
            image.setResource(IDEImageBundle.INSTANCE.delete());
            message.setText(job.getError().getMessage());
            break;
      }
   }

   /**
    * @param backgroundColor
    */
   private void setBackgroundColor(String backgroundColor)
   {
      DOM.setStyleAttribute(getElement(), "background", backgroundColor);
   }
}
