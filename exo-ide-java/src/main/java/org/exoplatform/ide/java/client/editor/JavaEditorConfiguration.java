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
package org.exoplatform.ide.java.client.editor;

import org.exoplatform.ide.texteditor.api.TextEditorConfiguration;
import org.exoplatform.ide.texteditor.api.TextEditorPartDisplay;
import org.exoplatform.ide.texteditor.api.parser.Parser;
import org.exoplatform.ide.texteditor.parser.BasicTokenFactory;
import org.exoplatform.ide.texteditor.parser.CmParser;
import org.exoplatform.ide.texteditor.parser.CodeMirror2;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaEditorConfiguration extends TextEditorConfiguration
{

   /**
    * @see org.exoplatform.ide.texteditor.api.TextEditorConfiguration#getParser()
    */
   @Override
   public Parser getParser(TextEditorPartDisplay display)
   {
      CmParser parser = CodeMirror2.getParserForMime("text/x-java");
      parser.setNameAndFactory("clike", new BasicTokenFactory());
      return parser;
   }
}
