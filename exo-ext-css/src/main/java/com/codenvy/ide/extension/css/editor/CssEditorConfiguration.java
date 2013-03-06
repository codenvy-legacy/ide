/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.extension.css.editor;

import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistant;
import com.codenvy.ide.texteditor.api.parser.Parser;
import com.codenvy.ide.texteditor.codeassistant.CodeAssistantImpl;
import com.codenvy.ide.texteditor.parser.CmParser;
import com.codenvy.ide.texteditor.parser.CodeMirror2;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class CssEditorConfiguration extends TextEditorConfiguration
{

   private CssResources resourcess;

   /**
    * @param resourcess
    */
   public CssEditorConfiguration(CssResources resourcess)
   {
      super();
      this.resourcess = resourcess;
   }

   /**
    * @see com.codenvy.ide.texteditor.api.TextEditorConfiguration#getParser()
    */
   @Override
   public Parser getParser(TextEditorPartView view)
   {
      CmParser parser = CodeMirror2.getParserForMime("text/css");
      parser.setNameAndFactory("css", new CssTokenFactory());
      return parser;
   }

   /**
    * @see com.codenvy.ide.texteditor.api.TextEditorConfiguration#getContentAssistant(com.codenvy.ide.texteditor.api.TextEditorPartView)
    */
   @Override
   public CodeAssistant getContentAssistant(TextEditorPartView view)
   {
      CodeAssistantImpl codeAssistant = new CodeAssistantImpl();
      codeAssistant.setCodeAssistantProcessor(Document.DEFAULT_CONTENT_TYPE, new CssCodeAssistantProcessor(resourcess));
      return codeAssistant;
   }
}
