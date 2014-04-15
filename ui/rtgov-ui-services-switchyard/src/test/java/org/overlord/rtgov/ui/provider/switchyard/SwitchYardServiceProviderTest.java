package org.overlord.rtgov.ui.provider.switchyard;

import static org.junit.Assert.*;

import org.junit.Test;

public class SwitchYardServiceProviderTest {

	private static final String HTTP_TEST1 = "http://test1";
	private static final String HTTP_TEST2 = "http://test2";
	private static final String HTTP_TEST3 = "http://test3";

	@Test
	public void testGetDefaultURL() {
		SwitchYardServicesProvider handler=new SwitchYardServicesProvider();
		
		handler.setServerURLs(""); // Prevent it accessing rtgov properties, which in old
									// version attempts to use jndi
		
		java.util.List<String> urls=handler.getURLList();
		
		if (urls == null) {
			fail("Null URL list");
		}
		
		if (urls.size() != 1) {
			fail("Only 1 URL expected: "+urls.size());
		}
		
		if (!urls.get(0).equals(SwitchYardServicesProvider.DEFAULT_REMOTE_INVOKER_URL)) {
			fail("URL not expected: "+urls.get(0));
		}
	}

	@Test
	public void testGetSingleURL() {
		SwitchYardServicesProvider handler=new SwitchYardServicesProvider();
		
		handler.setServerURLs(HTTP_TEST1);
		
		java.util.List<String> urls=handler.getURLList();
		
		if (urls == null) {
			fail("Null URL list");
		}
		
		if (urls.size() != 1) {
			fail("Only 1 URL expected: "+urls.size());
		}
		
		if (!urls.get(0).equals(HTTP_TEST1)) {
			fail("URL not expected: "+urls.get(0));
		}
	}

	@Test
	public void testGetDoubleURLNoSpace() {
		SwitchYardServicesProvider handler=new SwitchYardServicesProvider();
		
		handler.setServerURLs(HTTP_TEST1+","+HTTP_TEST2);
		
		java.util.List<String> urls=handler.getURLList();
		
		if (urls == null) {
			fail("Null URL list");
		}
		
		if (urls.size() != 2) {
			fail("Expecting 2 URLs, got: "+urls.size());
		}
		
		if (!urls.get(0).equals(HTTP_TEST1)) {
			fail("URL1 not expected: "+urls.get(0));
		}
		
		if (!urls.get(1).equals(HTTP_TEST2)) {
			fail("URL2 not expected: "+urls.get(1));
		}
	}

	@Test
	public void testGetDoubleURLSpaces() {
		SwitchYardServicesProvider handler=new SwitchYardServicesProvider();
		
		handler.setServerURLs(HTTP_TEST1+" , "+HTTP_TEST2+" ");
		
		java.util.List<String> urls=handler.getURLList();
		
		if (urls == null) {
			fail("Null URL list");
		}
		
		if (urls.size() != 2) {
			fail("Expecting 2 URLs, got: "+urls.size());
		}
		
		if (!urls.get(0).equals(HTTP_TEST1)) {
			fail("URL1 not expected: "+urls.get(0));
		}
		
		if (!urls.get(1).equals(HTTP_TEST2)) {
			fail("URL2 not expected: "+urls.get(1));
		}
	}

	@Test
	public void testGetURLListRoundRobin() {
		SwitchYardServicesProvider handler=new SwitchYardServicesProvider();
		
		handler.setServerURLs(HTTP_TEST1+","+HTTP_TEST2+","+HTTP_TEST3);
		
		java.util.List<String> urls1=handler.getURLList();
		
		if (urls1 == null) {
			fail("Null URL list");
		}
		
		if (urls1.size() != 3) {
			fail("Expecting 3 URLs, got: "+urls1.size());
		}
		
		if (!urls1.get(0).equals(HTTP_TEST1)) {
			fail("URL1 not expected: "+urls1.get(0));
		}
		
		if (!urls1.get(1).equals(HTTP_TEST2)) {
			fail("URL2 not expected: "+urls1.get(1));
		}
		
		if (!urls1.get(2).equals(HTTP_TEST3)) {
			fail("URL3 not expected: "+urls1.get(2));
		}
		
		java.util.List<String> urls2=handler.getURLList();
		
		if (urls2 == null) {
			fail("2nd Null URL list");
		}
		
		if (urls2.size() != 3) {
			fail("2nd URL list expecting 3 URLs, got: "+urls2.size());
		}
		
		if (!urls2.get(0).equals(HTTP_TEST2)) {
			fail("2nd URL2 not expected: "+urls2.get(0));
		}
		
		if (!urls2.get(1).equals(HTTP_TEST3)) {
			fail("2nd URL3 not expected: "+urls2.get(1));
		}
		
		if (!urls2.get(2).equals(HTTP_TEST1)) {
			fail("2nd URL1 not expected: "+urls2.get(2));
		}
		
		java.util.List<String> urls3=handler.getURLList();
		
		if (urls3 == null) {
			fail("3rd Null URL list");
		}
		
		if (urls3.size() != 3) {
			fail("3rd URL list expecting 3 URLs, got: "+urls2.size());
		}
		
		if (!urls3.get(0).equals(HTTP_TEST3)) {
			fail("3rd URL3 not expected: "+urls3.get(0));
		}
		
		if (!urls3.get(1).equals(HTTP_TEST1)) {
			fail("3rd URL1 not expected: "+urls3.get(1));
		}
		
		if (!urls3.get(2).equals(HTTP_TEST2)) {
			fail("3rd URL2 not expected: "+urls3.get(2));
		}
	}
}
