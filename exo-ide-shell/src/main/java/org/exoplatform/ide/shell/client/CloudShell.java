package org.exoplatform.ide.shell.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.shell.client.ConsoleWriter;
import org.exoplatform.ide.shell.client.Messages;
import org.exoplatform.ide.shell.client.ShellInitializer;
import org.exoplatform.ide.shell.client.ShellPresenter;
import org.exoplatform.ide.shell.client.commands.CatCommand;
import org.exoplatform.ide.shell.client.commands.CdCommand;
import org.exoplatform.ide.shell.client.commands.ClearCommand;
import org.exoplatform.ide.shell.client.commands.HelpCommand;
import org.exoplatform.ide.shell.client.commands.LsCommand;
import org.exoplatform.ide.shell.client.commands.MkdirCommand;
import org.exoplatform.ide.shell.client.commands.PwdCommand;
import org.exoplatform.ide.shell.client.commands.RmCommand;
import org.exoplatform.ide.shell.shared.CLIResource;

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
