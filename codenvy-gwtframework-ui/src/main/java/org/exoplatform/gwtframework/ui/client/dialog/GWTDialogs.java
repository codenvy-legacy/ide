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
package org.exoplatform.gwtframework.ui.client.dialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;

import org.exoplatform.gwtframework.ui.client.WindowResource;
import org.exoplatform.gwtframework.ui.client.api.BooleanCallback;
import org.exoplatform.gwtframework.ui.client.api.ValueCallback;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.gwtframework.ui.client.window.CloseClickHandler;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GWTDialogs extends Dialogs {

    private Panel rootPanel;

    public GWTDialogs() {
    }

    public GWTDialogs(Panel rootPanel) {
        this.rootPanel = rootPanel;
    }

    public Panel getRootPanel() {
        return rootPanel;
    }

    public void setRootPanel(Panel rootPanel) {
        this.rootPanel = rootPanel;
    }

    private BooleanCallback booleanCallback = new BooleanCallback() {
        public void execute(Boolean value) {
            if (currentDialog.getBooleanValueReceivedHandler() != null) {
                try {
                    currentDialog.getBooleanValueReceivedHandler().booleanValueReceived(value);
                } catch (Throwable exc) {
                    exc.printStackTrace();
                }
            }

            showQueueDialog();
        }
    };

    private ValueCallback valueCallback = new ValueCallback() {
        public void execute(String value) {
            if (currentDialog.getStringValueReceivedHandler() != null) {
                try {
                    currentDialog.getStringValueReceivedHandler().stringValueReceived(value);
                } catch (Throwable exc) {
                    exc.printStackTrace();
                }
            }

            showQueueDialog();
        }
    };

    /**
     * @param name
     * @param title
     *         title near input field
     * @param width
     *         width
     * @param value
     *         value by default
     * @return {@link TextField}
     */
    public static TextField createTextField(String name, String title, int width, String value) {
        TextField textField = new TextField(name, title);
        textField.setTitleOrientation(TitleOrientation.TOP);
        textField.setHeight(22);
        textField.setWidth(width);
        textField.setValue(value);
        return textField;
    }

    /*
     * VALUE ASKING
     */
    @Override
    protected void openAskForValueDialog(Dialog dialog) {
        final TextField textField = createTextField("valueField", dialog.getMessage(), 350, dialog.getDefaultValue());
        final GWTDialogsWindow dialogWindow = new GWTDialogsWindow("exoAskForValueDialog", dialog.getTitle(), 400, 160, textField);
        dialogWindow.setModal(dialog.getModal());
        ImageButton okButton = createButton("Ok", null);
        ImageButton cancelButton = createButton("Cancel", null);
        dialogWindow.getButtonsLayout().add(okButton);
        dialogWindow.getButtonsLayout().add(cancelButton);

        okButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (valueCallback != null) {
                    dialogWindow.destroy();
                    valueCallback.execute(textField.getValue());
                }
            }
        });

        cancelButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (valueCallback != null) {
                    dialogWindow.destroy();
                    valueCallback.execute(null);
                }
            }
        });

        dialogWindow.addCloseClickHandler(new CloseClickHandler() {

            public void onCloseClick() {
                if (valueCallback != null) {
                    dialogWindow.destroy();
                    valueCallback.execute(null);
                }
            }
        });

        dialogWindow.showCentered(rootPanel);
    }

    ;

    @Override
    protected void openAskDialog(Dialog dialog) {
        HorizontalPanel content = createImageWithTextLayout(WindowResource.INSTANCE.askDialog(), dialog.getMessage());
        final GWTDialogsWindow dialogWindow = new GWTDialogsWindow("exoAskDialog", dialog.getTitle(), 400, 130, content);
        dialogWindow.setModal(dialog.getModal());
        ImageButton yesButton = createButton("Yes", null);
        ImageButton noButton = createButton("No", null);
        dialogWindow.getButtonsLayout().add(yesButton);
        dialogWindow.getButtonsLayout().add(noButton);

        yesButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (valueCallback != null) {
                    dialogWindow.destroy();
                    booleanCallback.execute(true);
                }
            }
        });

        noButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (valueCallback != null) {
                    dialogWindow.destroy();
                    booleanCallback.execute(false);
                }
            }
        });

        dialogWindow.addCloseClickHandler(new CloseClickHandler() {

            public void onCloseClick() {
                if (valueCallback != null) {
                    dialogWindow.destroy();
                    booleanCallback.execute(null);
                }
            }
        });

        dialogWindow.showCentered(rootPanel);
    }

    ;

    @Override
    protected void openWarningDialog(Dialog dialog) {
        HorizontalPanel content = createImageWithTextLayout(WindowResource.INSTANCE.warnDialog(), dialog.getMessage());
        final GWTDialogsWindow dialogWindow = new GWTDialogsWindow("exoWarningDialog", dialog.getTitle(), 400, 130, content);
        dialogWindow.setModal(dialog.getModal());
        ImageButton okButton = createButton("Ok", null);
        dialogWindow.getButtonsLayout().add(okButton);

        okButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (valueCallback != null) {
                    dialogWindow.destroy();
                    booleanCallback.execute(true);
                }
            }
        });

        dialogWindow.addCloseClickHandler(new CloseClickHandler() {

            public void onCloseClick() {
                if (valueCallback != null) {
                    dialogWindow.destroy();
                    booleanCallback.execute(null);
                }
            }
        });

        dialogWindow.showCentered(rootPanel);
    }

    ;

    @Override
    protected void openInfoDialog(Dialog dialog) {
        HorizontalPanel content = createImageWithTextLayout(WindowResource.INSTANCE.sayDialog(), dialog.getMessage());
        final GWTDialogsWindow dialogWindow = new GWTDialogsWindow("exoInfoDialog", dialog.getTitle(), 400, 130, content);
        dialogWindow.setModal(dialog.getModal());
        ImageButton okButton = createButton("Ok", null);
        dialogWindow.getButtonsLayout().add(okButton);

        okButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (valueCallback != null) {
                    dialogWindow.destroy();
                    booleanCallback.execute(true);
                }
            }
        });

        dialogWindow.addCloseClickHandler(new CloseClickHandler() {
            public void onCloseClick() {
                if (valueCallback != null) {
                    dialogWindow.destroy();
                    booleanCallback.execute(null);
                }
            }
        });

        dialogWindow.showCentered(rootPanel);
    }

    ;

    /**
     * Create button.
     *
     * @param title
     *         button's title
     * @param icon
     *         button's image
     * @return {@link IButton}
     */
    public ImageButton createButton(String title, ImageResource icon) {
        ImageButton button = new DialogsImageButton(title);
        if (icon != null)
            button.setImage(new Image(icon));
        return button;
    }

    private class DialogsImageButton extends ImageButton {

        public DialogsImageButton(String text) {
            super(text);
        }

        @Override
        protected void onAttach() {
            setButtonId(getParent().getElement().getId() + getText() + "Button");
            super.onAttach();
        }

    }


    /**
     * Creates layout with pointed image and text near it.
     *
     * @param icon
     *         image to display
     * @param text
     *         text to display
     * @return {@link HorizontalPanel}
     */
    public static HorizontalPanel createImageWithTextLayout(ImageResource icon, String text) {
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.setWidth("100%");
        hPanel.setHeight(32 + "px");
        hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        hPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

        // Add image
        Image image = new Image(icon);
        image.setWidth(32 + "px");
        image.setHeight(32 + "px");
        hPanel.add(image);
        hPanel.setCellWidth(image, "42px");

        // Add text:
        Label label = new Label();
        label.getElement().setInnerHTML(text);
        hPanel.add(label);

        return hPanel;
    }

}
