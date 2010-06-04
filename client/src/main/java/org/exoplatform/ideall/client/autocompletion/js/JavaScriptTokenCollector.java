/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.autocompletion.js;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ideall.client.autocompletion.TokenCollector;
import org.exoplatform.ideall.client.autocompletion.TokensCollectedCallback;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class JavaScriptTokenCollector implements TokenCollector
{

   private HandlerManager eventBus;

   private ApplicationContext context;

   private TokensCollectedCallback tokensCollectedCallback;

   public JavaScriptTokenCollector(HandlerManager eventBus, ApplicationContext context,
      TokensCollectedCallback tokensCollectedCallback)
   {
      this.context = context;
      this.eventBus = eventBus;
      this.tokensCollectedCallback = tokensCollectedCallback;

   }

   public void getTokens(String prefix)
   {
      List<String> tokens = new ArrayList<String>();
      
      tokens.add("abstract");
      tokens.add("boolean");
      tokens.add("break");
      tokens.add("byte");
      tokens.add("case");
      tokens.add("catch");
      tokens.add("char");
      tokens.add("class");
      tokens.add("const");
      tokens.add("continue");
      tokens.add("debugger");
      tokens.add("default");
      tokens.add("delete");
      tokens.add("do");
      tokens.add("double");
      tokens.add("else");
      tokens.add("enum");
      tokens.add("export");
      tokens.add("extends");
      tokens.add("false");
      tokens.add("final");
      tokens.add("finally");
      tokens.add("float");
      tokens.add("for");
      tokens.add("function");
      tokens.add("goto");
      tokens.add("if");
      tokens.add("implements");
      tokens.add("import");
      tokens.add("in");
      tokens.add("instanceof");
      tokens.add("int");
      tokens.add("interface");
      tokens.add("long");
      tokens.add("native");
      tokens.add("new");
      tokens.add("null");
      tokens.add("package");
      tokens.add("private");
      tokens.add("protected");
      tokens.add("public");
      tokens.add("return");
      tokens.add("short");
      tokens.add("static");
      tokens.add("super");
      tokens.add("switch");
      tokens.add("synchronized");
      tokens.add("this");
      tokens.add("throw");
      tokens.add("throws");
      tokens.add("transient");
      tokens.add("true");
      tokens.add("try");
      tokens.add("typeof");
      tokens.add("var");
      tokens.add("void");
      tokens.add("volatile");
      tokens.add("while");
      tokens.add("with");
      
      tokensCollectedCallback.onTokensCollected(tokens);
   }

}
