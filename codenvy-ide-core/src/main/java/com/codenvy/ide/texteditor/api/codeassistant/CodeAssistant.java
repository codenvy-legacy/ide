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
package com.codenvy.ide.texteditor.api.codeassistant;

import com.codenvy.ide.texteditor.api.TextEditorPartView;

/**
 * An <code>CodeAssistant</code> provides support on interactive content completion.
 * The content assistant is a {@link TextEditorPartView} add-on. Its
 * purpose is to propose, display, and insert completions of the content
 * of the text viewer's document at the viewer's cursor position. In addition
 * to handle completions, a content assistant can also be requested to provide
 * context information. Context information is shown in a tool tip like popup.
 * As it is not always possible to determine the exact context at a given
 * document offset, a content assistant displays the possible contexts and requests
 * the user to choose the one whose information should be displayed.
 * <p>
 * A content assistant has a list of {@link CodeAssistProcessor}
 * objects each of which is registered for a  particular document content
 * type. The content assistant uses the processors to react on the request
 * of completing documents or presenting context information.
 * </p>
 * <p>
 * The interface can be implemented by clients. By default, clients use
 *  <b>CodeAssistantImpl</b> as the standard
 * implementer of this interface.
 * </p>
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public interface CodeAssistant
{
   /**
    * Installs content assist support on the given text view.
    *
    * @param view the text view on which content assist will work
    */
   void install(TextEditorPartView view);

   /**
    * Uninstalls content assist support from the text view it has
    * previously be installed on.
    */
   void uninstall();

   /**
    * Shows all possible completions of the content at the display's cursor position.
    *
    * @return an optional error message if no proposals can be computed
    */
   String showPossibleCompletions();

   /**
    * Returns the code assist processor to be used for the given content type.
    *
    * @param contentType the type of the content for which this
    *        content assistant is to be requested
    * @return an instance code assist processor or
    *         <code>null</code> if none exists for the specified content type
    */
   CodeAssistProcessor getCodeAssistProcessor(String contentType);
}
