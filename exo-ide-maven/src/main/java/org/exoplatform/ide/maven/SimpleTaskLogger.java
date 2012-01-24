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
package org.exoplatform.ide.maven;

import org.apache.maven.shared.invoker.InvocationOutputHandler;

import java.io.Reader;
import java.io.StringReader;

/**
 * Implementation of TaskLogger that appends logs in StringBuffer.
 *
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class SimpleTaskLogger implements TaskLogger
{
   /** Logs buffer. */
   private final StringBuffer logBuf;
   /**
    * InvocationOutputHandler for proxying log messages.
    * It may be useful if need store log messages in this TaskLogger and at the same time print them to stdout.
    */
   private final InvocationOutputHandler delegate;

   /**
    * @param delegate instance of InvocationOutputHandler for proxying of logs. This parameter may be <code>null</code>
    * if not need to proxying of log messages
    */
   public SimpleTaskLogger(InvocationOutputHandler delegate)
   {
      this.logBuf = new StringBuffer();
      this.delegate = delegate;
   }

   public SimpleTaskLogger()
   {
      this(null);
   }

   /** @see org.codehaus.plexus.util.cli.StreamConsumer#consumeLine(java.lang.String) */
   public void consumeLine(String line)
   {
      if (line != null)
      {
         logBuf.append(line);
      }
      logBuf.append('\n');

      if (delegate != null)
      {
         delegate.consumeLine(line);
      }
   }

   /** @see org.exoplatform.ide.maven.TaskLogger#getLogReader() */
   @Override
   public Reader getLogReader()
   {
      return new StringReader(getLogAsString());
   }

   /** @see org.exoplatform.ide.maven.TaskLogger#getLogAsString() */
   @Override
   public String getLogAsString()
   {
      return logBuf.toString();
   }

   /** @see org.exoplatform.ide.maven.TaskLogger#close() */
   @Override
   public void close()
   {
   }
}