/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.common.config.crypt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Utility class that encrypt/decrypt using apache commons codec library. It
 * encrypt using AES symetric algorithm and Base 64 encoding
 *
 * @author David Virgil Naranjo
 */
public final class AesEncrypter {

    private static final String SECRET_KEY = "123!overlord!567"; //$NON-NLS-1$

    private static SecretKeySpec _skeySpec;

    static {
        byte[] ivraw = SECRET_KEY.getBytes();
        _skeySpec = new SecretKeySpec(ivraw, "AES"); //$NON-NLS-1$

    }
    
    /**
     * Default constructor.
     */
    private AesEncrypter() {
    }

    /**
     * Encrypt.
     *
     * @param plainText
     *            the plain text
     * @return the string
     */
    public static String encrypt(String plainText) {
        byte[] encrypted;
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES"); //$NON-NLS-1$
            cipher.init(Cipher.ENCRYPT_MODE, _skeySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        try {
            encrypted = cipher.doFinal(plainText.getBytes());
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
        return new String(Base64.encodeBase64(encrypted));

    }

    /**
     * Decrypt.
     *
     * @param encryptedText
     *            the encrypted text
     * @return the string
     */
    public static final String decrypt(String encryptedText) {
        byte[] decoded = Base64.decodeBase64(encryptedText);
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES"); //$NON-NLS-1$
            cipher.init(Cipher.DECRYPT_MODE, _skeySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        try {
            String decryptedString = new String(cipher.doFinal(decoded));
            return decryptedString;
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
