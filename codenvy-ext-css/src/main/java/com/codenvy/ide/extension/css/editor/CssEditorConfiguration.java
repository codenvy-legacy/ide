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

import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.texteditor.api.parser.Parser;
import com.codenvy.ide.texteditor.parser.CmParser;

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

   private static native CmParser getParserForMime(String mime) /*-{
      conf = $wnd.CodeMirror.defaults;
      return $wnd.CodeMirror.getMode(conf, mime);
   }-*/;


   /**
    * {@inheritDoc}
    */
   @Override
   public Parser getParser(TextEditorPartView view)
   {
      CmParser parser = getParserForMime("text/css");
      parser.setNameAndFactory("css", new CssTokenFactory());
      return parser;
   }

   /**
    * @see com.codenvy.ide.texteditor.api.TextEditorConfiguration#getContentAssistantProcessors(com.codenvy.ide.texteditor.api.TextEditorPartView)
    */
   @Override
   public JsonStringMap<CodeAssistProcessor> getContentAssistantProcessors(TextEditorPartView view)
   {
      JsonStringMap<CodeAssistProcessor> map = JsonCollections.createStringMap();
      map.put(Document.DEFAULT_CONTENT_TYPE, new CssCodeAssistantProcessor(resourcess));
      return map;
   }
}
