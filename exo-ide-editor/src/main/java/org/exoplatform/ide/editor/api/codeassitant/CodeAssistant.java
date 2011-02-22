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
package org.exoplatform.ide.editor.api.codeassitant;

import java.util.List;

import org.exoplatform.ide.editor.api.Editor;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeAssistant Feb 22, 2011 12:43:13 PM evgen $
 *
 */
public interface CodeAssistant
{
  
   void errorMarckClicked(Editor editor, List<CodeError> codeErrorList, int markOffsetX, int markOffsetY, String fileMimeType);
  
  void autocompleteCalled(Editor editor, String mimeType, int cursorOffsetX, int cursorOffsetY,
     String lineContent, int cursorPositionX, int cursorPositionY, List<Token> tokenList, String lineMimeType, Token currentToken);
  
}
