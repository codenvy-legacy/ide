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
package org.exoplatform.ide.git.server.jgit;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.exoplatform.ide.git.server.InfoPage;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: LogPage.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
class LogPage implements InfoPage
{
   // The same as C git does.
   private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss yyyy ZZZZZ";

   private final Iterator<RevCommit> commits;

   LogPage(Iterator<RevCommit> commits)
   {
      this.commits = commits;
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

      while (commits.hasNext())
      {
         RevCommit commit = commits.next();

         writer.format("commit %1s\n", commit.getId().name());

         PersonIdent commiter = commit.getCommitterIdent();
         if (commiter != null)
         {
            writer.format("Author: %1s <%2s>\n", commiter.getName(), commiter.getEmailAddress());
            writer.format("Date:   %1s\n", dateFormat.format(commiter.getWhen()));
         }

         writer.println();

         // Message with indent.
         String[] lines = commit.getFullMessage().split("\n");
         for (String line : lines)
            writer.format("    %1s\n", line);

         writer.println();
      }
      writer.flush();
   }
}
