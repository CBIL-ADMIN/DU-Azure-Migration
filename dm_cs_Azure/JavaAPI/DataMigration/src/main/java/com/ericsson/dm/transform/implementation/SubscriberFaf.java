package com.ericsson.dm.transform.implementation;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ericsson.dm.Utils.CommonUtilities;
import com.ericsson.dm.inititialization.LoadSubscriberMapping;
import com.ericsson.dm.transformation.ExecuteTransformation;
import com.ericsson.jibx.beans.SubscriberXml;
import com.ericsson.jibx.beans.PROFILETAGLIST.PROFILETAGINFO;

public class SubscriberFaf {
	String msisdn;
	SubscriberXml subscriber;
	Set<String> rejectAndLog;
	Set<String> onlyLog;
	ProfileTagProcessing profileTag;
    
	public SubscriberFaf(SubscriberXml subscriber,Set<String> rejectAndLog, Set<String> onlyLog) {
		// TODO Auto-generated constructor stub
		this.subscriber=subscriber;
		this.rejectAndLog=rejectAndLog;
		this.onlyLog=onlyLog;
		profileTag = new ProfileTagProcessing(subscriber,this.onlyLog);
	}
	public Collection<? extends String> execute() {
		// TODO Auto-generated method stub
		msisdn = subscriber.getSubscriberInfoMSISDN();
		
		List<String> result = new ArrayList<String>();
		result.addAll(GenerateFromProfileTag());
	
		return result;
	}
	private List<String> SNAFromProfileTag_old()
	{
		List<String> ProfileTagFAF = new ArrayList<>();
		Date currDate = new Date();
		SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if(subscriber.getProfiledumpInfoList().size() == 0)
			return ProfileTagFAF;
		
		for(String itr : LoadSubscriberMapping.Profile_Tags_MappingWithGroup.keySet())
		{
			PROFILETAGINFO profileMappingValue = LoadSubscriberMapping.Profile_Tags_MappingWithGroup.get(itr);
			String Symbol = profileMappingValue.getSymbols();
			String TargetName = profileMappingValue.getProfileTagName();
			String TargetValue = profileMappingValue.getProfileTagValue();
			String IgnoreFlag =  profileMappingValue.getIgnoreFlag();
			String GroupName = itr.split(",")[1];
						
			if(IgnoreFlag.equals("N"))
			{
								
				//***************logic for Dummy1, dummy2 and Dummy3
				String Profile_Value = profileTag.GetProfileTagValue(itr.split(",")[0]);
				
				if(TargetName.equals("BstrVNNRecur") && !Profile_Value.isEmpty())
				{
					if(Symbol.equals("="))
					{
						if(TargetValue.equals(Profile_Value))
						{
							List<String> PT_Values = new ArrayList<>();
							List<String> ValidPTCheck = new ArrayList<>();
							PT_Values = Arrays.asList(profileMappingValue.getAdditionalPTCheck().split("#"));
							
							for(String pt : PT_Values)
							{
								String profileTagName = pt.split("-")[0];
								String profileTagSymbol = pt.split("-")[1];
								String profileTagValue =  pt.split("-")[2];
								if(profileTagSymbol.equals(">="))
								{
									String additionalProfileValue = profileTag.GetProfileTagValue(profileTagName);
									String DateValue = "";
									if(additionalProfileValue.length() == 14)
									{
										DateValue = additionalProfileValue.substring(0,4) + "-" + additionalProfileValue.substring(4,6) + "-" + additionalProfileValue.substring(6,8) + " " + additionalProfileValue.substring(8,10) + ":" + additionalProfileValue.substring(10,12) + ":" + additionalProfileValue.substring(12,14);
									
										if(CommonUtilities.convertDateToEpoch(DateValue) <= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
										{
											break;
										}
										else
										{
											ValidPTCheck.add(profileTagName);
										}
									}
								}
								if(profileTagSymbol.equals("="))
								{
									String additionalProfileValue = profileTag.GetProfileTagValue(profileTagName);
									if(profileTagValue.equals(additionalProfileValue))
									{
										ValidPTCheck.add(profileTagName);
									}
									else
									{
										break;
									}
								}
								if(profileTagSymbol.equals(">"))
								{
									String additionalProfileValue = profileTag.GetProfileTagValue(profileTagName);
									if(Double.parseDouble(additionalProfileValue) > Double.parseDouble(profileTagValue) )
									{
										ValidPTCheck.add(profileTagName);
									}
									else
									{
										break;
									}
								}	
								if(profileTagSymbol.equals("!="))
								{
									String additionalProfileValue = profileTag.GetProfileTagValue(profileTagName);
									if(!additionalProfileValue.isEmpty())
									{
										if(!additionalProfileValue.equals("#"))
											ValidPTCheck.add(profileTagName);
										else
											break;
									}
									else
									{
										break;
									}
								}	
							}
							if(PT_Values.size() == ValidPTCheck.size())
							{
								String FAFProfileValue = profileTag.GetProfileTagValue(profileMappingValue.getFafCalledNumber().split("\\.")[0]);
								List<String> NatNumuberList = new ArrayList<>(Arrays.asList(FAFProfileValue.split("#")));
								for(String s : NatNumuberList)
								{
									
									StringBuilder sb = new StringBuilder();
									sb.append(msisdn).append(",");
									sb.append(removeCharacterFromStart(s)).append(",");
									sb.append(profileMappingValue.getFafIndicator()).append(",");
									sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO")).append(",");
									sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL"));
									ProfileTagFAF.add(sb.toString());
								}	
							}
						}
					}					
				}
				
				if(TargetName.equals("BstrVINRecur") && !Profile_Value.isEmpty())
				{
					if(Symbol.equals("="))
					{
						if(TargetValue.equals(Profile_Value))
						{
							List<String> PT_Values = new ArrayList<>();
							List<String> ValidPTCheck = new ArrayList<>();
							PT_Values = Arrays.asList(profileMappingValue.getAdditionalPTCheck().split("#"));
							
							for(String pt : PT_Values)
							{
								String profileTagName = pt.split("-")[0];
								String profileTagSymbol = pt.split("-")[1];
								String profileTagValue =  pt.split("-")[2];
								if(profileTagSymbol.equals(">="))
								{
									String additionalProfileValue = profileTag.GetProfileTagValue(profileTagName);
									String DateValue = "";
									if(additionalProfileValue.length() == 14)
									{
										DateValue = additionalProfileValue.substring(0,4) + "-" + additionalProfileValue.substring(4,6) + "-" + additionalProfileValue.substring(6,8) + " " + additionalProfileValue.substring(8,10) + ":" + additionalProfileValue.substring(10,12) + ":" + additionalProfileValue.substring(12,14);
									
										if(CommonUtilities.convertDateToEpoch(DateValue) <= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
										{
											break;
										}
										else
										{
											ValidPTCheck.add(profileTagName);
										}
									}
								}
								if(profileTagSymbol.equals("="))
								{
									String additionalProfileValue = profileTag.GetProfileTagValue(profileTagName);
									if(profileTagValue.equals(additionalProfileValue))
									{
										ValidPTCheck.add(profileTagName);
									}
									else
									{
										break;
									}
								}
								if(profileTagSymbol.equals(">"))
								{
									String additionalProfileValue = profileTag.GetProfileTagValue(profileTagName);
									if(!additionalProfileValue.isEmpty())
									{
										if(Double.parseDouble(additionalProfileValue) > Double.parseDouble(profileTagValue))
										{
											ValidPTCheck.add(profileTagName);
										}
										else
										{
											break;
										}
									}
								}
								if(profileTagSymbol.equals("!="))
								{
									String additionalProfileValue = profileTag.GetProfileTagValue(profileTagName);
									if(!additionalProfileValue.isEmpty())
									{
										if(!additionalProfileValue.equals("#"))
											ValidPTCheck.add(profileTagName);
										else
											break;
									}
									else
									{
										break;
									}
								}
							}
							if(PT_Values.size() == ValidPTCheck.size())
							{
								String FAFProfileValue = profileTag.GetProfileTagValue(profileMappingValue.getFafCalledNumber().split("\\.")[0]);
								List<String> NatNumuberList = new ArrayList<>(Arrays.asList(FAFProfileValue.split("#")));
								for(String s : NatNumuberList)
								{
									
									StringBuilder sb = new StringBuilder();
									sb.append(msisdn).append(",");
									sb.append(removeCharacterFromStart(s.trim())).append(",");
									sb.append(profileMappingValue.getFafIndicator()).append(",");
									sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO")).append(",");
									sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL"));
									ProfileTagFAF.add(sb.toString());
								}	
							}
						}
					}					
				}				
			}
			else
			{
				//log for ignore
			}
		}
		return ProfileTagFAF;
	}
	
	
	private Collection<? extends String> GenerateFromProfileTag() 
	{
		List<String> ProfileTagFAF = new ArrayList<>();
		String msisdn = subscriber.getSubscriberInfoMSISDN();
		
		if(subscriber.getProfiledumpInfoList().size() == 0)
			return ProfileTagFAF;
		
		String BstrVceNatNumTree = subscriber.getProfiledumpInfoList().get(0).getBstrVceNatNumTree();
		if(BstrVceNatNumTree.length() !=0)
		{
			
			List<String> NatNumuberList = new ArrayList<>(Arrays.asList(BstrVceNatNumTree.split("#")));
			for(String s : NatNumuberList)
			{
				
				StringBuilder sb = new StringBuilder();
				sb.append(msisdn).append(",");
				sb.append(removeCharacterFromStart(s)).append(",");
				sb.append("100").append(",");
				sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO")).append(",");
				sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL"));
				ProfileTagFAF.add(sb.toString());
			}			
		}
		
		String BstrVceIntNumTree = subscriber.getProfiledumpInfoList().get(0).getBstrVceIntNumTree();
		if(BstrVceIntNumTree.length() != 0)
		{
			
			List<String> NatNumuberList = new ArrayList<>(Arrays.asList(BstrVceIntNumTree.split("#")));
			
			for(String s : NatNumuberList)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(msisdn).append(",");
				sb.append(removeCharacterFromStart(s)).append(",");
				sb.append("200").append(",");
				sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO")).append(",");
				sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL"));
				ProfileTagFAF.add(sb.toString());
			}			
		}
		
		return ProfileTagFAF;
	}
	
	public static String removeCharacterFromStart(String str) 
    { 
		StringBuffer sb = new StringBuffer(str); 
		if(str.startsWith("+"))
		{
			sb.replace(0,1, ""); 
		}
		else
		{
			// Count leading zeros 
	        int i = 0; 
	        while (i < str.length() && str.charAt(i) == '0') 
	            i++; 
	        sb.replace(0, i, ""); 
		}
        return sb.toString();  // return in String 
    } 
}
