/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.client.operation.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.ItemTree;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SearchResultsViewExtended extends ViewImpl implements
                                                        org.exoplatform.ide.client.operation.search.SearchResultsPresenter.Display {

    private static final String ID = "ideSearchResultView";

    /** Initial width of this view */
    private static final int WIDTH = 250;

    /** Initial height of this view */
    private static final int HEIGHT = 450;

    private static SearchResultsViewExtendedUiBinder uiBinder = GWT.create(SearchResultsViewExtendedUiBinder.class);

    interface SearchResultsViewExtendedUiBinder extends UiBinder<Widget, SearchResultsViewExtended> {
    }

    @UiField
    ItemTree treeGrid;

    private static final String TITLE = IDE.NAVIGATION_CONSTANT.searchResultTitle();

    public SearchResultsViewExtended() {
        super(ID, "navigation", TITLE, new Image(IDEImageBundle.INSTANCE.search()), WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));
    }

    @Override
    public TreeGridItem<Item> getSearchResultTree() {
        return treeGrid;
    }

    @Override
    public List<Item> getSelectedItems() {
        return treeGrid.getSelectedItems();
    }

    @Override
    public void selectItem(String href) {
        treeGrid.selectItem(href);
    }

    @Override
    public void deselectAllItems() {
        treeGrid.deselectAllRecords();
    }

}
