package org.exoplatform.ide.git.client.ssh;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.UnauthorizedException;
import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.JsPopUpOAuthWindow;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.ssh.client.JsonpAsyncCallback;
import org.exoplatform.ide.extension.ssh.client.SshKeyService;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.GenerateGitHubKeyEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.GenerateGitHubKeyHandler;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.RefreshKeysEvent;
import org.exoplatform.ide.extension.ssh.client.marshaller.SshKeysUnmarshaller;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.marshaller.GitUrlInfoUnmarshaller;
import org.exoplatform.ide.git.shared.GitUrlVendorInfo;

import java.util.List;

/**
 * Processor, that perform check if we have private key for specific Git service, url of which we received.
 * If private key found, we call success message for callback, otherwise we try to authorize user if we can do this.
 */
public class SSHKeyProcessor implements SSHKeyProcessorHandler, GenerateGitHubKeyHandler {
    private Callback callback;

    public interface Callback {
        public void onSuccess();
    }

    public SSHKeyProcessor() {
        IDE.addHandler(SSHKeyProcessorEvent.TYPE, this);
        IDE.addHandler(GenerateGitHubKeyEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onSSHKeyProcess(final SSHKeyProcessorEvent event) {
        callback = event.getCallback();

        GitUrlInfoUnmarshaller unmarshaller = new GitUrlInfoUnmarshaller(new GitUrlVendorInfo());
        try {
            GitClientService.getInstance().getUrlVendorInfo(event.getVcsUrl(), new AsyncRequestCallback<GitUrlVendorInfo>(unmarshaller) {
                @Override
                protected void onSuccess(GitUrlVendorInfo info) {
                    if (info.getVendorName() != null && info.isGivenUrlSSH()) {
                        if (event.isUpdatePublicKey()) {
                            generateNewKeyPair(info);
                        } else {
                            getPublicKey(info);
                        }
                    } else {
                        callback.onSuccess();
                    }
                }

                @Override
                protected void onFailure(Throwable e) {
                    IDE.fireEvent(new ExceptionThrownEvent(e));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Search private key for specific host.
     *
     * @param info
     *         {@link org.exoplatform.ide.git.shared.GitUrlVendorInfo}
     */
    private void getPublicKey(final GitUrlVendorInfo info) {
        SshKeyService.get().getAllKeys(new JsonpAsyncCallback<JavaScriptObject>() {
            @Override
            public void onFailure(Throwable e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            }

            @Override
            public void onSuccess(JavaScriptObject result) {
                try {
                    List<KeyItem> keys = SshKeysUnmarshaller.unmarshal(result);
                    for (KeyItem key : keys) {
                        if (key.getHost().equals(info.getVendorBaseHost())) {
                            callback.onSuccess();
                            return;
                        }
                    }

                    //key not found, lets generate it
                    generateNewKeyPair(info);
                } catch (UnmarshallerException e) {
                    IDE.fireEvent(new ExceptionThrownEvent(e));
                }
            }
        });
    }

    /**
     * Generate new Key pair for specific host.
     *
     * @param info
     *         {@link org.exoplatform.ide.git.shared.GitUrlVendorInfo}
     */
    private void generateNewKeyPair(final GitUrlVendorInfo info) {
        final JsPopUpOAuthWindow.Callback authCallback = new JsPopUpOAuthWindow.Callback() {
            @Override
            public void oAuthFinished(int authenticationStatus) {
                if (authenticationStatus == 2) {
                    generateNewKeyPair(info);
                } else {
                    IDE.fireEvent(new ExceptionThrownEvent("Authorization failed."));
                }
            }
        };

        AsyncRequestCallback<Void> genCallback = new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                //we already know that new key pair have been already generated and uploaded, lets try to clone
                callback.onSuccess();
            }

            @Override
            protected void onFailure(Throwable e) {
                if (e instanceof UnauthorizedException) {
                    Dialogs.getInstance().ask(GitExtension.MESSAGES.authorizeTitle(),
                                              GitExtension.MESSAGES.authorizeBody(info.getVendorBaseHost()),
                                              new BooleanValueReceivedHandler() {
                                                  @Override
                                                  public void booleanValueReceived(Boolean value) {
                                                      if (value != null && value) {
                                                          new JsPopUpOAuthWindow().withOauthProvider(info.getVendorName())
                                                                                  .withScopes(info.getOAuthScopes())
                                                                                  .withCallback(authCallback)
                                                                                  .login();
                                                      }
                                                  }
                                              });
                } else {
                    IDE.fireEvent(new ExceptionThrownEvent(e));
                }
            }
        };

        try {
            AsyncRequest.build(RequestBuilder.POST, Utils.getRestContext() + Utils.getWorkspaceName() + "/git-service/" +
                                                    info.getVendorName() + "/ssh/upload").send(genCallback);
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * *******************************************************************************
     * SSH Key Management Window Processing
     * *******************************************************************************
     */
    @Override
    public void onGenerateGitHubSshKey(GenerateGitHubKeyEvent event) {
        this.callback = sshKeyGeneratedCallback;
        //TODO need adapt code to use there
    }

    Callback sshKeyGeneratedCallback = new Callback() {
        @Override
        public void onSuccess() {
            IDE.fireEvent(new RefreshKeysEvent());
        }
    };
}
