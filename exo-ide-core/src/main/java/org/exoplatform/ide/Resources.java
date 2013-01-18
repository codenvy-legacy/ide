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

package org.exoplatform.ide;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;

import org.exoplatform.ide.menu.MenuResources;
import org.exoplatform.ide.part.PartStackUIResources;
import org.exoplatform.ide.texteditor.EditableContentArea;
import org.exoplatform.ide.texteditor.TextEditorViewImpl;
import org.exoplatform.ide.texteditor.renderer.LineNumberRenderer;
import org.exoplatform.ide.tree.FileTreeNodeRenderer;
import org.exoplatform.ide.tree.Tree;
import org.exoplatform.ide.ui.list.SimpleList;
import org.exoplatform.ide.wizard.newgenericproject.NewGenericProjectWizardResource;
import org.exoplatform.ide.wizard.newproject.NewProjectWizardResource;
import org.exoplatform.ide.wizard.newresource.NewResourceWizardResources;

/**
 * Interface for resources, e.g., css, images, text files, etc.
 * 
 * Tree.Resources, 
 * FileTreeNodeRenderer.Resources, 
 * Editor.Resources, 
 * LineNumberRenderer.Resources,
 * EditableContentArea.Resources, 
 * PartStackUIResources, 
 * impleList.Resources
 * 
 */
public interface Resources extends
   //    StatusPresenter.Resources,
   Tree.Resources, FileTreeNodeRenderer.Resources, TextEditorViewImpl.Resources, LineNumberRenderer.Resources,
   EditableContentArea.Resources, PartStackUIResources, SimpleList.Resources, NewProjectWizardResource,
   NewGenericProjectWizardResource, NewResourceWizardResources, MenuResources

// TODO: Once we have actual consumers of the Tooltip class, we
// can just have them extend it instead of doing it on the base interface.
//    Tooltip.Resources,
{

   /**
    * Interface for css resources.
    */
   public interface CoreCss extends CssResource
   {
      String simpleListContainer();
   }

   @Source({"Core.css", "org/exoplatform/ide/common/constants.css"})
   @NotStrict
   CoreCss coreCss();
}
