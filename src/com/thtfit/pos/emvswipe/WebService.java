package com.thtfit.pos.emvswipe;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WebService {
	private static String NAMESPACE = "http://tempuri.org/";
	private static String URL = "https://developer.bbpos.com/bbdevservice.asmx";
	private static String SOAP_ACTION = "http://tempuri.org/";
	
	public static String invokeGetAutoConfigString(String manf, String model, String apiVersion, String webMethName) {
		String resTxt = null;

		// Create request
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		// Property which holds input parameters
		PropertyInfo manfProperty = new PropertyInfo();
		// Set Name
		manfProperty.setName("manf");
		// Set Value
		manfProperty.setValue(manf);
		// Set dataType
		manfProperty.setType(String.class);
		
		PropertyInfo modelProperty = new PropertyInfo();
		modelProperty.setName("model");
		modelProperty.setValue(model);
		modelProperty.setType(String.class);
		
		PropertyInfo apiVersionProperty = new PropertyInfo();
		apiVersionProperty.setName("apiVersion");
		apiVersionProperty.setValue(apiVersion);
		apiVersionProperty.setType(String.class);
		
		// Add the property to request object
		request.addProperty(manfProperty);
		request.addProperty(modelProperty);
		request.addProperty(apiVersionProperty);
		
		// Create envelope
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		// Set output SOAP object
		envelope.setOutputSoapObject(request);
		// Create HTTP call object
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			// Invole web service
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			// Get the response
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			// Assign it to fahren static variable
			resTxt = response.toString();//null

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		} 
		
		return resTxt;
	}	
}
