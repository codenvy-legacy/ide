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

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Repository;
import org.exoplatform.ide.git.server.DiffPage;
import org.exoplatform.ide.git.shared.DiffRequest;
import org.exoplatform.ide.git.shared.DiffRequest.DiffType;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: DiffPage.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
class JGitDiffPage extends DiffPage
{
   private final List<DiffEntry> diff;
   private final DiffRequest request;
   private final Repository repository;

   /**
    * @param diff
    * @param request
    * @param repository
    */
   JGitDiffPage(List<DiffEntry> diff, DiffRequest request, Repository repository)
   {
      this.diff = diff;
      this.request = request;
      this.repository = repository;
   }

   @Override
   public void writeTo(OutputStream out) throws IOException
   {
      DiffType type = request.getType();
      try
      {
         if (type == DiffType.NAME_ONLY)
            writeNames(out);
         else if (type == DiffType.NAME_STATUS)
            writeNamesAndStatus(out);
         else
            writeDiff(out);
      }
      finally
      {
         repository.close();
      }
   }

   private void writeDiff(OutputStream out) throws IOException
   {
      DiffFormatter formatter = new DiffFormatter(new BufferedOutputStream(out));
      formatter.setRepository(repository);
      formatter.format(diff);
      formatter.flush();
   }

   private void writeNames(OutputStream out) throws IOException
   {
      PrintWriter writer = new PrintWriter(out);
      for (DiffEntry de : diff)
         writer.println(de.getChangeType() == ChangeType.DELETE ? de.getOldPath() : de.getNewPath());
      writer.flush();
   }

   private void writeNamesAndStatus(OutputStream out) throws IOException
   {
      PrintWriter writer = new PrintWriter(out);
      for (DiffEntry de : diff)
      {
         if (de.getChangeType() == ChangeType.ADD)
            writer.println("A\t" + de.getNewPath());
         else if (de.getChangeType() == ChangeType.DELETE)
            writer.println("D\t" + de.getOldPath());
         else if (de.getChangeType() == ChangeType.MODIFY)
            writer.println("M\t" + de.getNewPath());
         else if (de.getChangeType() == ChangeType.COPY)
            writer.println("C\t" + de.getOldPath() + '\t' + de.getNewPath());
         else if (de.getChangeType() == ChangeType.RENAME)
            writer.println("R\t" + de.getOldPath() + '\t' + de.getNewPath());
      }
      writer.flush();
   }
}
