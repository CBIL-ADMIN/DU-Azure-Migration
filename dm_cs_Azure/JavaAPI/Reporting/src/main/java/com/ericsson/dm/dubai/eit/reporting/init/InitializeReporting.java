package com.ericsson.dm.dubai.eit.reporting.init;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.print.attribute.HashAttributeSet;

import org.apache.commons.io.FileUtils;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.ericsson.jibx.beans.BALANCEMAPPINGLIST;
import com.ericsson.jibx.beans.BALANCEMAPPINGLIST.BALANCEMAPPINGINFO;
import com.ericsson.jibx.beans.DEFAULTSERVICESMAPPINGLIST;
import com.ericsson.jibx.beans.DEFAULTSERVICESMAPPINGLIST.DEFAULTSERVICESMAPPINGINFO;
import com.ericsson.jibx.beans.PROFILETAGLIST;
import com.ericsson.jibx.beans.PROFILETAGLIST.PROFILETAGINFO;

public class InitializeReporting {
	public static DB mapDbDatabase;
	public static String databaseDir;
	public static String logDir, inputDir;
	public static String sdpId;
	public static final Map<String, String> msisdn2ErrorCode = new ConcurrentHashMap<String, String>(10000, 0.75f, 20);
	public static final Map<String, String> msisdn2ErrorCodeSnaCount = new ConcurrentHashMap<String, String>(10000,
			0.75f, 20);
	public static final Map<String, String> msisdn2ErrorCodeForBalanceMapping = new ConcurrentHashMap<String, String>(
			10000, 0.75f, 20);
	public static final Map<String, String> msisdn2ErrorCodeForProfileTagMapping = new ConcurrentHashMap<String, String>(
			10000, 0.75f, 20);
	public static final Map<String, Map<String, Integer>> msisdn2ErrorCodeForTrackExtra = new ConcurrentHashMap<String, Map<String, Integer>>(
			10000, 0.75f, 20);
	public static final Map<String, String> msisdn2ErrorCodeForCug = new ConcurrentHashMap<String, String>(10000, 0.75f,
			20);
	public static final Map<String, Integer> sourceStateCount = new ConcurrentHashMap<String, Integer>(10000, 0.75f,
			20);
	public static final Map<String, Integer> discardSourceStateCount = new ConcurrentHashMap<String, Integer>(10000,
			0.75f, 20);
	public static final Map<String, Integer> sourceRatePlanCount = new ConcurrentHashMap<String, Integer>(10000, 0.75f,
			20);
	public static final Map<String, Integer> discardSourceRatePlanCount = new ConcurrentHashMap<String, Integer>(10000,
			0.75f, 20);
	public static final Map<String, Integer> discardSourceRatePlanStateCount = new ConcurrentHashMap<String, Integer>(
			10000, 0.75f, 20);
	public static final Map<String, Integer> sourceRatePlanStateCount = new ConcurrentHashMap<String, Integer>(10000,
			0.75f, 20);
	public static final Map<String, Integer> sourceCugCount = new ConcurrentHashMap<String, Integer>(10000, 0.75f, 20);
	public static final Map<String, Integer> discardSourceCugCount = new ConcurrentHashMap<String, Integer>(10000,
			0.75f, 20);
	public static final Map<String, Integer> sourceFafCount = new ConcurrentHashMap<String, Integer>(10000, 0.75f, 20);
	public static final Map<String, Integer> discardSourceFafCount = new ConcurrentHashMap<String, Integer>(10000,
			0.75f, 20);
	public static final Map<String, Integer> sourceBalanceIdCount = new ConcurrentHashMap<String, Integer>(10000, 0.75f,
			20);
	public static final Map<String, Integer> discardSourceBalanceIdCount = new ConcurrentHashMap<String, Integer>(10000,
			0.75f, 20);
	public static final Map<String, BigDecimal> sourceBalanceIdSum = new ConcurrentHashMap<String, BigDecimal>(10000,
			0.75f, 20);
	public static final Map<String, BigDecimal> discardSourceBalanceIdSum = new ConcurrentHashMap<String, BigDecimal>(
			10000, 0.75f, 20);
	public static final Map<String, Integer> sourceProfileTagCount = new ConcurrentHashMap<String, Integer>(10000,
			0.75f, 20);
	public static final Map<String, Integer> discardSourceProfileTagCount = new ConcurrentHashMap<String, Integer>(
			10000, 0.75f, 20);
	public static final Map<String, Integer> sourceSnaCount = new ConcurrentHashMap<String, Integer>(10000, 0.75f, 20);
	public static final Map<String, Integer> discardSourceSnaCount = new ConcurrentHashMap<String, Integer>(10000,
			0.75f, 20);
	public static final Map<String, Map<String, Integer>> sourceConditionalBalanceMappingCount = new ConcurrentHashMap<String, Map<String, Integer>>(
			10000, 0.75f, 20);
	public static final Map<String, Map<String, Integer>> discardSourceConditionalBalanceMappingCount = new ConcurrentHashMap<String, Map<String, Integer>>(
			10000, 0.75f, 20);
	public static final Map<String, Map<String, Integer>> sourceConditionalCisCount = new ConcurrentHashMap<String, Map<String, Integer>>(
			10000, 0.75f, 20);
	public static final Map<String, Integer> discardSourceConditionalCisCount = new ConcurrentHashMap<String, Integer>(
			10000, 0.75f, 20);
	public static final Map<String, Map<String, BigDecimal>> sourceConditionalBalanceMappingSum = new ConcurrentHashMap<String, Map<String, BigDecimal>>(
			10000, 0.75f, 20);
	public static final Map<String, Map<String, BigDecimal>> discardSourceConditionalBalanceMappingSum = new ConcurrentHashMap<String, Map<String, BigDecimal>>(
			10000, 0.75f, 20);
	public static final Map<String, Map<String, BigDecimal>> sourceConditionalCisSum = new ConcurrentHashMap<String, Map<String, BigDecimal>>(
			10000, 0.75f, 20);
	public static final Map<String, BigDecimal> discardSourceConditionalCisSum = new ConcurrentHashMap<String, BigDecimal>(
			10000, 0.75f, 20);
	public static final Map<String, Map<String, Integer>> sourceConditionalProfileTagCount = new ConcurrentHashMap<String, Map<String, Integer>>(
			10000, 0.75f, 20);
	public static final Map<String, Map<String, Integer>> discardSourceConditionalProfileTagCount = new ConcurrentHashMap<String, Map<String, Integer>>(
			10000, 0.75f, 20);
	public static final Map<String, Map<String, Integer>> sourceConditionalCisProfileCount = new ConcurrentHashMap<String, Map<String, Integer>>(
			10000, 0.75f, 20);
	public static final Map<String, Integer> discardSourceCisProfileCount = new ConcurrentHashMap<String, Integer>(
			10000, 0.75f, 20);
	public static final Map<String, Map<String, Integer>> discardSourceConditionalCisProfileCount = new ConcurrentHashMap<String, Map<String, Integer>>(
			10000, 0.75f, 20);
	public static final Map<String, Map<String, Integer>> discardSourceConditionalTrackExtra = new ConcurrentHashMap<String, Map<String, Integer>>(
			10000, 0.75f, 20);
	public static final Map<String, String> commonConfigMap = new ConcurrentHashMap<String, String>(10000, 0.75f, 20);
	public static List<String> ratePlanIdentifiers = new ArrayList<String>();

	// Rejection stats
	public static final Map<String, Integer> rejectionStatsCugCount = new ConcurrentHashMap<String, Integer>(10000,
			0.75f, 20);
	public static final Map<String, Integer> rejectionStatsSnaCount = new ConcurrentHashMap<String, Integer>(10000,
			0.75f, 20);
	public static final Map<String, Integer> rejectionStatsFafCount = new ConcurrentHashMap<String, Integer>(10000,
			0.75f, 20);
	public static final Map<String, Integer> rejectionUnmatchRatePlanCount = new ConcurrentHashMap<String, Integer>(
			10000, 0.75f, 20);
	public static final Map<String, Integer> rejectionIgnoreRatePlanCount = new ConcurrentHashMap<String, Integer>(
			10000, 0.75f, 20);
	public static final Map<String, Integer> rejectionUnmatchLfc = new ConcurrentHashMap<String, Integer>(10000, 0.75f,
			20);
	public static final Map<String, Integer> rejectionUnmatchCugId = new ConcurrentHashMap<String, Integer>(10000,
			0.75f, 20);

	public static final Map<String, List<BALANCEMAPPINGINFO>> balanceMapping = new ConcurrentHashMap<String, List<BALANCEMAPPINGINFO>>(
			10000, 0.75f, 50);
	public static final Map<String, List<PROFILETAGINFO>> profileMapping = new ConcurrentHashMap<String, List<PROFILETAGINFO>>(
			10000, 0.75f, 50);
	public static final Map<String, List<DEFAULTSERVICESMAPPINGINFO>> defaultMapping = new ConcurrentHashMap<String, List<DEFAULTSERVICESMAPPINGINFO>>(
			10000, 0.75f, 50);

	// Snapshot
	public static final Map<String, String> subscriberDump = new ConcurrentHashMap<String, String>(10000, 0.75f, 50);
	public static final Map<String, Integer> cugStats = new ConcurrentHashMap<String, Integer>(10000, 0.75f, 50);
	public static final Map<String, Map<String, String>> offerDump = new ConcurrentHashMap<String, Map<String, String>>(
			10000, 0.75f, 50);
	public static final Map<String, Integer> offerAttrDump = new ConcurrentHashMap<String, Integer>(10000, 0.75f, 50);
	public static final Map<String, Integer> offerCounter7001 = new ConcurrentHashMap<String, Integer>(10000, 0.75f,
			50);
	public static final Map<String, Integer> offerCounter7004 = new ConcurrentHashMap<String, Integer>(10000, 0.75f,
			50);
	public static final Map<String, Integer> daCounter7002 = new ConcurrentHashMap<String, Integer>(10000, 0.75f, 50);
	public static final Map<String, Integer> ucCounter7003 = new ConcurrentHashMap<String, Integer>(10000, 0.75f, 50);

	public static final Map<String, Integer> targetDaCounter = new ConcurrentHashMap<String, Integer>(10000, 0.75f, 50);
	public static final Map<String, BigDecimal> targetDaSummer = new ConcurrentHashMap<String, BigDecimal>(10000, 0.75f,
			50);
	
	public static final Map<String, Integer> targetUcCounter = new ConcurrentHashMap<String, Integer>(10000, 0.75f, 50);
	public static final Map<String, BigDecimal> targetUcSummer = new ConcurrentHashMap<String, BigDecimal>(10000, 0.75f,
			50);
	
	public static final Map<String, Integer> fafCounter = new ConcurrentHashMap<String, Integer>(10000, 0.75f, 50);
	
	public static final Map<String, Integer> accumulatorCounter = new ConcurrentHashMap<String, Integer>(10000, 0.75f,
			50);
	public static final Map<String, BigDecimal> accumulatorSummer = new ConcurrentHashMap<String, BigDecimal>(10000,
			0.75f, 50);
	public static final Map<String, Integer> snaCounter = new ConcurrentHashMap<String, Integer>(10000, 0.75f, 50);
	
	public static final Map<String, Integer> bundleTypeCounter = new ConcurrentHashMap<String, Integer>(10000, 0.75f, 50);


	public InitializeReporting(String databaseDir, String logDir, String inputDir, String sdpId, String dataFolder,
			String snapshotDir, String workingMode,String outputFolder) throws IOException {
		InitializeReporting.databaseDir = databaseDir;
		InitializeReporting.logDir = logDir;
		InitializeReporting.sdpId = sdpId;
		InitializeReporting.inputDir = inputDir;
		initializeMapDb();
		readLogFiles();
		LoadxmlBalanceMappingMap(dataFolder);
		LoadxmlProfileTagMap(dataFolder);
		LoadxmlDefaultMap(dataFolder);
		readCommonConfigMap(dataFolder);
		if (workingMode.equals("SNAPSHOT_STATISTICS")) {
			Map<String,String> mapOfFileNames = fileNamesFromFolder( snapshotDir);
			readSubscriberDump(snapshotDir+"/"+mapOfFileNames.get("SUB"));
			readOfferDump(snapshotDir+"/"+mapOfFileNames.get("OFFER"));
			readOfferAttrDump(snapshotDir+"/"+mapOfFileNames.get("OFFERATTR"));
			readDASnapshotFile(snapshotDir+"/"+mapOfFileNames.get("DA"));
			readAccumulatorSnapshotFile(snapshotDir+"/"+mapOfFileNames.get("ACCU"));
			readUsageCounterSnapshotFile(snapshotDir+"/"+mapOfFileNames.get("UC"));
			readFafSnapshotFile(snapshotDir+"/"+mapOfFileNames.get("FAF"));
			readSnaSnapshotFile(outputFolder);
			readBundleTypeSnapshotFile(outputFolder);
			
		}

	}

	private void readOfferAttrDump(String snapshotDir) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(snapshotDir + "/"));
		String line = "";
		while ((line = br.readLine()) != null) {
			String temp[] = line.split(",");
			String msisdn = temp[0];
			String offerId = temp[1];
			String attrName = temp[2];
			String key = offerId + "-" + attrName;

			if (offerAttrDump.containsKey(key)) {
				int counter = offerAttrDump.get(key);
				counter += 1;
				offerAttrDump.put(key, counter);
			} else {
				offerAttrDump.put(key, 1);
			}

			br.close();
		}

	}

	private void readSubscriberDump(String snapshotDir) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(snapshotDir ));
		String line = "";
		while ((line = br.readLine()) != null) {
			String temp[] = line.split(",");
			String msisdn = temp[0];
			String serviceClass = temp[17];
			String tempBlockFlag = temp[2];
			String activationDate = temp[44];
			String deactivationDate = temp[25];
			String cugid1 = temp[41];
			String cugid2 = temp[42];
			String cugid3 = temp[43];
			String snaSeries = msisdn.substring(0, 6);
			// InitializeReporting.commonConfigMap.containsKey("SNA_Rate_Plan_ID_Identifier");
			if (cugid1 != null && !cugid1.equals("0")) {
				if (cugStats.containsKey(cugid1)) {
					int counter = 0;
					counter = cugStats.get(cugid1);
					cugStats.put(cugid1, counter);
				} else {
					cugStats.put(cugid1, 1);
				}
			} else if (cugid2 != null && !cugid2.equals("0")) {
				if (cugStats.containsKey(cugid2)) {
					int counter = 0;
					counter = cugStats.get(cugid2);
					cugStats.put(cugid2, counter);
				} else {
					cugStats.put(cugid2, 1);
				}
			} else if (cugid3 != null && !cugid3.equals("0")) {
				if (cugStats.containsKey(cugid3)) {
					int counter = 0;
					counter = cugStats.get(cugid3);
					cugStats.put(cugid3, counter);
				} else {
					cugStats.put(cugid3, 1);
				}
			}
			subscriberDump.put(msisdn,
					serviceClass + "," + tempBlockFlag + "," + activationDate + "," + deactivationDate);

		}
		br.close();
	}

	private void readOfferDump(String snapshotDir) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(snapshotDir));
		String line = "";
		while ((line = br.readLine()) != null) {
			String temp[] = line.split(",");
			String msisdn = temp[0];
			String offerId = temp[1];

			if (offerDump.containsKey(msisdn)) {
				Map<String, String> mapOfOfferIds = offerDump.get(msisdn);
				mapOfOfferIds.put(offerId, offerId);
				offerDump.put(msisdn, mapOfOfferIds);
			} else {
				Map<String, String> mapOfOfferIds = new HashMap<String, String>();
				mapOfOfferIds.put(offerId, offerId);
				offerDump.put(msisdn, mapOfOfferIds);
			}

			br.close();
		}
	}

	
	private void initializeMapDb() {
		String filesToBeInserted = getInputFile(InitializeReporting.inputDir);
		InitializeReporting.sdpId = filesToBeInserted.split("_")[0];

		mapDbDatabase = DBMaker.fileDB(new File(InitializeReporting.databaseDir + "/db_" + InitializeReporting.sdpId))
				.fileMmapEnable().make();
	}

	private void readLogFiles() {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(InitializeReporting.logDir + "/" + "Exception.log"));
			String line = "";
			while ((line = br.readLine()) != null) {
				/*
				 * if(line.startsWith("INC1001") || line.startsWith("INC1002")
				 * || line.startsWith("INC1003") || line.startsWith("INC1004")){
				 * String datas [] = line.split(":",-4); String msisdnStr[] =
				 * datas[2].split("="); String msisdn = msisdnStr[1];
				 * msisdn2ErrorCode.put(msisdn, datas[0]);
				 * //System.out.println(msisdn+","+ datas[0]); }
				 */
				if (line.startsWith("INC4001") || line.startsWith("INC4002") || line.startsWith("INC4003")
						|| line.startsWith("INC4004") || line.startsWith("4010")) {
					String datas[] = line.split(":", -4);
					String msisdnStr[] = datas[2].split("=");
					String msisdn = msisdnStr[1];
					String bucketIdStr[] = datas[5].split("=");
					String bucketId = bucketIdStr[1];
					msisdn2ErrorCodeForBalanceMapping.put(msisdn + "@@" + bucketId, datas[0]);
					// System.out.println(msisdn+"@@"+bucketId+","+ datas[0]);
				}
				if (line.startsWith("INC5001")) {
					String datas[] = line.split(":", -4);
					String msisdnStr[] = datas[2].split("=");
					String msisdn = msisdnStr[1];
					msisdn2ErrorCodeForBalanceMapping.put(msisdn, datas[0]);
					// System.out.println(msisdn+"@@"+bucketId+","+ datas[0]);
				}
				if (line.startsWith("INC6001") || line.startsWith("INC6002") || line.startsWith("INC6003")
						|| line.startsWith("INC6004")) {
					String datas[] = line.split(":", -4);
					String msisdnStr[] = datas[2].split("=");
					String msisdn = msisdnStr[1];
					String attrNameStr[] = datas[3].split("=");
					String attrName = attrNameStr[1];
					msisdn2ErrorCodeForProfileTagMapping.put(msisdn + "@@" + attrName, datas[0]);
					// System.out.println(msisdn+"@@"+attrName+","+ datas[0]);
				}
				if (line.startsWith("INC7001") || line.startsWith("INC7004")) {
					String datas[] = line.split(":", -4);
					String msisdnStr[] = datas[2].split("=");
					String msisdn = msisdnStr[1];
					String balanceTypeStr[] = datas[4].split("=");
					String balanceType = balanceTypeStr[1];
					String offerStr[] = datas[6].split("=");
					String offerId = offerStr[1];
					if (msisdn2ErrorCodeForTrackExtra.containsKey(balanceType)) {
						Map<String, Integer> mapOfOfferIdCount = msisdn2ErrorCodeForTrackExtra.get(balanceType);
						if (mapOfOfferIdCount.containsKey(offerId)) {
							int count = mapOfOfferIdCount.get(offerId);
							count += 1;
							mapOfOfferIdCount.put(offerId, count);
							msisdn2ErrorCodeForTrackExtra.put(balanceType, mapOfOfferIdCount);
						} else {
							int count = 1;
							mapOfOfferIdCount.put(offerId, count);
							msisdn2ErrorCodeForTrackExtra.put(balanceType, mapOfOfferIdCount);
						}
					} else {
						Map<String, Integer> mapOfOfferIdCount = new HashMap<String, Integer>();
						int count = 1;
						mapOfOfferIdCount.put(offerId, count);
						msisdn2ErrorCodeForTrackExtra.put(balanceType, mapOfOfferIdCount);
					}
				}

				if (line.startsWith("INC7001")) {
					String datas[] = line.split(":", -4);
					String offerStr[] = datas[6].split("=");
					String offerId = offerStr[1];
					if (offerCounter7001.containsKey(offerId)) {
						int counter = offerCounter7001.get(offerId);
						counter += 1;
						offerCounter7001.put(offerId, counter);
					} else {
						offerCounter7001.put(offerId, 1);
					}
				} else if (line.startsWith("INC7004")) {
					String datas[] = line.split(":", -4);
					String offerStr[] = datas[6].split("=");
					String offerId = offerStr[1];
					if (offerCounter7004.containsKey(offerId)) {
						int counter = offerCounter7004.get(offerId);
						counter += 1;
						offerCounter7004.put(offerId, counter);
					} else {
						offerCounter7004.put(offerId, 1);
					}
				} else if (line.startsWith("INC7002")) {
					String datas[] = line.split(":", -4);
					String daStr[] = datas[6].split("=");
					String daId = daStr[1];
					if (daCounter7002.containsKey(daId)) {
						int counter = daCounter7002.get(daId);
						counter += 1;
						daCounter7002.put(daId, counter);
					} else {
						daCounter7002.put(daId, 1);
					}
				} else if (line.startsWith("INC7003")) {
					String datas[] = line.split(":", -4);
					String daStr[] = datas[6].split("=");
					String daId = daStr[1];
					if (ucCounter7003.containsKey(daId)) {
						int counter = ucCounter7003.get(daId);
						counter += 1;
						ucCounter7003.put(daId, counter);
					} else {
						ucCounter7003.put(daId, 1);
					}
				}
			}
			br.close();
			br = new BufferedReader(new FileReader(InitializeReporting.logDir + "/" + "Discarded.log"));
			line = "";
			while ((line = br.readLine()) != null) {
				if (line.startsWith("INC1001") || line.startsWith("INC1002") || line.startsWith("INC1003")
						|| line.startsWith("INC1004")) {
					String datas[] = line.split(":", -4);
					String msisdnStr[] = datas[2].split("=");
					String msisdn = msisdnStr[1];
					msisdn2ErrorCode.put(msisdn, datas[0]);
					// System.out.println(msisdn+","+ datas[0]);
				}

			}
			br.close();
			br = new BufferedReader(new FileReader(InitializeReporting.logDir + "/" + "Discarded.log"));
			while ((line = br.readLine()) != null) {
				if (line.startsWith("INC9002") || line.startsWith("INC9003")) {
					String datas[] = line.split(":", -4);
					String msisdnStr[] = datas[2].split("=");
					String msisdn = msisdnStr[1];
					msisdn2ErrorCodeSnaCount.put(msisdn, datas[0]);
					// System.out.println(msisdn+","+ datas[0]);
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("msisdn2ErrorCode: " + msisdn2ErrorCode.size());
		System.out.println("msisdn2ErrorCodeForBalanceMapping: " + msisdn2ErrorCodeForBalanceMapping.size());
		System.out.println("msisdn2ErrorCodeForTrackExtra: " + msisdn2ErrorCodeForTrackExtra.size());
	}

	private void LoadxmlBalanceMappingMap(String dataFolderPath) {
		IBindingFactory bindingFactoryBalance = null;
		IUnmarshallingContext unmarshallingContextBalance;

		try {
			bindingFactoryBalance = BindingDirectory.getFactory(com.ericsson.jibx.beans.BALANCEMAPPINGLIST.class);
			unmarshallingContextBalance = bindingFactoryBalance.createUnmarshallingContext();
			BALANCEMAPPINGLIST balancelist = (com.ericsson.jibx.beans.BALANCEMAPPINGLIST) unmarshallingContextBalance
					.unmarshalDocument(
							new ByteArrayInputStream(
									FileUtils.readFileToByteArray(new File(dataFolderPath + "/BalanceMapping.xml"))),
							null);

			for (BALANCEMAPPINGINFO balanceInfo : balancelist.getBALANCEMAPPINGINFOList()) {
				if (balanceMapping.containsKey(balanceInfo.getBTID())) {
					List<BALANCEMAPPINGINFO> listOfBalanceInfo = balanceMapping.get(balanceInfo.getBTID());
					listOfBalanceInfo.add(balanceInfo);
					balanceMapping.put(balanceInfo.getBTID(), listOfBalanceInfo);
				} else {
					List<BALANCEMAPPINGINFO> listOfBalanceInfo = new ArrayList<BALANCEMAPPINGINFO>();
					listOfBalanceInfo.add(balanceInfo);
					balanceMapping.put(balanceInfo.getBTID(), listOfBalanceInfo);
				}
			}
			System.out.println("balanceMapping : " + balanceMapping.size());
			System.out.println(balanceMapping.get("21").get(0).getBalanceTypeName());
		}

		catch (Exception e) {

		}
	}

	private void LoadxmlProfileTagMap(String dataFolderPath) {
		IBindingFactory bindingFactoryBalance = null;
		IUnmarshallingContext unmarshallingContextBalance;

		try {
			bindingFactoryBalance = BindingDirectory.getFactory(com.ericsson.jibx.beans.PROFILETAGLIST.class);
			unmarshallingContextBalance = bindingFactoryBalance.createUnmarshallingContext();
			PROFILETAGLIST profileTagList = (com.ericsson.jibx.beans.PROFILETAGLIST) unmarshallingContextBalance
					.unmarshalDocument(
							new ByteArrayInputStream(
									FileUtils.readFileToByteArray(new File(dataFolderPath + "/ProfileTagMapping.xml"))),
							null);

			for (PROFILETAGINFO profileTagInfo : profileTagList.getPROFILETAGINFOList()) {
				if (profileMapping.containsKey(profileTagInfo.getProfileTagName())) {
					List<PROFILETAGINFO> listOfProfileTagInfo = profileMapping.get(profileTagInfo.getProfileTagName());
					listOfProfileTagInfo.add(profileTagInfo);
					profileMapping.put(profileTagInfo.getProfileTagName(), listOfProfileTagInfo);
				} else {
					List<PROFILETAGINFO> listOfProfileTagInfo = new ArrayList<PROFILETAGINFO>();
					listOfProfileTagInfo.add(profileTagInfo);
					profileMapping.put(profileTagInfo.getProfileTagName(), listOfProfileTagInfo);
				}
			}
			System.out.println("ProfileTag : " + profileMapping.size());
			System.out.println(profileMapping.get("NewPPBundle").get(0).getProfileTagName());
		}

		catch (Exception e) {

		}
	}

	private void LoadxmlDefaultMap(String dataFolderPath) {
		IBindingFactory bindingFactoryBalance = null;
		IUnmarshallingContext unmarshallingContextBalance;

		try {
			bindingFactoryBalance = BindingDirectory
					.getFactory(com.ericsson.jibx.beans.DEFAULTSERVICESMAPPINGLIST.class);
			unmarshallingContextBalance = bindingFactoryBalance.createUnmarshallingContext();
			DEFAULTSERVICESMAPPINGLIST defaultServiceList = (com.ericsson.jibx.beans.DEFAULTSERVICESMAPPINGLIST) unmarshallingContextBalance
					.unmarshalDocument(
							new ByteArrayInputStream(FileUtils
									.readFileToByteArray(new File(dataFolderPath + "/DefaultServicesMapping.xml"))),
							null);

			for (DEFAULTSERVICESMAPPINGINFO defaultServiceInfo : defaultServiceList
					.getDEFAULTSERVICESMAPPINGINFOList()) {
				if (defaultServiceInfo.getAttrName() != null) {
					if (defaultMapping.containsKey(defaultServiceInfo.getAttrName())) {
						List<DEFAULTSERVICESMAPPINGINFO> listOfDefaultServiceInfo = defaultMapping
								.get(defaultServiceInfo.getAttrName());
						listOfDefaultServiceInfo.add(defaultServiceInfo);
						defaultMapping.put(defaultServiceInfo.getAttrName(), listOfDefaultServiceInfo);
					} else {
						List<DEFAULTSERVICESMAPPINGINFO> listOfDefaultServiceInfo = new ArrayList<DEFAULTSERVICESMAPPINGINFO>();
						listOfDefaultServiceInfo.add(defaultServiceInfo);
						defaultMapping.put(defaultServiceInfo.getAttrName(), listOfDefaultServiceInfo);
					}
					// System.out.println("Attr
					// :"+defaultServiceInfo.getAttrName());
				}
			}
			System.out.println("ProfileTag : " + defaultMapping.size());

		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readCommonConfigMap(String dataFolderPath) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(dataFolderPath + "/CommonConfigMapping.txt"));
			String line = "";
			while ((line = br.readLine()) != null) {
				String temp[] = line.split(",");
				commonConfigMap.put(temp[0], temp[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String strsplit[] = commonConfigMap.get("SNA_Rate_Plan_ID_Identifier").split("\\|");
		ratePlanIdentifiers = Arrays.asList(strsplit);
		// System.out.println(commonConfigMap.get("SNA_Rate_Plan_ID_Identifier"));
		// System.out.println("RateplanIdentifier:" +ratePlanIdentifiers );
	}

	private static String getInputFile(String dumpFilePath) {
		File folder = new File(dumpFilePath);
		File[] listOfFiles = folder.listFiles();
		String FileName = "";

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println(listOfFiles[i].getName());
				if (!listOfFiles[i].getName().contains("_subscriber_be_dump.csv.gz")) {
					FileName += listOfFiles[i].getName().trim() + ',';
				}
				// results.add(listOfFiles[i].getName().trim());
			}
		}
		return FileName.substring(0, FileName.length() - 1).toString();
	}

	public void readSubscriberDAFile(String path) {

	}

	private void readDASnapshotFile(String dataFolderPath) {
		// TODO Auto-generated method stub

		try {
			BufferedReader br = new BufferedReader(new FileReader(dataFolderPath));
			String line = "";
			while ((line = br.readLine()) != null) {
				String dats[] = line.split(",", -6);
				if (dats.length > 10) {
					String daid = dats[1];

					if (targetDaCounter.containsKey(daid)) {
						int counter = targetDaCounter.get(daid);
						counter += 1;
						targetDaCounter.put(daid, counter);
					} else {
						targetDaCounter.put(daid, 1);
					}
					if (targetDaSummer.containsKey(daid)) {
						String bal = "";
						BigDecimal origBal = targetDaSummer.get(daid);
						if (dats[7] != null && dats[7].equals("1")) {
							bal = dats[2];
						} else {
							bal = dats[10];
						}
						BigDecimal val = origBal.add(new BigDecimal(bal));
						targetDaSummer.put(daid, val);
					} else {
						String bal = "";
						if (dats[7] != null && dats[7].equals("1")) {
							bal = dats[2];
						} else {
							bal = dats[10];
						}
						BigDecimal val = new BigDecimal(bal);
						targetDaSummer.put(daid, val);
					}
				}
			}
			br.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// LOG.error(e);
		}
		// LOG.warn("FULL SIZE DA : " + daSnapshotMap.keySet().size());
	}

	private void readAccumulatorSnapshotFile(String dataFolderPath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(dataFolderPath));
			String line = "";
			while ((line = br.readLine()) != null) {
				String dats[] = line.split(",", -6);
				if (dats.length > 4) {
					String accumulatorId = dats[1];
					String val = dats[2];

					if (accumulatorCounter.containsKey(accumulatorId)) {
						int counter = accumulatorCounter.get(accumulatorId);
						counter += 1;
						accumulatorCounter.put(accumulatorId, counter);
					} else {
						accumulatorCounter.put(accumulatorId, 1);
					}
					if (accumulatorSummer.containsKey(accumulatorId)) {
						BigDecimal bd = accumulatorSummer.get(accumulatorId);
						bd=bd.add(new BigDecimal(val));
						accumulatorSummer.put(accumulatorId, bd);
					} else {
						accumulatorSummer.put(accumulatorId, new BigDecimal(val));
					}
				}
			}
			br.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// LOG.error(e);
		}
		// LOG.warn("FULL SIZE DA : " + daSnapshotMap.keySet().size());
	}
	
	private void readUsageCounterSnapshotFile(String dataFolderPath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(dataFolderPath));
			String line = "";
			while ((line = br.readLine()) != null) {
				String dats[] = line.split(",", -6);
				if (dats.length > 4) {
					String ucid = dats[1];

					if (targetUcCounter.containsKey(ucid)) {
						int counter = targetUcCounter.get(ucid);
						counter += 1;
						targetUcCounter.put(ucid, counter);
					} else {
						targetUcCounter.put(ucid, 1);
					}
					if (targetUcSummer.containsKey(ucid)) {
						//String bal = "";
						BigDecimal origBal = targetUcSummer.get(ucid);
						BigDecimal actualBal = new BigDecimal(dats[3]);
						BigDecimal val = origBal.add(actualBal);
						targetUcSummer.put(ucid, val);
					} else {
						BigDecimal actualBal = new BigDecimal(dats[3]);
						targetUcSummer.put(ucid, actualBal);
					}
				}
			}
			br.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// LOG.error(e);
		}
		// LOG.warn("FULL SIZE DA : " + daSnapshotMap.keySet().size());
	}
	
	private void readBundleTypeSnapshotFile(String outputfolder) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(outputfolder+"/CIS_Renewal_Bundles.csv"));
			String line = "";
			while ((line = br.readLine()) != null) {
				String dats[] = line.split(",", -15);
				if (dats.length > 15) {
					String prodId = dats[11];
					String key = "CIS_Renewal_Bundles,"+prodId;
					if (bundleTypeCounter.containsKey(key)) {
						int counter = bundleTypeCounter.get(key);
						counter += 1;
						bundleTypeCounter.put(key, counter);
					} else {
						bundleTypeCounter.put(key, 1);
					}
				}
			}
			br.close();
			
			br = new BufferedReader(new FileReader(outputfolder+"/CIS_OnceOff_Bundles.csv"));
		    line = "";
			while ((line = br.readLine()) != null) {
				String dats[] = line.split(",", -15);
				if (dats.length > 15) {
					String prodId = dats[2];
					String key = "CIS_OnceOff_Bundles,"+prodId;
					if (bundleTypeCounter.containsKey(key)) {
						int counter = bundleTypeCounter.get(key);
						counter += 1;
						bundleTypeCounter.put(key, counter);
					} else {
						bundleTypeCounter.put(key, 1);
					}
				}
			}
			br.close();
			

			br = new BufferedReader(new FileReader(outputfolder+"/CIS_Parking_Products.csv"));
		    line = "";
			while ((line = br.readLine()) != null) {
				String dats[] = line.split(",", -15);
				if (dats.length > 15) {
					String prodId = dats[2];
					String key = "CIS_Parking_Products,"+prodId;
					if (bundleTypeCounter.containsKey(key)) {
						int counter = bundleTypeCounter.get(key);
						counter += 1;
						bundleTypeCounter.put(key, counter);
					} else {
						bundleTypeCounter.put(key, 1);
					}
				}
			}
			br.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// LOG.error(e);
		}
		// LOG.warn("FULL SIZE DA : " + daSnapshotMap.keySet().size());
	}
	

	private void readFafSnapshotFile(String dataFolderPath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(dataFolderPath));
			String line = "";
			while ((line = br.readLine()) != null) {
				String dats[] = line.split(",", -6);
				if (dats.length > 3) {
					String fafIndicator = dats[2];

					if (fafCounter.containsKey(fafIndicator)) {
						int counter = fafCounter.get(fafIndicator);
						counter += 1;
						fafCounter.put(fafIndicator, counter);
					} else {
						fafCounter.put(fafIndicator, 1);
					}
				}
			}
			br.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// LOG.error(e);
		}
		// LOG.warn("FULL SIZE DA : " + daSnapshotMap.keySet().size());
	}

	private void readSnaSnapshotFile(String dataFolderPath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(dataFolderPath));
			String line = "";
			while ((line = br.readLine()) != null) {
				String dats[] = line.split(",", -6);
				if (dats.length > 4) {
					String msidn = dats[1];
					String rp = dats[4];
					String submsisdn = msidn.substring(0, 6);
					String key = submsisdn+"-"+rp;
					

					if (snaCounter.containsKey(key)) {
						int counter = snaCounter.get(key);
						counter += 1;
						snaCounter.put(key, counter);
					} else {
						snaCounter.put(key, 1);
					}
				}
			}
			br.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// LOG.error(e);
		}
		// LOG.warn("FULL SIZE DA : " + daSnapshotMap.keySet().size());
	}
	
	public static Map<String,String> fileNamesFromFolder(String snapshotDir){
		Map<String,String> result = new HashMap<String, String>();
		File directory = new File(snapshotDir);
		for(File file : directory.listFiles()){
			
			if(file.getName().contains("DUMP_offer.")){
				result.put("OFFER",file.getName());
				//LOG.warn("FILES : "+file.getName());
			}
			else if(file.getName().contains("DUMP_offerattribute.")){
				result.put("OFFERATTR",file.getName());
				//LOG.warn("FILES : "+file.getName());
			}
			else if(file.getName().contains("DUMP_dedicatedaccount.")){
				result.put("DA",file.getName());
			}
			else if(file.getName().contains("DUMP_subscriber_offer.")){
				result.put("SUBOFFER",file.getName());
				//LOG.warn("FILES : "+file.getName());
			}
			else if(file.getName().contains("DUMP_usage_counter.")){
				result.put("UC",file.getName());
			}
			else if(file.getName().contains("DUMP_usage_threshold.")){
				result.put("UT",file.getName());
			}
			else if(file.getName().contains("DUMP_subscriber.")){
				result.put("SUB",file.getName());
				//LOG.warn("FILES : "+file.getName());
			}
			else if(file.getName().contains("DUMP_provider_usage_counter.")){
				result.put("PUC",file.getName());
			}
			else if(file.getName().contains("DUMP_tree_parameters_account.")){
				result.put("OFFERTREE",file.getName());
			}
			else if(file.getName().contains("DUMP_faflistSub.")){
				result.put("FAF",file.getName());
			}
			else if(file.getName().contains("service_number")){
				result.put("SNA",file.getName());
			}
			
			
			
		}
		
		return result;
	 }
}