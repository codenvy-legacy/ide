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
package org.exoplatform.ide.extension.maven;

import org.apache.maven.shared.invoker.InvocationOutputHandler;

import java.io.Reader;
import java.io.StringReader;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class SimpleTaskLogger implements TaskLogger
{
   private StringBuilder logBuf;
   private InvocationOutputHandler delegate;

   public SimpleTaskLogger(InvocationOutputHandler delegate)
   {
      this.logBuf = new StringBuilder();
      this.delegate = delegate;
   }

   public SimpleTaskLogger()
   {
      this(null);
   }

   /**
    * @see org.codehaus.plexus.util.cli.StreamConsumer#consumeLine(java.lang.String)
    */
   public void consumeLine(String line)
   {
      synchronized (logBuf)
      {
         if (line == null)
            logBuf.append('\n');
         else
            logBuf.append('\n').append(line);
      }
      if (delegate != null)
         delegate.consumeLine(line);
   }

   /**
    * @see org.exoplatform.ide.extension.maven.TaskLogger#getLogReader()
    */
   @Override
   public Reader getLogReader()
   {
      String log;
      synchronized (logBuf)
      {
         log = logBuf.toString();
         logBuf.setLength(0);
      }
      return new StringReader(log);
   }
}