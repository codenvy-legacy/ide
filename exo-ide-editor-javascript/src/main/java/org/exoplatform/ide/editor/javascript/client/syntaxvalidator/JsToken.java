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
     * Returns the body of the function. It is an array of nested tokens.
     * 
     * @return array of function's nested tokens
     */
    public native JsoArray<JsToken> getBody()/*-{
                                             if (this.type == "FunctionDeclaration" && this.body.type == "BlockStatement") {
                                                 return this.body.body;
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
