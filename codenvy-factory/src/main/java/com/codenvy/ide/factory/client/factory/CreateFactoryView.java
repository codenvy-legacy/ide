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
package com.codenvy.ide.factory.client.factory;

import com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.StyleChangedHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextAreaInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class CreateFactoryView extends ViewImpl
    implements com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display {

    private static CreateFactoryViewUiBinder uiBinder = GWT.create(CreateFactoryViewUiBinder.class);

    interface CreateFactoryViewUiBinder extends UiBinder<Widget, CreateFactoryView> {
    }
    
    interface Style extends CssResource {
        
        String blockHeader();
        
        String blockHeaderExpanded();
        
        String blockHeaderCollapsed();
        
    }

    @UiField
    Style style;    

    public static final String    ID          = "ide.factory.pupop";

    public static final String    TITLE       = "Create and Publish a Factory";
    
    public static final int       INITIAL_WIDTH       = 800;
    
    public static final int       INITIAL_HEIGHT      = 270;

    public static final int       WIDTH       = 788;

    public static final int       HEIGHT      = 480;

    public static final int       HEIGHT_MINI = 270;

    @UiField
    DivElement                    wizard1, wizard2;
    
    @UiField
    IFrameElement                 previewFrame1, previewFrame2;

    @UiField
    InputElement                  checkShowCounter, radioVertical, radioHorizontal, radioDark, radioWhite, openAfterLaunchField;

    @UiField
    ImageButton                   buttonCancel, buttonCreate, buttonBack, buttonFinish;
    
    @UiField
    TextAreaInput                 snippetWebsites, snippetGitHub, snippetDirectSharing;
    
    @UiField
    DivElement                    advancedOptionsControl, advancedOptions;
    
    @UiField
    Image         shareFacebookButton;

    @UiField
    Image         shareGooglePlusButton;

    @UiField
    Image         shareTwitterButton;

    @UiField
    Image         shareEmailButton;    

    private int                   currentPage = 0;

    private StyleChangedHandler refreshCallback;
    
    private boolean advancedOptionsVisible = false;
    
    private int previousHeight = HEIGHT_MINI;
    
    private int animationLeft;

    public CreateFactoryView() {
        super(ID, ViewType.MODAL, TITLE, null, INITIAL_WIDTH, INITIAL_HEIGHT, false);
        add(uiBinder.createAndBindUi(this));

        checkShowCounter.setId("checkShowCounter");
        radioVertical.setId("radioVertical");
        radioHorizontal.setId("radioHorizontal");
        radioDark.setId("radioDark");
        radioWhite.setId("radioWhite");

        // sinking events
        addChangeListener(checkShowCounter);
        addChangeListener(radioVertical);
        addChangeListener(radioHorizontal);
        addChangeListener(radioDark);
        addChangeListener(radioWhite);
        
        snippetWebsites.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                snippetWebsites.selectAll();
            }
        });
        
        addClickHandler(advancedOptionsControl, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showHideAdvancedOptions();
            }
        });
        
        advancedOptions.getStyle().setDisplay(Display.NONE);
        advancedOptionsControl.setClassName(style.blockHeader() + " " + style.blockHeaderCollapsed());
    }
    
    private void addChangeListener(InputElement element) {
        DOM.sinkEvents((com.google.gwt.user.client.Element)element.cast(), Event.ONCHANGE);        
        DOM.setEventListener((com.google.gwt.user.client.Element)element.cast(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if (refreshCallback != null) {
                    refreshCallback.onRefresh();
                }
            }
        });
    }
    
    private void addClickHandler(DivElement element, final ClickHandler clickHandler) {
        DOM.sinkEvents((com.google.gwt.user.client.Element)element.cast(), Event.ONCLICK);        
        DOM.setEventListener((com.google.gwt.user.client.Element)element.cast(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if (clickHandler != null) {
                    clickHandler.onClick(null);
                }
            }
        });
    }
    
    private void showAdvancedOptions() {
        advancedOptions.getStyle().clearDisplay();
        advancedOptionsControl.setClassName(style.blockHeader() + " " + style.blockHeaderExpanded());
        advancedOptionsVisible = true;
        updateDimensions();
    }
    
    private void hideAdvancedOptions() {
        advancedOptions.getStyle().setDisplay(Display.NONE);
        advancedOptionsControl.setClassName(style.blockHeader() + " " + style.blockHeaderCollapsed());
        advancedOptionsVisible = false;
        updateDimensions();
    }
    
    private void showHideAdvancedOptions() {
        if ("none".equalsIgnoreCase(advancedOptions.getStyle().getDisplay())) {
            showAdvancedOptions();
        } else {
            hideAdvancedOptions();
        }
    }
    
    private void updateDimensions() {
        setPopupWidth(WIDTH);
        
        if (currentPage == 0 && !advancedOptionsVisible) {
            setPopupHeight(HEIGHT_MINI);
            if (previousHeight != HEIGHT_MINI) {
                shiftVertical((HEIGHT - HEIGHT_MINI) / 2);
                previousHeight = HEIGHT_MINI;
            }
        } else {
            setPopupHeight(HEIGHT);
            if (previousHeight != HEIGHT) {
                shiftVertical((HEIGHT_MINI - HEIGHT) / 2);
                previousHeight = HEIGHT;                
            }
        }
    }
        
    @Override
    public void nextPage() {
        if (currentPage == 1) {
            return;
        }

        currentPage++;
        updateDimensions();
        animationLeft = 0;
        disableButtons();

        new Timer() {
            @Override
            public void run() {
                animationLeft++;
                wizard1.getStyle().setProperty("left", "-" + (animationLeft * 10) + "%");
                wizard2.getStyle().setProperty("left", "" + (100 - animationLeft * 10) + "%");
                if (animationLeft == 10) {
                    cancel();
                    pageChanged();
                }
            }
        }.scheduleRepeating(25);
    }

    @Override
    public void previousPage() {
        if (currentPage == 0) {
            return;
        }

        animationLeft = 10;
        disableButtons();

        new Timer() {
            @Override
            public void run() {
                animationLeft--;
                wizard2.getStyle().setProperty("left", "" + (100 - animationLeft * 10) + "%");
                wizard1.getStyle().setProperty("left", "-" + (animationLeft * 10) + "%");
                if (animationLeft == 0) {
                    cancel();
                    
                    currentPage--;
                    updateDimensions();
                    
                    pageChanged();
                }
            }
        }.scheduleRepeating(25);
    }

    private void disableButtons() {
        buttonCancel.setEnabled(false);
        buttonCreate.setEnabled(false);
        buttonBack.setEnabled(false);
        buttonFinish.setEnabled(false);        
        
    }
    
    private void pageChanged() {
        buttonCancel.setEnabled(true);
        buttonCreate.setEnabled(true);
        buttonBack.setEnabled(true);
        buttonFinish.setEnabled(true);        
        
        switch (currentPage) {
            case 0:
                buttonCancel.setVisible(true);
                buttonCreate.setVisible(true);
                buttonBack.setVisible(false);
                buttonFinish.setVisible(false);
                break;

            case 1:
                buttonCancel.setVisible(false);
                buttonCreate.setVisible(false);
                buttonBack.setVisible(true);
                buttonFinish.setVisible(true);
                break;
        }
    }

    @Override
    public HasClickHandlers getCancelButton() {
        return buttonCancel;
    }

    @Override
    public HasClickHandlers getCreateButton() {
        return buttonCreate;
    }

    @Override
    public int getPageIndex() {
        return currentPage;
    }

    @Override
    public HasClickHandlers getBackButton() {
        return buttonBack;
    }

    @Override
    public HasClickHandlers getFinishButton() {
        return buttonFinish;
    }

    @Override
    public void addStyleChangedHandler(StyleChangedHandler callback) {
        this.refreshCallback = callback;
    }

    @Override
    public native void setPreviewContent(String content) /*-{
        var frame1 = this.@com.codenvy.ide.factory.client.factory.CreateFactoryView::previewFrame1;
        frame1 = (frame1.contentWindow) ? frame1.contentWindow : (frame1.contentDocument.document) ? frame1.contentDocument.document : frame1.contentDocument;
        frame1.document.open();
        frame1.document.write(content);
        frame1.document.close();
        
        var frame2 = this.@com.codenvy.ide.factory.client.factory.CreateFactoryView::previewFrame2;
        frame2 = (frame2.contentWindow) ? frame2.contentWindow : (frame2.contentDocument.document) ? frame2.contentDocument.document : frame2.contentDocument;
        frame2.document.open();
        frame2.document.write(content);
        frame2.document.close();         
                 
    }-*/;

    @Override
    public boolean showCounter() {
        return checkShowCounter.isChecked();
    }

    @Override
    public boolean isVerticalOrientation() {
        return radioVertical.isChecked();
    }

    @Override
    public boolean isWhiteStyle() {
        return radioWhite.isChecked();
    }

    @Override
    public HasValue<String> snippetWebsites() {
        return snippetWebsites;
    }

    @Override
    public HasValue<String> snippetGitHub() {
        return snippetGitHub;
    }

    @Override
    public HasValue<String> snippetDirectSharing() {
        return snippetDirectSharing;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter.Display#getShareFacebookButton()
     */
    @Override
    public HasClickHandlers getShareFacebookButton() {
        return shareFacebookButton;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter.Display#getShareGooglePlusButton()
     */
    @Override
    public HasClickHandlers getShareGooglePlusButton() {
        return shareGooglePlusButton;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter.Display#getShareTwitterButton()
     */
    @Override
    public HasClickHandlers getShareTwitterButton() {
        return shareTwitterButton;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter.Display#getShareEmailButton()
     */
    @Override
    public HasClickHandlers getShareEmailButton() {
        return shareEmailButton;
    }

    @Override
    public void addOpenAfterLaunchClickHandler(final ClickHandler clickHandler) {
        DOM.sinkEvents((com.google.gwt.user.client.Element)openAfterLaunchField.cast(), Event.ONCLICK);        
        DOM.setEventListener((com.google.gwt.user.client.Element)openAfterLaunchField.cast(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if (clickHandler != null) {
                    clickHandler.onClick(null);
                }
            }
        });
    }

    @Override
    public void setOpenAfterLaunch(String path) {
        openAfterLaunchField.setValue(path);
    }
    
}
