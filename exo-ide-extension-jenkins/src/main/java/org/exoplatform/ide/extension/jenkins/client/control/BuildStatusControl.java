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
package org.exoplatform.ide.extension.jenkins.client.control;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.gwtframework.ui.client.command.StatusTextControl;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.jenkins.client.JenkinsExtension;
import org.exoplatform.ide.extension.jenkins.shared.JobStatus;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class BuildStatusControl extends StatusTextControl implements IDEControl
{

   public static final String ID = "__jenkins_build_status";

   /**
    * @param id
    */
   public BuildStatusControl()
   {
      super(ID);
      setEnabled(true);
      setVisible(true);
      setSize(80);
      setText("&nbsp;");
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize(HandlerManager eventBus)
   {
   }

   public void updateStatus(JobStatus status)
   {
      ImageResource icon = null;
      String message = "";
      switch (status.getStatus())
      {
         case BUILD :
            icon = JenkinsExtension.RESOURCES.blue_anime();
            message = status.getStatus().toString();
            break;
         case QUEUE :
            icon = JenkinsExtension.RESOURCES.grey_anime();
            message = status.getStatus().toString();
            break;

         case END :
            icon = getIconForBuildResult(status.getLastBuildResult());
            message = status.getLastBuildResult();
            break;
      }
      setText(prepareText(message, icon));
   }

   /**
    * @param lastBuildResult
    * @return
    */
   private ImageResource getIconForBuildResult(String result)
   {
      if ("SUCCESS".equals(result))
         return JenkinsExtension.RESOURCES.blue();
      if ("UNSTABLE".equals(result))
         return JenkinsExtension.RESOURCES.yellow();
      if ("FAILURE".equals(result))
         return JenkinsExtension.RESOURCES.red();
      //if("NOT_BUILT".equals(result))
      //if ABORTED
      return JenkinsExtension.RESOURCES.grey();
   }

   private String prepareText(String message, ImageResource icon)
   {
      String table =
         "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"height:16px; border-collapse: collapse;\">"
            + "<tr>"
            +
            "<td style=\"width:16px; height:16px;\">"
            + ImageHelper.getImageHTML(icon)
            + "</td>"
            +
            "<td style=\"border: none; font-family:Verdana,Bitstream Vera Sans,sans-serif; font-size:11px; font-style:normal; \"><nobr>"
            + message + "</nobr></td>" + "</tr>" + "</table>";
      return table;
   }

   /**
    * 
    */
   public void setStartBuildingMessage()
   {
      setText(prepareText("Starting", JenkinsExtension.RESOURCES.grey()));
   }

}
