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
package org.exoplatform.ide.git.server;

import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.ide.git.shared.Log;
import org.exoplatform.ide.git.shared.Revision;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class LogPage extends Log
{
   // The same as C git does.
   private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss yyyy ZZZZZ";

   public LogPage(List<Revision> commits)
   {
      super(commits);
   }

   /**
    * @see org.exoplatform.ide.git.LogPage#writeTo(java.io.OutputStream)
    */
   @Override
   public void writeTo(OutputStream out) throws IOException
   {
      // Default behavior only at the moment. 
      PrintWriter writer = new PrintWriter(out);
      DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
      TimeZone timeZone = TimeZone.getDefault();
      dateFormat.setTimeZone(timeZone);

      for (Revision commit : commits)
      {
         writer.format("commit %1s\n", commit.getId());

         GitUser commiter = commit.getCommitter();
         if (commiter != null)
            writer.format("Author: %1s <%2s>\n", commiter.getName(), commiter.getEmail());

         long commitTime = commit.getCommitTime();
         if (commitTime > 0)
            writer.format("Date:   %1s\n", dateFormat.format(new Date(commitTime)));

         writer.println();

         // Message with indent.
         String[] lines = commit.getMessage().split("\n");
         for (String line : lines)
            writer.format("    %1s\n", line);

         writer.println();
      }
      writer.flush();
   }
}
