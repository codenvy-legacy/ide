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

package org.eclipse.che.ide;

import org.eclipse.che.ide.api.parts.PartStackUIResources;

import org.eclipse.che.ide.notification.NotificationResources;
import org.eclipse.che.ide.part.projectexplorer.ProjectTreeNodeRenderer;
import org.eclipse.che.ide.projecttype.wizard.ProjectWizardResources;
import org.eclipse.che.ide.menu.MenuResources;

import org.eclipse.che.ide.ui.DialogBoxResources;
import org.eclipse.che.ide.ui.buttonLoader.ButtonLoaderResources;
import org.eclipse.che.ide.ui.cellview.CellTableResources;
import org.eclipse.che.ide.ui.cellview.DataGridResources;
import org.eclipse.che.ide.ui.list.CategoriesList;
import org.eclipse.che.ide.ui.list.SimpleList;
import org.eclipse.che.ide.ui.tree.Tree;
import org.eclipse.che.ide.ui.zeroClipboard.ZeroClipboardResources;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Interface for resources, e.g., css, images, text files, etc.
 * <p/>
 * Tree.Resources,
 * ProjectTreeNodeRenderer.Resources,
 * Editor.Resources,
 * LineNumberRenderer.Resources,
 * EditableContentArea.Resources,
 * PartStackUIResources,
 */
public interface Resources extends Tree.Resources,
                                   ProjectTreeNodeRenderer.Resources,
                                   PartStackUIResources,
                                   SimpleList.Resources,
                                   MenuResources,
                                   DialogBoxResources,
                                   ZeroClipboardResources,
                                   NotificationResources,
                                   DataGridResources,
                                   CellTableResources,
                                   CategoriesList.Resources,
                                   ButtonLoaderResources,
                                   ProjectWizardResources {

    /** Interface for css resources. */
    public interface CoreCss extends CssResource {
        String simpleListContainer();

        String mainText();

        // wizard's styles
        String mainFont();

        String mainBoldFont();

        String defaultFont();

        String warningFont();

        String errorFont();

        String greyFontColor();

        String cursorPointer();

        String line();

        String editorFullScreen();

        String editorFullScreenSvgDown();

        String privacy();

        String privacyTooltip();

        String privateProjectSvg();

        String publicProjectSvg();
    }

    @Source({"Core.css", "org/eclipse/che/ide/common/constants.css", "org/eclipse/che/ide/api/ui/style.css"})
    @NotStrict
    CoreCss coreCss();

    @Source("part/projectexplorer/project_explorer.png")
    ImageResource projectExplorer();

    @Source("part/projectexplorer/project-closed.png")
    ImageResource projectClosed();

    @Source("wizard/arrow.svg")
    SVGResource wizardArrow();

    @Source("extension/extention.png")
    ImageResource extension();

    @Source("texteditor/save-all.png")
    ImageResource saveAll();

    @Source("texteditor/open-list.png")
    ImageResource listOpenedEditors();

    @Source("xml/xml.svg")
    SVGResource xmlFile();

    @Source("about/logo.png")
    ImageResource logo();

    @Source("console/clear.svg")
    SVGResource clear();

    @Source("actions/about.svg")
    SVGResource about();

    @Source("actions/help.svg")
    SVGResource help();

    @Source("actions/find-actions.svg")
    SVGResource findActions();

    @Source("actions/undo.svg")
    SVGResource undo();

    @Source("actions/redo.svg")
    SVGResource redo();

    @Source("actions/project-configuration.svg")
    SVGResource projectConfiguration();

    @Source("actions/forums.svg")
    SVGResource forums();

    @Source("actions/feature-vote.svg")
    SVGResource featureVote();

    @Source("actions/close-project.svg")
    SVGResource closeProject();

    @Source("actions/delete.svg")
    SVGResource delete();

    @Source("actions/new-resource.svg")
    SVGResource newResource();

    @Source("actions/navigate-to-file.svg")
    SVGResource navigateToFile();

    @Source("actions/open-project.svg")
    SVGResource openProject();

    @Source("actions/save.svg")
    SVGResource save();

    @Source("actions/preferences.svg")
    SVGResource preferences();

    @Source("actions/rename.svg")
    SVGResource rename();

    @Source("actions/format.svg")
    SVGResource format();

    @Source("actions/import.svg")
    SVGResource importProject();

    @Source("actions/upload-file.svg")
    SVGResource uploadFile();

    @Source("actions/resize-icon.svg")
    SVGResource fullscreen();

    @Source("project/private-project.svg")
    SVGResource privateProject();

    @Source("project/public-project.svg")
    SVGResource publicProject();

    @Source("workspace/codenvy-placeholder.png")
    ImageResource codenvyPlaceholder();

    @Source("defaulticons/file.svg")
    SVGResource defaultFile();

    @Source("defaulticons/default.svg")
    SVGResource defaultIcon();

    @Source("defaulticons/folder.svg")
    SVGResource defaultFolder();

    @Source("defaulticons/project.svg")
    SVGResource defaultProject();

    @Source("defaulticons/image-icon.svg")
    SVGResource defaultImage();

    @Source("defaulticons/md.svg")
    SVGResource mdFile();

    @Source("defaulticons/json.svg")
    SVGResource jsonFile();

    @Source("part/outline/no-outline.svg")
    SVGResource noOutline();
}