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
package org.exoplatform.ide.git.client.remove;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.shared.Folder;

/**
 * Presenter for removing files in index and file system. The view must implement {@link RemoveFromIndexPresenter.Display}. Add view to
 * View.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 29, 2011 4:35:16 PM anya $
 */
public class RemoveFromIndexPresenter extends GitPresenter implements RemoveFilesHandler {
    public interface Display extends IsView {
        /**
         * Get remove button click handler.
         * 
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getRemoveButton();

        /**
         * Get cancel button click handler.
         * 
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getCancelButton();

        /**
         * Get message label value.
         * 
         * @return {@link HasValue}
         */
        HasValue<String> getMessage();

        /**
         * Get only from index checkbox value
         * 
         * @return {@link HasValue}
         */
        HasValue<Boolean> getFromIndexValue();
    }

    /** Presenter's display. */
    private Display display;

    public RemoveFromIndexPresenter() {
        IDE.addHandler(RemoveFilesEvent.TYPE, this);
    }

    /**
     * @param d display
     */
    public void bindDisplay(Display d) {
        this.display = d;

        display.getRemoveButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doRemove();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
    }

    /** @see org.exoplatform.ide.git.client.add.RemoveFilesHandler#onRemoveFiles(org.exoplatform.ide.git.client.remove.RemoveFilesEvent) */
    @Override
    public void onRemoveFiles(RemoveFilesEvent event) {
        if (makeSelectionCheck()) {
            Display d = GWT.create(Display.class);
            IDE.getInstance().openView(d.asView());
            bindDisplay(d);
            String workDir = getSelectedProject().getPath();
            display.getMessage().setValue(formMessage(workDir), true);
        }
    }

    /**
     * Form the message to display for removing from index, telling the user what is gonna to be removed.
     * 
     * @return {@link String} message to display
     */
    private String formMessage(String workdir) {
        if (selectedItem == null) {
            return "";
        }

        String pattern = selectedItem.getPath().replaceFirst(workdir, "");
        pattern = (pattern.startsWith("/")) ? pattern.replaceFirst("/", "") : pattern;

        // Root of the working tree:
        if (pattern.length() == 0 || "/".equals(pattern)) {
            return GitExtension.MESSAGES.removeFromIndexAll();
        }

        if (selectedItem instanceof Folder) {
            return GitExtension.MESSAGES.removeFromIndexFolder(pattern);
        } else {
            return GitExtension.MESSAGES.removeFromIndexFile(pattern);
        }
    }

    /** Perform removing from index (sends request over HTTP). */
    private void doRemove() {
        try {
            GitClientService.getInstance().remove(vfs.getId(), getSelectedProject().getId(), getFilePatterns(),
                                                  display.getFromIndexValue().getValue().booleanValue(),
                                                  new AsyncRequestCallback<String>() {
                                                      @Override
                                                      protected void onSuccess(String result) {
                                                          IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.removeFilesSuccessfull()));
                                                          if (display.getFromIndexValue().getValue().booleanValue()) {
                                                              IDE.fireEvent(new TreeRefreshedEvent(getSelectedProject()));
                                                          }
                                                          else {
                                                              if (selectedItem instanceof ItemContext) {
                                                                  IDE.fireEvent(new RefreshBrowserEvent(
                                                                                                        ((ItemContext)selectedItem).getParent()));
                                                              }
                                                              else {
                                                                  IDE.fireEvent(new RefreshBrowserEvent());
                                                              }

                                                          }
                                                      }

                                                      @Override
                                                      protected void onFailure(Throwable exception) {
                                                          handleError(exception);
                                                      }
                                                  });
        } catch (RequestException e) {
            handleError(e);
        }
        IDE.getInstance().closeView(display.asView().getId());
    }

    /**
     * Returns pattern of the files to be removed.
     * 
     * @return pattern of the files to be removed
     */
    private String[] getFilePatterns() {
        String projectPath = getSelectedProject().getPath();
        String pattern = selectedItem.getPath().replaceFirst(projectPath, "");

        pattern = (pattern.startsWith("/")) ? pattern.replaceFirst("/", "") : pattern;
        return (pattern.length() == 0 || "/".equals(pattern)) ? new String[]{"."} : new String[]{pattern};
    }

    private void handleError(Throwable t) {
        String errorMessage =
                              (t.getMessage() != null && t.getMessage().length() > 0) ? t.getMessage()
                                  : GitExtension.MESSAGES
                                                         .removeFilesFailed();
        IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
    }

}
