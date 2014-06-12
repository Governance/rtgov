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
package org.overlord.rtgov.tests.platforms.jbossas.servicedependency;

import static org.junit.Assert.*;

public class TestUtils {
    
    public static java.io.File copyToTmpFile(java.io.File source, String filename) {
        String tmpdir=System.getProperty("java.io.tmpdir");
        java.io.File dir=new java.io.File(tmpdir+java.io.File.separator+"bamtests"+System.currentTimeMillis());
        
        dir.mkdir();
        
        dir.deleteOnExit();
        
        java.io.File ret=new java.io.File(dir, filename);
        ret.deleteOnExit();
        
        // Copy contents to the tmp file
        try {
            java.io.FileInputStream fis=new java.io.FileInputStream(source);
            java.io.FileOutputStream fos=new java.io.FileOutputStream(ret);
            
            byte[] b=new byte[10240];
            int len=0;
            
            while ((len=fis.read(b)) > 0) {
                fos.write(b, 0, len);
            }
            
            fis.close();
            
            fos.flush();
            fos.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to copy file '"+filename+"': "+e);
        }
        
        return(ret);
    }
}