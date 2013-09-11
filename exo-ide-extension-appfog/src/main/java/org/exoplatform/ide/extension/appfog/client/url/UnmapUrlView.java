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
package org.exoplatform.ide.extension.appfog.client.url;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;

/**
 * Unmap URL view.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class UnmapUrlView extends ViewImpl implements UnmapUrlPresenter.Display {
    public static final String ID = "ideAppfogApplicationInfoView";

    public static final String LISTGRID_ID = "ideAppfogRegisteredUrlsGridView";

    private static final String MAP_URL_FIELD_ID = "applicationURLsURLField";

    private static final int HEIGHT = 305;

    private static final int WIDTH = 450;

    private static UnmapUrlViewUiBinder uiBinder = GWT.create(UnmapUrlViewUiBinder.class);

    @UiField
    TextInput mapUrlField;

    @UiField
    ImageButton mapUrlButton;

    @UiField
    ImageButton closeButton;

    @UiField
    RegisteredUrlsGrid registeredUrlsGrid;

    interface UnmapUrlViewUiBinder extends UiBinder<Widget, UnmapUrlView> {
    }

    public UnmapUrlView() {
        super(ID, ViewType.MODAL, AppfogExtension.LOCALIZATION_CONSTANT.unmapUrlViewTitle(), null, WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));
        registeredUrlsGrid.setID(LISTGRID_ID);
        mapUrlField.setName(MAP_URL_FIELD_ID);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.url.UnmapUrlPresenter.Display#getCloseButton() */
    @Override
    public HasClickHandlers getCloseButton() {
        return closeButton;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.url.UnmapUrlPresenter.Display#getRegisteredUrlsGrid() */
    @Override
    public ListGridItem<String> getRegisteredUrlsGrid() {
        return registeredUrlsGrid;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.url.UnmapUrlPresenter.Display#getUnmapUrlListGridButton() */
    @Override
    public HasUnmapClickHandler getUnmapUrlListGridButton() {
        return registeredUrlsGrid;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.url.UnmapUrlPresenter.Display#getMapUrlField() */
    @Override
    public HasValue<String> getMapUrlField() {
        return mapUrlField;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.url.UnmapUrlPresenter.Display#getMapUrlButton() */
    @Override
    public HasClickHandlers getMapUrlButton() {
        return mapUrlButton;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.url.UnmapUrlPresenter.Display#enableMapUrlButton(boolean) */
    @Override
    public void enableMapUrlButton(boolean enable) {
        mapUrlButton.setEnabled(enable);
    }

}
