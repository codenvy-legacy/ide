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

package com.codenvy.ide;

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.menu.MenuResources;
import com.codenvy.ide.notification.NotificationResources;
import com.codenvy.ide.texteditor.EditableContentArea;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.texteditor.renderer.LineNumberRenderer;
import com.codenvy.ide.tree.FileTreeNodeRenderer;
import com.codenvy.ide.ui.DialogBoxResources;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.welcome.WelcomePageResources;
import com.codenvy.ide.wizard.WizardResource;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource;


/**
 * Interface for resources, e.g., css, images, text files, etc.
 * <p/>
 * Tree.Resources,
 * FileTreeNodeRenderer.Resources,
 * Editor.Resources,
 * LineNumberRenderer.Resources,
 * EditableContentArea.Resources,
 * PartStackUIResources,
 * impleList.Resources
 */
public interface Resources extends Tree.Resources, FileTreeNodeRenderer.Resources, TextEditorViewImpl.Resources,
                                   LineNumberRenderer.Resources, EditableContentArea.Resources, PartStackUIResources, SimpleList.Resources,
                                   WizardResource, MenuResources, DialogBoxResources, WelcomePageResources, NotificationResources {

    /** Interface for css resources. */
    public interface CoreCss extends CssResource {
        String simpleListContainer();

        String mainText();
    }

    @Source({"Core.css", "com/codenvy/ide/common/constants.css", "com/codenvy/ide/api/ui/style.css"})
    @NotStrict
    CoreCss coreCss();


    @Source("tree/project_explorer.png")
    ImageResource projectExplorer();

    @Source("tree/delete.png")
    ImageResource delete();

    @Source("tree/project-closed.png")
    ImageResource projectClosed();

    @Source("extension/extention.png")
    ImageResource extension();
}
