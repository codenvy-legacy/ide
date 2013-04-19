/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.search;

import com.codenvy.ide.part.PartStackUIResources;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerView;
import com.codenvy.ide.part.view.BaseView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class SearchPartViewImpl extends BaseView<SearchPartView.ActionDelegate> implements SearchPartView {
    private static SearchPartViewImplUiBinder ourUiBinder = GWT.create(SearchPartViewImplUiBinder.class);

    @Inject
    public SearchPartViewImpl(PartStackUIResources resources) {
        super(resources);
        container.add(ourUiBinder.createAndBindUi(this));

    }

    interface SearchPartViewImplUiBinder
            extends UiBinder<HTMLPanel, SearchPartViewImpl> {
    }
}