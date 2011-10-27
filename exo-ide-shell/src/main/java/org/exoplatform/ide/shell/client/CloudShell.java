package org.exoplatform.ide.shell.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

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
   }

   /**
    * @return {@link ConsoleWriter}
    */
   public static ConsoleWriter console()
   {
      return consoleWriter;
   }

   public static Set<CLIResource> getCommands()
   {
      return commands;
   }

}
