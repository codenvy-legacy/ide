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

package com.google.collide.client;

import com.google.collide.client.code.ParticipantList;
import com.google.collide.client.common.BaseResources;
import com.google.collide.client.editor.Editor;
import com.google.collide.client.editor.gutter.GutterNotificationResources;
import com.google.collide.client.editor.renderer.LineNumberRenderer;
import com.google.collide.client.status.StatusPresenter;
import com.google.collide.client.ui.popup.CenterPanel;
import com.google.collide.client.ui.popup.Popup;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource;

/**
 * Interface for resources, e.g., css, images, text files, etc. Make sure you
 * add your resource to  {@link CollabEditorExtension}
 */
public interface Resources extends
                           BaseResources.Resources,
//    StatusPresenter.Resources,
                           Editor.Resources,
                           LineNumberRenderer.Resources,
                           com.google.collide.client.code.EditableContentArea.Resources,
                           com.google.collide.client.syntaxhighlighter.SyntaxHighlighterRenderer.Resources,
                           GutterNotificationResources,
                           com.google.collide.client.code.autocomplete.integration.AutocompleteUiController.Resources,
                           ParticipantList.Resources,
                                   // TODO: Once we have actual consumers of the Tooltip class, we
                                   // can just have them extend it instead of doing it on the base interface.
//    Tooltip.Resources,
                           Popup.Resources, CenterPanel.Resources, StatusPresenter.Resources {

    /** Interface for css resources. */
    public interface AppCss extends CssResource {
    }

    @Source({"app.css", "com/google/collide/client/common/constants.css"})
    @NotStrict
    AppCss appCss();

    @Source("com/google/collide/client/collaboration/collaborators.png")
    ImageResource getCollaborationImage();

    @Source("com/google/collide/client/collaboration/collaboratorsDisabled.png")
    ImageResource getCollaborationImageDisabled();

}
