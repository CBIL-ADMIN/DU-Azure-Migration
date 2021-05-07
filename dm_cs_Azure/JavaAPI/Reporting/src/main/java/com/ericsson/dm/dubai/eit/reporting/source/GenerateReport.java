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

public class GenerateReport {
	private IBindingFactory bindingFactorySubuscriber;
	private IUnmarshallingContext UnmarshallingContextSubuscriber;

	public GenerateReport() {
		try {

			bindingFactorySubuscriber = BindingDirectory.getFactory(SubscriberXml.class);
			UnmarshallingContextSubuscriber = bindingFactorySubuscriber.createUnmarshallingContext();
		} catch (JiBXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// LOG.error("JIBX Exception ", e);
		}

	}

	public void generateSourceReport(String xml) {

		SubscriberXml subs;
		try {
			subs = (SubscriberXml) UnmarshallingContextSubuscriber
					.unmarshalDocument(new ByteArrayInputStream(xml.getBytes()), null);

			String msisdn = subs.getSubscriberInfoMSISDN();
			String serviceState = subs.getSubscriberInfoSERVICESTATE();
			String rateplanName = subs.getSubscriberInfoCCSACCTTYPENAME();
			String rateplanId = subs.getSubscriberInfoCCSACCTTYPEID();
			ProfileTagFetching pft = new ProfileTagFetching(subs);
			// String creationDate = subs.getSubscriberInfoCREATIONDATE();
			// String expiryDate = subs.getSubscriberInfoWALLETEXPIRY();
			Map<String, List<SchemasubscriberbalancesdumpInfo>> mapOfBalanceId2BucketValue = new HashMap<String, List<SchemasubscriberbalancesdumpInfo>>();
			//Map<String, String> mapOfProfileId2Value = new HashMap<String, String>();
			ProfileTagFetching pftmapping = new ProfileTagFetching(subs);
			storeBalanceInformationBasedOnBalanceId(subs, mapOfBalanceId2BucketValue);
			//storeProfileInformationBasedOnProfileId(subs, mapOfProfileId2Value);
			generateGenericReport(subs, serviceState, rateplanName, rateplanId, mapOfBalanceId2BucketValue);
			generateReportFromBalanceMapping(msisdn, mapOfBalanceId2BucketValue, rateplanId, pft);
			generateReportFromProfileTagMapping(msisdn, subs, xml,pftmapping);
			generateReportFromDefaultServices(msisdn, subs, xml,pftmapping);
			
			
			if (InitializeReporting.msisdn2ErrorCode.containsKey(msisdn)) {
				String errorCode = InitializeReporting.msisdn2ErrorCode.get(msisdn);
				// System.out.println("MSISDN2ERRORCODE:"+ msisdn
				// +","+errorCode);
				generateGenericDiscardedReport(subs, serviceState, rateplanName, rateplanId, errorCode,
						mapOfBalanceId2BucketValue);
			} // else {

			// }
		} catch (JiBXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*private void storeProfileInformationBasedOnProfileId(SubscriberXml subs, Map<String, String> mapOfProfileId2Value) {
		for (SchemasubscriberprofiledumpInfo profileInfo : subs.getProfiledumpInfoList()) {
			mapOfProfileId2Value.put("BlckBrryAct", profileInfo.getBlckBrryAct());
			mapOfProfileId2Value.put("BlckBrryCnfdAct", profileInfo.getBlckBrryCnfdAct());
			mapOfProfileId2Value.put("PAYGMet", profileInfo.getPAYGMet());
			mapOfProfileId2Value.put("SmsBndlType", profileInfo.getSmsBndlType());
			mapOfProfileId2Value.put("SmsBndl1Recur", profileInfo.getSmsBndl1Recur());
			mapOfProfileId2Value.put("MBB", profileInfo.getMBB());
			mapOfProfileId2Value.put("MBBUnlimited", profileInfo.getMBBUnlimited());
			mapOfProfileId2Value.put("MBB2GB", profileInfo.getMBB2GB());
			mapOfProfileId2Value.put("MBB10GB", profileInfo.getMBB10GB());
			mapOfProfileId2Value.put("PAYGDataWH", profileInfo.getPAYGDataWH());
			mapOfProfileId2Value.put("PAYGDataWHSP", profileInfo.getPAYGDataWHSP());
			mapOfProfileId2Value.put("PAYGDataLineOffer", profileInfo.getPAYGDataLineOffer());
			mapOfProfileId2Value.put("BstrVNNRecur", profileInfo.getBstrVNNRecur());
			mapOfProfileId2Value.put("IDD2Act", profileInfo.getIDD2Act());
			mapOfProfileId2Value.put("BstrVINRecur", profileInfo.getBstrVINRecur());
			mapOfProfileId2Value.put("MBBGraceAct", profileInfo.getMBBGraceAct());
			mapOfProfileId2Value.put("BlckBrryBundle", profileInfo.getBlckBrryBundle());
			mapOfProfileId2Value.put("CVM", profileInfo.getCVM());
			mapOfProfileId2Value.put("NewPPBundle", profileInfo.getNewPPBundle());
			mapOfProfileId2Value.put("Absher", profileInfo.getAbsher());
			mapOfProfileId2Value.put("Bespoke", profileInfo.getBespoke());
			mapOfProfileId2Value.put("DataGraceAct", profileInfo.getDataGraceAct());
			mapOfProfileId2Value.put("EmiratiPlan", profileInfo.getEmiratiPlan());
			mapOfProfileId2Value.put("entBsnssCrclActv", profileInfo.getEntBsnssCrclActv());
			mapOfProfileId2Value.put("IDDCutRateAct", profileInfo.getIDDCutRateActDate());
			mapOfProfileId2Value.put("MBB10GBWelcome", profileInfo.getMBB10GBWelcome());
			mapOfProfileId2Value.put("MBB2GBWelcome", profileInfo.getMBB2GBWelcome());
			mapOfProfileId2Value.put("MBBUnlimWelcome", profileInfo.getMBBUnlimWelcome());
			mapOfProfileId2Value.put("MBBWelcome", profileInfo.getMBBWelcome());
			mapOfProfileId2Value.put("Prepaid", profileInfo.getPrepaid());
			mapOfProfileId2Value.put("TopXCountr1", profileInfo.getTopXCountr1());
			mapOfProfileId2Value.put("TopXCountr2", profileInfo.getTopXCountr2());
			mapOfProfileId2Value.put("TopXCountr3", profileInfo.getTopXCountr3());
			mapOfProfileId2Value.put("TopXCountr4", profileInfo.getTopXCountr4());
			mapOfProfileId2Value.put("TopXCountr5", profileInfo.getTopXCountr5());
			mapOfProfileId2Value.put("TP_Social_Deact_Conf", profileInfo.getTPSocialDeactConf());
			mapOfProfileId2Value.put("Plan", profileInfo.getPlan());
			mapOfProfileId2Value.put("CVMCounter", profileInfo.getCVMCounter());
			mapOfProfileId2Value.put("ManRenDateLess1Y", profileInfo.getManRenDateLess1Y());
			mapOfProfileId2Value.put("MBBOfferExpDate", profileInfo.getMBBOfferExpDate());
			mapOfProfileId2Value.put("guiBalAdjCount", profileInfo.getGuiBalAdjCount());
		}
	}*/

	private void storeBalanceInformationBasedOnBalanceId(SubscriberXml subs,
			Map<String, List<SchemasubscriberbalancesdumpInfo>> mapOfBalanceId2BucketValue) {
		for (SchemasubscriberbalancesdumpInfo balancesinfo : subs.getBalancesdumpInfoList()) {
			String balanceType = balancesinfo.getBALANCETYPE();
			String balanceTypeName = balancesinfo.getBALANCETYPENAME();
			// String bucketValue = balancesinfo.getBEBUCKETVALUE();
			String key = balanceType + "@@" + balanceTypeName;
			if (mapOfBalanceId2BucketValue.containsKey(key)) {
				List<SchemasubscriberbalancesdumpInfo> listOfValues = mapOfBalanceId2BucketValue.get(key);
				listOfValues.add(balancesinfo);
				mapOfBalanceId2BucketValue.put(key, listOfValues);
			} else {
				List<SchemasubscriberbalancesdumpInfo> listOfValues = new ArrayList<SchemasubscriberbalancesdumpInfo>();
				listOfValues.add(balancesinfo);
				mapOfBalanceId2BucketValue.put(key, listOfValues);
			}
		}
	}

	private void generateGenericReport(SubscriberXml subs, String serviceState, String rateplanName, String rateplanId,
			Map<String, List<SchemasubscriberbalancesdumpInfo>> mapOfBalanceId2BucketValue) {
		synchronized (InitializeReporting.sourceStateCount) {
			if (InitializeReporting.sourceStateCount.containsKey(serviceState)) {
				int count = InitializeReporting.sourceStateCount.get(serviceState);
				count += 1;
				InitializeReporting.sourceStateCount.put(serviceState, count);
			} else {
				InitializeReporting.sourceStateCount.put(serviceState, 1);
			}
		}
		synchronized (InitializeReporting.sourceRatePlanCount) {
			String key = rateplanId + "@@" + rateplanName;
			if (InitializeReporting.sourceRatePlanCount.containsKey(key)) {
				int count = InitializeReporting.sourceRatePlanCount.get(key);
				count += 1;
				InitializeReporting.sourceRatePlanCount.put(key, count);
			} else {
				InitializeReporting.sourceRatePlanCount.put(key, 1);
			}
		}

		synchronized (InitializeReporting.sourceRatePlanStateCount) {
			String key = serviceState + "@@" + rateplanId + "@@" + rateplanName;
			if (InitializeReporting.sourceRatePlanStateCount.containsKey(key)) {
				int count = InitializeReporting.sourceRatePlanStateCount.get(key);
				count += 1;
				InitializeReporting.sourceRatePlanStateCount.put(key, count);
			} else {
				InitializeReporting.sourceRatePlanStateCount.put(key, 1);
			}
		}

		// HTreeMap<String, String> htreeCug =
		// InitializeReporting.mapDbDatabase.get("schemasubscribercugclidump");
		// if (htreeCug.containsKey(msisdn)) {
		synchronized (InitializeReporting.sourceCugCount) {
			if (subs.getCugclidumpInfoList() != null && subs.getCugclidumpInfoList().size() > 0) {
				List<SchemasubscribercugclidumpInfo> listOfCug = subs.getCugclidumpInfoList();
				for (SchemasubscribercugclidumpInfo cugdata : listOfCug) {
					String key = cugdata.getCUGNAME();
					if (InitializeReporting.sourceCugCount.containsKey(key)) {
						int count = InitializeReporting.sourceCugCount.get(key);
						count += 1;
						InitializeReporting.sourceCugCount.put(key, count);
					} else {
						InitializeReporting.sourceCugCount.put(key, 1);
					}
				}
			}
		}
		// HTreeMap<String, String> htreeFaf = InitializeReporting.mapDbDatabase
		// .get("schemasubscriberprofiledump");
		// if (htreeFaf.containsKey(msisdn)) {
		synchronized (InitializeReporting.sourceFafCount) {
			if (subs.getProfiledumpInfoList() != null && subs.getProfiledumpInfoList().size() > 0) {
				List<SchemasubscriberprofiledumpInfo> listOfFaf = subs.getProfiledumpInfoList();
				for (SchemasubscriberprofiledumpInfo fafData : listOfFaf) {
					String key = null;
					if (fafData.getBstrVceIntNumTree() != null && fafData.getBstrVceIntNumTree().length() > 0) {
						key = "International";
					} else if (fafData.getBstrVceNatNumTree() != null && fafData.getBstrVceNatNumTree().length() > 0) {
						key = "National";
					}
					if (key != null) {
						if (InitializeReporting.sourceFafCount.containsKey(key)) {
							int count = InitializeReporting.sourceFafCount.get(key);
							count += 1;
							InitializeReporting.sourceFafCount.put(key, count);
						} else {
							InitializeReporting.sourceFafCount.put(key, 1);
						}
					}
				}
			}
		}

		synchronized (InitializeReporting.sourceSnaCount) {
			if (subs.getSubscriberInfoMSISDN().length() > 6) {
				if (InitializeReporting.ratePlanIdentifiers.contains(subs.getSubscriberInfoCCSACCTTYPEID())) {
					// System.out.println("SNA:" +
					// subs.getSubscriberInfoMSISDN()+","+rateplanId);
					String trimmedMsisidn = subs.getSubscriberInfoMSISDN().substring(0, 6);
					String key = trimmedMsisidn + "-" + rateplanId;
					if (InitializeReporting.sourceSnaCount.containsKey(key)) {
						int count = InitializeReporting.sourceSnaCount.get(key);
						count += 1;
						InitializeReporting.sourceSnaCount.put(key, count);
					} else {
						InitializeReporting.sourceSnaCount.put(key, 1);
					}
				}
			}
		}

		synchronized (InitializeReporting.sourceBalanceIdCount) {
			for (String balanceId : mapOfBalanceId2BucketValue.keySet()) {
				for (SchemasubscriberbalancesdumpInfo dumpinfo : mapOfBalanceId2BucketValue.get(balanceId)) {
					if (InitializeReporting.sourceBalanceIdCount.containsKey(balanceId)) {
						int count = InitializeReporting.sourceBalanceIdCount.get(balanceId);
						count += 1;
						InitializeReporting.sourceBalanceIdCount.put(balanceId, count);
					} else {
						InitializeReporting.sourceBalanceIdCount.put(balanceId, 1);
					}
				}
			}
		}
		synchronized (InitializeReporting.sourceBalanceIdSum) {
			for (String balanceId : mapOfBalanceId2BucketValue.keySet()) {
				for (SchemasubscriberbalancesdumpInfo dumpinfo : mapOfBalanceId2BucketValue.get(balanceId)) {
					String valStr = dumpinfo.getBEBUCKETVALUE();
					BigDecimal val1 = new BigDecimal(valStr);
					if (InitializeReporting.sourceBalanceIdSum.containsKey(balanceId)) {
						BigDecimal val = InitializeReporting.sourceBalanceIdSum.get(balanceId);
						val = val.add(val1);
						InitializeReporting.sourceBalanceIdSum.put(balanceId, val);
					} else {
						InitializeReporting.sourceBalanceIdSum.put(balanceId, val1);
					}
				}
			}
		}

	}

	private void generateGenericDiscardedReport(SubscriberXml subs, String serviceState, String rateplanName,
			String rateplanId, String errorCode,
			Map<String, List<SchemasubscriberbalancesdumpInfo>> mapOfBalanceId2BucketValue) {
		String msisdn = subs.getSubscriberInfoMSISDN();
		synchronized (InitializeReporting.discardSourceStateCount) {
			String key = serviceState + "@@" + errorCode;
			if (InitializeReporting.discardSourceStateCount.containsKey(key)) {
				int count = InitializeReporting.discardSourceStateCount.get(key);
				count += 1;
				InitializeReporting.discardSourceStateCount.put(key, count);
			} else {
				InitializeReporting.discardSourceStateCount.put(key, 1);
			}
		}
		synchronized (InitializeReporting.discardSourceRatePlanCount) {
			String key = rateplanId + "@@" + rateplanName + "@@" + errorCode;
			if (InitializeReporting.discardSourceRatePlanCount.containsKey(key)) {
				int count = InitializeReporting.discardSourceRatePlanCount.get(key);
				count += 1;
				InitializeReporting.discardSourceRatePlanCount.put(key, count);
			} else {
				InitializeReporting.discardSourceRatePlanCount.put(key, 1);
			}
		}
		synchronized (InitializeReporting.discardSourceRatePlanStateCount) {
			String key = serviceState + "@@" + rateplanId + "@@" + rateplanName + "@@" + errorCode;
			if (InitializeReporting.discardSourceRatePlanStateCount.containsKey(key)) {
				int count = InitializeReporting.discardSourceRatePlanStateCount.get(key);
				count += 1;
				InitializeReporting.discardSourceRatePlanStateCount.put(key, count);
			} else {
				InitializeReporting.discardSourceRatePlanStateCount.put(key, 1);
			}
		}

		synchronized (InitializeReporting.discardSourceSnaCount) {
			if (InitializeReporting.ratePlanIdentifiers.contains(rateplanId)) {
				String trimmedMsisidn = msisdn.substring(0, 6);
				String key = trimmedMsisidn + "-" + rateplanId + "@@" + errorCode;
				if (InitializeReporting.discardSourceSnaCount.containsKey(key)) {
					int count = InitializeReporting.discardSourceSnaCount.get(key);
					count += 1;
					InitializeReporting.discardSourceSnaCount.put(key, count);
				} else {
					InitializeReporting.discardSourceSnaCount.put(key, 1);
				}
			}
		}
		// HTreeMap<String, String> htreeCug =
		// InitializeReporting.mapDbDatabase.get("schemasubscribercugclidump");
		// if (htreeCug.containsKey(msisdn)) {
		synchronized (InitializeReporting.discardSourceCugCount) {
			if (subs.getCugclidumpInfoList() != null && subs.getCugclidumpInfoList().size() > 0) {
				List<SchemasubscribercugclidumpInfo> listOfCug = subs.getCugclidumpInfoList();
				for (SchemasubscribercugclidumpInfo cugdata : listOfCug) {
					String key = cugdata.getCUGNAME() + "@@" + errorCode;
					if (InitializeReporting.discardSourceCugCount.containsKey(key)) {
						int count = InitializeReporting.discardSourceCugCount.get(key);
						count += 1;
						InitializeReporting.discardSourceCugCount.put(key, count);
					} else {
						InitializeReporting.discardSourceCugCount.put(key, 1);
					}
				}
			}
		}

		// HTreeMap<String, String> htreeFaf =
		// InitializeReporting.mapDbDatabase.get("schemasubscriberprofiledump");
		// if (htreeFaf.containsKey(msisdn)) {
		synchronized (InitializeReporting.discardSourceFafCount) {
			if (subs.getCugclidumpInfoList() != null && subs.getCugclidumpInfoList().size() > 0) {
				List<SchemasubscriberprofiledumpInfo> listOfFaf = subs.getProfiledumpInfoList();
				for (SchemasubscriberprofiledumpInfo fafData : listOfFaf) {
					String key = null;
					if (fafData.getBstrVceIntNumTree() != null && fafData.getBstrVceIntNumTree().length() > 0) {
						key = "International" + "@@" + errorCode;
					} else if (fafData.getBstrVceNatNumTree() != null && fafData.getBstrVceNatNumTree().length() > 0) {
						key = "National" + "@@" + errorCode;
					}
					if (key != null) {
						if (InitializeReporting.discardSourceFafCount.containsKey(key)) {
							int count = InitializeReporting.discardSourceFafCount.get(key);
							count += 1;
							InitializeReporting.discardSourceFafCount.put(key, count);
						} else {
							InitializeReporting.discardSourceFafCount.put(key, 1);
						}
					}
				}
			}
		}

		synchronized (InitializeReporting.discardSourceBalanceIdCount) {
			for (String balanceId : mapOfBalanceId2BucketValue.keySet()) {
				for (SchemasubscriberbalancesdumpInfo balanceDumpInfo : mapOfBalanceId2BucketValue.get(balanceId)) {
					String key = balanceId + "@@" + errorCode;
					if (InitializeReporting.discardSourceBalanceIdCount.containsKey(key)) {
						int count = InitializeReporting.discardSourceBalanceIdCount.get(key);
						count += 1;
						InitializeReporting.discardSourceBalanceIdCount.put(key, count);
					} else {
						InitializeReporting.discardSourceBalanceIdCount.put(key, 1);
					}
				}
			}

		}
		synchronized (InitializeReporting.discardSourceBalanceIdSum) {
			for (String balanceId : mapOfBalanceId2BucketValue.keySet()) {
				for (SchemasubscriberbalancesdumpInfo balanceDumpInfo : mapOfBalanceId2BucketValue.get(balanceId)) {
					String key = balanceId + "@@" + errorCode;
					// List<SchemasubscriberbalancesdumpInfo> listOfValues =
					// mapOfBalanceId2BucketValue.get(balanceId);
					// for(SchemasubscriberbalancesdumpInfo dumpInfo:
					// listOfValues){
					BigDecimal val1 = new BigDecimal(balanceDumpInfo.getBEBUCKETVALUE());
					if (InitializeReporting.discardSourceBalanceIdSum.containsKey(key)) {
						BigDecimal val = InitializeReporting.discardSourceBalanceIdSum.get(key);
						val = val.add(val1);
						InitializeReporting.discardSourceBalanceIdSum.put(key, val);
					} else {
						InitializeReporting.discardSourceBalanceIdSum.put(key, val1);
					}
					// }
				}
			}
		}
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

	public void writeReport(String reportDir) throws IOException {

		BufferedWriter bwSourceStateCount = new BufferedWriter(
				new FileWriter(reportDir + "/" + "source_state_stats.txt"));
		BufferedWriter bwSourceRatePlanCount = new BufferedWriter(
				new FileWriter(reportDir + "/" + "source_rateplan_stats.txt"));
		BufferedWriter bwSourceRatePlanStateCount = new BufferedWriter(
				new FileWriter(reportDir + "/" + "source_rateplan_state_stats.txt"));
		BufferedWriter bwSourceCugCount = new BufferedWriter(new FileWriter(reportDir + "/" + "source_cug_stats.txt"));
		BufferedWriter bwSourceFafCount = new BufferedWriter(new FileWriter(reportDir + "/" + "source_faf_stats.txt"));
		BufferedWriter bwSourceBalanceStats = new BufferedWriter(
				new FileWriter(reportDir + "/" + "source_bal_stats.txt"));
		BufferedWriter bwSourceSnaCount = new BufferedWriter(new FileWriter(reportDir + "/" + "source_sna_stats.txt"));
		BufferedWriter bwSourceProfileTag = new BufferedWriter(
				new FileWriter(reportDir + "/" + "source_profiletag_stats.txt"));
		BufferedWriter bwSourceCisReport = new BufferedWriter(new FileWriter(reportDir + "/" + "source_cis_stats.txt"));

		bwSourceStateCount.write("STATE,SOURCE_CNT,DISCARDED_CNT,DISCARDED_REMARK\n");
		bwSourceRatePlanCount.write("RATEPLAN_NAME,RATE_PLAN_ID,SOURCE_CNT,DISCARDED_CNT,DISCARDED_REMARK\n");
		bwSourceRatePlanStateCount
				.write("RATEPLAN_NAME,RATE_PLAN_ID-STATE,SOURCE_CNT,DISCARDED_CNT,DISCARDED_REMARK\n");
		bwSourceCugCount.write("CUG_ID,SOURCE_COUNT,DISCARDED_COUNT,DISCARDED_REASON\n");
		bwSourceFafCount.write("FAF_TYPE,SOURCE_COUNT,DISCARDED_COUNT,DISCARDED_REMARK\n");
		bwSourceSnaCount.write("SNA_SERIES-RATEPLANID,SOURCE_COUNT,DISCARDED_COUNT,DISCARDED_REMARK\n");
		bwSourceBalanceStats.write(
				"BALANCE_TYPE_NAME,BT_ID,SOURCE_CNT,SOURCE_VAL,DISCARDED_CNT,DISCARDED_VAL,BT-CONDITION,CONDITIONAL_SOURCE_CNT,CONDITIONAL_SOURCE_VAL,CONDITIONAL_DISCARDED_CNT,CONDITIONAL_DISCARDED_VAL,TRACK_EXTRA,DISCARDED_REMARK\n");
		bwSourceProfileTag.write(
				"PROFILE_TAG_NAME,SOURCE_CNT,DISCARDED_CNT,PT-CONDITION,CONDITIONAL_SOURCE_CNT,CONDITIONAL_DISCARDED_CNT,DISCARDED_REMARK\n");
		bwSourceCisReport
				.write("BALANCE_TYPE_NAME,BT_ID,PROFILE_TAG,CONDITION,SOURCE_CNT,DISCARDED_CNT,DISCARDED_REMARK\n");
		Set<String> listOfWholestate = new HashSet<String>();
		listOfWholestate.addAll(InitializeReporting.sourceStateCount.keySet());
		// listOfWholestate.addAll(InitializeReporting.discardSourceStateCount.keySet());

		for (String state : listOfWholestate) {
			int totalDiscardedCount = 0;
			int discardedCountINC10001 = 0, discardedCountINC10002 = 0, discardedCountINC10003 = 0,
					discardedCountINC10004 = 0;

			// System.out.println(":"+state);
			int totalSourceStateCount = InitializeReporting.sourceStateCount.get(state);
			String logs = "";

			if (InitializeReporting.discardSourceStateCount.containsKey(state + "@@" + "INC1001")) {
				totalDiscardedCount = InitializeReporting.discardSourceStateCount.get(state + "@@" + "INC1001");
				discardedCountINC10001 = InitializeReporting.discardSourceStateCount.get(state + "@@" + "INC1001");
				logs = "Service class Lookup Failed-" + discardedCountINC10001;
			}
			if (InitializeReporting.discardSourceStateCount.containsKey(state + "@@" + "INC1002")) {
				totalDiscardedCount += InitializeReporting.discardSourceStateCount.get(state + "@@" + "INC1002");
				discardedCountINC10002 = InitializeReporting.discardSourceStateCount.get(state + "@@" + "INC1002");
				logs = logs + ";Service class Ignored-" + discardedCountINC10002;
			}
			if (InitializeReporting.discardSourceStateCount.containsKey(state + "@@" + "INC1003")) {
				totalDiscardedCount += InitializeReporting.discardSourceStateCount.get(state + "@@" + "INC1003");
				discardedCountINC10003 = InitializeReporting.discardSourceStateCount.get(state + "@@" + "INC1003");
				logs = logs + ";Subscriber relation data missing-" + discardedCountINC10003;
			}
			if (InitializeReporting.discardSourceStateCount.containsKey(state + "@@" + "INC1004")) {
				totalDiscardedCount += InitializeReporting.discardSourceStateCount.get(state + "@@" + "INC1004");
				discardedCountINC10004 = InitializeReporting.discardSourceStateCount.get(state + "@@" + "INC1004");
				logs = logs + ";Lifecycle lookup mismatch-" + discardedCountINC10004;
			}
			bwSourceStateCount
					.write(state + "," + totalSourceStateCount + "," + totalDiscardedCount + "," + logs + "\n");
		}
		bwSourceStateCount.close();

		Set<String> listOfWholeSna = new HashSet<String>();
		listOfWholeSna.addAll(InitializeReporting.sourceSnaCount.keySet());
		// listOfWholeSna.addAll(InitializeReporting.discardSourceSnaCount.keySet());
		for (String key : listOfWholeSna) {
			int totalDiscardedCount = 0;
			int discardedCountINC10001 = 0, discardedCountINC10002 = 0, discardedCountINC10003 = 0,
					discardedCountINC10004 = 0;
			int totalSourceStateCount = InitializeReporting.sourceSnaCount.get(key);
			String logs = "";

			if (InitializeReporting.discardSourceSnaCount.containsKey(key + "@@" + "INC1001")) {
				totalDiscardedCount = InitializeReporting.discardSourceSnaCount.get(key + "@@" + "INC1001");
				discardedCountINC10001 = InitializeReporting.discardSourceSnaCount.get(key + "@@" + "INC1001");
				logs = "Service class Lookup Failed-" + discardedCountINC10001;
			}
			if (InitializeReporting.discardSourceSnaCount.containsKey(key + "@@" + "INC1002")) {
				totalDiscardedCount += InitializeReporting.discardSourceSnaCount.get(key + "@@" + "INC1002");
				discardedCountINC10002 = InitializeReporting.discardSourceSnaCount.get(key + "@@" + "INC1002");
				logs = logs + ";Service class Ignored-" + discardedCountINC10002;
			}
			if (InitializeReporting.discardSourceSnaCount.containsKey(key + "@@" + "INC1003")) {
				totalDiscardedCount += InitializeReporting.discardSourceSnaCount.get(key + "@@" + "INC1003");
				discardedCountINC10003 = InitializeReporting.discardSourceSnaCount.get(key + "@@" + "INC1003");
				logs = logs + ";Subscriber relation data missing-" + discardedCountINC10003;
			}
			if (InitializeReporting.discardSourceSnaCount.containsKey(key + "@@" + "INC1004")) {
				totalDiscardedCount += InitializeReporting.discardSourceSnaCount.get(key + "@@" + "INC1004");
				discardedCountINC10004 = InitializeReporting.discardSourceSnaCount.get(key + "@@" + "INC1004");
				logs = logs + ";Lifecycle lookup mismatch-" + discardedCountINC10004;
			}
			bwSourceSnaCount.write(key.replaceAll("@@", ",") + "," + totalSourceStateCount + "," + totalDiscardedCount
					+ "," + logs + "\n");
		}
		bwSourceSnaCount.close();

		Set<String> listOfRatePlaIds = new HashSet<String>();
		listOfRatePlaIds.addAll(InitializeReporting.sourceRatePlanCount.keySet());
		// listOfRatePlaIds.addAll(InitializeReporting.discardSourceRatePlanCount.keySet());
		for (String ratePlanIdName : listOfRatePlaIds) {
			int totalDiscardedCount = 0;
			int discardedCountINC10001 = 0, discardedCountINC10002 = 0, discardedCountINC10003 = 0,
					discardedCountINC10004 = 0;
			int totalSourceRatePlanCount = InitializeReporting.sourceRatePlanCount.get(ratePlanIdName);
			String split[] = ratePlanIdName.split("@@");
			String logs = "";

			if (InitializeReporting.discardSourceRatePlanCount.containsKey(ratePlanIdName + "@@" + "INC1001")) {
				totalDiscardedCount = InitializeReporting.discardSourceRatePlanCount
						.get(ratePlanIdName + "@@" + "INC1001");
				discardedCountINC10001 = InitializeReporting.discardSourceRatePlanCount
						.get(ratePlanIdName + "@@" + "INC1001");
				logs = "Service class Lookup Failed-" + discardedCountINC10001;
			}
			if (InitializeReporting.discardSourceRatePlanCount.containsKey(ratePlanIdName + "@@" + "INC1002")) {
				totalDiscardedCount += InitializeReporting.discardSourceRatePlanCount
						.get(ratePlanIdName + "@@" + "INC1002");
				discardedCountINC10002 = InitializeReporting.discardSourceRatePlanCount
						.get(ratePlanIdName + "@@" + "INC1002");
				logs = logs + ";Service class Ignored-" + discardedCountINC10002;
			}
			if (InitializeReporting.discardSourceRatePlanCount.containsKey(ratePlanIdName + "@@" + "INC1003")) {
				totalDiscardedCount += InitializeReporting.discardSourceRatePlanCount
						.get(ratePlanIdName + "@@" + "INC1003");
				discardedCountINC10003 = InitializeReporting.discardSourceRatePlanCount
						.get(ratePlanIdName + "@@" + "INC1003");
				logs = logs + ";Subscriber relation data missing-" + discardedCountINC10003;

			}
			if (InitializeReporting.discardSourceRatePlanCount.containsKey(ratePlanIdName + "@@" + "INC1004")) {
				totalDiscardedCount += InitializeReporting.discardSourceRatePlanCount
						.get(ratePlanIdName + "@@" + "INC1004");
				discardedCountINC10004 = InitializeReporting.discardSourceRatePlanCount
						.get(ratePlanIdName + "@@" + "INC1004");
				logs = logs + ";Lifecycle lookup mismatch-" + discardedCountINC10004;
			}
			bwSourceRatePlanCount.write(split[1] + "," + split[0] + "," + totalSourceRatePlanCount + ","
					+ totalDiscardedCount + "," + "Service class Lookup Failed-" + discardedCountINC10001
					+ "; Service class Ignored-" + discardedCountINC10002 + "; Subscriber relation data missing-"
					+ discardedCountINC10003 + "; Lifecycle lookup mismatch-" + discardedCountINC10004 + "\n");
		}
		bwSourceRatePlanCount.close();

		Set<String> listOfRatePlanStaateIds = new HashSet<String>();
		listOfRatePlanStaateIds.addAll(InitializeReporting.sourceRatePlanStateCount.keySet());
		// listOfRatePlanStaateIds.addAll(InitializeReporting.discardSourceRatePlanStateCount.keySet());
		for (String ratePlanIdNameState : listOfRatePlanStaateIds) {
			int totalDiscardedCount = 0;
			int discardedCountINC10001 = 0, discardedCountINC10002 = 0, discardedCountINC10003 = 0,
					discardedCountINC10004 = 0;
			int totalSourceRatePlanCount = InitializeReporting.sourceRatePlanStateCount.get(ratePlanIdNameState);
			String split[] = ratePlanIdNameState.split("@@");
			String logs = "";

			if (InitializeReporting.discardSourceRatePlanStateCount
					.containsKey(ratePlanIdNameState + "@@" + "INC1001")) {
				totalDiscardedCount = InitializeReporting.discardSourceRatePlanStateCount
						.get(ratePlanIdNameState + "@@" + "INC1001");
				discardedCountINC10001 = InitializeReporting.discardSourceRatePlanStateCount
						.get(ratePlanIdNameState + "@@" + "INC1001");
				logs = "Service class Lookup Failed-" + discardedCountINC10001;
			}
			if (InitializeReporting.discardSourceRatePlanStateCount
					.containsKey(ratePlanIdNameState + "@@" + "INC1002")) {
				totalDiscardedCount += InitializeReporting.discardSourceRatePlanStateCount
						.get(ratePlanIdNameState + "@@" + "INC1002");
				discardedCountINC10002 = InitializeReporting.discardSourceRatePlanStateCount
						.get(ratePlanIdNameState + "@@" + "INC1002");
				logs = logs + ";Service class Ignored-" + discardedCountINC10002;
			}
			if (InitializeReporting.discardSourceRatePlanStateCount
					.containsKey(ratePlanIdNameState + "@@" + "INC1003")) {
				totalDiscardedCount += InitializeReporting.discardSourceRatePlanStateCount
						.get(ratePlanIdNameState + "@@" + "INC1003");
				discardedCountINC10003 = InitializeReporting.discardSourceRatePlanStateCount
						.get(ratePlanIdNameState + "@@" + "INC1003");
				logs = logs + ";Subscriber relation data missing-" + discardedCountINC10003;
			}
			if (InitializeReporting.discardSourceRatePlanStateCount
					.containsKey(ratePlanIdNameState + "@@" + "INC1004")) {
				totalDiscardedCount += InitializeReporting.discardSourceRatePlanStateCount
						.get(ratePlanIdNameState + "@@" + "INC1004");
				discardedCountINC10004 = InitializeReporting.discardSourceRatePlanStateCount
						.get(ratePlanIdNameState + "@@" + "INC1004");
				logs = logs + ";Lifecycle lookup mismatch-" + discardedCountINC10004;
			}
			bwSourceRatePlanStateCount.write(split[2] + "," + split[1] + "," + split[0] + "," + totalSourceRatePlanCount
					+ "," + totalDiscardedCount + "," + logs + "\n");
		}
		bwSourceRatePlanStateCount.close();

		Set<String> listOfcugids = new HashSet<String>();
		listOfcugids.addAll(InitializeReporting.sourceCugCount.keySet());
		// listOfcugids.addAll(InitializeReporting.discardSourceCugCount.keySet());

		for (String cugid : listOfcugids) {
			int totalDiscardedCount = 0;
			int discardedCountINC10001 = 0, discardedCountINC10002 = 0, discardedCountINC10003 = 0,
					discardedCountINC10004 = 0;
			int totalSourceCugCount = InitializeReporting.sourceCugCount.get(cugid);
			String logs = "";

			if (InitializeReporting.discardSourceCugCount.containsKey(cugid + "@@" + "INC1001")) {
				totalDiscardedCount = InitializeReporting.discardSourceCugCount.get(cugid + "@@" + "INC1001");
				discardedCountINC10001 = InitializeReporting.discardSourceCugCount.get(cugid + "@@" + "INC1001");
				logs = "Service class Lookup Failed-" + discardedCountINC10001;
			}
			if (InitializeReporting.discardSourceCugCount.containsKey(cugid + "@@" + "INC1002")) {
				totalDiscardedCount += InitializeReporting.discardSourceCugCount.get(cugid + "@@" + "INC1002");
				discardedCountINC10002 = InitializeReporting.discardSourceCugCount.get(cugid + "@@" + "INC1002");
				logs = logs + ";Service class Ignored-" + discardedCountINC10002;
			}
			if (InitializeReporting.discardSourceCugCount.containsKey(cugid + "@@" + "INC1003")) {
				totalDiscardedCount += InitializeReporting.discardSourceCugCount.get(cugid + "@@" + "INC1003");
				discardedCountINC10003 = InitializeReporting.discardSourceCugCount.get(cugid + "@@" + "INC1003");
				logs = logs + ";Subscriber relation data missing-" + discardedCountINC10003;
			}
			if (InitializeReporting.discardSourceCugCount.containsKey(cugid + "@@" + "INC1004")) {
				totalDiscardedCount += InitializeReporting.discardSourceCugCount.get(cugid + "@@" + "INC1004");
				discardedCountINC10004 = InitializeReporting.discardSourceCugCount.get(cugid + "@@" + "INC1004");
				logs = logs + ";Lifecycle lookup mismatch-" + discardedCountINC10004;
			}
			bwSourceCugCount.write(cugid + "," + totalSourceCugCount + "," + totalDiscardedCount + "," + logs + "\n");
		}
		bwSourceCugCount.close();

		Set<String> listOfFafCounts = new HashSet<String>();
		listOfFafCounts.addAll(InitializeReporting.sourceFafCount.keySet());
		// listOfFafCounts.addAll(InitializeReporting.discardSourceFafCount.keySet());
		for (String faf : listOfFafCounts) {
			int totalDiscardedCount = 0;
			int discardedCountINC10001 = 0, discardedCountINC10002 = 0, discardedCountINC10003 = 0,
					discardedCountINC10004 = 0;
			int totalSourceFafCount = InitializeReporting.sourceFafCount.get(faf);
			String logs = "";

			if (InitializeReporting.discardSourceFafCount.containsKey(faf + "@@" + "INC1001")) {
				totalDiscardedCount = InitializeReporting.discardSourceFafCount.get(faf + "@@" + "INC1001");
				discardedCountINC10001 = InitializeReporting.discardSourceFafCount.get(faf + "@@" + "INC1001");
				logs = "Service class Lookup Failed-" + discardedCountINC10001;
			}
			if (InitializeReporting.discardSourceFafCount.containsKey(faf + "@@" + "INC1002")) {
				totalDiscardedCount += InitializeReporting.discardSourceFafCount.get(faf + "@@" + "INC1002");
				discardedCountINC10002 = InitializeReporting.discardSourceFafCount.get(faf + "@@" + "INC1002");
				logs = logs + ";Service class Ignored-" + discardedCountINC10002;
			}
			if (InitializeReporting.discardSourceFafCount.containsKey(faf + "@@" + "INC1003")) {
				totalDiscardedCount += InitializeReporting.discardSourceFafCount.get(faf + "@@" + "INC1003");
				discardedCountINC10003 = InitializeReporting.discardSourceFafCount.get(faf + "@@" + "INC1003");
				logs = logs + ";Subscriber relation data missing-" + discardedCountINC10003;
			}
			if (InitializeReporting.discardSourceFafCount.containsKey(faf + "@@" + "INC1004")) {
				totalDiscardedCount += InitializeReporting.discardSourceFafCount.get(faf + "@@" + "INC1004");
				discardedCountINC10004 = InitializeReporting.discardSourceFafCount.get(faf + "@@" + "INC1004");
				logs = logs + ";Lifecycle lookup mismatch-" + discardedCountINC10004;
			}
			bwSourceFafCount.write(faf + "," + totalSourceFafCount + "," + totalDiscardedCount + "," + logs + "\n");
		}
		bwSourceFafCount.close();

		Set<String> listOfBalanceIds = new HashSet<String>();
		listOfBalanceIds.addAll(InitializeReporting.sourceBalanceIdCount.keySet());
		// listOfBalanceIds.addAll(InitializeReporting.discardSourceBalanceIdCount.keySet());
		for (String balanceIdType : listOfBalanceIds) {
			// System.out.println(balanceIdType);
			String balanceId = balanceIdType.split("@@")[0];
			String balanceType = balanceIdType.split("@@")[1];
			int balanceCount = InitializeReporting.sourceBalanceIdCount.get(balanceIdType);
			BigDecimal sourceBalanceSum = InitializeReporting.sourceBalanceIdSum.get(balanceIdType);
			int discardedCountINC10001 = 0, discardedCountINC10002 = 0, discardedCountINC10003 = 0,
					discardedCountINC10004 = 0;
			int totalDiscardedCount = 0;
			BigDecimal totalDiscradedSum = new BigDecimal("0");
			;
			String logs = "";

			if (InitializeReporting.discardSourceBalanceIdCount.containsKey(balanceIdType + "@@" + "INC1001")) {
				totalDiscardedCount = InitializeReporting.discardSourceBalanceIdCount
						.get(balanceIdType + "@@" + "INC1001");
				discardedCountINC10001 = InitializeReporting.discardSourceBalanceIdCount
						.get(balanceIdType + "@@" + "INC1001");
				totalDiscradedSum = InitializeReporting.discardSourceBalanceIdSum.get(balanceIdType + "@@" + "INC1001");
				logs = "Service class Lookup Failed-" + discardedCountINC10001;
			}
			if (InitializeReporting.discardSourceBalanceIdCount.containsKey(balanceIdType + "@@" + "INC1002")) {
				totalDiscardedCount += InitializeReporting.discardSourceBalanceIdCount
						.get(balanceIdType + "@@" + "INC1002");
				totalDiscradedSum
						.add(InitializeReporting.discardSourceBalanceIdSum.get(balanceIdType + "@@" + "INC1002"));
				discardedCountINC10002 = InitializeReporting.discardSourceBalanceIdCount
						.get(balanceIdType + "@@" + "INC1002");
				logs = logs + ";Service class Ignored-" + discardedCountINC10002;

			}
			if (InitializeReporting.discardSourceBalanceIdCount.containsKey(balanceIdType + "@@" + "INC1003")) {
				totalDiscardedCount += InitializeReporting.discardSourceBalanceIdCount
						.get(balanceIdType + "@@" + "INC1003");
				totalDiscradedSum
						.add(InitializeReporting.discardSourceBalanceIdSum.get(balanceIdType + "@@" + "INC1003"));
				discardedCountINC10003 = InitializeReporting.discardSourceBalanceIdCount
						.get(balanceIdType + "@@" + "INC1003");
				logs = logs + ";Subscriber relation data missing-" + discardedCountINC10003;
			}
			if (InitializeReporting.discardSourceBalanceIdCount.containsKey(balanceIdType + "@@" + "INC1004")) {
				totalDiscardedCount += InitializeReporting.discardSourceBalanceIdCount
						.get(balanceIdType + "@@" + "INC1004");
				totalDiscradedSum
						.add(InitializeReporting.discardSourceBalanceIdSum.get(balanceIdType + "@@" + "INC1004"));
				discardedCountINC10004 = InitializeReporting.discardSourceBalanceIdCount
						.get(balanceIdType + "@@" + "INC1004");

				logs = logs + ";Lifecycle lookup mismatch-" + discardedCountINC10004;
			}
			String tracklog = "";
			if (InitializeReporting.msisdn2ErrorCodeForTrackExtra.containsKey((balanceId))) {
				for (String offerId : InitializeReporting.msisdn2ErrorCodeForTrackExtra.get(balanceId).keySet()) {
					tracklog = offerId + "-"
							+ InitializeReporting.msisdn2ErrorCodeForTrackExtra.get(balanceId).get(offerId) + ";";
				}
			}

			if (InitializeReporting.sourceConditionalBalanceMappingCount.containsKey(balanceIdType)) {
				Map<String, Integer> conditionCount = InitializeReporting.sourceConditionalBalanceMappingCount
						.get(balanceIdType);
				for (String condition : conditionCount.keySet()) {
					Integer conditionalBalanceCount = InitializeReporting.sourceConditionalBalanceMappingCount
							.get(balanceIdType).get(condition);
					BigDecimal conditionalBalanceSum = InitializeReporting.sourceConditionalBalanceMappingSum
							.get(balanceIdType).get(condition);
					int discardedCount = 0;
					int discardedCountINC4001, discardedCountINC4002, discardedCountINC4003, discardedCountINC4004,
							discardedCountINC4007;
					BigDecimal discardedSum = new BigDecimal("0");
					if (InitializeReporting.discardSourceConditionalBalanceMappingCount.containsKey(balanceIdType)) {
						if (InitializeReporting.discardSourceConditionalBalanceMappingCount.get(balanceIdType)
								.containsKey(condition + "@@" + "INC4001")) {
							discardedCount = InitializeReporting.discardSourceConditionalBalanceMappingCount
									.get(balanceIdType).get(condition + "@@" + "INC4001");
							discardedCountINC4001 = InitializeReporting.discardSourceConditionalBalanceMappingCount
									.get(balanceIdType).get(condition + "@@" + "INC4001");
							logs = logs + ";Balance_Type expired-" + discardedCountINC4001;
						}

					}
					if (InitializeReporting.discardSourceConditionalBalanceMappingCount.containsKey(balanceIdType)) {
						if (InitializeReporting.discardSourceConditionalBalanceMappingCount.get(balanceIdType)
								.containsKey(condition + "@@" + "INC4002")) {
							discardedCount += InitializeReporting.discardSourceConditionalBalanceMappingCount
									.get(balanceIdType).get(condition + "@@" + "INC4002");
							discardedCountINC4002 = InitializeReporting.discardSourceConditionalBalanceMappingCount
									.get(balanceIdType).get(condition + "@@" + "INC4002");
							logs = logs + ";Balance_Type lookup failed-" + discardedCountINC4002;
						}

					}
					if (InitializeReporting.discardSourceConditionalBalanceMappingCount.containsKey(balanceIdType)) {
						if (InitializeReporting.discardSourceConditionalBalanceMappingCount.get(balanceIdType)
								.containsKey(condition + "@@" + "INC4003")) {
							discardedCount += InitializeReporting.discardSourceConditionalBalanceMappingCount
									.get(balanceIdType).get(condition + "@@" + "INC4003");
							discardedCountINC4003 = InitializeReporting.discardSourceConditionalBalanceMappingCount
									.get(balanceIdType).get(condition + "@@" + "INC4003");
							logs = logs + ";Balance_Type Ignored-" + discardedCountINC4003;
						}

					}
					if (InitializeReporting.discardSourceConditionalBalanceMappingCount.containsKey(balanceIdType)) {
						if (InitializeReporting.discardSourceConditionalBalanceMappingCount.get(balanceIdType)
								.containsKey(condition + "@@" + "INC4004")) {
							discardedCount += InitializeReporting.discardSourceConditionalBalanceMappingCount
									.get(balanceIdType).get(condition + "@@" + "INC4004");
							discardedCountINC4004 = InitializeReporting.discardSourceConditionalBalanceMappingCount
									.get(balanceIdType).get(condition + "@@" + "INC4004");
							logs = logs + ";Balance_Type match condition failed-" + discardedCountINC4004;
						}

					}
					if (InitializeReporting.discardSourceConditionalBalanceMappingCount.containsKey(balanceIdType)) {
						if (InitializeReporting.discardSourceConditionalBalanceMappingCount.get(balanceIdType)
								.containsKey(condition + "@@" + "INC4007")) {
							discardedCount += InitializeReporting.discardSourceConditionalBalanceMappingCount
									.get(balanceIdType).get(condition + "@@" + "INC4007");
							discardedCountINC4007 = InitializeReporting.discardSourceConditionalBalanceMappingCount
									.get(balanceIdType).get(condition + "@@" + "INC4007");
							logs = logs + ";DA Monetary value RoundOff-" + discardedCountINC4007;
						}

					}
					if (InitializeReporting.discardSourceConditionalBalanceMappingSum.containsKey(balanceIdType)) {
						if (InitializeReporting.discardSourceConditionalBalanceMappingSum.get(balanceIdType)
								.containsKey(condition + "@@" + "INC4001")) {
							discardedSum = InitializeReporting.discardSourceConditionalBalanceMappingSum
									.get(balanceIdType).get(condition + "@@" + "INC4001");
						}

					}
					if (InitializeReporting.discardSourceConditionalBalanceMappingSum.containsKey(balanceIdType)) {
						if (InitializeReporting.discardSourceConditionalBalanceMappingSum.get(balanceIdType)
								.containsKey(condition + "@@" + "INC4002")) {
							discardedSum.add(InitializeReporting.discardSourceConditionalBalanceMappingSum
									.get(balanceIdType).get(condition + "@@" + "INC4002"));
						}

					}
					if (InitializeReporting.discardSourceConditionalBalanceMappingSum.containsKey(balanceIdType)) {
						if (InitializeReporting.discardSourceConditionalBalanceMappingSum.get(balanceIdType)
								.containsKey(condition + "@@" + "INC4003")) {
							discardedSum.add(InitializeReporting.discardSourceConditionalBalanceMappingSum
									.get(balanceIdType).get(condition + "@@" + "INC4003"));
						}

					}
					if (InitializeReporting.discardSourceConditionalBalanceMappingSum.containsKey(balanceIdType)) {
						if (InitializeReporting.discardSourceConditionalBalanceMappingSum.get(balanceIdType)
								.containsKey(condition + "@@" + "INC4004")) {
							discardedSum.add(InitializeReporting.discardSourceConditionalBalanceMappingSum
									.get(balanceIdType).get(condition + "@@" + "INC4004"));
						}

					}
					if (InitializeReporting.discardSourceConditionalBalanceMappingSum.containsKey(balanceIdType)) {
						if (InitializeReporting.discardSourceConditionalBalanceMappingSum.get(balanceIdType)
								.containsKey(condition + "@@" + "INC4007")) {
							discardedSum.add(InitializeReporting.discardSourceConditionalBalanceMappingSum
									.get(balanceIdType).get(condition + "@@" + "INC4007"));
						}

					}
					totalDiscradedSum = totalDiscradedSum.add(discardedSum);
					totalDiscardedCount = discardedCount + totalDiscardedCount;
					bwSourceBalanceStats.write(balanceType + "," + balanceId + "," + balanceCount + ","
							+ sourceBalanceSum + "," + totalDiscardedCount + "," + totalDiscradedSum + "," + condition
							+ "," + conditionalBalanceCount + "," + conditionalBalanceSum + "," + discardedCount + ","
							+ discardedSum + "," + tracklog + "," + logs + "\n");
				}
			}

		}
		bwSourceBalanceStats.close();

		Set<String> listOfAttrNames = new HashSet<String>();
		listOfAttrNames.addAll(InitializeReporting.sourceProfileTagCount.keySet());
		// listOfAttrNames.addAll(InitializeReporting.discardSourceProfileTagCount.keySet());
		for (String attrName : listOfAttrNames) {
			// System.out.println(attrName);

			int balanceCount = InitializeReporting.sourceProfileTagCount.get(attrName);
			// System.out.println(attrName+","+InitializeReporting.sourceConditionalProfileTagCount.get(attrName));

			int discardedCountINC10001 = 0, discardedCountINC10002 = 0, discardedCountINC10003 = 0,
					discardedCountINC10004 = 0;
			int totalDiscardedCount = 0;

			String logs = "";

			if (InitializeReporting.discardSourceProfileTagCount.containsKey(attrName + "@@" + "INC1001")) {
				totalDiscardedCount = InitializeReporting.discardSourceProfileTagCount.get(attrName + "@@" + "INC1001");
				discardedCountINC10001 = InitializeReporting.discardSourceProfileTagCount
						.get(attrName + "@@" + "INC1001");
				logs = "Service class Lookup Failed-" + discardedCountINC10001;
			}
			if (InitializeReporting.discardSourceProfileTagCount.containsKey(attrName + "@@" + "INC1002")) {
				totalDiscardedCount += InitializeReporting.discardSourceProfileTagCount
						.get(attrName + "@@" + "INC1002");
				discardedCountINC10002 = InitializeReporting.discardSourceProfileTagCount
						.get(attrName + "@@" + "INC1002");
				logs = logs + ";Service class Ignored-" + discardedCountINC10002;

			}
			if (InitializeReporting.discardSourceProfileTagCount.containsKey(attrName + "@@" + "INC1003")) {
				totalDiscardedCount += InitializeReporting.discardSourceProfileTagCount
						.get(attrName + "@@" + "INC1003");
				discardedCountINC10003 = InitializeReporting.discardSourceProfileTagCount
						.get(attrName + "@@" + "INC1003");
				logs = logs + ";Subscriber relation data missing-" + discardedCountINC10003;
			}
			if (InitializeReporting.discardSourceProfileTagCount.containsKey(attrName + "@@" + "INC1004")) {
				totalDiscardedCount += InitializeReporting.discardSourceProfileTagCount
						.get(attrName + "@@" + "INC1004");
				discardedCountINC10004 = InitializeReporting.discardSourceProfileTagCount
						.get(attrName + "@@" + "INC1004");
				logs = logs + ";Lifecycle lookup mismatch-" + discardedCountINC10004;
			}

			if (InitializeReporting.sourceConditionalProfileTagCount.containsKey(attrName)) {
				Map<String, Integer> conditionCount = InitializeReporting.sourceConditionalProfileTagCount
						.get(attrName);
				for (String condition : conditionCount.keySet()) {
					Integer conditionalBalanceCount = InitializeReporting.sourceConditionalProfileTagCount.get(attrName)
							.get(condition);
					int discardedCount = 0;
					int discardedCountINC4001, discardedCountINC4002, discardedCountINC4003, discardedCountINC4004,
							discardedCountINC4007;
					BigDecimal discardedSum = new BigDecimal("0");
					if (InitializeReporting.discardSourceConditionalProfileTagCount.containsKey(attrName)) {
						if (InitializeReporting.discardSourceConditionalProfileTagCount.get(attrName)
								.containsKey(condition + "@@" + "INC6001")) {
							discardedCount = InitializeReporting.discardSourceConditionalProfileTagCount.get(attrName)
									.get(condition + "@@" + "INC6001");
							discardedCountINC4001 = InitializeReporting.discardSourceConditionalBalanceMappingCount
									.get(attrName).get(condition + "@@" + "INC6001");
							logs = logs + ";Profile_Tags Mapping lookup Failed-" + discardedCountINC4001;
						}

					}
					if (InitializeReporting.discardSourceConditionalProfileTagCount.containsKey(attrName)) {
						if (InitializeReporting.discardSourceConditionalProfileTagCount.get(attrName)
								.containsKey(condition + "@@" + "INC6002")) {
							discardedCount += InitializeReporting.discardSourceConditionalProfileTagCount.get(attrName)
									.get(condition + "@@" + "INC6002");
							discardedCountINC4002 = InitializeReporting.discardSourceConditionalProfileTagCount
									.get(attrName).get(condition + "@@" + "INC6002");
							logs = logs + ";Profile_Tags Mapping Ignored-" + discardedCountINC4002;
						}

					}
					if (InitializeReporting.discardSourceConditionalProfileTagCount.containsKey(attrName)) {
						if (InitializeReporting.discardSourceConditionalProfileTagCount.get(attrName)
								.containsKey(condition + "@@" + "INC6003")) {
							discardedCount += InitializeReporting.discardSourceConditionalProfileTagCount.get(attrName)
									.get(condition + "@@" + "INC6003");
							discardedCountINC4003 = InitializeReporting.discardSourceConditionalProfileTagCount
									.get(attrName).get(condition + "@@" + "INC6003");
							logs = logs + ";Profile Tag match condition Failed-" + discardedCountINC4003;
						}

					}
					if (InitializeReporting.discardSourceConditionalProfileTagCount.containsKey(attrName)) {
						if (InitializeReporting.discardSourceConditionalProfileTagCount.get(attrName)
								.containsKey(condition + "@@" + "INC6004")) {
							discardedCount += InitializeReporting.discardSourceConditionalProfileTagCount.get(attrName)
									.get(condition + "@@" + "INC6004");
							discardedCountINC4004 = InitializeReporting.discardSourceConditionalProfileTagCount
									.get(attrName).get(condition + "@@" + "INC6004");
							logs = logs + ";Profile Tag mapped attribute Lookup failed-" + discardedCountINC4004;
						}

					}
					bwSourceProfileTag.write(attrName + "," + balanceCount + "," + totalDiscardedCount + "," + condition
							+ "," + conditionalBalanceCount + "," + discardedCount + "," + logs + "\n");
				}

			} else {
				bwSourceProfileTag.write(attrName + "," + balanceCount + "," + totalDiscardedCount + "," + "::::::"
						+ "," + balanceCount + "," + totalDiscardedCount + "," + "," + logs + "\n");
			}

		}
		bwSourceProfileTag.close();
		Set<String> listOfCisBalances = new HashSet<String>();
		listOfCisBalances.addAll(InitializeReporting.sourceConditionalCisCount.keySet());
		// listOfCisBalances.addAll(InitializeReporting.discardSourceConditionalCisCount.keySet());
		for (String balanceIdType : listOfCisBalances) {
			// System.out.println(balanceIdType);
			String balanceId = balanceIdType.split("@@")[0];
			String balanceType = balanceIdType.split("@@")[1];
			int discardedCountINC10001 = 0, discardedCountINC10002 = 0, discardedCountINC10003 = 0,
					discardedCountINC10004 = 0;
			int totalDiscardedCount = 0;
			BigDecimal totalDiscradedSum = new BigDecimal("0");
			;
			String logs = "";

			if (InitializeReporting.discardSourceConditionalCisCount.containsKey(balanceIdType + "@@" + "INC1001")) {
				totalDiscardedCount = InitializeReporting.discardSourceConditionalCisCount
						.get(balanceIdType + "@@" + "INC1001");
				discardedCountINC10001 = InitializeReporting.discardSourceConditionalCisCount
						.get(balanceIdType + "@@" + "INC1001");
				totalDiscradedSum = InitializeReporting.discardSourceConditionalCisSum
						.get(balanceIdType + "@@" + "INC1001");
				logs = "Service class Lookup Failed-" + discardedCountINC10001;
			}
			if (InitializeReporting.discardSourceConditionalCisCount.containsKey(balanceIdType + "@@" + "INC1002")) {
				totalDiscardedCount += InitializeReporting.discardSourceConditionalCisCount
						.get(balanceIdType + "@@" + "INC1002");
				discardedCountINC10002 = InitializeReporting.discardSourceConditionalCisCount
						.get(balanceIdType + "@@" + "INC1002");
				logs = logs + ";Service class Ignored-" + discardedCountINC10002;

			}
			if (InitializeReporting.discardSourceConditionalCisCount.containsKey(balanceIdType + "@@" + "INC1003")) {
				totalDiscardedCount += InitializeReporting.discardSourceConditionalCisCount
						.get(balanceIdType + "@@" + "INC1003");
				discardedCountINC10003 = InitializeReporting.discardSourceConditionalCisCount
						.get(balanceIdType + "@@" + "INC1003");
				logs = logs + ";Subscriber relation data missing-" + discardedCountINC10003;
			}
			if (InitializeReporting.discardSourceConditionalCisCount.containsKey(balanceIdType + "@@" + "INC1004")) {
				totalDiscardedCount += InitializeReporting.discardSourceConditionalCisCount
						.get(balanceIdType + "@@" + "INC1004");
				discardedCountINC10004 = InitializeReporting.discardSourceConditionalCisCount
						.get(balanceIdType + "@@" + "INC1004");

				logs = logs + ";Lifecycle lookup mismatch-" + discardedCountINC10004;
			}

			if (InitializeReporting.sourceConditionalCisCount.containsKey(balanceIdType)) {
				Map<String, Integer> conditionCount = InitializeReporting.sourceConditionalCisCount.get(balanceIdType);
				for (String condition : conditionCount.keySet()) {
					Integer conditionalBalanceCount = InitializeReporting.sourceConditionalCisCount.get(balanceIdType)
							.get(condition);

					bwSourceCisReport.write(balanceType + "," + balanceId + "," + "" + "," + condition
							+ +conditionalBalanceCount + "," + totalDiscardedCount + "," + logs + "\n");
				}
			}

		}
		Set<String> listOfAttrNames1 = new HashSet<String>();
		listOfAttrNames1.addAll(InitializeReporting.sourceConditionalCisProfileCount.keySet());
		listOfAttrNames1.addAll(InitializeReporting.discardSourceCisProfileCount.keySet());
		for (String attrName : listOfAttrNames1) {
			// System.out.println(attrName);

			// System.out.println(attrName+","+InitializeReporting.sourceConditionalProfileTagCount.get(attrName));

			int discardedCountINC10001 = 0, discardedCountINC10002 = 0, discardedCountINC10003 = 0,
					discardedCountINC10004 = 0;
			int totalDiscardedCount = 0;

			String logs = "";

			if (InitializeReporting.discardSourceCisProfileCount.containsKey(attrName + "@@" + "INC1001")) {
				totalDiscardedCount = InitializeReporting.discardSourceCisProfileCount.get(attrName + "@@" + "INC1001");
				discardedCountINC10001 = InitializeReporting.discardSourceCisProfileCount
						.get(attrName + "@@" + "INC1001");
				logs = "Service class Lookup Failed-" + discardedCountINC10001;
			}
			if (InitializeReporting.discardSourceCisProfileCount.containsKey(attrName + "@@" + "INC1002")) {
				totalDiscardedCount += InitializeReporting.discardSourceCisProfileCount
						.get(attrName + "@@" + "INC1002");
				discardedCountINC10002 = InitializeReporting.discardSourceCisProfileCount
						.get(attrName + "@@" + "INC1002");
				logs = logs + ";Service class Ignored-" + discardedCountINC10002;

			}
			if (InitializeReporting.discardSourceCisProfileCount.containsKey(attrName + "@@" + "INC1003")) {
				totalDiscardedCount += InitializeReporting.discardSourceCisProfileCount
						.get(attrName + "@@" + "INC1003");
				discardedCountINC10003 = InitializeReporting.discardSourceCisProfileCount
						.get(attrName + "@@" + "INC1003");
				logs = logs + ";Subscriber relation data missing-" + discardedCountINC10003;
			}
			if (InitializeReporting.discardSourceCisProfileCount.containsKey(attrName + "@@" + "INC1004")) {
				totalDiscardedCount += InitializeReporting.discardSourceCisProfileCount
						.get(attrName + "@@" + "INC1004");
				discardedCountINC10004 = InitializeReporting.discardSourceCisProfileCount
						.get(attrName + "@@" + "INC1004");
				logs = logs + ";Lifecycle lookup mismatch-" + discardedCountINC10004;
			}

			if (InitializeReporting.sourceConditionalCisProfileCount.containsKey(attrName)) {
				Map<String, Integer> conditionCount = InitializeReporting.sourceConditionalCisProfileCount
						.get(attrName);
				for (String condition : conditionCount.keySet()) {
					Integer conditionalBalanceCount = InitializeReporting.sourceConditionalCisProfileCount.get(attrName)
							.get(condition);
					int discardedCount = 0;
					int discardedCountINC4001, discardedCountINC4002, discardedCountINC4003, discardedCountINC4004,
							discardedCountINC4007;
					BigDecimal discardedSum = new BigDecimal("0");
					if (InitializeReporting.discardSourceConditionalCisProfileCount.containsKey(attrName)) {
						if (InitializeReporting.discardSourceConditionalCisProfileCount.get(attrName)
								.containsKey(condition + "@@" + "INC6001")) {
							discardedCount = InitializeReporting.discardSourceConditionalCisProfileCount.get(attrName)
									.get(condition + "@@" + "INC6001");
							discardedCountINC4001 = InitializeReporting.discardSourceConditionalCisProfileCount
									.get(attrName).get(condition + "@@" + "INC6001");
							logs = logs + ";Profile_Tags Mapping lookup Failed-" + discardedCountINC4001;
						}

					}
					if (InitializeReporting.discardSourceConditionalCisProfileCount.containsKey(attrName)) {
						if (InitializeReporting.discardSourceConditionalCisProfileCount.get(attrName)
								.containsKey(condition + "@@" + "INC6002")) {
							discardedCount += InitializeReporting.discardSourceConditionalCisProfileCount.get(attrName)
									.get(condition + "@@" + "INC6002");
							discardedCountINC4002 = InitializeReporting.discardSourceConditionalCisProfileCount
									.get(attrName).get(condition + "@@" + "INC6002");
							logs = logs + ";Profile_Tags Mapping Ignored-" + discardedCountINC4002;
						}

					}
					if (InitializeReporting.discardSourceConditionalCisProfileCount.containsKey(attrName)) {
						if (InitializeReporting.discardSourceConditionalCisProfileCount.get(attrName)
								.containsKey(condition + "@@" + "INC6003")) {
							discardedCount += InitializeReporting.discardSourceConditionalCisProfileCount.get(attrName)
									.get(condition + "@@" + "INC6003");
							discardedCountINC4003 = InitializeReporting.discardSourceConditionalCisProfileCount
									.get(attrName).get(condition + "@@" + "INC6003");
							logs = logs + ";Profile Tag match condition Failed-" + discardedCountINC4003;
						}

					}
					if (InitializeReporting.discardSourceConditionalCisProfileCount.containsKey(attrName)) {
						if (InitializeReporting.discardSourceConditionalCisProfileCount.get(attrName)
								.containsKey(condition + "@@" + "INC6004")) {
							discardedCount += InitializeReporting.discardSourceConditionalCisProfileCount.get(attrName)
									.get(condition + "@@" + "INC6004");
							discardedCountINC4004 = InitializeReporting.discardSourceConditionalCisProfileCount
									.get(attrName).get(condition + "@@" + "INC6004");
							logs = logs + ";Profile Tag mapped attribute Lookup failed-" + discardedCountINC4004;
						}

					}
					totalDiscardedCount = totalDiscardedCount + discardedCount;
					bwSourceCisReport.write(",," + attrName + condition + "," + conditionalBalanceCount + ","
							+ totalDiscardedCount + "," + logs + "\n");
				}
			}

		}
		bwSourceCisReport.close();

	}

	private void generateReportFromProfileTagMapping(String msisdn, SubscriberXml subs, String xml,ProfileTagFetching pftmapping) {
		// TODO Auto-generated method stub
		Document xmlDocument = initDomDocument(xml);
		for (String attrName : InitializeReporting.profileMapping.keySet()) {
			Set<String> unqueCnd = new HashSet<String>();
			Set<String> unqueCndCis = new HashSet<String>();
			String tagValue = pftmapping.GetProfileTagValue(attrName);
			
			

			if (tagValue != null && tagValue.length() > 0) {
				synchronized (InitializeReporting.sourceProfileTagCount) {
					if (InitializeReporting.sourceProfileTagCount.containsKey(attrName)) {
						int count = InitializeReporting.sourceProfileTagCount.get(attrName);
						count += 1;
						InitializeReporting.sourceProfileTagCount.put(attrName, count);
					} else {
						InitializeReporting.sourceProfileTagCount.put(attrName, 1);
					}
				}
				if (InitializeReporting.msisdn2ErrorCode.containsKey(msisdn)) {
					String errorCode = InitializeReporting.msisdn2ErrorCode.get(msisdn);
					synchronized (InitializeReporting.discardSourceProfileTagCount) {
						String key = attrName + "@@" + errorCode;
						if (InitializeReporting.discardSourceProfileTagCount.containsKey(key)) {
							int count = InitializeReporting.discardSourceProfileTagCount.get(key);
							count += 1;
							InitializeReporting.discardSourceProfileTagCount.put(key, count);
						} else {
							InitializeReporting.discardSourceProfileTagCount.put(key, 1);
						}
					}

				}
			}

			List<PROFILETAGINFO> listOfProfileTags = InitializeReporting.profileMapping.get(attrName);
			for (PROFILETAGINFO ptInfo : listOfProfileTags) {
				String symbol = ptInfo.getSymbols();
				String ptValue = ptInfo.getProfileTagValue();
				String subState = ptInfo.getSubState();
				String ratePlanId = ptInfo.getRatePlanID();
				String ratePlanOperator = ptInfo.getRatePlanOperator();
				String additionalPtCheck = ptInfo.getAdditionalPTCheck();
				subState = subState != null ? subState : "";
				ratePlanId = ratePlanId != null ? ratePlanId : "";
				ratePlanOperator = ratePlanOperator != null ? ratePlanOperator : "";
				boolean datefound = false;
				boolean found = true;
				String condition = "";
				if (ptValue.equals("migdate")) {
					String migrationDate = InitializeReporting.commonConfigMap.get("migration_date");
					migrationDate = migrationDate.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
					Long migDate = Long.parseLong(migrationDate);
					if (tagValue != null && tagValue.length() > 0) {
						Long actualDate = Long.parseLong(tagValue);
						if (actualDate >= migDate) {
							found = true;
						} else {
							found = false;
						}
					} else {
						found = false;
					}
					datefound = true;
				}
				// System.out.println("ptValue:"+ptValue);
				// System.out.println("tagValue:"+tagValue);
				if (datefound == false) {
					switch (symbol) {
					case "=":
						found = ptValue.equals(tagValue) ? true : false;
						break;
					case "!=":
						found = !ptValue.equals(tagValue) ? true : false;
						break;
					case ">":
						found = !ptValue.equals("migdate") && tagValue != null && tagValue.length() > 0
								&& Double.parseDouble(tagValue) > Double.parseDouble(ptValue) ? true : false;
						break;
					case "<":
						found = !ptValue.equals("migdate") && tagValue != null && tagValue.length() > 0
								&& Double.parseDouble(tagValue) < Double.parseDouble(ptValue) ? true : false;
						break;
					case ">=":
						found = !ptValue.equals("migdate") && tagValue != null && tagValue.length() > 0
								&& Double.parseDouble(tagValue) >= Double.parseDouble(ptValue) ? true : false;
						break;
					}
				}
				/*
				 * if(found==false){ System.out.println("FAILED: "+ msisdn
				 * +","+tagValue+""+symbol+""+ptValue); } else{
				 * System.out.println("PASSED: "+ msisdn
				 * +","+tagValue+""+symbol+""+ptValue); }
				 */
				if (subState != null && subState.length() > 0) {
					if (subState.equals(subs.getSubscriberInfoSERVICESTATE())) {
						// System.out.println("SERVICESTATE_PASSED: RPID
						// "+found+","+msisdn);
						found = found & true;
					} else {
						found = found & false;
					}
				}

				if (ratePlanId != null && ratePlanId.length() > 0) {
					if (ratePlanOperator.equals("=") && ratePlanId.equals(subs.getSubscriberInfoCCSACCTTYPEID())) {
						found = found & true;
						// System.out.println("PROFILE_TAG_PASSED: RPID
						// "+found+","+msisdn);
					} else if (ratePlanOperator.equals("!=")
							&& !ratePlanId.equals(subs.getSubscriberInfoCCSACCTTYPEID())) {
						found = found & true;
						// System.out.println("PROFILE_TAG_PASSED: RPSymbol
						// "+found+","+msisdn);
					} else if (ratePlanOperator.equals("or")) {
						List<String> listOfrpids = Arrays.asList(ratePlanId.split("#"));
						if (listOfrpids.contains(subs.getSubscriberInfoCCSACCTTYPEID())) {
							found = found & true;
						} else {
							found = found & false;
						}
					} else {
						// found = found & false;
						// System.out.println("PROFILE_TAG_PASSED: RPSymbol ID
						// "+found+","+msisdn);
					}
				} else {
					found = found & true;
				}

				// check for profile tag
				if (found) {
					String profileTagName = ptInfo.getAdditionalPTCheck();
					if (profileTagName != null && profileTagName.length() > 0) {
						List<String> listOfProfileTags1 = Arrays.asList(profileTagName.split("#"));
						for (String profileTagWithCondition : listOfProfileTags1) {
							String splittedData[] = profileTagWithCondition.split("-");
							String profileName = splittedData[0];
							String profileSymbol = splittedData[1];
							String profileValue = splittedData[2];
							String additionalCheckTagValue = pftmapping.GetProfileTagValue(profileName);//getProfileTagValue(xmlDocument, profileName);
							if (profileValue.equals("EMPTY")) {
								if (profileSymbol.equals("!=")) {
									if (additionalCheckTagValue != null && additionalCheckTagValue.length() > 0) {
										found = found & true;
									} else {
										found = found & false;
									}
								} else if (profileSymbol.equals("=")) {
									if (additionalCheckTagValue == null) {
										found = found & true;
									} else {
										found = found & false;
									}
								}
							} else if (additionalCheckTagValue == null || (additionalCheckTagValue!=null && additionalCheckTagValue.equals(""))) {
								found = found & false;
							} else if (profileValue.equalsIgnoreCase("MIGDATE")) {
								String migDateStr = InitializeReporting.commonConfigMap.get("migration_date");
								if(migDateStr.length()==0){
									System.out.println(migDateStr);
								}
								migDateStr = migDateStr.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
								Long migDate = Long.parseLong(migDateStr);
								if (profileSymbol.equals("!=")) {
									if (additionalCheckTagValue != null
											&& !additionalCheckTagValue.equals(migDateStr)) {
										found = found & true;
									} else {
										found = found & false;
									}
								} else if (profileSymbol.equals("=")) {
									if (additionalCheckTagValue != null && additionalCheckTagValue.equals(migDateStr)) {
										found = found & true;
									} else {
										found = found & false;
									}
								} else if (profileSymbol.equals(">=")) {
									if (additionalCheckTagValue != null
											&& Long.parseLong(additionalCheckTagValue) >= migDate) {
										found = found & true;
									} else {
										found = found & false;
									}
								} else if (profileSymbol.equals("<")) {
									if (additionalCheckTagValue != null
											&& Long.parseLong(additionalCheckTagValue) < migDate) {
										found = found & true;
									} else {
										found = found & false;
									}
								}
							} else {
								if (profileSymbol.equals("!=")) {
									if (additionalCheckTagValue != null
											&& !additionalCheckTagValue.equals(profileValue)) {
										found = found & true;
									} else {
										found = found & false;
									}
								} else if (profileSymbol.equals("=")) {
									if (additionalCheckTagValue != null
											&& additionalCheckTagValue.equals(profileValue)) {
										found = found & true;
									} else {
										found = found & false;
									}
								} else if (profileSymbol.equals("<")) {
									if (additionalCheckTagValue != null && (Long
											.parseLong(additionalCheckTagValue) < Long.parseLong(profileValue))) {
										found = found & true;
									} else {
										found = found & false;
									}
								} else if (profileSymbol.equals(">=")) {
									if (additionalCheckTagValue != null && (Long
											.parseLong(additionalCheckTagValue) >= Long.parseLong(profileValue))) {
										found = found & true;
									} else {
										found = found & false;
									}
								}
							}

						}

					}
				}

				// Calculate Stats
				if (found) {
					condition = attrName + ":" + symbol + ":" + ptValue + ":" + subState + ":" + ratePlanOperator + ":"
							+ ratePlanId + ":" + additionalPtCheck;
					// System.out.println("Calculating stats: "+ condition);
					if (InitializeReporting.sourceConditionalProfileTagCount.containsKey(attrName)) {
						Map<String, Integer> mapOfConditionCount = InitializeReporting.sourceConditionalProfileTagCount
								.get(attrName);
						if (mapOfConditionCount.containsKey(condition)) {
							if (unqueCnd.contains(attrName + "_" + condition + "_" + tagValue + "")) {
								// dont count or add
							} else {
								int count = mapOfConditionCount.get(condition);
								count += 1;
								mapOfConditionCount.put(condition, count);
								InitializeReporting.sourceConditionalProfileTagCount.put(attrName, mapOfConditionCount);
								// System.out.println("Calculating stats: "+
								// condition);
							}
						} else {
							mapOfConditionCount.put(condition, 1);
							InitializeReporting.sourceConditionalProfileTagCount.put(attrName, mapOfConditionCount);
							// System.out.println("Calculating stats: "+
							// condition);
						}
					} else {
						Map<String, Integer> mapOfConditionCount = new HashMap<String, Integer>();
						mapOfConditionCount.put(condition, 1);
						InitializeReporting.sourceConditionalProfileTagCount.put(attrName, mapOfConditionCount);
						// System.out.println("Calculating stats: "+ condition);
					}

					if (InitializeReporting.msisdn2ErrorCodeForProfileTagMapping
							.containsKey(msisdn + "@@" + attrName)) {

						String errorCode = InitializeReporting.msisdn2ErrorCodeForProfileTagMapping
								.get(msisdn + "@@" + attrName);
						String key = condition + "@@" + errorCode;
						// System.out.println("FOUND DISCARD ATTR :" + msisdn +
						// "@@" + attrName + "@@" + errorCode);
						if (InitializeReporting.discardSourceConditionalBalanceMappingCount.containsKey(attrName)) {
							Map<String, Integer> mapOfConditionCount = InitializeReporting.discardSourceConditionalBalanceMappingCount
									.get(attrName);

							if (mapOfConditionCount.containsKey(key)) {
								if (unqueCnd.contains(attrName + "_" + condition + "_" + tagValue + "")) {
									// dont count or add
								} else {
									int count = mapOfConditionCount.get(key);
									count += 1;
									mapOfConditionCount.put(key, count);
									InitializeReporting.discardSourceConditionalBalanceMappingCount.put(attrName,
											mapOfConditionCount);
								}
							} else {
								mapOfConditionCount.put(key, 1);
								InitializeReporting.discardSourceConditionalBalanceMappingCount.put(attrName,
										mapOfConditionCount);
							}
						} else {
							Map<String, Integer> mapOfConditionCount = new HashMap<String, Integer>();
							mapOfConditionCount.put(key, 1);
							InitializeReporting.discardSourceConditionalBalanceMappingCount.put(attrName,
									mapOfConditionCount);
						}

					}

					// For CIS
					if (ptInfo.getCISReference() != null && ptInfo.getCISReference().length() > 0) {
						if (InitializeReporting.sourceConditionalCisProfileCount.containsKey(attrName)) {
							Map<String, Integer> mapOfConditionCount = InitializeReporting.sourceConditionalCisProfileCount
									.get(attrName);
							if (mapOfConditionCount.containsKey(condition)) {
								if (unqueCndCis.contains(attrName + "_" + condition + "_" + tagValue + "")) {
									// dont count or add
								} else {
									int count = mapOfConditionCount.get(condition);
									count += 1;
									mapOfConditionCount.put(condition, count);
									InitializeReporting.sourceConditionalCisProfileCount.put(attrName,
											mapOfConditionCount);
									// System.out.println("Calculating stats: "+
									// condition);
								}
							} else {
								mapOfConditionCount.put(condition, 1);
								InitializeReporting.sourceConditionalCisProfileCount.put(attrName, mapOfConditionCount);
								// System.out.println("Calculating stats: "+
								// condition);
							}
						} else {
							Map<String, Integer> mapOfConditionCount = new HashMap<String, Integer>();
							mapOfConditionCount.put(condition, 1);
							InitializeReporting.sourceConditionalCisProfileCount.put(attrName, mapOfConditionCount);
							// System.out.println("Calculating stats: "+
							// condition);
						}

						if (InitializeReporting.msisdn2ErrorCodeForProfileTagMapping
								.containsKey(msisdn + "@@" + attrName)) {

							String errorCode = InitializeReporting.msisdn2ErrorCodeForProfileTagMapping
									.get(msisdn + "@@" + attrName);
							String key = condition + "@@" + errorCode;
							System.out.println("FOUND DISCARD ATTR :" + msisdn + "@@" + attrName + "@@" + errorCode);
							if (InitializeReporting.discardSourceConditionalCisProfileCount.containsKey(attrName)) {
								Map<String, Integer> mapOfConditionCount = InitializeReporting.discardSourceConditionalCisProfileCount
										.get(attrName);

								if (mapOfConditionCount.containsKey(key)) {
									if (unqueCndCis.contains(attrName + "_" + condition + "_" + tagValue + "")) {
										// dont count or add
									} else {
										int count = mapOfConditionCount.get(key);
										count += 1;
										mapOfConditionCount.put(key, count);
										InitializeReporting.discardSourceConditionalCisProfileCount.put(attrName,
												mapOfConditionCount);
									}
								} else {
									mapOfConditionCount.put(key, 1);
									InitializeReporting.discardSourceConditionalCisProfileCount.put(attrName,
											mapOfConditionCount);
								}
							} else {
								Map<String, Integer> mapOfConditionCount = new HashMap<String, Integer>();
								mapOfConditionCount.put(key, 1);
								InitializeReporting.discardSourceConditionalCisProfileCount.put(attrName,
										mapOfConditionCount);
							}

						}
						// For 1001 to 1004
						if (InitializeReporting.msisdn2ErrorCode.containsKey(msisdn + "@@" + attrName)) {

							String errorCode = InitializeReporting.msisdn2ErrorCode.get(msisdn + "@@" + attrName);

							// System.out.println("FOUND DISCARD ATTR :" +
							// msisdn + "@@" + attrName + "@@" + errorCode);
							if (InitializeReporting.discardSourceCisProfileCount
									.containsKey(msisdn + "@@" + attrName)) {

								int count = InitializeReporting.discardSourceCisProfileCount
										.get(msisdn + "@@" + attrName);
								count += 1;

								InitializeReporting.discardSourceCisProfileCount.put(attrName, count);
							}
						} else {
							InitializeReporting.discardSourceCisProfileCount.put(attrName, 1);
						}
						unqueCndCis.add(attrName + "_" + condition + "_" + tagValue + "");
					}

					unqueCnd.add(attrName + "_" + condition + "_" + tagValue + "");
				}
			}
		}
	}

	private void generateReportFromDefaultServices(String msisdn, SubscriberXml subs, String xml,ProfileTagFetching pftmapping) {
		// TODO Auto-generated method stub
		Document xmlDocument = initDomDocument(xml);
		for (String attrName : InitializeReporting.defaultMapping.keySet()) {
			String tagValue = pftmapping.GetProfileTagValue(attrName);
			// System.out.println("DEFAULT:"+attrName+","+tagValue);
			if (tagValue != null && tagValue.length() > 0) {

				synchronized (InitializeReporting.sourceProfileTagCount) {
					if (InitializeReporting.sourceProfileTagCount.containsKey(attrName)) {
						int count = InitializeReporting.sourceProfileTagCount.get(attrName);
						count += 1;
						InitializeReporting.sourceProfileTagCount.put(attrName, count);
					} else {
						InitializeReporting.sourceProfileTagCount.put(attrName, 1);
					}
				}

				if (InitializeReporting.msisdn2ErrorCodeForProfileTagMapping.containsKey(msisdn + "@@" + attrName)) {
					String errorCode = InitializeReporting.msisdn2ErrorCodeForProfileTagMapping
							.get(msisdn + "@@" + attrName);
					synchronized (InitializeReporting.discardSourceProfileTagCount) {
						String key = attrName + "@@" + errorCode;
						if (InitializeReporting.discardSourceProfileTagCount.containsKey(key)) {
							int count = InitializeReporting.discardSourceProfileTagCount.get(key);
							count += 1;
							InitializeReporting.discardSourceProfileTagCount.put(key, count);
						} else {
							InitializeReporting.discardSourceProfileTagCount.put(key, 1);
						}
					}

				}
			}
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
				this.generateSourceReport(xml);
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

	private void generateReportFromBalanceMapping(String msisdn,
			Map<String, List<SchemasubscriberbalancesdumpInfo>> mapOfBalanceId2BucketValue,
			 String rateplanId, ProfileTagFetching pft) {

		for (String balanceIdName : mapOfBalanceId2BucketValue.keySet()) {
			String balanceId = balanceIdName.split("@@")[0];
			if (InitializeReporting.balanceMapping.containsKey(balanceId)) {
				Set<String> unqueCnd = new HashSet<String>();
				Set<String> unqueCndCis = new HashSet<String>();
				List<BALANCEMAPPINGINFO> listBalanceMappingInfo = InitializeReporting.balanceMapping.get(balanceId);
				for (BALANCEMAPPINGINFO balanceMappingInfo : listBalanceMappingInfo) {
					// String balanceTypeFromMap =
					// balanceMappingInfo.getBTTYPE();
					String balanceValueFromMap = balanceMappingInfo.getBTValue();
					BigDecimal foundValue = new BigDecimal("0");
					String condition = "";
					String bucketId = "";
					boolean found = false;

					List<SchemasubscriberbalancesdumpInfo> listOfValuesForBalanceId = mapOfBalanceId2BucketValue
							.get(balanceIdName);
					// if
					// (mapOfBalanceType2Value.containsKey(balanceTypeFromMap))
					// {
					/*
					 * if (listOfValuesForBalanceId == null) {
					 * System.out.println("MSISDN:" + msisdn + ",BALANCE_ID:" +
					 * balanceId); }
					 */
					for (SchemasubscriberbalancesdumpInfo balanceDumpInfo : listOfValuesForBalanceId) {
						String tempValue = balanceDumpInfo.getBEBUCKETVALUE();
						double actualValue = Double.parseDouble(tempValue);
						String symbols = balanceMappingInfo.getSymbols();
						bucketId = balanceDumpInfo.getBEBUCKETID();
						// condition=balanceDumpInfo.getBEBUCKETVALUE()+":"+balanceMappingInfo.getSymbols()+":"+balanceMappingInfo.getBTValue()+":"+balanceMappingInfo.getSCIdentifier()+":"+balanceMappingInfo.getRPID()+":"+balanceMappingInfo.getPTName();
						if (!symbols.equals("or")) {
							double value2beCompared = Double.parseDouble(balanceValueFromMap);
							switch (symbols) {
							case ">=":
								actualValue = actualValue >= value2beCompared ? actualValue : -777111;
								break;
							case "=":
								actualValue = actualValue == value2beCompared ? actualValue : -777111;
								break;
							case "<":
								actualValue = actualValue < value2beCompared ? actualValue : -777111;
								break;
							case ">":
								actualValue = actualValue > value2beCompared ? actualValue : -777111;
								break;
							}
						} else {
							List<String> listOfValues = Arrays.asList(balanceValueFromMap.split("#"));
							if (listOfValues.contains(actualValue)) {

							} else {
								actualValue = -777111;
							}

						}
						if (actualValue == -777111) {
							found = false;
							;
						} else {
							found = true;
							foundValue = new BigDecimal(actualValue + "");
							;
							condition = balanceMappingInfo.getBTID() + ":" + balanceMappingInfo.getSymbols() + ":"
									+ balanceMappingInfo.getBTValue() + ":" + balanceMappingInfo.getSCIdentifier() + ":"
									+ balanceMappingInfo.getRPID() + ";" + balanceMappingInfo.getPTName();
						}

						// Check for rate plan existance
						if (found) {
							String rpid = balanceMappingInfo.getRPID();
							String rpSymbol = balanceMappingInfo.getSCIdentifier();
							if (rpid != null && rpid.length() > 0) {
								if (rpSymbol.equals("=") && rateplanId.equals(rpid)) {
									found = found & true;
									// System.out.println("PROFILE_TAG_PASSED:
									// RPID "+found+","+msisdn);
								} else if (rpSymbol.equals("!=") && !rateplanId.equals(rpid)) {
									found = found & true;
									// System.out.println("PROFILE_TAG_PASSED:
									// RPSymbol "+found+","+msisdn);
								} else {
									found = found & false;
									// System.out.println("PROFILE_TAG_PASSED:
									// RPSymbol ID "+found+","+msisdn);
								}
							} else {
								found = found & true;
							}
						}

						// check for profile tag
						if (found) {
							String profileTagName = balanceMappingInfo.getPTName();
							if (profileTagName != null && profileTagName.length() > 0) {
								List<String> listOfProfileTags = Arrays.asList(profileTagName.split("#"));
								for (String profileTagWithCondition : listOfProfileTags) {
									String splittedData[] = profileTagWithCondition.split("-");
									String profileName = splittedData[0];
									String profileSymbol = splittedData[1];
									String profileValue = splittedData[2];
									if (profileSymbol.equals("=")) {
										String tagValue = pft.GetProfileTagValue(profileName);
										if (tagValue != null
												&& tagValue.equals(profileValue)) {
											found = found & true;
											// System.out.println("PROFILE_TAG_PASSED:
											// Profile Tag "+found+","+msisdn);
										} else {
											found = found & false;
										}
									} else if (profileSymbol.equals("!=")) {
										String tagValue = pft.GetProfileTagValue(profileName);
										if (tagValue != null
												&& !tagValue.equals(profileValue)) {
											found = found & true;
											// System.out.println("PROFILE_TAG_PASSED:
											// Profile Tag "+found+","+msisdn);
										} else {
											found = found & false;
										}
									}

								}

							}
						}

						//
						// calculate stats
						if (found) {

							if (InitializeReporting.sourceConditionalBalanceMappingCount.containsKey(balanceIdName)) {
								Map<String, Integer> mapOfConditionCount = InitializeReporting.sourceConditionalBalanceMappingCount
										.get(balanceIdName);
								if (mapOfConditionCount.containsKey(condition)) {
									if (unqueCnd.contains(balanceId + "_" + condition + "_" + foundValue + "")) {
										// dont count or add
									} else {
										int count = mapOfConditionCount.get(condition);
										count += 1;
										mapOfConditionCount.put(condition, count);
										InitializeReporting.sourceConditionalBalanceMappingCount.put(balanceIdName,
												mapOfConditionCount);
									}
								} else {
									mapOfConditionCount.put(condition, 1);
									InitializeReporting.sourceConditionalBalanceMappingCount.put(balanceIdName,
											mapOfConditionCount);
								}
							} else {
								Map<String, Integer> mapOfConditionCount = new HashMap<String, Integer>();
								mapOfConditionCount.put(condition, 1);
								InitializeReporting.sourceConditionalBalanceMappingCount.put(balanceIdName,
										mapOfConditionCount);
							}

							if (InitializeReporting.sourceConditionalBalanceMappingSum.containsKey(balanceIdName)) {
								Map<String, BigDecimal> mapOfConditionCount = InitializeReporting.sourceConditionalBalanceMappingSum
										.get(balanceIdName);
								if (mapOfConditionCount.containsKey(condition)) {
									if (unqueCnd.contains(balanceId + "_" + condition + "_" + foundValue + "")) {
										// dont count or add
									} else {
										BigDecimal storedValue = mapOfConditionCount.get(condition);
										storedValue.add(foundValue);
										mapOfConditionCount.put(condition, storedValue);
										InitializeReporting.sourceConditionalBalanceMappingSum.put(balanceIdName,
												mapOfConditionCount);
									}
								} else {
									mapOfConditionCount.put(condition, foundValue);
									InitializeReporting.sourceConditionalBalanceMappingSum.put(balanceIdName,
											mapOfConditionCount);
								}
							} else {
								Map<String, BigDecimal> mapOfConditionCount = new HashMap<String, BigDecimal>();
								mapOfConditionCount.put(condition, foundValue);
								InitializeReporting.sourceConditionalBalanceMappingSum.put(balanceIdName,
										mapOfConditionCount);
							}

							if (InitializeReporting.msisdn2ErrorCodeForBalanceMapping
									.containsKey(msisdn + "@@" + bucketId)) {

								String errorCode = InitializeReporting.msisdn2ErrorCodeForBalanceMapping
										.get(msisdn + "@@" + bucketId);
								String key = condition + "@@" + errorCode;
								// System.out.println(
								// "FOUND DISCARD BUCKET :" + msisdn + "@@" +
								// bucketId + "@@" + errorCode);
								if (InitializeReporting.discardSourceConditionalBalanceMappingCount
										.containsKey(balanceIdName)) {
									Map<String, Integer> mapOfConditionCount = InitializeReporting.discardSourceConditionalBalanceMappingCount
											.get(balanceIdName);

									if (mapOfConditionCount.containsKey(key)) {
										if (unqueCnd.contains(balanceId + "_" + condition + "_" + foundValue + "")) {
											// dont count or add
										} else {
											int count = mapOfConditionCount.get(key);
											count += 1;
											mapOfConditionCount.put(key, count);
											InitializeReporting.discardSourceConditionalBalanceMappingCount
													.put(balanceIdName, mapOfConditionCount);
										}
									} else {
										mapOfConditionCount.put(key, 1);
										InitializeReporting.discardSourceConditionalBalanceMappingCount
												.put(balanceIdName, mapOfConditionCount);
									}
								} else {
									Map<String, Integer> mapOfConditionCount = new HashMap<String, Integer>();
									mapOfConditionCount.put(key, 1);
									InitializeReporting.discardSourceConditionalBalanceMappingCount.put(balanceIdName,
											mapOfConditionCount);
								}

								if (InitializeReporting.discardSourceConditionalBalanceMappingSum
										.containsKey(balanceIdName)) {
									Map<String, BigDecimal> mapOfConditionCount = InitializeReporting.discardSourceConditionalBalanceMappingSum
											.get(balanceIdName);

									if (mapOfConditionCount.containsKey(key)) {
										if (unqueCnd.contains(balanceId + "_" + condition + "_" + foundValue + "")) {
											// dont count or add
										} else {
											BigDecimal strdValue = mapOfConditionCount.get(key);
											strdValue.add(foundValue);
											mapOfConditionCount.put(key, strdValue);
											InitializeReporting.discardSourceConditionalBalanceMappingSum
													.put(balanceIdName, mapOfConditionCount);
										}
									} else {
										mapOfConditionCount.put(key, foundValue);
										InitializeReporting.discardSourceConditionalBalanceMappingSum.put(balanceIdName,
												mapOfConditionCount);
									}
								} else {
									Map<String, BigDecimal> mapOfConditionCount = new HashMap<String, BigDecimal>();
									mapOfConditionCount.put(key, foundValue);
									InitializeReporting.discardSourceConditionalBalanceMappingSum.put(balanceIdName,
											mapOfConditionCount);
								}
							}
							//
							// Start of cis
							if (balanceMappingInfo.getCISReference() != null
									&& balanceMappingInfo.getCISReference().length() > 0) {
								if (InitializeReporting.sourceConditionalCisCount.containsKey(balanceIdName)) {
									Map<String, Integer> mapOfConditionCount = InitializeReporting.sourceConditionalCisCount
											.get(balanceIdName);
									if (mapOfConditionCount.containsKey(condition)) {
										if (unqueCndCis.contains(balanceId + "_" + condition + "_" + foundValue + "")) {
											// dont count or add
										} else {
											int count = mapOfConditionCount.get(condition);
											count += 1;
											mapOfConditionCount.put(condition, count);
											InitializeReporting.sourceConditionalCisCount.put(balanceIdName,
													mapOfConditionCount);
										}
									} else {
										mapOfConditionCount.put(condition, 1);
										InitializeReporting.sourceConditionalCisCount.put(balanceIdName,
												mapOfConditionCount);
									}
								} else {
									Map<String, Integer> mapOfConditionCount = new HashMap<String, Integer>();
									mapOfConditionCount.put(condition, 1);
									InitializeReporting.sourceConditionalCisCount.put(balanceIdName,
											mapOfConditionCount);
								}

								if (InitializeReporting.sourceConditionalCisSum.containsKey(balanceIdName)) {
									Map<String, BigDecimal> mapOfConditionCount = InitializeReporting.sourceConditionalCisSum
											.get(balanceIdName);
									if (mapOfConditionCount.containsKey(condition)) {
										if (unqueCndCis.contains(balanceId + "_" + condition + "_" + foundValue + "")) {
											// dont count or add
										} else {
											BigDecimal storedValue = mapOfConditionCount.get(condition);
											storedValue.add(foundValue);
											mapOfConditionCount.put(condition, storedValue);
											InitializeReporting.sourceConditionalCisSum.put(balanceIdName,
													mapOfConditionCount);
										}
									} else {
										mapOfConditionCount.put(condition, foundValue);
										InitializeReporting.sourceConditionalCisSum.put(balanceIdName,
												mapOfConditionCount);
									}
								} else {
									Map<String, BigDecimal> mapOfConditionCount = new HashMap<String, BigDecimal>();
									mapOfConditionCount.put(condition, foundValue);
									InitializeReporting.sourceConditionalCisSum.put(balanceIdName, mapOfConditionCount);
								}
								if (InitializeReporting.msisdn2ErrorCode.containsKey(msisdn)) {

									String errorCode = InitializeReporting.msisdn2ErrorCode.get(msisdn);
									// String key = condition + "@@" +
									// errorCode;
									// System.out.println(
									// "FOUND DISCARD BUCKET :" + msisdn + "@@"
									// + bucketId + "@@" + errorCode);
									if (InitializeReporting.discardSourceConditionalCisCount
											.containsKey(balanceIdName + "@@" + errorCode)) {
										int count = InitializeReporting.discardSourceConditionalCisCount
												.get(balanceIdName + "@@" + errorCode);
										count += 1;
										InitializeReporting.discardSourceConditionalCisCount
												.put(balanceIdName + "@@" + errorCode, count);
									} else {

										InitializeReporting.discardSourceConditionalCisCount
												.put(balanceIdName + "@@" + errorCode, 1);
									}

								}

							}
						}

						unqueCndCis.add(balanceId + "_" + condition + "_" + foundValue + "");

					}
					unqueCnd.add(balanceId + "_" + condition + "_" + foundValue + "");
				}

			}

		}

	}

	public static void main(String args[]) throws IOException {

		InitializeReporting inr = new InitializeReporting("C:/Projects/DU/database/", "C:/Projects/DU/logs/",
				"C:/Projects/DU/Input/", "meydvvmsdp33", "C:/Projects/DU/data/","","","");
		/*
		 * for(String key : InitializeReporting.msisdn2ErrorCode.keySet()){
		 * System.out.println(key
		 * +":"+InitializeReporting.msisdn2ErrorCode.get(key) ); }
		 */
		GenerateReport gnr = new GenerateReport();
		gnr.streamInput();

		gnr.writeReport("C:/Projects/DU/Report/");

	}

	/*public String getProfileTagValue(Document xmlDocument, String tagName) {
		XPath xPath = XPathFactory.newInstance().newXPath();
		if (tagName.equals("1stTimeActivation")) {
			tagName = "FstTimeActivation";
		}
		String expression = "/subscriber_xml/profiledump_info/schemasubscriberprofiledump_info/" + tagName;

		// read a string value
		String tagVlaue = null;

		try {
			tagVlaue = xPath.compile(expression).evaluate(xmlDocument);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// System.out.println(expression);

		}
		return tagVlaue;

	}*/
	
	/*public String getProfileTagValue(SubscriberXml subscriber, String tagName) {
		if( subscriber.getProfiledumpInfoList()!=null &&  subscriber.getProfiledumpInfoList().size()>0){
		Class profiledumpClass = subscriber.getProfiledumpInfoList().get(0).getClass();
		SchemasubscriberprofiledumpInfo profileDumpInfo =subscriber.getProfiledumpInfoList().get(0);
		String tagValue="";
		Method method;
		//no paramater
		Class noparams[] = {};
		try {
			for(Method m : profiledumpClass.getDeclaredMethods()){
				System.out.println(m.getName());
			}
			method = profiledumpClass.getDeclaredMethod("get"+tagName, noparams);
			tagValue = (String)method.invoke((Object)profileDumpInfo, noparams);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tagValue;
		}
		else{
			return "";
		}

	}*/
	
	/*public String getProfileTagValue(SubscriberXml subscriber, String tagName) {
		if( subscriber.getProfiledumpInfoList()!=null &&  subscriber.getProfiledumpInfoList().size()>0){
		Class profiledumpClass = subscriber.getProfiledumpInfoList().get(0).getClass();
		SchemasubscriberprofiledumpInfo profileDumpInfo =subscriber.getProfiledumpInfoList().get(0);
		String tagValue="";
		Method method;
		//no paramater
		Class noparams[] = {};
		try {
			for(Method m : profiledumpClass.getDeclaredMethods()){
				System.out.println(m.getName());
			}
			method = profiledumpClass.getDeclaredMethod("get"+tagName, noparams);
			tagValue = (String)method.invoke((Object)profileDumpInfo, noparams);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tagValue;
		}
		else{
			return "";
		}

	}
*/
	public Document initDomDocument(String xml) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document xmlDocument = null;

		try {
			builder = builderFactory.newDocumentBuilder();

			xmlDocument = builder.parse(new ByteArrayInputStream(xml.getBytes()));

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xmlDocument;
	}
}
