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

package com.codenvy.ide.texteditor.renderer;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.util.executor.Executor;

/**
 * An executor that will defer the commands given to {@link #execute(Runnable)}
 * until render-time.
 */
public class RenderTimeExecutor implements Executor {

    private final Array<Runnable> commands = Collections.createArray();

    @Override
    public void execute(Runnable command) {
        commands.add(command);
    }

    void executeQueuedCommands() {
        for (int i = 0, n = commands.size(); i < n; i++) {
            commands.get(i).run();
        }

        commands.clear();
    }
}
