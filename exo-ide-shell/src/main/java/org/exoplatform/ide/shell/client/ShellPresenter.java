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
package org.exoplatform.ide.shell.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;

import org.exoplatform.gwtframework.commons.exception.JobNotFoundException;
import org.exoplatform.gwtframework.commons.exception.UnauthorizedException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;
import org.exoplatform.ide.shell.client.commands.Utils;
import org.exoplatform.ide.shell.client.marshal.GenericJsonUnmarshaller;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for Shell console.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Aug 2, 2011 11:03:32 AM anya $
 */
public class ShellPresenter {
    interface Display extends ConsoleWriter {
        /**
         * Get key press handler.
         *
         * @return {@link HasKeyPressHandlers} handler
         */
        HasKeyPressHandlers getKeyPressHandler();

        /**
         * Get key down handler.
         *
         * @return {@link HasKeyDownHandlers} handler
         */
        HasKeyDownHandlers getKeyDownHandler();

        /**
         * Append symbol to typed buffer.
         *
         * @param c
         *         char to append
         */
        void appendBuffer(char c);

        /**
         * Append symbols to typed buffer.
         *
         * @param c
         *         symbols
         */
        void appendBuffer(CharSequence c);

        /** Remove last symbol from typed buffer. */
        void removeFromBuffer();

        /** Clear typed buffer value. */
        void clearBuffer();

        /**
         * Submit typed buffer's value.
         *
         * @return {@link String} submitted line
         */
        String submitBuffer();

        /** Refresh console. */
        void refreshConsole();

        /**
         * Get the value of typed buffer.
         *
         * @return {@link String}
         */
        String getBuffer();

        /** Focus in console. */
        void focusInConsole();

        /** Prepare for paste operation. */
        void preparePaste();

        /** Finish paste operation. */
        void finishPaste();

        /** Move cursor to the left. */
        void moveLeft();

        /** Move cursor the right. */
        void moveRight();

        /** Move cursor to the home of the input. */
        void moveHome();

        /** Move cursor to the end of the input. */
        void moveEnd();

        /** Delete symbol after cursor. */
        void deleteSymbol();
    }

    private Display display;

    /** Buffer of Shell commands. */
    private ShellComandBuffer buffer;

    /** Last pressed key. */
    private int lastKeyPressed;

    /** Is TAB key pressed. */
    private boolean isTabPressed;

    public ShellPresenter(Display display) {
        this.display = display;
        bindDisplay();
        buffer = new ShellComandBuffer();
        try {
            String com = Environment.get().getValue(EnvironmentVariables.COMMAND_BUFFER);
            JSONValue value = JSONParser.parseLenient(com);
            buffer.init(value.isArray());
        } catch (Exception e) {
            // TODO: handle exception
        }
        Window.addWindowClosingHandler(new ClosingHandler() {

            @Override
            public void onWindowClosing(ClosingEvent event) {
                Environment.get().saveValue(EnvironmentVariables.COMMAND_BUFFER, buffer.toJSON());
            }
        });
    }

    public void bindDisplay() {
        display.getKeyPressHandler().addKeyPressHandler(new KeyPressHandler() {

            @Override
            public void onKeyPress(KeyPressEvent event) {
                int code = event.getNativeEvent().getKeyCode();

                boolean handled = false;
                if (code == KeyCodes.KEY_BACKSPACE) {
                    display.removeFromBuffer();
                    handled = true;
                } else if (code == KeyCodes.KEY_ENTER) {
                    String s = display.submitBuffer();
                    if (!s.isEmpty()) {
                        processCommand(s);
                    } else
                        display.printPrompt();

                    handled = true;
                } else if (code == KeyCodes.KEY_TAB) {
                    performComplete();
                    handled = true;
                } else if (code == KeyCodes.KEY_UP) {
                    goUp();
                    handled = true;
                } else if (code == KeyCodes.KEY_DOWN) {
                    goDown();
                    handled = true;
                } else if (code == KeyCodes.KEY_LEFT) {
                    display.moveLeft();
                    handled = true;
                } else if (code == KeyCodes.KEY_RIGHT) {
                    display.moveRight();
                    handled = true;
                } else if (code == KeyCodes.KEY_HOME) {
                    display.moveHome();
                    handled = true;
                } else if (code == KeyCodes.KEY_END) {
                    display.moveEnd();
                    handled = true;
                } else if (code == KeyCodes.KEY_DELETE && BrowserResolver.CURRENT_BROWSER == Browser.FIREFOX) {
                    display.deleteSymbol();
                    handled = true;
                } else {
                    char c = event.getCharCode();

                    if ((int)c != 0 && !event.isControlKeyDown()) {
                        display.appendBuffer(c);
                        handled = true;
                    }
                }
                lastKeyPressed = code;
                if (handled) {
                    display.refreshConsole();
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        display.getKeyDownHandler().addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                int code = event.getNativeKeyCode();
                // key code 86 is 'v'
                if (event.getNativeEvent().getKeyCode() == 86 && event.isControlKeyDown()) {
                    display.preparePaste();
                    new Timer() {

                        @Override
                        public void run() {
                            display.finishPaste();
                        }
                    }.schedule(10);
                }
                // key code '88' is x
                else if (event.getNativeEvent().getKeyCode() == 88 && event.isControlKeyDown() && event.isShiftKeyDown()) {
                    display.clearConsole();
                } else if (BrowserResolver.CURRENT_BROWSER != Browser.FIREFOX) {
                    boolean handled = false;
                    if (code == KeyCodes.KEY_BACKSPACE) {
                        display.removeFromBuffer();
                        handled = true;
                    } else if (code == KeyCodes.KEY_UP) {
                        goUp();
                        handled = true;
                    } else if (code == KeyCodes.KEY_DOWN) {
                        goDown();
                        handled = true;
                    } else if (code == KeyCodes.KEY_TAB) {
                        performComplete();
                        handled = true;
                    } else if (code == KeyCodes.KEY_LEFT) {
                        display.moveLeft();
                        handled = true;
                    } else if (code == KeyCodes.KEY_RIGHT) {
                        display.moveRight();
                        handled = true;
                    } else if (code == KeyCodes.KEY_HOME) {
                        display.moveHome();
                        handled = true;
                    } else if (code == KeyCodes.KEY_END) {
                        display.moveEnd();
                        handled = true;
                    } else if (code == KeyCodes.KEY_DELETE) {
                        display.deleteSymbol();
                        handled = true;
                    }

                    lastKeyPressed = code;
                    if (handled) {
                        display.refreshConsole();
                        event.preventDefault();
                        event.stopPropagation();
                    }
                }
            }
        });
    }

    /**
     * Process user command.
     *
     * @param command
     */
    public void processCommand(final String command) {
        buffer.add(command);
        AsyncRequestCallback<StringBuilder> asyncRequestCallback =
                new AsyncRequestCallback<StringBuilder>(new GenericJsonUnmarshaller(new StringBuilder())) {
                    /**
                     * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onSuccess(java.lang.Object)
                     */
                    @Override
                    protected void onSuccess(StringBuilder result) {
                        String res = result.toString();
                        res = (res.endsWith("\n") || res.isEmpty()) ? res : res + "\n";
                        display.print(res);
                    }

                    /**
                     * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
                     */
                    @Override
                    protected void onFailure(Throwable exception) {
                        if (exception instanceof JobNotFoundException) {
                            return;
                        }

                        if (exception instanceof UnauthorizedException) {
                            display.println("Unauthorized to perform operation.");
                        }

                        String message =
                                (exception.getMessage() != null) ? exception.getMessage()
                                                                 : "Unknown error in processing the command.\n";
                        message = (message.endsWith("\n")) ? message : message + "\n";
                        display.print(message);
                    }
                };

        ShellService.getService().processCommand(command, asyncRequestCallback);

        // TODO check command is asynchronous:
        if (command.trim().endsWith("&")) {
            display.print("");
        }
    }

    /** Perform the autocomplete of the command. */
    public void performComplete() {
        String prefix = display.getBuffer();
        if (prefix.isEmpty())
            return;

        List<String> commands = CLIResourceUtil.getAllCommandNames(CloudShell.getCommands());
        List<String> suggestions = new ArrayList<String>();
        for (String name : commands) {
            if (name.startsWith(prefix)) {
                suggestions.add(name);
            }
        }

        if (suggestions.isEmpty()) {
            if (lastKeyPressed == KeyCodes.KEY_TAB) {
                isTabPressed = true;
            } else
                isTabPressed = false;
            performFolderNameComplete();
            return;
        }

        if (suggestions.size() == 1) {
            display.clearBuffer();
            display.appendBuffer(suggestions.get(0) + " ");
            display.refreshConsole();
        } else {
            display.appendBuffer("\n");
            for (String key : suggestions) {
                display.appendBuffer(key + " ");
            }
            display.submitBuffer();
            display.printPrompt();
            display.appendBuffer(prefix);
            display.refreshConsole();
        }

    }

    /** Complete folder's name. */
    private void performFolderNameComplete() {
        final String prefix =
                display.getBuffer().substring(display.getBuffer().lastIndexOf(" ") + 1, display.getBuffer().length());
        if (prefix.contains("/")) {
            final String folderPath = prefix.substring(0, prefix.lastIndexOf("/"));
            String newPath = Utils.getPath(Environment.get().getCurrentFolder(), folderPath);
            try {
                VirtualFileSystem.getInstance().getItemByPath(newPath,
                                                              new AsyncRequestCallback<ItemWrapper>(
                                                                      new ItemUnmarshaller(new ItemWrapper())) {

                                                                  @Override
                                                                  protected void onSuccess(ItemWrapper result) {
                                                                      Item i = result.getItem();
                                                                      if (i instanceof Folder) {
                                                                          getFolderChildren((Folder)i, prefix);
                                                                      }
                                                                      // TODO
                                                                  }

                                                                  @Override
                                                                  protected void onFailure(Throwable exception) {
                                                                      CloudShell.console()
                                                                                .print(CloudShell.messages.cdErrorFolder(folderPath) +
                                                                                       "\n");
                                                                  }
                                                              });
            } catch (RequestException e) {
                CloudShell.console().print(CloudShell.messages.cdErrorFolder(folderPath) + "\n");
            }
        } else {
            getFolderChildren(Environment.get().getCurrentFolder(), prefix);
        }

    }

    private void childrenReceived(List<Item> childrens, String prefix) {
        final String namePrefix;
        if (prefix.contains("/")) {
            namePrefix = prefix.substring(prefix.lastIndexOf("/") + 1, prefix.length());
        } else
            namePrefix = prefix;
        if (prefix.isEmpty()) {
            display.appendBuffer("\n");
            display.appendBuffer(Utils.formatItems(childrens));
            display.submitBuffer();
            display.printPrompt();
            display.appendBuffer(prefix);
            display.refreshConsole();
        } else {
            List<Item> items = new ArrayList<Item>();
            for (Item i : childrens) {
                if (i.getName().startsWith(namePrefix)) {
                    items.add(i);
                }
            }
            if (items.isEmpty()) {
                return;
            }
            if (items.size() == 1) {
                String buf = display.getBuffer();
                display.clearBuffer();
                Item i = items.get(0);
                String s = "";
                if (i.getItemType() != ItemType.FILE)
                    s = "/";
                else
                    s = " ";
                display.appendBuffer(buf + items.get(0).getName().substring(namePrefix.length()) + s);
                display.refreshConsole();
            } else {
                boolean flag = true;
                String p = namePrefix;
                String maxName = Utils.getMaxLengthName(items);
                while (flag) {
                    if (p.equals(maxName)) {
                        break;
                    }
                    p += maxName.charAt(p.length());
                    for (Item i : items) {
                        if (!i.getName().startsWith(p)) {
                            flag = false;
                            p = p.substring(0, p.length() - 1);
                            break;
                        }
                    }
                }
                if (namePrefix.equals(p) && isTabPressed) {
                    String buf = display.getBuffer();
                    display.appendBuffer("\n");
                    display.appendBuffer(Utils.formatItems(items));
                    display.submitBuffer();
                    display.printPrompt();
                    display.appendBuffer(buf);
                    display.refreshConsole();
                } else {
                    p = p.substring(namePrefix.length());
                    String buf = display.getBuffer();
                    display.clearBuffer();
                    display.appendBuffer(buf + p);
                    display.refreshConsole();
                }
            }

        }
    }

    private void getFolderChildren(final Folder folder, final String prefix) {
        try {
            VirtualFileSystem.getInstance().getChildren(folder,
                                                        new AsyncRequestCallback<List<Item>>(
                                                                new ChildrenUnmarshaller(new ArrayList<Item>())) {

                                                            @Override
                                                            protected void onSuccess(List<Item> result) {
                                                                childrenReceived(result, prefix);
                                                            }

                                                            @Override
                                                            protected void onFailure(Throwable exception) {
                                                                CloudShell.console().println(
                                                                        CloudShell.messages
                                                                                  .lsError(Environment.get().getCurrentFolder().getPath()));
                                                            }
                                                        });
        } catch (RequestException e) {
            CloudShell.console().println(CloudShell.messages.lsError(Environment.get().getCurrentFolder().getPath()));
        }
    }

    /** Navigate the shell command buffer in up direction. */
    public void goUp() {
        String command = buffer.goUp();
        if (command == null)
            return;

        display.clearBuffer();
        display.appendBuffer(command);
        display.refreshConsole();
    }

    /** Navigate the shell command buffer in down direction. */
    public void goDown() {
        String command = buffer.goDown();

        if (command == null) {
            command = "";
        }
        display.clearBuffer();
        display.appendBuffer(command);
        display.refreshConsole();
    }
}
