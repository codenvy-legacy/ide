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

import org.eclipse.jgit.lib.Constants;
import org.exoplatform.ide.extension.heroku.server.Arg;
import org.exoplatform.ide.extension.heroku.server.CommandException;
import org.exoplatform.ide.extension.heroku.server.Default;
import org.exoplatform.ide.extension.heroku.server.Heroku;
import org.exoplatform.ide.extension.heroku.server.HerokuCommand;
import org.exoplatform.ide.extension.heroku.server.HerokuException;
import org.exoplatform.ide.extension.heroku.server.Option;
import org.exoplatform.ide.extension.heroku.shared.HerokuApplicationInfo;
import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitConnectionFactory;
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.shared.RemoteAddRequest;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
public class AppsCreate extends HerokuCommand
{
   @Arg(index = 0)
   private String name;

   @Option(name = "--remote")
   @Default("heroku")
   private String remote;

   /*@Option("--stack")
   private String stack;*/

   public AppsCreate(File gitWorkDir)
   {
      super(gitWorkDir);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.server.HerokuCommand#execute()
    */
   @Override
   public Object execute() throws HerokuException, CommandException
   {
      HttpURLConnection http = null;
      try
      {
         URL url = new URL(Heroku.HEROKU_API + "/apps");
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("POST");
         http.setRequestProperty("Accept", "application/xml");
         authenticate(http);
         if (name != null)
         {
            http.setDoOutput(true);
            http.setRequestProperty("Content-type", "application/xml, */*");
            OutputStream output = http.getOutputStream();
            try
            {
               output.write(("<?xml version='1.0' encoding='UTF-8'?><app><name>" + name + "</name></app>").getBytes());
               output.flush();
            }
            finally
            {
               output.close();
            }
         }
         
         int status = http.getResponseCode();
         if (status < 200 || status > 202)
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
         String name = (String)xPath.evaluate("/app/name", xmlDoc, XPathConstants.STRING);
         String gitUrl = (String)xPath.evaluate("/app/git_url", xmlDoc, XPathConstants.STRING);
         String webUrl = (String)xPath.evaluate("/app/web_url", xmlDoc, XPathConstants.STRING);

         if (gitWorkDir != null && new File(gitWorkDir, Constants.DOT_GIT).exists())
         {
            GitConnection git = GitConnectionFactory.getInstance().getConnection(gitWorkDir, null);
            try
            {
               git.remoteAdd(new RemoteAddRequest(remote, gitUrl));
            }
            finally
            {
               git.close();
            }
         }
         return new HerokuApplicationInfo(name, webUrl, null, gitUrl, 0, 0, null, null, null, null, null);
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
      catch (GitException ge)
      {
         throw new CommandException(ge.getMessage(), ge);
      }
      finally
      {
         if (http != null)
            http.disconnect();
      }
   }
}
