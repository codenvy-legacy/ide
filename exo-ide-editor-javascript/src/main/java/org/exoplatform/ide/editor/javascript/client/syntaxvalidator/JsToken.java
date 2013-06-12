/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.editor.javascript.client.syntaxvalidator;

import com.codenvy.ide.json.client.JsoArray;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Class represents JavaScript token which was produced by Esprima parser.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: JsToken.java May 29, 2013 1:36:58 PM azatsarynnyy $
 */
final public class JsToken extends JavaScriptObject {

    /** Default constructor. */
    protected JsToken() {
    }

    /**
     * Returns token type.
     * 
     * @return type of the token
     */
    public native String getType()/*-{
                                      return this.type;
                                  }-*/;

    /**
     * Returns token's name.
     * 
     * @return token's name
     */
    public native String getName()/*-{
                                  if (this.type == "FunctionDeclaration") {
                                      return this.id.name;
                                  } else if (this.type == "VariableDeclaration") {
                                      return this.declarations[0].id.name;
                                  } else if (this.type == "Identifier") {
                                      return this.name;
                                  } else if (this.type == "Property") {
                                      if (this.key.type == "Identifier") {
                                          return this.key.name;
                                      } else if (this.key.type == "Literal") {
                                          return this.key.raw;
                                      }
                                  }
                                  }-*/;

    /**
     * Returns the line number in source where the token begins.
     * 
     * @return the line number in source where the token begins
     */
    public native int getLineNumber()/*-{
                                         return this.loc.start.line;
                                     }-*/;

    /**
     * Returns an array of sub-tokens of this token.
     * 
     * @return array of tokens's sub-tokens
     */
    public native JsoArray<JsToken> getSubTokens()/*-{
                                             if (this.type == "FunctionDeclaration" && this.body.type == "BlockStatement") {
                                                 return this.body.body;
                                             } else if (this.type == "VariableDeclaration") {
                                                 if (this.declarations[0].init.type == "ObjectExpression") {
                                                     return this.declarations[0].init.properties;
                                                 } else if (this.declarations[0].init.type == "CallExpression") {
                                                     if (this.declarations[0].init.callee.type == "FunctionExpression") {
                                                         return this.declarations[0].init.callee.body.body;
                                                     }
                                                 }
                                             }
                                                 return null;
                                             }-*/;

    /**
     * Returns parameters array of the function.
     * 
     * @return array of function's parameters
     */
    public native JsoArray<JsToken> getParams()/*-{
                                               if (this.type == "FunctionDeclaration") {
                                                   return this.params;
                                               }
                                                   return null;
                                               }-*/;
}
