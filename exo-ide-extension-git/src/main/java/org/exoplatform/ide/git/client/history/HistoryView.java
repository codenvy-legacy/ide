/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.git.client.history;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.button.IconButton;
import org.exoplatform.gwtframework.ui.client.component.Panel;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.toolbar.Toolbar;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorParameters;
import org.exoplatform.ide.editor.api.EditorProducer;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.codemirror.CodeMirrorProducer;
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.commit.RevisionGrid;
import org.exoplatform.ide.git.shared.Revision;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * View for displaying the history of commits and it's diff.
 * Must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 29, 2011 3:00:37 PM anya $
 *
 */
public class HistoryView extends ViewImpl implements HistoryPresenter.Display
{
   public static final String ID = "ideHistoryView";

   public static final String TYPE = "information";

   public static final String TITLE = "History";

   public static final String PROJECT_CHANGES_BUTTON_TITLE = "Show changes in project";

   public static final String RESOURCE_CHANGES_BUTTON_TITLE = "Show changes of selected resource";

   public static final String DIFF_WITH_INDEX_BUTTON_TITLE = "Show diff with index";

   public static final String DIFF_WITH_WORK_TREE_BUTTON_TITLE = "Show diff with working tree";

   public static final String DIFF_WITH_PREV_VERSION_BUTTON_TITLE = "Show diff with previous version";

   private Editor editor;

   /**
    * Grid for displaying revisions.
    */
   @UiField
   RevisionGrid revisionGrid;

   /**
    * View's toolbar.
    */
   @UiField
   Toolbar toolbar;

   /**
    * Editor panel.
    */
   @UiField
   Panel editorPanel;

   /**
    * Field to show commit date of first commit.
    */
   @UiField
   TextField comitADate;
   
   /**
    * Field to show commit revision of first commit.
    */
   @UiField
   TextField comitARevision;
   
   /**
    * Field to show commit date of second commit.
    */
   @UiField
   TextField comitBDate;
   
   /**
    * Field to show commit's revision of second commit.
    */
   @UiField
   TextField comitBRevision;

   @UiField
   TableCellElement revisionBTitle;

   @UiField
   TableCellElement revisionBDate;

   /**
    * Refresh button.
    */
   private IconButton refreshButton;

   /**
    * Show changes in whole project button.
    */
   private IconButton projectChangesButton;

   /**
    * Show changes of the resource button.
    */
   private IconButton resourceChangesButton;

   /**
    * Diff selected commit with index button.
    */
   private IconButton diffWithIndexButton;
   
   /**
    * Diff selected commit with working tree button.
    */
   private IconButton diffWithWorkTreeButton;
   
   /**
    * Diff selected commit with previous commit button.
    */
   private IconButton diffWithPrevCommitButton;

   /**
    * Editor's producer.
    */
   private EditorProducer editorProducer = new CodeMirrorProducer(MimeType.DIFF, "CodeMirror diff editor", "diff", "",
      true, new CodeMirrorConfiguration("['parsediff.js']", // generic code parsers
         "['" + CodeMirrorConfiguration.PATH + "css/diffcolors.css']" // code styles
      ));

   /**
    * UI binder for this view.
    */
   private static HistoryViewUiBinder uiBinder = GWT.create(HistoryViewUiBinder.class);

   interface HistoryViewUiBinder extends UiBinder<Widget, HistoryView>
   {
   }

   public HistoryView()
   {
      super(ID, TYPE, TITLE, new Image(GitClientBundle.INSTANCE.history()), 400, 250, true);
      add(uiBinder.createAndBindUi(this));
      
      comitADate.setHeight(20);
      comitARevision.setHeight(20);
      comitBDate.setHeight(20);
      comitARevision.setHeight(20);
      createControls();
   }

   /**
    * Create view's controls.
    */
   private void createControls()
   {
      refreshButton =
         new IconButton(ImageHelper.getImageHTML(GitClientBundle.INSTANCE.refresh()),
            ImageHelper.getImageHTML(GitClientBundle.INSTANCE.refreshDisabled()));
      toolbar.addItem(refreshButton, true);
      toolbar.addDelimiter(true);

      projectChangesButton =
         new IconButton(ImageHelper.getImageHTML(GitClientBundle.INSTANCE.projectLevel()),
            ImageHelper.getImageHTML(GitClientBundle.INSTANCE.projectLevelDisabled()));
      projectChangesButton.setTitle(PROJECT_CHANGES_BUTTON_TITLE);
      toolbar.addItem(projectChangesButton, true);

      resourceChangesButton =
         new IconButton(ImageHelper.getImageHTML(GitClientBundle.INSTANCE.resourceLevel()),
            ImageHelper.getImageHTML(GitClientBundle.INSTANCE.resourceLevelDisabled()));
      resourceChangesButton.setTitle(RESOURCE_CHANGES_BUTTON_TITLE);
      toolbar.addItem(resourceChangesButton, true);
      toolbar.addDelimiter(true);

      diffWithIndexButton =
         new IconButton(ImageHelper.getImageHTML(GitClientBundle.INSTANCE.diffIndex()),
            ImageHelper.getImageHTML(GitClientBundle.INSTANCE.diffIndexDisabled()));
      diffWithIndexButton.setTitle(DIFF_WITH_INDEX_BUTTON_TITLE);
      toolbar.addItem(diffWithIndexButton, true);

      diffWithWorkTreeButton =
         new IconButton(ImageHelper.getImageHTML(GitClientBundle.INSTANCE.diffWorkTree()),
            ImageHelper.getImageHTML(GitClientBundle.INSTANCE.diffWorTreeDisabled()));
      diffWithWorkTreeButton.setTitle(DIFF_WITH_WORK_TREE_BUTTON_TITLE);
      toolbar.addItem(diffWithWorkTreeButton, true);

      diffWithPrevCommitButton =
         new IconButton(ImageHelper.getImageHTML(GitClientBundle.INSTANCE.diffPrevVersion()),
            ImageHelper.getImageHTML(GitClientBundle.INSTANCE.diffPrevVersionDisabled()));
      diffWithPrevCommitButton.setTitle(DIFF_WITH_PREV_VERSION_BUTTON_TITLE);
      toolbar.addItem(diffWithPrevCommitButton, true);
   }

   /**
    * Create editor for diff content.
    * 
    * @param content content to be shown
    * @return {@link Editor} created editor
    */
   public Editor createEditor(String content)
   {
      final HashMap<String, Object> params = new HashMap<String, Object>();
      params.put(EditorParameters.IS_READ_ONLY, true);
      params.put(EditorParameters.IS_SHOW_LINE_NUMER, false);
      params.put(EditorParameters.HOT_KEY_LIST, new ArrayList<String>());

      editor = editorProducer.createEditor(content, IDE.EVENT_BUS, params);
      editorPanel.add(editor);
      return editor;
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#getRevisionGrid()
    */
   @Override
   public ListGridItem<Revision> getRevisionGrid()
   {
      return revisionGrid;
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#getRefreshButton()
    */
   @Override
   public HasClickHandlers getRefreshButton()
   {
      return refreshButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#getProjectChangesButton()
    */
   @Override
   public HasClickHandlers getProjectChangesButton()
   {
      return projectChangesButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#getResourceChangesButton()
    */
   @Override
   public HasClickHandlers getResourceChangesButton()
   {
      return resourceChangesButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#selectProjectChangesButton(boolean)
    */
   @Override
   public void selectProjectChangesButton(boolean selected)
   {
      projectChangesButton.setSelected(selected);
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#selectResourceChangesButton(boolean)
    */
   @Override
   public void selectResourceChangesButton(boolean selected)
   {
      resourceChangesButton.setSelected(selected);
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#displayDiffContent(java.lang.String)
    */
   @Override
   public void displayDiffContent(String diffContent)
   {
      if (editor == null)
      {
         editor = createEditor(diffContent);
      }
      else
      {
         editor.setText(diffContent);
      }
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#getDiffWithIndexButton()
    */
   @Override
   public HasClickHandlers getDiffWithIndexButton()
   {
      return diffWithIndexButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#getDiffWithWorkingTreeButton()
    */
   @Override
   public HasClickHandlers getDiffWithWorkingTreeButton()
   {
      return diffWithWorkTreeButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#getDiffWithPrevVersionButton()
    */
   @Override
   public HasClickHandlers getDiffWithPrevVersionButton()
   {
      return diffWithPrevCommitButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#selectDiffWithIndexButton(boolean)
    */
   @Override
   public void selectDiffWithIndexButton(boolean selected)
   {
      diffWithIndexButton.setSelected(selected);
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#selectDiffWithWorkingTreeButton(boolean)
    */
   @Override
   public void selectDiffWithWorkingTreeButton(boolean selected)
   {
      diffWithWorkTreeButton.setSelected(selected);
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#selectDiffWithPrevVersionButton(boolean)
    */
   @Override
   public void selectDiffWithPrevVersionButton(boolean selected)
   {
      diffWithPrevCommitButton.setSelected(selected);
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#displayCompareText(org.exoplatform.ide.git.shared.Revision, java.lang.String)
    */
   @Override
   public void displayCompareText(Revision revision, String text)
   {
      if (revision == null)
      {
         comitADate.setValue("");
         comitARevision.setValue("");
      }
      else
      {
         DateTimeFormat formatter = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
         comitADate.setValue(formatter.format(new Date(revision.getCommitTime())));
         comitARevision.setValue(revision.getId());
      }

      revisionBDate.setAttribute("rowspan", "2");
      revisionBDate.setInnerHTML("<nobr>" + text + "</nobr>");
      revisionBTitle.setInnerHTML("");
      comitBDate.hide();
      comitBRevision.hide();
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#displayCompareVersion(org.exoplatform.ide.git.shared.Revision, org.exoplatform.ide.git.shared.Revision)
    */
   @Override
   public void displayCompareVersion(Revision revisionA, Revision revisionB)
   {
      revisionBDate.setAttribute("rowspan", "0");
      revisionBDate.setInnerText("Date");
      revisionBTitle.setInnerText("Rev.");
      comitBDate.show();
      comitBRevision.show();
      DateTimeFormat formatter = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
      comitADate.setValue(formatter.format(new Date(revisionA.getCommitTime())));
      comitARevision.setValue(revisionA.getId());
      comitBDate.setValue(formatter.format(new Date(revisionB.getCommitTime())));
      comitBRevision.setValue(revisionB.getId());
   }
}
