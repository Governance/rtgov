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
package org.overlord.rtgov.epn.validation;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.text.MessageFormat;

import org.junit.Test;
import org.overlord.rtgov.ep.EventProcessor;
import org.overlord.rtgov.epn.Network;
import org.overlord.rtgov.epn.Node;
import org.overlord.rtgov.epn.Notification;
import org.overlord.rtgov.epn.NotificationType;
import org.overlord.rtgov.epn.Subscription;

public class EPNValidatorTest {
	
	@Test
	public void testValidateNetwork() {
		Network epn=new Network();
		epn.setName("network");
		epn.setVersion("version");
		
		TestListener l=new TestListener();
		
		if (!EPNValidator.validateNetwork(epn, l)) {
			fail("Should be valid");
		}
		
		String[] expected=new String[] {
		};
		
		validateIssues(expected, l.getIssues());
	}
	
	@Test
	public void testValidateEmptyNetwork() {
		Network epn=new Network();
		
		TestListener l=new TestListener();
		
		if (EPNValidator.validateNetwork(epn, l)) {
			fail("Should be invalid");
		}
		
		String[] expected=new String[] {
				MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
	                    "epn-core.Messages").getString("EPN-CORE-6"),
	                    "Network", "name"),
                MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
	                    "epn-core.Messages").getString("EPN-CORE-6"),
	                    "Network", "version")
		};
		
		validateIssues(expected, l.getIssues());
	}
	
	@Test
	public void testValidateSubscriptions() {
		TestNetwork epn=new TestNetwork();
		
		Subscription sub=new Subscription();
		sub.setNodeName("node");
		sub.setSubject("sub");
		epn.getSubscriptions().add(sub);
		
		Node node=new Node();
		node.setName("node");
		node.setEventProcessor(new EventProcessor() {
			public Serializable process(String source, Serializable event,
					int retriesLeft) throws Exception {
				return null;
			}			
		});
		epn.getNodes().add(node);
		
		epn.init();

		TestListener l=new TestListener();
		
		if (!EPNValidator.validateSubscriptions(epn, l)) {
			fail("Should be valid");
		}
		
		String[] expected=new String[] {
		};
		
		validateIssues(expected, l.getIssues());
	}

	@Test
	public void testValidateEmptySubscriptions() {
		Network epn=new Network();
		
		Subscription sub=new Subscription();
		epn.getSubscriptions().add(sub);
		
		TestListener l=new TestListener();
		
		if (EPNValidator.validateSubscriptions(epn, l)) {
			fail("Should be invalid");
		}
		
		String[] expected=new String[] {
				MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "epn-core.Messages").getString("EPN-CORE-6"),
                        "Subscription", "nodeName"),
                MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "epn-core.Messages").getString("EPN-CORE-6"),
                        "Subscription", "subject")
		};
		
		validateIssues(expected, l.getIssues());
	}

	@Test
	public void testValidateSubscriptionsNoDuplicate() {
		TestNetwork epn=new TestNetwork();
		
		Subscription sub1=new Subscription();
		sub1.setNodeName("n1");
		sub1.setSubject("sub1");
		epn.getSubscriptions().add(sub1);
		
		Node n1=new Node();
		n1.setName("n1");
		epn.getNodes().add(n1);
		
		Subscription sub2=new Subscription();
		sub2.setNodeName("n2");
		sub2.setSubject("sub2");
		epn.getSubscriptions().add(sub2);
		
		Node n2=new Node();
		n2.setName("n2");
		epn.getNodes().add(n2);
		
		epn.init();
		
		TestListener l=new TestListener();
		
		if (!EPNValidator.validateSubscriptions(epn, l)) {
			fail("Should be valid");
		}
		
		String[] expected=new String[] {
		};
		
		validateIssues(expected, l.getIssues());
	}

	@Test
	public void testValidateSubscriptionsDuplicate() {
		TestNetwork epn=new TestNetwork();
		
		Subscription sub1=new Subscription();
		sub1.setNodeName("n1");
		sub1.setSubject("sub1");
		epn.getSubscriptions().add(sub1);
		
		Node n1=new Node();
		n1.setName("n1");
		epn.getNodes().add(n1);
		
		Subscription sub2=new Subscription();
		sub2.setNodeName("n1");
		sub2.setSubject("sub1");
		epn.getSubscriptions().add(sub2);
		
		epn.init();
		
		TestListener l=new TestListener();
		
		if (EPNValidator.validateSubscriptions(epn, l)) {
			fail("Should be invalid");
		}
		
		String[] expected=new String[] {
				MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "epn-core.Messages").getString("EPN-CORE-5"),
                        sub1.getSubject(), sub1.getNodeName())
		};
		
		validateIssues(expected, l.getIssues());
	}

	@Test
	public void testValidateSubscriptionsNodeMissing() {
		Network epn=new Network();
		
		Subscription sub1=new Subscription();
		sub1.setNodeName("n1");
		sub1.setSubject("sub1");
		epn.getSubscriptions().add(sub1);
		
		TestListener l=new TestListener();
		
		if (EPNValidator.validateSubscriptions(epn, l)) {
			fail("Should be invalid");
		}
		
		String[] expected=new String[] {
				MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "epn-core.Messages").getString("EPN-CORE-7"),
                        sub1.getNodeName(), sub1.getSubject())
		};
		
		validateIssues(expected, l.getIssues());
	}

	@Test
	public void testValidateNode() {
		TestNetwork epn=new TestNetwork();
		
		Node node=new Node();
		node.setName("node");
		node.setEventProcessor(new EventProcessor() {
			public Serializable process(String source, Serializable event,
					int retriesLeft) throws Exception {
				return null;
			}			
		});
		epn.getNodes().add(node);
		
		epn.init();
		
		TestListener l=new TestListener();
		
		if (!EPNValidator.validateNodes(epn, l)) {
			fail("Should be valid");
		}
		
		String[] expected=new String[] {
		};
		
		validateIssues(expected, l.getIssues());
	}

	@Test
	public void testValidateEmptyNode() {
		Network epn=new Network();
		
		Node node=new Node();
		epn.getNodes().add(node);
		
		TestListener l=new TestListener();
		
		if (EPNValidator.validateNodes(epn, l)) {
			fail("Should be invalid");
		}
		
		String[] expected=new String[] {
				MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "epn-core.Messages").getString("EPN-CORE-6"),
                        "Node", "name"),
                MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "epn-core.Messages").getString("EPN-CORE-6"),
                        "Node", "eventProcessor")
		};
		
		validateIssues(expected, l.getIssues());
	}

	@Test
	public void testValidateNodeDuplicateName() {
		TestNetwork epn=new TestNetwork();
		
		Node node1=new Node();
		node1.setName("node");
		node1.setEventProcessor(new EventProcessor() {
			public Serializable process(String source, Serializable event,
					int retriesLeft) throws Exception {
				return null;
			}});
		epn.getNodes().add(node1);
		
		Node node2=new Node();
		node2.setName("node");
		node2.setEventProcessor(new EventProcessor() {
			public Serializable process(String source, Serializable event,
					int retriesLeft) throws Exception {
				return null;
			}});
		epn.getNodes().add(node2);
		
		epn.init();
		
		TestListener l=new TestListener();
		
		if (EPNValidator.validateNodes(epn, l)) {
			fail("Should be invalid");
		}
		
		String[] expected=new String[] {
				MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "epn-core.Messages").getString("EPN-CORE-8"),
                        node1.getName())
		};
		
		validateIssues(expected, l.getIssues());
	}

	@Test
	public void testValidateNodeCyclicDependencyImmediate() {
		TestNetwork epn=new TestNetwork();
		
		Node node1=new Node();
		node1.setName("node1");
		node1.setEventProcessor(new EventProcessor() {
			public Serializable process(String source, Serializable event,
					int retriesLeft) throws Exception {
				return null;
			}});
		node1.getSourceNodes().add(node1.getName());
		epn.getNodes().add(node1);
		
		epn.init();
		
		TestListener l=new TestListener();
		
		if (EPNValidator.validateNodes(epn, l)) {
			fail("Should be invalid");
		}
		
		String[] expected=new String[] {
				MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
	                    "epn-core.Messages").getString("EPN-CORE-10"),
	                    "node1->node1")
		};
		
		validateIssues(expected, l.getIssues());
	}

	@Test
	public void testValidateNodeCyclicDependencyIndirect() {
		TestNetwork epn=new TestNetwork();
		
		Node node0=new Node();
		node0.setName("node0");
		node0.setEventProcessor(new EventProcessor() {
			public Serializable process(String source, Serializable event,
					int retriesLeft) throws Exception {
				return null;
			}});
		
		Node node1=new Node();
		node1.setName("node1");
		node1.setEventProcessor(new EventProcessor() {
			public Serializable process(String source, Serializable event,
					int retriesLeft) throws Exception {
				return null;
			}});
		
		Node node2=new Node();
		node2.setName("node2");
		node2.setEventProcessor(new EventProcessor() {
			public Serializable process(String source, Serializable event,
					int retriesLeft) throws Exception {
				return null;
			}});

		Node node3=new Node();
		node3.setName("node3");
		node3.setEventProcessor(new EventProcessor() {
			public Serializable process(String source, Serializable event,
					int retriesLeft) throws Exception {
				return null;
			}});

		node0.getSourceNodes().add(node2.getName());
		epn.getNodes().add(node0);

		node1.getSourceNodes().add(node0.getName());
		epn.getNodes().add(node1);

		node2.getSourceNodes().add(node1.getName());
		epn.getNodes().add(node2);
		
		node3.getSourceNodes().add(node2.getName());
		epn.getNodes().add(node3);
		
		epn.init();
		
		TestListener l=new TestListener();
		
		if (EPNValidator.validateNodes(epn, l)) {
			fail("Should be invalid");
		}
		
		String[] expected=new String[] {
				MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
	                    "epn-core.Messages").getString("EPN-CORE-10"),
	                    "node1->node0->node2->node1"),
				MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
	                    "epn-core.Messages").getString("EPN-CORE-10"),
	                    "node0->node2->node1->node0"),
				MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
	                    "epn-core.Messages").getString("EPN-CORE-10"),
	                    "node2->node1->node0->node2")
		};
		
		validateIssues(expected, l.getIssues());
	}

	@Test
	public void testValidateNodeSourceExists() {
		TestNetwork epn=new TestNetwork();
		
		Node node1=new Node();
		node1.setName("node1");
		node1.setEventProcessor(new EventProcessor() {
			public Serializable process(String source, Serializable event,
					int retriesLeft) throws Exception {
				return null;
			}});
		epn.getNodes().add(node1);
		
		Node node2=new Node();
		node2.setName("node2");
		node2.setEventProcessor(new EventProcessor() {
			public Serializable process(String source, Serializable event,
					int retriesLeft) throws Exception {
				return null;
			}});
		node2.getSourceNodes().add("node1");
		epn.getNodes().add(node2);
		
		epn.init();
		
		TestListener l=new TestListener();
		
		if (!EPNValidator.validateNodes(epn, l)) {
			fail("Should be valid");
		}
		
		String[] expected=new String[] {
		};
		
		validateIssues(expected, l.getIssues());
	}

	@Test
	public void testValidateNodeSourceDoesNotExist() {
		TestNetwork epn=new TestNetwork();
		
		Node node1=new Node();
		node1.setName("node");
		node1.setEventProcessor(new EventProcessor() {
			public Serializable process(String source, Serializable event,
					int retriesLeft) throws Exception {
				return null;
			}});
		node1.getSourceNodes().add("other");
		epn.getNodes().add(node1);
		
		epn.init();
		
		TestListener l=new TestListener();
		
		if (EPNValidator.validateNodes(epn, l)) {
			fail("Should be invalid");
		}
		
		String[] expected=new String[] {
				MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "epn-core.Messages").getString("EPN-CORE-9"),
                        "other", node1.getName())
		};
		
		validateIssues(expected, l.getIssues());
	}

	@Test
	public void testValidateEmptyNotification() {
		Network epn=new Network();
		
		Node node=new Node();
		epn.getNodes().add(node);
		
		Notification not=new Notification();
		node.getNotifications().add(not);
		
		TestListener l=new TestListener();
		
		if (EPNValidator.validateNotifications(epn, node, l)) {
			fail("Should be invalid");
		}
		
		String[] expected=new String[] {
				MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "epn-core.Messages").getString("EPN-CORE-11"),
                        "Notification", "subject", node.getName()),
                MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "epn-core.Messages").getString("EPN-CORE-11"),
                        "Notification", "type", node.getName())
		};
		
		validateIssues(expected, l.getIssues());
	}

	@Test
	public void testValidateNotification() {
		Network epn=new Network();
		
		Node node=new Node();
		epn.getNodes().add(node);
		
		Notification not=new Notification();
		not.setType(NotificationType.Processed);
		not.setSubject("sub");
		node.getNotifications().add(not);
		
		TestListener l=new TestListener();
		
		if (!EPNValidator.validateNotifications(epn, node, l)) {
			fail("Should be valid");
		}
		
		String[] expected=new String[] {
		};
		
		validateIssues(expected, l.getIssues());
	}

	protected void validateIssues(String[] expected, java.util.List<String> issues) {
		String errmsg="";
		
		for (String exp : expected) {
			if (!issues.remove(exp)) {
				errmsg += "    -> Expected issue missing: "+exp+"\r\n";
			}
		}
		
		for (String issue : issues) {
			errmsg += "    -> Unxpected issue: "+issue+"\r\n";
		}
		
		if (errmsg.length() > 0) {
			fail("Errors detected:\r\n"+errmsg);
		}
	}

	public class TestListener implements EPNValidationListener {

		private java.util.List<String> _issues=new java.util.Vector<String>();
		
		public void error(Network epn, Object target, String issue) {
			_issues.add(issue);
		}
		
		public java.util.List<String> getIssues() {
			return (_issues);
		}
	}
	
	public class TestNetwork extends Network {
		
		public void init() {
			super.initNameMap();
		}
	}
}
