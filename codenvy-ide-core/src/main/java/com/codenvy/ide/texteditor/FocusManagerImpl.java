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

package com.codenvy.ide.texteditor;

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.dom.Element;

import com.codenvy.ide.texteditor.api.FocusManager;
import com.codenvy.ide.util.ListenerManager;
import com.codenvy.ide.util.ListenerManager.Dispatcher;
import com.codenvy.ide.util.ListenerRegistrar;
import com.codenvy.ide.util.dom.Elements;


/** Tracks the focus state of the editor. */
public class FocusManagerImpl implements FocusManager {

    private final ListenerManager<com.codenvy.ide.texteditor.api.FocusManager.FocusListener> focusListenerManager =
            ListenerManager.create();

    private boolean hasFocus;

    private final Element inputElement;

    FocusManagerImpl(Buffer buffer, Element inputElement) {
        this.inputElement = inputElement;

        attachEventHandlers(buffer);
        hasFocus = inputElement.equals(Elements.getActiveElement());
    }

    private void attachEventHandlers(Buffer buffer) {
        inputElement.addEventListener(Event.FOCUS, new EventListener() {
            private final Dispatcher<com.codenvy.ide.texteditor.api.FocusManager.FocusListener> dispatcher =
                    new Dispatcher<com.codenvy.ide.texteditor.api.FocusManager.FocusListener>() {
                        @Override
                        public void dispatch(FocusListener listener) {
                            listener.onFocusChange(true);
                        }
                    };

            @Override
            public void handleEvent(Event evt) {
                hasFocus = true;
                focusListenerManager.dispatch(dispatcher);
            }
        }, false);

        inputElement.addEventListener(Event.BLUR, new EventListener() {
            private final Dispatcher<com.codenvy.ide.texteditor.api.FocusManager.FocusListener> dispatcher =
                    new Dispatcher<com.codenvy.ide.texteditor.api.FocusManager.FocusListener>() {
                        @Override
                        public void dispatch(FocusListener listener) {
                            listener.onFocusChange(false);
                        }
                    };

            @Override
            public void handleEvent(Event evt) {
                hasFocus = false;
                focusListenerManager.dispatch(dispatcher);
            }
        }, false);

        buffer.getMouseClickListenerRegistrar().add(new Buffer.MouseClickListener() {
            @Override
            public void onMouseClick(int x, int y) {
                focus();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public ListenerRegistrar<com.codenvy.ide.texteditor.api.FocusManager.FocusListener> getFocusListenerRegistrar() {
        return focusListenerManager;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasFocus() {
        return hasFocus;
    }

    /** {@inheritDoc} */
    @Override
    public void focus() {
        inputElement.focus();
    }
}
