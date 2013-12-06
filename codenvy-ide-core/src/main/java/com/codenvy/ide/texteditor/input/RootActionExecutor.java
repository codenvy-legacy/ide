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

package com.codenvy.ide.texteditor.input;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.util.input.SignalEvent;

/** Implementation of proxy that delegated execution to a list of executors. */
public class RootActionExecutor implements ActionExecutor {

    /** Object used to unregister delegate. */
    public class Remover {

        public Remover(ActionExecutor instance) {
            this.instance = instance;
        }

        private ActionExecutor instance;

        public void remove() {
            delegates.remove(instance);
        }
    }

    private final Array<ActionExecutor> delegates = Collections.createArray();

    public Remover addDelegate(ActionExecutor delegate) {
        delegates.add(delegate);
        return new Remover(delegate);
    }

    @Override
    public boolean execute(String actionName, InputScheme scheme, SignalEvent event) {
        for (int i = 0, l = delegates.size(); i < l; i++) {
            if (delegates.get(i).execute(actionName, scheme, event)) {
                return true;
            }
        }
        return false;
    }
}
