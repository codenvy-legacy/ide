/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.factory.client.share;

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.factory.client.FactoryLocalizationConstant;
import com.codenvy.ide.factory.client.FactoryResources;
import com.codenvy.ide.navigation.NavigateToFileViewImpl;
import com.codenvy.ide.ui.switcher.Switcher;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.vectomatic.dom.svg.ui.SVGImage;

import java.util.ArrayList;
import java.util.List;

/**
 * UI for {@link ShareFactoryView}.
 * 
 * @author Ann Shumilova
 */
public class ShareFactoryViewImpl extends BaseView<ShareFactoryView.ActionDelegate> implements ShareFactoryView {
    private static final String ENCODED_URL_ID     = "encoded-url";
    private static final String NON_ENCODED_URL_ID = "non-encoded-url";

    interface ShareFactoryViewImplUiBinder extends UiBinder<Widget, ShareFactoryViewImpl> {
    }

    @UiField
    Button                      nonEncodedHtmlButton;
    @UiField
    Button                      nonEncodedGitHubButton;
    @UiField
    Button                      nonEncodediFrameButton;
    @UiField
    ToggleButton                nonEncodedSocialButton;
    @UiField
    Button                      encodedHtmlButton;
    @UiField
    Button                      encodedGitHubButton;
    @UiField
    Button                      encodediFrameButton;
    @UiField
    ToggleButton                encodedSocialButton;
    @UiField
    Button                      generateEncodedUrlButton;

    @UiField
    Hyperlink                   nonencodedUrl;
    @UiField
    Hyperlink                   encodedUrl;
    @UiField
    Label                       encodedLabel;
    @UiField
    FlowPanel                   encodedButtons;
    @UiField
    HorizontalPanel             encodedPanel;


    @UiField
    SVGImage                    nonEncodedUrlCopy;
    @UiField
    SVGImage                    facebookNonEncoded;
    @UiField
    SVGImage                    twitterNonEncoded;
    @UiField
    SVGImage                    googlePlusNonEncoded;
    @UiField
    SVGImage                    emailNonEncoded;
    @UiField
    FlowPanel                   socialNonEncoded;
    @UiField
    SVGImage                    encodedUrlCopy;
    @UiField
    SVGImage                    facebookEncoded;
    @UiField
    SVGImage                    twitterEncoded;
    @UiField
    SVGImage                    googlePlusEncoded;
    @UiField
    SVGImage                    emailEncoded;
    @UiField
    FlowPanel                   socialEncoded;

    @UiField
    TextBox                     authorField;
    @UiField(provided = true)
    SuggestBox                  openFileField;
    @UiField
    TextBox                     expirationDateField;
    @UiField
    TextArea                    descriptionField;
    @UiField
    TextArea                    findAndReplaceField;

    @UiField
    RadioButton                 verticalAlignField;
    @UiField
    RadioButton                 horizontalAlignField;
    @UiField
    RadioButton                 whiteThemeField;
    @UiField
    RadioButton                 darkThemeField;
    @UiField
    Switcher                    showNumberOfProjects;

    @UiField
    Frame                       previewFrame;

    @UiField(provided = true)
    FactoryLocalizationConstant locale;
    @UiField(provided = true)
    FactoryResources            resources;

    @Inject
    protected ShareFactoryViewImpl(ShareFactoryViewImplUiBinder uibinder,
                                   PartStackUIResources partStackUIResources,
                                   FactoryLocalizationConstant locale,
                                   FactoryResources resources) {
        super(partStackUIResources);
        this.locale = locale;
        this.resources = resources;
        openFileField = new SuggestBox(new OpenedFilesSuggestOracle());

        setTitle(locale.factoryViewTitle());

        container.add(uibinder.createAndBindUi(this));

        nonEncodedUrlCopy.getElement().setAttribute("class", resources.factoryCSS().smallButton());
        encodedUrlCopy.getElement().setAttribute("class", resources.factoryCSS().smallButton());
    }

    @UiHandler("nonEncodedHtmlButton")
    public void onNonEncodedHtmlButtonClicked(ClickEvent event) {
        delegate.onGitHubSnippetClicked(false);
    }

    @UiHandler("nonEncodedGitHubButton")
    public void onNonEncodedGitHubButtonClicked(ClickEvent event) {
        delegate.onGitHubSnippetClicked(false);
    }

    @UiHandler("nonEncodediFrameButton")
    public void onNonEncodediFrameButtonClicked(ClickEvent event) {
        delegate.onIFrameSnippetClicked(false);
    }

    @UiHandler("nonEncodedSocialButton")
    public void onNonEncodedSocialButtonClicked(ClickEvent event) {
        delegate.onNonEncodedSocialClicked(nonEncodedSocialButton.isDown());
    }

    @UiHandler("encodedHtmlButton")
    public void onEncodedHtmlButtonClicked(ClickEvent event) {
        delegate.onHtmlSnippetClicked(true);
    }

    @UiHandler("encodedGitHubButton")
    public void onEncodedGitHubButtonClicked(ClickEvent event) {
        delegate.onGitHubSnippetClicked(true);
    }

    @UiHandler("encodediFrameButton")
    public void onEncodediFrameButtonClicked(ClickEvent event) {
        delegate.onIFrameSnippetClicked(true);
    }

    @UiHandler("encodedSocialButton")
    public void onEncodedSocialButtonClicked(ClickEvent event) {
        delegate.onEncodedSocialClicked(encodedSocialButton.isDown());
    }

    @UiHandler("generateEncodedUrlButton")
    public void onGenerateEncodedUrlButtonClicked(ClickEvent event) {
        delegate.onGenerateEncodedUrlClicked();
    }

    @UiHandler("nonEncodedUrlCopy")
    public void onNonEncodedUrlCopyClicked(ClickEvent event) {
        selectText(nonencodedUrl.getElement());
    }

    @UiHandler("encodedUrlCopy")
    public void onEncodedUrlCopyClicked(ClickEvent event) {
        selectText(encodedUrl.getElement());
    }

    @UiHandler("emailEncoded")
    public void onEmailEncodedClicked(ClickEvent event) {
        delegate.onMailClicked(true);
        showSocialEncoded(false);
        encodedSocialButton.setDown(false);
    }

    @UiHandler("googlePlusEncoded")
    public void onGoogleEncodedClicked(ClickEvent event) {
        delegate.onGooglePlusClicked(true);
        showSocialEncoded(false);
        encodedSocialButton.setDown(false);
    }

    @UiHandler("twitterEncoded")
    public void onTwitterEncodedClicked(ClickEvent event) {
        delegate.onTwitterClicked(true);
        showSocialEncoded(false);
        encodedSocialButton.setDown(false);
    }

    @UiHandler("facebookEncoded")
    public void onFacebookEncodedClicked(ClickEvent event) {
        delegate.onFacebookClicked(true);
        showSocialEncoded(false);
        encodedSocialButton.setDown(false);
    }


    @UiHandler("emailNonEncoded")
    public void onEmailNonEncodedClicked(ClickEvent event) {
        delegate.onMailClicked(false);
        showSocialNonEncoded(false);
        nonEncodedSocialButton.setDown(false);
    }

    @UiHandler("googlePlusNonEncoded")
    public void onGoogleNonEncodedClicked(ClickEvent event) {
        delegate.onGooglePlusClicked(false);
        showSocialNonEncoded(false);
        nonEncodedSocialButton.setDown(false);
    }

    @UiHandler("twitterNonEncoded")
    public void onTwitterNonEncodedClicked(ClickEvent event) {
        delegate.onTwitterClicked(false);
        showSocialNonEncoded(false);
        nonEncodedSocialButton.setDown(false);
    }

    @UiHandler("facebookNonEncoded")
    public void onFacebookNonEncodedClicked(ClickEvent event) {
        delegate.onFacebookClicked(false);
        showSocialNonEncoded(false);
        nonEncodedSocialButton.setDown(false);
    }


    /**
     * Select the content of the element.
     * 
     * @param elem
     */
    private native void selectText(Element elem) /*-{
		if ($doc.selection && $doc.selection.createRange) {
			var range = $doc.selection.createRange();
			range.moveToElementText(elem);
			range.select();
		} else if ($doc.createRange && $wnd.getSelection) {
			var range = $doc.createRange();
			range.selectNode(elem);
			var selection = $wnd.getSelection();
			selection.removeAllRanges();
			selection.addRange(range);
		}
    }-*/;

    @UiHandler(value = {"verticalAlignField", "horizontalAlignField", "darkThemeField", "whiteThemeField", "showNumberOfProjects"})
    public void onFactoryButtonStyleChanged(ValueChangeEvent<Boolean> event) {
        delegate.onFactoryButtonStyleChanged();
    }

    @UiHandler(value = {"authorField", "openFileField", "expirationDateField", "descriptionField", "findAndReplaceField"})
    public void onFactoryParametersChanged(ValueChangeEvent<String> event) {
        delegate.onParametersChanged();
    }

    /** {@inheritDoc} */
    @Override
    public void setNonEncodedLink(String link) {
        nonencodedUrl.setText(link);
    }

    /** {@inheritDoc} */
    @Override
    public void setEncodedLink(String link) {
        encodedUrl.setText(link);
    }


    /** {@inheritDoc} */
    @Override
    public boolean getVerticalAlign() {
        return verticalAlignField.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public boolean getHorizontalAlign() {
        return horizontalAlignField.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public boolean getWhiteTheme() {
        return whiteThemeField.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public boolean getDarkTheme() {
        return darkThemeField.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public boolean getShowCounter() {
        return showNumberOfProjects.getValue();
    }


    /** {@inheritDoc} */
    @Override
    public native void previewFactoryButton(String content) /*-{
		var frame = this.@com.codenvy.ide.factory.client.share.ShareFactoryViewImpl::previewFrame;
		frame = frame.@com.google.gwt.user.client.ui.Frame::getElement()();
		frame = (frame.contentWindow) ? frame.contentWindow
				: (frame.contentDocument.document) ? frame.contentDocument.document
						: frame.contentDocument;
		frame.document.open();
		frame.document.write(content);
		frame.document.close();
    }-*/;

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return descriptionField.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public String getAuthor() {
        return authorField.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public String getOpenFile() {
        return openFileField.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public String getFindReplace() {
        return findAndReplaceField.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public String getExpirationDate() {
        return expirationDateField.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void showEncodedPanel(boolean isVisible) {
        encodedButtons.setVisible(isVisible);
        encodedPanel.setVisible(isVisible);
        encodedLabel.setVisible(isVisible);
    }

    /** {@inheritDoc} */
    @Override
    public void showGenerateButton(boolean isVisible) {
        generateEncodedUrlButton.setVisible(isVisible);
    }

    private class OpenedFilesSuggestOracle extends SuggestOracle {
        /** {@inheritDoc} */
        @Override
        public boolean isDisplayStringHTML() {
            return true;
        }

        @Override
        public void requestSuggestions(final Request request, final Callback callback) {
            delegate.onRequestFileSuggestions(request.getQuery(), new AsyncCallback<Array<String>>() {
                /** {@inheritDoc} */
                @Override
                public void onSuccess(Array<String> result) {
                    final List<SuggestOracle.Suggestion> suggestions = new ArrayList<>(result.size());
                    for (final String item : result.asIterable()) {
                        suggestions.add(new SuggestOracle.Suggestion() {
                            @Override
                            public String getDisplayString() {
                                return getDisplayName(item);
                            }

                            @Override
                            public String getReplacementString() {
                                return item;
                            }
                        });
                    }

                    callback.onSuggestionsReady(request, new Response(suggestions));
                }

                /** {@inheritDoc} */
                @Override
                public void onFailure(Throwable caught) {
                    Log.error(NavigateToFileViewImpl.class, "Failed to search files.");
                }
            });
        }

        /** Returns the formed display name of the specified path. */
        private String getDisplayName(String path) {
            final String itemName = path.substring(path.lastIndexOf('/') + 1);
            final String itemPath = path.replaceFirst("/", "");
            String displayString = itemName + "   (" + itemPath.substring(0, itemPath.length() - itemName.length() - 1) + ")";

            String[] parts = displayString.split(" ");
            if (parts.length > 1) {
                displayString = parts[0];
                displayString += " <span style=\"color: #989898;\">";
                for (int i = 1; i < parts.length; i++) {
                    displayString += parts[i];
                }
                displayString += "</span>";
            }
            return displayString;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setStyleInitialState() {
        verticalAlignField.setValue(true);
        whiteThemeField.setValue(true);
        showNumberOfProjects.setValue(true);
    }

    /** {@inheritDoc} */
    @Override
    public void showSocialEncoded(boolean isVisible) {
        socialEncoded.setVisible(isVisible);
    }

    /** {@inheritDoc} */
    @Override
    public void showSocialNonEncoded(boolean isVisible) {
        socialNonEncoded.setVisible(isVisible);
    }
}
