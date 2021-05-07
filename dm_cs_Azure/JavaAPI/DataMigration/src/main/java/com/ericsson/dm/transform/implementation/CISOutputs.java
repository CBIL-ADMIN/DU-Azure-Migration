package com.ericsson.dm.transform.implementation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.ericsson.dm.Utils.CommonUtilities;
import com.ericsson.dm.inititialization.LoadSubscriberMapping;
import com.ericsson.jibx.beans.SubscriberXml;
import com.ericsson.jibx.beans.PROFILETAGLIST.PROFILETAGINFO;
import com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo;

public class CISOutputs implements Comparator<SchemasubscriberbalancesdumpInfo>{
	SubscriberXml subscriber;
	String msisdn;
	String Targetlanguage;
	String INITIAL_ACTIVATION_DATE;
	int CISOnceOffCounter;
	int CISRenewalCounter;
	List<String> CISOnceOffList;
	List<String> CISRenewalList;
	List<String> CISParkingList;
	List<String> CISnwpcList;
	Set<String> rejectAndLog;
	Set<String> onlyLog;
	Set<String> trackLog;
	Set<String> AMBTIDSet;
	Set<String> CommonBTIDSet;
	private Map<String, Set<String>> ProductIDLookUpMap;
	
	ProfileTagProcessing profileTag;
	CommonFunctions commonfunction;
	 
	public CopyOnWriteArrayList<SchemasubscriberbalancesdumpInfo> SortedBalanceInput;
		
	public CISOutputs()
	{
		
	}
	public CISOutputs(SubscriberXml subscriber,Set<String> rejectAndLog, Set<String> onlyLog, Set<String> trackLog, String INITIAL_ACTIVATION_DATE, Map<String, Set<String>> ProductIDLookUpMap) {
		// TODO Auto-generated constructor stub
		this.subscriber=subscriber;
		this.INITIAL_ACTIVATION_DATE = INITIAL_ACTIVATION_DATE;
		this.rejectAndLog=rejectAndLog;
		this.onlyLog=onlyLog;
		this.trackLog = trackLog;
		this.CISOnceOffCounter = 1;
		this.CISRenewalCounter = 1;
		this.CISOnceOffList = new ArrayList<>();
		this.CISRenewalList = new ArrayList<>();
		this.CISParkingList = new ArrayList<>();
		this.CISnwpcList = new ArrayList<>();
		this.ProductIDLookUpMap = ProductIDLookUpMap;
		SortedBalanceInput = new CopyOnWriteArrayList<>();
		
		profileTag = new ProfileTagProcessing(subscriber,this.onlyLog);
		commonfunction = new CommonFunctions(subscriber,ProductIDLookUpMap,this.onlyLog);
		this.AMBTIDSet = new HashSet<>();
		this.CommonBTIDSet = new HashSet<>();
		
		String SourceLanguage = "";
		if(subscriber.getProfiledumpInfoList().size() == 1)
			SourceLanguage = profileTag.GetProfileTagValue("Language"); //subscriber.getProfiledumpInfoList().get(0).getLanguageID();
		
		if(LoadSubscriberMapping.LanguageMap.get(SourceLanguage) == null)
			this.Targetlanguage = LoadSubscriberMapping.CommonConfigMap.get("default_language");
		else
			this.Targetlanguage = LoadSubscriberMapping.LanguageMap.get(SourceLanguage);
	}
	
	@Override
	public int compare(SchemasubscriberbalancesdumpInfo o1, SchemasubscriberbalancesdumpInfo o2) {
		int value1 = (o2.getBEEXPIRY()).compareTo((o1.getBEEXPIRY()));
        if (value1 == 0) {
        	return  o2.getBEBUCKETID().compareTo(o1.getBEBUCKETID());
        }
        return value1;
	}
	
	public Map<String, List<String>> execute() {
		// TODO Auto-generated method stub
		msisdn = subscriber.getSubscriberInfoMSISDN();
		
		SortedBalanceInput.addAll(subscriber.getBalancesdumpInfoList());
		Collections.sort(SortedBalanceInput,new Offer());
		
		CISGenerationFromAMBalanceMapping();
		CISGenerationFromCommonBalanceMapping();
		CISGenerationFromBalanceMapping();
		CISGenerationFromProfileTags();
		CISGenerationFromGraceBalanceMapping();
		CISGenerationFromGraceMBalanceMapping();
		//CISGenerationFromGGroupBalanceMapping();
		
		Map<String,List<String>> map = new HashMap<>();
		map.put("CIS_Renewal_Bundles", this.CISRenewalList);
		map.put("CIS_OnceOff_Bundles", this.CISOnceOffList);
		map.put("CIS_nwpc_Bundles", this.CISnwpcList);
		map.put("CIS_Parking_Bundles", this.CISParkingList);
		return map;
	}	
	
	private void CISGenerationFromAMBalanceMapping() {
		//Date currDate = new Date();
		SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		
		//Check if BT 1633 is present this is only BT which comes with AM Group
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{
			String Balance_ID = balanceInput.getBALANCETYPE();	
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			String Balance_StartDate = balanceInput.getBEBUCKETSTARTDATE();
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			
			if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(Balance_ID))
				continue;
			
			if(Balance_ID.equals("1633"))
			{
				if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID).equals("Y"))
				{
					AMBTIDSet.add(balanceInput.getBEBUCKETID());
					break;
				}
				else
				{
					if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
					{
						AMBTIDSet.add(balanceInput.getBEBUCKETID());
						break;
					}
					else
					{
						boolean gotSameExpiry = false;
						Map<String,String> AM1633GroupBTs = new HashMap<>();
						for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo TempbalanceInput : SortedBalanceInput)
						{
							String TempBalance_ID = TempbalanceInput.getBALANCETYPE();							
							//AM1633GroupBTs.clear();
							if(TempBalance_ID.equals("55"))
							{
								AM1633GroupBTs.clear();
								String TempBalance_Value = TempbalanceInput.getBEBUCKETVALUE();
								String TempBalance_StartDate = TempbalanceInput.getBEBUCKETSTARTDATE();
								String TempBalance_ExpiryDate = TempbalanceInput.getBEEXPIRY();
								if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
								{
									AMBTIDSet.add(balanceInput.getBEBUCKETID());
									continue;
								}
								if(Balance_ExpiryDate.equals(TempBalance_ExpiryDate))
								{
									//If both expiry is same, so Create the Ambt									
									gotSameExpiry = true;
									AM1633GroupBTs.put(TempbalanceInput.getBEBUCKETID(), TempBalance_ID + "|" + TempBalance_Value + "|" + TempBalance_StartDate + "|" + TempBalance_ExpiryDate);
									AM1633GroupBTs.put(balanceInput.getBEBUCKETID(), Balance_ID + "|" + Balance_Value + "|" + Balance_StartDate + "|" + Balance_ExpiryDate);
									Map<String,List<String>> AMGroupOfferMap = ComputeAMGroup(AM1633GroupBTs, new HashSet<>(Arrays.asList("")));
									//Now Populate the MGroup
									if(AMGroupOfferMap.size() != 0)
									{
										for(Entry<String, List<String>> item : AMGroupOfferMap.entrySet())
										{
											List<String> ValidAMGroupBalanceOffer = item.getValue();
											Set<String> GroupBTID = new HashSet<>();
											ValidAMGroupBalanceOffer.forEach(item1->{GroupBTID.add(item1.split("\\|")[1].split(";")[3]);});
											for(String Str : ValidAMGroupBalanceOffer)
											{
												String TargetValue = Str.split("\\|")[1];
												String SourceValue = Str.split("\\|")[0];
												String CIS_Reference = SourceValue.split(";")[1];
												if(CIS_Reference.length() != 0)
												{
													if(CIS_Reference.startsWith("Renewal")  && CommonUtilities.convertDateToEpoch(TargetValue.split(";")[2]) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
													{
														CISRenewalList.add(PopulateCISRenewal(CIS_Reference,SourceValue.split(";")[1],SourceValue.split(";")[2],TargetValue.split(";")[0],TargetValue.split(";")[1], TargetValue.split(";")[2],"", TargetValue.split(";")[3]));
													}
													if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(TargetValue.split(";")[2]) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
													{
														CISOnceOffList.add(PopulateCISOnce(CIS_Reference,SourceValue.split(";")[1],SourceValue.split(";")[2],TargetValue.split(";")[0],TargetValue.split(";")[1], TargetValue.split(";")[2], TargetValue.split(";")[3]));
													}
													AMBTIDSet.addAll(GroupBTID);	
												}											
											}													
										}
										break;
									}
								}
								else
								{
									//Expiry not equal
									gotSameExpiry = false;
									//AM1633GroupBTs.put(TempbalanceInput.getBEBUCKETID(), TempBalance_ID + "|" + TempBalance_Value + "|" + TempBalance_StartDate + "|" + TempBalance_ExpiryDate);
									AM1633GroupBTs.put(balanceInput.getBEBUCKETID(), Balance_ID + "|" + Balance_Value + "|" + Balance_StartDate + "|" + Balance_ExpiryDate);									
								}								
							}							
						}						
					}
				}
			}
		}
		
		//Check if BT 1635 is present and Check if BT55 and BT1635 has same ExpiryDate and group is formed with PC 
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{
			String Balance_ID = balanceInput.getBALANCETYPE();	
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			String Balance_StartDate = balanceInput.getBEBUCKETSTARTDATE();
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			if(AMBTIDSet.contains(balanceInput.getBEBUCKETID()))
				continue;
			Map<String,String> BT_TypeValue = new HashMap<>();
			if(Balance_ID.equals("1635"))
			{
				BT_TypeValue.clear();
				BT_TypeValue.put(Balance_ID, balanceInput.getBEBUCKETID() + "|" + Balance_Value + "|" + Balance_StartDate + "|" + Balance_ExpiryDate );
				if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID).equals("Y"))
				{
					AMBTIDSet.add(balanceInput.getBEBUCKETID());
					break;
				}
				else
				{
					if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
					{
						AMBTIDSet.add(balanceInput.getBEBUCKETID());
						break;
					}
					else
					{
						for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo TempbalanceInput : SortedBalanceInput)
						{
							if(BT_TypeValue.size() == 0)
								break;
							String TempBalance_ID = TempbalanceInput.getBALANCETYPE();
							if(AMBTIDSet.contains(TempbalanceInput.getBEBUCKETID()))
								continue;
							boolean gotSameExpiry = false;
							if(TempBalance_ID.equals("55"))
							{
								String TempBalance_Value = TempbalanceInput.getBEBUCKETVALUE();
								String TempBalance_StartDate = TempbalanceInput.getBEBUCKETSTARTDATE();
								String TempBalance_ExpiryDate = TempbalanceInput.getBEEXPIRY();
								
								if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
								{
									AMBTIDSet.add(balanceInput.getBEBUCKETID());
									continue;
								}
								if(Balance_ExpiryDate.equals(TempBalance_ExpiryDate))
								{
									//If both expiry is same, so Create the Ambt									
									gotSameExpiry = false;
									BT_TypeValue.put(TempBalance_ID, TempbalanceInput.getBEBUCKETID() + "|" + TempBalance_Value + "|" + TempBalance_StartDate + "|" + TempBalance_ExpiryDate );
									for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo TempbalanceInput1 : SortedBalanceInput)
									{
										if(BT_TypeValue.size() == 0)
											break;
										
										String TempBalance_ID1 = TempbalanceInput1.getBALANCETYPE();
										if(AMBTIDSet.contains(TempbalanceInput1.getBEBUCKETID()))
											continue;
										if(LoadSubscriberMapping.ExceptionBalancesPCForAMGroup.contains(TempBalance_ID1))
										{											
											BT_TypeValue.put(TempBalance_ID1, TempbalanceInput1.getBEBUCKETID() + "|" + TempbalanceInput1.getBEBUCKETVALUE() + "|" + TempbalanceInput1.getBEBUCKETSTARTDATE() + "|" + TempbalanceInput1.getBEEXPIRY() );
											
											List<String> GroupBalanceOffer = new ArrayList<>();
											List<String> AMGroupName = (commonfunction.getASpecialGroupKey(LoadSubscriberMapping.BalanceOnlySpecialAMGroupMap,TempBalance_ID1));
											for(String GroupName : AMGroupName)
											{
												GroupBalanceOffer.clear();
												if(!GroupName.startsWith("A-M"))
												{													
													Set<String> GroupBTItems = LoadSubscriberMapping.BalanceOnlySpecialAMGroupMap.get(GroupName);
													for(Entry<String, String> item : BT_TypeValue.entrySet())
													{														
														String SourceBTID = item.getKey();
														String SourceBTValue = item.getValue().split("\\|")[1];
														String SourceBTBucket = item.getValue().split("\\|")[0];
														String SourceStart = item.getValue().split("\\|")[2];
														String SourceExpiry = item.getValue().split("\\|")[3];
														if(GroupBTItems.contains(SourceBTID))
														{
															if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName) != null)
															{							
																String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getOfferID();
																String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getSymbols();
																String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTValue();
																String CIS_Reference = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getCISReference();
																String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTTYPE();
																if(Symbol.equals(">=") && Integer.parseInt(SourceBTValue) >= Integer.parseInt(BT_Value))
																{	
																	GroupBalanceOffer.add(BT_Type + ";" + CIS_Reference + ";" + SourceBTID + ";" + SourceBTBucket +"|" + Offer_ID + ";" + SourceStart + ";" + SourceExpiry  + ";" + SourceBTBucket);
																	continue;
																}
																else if(Symbol.equals(">") && Integer.parseInt(SourceBTValue) > Integer.parseInt(BT_Value))
																{
																	GroupBalanceOffer.add(BT_Type + ";" + CIS_Reference + ";" + SourceBTID + ";" + SourceBTBucket +"|" + Offer_ID + ";" + SourceStart + ";" + SourceExpiry  + ";" + SourceBTBucket);
																	continue;
																}
																else if(Symbol.equals("=") && Integer.parseInt(SourceBTValue) == Integer.parseInt(BT_Value))
																{
																	GroupBalanceOffer.add(BT_Type + ";" + CIS_Reference + ";" + SourceBTID + ";" + SourceBTBucket +"|" + Offer_ID + ";" + SourceStart + ";" + SourceExpiry  + ";" + SourceBTBucket);
																	continue;
																}
																else if(Symbol.equals("or"))
																{
																	//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
																	String[] values = BT_Value.split("#");											
																	if(Arrays.stream(values).anyMatch(SourceBTValue::equals))
																	{
																		GroupBalanceOffer.add(BT_Type + ";" + CIS_Reference + ";" + SourceBTID + ";" + SourceBTBucket +"|" + Offer_ID + ";" + SourceStart + ";" + SourceExpiry  + ";" + SourceBTBucket);
																		continue;
																	}
																}															
															}
														}
													}
													if(GroupBalanceOffer.size() == GroupBTItems.size())
													{
														Set<String> GroupBTID = new HashSet<>();
														gotSameExpiry = true;
														GroupBalanceOffer.forEach(item1->{GroupBTID.add(item1.split("\\|")[1].split(";")[3]);});
														for(String Str : GroupBalanceOffer)
														{
															String TargetValue = Str.split("\\|")[1];
															String SourceValue = Str.split("\\|")[0];
															String CIS_Reference = SourceValue.split(";")[1];
															if(CIS_Reference.length() != 0)
															{
																if(CIS_Reference.startsWith("Renewal")  && CommonUtilities.convertDateToEpoch(TargetValue.split(";")[2]) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
																{
																	CISRenewalList.add(PopulateCISRenewal(CIS_Reference,SourceValue.split(";")[2],SourceValue.split(";")[3],TargetValue.split(";")[0],TargetValue.split(";")[1], TargetValue.split(";")[2],"", TargetValue.split(";")[3]));
																}
																if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(TargetValue.split(";")[2]) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
																{
																	CISOnceOffList.add(PopulateCISOnce(CIS_Reference,SourceValue.split(";")[2],SourceValue.split(";")[3],TargetValue.split(";")[0],TargetValue.split(";")[1], TargetValue.split(";")[2], TargetValue.split(";")[3]));
																}
																AMBTIDSet.addAll(GroupBTID);	
															}											
														}
														BT_TypeValue.clear();
														break;
													}
												}
											}											
										}
									}
									if(!gotSameExpiry)
									{
										Map<String,String> AM1635GroupBTs = new HashMap<>();
										AM1635GroupBTs.put(TempbalanceInput.getBEBUCKETID(), TempBalance_ID + "|" + TempBalance_Value + "|" + TempBalance_StartDate + "|" + TempBalance_ExpiryDate);
										AM1635GroupBTs.put(balanceInput.getBEBUCKETID(), Balance_ID + "|" + Balance_Value + "|" + Balance_StartDate + "|" + Balance_ExpiryDate);
										Map<String,List<String>> AMGroupOfferMap = ComputeAMGroup(AM1635GroupBTs, new HashSet<>(Arrays.asList("")));
										//Now Populate the MGroup
										if(AMGroupOfferMap.size() != 0)
										{
											for(Entry<String, List<String>> item : AMGroupOfferMap.entrySet())
											{
												List<String> ValidAMGroupBalanceOffer = item.getValue();
												Set<String> GroupBTID = new HashSet<>();
												ValidAMGroupBalanceOffer.forEach(item1->{GroupBTID.add(item1.split("\\|")[1].split(";")[3]);});
												for(String Str : ValidAMGroupBalanceOffer)
												{
													String TargetValue = Str.split("\\|")[1];
													String SourceValue = Str.split("\\|")[0];
													String CIS_Reference = SourceValue.split(";")[1];
													if(CIS_Reference.length() != 0)
													{
														if(CIS_Reference.startsWith("Renewal")  && CommonUtilities.convertDateToEpoch(TargetValue.split(";")[2]) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
														{
															CISRenewalList.add(PopulateCISRenewal(CIS_Reference,SourceValue.split(";")[2],SourceValue.split(";")[3],TargetValue.split(";")[0],TargetValue.split(";")[1], TargetValue.split(";")[2],"", TargetValue.split(";")[3]));
														}
														if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(TargetValue.split(";")[2]) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
														{
															CISOnceOffList.add(PopulateCISOnce(CIS_Reference,SourceValue.split(";")[2],SourceValue.split(";")[3],TargetValue.split(";")[0],TargetValue.split(";")[1], TargetValue.split(";")[2], TargetValue.split(";")[3]));
														}
														AMBTIDSet.addAll(GroupBTID);	
													}												
												}													
											}
											break;
										}
									}
								}								
							}
						}
					}
				}
			}
		}
		
		//Check if BT 1635 is present, Now since Group is not formed now try to form the group with PC alone excluding BT55
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{
			String Balance_ID = balanceInput.getBALANCETYPE();	
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			String Balance_StartDate = balanceInput.getBEBUCKETSTARTDATE();
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			if(AMBTIDSet.contains(balanceInput.getBEBUCKETID()))
				continue;
			Map<String,String> BT_TypeValue = new HashMap<>();
			if(Balance_ID.equals("1635"))
			{
				BT_TypeValue.clear();
				BT_TypeValue.put(Balance_ID, balanceInput.getBEBUCKETID() + "|" + Balance_Value + "|" + Balance_StartDate + "|" + Balance_ExpiryDate );
				if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID).equals("Y"))
				{
					AMBTIDSet.add(balanceInput.getBEBUCKETID());
					break;
				}
				else
				{
					if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
					{
						AMBTIDSet.add(balanceInput.getBEBUCKETID());
						break;
					}
					else
					{
						//Since BT55 and BT1635 expiry date not matching so we need to look to form the group with PC alone 
						//the group which can be formed are A-156 to A-165
						boolean gotSameExpiry = false;
						for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo TempbalanceInput1 : SortedBalanceInput)
						{
							if(BT_TypeValue.size() == 0)
								break;
							String TempBalance_ID1 = TempbalanceInput1.getBALANCETYPE();
							if(AMBTIDSet.contains(TempbalanceInput1.getBEBUCKETID()))
								continue;
							if(LoadSubscriberMapping.ExceptionBalancesPCForAMGroup.contains(TempBalance_ID1))
							{											
								BT_TypeValue.put(TempBalance_ID1, TempbalanceInput1.getBEBUCKETID() + "|" + TempbalanceInput1.getBEBUCKETVALUE() + "|" + TempbalanceInput1.getBEBUCKETSTARTDATE() + "|" + TempbalanceInput1.getBEEXPIRY() );
								
								List<String> GroupBalanceOffer = new ArrayList<>();
								List<String> AMGroupName = (commonfunction.getASpecialGroupKey(LoadSubscriberMapping.BalanceOnlySpecialAMGroupMap,TempBalance_ID1));
								for(String GroupName : AMGroupName)
								{
									GroupBalanceOffer.clear();
									if(!GroupName.startsWith("A-M"))
									{													
										Set<String> GroupBTItems = LoadSubscriberMapping.BalanceOnlySpecialAMGroupMap.get(GroupName);
										for(Entry<String, String> item : BT_TypeValue.entrySet())
										{														
											String SourceBTID = item.getKey();
											String SourceBTValue = item.getValue().split("\\|")[1];
											String SourceBTBucket = item.getValue().split("\\|")[0];
											String SourceStart = item.getValue().split("\\|")[2];
											String SourceExpiry = item.getValue().split("\\|")[3];
											if(GroupBTItems.contains(SourceBTID))
											{
												if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName) != null)
												{							
													String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getOfferID();
													String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getSymbols();
													String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTValue();
													String CIS_Reference = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getCISReference();
													String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTTYPE();
													
													if(Symbol.equals(">=") && Integer.parseInt(SourceBTValue) >= Integer.parseInt(BT_Value))
													{	
														GroupBalanceOffer.add(BT_Type + ";" + CIS_Reference + ";" + SourceBTID + ";" + SourceBTBucket +"|" + Offer_ID + ";" + SourceStart + ";" + SourceExpiry  + ";" + SourceBTBucket);
														continue;
													}
													else if(Symbol.equals(">") && Integer.parseInt(SourceBTValue) > Integer.parseInt(BT_Value))
													{
														GroupBalanceOffer.add(BT_Type + ";" + CIS_Reference + ";" + SourceBTID + ";" + SourceBTBucket +"|" + Offer_ID + ";" + SourceStart + ";" + SourceExpiry  + ";" + SourceBTBucket);
														continue;
													}
													else if(Symbol.equals("=") && Integer.parseInt(SourceBTValue) == Integer.parseInt(BT_Value))
													{
														GroupBalanceOffer.add(BT_Type + ";" + CIS_Reference + ";" + SourceBTID + ";" + SourceBTBucket +"|" + Offer_ID + ";" + SourceStart + ";" + SourceExpiry  + ";" + SourceBTBucket);
														continue;
													}
													else if(Symbol.equals("or"))
													{
														//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
														String[] values = BT_Value.split("#");											
														if(Arrays.stream(values).anyMatch(SourceBTValue::equals))
														{
															GroupBalanceOffer.add(BT_Type + ";" + CIS_Reference + ";" + SourceBTID + ";" + SourceBTBucket +"|" + Offer_ID + ";" + SourceStart + ";" + SourceExpiry  + ";" + SourceBTBucket);
															continue;
														}
													}
												}
											}
										}
										if(GroupBalanceOffer.size() == 2)
										{
											Set<String> GroupBTID = new HashSet<>();
											GroupBalanceOffer.forEach(item1->{GroupBTID.add(item1.split("\\|")[1].split(";")[3]);});
											for(String Str : GroupBalanceOffer)
											{
												String TargetOffer = Str.split("\\|")[1];											
												if(TargetOffer.split(";")[0].length() != 0)
												{
													String TargetValue = Str.split("\\|")[1];
													String SourceValue = Str.split("\\|")[0];
													String CIS_Reference = SourceValue.split(";")[1];
													if(CIS_Reference.length() != 0)
													{
														if(CIS_Reference.startsWith("Renewal") && CommonUtilities.convertDateToEpoch(TargetValue.split(";")[2]) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
														{
															CISRenewalList.add(PopulateCISRenewal(CIS_Reference,SourceValue.split(";")[2],SourceValue.split(";")[3],TargetValue.split(";")[0],TargetValue.split(";")[1], TargetValue.split(";")[2],"", TargetValue.split(";")[3]));
														}
														if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(TargetValue.split(";")[2]) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
														{
															CISOnceOffList.add(PopulateCISOnce(CIS_Reference,SourceValue.split(";")[2],SourceValue.split(";")[3],TargetValue.split(";")[0],TargetValue.split(";")[1], TargetValue.split(";")[2], TargetValue.split(";")[3]));
														}
														AMBTIDSet.addAll(GroupBTID);	
													}	
													
												}											
											}
											BT_TypeValue.clear();
											gotSameExpiry = true;
											break;											
										}
										else
										{
											BT_TypeValue.remove(TempBalance_ID1);
											//Now the condition with PC doesn't match so i need to find AM;														
										}
									}
								}											
							}
						}						
					}
				}
			}
		}
	
		//Now All combination met, so now check if any PC left out and create master for it
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{
			String BT_ID = balanceInput.getBALANCETYPE();
			String BT_ExpiryDate = balanceInput.getBEEXPIRY();
			if(AMBTIDSet.contains(balanceInput.getBEBUCKETID()))
				continue;
			
			if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(BT_ID))
				continue;
			
			if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(BT_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(BT_ID).equals("Y"))
			{
				AMBTIDSet.add(balanceInput.getBEBUCKETID());
				continue;
			}
		
			if(!BT_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(BT_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
			{
				AMBTIDSet.add(balanceInput.getBEBUCKETID());
				continue;
			}
			
			if(LoadSubscriberMapping.ExceptionBalancesPCForAMGroup.contains(BT_ID))
			{
				/*if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M") != null)
				{
					String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getOfferID();
					String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getOfferType();
					String Offer_flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getOfferFlag();
					boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getOfferStartDate().length() > 0 ? true:false;
					boolean expiryFalg = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getOfferExpiryDate().length() > 0 ? true:false;
					String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getSymbols();
					String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getBTValue();
					
					if(Symbol.equals(">") && Integer.parseInt(BT_Value) > Integer.parseInt(BT_VALUE))
					{
						AMBTIDSet.add(BE_BucketId);
						FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,BT_StartDate, BT_ExpiryDate,"",Offer_flag,new HashSet<>(Arrays.asList(BE_BucketId))));
						trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ BE_BucketId  +":ACTION=Logging");
						continue;
					}
					else if(Symbol.equals(">=") && Integer.parseInt(BT_Value) >= Integer.parseInt(BT_VALUE))
					{
						AMBTIDSet.add(BE_BucketId);
						FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,BT_StartDate, BT_ExpiryDate,"",Offer_flag,new HashSet<>(Arrays.asList(BE_BucketId))));
						trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ BE_BucketId  +":ACTION=Logging");
						continue;
					}
					else if(Symbol.equals("=") && Integer.parseInt(BT_Value) == Integer.parseInt(BT_VALUE))
					{
						AMBTIDSet.add(BE_BucketId);
						FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,BT_StartDate, BT_ExpiryDate,"",Offer_flag,new HashSet<>(Arrays.asList(BE_BucketId))));
						trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ BE_BucketId  +":ACTION=Logging");
						continue;
					}
					else if(Symbol.equals("or"))
					{
						String[] values = BT_VALUE.split("#");											
						if(Arrays.stream(values).anyMatch(BT_Value::equals))
						{
							AMBTIDSet.add(BE_BucketId);
							FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,BT_StartDate, BT_ExpiryDate,"",Offer_flag,new HashSet<>(Arrays.asList(BE_BucketId))));
							trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ BE_BucketId  +":ACTION=Logging");
							continue;
						}					
					}
					else
					{
						AMBTIDSet.add(BE_BucketId);
						onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID="+ BE_BucketId  +":ACTION=Logging");
					}
				}
				else
				{
					AMBTIDSet.add(BE_BucketId);
					onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID="+ BE_BucketId  +":ACTION=Logging");
				}*/
			}
		}	
	}
	
	private void CISGenerationFromCommonBalanceMapping() {
		//Date currDate = new Date();
		//SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Set<String> CompletedBT_ID = new HashSet<>();
		CompletedBT_ID.addAll(AMBTIDSet);
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{			
			String Balance_ID = balanceInput.getBALANCETYPE();
			//System.out.println("Master Balance_ID: " + Balance_ID);
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			String Balance_StartDate = balanceInput.getBEBUCKETSTARTDATE();
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			if(Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
				Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
			
			if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
				continue;
			
			if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(Balance_ID))
				continue;
			
			if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID).equals("Y"))
			{
				CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				continue;
			}			
			
			
			if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
			{
				CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				continue;
			}
			
			//Two BT 3503 and 3412 are exceptional as they belong to dummy and Group so just handling based on its value.
			if(LoadSubscriberMapping.CommonBTForDummyGroup.contains(Balance_ID))
			{	
				if(Balance_ID.equals("3503"))
				{
					if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
					{
						String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
						String CIS_Reference = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getCISReference();
						String StartDate = profileTag.GetProfileTagValue("RLHAct_ReqDate"); //subscriber.getProfiledumpInfoList().get(0).getPriceShout();
						
						if(CIS_Reference.toLowerCase().startsWith("parking"))
							CISParkingList.add(PopulateCISParking(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
						if(CIS_Reference.startsWith("Renewal") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
							CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"", balanceInput.getBEBUCKETID()));
						if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
							CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));							
						
						
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						continue;
					}
				}
				
				if(Balance_ID.equals("3412"))
				{
					if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
					{
						String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
						String CIS_Reference = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getCISReference();
						if(Balance_ExpiryDate.equals("1970-01-01 04:00:00"))
							Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
						if(!Offer_ID.isEmpty())
						{
							if(CIS_Reference.toLowerCase().startsWith("parking"))
								CISParkingList.add(PopulateCISParking(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate , balanceInput.getBEBUCKETID()));
							if(CIS_Reference.startsWith("Renewal") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
								CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"" , balanceInput.getBEBUCKETID()));
							if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
								CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							continue;
						}
					}							
				}
				
				if(Balance_ID.equals("3011"))
				{
					if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
					{
						String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
						String CIS_Reference = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getCISReference();
						if(Balance_ExpiryDate.equals("1970-01-01 04:00:00"))
							Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
						if(!Offer_ID.isEmpty())
						{
							if(CIS_Reference.toLowerCase().startsWith("parking"))
								CISParkingList.add(PopulateCISParking(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate , balanceInput.getBEBUCKETID()));
							if(CIS_Reference.startsWith("Renewal") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
								CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"" , balanceInput.getBEBUCKETID()));
							if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
								CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							continue;
						}
					}							
				}
				
				if(Balance_ID.equals("3411"))
				{
					if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
					{
						String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
						String CIS_Reference = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getCISReference();
						if(Balance_ExpiryDate.equals("1970-01-01 04:00:00"))
							Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
						
						if(!Offer_ID.isEmpty())
						{
							if(CIS_Reference.toLowerCase().startsWith("parking"))
								CISParkingList.add(PopulateCISParking(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
							if(CIS_Reference.startsWith("Renewal") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
								CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"", balanceInput.getBEBUCKETID()));
							if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
								CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							continue;
						}
					}							
				}
			}
		}
		CommonBTIDSet.addAll(CompletedBT_ID);
	}
	
	private void CISGenerationFromBalanceMapping() {
		//Date currDate = new Date();
		//SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Set<String> CompletedBT_ID = new HashSet<>();
		CompletedBT_ID.addAll(AMBTIDSet);
		CompletedBT_ID.addAll(CommonBTIDSet);
		
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{			
			String Balance_ID = balanceInput.getBALANCETYPE();
			//System.out.println("Master Balance_ID: " + Balance_ID);
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			String Balance_StartDate = balanceInput.getBEBUCKETSTARTDATE();
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			boolean BT25OfferCreated = false;
			boolean BT759fferCreated = false;
			boolean BTPA14BalanceDummy = false;
			boolean BT3216fferCreated = false;
			if(Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
				Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
			
			if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
				continue;
			
			if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(Balance_ID))
				continue;
			
			if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID).equals("Y"))
			{
				CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				continue;
			}			
			
			
			if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
			{
				CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				continue;
			}
			
			//Two BT 3503 and 3412 are exceptional as they belong to dummy and Group so just handling based on its value.
			if(LoadSubscriberMapping.CommonBTForDummyGroup.contains(Balance_ID))
			{	
				if(Balance_ID.equals("3503"))
				{
					if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
					{
						/*String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
						String CIS_Reference = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getCISReference();
						String StartDate = profileTag.GetProfileTagValue("RLHAct_ReqDate"); //subscriber.getProfiledumpInfoList().get(0).getPriceShout();
						
						if(CIS_Reference.toLowerCase().startsWith("parking"))
							CISParkingList.add(PopulateCISParking(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
						if(CIS_Reference.startsWith("Renewal") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
							CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"", balanceInput.getBEBUCKETID()));
						if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
							CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));*/							
						
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						continue;
					}
				}
				
				if(Balance_ID.equals("3412"))
				{
					if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
					{
						/*String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
						String CIS_Reference = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getCISReference();
						if(Balance_ExpiryDate.equals("1970-01-01 04:00:00"))
							Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
						if(!Offer_ID.isEmpty())
						{
							if(CIS_Reference.toLowerCase().startsWith("parking"))
								CISParkingList.add(PopulateCISParking(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate , balanceInput.getBEBUCKETID()));
							if(CIS_Reference.startsWith("Renewal") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
								CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"" , balanceInput.getBEBUCKETID()));
							if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
								CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							continue;
						}*/
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						continue;
					}							
				}
				
				if(Balance_ID.equals("3011"))
				{
					if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
					{
						/*String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
						String CIS_Reference = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getCISReference();
						if(Balance_ExpiryDate.equals("1970-01-01 04:00:00"))
							Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
						if(!Offer_ID.isEmpty())
						{
							if(CIS_Reference.toLowerCase().startsWith("parking"))
								CISParkingList.add(PopulateCISParking(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate , balanceInput.getBEBUCKETID()));
							if(CIS_Reference.startsWith("Renewal") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
								CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"" , balanceInput.getBEBUCKETID()));
							if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
								CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							continue;
						}*/
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						continue;
					}							
				}
				
				if(Balance_ID.equals("3411"))
				{
					if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
					{
						/*String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
						String CIS_Reference = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getCISReference();
						if(Balance_ExpiryDate.equals("1970-01-01 04:00:00"))
							Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
						
						if(!Offer_ID.isEmpty())
						{
							if(CIS_Reference.toLowerCase().startsWith("parking"))
								CISParkingList.add(PopulateCISParking(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
							if(CIS_Reference.startsWith("Renewal") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
								CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"", balanceInput.getBEBUCKETID()));
							if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
								CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							continue;
						}*/
						
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						continue;
					}							
				}
			}
			
			//Check if BT is valid for migration for group element
			if(LoadSubscriberMapping.AllBTBalancesValueSet.contains(Balance_ID))
			{
				if(LoadSubscriberMapping.AllBTBalancesValueMap.containsKey(Balance_ID))
				{
					String Symbol = LoadSubscriberMapping.AllBTBalancesValueMap.get(Balance_ID).getSymbols();
					String BT_Value = LoadSubscriberMapping.AllBTBalancesValueMap.get(Balance_ID).getBTValue();
					boolean ValidBT = false;
					if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
						ValidBT = true;
					else if(Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_Value))
						ValidBT = true;
					else if(Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value))
						ValidBT = true;
					else if(Symbol.equals("or"))
					{
						//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
						String[] values = BT_Value.split("#");
						
						if(Arrays.stream(values).anyMatch(Balance_Value::equals))
						{
							ValidBT = true;
						}
					}
					if(!ValidBT)
					{
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						continue;
					}
				}
			}
			//Check if only V BT is valid for migration for group element
			if(LoadSubscriberMapping.OnlyVBalancesValueSet.contains(Balance_ID))
			{
				if(LoadSubscriberMapping.OnlyVBalancesValueMap.get(Balance_ID) != null)
				{
					Set<String> BalanceValueKey = LoadSubscriberMapping.OnlyVBalancesValueMap.get(Balance_ID);
					if(!BalanceValueKey.contains(Balance_Value))
					{						
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						continue;
					}
				}
			}
			
			if(LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|") != null)
			{
				String Offer_ID = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getOfferID();
				String Symbol = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getSymbols();
				String BT_Value = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getBTValue();
				String CIS_Reference = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getCISReference();
				if(Balance_ExpiryDate.equals("1970-01-01 04:00:00"))
					Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
				
				if(!Offer_ID.isEmpty())
				{					
					if(Symbol.isEmpty() && BT_Value.isEmpty())
					{	
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
					}
					else
					{
						
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						if(CIS_Reference.startsWith("Renewal") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
						{
							CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"", balanceInput.getBEBUCKETID()));
							
						}
						if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
						{
							CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));							
						}
						/*if(CIS_Reference.equals("Parking") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
						{
							CISParkingList.add(PopulateCISParking(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate));
						}*/
					}
				}	
			}
			
			if(LoadSubscriberMapping.ExceptionBalances.contains(Balance_ID))
			{	
				if(Balance_ID.equals("3216"))
				{
					boolean DummyDoneFlag = false;
					for(String str : LoadSubscriberMapping.ProfileTagDummy)
					{							 
						if(LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str) != null && str.contains(Balance_ID))
						{
							String Symbol = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getSymbols();
							String BT_Value = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getBTValue();
							String BT_SubState = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getSubState();
							String BT_TYPE = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getBTTYPE();
							if(subscriber.getSubscriberInfoSERVICESTATE().equals(BT_SubState))
							{
								if(Symbol.equals("or")) //&& Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
								{
									String[] values = BT_Value.split("#");											
									if(Arrays.stream(values).anyMatch(Balance_Value::equals))
									{
										List<String> PTMappingValue = Arrays.asList(LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getPTName().split("#"));
										
										for(String PtValue : PTMappingValue)
										{
											String PT_Name = PtValue.split("-")[0];
											String PT_Symbol = PtValue.split("-")[1];
											String PT_Value = PtValue.split("-")[2];
																														
											String PT_InputValue = profileTag.GetProfileTagValue(PT_Name);
											if(PT_Symbol.equals("=") && PT_InputValue.equals(PT_Value))
											{
												String Offer_ID = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferID();
												String expiryDate = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferExpiryDate();
												String CIS_Reference = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getCISReference();
												if(!expiryDate.equals("BT_Expiry_Date"))
												{
													String Date = profileTag.GetProfileTagValue("SmsExpDate");
													if(!Date.isEmpty() && Date.length() == 14)
														expiryDate = Date.substring(0,4) + "-" + Date.substring(4,6) + "-" + Date.substring(6,8) + " " + Date.substring(8,10) + ":" + Date.substring(10,12) + ":" + Date.substring(12,14);
													else
														expiryDate = Balance_ExpiryDate;
												}	
												else
												{
													expiryDate = Balance_ExpiryDate;
												}
												//System.out.println("MSISDN------- " + msisdn );
												if(!BT3216fferCreated)
												{
													if(CIS_Reference.startsWith("Parking"))
													{
														CISParkingList.add(PopulateCISParking(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
														BT3216fferCreated = true;
														DummyDoneFlag = true;
														break;
													}													
												}
											}	
										}	
									}
								}
							}
						}
					}
				}
				
				if( Balance_ID.equals("759"))
				{
					boolean DummyDoneFlag = false;
					for(String str : LoadSubscriberMapping.ProfileTagDummy)
					{							 
						if(LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str) != null)
						{
							String Symbol = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getSymbols();
							String BT_Value = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getBTValue();
							if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
							{										
								String PT_Name = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getPTName().split("-")[0];
								String PT_Symbol = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getPTName().split("-")[1];
								String PT_Value = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getPTName().split("-")[2];
																											
								String PT_InputValue = profileTag.GetProfileTagValue(PT_Name);
								if(PT_Symbol.equals("=") && PT_InputValue.equals(PT_Value))
								{
									String Offer_ID = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferID();
									String expiryDate = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferExpiryDate();
									String CIS_Reference = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getCISReference();
									
									if(!expiryDate.equals("BT_Expiry_Date"))
									{
										String ExpiryPTValue = (expiryDate.split("\\.")[0]);
										String Date = profileTag.GetProfileTagValue(ExpiryPTValue);
										if(!Date.isEmpty() && Date.length() == 14)
											expiryDate = Date.substring(0,4) + "-" + Date.substring(4,6) + "-" + Date.substring(6,8) + " " + Date.substring(8,10) + ":" + Date.substring(10,12) + ":" + Date.substring(12,14);
									}	
									else
									{
										expiryDate = Balance_ExpiryDate;
									}
									if(!BT759fferCreated)
									{
										CompletedBT_ID.add(balanceInput.getBEBUCKETID());
										if(Balance_ExpiryDate.equals("1970-01-01 04:00:00"))
											Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
										
										if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
										{
											CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
										}
										if(CIS_Reference.startsWith("Renewal")  && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
										{
											CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"", balanceInput.getBEBUCKETID()));
										}
										BT759fferCreated = true;
									}
								}									
							}
						}
					}
				}
				
				if(Balance_ID.equals("25") || Balance_ID.equals("240") || Balance_ID.equals("260"))
				{
					for(String str : LoadSubscriberMapping.ProfileTagDummy)
					{							 
						if(LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str) != null && str.contains(Balance_ID))
						{
							String Symbol = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getSymbols();
							String BT_Value = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getBTValue();
							if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
							{
								List<String> PTMappingValue = Arrays.asList(LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getPTName().split("#"));
								List<String> ValidProfileTagValue = new ArrayList<>();
								Set<String> CISOutput = new HashSet();
								
								for(String PtValue : PTMappingValue)
								{
									
									String PT_Name = PtValue.split("-")[0];
									String PT_Symbol = PtValue.split("-")[1];
									String PT_Value = PtValue.split("-")[2];
																												
									String PT_InputValue = profileTag.GetProfileTagValue(PT_Name);
									if(PT_Symbol.equals("=") && PT_InputValue.equals(PT_Value))
									{
										String Offer_ID = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferID();
										String expiryDate = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferExpiryDate();
										String CIS_Reference = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getCISReference();
										if(!expiryDate.equals("BT_Expiry_Date"))
										{
											String Date = profileTag.GetProfileTagValue("SmsExpDate");
											if(!Date.isEmpty() && Date.length() == 14)
												expiryDate = Date.substring(0,4) + "-" + Date.substring(4,6) + "-" + Date.substring(6,8) + " " + Date.substring(8,10) + ":" + Date.substring(10,12) + ":" + Date.substring(12,14);
											else
												expiryDate = Balance_ExpiryDate;
										}	
										else
										{
											expiryDate = Balance_ExpiryDate;
										}
										//System.out.println("MSISDN------- " + msisdn );
										if(!BT25OfferCreated)
										{
											ValidProfileTagValue.add(balanceInput.getBEBUCKETID());
											CompletedBT_ID.add(balanceInput.getBEBUCKETID());
											if(Balance_ExpiryDate.equals("1970-01-01 04:00:00"))
												Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
											
											if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
											{									
												CISOutput.add(CIS_Reference  + ";" +  Balance_ID  + ";" + Balance_Value + ";" + Offer_ID + ";" + Balance_StartDate + ";" + Balance_ExpiryDate + ";" + balanceInput.getBEBUCKETID());
											}
											if(CIS_Reference.startsWith("Renewal")  && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
											{
												CISOutput.add(CIS_Reference  + ";" +  Balance_ID  + ";" + Balance_Value + ";" + Offer_ID + ";" + Balance_StartDate + ";" + Balance_ExpiryDate + ";" + balanceInput.getBEBUCKETID());
											}
										}
									}
									else if(PT_Symbol.equals("!=") && !PT_InputValue.equals(PT_Value))
									{
										String Offer_ID = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferID();
										String expiryDate = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferExpiryDate();
										String CIS_Reference = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getCISReference();
										if(!expiryDate.equals("BT_Expiry_Date"))
										{
											String Date = profileTag.GetProfileTagValue("SmsExpDate");
											if(!Date.isEmpty() && Date.length() == 14)
												expiryDate = Date.substring(0,4) + "-" + Date.substring(4,6) + "-" + Date.substring(6,8) + " " + Date.substring(8,10) + ":" + Date.substring(10,12) + ":" + Date.substring(12,14);
											else
												expiryDate = Balance_ExpiryDate;
										}	
										else
										{
											expiryDate = Balance_ExpiryDate;
										}
										//System.out.println("MSISDN------- " + msisdn );
										if(!BT25OfferCreated)
										{
											ValidProfileTagValue.add(balanceInput.getBEBUCKETID());
											CompletedBT_ID.add(balanceInput.getBEBUCKETID());
											if(Balance_ExpiryDate.equals("1970-01-01 04:00:00"))
												Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
											
											if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
											{									
												CISOutput.add(CIS_Reference  + ";" +  Balance_ID  + ";" + Balance_Value + ";" + Offer_ID + ";" + Balance_StartDate + ";" + Balance_ExpiryDate + ";" + balanceInput.getBEBUCKETID());
											}
											if(CIS_Reference.startsWith("Renewal")  && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
											{
												CISOutput.add(CIS_Reference  + ";" +  Balance_ID  + ";" + Balance_Value + ";" + Offer_ID + ";" + Balance_StartDate + ";" + Balance_ExpiryDate + ";" + balanceInput.getBEBUCKETID());
											}
										}
									}	
								}
								if(PTMappingValue.size() == ValidProfileTagValue.size() && !BT25OfferCreated)
								{
									for(String s : CISOutput)
									{
										String CIS_Reference = s.split(";")[0];
										if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
										{									
											CISOnceOffList.add(PopulateCISOnce(s.split(";")[0],s.split(";")[1],s.split(";")[2],s.split(";")[3],s.split(";")[4], s.split(";")[5], s.split(";")[6]));
										}
										if(CIS_Reference.startsWith("Renewal")  && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
										{
											CISRenewalList.add(PopulateCISRenewal(s.split(";")[0],s.split(";")[1],s.split(";")[2],s.split(";")[3],s.split(";")[4],s.split(";")[5],"", s.split(";")[6]));
										}
										BT25OfferCreated = true;
									}
									//BT25OfferCreated = true;
								}
							}
							/*if(Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_Value))
							{
								List<String> PTMappingValue = Arrays.asList(LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getPTName().split("#"));
								
								for(String PtValue : PTMappingValue)
								{
									String PT_Name = PtValue.split("-")[0];
									String PT_Symbol = PtValue.split("-")[1];
									String PT_Value = PtValue.split("-")[2];
																												
									String PT_InputValue = profileTag.GetProfileTagValue(PT_Name);
									if(PT_Symbol.equals("=") && PT_InputValue.equals(PT_Value))
									{
										String Offer_ID = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferID();
										String CIS_Reference = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getCISReference();
										String expiryDate = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferExpiryDate();
										if(!expiryDate.equals("BT_Expiry_Date"))
										{
											String Date = profileTag.GetProfileTagValue("SmsExpDate");
											if(!Date.isEmpty() && Date.length() == 14)
												expiryDate = Date.substring(0,4) + "-" + Date.substring(4,6) + "-" + Date.substring(6,8) + " " + Date.substring(8,10) + ":" + Date.substring(10,12) + ":" + Date.substring(12,14);
											else
												expiryDate = Balance_ExpiryDate;
										}	
										else
										{
											expiryDate = Balance_ExpiryDate;
										}
										
										if(!BT25OfferCreated)
										{
											CompletedBT_ID.add(balanceInput.getBEBUCKETID());
											if(Balance_ExpiryDate.equals("1970-01-01 04:00:00"))
												Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
											
											if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
											{
												CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
											}
											if(CIS_Reference.startsWith("Renewal")  && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
											{
												CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"", balanceInput.getBEBUCKETID()));
											}
											BT25OfferCreated = true;
											break;
										}
									}	
								}
							}*/
						}						
					}
				}
				if(Balance_ID.equals("1266"))
				{
					if(Balance_Value.equals("0"))
					{
						String Offer_ID = LoadSubscriberMapping.Special1266BalanceDummy.get(Balance_ID + "|=").getOfferID();
						String CIS_Reference = LoadSubscriberMapping.Special1266BalanceDummy.get(Balance_ID + "|=").getCISReference();
						
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						if(Balance_ExpiryDate.equals("1970-01-01 04:00:00"))
							Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
						
						if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
						{
							CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
						}
						if(CIS_Reference.startsWith("Renewal")  && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
						{
							CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"", balanceInput.getBEBUCKETID()));
						}			
					}
					/*else if(Integer.parseInt(Balance_Value) >= 0)
					{
						String Offer_ID = LoadSubscriberMapping.Special1266BalanceDummy.get(Balance_ID + "|>=").getOfferID();
						String Offer_Type = LoadSubscriberMapping.Special1266BalanceDummy.get(Balance_ID + "|>=").getOfferType();
						boolean startFlag = LoadSubscriberMapping.Special1266BalanceDummy.get(Balance_ID + "|>=").getOfferStartDate().length() > 0 ? true:false;
						boolean expiryFalg = LoadSubscriberMapping.Special1266BalanceDummy.get(Balance_ID + "|>=").getOfferExpiryDate().length() > 0 ? true:false;
						BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"",""));
					}*/
					CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				}
				
				if(Balance_ID.equals("3280"))
				{
					for(String str : LoadSubscriberMapping.ProfileTagDummy)
					{							 
						if(LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str) != null)
						{
							String Symbol = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getSymbols();
							String BT_Value = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getBTValue();
							if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
							{										
								String PT_Name = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getPTName().split("-")[0];
								String PT_Symbol = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getPTName().split("-")[1];
								String PT_Value = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getPTName().split("-")[2];
																											
								String PT_InputValue = profileTag.GetProfileTagValue(PT_Name);
								if(PT_Symbol.equals("=") && PT_InputValue.equals(PT_Value) && !BTPA14BalanceDummy)
								{
									String Offer_ID = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferID();
									String CIS_Reference = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getCISReference();
									//String Symbol = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getSymbols();
									CompletedBT_ID.add(balanceInput.getBEBUCKETID());
									if(Balance_ExpiryDate.equals("1970-01-01 04:00:00"))
										Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
									
									if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
									{
										BTPA14BalanceDummy = true;
										CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
									}
								}									
							}
						}
					}
				}
				if(Balance_ID.equals("1383"))
				{
					for(String dummyValue : LoadSubscriberMapping.SpecialPA14BalanceDummy.keySet())
					{
						if(dummyValue.contains(Balance_ID))
						{
							String Symbol = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getSymbols();
							String BT_Value = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getBTValue();
							if(Balance_ID.equals("1383"))
							{
								if(Symbol.equals("<=") && Integer.parseInt(Balance_Value) <= Integer.parseInt(BT_Value) && !BTPA14BalanceDummy)
								{
									String Offer_ID = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferID();
									String CIS_Reference = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getCISReference();
									//String Symbol = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getSymbols();
									CompletedBT_ID.add(balanceInput.getBEBUCKETID());
									if(Balance_ExpiryDate.equals("1970-01-01 04:00:00"))
										Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
									
									if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
									{
										CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
									}
								}
								else if(Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_Value) && !BTPA14BalanceDummy)
								{
									String Offer_ID = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferID();
									String CIS_Reference = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getCISReference();
									//String Symbol = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getSymbols();
									CompletedBT_ID.add(balanceInput.getBEBUCKETID());
									if(Balance_ExpiryDate.equals("1970-01-01 04:00:00"))
										Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
									if(CIS_Reference.startsWith("Renewal")  && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
									{
										CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"", balanceInput.getBEBUCKETID()));
									}									
								}
								continue;
							}
						}
					}
				}
				if(Balance_ID.equals("1832") || Balance_ID.equals("1512") || Balance_ID.equals("1219") || Balance_ID.equals("1387") || Balance_ID.equals("2112") || Balance_ID.equals("2432"))
				{
					if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
					{
						String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
						String CIS_Reference = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getCISReference();
						//String Symbol = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getSymbols();
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						if(Balance_ExpiryDate.equals("1970-01-01 04:00:00"))
							Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
						if(CIS_Reference.startsWith("Renewal")  && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
						{
							CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"", balanceInput.getBEBUCKETID()));
						}
						if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
						{
							CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
						}
						/*if(CIS_Reference.equals("Parking") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
						{
							CISParkingList.add(PopulateCISParking(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate));
						}*/
					}
				}	
				if(Balance_ID.equals("3011"))
				{
					/*3011 is special case in which BT_VALUE is "0|1|2|3|4|5|6|7|8|9|10|11" but from input i will get
					individual value, so i need to find some logic to fix this */
					
					String BTValue3011 = "";
					for(String str : LoadSubscriberMapping.BT_VALUE_3011)
					{
						String[] TempBTValue = str.split("#");
						if(Arrays.stream(TempBTValue).anyMatch(Balance_Value::equals)){
							BTValue3011 = str;
							break;
						}											
					}
					
					if(BTValue3011.length() > 0)
					{
						if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + BTValue3011) != null)
						{
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + BTValue3011).getOfferID();
							String CIS_Reference = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + BTValue3011).getCISReference();
							if(Balance_ExpiryDate.equals("1970-01-01 04:00:00"))
								Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
							
							if(CIS_Reference.toLowerCase().startsWith("parking"))
								CISParkingList.add(PopulateCISParking(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
							if(CIS_Reference.startsWith("Renewal") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
								CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"", balanceInput.getBEBUCKETID()));
						}					
					}
					CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				}
			}
			else if(LoadSubscriberMapping.BalanceOnlySpecialASGroupSet.contains(Balance_ID))
			{	
				CompletedBT_ID.addAll(ComputeASpecialGroup(Balance_ID,CompletedBT_ID));
				continue;
			}
			/*else if(LoadSubscriberMapping.BalanceOnlySpecialAMGroupSet.contains(Balance_ID))
			{
				Map<String,Map<String,List<String>>> AMGroupOfferMap = new HashMap<>();
				AMGroupOfferMap = ComputeAMSpecialGroup(Balance_ID,Balance_Value,Balance_StartDate, Balance_ExpiryDate,balanceInput.getBEBUCKETID(),CompletedBT_ID);
				
				//AMGroupOfferMap.forEach((k,v)->System.out.println(k + "----" + v));
				
				Map<String,List<String>> OutputDetails = new HashMap<>();
				OutputDetails = AMGroupOfferMap.get("AMOutputDetails");
				if(OutputDetails.containsKey("Offer"))
					BalanceOfferList.addAll(OutputDetails.get("Offer"));
				if(OutputDetails.containsKey("CompletedBT"))
					CompletedBT_ID.addAll(OutputDetails.get("CompletedBT"));
				
				continue;
			}*/
			else
			{
				String GroupName = "";
				List<String> CurrentGroupBalance = new ArrayList<>();
				List<String> ValidGroupBalanceCIS = new ArrayList<>();
				//List<String> tempCISRenewalList = new ArrayList<>();
				//List<String> tempCISOnceOffList = new ArrayList<>();
				List<String> tempCISList = new ArrayList<>();
				for(Set<String> valueList : LoadSubscriberMapping.BalanceGroupingMap.values()) {					
					if(valueList.contains(Balance_ID)){
						GroupName = commonfunction.getKey(LoadSubscriberMapping.BalanceGroupingMap, valueList);
						if(GroupName.startsWith("DUMMY"))
							continue;
						if(GroupName.startsWith("D-"))
						{
							GroupName = commonfunction.ComputeDGroup(Balance_ID,GroupName,CompletedBT_ID);
							CurrentGroupBalance.addAll(LoadSubscriberMapping.BalanceGroupingMap.get(GroupName));
						}
						if(GroupName.startsWith("B-"))
						{
							GroupName = commonfunction.ComputeBGroup(Balance_ID,GroupName,CompletedBT_ID);
							CurrentGroupBalance.addAll(LoadSubscriberMapping.BalanceGroupingMap.get(GroupName));
						}
						if(GroupName.startsWith("A-"))
						{
							GroupName = commonfunction.ComputeAGroup(Balance_ID,GroupName,CompletedBT_ID);
							//Find if group name is part of AGroup which has same PC shared among multiple group 
							//and differentiated by the value of VBT.
							if(commonfunction.FindAGroupPCitemMultipleGroup(GroupName))
							{
								GroupName = commonfunction.ComputeAGroupPCitemMultipleGroup(Balance_ID,GroupName,CompletedBT_ID);
							}
							CurrentGroupBalance.addAll(LoadSubscriberMapping.BalanceGroupingMap.get(GroupName));
						}
						if(GroupName.startsWith("C-"))
						{
							GroupName = commonfunction.ComputeCGroup(Balance_ID,GroupName,CompletedBT_ID);
							CurrentGroupBalance.addAll(LoadSubscriberMapping.BalanceGroupingMap.get(GroupName));
						}
						if(GroupName.startsWith("F-"))
						{
							GroupName = commonfunction.ComputeFGroup(Balance_ID,GroupName,CompletedBT_ID);
							CurrentGroupBalance.addAll(LoadSubscriberMapping.BalanceGroupingMap.get(GroupName));
						}
						if(GroupName.startsWith("G-"))
						{
							continue;
						}
						break;
					}
				}		
				//System.out.println(Balance_ID + "----" +GroupName);						
				if(CurrentGroupBalance.size() > 0)
				{
					String FinalGroupName = GroupName;
					//System.out.println(FinalGroupName);	
					for(String id : CurrentGroupBalance)
					{
						//System.out.println(id);
						for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo TempbalanceInput :  SortedBalanceInput){
							String TempBalance_ID = TempbalanceInput.getBALANCETYPE();
							String TempBalance_Value = TempbalanceInput.getBEBUCKETVALUE();
							String TempBalance_StartDate = TempbalanceInput.getBEBUCKETSTARTDATE();
							String TempBalance_ExpiryDate = TempbalanceInput.getBEEXPIRY();							
							if(CompletedBT_ID.contains(TempbalanceInput.getBEBUCKETID()))
								continue;
							if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(TempBalance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(TempBalance_ID).equals("Y"))
							{
								CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
								continue;
							}
							
							if(!TempBalance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(TempBalance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
							{													
								CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
								continue;
							}
							
							if(Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
								Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
							
							
							if(id.equals(TempBalance_ID))
							{
								if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(id + "|" + FinalGroupName) != null)
								{
									String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getOfferID();
									String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getSymbols();
									String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTValue();
									String CIS_Reference = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getCISReference();
									String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTTYPE();
									
									if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
									{
										ValidGroupBalanceCIS.add(BT_Type + ";" + TempBalance_ID + ";" + Offer_ID + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate );
										CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
										if(CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
											tempCISList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + CIS_Reference + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
										
										break;
									}
									else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
									{
										ValidGroupBalanceCIS.add(BT_Type + ";" + TempBalance_ID + ";" + Offer_ID + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate );
										CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
										if(CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
											tempCISList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + CIS_Reference + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
										
										break;
									}
									else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
									{
										ValidGroupBalanceCIS.add(BT_Type + ";" + TempBalance_ID + ";" + Offer_ID + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate );
										CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
										if(CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
											tempCISList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + CIS_Reference + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
										
										break;
									}
									else if(Symbol.equals("or"))
									{	
										String[] values = BT_Value.split("#");
										if(Arrays.stream(values).anyMatch(TempBalance_Value::equals))
										{
											ValidGroupBalanceCIS.add(BT_Type + ";" + TempBalance_ID + ";" + Offer_ID + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate );
											CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
											if(CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
												tempCISList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + CIS_Reference + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
										}
										break;											
									}										
								}									
							}
						}
					}
					if(FinalGroupName.startsWith("A-"))
					{
						if(ValidGroupBalanceCIS.size() == CurrentGroupBalance.size())
						{
							String TargetCIS = "";
							for(String s : tempCISList)
							{
								if(s.split("\\|")[1].split(";")[0].length() > 0)
								{
									TargetCIS = s;
									break;
								}
							}
							
							if(TargetCIS.length() > 0)
							{
								String StartDate = TargetCIS.split("\\|")[1].split(";")[1];
								String ExpiryDate = TargetCIS.split("\\|")[1].split(";")[2];
								String CISType = TargetCIS.split("\\|")[1].split(";")[0].split("-")[0];
								String VBT_ID = TargetCIS.split("\\|")[1].split(";")[0].split("-")[2];
								
								if(TargetCIS.split("\\|")[1].split(";")[0].endsWith("NW#PC"))
								{
									
									CISRenewalList.add(PopulateCISRenewalNWPCs(TargetCIS.split("\\|")[1].split(";")[0], TargetCIS.split("\\|")[0].split(";")[2], TargetCIS.split("\\|")[0].split(";")[3], "",StartDate,ExpiryDate,"", TargetCIS.split("\\|")[1].split(";")[3], tempCISList));
								}
								else
								{									
									String BalanceValue = "";
									//BT_Type + ";" + getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + getBEBUCKETVALUE() +"|" + CISReference + ";" + startFlag + ";" + expiryFlag + ";" + getBEBUCKETSTARTDATE() + ";" + getBEEXPIRY()  + ";" + getBEBUCKETID());
									for(String s : tempCISList)
									{
										if(s.split("\\|")[0].split(";")[2].equals(VBT_ID))
										{
											BalanceValue = s.split("\\|")[0].split(";")[3];
											break;
										}
									}
									/*//Calling OnceOff for RENEWALOPTOUT
									if(CISType.toUpperCase().startsWith("RENEWALOPTOUT"))
									{
										String newCISReference = TargetCIS.split("\\|")[1].split(";")[0];
										newCISReference.replace("RenewalOPTOUT", "OnceOff");
										//String cisReference, String Balance_ID, String Balance_Value,String Offer_ID, String StartDate, String ExpiryDate,String NetworkType, String Bucket_ID) {
										CISOnceOffList.add(PopulateCISOnceOptOut(newCISReference,Balance_ID,Balance_Value,"",StartDate, ExpiryDate, TargetCIS.split("\\|")[1].split(";")[3]));
										//TargetCIS.split("\\|")[1].split(";")[0], TargetCIS.split("\\|")[0].split(";")[2], TargetCIS.split("\\|")[0].split(";")[3], "",StartDate,ExpiryDate,TargetCIS.split("\\|")[1].split(";")[3]));
									}*/									
									
									if(CISType.startsWith("Renewal"))
									{
										// cisReference,  Balance_ID,  Balance_Value, Offer_ID,  StartDate,  ExpiryDate
										if(!VBT_ID.equals("0"))
											CISRenewalList.add(PopulateCISRenewal(TargetCIS.split("\\|")[1].split(";")[0], VBT_ID, BalanceValue, "",StartDate,ExpiryDate,"" ,TargetCIS.split("\\|")[1].split(";")[3]));
										else
											CISRenewalList.add(PopulateCISRenewal(TargetCIS.split("\\|")[1].split(";")[0], TargetCIS.split("\\|")[0].split(";")[2], TargetCIS.split("\\|")[0].split(";")[3], "",StartDate,ExpiryDate,"", TargetCIS.split("\\|")[1].split(";")[3]));
									}
									else if(CISType.startsWith("OnceOff"))
									{
										// cisReference,  Balance_ID,  Balance_Value,  Offer_ID,  StartDate,  ExpiryDate
										if(!VBT_ID.equals("0"))
											CISOnceOffList.add(PopulateCISOnce(TargetCIS.split("\\|")[1].split(";")[0], VBT_ID, BalanceValue, "",StartDate,ExpiryDate,TargetCIS.split("\\|")[1].split(";")[3]));
										else
											CISOnceOffList.add(PopulateCISOnce(TargetCIS.split("\\|")[1].split(";")[0], TargetCIS.split("\\|")[0].split(";")[2], TargetCIS.split("\\|")[0].split(";")[3], "",StartDate,ExpiryDate,TargetCIS.split("\\|")[1].split(";")[3]));
									}
								}
							}
						}
						else
						{
							if(FinalGroupName.equals("A-3") && ValidGroupBalanceCIS.size() == 3)
							{
								Set<String> A_S_2 = new HashSet<>(Arrays.asList("2924","3034","2972"));
								Set<String> GroupBTID = new HashSet<>();
								ValidGroupBalanceCIS.forEach(item->{GroupBTID.add(item.split("\\|")[0].split(";")[2]);});
								
								
								if(A_S_2.containsAll(GroupBTID))
								{
									PopulateAS2Group(ValidGroupBalanceCIS,tempCISList);
									continue;
								}
							}
							
							if(LoadSubscriberMapping.BalanceAVGroupLookup.get(FinalGroupName) != null)
							{
								if(ValidGroupBalanceCIS.size() >= 2 && tempCISList.stream().filter(item->item.startsWith("P")).count() >=1 && tempCISList.stream().filter(item->item.startsWith("V")).count() >=1)
								{
									String TargetCIS = "";
									for(String s : tempCISList)
									{
										if(s.split("\\|")[1].split(";")[0].length() > 0)
										{
											TargetCIS = s;
											break;
										}
									}
									if(TargetCIS.length() > 0)
									{
										String StartDate = TargetCIS.split("\\|")[1].split(";")[1];
										String ExpiryDate = TargetCIS.split("\\|")[1].split(";")[2];
										String CISType = TargetCIS.split("\\|")[1].split(";")[0].split("-")[0];
										String VBT_ID = TargetCIS.split("\\|")[1].split(";")[0].split("-")[2];
										
										String BalanceValue = "";
										for(String s : tempCISList)
										{
											if(s.split("\\|")[0].split(";")[2].equals(VBT_ID))
											{
												BalanceValue = s.split("\\|")[0].split(";")[3];
												break;
											}
										}
										
										
										if(CISType.startsWith("Renewal"))
										{
											if(!VBT_ID.equals("0"))
												CISRenewalList.add(PopulateCISRenewal(TargetCIS.split("\\|")[1].split(";")[0], VBT_ID, BalanceValue, "",StartDate,ExpiryDate,"", TargetCIS.split("\\|")[1].split(";")[3]));
											else
												CISRenewalList.add(PopulateCISRenewal(TargetCIS.split("\\|")[1].split(";")[0], TargetCIS.split("\\|")[0].split(";")[2], TargetCIS.split("\\|")[0].split(";")[3], "",StartDate,ExpiryDate,"", TargetCIS.split("\\|")[1].split(";")[3]));
										}
										else if(CISType.startsWith("OnceOff"))
										{
											if(!VBT_ID.equals("0"))
												CISOnceOffList.add(PopulateCISOnce(TargetCIS.split("\\|")[1].split(";")[0], VBT_ID, BalanceValue, "",StartDate,ExpiryDate,TargetCIS.split("\\|")[1].split(";")[3]));
											else
												CISOnceOffList.add(PopulateCISOnce(TargetCIS.split("\\|")[1].split(";")[0], TargetCIS.split("\\|")[0].split(";")[2], TargetCIS.split("\\|")[0].split(";")[3], "", StartDate, ExpiryDate, TargetCIS.split("\\|")[1].split(";")[3]));
										}
									}		
								}
								else
								{
									if(tempCISList.stream().filter(item->item.startsWith("P")).count() >=1)
									{
										String TargetCIS =  tempCISList.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0);
										//CISonlyLog.add("INC1103:CIS group condition failed:MSISDN=" + msisdn + ":BUNDLE_TYPE=" + "Renewal" + ":PRODUCT_ID=" + TargetCIS.split("\\|")[1].split(";")[0].split("-")[1] + ":BT_ID=" + TargetCIS.split("\\|")[0].split(";")[2] + ":BT_VALUE=" + TargetCIS.split("\\|")[0].split(";")[3] + ":ACTION=Logging");
										/*for(String s : tempCISList)
										{
											String ProductID = "";
											if(!s.split("\\|")[1].split(";")[0].isEmpty())
												ProductID = s.split("\\|")[1].split(";")[0].split("-")[0];
											onlyLog.add("INC4006:BT mapped CIS Product Mapping failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + s.split("\\|")[0].split(";")[2] + ":BE_BUCKET_VALUE=" + s.split("\\|")[0].split(";")[3] + ":BE_BUCKET_ID=" + s.split("\\|")[1].split(";")[3] +  ":BUNDLE_TYPE=" + "OnceOff" + ":PRODUCT_ID=" + ProductID + ":ACTION=Logging");
										}*/
									}									
								}
								//Check and Populate master for CIS if that is present
								PopulateMasterCIS(tempCISList);
							}
							else
							{
								if(ValidGroupBalanceCIS.size() >= 1 && ValidGroupBalanceCIS.stream().filter(item->item.startsWith("P")).count() >=1)
								{
									String TargetCIS = "";
									for(String s : tempCISList)
									{
										if(s.split("\\|")[1].split(";")[0].length() > 0)
										{
											TargetCIS = s;
											break;
										}
									}
									if(TargetCIS.length() > 0)
									{
										String StartDate = TargetCIS.split("\\|")[1].split(";")[1];
										String ExpiryDate = TargetCIS.split("\\|")[1].split(";")[2];
										String CISType = TargetCIS.split("\\|")[1].split(";")[0].split("-")[0];
										
										String VBT_ID = TargetCIS.split("\\|")[1].split(";")[0].split("-")[2];
										
										String BalanceValue = "";
										for(String s : tempCISList)
										{
											if(s.split("\\|")[0].split(";")[2].equals(VBT_ID))
											{
												BalanceValue = s.split("\\|")[0].split(";")[3];
												break;
											}
										}
										
										if(CISType.startsWith("Renewal"))
										{
											if(!VBT_ID.equals("0"))
												CISRenewalList.add(PopulateCISRenewal(TargetCIS.split("\\|")[1].split(";")[0], VBT_ID, BalanceValue, "",StartDate,ExpiryDate,"", TargetCIS.split("\\|")[1].split(";")[3]));
											else
												CISRenewalList.add(PopulateCISRenewal(TargetCIS.split("\\|")[1].split(";")[0], TargetCIS.split("\\|")[0].split(";")[2], TargetCIS.split("\\|")[0].split(";")[3], "",StartDate,ExpiryDate,"", TargetCIS.split("\\|")[1].split(";")[3]));
										}
										else if(CISType.startsWith("OnceOff"))
										{
											if(!VBT_ID.equals("0"))
												CISOnceOffList.add(PopulateCISOnce(TargetCIS.split("\\|")[1].split(";")[0], VBT_ID, BalanceValue, "", StartDate, ExpiryDate, TargetCIS.split("\\|")[1].split(";")[3]));
											else
												CISOnceOffList.add(PopulateCISOnce(TargetCIS.split("\\|")[1].split(";")[0], TargetCIS.split("\\|")[0].split(";")[2], TargetCIS.split("\\|")[0].split(";")[3], "", StartDate, ExpiryDate, TargetCIS.split("\\|")[1].split(";")[3]));
										}
									}										
								}
								else
								{
									
								}
							}
						}
					}
					if(FinalGroupName.startsWith("B-"))
					{
						String TargetCIS = "";
						for(String s : tempCISList)
						{
							if(s.split("\\|")[1].split(";")[0].length() > 0)
							{
								TargetCIS = s;
								break;
							}
						}
						
						if(TargetCIS.length() > 0)
						{
							String StartDate = TargetCIS.split("\\|")[1].split(";")[1];
							String ExpiryDate = TargetCIS.split("\\|")[1].split(";")[2];
							String CISType = TargetCIS.split("\\|")[1].split(";")[0].split("-")[0];
							
							String VBT_ID = TargetCIS.split("\\|")[1].split(";")[0].split("-")[2];
							
							String BalanceValue = "";
							for(String s : tempCISList)
							{
								if(s.split("\\|")[0].split(";")[2].equals(VBT_ID))
								{
									BalanceValue = s.split("\\|")[0].split(";")[3];
									break;
								}
							}
							
							if(CISType.startsWith("Renewal"))
							{
								if(!VBT_ID.equals("0"))
									CISRenewalList.add(PopulateCISRenewal(TargetCIS.split("\\|")[1].split(";")[0], VBT_ID, BalanceValue, "",StartDate,ExpiryDate,"", TargetCIS.split("\\|")[1].split(";")[3]));
								else
									CISRenewalList.add(PopulateCISRenewal(TargetCIS.split("\\|")[1].split(";")[0], TargetCIS.split("\\|")[0].split(";")[2], TargetCIS.split("\\|")[0].split(";")[3], "",StartDate,ExpiryDate,"", TargetCIS.split("\\|")[1].split(";")[3]));
							}
							else if(CISType.startsWith("OnceOff"))
							{
								if(!VBT_ID.equals("0"))
									CISOnceOffList.add(PopulateCISOnce(TargetCIS.split("\\|")[1].split(";")[0], VBT_ID, BalanceValue, "", StartDate, ExpiryDate, TargetCIS.split("\\|")[1].split(";")[3]));
								else
									CISOnceOffList.add(PopulateCISOnce(TargetCIS.split("\\|")[1].split(";")[0], TargetCIS.split("\\|")[0].split(";")[2], TargetCIS.split("\\|")[0].split(";")[3], "", StartDate, ExpiryDate, TargetCIS.split("\\|")[1].split(";")[3]));
							}
						}						
					}
					if(FinalGroupName.startsWith("D-"))
					{
						if(ValidGroupBalanceCIS.size() == CurrentGroupBalance.size())
						{
							String TargetCIS = "";
							for(String s : tempCISList)
							{
								if(s.split("\\|")[1].split(";")[0].length() > 0)
								{
									TargetCIS = s;
									break;
								}
							}
							
							if(TargetCIS.length() > 0)
							{
								String StartDate = TargetCIS.split("\\|")[1].split(";")[1];
								String ExpiryDate = TargetCIS.split("\\|")[1].split(";")[2];
								String CISType = TargetCIS.split("\\|")[1].split(";")[0].split("-")[0];
								
								String VBT_ID = TargetCIS.split("\\|")[1].split(";")[0].split("-")[2];
								
								String BalanceValue = "";
								for(String s : tempCISList)
								{
									if(s.split("\\|")[0].split(";")[2].equals(VBT_ID))
									{
										BalanceValue = s.split("\\|")[0].split(";")[3];
										break;
									}
								}
								
								if(CISType.startsWith("Renewal"))
								{
									if(!VBT_ID.equals("0"))
										CISRenewalList.add(PopulateCISRenewal(TargetCIS.split("\\|")[1].split(";")[0], VBT_ID, BalanceValue, "",StartDate,ExpiryDate,"", TargetCIS.split("\\|")[1].split(";")[3]));
									else
										CISRenewalList.add(PopulateCISRenewal(TargetCIS.split("\\|")[1].split(";")[0], TargetCIS.split("\\|")[0].split(";")[2], TargetCIS.split("\\|")[0].split(";")[3], "",StartDate,ExpiryDate,"", TargetCIS.split("\\|")[1].split(";")[3]));
								}
								else if(CISType.startsWith("OnceOff"))
								{
									if(!VBT_ID.equals("0"))
										CISOnceOffList.add(PopulateCISOnce(TargetCIS.split("\\|")[1].split(";")[0], VBT_ID, BalanceValue, "", StartDate, ExpiryDate, TargetCIS.split("\\|")[1].split(";")[3]));
									else
										CISOnceOffList.add(PopulateCISOnce(TargetCIS.split("\\|")[1].split(";")[0], TargetCIS.split("\\|")[0].split(";")[2], TargetCIS.split("\\|")[0].split(";")[3], "", StartDate, ExpiryDate, TargetCIS.split("\\|")[1].split(";")[3]));
								}
							}	
						}
					}
					if(FinalGroupName.startsWith("F-"))
					{
						String TargetCIS = "";
						for(String s : tempCISList)
						{
							if(s.split("\\|")[1].split(";")[0].length() > 0)
							{
								TargetCIS = s;
								break;
							}
						}
						
						if(TargetCIS.length() > 0)
						{
							String StartDate = TargetCIS.split("\\|")[1].split(";")[1];
							String ExpiryDate = TargetCIS.split("\\|")[1].split(";")[2];
							String CISType = TargetCIS.split("\\|")[1].split(";")[0].split("-")[0];
							
							String VBT_ID = TargetCIS.split("\\|")[1].split(";")[0].split("-")[2];
							
							String BalanceValue = "";
							for(String s : tempCISList)
							{
								if(s.split("\\|")[0].split(";")[2].equals(VBT_ID))
								{
									BalanceValue = s.split("\\|")[0].split(";")[3];
									break;
								}
							}
							
							if(CISType.startsWith("Renewal"))
							{
								if(!VBT_ID.equals("0"))
									CISRenewalList.add(PopulateCISRenewal(TargetCIS.split("\\|")[1].split(";")[0], VBT_ID, BalanceValue, "",StartDate,ExpiryDate,"", TargetCIS.split("\\|")[1].split(";")[3]));
								else
									CISRenewalList.add(PopulateCISRenewal(TargetCIS.split("\\|")[1].split(";")[0], TargetCIS.split("\\|")[0].split(";")[2], TargetCIS.split("\\|")[0].split(";")[3], "",StartDate,ExpiryDate,"", TargetCIS.split("\\|")[1].split(";")[3]));
							}
							else if(CISType.startsWith("OnceOff"))
							{
								if(!VBT_ID.equals("0"))
									CISOnceOffList.add(PopulateCISOnce(TargetCIS.split("\\|")[1].split(";")[0], VBT_ID, BalanceValue, "", StartDate, ExpiryDate, TargetCIS.split("\\|")[1].split(";")[3]));
								else
									CISOnceOffList.add(PopulateCISOnce(TargetCIS.split("\\|")[1].split(";")[0], TargetCIS.split("\\|")[0].split(";")[2], TargetCIS.split("\\|")[0].split(";")[3], "", StartDate, ExpiryDate, TargetCIS.split("\\|")[1].split(";")[3]));
							}
						}
						else
						{
							if(FinalGroupName.startsWith("F-"))
							{
								for(String CISValue : tempCISList)
								{		
									String GroupLastChar = FinalGroupName.substring(FinalGroupName.lastIndexOf('-')+1,FinalGroupName.length());
									String MasterGroupName = FinalGroupName.replace(GroupLastChar, "M");
									String BT_ID = CISValue.split("\\|")[0].split(";")[2];
									//BT_Type + ";" + getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + getBEBUCKETVALUE() +"|" + CIS_Reference + ";" + getBEBUCKETSTARTDATE() + ";" + getBEEXPIRY()  + ";" + getBEBUCKETID());
									if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName) != null)
									{
										String CISType = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getCISReference();
										if(!CISType.isEmpty())
										{
											String SourceBT_Value = CISValue.split("\\|")[0].split(";")[3];											
											String BT_StartDate  = CISValue.split("\\|")[1].split(";")[1];
											String BT_ExpiryDate  = CISValue.split("\\|")[1].split(";")[2];
											
											String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getOfferID();
											String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getSymbols();
											String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getBTValue();													
											boolean bCreateOffer = false;
											
											if(Symbol.equals(">=") && Integer.parseInt(SourceBT_Value) >= Integer.parseInt(BT_VALUE))
												bCreateOffer = true;
											else if(Symbol.equals(">") && Integer.parseInt(SourceBT_Value) > Integer.parseInt(BT_VALUE))
												bCreateOffer = true;
											else if(Symbol.equals("=") && Integer.parseInt(SourceBT_Value) == Integer.parseInt(BT_VALUE))
												bCreateOffer = true;
											else
											{											
												break;
											}
											if(bCreateOffer)
											{
												if(CISType.startsWith("Renewal"))
												{
													CISRenewalList.add(PopulateCISRenewal(CISType, BT_ID, SourceBT_Value, Offer_ID,BT_StartDate,BT_ExpiryDate,"", CISValue.split("\\|")[1].split(";")[2]));
												}
												else if(CISType.startsWith("OnceOff"))
												{
													CISOnceOffList.add(PopulateCISOnce(CISType, BT_ID, SourceBT_Value, Offer_ID, BT_StartDate, BT_ExpiryDate, CISValue.split("\\|")[1].split(";")[3]));
												}
											}
										}									
									//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ SourceOffer.split(";")[0]  +":ACTION=Logging");
									}
								}
							}
							else
							{
								//PopulateMasterCIS(tempCISList);
							}
						}
					}
				}
			}			
		}
	}
	
	private void PopulateAS2Group(List<String> validGroupBalanceCIS, List<String> tempCISList) 
	{
		
		String TargetCIS = "";
		for(String s : tempCISList)
		{
			//if(s.split("\\|")[1].split(";")[0].length() > 0)
			{
				if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.containsKey(s.split("\\|")[0].split(";")[2] + "|A-S-2"))
				{
					//String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(s.split("\\|")[0].split(";")[2] + "|A-S-2").getOfferID();
					String CIS_Reference = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(s.split("\\|")[0].split(";")[2] + "|A-S-2").getCISReference();
					if(!CIS_Reference.isEmpty())
					{
						TargetCIS = s.split("\\|")[0] + "|" + CIS_Reference + ";" + s.split("\\|")[1].split(";")[1] + ";" + s.split("\\|")[1].split(";")[2] + ";" + s.split("\\|")[1].split(";")[3];
						break;
					}
				}
			}
		}
		if(TargetCIS.length() > 0)
		{
			String StartDate = TargetCIS.split("\\|")[1].split(";")[1];
			String ExpiryDate = TargetCIS.split("\\|")[1].split(";")[2];
			String CISType = TargetCIS.split("\\|")[1].split(";")[0].split("-")[0];
			String VBT_ID = TargetCIS.split("\\|")[1].split(";")[0].split("-")[2];
			
			String BalanceValue = "";
			for(String s : tempCISList)
			{
				if(s.split("\\|")[0].split(";")[2].equals(VBT_ID))
				{
					BalanceValue = s.split("\\|")[0].split(";")[3];
					break;
				}
			}
			
			if(CISType.startsWith("Renewal"))
			{
				if(!VBT_ID.equals("0"))
					CISRenewalList.add(PopulateCISRenewal(TargetCIS.split("\\|")[1].split(";")[0], VBT_ID, BalanceValue, "",StartDate,ExpiryDate,"", TargetCIS.split("\\|")[1].split(";")[3]));
				else
					CISRenewalList.add(PopulateCISRenewal(TargetCIS.split("\\|")[1].split(";")[0], TargetCIS.split("\\|")[0].split(";")[2], TargetCIS.split("\\|")[0].split(";")[3], "",StartDate,ExpiryDate,"", TargetCIS.split("\\|")[1].split(";")[3]));
			}
			else if(CISType.startsWith("OnceOff"))
			{
				if(!VBT_ID.equals("0"))
					CISOnceOffList.add(PopulateCISOnce(TargetCIS.split("\\|")[1].split(";")[0], VBT_ID, BalanceValue, "",StartDate,ExpiryDate,TargetCIS.split("\\|")[1].split(";")[3]));
				else
					CISOnceOffList.add(PopulateCISOnce(TargetCIS.split("\\|")[1].split(";")[0], TargetCIS.split("\\|")[0].split(";")[2], TargetCIS.split("\\|")[0].split(";")[3], "", StartDate, ExpiryDate, TargetCIS.split("\\|")[1].split(";")[3]));
			}
		}
	}
	
	private List<String> PopulateMasterCIS(List<String> ValidGroupBalanceCIS) //,Set<String> BEIDForProductID)
	{
		List<String> BalanceOfferList = new ArrayList<>();
		
		for(String Str : ValidGroupBalanceCIS)
		{
			String TargetValue = Str.split("\\|")[0];
			String SourceValue = Str.split("\\|")[1];
			
			if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetValue.split(";")[2] + "|M") != null)
			{
				String CISType = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetValue.split(";")[2] + "|M").getCISReference();
				
				if(!CISType.isEmpty())
				{	
					String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetValue.split(";")[2] + "|M").getOfferID();
					String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetValue.split(";")[2] + "|M").getSymbols();
					String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetValue.split(";")[2] + "|M").getBTValue();
					
					String VBT_ID = CISType.split("-")[2];
					String StartDate = SourceValue.split(";")[1];
					String ExpiryDate = SourceValue.split(";")[2];
					String productID = CISType.split("-")[1];
					
					if(Symbol.equals(">") && Integer.parseInt(TargetValue.split(";")[3]) > Integer.parseInt(BT_VALUE))
					{
						if(CISType.startsWith("Renewal"))
						{
							CISRenewalList.add(PopulateCISRenewal(CISType, TargetValue.split(";")[2], TargetValue.split(";")[3], Offer_ID,StartDate,ExpiryDate,"", SourceValue.split(";")[3]));
						}
						else if(CISType.startsWith("OnceOff"))
						{
							CISOnceOffList.add(PopulateCISOnce(CISType, TargetValue.split(";")[2], TargetValue.split(";")[3], Offer_ID, StartDate, ExpiryDate, SourceValue.split(";")[3]));
						}
						continue;
					}
					if(Symbol.equals(">=") && Integer.parseInt(TargetValue.split(";")[3]) >= Integer.parseInt(BT_VALUE))
					{
						if(CISType.startsWith("Renewal"))
						{
							CISRenewalList.add(PopulateCISRenewal(CISType, TargetValue.split(";")[2], TargetValue.split(";")[3], Offer_ID,StartDate,ExpiryDate,"", SourceValue.split(";")[3]));
						}
						else if(CISType.startsWith("OnceOff"))
						{
							CISOnceOffList.add(PopulateCISOnce(CISType, TargetValue.split(";")[2], TargetValue.split(";")[3], Offer_ID, StartDate, ExpiryDate, SourceValue.split(";")[3]));
						}
						continue;
					}
					if(Symbol.equals("=") && Integer.parseInt(TargetValue.split(";")[3]) == Integer.parseInt(BT_VALUE))
					{
						if(CISType.startsWith("Renewal"))
						{
							CISRenewalList.add(PopulateCISRenewal(CISType, TargetValue.split(";")[2], TargetValue.split(";")[3], Offer_ID,StartDate,ExpiryDate,"", SourceValue.split(";")[3]));
						}
						else if(CISType.startsWith("OnceOff"))
						{
							CISOnceOffList.add(PopulateCISOnce(CISType, TargetValue.split(";")[2], TargetValue.split(";")[3], Offer_ID, StartDate, ExpiryDate, SourceValue.split(";")[3]));
						}
						continue;
					}
					if(Symbol.equals("or"))
					{
						String[] values = BT_VALUE.split("#");											
						if(Arrays.stream(values).anyMatch(TargetValue.split(";")[3]::equals))
						{
							//Special case of BT1454, where expiry date should be populated as migration date and expiry as migration_Date+30
							if(TargetValue.split(";")[2].equals("1454"))
							{
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Calendar c = Calendar.getInstance();
								try 
								{
									String CurrectExpiryDate = "";
									c.setTime(sdf.parse(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()));
									c.add(Calendar.DAY_OF_MONTH, 30); 
									CurrectExpiryDate = sdf.format(c.getTime());
									if(CISType.startsWith("Renewal"))
									{
										CISRenewalList.add(PopulateCISRenewal(CISType, TargetValue.split(";")[2], TargetValue.split(";")[3], Offer_ID,StartDate,ExpiryDate,"", SourceValue.split(";")[3]));
									}
									else if(CISType.startsWith("OnceOff"))
									{
										CISOnceOffList.add(PopulateCISOnce(CISType, TargetValue.split(";")[2], TargetValue.split(";")[3], Offer_ID, StartDate, ExpiryDate, SourceValue.split(";")[3]));
									}
									
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
							else
							{
								if(CISType.startsWith("Renewal"))
								{
									CISRenewalList.add(PopulateCISRenewal(CISType, TargetValue.split(";")[2], TargetValue.split(";")[3], Offer_ID,StartDate,ExpiryDate,"", SourceValue.split(";")[3]));
								}
								else if(CISType.startsWith("OnceOff"))
								{
									CISOnceOffList.add(PopulateCISOnce(CISType, TargetValue.split(";")[2], TargetValue.split(";")[3], Offer_ID, StartDate, ExpiryDate, SourceValue.split(";")[3]));
								}
								//BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,SourceOffer.split(";")[4], SourceOffer.split(";")[5],"",Offer_flag, new HashSet<>(Arrays.asList(SourceOffer.split(";")[9]))));
							}
							continue;
						}					
					}		
				}
			}
		}		
		return BalanceOfferList;
	}
	
	private void CISGenerationFromGraceBalanceMapping()
	{
		Set<String> CompletedBT_ID = new HashSet<>();
		Set<String> CompletedGroupBT_ID = new HashSet<>();
		
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{			
			String Balance_ID = balanceInput.getBALANCETYPE();
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			CompletedGroupBT_ID.clear();
			if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
				continue;
			
			if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(Balance_ID))
				continue;
			
			if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) == "Y")
			{
				CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				continue;
			}
			
			//Check for expiry Date, log it and proceed further
			if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
			{
				CompletedBT_ID.add(balanceInput.getBEBUCKETID());
			}
			else
			{
				if(LoadSubscriberMapping.SpecialGraceList.contains(Balance_ID))
				{	
					List<String> GroupNames =  commonfunction.getASpecialGroupKey(LoadSubscriberMapping.GraceBalanceGroupingMap, Balance_ID);
					
					for(String gracename : GroupNames)
					{
						Set<String> GraceBTs = LoadSubscriberMapping.GraceBalanceGroupingMap.get(gracename);
						List<String> BalanceCISList = new ArrayList<>();
						List<String> ValidCISList = new ArrayList<>();
						for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo tempbalanceInput : SortedBalanceInput)
						{			
							String TempBalance_ID = tempbalanceInput.getBALANCETYPE();
							//System.out.println("Master Balance_ID: " + Balance_ID);
							String TempBalance_Value = tempbalanceInput.getBEBUCKETVALUE();
							String TempBalance_StartDate = tempbalanceInput.getBEBUCKETSTARTDATE();
							String TempBalance_ExpiryDate = tempbalanceInput.getBEEXPIRY();
							String TempBeBucket_ID = tempbalanceInput.getBEBUCKETID();
							if(CompletedBT_ID.contains(TempBeBucket_ID))
								continue;
							
							if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(TempBalance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(TempBalance_ID) == "Y")
							{
								CompletedBT_ID.add(TempBeBucket_ID);
								continue;
							}
							
							if(!TempBalance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(TempBalance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
							{
								CompletedBT_ID.add(tempbalanceInput.getBEBUCKETID());
								continue;
							}
							
							if(GraceBTs.contains(TempBalance_ID))							
							{								
		 						if(LoadSubscriberMapping.SpecialGraceBalance.get(TempBalance_ID + '|' + gracename) != null)
								{
									String Offer_ID = LoadSubscriberMapping.SpecialGraceBalance.get(TempBalance_ID + '|' + gracename).getOfferID();
									String Symbol = LoadSubscriberMapping.SpecialGraceBalance.get(TempBalance_ID + '|' + gracename).getSymbols();
									String BT_Value = LoadSubscriberMapping.SpecialGraceBalance.get(TempBalance_ID + '|' + gracename).getBTValue();
									String CIS_Reference = 	LoadSubscriberMapping.SpecialGraceBalance.get(TempBalance_ID + '|' + gracename).getCISReference();
									String BT_TYPE = LoadSubscriberMapping.SpecialGraceBalance.get(TempBalance_ID + '|' + gracename).getBTTYPE();
									
									if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
									{
										BalanceCISList.add(CIS_Reference + ";" + TempBalance_ID + ";"+ TempBalance_Value + ";" + Offer_ID + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate + ";" + TempBeBucket_ID);
										ValidCISList.add(tempbalanceInput.getBEBUCKETID());
									}
									else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
									{
										BalanceCISList.add(CIS_Reference + ";" + TempBalance_ID + ";"+ TempBalance_Value + ";" + Offer_ID + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate + ";" + TempBeBucket_ID);
										ValidCISList.add(tempbalanceInput.getBEBUCKETID());	
									}
									else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
									{
										BalanceCISList.add(CIS_Reference + ";" + TempBalance_ID + ";"+ TempBalance_Value + ";" + Offer_ID + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate + ";" + TempBeBucket_ID);
										ValidCISList.add(tempbalanceInput.getBEBUCKETID());
									}
									else if(Symbol.equals("or"))
									{
										//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
										String[] values = BT_Value.split("#");
										
										if(Arrays.stream(values).anyMatch(TempBalance_Value::equals))
										{
											BalanceCISList.add(CIS_Reference + ";" + TempBalance_ID + ";"+ TempBalance_Value + ";" + Offer_ID + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate + ";" + TempBeBucket_ID);
											ValidCISList.add(tempbalanceInput.getBEBUCKETID());	
										}
										else
										{
											if(LoadSubscriberMapping.GraceFullBalanceGroupingMap.containsKey(TempBalance_ID))
											{
												Set<String> BTValues = new HashSet<String>();
												BTValues.addAll(LoadSubscriberMapping.GraceFullBalanceGroupingMap.get(TempBalance_ID));
												if(!BTValues.contains(TempBalance_Value))
												{	
													CompletedBT_ID.add(tempbalanceInput.getBEBUCKETID());
													break;
												}
											}
										}
									}
									else
									{
										if(LoadSubscriberMapping.GraceFullBalanceGroupingMap.containsKey(TempBalance_ID))
										{
											Set<String> BTValues = new HashSet<String>();
											BTValues.addAll(LoadSubscriberMapping.GraceFullBalanceGroupingMap.get(TempBalance_ID));
											if(!BTValues.contains(TempBalance_Value))
											{												
												CompletedBT_ID.add(tempbalanceInput.getBEBUCKETID());
											}
										}
									}
								}
							}
						}
						if(ValidCISList.size() == GraceBTs.size())
						{
							for(String item : BalanceCISList)
							{
								if(item.startsWith("Renewal"))
									CISRenewalList.add(PopulateCISRenewal(item.split(";")[0],item.split(";")[1],item.split(";")[2], item.split(";")[3],item.split(";")[4],item.split(";")[5],"Grace", item.split(";")[6]));
							}
							
							for(String item : BalanceCISList)
							{
								if(item.startsWith("OnceOff"))
									CISOnceOffList.add(PopulateCISOnce(item.split(";")[0],item.split(";")[1],item.split(";")[2], item.split(";")[3],item.split(";")[4], item.split(";")[5], item.split(";")[6]));
							}
							CompletedBT_ID.addAll(ValidCISList);
							break;
						}
					}
				}
				else
					CompletedBT_ID.add(balanceInput.getBEBUCKETID());
			}
		}		
	}
	
	private void CISGenerationFromGraceMBalanceMapping()
	{
		Set<String> CompletedBT_ID = new HashSet<>();
		Set<String> CompletedGroupBT_ID = new HashSet<>();
		
		//System.out.println(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()));
		
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{			
			String Balance_ID = balanceInput.getBALANCETYPE();
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			//System.out.println("Master Balance_ID: " + Balance_ID);
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			String Balance_StartDate = balanceInput.getBEBUCKETSTARTDATE();
			CompletedGroupBT_ID.clear();
			if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
				continue;
			
			if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(Balance_ID))
				continue;
			
			if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) == "Y")
			{
				CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				continue;
			}
			
			//Check for expiry Date, log it and proceed further
			if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
			{
				CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				continue;
			}
			else
			{
				if(LoadSubscriberMapping.SpecialGraceList.contains(Balance_ID))
				{	
					List<String> GroupNames =  commonfunction.getASpecialGroupKey(LoadSubscriberMapping.GraceBalanceGroupingMap, Balance_ID);
					
					for(String gracename : GroupNames)
					{
						if(!gracename.equals("GRACE-M"))
							continue;
						
						Set<String> GraceBTs = LoadSubscriberMapping.GraceBalanceGroupingMap.get(gracename);
						
						if(GraceBTs.contains(Balance_ID) && gracename.equals("GRACE-M"))							
						{								
	 						if(LoadSubscriberMapping.SpecialGraceBalance.get(Balance_ID + '|' + gracename) != null)
							{
								String Offer_ID = LoadSubscriberMapping.SpecialGraceBalance.get(Balance_ID + '|' + gracename).getOfferID();
								String Symbol = LoadSubscriberMapping.SpecialGraceBalance.get(Balance_ID + '|' + gracename).getSymbols();
								String BT_Value = LoadSubscriberMapping.SpecialGraceBalance.get(Balance_ID + '|' + gracename).getBTValue();
								String CIS_Reference = 	LoadSubscriberMapping.SpecialGraceBalance.get(Balance_ID + '|' + gracename).getCISReference();
								String BT_TYPE = LoadSubscriberMapping.SpecialGraceBalance.get(Balance_ID + '|' + gracename).getBTTYPE();
								
								if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
								{
									if(CIS_Reference.startsWith("Renewal") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
										CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"Grace", balanceInput.getBEBUCKETID()));
									if(CIS_Reference.startsWith("OnceOff"))
										CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
									CompletedBT_ID.add(balanceInput.getBEBUCKETID());										
								}
								else if(Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_Value))
								{
									if(CIS_Reference.startsWith("Renewal") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
										CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"Grace", balanceInput.getBEBUCKETID()));
									if(CIS_Reference.startsWith("OnceOff"))
										CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
									CompletedBT_ID.add(balanceInput.getBEBUCKETID());
								}
								else if(Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value))
								{
									if(CIS_Reference.startsWith("Renewal") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
										CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"Grace", balanceInput.getBEBUCKETID()));
									if(CIS_Reference.startsWith("OnceOff")&& CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
										CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
									CompletedBT_ID.add(balanceInput.getBEBUCKETID());
								}
								else if(Symbol.equals("or"))
								{
									//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
									String[] values = BT_Value.split("#");
									
									if(Arrays.stream(values).anyMatch(Balance_Value::equals))
									{
										if(CIS_Reference.startsWith("Renewal") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
											CISRenewalList.add(PopulateCISRenewal(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate,"Grace", balanceInput.getBEBUCKETID()));
										if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
											CISOnceOffList.add(PopulateCISOnce(CIS_Reference,Balance_ID,Balance_Value,Offer_ID,Balance_StartDate, Balance_ExpiryDate, balanceInput.getBEBUCKETID()));
										CompletedBT_ID.add(balanceInput.getBEBUCKETID());
									}
								}										
							}
						}
					}
				}
				else
					CompletedBT_ID.add(balanceInput.getBEBUCKETID());
			}
		}
	}
		
	/*private void CISGenerationFromGGroupBalanceMapping()
	{
		Date currDate = new Date();
		SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<String> BalanceCISList =new ArrayList<>();
		
		Set<String> CompletedBT_ID = new HashSet<>();
		Set<String> CompletedGroupBT_ID = new HashSet<>();
		
		//System.out.println(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()));
		
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{			
			String Balance_ID = balanceInput.getBALANCETYPE();
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			CompletedGroupBT_ID.clear();
			BalanceCISList.clear();
			if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
				continue;
			
			if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) == "Y")
			{
				CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				continue;
			}
			//Check for expiry Date, log it and proceed further
			if(!Balance_ExpiryDate.equals("1970-01-01 04:00:00") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
			{
				CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				continue;
			}
			else
			{	
				if(LoadSubscriberMapping.SpecialGGroupList.contains(Balance_ID))
				{					
					Map<String,Map<String,List<String>>> CISGGroupList = new HashMap<>();
					String GroupName = "";
					CISGGroupList.putAll(PopulateGGroup(Balance_ID,GroupName,CompletedBT_ID));	
					Map<String,List<String>> OutputDetails = new HashMap<>();
					OutputDetails = CISGGroupList.get("GOutput");
					
					if(OutputDetails == null)
						continue;
					
					if(OutputDetails.containsKey("CIS_Value"))
					{
						BalanceCISList.addAll(OutputDetails.get("CIS_Value"));
						//String CISRenewal = BalanceCISList.stream().filter(item->item.split("\\|")[1].split(";")[0].startsWith("R")).collect(Collectors.toList()).get(0);
						//BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + CISReference + ";" + startFlag + ";" + expiryFlag + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
						if(BalanceCISList.size() > 0)
						{
							String TargetOffer =  BalanceCISList.stream().filter(item->item.startsWith("P")).findFirst().orElse(null);
							
							if(TargetOffer != null)
							{
								String StartDate = TargetOffer.split("\\|")[1].split(";")[1];
								String ExpiryDate = TargetOffer.split("\\|")[1].split(";")[2];
								String CISType = TargetOffer.split("\\|")[1].split(";")[0].split("-")[0];
								String productID = TargetOffer.split("\\|")[1].split(";")[0].split("-")[1];
								String VBT_ID = TargetOffer.split("\\|")[1].split(";")[0].split("-")[2];
								
								String BalanceValue = "";
								//BT_Type + ";" + getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + getBEBUCKETVALUE() +"|" + CISReference + ";" + startFlag + ";" + expiryFlag + ";" + getBEBUCKETSTARTDATE() + ";" + getBEEXPIRY()  + ";" + getBEBUCKETID());
								for(String s : BalanceCISList)
								{
									if(s.split("\\|")[0].split(";")[2].equals(VBT_ID))
									{
										BalanceValue = s.split("\\|")[0].split(";")[3];
										break;
									}
								}
								if(TargetOffer.split("\\|")[1].split(";")[0].startsWith("Renewal"))
								{
									CISRenewalList.add(PopulateCISRenewal(TargetOffer.split("\\|")[1].split(";")[0], TargetOffer.split("\\|")[0].split(";")[2], BalanceValue, "",StartDate,ExpiryDate,"", TargetOffer.split("\\|")[1].split(";")[5]));
									CISnwpcList.add("CISnwpcCounter"+ "|" +msisdn+ "|" +productID+ "|" +"RSPreExpiryNotif" + "|" +ExpiryDate+ "|" +"-1"+ "|" +Targetlanguage+ "|" +LoadSubscriberMapping.CommonConfigMap.get("migration_date")+ "|" +(msisdn.charAt(msisdn.length()-1) + "|Yes" ));						
								}
								else if(TargetOffer.split("\\|")[1].split(";")[0].startsWith("OnceOff"))
								{
									CISOnceOffList.add(PopulateCISOnce(TargetOffer.split("\\|")[1].split(";")[0], TargetOffer.split("\\|")[0].split(";")[2], BalanceValue,"",StartDate, ExpiryDate, TargetOffer.split("\\|")[1].split(";")[5]));
								}
							}
							else
							{
								for(String item : BalanceCISList)
								{
									if(item.split("\\|")[1].split(";")[0].length() != 0)
									{
										String CISType = item.split("\\|")[1].split(";")[0].split("-")[0];
										String productID = item.split("\\|")[1].split(";")[0].split("-")[1];
										String VBT_ID = item.split("\\|")[1].split(";")[0].split("-")[2];
										
										//[V;MercuryState;3011;6|Renewal-1523-3011-NW;;2019-06-04 21:39:25;;2019-06-04 21:39:25;-1578984996]
										String ExpiryDate = item.split("\\|")[1].split(";")[2];
										
										//BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + CISReference + ";" + startFlag + ";" + expiryFlag + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
										//String cisReference, String Balance_ID, String Balance_Value,String Offer_ID, String StartDate, String ExpiryDate)
										
										if(item.split("\\|")[1].split(";")[0].startsWith("Renewal"))
										{
											CISRenewalList.add(PopulateCISRenewal(item.split("\\|")[1].split(";")[0],item.split("\\|")[0].split(";")[2], item.split("\\|")[0].split(";")[3],"",item.split("\\|")[1].split(";")[1],item.split("\\|")[1].split(";")[2],"",item.split("\\|")[1].split(";")[5]));
											CISnwpcList.add("CISnwpcCounter"+ "|" +msisdn+ "|" +productID+ "|" +"RSPreExpiryNotif" + "|" +ExpiryDate+ "|" +"-1"+ "|" +Targetlanguage+ "|" +LoadSubscriberMapping.CommonConfigMap.get("migration_date")+ "|" +(msisdn.charAt(msisdn.length()-1) + "|Yes" ));						
										}
										else if(item.split("\\|")[1].split(";")[0].startsWith("OnceOff"))
										{
											CISOnceOffList.add(PopulateCISOnce(item.split("\\|")[1].split(";")[0], item.split("\\|")[0].split(";")[2], item.split("\\|")[0].split(";")[3],"",item.split("\\|")[1].split(";")[1], item.split("\\|")[1].split(";")[2], item.split("\\|")[1].split(";")[5]));
										}
										
									}
								}
							}
						}
					}
					
					if(OutputDetails.containsKey("BT_ID"))
						CompletedBT_ID.addAll(OutputDetails.get("BT_ID"));										
				}
				else
				{
					CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				}
			}
		}	
	}*/
	
	public Map<String,Map<String,List<String>>> PopulateGGroup(String inputBalance_ID, String inputGroupName , Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub
		String FinalGroupName ="";
		
		List<String> AllAvailableGroup = new ArrayList<>();
		List<String> GroupBalanceCIS = new ArrayList<>();
		List<String> FinalBalanceCIS = new ArrayList<>();
		Map<String,Map<String,List<String>>> GGroupOutput = new HashMap<>();
		boolean GGroupFormed = false;
		Set<String> GBT_ID = new HashSet<>();
		
		/*for(Set<String> valueList : LoadSubscriberMapping.GGroupBalanceGroupingMap.values()) {					
			if(valueList.contains(inputBalance_ID)){
				AllAvailableGroup.add(commonfunction.getGraceGroupKey(LoadSubscriberMapping.GGroupBalanceGroupingMap, inputBalance_ID));	
			}
		}*/
		
		AllAvailableGroup.addAll(commonfunction.getASpecialGroupKey(LoadSubscriberMapping.GGroupBalanceGroupingMap, inputBalance_ID));
		
		for(String GroupName : AllAvailableGroup)
		{
			Set<String> GroupElements = new HashSet<>();
			if(GroupName.startsWith("G-"))
			{
				GroupBalanceCIS.clear();
				GBT_ID.clear();
				GroupElements.clear();
				Set<String> GroupBTItems = LoadSubscriberMapping.GGroupBalanceGroupingMap.get(GroupName);
				for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo TempbalanceInput : SortedBalanceInput)
				{
					String TempBalance_ID = TempbalanceInput.getBALANCETYPE();
					String TempBalance_Value = TempbalanceInput.getBEBUCKETVALUE();
					String TempStart_Date = TempbalanceInput.getBEBUCKETSTARTDATE();
					String TempExpiry_Date = TempbalanceInput.getBEEXPIRY();
					//Check if BT is valid for migration for group element
					
					
					if(CompletedBT_ID.contains(TempbalanceInput.getBEBUCKETID()))
						continue;
					
					if(!TempExpiry_Date.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(TempExpiry_Date) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
					{													
						//onlyLog.add("INC4001:Balance_Type expired:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value +  ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + TempbalanceInput.getBEEXPIRY() + ":ACTION=Logging");
						CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
						continue;
					}
					
					if(GroupBTItems.contains(TempBalance_ID))
					{
						if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName) != null)
						{							
							String CISReference = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getCISReference();
							String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getSymbols();
							String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getBTValue();
							String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getBTTYPE();
							
							if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
							{	
								if(!GroupElements.contains(TempBalance_ID))
								{
									GroupElements.add(TempBalance_ID);
									GBT_ID.add(TempbalanceInput.getBEBUCKETID());
									GroupBalanceCIS.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + CISReference + ";" + TempStart_Date + ";" + TempExpiry_Date + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
								}
								continue;
							}
							else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
							{
								if(!GroupElements.contains(TempBalance_ID))
								{
									GroupElements.add(TempBalance_ID);
									GBT_ID.add(TempbalanceInput.getBEBUCKETID());
									GroupBalanceCIS.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + CISReference + ";" + TempStart_Date + ";" + TempExpiry_Date + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
								}
								continue;
							}
							else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
							{
								if(!GroupElements.contains(TempBalance_ID))
								{
									GroupElements.add(TempBalance_ID);
									GBT_ID.add(TempbalanceInput.getBEBUCKETID());
									GroupBalanceCIS.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + CISReference + ";" + TempStart_Date + ";" + TempExpiry_Date + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
								}
								continue;
							}
							else if(Symbol.equals("or"))
							{
								//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
								String[] values = BT_Value.split("#");											
								if(Arrays.stream(values).anyMatch(TempBalance_Value::equals))
								{
									if(!GroupElements.contains(TempBalance_ID))
									{
										GroupElements.add(TempBalance_ID);
										GBT_ID.add(TempbalanceInput.getBEBUCKETID());
										GroupBalanceCIS.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + CISReference + ";" + TempStart_Date + ";" + TempExpiry_Date + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
									}
									continue;
								}
								else
								{
									if(BT_Type.toUpperCase().equals("P"))
									{								
										if(Integer.parseInt(TempBalance_Value) < 3)
											onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
										else
											onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
									
										CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
									}
									else
									{
										if(LoadSubscriberMapping.GraceFullBalanceGroupingMap.containsKey(TempBalance_ID))
										{
											Set<String> BTValues = new HashSet<String>();
											BTValues.addAll(LoadSubscriberMapping.GraceFullBalanceGroupingMap.get(TempBalance_ID));
											if(!BTValues.contains(TempBalance_Value))
											{
												if(BT_Type.toUpperCase().equals("P")  && Integer.parseInt(TempBalance_Value) < 3)
													onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
												else
													onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
												CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
											}
										}
									}
								}
							}
							else
							{
								if(BT_Type.toUpperCase().equals("P"))
								{								
									if(Integer.parseInt(TempBalance_Value) < 3)
										onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
									else
										onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
								
									CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
								}
								else
								{
									if(LoadSubscriberMapping.GraceFullBalanceGroupingMap.containsKey(TempBalance_ID))
									{
										Set<String> BTValues = new HashSet<String>();
										BTValues.addAll(LoadSubscriberMapping.GraceFullBalanceGroupingMap.get(TempBalance_ID));
										if(!BTValues.contains(TempBalance_Value))
										{
											if(BT_Type.toUpperCase().equals("P")  && Integer.parseInt(TempBalance_Value) < 3)
												onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
											else
												onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
										}
									}
								}
							}
						}	
					}
				}
				if(GroupBalanceCIS.size() == GroupBTItems.size())
				{
					GGroupFormed = true;
					break;					
				}				
			}
		}
		if(GGroupFormed)
		{
			Map<String,List<String>> temp = new HashMap<>();
			List<String> templist = new ArrayList<>( GBT_ID);
			temp.put("BT_ID", templist);
			temp.put("CIS_Value", GroupBalanceCIS);
			CompletedBT_ID.addAll(GBT_ID);
			GGroupOutput.put("GOutput", temp);
			
			return GGroupOutput;
		}
		else
		{
			AllAvailableGroup.clear();
			boolean GSGroupFormed = false;
			AllAvailableGroup.addAll(commonfunction.getASpecialGroupKey(LoadSubscriberMapping.GSGroupBalanceGroupingMap, inputBalance_ID));
			
			for(String GroupName : AllAvailableGroup)
			{
				Set<String> GroupElements = new HashSet<>();
				if(GroupName.startsWith("G-S"))
				{
					GroupBalanceCIS.clear();
					GBT_ID.clear();
					GroupElements.clear();
					Set<String> GroupBTItems = LoadSubscriberMapping.GSGroupBalanceGroupingMap.get(GroupName);
					for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo TempbalanceInput : SortedBalanceInput)
					{
						String TempBalance_ID = TempbalanceInput.getBALANCETYPE();
						String TempBalance_Value = TempbalanceInput.getBEBUCKETVALUE();
						String TempStart_Date = TempbalanceInput.getBEBUCKETSTARTDATE();
						String TempExpiry_Date = TempbalanceInput.getBEEXPIRY();
						
						if(CompletedBT_ID.contains(TempbalanceInput.getBEBUCKETID()))
							continue;
						
						if(GroupBTItems.contains(TempBalance_ID))
						{
							if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName) != null)
							{							
								String CISReference = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getCISReference();
								String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getSymbols();
								String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getBTValue();
								String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getBTTYPE();
								
								if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
								{	
									if(!GroupElements.contains(TempBalance_ID))
									{
										GroupElements.add(TempBalance_ID);
										GBT_ID.add(TempbalanceInput.getBEBUCKETID());
										GroupBalanceCIS.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + CISReference + ";" + TempStart_Date + ";" + TempExpiry_Date + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
									}
									continue;
								}
								else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
								{
									if(!GroupElements.contains(TempBalance_ID))
									{
										GroupElements.add(TempBalance_ID);
										GBT_ID.add(TempbalanceInput.getBEBUCKETID());
										GroupBalanceCIS.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + CISReference + ";" + TempStart_Date + ";" + TempExpiry_Date + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
									}
									continue;
								}
								else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
								{
									if(!GroupElements.contains(TempBalance_ID))
									{
										GroupElements.add(TempBalance_ID);
										GBT_ID.add(TempbalanceInput.getBEBUCKETID());
										GroupBalanceCIS.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + CISReference + ";" + TempStart_Date + ";" + TempExpiry_Date + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
									}
									continue;
								}
								else if(Symbol.equals("or"))
								{
									//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
									String[] values = BT_Value.split("#");											
									if(Arrays.stream(values).anyMatch(TempBalance_Value::equals))
									{
										if(!GroupElements.contains(TempBalance_ID))
										{
											GroupElements.add(TempBalance_ID);
											GBT_ID.add(TempbalanceInput.getBEBUCKETID());
											GroupBalanceCIS.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + CISReference + ";" + TempStart_Date + ";" + TempExpiry_Date + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
										}
										continue;
									}																					
								}
								else
								{
									if(BT_Type.toUpperCase().equals("P"))
									{								
										if(Integer.parseInt(TempBalance_Value) < 3)
											onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
										else
											onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
									
										CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
									}
									else
									{
										if(LoadSubscriberMapping.GraceFullBalanceGroupingMap.containsKey(TempBalance_ID))
										{
											Set<String> BTValues = new HashSet<String>();
											BTValues.addAll(LoadSubscriberMapping.GraceFullBalanceGroupingMap.get(TempBalance_ID));
											if(!BTValues.contains(TempBalance_Value))
											{
												if(BT_Type.toUpperCase().equals("P")  && Integer.parseInt(TempBalance_Value) < 3)
													onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
												else
													onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
												CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
											}
										}
									}
								}
									
							}	
						}
					}
					if(GroupBalanceCIS.size() == GroupBTItems.size())
					{
						GSGroupFormed = true;
						break;					
					}				
				}
			}
			if(GSGroupFormed)
			{
				Map<String,List<String>> temp = new HashMap<>();
				List<String> templist = new ArrayList<>( GBT_ID);
				temp.put("BT_ID", templist);
				temp.put("CIS_Value", GroupBalanceCIS);
				
				GGroupOutput.put("GOutput", temp);
				return GGroupOutput;
			}
			else if(GroupBalanceCIS.size() > 0)
			{
				for(String item : GroupBalanceCIS)
				{
					if(LoadSubscriberMapping.GraceFullBalanceGroupingMap.containsKey(item.split("\\|")[0].split(";")[2]))
					{
						Set<String> BTValues = new HashSet<String>();
						BTValues.addAll(LoadSubscriberMapping.GraceFullBalanceGroupingMap.get(item.split("\\|")[0].split(";")[2]));
						if(!BTValues.contains(item.split("\\|")[0].split(";")[3]))
						{
							if(item.split("\\|")[0].split(";")[0].toUpperCase().equals("P")  && Integer.parseInt(item.split("\\|")[0].split(";")[3]) < 3)
								onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + item.split("\\|")[0].split(";")[2] + ":BE_BUCKET_VALUE=" + item.split("\\|")[0].split(";")[3] + ":BE_BUCKET_ID=" + item.split("\\|")[1].split(";")[5] +":ACTION=Logging");
							else
								onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + item.split("\\|")[0].split(";")[2] + ":BE_BUCKET_VALUE=" + item.split("\\|")[0].split(";")[3] + ":BE_BUCKET_ID=" + item.split("\\|")[1].split(";")[5] +":ACTION=Logging");
						}
					}
					else
					{
						if(item.split("\\|")[0].split(";")[0].toUpperCase().equals("P")  && Integer.parseInt(item.split("\\|")[0].split(";")[3]) < 3)
							onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + item.split("\\|")[0].split(";")[2] + ":BE_BUCKET_VALUE=" + item.split("\\|")[0].split(";")[3] + ":BE_BUCKET_ID=" + item.split("\\|")[1].split(";")[5] +":ACTION=Logging");
						else
							onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + item.split("\\|")[0].split(";")[2] + ":BE_BUCKET_VALUE=" + item.split("\\|")[0].split(";")[3] + ":BE_BUCKET_ID=" + item.split("\\|")[1].split(";")[5] +":ACTION=Logging");
					}
					//[M;SplRecSocDeactServRetry;3036;3|Renewal-1447-3036-NW;;1970-01-01 04:00:00;;1970-01-01 04:00:00;1617229292]
					//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + item.split("\\|")[0].split(";")[2] + ":BE_BUCKET_VALUE=" + item.split("\\|")[0].split(";")[3] + ":BE_BUCKET_ID=" + item.split("\\|")[1].split(";")[5] +":ACTION=Logging");
					Map<String,List<String>> temp = new HashMap<>();
					List<String> templist = new ArrayList<>( GBT_ID);
					temp.put("BT_ID", templist);
					temp.put("CIS_Value", new ArrayList<>());
					
					GGroupOutput.put("GOutput", temp);
					return GGroupOutput;
				}
			}
		}		
		return GGroupOutput;
	}
	
	private void CISGenerationFromProfileTags()
	{
		
		if(subscriber.getProfiledumpInfoList().size() == 0)
			return ;
		
		for(String itr : LoadSubscriberMapping.Profile_Tags_Mapping.keySet())
		{
			PROFILETAGINFO profileMappingValue = LoadSubscriberMapping.Profile_Tags_Mapping.get(itr);
			String Symbol = profileMappingValue.getSymbols();
			String TargetValue = profileMappingValue.getProfileTagValue();
			String IgnoreFlag =  profileMappingValue.getIgnoreFlag();
			String CIS_Reference = profileMappingValue.getCISReference();
			String Offer_ID = profileMappingValue.getOfferId();
			if(IgnoreFlag.equals("N"))
			{
				/*if(itr.equals("IDD2Act") || itr.equals("PAYGMet")  || itr.equals("Absher") || itr.equals("Bespoke") || itr.equals("EmiratiPlan") 
					|| itr.equals("entBsnssCrclActv") || itr.equals("IDDCutRateAct") || itr.equals("Prepaid")
					|| itr.equals("TopXCountr1") || itr.equals("TopXCountr2") || itr.equals("TopXCountr3") || itr.equals("TopXCountr4") 
					|| itr.equals("TopXCountr5") || itr.equals("TP_Social_Deact_Conf") || itr.equals("Plan"))*/
				if(LoadSubscriberMapping.ProfileTagName.contains(itr))
				{
					String Profile_Value = profileTag.GetProfileTagValue(itr);
					if(Profile_Value.isEmpty() || CIS_Reference.isEmpty())
						continue;
					
					
					if(profileMappingValue.getSubState().isEmpty())
					{						
						if(Symbol.equals("="))
						{
							if(TargetValue.equals(Profile_Value))
							{
								if(CIS_Reference.startsWith("OnceOff"))
								{
									CISOnceOffList.add(PopulateCISOnce(CIS_Reference,itr,Profile_Value,Offer_ID,"", "","PROFILE"));
								}
								if(CIS_Reference.startsWith("Renewal"))
								{
									CISRenewalList.add(PopulateCISRenewal(CIS_Reference,itr,Profile_Value,Offer_ID,"", "","","PROFILE"));
								}
							}
							else if(Offer_ID.isEmpty() && !Profile_Value.isEmpty())
							{
								//this logging is needed because offer is not created for these PT so need to log in CIS
								onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE="+ Profile_Value + ":ACTION=Logging");
							}
						}
						
					}
					else if(profileMappingValue.getSubState().equals(Profile_Value))
					{
						if(Symbol.equals("="))
						{
							if(TargetValue.equals(Profile_Value))
							{
								if(CIS_Reference.startsWith("OnceOff"))
								{
									CISOnceOffList.add(PopulateCISOnce(CIS_Reference,itr,Profile_Value,Offer_ID,"", "","PROFILE"));
								}
								if(CIS_Reference.startsWith("Renewal"))
								{
									CISRenewalList.add(PopulateCISRenewal(CIS_Reference,itr,Profile_Value,Offer_ID,"", "","","PROFILE"));
								}
							}
						}
					}
				}
			}			
		}
		
		for(String itr : LoadSubscriberMapping.Profile_Tags_MappingWithGroup.keySet())
		{
			PROFILETAGINFO profileMappingValue = LoadSubscriberMapping.Profile_Tags_MappingWithGroup.get(itr);
			String Symbol = profileMappingValue.getSymbols();
			String TargetName = profileMappingValue.getProfileTagName();
			String TargetValue = profileMappingValue.getProfileTagValue();
			String IgnoreFlag =  profileMappingValue.getIgnoreFlag();
			String GroupName = itr.split(",")[1];
			String CIS_Reference = profileMappingValue.getCISReference();
			String Offer_ID = profileMappingValue.getOfferId();
						
			if(IgnoreFlag.equals("N"))
			{
								
				//***************logic for Dummy1, dummy2 and Dummy3
				String Profile_Value = profileTag.GetProfileTagValue(itr.split(",")[0]);
				if(Profile_Value.isEmpty() || CIS_Reference.isEmpty())
					continue;
				if(TargetName.equals("CVM"))					
				{
					if(Symbol.equals("="))
					{	
						if(Integer.parseInt(TargetValue) == Integer.parseInt(Profile_Value))
						{
							if(CIS_Reference.startsWith("OnceOff"))
							{
								CISOnceOffList.add(PopulateCISOnce(CIS_Reference,itr,Profile_Value,Offer_ID,"", "","PROFILE"));
								continue;
							}
						}	
					}
					continue;
				}
					
				if(TargetName.equals("MBBGraceAct") || TargetName.equals("DataGraceAct"))					
				{
					//if(profileMappingValue.getSubState().equals(Profile_Value))
					{
						if(Symbol.equals("="))
						{	
							if(TargetValue.equals(Profile_Value))
							{
								if(CIS_Reference.startsWith("OnceOff"))
								{
									CISRenewalList.add(PopulateCISRenewal(CIS_Reference,itr,Profile_Value,Offer_ID,"", "","Grace","PROFILE"));
									continue;
								}
							}	
						}
					}
					continue;
				}
				
				if(TargetName.equals("BlckBrryBundle"))					
				{
					if(profileMappingValue.getSubState().equals(Profile_Value))
					{
						if(Symbol.equals("="))
						{	
							if(TargetValue.equals(Profile_Value))
							{
								if(CIS_Reference.startsWith("Parking"))
								{
									CISParkingList.add(PopulateCISParking(CIS_Reference,itr,Profile_Value,Offer_ID,"", "","PROFILE"));
									continue;
								}
							}	
						}
					}
					continue;
				}
				
				if(TargetName.equals("NewPPBundle") && Profile_Value.length() !=0)					
				{
					if(profileMappingValue.getSubState().equals(subscriber.getSubscriberInfoSERVICESTATE()))
					{
						String[] values = {"Monthly1","Monthly2","Monthly3","Monthly4","Monthly5","Monthly6","Monthly7","Monthly8","Monthly9","Monthly10","Daily1"};
						if(Symbol.equals("="))
						{
							//if(Arrays.stream(values).anyMatch(Profile_Value::equals))
							if( TargetValue.equals(Profile_Value))
							{
								if(CIS_Reference.startsWith("Parking"))
								{
									CISParkingList.add(PopulateCISParking(CIS_Reference,itr,Profile_Value,Offer_ID,"", "","PROFILE"));
									continue;
								}
							}
						}
					}					
					continue;
				}
				
				if(TargetName.equals("ManRenDateLess1Y"))
				{
					String ManRenDate = "";
					if(Profile_Value.length() == 14)
					{
						ManRenDate = Profile_Value.substring(0,4) + "-" + Profile_Value.substring(4,6) + "-" + Profile_Value.substring(6,8) + " " + Profile_Value.substring(8,10) + ":" + Profile_Value.substring(10,12) + ":" + Profile_Value.substring(12,14);
					
						if(CommonUtilities.convertDateToEpoch(ManRenDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
						{
							//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + "BstrVceIntNumTree" + ":PROFILE_TAG_VALUE=" + ManRenDate +":ACTION=Logging");
						}
						else
						{
							if(profileMappingValue.getRatePlanOperator().equals("="))
							{
								if(subscriber.getSubscriberInfoCCSACCTTYPEID().equals(profileMappingValue.getRatePlanID()))
								{
									if(CIS_Reference.startsWith("OnceOff"))
									{
										CISOnceOffList.add(PopulateCISOnce(CIS_Reference,itr,Profile_Value,Offer_ID,"", "","PROFILE"));
										continue;
									}
								}
							}
							if(profileMappingValue.getRatePlanOperator().equals("or"))
							{
								List<String> RatePlanID = Arrays.asList(profileMappingValue.getRatePlanID().split("#"));
								
								if(RatePlanID.contains(subscriber.getSubscriberInfoCCSACCTTYPEID()))
								{
									if(CIS_Reference.startsWith("OnceOff"))
									{
										CISOnceOffList.add(PopulateCISOnce(CIS_Reference,itr,Profile_Value,Offer_ID,"", "","PROFILE"));
										continue;
									}
								}								
							}					
						}
					}
					continue;
				}
				
				if(TargetName.equals("BstrVNNRecur"))
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
											//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
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
										//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
										break;
									}
								}
								if(profileTagSymbol.equals(">"))
								{
									String additionalProfileValue = profileTag.GetProfileTagValue(profileTagName);
									if(!additionalProfileValue.isEmpty())
									{
										if(Double.parseDouble(additionalProfileValue) > Double.parseDouble(profileTagValue) )
										{
											ValidPTCheck.add(profileTagName);
										}
										else
										{
											//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
											break;
										}
									}
								}	
								if(profileTagSymbol.equals("!="))
								{
									String additionalProfileValue = profileTag.GetProfileTagValue(profileTagName);
									if(!additionalProfileValue.isEmpty())
									{
										ValidPTCheck.add(profileTagName);
									}
									else
									{
										//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
										break;
									}
								}
							}
							if(PT_Values.size() == ValidPTCheck.size())
							{
								if(CIS_Reference.startsWith("OnceOff"))
								{
									CISOnceOffList.add(PopulateCISOnce(CIS_Reference,itr,Profile_Value,Offer_ID,"", "","PROFILE"));
								}
								if(CIS_Reference.startsWith("Renewal"))
								{
									CISRenewalList.add(PopulateCISRenewal(CIS_Reference,itr,Profile_Value,Offer_ID,"", "","","PROFILE"));
								}
							}
						}
					}	
					continue;
				}
				
				if(TargetName.equals("BstrVINRecur"))
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
											//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
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
										//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
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
											//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
											break;
										}
									}
								}
								if(profileTagSymbol.equals("!="))
								{
									String additionalProfileValue = profileTag.GetProfileTagValue(profileTagName);
									if(!additionalProfileValue.isEmpty())
									{
										ValidPTCheck.add(profileTagName);
									}
									else
									{
										//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
										break;
									}
								}
							}
							if(PT_Values.size() == ValidPTCheck.size())
							{
								if(CIS_Reference.startsWith("OnceOff"))
								{
									CISOnceOffList.add(PopulateCISOnce(CIS_Reference,itr,Profile_Value,Offer_ID,"", "","PROFILE"));
								}
								if(CIS_Reference.startsWith("Renewal"))
								{
									CISRenewalList.add(PopulateCISRenewal(CIS_Reference,itr,Profile_Value,Offer_ID,"", "", "","PROFILE"));
								}
							}
						}
					}
					continue;
				}
				if(TargetName.equals("BlckBrryAct"))
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
								if(profileTagSymbol.equals("="))
								{
									String additionalProfileValue = profileTag.GetProfileTagValue(profileTagName);
									if(profileTagValue.equals(additionalProfileValue))
									{
										ValidPTCheck.add(profileTagName);
									}
									else if(Offer_ID.isEmpty() && !Profile_Value.isEmpty())
									{
										//this logging is needed because offer is not created for these PT so need to log in CIS
										onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
										break;
									}
								}
							}
							if(PT_Values.size() == ValidPTCheck.size())
							{
								if(CIS_Reference.startsWith("OnceOff"))
								{
									CISOnceOffList.add(PopulateCISOnce(CIS_Reference,itr,Profile_Value,Offer_ID,"", "","PROFILE"));
								}
								if(CIS_Reference.startsWith("Renewal"))
								{
									CISRenewalList.add(PopulateCISRenewal(CIS_Reference,itr,Profile_Value,Offer_ID,"", "", "","PROFILE"));
								}
							}
						}
						else if(Offer_ID.isEmpty() && !Profile_Value.isEmpty())
						{
							//this logging is needed because offer is not created for these PT so need to log in CIS
							onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE=" + Profile_Value +":ACTION=Logging");
							break;
						}
					}
					continue;
				}
			}
			else
			{
				//log for ignore
			}
		}
	}
	
	public Map<String,List<String>> ComputeAMGroup(Map<String, String> AMBalanceBT, Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub
	
		String FinalGroupName ="";
		String ComputedGroupName = "";
		boolean BestMatchFound = false;
		Set<String> A_currentGroup = new HashSet<>();
		
		Set<Integer> UniqueAMBT = new TreeSet<Integer>();
		
		for(Entry<String,String> item:  AMBalanceBT.entrySet())
		{
			UniqueAMBT.add(Integer.parseInt(item.getValue().split("\\|")[0].trim()));
		}
		// find the AM group	 
		
		
		Set<String> AMGroupItems = new HashSet<>();
		AMGroupItems.addAll(commonfunction.getAMGroupKey(LoadSubscriberMapping.BalanceOnlyAMGroupMap,UniqueAMBT));
		
		for(String individualGroup : AMGroupItems)
		{
			int i = 0;
			A_currentGroup.clear();
			for(Map.Entry<String, String> entry : AMBalanceBT.entrySet())
			{
				String BT_ID = entry.getValue().split("\\|")[0];
				String BT_BALANCE = entry.getValue().split("\\|")[1];
				
				if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + individualGroup) != null)
				{							
					String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + individualGroup).getSymbols();
					String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + individualGroup).getBTValue();
					
					if(Symbol.equals(">=") && Integer.parseInt(BT_BALANCE) >= Integer.parseInt(BT_Value))
					{
						FinalGroupName = individualGroup;
						A_currentGroup.add(BT_ID);
						continue;
					}
					else if(Symbol.equals(">") && Integer.parseInt(BT_BALANCE) > Integer.parseInt(BT_Value))
					{
						FinalGroupName = individualGroup;
						A_currentGroup.add(BT_ID);
						continue;
					}
					else if(Symbol.equals("=") && Integer.parseInt(BT_BALANCE) == Integer.parseInt(BT_Value))
					{
						FinalGroupName = individualGroup;
						A_currentGroup.add(BT_ID);
						continue;
					}
					else if(Symbol.equals("or"))
					{
						//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
						String[] values = BT_Value.split("#");											
						if(Arrays.stream(values).anyMatch(BT_BALANCE::equals))
						{
							FinalGroupName = individualGroup;
							A_currentGroup.add(BT_ID);
							continue;
						}																					
					}								
				}						
			}
			if(A_currentGroup.size() == 2)
			{
				BestMatchFound = true;
				ComputedGroupName = FinalGroupName; 
				break;
			}
		}
		
		/*if(!BestMatchFound && BestMatch.size() > 1)
			FinalGroupName = Collections.max(BestMatch.entrySet(), (entry1, entry2) -> entry1.getValue().size() - entry2.getValue().size()).getKey();*/
		
		List<String> GroupBalanceOffer = new ArrayList<>();
		Map<String,List<String>> AMGroupOfferMap = new HashMap<>();
		Set<String> AMCompletedBT = new HashSet<>();
		if(ComputedGroupName.length() != 0)
		{	
			for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
			{
				String TempBalance_ID = balanceInput.getBALANCETYPE();
				if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName) != null)
				{
					if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
						continue;
				
					if(AMBalanceBT.containsKey(balanceInput.getBEBUCKETID()))
					{
						
						String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getOfferID();
						String CIS_Reference = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getCISReference();
						String BT_TYPE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getBTTYPE();
						
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						AMCompletedBT.add(TempBalance_ID);
						GroupBalanceOffer.add(BT_TYPE + ";" + CIS_Reference + ";" + TempBalance_ID + ";" + balanceInput.getBEBUCKETVALUE() +"|" + Offer_ID + ";" + balanceInput.getBEBUCKETSTARTDATE() + ";" + balanceInput.getBEEXPIRY()  + ";" + balanceInput.getBEBUCKETID());
						if(GroupBalanceOffer.size() == 2)
							break;
					}					
				}
			}	
			List<String> strings2 = AMCompletedBT.stream().map(Object::toString)
	                .collect(Collectors.toList());
			String AMBTValue2 = String.join(",", strings2);
			AMGroupOfferMap.put(FinalGroupName + "|" + AMBTValue2, GroupBalanceOffer);
		}
		
		return AMGroupOfferMap;
	}
	
	public Set<String>  ComputeASpecialGroup(String inputBalance_ID, Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub Map<String,List<String>>
		//Date currDate = new Date();
		//SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		List<String> ASGroupName = new ArrayList<>();		
		List<String> GroupBalanceOffer = new ArrayList<>();
		Set<String> ASBT_ID = new HashSet<>();
		boolean ASGroupFormed = false;
		Map<String,List<String>> tempGroupBalanceOffer = new HashMap<>();
		
		ASGroupName = (commonfunction.getASpecialGroupKey(LoadSubscriberMapping.BalanceOnlySpecialASGroupMap,inputBalance_ID));

		//CheckIf A-S-1 is present in the input
		//Map<String,Map<String,List<String>>> ASGroupOfferMap = new HashMap<>();
		CompletedBT_ID.addAll(CheckifA_S_1Present(CompletedBT_ID));
		
		for(String GroupName : ASGroupName)
		{
			Set<String> GroupElements = new HashSet<>();
			if(!GroupName.startsWith("A-S"))
			{
				GroupBalanceOffer.clear();
				ASBT_ID.clear();
				Set<String> GroupBTItems = LoadSubscriberMapping.BalanceOnlySpecialASGroupMap.get(GroupName);
				for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo TempbalanceInput : SortedBalanceInput)
				{
					String TempBalance_ID = TempbalanceInput.getBALANCETYPE();
					String TempBalance_Value = TempbalanceInput.getBEBUCKETVALUE();
					if(CompletedBT_ID.contains(TempbalanceInput.getBEBUCKETID()))
						continue;
					
					if(GroupBTItems.contains(TempBalance_ID))
					{
						if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName) != null)
						{							
							String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getOfferID();
							String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getSymbols();
							String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getBTValue();
							String CIS_Reference = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getCISReference();
							String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getBTTYPE();
							
							if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
							{								
								if(!GroupElements.contains(TempBalance_ID))
								{
									ASBT_ID.add(TempbalanceInput.getBEBUCKETID());
									GroupElements.add(TempBalance_ID);
									GroupBalanceOffer.add(BT_Type + ";" + CIS_Reference + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + Offer_ID + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
									continue;
								}
							}
							else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
							{								
								if(!GroupElements.contains(TempBalance_ID))
								{
									ASBT_ID.add(TempbalanceInput.getBEBUCKETID());
									GroupElements.add(TempBalance_ID);
									GroupBalanceOffer.add(BT_Type + ";" + CIS_Reference + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + Offer_ID + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
									continue;
								}
							}
							else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
							{
								if(!GroupElements.contains(TempBalance_ID))
								{
									ASBT_ID.add(TempbalanceInput.getBEBUCKETID());
									GroupElements.add(TempBalance_ID);
									GroupBalanceOffer.add(BT_Type + ";" + CIS_Reference + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + Offer_ID + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
									continue;
								}
							}
							else if(Symbol.equals("or"))
							{
								//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
								String[] values = BT_Value.split("#");											
								if(Arrays.stream(values).anyMatch(TempBalance_Value::equals))
								{
									if(!GroupElements.contains(TempBalance_ID))
									{
										ASBT_ID.add(TempbalanceInput.getBEBUCKETID());
										GroupElements.add(TempBalance_ID);
										GroupBalanceOffer.add(BT_Type + ";" + CIS_Reference + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + Offer_ID + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + TempbalanceInput.getBEBUCKETID());
										continue;
									}
								}
							}
						}
					}
				}
				if(GroupBalanceOffer.size() == GroupBTItems.size())
				{
					ASGroupFormed = true;
					break;					
				}
				else
				{
					List<String> temp = new ArrayList<>( GroupBalanceOffer);
					tempGroupBalanceOffer.put(GroupName, temp);
				}
			}
		}
		if(ASGroupFormed)
		{
			String TargetValue = GroupBalanceOffer.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[1];
			String SourceValue = GroupBalanceOffer.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[0];
			String CIS_Reference = SourceValue.split(";")[1];
			if(SourceValue.split(";")[0].length() != 0)
			{
				if(CIS_Reference.startsWith("Renewal")  && CommonUtilities.convertDateToEpoch(TargetValue.split(";")[2]) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
				{
					CISRenewalList.add(PopulateCISRenewal(CIS_Reference,SourceValue.split(";")[2],SourceValue.split(";")[3],TargetValue.split(";")[0],TargetValue.split(";")[1], TargetValue.split(";")[2],"", TargetValue.split(";")[3]));
				}
				if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(TargetValue.split(";")[2]) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
				{
					CISOnceOffList.add(PopulateCISOnce(CIS_Reference,SourceValue.split(";")[2],SourceValue.split(";")[3],TargetValue.split(";")[0],TargetValue.split(";")[1], TargetValue.split(";")[2], TargetValue.split(";")[3]));
				}
			}			
		}
		return ASBT_ID;
	}
	
	public Set<String> CheckifA_S_1Present(Set<String> CompletedBT_ID)
	{
		//Date currDate = new Date();
		//SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		List<String> GroupBalanceOffer = new ArrayList<>();
		Set<String> ASBT_ID = new HashSet<>();
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{
			String TempBalance_ID = balanceInput.getBALANCETYPE();
			String TempBalance_Value = balanceInput.getBEBUCKETVALUE();
			
			if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
				continue;
			
			if(TempBalance_ID.equals("1035") && TempBalance_Value.equals("2"))
			{
				String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getOfferID();
				String CIS_Reference = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getCISReference();
				String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getBTTYPE();
				ASBT_ID.add(balanceInput.getBEBUCKETID());
				GroupBalanceOffer.add(BT_Type + ";" + CIS_Reference + ";" + TempBalance_ID + ";" + balanceInput.getBEBUCKETVALUE() +"|" + Offer_ID + ";" + balanceInput.getBEBUCKETSTARTDATE() + ";" + balanceInput.getBEEXPIRY()  + ";" + balanceInput.getBEBUCKETID()+ ";" + balanceInput.getBALANCETYPENAME());
				
			}
			if(TempBalance_ID.equals("74") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt("-999999"))
			{
				String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getOfferID();
				String CIS_Reference = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getCISReference();
				String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getBTTYPE();
				ASBT_ID.add(balanceInput.getBEBUCKETID());
				GroupBalanceOffer.add(BT_Type + ";" + CIS_Reference + ";" + TempBalance_ID + ";" + balanceInput.getBEBUCKETVALUE() +"|" + Offer_ID + ";" + balanceInput.getBEBUCKETSTARTDATE() + ";" + balanceInput.getBEEXPIRY()  + ";" + balanceInput.getBEBUCKETID() + ";" + balanceInput.getBALANCETYPENAME());
				
			}
		}
		
		if(GroupBalanceOffer.size() == 2)
		{
			if(GroupBalanceOffer.stream().filter(x->x.contains("Blackberry Bundle")).count() == 1 && GroupBalanceOffer.stream().filter(x->x.contains("Blackberry KB")).count() == 1)
			{
				for(String Str : GroupBalanceOffer)
				{
					//M;Blackberry Bundle;1035;2|6014;Timer;false;false;;1970-01-01 00:00:00;Yes;NULL;;752817472
					String SourceValue = Str.split("\\|")[0];
					String TargetValue = Str.split("\\|")[1];
					String CIS_Reference = Str.split("\\|")[0].split(";")[1];
					String Balance_ExpiryDate = "";
					if(TargetValue.split(";")[2].equals("1970-01-01 04:00:00"))
						Balance_ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
					if(CIS_Reference.startsWith("Renewal")  && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
					{
						CISRenewalList.add(PopulateCISRenewal(CIS_Reference,SourceValue.split(";")[2],SourceValue.split(";")[3],TargetValue.split(";")[0],TargetValue.split(";")[1], TargetValue.split(";")[2],"", TargetValue.split(";")[3]));
					}
					if(CIS_Reference.startsWith("OnceOff") && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) >= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
					{
						CISOnceOffList.add(PopulateCISOnce(CIS_Reference,SourceValue.split(";")[2],SourceValue.split(";")[3],TargetValue.split(";")[0],TargetValue.split(";")[1], TargetValue.split(";")[2], TargetValue.split(";")[3]));
					}
				}
			}
			else
			{
				ASBT_ID.clear();								
			}
		}
		else
		{
			if(GroupBalanceOffer.size() == 1 && GroupBalanceOffer.stream().filter(x->x.contains("Blackberry Bundle")).count() == 1)
			{
				
			}
			else
				ASBT_ID.clear();			
		}
		
		return ASBT_ID;
	}
	
	private String PopulateCISOnceOptOut(String cisReference, String Balance_ID, String Balance_Value, String Offer_ID, String StartDate, String ExpiryDate, String Bucket_ID) 
	{
		StringBuffer sb = new StringBuffer();
		String ProductID = cisReference.split("-")[1];
		String NwStatus = cisReference.split("-")[3];
		if(ExpiryDate.equals("1970-01-01 04:00:00") ||  ExpiryDate.length() == 0)	
			ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
		
		if(StartDate.equals("1970-01-01 04:00:00") ||  StartDate.length() == 0)	
			StartDate = LoadSubscriberMapping.CommonConfigMap.get("migration_date");
		
		String last_action_date = "";
		if(StartDate.length() != 0)
			last_action_date = StartDate;
		else
			last_action_date = LoadSubscriberMapping.CommonConfigMap.get("migration_date");
		
		sb.append("CISOnceOffCounter").append("|");
		sb.append(msisdn).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductId()).append("|");			
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("CIS_OnceOff_OPTIN_Status")).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPreNotificationCount()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPostNotificationCount()).append("|");
		sb.append(ExpiryDate).append("|");
		sb.append(StartDate).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductType()).append("|");
		sb.append("").append("|");			
		sb.append(last_action_date).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductDescription()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getIsPamProduct()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPaySrc()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getBenMsisdn()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getSendSms()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getSplitNo()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductCost()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPamId()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getEnableNotification()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getRenewalValue()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getSrcchannel()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getOfferID()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductCategory()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getBundleName()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductPurchaseType()).append("|");
		sb.append(Targetlanguage).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPreNotifStatus()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPostNotifStatus()).append("|"); 
		sb.append(msisdn.charAt(msisdn.length()-1)).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getDeprovStatus()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getRetryLimit()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getGiftedBy()).append("|"); 
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPriority()).append("|");
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPreviousStatus()).append("|"); 
		sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getCorrelationId()).append("|");
		//sb.append(LoadSubscriberMapping.CommonConfigMap.get("CIS_OnceOff_OPTIN_Network_Status")).append("|");
		if(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getNetworkStatus().isEmpty())
		{
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getNetworkStatus()).append("|");
		}
		else
		{
			sb.append("DeprovisionCompleted").append("|");
		}
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("migration_date")).append("|");
		sb.append("").append("|");
		
		return sb.toString();
	}
	
	private String PopulateCISOnce(String cisReference, String Balance_ID, String Balance_Value, String Offer_ID, String StartDate, String ExpiryDate, String Bucket_ID) {
		
		StringBuffer sb = new StringBuffer();
		String ProductID = cisReference.split("-")[1];
		String NwStatus = cisReference.split("-")[3];
		if(ExpiryDate.equals("1970-01-01 04:00:00") ||  ExpiryDate.length() == 0)	
			ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
		
		if(StartDate.equals("1970-01-01 04:00:00") ||  StartDate.length() == 0)	
			StartDate = LoadSubscriberMapping.CommonConfigMap.get("migration_date");
		
		if(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID) != null)
		{
			/*String SourceLanguage = "";
			String Targetlanguage = "";
			if(subscriber.getProfiledumpInfoList().size() == 1)
				SourceLanguage = subscriber.getProfiledumpInfoList().get(0).getLanguageID();
			
			if(LoadSubscriberMapping.LanguageMap.get(SourceLanguage) == null)
				Targetlanguage = LoadSubscriberMapping.CommonConfigMap.get("default_language");
			else
				Targetlanguage = LoadSubscriberMapping.LanguageMap.get(SourceLanguage);*/
			
			//Rule A
			String Status ="";
			String NetworkStatus = "";
			if(NwStatus.toUpperCase().equals("NW"))
			{
				if(LoadSubscriberMapping.CIS_NWPC_Mapping.containsKey(Balance_ID + "," + Balance_Value))
				{
					Status = LoadSubscriberMapping.CIS_NWPC_Mapping.get(Balance_ID + "," + Balance_Value).getStatus();
					NetworkStatus = LoadSubscriberMapping.CIS_NWPC_Mapping.get(Balance_ID + "," + Balance_Value).getNetworkStatus();
				}
				else
				{
					Status = LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getStatus();
					NetworkStatus = LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getNetworkStatus();
					if(!LoadSubscriberMapping.CIS_NWPC_BTID.contains(Balance_ID))
					{
						onlyLog.add("INC4007:BT mapped CIS NWPC Mapping failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + Bucket_ID +  ":BUNDLE_TYPE=" + "OnceOff" + ":PRODUCT_ID=" + ProductID +":ACTION=Logging");
						return "";
					}
					//CISonlyLog.add("INC1102:CIS NWPC Mapping failed:MSISDN=" + msisdn + ":BUNDLE_TYPE=" + "OnceOff" + ":PRODUCT_ID=" + ProductID + ":BT_ID=" + Balance_ID + ":BT_VALUE=" + Balance_Value + ":ACTION=Logging");
				}
			}
			else
			{				
				Status = LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getStatus();
				NetworkStatus = LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getNetworkStatus();
			}
			
			//Rule B
			
			if(StartDate.length() == 0)
				StartDate = LoadSubscriberMapping.CommonConfigMap.get("migration_date");
			
			
			//Rule E			
			String Billcycle = "";
			if(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getProductType().toUpperCase().equals("BILLCYCLE")
					|| LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getProductType().toUpperCase().equals("IN_EXT8"))
			{
				int expiryDay = CommonUtilities.convertDateToTimerOfferDate(ExpiryDate)[0];
				int startDay = CommonUtilities.convertDateToTimerOfferDate(StartDate)[0];
				Billcycle = String.valueOf(expiryDay - startDay);
			}
			else
			{
				Billcycle = LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getRenewalValue();
			}
			
			//CISONCEOFFINFO cisOnceInfo = LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).
			sb.append("CISOnceOffCounter").append("|");
			sb.append(msisdn).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getProductId()).append("|");
			sb.append(Status).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getPreNotificationCount()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getPostNotificationCount()).append("|");
			sb.append(ExpiryDate).append("|");
			sb.append(StartDate).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getProductType()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getServiceName()).append("|");
			sb.append(StartDate).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getProductDescription()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getIsPamProduct()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getPaySrc()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getBenMsisdn()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getSendSms()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getSplitNo()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getProductCost()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getPamId()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getEnableNotification()).append("|");
			//Commented in PA13_1
			//sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getRenewalValue()).append("|");
			sb.append(Billcycle).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getSrcchannel()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getOfferId()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getProductCategory()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getBundleName()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getProductPurchaseType()).append("|");
			sb.append(Targetlanguage).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getPreNotifStatus()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getPostNotifStatus()).append("|");
			sb.append(msisdn.charAt(msisdn.length()-1)).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getDeprovStatus()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getRetryLimit()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getGiftedBy()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getPriority()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getPreviousStatus()).append("|");
			sb.append(LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).getCorrelationId()).append("|");
			sb.append(NetworkStatus).append("|");
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("migration_date")).append("|");
			sb.append("");
			
			//this.CISOnceOffCounter++;
			CISnwpcList.add("CISnwpcCounter"+ "|" +msisdn+ "|" +ProductID+ "|" +"AdhocExpireNotificat" + "|" +ExpiryDate+ "|" +"-1"+ "|" +Targetlanguage+ "|" +LoadSubscriberMapping.CommonConfigMap.get("migration_date")+ "|" +(msisdn.charAt(msisdn.length()-1) + "|TRUE"));
		}
		else
		{
			if(Bucket_ID.equals("PROFILE"))
			{
				onlyLog.add("INC6005:BT mapped CIS Product Mapping failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + Balance_ID + ":PROFILE_TAG_VALUE=" + Balance_Value + ":BUNDLE_TYPE=" + "OnceOff" + ":PRODUCT_ID=" + ProductID + ":ACTION=Logging");
			}
			else
			{
				onlyLog.add("INC4006:BT mapped CIS Product Mapping failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + Bucket_ID +  ":BUNDLE_TYPE=" + "OnceOff" + ":PRODUCT_ID=" + ProductID + ":ACTION=Logging");
			}
	   }
	
	   return sb.toString();
	}
	
	private String PopulateCISRenewalNWPCs(String cisReference, String Balance_ID, String Balance_Value,String Offer_ID, String StartDate, String ExpiryDate,String NetworkType, String Bucket_ID,  List<String> ValidGroupBalanceCIS) {
		
		StringBuffer sb = new StringBuffer();
		String ProductID = cisReference.split("-")[1];
		String BT_PCNW = cisReference.split("-")[2];
		String NwStatus = cisReference.split("-")[3];
		String RefNWStatus = cisReference.split("-")[3];
		
		String NWExpiryDate = "";
		String NWBalance_ID = "";
		String NWBalance_Value = "";
		String NWBucket_ID = "";
		
		for(String s :ValidGroupBalanceCIS)
		{
			if(s.split("\\|")[0].split(";")[2].equals(BT_PCNW.split("#")[0]))
			{
				NWExpiryDate = s.split("\\|")[1].split(";")[2];
				NWBalance_ID = s.split("\\|")[0].split(";")[2];
				NWBalance_Value = s.split("\\|")[0].split(";")[3];
				NWBucket_ID = s.split("\\|")[1].split(";")[3];
			}
		}
				
		if(ExpiryDate.equals("1970-01-01 04:00:00") ||  ExpiryDate.length() == 0)	
			ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
	
		if(StartDate.equals("1970-01-01 04:00:00") ||  StartDate.length() == 0)	
			StartDate = LoadSubscriberMapping.CommonConfigMap.get("migration_date");
		
		if(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID) != null)
		{			
			
			//Rule C
			
			DateTime GraceDateTime = new DateTime();
			int recurringgraceperiod = Integer.parseInt(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getRecurringgraceperiod());
					
			//2018-08-02 13:10:55
			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
			DateTime ExpiryDateTime = formatter.parseDateTime(ExpiryDate);
			
			
			//Rule D
			String last_action_date = "";
			if(StartDate.length() != 0)
				last_action_date = StartDate;
			else
				last_action_date = LoadSubscriberMapping.CommonConfigMap.get("migration_date");
			
			
			//Rule B
			/*here i am handling special case of MercuryState in which NW#PC will come so 
			 * just checking if # present i will reassign value to PC and Latter for NW*/
			
			if(RefNWStatus.contains("#"))
				NwStatus = "PC";
			
			String renewal_date = "";
			if(NwStatus.toUpperCase().equals("PC"))
			{
				if(LoadSubscriberMapping.CIS_NWPC_Mapping.containsKey(Balance_ID + "," + Balance_Value))
				{
					int OffsetDays = Integer.parseInt(LoadSubscriberMapping.CIS_NWPC_Mapping.get(Balance_ID + "," + Balance_Value).getOffSetDays());
					String tempRenewal_date = ExpiryDateTime.plusDays(OffsetDays).toLocalDateTime().toString(formatter);
					
					renewal_date = tempRenewal_date.split(" ")[0] + " " + StartDate.split(" ") [1];					
				}
				else
				{
					renewal_date = ExpiryDate;
					if(!LoadSubscriberMapping.CIS_NWPC_BTID.contains(Balance_ID))
					{
						onlyLog.add("INC4007:BT mapped CIS NWPC Mapping failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + Bucket_ID +  ":BUNDLE_TYPE=" + "Renewal" + ":PRODUCT_ID=" + ProductID +":ACTION=Logging");
						return "";
					}
					//CISonlyLog.add("INC1102:CIS NWPC Mapping failed:MSISDN=" + msisdn + ":BUNDLE_TYPE=" + "Renewal" + ":PRODUCT_ID=" + ProductID + ":BT_ID=" + Balance_ID + ":BT_VALUE=" + Balance_Value + ":ACTION=Logging");
				}
			}
			else
			{
				renewal_date = ExpiryDate.split(" ")[0] + " " + StartDate.split(" ") [1];;
			}
			
			if(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductType().toUpperCase().equals("HOURLY"))
				GraceDateTime = formatter.parseDateTime(renewal_date).plusHours(recurringgraceperiod);
			else if(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductType().toUpperCase().equals("DAILY"))
				GraceDateTime = formatter.parseDateTime(renewal_date).plusDays(recurringgraceperiod);
			else if(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductType().toUpperCase().equals("MONTHLY"))
				GraceDateTime = formatter.parseDateTime(renewal_date).plusMonths(recurringgraceperiod);
			
			//Rule A
			String Status ="";
			String NetworkStatus = "";
			
			/*here i am handling special case of MercuryState in which NW#PC will come so 
			 * just checking if # present i will reassign value to PC and Latter for NW*/
			if(RefNWStatus.contains("#"))
				NwStatus = "NW";
			
			if(NwStatus.toUpperCase().equals("NW"))
			{
				if(LoadSubscriberMapping.CIS_NWPC_Mapping.containsKey(NWBalance_ID + "," + NWBalance_Value))
				{
					Status = LoadSubscriberMapping.CIS_NWPC_Mapping.get(NWBalance_ID + "," + NWBalance_Value).getStatus();
					NetworkStatus = LoadSubscriberMapping.CIS_NWPC_Mapping.get(NWBalance_ID + "," + NWBalance_Value).getNetworkStatus();
				}
				else
				{
					Status = LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getStatus();
					NetworkStatus = LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getNetworkStatus();
					if(!LoadSubscriberMapping.CIS_NWPC_BTID.contains(Balance_ID))
					{
						onlyLog.add("INC4007:BT mapped CIS NWPC Mapping failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + NWBalance_ID + ":BE_BUCKET_VALUE=" + NWBalance_Value + ":BE_BUCKET_ID=" + NWBucket_ID +  ":BUNDLE_TYPE=" + "Renewal" + ":PRODUCT_ID=" + ProductID +":ACTION=Logging");
						return "";
					}
					//CISonlyLog.add("INC1102:CIS NWPC Mapping failed:MSISDN=" + msisdn + ":BUNDLE_TYPE=" + "Renewal" + ":PRODUCT_ID=" + ProductID + ":BT_ID=" + Balance_ID + ":BT_VALUE=" + Balance_Value + ":ACTION=Logging");
				}
			}					
			else
			{	
				if( NetworkType.equals("Grace"))
				{
					Status = LoadSubscriberMapping.CommonConfigMap.get("CIS_Renewal_Grace_Status");
				}
				else
				{
					Status = "0";
				}
				//Status = LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getStatus();
				NetworkStatus = LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getNetworkStatus();
			}
			
			//Rule E
			String Billcycle = "";
			if(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductType().toUpperCase().equals("BILLCYCLE"))
			{
				int expiryDay = CommonUtilities.convertDateToTimerOfferDate(ExpiryDate)[0];
				int startDay = CommonUtilities.convertDateToTimerOfferDate(StartDate)[0];
				Billcycle = String.valueOf(expiryDay - startDay);
			}
			else
			{
				Billcycle = LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getRenewalValue();
			}
			//Rule G
			String RenewalCount = "";
			String Period = "";
			if(LoadSubscriberMapping.CIS_NWPC_Mapping.containsKey(Balance_ID + "," + Balance_Value))
			{
				RenewalCount = LoadSubscriberMapping.CIS_NWPC_Mapping.get(Balance_ID + "," + Balance_Value).getRenewalCount();
				Period = LoadSubscriberMapping.CIS_NWPC_Mapping.get(Balance_ID + "," + Balance_Value).getPeriod();
			}
			Long RenewalCountValue = 0L;
			//CISONCEOFFINFO cisOnceInfo = LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).
			sb.append(msisdn).append("|");
			if(!RenewalCount.isEmpty())
			{
				Long pcStartDate = CommonUtilities.convertDateToEpoch(StartDate);
				Long pcExpiryDate = CommonUtilities.convertDateToEpoch(ExpiryDate);
				
				//CIS.Renewal_Count=renewal_count – {(PC_Expiry_Date – PC_Start_date)/(period)} – 1]
				RenewalCountValue = Long.parseLong(RenewalCount) - (((pcExpiryDate - pcStartDate)/Long.parseLong(Period)) - 1);				
				sb.append(RenewalCountValue).append("|");
			}
			else
				sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getRenewalCount()).append("|");
			sb.append(GraceDateTime.toLocalDateTime().toString(formatter)).append("|");
			sb.append(last_action_date).append("|");
			sb.append(last_action_date).append("|");
			sb.append(renewal_date).append("|");
			//Added code as per 14_4 mapping we need to check renewlOPTOUT
			//Added code as per 14_6 mapping we need read from common_configs
			if(cisReference.toUpperCase().startsWith("RENEWALOPTOUT"))
				sb.append(LoadSubscriberMapping.CommonConfigMap.get("CIS_Renewal-OPTOUT_Status")).append("|");
			else
				sb.append(Status).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProcessingState()).append("|");
			sb.append(last_action_date).append("|");
			sb.append(StartDate).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getCircleId()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductId()).append("|");			
			sb.append("CISRenewalCounter").append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductDescription()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getSplitAction()).append("|");
			//Commented in PA13_1
			//sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getRenewalValue()).append("|");
			sb.append(Billcycle).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getExpiryNotificationFlag()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductType()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPreNotificationCount()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPostNotificationCount()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getMarketingText()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPreMarketingTextEnabled()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPostMarketingTextEnabled()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getRetryLimit()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getIsPamProduct()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPaySrc()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getBenMsisdn()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getSendSms()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getSplitNo()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductCost()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPamId()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getEnableNotification()).append("|");
			//commneted the code as per mappng PA13_1
			//sb.append(GraceDateTime.toLocalDateTime().toString(formatter)).append("|");
			sb.append(String.valueOf(recurringgraceperiod)).append("|");			
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getSrcchannel()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getOfferID()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductCategory()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getBundleName()).append("|");
			//Added code as per 14_4 mapping we need to check renewlOPTOUT
			//Added code as per 14_6 mapping we need read from common_config
			if(cisReference.toUpperCase().startsWith("RENEWALOPTOUT"))
				sb.append(LoadSubscriberMapping.CommonConfigMap.get("CIS_Renewal-OPTOUT_PPT")).append("|");
			else
				sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductPurchaseType()).append("|");
			sb.append(Targetlanguage).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getRenewalStatus()).append("|"); 
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPreNotifStatus()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPostNotifStatus()).append("|"); 
			sb.append(msisdn.charAt(msisdn.length()-1)).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getDeprovRetryLimit()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getDeprovStatus()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getBaseBundleName()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getGiftedBy()).append("|"); 
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPriority()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPreGraceExpNotifStatus()).append("|"); 
			if(!RenewalCount.isEmpty())
			{
				Long RenewalValue = Long.parseLong(RenewalCount) - (RenewalCountValue); 
				sb.append(RenewalValue).append("|");
			}
			else
				sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getRenewalNum()).append("|"); 
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPreviousStatus()).append("|"); 
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getCorrelationId()).append("|");
			if(cisReference.toUpperCase().startsWith("RENEWALOPTOUT"))
				sb.append(LoadSubscriberMapping.CommonConfigMap.get("CIS_Renewal_OPTOUT_Network_Status")).append("|");
			else
				sb.append(NetworkStatus).append("|");
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("migration_date")).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getIsGraceChargeable()).append("|");
			sb.append("").append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getOnGraceNetworkDeactEnabled());
			
			if( NetworkType.equals("Grace"))
			{
				CISnwpcList.add("CISnwpcCounter"+ "|" +msisdn+ "|" +ProductID+ "|" +"GraceExpirePreNotif" + "|" +ExpiryDate+ "|" +"-1"+ "|" +Targetlanguage+ "|" +LoadSubscriberMapping.CommonConfigMap.get("migration_date")+ "|" +(msisdn.charAt(msisdn.length()-1) + "|TRUE"));
			}
			else
			{
				CISnwpcList.add("CISnwpcCounter"+ "|" +msisdn+ "|" +ProductID+ "|" +"RSPreExpiryNotif" + "|" +ExpiryDate+ "|" +"-1"+ "|" +Targetlanguage+ "|" +LoadSubscriberMapping.CommonConfigMap.get("migration_date")+ "|" +(msisdn.charAt(msisdn.length()-1)) + "|TRUE");
			}
			
			//Calling OnceOff for RENEWALOPTOUT
			if(cisReference.toUpperCase().startsWith("RENEWALOPTOUT"))
			{
				String newCISReference = cisReference;
				newCISReference.replace("RenewalOPTOUT", "OnceOff");
				//String cisReference, String Balance_ID, String Balance_Value,String Offer_ID, String StartDate, String ExpiryDate,String NetworkType, String Bucket_ID) {
				CISOnceOffList.add(PopulateCISOnceOptOut(newCISReference,Balance_ID,Balance_Value,Offer_ID,StartDate, ExpiryDate, Bucket_ID));
			}
		}
		else
		{
			if(Bucket_ID.equals("PROFILE"))
			{
				onlyLog.add("INC6005:BT mapped CIS Product Mapping failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + Balance_ID + ":PROFILE_TAG_VALUE=" + Balance_Value + ":BUNDLE_TYPE=" + "Renewal" + ":PRODUCT_ID=" + ProductID + ":ACTION=Logging");
			}
			else
			{
				onlyLog.add("INC4006:BT mapped CIS Product Mapping failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + Bucket_ID +  ":BUNDLE_TYPE=" + "Renewal" + ":PRODUCT_ID=" + ProductID + ":ACTION=Logging");
			}
		}
		return sb.toString();
	}
	
	private String PopulateCISRenewal(String cisReference, String Balance_ID, String Balance_Value,String Offer_ID, String StartDate, String ExpiryDate,String NetworkType, String Bucket_ID) {
		
		StringBuffer sb = new StringBuffer();
		String ProductID = cisReference.split("-")[1];
		String NwStatus = cisReference.split("-")[3];
		
		
		if(ExpiryDate.equals("1970-01-01 04:00:00") ||  ExpiryDate.length() == 0)	
			ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
	
		if(StartDate.equals("1970-01-01 04:00:00") ||  StartDate.length() == 0)	
			StartDate = LoadSubscriberMapping.CommonConfigMap.get("migration_date");
		
		if(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID) != null)
		{	
			//Rule C
			
			DateTime GraceDateTime = new DateTime();
			int recurringgraceperiod = Integer.parseInt(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getRecurringgraceperiod());
					
			//2018-08-02 13:10:55
			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
			DateTime ExpiryDateTime = formatter.parseDateTime(ExpiryDate);
			
			
			//Rule D
			String last_action_date = "";
			if(StartDate.length() != 0)
				last_action_date = StartDate;
			else
				last_action_date = LoadSubscriberMapping.CommonConfigMap.get("migration_date");
			
			
			//Rule B
			String renewal_date = "";
			if(NwStatus.toUpperCase().equals("PC"))
			{
				if(LoadSubscriberMapping.CIS_NWPC_Mapping.containsKey(Balance_ID + "," + Balance_Value))
				{
					int OffsetDays = Integer.parseInt(LoadSubscriberMapping.CIS_NWPC_Mapping.get(Balance_ID + "," + Balance_Value).getOffSetDays());
					String tempRenewal_date = ExpiryDateTime.plusDays(OffsetDays).toLocalDateTime().toString(formatter);
					
					renewal_date = tempRenewal_date.split(" ")[0] + " " + StartDate.split(" ") [1];					
				}
				else
				{
					renewal_date = ExpiryDate;
					if(!LoadSubscriberMapping.CIS_NWPC_BTID.contains(Balance_ID))
					{
						onlyLog.add("INC4007:BT mapped CIS NWPC Mapping failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + Bucket_ID +  ":BUNDLE_TYPE=" + "Renewal" + ":PRODUCT_ID=" + ProductID +":ACTION=Logging");
						return "";
					}
					//CISonlyLog.add("INC1102:CIS NWPC Mapping failed:MSISDN=" + msisdn + ":BUNDLE_TYPE=" + "Renewal" + ":PRODUCT_ID=" + ProductID + ":BT_ID=" + Balance_ID + ":BT_VALUE=" + Balance_Value + ":ACTION=Logging");
				}
			}
			else
			{
				renewal_date = ExpiryDate.split(" ")[0] + " " + StartDate.split(" ") [1];;
			}
			
			if(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductType().toUpperCase().equals("HOURLY"))
				GraceDateTime = formatter.parseDateTime(renewal_date).plusHours(recurringgraceperiod);
			else if(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductType().toUpperCase().equals("DAILY"))
				GraceDateTime = formatter.parseDateTime(renewal_date).plusDays(recurringgraceperiod);
			else if(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductType().toUpperCase().equals("MONTHLY"))
				GraceDateTime = formatter.parseDateTime(renewal_date).plusMonths(recurringgraceperiod);
			
			//Rule A
			String Status ="";
			String NetworkStatus = "";
			
			if(NwStatus.toUpperCase().equals("NW"))
			{
				if(LoadSubscriberMapping.CIS_NWPC_Mapping.containsKey(Balance_ID + "," + Balance_Value))
				{
					Status = LoadSubscriberMapping.CIS_NWPC_Mapping.get(Balance_ID + "," + Balance_Value).getStatus();
					NetworkStatus = LoadSubscriberMapping.CIS_NWPC_Mapping.get(Balance_ID + "," + Balance_Value).getNetworkStatus();
				}
				else
				{
					Status = LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getStatus();
					NetworkStatus = LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getNetworkStatus();
					if(!LoadSubscriberMapping.CIS_NWPC_BTID.contains(Balance_ID))
					{
						onlyLog.add("INC4007:BT mapped CIS NWPC Mapping failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + Bucket_ID +  ":BUNDLE_TYPE=" + "Renewal" + ":PRODUCT_ID=" + ProductID +":ACTION=Logging");
						return "";
					}
					//CISonlyLog.add("INC1102:CIS NWPC Mapping failed:MSISDN=" + msisdn + ":BUNDLE_TYPE=" + "Renewal" + ":PRODUCT_ID=" + ProductID + ":BT_ID=" + Balance_ID + ":BT_VALUE=" + Balance_Value + ":ACTION=Logging");
				}
			}					
			else
			{	
				if( NetworkType.equals("Grace"))
				{
					Status = LoadSubscriberMapping.CommonConfigMap.get("CIS_Renewal_Grace_Status");
				}
				else
				{
					Status = "0";
				}
				//Status = LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getStatus();
				NetworkStatus = LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getNetworkStatus();
			}
			
			//Rule E
			String Billcycle = "";
			if(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductType().toUpperCase().equals("BILLCYCLE"))
			{
				int expiryDay = CommonUtilities.convertDateToTimerOfferDate(ExpiryDate)[0];
				int startDay = CommonUtilities.convertDateToTimerOfferDate(StartDate)[0];
				Billcycle = String.valueOf(expiryDay - startDay);
			}
			else
			{
				Billcycle = LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getRenewalValue();
			}
			
			//Rule G
			String RenewalCount = "";
			String Period = "";
			if(LoadSubscriberMapping.CIS_NWPC_Mapping.containsKey(Balance_ID + "," + Balance_Value))
			{
				RenewalCount = LoadSubscriberMapping.CIS_NWPC_Mapping.get(Balance_ID + "," + Balance_Value).getRenewalCount();
				Period = LoadSubscriberMapping.CIS_NWPC_Mapping.get(Balance_ID + "," + Balance_Value).getPeriod();
			}
			Long RenewalCountValue = 0L;
			//CISONCEOFFINFO cisOnceInfo = LoadSubscriberMapping.CIS_OnceOff_Mapping.get(ProductID).
			
			sb.append(msisdn).append("|");
			if(!RenewalCount.isEmpty())
			{
				Long pcStartDate = CommonUtilities.convertDateToEpoch(StartDate);
				Long pcExpiryDate = CommonUtilities.convertDateToEpoch(ExpiryDate);
				
				//CIS.Renewal_Count=renewal_count – {(PC_Expiry_Date – PC_Start_date)/(period)} – 1]
				RenewalCountValue = Long.parseLong(RenewalCount) - (((pcExpiryDate - pcStartDate)/Long.parseLong(Period)) - 1);
				
				sb.append(RenewalCountValue).append("|");
			}
			else
				sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getRenewalCount()).append("|");
			sb.append(GraceDateTime.toLocalDateTime().toString(formatter)).append("|");
			sb.append(last_action_date).append("|");
			sb.append(last_action_date).append("|");
			sb.append(renewal_date).append("|");
			//Added code as per 14_4 mapping we need to check renewlOPTOUT
			//Added code as per 14_6 mapping we need read from common_configs
			if(cisReference.toUpperCase().startsWith("RENEWALOPTOUT"))
				sb.append(LoadSubscriberMapping.CommonConfigMap.get("CIS_Renewal-OPTOUT_Status")).append("|");
			else
				sb.append(Status).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProcessingState()).append("|");
			sb.append(last_action_date).append("|");
			sb.append(StartDate).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getCircleId()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductId()).append("|");			
			sb.append("CISRenewalCounter").append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductDescription()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getSplitAction()).append("|");
			//Commented in PA13_1
			//sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getRenewalValue()).append("|");
			sb.append(Billcycle).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getExpiryNotificationFlag()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductType()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPreNotificationCount()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPostNotificationCount()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getMarketingText()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPreMarketingTextEnabled()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPostMarketingTextEnabled()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getRetryLimit()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getIsPamProduct()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPaySrc()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getBenMsisdn()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getSendSms()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getSplitNo()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductCost()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPamId()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getEnableNotification()).append("|");
			//commneted the code as per mappng PA13_1
			//sb.append(GraceDateTime.toLocalDateTime().toString(formatter)).append("|");
			sb.append(String.valueOf(recurringgraceperiod)).append("|");			
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getSrcchannel()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getOfferID()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductCategory()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getBundleName()).append("|");
			//Added code as per 14_4 mapping we need to check renewlOPTOUT
			if(cisReference.toUpperCase().startsWith("RENEWALOPTOUT"))
				sb.append(LoadSubscriberMapping.CommonConfigMap.get("CIS_Renewal-OPTOUT_PPT")).append("|");
			else
				sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getProductPurchaseType()).append("|");			
			sb.append(Targetlanguage).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getRenewalStatus()).append("|"); 
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPreNotifStatus()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPostNotifStatus()).append("|"); 
			sb.append(msisdn.charAt(msisdn.length()-1)).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getDeprovRetryLimit()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getDeprovStatus()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getBaseBundleName()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getGiftedBy()).append("|"); 
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPriority()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPreGraceExpNotifStatus()).append("|");
			if(!RenewalCount.isEmpty())
			{
				Long RenewalValue = Long.parseLong(RenewalCount) - (RenewalCountValue); 
				sb.append(RenewalValue).append("|");
			}
			else
				sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getRenewalNum()).append("|"); 
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getPreviousStatus()).append("|"); 
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getCorrelationId()).append("|");
			if(cisReference.toUpperCase().startsWith("RENEWALOPTOUT"))
				sb.append(LoadSubscriberMapping.CommonConfigMap.get("CIS_Renewal_OPTOUT_Network_Status")).append("|");
			else
				sb.append(NetworkStatus).append("|");
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("migration_date")).append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getIsGraceChargeable()).append("|");
			sb.append("").append("|");
			sb.append(LoadSubscriberMapping.CIS_Renewal_Mapping.get(ProductID).getOnGraceNetworkDeactEnabled());
			
			if( NetworkType.equals("Grace"))
			{
				CISnwpcList.add("CISnwpcCounter"+ "|" +msisdn+ "|" +ProductID+ "|" +"GraceExpirePreNotif" + "|" +ExpiryDate+ "|" +"-1"+ "|" +Targetlanguage+ "|" +LoadSubscriberMapping.CommonConfigMap.get("migration_date")+ "|" +(msisdn.charAt(msisdn.length()-1))+ "|TRUE");
			}
			else
			{
				CISnwpcList.add("CISnwpcCounter"+ "|" +msisdn+ "|" +ProductID+ "|" +"RSPreExpiryNotif" + "|" +ExpiryDate+ "|" +"-1"+ "|" +Targetlanguage+ "|" +LoadSubscriberMapping.CommonConfigMap.get("migration_date")+ "|" +(msisdn.charAt(msisdn.length()-1)) + "|TRUE");
			}
			
			//Calling OnceOff for RENEWALOPTOUT
			if(cisReference.toUpperCase().startsWith("RENEWALOPTOUT"))
			{
				String newCISReference = cisReference.replace("RenewalOPTOUT", "OnceOff");
				//String cisReference, String Balance_ID, String Balance_Value,String Offer_ID, String StartDate, String ExpiryDate,String NetworkType, String Bucket_ID) {
				CISOnceOffList.add(PopulateCISOnceOptOut(newCISReference,Balance_ID,Balance_Value,Offer_ID,StartDate, ExpiryDate, Bucket_ID));
			}
		}
		else
		{
			if(Bucket_ID.equals("PROFILE"))
			{
				onlyLog.add("INC6005:BT mapped CIS Product Mapping failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + Balance_ID + ":PROFILE_TAG_VALUE=" + Balance_Value + ":BUNDLE_TYPE=" + "Renewal" + ":PRODUCT_ID=" + ProductID + ":ACTION=Logging");
			}
			else
			{
				onlyLog.add("INC4006:BT mapped CIS Product Mapping failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + Bucket_ID +  ":BUNDLE_TYPE=" + "Renewal" + ":PRODUCT_ID=" + ProductID + ":ACTION=Logging");
			}
		}
		
		return sb.toString();
	}
	
	private String PopulateCISParking(String cisReference, String Balance_ID, String Balance_Value,String Offer_ID, String StartDate, String ExpiryDate, String Bucket_ID) {
		
		StringBuffer sb = new StringBuffer();
		String ProductID = cisReference.split("-")[1];
		String NwStatus = cisReference.split("-")[3];
		
		if(ExpiryDate.equals("1970-01-01 04:00:00") ||  ExpiryDate.length() == 0)	
			ExpiryDate = LoadSubscriberMapping.CommonConfigMap.get("max_date").replace("'", "") + " 00:00:00";
		
		if(StartDate.equals("1970-01-01 04:00:00") ||  StartDate.length() == 0)	
			StartDate = LoadSubscriberMapping.CommonConfigMap.get("migration_date");
				
		if(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID) != null)
		{		
			//Rule A
			String Status ="";
			//String NetworkStatus = "";
			if(NwStatus.toUpperCase().equals("NW"))
			{
				if(LoadSubscriberMapping.CIS_NWPC_Mapping.containsKey(Balance_ID + "," + Balance_Value))
				{
					Status = LoadSubscriberMapping.CIS_NWPC_Mapping.get(Balance_ID + "," + Balance_Value).getStatus();
					//NetworkStatus = LoadSubscriberMapping.CIS_NWPC_Mapping.get(Balance_ID + "," + Balance_Value).getNetworkStatus();
				}
				else
				{
					Status = LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getStatus();
					//NetworkStatus = LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getNetworkStatus();
					if(!LoadSubscriberMapping.CIS_NWPC_BTID.contains(Balance_ID))
					{
						onlyLog.add("INC4007:BT mapped CIS NWPC Mapping failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + Bucket_ID +  ":BUNDLE_TYPE=" + "Parking" + ":PRODUCT_ID=" + ProductID +":ACTION=Logging");
						return "";
					}
					//CISonlyLog.add("INC1102:CIS NWPC Mapping failed:MSISDN=" + msisdn + ":BUNDLE_TYPE=" + "Parking" + ":PRODUCT_ID=" + ProductID + ":BT_ID=" + Balance_ID + ":BT_VALUE=" + Balance_Value + ":ACTION=Logging");
				}
			}
			else
			{
				Status = LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getStatus();
				//NetworkStatus = LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getNetworkStatus();
			}
			
			sb.append("CISParkingCounter").append("|");
			sb.append(msisdn).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getProductId()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getCircleId()).append("|");
			sb.append(Status).append("|");
			sb.append(ExpiryDate).append("|");
			sb.append(StartDate).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getProductType()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getServiceName()).append("|");			
			sb.append(ExpiryDate).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getProductDescription()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getPaySrc()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getBenMsisdn()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getProductCost()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getSrcchannel()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getOfferId()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getProductCategory()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getBundleName()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getBaseBundleName()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getProductPurchaseType()).append("|");
			sb.append(Targetlanguage).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getPreNotifStatus()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getPostNotifStatus()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getRetryLimit()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getPriority()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getProcessingState()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getSendSms()).append("|");
			sb.append(msisdn.charAt(msisdn.length()-1)).append("|");		
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getPreviousStatus()).append("|");
			//Commented in PA13_5
			//sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getCorrelationId()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getPreNotificationCount()).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getPostNotificationCount()).append("|");
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("migration_date")).append("|");
			sb.append(LoadSubscriberMapping.CIS_Parking_Mapping.get(ProductID).getSubscriptionMode()).append("|");			
			sb.append("");
			
			CISnwpcList.add("CISnwpcCounter"+ "|" +msisdn+ "|" +ProductID+ "|" +"ParkExpirePreNotif" + "|" +ExpiryDate+ "|" +"-1"+ "|" +Targetlanguage+ "|" +LoadSubscriberMapping.CommonConfigMap.get("migration_date")+ "|" +(msisdn.charAt(msisdn.length()-1))+ "|TRUE");
			
			//populate the tracklog for network bundle
			if(Bucket_ID.equals("PROFILE"))
			{
				//INC7013	PT mapped to Parking offer	MSISDN,PROFILE_TAG_NAME,OFFER_ID
				trackLog.add("INC7013:PT mapped to Parking offer:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + Balance_ID + ":OFFER_ID=" + Offer_ID + ":ACTION=Logging");
			}
			else
			{
				//INC7012	BT mapped to Parking offer	MSISDN,BALANCE_TYPE,BE_BUCKET_ID,OFFER_ID
				trackLog.add("INC7012:BT mapped to Parking offer:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_ID=" + Bucket_ID + ":OFFER_ID=" + Offer_ID + ":ACTION=Logging");
			}
		}
		else
		{
			if(Bucket_ID.equals("PROFILE"))
			{
				onlyLog.add("INC6005:BT mapped CIS Product Mapping failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + Balance_ID + ":PROFILE_TAG_VALUE=" + Balance_Value + ":BUNDLE_TYPE=" + "Parking" + ":PRODUCT_ID=" + ProductID + ":ACTION=Logging");
			}
			else
			{
				onlyLog.add("INC4006:BT mapped CIS Product Mapping failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + Bucket_ID +  ":BUNDLE_TYPE=" + "Parking" + ":PRODUCT_ID=" + ProductID + ":ACTION=Logging");
			}
		}
		return sb.toString();
	}	
	
	/***********Backup Code**************/
		
}
