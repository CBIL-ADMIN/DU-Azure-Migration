package com.ericsson.dm.dubai.eit.reporting.source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.mapdb.HTreeMap;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
//...

import com.ericsson.dm.Utils.CommonUtilities;
import com.ericsson.dm.dubai.eit.reporting.init.InitializeReporting;
import com.ericsson.jibx.beans.BALANCEMAPPINGLIST.BALANCEMAPPINGINFO;
import com.ericsson.jibx.beans.DEFAULTSERVICESMAPPINGLIST.DEFAULTSERVICESMAPPINGINFO;
import com.ericsson.jibx.beans.PROFILETAGLIST.PROFILETAGINFO;
import com.ericsson.jibx.beans.SubscriberXml;
import com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo;
import com.ericsson.jibx.beans.SubscriberXml.SchemasubscribercugclidumpInfo;
import com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberprofiledumpInfo;

public class GenerateReportRejection {
	private IBindingFactory bindingFactorySubuscriber;
	private IUnmarshallingContext UnmarshallingContextSubuscriber;

	public GenerateReportRejection() {
		try {

			bindingFactorySubuscriber = BindingDirectory.getFactory(SubscriberXml.class);
			UnmarshallingContextSubuscriber = bindingFactorySubuscriber.createUnmarshallingContext();
		} catch (JiBXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// LOG.error("JIBX Exception ", e);
		}

	}

	public void generateRejectionReport(String xml) {

		SubscriberXml subsxml;
		try {
			subsxml = (SubscriberXml) UnmarshallingContextSubuscriber
					.unmarshalDocument(new ByteArrayInputStream(xml.getBytes()), null);
			ProfileTagFetching ptf = new ProfileTagFetching(subsxml);


			generateCugRejectionStatistics(subsxml);
			generateSnaRejectionStatistics(subsxml);
			generateFafRejectionStatistics(subsxml,ptf);
			generateRatePlaStateRejectionStatistics(subsxml);
			

		} catch (JiBXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void generateCugRejectionStatistics(SubscriberXml subsxml) {
		String msisdn = subsxml.getSubscriberInfoMSISDN();
		if (subsxml.getCugclidumpInfoList() != null && subsxml.getCugclidumpInfoList().size() > 0
				&& InitializeReporting.msisdn2ErrorCode.containsKey(msisdn)) {
			String errorCode = InitializeReporting.msisdn2ErrorCode.get(msisdn);
			if (InitializeReporting.rejectionStatsCugCount.containsKey(errorCode)) {
				int counter = InitializeReporting.rejectionStatsCugCount.get(errorCode);
				counter = +1;
				InitializeReporting.rejectionStatsCugCount.put(errorCode, counter);

			} else {
				InitializeReporting.rejectionStatsCugCount.put(errorCode, 1);
			}

		}
		if (subsxml.getCugclidumpInfoList() != null && subsxml.getCugclidumpInfoList().size() > 0) {
			for (SchemasubscribercugclidumpInfo cugDumpInfo : subsxml.getCugclidumpInfoList()) {
				if (InitializeReporting.msisdn2ErrorCodeForCug.containsKey(msisdn)) {
					if (InitializeReporting.rejectionUnmatchCugId.containsKey(cugDumpInfo.getCUGNAME())) {
						int count = InitializeReporting.rejectionUnmatchCugId.get(cugDumpInfo.getCUGNAME());
						count += 1;
						InitializeReporting.rejectionUnmatchCugId.put(cugDumpInfo.getCUGNAME(), count);

					} else {
						InitializeReporting.rejectionUnmatchCugId.put(cugDumpInfo.getCUGNAME(), 1);
					}

				}
			}
		}

	}

	private void generateSnaRejectionStatistics(SubscriberXml subsxml) {
		String msisdn = subsxml.getSubscriberInfoMSISDN();
		String ratePlanId = subsxml.getSubscriberInfoCCSACCTTYPEID();
		if (InitializeReporting.ratePlanIdentifiers.contains(ratePlanId)
				&& InitializeReporting.msisdn2ErrorCode.containsKey(msisdn)) {
			String errorCode = InitializeReporting.msisdn2ErrorCode.get(msisdn);
			if (InitializeReporting.rejectionStatsSnaCount.containsKey(errorCode)) {
				int counter = InitializeReporting.rejectionStatsSnaCount.get(errorCode);
				counter = +1;
				InitializeReporting.rejectionStatsSnaCount.put(errorCode, counter);

			} else {
				InitializeReporting.rejectionStatsSnaCount.put(errorCode, 1);
			}

		}
	}

	private void generateRatePlaStateRejectionStatistics(SubscriberXml subsxml) {
		String msisdn = subsxml.getSubscriberInfoMSISDN();
		String ratePlanId = subsxml.getSubscriberInfoCCSACCTTYPEID();
		String state = subsxml.getSubscriberInfoSERVICESTATE();
		if (InitializeReporting.msisdn2ErrorCode.containsKey(msisdn)
				&& "INC1001".equals(InitializeReporting.msisdn2ErrorCode.get(msisdn))) {

			if (InitializeReporting.rejectionUnmatchRatePlanCount.containsKey(ratePlanId)) {
				int count = InitializeReporting.rejectionUnmatchRatePlanCount.get(ratePlanId);
				count += 1;
				InitializeReporting.rejectionUnmatchRatePlanCount.put(ratePlanId, count);

			} else {
				InitializeReporting.rejectionUnmatchRatePlanCount.put(ratePlanId, 1);
			}

		} else if (InitializeReporting.msisdn2ErrorCode.containsKey(msisdn)
				&& "INC1002".equals(InitializeReporting.msisdn2ErrorCode.get(msisdn))) {

			if (InitializeReporting.rejectionIgnoreRatePlanCount.containsKey(ratePlanId)) {
				int count = InitializeReporting.rejectionIgnoreRatePlanCount.get(ratePlanId);
				count += 1;
				InitializeReporting.rejectionIgnoreRatePlanCount.put(ratePlanId, count);

			} else {
				InitializeReporting.rejectionIgnoreRatePlanCount.put(ratePlanId, 1);
			}

		} else if (InitializeReporting.msisdn2ErrorCode.containsKey(msisdn)
				&& "INC1004".equals(InitializeReporting.msisdn2ErrorCode.get(msisdn))) {

			if (InitializeReporting.rejectionUnmatchLfc.containsKey(state)) {
				int count = InitializeReporting.rejectionUnmatchLfc.get(state);
				count += 1;
				InitializeReporting.rejectionUnmatchLfc.put(state, count);

			} else {
				InitializeReporting.rejectionUnmatchLfc.put(state, 1);
			}

		}
	}

	private void generateFafRejectionStatistics(SubscriberXml subsxml, ProfileTagFetching ptf) {
		String msisdn = subsxml.getSubscriberInfoMSISDN();
		// String ratePlanId = subsxml.getSubscriberInfoCCSACCTTYPEID();
		String tagValue1 = ptf.GetProfileTagValue("BstrVceIntNumTree");
		String tagValue2 = ptf.GetProfileTagValue("BstrVceNatNumTree");
		if (((tagValue1 != null && tagValue1.length() > 0) || (tagValue2 != null && tagValue2.length() > 0))
				&& InitializeReporting.msisdn2ErrorCode.containsKey(msisdn)) {
			String errorCode = InitializeReporting.msisdn2ErrorCode.get(msisdn);
			if (InitializeReporting.rejectionStatsFafCount.containsKey(errorCode)) {
				int counter = InitializeReporting.rejectionStatsFafCount.get(errorCode);
				counter = +1;
				InitializeReporting.rejectionStatsFafCount.put(errorCode, counter);

			} else {
				InitializeReporting.rejectionStatsFafCount.put(errorCode, 1);
			}

		}
	}

	public void generateBtRejectionStatistics(String reportDir) {
		Map<String, Integer> consolidatedErrorCode = new HashMap<String, Integer>();
		for (String msisdn : InitializeReporting.msisdn2ErrorCode.keySet()) {
			String errorCode = InitializeReporting.msisdn2ErrorCode.get(msisdn);
			if (consolidatedErrorCode.containsKey(errorCode)) {
				int counter = consolidatedErrorCode.get(errorCode);
				counter += 1;
				consolidatedErrorCode.put(errorCode, counter);
			} else {
				int counter = 1;
				consolidatedErrorCode.put(errorCode, counter);
			}
		}
		for (String msisdnBucketId : InitializeReporting.msisdn2ErrorCodeForBalanceMapping.keySet()) {
			String errorCode = InitializeReporting.msisdn2ErrorCodeForBalanceMapping.get(msisdnBucketId);
			if (consolidatedErrorCode.containsKey(errorCode)) {
				int counter = consolidatedErrorCode.get(errorCode);
				counter += 1;
				consolidatedErrorCode.put(errorCode, counter);
			} else {
				int counter = 1;
				consolidatedErrorCode.put(errorCode, counter);
			}
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(reportDir+"/bt_rejection_stats.txt"));
			bw.write("REJECTION_REASON, REJECTION_COUNT\n");
			for (String errorCode1 : consolidatedErrorCode.keySet()) {
				String errorCodeString  = getErrorString(errorCode1);

				bw.write(errorCodeString + "," + consolidatedErrorCode.get(errorCode1) + "\n");
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getErrorString(String errorCode1) {
		String errorCodeString="";
		if (errorCode1.equals("INC1001")) {
			errorCodeString = "Service class Lookup Failed";
		} else if (errorCode1.equals("INC1002")) {
			errorCodeString = "Service class Ignored";
		} else if (errorCode1.equals("INC1003")) {
			errorCodeString = "Subscriber relation data missing";
		} else if (errorCode1.equals("INC1004")) {
			errorCodeString = "Lifecycle lookup mismatch";
		} else if (errorCode1.equals("INC4001")) {
			errorCodeString = "Balance_Type expired";
		} else if (errorCode1.equals("INC4002")) {
			errorCodeString = "Balance_Type lookup failed";
		} else if (errorCode1.equals("INC4003")) {
			errorCodeString = "Balance_Type Ignored";
		} else if (errorCode1.equals("INC4004")) {
			errorCodeString = "Balance_Type match condition failed";
		} else if (errorCode1.equals("INC4010")) {
			errorCodeString = "BT mapped attribute Lookup failed";
		}else if (errorCode1.equals("INC9002")) {
			errorCodeString = "SNA Destination number is empty";
		}else if (errorCode1.equals("INC9003")) {
			errorCodeString = "SNA State is different";
		}
		
		return errorCodeString;
	}

	public void generateSubscriberRejectionStatistics(String reportDir) {
		Map<String, Integer> consolidatedErrorCode = new HashMap<String, Integer>();
		for (String msisdn : InitializeReporting.msisdn2ErrorCode.keySet()) {
			String errorCode = InitializeReporting.msisdn2ErrorCode.get(msisdn);
			if (consolidatedErrorCode.containsKey(errorCode)) {
				int counter = consolidatedErrorCode.get(errorCode);
				counter += 1;
				consolidatedErrorCode.put(errorCode, counter);
			} else {
				int counter = 1;
				consolidatedErrorCode.put(errorCode, counter);
			}
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(reportDir+"/"+"subs_rejection_stats.txt"));
			bw.write("REJECTION_REASON, REJECTION_COUNT\n");
			for (String errorCode1 : consolidatedErrorCode.keySet()) {
				String errorCodeString = getErrorString(errorCode1);

				bw.write(errorCodeString + "," + consolidatedErrorCode.get(errorCode1) + "\n");
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void generateReports(String reportDir){
		generateSubscriberRejectionStatistics(reportDir);
		generateBtRejectionStatistics(reportDir);
		streamInput();
		writeRejectionReport(reportDir);
		
		
	}
	
	public static void main(String args[]) throws IOException {

		InitializeReporting inr = new InitializeReporting("C:/Projects/DU/database/", "C:/Projects/DU/logs/",
				"C:/Projects/DU/Input/", "meydvvmsdp33", "C:/Projects/DU/data/","","","");
		/*
		 * for(String key : InitializeReporting.msisdn2ErrorCode.keySet()){
		 * System.out.println(key
		 * +":"+InitializeReporting.msisdn2ErrorCode.get(key) ); }
		 */
		GenerateReportRejection gnr = new GenerateReportRejection();
		gnr.generateReports("C:/Projects/DU/Report/");
		gnr.streamInput();

		gnr.writeRejectionReport("C:/Projects/DU/Report/");

	}

	
	public void writeRejectionReport(String reportDir) {
		try {
			BufferedWriter bwRejCug = new BufferedWriter( new FileWriter(reportDir+"/rejection_cug.txt"));
			BufferedWriter bwRejSna= new BufferedWriter( new FileWriter(reportDir+"/rejected_sna.txt"));
			BufferedWriter bwRejFaf = new BufferedWriter( new FileWriter(reportDir+"/rejected_faf.txt"));
			BufferedWriter bwRejUnmatchedRP = new BufferedWriter( new FileWriter(reportDir+"/unmatch_rateplan.txt"));
			BufferedWriter bwRejIgnoreRP = new BufferedWriter( new FileWriter(reportDir+"/ignore_rateplan.txt"));
			BufferedWriter bwRejUnmatchLfc = new BufferedWriter( new FileWriter(reportDir+"/unmatch_lfc.txt"));
			BufferedWriter bwRejUnmatchCug = new BufferedWriter( new FileWriter(reportDir+"/unmatch_cugid.txt"));
			bwRejCug.write("REJECTION_REASON,REJECTION_COUNT\n");
			for(String errorCode:InitializeReporting.rejectionStatsCugCount.keySet()){
				String errorString = getErrorString(errorCode);
				bwRejCug.write(errorString+","+InitializeReporting.rejectionStatsCugCount.get(errorCode)+"\n");
			}
			bwRejCug.close();
			
			bwRejSna.write("REJECTION_REASON,REJECTION_COUNT\n");
			for(String errorCode:InitializeReporting.rejectionStatsSnaCount.keySet()){
				String errorString = getErrorString(errorCode);
				bwRejSna.write(errorString+","+InitializeReporting.rejectionStatsSnaCount.get(errorCode)+"\n");
			}
			bwRejSna.close();
			
			bwRejFaf.write("REJECTION_REASON,REJECTION_COUNT\n");
			for(String errorCode:InitializeReporting.rejectionStatsFafCount.keySet()){
				String errorString = getErrorString(errorCode);
				bwRejFaf.write(errorString+","+InitializeReporting.rejectionStatsFafCount.get(errorCode)+"\n");
			}
			bwRejFaf.close();
			
			bwRejUnmatchedRP.write("RATEPLAN_ID,REJECTION_COUNT\n");			
			for(String rp:InitializeReporting.rejectionUnmatchRatePlanCount.keySet()){
				//String errorString = getErrorString(errorCode);
				bwRejUnmatchedRP.write(rp+","+InitializeReporting.rejectionUnmatchRatePlanCount.get(rp)+"\n");
			}
			bwRejUnmatchedRP.close();
			
			bwRejIgnoreRP.write("RATEPLAN_ID,REJECTION_COUNT\n");
			for(String rp:InitializeReporting.rejectionIgnoreRatePlanCount.keySet()){
				//String errorString = getErrorString(errorCode);
				bwRejIgnoreRP.write(rp+","+InitializeReporting.rejectionIgnoreRatePlanCount.get(rp)+"\n");
			}
			bwRejIgnoreRP.close();
			
			bwRejUnmatchLfc.write("STATE,REJECTION_COUNT\n");
			for(String state:InitializeReporting.rejectionUnmatchLfc.keySet()){
				//String errorString = getErrorString(errorCode);
				bwRejUnmatchLfc.write(state+","+InitializeReporting.rejectionUnmatchLfc.get(state)+"\n");
			}
			bwRejUnmatchLfc.close();
			
			bwRejUnmatchCug.write("CUG_ID,REJECTION_COUNT\n");
			for(String cugid:InitializeReporting.rejectionUnmatchCugId.keySet()){
				//String errorString = getErrorString(errorCode);
				bwRejUnmatchCug.write(cugid+","+InitializeReporting.rejectionUnmatchCugId.get(cugid)+"\n");
			}
			bwRejUnmatchCug.close();
				
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void streamInput() {
		GZIPInputStream gzip;
		// List<String> listOfInput = new ArrayList<>();
		String xml = null;
		try {
			gzip = new GZIPInputStream(new FileInputStream(InitializeReporting.inputDir + "/"
					+ InitializeReporting.sdpId + "_" + "subscriber_be_dump.csv.gz"));
			BufferedReader br = new BufferedReader(new InputStreamReader(gzip));
			String line = "";
			System.out.println("Started: " + new java.util.Date());
			while ((line = br.readLine()) != null) {
				String arr[] = line.split("\\|", -1);
				String msisdn = arr[0];
				String serviceState = arr[3];
				String rateplanName = arr[1];
				String rateplanId = arr[2];
				String creationDate = arr[4];
				String expiryDate = arr[5];
				xml = getXML(msisdn, rateplanName, rateplanId, serviceState, creationDate, expiryDate);
				// System.out.println(xml);
				this.generateRejectionReport(xml);
			}
			br.close();
			System.out.println("Ended: " + new java.util.Date());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
	
	
	public String getXML(String MSISDN, String CCS_ACCT_TYPE_NAME, String CCS_ACCT_TYPE_ID, String SERVICE_STATE,
			String CREATION_DATE, String WALLET_EXPIRY) {
		HTreeMap<String, String> btree_balance = InitializeReporting.mapDbDatabase.get("schemasubscriberbalancesdump");
		HTreeMap<String, String> btree_cug = InitializeReporting.mapDbDatabase.get("schemasubscribercugclidump");
		HTreeMap<String, String> btree_usms = InitializeReporting.mapDbDatabase.get("schemasubscriberusmsdump");
		HTreeMap<String, String> btree_profile = InitializeReporting.mapDbDatabase.get("schemasubscriberprofiledump");
		HTreeMap<String, String> btree_balance01 = InitializeReporting.mapDbDatabase
				.get("schemasubscriberbalancesdump01");
		HTreeMap<String, String> btree_balance02 = InitializeReporting.mapDbDatabase
				.get("schemasubscriberbalancesdump02");
		HTreeMap<String, String> btree_balance03 = InitializeReporting.mapDbDatabase
				.get("schemasubscriberbalancesdump03");
		HTreeMap<String, String> btree_balance04 = InitializeReporting.mapDbDatabase
				.get("schemasubscriberbalancesdump04");
		// System.out.println("going to get for " + MSISDN);
		String mainxml = "<subscriber_info>" + "<SUBSCRIBER>" + "<MSISDN>" + MSISDN + "</MSISDN>"
				+ "<CCS_ACCT_TYPE_NAME>" + CCS_ACCT_TYPE_NAME + "</CCS_ACCT_TYPE_NAME>" + "<CCS_ACCT_TYPE_ID>"
				+ CCS_ACCT_TYPE_ID + "</CCS_ACCT_TYPE_ID>" + "<SERVICE_STATE>" + SERVICE_STATE + "</SERVICE_STATE>"
				+ "<CREATION_DATE>" + CREATION_DATE + "</CREATION_DATE>" + "<WALLET_EXPIRY>" + WALLET_EXPIRY
				+ "</WALLET_EXPIRY>" + "</SUBSCRIBER>" + "</subscriber_info>";
		String balancexml1 = (btree_balance != null && !btree_balance.containsKey(MSISDN)) ? btree_balance.get(MSISDN)
				: "";
		String balancexml2 = (btree_balance01 != null && btree_balance01.containsKey(MSISDN))
				? btree_balance01.get(MSISDN) : "";
		String balancexml3 = (btree_balance02 != null && btree_balance02.containsKey(MSISDN))
				? btree_balance02.get(MSISDN) : "";
		String balancexml4 = (btree_balance03 != null && btree_balance03.containsKey(MSISDN))
				? btree_balance03.get(MSISDN) : "";
		String balancexml5 = (btree_balance04 != null && btree_balance04.containsKey(MSISDN))
				? btree_balance04.get(MSISDN) : "";

		String xml_balance = "<balancesdump_info>" + balancexml1 + balancexml2 + balancexml3 + balancexml4 + balancexml5
				+ "</balancesdump_info>";
		String xml_cug = "<cugclidump_info>" + btree_cug.get(MSISDN) + "</cugclidump_info>";
		String xml_usms = "<usmsdump_info>" + btree_usms.get(MSISDN) + "</usmsdump_info>";
		String xml_profile = "<profiledump_info>" + btree_profile.get(MSISDN) + "</profiledump_info>";
		return "<subscriber_xml>" + mainxml + xml_balance + xml_cug + xml_usms + xml_profile + "</subscriber_xml>";
	}


}
