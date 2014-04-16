package com.noisyroads.bind;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import oracle.soa.api.JNDIDirectConnectionFactory;
import oracle.soa.api.XMLMessageFactory;
import oracle.soa.api.invocation.DirectConnection;
import oracle.soa.api.invocation.DirectConnectionFactory;
import oracle.soa.api.invocation.FaultException;
import oracle.soa.api.invocation.InvocationException;
import oracle.soa.api.message.Message;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Order {
	
	private String serviceAddress = "soadirect:/default/Order!1.0/OrderDService";

	private DirectConnectionFactory factory = JNDIDirectConnectionFactory.newInstance();

	private DirectConnection dc = null;
	
	@Before
	public void setUp() throws Exception {

		Map<String, Object> props = new HashMap<String, Object>();
		props.put(Context.INITIAL_CONTEXT_FACTORY,
				"weblogic.jndi.WLInitialContextFactory");
		props.put(Context.PROVIDER_URL, "t3://localhost:7001/soa-infra");
		props.put(Context.SECURITY_PRINCIPAL, "weblogic");
		props.put(Context.SECURITY_CREDENTIALS, "welcome1");

		dc = factory.createConnection(serviceAddress, props);

	}

	@After
	public void tearDown() throws Exception {
		dc.close();
	}

	@Test
	public void test(){
		
		try {

			String operation = "receiveOrder";

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Document doc;

			doc = dbf.newDocumentBuilder().parse(
					new InputSource(new StringReader(receiveOrder)));

			Map<String, Element> payload = new HashMap<String, Element>();
			payload.put("parameters", doc.getDocumentElement());

			Message<Element> request = XMLMessageFactory.getInstance()
					.createMessage(payload);

			Map<String, Element> payloadResp = dc.request(operation, request).getPayload().getData();
			
			Element pay = payloadResp.get("payload");
			
			System.out.println("submitOrder: " + pay.getTextContent().trim());
			
			Assert.assertEquals("OK", pay.getTextContent().trim());

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (InvocationException e) {
			e.printStackTrace();
		} catch (FaultException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private static String receiveOrder =  "<ord:request xmlns:ord='http://www.noisyroads/Order/' xmlns:ord1='http://www.noisyroads.com/Order'> " +
			"<ord1:creditCardType>VISA</ord1:creditCardType> " +
			"<ord1:creditCardNumber>1234567890</ord1:creditCardNumber> " +
			"<ord1:value>100.00</ord1:value> " +
			"<ord1:product>Ipod</ord1:product> " +
			"<ord1:quantity>2</ord1:quantity> " +
			"</ord:request>";
	
	

}
