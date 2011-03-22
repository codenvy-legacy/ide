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
package org.exoplatform.ide.git.shared;


/**
 * Request to reset current HEAD to the specified state.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ResetRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class ResetRequest extends GitRequest
{
   /**
    * Type of reset operation.
    */
   public enum ResetType {
      /**
       * Resets the index but not the working tree (default).
       */
      MIXED {
         @Override
         public String toString()
         {
            return "--mixed";
         }
      },
      /**
       * Does not touch the index file nor the working tree at all.
       */
      SOFT {
         @Override
         public String toString()
         {
            return "--soft";
         }
      },
      /**
       * Resets the working tree and index to that of the tree being switched
       * to.
       */
      HARD {
         @Override
         public String toString()
         {
            return "--hard";
         }
      }
   }

   /**
    * Commit to which current head should be reset, e.g. 'HEAD^' one commit back
    * in history.
    */
   private String commit;

   /**
    * Type of reset.
    * 
    * @see ResetType
    */
   private ResetType type = ResetType.MIXED;

   /**
    * Files to reset in index. Content of files is untouched. Typically it is
    * useful to remove from index mistakenly added files. If paths is not
    * <code>null</code> or empty list then {@link #type} may not be other than
    * {@link ResetType#MIXED}.
    */
   private String[] paths;

   /**
    * @param commit commit to which current head should be reset
    * @param type type of reset
    * @see #commit
    * @see #type
    */
   public ResetRequest(String commit, ResetType type)
   {
      this.commit = commit;
      this.type = type;
   }

   /**
    * @param paths files to reset in index. Content of files is untouched.
    *           Typically it is useful to remove from index mistakenly added
    *           files. If paths is not <code>null</code> or empty list then
    *           {@link #type} may not be other than {@link ResetType#MIXED}
    */
   public ResetRequest(String[] paths)
   {
      this.setPaths(paths);
      this.commit = "HEAD";
      this.type = ResetType.MIXED;
   }

   /**
    * "Empty" reset request. Corresponding setters used to setup required
    * parameters.
    */
   public ResetRequest()
   {
   }

   /**
    * @return commit to which current head should be reset
    * @see #commit
    */
   public String getCommit()
   {
      return commit;
   }

   /**
    * @param commit commit to which current head should be reset
    * @see #commit
    */
   public void setCommit(String commit)
   {
      this.commit = commit;
   }

   /**
    * @return type of reset.
    * @see ResetType
    */
   public ResetType getType()
   {
      return type;
   }

   /**
    * @param type type of reset.
    * @see ResetType
    */
   public void setType(ResetType type)
   {
      this.type = type;
   }

   /**
    * @return files to reset in index
    * @see #paths
    */
   public String[] getPaths()
   {
      return paths;
   }

   /**
    * @param paths files to reset in index
    * @see #paths
    */
   public void setPaths(String[] paths)
   {
      this.paths = paths;
   }
}
