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
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 11, 2011 evgen $
 * 
 */
public class RmCommand extends ClientCommand
{
   private static final Set<String> commads = new HashSet<String>();

   static
   {
      commads.add("rm");
   }

   /**
    * 
    */
   public RmCommand()
   {
      super(commads, new Options(), CloudShell.messages.rmHelp());
   }

   /**
    * @see org.exoplatform.ide.shell.client.model.ClientCommand#execute(org.exoplatform.ide.shell.client.cli.CommandLine)
    */
   @Override
   public void execute(CommandLine commandLine)
   {
      if (commandLine.hasOption("h"))
      {
         printHelp(CloudShell.messages.rmUsage(), CloudShell.messages.rmHeader());
         return;
      }
      @SuppressWarnings("unchecked")
      List<String> args = commandLine.getArgList();
      args.remove(commads.iterator().next());
      if (args.size() == 1)
      {
         String path = args.get(0);
         final String newPath = Utils.getPath(Environment.get().getCurrentFolder(), path);
         try
         {
            VirtualFileSystem.getInstance().getItemByPath(newPath,
               new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper()))
               {

                  @Override
                  protected void onSuccess(ItemWrapper result)
                  {
                     deleteItem(result.getItem());
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     CloudShell.console().println(exception.getMessage());
                  }
               });
         }
         catch (RequestException e)
         {
            CloudShell.console().println(e.getMessage());
         }
      }
   }

   private void deleteItem(Item item)
   {
      try
      {
         VirtualFileSystem.getInstance().delete(item, new AsyncRequestCallback<String>()
         {

            @Override
            protected void onSuccess(String result)
            {
               CloudShell.console().printPrompt();
            }

            /**
             * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
             */
            @Override
            protected void onFailure(Throwable exception)
            {
               CloudShell.console().println(exception.getMessage());
            }
         });
      }
      catch (RequestException e)
      {
         CloudShell.console().println(e.getMessage());
      }
   }

}
