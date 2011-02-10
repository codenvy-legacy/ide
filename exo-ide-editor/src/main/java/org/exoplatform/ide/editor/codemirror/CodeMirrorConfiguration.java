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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.editor.codemirror.CodemirrorProducer;
import org.exoplatform.ide.editor.api.DafaultParser;
import org.exoplatform.ide.editor.api.Parser;
import org.exoplatform.ide.editor.api.codeassitant.autocompletehelper.AutoCompleteHelper;
import org.exoplatform.ide.editor.api.codeassitant.autocompletehelper.DefaultAutocompleteHelper;
import org.exoplatform.ide.editor.api.codeassitant.codevalidator.CodeValidator;
import org.exoplatform.ide.editor.api.codeassitant.codevalidator.DefaultCodeValidator;

import com.google.gwt.core.client.GWT;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class CodeMirrorConfiguration
{
   private final boolean textWrapping = false;

   private final int continuousScanning = 100;

   public final static String PATH = GWT.getModuleBaseURL() + "codemirror/";

   private final String jsDirectory = CodemirrorProducer.PATH + "js/";

   private String codeParsers;

   private String codeStyles;

   private boolean canBeOutlined;

   private boolean canBeAutocompleted;

   private boolean canBeValidated;

   private Parser parser;

   private CodeValidator codeValidator;

   private AutoCompleteHelper autocompleteHelper;

   public CodeMirrorConfiguration()
   {
      this("['parsexml.js', 'parsecss.js']", "['" + PATH + "css/xmlcolors.css']");
   }

   public CodeMirrorConfiguration(String codeParsers, String codeStyles)
   {
      this(codeParsers, codeStyles, false, false, new DafaultParser(), new DefaultAutocompleteHelper(), false,
         new DefaultCodeValidator());
   }

   public CodeMirrorConfiguration(String codeParsers, String codeStyles, boolean canBeOutlined,
      boolean canBeAutocompleted, Parser parser)
   {
      this(codeParsers, codeStyles, canBeOutlined, canBeAutocompleted, parser, new DefaultAutocompleteHelper(), false,
         new DefaultCodeValidator());
   }

   public CodeMirrorConfiguration(String codeParsers, String codeStyles, boolean canBeOutlined,
      boolean canBeAutocompleted, Parser parser, AutoCompleteHelper autocompleteHelper)
   {
      this(codeParsers, codeStyles, canBeOutlined, canBeAutocompleted, parser, autocompleteHelper, false,
         new DefaultCodeValidator());
   }

   public CodeMirrorConfiguration(String codeParsers, String codeStyles, boolean canBeOutlined,
      boolean canBeAutocompleted, Parser parser, AutoCompleteHelper autocompleteHelper, boolean canBeValidated,
      CodeValidator codeValidator)
   {
      this.codeParsers = codeParsers;
      this.codeStyles = codeStyles;
      this.canBeOutlined = canBeOutlined;
      this.canBeAutocompleted = canBeAutocompleted;
      this.parser = parser;
      this.autocompleteHelper = autocompleteHelper;
      this.canBeValidated = canBeValidated;
      this.codeValidator = codeValidator;
   }

   public String getCodeParsers()
   {
      return codeParsers;
   }

   public String getCodeStyles()
   {
      return codeStyles;
   }

   public boolean canBeOutlined()
   {
      return canBeOutlined;
   }

   public boolean canBeAutocompleted()
   {
      return canBeAutocompleted;
   }

   public boolean canBeValidated()
   {
      return canBeValidated;
   }

   public Parser getParser()
   {
      return parser;
   }

   public CodeValidator getCodeValidator()
   {
      return codeValidator;
   }

   public AutoCompleteHelper getAutocompleteHelper()
   {
      return autocompleteHelper;
   }

   public static boolean canHaveSeveralMimeTypes(String mimeType)
   {
      return mimeType.equals(MimeType.GOOGLE_GADGET) || mimeType.equals(MimeType.TEXT_HTML)
         || mimeType.equals(MimeType.GROOVY_TEMPLATE) || mimeType.equals(MimeType.UWA_WIDGET);
   }

   /**
    * @return the textWrapping
    */
   public boolean isTextWrapping()
   {
      return textWrapping;
   }

   /**
    * @return the continuousScanning
    */
   public int getContinuousScanning()
   {
      return continuousScanning;
   }

   /**
    * @return the jsDirectory
    */
   public String getJsDirectory()
   {
      return jsDirectory;
   }

}
