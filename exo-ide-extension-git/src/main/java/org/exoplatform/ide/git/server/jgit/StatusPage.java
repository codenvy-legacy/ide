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

import org.exoplatform.ide.git.server.InfoPage;
import org.exoplatform.ide.git.shared.StatusRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: StatusPage.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
class StatusPage implements InfoPage
{
   private final StatusRequest request;

   final String branchName;

   final List<GitFile> changedNotUpdated;
   final List<GitFile> changedNotCommited;
   final List<GitFile> untracked;

   /**
    * @param branchName
    * @param changedNotUpdated
    * @param changedNotCommited
    * @param untracked
    * @param request
    */
   StatusPage(String branchName, List<GitFile> changedNotUpdated, List<GitFile> changedNotCommited,
      List<GitFile> untracked, StatusRequest request)
   {
      this.branchName = branchName;
      this.changedNotUpdated = changedNotUpdated;
      this.changedNotCommited = changedNotCommited;
      this.untracked = untracked;
      this.request = request;
   }

   /**
    * @see org.exoplatform.ide.git.server.InfoPage#writeTo(java.io.OutputStream)
    */
   public void writeTo(OutputStream out) throws IOException
   {
      if (request.isShortFormat())
         writeShortStatus(out);
      else
         writeStatus(out);
   }

   private void writeShortStatus(OutputStream out)
   {
      if ((changedNotUpdated == null || changedNotUpdated.isEmpty()) //
         && (changedNotCommited == null || changedNotCommited.isEmpty()) //
         && (untracked == null || untracked.isEmpty())) //
         return;

      PrintWriter writer = new PrintWriter(out);

      if (changedNotUpdated != null && !changedNotUpdated.isEmpty())
      {
         for (GitFile f : changedNotUpdated)
            writer.format(" %1s %2s\n", f.getStatus().getShortStatus(), f.getPath());
      }

      if (changedNotCommited != null && !changedNotCommited.isEmpty())
      {
         for (GitFile f : changedNotCommited)
            writer.format("%1s  %2s\n", f.getStatus().getShortStatus(), f.getPath());
      }

      if (untracked != null && !untracked.isEmpty())
      {
         for (GitFile f : untracked)
            writer.format("%1s %2s\n", f.getStatus().getShortStatus(), f.getPath());
      }

      writer.flush();
   }

   private void writeStatus(OutputStream out) throws IOException
   {
      PrintWriter writer = new PrintWriter(out);

      writer.format("# On branch %1s\n", branchName);
      
      if ((changedNotUpdated == null || changedNotUpdated.isEmpty()) //
         && (changedNotCommited == null || changedNotCommited.isEmpty()) //
         && (untracked == null || untracked.isEmpty())) //
      {
         writer.println("nothing to commit");
      }
      else
      {
         if (changedNotCommited != null && !changedNotCommited.isEmpty())
         {
            writer.println("# Changes to be committed:");
            writer.println('#');
            for (GitFile f : changedNotCommited)
               writer.format("#       %1s:    %2s\n", f.getStatus().getLongStatus(), f.getPath());
         }
         writer.println('#');

         if (changedNotUpdated != null && !changedNotUpdated.isEmpty())
         {
            writer.println("# Changed but not updated:");
            writer.println('#');
            for (GitFile f : changedNotUpdated)
               writer.format("#       %1s:    %2s\n", f.getStatus().getLongStatus(), f.getPath());
            writer.println('#');
         }

         if (untracked != null && !untracked.isEmpty())
         {
            writer.println("# Untracked files:");
            writer.println('#');
            for (GitFile f : untracked)
               writer.format("#       %1s\n", f.getPath());
         }
      }
      writer.flush();
   }
}
