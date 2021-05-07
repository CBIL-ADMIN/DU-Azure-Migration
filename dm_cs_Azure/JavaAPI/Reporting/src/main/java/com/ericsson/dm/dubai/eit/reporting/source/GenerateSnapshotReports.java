package com.ericsson.dm.dubai.eit.reporting.source;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ericsson.dm.Utils.CommonUtilities;
import com.ericsson.dm.dubai.eit.reporting.init.InitializeReporting;

public class GenerateSnapshotReports {
	
	public void GenerateSubscriberStatsReport(String reportDir){
	    Map<String,Integer> targetStateStats = new HashMap<String,Integer>();
	    Map<String,Integer> targetServiceClassStats = new HashMap<String,Integer>();
	    Map<String,Integer> targetStateServiceClassStats = new HashMap<String,Integer>();
	    Map<String,Integer> targetOfferStats = new HashMap<String,Integer>();
	    
	    
		for(String msisdn : InitializeReporting.subscriberDump.keySet()){
			String datas[] = InitializeReporting.subscriberDump.get(msisdn).split(",");
			String serviceClass = datas[0];
			String tempBlockFlag = datas[1];
			String activatedDate = datas[2];
			String disconnectedDate = datas[3];
			
			long disconnectiondate = 0;
			if(disconnectedDate!=null){
				CommonUtilities.convertDateToEpoch(disconnectedDate + " 00:00:00");
			}
			long migrationDate = CommonUtilities.convertDateToEpoch(InitializeReporting.commonConfigMap.get("migration_date"));
					
			boolean checkOffer552 = false,checkOffer553 = false;
			if(InitializeReporting.offerDump.containsKey(msisdn)){
				if(InitializeReporting.offerDump.get(msisdn).containsKey("552")){
					checkOffer552 = true;
				}
				else if(InitializeReporting.offerDump.get(msisdn).containsKey("553")){
					checkOffer553 = true;
				}
			}
			updateServiceClassStats( targetServiceClassStats,serviceClass);
			updateOfferCount( msisdn,targetOfferStats);

			if(tempBlockFlag!=null && tempBlockFlag.equals("1")){
				updateStateStats(targetStateStats,"S");
				updateServiceClassStateStats( targetStateServiceClassStats,  serviceClass, "S");
			}
			else if(activatedDate!=null){
				updateStateStats(targetStateStats,"P");
				updateServiceClassStateStats( targetStateServiceClassStats,  serviceClass, "P");
			}
			else if(checkOffer552==true){
				updateStateStats(targetStateStats,"F");
				updateServiceClassStateStats( targetStateServiceClassStats,  serviceClass, "F");
			}
			else if(checkOffer553==true){
				updateStateStats(targetStateStats,"D");
				updateServiceClassStateStats( targetStateServiceClassStats,  serviceClass, "D");
			}
			else if(disconnectiondate>migrationDate){
				updateStateStats(targetStateStats,"A");
				updateServiceClassStateStats( targetStateServiceClassStats,  serviceClass, "A");
			}
			else{
				updateStateStats(targetStateStats,"T");
				updateServiceClassStateStats( targetStateServiceClassStats,  serviceClass, "T");
			}
			
		}
		try {
			BufferedWriter bw = new BufferedWriter( new FileWriter(reportDir+"/target_state_stats.txt"));
			bw.write("STATE, TARGET_CNT\n");
			for(String key: targetStateStats.keySet()){
				bw.write(key+","+targetStateStats.get(key) + "\n" );
			}
			
			bw.close();
			bw = new BufferedWriter( new FileWriter(reportDir+"/target_sc_stats.txt"));
			bw.write("SERVICE_CLASS, TARGET_CNT\n");
			for(String key: targetServiceClassStats.keySet()){
				bw.write(key+","+targetServiceClassStats.get(key) + "\n" );
			}
			bw.close();
			bw = new BufferedWriter( new FileWriter(reportDir+"/target_sc_state_stats.txt"));
			bw.write("SERVICE_CLASS-STATE, TARGET_CNT\n");
			for(String key: targetStateServiceClassStats.keySet()){
				bw.write(key+","+targetStateServiceClassStats.get(key) + "\n" );
			}
			bw.close();
			bw = new BufferedWriter( new FileWriter(reportDir+"/target_offer_stats.txt"));
			bw.write("OFFER_ID, TARGET_CNT, MASTER_MAPPED_CNT\n");
			for(String key: targetOfferStats.keySet()){
				bw.write(key+","+targetOfferStats.get(key) +","+(InitializeReporting.offerCounter7001.get(key)!=null?InitializeReporting.offerCounter7001.get(key):0)+ "\n" );
			}
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	private void updateOfferCount(String msisdn, Map<String, Integer> targetOfferStats) {
		if(InitializeReporting.offerDump.containsKey(msisdn)){
			Map<String,String> offerIdsMap = InitializeReporting.offerDump.get(msisdn);
			for(String offerId : offerIdsMap.keySet()){
				if(targetOfferStats.containsKey(offerId)){
					int counter = targetOfferStats.get(offerId);
					counter+=1;
					targetOfferStats.put(offerId, counter);
				}
				else{
					targetOfferStats.put(offerId, 1);
				}
			}
		}
		
	}

	private void updateStateStats(Map<String, Integer> targetStateStats, String state) {
		
		if(targetStateStats.containsKey(state)){
			int counter = targetStateStats.get(state);
			counter +=1;
			targetStateStats.put(state, counter);
		}
		else{
			targetStateStats.put(state, 1);
		}
	}
	
private void updateServiceClassStats(Map<String, Integer> targetServiceClassStats, String serviceClass) {
		
		if(targetServiceClassStats.containsKey(serviceClass)){
			int counter = targetServiceClassStats.get(serviceClass);
			counter +=1;
			targetServiceClassStats.put(serviceClass, counter);
		}
		else{
			targetServiceClassStats.put(serviceClass, 1);
		}
	}

private void updateServiceClassStateStats(Map<String, Integer> targetServiceClassStats, String serviceClass,String state) {
	
	if(targetServiceClassStats.containsKey(serviceClass+"-"+state)){	
		int counter = targetServiceClassStats.get(serviceClass+"-"+state);
		counter +=1;
		targetServiceClassStats.put(serviceClass+"-"+state, counter);
	}
	else{
		targetServiceClassStats.put(serviceClass+"-"+state, 1);
	}
}

public void writeReports(String reportDir){
	
	writeOfferAttribute(reportDir);
	writeDAStats( reportDir);
	writeCugStats(reportDir);
	writeUCStats(reportDir);
	writeAccumulatorStats(reportDir);
	writeFafStats(reportDir);
	 writSnaStats( reportDir);
	 writeBundleStats(reportDir);
	
}

private void writeFafStats(String reportDir) {
	
	try {
		BufferedWriter bw = new BufferedWriter(new FileWriter(reportDir+"/target_faf_stats.txt"));
		bw.write("FAF-INDICATOR, TARGET_COUNT\n");
		for(String key: InitializeReporting.fafCounter.keySet()){
			bw.write(key + ","+ InitializeReporting.fafCounter.get(key)+"\n");
		}
		bw.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

private void writSnaStats(String reportDir) {
	
	try {
		BufferedWriter bw = new BufferedWriter(new FileWriter(reportDir+"/target_sna_stats.txt"));
		bw.write("SNASeries-RatePlanName, TARGET_SNA_COUNT\n");
		for(String key: InitializeReporting.snaCounter.keySet()){
			bw.write(key + ","+ InitializeReporting.snaCounter.get(key)+"\n");
		}
		bw.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

private void writeOfferAttribute(String reportDir) {
	
	try {
		BufferedWriter bw = new BufferedWriter(new FileWriter(reportDir+"/target_attr_stats.txt"));
		bw.write("OFFERID-ATTRNAME, TARGET_COUNT\n");
		for(String key: InitializeReporting.offerAttrDump.keySet()){
			bw.write(key + ","+ InitializeReporting.offerAttrDump.get(key)+"\n");
		}
		bw.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

private void writeDAStats(String reportDir) {
	
	try {
		BufferedWriter bw = new BufferedWriter(new FileWriter(reportDir+"/target_da_stats.txt"));
		bw.write("DA_ID, TARGET_CNT, TARGET_BALANCE_TOTAL, ADD_DA_CNT, ADD_DA_BALANCE_TOTAL\n");
		for(String key: InitializeReporting.targetDaCounter.keySet()){
			bw.write(key + ","+ InitializeReporting.targetDaCounter.get(key)+","+
		(InitializeReporting.daCounter7002.containsKey(key)?InitializeReporting.daCounter7002.get(key):"0")+",0"+"\n");
		}
		bw.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

private void writeUCStats(String reportDir) {
	
	try {
		BufferedWriter bw = new BufferedWriter(new FileWriter(reportDir+"/target_uc_stats.txt"));
		bw.write("UC_ID, TARGET_CNT, TARGET_VALUE_TOTAL, ADD_UC_CNT, ADD_UC_VALUE_TOTAL\n");
		for(String key: InitializeReporting.targetUcCounter.keySet()){
			bw.write(key + ","+ InitializeReporting.targetUcCounter.get(key)+","+InitializeReporting.targetUcSummer.get(key)+
		(InitializeReporting.ucCounter7003.containsKey(key)?InitializeReporting.ucCounter7003.get(key):"0")+",0"+"\n");
		}
		bw.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}


private void writeAccumulatorStats(String reportDir) {
	
	try {
		BufferedWriter bw = new BufferedWriter(new FileWriter(reportDir+"/target_accm_stats.txt"));
		bw.write("ACCM_ID, TARGET_CNT, TARGET_VALUE_TOTAL\n");
		for(String key: InitializeReporting.accumulatorCounter.keySet()){
			bw.write(key + ","+ InitializeReporting.accumulatorCounter.get(key)+","+
					InitializeReporting.accumulatorSummer.get(key)+"\n");
		}
		bw.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}


private void writeCugStats(String reportDir) {
	
	try {
		BufferedWriter bw = new BufferedWriter(new FileWriter(reportDir+"/target_cug_stats.txt"));
		bw.write("CUG_ID, TARGET_CUG_CNT\n");
		//String communityId1="0",communityId2="0",communityId3="0";
		//int counter =0 ;
		for(String key: InitializeReporting.cugStats.keySet()){
			bw.write(key + ","+ InitializeReporting.cugStats.get(key));
		}
		bw.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
	
private void writeBundleStats(String reportDir) {
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(reportDir+"/target_cis_stats.txt"));
			bw.write("BUNDLE_TYPE, PRODUCTID, TARGET_COUNT\n");
			//String communityId1="0",communityId2="0",communityId3="0";
			//int counter =0 ;
			for(String key: InitializeReporting.bundleTypeCounter.keySet()){
				bw.write(key + ","+ InitializeReporting.bundleTypeCounter.get(key));
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
}


}
