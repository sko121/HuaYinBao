package com.thtfit.pos.conn;

/*
 *	  @brief Tests a SSL/TLS connection to the Apriva server
 *	  @date 09/22/2016
 *	  @version 1.3
 *
 * 	  Copyright (c) 2016 by Apriva
 *	  All rights reserved
 *
 *	  CONFIDENTIAL AND PROPRIETARY
 *
 *	  This software may contain confidential and trade secret
 *	  information and technology and may not be used, disclosed or
 *	  made available to others without the permission of Apriva.
 *	  Copies may only be made with permission and must contain the
 *	  above copyright notice. Neither title to the software nor
 *	  ownership of the software is hereby transferred.
 *************************************************************************/
import java.io.*;
import java.security.*;
import java.util.Hashtable;

import javax.net.ssl.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.dspread.xpos.QPOSService.AmountType;

import android.R.string;
import android.content.Context;
import android.util.Log;

public class AprivaConn {
	
	private Context mContext;
	private Hashtable<String, String> decodeData;
	private Hashtable<String, String> otherInfo;
	
	public AprivaConn(Context mContext,Hashtable<String, String> decodeData,Hashtable<String, String> otherInfo) {
		super();
		this.mContext = mContext;
		this.decodeData = decodeData;
		this.otherInfo = otherInfo;
	}

	// Create an SSL socket factory to use to connect to the Apriva server with
	// Read the appropriate certificate chains and keys from files into the SSL factory
	protected javax.net.ssl.SSLSocketFactory createSSLFactory () {
		Log.i("POS_LOG", "AprivaConn:createSSLFactory");
		try {
			// *** Client Side Certificate *** //
			System.out.println ("2. Loading p12 file");

			// Load the certificate file into the keystore
			KeyStore keystore = KeyStore.getInstance("BKS");
			InputStream inputFile = mContext.getAssets().open(clientCertFileName);

			char [] clientPassphrase = clientCertPassword.toCharArray ();
			keystore.load (inputFile, clientPassphrase);

			// Create the factory
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
			keyManagerFactory.init (keystore, clientPassphrase);

			//The following section demonstrates how to configure the server trust for production.
			//It is not required for test environments and that is why the code is commented out.
			//Each line required will have the term "JKS line needed for production" following it.
			//The AprivaTrust.jks file included in this project can be used for production.
			
			// *** Server Trust *** //
			//System.out.println ("3. Loading JKS file");
			//KeyStore truststore = KeyStore.getInstance("JKS"); //JKS line needed for production
			//FileInputStream trustInputFile = new FileInputStream (serverTrustFileName); //JKS line needed for production

			//char [] serverTrustPassphrase = serverTrustPassword.toCharArray (); //JKS line needed for production
			//truststore.load (trustInputFile, serverTrustPassphrase); //JKS line needed for production

			//TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()); //JKS line needed for production
			//tmf.init (truststore); //JKS line needed for production

			//TrustManager[] trustManagers = tmf.getTrustManagers (); //JKS line needed for production

			// Create the SSL context and use it to initialize the factory
			SSLContext ctx = SSLContext.getInstance("TLS");
			//ctx.init (keyManagerFactory.getKeyManagers(), trustManagers, null); //JKS line needed for production
			ctx.init (keyManagerFactory.getKeyManagers(), null, null); //This line should be removed in production, the line above replaces it

			SSLSocketFactory sslFactory = ctx.getSocketFactory();
			return sslFactory;

		} catch (Exception e) {

			e.printStackTrace ();
		}

		return null;
	}

	// Perform the test by connecting to the Apriva server
	protected String test (String host, int port) {
		Log.i("POS_LOG", "AprivaConn:test()");

		try {
			// Create an SSL factory and use it to create an SSL socket
			SSLSocketFactory sslFactory = createSSLFactory ();

			System.out.println ("4. Connecting to " + host +  " port " + port);
			SSLSocket socket = (SSLSocket) sslFactory.createSocket (host, port);

			// Connect
			socket.startHandshake();

			// Send the XML request to the server
			OutputStream outputstream = socket.getOutputStream();
			OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream);

			BufferedWriter bufferedWriter = new BufferedWriter(outputstreamwriter);
			
			/***
			 * Transaction Number – Stan
			 * 
			***/
			
			//get ksn/enctrack2 etc information from decodeData 
			//pass it to testXML
			DUKPK2009_CBC.ksn = decodeData.get("trackksn");
			String result = "";
			
			//i don't know stan(Transaction Number),so use a random number between 0-100000 when testing
			int stan = (int)(Math.random() * 100000);
//			String cardNumber = DUKPK2009_CBC.getCardNumber(decodeData.get("encTrack2"));
			String cardNumber = "";
			
			String  Amount =  otherInfo.get("amount") ;
			String track2 = decodeData.get("encTrack2");
			String ksn = decodeData.get("trackksn");
			String keyID = decodeData.get("formatID");
			
			//generate expiredate
			String ExpireDate = decodeData.get("expiryDate").substring(0,2) + "/" + decodeData.get("expiryDate").substring(2,4);
			
			System.out.println ("expiredate:" + decodeData.get("expiryDate") + "\n");
			System.out.println ("expiredate:" + ExpireDate + "\n");
			System.out.println ("cardnumber::" + cardNumber + "\n");
			
			String formatID = decodeData.get("formatID");
			if (formatID.equals("31") || formatID.equals("40") || formatID.equals("37") || formatID.equals("17")
					|| formatID.equals("11") || formatID.equals("10")) {
				return "";
			}else {
				result +=  "CardHolder:" + decodeData.get("cardholderName") + "\n";
				result +=  "CardNumber:" + DUKPK2009_CBC.getCardNumber(decodeData.get("encTrack2")) + "\n\n";
				result += "***************Response from Apriva****************\n";
			}
			
			//the following value for test...
//			ExpireDate = "17/08"; //java test
//			cardNumber = "4111111111111112";//java test
//			cardNumber = "4003000123456781";
			cardNumber = decodeData.get("maskedPAN");
//			ksn = "01116062000001E00024";
//			track2 = "42325DEB54A724297D8C93334A9B54B3C7471064A03B8BE9";
//			cardNumber = "5499990123456781";
//			stan = 99913;
			
//			String testXML =
//					"<AprivaPosXml DeviceAddress=\"7771314\">"
//					+ "<Credit MessageType=\"Request\" Version=\"5.0\" ProcessingCode=\"Sale\">"
//					+ "<Stan>"+ stan + "</Stan>"
//					+ "<CardPresent>YES</CardPresent>"
//					+ "<EntryMode>Manual</EntryMode>"
//					+ "<EntryModeType>Standard</EntryModeType>"
//					+ "<ExpireDate>" + ExpireDate + "</ExpireDate>"
//					+ "<Amount>" + Amount + "</Amount>"
//					+ "<AccountNumber>" +  cardNumber+ "</AccountNumber>"
//					+ "</Credit></AprivaPosXml>";
			
			String testXML =
					"<AprivaPosXml DeviceAddress=\"0000000008\">" 
							+ "<Credit MessageType=\"Request\" Version=\"5.0\" ProcessingCode=\"Sale\">"
								+ "<Amount>" + String.valueOf(Double.valueOf(Amount)*0.01) + "</Amount>"
								+ "<Stan>"+ stan + "</Stan>"
								+ "<EntryModeType>Standard</EntryModeType>"
								+ "<EntryMode>Track2</EntryMode>"
								+ "<EncryptedCardDataEncoding>ASCIIISOBCD</EncryptedCardDataEncoding>"
								+ "<CardInfo IsEncrypted=\"1\">"
									+"<CardData>"
										+"<EncryptedCardDataKeyID>" + "11" + "</EncryptedCardDataKeyID>"
										+"<EncryptedCardDataKsn>" + ksn + "</EncryptedCardDataKsn>"
										+"<EncryptedCardDataEncryptedData>"+ track2 + "</EncryptedCardDataEncryptedData>"
										+"<EncryptedCardDataMaskedPAN>" +  cardNumber+ "</EncryptedCardDataMaskedPAN>"
									+"</CardData>"
								+"</CardInfo>"
							+ "</Credit>"
					+ "</AprivaPosXml>";
			
			
//			String testXML =
//					<AprivaPosXml DeviceAddress="0000000008">
//			  <Credit MessageType="Request" Version="5.0" ProcessingCode="Sale">
//			    <Amount>1.23</Amount>
//			    <Stan>12</Stan>
//			    <EntryModeType>Standard</EntryModeType>
//			    <EntryMode>Track2</EntryMode>
//			    <EncryptedCardDataEncoding>ASCIIISOBCD</EncryptedCardDataEncoding>
//			    <CardInfo IsEncrypted="1">
//			      <CardData>
//			        <EncryptedCardDataKeyID>11</EncryptedCardDataKeyID>
//			        <EncryptedCardDataKsn>01126062000001E00003</EncryptedCardDataKsn>
//			        <EncryptedCardDataEncryptedData>61C91A928FD9CDED76D2B13162FD2CFF402621FE45C57DAB</EncryptedCardDataEncryptedData>
//			        <EncryptedCardDataMaskedPAN>549999XXXXXX6781</EncryptedCardDataMaskedPAN>
//			      </CardData>
//			    </CardInfo>
//			    <CardPresent>YES</CardPresent>
//			  </Credit>
//			</AprivaPosXml>
			
//			String testXML = "<AprivaPosXml DeviceAddress=\"7771314\"><Credit MessageType=\"Request\" Version=\"5.0\" ProcessingCode=\"Sale\"><Stan>5</Stan><CardPresent>YES</CardPresent><EntryMode>Manual</EntryMode><EntryModeType>Standard</EntryModeType><ExpireDate>17/08</ExpireDate><Amount>123.00</Amount><AccountNumber>;</AccountNumber></Credit></AprivaPosXml>";

			System.out.println ("5. Sending Request --->>>>>>");
			System.out.println (formatPrettyXML(testXML));
			
			System.out.println("amount == " + String.valueOf(Double.valueOf(Amount)*0.01));
			bufferedWriter.write (testXML);
			bufferedWriter.flush ();

			System.out.println ("6. Waiting for Response <<<<<<--------");
			InputStream inputstream = socket.getInputStream();
			InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
			BufferedReader bufferedReader = new BufferedReader(inputstreamreader);
			
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(formatPrettyXML(line));
				result += formatPrettyXML(line);
			}
			inputstream.close();
			outputstream.close();
			socket.close();
			sslFactory = null;
			
			return result;

		} catch (Exception e) {
			e.printStackTrace ();
			return null;
		}
		
	}

	protected static String formatPrettyXML(String unformattedXML) {
		String prettyXMLString = null;
		
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StreamResult result = new StreamResult(new StringWriter());
			StreamSource source = new StreamSource(new StringReader(unformattedXML));
			transformer.transform(source, result);
			prettyXMLString = result.getWriter().toString();			
		} catch (TransformerConfigurationException e) {
			System.out.println("Unable to transform XML " + e.getMessage());
		} catch (TransformerFactoryConfigurationError e) {
			System.out.println("Unable to transform XML " + e.getMessage());
		} catch (TransformerException e) {
			System.out.println("Unable to transform XML " + e.getMessage());
		}
		
		return prettyXMLString;
	}
	
	// Main Function (EntryPoint)
	public  String connect() throws IOException
	{	
		
		// Display the current local directory
		String current = new java.io.File( "." ).getCanonicalPath();
	    System.out.println("Current dir: "+current);
		
		String HostName = "aibapp53.aprivaeng.com";
		String HostPort = "11098";
		
		// The file containing the client certificate, private key, and chain
		clientCertFileName = "cert/AprivaDeveloperBKS.p12";
		clientCertPassword = "P@ssword";

		// The file containing the server trust chain
		serverTrustFileName = "cert/AprivaTrust.jks";
		serverTrustPassword = "P@ssword";

		String host = HostName;
		int port = Integer.parseInt(HostPort);
		System.out.println ("Java Sample App v1.2 - AIB .53");
		System.out.println ("1. Running Test");
		return test (host, port);
	}

	
	static String clientCertFileName;
	static String clientCertPassword;
	static String serverTrustFileName;
	static String serverTrustPassword;
	
}



