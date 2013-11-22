package org.exoplatform.ide.git.server.provider.wso2;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.extension.ssh.server.SshKey;
import org.exoplatform.ide.extension.ssh.server.SshKeyStoreException;
import org.exoplatform.ide.git.server.provider.GitVendorService;
import org.exoplatform.ide.git.server.provider.rest.ProviderException;
import org.exoplatform.ide.vfs.server.exceptions.NotSupportedException;

/**
 * Git service for WSO2.
 */
public class WSO2 extends GitVendorService {
    public WSO2(InitParams initParams) {
        super(initParams);
    }

    /** {@inheritDoc} */
    @Override
    public void uploadNewPublicKey(SshKey publicKey) throws ProviderException {
        throw new NotSupportedException("This operation is not supported for this service.");
    }

    /** {@inheritDoc} */
    @Override
    public void generateAndUploadNewPublicKey() throws SshKeyStoreException, ProviderException {
        throw new NotSupportedException("This operation is not supported for this service.");
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRepositoryPrivate(String repositoryName) {
        return false;
    }
}
