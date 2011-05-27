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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * List of heroku applications for current user.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Apps extends HerokuCommand
{
   /**
    * @return List of names of applications for current user
    * @throws HerokuException if heroku server return unexpected or error status for request
    * @throws CredentialsNotFoundException if cannot get access to heroku.com server since user is not login yet and has
    *            not credentials. Must use {@link AuthLogin#execute(String, String)} first.
    * @throws CommandException if any other exception occurs
    */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> list() throws HerokuException, CredentialsNotFoundException, CommandException
   {
      HttpURLConnection http = null;
      try
      {
         URL url = new URL(Heroku.HEROKU_API + "/apps");
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

         XPath xPath = XPathFactory.newInstance().newXPath();
         NodeList appNodes = (NodeList)xPath.evaluate("/apps/app", xmlDoc, XPathConstants.NODESET);
         int appLength = appNodes.getLength();
         List<String> apps = new ArrayList<String>(appLength);
         for (int i = 0; i < appLength; i++)
         {
            String name = (String)xPath.evaluate("name", appNodes.item(i), XPathConstants.STRING);
            apps.add(name);
         }
         return apps;
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
