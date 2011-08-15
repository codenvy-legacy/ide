package org.exoplatform.cloudshell.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.cloudshell.client.commands.CatCommand;
import org.exoplatform.cloudshell.client.commands.CdCommand;
import org.exoplatform.cloudshell.client.commands.ClearCommand;
import org.exoplatform.cloudshell.client.commands.HelpCommand;
import org.exoplatform.cloudshell.client.commands.LsCommand;
import org.exoplatform.cloudshell.client.commands.MkdirCommand;
import org.exoplatform.cloudshell.client.commands.PwdCommand;
import org.exoplatform.cloudshell.client.commands.RmCommand;
import org.exoplatform.cloudshell.shared.CLIResource;

import java.util.HashSet;
import java.util.Set;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CloudShell implements EntryPoint
{
   public static final Messages messages = GWT.create(Messages.class);

   static ConsoleWriter consoleWriter;

   private static Set<CLIResource> commands = new HashSet<CLIResource>();

   public static final HandlerManager EVENT_BUS = new HandlerManager(null);

   public void onModuleLoad()
   {
      new ShellInitializer().init();
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

}
