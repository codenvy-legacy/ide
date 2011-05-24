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
import org.exoplatform.ide.extension.heroku.shared.HerokuKey;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
public class Keys extends HerokuCommand
{
   @Option(name = "--long")
   private boolean inLongFormat;

   public Keys(File gitWorkDir)
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
         URL url = new URL(Heroku.HEROKU_API + "/user/keys");
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
         NodeList keyNodes = (NodeList)xPath.evaluate("/keys/key", xmlDoc, XPathConstants.NODESET);
         int keyLength = keyNodes.getLength();
         List<HerokuKey> keys = new ArrayList<HerokuKey>(keyLength);
         for (int i = 0; i < keyLength; i++)
         {
            Node n = keyNodes.item(i);
            String email = (String)xPath.evaluate("email", n, XPathConstants.STRING);
            String contents = (String)xPath.evaluate("contents", n, XPathConstants.STRING);
            if (!inLongFormat)
               contents = formatKey(contents);
            keys.add(new HerokuKey(email, contents));
         }
         return keys;
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

   private String formatKey(String source)
   {
      String[] parts = source.split(" ");
      StringBuilder key = new StringBuilder();
      key.append(parts[0]) //
         .append(' ') //
         .append(parts[1].substring(0, 10)) //
         .append('.') //
         .append('.') //
         .append('.') //
         .append(parts[1].substring(parts[1].length() - 10, parts[1].length()));
      if (parts.length > 2)
         key.append(' ') //
            .append(parts[2]);
      return key.toString();
   }
}
