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
package org.exoplatform.ide.extension.logreader.client.ui;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;

/**
 * Ui component for log piece.
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class LogRecord extends HTML implements MouseOutHandler, MouseOverHandler
{

   private static final String LOG_COLOR = "#000077";

   private static final String EVEN_BACKGROUND = "#FFFFFF";

   private static final String ODD_BACKGROUND = "#f8f8f8";

   private static final String OVER_BACKGROUND = "#D9E8FF";

   private String backgroundColor;

   public LogRecord(String message, boolean odd)
   {
      setContents("<pre style=\"color:" + LOG_COLOR + ";\"\">" + message + "</pre>");

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
         "<table cellpadding=\"3\" cellspacing=\"0\" border=\"0\"><tr>" + "<td style=\""
            + "font-family: Verdana,Bitstream Vera Sans,sans-serif;" + "font-size: 11px;" + "font-style: normal;"
            + "font-weight: normal" + "\">" + html + "</td></tr></table>";
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
