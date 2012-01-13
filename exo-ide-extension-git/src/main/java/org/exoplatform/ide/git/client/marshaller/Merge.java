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

import org.exoplatform.ide.git.shared.MergeResult;

/**
 * Represents the merge operation result.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 20, 2011 11:47:56 AM anya $
 * 
 */
public class Merge implements MergeResult
{
   /**
    * Commit head after merge.
    */
   private String newHead;

   /**
    * Status of merge operation.
    */
   private MergeStatus mergeStatus;

   /**
    * List of merged commits.
    */
   private String[] mergedCommits;

   /**
    * List of files with conflicts.
    */
   private String[] conflicts;

   /**
    * @see org.exoplatform.ide.git.shared.MergeResult#getNewHead()
    */
   @Override
   public String getNewHead()
   {
      return newHead;
   }

   /**
    * @see org.exoplatform.ide.git.shared.MergeResult#getMergeStatus()
    */
   @Override
   public MergeStatus getMergeStatus()
   {
      return mergeStatus;
   }

   /**
    * @see org.exoplatform.ide.git.shared.MergeResult#getMergedCommits()
    */
   @Override
   public String[] getMergedCommits()
   {
      return mergedCommits;
   }

   /**
    * @see org.exoplatform.ide.git.shared.MergeResult#getConflicts()
    */
   @Override
   public String[] getConflicts()
   {
      return conflicts;
   }

   /**
    * @param newHead the newHead to set
    */
   public void setNewHead(String newHead)
   {
      this.newHead = newHead;
   }

   /**
    * @param mergeStatus the mergeStatus to set
    */
   public void setMergeStatus(MergeStatus mergeStatus)
   {
      this.mergeStatus = mergeStatus;
   }

   /**
    * @param mergedCommits the mergedCommits to set
    */
   public void setMergedCommits(String[] mergedCommits)
   {
      this.mergedCommits = mergedCommits;
   }

   /**
    * @param conflicts the conflicts to set
    */
   public void setConflicts(String[] conflicts)
   {
      this.conflicts = conflicts;
   }
}
