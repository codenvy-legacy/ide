package org.exoplatform.cloudshell.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

import org.exoplatform.cloudshell.shared.CLIResource;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;

import java.util.Set;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CloudShell implements EntryPoint
{
   private static final Messages messages = GWT.create(Messages.class);

   private static ConsoleWriter consoleWriter;

   private static Set<CLIResource> commands;

   public void onModuleLoad()
   {
      consoleWriter = new ShellPresenter();

      ShellService.getService().getCommands(new AsyncRequestCallback<Set<CLIResource>>()
      {
         @Override
         protected void onSuccess(Set<CLIResource> result)
         {
            commands = result;
         }
      });

   }

   /**
    * @return {@link ConsoleWriter}
    */
   public static ConsoleWriter console()
   {
      if (consoleWriter == null)
      {
         consoleWriter = new ShellPresenter();
      }
      return consoleWriter;
   }

   public static Set<CLIResource> getCommands()
   {
      return commands;
   }
}
