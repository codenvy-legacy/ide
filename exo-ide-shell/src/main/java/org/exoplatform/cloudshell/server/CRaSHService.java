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
package org.exoplatform.cloudshell.server;

import org.crsh.plugin.PluginContext;
import org.crsh.plugin.WebPluginLifeCycle;
import org.crsh.shell.Shell;
import org.crsh.shell.concurrent.SyncShellResponseContext;
import org.crsh.shell.impl.CRaSH;
import org.crsh.util.Strings;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.DenyAll;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * REST wrapper for CRaSH Shell
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 1, 2011 evgen $
 *
 */
@Path("ide/crash")
public class CRaSHService
{
   /**
    * 
    */
   private static final String JCOMMAND_PREFIX = "jcr ";

   private PluginContext pluginContext;

   private HttpSession httpSession;

   /**
    * 
    */
   public CRaSHService(@Context HttpServletRequest httpServletRequest, @Context ServletContext servletContext)
   {
      this.httpSession = httpServletRequest.getSession();
      pluginContext = WebPluginLifeCycle.getPluginContext(servletContext);
   }

   private Shell getShell()
   {
      CRaSH shell = (CRaSH)httpSession.getAttribute(CRaSHService.class.getName());
      if (shell == null)
      {
         shell = new CRaSH(pluginContext);
         httpSession.setAttribute(CRaSHService.class.getName(), shell);
      }
      return shell;
   }

   @GET
   @Path("welcome")
   @DenyAll
   public String getWelcome()
   {
      Shell shell = getShell();
      return shell.getWelcome() + "\n" + shell.getPrompt();
   }

   @POST
   @Path("command")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.TEXT_PLAIN)
   public String processCommand(HashMap<String, String> command)
   {
      Shell shell = getShell();
      StringBuilder sb = new StringBuilder();
      try
      {
         String cmd = command.get("cmd");
         if(cmd.startsWith(JCOMMAND_PREFIX))
         {
            cmd = cmd.substring(JCOMMAND_PREFIX.length());
         }
         SyncShellResponseContext resp = new SyncShellResponseContext();
         shell.createProcess(cmd).execute(resp);
         String text = resp.getResponse().getText();
         sb.append(text);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
         sb.append("failure ").append(e.getMessage());
      }
      sb.append('\n');
      return sb.toString();
   }

   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("complete")
   @DenyAll
   public Map<String, String> complete(String s)
   {
      Shell shell = getShell();

      // Obtain completions from the shell
      Map<String, String> completions = shell.complete(s);

      // Try to find the greatest prefix among all the results
      String commonCompletion;
      if (completions.size() == 0)
      {
         commonCompletion = "";
      }
      else if (completions.size() == 1)
      {
         Map.Entry<String, String> entry = completions.entrySet().iterator().next();
         commonCompletion = entry.getKey() + entry.getValue();
      }
      else
      {
         commonCompletion = Strings.findLongestCommonPrefix(completions.keySet());
      }

      // Use our hashmap so we are sure it will be correctly serialized
      Map<String, String> ret = new HashMap<String, String>();
      if (commonCompletion.length() > 0)
      {
         ret.put(commonCompletion, "");
      }
      else
      {
         if (completions.size() > 1)
         {
            ret.putAll(completions);
         }
      }
      return ret;
   }

}
