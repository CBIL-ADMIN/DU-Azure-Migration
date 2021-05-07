package com.ericsson.dm.transform.implementation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ericsson.dm.inititialization.LoadSubscriberMapping;
import com.ericsson.dm.transformation.ExecuteTransformation;
import com.ericsson.jibx.beans.SubscriberXml;
import com.ericsson.jibx.beans.PROFILETAGLIST.PROFILETAGINFO;

public class ServiceNumberApplication {
	SubscriberXml subscriber;
	Set<String> rejectAndLog;
	Set<String> onlyLog;
	Set<String> trackLog;
    
	public ServiceNumberApplication(SubscriberXml subscriber,Set<String> rejectAndLog, Set<String> onlyLog, Set<String> trackLog) {
		// TODO Auto-generated constructor stub
		this.subscriber=subscriber;
		this.rejectAndLog=rejectAndLog;
		this.onlyLog=onlyLog;
		this.trackLog = trackLog;
	}
	public Collection<? extends String> execute() {
		// TODO Auto-generated method stub
		
		String msisdn = subscriber.getSubscriberInfoMSISDN();
		List<String> SNA = new ArrayList<String>();
		
		if(subscriber.getProfiledumpInfoList().size() == 1)
		{
			String SNAValue = generateServiceNumberApplication(msisdn);
			if(SNAValue.length() != 0)
				SNA.add(SNAValue);			
		}		
		return SNA;
	}
	private String generateServiceNumberApplication(String msisdn) {
						
		String ServiceState = subscriber.getSubscriberInfoSERVICESTATE();
		String CCS_ACT = subscriber.getSubscriberInfoCCSACCTTYPENAME();
		String DestinationNumber = "";
		if(msisdn.length()>=6)
		{
			String TranslatedNumber = subscriber.getProfiledumpInfoList().get(0).getTranslatedNumber();
			String TransNumber = subscriber.getProfiledumpInfoList().get(0).getTransNumber();
			
			if(msisdn.substring(0, 6).equals("971800") || msisdn.substring(0, 6).equals("971600"))
			{
				DestinationNumber = TranslatedNumber;
				if(TranslatedNumber.length() == 0)
					rejectAndLog.add("INC9002:SNA Destination number is empty:MSISDN=" + msisdn + ":Rate_Plan=" + CCS_ACT+ ":ACTION=Logging");
			}
			else if(msisdn.substring(0, 6).equals("971900"))
			{
				DestinationNumber = TransNumber;
				if(TransNumber.length() == 0)
					rejectAndLog.add("INC9002:SNA Destination number is empty:MSISDN=" + msisdn + ":Rate_Plan=" + CCS_ACT+ ":ACTION=Logging");
			}
			else
			{	
				
				if(TranslatedNumber.length() != 0)
				{
					DestinationNumber = TranslatedNumber;
					onlyLog.add("INC9001:SNA Number series is different:MSISDN=" + msisdn + ":TranslatedNumber=" + TranslatedNumber+ ":ACTION=Logging");
				}
				else if(TransNumber.length() != 0)
				{
					DestinationNumber = TransNumber;
					onlyLog.add("INC9001:SNA Number series is different:MSISDN=" + msisdn + ":TransNumber=" + TransNumber+ ":ACTION=Logging");
				}
				else if(TranslatedNumber.length() == 0 && TranslatedNumber.length() == 0)
				{
					rejectAndLog.add("INC9002:SNA Destination number is empty:MSISDN=" + msisdn + ":Rate_Plan=" + CCS_ACT+ ":ACTION=Logging");
				}					
			}
			if(DestinationNumber.length() != 0)
			{
				StringBuilder sb = new StringBuilder();
				sb.append("SNA-"+msisdn).append(",");
				sb.append(msisdn).append(",");
				sb.append(DestinationNumber).append(",");
				if(ServiceState.equals("A"))
				{				
					sb.append("Active").append(",");
				}
				else
				{
					sb.append("Deactive").append(",");
					trackLog.add("INC9003:SNA State is different:MSISDN=" + msisdn + ":SERVICE_STATE=" + ServiceState+ ":ACTION=Logging");
				}
				
				sb.append(subscriber.getSubscriberInfoCCSACCTTYPENAME()).append(",");
				sb.append(subscriber.getUsmsdumpInfoList().get(0).getINITIALACTIVATIONDATE());
				return sb.toString().trim();
			}
		}
		else
			rejectAndLog.add("INC9004:SNA length is less than six Digit:MSISDN=" + msisdn + ":SERVICE_STATE=" + ":Rate_Plan=" + CCS_ACT+ ":ACTION=Logging");
		
		return "";
	}	
}
