/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.editor.codemirror;

import org.exoplatform.ide.editor.api.CodeValidator;
import org.exoplatform.ide.editor.api.Parser;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;
import org.exoplatform.ide.editor.codemirror.autocomplete.AutocompleteHelper;

import com.google.gwt.core.client.GWT;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class CodeMirrorConfiguration
{
   private boolean isTextWrapping = false;

   /**
    * 0 to turn off continuous scanning, or value like 100 in millisec as scanning period
    */
   private int continuousScanning = 0;

   public final static String PATH = GWT.getModuleBaseURL() + "codemirror/";

   private String jsDirectory = CodeMirrorConfiguration.PATH + "js/";

   private String codeParsers;

   private String codeStyles;

   private boolean canBeOutlined = false;

   private Parser parser;

   private CodeValidator codeValidator;

   private AutocompleteHelper autocompleteHelper;

   private CodeAssistant codeAssistant;

   private boolean canHaveSeveralMimeTypes = false;
   
   private String codeErrorMarkStyle = CodeMirrorClientBundle.INSTANCE.css().codeErrorMarkStyle();
   
   /**
    * Preset configuration of plain text
    */
   public CodeMirrorConfiguration()
   {
      this.codeParsers = "['parsexml.js']";
      this.codeStyles = "['" + PATH + "css/xmlcolors.css']";
   } 
   
   public String getCodeParsers()
   {
      return codeParsers;
   }

   /**
    * Set generic CodeMirror library parsing files *.js
    * @param codeParsers
    * @return configuration instance
    */
   public CodeMirrorConfiguration setGenericParsers(String codeParsers)
   {
      this.codeParsers = codeParsers;
      return this;
   }
   
   public String getCodeStyles()
   {
      return codeStyles;
   }

   /**
    * Set generic CodeMirror library style files *.css
    * @param codeStyles
    * @return configuration instance
    */
   public CodeMirrorConfiguration setGenericStyles(String codeStyles)
   {
      this.codeStyles = codeStyles;
      return this;
   }
   
   public boolean canBeOutlined()
   {
      return canBeOutlined;
   }

   public CodeMirrorConfiguration setCanBeOutlined(boolean canBeOutlined)
   {
      this.canBeOutlined = canBeOutlined;
      return this;
   }
   
   public boolean canBeAutocompleted()
   {
      return this.parser != null  
               && this.codeAssistant != null;
   }

//   public CodeMirrorConfiguration setCanBeAutocompleted(boolean canBeAutocompleted)
//   {
//      this.canBeAutocompleted = canBeAutocompleted;
//      return this;
//   }
   
   public boolean canBeValidated()
   {
      return this.parser != null
               && this.codeValidator != null
               && this.codeAssistant != null;
   }

//   public CodeMirrorConfiguration setCanBeValidated(boolean canBeValidated)
//   {
//      this.canBeValidated = canBeValidated;
//      return this;
//   }
   
   public Parser getParser()
   {
      return parser;
   }

   public CodeMirrorConfiguration setParser(Parser parser)
   {
      this.parser = parser;
      return this;
   }
   
   public CodeValidator getCodeValidator()
   {
      return codeValidator;
   }

   public CodeMirrorConfiguration setCodeValidator(CodeValidator codeValidator)
   {
      this.codeValidator = codeValidator;
      return this;
   }
   
   public AutocompleteHelper getAutocompleteHelper()
   {
      return autocompleteHelper;
   }

   public CodeMirrorConfiguration setAutocompleteHelper(AutocompleteHelper autocompleteHelper)
   {
      this.autocompleteHelper = autocompleteHelper;
      return this;
   }
   
   public boolean canHaveSeveralMimeTypes()
   {
      return canHaveSeveralMimeTypes;
   }

   public CodeMirrorConfiguration setCanHaveSeveralMimeTypes(boolean canHaveSeveralMimeTypes)
   {
      this.canHaveSeveralMimeTypes = canHaveSeveralMimeTypes;
      return this;
   }
   
   /**
    * @return the textWrapping
    */
   public boolean isTextWrapping()
   {
      return isTextWrapping;
   }
   
   public CodeMirrorConfiguration setIsTextWrapping(boolean isTextWrapping)
   {
      this.isTextWrapping = isTextWrapping;
      return this;
   }
   
   /**
    * @return the continuousScanning
    */
   public int getContinuousScanning()
   {
      return continuousScanning;
   }

   public CodeMirrorConfiguration setContinuousScanning(int continuousScanning)
   {
      this.continuousScanning = continuousScanning;
      return this;
   }
   
   /**
    * @return the jsDirectory
    */
   public String getJsDirectory()
   {
      return jsDirectory;
   }
   
   public CodeMirrorConfiguration setJsDirectory(String jsDirectory)
   {
      this.jsDirectory = jsDirectory;
      return this;
   }

   /**
    * @return the codeAssistant
    */
   public CodeAssistant getCodeAssistant()
   {
      return codeAssistant;
   }

   public CodeMirrorConfiguration setCodeAssistant(CodeAssistant codeAssistant)
   {
      this.codeAssistant = codeAssistant;
      return this;
   }
   
   public CodeMirrorConfiguration setCodeErrorMarkStyle(String codeErrorMarkStyle)
   {
      this.codeErrorMarkStyle = codeErrorMarkStyle;
      return this;
   }

   public String getCodeErrorMarkStyle()
   {
      return codeErrorMarkStyle;
   }

}