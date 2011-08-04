package org.exoplatform.cloudshell.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

import org.exoplatform.cloudshell.client.crash.CRaSHClientService;
import org.exoplatform.cloudshell.client.crash.CRaSHOutputAsyncRequestCallback;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CloudShell implements EntryPoint
{

   private static final Messages messages = GWT.create(Messages.class);

   /**
    * This is the entry point method.
    */

   private static ConsoleWriter consoleWriter;

   public void onModuleLoad()
   {
      CRaSHClientService.getService().welcome(new CRaSHOutputAsyncRequestCallback());
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
}
