package com.ericsson.dm.transform.implementation;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


import com.ericsson.dm.Utils.CommonUtilities;
import com.ericsson.dm.inititialization.LoadSubscriberMapping;
import com.ericsson.dm.transformation.ExecuteTransformation;
import com.ericsson.jibx.beans.PROFILETAGLIST.PROFILETAGINFO;
import com.ericsson.jibx.beans.SubscriberXml;
import com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo;

public class OfferAttribute implements Comparator<SchemasubscriberbalancesdumpInfo> {

	SubscriberXml subscriber;
	String msisdn;
	String INITIAL_ACTIVATION_DATE;
	 
	Set<String> rejectAndLog;
	Set<String> onlyLog;
	
	Set<String> AMBTIDSet;
	Set<String> CommonBTIDSet;
	private Map<String, Set<String>> ProductIDLookUpMap;
	
	ProfileTagProcessing profileTag;
	CommonFunctions commonfunction;
	public CopyOnWriteArrayList<SchemasubscriberbalancesdumpInfo> SortedBalanceInput;
	
	public OfferAttribute()
	{
		
	}
	
	public OfferAttribute(SubscriberXml subscriber,Set<String> rejectAndLog, Set<String> onlyLog, String INITIAL_ACTIVATION_DATE,Map<String, Set<String>> ProductIDLookUpMap) {
		// TODO Auto-generated constructor stub
		this.subscriber=subscriber;		
		this.rejectAndLog=rejectAndLog;
		this.onlyLog=onlyLog;
		this.ProductIDLookUpMap = ProductIDLookUpMap;
		this.INITIAL_ACTIVATION_DATE = INITIAL_ACTIVATION_DATE;
		this.AMBTIDSet = new HashSet<>();
		this.CommonBTIDSet = new HashSet<>();
		SortedBalanceInput = new CopyOnWriteArrayList<>();
		profileTag = new ProfileTagProcessing(subscriber,this.onlyLog);		
		commonfunction = new CommonFunctions(subscriber, ProductIDLookUpMap, this.onlyLog);
	}
	
	public Map<String, List<String>> execute() {
		// TODO Auto-generated method stub
		msisdn = subscriber.getSubscriberInfoMSISDN();
		
		SortedBalanceInput.addAll(subscriber.getBalancesdumpInfoList());
		Collections.sort(SortedBalanceInput,new Offer());
		
		Map<String,List<String>> map = new HashMap<>();
		map.put("OfferAttribute", generateOfferAttributes());

		SortedBalanceInput.clear();
		return map;
	}
	
	@Override
	public int compare(SchemasubscriberbalancesdumpInfo o1, SchemasubscriberbalancesdumpInfo o2) {
		int value1 = (o2.getBEEXPIRY()).compareTo((o1.getBEEXPIRY()));
        if (value1 == 0) {
        	return  o2.getBEBUCKETID().compareTo(o1.getBEBUCKETID());
        }
        return value1;
	}
	
	private List<String> generateOfferAttributes(){
		
		List<String> OfferAttributeList = new ArrayList<>();
		OfferAttributeList.addAll(OfferAttrFromAMBalanceMapping());
		OfferAttributeList.addAll(OfferAttrFromCommonBalanceMapping());
		OfferAttributeList.addAll(offerAttributeFromDefaultService(msisdn));
		OfferAttributeList.addAll(offerAttributeFromBalanceMapping(msisdn));
		OfferAttributeList.addAll(offerAttributeFromProfileMapping(msisdn));
		return OfferAttributeList;
	}
	
	public List<String> OfferAttrFromAMBalanceMapping()
	{
		List<String> FinalBalanceOffer = new ArrayList<>();
		
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
				//INC4001	Balance_Type expired	MSISDN,BALANCE_TYPE,BE_BUCKET_VALUE,BE_BUCKET_ID,BE_EXPIRY
				AMBTIDSet.add(balanceInput.getBEBUCKETID());
				continue;
			}
			if(LoadSubscriberMapping.ExceptionBalancesPCForAMGroup.contains(BT_ID))
			{
				List<String> AMGroupName = (commonfunction.getASpecialGroupKey(LoadSubscriberMapping.BalanceOnlySpecialAMGroupMap,BT_ID));
				boolean ExtraOfferFlag = false;
				for(String GroupName : AMGroupName)
				{
					if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + GroupName) != null)
					{	
						
					}
				}				
			}
		}
		
		return FinalBalanceOffer;
	}
	
	public List<String> OfferAttrFromCommonBalanceMapping()
	{
		Date currDate = new Date();
		SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		List<String> BalanceOffAttrList =new ArrayList<>();
		Set<String> CompletedBT_ID = new HashSet<>();
		//Set<String> CompletedGroupBT_ID = new HashSet<>();
		CompletedBT_ID.addAll(AMBTIDSet);
		
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{			
			String Balance_ID = balanceInput.getBALANCETYPE();
			if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(Balance_ID))
				continue;
			
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
				continue;
			
			if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID).equals("Y"))
			{
				CompletedBT_ID.add(Balance_ID);
				continue;
			}
			
			if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
			{
				CompletedBT_ID.add(Balance_ID);
				continue;
			}
			//Two BT 3503 and 3412 are exceptional as they belong to dummy and Group so just handling based on its value.
			if(LoadSubscriberMapping.CommonBTForDummyGroup.contains(Balance_ID))
			{
				if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
				{
					if(Balance_ID.equals("3412"))
					{
						String OffAttr_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getAttrOfferId();
						String OffAttr_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getAttrType();
						String OffAttr_Name = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getAttrName();
						
						if(!OffAttr_ID.isEmpty())
						{
							BalanceOffAttrList.add(PopulateOfferAttribute(OffAttr_Type, OffAttr_Name, OffAttr_ID, Balance_ID, Balance_Value, balanceInput.getBEBUCKETID()));									
						}
						CompletedBT_ID.add(Balance_ID);
						continue;
					}
					if(Balance_ID.equals("3411"))
					{
						if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
						{
							String OffAttr_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getAttrOfferId();
							String OffAttr_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getAttrType();
							String OffAttr_Name = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getAttrName();
							
							if(!OffAttr_ID.isEmpty())
							{
								BalanceOffAttrList.add(PopulateOfferAttribute(OffAttr_Type, OffAttr_Name, OffAttr_ID, Balance_ID, Balance_Value, balanceInput.getBEBUCKETID()));									
							}
							CompletedBT_ID.add(Balance_ID);
							continue;
						}						
					}
				}
			}
		}
		
		CommonBTIDSet.addAll(CompletedBT_ID);
		return BalanceOffAttrList.stream().distinct().collect(Collectors.toList());
	}
	
	private synchronized Collection<? extends String> offerAttributeFromBalanceMapping(String msisdn) {
		Date currDate = new Date();
		SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		List<String> BalanceOffAttrList =new ArrayList<>();
		Set<String> CompletedBT_ID = new HashSet<>();
		//Set<String> CompletedGroupBT_ID = new HashSet<>();
		CompletedBT_ID.addAll(AMBTIDSet);
		CompletedBT_ID.addAll(CommonBTIDSet);
		
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{	
			boolean BT3216fferCreated = false;
			String Balance_ID = balanceInput.getBALANCETYPE();
			if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(Balance_ID))
				continue;
			
			
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
				continue;
			
			if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID).equals("Y"))
			{
				CompletedBT_ID.add(Balance_ID);
				continue;
			}
			
			if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
			{
				CompletedBT_ID.add(Balance_ID);
				continue;
			}
			//Two BT 3503 and 3412 are exceptional as they belong to dummy and Group so just handling based on its value.
			if(LoadSubscriberMapping.CommonBTForDummyGroup.contains(Balance_ID))
			{
				if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
				{
					if(Balance_ID.equals("3412"))
					{
						/*String OffAttr_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getAttrOfferId();
						String OffAttr_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getAttrType();
						String OffAttr_Name = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getAttrName();
						
						if(!OffAttr_ID.isEmpty())
						{
							BalanceOffAttrList.add(PopulateOfferAttribute(OffAttr_Type, OffAttr_Name, OffAttr_ID, Balance_ID, Balance_Value, balanceInput.getBEBUCKETID()));									
						}*/
						
						CompletedBT_ID.add(Balance_ID);
						continue;
					}
					if(Balance_ID.equals("3411"))
					{
						if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
						{
							/*String OffAttr_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getAttrOfferId();
							String OffAttr_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getAttrType();
							String OffAttr_Name = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getAttrName();
							
							if(!OffAttr_ID.isEmpty())
							{
								BalanceOffAttrList.add(PopulateOfferAttribute(OffAttr_Type, OffAttr_Name, OffAttr_ID, Balance_ID, Balance_Value, balanceInput.getBEBUCKETID()));									
							}*/
							CompletedBT_ID.add(Balance_ID);
							continue;
						}						
					}
				}
			}
			
			//Check if BT is valid for migration for group element
			if(LoadSubscriberMapping.AllBTBalancesValueSet.contains(Balance_ID))
			{
				if(LoadSubscriberMapping.AllBTBalancesValueMap.containsKey(Balance_ID))
				{
					String BT_TYPE = LoadSubscriberMapping.AllBTBalancesValueMap.get(Balance_ID).getBTTYPE();
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
						
						if(BT_TYPE.toUpperCase().equals("P"))
						{
							Set<String> BTValues = new HashSet<String>();
							if(LoadSubscriberMapping.GraceFullBalanceGroupingMap.containsKey(Balance_ID))
							{
								BTValues.addAll(LoadSubscriberMapping.GraceFullBalanceGroupingMap.get(Balance_ID));
								values = BTValues.stream().toArray(String[] ::new);
							}								
						}
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
				String OffAttr_ID = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getAttrOfferId();
				String Symbol = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getSymbols();
				String BT_Value = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getBTValue();
				String OffAttr_Name = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getAttrName();
				String OffAttr_Type = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getAttrType();
							
				if(!OffAttr_ID.isEmpty())
				{					
					if(Symbol.isEmpty() && BT_Value.isEmpty())
					{	
						BalanceOffAttrList.add(PopulateOfferAttribute(OffAttr_Type, OffAttr_Name, OffAttr_ID, Balance_ID, Balance_Value, balanceInput.getBEBUCKETID()));						
					}
					else
					{
						boolean OffAttrCreation = false;
						if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
							OffAttrCreation = true;
						else if(Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_Value))
							OffAttrCreation = true;
						else if(Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value))
							OffAttrCreation = true;
						else if(Symbol.equals("or"))
						{
							//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
							String[] values = BT_Value.split("#");
							
							if(Arrays.stream(values).anyMatch(Balance_Value::equals))
								OffAttrCreation = true;
						}					
						if(OffAttrCreation)
						{
							BalanceOffAttrList.add(PopulateOfferAttribute(OffAttr_Type, OffAttr_Name, OffAttr_ID, Balance_ID, Balance_Value, balanceInput.getBEBUCKETID()));							
						}						
					}
				}	
			}	
			else
			{					
				
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
													String OffAttr_ID = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getAttrOfferId();
													String OffAttr_Type = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getAttrType();
													String OffAttr_Name = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getAttrName();
													
													
													//System.out.println("MSISDN------- " + msisdn );
													if(!BT3216fferCreated && !OffAttr_ID.isEmpty())
													{
														BalanceOffAttrList.add(PopulateOfferAttribute(OffAttr_Type, OffAttr_Name, OffAttr_ID, Balance_ID, Balance_Value, balanceInput.getBEBUCKETID()));
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
								String OffAttr_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + BTValue3011).getAttrOfferId();
								String OffAttr_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + BTValue3011).getAttrType();
								String OffAttr_Name = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + BTValue3011).getAttrName();
								
								if(!OffAttr_ID.isEmpty())
								{
									BalanceOffAttrList.add(PopulateOfferAttribute(OffAttr_Type, OffAttr_Name, OffAttr_ID, Balance_ID, Balance_Value, balanceInput.getBEBUCKETID()));									
								}									
							}
						}
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
					}
				}
				else
				{
					String GroupName = "";
					List<String> CurrentGroupBalance = new ArrayList<>();
					List<String> ValidGroupBalanceOfferAttr = new ArrayList<>();
					Set<String> ValidGroupBT_ID = new HashSet<>();
					List<String> FinalAttrList = new ArrayList<>();
					boolean ExtraOfferFlag = false;
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
							if(GroupName.startsWith("B-"))
							{
								GroupName = commonfunction.ComputeBGroup(Balance_ID,GroupName,CompletedBT_ID);
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
							if(GroupName.startsWith("H-"))
							{
								GroupName = commonfunction.ComputeHGroup(Balance_ID,GroupName,CompletedBT_ID);
								CurrentGroupBalance.addAll(LoadSubscriberMapping.BalanceGroupingMap.get(GroupName));
							}
							if(GroupName.startsWith("G"))
							{
								continue;
							}
							break;
						}
					}	
					
					
					//if(!CompletedGroup.contains(GroupName))
					{
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
									String TempBalance_Name = TempbalanceInput.getBALANCETYPENAME();
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
									
									if(id.equals(TempBalance_ID))
									{
										if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(id + "|" + FinalGroupName) != null)
										{
											if(!TempBalance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(TempBalance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
											{
												CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
												continue;
											}
											String OffAttr_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAttrOfferId();
											String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getSymbols();
											String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTValue();
											String OffAttr_Name = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAttrName();
											String OffAttr_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAttrType();
											String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTTYPE();
											
											if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
											{
												CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
												ValidGroupBT_ID.add(TempBalance_ID);
												ValidGroupBalanceOfferAttr.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + OffAttr_ID + ";" + OffAttr_Name + ";" + OffAttr_Type + ";" +  TempBalance_StartDate + ";" + TempBalance_ExpiryDate + ";" + TempbalanceInput.getBEBUCKETID());
												if(!OffAttr_ID.isEmpty())
													FinalAttrList.add(TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + OffAttr_ID + ";" + OffAttr_Name + ";" + OffAttr_Type + ";" +  TempBalance_StartDate + ";" + TempBalance_ExpiryDate + ";" + TempbalanceInput.getBEBUCKETID());
												break;
											}
											else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
											{
												CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
												ValidGroupBT_ID.add(TempBalance_ID);
												ValidGroupBalanceOfferAttr.add(BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + OffAttr_ID + ";" + OffAttr_Name + ";" + OffAttr_Type + ";" +  TempBalance_StartDate + ";" + TempBalance_ExpiryDate + ";" + TempbalanceInput.getBEBUCKETID());
												if(!OffAttr_ID.isEmpty())
													FinalAttrList.add(TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + OffAttr_ID + ";" + OffAttr_Name + ";" + OffAttr_Type + ";" +  TempBalance_StartDate + ";" + TempBalance_ExpiryDate + ";" + TempbalanceInput.getBEBUCKETID());
												break;
											}
											else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
											{
												CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
												ValidGroupBT_ID.add(TempBalance_ID);
												ValidGroupBalanceOfferAttr.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + OffAttr_ID + ";" + OffAttr_Name + ";" + OffAttr_Type + ";" +  TempBalance_StartDate + ";" + TempBalance_ExpiryDate + ";" + TempbalanceInput.getBEBUCKETID());
												if(!OffAttr_ID.isEmpty())
													FinalAttrList.add(TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + OffAttr_ID + ";" + OffAttr_Name + ";" + OffAttr_Type + ";" +  TempBalance_StartDate + ";" + TempBalance_ExpiryDate + ";" + TempbalanceInput.getBEBUCKETID());
												break;
											}
											else if(Symbol.equals("or"))
											{
												//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
												String[] values = BT_Value.split("#");											
												if(Arrays.stream(values).anyMatch(TempBalance_Value::equals))
												{
													CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
													ValidGroupBT_ID.add(TempBalance_ID);
													ValidGroupBalanceOfferAttr.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + OffAttr_ID + ";" + OffAttr_Name + ";" + OffAttr_Type + ";" +  TempBalance_StartDate + ";" + TempBalance_ExpiryDate + ";" + TempbalanceInput.getBEBUCKETID());
													if(!OffAttr_ID.isEmpty())
														FinalAttrList.add(TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + OffAttr_ID + ";" + OffAttr_Name + ";" + OffAttr_Type + ";" +  TempBalance_StartDate + ";" + TempBalance_ExpiryDate + ";" + TempbalanceInput.getBEBUCKETID());
													break;
												}
											}
										}
									}
								}
							}
							if(FinalGroupName.startsWith("B-") && ValidGroupBalanceOfferAttr.size() != 0)
							{
								if(ValidGroupBalanceOfferAttr.size() == CurrentGroupBalance.size())
								{
									FinalAttrList.forEach(item->{
										String Attr_Fields = item.split("\\|")[1];
										String BalanceValue = item.split("\\|")[0].split(";")[2];
										String SourceValue = item.split("\\|")[0];
										
										// OffAttr_Type,OffAttr_Name,  OffAttr_ID, Balance_ID, String Balance_Value, String BEBucketID)
										BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Fields.split(";")[2], Attr_Fields.split(";")[1], Attr_Fields.split(";")[0], SourceValue.split(";")[1], BalanceValue, Attr_Fields.split(";")[5]));
									
									});//CompletedBT_ID.addAll(CurrentGroupBalance);
								}
								else
								{
															
								}
							}
							
							if(FinalGroupName.startsWith("A-") && ValidGroupBalanceOfferAttr.size() != 0)
							{
								if(ValidGroupBalanceOfferAttr.size() == CurrentGroupBalance.size())
								{
									FinalAttrList.forEach(item->{
										String Attr_Fields = item.split("\\|")[1];
										String BalanceValue = item.split("\\|")[0].split(";")[2];
										String SourceValue = item.split("\\|")[0];
										
										BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Fields.split(";")[2], Attr_Fields.split(";")[1], Attr_Fields.split(";")[0], SourceValue.split(";")[1], BalanceValue, Attr_Fields.split(";")[5]));										
									});								
								}
								else
								{									
									if(LoadSubscriberMapping.BalanceAVGroupLookup.get(FinalGroupName) != null)
									{
										//ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).count()
										if(ValidGroupBalanceOfferAttr.size() >= 2 && ValidGroupBalanceOfferAttr.stream().filter(item->item.startsWith("P")).count() >=1 && ValidGroupBalanceOfferAttr.stream().filter(item->item.startsWith("V")).count() >=1){
											FinalAttrList.forEach(item->{
												String Attr_Fields = item.split("\\|")[1];
												String BalanceValue = item.split("\\|")[0].split(";")[2];
												String SourceValue = item.split("\\|")[0];
												if(Attr_Fields.split(";")[0].length() > 0)
													BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Fields.split(";")[2], Attr_Fields.split(";")[1], Attr_Fields.split(";")[0], SourceValue.split(";")[1], BalanceValue, Attr_Fields.split(";")[5]));										
											});
										}
										//else if(ValidGroupBalanceOffer.size() >= 2 &&  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("V")).count() >=1){
										else if(ValidGroupBalanceOfferAttr.size() >= 2 ){
											for(String s : FinalAttrList)
											{
												//ChatnCall Status;1758;1|3034;ChatnCallStatus;4;;1970-01-01 04:00:00;2119891998]
												String TargetValue = s.split("\\|")[1];
												String SourceValue = s.split("\\|")[0];
												if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M") != null)
												{
													//String Attr_Fields = item.split("\\|")[1];
													String Attr_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getAttrOfferId();
													String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getSymbols();
													String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getBTValue();
													String Attr_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getAttrType();
													String Attr_Name = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getAttrName();
													String BalanceValue = SourceValue.split(";")[2];
													String OfferAttr_Defination = "";
													
													if(Attr_ID.length() > 0 && Symbol.equals(">") && Integer.parseInt(BalanceValue) > Integer.parseInt(BT_VALUE))
													{
														//String OffAttr_Type,String OffAttr_Name, String OffAttr_ID, String Balance_ID, String Balance_Value, String BEBucketID)
														BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
													}
													if(Attr_ID.length() > 0 && Symbol.equals(">=") && Integer.parseInt(BalanceValue) >= Integer.parseInt(BT_VALUE))
													{
														//String OffAttr_Type,String OffAttr_Name, String OffAttr_ID, String Balance_ID, String Balance_Value, String BEBucketID)
														BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
													}
													if(Attr_ID.length() > 0 && Symbol.equals("=") && Integer.parseInt(BalanceValue) == Integer.parseInt(BT_VALUE))
													{
														//String OffAttr_Type,String OffAttr_Name, String OffAttr_ID, String Balance_ID, String Balance_Value, String BEBucketID)
														BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
													}
													else if( Attr_ID.length() > 0 && Symbol.equals("or"))
													{
														//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
														String[] values = BT_VALUE.split("#");											
														if(Arrays.stream(values).anyMatch(BalanceValue::equals))
														{
															BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
														}
													}
												}												
											}
										}
										else if(ValidGroupBalanceOfferAttr.size() == 1)
										{
											//BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private
											if(ValidGroupBalanceOfferAttr.stream().filter(item->item.startsWith("P")).count() == 1 ){
												
											}												
											if((ValidGroupBalanceOfferAttr.stream().filter(item->item.startsWith("V")).count() == 1)){
												for(String s : FinalAttrList)
												{
													//ChatnCall Status;1758;1|3034;ChatnCallStatus;4;;1970-01-01 04:00:00;2119891998]
													String TargetValue = s.split("\\|")[1];
													String SourceValue = s.split("\\|")[0];
													if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M") != null)
													{
														//String Attr_Fields = item.split("\\|")[1];
														String Attr_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getAttrOfferId();
														String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getSymbols();
														String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getBTValue();
														String Attr_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getAttrType();
														String Attr_Name = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getAttrName();
														String BalanceValue = SourceValue.split(";")[2];
														String OfferAttr_Defination = "";
														
														if(Attr_ID.length() > 0 && Symbol.equals(">") && Integer.parseInt(BalanceValue) > Integer.parseInt(BT_VALUE))
														{
															//String OffAttr_Type,String OffAttr_Name, String OffAttr_ID, String Balance_ID, String Balance_Value, String BEBucketID)
															BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
														}
														if(Attr_ID.length() > 0 && Symbol.equals(">=") && Integer.parseInt(BalanceValue) >= Integer.parseInt(BT_VALUE))
														{
															//String OffAttr_Type,String OffAttr_Name, String OffAttr_ID, String Balance_ID, String Balance_Value, String BEBucketID)
															BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
														}
														if(Attr_ID.length() > 0 && Symbol.equals("=") && Integer.parseInt(BalanceValue) == Integer.parseInt(BT_VALUE))
														{
															//String OffAttr_Type,String OffAttr_Name, String OffAttr_ID, String Balance_ID, String Balance_Value, String BEBucketID)
															BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
														}
														else if( Attr_ID.length() > 0 && Symbol.equals("or"))
														{
															//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
															String[] values = BT_VALUE.split("#");											
															if(Arrays.stream(values).anyMatch(BalanceValue::equals))
															{
																BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
															}
														}
													}
												}
											}
											if((ValidGroupBalanceOfferAttr.stream().filter(item->item.startsWith("M")).count() == 1)){
												for(String s : FinalAttrList)
												{
													//ChatnCall Status;1758;1|3034;ChatnCallStatus;4;;1970-01-01 04:00:00;2119891998]
													String TargetValue = s.split("\\|")[1];
													String SourceValue = s.split("\\|")[0];
													if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M") != null)
													{
														//String Attr_Fields = item.split("\\|")[1];
														String Attr_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getAttrOfferId();
														String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getSymbols();
														String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getBTValue();
														String Attr_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getAttrType();
														String Attr_Name = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getAttrName();
														String BalanceValue = SourceValue.split(";")[2];
														String OfferAttr_Defination = "";
														
														if(Attr_ID.length() > 0 && Symbol.equals(">") && Integer.parseInt(BalanceValue) > Integer.parseInt(BT_VALUE))
														{
															//String OffAttr_Type,String OffAttr_Name, String OffAttr_ID, String Balance_ID, String Balance_Value, String BEBucketID)
															BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
														}
														if(Attr_ID.length() > 0 && Symbol.equals(">=") && Integer.parseInt(BalanceValue) >= Integer.parseInt(BT_VALUE))
														{
															//String OffAttr_Type,String OffAttr_Name, String OffAttr_ID, String Balance_ID, String Balance_Value, String BEBucketID)
															BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
														}
														if(Attr_ID.length() > 0 && Symbol.equals("=") && Integer.parseInt(BalanceValue) == Integer.parseInt(BT_VALUE))
														{
															//String OffAttr_Type,String OffAttr_Name, String OffAttr_ID, String Balance_ID, String Balance_Value, String BEBucketID)
															BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
														}
														else if( Attr_ID.length() > 0 && Symbol.equals("or"))
														{
															//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
															String[] values = BT_VALUE.split("#");											
															if(Arrays.stream(values).anyMatch(BalanceValue::equals))
															{
																BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
															}
														}															
													}
												}
											}
											//CompletedBT_ID.addAll(CurrentGroupBalance);
										}
									}
									else
									{
										//ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).count()
										if(ValidGroupBalanceOfferAttr.size() >= 2 && ValidGroupBalanceOfferAttr.stream().filter(item->item.startsWith("P")).count() >=1 && ValidGroupBalanceOfferAttr.stream().filter(item->item.startsWith("M")).count() >=1){
											FinalAttrList.forEach(item->{
												String Attr_Fields = item.split("\\|")[1];
												String BalanceValue = item.split("\\|")[0].split(";")[2];
												String SourceValue = item.split("\\|")[0];
												if(Attr_Fields.split(";")[0].length() > 0)
													BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Fields.split(";")[2], Attr_Fields.split(";")[1], Attr_Fields.split(";")[0], SourceValue.split(";")[1], BalanceValue, Attr_Fields.split(";")[5]));										
											});
										}
										else if(ValidGroupBalanceOfferAttr.size() >= 2 &&  ValidGroupBalanceOfferAttr.stream().filter(item->item.startsWith("M")).count() >=1){
											for(String s : FinalAttrList)
											{
												//ChatnCall Status;1758;1|3034;ChatnCallStatus;4;;1970-01-01 04:00:00;2119891998]
												String TargetValue = s.split("\\|")[1];
												String SourceValue = s.split("\\|")[0];
												if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M") != null)
												{
													//String Attr_Fields = item.split("\\|")[1];
													String Attr_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getAttrOfferId();
													String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getSymbols();
													String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getBTValue();
													String Attr_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getAttrType();
													String Attr_Name = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getAttrName();
													String BalanceValue = SourceValue.split(";")[2];
													String OfferAttr_Defination = "";
													
													if(Attr_ID.length() > 0 && Symbol.equals(">") && Integer.parseInt(BalanceValue) > Integer.parseInt(BT_VALUE))
													{
														//String OffAttr_Type,String OffAttr_Name, String OffAttr_ID, String Balance_ID, String Balance_Value, String BEBucketID)
														BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
													}
													if(Attr_ID.length() > 0 && Symbol.equals(">=") && Integer.parseInt(BalanceValue) >= Integer.parseInt(BT_VALUE))
													{
														//String OffAttr_Type,String OffAttr_Name, String OffAttr_ID, String Balance_ID, String Balance_Value, String BEBucketID)
														BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
													}
													if(Attr_ID.length() > 0 && Symbol.equals("=") && Integer.parseInt(BalanceValue) == Integer.parseInt(BT_VALUE))
													{
														//String OffAttr_Type,String OffAttr_Name, String OffAttr_ID, String Balance_ID, String Balance_Value, String BEBucketID)
														BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
													}
													else if( Attr_ID.length() > 0 && Symbol.equals("or"))
													{
														//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
														String[] values = BT_VALUE.split("#");											
														if(Arrays.stream(values).anyMatch(BalanceValue::equals))
														{
															BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
														}
													}
												}
											}										
										}
										else if(ValidGroupBalanceOfferAttr.size() == 1)
										{
											//BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private
											if(ValidGroupBalanceOfferAttr.stream().filter(item->item.startsWith("P")).count() == 1 ){
												FinalAttrList.forEach(item->{
													String Attr_Fields = item.split("\\|")[1];
													String BalanceValue = item.split("\\|")[0].split(";")[2];
													String SourceValue = item.split("\\|")[0];
													if(Attr_Fields.split(";")[0].length() > 0)
														BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Fields.split(";")[2], Attr_Fields.split(";")[1], Attr_Fields.split(";")[0], SourceValue.split(";")[1], BalanceValue, Attr_Fields.split(";")[5]));										
												});
											}
											if(ValidGroupBalanceOfferAttr.stream().filter(item->item.startsWith("M")).count() == 1){
												for(String s : FinalAttrList)
												{
													//ChatnCall Status;1758;1|3034;ChatnCallStatus;4;;1970-01-01 04:00:00;2119891998]
													String TargetValue = s.split("\\|")[1];
													String SourceValue = s.split("\\|")[0];
													if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M") != null)
													{
														//String Attr_Fields = item.split("\\|")[1];
														String Attr_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getAttrOfferId();
														String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getSymbols();
														String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getBTValue();
														String Attr_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getAttrType();
														String Attr_Name = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getAttrName();
														String BalanceValue = SourceValue.split(";")[2];
														
														if(Attr_ID.length() > 0 && Symbol.equals(">") && Integer.parseInt(BalanceValue) > Integer.parseInt(BT_VALUE))
														{
															//String OffAttr_Type,String OffAttr_Name, String OffAttr_ID, String Balance_ID, String Balance_Value, String BEBucketID)
															BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
														}
														if(Attr_ID.length() > 0 && Symbol.equals(">=") && Integer.parseInt(BalanceValue) >= Integer.parseInt(BT_VALUE))
														{
															//String OffAttr_Type,String OffAttr_Name, String OffAttr_ID, String Balance_ID, String Balance_Value, String BEBucketID)
															BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
														}
														if(Attr_ID.length() > 0 && Symbol.equals("=") && Integer.parseInt(BalanceValue) == Integer.parseInt(BT_VALUE))
														{
															//String OffAttr_Type,String OffAttr_Name, String OffAttr_ID, String Balance_ID, String Balance_Value, String BEBucketID)
															BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
														}
														else if( Attr_ID.length() > 0 && Symbol.equals("or"))
														{
															//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
															String[] values = BT_VALUE.split("#");											
															if(Arrays.stream(values).anyMatch(BalanceValue::equals))
															{
																BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Type, Attr_Name, Attr_ID, SourceValue.split(";")[1], BalanceValue, TargetValue.split(";")[5]));
															}
														}
													}
												}
											}
											//CompletedBT_ID.addAll(CurrentGroupBalance);
										}
									}										
								}
							}
							if(FinalGroupName.startsWith("D-") && ValidGroupBalanceOfferAttr.size() != 0)
							{
								if(ValidGroupBalanceOfferAttr.size() == CurrentGroupBalance.size())
								{
									FinalAttrList.forEach(item->{
										String Attr_Fields = item.split("\\|")[1];
										String BalanceValue = item.split("\\|")[0].split(";")[2];
										String SourceValue = item.split("\\|")[0];
										
										BalanceOffAttrList.add(PopulateOfferAttribute(Attr_Fields.split(";")[2], Attr_Fields.split(";")[1], Attr_Fields.split(";")[0], SourceValue.split(";")[1], BalanceValue, Attr_Fields.split(";")[5]));										
									});								
								}
							}
							//Add code here
						}
						else
						{	CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							//onlyLog.add("INC4002:Balance_Type lookup failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
						}
					}
				}
			}
		}
		
		//return BalanceOffAttrList;
		return BalanceOffAttrList.stream().distinct().collect(Collectors.toList());
	}
	
	private String PopulateOfferAttribute(String OffAttr_Type,String OffAttr_Name, String OffAttr_ID, String Balance_ID, String Balance_Value, String BEBucketID)
	{
		StringBuilder sb = new StringBuilder();
		String OfferAttr_Defination = "";
		if(Integer.parseInt(OffAttr_Type) == 4 || Integer.parseInt(OffAttr_Type) == 1 || Integer.parseInt(OffAttr_Type) == 3)
			OfferAttr_Defination = LoadSubscriberMapping.OfferAttrDefMap.get(OffAttr_ID + ',' + OffAttr_Name);
			
		try {
			if(OfferAttr_Defination == null)
				onlyLog.add("INC4010:BT mapped attribute Lookup failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + BEBucketID +  ":ATTR_NAME=" + OffAttr_Name + ":ACTION=Logging");
			else
			{				
				sb.append(msisdn).append(",");
				sb.append(OffAttr_ID).append(",");
				sb.append(OfferAttr_Defination).append(",");
				if(Integer.parseInt(OffAttr_Type) == 4 || Integer.parseInt(OffAttr_Type) == 1)
					sb.append(CommonUtilities.toHexadecimal(Balance_Value)).append(",");
				else if(Integer.parseInt(OffAttr_Type) == 3)
					sb.append(CommonUtilities.toOfferAttrType3(Balance_Value)).append(",");
				String Product_ID = commonfunction.GetProductIDCreation(new HashSet<>(Arrays.asList(BEBucketID)));
				if(Product_ID.length() != 0)
					sb.append(Product_ID);
				else
					sb.append("0");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	private Map<String,Map<String,List<String>>> populateAMGroupResult(String balance_ID, String balance_Value,String START_DATE, String EXPIRY_DATE, String bebucketid, Set<String> CompletedBT_ID) 
	{
		List<String> BalanceOfferList = new ArrayList<>();
		List<String> AMCompleted_ID = new ArrayList<>();
		Map<String,Map<String,List<String>>> GroupOfferMap = new HashMap<>();
		
		Map<String,String> AMGroupBTs = new HashMap<>();
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo TempbalanceInput :  SortedBalanceInput){
			String TempBalance_ID = TempbalanceInput.getBALANCETYPE();
			String TempBalance_Value = TempbalanceInput.getBEBUCKETVALUE();
			String TempBalance_ExpiryDate = TempbalanceInput.getBEEXPIRY();
			//CurrentGroupBalanceID.put(TempBalance_ID + "," + TempBalance_Value + "," + TempBalance_ExpiryDate, TempbalanceInput.getBEBUCKETID());
			
			if(CompletedBT_ID.contains(TempbalanceInput.getBEBUCKETID()))
				continue;
			if(TempBalance_ExpiryDate.equals(EXPIRY_DATE) && LoadSubscriberMapping.UniqueBalanceOnlyAMGroupMap.contains(TempBalance_ID))
			{
				AMGroupBTs.put(TempbalanceInput.getBEBUCKETID(), TempBalance_ID + "|" + TempBalance_Value);
			}
		}
		
		
		Map<String,List<String>> AMGroupOfferMap = ComputeAMGroup(AMGroupBTs, CompletedBT_ID);
		
		//Now Populate the MGroup
		if(AMGroupOfferMap.size() != 0)
		{
			for(Entry<String, List<String>> item : AMGroupOfferMap.entrySet())
			{
				String AMGroupName = item.getKey().split("\\|")[0];
				List<String> UniqueBTID = Arrays.asList(item.getKey().split("\\|")[1].split(",")); 
				List<String> ValidAMGroupBalanceOffer = item.getValue();
				
				int i = 1;
				Set<String> CompletedAMBT = new HashSet<>();
				for(String Str : ValidAMGroupBalanceOffer)
				{
					String SourceOffer = Str.split("\\|")[0];
					String TargetOffer = Str.split("\\|")[1];
					AMCompleted_ID.add(TargetOffer.split(";")[5]);												
				}													
			}
		}
		
		Map<String,List<String>> AMOutputDetails = new HashMap<>();
		if(AMCompleted_ID.size() != 0)
			AMOutputDetails.put("CompletedBT", AMCompleted_ID);
		if(BalanceOfferList.size() != 0)
			AMOutputDetails.put("Offer", BalanceOfferList);
		
		
		GroupOfferMap.put("AMOutputDetails",AMOutputDetails);
		
		return GroupOfferMap;
	}
	
	public Map<String,List<String>> ComputeAMGroup(Map<String, String> AMBalanceBT, Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub
	
		String FinalGroupName ="";
		boolean BestMatchFound = false;
		Map<String,Set<String>> BestMatch = new ConcurrentHashMap<>(1000, 0.75f, 30);
		Set<String> A_currentGroup = new HashSet<>();
		
		Set<Integer> UniqueAMBT = new TreeSet<Integer>();
		
		for(Entry<String,String> item:  AMBalanceBT.entrySet())
		{
			UniqueAMBT.add(Integer.parseInt(item.getValue().split("\\|")[0].trim()));
		}
		// find the AM group	 
		
		List<String> strings = UniqueAMBT.stream().map(Object::toString)
                .collect(Collectors.toList());
		String AMBTValue = String.join(",", strings);
		Set<String> AMGroupItems = new HashSet<>();
		AMGroupItems.addAll(commonfunction.getAMGroupKey(LoadSubscriberMapping.BalanceOnlyAMGroupMap,UniqueAMBT));
		
		for(String individualGroup : AMGroupItems)
		{
			//for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
			{
				//if(AMBalanceBT.containsKey(balanceInput.getBEBUCKETID()))
				{
					int i = 0;
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
				}
			}
			if(A_currentGroup.size() == UniqueAMBT.size())
			{
				BestMatchFound = true;
				break;
			}
			else
				BestMatch.put(FinalGroupName, A_currentGroup);
		}
		
		if(!BestMatchFound && BestMatch.size() > 1)
			FinalGroupName = Collections.max(BestMatch.entrySet(), (entry1, entry2) -> entry1.getValue().size() - entry2.getValue().size()).getKey();
	
		List<String> GroupBalanceOfferAttr = new ArrayList<>();
		Map<String,List<String>> AMGroupOfferMap = new HashMap<>();
		if(FinalGroupName.length() != 0)
		{	
			boolean ExtraOfferFlag = false;
			for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
			{
				String TempBalance_ID = balanceInput.getBALANCETYPE();
				if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName) != null)
				{
					if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
						continue;
				
					if(AMBalanceBT.containsKey(balanceInput.getBEBUCKETID()))
					{
						String OffAttr_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAttrOfferId();
						String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getSymbols();
						String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTValue();
						String OffAttr_Name = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAttrName();
						String OffAttr_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAttrType();
						
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						GroupBalanceOfferAttr.add(balanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + balanceInput.getBEBUCKETVALUE() +"|" + OffAttr_ID + ";" + OffAttr_Name + ";" + OffAttr_Type + ";" +  balanceInput.getBEBUCKETSTARTDATE() + ";" + balanceInput.getBEEXPIRY() + ";" + balanceInput.getBEBUCKETID());
					}					
				}
			}			
			AMGroupOfferMap.put(FinalGroupName + "|" + AMBTValue, GroupBalanceOfferAttr);
		}
		
		return AMGroupOfferMap;
	}
	
	public Map<String,List<String>> ComputeASGroup(String inputBalance_ID, Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub Map<String,List<String>>
		
		String FinalGroupName ="";		
		List<String>AllAvailableGroup = new ArrayList<>();	
		List<String> GroupBalanceOfferAttr = new ArrayList<>();
		Set<String> ASGroupItems = new HashSet<>();
		boolean ExtraOfferFlag = false;
		Map<String,List<String>> ASGroupOfferMap = new HashMap<>();
		FinalGroupName = (commonfunction.getASGroupKey(LoadSubscriberMapping.BalanceOnlyASGroupMap,inputBalance_ID)).trim();

		//String temp1 = LoadSubscriberMapping.BalanceOnlyASGroupMap.get(FinalGroupName);
		ASGroupItems = Arrays.stream(LoadSubscriberMapping.BalanceOnlyASGroupMap.get(FinalGroupName).split(",")).collect(Collectors.toSet()); 
		
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo TempbalanceInput : SortedBalanceInput)
		{
			String TempBalance_ID = TempbalanceInput.getBALANCETYPE();
			String TempBalance_Value = TempbalanceInput.getBEBUCKETVALUE();
			//CurrentGroupBalanceID.put(TempBalance_ID + "," + TempBalance_Value + "," + TempBalance_ExpiryDate, TempbalanceInput.getBEBUCKETID());
			
			if(CompletedBT_ID.contains(TempbalanceInput.getBEBUCKETID()))
				continue;
			if(ASGroupItems.contains(TempBalance_ID))
			{
				if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName) != null)
				{							
					String OffAttr_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAttrOfferId();
					String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getSymbols();
					String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTValue();
					String OffAttr_Name = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAttrName();
					String OffAttr_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAttrType();
					
					CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
					
					if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
					{
						GroupBalanceOfferAttr.add(TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + OffAttr_ID + ";" + OffAttr_Name + ";" + OffAttr_Type + ";" +  TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY() + ";" + TempbalanceInput.getBEBUCKETID());
						continue;
					}
					else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
					{						
						GroupBalanceOfferAttr.add(TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + OffAttr_ID + ";" + OffAttr_Name + ";" + OffAttr_Type + ";" +  TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY() + ";" + TempbalanceInput.getBEBUCKETID());
						continue;
					}
					else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
					{
						GroupBalanceOfferAttr.add(TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + OffAttr_ID + ";" + OffAttr_Name + ";" + OffAttr_Type + ";" +  TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY() + ";" + TempbalanceInput.getBEBUCKETID());
						continue;
					}
					else if(Symbol.equals("or"))
					{
						//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
						String[] values = BT_Value.split("#");											
						if(Arrays.stream(values).anyMatch(TempBalance_Value::equals))
						{
							GroupBalanceOfferAttr.add(TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + OffAttr_ID + ";" + OffAttr_Name + ";" + OffAttr_Type + ";" +  TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY() + ";" + TempbalanceInput.getBEBUCKETID());
							continue;
						}																					
					}
				}
			}
		}
		String temp = String.join(",", ASGroupItems);
		ASGroupOfferMap.put(FinalGroupName +"|" + temp, GroupBalanceOfferAttr);
		return ASGroupOfferMap;
	}
	
	private List<String> offerAttributeFromDefaultService(String msisdn)
	{
		List<String> defaultOfferList = new ArrayList<>();
		
		/*for (String string : LoadSubscriberMapping.DefaultServicesMap.keySet()) 
		{
			
		}*/	
		if(subscriber.getProfiledumpInfoList().size() == 1)
		{
		
			if(subscriber.getProfiledumpInfoList().get(0).getIMEI().length() == 0 || subscriber.getProfiledumpInfoList().get(0).getIMSI().length() == 0 || subscriber.getProfiledumpInfoList().get(0).getHLRAddress().length() == 0 || subscriber.getProfiledumpInfoList().get(0).getLanguageID().length() == 0)
			{
				onlyLog.add("INC2001:Subscriber Profile field Missing:MSISDN=" + msisdn + ":IMEI=" + subscriber.getProfiledumpInfoList().get(0).getIMEI() + ":IMSI=" + subscriber.getProfiledumpInfoList().get(0).getIMSI() + ":HLRAddress=" + subscriber.getProfiledumpInfoList().get(0).getHLRAddress() + ":LanguageID=" + subscriber.getProfiledumpInfoList().get(0).getLanguageID() +":ACTION=Logging");
			}
			
			for (Map.Entry<String, String> entry : LoadSubscriberMapping.DefaultServicesMap.entrySet())
			{
				String v = entry.getValue();
				
				if (v.split(",",-1)[1].trim().equals("N"))
				{
					if (!v.split(",",-1)[11].isEmpty() && v.split(",",-1)[11].length() != 0)
					{
						String OfferAttr_ID = v.split(",",-1)[11];
						String Attr_name = v.split(",",-1)[12];
						String Attr_type = v.split(",",-1)[13];
						String ProfileTag = v.split(",",-1)[14].split("\\.")[0];
						
						/*if(ProfileTag.equals("entPDIntNum"))
							System.out.println("Vipin");*/
						
						String Attr_value = "";
						String OfferAttr_Defination = "";

						Attr_value =  profileTag.GetProfileTagValue(ProfileTag);
						StringBuffer sb = new StringBuffer();	
						if(Integer.parseInt(Attr_type) == 4)
						{
							OfferAttr_Defination = LoadSubscriberMapping.OfferAttrDefMap.get(OfferAttr_ID + ',' + Attr_name);
							if(OfferAttr_Defination == null && !Attr_value.isEmpty())
								onlyLog.add("INC3001:Default service mapped attribute lookup failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + ProfileTag + ":PROFILE_TAG_VALUE=" + Attr_value + ":ATTR_NAME=" + Attr_name + ":ACTION=Logging");
							else
							{
								if(Attr_value.length() !=0 && OfferAttr_Defination != null)
								{
									try {	
										sb.append(msisdn).append(",");
										sb.append(OfferAttr_ID).append(",");
										sb.append(OfferAttr_Defination).append(",");
										sb.append(CommonUtilities.toHexadecimal(Attr_value)).append(",");
										sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									defaultOfferList.add(sb.toString());
								}
							}
						}
						if(Integer.parseInt(Attr_type) == 1)
						{
							String[] tagNames = Attr_value.split("#");
							int i = 0;
							for(String s : tagNames)
							{
								OfferAttr_Defination = LoadSubscriberMapping.OfferAttrDefMap.get(OfferAttr_ID + ',' + Attr_name);
								
								if(OfferAttr_Defination == null && !Attr_value.isEmpty())
									onlyLog.add("INC3001:Default service mapped attribute lookup failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + ProfileTag + ":PROFILE_TAG_VALUE=" + Attr_value + ":ATTR_NAME=" + Attr_name + ":ACTION=Logging");
								else
								{
									if(Attr_value.length() !=0 && OfferAttr_Defination != null)
									{
										try {
											i++;
											sb.append(msisdn).append(",");
											sb.append(OfferAttr_ID).append(",");
											sb.append(OfferAttr_Defination).append(",");
											sb.append(CommonUtilities.toHexadecimal(s)).append(",");
											sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
											if(i < tagNames.length)
												sb.append("\n");
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
									}
								}
							}
							defaultOfferList.add(sb.toString());
						}
						sb = null;
					}
				}
			}
			/*LoadSubscriberMapping.DefaultServicesMap.forEach((k,v)->{
			});*/
		}
		
		return defaultOfferList;	
	}
	
	private Collection<? extends String> offerAttributeFromProfileMapping(String msisdn) {

		List<String> ProfileTagOfferAttrList = new ArrayList<>();
		Date currDate = new Date();
		SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if(subscriber.getProfiledumpInfoList().size() == 0)
			return ProfileTagOfferAttrList;
		
		boolean OfferAttr4805Created = true;
		//boolean OfferAttr1201Created = true;
		
		for(String itr : LoadSubscriberMapping.Profile_Tags_Mapping.keySet())
		{
			PROFILETAGINFO profileMappingValue = LoadSubscriberMapping.Profile_Tags_Mapping.get(itr);
			String Symbol = profileMappingValue.getSymbols();
			String TargetValue = profileMappingValue.getProfileTagValue();
			String IgnoreFlag =  profileMappingValue.getIgnoreFlag();
			if(IgnoreFlag.equals("N"))
			{				
				if(LoadSubscriberMapping.ProfileTagName.contains(itr))
				{	
					String Profile_Value = profileTag.GetProfileTagValue(itr);
					
					if(Profile_Value.isEmpty() || profileMappingValue.getAttr1OfferId().isEmpty())
						continue;
					
					if(profileMappingValue.getSubState().isEmpty())
					{						
						if(Symbol.equals("="))
						{
							String OfferAttr_Defination = "";
							if(Integer.parseInt(profileMappingValue.getAttr1Type()) == 4)
								OfferAttr_Defination = LoadSubscriberMapping.OfferAttrDefMap.get(profileMappingValue.getAttr1OfferId() + ',' + profileMappingValue.getAttr1Name());
							
							try {
								if(OfferAttr_Defination == null)
									onlyLog.add("INC6004:Profile Tag mapped attribute Lookup failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ATTR_NAME=" + profileMappingValue.getAttr1Name() + ":ACTION=Logging");
								else
									ProfileTagOfferAttrList.add(msisdn+ "," + profileMappingValue.getAttr1OfferId() + "," + OfferAttr_Defination + "," + CommonUtilities.toHexadecimal(Profile_Value) + "," +  LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
							} 
							catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					else if(profileMappingValue.getSubState().equals(subscriber.getSubscriberInfoSERVICESTATE()))
					{
						if(Symbol.equals("="))
						{
							if(TargetValue.equals(profileTag.GetProfileTagValue(itr)))
							{
								String OfferAttr_Defination = "";
								if(Integer.parseInt(profileMappingValue.getAttr1Type()) == 1)
									OfferAttr_Defination = LoadSubscriberMapping.OfferAttrDefMap.get(profileMappingValue.getAttr1OfferId() + ',' + profileMappingValue.getAttr1Name());
								
								try {
									if(OfferAttr_Defination == null)
										onlyLog.add("INC6004:Profile Tag mapped attribute Lookup failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ATTR_NAME=" + profileMappingValue.getAttr1Name() + ":ACTION=Logging");
									else
										ProfileTagOfferAttrList.add(msisdn+ "," + profileMappingValue.getAttr1OfferId() + "," + OfferAttr_Defination + "," + CommonUtilities.toHexadecimal(Profile_Value) + "," +  LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
								} 
								catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}					
				}
				if(itr.equals("bdgtCntrlTopUp") || itr.equals("NewTopUpValue"))
				{
					String Profile_Value = profileTag.GetProfileTagValue(itr);
					if(Symbol.equals("!="))
					{
						if(Profile_Value.length() > 0)
						{
							if(!profileMappingValue.getAttr1OfferId().isEmpty())
							{
								String OfferAttr_Defination = LoadSubscriberMapping.OfferAttrDefMap.get(profileMappingValue.getAttr1OfferId() + ',' + profileMappingValue.getAttr1Name());
								if(Integer.parseInt(profileMappingValue.getAttr1Type()) == 4 )
								{
									
									try 
									{
										if(OfferAttr_Defination == null)
											onlyLog.add("INC6004:Profile Tag mapped attribute Lookup failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ATTR_NAME=" + profileMappingValue.getAttr1Name() + ":ACTION=Logging");
										else
										{
											ProfileTagOfferAttrList.add(msisdn+ "," + profileMappingValue.getAttr1OfferId() + "," + OfferAttr_Defination + "," + CommonUtilities.toHexadecimal(Profile_Value) + "," +  LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
										}
									} 
									catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								if(Integer.parseInt(profileMappingValue.getAttr1Type()) == 1)
								{
									try 
									{
										if(OfferAttr_Defination == null)
											onlyLog.add("INC6004:Profile Tag mapped attribute Lookup failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ATTR_NAME=" + profileMappingValue.getAttr1Name() + ":ACTION=Logging");
										else
										{
											String[] OfferAttr = Profile_Value.split("#");
											for(String OffAttrValue : OfferAttr)
											{
												ProfileTagOfferAttrList.add(msisdn+ "," + profileMappingValue.getAttr1OfferId() + "," + OfferAttr_Defination + "," + CommonUtilities.toHexadecimal(OffAttrValue) + "," +  LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
											}										
										}
									} 
									catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								if(Integer.parseInt(profileMappingValue.getAttr1Type()) == 3)
								{									
									try 
									{
										if(OfferAttr_Defination == null)
											onlyLog.add("INC6004:Profile Tag mapped attribute Lookup failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ATTR_NAME=" + profileMappingValue.getAttr1Name() + ":ACTION=Logging");
										else
										{
											ProfileTagOfferAttrList.add(msisdn+ "," + profileMappingValue.getAttr1OfferId() + "," + OfferAttr_Defination + "," + CommonUtilities.toOfferAttrType3(Profile_Value) + "," +  LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
										}
									} 
									catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								//ProfileTagOfferList.add(populateProfileOffer(profileMappingValue.getOfferId(),"","",profileMappingValue.getProductPrivate(),itr,Profile_Value));
							}
						}
						else if(Profile_Value.length() > 0)
							onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE="+ Profile_Value + ":ACTION=Logging");
							
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
						
			if(IgnoreFlag.equals("N"))
			{
								
				//***************logic for Dummy1, dummy2 and Dummy3
				String Profile_Value = profileTag.GetProfileTagValue(itr.split(",")[0]);
				
				if(TargetName.equals("NewPPBundle") && Profile_Value.length() !=0)					
				{
					if(profileMappingValue.getSubState().equals(subscriber.getSubscriberInfoSERVICESTATE()))
					{
						String[] values = {"Monthly1","Monthly2","Monthly3","Monthly4","Monthly5","Monthly6","Monthly7","Monthly8","Monthly9","Monthly10","Daily1"};
						if(Symbol.equals("="))
						{	
							if(TargetValue.equals(Profile_Value))
							{
								String OfferAttr_Defination = null;
								if(Integer.parseInt(profileMappingValue.getAttr1Type()) == 1)
									OfferAttr_Defination = LoadSubscriberMapping.OfferAttrDefMap.get(profileMappingValue.getAttr1OfferId() + ',' + profileMappingValue.getAttr1Name());
								
								try {
									if(OfferAttr_Defination == null)
										onlyLog.add("INC6004:Profile Tag mapped attribute Lookup failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ATTR_NAME=" + profileMappingValue.getAttr1Name() + ":ACTION=Logging");
									else
										ProfileTagOfferAttrList.add(msisdn+ "," + profileMappingValue.getAttr1OfferId() + "," + OfferAttr_Defination + "," + CommonUtilities.toHexadecimal(Profile_Value) + "," +  LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
								} 
								catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}	
						}
					}
				}
				
				if(TargetName.equals("OWFnFLabel") || TargetName.equals("OneWayFnF") || TargetName.equals("OneWayFnFModCount"))					
				{
					if(!Profile_Value.isEmpty())
					{
						String OfferAttr_Defination = null;
						
						if(TargetName.equals("OWFnFLabel") || TargetName.equals("OneWayFnF"))
						{
							if(Integer.parseInt(profileMappingValue.getAttr1Type()) == 4 )
							{
								OfferAttr_Defination = LoadSubscriberMapping.OfferAttrDefMap.get(profileMappingValue.getAttr1OfferId() + ',' + profileMappingValue.getAttr1Name());
								
								
								try {
									if(OfferAttr_Defination == null)
										onlyLog.add("INC6004:Profile Tag mapped attribute Lookup failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ATTR_NAME=" + profileMappingValue.getAttr1Name() + ":ACTION=Logging");
									else
									{
										OfferAttr4805Created = false;
										ProfileTagOfferAttrList.add(msisdn+ "," + profileMappingValue.getAttr1OfferId() + "," + OfferAttr_Defination + "," + CommonUtilities.toHexadecimal(Profile_Value) + "," +  LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
									}
								} 
								catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							if(Integer.parseInt(profileMappingValue.getAttr1Type()) == 1)
							{
								OfferAttr_Defination = LoadSubscriberMapping.OfferAttrDefMap.get(profileMappingValue.getAttr1OfferId() + ',' + profileMappingValue.getAttr1Name());
								
								
								try {
									if(OfferAttr_Defination == null)
										onlyLog.add("INC6004:Profile Tag mapped attribute Lookup failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ATTR_NAME=" + profileMappingValue.getAttr1Name() + ":ACTION=Logging");
									else
									{
										String[] OfferAttr = Profile_Value.split("#");
										for(String OffAttrValue : OfferAttr)
										{
											OfferAttr4805Created = false;
											ProfileTagOfferAttrList.add(msisdn+ "," + profileMappingValue.getAttr1OfferId() + "," + OfferAttr_Defination + "," + CommonUtilities.toHexadecimal(OffAttrValue) + "," +  LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
										}										
									}
								} 
								catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						
						if(TargetName.equals("OneWayFnFModCount"))
						{
							//concat('01',dm:int-to-hex($val,64),dm:int-to-hex(dm:power(xs:integer($DEFAULT_VALUE_TEN),$decimals),64))"/>
							
							if(Integer.parseInt(profileMappingValue.getAttr1Type()) == 3)
								OfferAttr_Defination = LoadSubscriberMapping.OfferAttrDefMap.get(profileMappingValue.getAttr1OfferId() + ',' + profileMappingValue.getAttr1Name());
							
							try {
								if(OfferAttr_Defination == null)
									onlyLog.add("INC6004:Profile Tag mapped attribute Lookup failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ATTR_NAME=" + profileMappingValue.getAttr1Name() + ":ACTION=Logging");
								else
								{
									OfferAttr4805Created = false;
									ProfileTagOfferAttrList.add(msisdn+ "," + profileMappingValue.getAttr1OfferId() + "," + OfferAttr_Defination + "," + CommonUtilities.toOfferAttrType3(Profile_Value) + "," +  LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
								}
							} 
							catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					}
				}
			}			
		}		
		return ProfileTagOfferAttrList;
	}	
}