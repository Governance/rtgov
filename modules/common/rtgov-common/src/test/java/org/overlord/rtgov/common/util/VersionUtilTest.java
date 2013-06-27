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
package org.overlord.rtgov.common.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.common.util.VersionUtil;

public class VersionUtilTest {

    @Test
    public void testIsNewerVersionNumeric() {
        if (!VersionUtil.isNewerVersion("1", "2")) {
            fail("Wrong result 1");
        }
        
        if (!VersionUtil.isNewerVersion(""+System.currentTimeMillis(),
                         ""+System.currentTimeMillis()+2000)) {
            fail("Wrong result 2");
        }
    }
    
    @Test
    public void testIsNewerVersionLexical() {
        if (!VersionUtil.isNewerVersion("1.1.0.Final", "1.1.1.Final")) {
            fail("Wrong result 1");
        }
        
        if (!VersionUtil.isNewerVersion("1.1.0.Final", "1.10.0.Final")) {
            fail("Wrong result 2");
        }
        
        if (!VersionUtil.isNewerVersion("1.2.0.Final", "1.10.0.Final")) {
            fail("Wrong result 3");
        }
        
        if (!VersionUtil.isNewerVersion("1.2.0.Final", "2.1.0.Final")) {
            fail("Wrong result 4");
        }
        
        if (!VersionUtil.isNewerVersion("1.2.0.Final", "1.2.0.GA")) {
            fail("Wrong result 5");
        }
    }
}
