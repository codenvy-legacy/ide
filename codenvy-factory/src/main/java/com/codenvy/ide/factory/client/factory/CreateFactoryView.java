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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
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

    public static final String    ID          = "ide.factory.pupop";

    public static final String    TITLE       = "Create and Publish a Factory";

    public static final int       WIDTH       = 800;

    public static final int       HEIGHT      = 480;

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
    Image         shareFacebookButton;

    @UiField
    Image         shareGooglePlusButton;

    @UiField
    Image         shareTwitterButton;

    @UiField
    Image         shareEmailButton;    

    private int                   currentPage = 0;

    private StyleChangedHandler refreshCallback;

    public CreateFactoryView() {
        super(ID, ViewType.MODAL, TITLE, null, WIDTH, HEIGHT, false);
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


    int                   tmpLeft;

    @Override
    public void nextPage() {
        if (currentPage == 1) {
            return;
        }

        currentPage++;
        tmpLeft = 0;
        disableButtons();

        new Timer() {
            @Override
            public void run() {
                tmpLeft++;
                wizard1.getStyle().setProperty("left", "-" + (tmpLeft * 10) + "%");
                wizard2.getStyle().setProperty("left", "" + (100 - tmpLeft * 10) + "%");
                if (tmpLeft == 10) {
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

        currentPage--;
        tmpLeft = 10;
        disableButtons();

        new Timer() {
            @Override
            public void run() {
                tmpLeft--;
                wizard2.getStyle().setProperty("left", "" + (100 - tmpLeft * 10) + "%");
                wizard1.getStyle().setProperty("left", "-" + (tmpLeft * 10) + "%");
                if (tmpLeft == 0) {
                    cancel();
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
