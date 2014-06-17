/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.requirejs;

import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

/**
 * Loads javascript modules with requirejs.
 * 
 * @author "Mickaël Leduque"
 */
public class RequireJsLoader {

    private ModuleHolder moduleHolder;

    @Inject
    public RequireJsLoader(final ModuleHolder moduleHolder) {
        this.moduleHolder = moduleHolder;
    }

    public void require(final Callback<Void, Throwable> callback,
                        final String[] requiredScripts,
                        final String[] moduleKeys) {

        JsArrayString jsReqScripts = (JsArrayString)JavaScriptObject.createArray();
        for (final String script : requiredScripts) {
            jsReqScripts.push(script);
        }
        requireNative(jsReqScripts, new Callback<JsArray<JavaScriptObject>, Throwable>() {

            @Override
            public void onFailure(final Throwable reason) {
                callback.onFailure(reason);
            }

            @Override
            public void onSuccess(final JsArray<JavaScriptObject> result) {
                for (int i = 0; i < Math.max(result.length(), moduleKeys.length); i++) {
                    moduleHolder.setModule(moduleKeys[i], result.get(i));
                }
                callback.onSuccess(null);
            }
        });
    }

    private static native void requireNative(JsArrayString requiredScripts, Callback<JsArray<JavaScriptObject>, Throwable> callback) /*-{
        $wnd.require(requiredScripts, function() {
            var result = [];
            var args = Array.prototype.slice.call(arguments);
            args.forEach(function(module) {
                result.push(module);
            });
            callback.@com.google.gwt.core.client.Callback::onSuccess(*)(result);
        }, function(err) {
            callback.@com.google.gwt.core.client.Callback::onFailure(*)(err);
        });
    }-*/;
}
