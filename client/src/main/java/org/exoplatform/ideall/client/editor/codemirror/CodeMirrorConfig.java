/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */

package org.exoplatform.ideall.client.editor.codemirror;

import java.util.HashMap;
import java.util.Iterator;

import org.exoplatform.gwt.commons.rest.MimeType;

import com.google.gwt.core.client.GWT;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CodeMirrorConfig
{

   public static final String DEFAULT_MIMETYPE = "text/plain";

   static final String PATH = GWT.getModuleBaseURL() + "codemirror-0.65/";

   private static HashMap<String, String> codeParsers = new HashMap<String, String>();

   private static HashMap<String, String> codeStyles = new HashMap<String, String>();

   static
   {
      codeParsers.put(MimeType.TEXT_PLAIN, "['parsexml.js', 'parsecss.js']");
      codeStyles.put(MimeType.TEXT_PLAIN, "['" + PATH + "css/xmlcolors.css']");

      codeParsers.put(MimeType.TEXT_XML, "['parsexml.js', 'tokenize.js']");
      codeStyles.put(MimeType.TEXT_XML, "['" + PATH + "css/xmlcolors.css']");

      codeParsers.put(MimeType.APPLICATION_XML, "['parsexml.js', 'tokenize.js']");
      codeStyles.put(MimeType.APPLICATION_XML, "['" + PATH + "css/xmlcolors.css']");

      codeParsers.put(MimeType.TEXT_CSS, "['parsecss.js']");
      codeStyles.put(MimeType.TEXT_CSS, "['" + PATH + "css/csscolors.css']");

      codeParsers.put(MimeType.TEXT_JAVASCRIPT, "['tokenizejavascript.js', 'parsejavascript.js']");
      codeStyles.put(MimeType.TEXT_JAVASCRIPT, "['" + PATH + "css/jscolors.css']");

      codeParsers.put(MimeType.APPLICATION_JAVASCRIPT, "['tokenizejavascript.js', 'parsejavascript.js']");
      codeStyles.put(MimeType.APPLICATION_JAVASCRIPT, "['" + PATH + "css/jscolors.css']");

      codeParsers.put(MimeType.APPLICATION_X_JAVASCRIPT, "['tokenizejavascript.js', 'parsejavascript.js']");
      codeStyles.put(MimeType.APPLICATION_X_JAVASCRIPT, "['" + PATH + "css/jscolors.css']");

      codeParsers.put(MimeType.TEXT_HTML,
         "['parsexml.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'parsehtmlmixed.js']");
      codeStyles.put(MimeType.TEXT_HTML, "['" + PATH + "css/xmlcolors.css', '" + PATH + "css/jscolors.css', '" + PATH
         + "css/csscolors.css']");

      codeParsers.put(MimeType.SCRIPT_GROOVY, "['parsegroovy.js', 'tokenizegroovy.js']");
      codeStyles.put(MimeType.SCRIPT_GROOVY, "['" + PATH + "css/groovycolors.css']");

      codeParsers.put(MimeType.GOOGLE_GADGET,
         "['parsexml.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'parsehtmlmixed.js']");
      codeStyles.put(MimeType.GOOGLE_GADGET, "['" + PATH + "css/xmlcolors.css', '" + PATH + "css/jscolors.css', '"
         + PATH + "css/csscolors.css']");
   }

   public String id = String.valueOf(this.hashCode());

   private String height = "100%";

   private String width = ""; // to fix bug [WBT-238] "1) line numbers isn't displayed in Mozilla Firefox; 3) right part of wide text of editor is cutted"

   private boolean readOnly = false;

   private int continuousScanning = 1000;

   //   private boolean lineNumbers = false;
   private boolean lineNumbers = true;

   private boolean textWrapping = false;

   private String mimeType;

   public CodeMirrorConfig()
   {
      this(DEFAULT_MIMETYPE);
   }

   public CodeMirrorConfig(String mimeType)
   {
      this.mimeType = mimeType;
   }

   /**
    * @return String 
    * This method returns the current height of the editor if is set. The default is 350px
    */
   public String getHeight()
   {
      return height;
   }

   /**
    * @param height
    * This method sets the height of the editor
    */
   public void setHeight(String height)
   {
      this.height = height;
   }

   /**
    * @return boolean 
    * This method returns the current readonly state of the editor. default is false
    */
   public boolean isReadOnly()
   {
      return readOnly;
   }

   /**
    * @param readOnly
    * This method sets disables the editor and make it for read only perposes
    */
   public void setReadOnly(boolean readOnly)
   {
      this.readOnly = readOnly;
   }

   /**
    * @return int 
    * the time that the editor checks for the changes. the default is 1000
    */
   public int getContinuousScanning()
   {
      return continuousScanning;
   }

   /**
    * @param continuousScanning
    * sets the time the editor checks for the changes
    */
   public void setContinuousScanning(int continuousScanning)
   {
      this.continuousScanning = continuousScanning;
   }

   /**
    * @return boolean 
    * The current state of the line number.
    */
   public boolean isLineNumbers()
   {
      return lineNumbers;
   }

   /**
    * @param lineNumbers
    * Set the current state of the line number. 
    * true for visible and false for invisible
    */
   public void setLineNumbers(boolean lineNumbers)
   {
      this.lineNumbers = lineNumbers;
   }

   public String getWidth()
   {
      return width;
   }

   public void setWidth(String width)
   {
      this.width = width;
   }

   public boolean isTextWrapping()
   {
      return textWrapping;
   }

   public void setTextWrapping(boolean textWrapping)
   {
      this.textWrapping = textWrapping;
   }

   public String getMimeType()
   {
      return mimeType;
   }

   public void setMimeType(String mimeType)
   {
      this.mimeType = mimeType;
   }

   public String getStyleUrl()
   {
      Iterator<String> keys = codeStyles.keySet().iterator();
      while (keys.hasNext())
      {
         String key = keys.next();
         if (mimeType.contains(key))
         {
            return codeStyles.get(key);
         }
      }

      return codeStyles.get(DEFAULT_MIMETYPE);
   }

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getParserNames()
   {
      Iterator<String> keys = codeParsers.keySet().iterator();
      while (keys.hasNext())
      {
         String key = keys.next();
         if (mimeType.contains(key))
         {
            return codeParsers.get(key);
         }
      }

      return codeParsers.get(DEFAULT_MIMETYPE);
   }

}
