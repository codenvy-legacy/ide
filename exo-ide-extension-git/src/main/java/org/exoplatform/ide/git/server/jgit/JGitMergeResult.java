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

import org.eclipse.jgit.lib.ObjectId;
import org.exoplatform.ide.git.shared.MergeResult;

import java.util.Arrays;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: JGitMergeResult.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class JGitMergeResult implements MergeResult
{
   private final org.eclipse.jgit.api.MergeResult jgitMergeResult;

   /**
    * @param jgitMergeResult
    */
   public JGitMergeResult(org.eclipse.jgit.api.MergeResult jgitMergeResult)
   {
      this.jgitMergeResult = jgitMergeResult;
   }

   /**
    * @see org.exoplatform.ide.git.shared.MergeResult#getNewHead()
    */
   @Override
   public String getNewHead()
   {
      ObjectId newHead = jgitMergeResult.getNewHead();
      if (newHead != null)
         return newHead.getName();
      // Merge failed.
      return null;
   }

   /**
    * @see org.exoplatform.ide.git.shared.MergeResult#getMergeStatus()
    */
   @Override
   public MergeStatus getMergeStatus()
   {
      switch (jgitMergeResult.getMergeStatus())
      {
         case ALREADY_UP_TO_DATE :
            return MergeStatus.ALREADY_UP_TO_DATE;
         case CONFLICTING :
            return MergeStatus.CONFLICTING;
         case FAILED :
            return MergeStatus.FAILED;
         case FAST_FORWARD :
            return MergeStatus.FAST_FORWARD;
         case MERGED :
            return MergeStatus.MERGED;
         case NOT_SUPPORTED :
            return MergeStatus.NOT_SUPPORTED;
      }
      throw new IllegalStateException("Unknown merge status " + jgitMergeResult.getMergeStatus());
   }

   /**
    * @see org.exoplatform.ide.git.shared.MergeResult#getMergedCommits()
    */
   @Override
   public String[] getMergedCommits()
   {
      ObjectId[] jgitMergedCommits = jgitMergeResult.getMergedCommits();
      String[] mergedCommits = new String[jgitMergedCommits.length];
      for (int i = 0; i < jgitMergedCommits.length; i++)
         mergedCommits[i] = jgitMergedCommits[i].getName();
      return mergedCommits;
   }

   /**
    * @see org.exoplatform.ide.git.shared.MergeResult#getConflicts()
    */
   @Override
   public String[] getConflicts()
   {
      Map<String, int[][]> conflicts = jgitMergeResult.getConflicts();
      String[] files = null;
      if (conflicts != null)
      {
         files = new String[conflicts.size()];
         int i = 0;
         for (String file : conflicts.keySet())
            files[i++] = file;
      }
      return files;
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "JGitMergeResult [getNewHead()=" + getNewHead() + ", getMergeStatus()=" + getMergeStatus()
         + ", getMergedCommits()=" + Arrays.toString(getMergedCommits()) + ", getConflicts()="
         + Arrays.toString(getConflicts()) + "]";
   }
}
