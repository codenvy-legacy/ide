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
package org.exoplatform.ide.extension.samples.client.inviting.github;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import org.exoplatform.ide.git.shared.GitHubUser;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class GitHubUserTile extends Composite {

    private static GitHubUserTileUiBinder uiBinder = GWT.create(GitHubUserTileUiBinder.class);

    interface GitHubUserTileUiBinder extends UiBinder<Widget, GitHubUserTile> {
    }

    interface Style extends CssResource {
        String userFieldBody();

        String userFieldBodySelected();
    }

    @UiField
    Style style;

    @UiField
    FlowPanel userFieldBody;

    @UiField
    CheckBox checkBox;

    @UiField
    Image avatarImage;

    @UiField
    Label name, company, email;

    private GitHubUser gitUser;

    private GitHubUserSelectionChangedHandler selectionChangedHandler;

    public GitHubUserTile(GitHubUser user) {
        gitUser = user;

        initWidget(uiBinder.createAndBindUi(this));

        avatarImage.setUrl(user.getAvatarUrl());
        name.setText(user.getName());
        company.setText(user.getCompany());
        email.setText(user.getEmail());

        checkBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setSelected(checkBox.getValue());
                if (selectionChangedHandler != null) {
                    selectionChangedHandler.onGitHubUserSelectionChanged(gitUser, checkBox.getValue().booleanValue());
                }
            }
        });

        avatarImage.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setSelected(!checkBox.getValue());
                if (selectionChangedHandler != null) {
                    selectionChangedHandler.onGitHubUserSelectionChanged(gitUser, checkBox.getValue().booleanValue());
                }
            }
        });
    }

    public void setSelectionChangedHandler(GitHubUserSelectionChangedHandler selectionChangedHandler) {
        this.selectionChangedHandler = selectionChangedHandler;
    }

    public boolean isSelected() {
        return checkBox.getValue();
    }

    public void setSelected(boolean selected) {
        checkBox.setValue(selected);
        if (selected) {
            userFieldBody.setStyleName(style.userFieldBodySelected());
        } else {
            userFieldBody.setStyleName(style.userFieldBody());
        }
    }
}
