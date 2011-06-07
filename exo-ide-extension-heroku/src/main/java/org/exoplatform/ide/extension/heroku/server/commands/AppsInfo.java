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
package org.exoplatform.ide.extension.heroku.server.commands;

import org.exoplatform.ide.extension.heroku.server.CommandException;
import org.exoplatform.ide.extension.heroku.server.CredentialsNotFoundException;
import org.exoplatform.ide.extension.heroku.server.Heroku;
import org.exoplatform.ide.extension.heroku.server.HerokuCommand;
import org.exoplatform.ide.extension.heroku.server.HerokuException;
import org.exoplatform.ide.git.server.rest.GitLocation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Provide detailed information about application.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AppsInfo extends HerokuCommand
{
   /**
    * @param name application name to get information. If <code>null</code> then try to determine application name from
    *           git configuration. To be able determine application name <code>workDir</code> must not be
    *           <code>null</code> at least
    * @param inRawFormat if <code>true</code> then get result as raw Map. If <code>false</code> (default) result
    *           is Map that contains predefined set of key-value pair
    * @param workDir git working directory. May be <code>null</code> if command executed out of git repository in this
    *           case <code>name</code> parameter must be not <code>null</code>
    * @return result of execution of {@link #execute()} depends to {@link #inRawFormat} parameter. If
    *         {@link #inRawFormat} is <code>false</code> (default) then method returns with predefined set of attributes
    *         otherwise method returns raw Map that contains all attributes
    * @throws HerokuException if heroku server return unexpected or error status for request
    * @throws CredentialsNotFoundException if cannot get access to heroku.com server since user is not login yet and has
    *            not credentials. Must use {@link AuthLogin#execute(String, String)} first.
    * @throws CommandException if any other exception occurs
    */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> info( //
      @QueryParam("name") String name, //
      @QueryParam("raw") boolean inRawFormat, //
      @QueryParam("workdir") GitLocation workDir, //
      @Context UriInfo uriInfo
   ) throws HerokuException, CredentialsNotFoundException, CommandException
   {
      if (name == null || name.isEmpty())
      {
         name = detectAppName(new File(workDir.getLocalPath(uriInfo)));
         if (name == null || name.isEmpty())
            throw new CommandException("Application name is not defined. ");
      }

      HttpURLConnection http = null;
      try
      {
         URL url = new URL(Heroku.HEROKU_API + "/apps/" + name);
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("GET");
         authenticate(http);
         http.setRequestProperty("Accept", "application/xml, */*");

         if (http.getResponseCode() != 200)
            throw fault(http);

         InputStream input = http.getInputStream();
         Document xmlDoc;
         try
         {
            xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
         }
         finally
         {
            input.close();
         }

         // TODO : Add 'addons', 'collaborators' to be conform to ruby implementation.
         XPath xPath = XPathFactory.newInstance().newXPath();

         Map<String, String> info = new HashMap<String, String>();

         if (!inRawFormat)
         {
            info.put("name", (String)xPath.evaluate("/app/name", xmlDoc, XPathConstants.STRING));
            info.put("webUrl", (String)xPath.evaluate("/app/web_url", xmlDoc, XPathConstants.STRING));
            info.put("domainName", (String)xPath.evaluate("/app/domain_name", xmlDoc, XPathConstants.STRING));
            info.put("gitUrl", (String)xPath.evaluate("/app/git_url", xmlDoc, XPathConstants.STRING));
            info.put("dynos", (String)xPath.evaluate("/app/dynos", xmlDoc, XPathConstants.STRING));
            info.put("workers", (String)xPath.evaluate("/app/workers", xmlDoc, XPathConstants.STRING));
            info.put("repoSize", (String)xPath.evaluate("/app/repo-size", xmlDoc, XPathConstants.STRING));
            info.put("slugSize", (String)xPath.evaluate("/app/slug-size", xmlDoc, XPathConstants.STRING));
            info.put("stack", (String)xPath.evaluate("/app/stack", xmlDoc, XPathConstants.STRING));
            info.put("owner", (String)xPath.evaluate("/app/owner", xmlDoc, XPathConstants.STRING));
            info.put("databaseSize", (String)xPath.evaluate("/app/database_size", xmlDoc, XPathConstants.STRING));
            return info;
         }
         else
         {
            NodeList appNodes = (NodeList)xPath.evaluate("/app/*", xmlDoc, XPathConstants.NODESET);
            int appLength = appNodes.getLength();
            for (int i = 0; i < appLength; i++)
            {
               Node item = appNodes.item(i);
               if (!item.getNodeName().equals("dyno_hours"))
                  info.put(item.getNodeName(), item.getTextContent());
            }
            return info;
         }
      }
      catch (IOException ioe)
      {
         throw new CommandException(ioe.getMessage(), ioe);
      }
      catch (ParserConfigurationException pce)
      {
         throw new CommandException(pce.getMessage(), pce);
      }
      catch (SAXException sae)
      {
         throw new CommandException(sae.getMessage(), sae);
      }
      catch (XPathExpressionException xpe)
      {
         throw new CommandException(xpe.getMessage(), xpe);
      }
      finally
      {
         if (http != null)
            http.disconnect();
      }
   }
}
