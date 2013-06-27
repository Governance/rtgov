/*
 * 2012-3 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.content.acs;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.active.collection.ActiveCollectionSource;
import org.overlord.rtgov.active.collection.util.ActiveCollectionUtil;

public class RTGovACSTest {

    @Test
    public void testLoadACS() {
        try {
            java.io.InputStream is=RTGovACSTest.class.getResourceAsStream("/acs.json");
            
            byte[] b = new byte[is.available()];
            is.read(b);
            
            is.close();
            
            java.util.List<ActiveCollectionSource> acslist=ActiveCollectionUtil.deserializeACS(b);
            
            if (acslist.size() != 5) {
                fail("List should have 5 sources: "+acslist.size());
            }
        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to deserialize: "+e);
        }
    }
}
