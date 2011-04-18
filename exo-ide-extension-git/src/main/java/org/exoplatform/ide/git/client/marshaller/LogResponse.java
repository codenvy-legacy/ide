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
package org.exoplatform.ide.git.client.marshaller;

import org.exoplatform.ide.git.shared.Log;

/**
 * The response with the log of commits.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 14, 2011 4:32:17 PM anya $
 *
 */
public class LogResponse extends Log
{
   /**
    * The text format of the log response.
    */
   private String textLog;

   /**
    * @return the textLog text format of the log response
    */
   public String getTextLog()
   {
      return textLog;
   }

   /**
    * @param textLog the textLog text format of the log response
    */
   public void setTextLog(String textLog)
   {
      this.textLog = textLog;
   }
}
