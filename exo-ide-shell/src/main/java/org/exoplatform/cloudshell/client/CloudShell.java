package org.exoplatform.cloudshell.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.Messages;

import org.exoplatform.cloudshell.client.commands.CatCommand;
import org.exoplatform.cloudshell.client.commands.CdCommand;
import org.exoplatform.cloudshell.client.commands.ClearCommand;
import org.exoplatform.cloudshell.client.commands.HelpCommand;
import org.exoplatform.cloudshell.client.commands.LsCommand;
import org.exoplatform.cloudshell.client.commands.MkdirCommand;
import org.exoplatform.cloudshell.client.commands.PwdCommand;
import org.exoplatform.cloudshell.client.commands.RmCommand;
import org.exoplatform.cloudshell.shared.CLIResource;
import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.module.vfs.webdav.WebDavVirtualFileSystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CloudShell implements EntryPoint
{
   private static final Messages messages = GWT.create(Messages.class);

   private static ConsoleWriter consoleWriter;

   private static Set<CLIResource> commands = new HashSet<CLIResource>();

   public static final String ENTRY_POINT = GWT.getModuleBaseURL() + "rest/private/jcr/repository/dev-monit/";

   
   public static final HandlerManager EVENT_BUS = new HandlerManager(null);

   public void onModuleLoad()
   {
      new WebDavVirtualFileSystem(EVENT_BUS, new EmptyLoader(), new HashMap<String, String>(), "/rest/private/");
      VirtualFileSystem.getInstance().setEnvironmentVariable(EnvironmentVariables.WORKDIR, ENTRY_POINT);

      consoleWriter = new ShellPresenter();

      ShellService.getService().getCommands(new AsyncRequestCallback<Set<CLIResource>>()
      {
         @Override
         protected void onSuccess(Set<CLIResource> result)
         {
            commands.addAll(result);
            login();
         }
      });
      
      commands.add(new LsCommand());
      commands.add(new HelpCommand());
      commands.add(new MkdirCommand());
      commands.add(new PwdCommand());
      commands.add(new CdCommand());
      commands.add(new RmCommand());
      commands.add(new ClearCommand());
      commands.add(new CatCommand());
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

   private void login()
   {
      String command = "ws login dev-monit";
      ShellService.getService().login(command, new AsyncRequestCallback<String>()
      {

         @Override
         protected void onSuccess(String result)
         {
            console().print("Welcome to eXo Cloud Shell\n" + result);
         }
         
         /**
          * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
          */
         @Override
         protected void onFailure(Throwable exception)
         {
            console().print(exception.getMessage());
         }
      });
   }
}
