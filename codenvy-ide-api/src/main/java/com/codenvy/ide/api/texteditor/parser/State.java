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

package com.codenvy.ide.api.texteditor.parser;

/** Interface that represents Parser state. */
public interface State {

    /**
     * Clones the object.
     * <p/>
     * <p>Parser parameter is used by native Parser implementation.
     *
     * @param parser
     *         parser that created this instance
     */
    State copy(Parser parser);
}
