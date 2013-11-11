package com.codenvy.ide.tutorial.dto.createGist;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.tutorial.dto.TutorialDtoLocalizationConstant;
import com.codenvy.ide.tutorial.dto.TutorialDtoResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** The implementation of {@link CreateGistView}. */
@Singleton
public class CreateGistViewImpl extends DialogBox implements CreateGistView {
    private static CommitViewImplUiBinder ourUiBinder = GWT.create(CommitViewImplUiBinder.class);
    @UiField(provided = true)
    final TutorialDtoResources            res;
    @UiField(provided = true)
    final TutorialDtoLocalizationConstant locale;
    @UiField
    CheckBox                  publicField;
    @UiField
    TextArea                  snippet;
    @UiField
    com.codenvy.ide.ui.Button btnCreate;
    @UiField
    com.codenvy.ide.ui.Button btnCancel;
    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param resources
     * @param locale
     */
    @Inject
    protected CreateGistViewImpl(TutorialDtoResources resources, TutorialDtoLocalizationConstant locale) {
        this.res = resources;
        this.locale = locale;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText(locale.createViewTitle());
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getSnippet() {
        return snippet.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setSnippet(@NotNull String snippet) {
        this.snippet.setText(snippet);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPublic() {
        return publicField.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setPublic(boolean isPublic) {
        publicField.setValue(isPublic);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableCreateButton(boolean enable) {
        btnCreate.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void focusInSnippetField() {
        snippet.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnCreate")
    public void onCommitClicked(ClickEvent event) {
        delegate.onCreateClicked();
    }

    @UiHandler("btnCancel")
    public void onCancelClicked(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("snippet")
    public void onMessageChanged(KeyUpEvent event) {
        delegate.onValueChanged();
    }

    interface CommitViewImplUiBinder extends UiBinder<Widget, CreateGistViewImpl> {
    }
}
