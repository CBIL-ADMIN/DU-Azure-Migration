package com.ericsson.dm.scriptTransformation;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.ericsson.dm.Utils.CommonUtilities;
import com.ericsson.dm.inititialization.LoadSubscriberMapping;
import com.ericsson.dm.transformation.ExecuteTransformation;

public class ScriptOutputTransformation {
	private Set<String> offerListBuffer;
	private List<String> offerAttributeListBuffer;
	private List<String> pamListBuffer;
	private List<String> accumulatorBuffer;
	private List<String> daBuffer;
	private List<String> cisOnceOff;
	private List<String> cisRenewal;
	
	private int uniqueNumber;
	private String pathtoApplicationContext;
	private String pathOfOutputFolder;
	
	
	private static final String DACOLUMNS = "ID_1,BALANCE_1,EXPIRY_DATE_1,ID_2,BALANCE_2,EXPIRY_DATE_2,ID_3,BALANCE_3,EXPIRY_DATE_3,ID_4,BALANCE_4,EXPIRY_DATE_4,ID_5,BALANCE_5,EXPIRY_DATE_5,ID_6,BALANCE_6,EXPIRY_DATE_6,ID_7,BALANCE_7,EXPIRY_DATE_7,ID_8,BALANCE_8,EXPIRY_DATE_8,ID_9,BALANCE_9,EXPIRY_DATE_9,ID_10,BALANCE_10,EXPIRY_DATE_10,START_DATE_10,PRODUCT_ID_1,PRODUCT_ID_2,PRODUCT_ID_3,PRODUCT_ID_4,START_DATE_1,PRODUCT_ID_10,PRODUCT_ID_5,START_DATE_2,PRODUCT_ID_6,START_DATE_3,PRODUCT_ID_7,START_DATE_4,PRODUCT_ID_8,START_DATE_5,PRODUCT_ID_9,START_DATE_6,START_DATE_7,START_DATE_8,START_DATE_9,PAM_SERVICE_ID_1,PAM_SERVICE_ID_2,PAM_SERVICE_ID_3,PAM_SERVICE_ID_4,PAM_SERVICE_ID_5,PAM_SERVICE_ID_6,PAM_SERVICE_ID_7,PAM_SERVICE_ID_8,PAM_SERVICE_ID_9,PAM_SERVICE_ID_10";
	private static final String DACOLUMNS2 = "ID_11,BALANCE_11,EXPIRY_DATE_11,ID_12,BALANCE_12,EXPIRY_DATE_12,ID_13,BALANCE_13,EXPIRY_DATE_13,ID_14,BALANCE_14,EXPIRY_DATE_14,ID_15,BALANCE_15,EXPIRY_DATE_15,ID_16,BALANCE_16,EXPIRY_DATE_16,ID_17,BALANCE_17,EXPIRY_DATE_17,ID_18,BALANCE_18,EXPIRY_DATE_18,ID_19,BALANCE_19,EXPIRY_DATE_19,ID_20,BALANCE_20,EXPIRY_DATE_20,START_DATE_20,PRODUCT_ID_11,PRODUCT_ID_12,PRODUCT_ID_13,PRODUCT_ID_14,START_DATE_11,PRODUCT_ID_20,PRODUCT_ID_15,START_DATE_12,PRODUCT_ID_16,START_DATE_13,PRODUCT_ID_17,START_DATE_14,PRODUCT_ID_18,START_DATE_15,PRODUCT_ID_19,START_DATE_16,START_DATE_17,START_DATE_18,START_DATE_19,PAM_SERVICE_ID_11,PAM_SERVICE_ID_12,PAM_SERVICE_ID_13,PAM_SERVICE_ID_14,PAM_SERVICE_ID_15,PAM_SERVICE_ID_16,PAM_SERVICE_ID_17,PAM_SERVICE_ID_18,PAM_SERVICE_ID_19,PAM_SERVICE_ID_20";
	private static final String ACMCOLUMNS = "ACCOUNT_ID,SEQUENCE_ID,ID_1,VALUE_1,CLEARING_DATE_1,ID_2,VALUE_2,CLEARING_DATE_2,ID_3,VALUE_3,CLEARING_DATE_3,ID_4,VALUE_4,CLEARING_DATE_4,ID_5,VALUE_5,CLEARING_DATE_5,ID_6,VALUE_6,CLEARING_DATE_6,ID_7,VALUE_7,CLEARING_DATE_7,ID_8,VALUE_8,CLEARING_DATE_8,ID_9,VALUE_9,CLEARING_DATE_9,ID_10,VALUE_10,CLEARING_DATE_10";
	
	public ScriptOutputTransformation(String pathOfOutputFolder, final String pathtoApplicationContext) {
		this.pathOfOutputFolder = pathOfOutputFolder;
		
		this.pathtoApplicationContext = pathtoApplicationContext;
		offerListBuffer = new HashSet<>();
		offerAttributeListBuffer = new ArrayList<>();
		pamListBuffer = new ArrayList<>();
		daBuffer = new ArrayList<>();
		cisOnceOff = new ArrayList<>();
		cisRenewal = new ArrayList<>();
		accumulatorBuffer= new ArrayList<>();
		
		uniqueNumber = LoadSubscriberMapping.rand.nextInt(1000000);
	}
	
	public void generateOffer(String offer)
	{
		//971527083620,4602,NULL,NULL,NULL,0,Timer,OnceOff
		offerListBuffer.add(PopulateOffer(offer.split(",",-1)[0],offer.split(",",-1)[1],offer.split(",",-1)[6],
				offer.split(",",-1)[2], offer.split(",",-1)[3],offer.split(",",-1)[4],offer.split(",",-1)[5]));
		
		if(!offer.split(",",-1)[7].isEmpty() && offer.split(",",-1)[7].equals("OnceOff"))
		{
			//cisOnceOff.add(PopulateCISRenewal(Balance_ID,Offer_ID,Balance_StartDate, Balance_ExpiryDate));
		}
		else if(!offer.split(",",-1)[7].isEmpty() && offer.split(",",-1)[7].equals("Renewal"))
		{
			//cisRenewal.add(PopulateCISRenewal(Balance_ID,Offer_ID,Balance_StartDate, Balance_ExpiryDate));
		}
		
	}
	
	private String PopulateOffer(String msisdn, String offer_ID,String Offer_Type, String Balance_StartDate,
			String Balance_ExpiryDate, String flag, String Product_Private) {
		
		
			String Offer_Startdate = "";
			String Offer_StartSec= "";
			String Offer_Expirydate= "";
			String Offer_ExpirySec= "";
			
			if(Balance_StartDate.equals("1970-01-01 00:00:00"))
				Balance_StartDate = "";
			
			if(Balance_ExpiryDate.equals("1970-01-01 00:00:00"))
				Balance_ExpiryDate = "";
			
			if(Offer_Type.toUpperCase().equals("TIMER"))
			{
				if(!Balance_StartDate.equals("NULL"))
				{
					Offer_Startdate = CommonUtilities.convertDateToTimerOfferDate(Balance_StartDate)[0].toString();
					Offer_StartSec = CommonUtilities.convertDateToTimerOfferDate(Balance_StartDate)[1].toString();								
				}
				else
				{
					Offer_Startdate = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();
					Offer_StartSec = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();
				}
				if(!Balance_ExpiryDate.equals("NULL"))
				{
					Offer_Expirydate = CommonUtilities.convertDateToTimerOfferDate(Balance_ExpiryDate)[0].toString(); 
					Offer_ExpirySec = CommonUtilities.convertDateToTimerOfferDate(Balance_ExpiryDate)[1].toString(); 
				}
				else
				{
					Offer_Expirydate = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();
					Offer_ExpirySec = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();
				}
			}
			else
			{
				if(!Balance_StartDate.equals("NULL"))
				{
					Offer_Startdate = CommonUtilities.convertDateToTimerOfferDate(Balance_StartDate)[0].toString();
					Offer_StartSec = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();								
				}
				else
				{
					Offer_Startdate = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();
					Offer_StartSec = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();
				}
				if(!Balance_ExpiryDate.equals("NULL"))
				{
					Offer_Expirydate = CommonUtilities.convertDateToTimerOfferDate(Balance_ExpiryDate)[0].toString(); 
					Offer_ExpirySec = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString(); 
				}
				else
				{
					Offer_Expirydate = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();
					Offer_ExpirySec = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();
				}
			}			
			
			StringBuffer sb = new StringBuffer();
			sb.append(msisdn).append(",");
			sb.append(offer_ID).append(",");
			sb.append(Offer_Startdate).append(",");
			sb.append(Offer_Expirydate).append(",");
			sb.append(Offer_StartSec).append(",");
			sb.append(Offer_ExpirySec).append(",");
			if(flag.length() == 1)
			{
				sb.append(flag).append(",");
			}
			else
				sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
			sb.append(Product_Private);
		
		// TODO Auto-generated method stub
		return sb.toString();
	}
	
	public void generateOfferAttr(String offerAttr)
	{
		//MSISDN, OFFER_ID, ATTRIBUTE_DEF_ID, ATTRIBUTE_VALUE, ATTRIBUTE_VALUE_TYPE
		String hexValue = "";
		try {
			if(Integer.parseInt(offerAttr.split(",",-1)[4]) == 4)
				hexValue = CommonUtilities.toHexadecimal(offerAttr.split(",",-1)[3]);
			
		
			offerAttributeListBuffer.add(offerAttr.split(",",-1)[0]+ "," + offerAttr.split(",",-1)[2] + "," + offerAttr.split(",",-1)[3] + "," + hexValue + "," +  LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void generateDedicated(String DA)
	{
		
	}
	
	public void generateAccum(String ACC)
	{
		
	}
	
	public void generateCsv() {
		// TODO Auto-generated method stub
		
		try {
			File offerFile = new File(this.pathOfOutputFolder + "Offer_" + uniqueNumber + ".csv");
			File offerAttrFile = new File(this.pathOfOutputFolder + "OfferAttribute_" + uniqueNumber + ".csv");
			File accumulatorFile = new File(this.pathOfOutputFolder + "Accumulator_" + uniqueNumber + ".csv");
			File cisOnceOffFile = new File(this.pathOfOutputFolder + "CIS_OnceOff_Bundles_" + uniqueNumber + ".csv");
			File cisRenewalFile = new File(this.pathOfOutputFolder + "CIS_Renewal_Bundles_" + uniqueNumber + ".csv");
			File daFile = new File(this.pathOfOutputFolder + "DedicatedAccount_" + uniqueNumber + ".csv");
							
			if (!offerFile.exists()) {
				offerFile.createNewFile();
			}
			if (!offerAttrFile.exists()) {
				offerAttrFile.createNewFile();
			}
			if (!daFile.exists()) {
				daFile.createNewFile();
			}
			if (!accumulatorFile.exists()) {
				accumulatorFile.createNewFile();
			}
			if (!cisOnceOffFile.exists()) {
				cisOnceOffFile.createNewFile();
			}
			if (!cisRenewalFile.exists()) {
				cisRenewalFile.createNewFile();
			}
			
			FileUtils.writeLines(offerFile, this.offerListBuffer, true);
			FileUtils.writeLines(offerAttrFile, this.offerAttributeListBuffer, true);
			FileUtils.writeLines(daFile, this.daBuffer, true);
			FileUtils.writeLines(accumulatorFile, this.accumulatorBuffer, true);
			FileUtils.writeLines(cisOnceOffFile, this.cisOnceOff, true);
			FileUtils.writeLines(cisRenewalFile, this.cisRenewal, true);			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();				
		}
	}
	
	public static void main(String[] args) {
		
		System.out.println("Start the Execution!!!");
		
		String sdpid, configPath, dataFolderPath, workingPath, output, pathtoApplicationContext, pathOfLogFolder = null;
		
		dataFolderPath = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\data";
		configPath = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\config\\config";
		workingPath = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\working\\";
		output = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\output\\";
		pathtoApplicationContext = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\";
		pathOfLogFolder = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\logs\\";
		//String filesToBeInserted = "INFILE_Subscriber_Balances.csv.gz,INFILE_Subscriber_cug_cli.csv.gz,INFILE_Subscriber_USMS.csv.gz";
		//String filesToBeInserted = "sdp01_subscriber_balances_dump.csv.gz,sdp01_subscriber_cugcli_dump.csv.gz,sdp01_subscriber_usms_dump.csv.gz";
		sdpid = "LOAD_MAPPING";
		
		LoadSubscriberMapping lsm = new LoadSubscriberMapping(sdpid, configPath, dataFolderPath, workingPath);
		
		ScriptOutputTransformation sot = new ScriptOutputTransformation(output, pathtoApplicationContext);
		
		String OfferInput = "971527083620,100,NULL,NULL,NULL,0,Timer,";
		//"971527083620,5022,2019-01-31 04:35:00,2019-02-28 04:35:00,NULL,100,Timer,";//"971527083620,4602,NULL,NULL,NULL,0,Timer,OnceOff";
		String DAInput = "";
		String OfferAttr = "971527083620,100,21,424030018042199,4";
		String Accumulator = "";
		
		sot.generateOffer(OfferInput);
		sot.generateOfferAttr(OfferAttr);
		sot.generateDedicated(DAInput);
		sot.generateAccum(Accumulator);	
		sot.generateCsv();
		
		System.out.println("Completed the Execution!!!");
		
	}
}
