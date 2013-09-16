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
package org.exoplatform.ide.client.debug;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.util.ImageFactory;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ShowImagesView extends ViewImpl implements org.exoplatform.ide.client.debug.ShowImagesPresenter.Display {

    public static final String ID = "ideShowImagesView";

    private static final int WIDTH = 450;

    private static final int HEIGHT = 200;

    private static ShowImagesViewUiBinder uiBinder = GWT.create(ShowImagesViewUiBinder.class);

    interface ShowImagesViewUiBinder extends UiBinder<Widget, ShowImagesView> {
    }

    @UiField
    ImageButton closeButton;

    @UiField
    Grid imagesGrid;

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.showImagesTitle();

    public ShowImagesView() {
        super(ID, "information", TITLE, new Image(IDEImageBundle.INSTANCE.about()), WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));
    }

    @Override
    public HasClickHandlers getOkButton() {
        return closeButton;
    }

    @Override
    public void updateImageList() {
        List<String> imageNames = ImageFactory.getImageNames();
        imagesGrid.resize(imageNames.size() + 1, 3);

        int row = 0;
        for (String imageName : imageNames) {
            Image image = ImageFactory.getImage(imageName);
            Image disabledImage = ImageFactory.getDisabledImage(imageName);

            imagesGrid.getRowFormatter().getElement(row).getStyle().setProperty("height", "20px");

            imagesGrid.setWidget(row, 0, image);
            imagesGrid.getCellFormatter().getElement(row, 0).getStyle().setProperty("width", "20px");
            imagesGrid.setWidget(row, 1, disabledImage);
            imagesGrid.getCellFormatter().getElement(row, 1).getStyle().setProperty("width", "20px");
            imagesGrid.setText(row, 2, imageName);

            row++;
        }

        String div = "<div style=\"width:1px; height:1px; \"></div>";

        imagesGrid.setHTML(row, 0, div);
        imagesGrid.setHTML(row, 1, div);
        imagesGrid.setHTML(row, 2, div);

    }

}
