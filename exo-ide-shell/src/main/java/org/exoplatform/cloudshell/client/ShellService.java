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
package org.exoplatform.cloudshell.client;

import com.google.gwt.json.client.JSONBoolean;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import org.exoplatform.cloudshell.client.cli.CommandLine;
import org.exoplatform.cloudshell.client.cli.GnuParser;
import org.exoplatform.cloudshell.client.cli.Parser;
import org.exoplatform.cloudshell.client.cli.Util;
import org.exoplatform.cloudshell.client.marshal.LoginMarshaller;
import org.exoplatform.cloudshell.client.marshal.StringUnmarshaller;
import org.exoplatform.cloudshell.client.model.ClientCommand;
import org.exoplatform.cloudshell.shared.CLIResource;
import org.exoplatform.cloudshell.shared.CLIResourceParameter;
import org.exoplatform.cloudshell.shared.CLIResourceParameter.Type;
import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.gwtframework.commons.rest.MimeType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Aug 4, 2011 4:31:13 PM anya $
 *
 */
public class ShellService
{
   private static ShellService service;

   private final String REST_CONTEXT = "rest/private";

   private final String RESOURCES_PATH = "ide/cli/resources";

   public static ShellService getService()
   {
      if (service == null)
      {
         service = new ShellService();
      }
      return service;
   }

   public void getCommands(AsyncRequestCallback<Set<CLIResource>> callback)
   {
      String url = REST_CONTEXT + "/" + RESOURCES_PATH;

      Set<CLIResource> resources = new HashSet<CLIResource>();

      CLIResourceUnmarshaller unmarshaller = new CLIResourceUnmarshaller(resources);
      callback.setResult(resources);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.GET, url, new EmptyLoader())
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   public void processCommand(String cmd, AsyncRequestCallback<String> callback)
   {
      List<CLIResource> appropriateCommands = findAppropriateCommands(cmd);
      if (appropriateCommands.size() <= 0)
      {
         CloudShell.console().print("No appropriate command is found for : " + cmd + "\n");
      }
      else if (appropriateCommands.size() == 1)
      {
         try
         {
            CLIResource resource = appropriateCommands.get(0);
            if (resource instanceof ClientCommand)
            {
               ClientCommand command = (ClientCommand)resource;
               Parser parser = new GnuParser();
               String[] arguments = Util.translateCommandline(cmd);
               CommandLine commandLine = parser.parse(command.getOptions(), arguments);
               command.execute(commandLine);
            }
            else
            {
               String url =
                  (resource.getPath().startsWith("/")) ? REST_CONTEXT + resource.getPath() : REST_CONTEXT + "/"
                     + resource.getPath();

               AsyncRequest asyncRequest = createAsyncRequest(resource.getMethod(), url);
               if (canParseOptions(resource, cmd, asyncRequest))
               {
                  CommandLine commandLine = CLIResourceUtil.parseCommandLine(cmd, resource.getParams());
                  String query = formQueryString(resource.getParams(), commandLine);
                  url = (query != null && !query.isEmpty()) ? url + "?" + query : url;
                  asyncRequest = createAsyncRequest(resource.getMethod(), url);

                  setHeaderParameters(asyncRequest, resource.getParams(), commandLine);
                  setBody(resource.getParams(), commandLine, cmd, asyncRequest);
               }
               setAcceptTypes(resource, asyncRequest);
               setContentType(resource, asyncRequest);
               callback.setPayload(new StringUnmarshaller(callback));
               asyncRequest.send(callback);
            }
         }
         catch (MandatoryParameterNotFoundException me)
         {
            CloudShell.console().print(me.getMessage() + "\n");
         }
         catch (Exception e)
         {
            e.printStackTrace();
            //TODO
            CloudShell.console().print("Syntax error in : " + cmd + "\n");
         }
      }
      else
      {
         //TODO multiply commands:
      }

   }

   /**
    *TODO
    * 
    * @param resource
    * @param cmd
    * @param asyncRequest
    * @return {@link Boolean} 
    */
   protected boolean canParseOptions(CLIResource resource, String cmd, AsyncRequest asyncRequest)
   {
      if (resource.getParams() == null || resource.getParams().size() <= 0)
         return false;

      if (resource.getParams().size() == 1)
      {
         CLIResourceParameter parameter = resource.getParams().iterator().next();
         if (Type.BODY.equals(parameter.getType())
            && (parameter.getOptions() == null || parameter.getOptions().size() == 0))
         {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", new JSONString(cmd));
            asyncRequest.data(jsonObject.toString());
            return false;
         }
      }
      return true;
   }

   public void login(String command, AsyncRequestCallback<String> callback)
   {
      String url = REST_CONTEXT + "/ide/crash/command";
      LoginMarshaller marshaller = new LoginMarshaller(command);
      callback.setPayload(new StringUnmarshaller(callback));

      AsyncRequest.build(RequestBuilder.POST, url, new EmptyLoader()).data(marshaller)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);

   }

   /**
    * Set Accept types header for the request.
    * Depends on {@link CLIResource.#getProduces()} value.
    * 
    * @param resource resource
    * @param asyncRequest asynchronous request 
    */
   protected void setAcceptTypes(CLIResource resource, AsyncRequest asyncRequest)
   {
      if (resource.getProduces() != null && resource.getProduces().size() > 0)
      {
         String value = "";
         for (String type : resource.getProduces())
         {
            value += "," + type;
         }
         //Remove last separator:
         value = value.replaceFirst(",", "");
         asyncRequest.header(HTTPHeader.ACCEPT, value);
      }
   }

   /**
    * Set Content type header for the request.
    * Depends on {@link CLIResource.#getConsumes()} value.
    * 
    * @param resource resource
    * @param asyncRequest asynchronous request 
    */
   protected void setContentType(CLIResource resource, AsyncRequest asyncRequest)
   {
      if (resource.getConsumes() != null && resource.getConsumes().size() > 0)
      {
         String value = "";
         for (String type : resource.getConsumes())
         {
            value += "," + type;
         }
         //Remove last separator:
         value = value.replaceFirst(",", "");
         asyncRequest.header(HTTPHeader.CONTENT_TYPE, value);
      }
   }

   /**
    * Set the post data in body of the request.
    * 
    * @param params command's parameters
    * @param commandLine command line data
    * @param cmd string of the command line
    * @param asyncRequest asynchronous request
    * @throws MandatoryParameterNotFoundException
    */
   protected void setBody(Set<CLIResourceParameter> params, CommandLine commandLine, String cmd,
      AsyncRequest asyncRequest) throws MandatoryParameterNotFoundException
   {
      JSONObject jsonObject = new JSONObject();
      for (CLIResourceParameter parameter : params)
      {
         //Check type of the parameter:
         if (!Type.BODY.equals(parameter.getType()))
         {
            continue;
         }

         if (!parameter.isHasArg())
         {
            jsonObject.put(parameter.getName(),
               JSONBoolean.getInstance(isOptionPresent(parameter.getOptions(), commandLine)));
         }
         else
         {
            //Get value of the option:
            String value = getOptionValue(parameter.getOptions(), commandLine);

            if (parameter.isMandatory() && value == null)
            {
               throw new MandatoryParameterNotFoundException("Required parameter " + parameter.getName()
                  + " is not found.");
            }
            else if (value != null)
            {
               jsonObject.put(parameter.getName(), new JSONString(value));
            }
         }
      }
      if (jsonObject.keySet().size() > 0)
      {
         asyncRequest.data(jsonObject.toString());
      }
   }

   /**
    * Create HTTP asynchronous request with specified HTTP method,
    * and URL.
    * 
    * @param method HTTP Method
    * @param url
    * @return {@link AsyncRequest} build asynchronous request
    */
   protected AsyncRequest createAsyncRequest(String method, String url)
   {
      if (HTTPMethod.POST.equalsIgnoreCase(method))
      {
         return AsyncRequest.build(RequestBuilder.POST, url, new EmptyLoader());
      }
      else if (HTTPMethod.GET.equalsIgnoreCase(method))
      {
         return AsyncRequest.build(RequestBuilder.GET, url, new EmptyLoader());
      }
      else
      {
         return AsyncRequest.build(RequestBuilder.GET, url, new EmptyLoader()).header(
            HTTPHeader.X_HTTP_METHOD_OVERRIDE, method);
      }
   }

   /**
    * Form the string with query parameters.
    * 
    * @param params resource parameters
    * @param commandLine parsed command line
    * @return {@link String}  string with query parameters
    * @throws MandatoryParameterNotFoundException
    */
   protected String formQueryString(Set<CLIResourceParameter> params, CommandLine commandLine)
      throws MandatoryParameterNotFoundException
   {
      if (params == null || params.size() <= 0)
         return null;

      String query = "";
      for (CLIResourceParameter param : params)
      {
         if (!Type.QUERY.equals(param.getType()))
         {
            continue;
         }

         if (!param.isHasArg())
         {
            query += param.getName() + isOptionPresent(param.getOptions(), commandLine);
         }
         else
         {
            String value = getOptionValue(param.getOptions(), commandLine);
            if (param.isMandatory() && value == null)
            {
               throw new MandatoryParameterNotFoundException("Required parameter " + param.getName() + " is not found.");
            }
            query += param.getName() + "=" + value;
         }

      }
      return query;
   }

   /**
    * Get the value of the option.
    * 
    * @param options 
    * @param commandLine
    * @return {@link String} value of the option
    */
   protected String getOptionValue(Set<String> options, CommandLine commandLine)
   {
      for (String option : options)
      {
         option = (option.startsWith("--")) ? option.replaceFirst("--", "") : option;
         option = (option.startsWith("-")) ? option.replaceFirst("-", "") : option;
         String value = commandLine.getOptionValue(option);
         if (value != null)
            return value;
      }
      return null;
   }

   protected boolean isOptionPresent(Set<String> options, CommandLine commandLine)
   {
      for (String option : options)
      {
         option = (option.startsWith("--")) ? option.replaceFirst("--", "") : option;
         option = (option.startsWith("-")) ? option.replaceFirst("-", "") : option;
         boolean value = commandLine.hasOption(option);
         if (value)
            return value;
      }
      return false;
   }

   /**
    * Set all specified header parameters.
    * 
    * @param asyncRequest asynchronous request
    * @param params parameters
    * @param commandLine command line
    * @return {@link AsyncRequest} asynchronous request
    * @throws MandatoryParameterNotFoundException
    */
   protected void setHeaderParameters(AsyncRequest asyncRequest, Set<CLIResourceParameter> params,
      CommandLine commandLine) throws MandatoryParameterNotFoundException
   {
      for (CLIResourceParameter parameter : params)
      {
         //Check type of the parameter:
         if (!Type.HEADER.equals(parameter.getType()))
         {
            continue;
         }

         if (!parameter.isHasArg())
         {
            boolean present = isOptionPresent(parameter.getOptions(), commandLine);
            asyncRequest.header(parameter.getName(), String.valueOf(present));
         }
         else
         {
            String value = getOptionValue(parameter.getOptions(), commandLine);
            if (parameter.isMandatory() && value == null)
            {
               throw new MandatoryParameterNotFoundException("Required parameter " + parameter.getName()
                  + " is not found.");
            }
            asyncRequest.header(parameter.getName(), value);
         }
      }
   }

   /*  PATH("path"), ?
    + QUERY("query"), //
     +HEADER("header"), //
     MATRIX("matrix"), ?
     COOKIE("cookie"), ?
     FORM("form"), ?
     BODY("body");*/

   /**
    * Find the list of appropriate commands for the entered 
    * command line. 
    * If command line starts with available command or equals it, then
    * this command is added to the list.
    * 
    * @param cmd string command line
    * @return {@link List} list of the appropriate commands
    */
   protected List<CLIResource> findAppropriateCommands(String cmd)
   {
      List<CLIResource> appropriateCommands = new ArrayList<CLIResource>();
      cmd = cmd.trim();

      for (CLIResource resource : CloudShell.getCommands())
      {
         for (String command : resource.getCommand())
         {
            if (cmd.startsWith(command + " ") || command.equals(cmd))
            {
               appropriateCommands.add(resource);
            }
         }
      }
      return appropriateCommands;
   }
}
