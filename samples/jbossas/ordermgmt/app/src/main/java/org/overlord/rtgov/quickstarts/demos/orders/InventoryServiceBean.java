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

package org.overlord.rtgov.quickstarts.demos.orders;

import java.util.HashMap;
import java.util.Map;

import org.overlord.rtgov.client.ActivityReporter;
import org.overlord.rtgov.client.DefaultActivityReporter;
import org.switchyard.component.bean.Service;

@Service(InventoryService.class)
public class InventoryServiceBean implements InventoryService {

    private final Map<String, Item> _inventory = new HashMap<String, Item>();
    
    private ActivityReporter _reporter=new DefaultActivityReporter();
    
    public InventoryServiceBean() {
        Item butter = new Item()
            .setItemId("BUTTER")
            .setName("Not Parkay")
            .setQuantity(1000)
            .setUnitPrice(1.25);
        _inventory.put(butter.getItemId(), butter);
        
        Item jam = new Item()
            .setItemId("JAM")
            .setName("Strawberry Jam")
            .setQuantity(500)
            .setUnitPrice(2.40);
        _inventory.put(jam.getItemId(), jam);
    }

    @Override
    public Item lookupItem(String itemId) throws ItemNotFoundException {
        
        if (itemId.equals("ERROR")) {
            throw new RuntimeException("Failed with an error");
        }
        
        Item item = _inventory.get(itemId);
        if (item == null) {
            if (_reporter != null) {
                _reporter.logError("No item found for id '"+itemId+"'");
            }
            throw new ItemNotFoundException("We don't got any " + itemId);
        }
        
        if (itemId.equals("JAM")) {
            if (_reporter != null) {
                _reporter.logWarning("Going to take a bit of time ....");
            }
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (_reporter != null) {
            _reporter.logInfo("Found the item '"+itemId+"'");
        }

        return item;
    }
}
