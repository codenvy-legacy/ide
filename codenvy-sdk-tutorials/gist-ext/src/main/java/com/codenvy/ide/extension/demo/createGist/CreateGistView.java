package com.codenvy.ide.extension.demo.createGist;

import com.codenvy.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

import javax.validation.constraints.NotNull;

/** The view of {@link CreateGistPresenter}. */
@ImplementedBy(CreateGistViewImpl.class)
public interface CreateGistView extends View<CreateGistView.ActionDelegate> {
    /** Needs for delegate some function into {@link CreateGistView} view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Create button. */
        void onCreateClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having changed something. */
        void onValueChanged();
    }

    /** @return entered code snippet */
    @NotNull
    String getSnippet();

    /**
     * Set content into snippet field.
     *
     * @param snippet
     *         code snippet need to publish to Gist
     */
    void setSnippet(@NotNull String snippet);

    /** @return <code>true</code> if need to create public Gist, and <code>false</code> if private */
    boolean isPublic();

    /**
     * Set publicity of a created Gist.
     *
     * @param isPublic
     *         <code>true</code> need to create public Gist, <code>false</code> if private
     */
    void setPublic(boolean isPublic);

    /**
     * Change the enable state of the create button.
     *
     * @param enable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableCreateButton(boolean enable);

    /** Give focus to the snippet field. */
    void focusInSnippetField();

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}