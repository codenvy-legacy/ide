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

import org.exoplatform.cloudshell.shared.CLIResourceParameter;
import org.exoplatform.services.rest.Parameter;
import org.exoplatform.services.rest.method.MethodParameter;
import org.exoplatform.services.rest.resource.ResourceMethodDescriptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class PropertiesTransformRules extends TransformRules
{
   private static Pattern MULTIPLE_ARGUMENT_SPLITTER = Pattern.compile("\\s*,\\s*");

   private final Properties rules;

   public PropertiesTransformRules()
   {
      final String file = System.getProperty("org.exoplatform.ide.rest2cli.properties", "conf/rest2cli.properties");
      URL propsFileURL = Thread.currentThread().getContextClassLoader().getResource(file);
      if (propsFileURL == null)
      {
         throw new RuntimeException("Required file '" + file + "' not found. ");
      }
      rules = new Properties();
      try
      {
         rules.load(new FileReader(new File(propsFileURL.getPath())));
      }
      catch (FileNotFoundException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
   }

   public PropertiesTransformRules(Properties rules)
   {
      this.rules = rules;
   }

   /**
    * @see org.exoplatform.cloudshell.server.TransformRules#getCommand(java.lang.String, java.lang.String)
    */
   @Override
   public String getCommand(String path, String httpMethod)
   {
      String command = rules.getProperty(path + "." + httpMethod);
      if (command == null)
      {
         // Try with different combination of leading and terminal '/'
         if (path.startsWith("/"))
         {
            path = path.substring(1);
         }
         if (path.endsWith("/"))
         {
            path = path.substring(0, path.length() - 1);
         }
         String[] t = new String[]{path + "." + httpMethod, //
            "/" + path + "." + httpMethod, //
            path + "/." + httpMethod, //
            "/" + path + "/." + httpMethod};
         for (int i = 0; command == null && i < t.length; i++)
         {
            command = rules.getProperty(t[i]);
         }
      }
      return command;
   }

   /**
    * @see org.exoplatform.cloudshell.server.TransformRules#getParameters(java.lang.String,
    *      org.exoplatform.services.rest.resource.ResourceMethodDescriptor)
    */
   @Override
   public Map<String, CLIResourceParameter> getParameters(String command, ResourceMethodDescriptor method)
   {
      Map<String, CLIResourceParameter> cliParams = new HashMap<String, CLIResourceParameter>();
      List<MethodParameter> methodParameters = method.getMethodParameters();
      for (Parameter restParam : methodParameters)
      {
         Annotation restAnno = restParam.getAnnotation();
         if (restAnno != null)
         {
            CLIResourceParameter cliParameter = createCLIParameter(command, restAnno);
            // If parameter is supported via CLI interface add it to list.
            // Some rest parameters, e.g. that carry javax.ws.rs.core.Context annotation may not be passed
            // from client. Always skip such parameters.
            if (cliParameter != null)
            {
               Set<String> cliNames = cliParameter.getCliNames();
               if (cliNames != null && cliNames.size() > 0)
               {
                  for (String n : cliNames)
                     cliParams.put(n, cliParameter);
               }
            }
         }
      }

      // Get all body parameters. Body parameters in this case is virtual set of parameters.
      // Mainly need this to be able pass JSON content from client.
      //
      // Here is fragment of properties file:
      // /my/command/;POST=my_command
      // my_command.body.params=message
      // my_command.body.message=-m, --message
      //
      // For example above if client enter parameter -m or --message over IDE shell then
      // this parameter must be to server in body as parameter 'message'.
      // JSON:
      // {"message":"to be or not to be"}
      final String bodyParamsKey = command + "." + CLIResourceParameter.Type.BODY + ".params";
      String rawBodyParamsList = rules.getProperty(bodyParamsKey);
      if (rawBodyParamsList != null && rawBodyParamsList.length() > 0)
      {
         String[] bodyParamsList = MULTIPLE_ARGUMENT_SPLITTER.split(rawBodyParamsList);
         for (int i = 0; i < bodyParamsList.length; i++)
         {
            CLIResourceParameter cliParameter =
               createCLIParameter(command, bodyParamsList[i], CLIResourceParameter.Type.BODY);
            Set<String> cliNames = cliParameter.getCliNames();
            if (cliNames != null && cliNames.size() > 0)
            {
               for (String n : cliNames)
                  cliParams.put(n, cliParameter);
            }
         }
      }

      return cliParams;
   }

   @SuppressWarnings("rawtypes")
   private CLIResourceParameter createCLIParameter(String command, Annotation annotation)
   {
      Class annotationClass = annotation.annotationType();
      if (annotationClass == PathParam.class)
      {
         return createCLIParameter(command, ((PathParam)annotation).value(), CLIResourceParameter.Type.PATH);
      }
      else if (annotationClass == MatrixParam.class)
      {
         return createCLIParameter(command, ((MatrixParam)annotation).value(), CLIResourceParameter.Type.MATRIX);
      }
      else if (annotationClass == QueryParam.class)
      {
         return createCLIParameter(command, ((QueryParam)annotation).value(), CLIResourceParameter.Type.QUERY);
      }
      else if (annotationClass == HeaderParam.class)
      {
         return createCLIParameter(command, ((HeaderParam)annotation).value(), CLIResourceParameter.Type.HEADER);
      }
      else if (annotationClass == FormParam.class)
      {
         return createCLIParameter(command, ((FormParam)annotation).value(), CLIResourceParameter.Type.FORM);
      }
      else if (annotationClass == CookieParam.class)
      {
         return createCLIParameter(command, ((CookieParam)annotation).value(), CLIResourceParameter.Type.COOKIE);
      }
      // Not supported annotation on parameter - cannot describe it as CLI argument. 
      return null;
   }

   private CLIResourceParameter createCLIParameter(String command, String restName, CLIResourceParameter.Type type)
   {
      final String paramKey = command + "." + type + "." + restName;
      Set<String> cliNames = null;
      String rawCliNamesList = rules.getProperty(paramKey);
      if (rawCliNamesList != null && rawCliNamesList.length() > 0)
      {
         String[] t = MULTIPLE_ARGUMENT_SPLITTER.split(rawCliNamesList);
         cliNames = new HashSet<String>(t.length);
         for (int i = 0; i < t.length; i++)
            cliNames.add(t[i]);
      }
      else
      {
         // Use the original name of REST parameter in command line.
         cliNames = new HashSet<String>(1);
         cliNames.add(("-" + restName));
      }
      boolean mandatory = Boolean.parseBoolean(rules.getProperty(paramKey + ".mandatory"));

      return new CLIResourceParameter(restName, cliNames, type, mandatory);
   }
}
