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

package org.switchyard.quickstarts.demos.orders;

import org.overlord.rtgov.jee.ActivityReporter;
import org.overlord.rtgov.jee.DefaultActivityReporter;
import org.switchyard.component.bean.Service;

@Service(LogisticsService.class)
public class LogisticsServiceBean implements LogisticsService {

    private ActivityReporter _reporter=new DefaultActivityReporter();
    
    public LogisticsServiceBean() {
    }

    @Override
    public DeliveryAck deliver(Order order) {
        if (_reporter != null) {
            _reporter.logInfo("Delivering the goods");
        }
        return (new DeliveryAck().setOrderId(order.getOrderId()));
    }
}
