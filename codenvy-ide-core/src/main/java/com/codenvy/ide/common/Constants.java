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

package com.codenvy.ide.common;

import com.codenvy.ide.util.browser.UserAgent;

/** Constants that we can use in CssResource expressions. */
public final class Constants {
    public static final int SCROLLBAR_SIZE = UserAgent.isFirefox() ? 17 : 12;

    /** A timer delay for actions that happen after a "hover" period. */
    public static final int MOUSE_HOVER_DELAY = 600;

    private Constants() {
    } // COV_NF_LINE
}
