/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 * [2012] - [$today.year] Codenvy, S.A. 
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
package com.codenvy.ide.ui.dialogs;

import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vitaly Parfonov
 */
public class AskImpl {


    interface AskImplUiBinder extends UiBinder<Widget, AskImpl> {
    }

    private static AskImplUiBinder uiBinder = GWT.create(AskImplUiBinder.class);

    @Override
    protected void onClose() {

    }


    public void ask(String titel, String message, BooleanValueHandler handler) {
        setTitle(titel);
        Widget widget = uiBinder.createAndBindUi(this);
        setWidget(widget);



    }





//    @Override
//    protected void openAskDialog(Dialog dialog) {
//        HorizontalPanel content = createImageWithTextLayout(WindowResource.INSTANCE.askDialog(), dialog.getMessage());
//        final GWTDialogsWindow dialogWindow = new GWTDialogsWindow("exoAskDialog", dialog.getTitle(), 400, 130, content);
//        dialogWindow.setModal(dialog.getModal());
//        ImageButton yesButton = createButton("Yes", null);
//        ImageButton noButton = createButton("No", null);
//        dialogWindow.getButtonsLayout().add(yesButton);
//        dialogWindow.getButtonsLayout().add(noButton);
//
//        yesButton.addClickHandler(new ClickHandler() {
//            public void onClick(ClickEvent event) {
//                if (valueCallback != null) {
//                    dialogWindow.destroy();
//                    booleanCallback.execute(true);
//                }
//            }
//        });
//
//        noButton.addClickHandler(new ClickHandler() {
//            public void onClick(ClickEvent event) {
//                if (valueCallback != null) {
//                    dialogWindow.destroy();
//                    booleanCallback.execute(false);
//                }
//            }
//        });
//
//        dialogWindow.addCloseClickHandler(new CloseClickHandler() {
//
//            public void onCloseClick() {
//                if (valueCallback != null) {
//                    dialogWindow.destroy();
//                    booleanCallback.execute(null);
//                }
//            }
//        });
//
//        dialogWindow.showCentered(rootPanel);
//    }



}
