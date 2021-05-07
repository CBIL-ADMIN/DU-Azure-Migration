package com.ericsson.dm.transform.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ericsson.dm.Utils.CommonUtilities;
import com.ericsson.dm.inititialization.LoadSubscriberMapping;
import com.ericsson.dm.transformation.ExecuteTransformation;
import com.ericsson.jibx.beans.SubscriberXml;
import com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberusmsdumpInfo;

public class Account {
	SubscriberXml subscriber;	
	String INITIAL_ACTIVATION_DATE;
	Set<String> rejectAndLog;
	Set<String> onlyLog;
	boolean AccountProductIDFlag;
	String msisdn;
	CommonFunctions commonfunction;
	private Map<String, Set<String>> ProductIDLookUpMap;
	
	public Account(SubscriberXml subscriber, Set<String> rejectAndLog, Set<String> onlyLog,String INITIAL_ACTIVATION_DATE, boolean AccountProductIDFlag) {
		this.subscriber=subscriber;
		
		this.rejectAndLog=rejectAndLog;
		this.onlyLog=onlyLog;
		this.AccountProductIDFlag = AccountProductIDFlag;
		this.INITIAL_ACTIVATION_DATE = INITIAL_ACTIVATION_DATE;	
		this.msisdn = subscriber.getSubscriberInfoMSISDN();
		this.commonfunction = new CommonFunctions(subscriber, ProductIDLookUpMap,this.onlyLog);
	}


	public Collection<? extends String> execute() {
		// TODO Auto-generated method stub
		List<String> result = new ArrayList<String>();
		result = applyRulesAccount();		
		
		
		return result;
	}
	
	private List<String> applyRulesAccount() {
		List<String> accountList = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
		
		//String msisdn = subscriber.getSubscriberInfoMSISDN();
		String serviceClass = subscriber.getSubscriberInfoSERVICESTATE();
		String AccountClass = "";
		String MappingserviceClass = LoadSubscriberMapping.ServiceClassMap.get(subscriber.getSubscriberInfoCCSACCTTYPEID());
		String Type = MappingserviceClass.split("\\|")[0];
		String IgnoreFlag = MappingserviceClass.split("\\|")[1];
		String Target = MappingserviceClass.split("\\|")[2];
		if(IgnoreFlag.equals("N")){
			AccountClass = Target;
		}
		/*else{	
			onlyLog.add("INC4000:Service class Ignored :MSISDN=" + msisdn + ":CCS_ACCT_TYPE_ID=" + subscriber.getSubscriberInfoCCSACCTTYPEID() + ":SERVICE_STATE=" + serviceClass + ":ACTION=Logging");
		}*/
		
		//String INITIAL_ACTIVATION_DATE = subscriber.getUsmsdumpInfoINITIALACTIVATIONDATE();
		
		String INITIAL_ACTIVATION_DATE_FLAG;
		String WALLET_EXPIRY = subscriber.getSubscriberInfoWALLETEXPIRY();		
		if(INITIAL_ACTIVATION_DATE.length() == 0){
			INITIAL_ACTIVATION_DATE_FLAG = "Y";
		}
		else {
			INITIAL_ACTIVATION_DATE_FLAG = "N";
		}
		
		long actDate = 0;		
		long sfeedate = 0;
		long supdate = 0;

		String units = "0";
		String sfeeStatus = "0";
		String supStatus = "0";
		
		//Code modifed to accomodate NPP account Type as per mapping 
		
		String NPP_CCS_ACCT_TYPE_ID = LoadSubscriberMapping.LifeCycleMap.get(serviceClass+"|"+INITIAL_ACTIVATION_DATE_FLAG +"|N").split(",",-1)[15];
		String[] values = NPP_CCS_ACCT_TYPE_ID.split("#");
		
		if(Arrays.stream(values).anyMatch(subscriber.getSubscriberInfoCCSACCTTYPEID()::equals))
		{	
			String NPPResult =  commonfunction.FindNPPPBTItem(subscriber.getSubscriberInfoCCSACCTTYPEID(), serviceClass);
			if(NPPResult.isEmpty())
			{
				//rejectAndLog.add("INC1005:NPP_Lifecycle PBT_ID not found:MSISDN=" + msisdn + ":CCS_ACCT_TYPE_ID="+subscriber.getSubscriberInfoCCSACCTTYPEID()+ ":SERVICE_STATE=" + serviceClass + ":ACTION=Discard and log");
				
			}
			else
			{
				String SerialNumber = NPPResult.split("\\|",-1)[0];
				String BTExpiryDate = NPPResult.split("\\|",-1)[1];
				
				if (!(INITIAL_ACTIVATION_DATE_FLAG == "Y") && !(serviceClass == "P" || serviceClass == "S" )) 
					actDate = CommonUtilities.convertDateToEpoch(INITIAL_ACTIVATION_DATE);
				
				if(!SerialNumber.equals("0"))
				{
					sfeedate = CommonUtilities.convertDateToEpoch(BTExpiryDate) + Long.parseLong(LoadSubscriberMapping.NPPLifeCycleMap.get(SerialNumber).getSFEEEXPIRYDate());
					supdate = CommonUtilities.convertDateToEpoch(BTExpiryDate) + Long.parseLong(LoadSubscriberMapping.NPPLifeCycleMap.get(SerialNumber).getSFEEEXPIRYDate()) + Long.parseLong(LoadSubscriberMapping.NPPLifeCycleMap.get(SerialNumber).getSUFDAYS());
					supStatus = (LoadSubscriberMapping.NPPLifeCycleMap.get(SerialNumber).getSFEESTATUS());
					sfeeStatus = (LoadSubscriberMapping.NPPLifeCycleMap.get(SerialNumber).getSFEESTATUS());
				}
				else
				{
					String MAX_DATE = LoadSubscriberMapping.CommonConfigMap.get("migration_date");
					sfeedate = CommonUtilities.convertDateToEpoch(MAX_DATE) + 90;
					supdate = CommonUtilities.convertDateToEpoch(MAX_DATE) + 90;
					supStatus = "180";
					sfeeStatus = "0";
				}
			}
		}
		else
		{
			if (!(INITIAL_ACTIVATION_DATE_FLAG == "Y") && !(serviceClass == "P" || serviceClass == "S" )) 
				actDate = CommonUtilities.convertDateToEpoch(INITIAL_ACTIVATION_DATE);
				
			if (!(INITIAL_ACTIVATION_DATE_FLAG == "Y") && !(serviceClass == "P" || serviceClass == "S" )) 
			{
				String Value = LoadSubscriberMapping.ServiceClassMap.get(subscriber.getSubscriberInfoCCSACCTTYPEID());
				int ServiceFeePeriodLength = Integer.parseInt(Value.split("\\|")[4]);
				int SupervisionFeePeriodLength = Integer.parseInt(Value.split("\\|")[3]);
				if(WALLET_EXPIRY.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
				{	
					String MAX_DATE = LoadSubscriberMapping.CommonConfigMap.get("max_date").substring(1, 11) + " 00:00:00";
					sfeedate = CommonUtilities.convertDateToEpoch(MAX_DATE) + (ServiceFeePeriodLength - SupervisionFeePeriodLength);
				}			
				else
					sfeedate = CommonUtilities.convertDateToEpoch(WALLET_EXPIRY) + (ServiceFeePeriodLength - SupervisionFeePeriodLength);
			}
			else
				sfeedate = 0;
			
			if (!(INITIAL_ACTIVATION_DATE_FLAG == "Y") && !(serviceClass == "P" || serviceClass == "S" )) 
				if(WALLET_EXPIRY.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
				{	
					String MAX_DATE = LoadSubscriberMapping.CommonConfigMap.get("max_date").substring(1, 11) + " 00:00:00";
					supdate = CommonUtilities.convertDateToEpoch(MAX_DATE);
				}			
				else
					supdate = CommonUtilities.convertDateToEpoch(WALLET_EXPIRY);
			else
				supdate = 0;
			
			if (!(INITIAL_ACTIVATION_DATE_FLAG == "Y" && (serviceClass == "P" || serviceClass == "S" ))) 
			{			
				String Value = LoadSubscriberMapping.ServiceClassMap.get(subscriber.getSubscriberInfoCCSACCTTYPEID());
				supStatus = Value.split("\\|")[6];
				
				//Commented in PA14_1 as new logic came
				//sfeeStatus = LoadSubscriberMapping.LifeCycleMap.get(serviceClass+"|"+INITIAL_ACTIVATION_DATE_FLAG +"|N").split(",")[6];
			}
			else
				sfeeStatus = "0";
	
			
			if (!(INITIAL_ACTIVATION_DATE_FLAG == "Y" && (serviceClass == "P" || serviceClass == "S" ))) 
			{
				String Value = LoadSubscriberMapping.ServiceClassMap.get(subscriber.getSubscriberInfoCCSACCTTYPEID());
				supStatus = Value.split("\\|")[5];
				
				//Commented in PA14_1 as new logic came
				//supStatus = LoadSubscriberMapping.LifeCycleMap.get(serviceClass+"|"+INITIAL_ACTIVATION_DATE_FLAG +"|N").split(",")[4];
			}
			else
				supStatus = "0";
			
		}
		
		List<String> CommunityList = CreateCommunity();

		// validTo = ruleB(validTo);
		sb.append(msisdn).append(",");
		sb.append(AccountClass).append(",");
		sb.append(AccountClass).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
		sb.append(units).append(",");
		sb.append(actDate).append(",");
		sb.append(sfeedate).append(",");
		sb.append(supdate).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_SFEE_DONE_Date")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_PREVIOUS_SFEE_DONE_Date")).append(",");
		sb.append(sfeeStatus).append(",");
		sb.append(supStatus).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NEG_BALANCE_START")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NEG_BALANCE_BARRED")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ACCOUNT_DISCONNECT")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ACCOUNT_STATUS")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_PROM_NOTIFICATION")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_SERVICE_OFFERINGS")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ACCOUNT_GROUP_ID")).append(",");
		if(CommunityList.size() == 0)
		{
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO")).append(",");
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO")).append(",");
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO")).append(",");
		}
		else
		{
			for(int i =0;i<3;i++)
			{
				sb.append(CommunityList.get(i)).append(",");
			}		
		}
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_account_home_region")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_account_lock")).append(",");
		if(AccountProductIDFlag)
			sb.append("500");
		else
			sb.append("0");
		
		accountList.add(sb.toString());
		
		sb = null;
		return accountList;
	}


	private List<String> CreateCommunity() {
		List<String> communityList = new ArrayList<>();
		
		List<String> SortedCUGList = new ArrayList<>();
		subscriber.getCugclidumpInfoList().forEach(item->{
			SortedCUGList.add(item.getCUGNAME());});
		
		Collections.sort(SortedCUGList); 
		
		for(String cugname : SortedCUGList)
		{
			//if(cugname.matches("-?\\d+(\\.\\d+)?"))
			if(cugname.matches("[+-]?[0-9][0-9]*"))
			{
				communityList.add(cugname);
			}
			else
			{
				String CommunityID =LoadSubscriberMapping.CommunityMap.get(cugname);
				if( CommunityID != null)
					communityList.add(CommunityID);
				else
					onlyLog.add("INC5001:Community Lookup failed :MSISDN=" + msisdn + ":CUG_NAME=" + cugname + ":ACTION=Logging");
			}
		}
		// TODO Auto-generated method stub
		//Collections.sort(communityList);
		if(communityList.size() < 3)
		{
			int length = communityList.size();
			for(int i=0; i < (3-length); i++)
			{
				communityList.add("0");
			}
		}
		
		return communityList;
	}	
}
