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

import com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.ButtonStyleChangedHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextAreaInput;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 * Factory dialog view, also is implementation of CreateFactoryPresenter's display.
 * 
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
        
        // File Upload field
        
        String fileUploadSelected();
        
    }

    @UiField
    Style style;    

    public static final String    ID          = "ide.factory.pupop";

    public static final String    TITLE       = "Create and Publish a Factory";
    
    public static final int       INITIAL_WIDTH         = 800;
    
    public static final int       INITIAL_HEIGHT        = 340 + 30;

    public static final int       WIDTH                 = 788;

    public static final int       HEIGHT                = 550 + 30;

    @UiField
    DivElement                    wizard1, wizard2;
    
    @UiField
    IFrameElement                 previewFrame1, previewFrame2;

    @UiField
    InputElement                  checkShowCounter, radioVertical, radioHorizontal, radioDark, radioWhite, openAfterLaunchField, checkKeepGitInfo;

    @UiField    LabelElement        uploadFilePanel;
    
    @UiField    InputElement        uploadFileField;
    
    @UiField    SpanElement         selectedFileNameSpan;
    
    @UiField    DivElement          resetUploadButton;
    
    @UiField
    ImageButton                   buttonCancel, buttonCreate, buttonBack, buttonFinish;
    
    @UiField
    TextAreaInput                 snippetWebsites, snippetGitHub, snippetDirectSharing;
    
    @UiField
    DivElement                    advancedOptionsControl, advancedOptions;
    
    @UiField
    FormElement                   createFactoryForm;
    
    @UiField
    TextAreaElement               descriptionField;
    
    @UiField
    InputElement                  emailField, authorField, companyIdField, affiliateIdField;
    
    @UiField
    InputElement                  factoryUrlContent;
    
    @UiField
    Image                         shareFacebookButton;

    @UiField
    Image                         shareGooglePlusButton;

    @UiField
    Image                         shareTwitterButton;

    @UiField
    Image                         shareEmailButton;
    
    @UiField
    IFrameElement                 createFactoryIFrame;

    private int                   currentPage = 0;

    private ButtonStyleChangedHandler refreshCallback;
    
    private ValueChangeHandler<String> uploadLogoValueChangeHandler;
    
    private AsyncCallback<Boolean> validateImageCallback;
        
    private boolean advancedOptionsVisible = false;
    
    private int previousHeight = INITIAL_HEIGHT;
    
    private int animationLeft;
    
    /**
     * 
     */
    private AsyncCallback<String> createFactoryCallback;    

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
        
        handleFileUploadEvents();
        
        snippetWebsites.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                snippetWebsites.selectAll();
            }
        });

        snippetGitHub.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                snippetGitHub.selectAll();
            }
        });
        
        snippetDirectSharing.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                snippetDirectSharing.selectAll();
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
    
    /**
     *
     */
    public class UploadFieldChangeEvent extends ValueChangeEvent<String> {
        public UploadFieldChangeEvent(String value) {
            super(value);
        }
    };
    
    public void cancelUploadFile() {
        checkUploadFile("");
    }
    
    private void checkUploadFile(String value) {
        if (value == null || value.trim().isEmpty()) {
            createFactoryForm.reset();
            
            uploadFilePanel.removeClassName(style.fileUploadSelected());
            uploadFileField.removeAttribute("disabled");
            
            selectedFileNameSpan.setInnerText("Upload image (JPG, GIF or PNG)");
            
            resetUploadButton.getStyle().setDisplay(Display.NONE);        
        } else {
            uploadFilePanel.addClassName(style.fileUploadSelected());
            uploadFileField.setAttribute("disabled", "disabled");
            
            String name = value;
            if (name.indexOf("/") >= 0) {
                name = name.substring(name.lastIndexOf("/") + 1);
            } else {
                name = name.substring(name.lastIndexOf("\\") + 1);
            }
            if (name.length() > 22) {
                name = name.substring(0, 22) + "...";
            }
            
            selectedFileNameSpan.setInnerText(name);
            
            resetUploadButton.getStyle().setDisplay(Display.BLOCK);
        }
        
        if (uploadLogoValueChangeHandler != null) {
            uploadLogoValueChangeHandler.onValueChange(new UploadFieldChangeEvent(value));
        }        
    }
    
    private void handleFileUploadEvents() {
        DOM.sinkEvents((com.google.gwt.user.client.Element)uploadFileField.cast(), Event.ONCHANGE);        
        DOM.setEventListener((com.google.gwt.user.client.Element)uploadFileField.cast(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if (!checkFileType()) {
                    Dialogs.getInstance().showError("IDE", "Selected file is not a image.");
                    checkUploadFile("");
                    return;
                }

                if (!checkFileSize()) {
                    Dialogs.getInstance().showError("IDE", "Selected file size is bigger 1MB.");
                    checkUploadFile("");
                    return;
                }
                
                checkUploadFile(uploadFileField.getValue());                    
            }
        });
        
        DOM.sinkEvents((com.google.gwt.user.client.Element)resetUploadButton.cast(), Event.ONCLICK);  
        DOM.setEventListener((com.google.gwt.user.client.Element)resetUploadButton.cast(), new EventListener() {            
            @Override
            public void onBrowserEvent(Event event) {
                checkUploadFile("");
            }
        });
    }
    
    private native boolean checkFileType() /*-{
        var instance = this;
        var upload = instance.@com.codenvy.ide.factory.client.factory.CreateFactoryView::uploadFileField;
        var file = upload.files[0];
                
        var mask = "image/";
                
        if (!file.type.match("image/")) {
            return false;
        }
        
        return true;
    }-*/;
    
    private native boolean checkFileSize() /*-{
        var instance = this;
        var upload = instance.@com.codenvy.ide.factory.client.factory.CreateFactoryView::uploadFileField;
        var file = upload.files[0];
            
        if (file.size > (1024*1024)) {
            return false;
        }
    
        return true;
    }-*/;

    /**
     * Logo image becomes available only after rendering of iframe content.
     */
    private void loadLogoImage() {
        new Timer() {
            @Override
            public void run() {
                loadLogoImageData(previewFrame1);
                loadLogoImageData(previewFrame2);
            }
        }.schedule(100);
    }
    
    private native void loadLogoImageData(IFrameElement iframe) /*-{
        var instance = this;
    
        try {
            var frame = iframe;
            frame = (frame.contentWindow) ? frame.contentWindow : (frame.contentDocument.document) ? frame.contentDocument.document : frame.contentDocument;
            
            var factory = frame.factory;
            if (factory == undefined || factory == null) {
                return;
            }

            if ("advanced" == factory.getType()) {
                var image = factory.getLogoImage();
                
                var upload = instance.@com.codenvy.ide.factory.client.factory.CreateFactoryView::uploadFileField;
                var file = upload.files[0];
                
                var mask = "image/";
                
                if (!file.type.match("image/")) {
                    return;
                }
                
                image.file = file;

                var reader = new FileReader();
                reader.onload = (function(aImg) {
                    return function(e) {
                        aImg.src = e.target.result;
                    };
                })(image);
                
                reader.readAsDataURL(file);
            }
        } catch (e) {
            $wnd.alert("ERROR: " + e.message);
        }
    }-*/;

    private void addChangeListener(InputElement element) {
        DOM.sinkEvents((com.google.gwt.user.client.Element)element.cast(), Event.ONCHANGE);        
        DOM.setEventListener((com.google.gwt.user.client.Element)element.cast(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if (refreshCallback != null) {
                    refreshCallback.onButtonStyleChanged();
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
            setPopupHeight(INITIAL_HEIGHT);
            if (previousHeight != INITIAL_HEIGHT) {
                shiftVertical((HEIGHT - INITIAL_HEIGHT) / 2);
                previousHeight = INITIAL_HEIGHT;
            }
        } else {
            setPopupHeight(HEIGHT);
            if (previousHeight != HEIGHT) {
                shiftVertical((INITIAL_HEIGHT - HEIGHT) / 2);
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
    public void setButtonStyleChangedHandler(ButtonStyleChangedHandler callback) {
        this.refreshCallback = callback;
    }

    @Override
    public native void previewFactoryButton(String content) /*-{
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
        
        this.@com.codenvy.ide.factory.client.factory.CreateFactoryView::loadLogoImage()();
    }-*/;

    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#showCounterChecked()
     */
    @Override
    public boolean showCounterChecked() {
        return checkShowCounter.isChecked();
    }

    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#verticalOrientationSelected()
     */
    @Override
    public boolean verticalOrientationSelected() {
        return radioVertical.isChecked();
    }
    
    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#horizontalOrientationSelected()
     */
    @Override
    public boolean horizontalOrientationSelected() {
        return radioHorizontal.isChecked();
    }

    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#whiteStyleSelected()
     */
    @Override
    public boolean whiteStyleSelected() {
        return radioWhite.isChecked();
    }
    
    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#darkStyleSelected()
     */
    @Override
    public boolean darkStyleSelected() {
        return radioDark.isChecked();
    }

    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#websitesSnippet()
     */
    @Override
    public HasValue<String> websitesSnippet() {
        return snippetWebsites;
    }

    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#gitHubSnippet()
     */
    @Override
    public HasValue<String> gitHubSnippet() {
        return snippetGitHub;
    }

    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#directSharingSnippet()
     */
    @Override
    public HasValue<String> directSharingSnippet() {
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
    public void setOpenAfterLaunchFieldClickHandler(final ClickHandler clickHandler) {
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

    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#setOpenAfterLaunchFieldValue(java.lang.String)
     */
    @Override
    public void setOpenAfterLaunchFieldValue(String value) {
        openAfterLaunchField.setValue(value);
    }
    
    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#setUploadImageFieldValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public void setUploadImageFieldValueChangeHandler(ValueChangeHandler<String> handler) {
        this.uploadLogoValueChangeHandler = handler;
    }

    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#getUploadImageFieldValue()
     */
    @Override
    public String getUploadImageFieldValue() {
        return uploadFileField.getValue();
    }

    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#enableDefaultStyleOptions(boolean)
     */
    @Override
    public void enableDefaultStyleOptions(boolean enabled) {
        if (enabled) {
            radioVertical.setAttribute("disabled", "disabled");
            radioHorizontal.setAttribute("disabled", "disabled");
            radioWhite.setAttribute("disabled", "disabled");
            radioDark.setAttribute("disabled", "disabled");
        } else {
            radioVertical.removeAttribute("disabled");
            radioHorizontal.removeAttribute("disabled");
            radioWhite.removeAttribute("disabled");
            radioDark.removeAttribute("disabled");
        }
    }
    
    @Override
    public void setCreateFactoryRequestContent(String content) {
        factoryUrlContent.setValue(content);
    }
        
    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#createFactory(com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public native void createFactory(final AsyncCallback<String> callback) /*-{
        var instance = this;
        instance.@com.codenvy.ide.factory.client.factory.CreateFactoryView::createFactoryCallback = callback;
    
        var ifr = instance.@com.codenvy.ide.factory.client.factory.CreateFactoryView::createFactoryIFrame;
        
        ifr.onload = function() {          
            ifr = (ifr.contentWindow) ? ifr.contentWindow : (ifr.contentDocument.document) ? ifr.contentDocument.document : ifr.contentDocument;
            instance.@com.codenvy.ide.factory.client.factory.CreateFactoryView::factoryCreationResultReceived(Ljava/lang/String;)(ifr.document.body.innerText);
        };
        
        instance.@com.codenvy.ide.factory.client.factory.CreateFactoryView::submitForm()();
    }-*/;
    
    private boolean uploadDisabled;
    
    /**
     * 
     */
    private void submitForm() {
        if (uploadFileField.hasAttribute("disabled")) {
            uploadFileField.removeAttribute("disabled");
            uploadDisabled = true;
        } else {
            uploadDisabled = false;
        }
        
        createFactoryForm.setMethod("post");
        createFactoryForm.setEnctype("multipart/form-data");
        createFactoryForm.setAction("/api/factory");
        createFactoryForm.setTarget("createFactoryIFrame");
        createFactoryForm.submit();
    }
    
    /**
     * @param response
     */
    private void factoryCreationResultReceived(String response) {
        if (uploadDisabled) {
            uploadFileField.setAttribute("disabled", "disabled");
        }
        
        if (createFactoryCallback != null) {
            createFactoryCallback.onSuccess(response);
        }
    }

    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#getDescriptionFieldValue()
     */
    @Override
    public String getDescriptionFieldValue() {
        return descriptionField.getValue();
    }

    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#getEmailFieldValue()
     */
    @Override
    public String getEmailFieldValue() {
        return emailField.getValue();
    }

    @Override
    public void setEmailFieldValue(String email) {
        emailField.setValue(email);
    }

    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#getAuthorFieldValue()
     */
    @Override
    public String getAuthorFieldValue() {
        return authorField.getValue();
    }
    
    @Override
    public void setAuthorFieldValue(String author) {
        authorField.setValue(author);
    }

    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#getOpenAfterLaunchFieldValue()
     */
    @Override
    public String getOpenAfterLaunchFieldValue() {
        return openAfterLaunchField.getValue();
    }

    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#getOrganizationIdFieldValue()
     */
    @Override
    public String getOrganizationIdFieldValue() {
        return companyIdField.getValue();
    }

    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#getAffiliateIdFieldValue()
     */
    @Override
    public String getAffiliateIdFieldValue() {
        return affiliateIdField.getValue();
    }
    
    /**
     * @see com.codenvy.ide.factory.client.factory.CreateFactoryPresenter.Display#keepGitInfoSelected()
     */
    @Override
    public boolean keepGitInfoSelected() {
        return checkKeepGitInfo.isChecked();
    }

}
