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
package org.exoplatform.ide.shell.client.commands;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.shell.client.CloudShell;
import org.exoplatform.ide.shell.client.Environment;
import org.exoplatform.ide.shell.client.cli.CommandLine;
import org.exoplatform.ide.shell.client.cli.Options;
import org.exoplatform.ide.shell.client.model.ClientCommand;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 10, 2011 evgen $
 *
 */
public class LsCommand extends ClientCommand
{

   private static final Set<String> commads = new HashSet<String>();

   private static final String TAB = "  ";

   static
   {
      commads.add("ls");
   }

   /**
    * 
    */
   public LsCommand()
   {
      super(commads, new Options(), CloudShell.messages.lsHelp());
   }

   /**
    * @see org.exoplatform.ide.shell.client.model.ClientCommand#execute(org.exoplatform.ide.shell.client.cli.CommandLine)
    */
   @Override
   public void execute(CommandLine commandLine)
   {
      List<String> args = commandLine.getArgList();
      args.remove(0);

      if (commandLine.hasOption("h"))
      {
         printHelp(CloudShell.messages.lsUsage(), CloudShell.messages.lsHeader());
         return;
      }
      FolderModel current = Environment.get().getCurrentFolder();
      if (args.size() == 0)
      {
         getFolderContent(current);
      }
      else
      {

         String path = Utils.getPath(current, args.get(0));
         CloudShell.console().println(path);
         //TODO 

      }

   }

   private void getFolderContent(final FolderModel folder)
   {
      try
      {
         VirtualFileSystem.getInstance().getChildren(folder,
            new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
            {

               @Override
               protected void onSuccess(List<Item> result)
               {
                  CloudShell.console().println(fomatItems(result));
               }

               /**
                * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
                */
               @Override
               protected void onFailure(Throwable exception)
               {
                  CloudShell.console().println(CloudShell.messages.lsError(folder.getPath()));
               }
            });
      }
      catch (RequestException e)
      {
         CloudShell.console().println(CloudShell.messages.lsError(folder.getPath()));
         e.printStackTrace();
      }
   }

   /**
    * Format items in several columns. Main purpose is reducing terminal space.
    * @param items
    * @return
    */
   private String fomatItems(List<Item> items)
   {
      List<List<Item>> table = new ArrayList<List<Item>>();
      StringBuilder result = new StringBuilder();
      List<StringBuilder> strings = new ArrayList<StringBuilder>();

      int splitCount = 1;
      boolean formatComplete = false;
      do
      {
         table.clear();
         strings.clear();
         int i = 0;
         while (i < items.size())
         {
            if ((i + splitCount) > items.size())
               table.add(items.subList(i, items.size()));

            else
               table.add(items.subList(i, i + splitCount));
            i += splitCount;
         }
         int currentMaxLenght = 0;

         int lineLength[] = new int[splitCount];
         for (List<Item> list : table)
         {
            int maxLen = getMaxNameLength(list);
            for (int j = 0; j < list.size(); j++)
            {
               Item item = list.get(j);
               String name = item.getName();
               if (strings.size() <= j)
               {
                  strings.add(new StringBuilder());
               }
               char chars[] = new char[maxLen - name.length()];
               Arrays.fill(chars, (char)' ');
               StringBuilder builder = strings.get(j).append(TAB);
               if (item instanceof FolderModel)
               {
                  builder.append("<span style=\"color:#246fd5;\">").append(name).append("</span>");
               }
               else
                  builder.append(name);
               builder.append(chars);
               // line may contains some HTML code, we need count only symbols that displaying on terminal
               lineLength[j] += TAB.length() + name.length() + chars.length;
            }
         }

         for (int in : lineLength)
         {
            if (in > currentMaxLenght)
               currentMaxLenght = in;
         }
         if (currentMaxLenght > CloudShell.console().getLengts())
         {
            formatComplete = true;
            splitCount++;
         }
         else
            formatComplete = false;

      }
      while (formatComplete);

      for (StringBuilder b : strings)
      {
         result.append(b.toString()).append("\n");
      }

      return result.toString();
   }

   /**
    * Get longest name length 
    * @param items
    * @return
    */
   private int getMaxNameLength(List<Item> items)
   {
      int max = 0;
      for (Item i : items)
      {
         if (i.getName().length() > max)
         {
            max = i.getName().length();
         }
      }
      return max;
   }

}
