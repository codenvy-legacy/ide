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
package org.exoplatform.ide.git.core;

import org.exoplatform.ide.core.AbstractTestModule;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 24, 2011 11:57:52 AM anya $
 *
 */
public class Status extends AbstractTestModule
{
   public interface Messages
   {
      String NOT_COMMITED = "Changes to be committed:";

      String UNTRACKED = "Untracked files:";

      String NOT_UPDATED = "Changed but not updated:";

      String NEW_FILE = "new file: %s";
      
      String MODIFIED = "modified: %s";

      String NOTHING_TO_COMMIT = "nothing to commit";
   }

   public List<String> getNotCommited(String message)
   {
      List<String> files = new ArrayList<String>();
      message = message.replaceAll("#", "");
      String[] lines = message.split("\n");
      boolean notCommited = false;
      for (String line : lines)
      {
         line = line.trim();
         if (line.isEmpty())
         {
            continue;
         }
         if (line.startsWith(Messages.NOT_COMMITED))
         {
            notCommited = true;
            continue;
         }
         else if ((line.startsWith(Messages.UNTRACKED) && notCommited)
            || (line.startsWith(Messages.NOT_UPDATED) && notCommited))
         {
            return files;
         }
         if (notCommited)
         {
            files.add(line);
         }
      }
      return files;
   }

   public List<String> getNotUdated(String message)
   {
      List<String> files = new ArrayList<String>();
      message = message.replaceAll("#", "");
      String[] lines = message.split("\n");
      boolean notUpdated = false;
      for (String line : lines)
      {
         line = line.trim();
         if (line.isEmpty())
         {
            continue;
         }
         if (line.startsWith(Messages.NOT_UPDATED))
         {
            notUpdated = true;
            continue;
         }
         else if ((line.startsWith(Messages.NOT_COMMITED) && notUpdated)
            || (line.startsWith(Messages.UNTRACKED) && notUpdated))
         {
            return files;
         }
         if (notUpdated)
         {
            files.add(line);
         }
      }
      return files;
   }

   public List<String> getUntracked(String message)
   {
      List<String> files = new ArrayList<String>();
      message = message.replaceAll("#", "");
      String[] lines = message.split("\n");
      boolean untracked = false;
      for (String line : lines)
      {
         line = line.trim();
         if (line.isEmpty())
         {
            continue;
         }
         if (line.startsWith(Messages.UNTRACKED))
         {
            untracked = true;
            continue;
         }
         else if ((line.startsWith(Messages.NOT_COMMITED) && untracked)
            || (line.startsWith(Messages.NOT_UPDATED) && untracked))
         {
            return files;
         }
         if (untracked)
         {
            files.add(line);
         }
      }
      return files;
   }
}
