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
package org.exoplatform.ide.maven;

import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * File based TaskLogger.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class TaskLogger implements InvocationOutputHandler
{
   private static final Log LOG = ExoLogger.getLogger(TaskLogger.class);

   /** Log file. */
   private final File file;
   /** Log writer. */
   private final Writer writer;

   /**
    * InvocationOutputHandler for proxying log messages.
    * It may be useful if need store log messages in file and at the same time print them to stdout.
    */
   private final InvocationOutputHandler delegate;

   /**
    * @param file the log tile
    * @param delegate instance of InvocationOutputHandler for proxying of logs. This parameter may be <code>null</code>
    * if not need to proxying of log messages
    */
   public TaskLogger(File file, InvocationOutputHandler delegate)
   {
      this.file = file;
      this.delegate = delegate;
      try
      {
         writer = new BufferedWriter(new FileWriter(file, true));
      }
      catch (IOException ioe)
      {
         throw new RuntimeException(ioe.getMessage(), ioe);
      }
   }

   /** @param file the log tile */
   public TaskLogger(File file)
   {
      this(file, null);
   }

   /**
    * Get Reader of build log.
    *
    * @return reader
    * @throws java.io.IOException if any i/o errors occur
    */
   public Reader getLogReader() throws IOException
   {
      return new FileReader(file);
   }

   /** @see org.codehaus.plexus.util.cli.StreamConsumer#consumeLine(java.lang.String) */
   @Override
   public void consumeLine(String line)
   {
      try
      {
         if (line != null)
         {
            writer.write(line);
         }
         writer.write('\n');
      }
      catch (IOException e)
      {
         LOG.error(e.getMessage(), e);
      }

      if (delegate != null)
      {
         delegate.consumeLine(line);
      }
   }

   /** Close current TaskLogger. It should release any resources allocated by the TaskLogger such as file, etc. */
   public void close()
   {
      try
      {
         writer.close();
      }
      catch (IOException ignored)
      {
         // Ignore close error.
      }
   }

   /**
    * Get log file.
    *
    * @return the log file
    */
   public File getFile()
   {
      return file;
   }
}
