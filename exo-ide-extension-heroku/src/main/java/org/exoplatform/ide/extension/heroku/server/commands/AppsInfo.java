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
import org.exoplatform.ide.extension.heroku.server.Heroku;
import org.exoplatform.ide.extension.heroku.server.HerokuCommand;
import org.exoplatform.ide.extension.heroku.server.HerokuException;
import org.exoplatform.ide.extension.heroku.server.Option;
import org.exoplatform.ide.extension.heroku.shared.HerokuApplicationInfo;
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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AppsInfo extends HerokuCommand
{
   @Option(name = "--app")
   private String app;

   @Option(name = "--raw")
   private boolean raw;

   public AppsInfo(File gitWorkDir)
   {
      super(gitWorkDir);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.server.HerokuCommand#execute()
    */
   @Override
   public Object execute() throws HerokuException, CommandException
   {
      if (this.app == null)
      {
         String detectedApp = detectAppName();
         if (detectedApp == null || detectedApp.isEmpty())
            throw new CommandException("Application name is not defined. ");
         this.app = detectedApp;
      }

      HttpURLConnection http = null;
      try
      {
         URL url = new URL(Heroku.HEROKU_API + "/apps/" + app);
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
         if (!raw)
         {
            HerokuApplicationInfo info = new HerokuApplicationInfo();
            info.setName((String)xPath.evaluate("/app/name", xmlDoc, XPathConstants.STRING));
            info.setWebUrl((String)xPath.evaluate("/app/web_url", xmlDoc, XPathConstants.STRING));
            info.setDomainName((String)xPath.evaluate("/app/domain_name", xmlDoc, XPathConstants.STRING));
            info.setGitUrl((String)xPath.evaluate("/app/git_url", xmlDoc, XPathConstants.STRING));
            info.setDynos(((Double)xPath.evaluate("/app/dynos", xmlDoc, XPathConstants.NUMBER)).intValue());
            info.setWorkers(((Double)xPath.evaluate("/app/workers", xmlDoc, XPathConstants.NUMBER)).intValue());
            String nilOrValue = ((Node)xPath.evaluate("/app/repo-size", xmlDoc, XPathConstants.NODE)).getTextContent();
            if (nilOrValue != null && !nilOrValue.isEmpty())
               info.setRepoSize(new Integer(nilOrValue));
            nilOrValue = ((Node)xPath.evaluate("/app/slug-size", xmlDoc, XPathConstants.NODE)).getTextContent();
            if (nilOrValue != null && !nilOrValue.isEmpty())
               info.setSlugSize(new Integer(nilOrValue));
            info.setStack((String)xPath.evaluate("/app/stack", xmlDoc, XPathConstants.STRING));
            info.setOwner((String)xPath.evaluate("/app/owner", xmlDoc, XPathConstants.STRING));
            info.setDatabaseSize((String)xPath.evaluate("/app/database_size", xmlDoc, XPathConstants.STRING));
            return info;
         }
         else
         {
            NodeList appNodes = (NodeList)xPath.evaluate("/app/*", xmlDoc, XPathConstants.NODESET);
            int appLength = appNodes.getLength();
            Map<String, String> info = new HashMap<String, String>();
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
