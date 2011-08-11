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
package org.exoplatform.cloudshell.client.commands;

import com.google.gwt.user.client.Window;

import org.exoplatform.cloudshell.client.CloudShell;
import org.exoplatform.cloudshell.client.EnvironmentVariables;
import org.exoplatform.cloudshell.client.cli.CommandLine;
import org.exoplatform.cloudshell.client.cli.Options;
import org.exoplatform.cloudshell.client.model.ClientCommand;
import org.exoplatform.gwtframework.commons.xml.QName;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileCallback;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 11, 2011 evgen $
 *
 */
public class CatCommand extends ClientCommand
{

   private static final Set<String> commads = new HashSet<String>();

   static
   {
      commads.add("cat");
   }

   private List<String> files;

   private StringBuilder out = new StringBuilder();

   /**
    * 
    */
   public CatCommand()
   {
      super(commads, new Options(), "Concatenate files and print on the console");
   }

   /**
    * @see org.exoplatform.cloudshell.client.model.ClientCommand#execute(org.exoplatform.cloudshell.client.cli.CommandLine)
    */
   @Override
   public void execute(CommandLine commandLine)
   {
      List<String> args = commandLine.getArgList();
      args.remove(0);
      files = args;
      out.setLength(0);
      getNextContent();
   }

   private void getNextContent()
   {
      String workdir = VirtualFileSystem.getInstance().getEnvironmentVariable(EnvironmentVariables.WORKDIR);
      if (files.size() != 0)
      {

         final File f = new File(Utils.getPath(workdir, files.get(0)));

         List<QName> prop = new ArrayList<QName>();
         prop.add(ItemProperty.GETCONTENTTYPE);
         VirtualFileSystem.getInstance().getProperties(f, prop, new ItemPropertiesCallback()
         {

            @Override
            protected void onSuccess(Item result)
            {
               if (result.getProperty(ItemProperty.GETCONTENTTYPE) != null)
                  VirtualFileSystem.getInstance().getContent(f, new FileCallback()
                  {
                     @Override
                     protected void onSuccess(File result)
                     {
                        String content = result.getContent();
                        content = content.replaceAll("<", "&lt;");
                        content = content.replaceAll(">", "&gt;");
                        out.append(content);
                        out.append("\n");
                        files.remove(0);
                        getNextContent();
                     }

                     /**
                      * @see org.exoplatform.ide.client.framework.vfs.FileCallback#onFailure(java.lang.Throwable)
                      */
                     @Override
                     protected void onFailure(Throwable exception)
                     {
                        CloudShell.console().print("Can't get file content" + "\n");
                     }
                  });
               else
               {
                  CloudShell.console().print(files.get(0) + " is a directory\n");
               }

            }

            /**
             * @see org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback#onFailure(java.lang.Throwable)
             */
            @Override
            protected void onFailure(Throwable exception)
            {
               CloudShell.console().print(files.get(0) + " not found\n");
            }
         });

      }
      else
      {
         CloudShell.console().print(out.toString());
      }

   }

}
