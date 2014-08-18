/*
 * Copyright 2013 JBoss Inc
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
package org.overlord.rtgov.ui.client.shared.beans;

import org.junit.Assert;
import org.junit.Test;
import org.overlord.rtgov.ui.client.model.QName;

/**
 *
 * @author eric.wittmann@redhat.com
 */
public class QNameTest {

    /**
     * Test method for {@link org.overlord.rtgov.ui.client.model.QName#fromString(java.lang.String)}.
     */
    @Test
    public void testFromString() {
        QName qName = QName.fromString("{urn:namespace}localPart"); //$NON-NLS-1$
        Assert.assertNotNull(qName);
        Assert.assertEquals("urn:namespace", qName.getNamespaceURI()); //$NON-NLS-1$
        Assert.assertEquals("localPart", qName.getLocalPart()); //$NON-NLS-1$
    }

}
