// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.exoplatform.ide.texteditor.parser;

import com.google.gwt.resources.client.ClientBundle.Source;

import org.exoplatform.ide.util.dom.Elements;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * Wraps the CodeMirror2 syntax parser modes.
 *
 */
public class CodeMirror2
{

   /**
    * External parser javascript source.
    */
   public interface Resources extends ClientBundle
   {
      @Source("codemirror2_parsers.js")
      TextResource parser();

      @Source("codemirror2_base.js")
      TextResource base();
   }

   static
   {

      Resources resources = GWT.create(Resources.class);
      Elements.injectJs(resources.base().getText() + resources.parser().getText());
   }

   //   public static Parser getParser(String mimeType)
   //   {
   //      SyntaxType type = SyntaxType.syntaxTypeByMimeType(mimeType);
   //      CmParser parser = getParserForMime(type.getMimeType());
   //      Assert.isNotNull(parser);
   //      parser.setType(type);
   //
   ////      // TODO: testing no smart indentation to see how it feels
   ////      parser.setPreventSmartIndent(type != SyntaxType.PY);
   //      return parser;
   //   }

   public static native CmParser getParserForMime(String mime) /*-{
		conf = $wnd.CodeMirror.defaults;
		if (mime == "text/x-python") {
			conf["mode"] = {
				version : 2
			};
		}
		return $wnd.CodeMirror.getMode(conf, mime);
   }-*/;

   /**
    * Mode constant: JavaScript.
    */
   public static final String JAVASCRIPT = "javascript";

   /**
    * Mode constant: HTML.
    */
   public static final String HTML = "html";

   /**
    * Mode constant: CSS.
    */
   public static final String CSS = "css";
}
