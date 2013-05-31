/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.dialogs;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.ui.client.WindowResource;
import org.exoplatform.gwtframework.ui.client.api.BooleanCallback;
import org.exoplatform.gwtframework.ui.client.api.ValueCallback;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.gwtframework.ui.client.dialog.Dialog;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IDEDialogs extends Dialogs implements ViewClosedHandler {

    private DialogClosedHandler dialogClosedHandler;

    public IDEDialogs() {
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    private abstract class DialogClosedHandler implements ViewClosedHandler {

        private String id;

        public DialogClosedHandler(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

    }

    private BooleanCallback booleanCallback = new BooleanCallback() {
                                                public void execute(Boolean value) {
                                                    if (currentDialog.getBooleanValueReceivedHandler() != null) {
                                                        try {
                                                            currentDialog.getBooleanValueReceivedHandler().booleanValueReceived(value);
                                                        } catch (Throwable exc) {
                                                        }
                                                    }

                                                    showQueueDialog();
                                                }
                                            };

    private ValueCallback   valueCallback   = new ValueCallback() {
                                                public void execute(String value) {
                                                    if (currentDialog.getStringValueReceivedHandler() != null) {
                                                        try {
                                                            currentDialog.getStringValueReceivedHandler().stringValueReceived(value);
                                                        } catch (Throwable exc) {
                                                        }
                                                    }

                                                    showQueueDialog();
                                                }
                                            };

    /*
     * VALUE ASKING
     */
    @Override
    protected void openAskForValueDialog(Dialog dialog) {
        FlowPanel panel = new FlowPanel();
        panel.getElement().getStyle().setMarginLeft(-5, Unit.PX);

        final Element nobr = Document.get().createElement("nobr");
        panel.getElement().appendChild(nobr);

        final Element span = Document.get().createSpanElement();
        span.getStyle().setFloat(Float.LEFT);
        span.getStyle().setProperty("fontFamily", "Verdana,Bitstream Vera Sans,sans-serif");
        span.getStyle().setFontSize(11, Unit.PX);
        span.getStyle().setHeight(14, Unit.PX);
        span.getStyle().setMarginBottom(6, Unit.PX);
        span.getStyle().setMarginLeft(4, Unit.PX);
        span.getStyle().setTextAlign(TextAlign.LEFT);
        span.getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
        span.getStyle().setWidth(350, Unit.PX);
        span.setInnerHTML(dialog.getMessage());
        nobr.appendChild(span);

        final TextInput textInput = new TextInput();
        textInput.setName("valueField");
        textInput.setHeight("22px");
        textInput.setWidth("350px");
        textInput.setValue(dialog.getDefaultValue());
        textInput.getElement().getStyle().setMarginLeft(-5.0, Style.Unit.PX);

        panel.add(textInput);

        final IDEDialogsView view =
                                    new IDEDialogsView("codenvyAskForValueModalView", dialog.getTitle(), 400, 160, panel, dialog.getModal());
        ImageButton okButton = createButton("Ok", null);
        view.getButtonsLayout().add(okButton);
        okButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogClosedHandler = null;
                IDE.getInstance().closeView(view.getId());
                valueCallback.execute(textInput.getValue());
            }
        });

        textInput.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    dialogClosedHandler = null;
                    IDE.getInstance().closeView(view.getId());
                    valueCallback.execute(textInput.getValue());
                }
            }
        });

        ImageButton cancelButton = createButton("Cancel", null);
        view.getButtonsLayout().add(cancelButton);
        cancelButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogClosedHandler = null;
                IDE.getInstance().closeView(view.getId());
                valueCallback.execute(null);
            }
        });

        dialogClosedHandler = new DialogClosedHandler("codenvyAskForValueModalView") {
            @Override
            public void onViewClosed(ViewClosedEvent event) {
                dialogClosedHandler = null;
                valueCallback.execute(null);
            }
        };

        IDE.getInstance().openView(view);

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                textInput.focus();
            }
        });
    }

    @Override
    protected void openAskDialog(Dialog dialog) {
        HorizontalPanel content = createImageWithTextLayout(WindowResource.INSTANCE.askDialog(), dialog.getMessage());
        final IDEDialogsView view = new IDEDialogsView("ideAskModalView", dialog.getTitle(), 430, 150, content, dialog.getModal());
        ImageButton yesButton = createButton("Yes", null);
        view.getButtonsLayout().add(yesButton);
        yesButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogClosedHandler = null;
                IDE.getInstance().closeView(view.getId());
                booleanCallback.execute(true);
            }
        });

        ImageButton noButton = createButton("No", null);
        view.getButtonsLayout().add(noButton);
        noButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (valueCallback != null) {
                    dialogClosedHandler = null;
                    IDE.getInstance().closeView(view.getId());
                    booleanCallback.execute(false);
                }
            }
        });

        dialogClosedHandler = new DialogClosedHandler("ideAskModalView") {
            @Override
            public void onViewClosed(ViewClosedEvent event) {
                dialogClosedHandler = null;
                booleanCallback.execute(null);
            }
        };

        IDE.getInstance().openView(view);
    }

    @Override
    protected void openWarningDialog(Dialog dialog) {
        HorizontalPanel content = createImageWithTextLayout(WindowResource.INSTANCE.warnDialog(), dialog.getMessage());
        final IDEDialogsView view = new IDEDialogsView("ideWarningModalView", dialog.getTitle(), 450, 250, content, dialog.getModal());

        ImageButton okButton = createButton("Ok", null);
        view.getButtonsLayout().add(okButton);
        okButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogClosedHandler = null;
                IDE.getInstance().closeView(view.getId());
                booleanCallback.execute(true);
            }
        });

        dialogClosedHandler = new DialogClosedHandler("ideWarningModalView") {
            @Override
            public void onViewClosed(ViewClosedEvent event) {
                dialogClosedHandler = null;
                booleanCallback.execute(null);
            }
        };

        IDE.getInstance().openView(view);
    }

    @Override
    protected void openInfoDialog(Dialog dialog) {
        HorizontalPanel content = createImageWithTextLayout(WindowResource.INSTANCE.sayDialog(), dialog.getMessage());
        final IDEDialogsView view = new IDEDialogsView("ideInformationModalView", dialog.getTitle(), 400, 130, content);

        ImageButton okButton = createButton("Ok", null);
        view.getButtonsLayout().add(okButton);
        okButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogClosedHandler = null;
                IDE.getInstance().closeView(view.getId());
                booleanCallback.execute(true);
            }
        });

        dialogClosedHandler = new DialogClosedHandler("ideInformationModalView") {
            @Override
            public void onViewClosed(ViewClosedEvent event) {
                dialogClosedHandler = null;
                booleanCallback.execute(null);
            }
        };

        IDE.getInstance().openView(view);
    }

    /**
     * Create button.
     * 
     * @param title button's title
     * @param icon button's image
     * @return {@link IButton}
     */
    public ImageButton createButton(String title, ImageResource icon) {
        ImageButton button = new DialogsImageButton(title);
        if (icon != null) {
            button.setImage(new Image(icon));
        }
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
     * @param icon image to display
     * @param text text to display
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

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (dialogClosedHandler != null && dialogClosedHandler.getId().equals(event.getView().getId())) {
            dialogClosedHandler.onViewClosed(event);
        }
    }

}
