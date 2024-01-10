package org.secure.utilsCrypted.generator;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;

public class SessionKeyGenerator {
    public static SecretKey generateKey(String Algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        Security.addProvider(new BouncyCastleProvider());
        KeyGenerator generator = KeyGenerator.getInstance(Algorithm,"BC");
        generator.init(new SecureRandom());

        return generator.generateKey();
    }
}
