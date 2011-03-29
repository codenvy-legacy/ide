/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.operation.ui;

import org.exoplatform.ide.client.framework.output.event.OutputMessage;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OutputRecord extends HTML implements MouseOutHandler, MouseOverHandler
{

   private static final String LOG_COLOR = "#000077";

   private static final String INFO_COLOR = "#007700";

   private static final String WARNING_COLOR = "#AA0077";

   private static final String ERROR_COLOR = "#880000";

   private static final String OUTPUT_COLOR = "#000088";

   private static final String EVEN_BACKGROUND = "#FFFFFF";

   //   private static final String ODD_BACKGROUND = "#ff9999";
   private static final String ODD_BACKGROUND = "#f8f8f8";

   private static final String OVER_BACKGROUND = "#D9E8FF";

   private String backgroundColor;

   public OutputRecord(OutputMessage message, boolean odd)
   {
      if (message.getType() == OutputMessage.Type.LOG)
      {
         setContents("<font color=\"" + LOG_COLOR + "\">[" + OutputMessage.Type.LOG.name() + "] "
            + message.getMessage() + "</font>");
      }
      else if (message.getType() == OutputMessage.Type.INFO)
      {
         setContents("<font color=\"" + INFO_COLOR + "\">[" + OutputMessage.Type.INFO.name() + "] "
            + message.getMessage() + "</font>");
      }
      else if (message.getType() == OutputMessage.Type.WARNING)
      {
         setContents("<font color=\"" + WARNING_COLOR + "\">[" + OutputMessage.Type.WARNING.name() + "] "
            + message.getMessage() + "</font>");
      }
      else if (message.getType() == OutputMessage.Type.ERROR)
      {
         setContents("<font color=\"" + ERROR_COLOR + "\">[" + OutputMessage.Type.ERROR.name() + "] "
            + message.getMessage() + "</font>");
      }
      else if (message.getType() == OutputMessage.Type.OUTPUT)
      {
         setContents("<font color=\"" + OUTPUT_COLOR + "\">[" + OutputMessage.Type.OUTPUT.name() + "] "
            + message.getMessage() + "</font>");
      }

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
      addMouseOverHandler(this);
      addMouseOutHandler(this);
   }

   /**
    * @param string
    */
   private void setContents(String html)
   {
      String table =
         "<table cellpadding=\"3\" cellspacing=\"0\" border=\"0\"><tr>" +
            "<td style=\"" +
               "font-family: Verdana,Bitstream Vera Sans,sans-serif;" +
               "font-size: 11px;" +
               "font-style: normal;" +
               "font-weight: normal" +
               "\">" + html + "</td></tr></table>";
      getElement().setInnerHTML(table);
   }

   /**
    * @param backgroundColor
    */
   private void setBackgroundColor(String backgroundColor)
   {
      DOM.setStyleAttribute(getElement(), "background", backgroundColor);
   }

   /**
    * @see com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent)
    */
   @Override
   public void onMouseOver(MouseOverEvent event)
   {
      setBackgroundColor(OVER_BACKGROUND);
   }

   /**
    * @see com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google.gwt.event.dom.client.MouseOutEvent)
    */
   @Override
   public void onMouseOut(MouseOutEvent event)
   {
      setBackgroundColor(backgroundColor);
   }

}
