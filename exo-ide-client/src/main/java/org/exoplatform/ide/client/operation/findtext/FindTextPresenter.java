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
package org.exoplatform.ide.client.operation.findtext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.event.SearchCompleteCallback;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: ${date} ${time}
 */
public class FindTextPresenter implements EditorActiveFileChangedHandler, ViewClosedHandler, FindTextHandler {

    public interface Display extends IsView {

        HasValue<Boolean> getCaseSensitiveField();

        HasValue<String> getResultLabel();

        TextFieldItem getFindField();

        TextFieldItem getReplaceField();

        HasClickHandlers getFindButton();

        HasClickHandlers getReplaceButton();

        HasClickHandlers getReplaceFindButton();

        HasClickHandlers getReplaceAllButton();

        void enableFindButton(boolean isEnable);

        void enableReplaceFindButton(boolean isEnable);

        void enableReplaceButton(boolean isEnable);

        void enableReplaceAllButton(boolean isEnable);

        void focusInFindField();

    }

    private Display display;

    private final String STRING_NOT_FOUND = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT.findTextStringNotFound();

    private Editor editor;

    public FindTextPresenter() {
        IDE.getInstance().addControl(new FindTextControl(), Docking.TOOLBAR);

        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(FindTextEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client
     * .editor.event.EditorActiveFileChangedEvent) */
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        editor = event.getEditor();

        if (display == null) {
            return;
        }

        if (event.getFile() == null) {
            IDE.getInstance().closeView(display.asView().getId());
            return;
        }

        display.enableReplaceButton(false);
        display.enableReplaceFindButton(false);

        String query = display.getFindField().getValue();
        if (query == null || query.isEmpty()) {
            display.enableFindButton(false);
            display.enableReplaceAllButton(false);
        } else {
            display.enableFindButton(true);
            display.enableReplaceAllButton(true);
        }
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    @Override
    public void onFindText(FindTextEvent event) {
        if (display == null) {
            Display d = GWT.create(Display.class);
            IDE.getInstance().openView(d.asView());
            bindDisplay(d);
            display.focusInFindField();
        }
    }

    public void bindDisplay(Display d) {
        this.display = d;

        display.getFindButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doFind();
            }
        });

        display.getReplaceButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doReplace();
            }
        });

        display.getReplaceFindButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doReplaceFind();
            }
        });

        display.getReplaceAllButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                firstReplaceIteration = true;
                doReplaceAll();
            }
        });

        display.getFindField().addKeyPressHandler(queryFieldKeyPressHandler);

        display.enableFindButton(false);
        display.enableReplaceFindButton(false);
        display.enableReplaceAllButton(false);
        display.enableReplaceButton(false);
    }

    private KeyPressHandler queryFieldKeyPressHandler = new KeyPressHandler() {
        @Override
        public void onKeyPress(KeyPressEvent event) {
            display.enableReplaceButton(false);
            display.enableReplaceFindButton(false);
            replaceEnabled = false;

            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    display.enableReplaceButton(false);
                    display.enableReplaceFindButton(false);

                    String query = display.getFindField().getValue();
                    if (query == null || query.isEmpty()) {
                        display.enableFindButton(false);
                        display.enableReplaceAllButton(false);
                    } else {
                        display.enableFindButton(true);
                        display.enableReplaceAllButton(true);
                    }

                    display.getResultLabel().setValue("");
                }
            });
        }
    };

    /**
     * Enables or disables Replace, Replace All, Replace / Find buttons.
     *
     * @return
     */
    private boolean checkReplaceEnabled() {
        if (replaceEnabled) {
            display.enableReplaceButton(true);
            display.enableReplaceFindButton(true);
            return true;
        }

        display.enableReplaceButton(false);
        display.enableReplaceFindButton(false);
        return false;
    }

    private void doFind() {
        if (editor == null) {
            return;
        }

        String query = display.getFindField().getValue();
        if (query == null || query.isEmpty()) {
            return;
        }
        boolean caseSensitive = display.getCaseSensitiveField().getValue();

        editor.search(query, caseSensitive, new SearchCompleteCallback() {
            @Override
            public void onSearchComplete(boolean success) {
                replaceEnabled = success;
                checkReplaceEnabled();

                display.getResultLabel().setValue(success ? "" : STRING_NOT_FOUND);
            }
        });
    }

    private boolean replaceEnabled = false;

    private void doReplace() {
        if (!replaceEnabled || editor == null) {
            return;
        }

        String replacement = display.getReplaceField().getValue();
        if (replacement == null) {
            replacement = "";
        }

        editor.replaceMatch(replacement);
        display.enableReplaceButton(false);
        display.enableReplaceFindButton(false);
    }

    private void doReplaceFind() {
        if (!replaceEnabled || editor == null) {
            return;
        }
      
      /*
       * Replace
       */
        String replacement = display.getReplaceField().getValue();
        if (replacement == null) {
            replacement = "";
        }

        editor.replaceMatch(replacement);
        //display.enableReplaceButton(false);
      
      /*
       * Find
       */
        String query = display.getFindField().getValue();
        if (query == null || query.isEmpty()) {
            return;
        }
        boolean caseSensitive = display.getCaseSensitiveField().getValue();

        editor.search(query, caseSensitive, new SearchCompleteCallback() {
            @Override
            public void onSearchComplete(boolean success) {
                replaceEnabled = success;
                checkReplaceEnabled();

                display.getResultLabel().setValue(success ? "" : STRING_NOT_FOUND);
            }
        });
    }

    private boolean firstReplaceIteration = false;

    private void doReplaceAll() {
        if (editor == null) {
            return;
        }
      
      /*
       * Replace
       */
        String replacement = display.getReplaceField().getValue();
        if (replacement == null) {
            replacement = "";
        }

        editor.replaceMatch(replacement);
      
      /*
       * Find
       */
        String query = display.getFindField().getValue();
        if (query == null || query.isEmpty()) {
            return;
        }

        boolean caseSensitive = display.getCaseSensitiveField().getValue();

        editor.search(query, caseSensitive, new SearchCompleteCallback() {
            @Override
            public void onSearchComplete(boolean success) {
                if (firstReplaceIteration && !success) {
                    display.getResultLabel().setValue(STRING_NOT_FOUND);
                } else {
                    display.getResultLabel().setValue("");
                }

                firstReplaceIteration = false;

                replaceEnabled = success;
                if (checkReplaceEnabled()) {
                    doReplaceAll();
                }
            }
        });
    }

}
