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

package com.codenvy.ide.texteditor.api.parser;

import com.codenvy.ide.collections.Array;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;


/**
 * One of the CodeMirror2 parsing modes, with utilities for saving/restoring the
 * parser state and returning a list of tokens for a string of text.
 */
public class CmParser extends JavaScriptObject implements Parser {

    protected CmParser() {
    }

    @Override
    public final State defaultState() {
        return getStartState();
    }

    @Override
    public final void parseNext(Stream stream, State state, Array<Token> tokens) {
        String tokenName = token(stream, state);
//        Window.alert(" sdlfsdf  " + tokenName);
        String tokenValue = updateStreamPosition(stream);
        getTokenFactory().push(getName(state), state, tokenName, tokenValue, tokens);
    }

    private native TokenFactory<State> getTokenFactory()/*-{
        return this.factory;
    }-*/;

    private native String token(Stream stream, State state) /*-{
        return this.token(stream, state);
    }-*/;

    /**
     * Advance the stream index pointers.
     *
     * @return a String with the remaining stream contents to be parsed.
     */
    private native String updateStreamPosition(Stream stream) /*-{
        var value = stream.string.slice(stream.start, stream.pos);
        stream.start = stream.pos;
        return value;
    }-*/;

    private native CmState getStartState() /*-{
        var state = $wnd.CodeMirror.startState(this);
        return (state === true) ? {} : state;
    }-*/;

    @Override
    public final native String getName(State state) /*-{
        return state.mode || this.parserName;
    }-*/;

    @Override
    public final native boolean hasSmartIndent() /*-{
        return (this.indent && !this.__preventSmartIndent) ? true : false;
    }-*/;

    @Override
    public final native int indent(State stateAbove, String text) /*-{
        if (this.indent && stateAbove) {
            return this.indent(stateAbove, text);
        }
        return -1;
    }-*/;

    final native void setPreventSmartIndent(boolean preventSmartIndent) /*-{
        this.__preventSmartIndent = preventSmartIndent;
    }-*/;

    public final void setNameAndFactory(String name, TokenFactory<?> tokenFactory) {
        setNameFactory(name, tokenFactory);
    }

    private native void setNameFactory(String name, TokenFactory<?> tokenFactory)/*-{
        this.factory = tokenFactory;
        this.parserName = name;
    }-*/;

    @Override
    public final native Stream createStream(String text) /*-{
        return new $wnd.CodeMirror.StringStream(text);
    }-*/;

}
