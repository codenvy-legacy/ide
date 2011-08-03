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
package org.exoplatform.ide.editor.extension.groovy.client.codeassistant;

import com.google.gwt.resources.client.TextResource;

import org.exoplatform.ide.editor.api.codeassitant.TokenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.extension.java.client.codeassistant.JavaCodeAssistant;
import org.exoplatform.ide.editor.extension.java.client.codeassistant.JavaCodeAssistantErrorHandler;
import org.exoplatform.ide.editor.extension.java.client.codeassistant.services.CodeAssistantService;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GroovyCodeAssist Apr 6, 2011 1:56:58 PM evgen $
 *
 */
public class GroovyCodeAssistant extends JavaCodeAssistant
{

   /**
    * @param service
    * @param factory
    * @param errorHandler
    */
   public GroovyCodeAssistant(CodeAssistantService service, TokenWidgetFactory factory,
      JavaCodeAssistantErrorHandler errorHandler)
   {
      super(service, factory, errorHandler);
   }
   
   /**
    * @see org.exoplatform.ide.editor.extension.java.client.codeassistant.JavaCodeAssistant#parseKeyWords(com.google.gwt.resources.client.TextResource)
    */
   @Override
   protected void parseKeyWords(TextResource resource)
   {
      super.parseKeyWords(resource);
      keywords.add(new TokenImpl("as", TokenType.KEYWORD));
      keywords.add(new TokenImpl("in", TokenType.KEYWORD));
      keywords.add(new TokenImpl("def", TokenType.KEYWORD));
   }

}
