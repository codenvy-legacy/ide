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
package org.exoplatform.ide.client.framework.codeassistant.api;

import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.client.framework.codeassistant.TokensCollectedCallback;

/**
 * General token collector.
 * Every MimeType has own token collector.<br>
 * (Except "composite" mimetype such as {@link MimeType#GOOGLE_GADGET}, 
 *  that is composition of {@link MimeType#TEXT_XML},{@link MimeType#TEXT_HTML}, {@link MimeType#APPLICATION_JAVASCRIPT} 
 *  and {@link MimeType#TEXT_CSS})
 * <br>
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 30, 2010 10:13:08 AM evgen $
 *
 */
public interface TokenCollectorExt 
{

   /**
    * Token Collector entry point.
    * Calls from AutocopletionManager.
    * Must call {@link TokensCollectedCallback#onTokensCollected(List, String, String, String)} 
    * when tokens collected.
    * 
    * @param line Line where calls autocomplete;
    * @param token Current {@link Token} ;
    * @param lineNum Number of line;
    * @param cursorPos Position of cursor where called autocomplete;
    * @param tokenFromParser {@link List} of {@link Token} pased by editor;
    * @param tokensCollectedCallback Callback for return tokens;
    */
   void collectTokens(String line, Token token, int lineNum, int cursorPos,  List<Token> tokenFromParser, TokensCollectedCallback<TokenExt> tokensCollectedCallback);
   
}
