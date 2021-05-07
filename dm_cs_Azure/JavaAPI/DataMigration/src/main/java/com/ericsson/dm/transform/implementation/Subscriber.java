package com.ericsson.dm.transform.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ericsson.dm.Utils.CommonUtilities;
import com.ericsson.dm.Utils.Constants;
import com.ericsson.dm.inititialization.LoadSubscriberMapping;
import com.ericsson.dm.transformation.ExecuteTransformation;
import com.ericsson.jibx.beans.SubscriberXml;
import com.ericsson.jibx.beans.SubscriberList;

public class Subscriber {
	SubscriberXml subscriber;
	String INITIAL_ACTIVATION_DATE;
	Set<String> rejectAndLog;
	Set<String> onlyLog;
	CommonFunctions commonfunction;
	private Map<String, Set<String>> ProductIDLookUpMap;

	public Subscriber(SubscriberXml subscriber, Set<String> rejectAndLog, Set<String> onlyLog,String INITIAL_ACTIVATION_DATE) {
		// TODO Auto-generated constructor stub
		this.subscriber=subscriber;
		this.rejectAndLog=rejectAndLog;
		this.onlyLog=onlyLog;
		this.INITIAL_ACTIVATION_DATE = INITIAL_ACTIVATION_DATE;
		this.commonfunction = new CommonFunctions(subscriber, ProductIDLookUpMap,this.onlyLog);
	}
	public Collection<? extends String> execute() {
		// TODO Auto-generated method stub
		List<String> result = new ArrayList<String>();
		result = applyRulesSubscribers();
		
		
		return result;
	}

	private List<String> applyRulesSubscribers() {
		List<String> subsList =  new ArrayList<>();
		
		String msisdn = subscriber.getSubscriberInfoMSISDN();
		String serviceClass = subscriber.getSubscriberInfoSERVICESTATE();
		
		String INITIAL_ACTIVATION_DATE_FLAG;
		
		String subsStatus = "";
		String first_ivr_call_done = "";
		long first_call_done = 0;
		String Targetlanguage;
		
		if(INITIAL_ACTIVATION_DATE.length() == 0)
			INITIAL_ACTIVATION_DATE_FLAG = "Y";
		else
			INITIAL_ACTIVATION_DATE_FLAG = "N";
		
		String NPP_CCS_ACCT_TYPE_ID = LoadSubscriberMapping.LifeCycleMap.get(serviceClass+"|"+INITIAL_ACTIVATION_DATE_FLAG +"|N").split(",",-1)[15];
		String[] values = NPP_CCS_ACCT_TYPE_ID.split("#");
		
		if(Arrays.stream(values).anyMatch(subscriber.getSubscriberInfoCCSACCTTYPEID()::equals))
		{	
			String NPPResult =  commonfunction.FindNPPPBTItem(subscriber.getSubscriberInfoCCSACCTTYPEID(), serviceClass);
			if(NPPResult.isEmpty())
			{
				//rejectAndLog.add("INC1005:NPP_Lifecycle PBT_ID not found:MSISDN=" + msisdn + ":CCS_ACCT_TYPE_ID="+subscriber.getSubscriberInfoCCSACCTTYPEID()+ ":SERVICE_STATE=" + serviceClass + ":ACTION=Discard and log");
				//return accountList;
			}
			else
			{
				String SerialNumber = NPPResult.split("\\|",-1)[0];
				String BTExpiryDate = NPPResult.split("\\|",-1)[1];
				
				first_call_done = Long.parseLong(String.valueOf(CommonUtilities.convertDateToEpoch(INITIAL_ACTIVATION_DATE)));
				if(!SerialNumber.equals("0"))
				{
					first_ivr_call_done = LoadSubscriberMapping.NPPLifeCycleMap.get(SerialNumber).getFIRSTIVRCALLDONE();
					subsStatus = LoadSubscriberMapping.NPPLifeCycleMap.get(SerialNumber).getSUBSCRIBERSTATUS();
				}
				else
				{
					first_ivr_call_done = "1";
					subsStatus = "0"; 
				}
				
			}
		}
		else
		{
			if (!(INITIAL_ACTIVATION_DATE_FLAG == "Y" && (serviceClass == "P" || serviceClass == "S" ))) 
				subsStatus = LoadSubscriberMapping.LifeCycleMap.get(serviceClass+"|"+INITIAL_ACTIVATION_DATE_FLAG +"|N").split(",")[10];
			else
				subsStatus = "0";
			
			if (!(INITIAL_ACTIVATION_DATE_FLAG == "Y" && (serviceClass == "P" || serviceClass == "S" ))) 
				first_ivr_call_done = LoadSubscriberMapping.LifeCycleMap.get(serviceClass+"|"+INITIAL_ACTIVATION_DATE_FLAG +"|N").split(",")[8];
			else
				first_ivr_call_done = "0";
			
			if (!(INITIAL_ACTIVATION_DATE_FLAG == "Y" && (serviceClass == "P" || serviceClass == "S" ))) 
				first_call_done = CommonUtilities.convertDateToEpoch(INITIAL_ACTIVATION_DATE);
			else
				first_call_done = 0;
		}
		String SourceLanguage = "";
		if(subscriber.getProfiledumpInfoList().size() == 1)
			SourceLanguage = subscriber.getProfiledumpInfoList().get(0).getLanguageID();
		
		if(LoadSubscriberMapping.LanguageMap.get(SourceLanguage) == null)
		{
			Targetlanguage = LoadSubscriberMapping.CommonConfigMap.get("default_language");
			if(SourceLanguage.length()> 0)
				onlyLog.add("INC2002:Language Lookup failed:MSISDN=" + msisdn + ":LANGUAGE_ID=" + SourceLanguage + ":ACTION=Logging");
		}
		else
			Targetlanguage = LoadSubscriberMapping.LanguageMap.get(SourceLanguage);
		
		
		
		StringBuffer sb = new StringBuffer();
		sb.append(msisdn).append(",");
		sb.append(msisdn).append(",");
		sb.append(subsStatus).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_refill_failed")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_refill_bar_end")).append(",");
		sb.append(first_ivr_call_done).append(",");
		sb.append(first_call_done).append(",");
		sb.append(Targetlanguage).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_special_announc_played")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_sfee_warn_played")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_sup_warn_played")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_low_level_warn_played")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_wanted_block_status")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_actual_block_status")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_eoc_selection_id")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_pin_code")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_usage_statistic_flags"));
		
		subsList.add(sb.toString());
		sb = null;
		return subsList;
	}
}
