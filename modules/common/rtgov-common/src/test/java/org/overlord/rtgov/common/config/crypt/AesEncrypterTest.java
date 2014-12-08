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

import org.junit.Assert;
import org.junit.Test;
import org.overlord.rtgov.common.config.crypt.AesEncrypter;

public class AesEncrypterTest {

    /**
     * Test.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void test() throws Exception {
        String password = "123admin!"; //$NON-NLS-1$
        String encrypted = AesEncrypter.encrypt(password);
        String decrypted = AesEncrypter.decrypt(encrypted);

        Assert.assertEquals(decrypted, password);
    }
}
