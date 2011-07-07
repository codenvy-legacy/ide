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
package org.exoplatform.ide.editor.codemirror.parser;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;



import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 *
 */
public class CssParser extends CodeMirrorParserImpl
{
   @Override
   public List<TokenBeenImpl> getTokenList(String editorId, JavaScriptObject editor, HandlerManager eventBus)
   {
      return new ArrayList<TokenBeenImpl>();
   }
   
}