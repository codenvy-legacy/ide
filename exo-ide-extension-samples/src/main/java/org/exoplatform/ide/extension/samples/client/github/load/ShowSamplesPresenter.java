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
package org.exoplatform.ide.extension.samples.client.github.load;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.client.SamplesLocalizationConstant;
import org.exoplatform.ide.extension.samples.client.github.deploy.ImportSampleStep;
import org.exoplatform.ide.extension.samples.client.marshal.RepositoriesUnmarshaller;
import org.exoplatform.ide.git.client.github.GitHubClientService;
import org.exoplatform.ide.git.shared.GitHubRepository;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter to show the list of samples, that stored on github.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: GithubSamplesPresenter.java Aug 30, 2011 12:12:39 PM vereshchaka $
 */
public class ShowSamplesPresenter implements ShowSamplesHandler, ViewClosedHandler, ItemsSelectedHandler,
                                             ImportSampleStep<ProjectData> {

    public interface Display extends IsView {
        HasClickHandlers getNextButton();

        HasClickHandlers getCancelButton();

        ListGridItem<ProjectData> getSamplesListGrid();

        List<ProjectData> getSelectedItems();

        HasValue<String> getProjectNameField();

        void enableNextButton(boolean enable);

        /** Give focus to name field. */
        void focusInNameField();
    }

    private static SamplesLocalizationConstant lb = SamplesExtension.LOCALIZATION_CONSTANT;

    private Display display;

    List<Item> selectedItems;

    private List<ProjectData> selectedProjects;

    private ProjectData selectedProjectData;

    private ImportSampleStep<ProjectData> nextStep;

    public ShowSamplesPresenter() {
        IDE.addHandler(ShowSamplesEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
    }

    private void bindDisplay() {
        display.getNextButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (selectedProjects == null || selectedProjects.isEmpty()) {
                    Dialogs.getInstance().showError(lb.showSamplesErrorSelectRepository());
                    return;
                }

                selectedProjectData = selectedProjects.get(0);
                String name = display.getProjectNameField().getValue();
                if (name != null && !name.isEmpty()) {
                    selectedProjectData.setName(name);
                }
                nextStep.onOpen(selectedProjectData);
                closeView();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                closeView();
            }
        });

        display.getSamplesListGrid().addSelectionHandler(new SelectionHandler<ProjectData>() {
            @Override
            public void onSelection(SelectionEvent<ProjectData> event) {
                selectedProjects = display.getSelectedItems();
                if (selectedProjects == null || selectedProjects.isEmpty()) {
                    display.getProjectNameField().setValue("");
                    display.enableNextButton(false);
                } else {
                    String name = selectedProjects.get(0).getName();
                    display.getProjectNameField().setValue(name);
                    display.enableNextButton(true);
                }
            }
        });
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** @see org.exoplatform.ide.git.client.github.GitHubCollaboratorsHandler.samples.GithubSamplesShowHandler#onShowSamples(org
     * .exoplatform.ide.client.GetCollboratorsEvent.samples.ShowGithubSamplesEvent) */
    @Override
    public void onShowSamples(ShowSamplesEvent event) {
        try {
            // User's name will be taken from configuration:
            GitHubClientService.getInstance().getRepositoriesByUser(
                    null,
                    new AsyncRequestCallback<List<GitHubRepository>>(new RepositoriesUnmarshaller(
                            new ArrayList<GitHubRepository>())) {
                        @Override
                        protected void onSuccess(List<GitHubRepository> result) {
                            openView();
                            List<ProjectData> projectDataList = new ArrayList<ProjectData>();
                            for (GitHubRepository repo : result) {
                                ProjectData projectData =
                                        new ProjectData(repo.getName(), null, null, null, repo.getCloneUrl(), repo.getGitUrl());
                                processDescription(projectData, repo.getDescription());
                                projectDataList.add(projectData);
                            }
                            display.getSamplesListGrid().setValue(projectDataList);
                            display.enableNextButton(false);
                            display.focusInNameField();
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            IDE.fireEvent(new ExceptionThrownEvent(exception));
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void openView() {
        if (display == null) {
            Display d = GWT.create(Display.class);
            IDE.getInstance().openView((View)d);
            display = d;
            bindDisplay();
            return;
        } else {
            IDE.fireEvent(new ExceptionThrownEvent("Show Samples View must be null"));
        }
    }

    private void closeView() {
        IDE.getInstance().closeView(display.asView().getId());
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     * .framework.navigation.event.ItemsSelectedEvent) */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        this.selectedItems = event.getSelectedItems();
    }

    /**
     * Parse description of repository on GitHub, that store in such form:
     * <p/>
     * <code>Type: &lt;project type&gt; Desc: &lt;project description&gt;</code>
     * <p/>
     * Return an array with 2 elements, where element[0] is the type of project and element[1] is the description
     *
     * @param text
     * @return
     */
    private void processDescription(ProjectData projectData, String text) {
        if (text.contains("Targets: ")) {
            String[] res = text.split("^Type: | Desc: | Targets: ");
            if (res.length < 4) {
                projectData.setType(ProjectResolver.getProjectsTypes().toArray(new String[1])[0]);
                projectData.setDescription(text);
                Dialogs
                        .getInstance()
                        .showError(
                                "Can't parse project description: "
                                + text
                                +
                                ". <br/> It must be in format: Type: &lt;project type&gt; Desc: &lt;project description&gt; Targets: &lt;" +
                                "space separated targets&gt;");
            } else {
                projectData.setType(res[1]);
                projectData.setDescription(res[2]);
                projectData.setTargets(parseTargets(res[3]));
            }
        } else {
            String[] res = text.split("^Type: | Desc:");
            if (res.length < 3) {
                projectData.setType(ProjectResolver.getProjectsTypes().toArray(new String[1])[0]);
                projectData.setDescription(text);
                Dialogs.getInstance().showError(
                        "Can't parse project description: " + text
                        + ". <br/> It must be in format: Type: &lt;project type&gt; Desc: &lt;project description&gt;");
            } else {
                projectData.setType(res[1]);
                projectData.setDescription(res[2]);
            }
        }
    }

    private List<String> parseTargets(String targetsStr) {
        ArrayList<String> targets = new ArrayList<String>();
        if (targetsStr != null && !targetsStr.isEmpty()) {
            String[] parts = targetsStr.split(" ");
            for (String part : parts) {
                String target = part.trim();
                if (!target.isEmpty()) {
                    targets.add(target);
                }
            }
        }
        return targets;
    }

    /** @see org.exoplatform.ide.extension.samples.client.github.deploy.ImportSampleStep#onOpen(java.lang.Object) */
    @Override
    public void onOpen(ProjectData value) {
        // it is the first step
    }

    /** @see org.exoplatform.ide.extension.samples.client.github.deploy.ImportSampleStep#onReturn() */
    @Override
    public void onReturn() {
        try {
            // User's name will be taken from configuration:
            GitHubClientService.getInstance().getRepositoriesByUser(
                    null,
                    new AsyncRequestCallback<List<GitHubRepository>>(new RepositoriesUnmarshaller(
                            new ArrayList<GitHubRepository>())) {
                        @Override
                        protected void onSuccess(List<GitHubRepository> result) {
                            openView();
                            List<ProjectData> projectDataList = new ArrayList<ProjectData>();
                            for (GitHubRepository repo : result) {
                                ProjectData projectData =
                                        new ProjectData(repo.getName(), null, null, null, repo.getCloneUrl(), repo.getGitUrl());
                                processDescription(projectData, repo.getDescription());
                                projectDataList.add(projectData);
                            }
                            display.getSamplesListGrid().setValue(projectDataList);
                            display.enableNextButton(false);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            IDE.fireEvent(new ExceptionThrownEvent(exception));
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.extension.samples.client.github.deploy.ImportSampleStep#setNextStep(org.exoplatform.ide.extension.samples
     * .client.github.deploy.ImportSampleStep) */
    @Override
    public void setNextStep(ImportSampleStep<ProjectData> step) {
        nextStep = step;
    }

    /** @see org.exoplatform.ide.extension.samples.client.github.deploy.ImportSampleStep#setPreviousStep(org.exoplatform.ide.extension
     * .samples.client.github.deploy.ImportSampleStep) */
    @Override
    public void setPreviousStep(ImportSampleStep<ProjectData> step) {
        // has no prev step
    }

}
