package org.exoplatform.ide.git.client.ssh;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.UnauthorizedException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.JsPopUpOAuthWindow;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.ssh.client.SshKeyExtension;
import org.exoplatform.ide.extension.ssh.client.SshKeyService;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.GenerateGitHubKeyEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.GenerateGitHubKeyHandler;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.RefreshKeysEvent;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;
import org.exoplatform.ide.extension.ssh.shared.ListKeyItem;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.marshaller.GitUrlInfoUnmarshaller;
import org.exoplatform.ide.git.shared.GitUrlVendorInfo;

import java.util.Arrays;

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
        AutoBean<ListKeyItem> keysAutobean = SshKeyExtension.AUTO_BEAN_FACTORY.keyItems();
        AutoBeanUnmarshaller<ListKeyItem> unmarshaller = new AutoBeanUnmarshaller<ListKeyItem>(keysAutobean);

        try {
            SshKeyService.get().getAllKeys(new AsyncRequestCallback<ListKeyItem>(unmarshaller) {
                @Override
                protected void onSuccess(ListKeyItem result) {
                    for (KeyItem key : result.getKeys()) {
                        if (key.getHost().equals(info.getVendorBaseHost())) {
                            callback.onSuccess();
                            return;
                        }

                        //key not found, lets generate it
                        generateNewKeyPair(info);
                    }
                }

                @Override
                protected void onFailure(Throwable e) {
                    Dialogs.getInstance().showError(e.getLocalizedMessage());
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
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
                                              GitExtension.MESSAGES.authorizeSSHBody(info.getVendorBaseHost()),
                                              new BooleanValueReceivedHandler() {
                                                  @Override
                                                  public void booleanValueReceived(Boolean value) {
                                                      if (value != null && value) {
                                                          new JsPopUpOAuthWindow().withOauthProvider(info.getVendorName())
                                                                                  .withScopes(info.getOAuthScopes())
                                                                                  .withCallback(authCallback)
                                                                                  .login();
                                                      } else {
                                                          IDE.fireEvent(new RefreshKeysEvent());
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
        generateNewKeyPair(new GitUrlVendorInfo("github", "github.com", Arrays.asList("user", "repo"), false));
    }

    Callback sshKeyGeneratedCallback = new Callback() {
        @Override
        public void onSuccess() {
            IDE.fireEvent(new RefreshKeysEvent());
        }
    };
}
