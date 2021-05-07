package com.ericsson.dm.transform.implementation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.OnlyOnceErrorHandler;

import com.ericsson.dm.Utils.CommonUtilities;
import com.ericsson.dm.inititialization.LoadSubscriberMapping;
import com.ericsson.dm.transformation.ExecuteTransformation;
import com.ericsson.jibx.beans.SubscriberXml;
import com.ericsson.jibx.beans.PROFILETAGLIST.PROFILETAGINFO;
import com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo;

public class Accumulator implements Comparator<SchemasubscriberbalancesdumpInfo> {
	SubscriberXml subscriber;
	String msisdn;
	String INITIAL_ACTIVATION_DATE;
	private int indx;
	final static Logger LOG = Logger.getLogger(DedicatedAccount.class);
	
	Set<String> rejectAndLog;
	Set<String> onlyLog;
	private Map<String, Set<String>> ProductIDLookUpMap;
	private Map<String, Integer> ProfileProductIDLookUpMap;
	
	//String[] OnlyUA = {"1452","1693","1892","1449","1448","1498","2593","1323","3407","3341","3403","3317","3319","3320","63","1188"};
	
	String[] UA1RuleBT = {"3568", "3318"};
	
	CommonFunctions commonfunction;
	ProfileTagProcessing profileTag;	
	public CopyOnWriteArrayList<SchemasubscriberbalancesdumpInfo> SortedBalanceInput;
	
	public Accumulator()
	{
		
	}
	public Accumulator(SubscriberXml subs,Set<String> rejectAndLog, Set<String> onlyLog, String INITIAL_ACTIVATION_DATE,Map<String, Set<String>> ProductIDLookUpMap, Map<String, Integer> ProfileProductIDLookUpMap) {
		
		// TODO Auto-generated constructor stub
		this.subscriber=subs;
		this.rejectAndLog=rejectAndLog;
		this.onlyLog=onlyLog;
		this.indx = 1;	
		SortedBalanceInput = new CopyOnWriteArrayList<>();
		this.INITIAL_ACTIVATION_DATE = INITIAL_ACTIVATION_DATE;
		this.ProductIDLookUpMap = ProductIDLookUpMap;
		profileTag = new ProfileTagProcessing(subscriber,this.onlyLog);
		commonfunction = new CommonFunctions(subscriber,ProductIDLookUpMap, this.onlyLog);
		this.ProfileProductIDLookUpMap = ProfileProductIDLookUpMap;
	}
	
	@Override
	public int compare(SchemasubscriberbalancesdumpInfo o1, SchemasubscriberbalancesdumpInfo o2) {
		int value1 = (o2.getBEEXPIRY()).compareTo((o1.getBEEXPIRY()));
        if (value1 == 0) {
        	return  o2.getBEBUCKETID().compareTo(o1.getBEBUCKETID());
        }
        return value1;
	}
	
	public Map<String,String> execute() {
		// TODO Auto-generated method stub
		//msisdn = subscriber.getSubscriberInfoMSISDN();
		SortedBalanceInput.addAll(subscriber.getBalancesdumpInfoList());
		Collections.sort(SortedBalanceInput,new Offer());
		
		Map<String, String> ACMmap = new HashMap<>();
		ACMmap.putAll(generateACMFromProductMapping());
		ACMmap.putAll(generateACMFromProfileTag());
		ACMmap.putAll(generateACMFromDefaultService());
		ACMmap.putAll(generateACMLifeCycle());
		ACMmap.putAll(generateACMNPPLifeCycle());
		
		SortedBalanceInput.clear();
		return ACMmap;
	}
	
	private Map<String,String> generateACMLifeCycle()
	{
		Map<String,String> AccMap = new HashMap<>();
		
		String serviceClass = subscriber.getSubscriberInfoSERVICESTATE();
		String INITIAL_ACTIVATION_DATE_FLAG;
				
		if(INITIAL_ACTIVATION_DATE.length() == 0)
			INITIAL_ACTIVATION_DATE_FLAG = "Y";
		else
			INITIAL_ACTIVATION_DATE_FLAG = "N";
		
		String ACM_ID = "", ACM_Balance = "";
		
		ACM_ID = LoadSubscriberMapping.LifeCycleMap.get(serviceClass+"|"+INITIAL_ACTIVATION_DATE_FLAG +"|N").split(",",-1)[13];
		ACM_Balance = LoadSubscriberMapping.LifeCycleMap.get(serviceClass+"|"+INITIAL_ACTIVATION_DATE_FLAG +"|N").split(",",-1)[14];
		
		if(ACM_ID.length() > 0)
		{
			AccMap.put("ID_" + indx, ACM_ID);
			AccMap.put("VALUE_" + indx, ACM_Balance);
			AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
			
			this.indx++;
		}
		
		return AccMap;
	}
	
	private Map<String,String> generateACMNPPLifeCycle() {
		Map<String,String> AccMap = new HashMap<>();
		
		String serviceClass = subscriber.getSubscriberInfoSERVICESTATE();
		String INITIAL_ACTIVATION_DATE_FLAG;
				
		if(INITIAL_ACTIVATION_DATE.length() == 0)
			INITIAL_ACTIVATION_DATE_FLAG = "Y";
		else
			INITIAL_ACTIVATION_DATE_FLAG = "N";
		
		String NPP_CCS_ACCT_TYPE_ID = LoadSubscriberMapping.LifeCycleMap.get(serviceClass+"|"+INITIAL_ACTIVATION_DATE_FLAG +"|N").split(",",-1)[15];
		String[] values = NPP_CCS_ACCT_TYPE_ID.split("#");
		
		if(Arrays.stream(values).anyMatch(subscriber.getSubscriberInfoCCSACCTTYPEID()::equals))
		{	
			String NPPResult =  commonfunction.FindNPPPBTItem(subscriber.getSubscriberInfoCCSACCTTYPEID(), serviceClass);
			if(!NPPResult.isEmpty())
			{
				String SerialNumber = NPPResult.split("\\|",-1)[0];
				String BTExpiryDate = NPPResult.split("\\|",-1)[1];
				String BTStartDate = NPPResult.split("\\|",-1)[2];
				
				if(!SerialNumber.equals("0"))
				{
					if(!LoadSubscriberMapping.NPPLifeCycleMap.get(SerialNumber).getUA().isEmpty())
					{
						String[] AccValue = LoadSubscriberMapping.NPPLifeCycleMap.get(SerialNumber).getUA().split("#");
					
						for(String s : AccValue)
						{
							AccMap.put("ID_" + indx, s.split("-")[0]);
							if(s.split("-")[1].equals("BT_Value"))
								AccMap.put("VALUE_" + indx, NPPResult.split("\\|",-1)[3]);
							else
								AccMap.put("VALUE_" + indx, s.split("-")[1]);
							if(s.split("-")[2].equals("NEVER"))
								AccMap.put("CLEARING_DATE_" + indx,"1");
							else
								AccMap.put("CLEARING_DATE_" + indx,String.valueOf(CommonUtilities.convertDateToEpoch(s.split("-",-1)[2])));
							
							this.indx++;
						}
					}
				}
				else
				{
					AccMap.put("ID_" + indx, "60");
					AccMap.put("VALUE_" + indx, "0");
					AccMap.put("CLEARING_DATE_" + indx,"0");
					
					this.indx++;
					
					AccMap.put("ID_" + indx, "59");
					AccMap.put("VALUE_" + indx, "0");
					AccMap.put("CLEARING_DATE_" + indx,"0");
					
					this.indx++;
				}
			}			
		}		
		return AccMap;
	}
	
	private Map<String,String> generateACMFromDefaultService()
	{
		Map<String,String> AccMap = new HashMap<>();
		
		LoadSubscriberMapping.DefaultServicesMap.forEach((k,v)->{
			//System.out.println("Item : " + k + " Count : " + v);
			
			if (v.split(",",-1)[1].trim().equals("N"))
			{
				//System.out.println(v);
				if (v.split(",",-1)[27] != "" && v.split(",",-1)[27].length() != 0)
				{
					AccMap.put("ID_" + indx, v.split(",",-1)[27]);
					AccMap.put("VALUE_" + indx, v.split(",",-1)[28]);
					AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
					
					this.indx++;
				}
			}			
		});
		
		return AccMap;
	}

	private Map<? extends String, ? extends String> generateACMFromProductMapping() {
		Date currDate = new Date();
		SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Map<String,String> AccMap = new HashMap<>();
		
		Set<String> CompletedBT_ID = new HashSet<>();
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput :  SortedBalanceInput)
		{
			String Balance_ID = balanceInput.getBALANCETYPE();
			//System.out.println("Master Balance_ID: " + Balance_ID);
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			boolean BTPA14BalanceDummy = false;
			if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
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
				String AC_ID = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getUAID();
				String Symbol = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getSymbols();
				String BT_Value = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getBTValue();
				String UA_Value = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|" ).getUAValue();
				CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				if(!AC_ID.isEmpty())
				{					
					if(Symbol.isEmpty() && BT_Value.isEmpty())
					{
						AccMap.put("ID_" + indx, AC_ID);
						AccMap.put("VALUE_" + indx, Balance_Value);
						AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
						
						this.indx++;						
					}
					else
					{
						boolean GenerateUA = false;
						if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
							GenerateUA = true;
						else if(Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_Value))
							GenerateUA = true;
						else if(Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value))
							GenerateUA = true;
						else if(Symbol.equals("or"))
						{
							//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
							String[] values = BT_Value.split("#");
							
							if(Arrays.stream(values).anyMatch(Balance_Value::equals))
							{
								GenerateUA = true;
							}
						}
						else if(LoadSubscriberMapping.OnlyUAPresent.contains(Balance_ID))
							onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
						
						
						if(GenerateUA)
						{
							if(!LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|" ).getUAValue().equals("BT_Value"))
								Balance_Value = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|" ).getUAValue();
							
							String UABalance = balanceInput.getBEBUCKETVALUE();
							
							if(Arrays.stream(UA1RuleBT).anyMatch(Balance_ID::equals))
								UABalance = String.valueOf(Double.parseDouble(UABalance) * 30);
								
							AccMap.put("ID_" + indx, AC_ID);
							if(!UA_Value.isEmpty())
							{
								String[] UTValueList = UA_Value.split("-");
								if(UTValueList[0].equals("BT_Value"))
									AccMap.put("VALUE_" + indx, UABalance);
								else
									AccMap.put("VALUE_" + indx, UTValueList[0]);
								if(UTValueList[1].equals("MIGDATE"))
									AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
								else
									AccMap.put("CLEARING_DATE_" + indx,"1");
							}
							else
							{
								AccMap.put("VALUE_" + indx, UABalance);
								AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
							}
							this.indx++;
						}
					}
				}	
			}
			else
			{
				if(LoadSubscriberMapping.ExceptionBalances.contains(Balance_ID))
				{
					if(Balance_ID.equals("1832") || Balance_ID.equals("1387") || Balance_ID.equals("2112") || Balance_ID.equals("2432"))
					{
						if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
						{
							String ExtraUAValue = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getAddUA();
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());						
							
							if(!ExtraUAValue.isEmpty())
							{
								String[] ListofAddedUA = ExtraUAValue.split("#");
								
								for(int i = 0; i<ListofAddedUA.length; i++)
								{
									if(ListofAddedUA[i].length() > 1)
									{
										AccMap.put("ID_" + indx, ListofAddedUA[i].split("-")[0]);
										AccMap.put("VALUE_" + indx, ListofAddedUA[i].split("-")[1]);
										AccMap.put("CLEARING_DATE_" + indx,String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
										
										this.indx++;
										//trackLog.add("INC7003:Add_UC considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":UC_ID=" + UC_ID + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID()  + ":UC_VALUE=" + Balance +":ACTION=Logging");
									}											 
								}
							}
						}
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
					}				
					if(Balance_ID.equals("1266"))
					{
						/*if(Balance_Value.equals("0"))
						{
							String Offer_ID = LoadSubscriberMapping.Special1266BalanceDummy.get(Balance_ID + "|=").getOfferID();
							String Offer_Type = LoadSubscriberMapping.Special1266BalanceDummy.get(Balance_ID + "|=").getOfferType();
							boolean startFlag = LoadSubscriberMapping.Special1266BalanceDummy.get(Balance_ID + "|=").getOfferStartDate().length() > 0 ? true:false;
							boolean expiryFalg = LoadSubscriberMapping.Special1266BalanceDummy.get(Balance_ID + "|=").getOfferExpiryDate().length() > 0 ? true:false;
							BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"",""));								
						}*/
						if(Integer.parseInt(Balance_Value) > 0)
						{
							
							String ACCM_ID = LoadSubscriberMapping.Special1266BalanceDummy.get(Balance_ID + "|>").getUAID();
							String UA_Value = LoadSubscriberMapping.Special1266BalanceDummy.get(Balance_ID + "|>").getUAValue();
							
							if(ACCM_ID.length()>0)
							{
								AccMap.put("ID_" + indx, ACCM_ID);
								
								String[] UTValueList = UA_Value.split("-");
								if(UTValueList[0].equals("BT_Value"))
									AccMap.put("VALUE_" + indx, balanceInput.getBEBUCKETVALUE());
								else
									AccMap.put("VALUE_" + indx, UTValueList[0]);
								if(UTValueList[1].equals("MIGDATE"))
									AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
								else
									AccMap.put("CLEARING_DATE_" + indx,"1");
								
								this.indx++;
							}
						}
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
					}
					
					if(Balance_ID.equals("1239"))
					{
						for(String dummyValue : LoadSubscriberMapping.SpecialPA14BalanceDummy.keySet())
						{
							if(dummyValue.contains(Balance_ID))
							{
								String Symbol = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getSymbols();
								String BT_Value = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getBTValue();
								if(Balance_ID.equals("1239"))
								{
									if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value) && !BTPA14BalanceDummy)
									{
										String UA_ID = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue ).getUAID();
										String UA_Value = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getUAValue();
										if(!UA_ID.isEmpty())
										{
											BTPA14BalanceDummy = true;
											AccMap.put("ID_" + indx, UA_ID);
											String[] UTValueList = UA_Value.split("-");
											if(UTValueList[0].equals("BT_Value"))
												AccMap.put("VALUE_" + indx, balanceInput.getBEBUCKETVALUE());
											else
												AccMap.put("VALUE_" + indx, UTValueList[0]);
											if(UTValueList[1].equals("MIGDATE"))
												AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
											else
												AccMap.put("CLEARING_DATE_" + indx,"1");
											
											this.indx++;
											break;
										}
									}
									else if(Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value) && !BTPA14BalanceDummy)
									{
										String UA_ID = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue ).getUAID();
										String UA_Value = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getUAValue();
										if(!UA_ID.isEmpty())
										{
											BTPA14BalanceDummy = true;
											AccMap.put("ID_" + indx, UA_ID);
											String[] UTValueList = UA_Value.split("-");
											if(UTValueList[0].equals("BT_Value"))
												AccMap.put("VALUE_" + indx, balanceInput.getBEBUCKETVALUE());
											else
												AccMap.put("VALUE_" + indx, UTValueList[0]);
											if(UTValueList[1].equals("MIGDATE"))
												AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
											else
												AccMap.put("CLEARING_DATE_" + indx,"1");
											
											this.indx++;
											break;
										}
									}
								}								
							}
						}
					}
					
				}
				else if(LoadSubscriberMapping.BalanceOnlySpecialASGroupSet.contains(Balance_ID))
				{	
					Map<String,Map<String,Map<String,String>>> ASGroupOfferMap = new HashMap<>();
					ASGroupOfferMap = ComputeASpecialGroup(Balance_ID,CompletedBT_ID);
					
					Map<String,Map<String,String>> OutputDetails = new HashMap<>();
					OutputDetails = ASGroupOfferMap.get("AMOutputDetails");
					if(OutputDetails.containsKey("UA"))
					{											
						AccMap.putAll(OutputDetails.get("UA"));	
						Map<String,String> temp = new HashMap<>();
						temp = OutputDetails.get("CompletedBT");
						CompletedBT_ID.addAll(Arrays.asList(temp.get("BTID").split(",")));
					}
					continue;
				}
				else
				{
					String GroupName = "";
					List<String> CurrentGroupBalance = new ArrayList<>();
					List<String> UAList = new ArrayList<>();
					List<String> FinalUAList = new ArrayList<>();
					boolean ExtraAccumFlag = false;
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
							if(GroupName.startsWith("E-"))
							{
								GroupName = "E-1";
								CurrentGroupBalance.add("3288");
								CurrentGroupBalance.add("3289");
								CurrentGroupBalance.add("3295");
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
											String AC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getUAID();
											String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getSymbols();
											String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTValue();
											//String AC_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getUAType();
											String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTTYPE();
											String UA_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getUAValue();
											
											
											String ExtraAccmValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAddUA();
											
											if(!ExtraAccmValue.isEmpty())
											{
												ExtraAccumFlag = true;
											}
											else
											{
												ExtraAccmValue = "";
											}
											
											
											if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
											{
												CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
												if(!UA_Value.contains("BT_Value") && !UA_Value.isEmpty())
													TempBalance_Value = UA_Value.split("-")[0];
												UAList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + AC_ID + ";" + UA_Value + ";" + TempBalance_ExpiryDate + ";" + TempbalanceInput.getBEBUCKETID() + ";" + ExtraAccmValue);
												if(!AC_ID.isEmpty())
													FinalUAList.add(TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + AC_ID + ";" + UA_Value + ";" + TempBalance_ExpiryDate + ";" + TempbalanceInput.getBEBUCKETID() + ";" + ExtraAccmValue);
												break;
											}
											else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
											{
												CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
												if(!UA_Value.contains("BT_Value") && !UA_Value.isEmpty())
													TempBalance_Value = UA_Value.split("-")[0];
												UAList.add(BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + AC_ID + ";" + UA_Value + ";" + TempBalance_ExpiryDate + ";" + TempbalanceInput.getBEBUCKETID() + ";" + ExtraAccmValue);
												if(!AC_ID.isEmpty())
													FinalUAList.add(TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + AC_ID + ";" + UA_Value + ";" + TempBalance_ExpiryDate + ";" + TempbalanceInput.getBEBUCKETID() + ";" + ExtraAccmValue);
												break;
											}
											else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
											{
												CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
												if(!UA_Value.contains("BT_Value") && !UA_Value.isEmpty())
													TempBalance_Value = UA_Value.split("-")[0];
												UAList.add(BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + AC_ID + ";" + UA_Value + ";" + TempBalance_ExpiryDate + ";" + TempbalanceInput.getBEBUCKETID() + ";" + ExtraAccmValue);
												if(!AC_ID.isEmpty())
													FinalUAList.add(TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + AC_ID + ";" + UA_Value + ";" + TempBalance_ExpiryDate + ";" + TempbalanceInput.getBEBUCKETID() + ";" + ExtraAccmValue);
												break;
											}
											else if(Symbol.equals("or"))
											{
												//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
												String[] values = BT_Value.split("#");											
												if(Arrays.stream(values).anyMatch(TempBalance_Value::equals))
												{
													CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
													if(!UA_Value.contains("BT_Value") && !UA_Value.isEmpty())
														TempBalance_Value = UA_Value.split("-")[0];
													UAList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + AC_ID + ";" + UA_Value + ";" + TempBalance_ExpiryDate + ";" + TempbalanceInput.getBEBUCKETID() + ";" + ExtraAccmValue);
													if(!AC_ID.isEmpty())
														FinalUAList.add(TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + AC_ID + ";" + UA_Value + ";" + TempBalance_ExpiryDate + ";" + TempbalanceInput.getBEBUCKETID() + ";" + ExtraAccmValue);
													break;
												}
											}
										}									
									}
								}
							}
							if(FinalGroupName.startsWith("D-") && FinalUAList.size() != 0)
							{
								if(UAList.size() == CurrentGroupBalance.size())
								{
									ExtraAccumFlag = false;
									FinalUAList.forEach(item->{
										String AC_ID = item.split("\\|")[1].split(";")[0];
										String UA_Value = item.split("\\|")[1].split(";")[1];
										String BalanceValue = item.split("\\|")[0].split(";")[2];
										if(AC_ID.length() > 1)
										{
											AccMap.put("ID_" + indx, AC_ID);
											if(!UA_Value.isEmpty())
											{
												String[] UTValueList = UA_Value.split("-");
												if(UTValueList[0].equals("BT_Value"))
													AccMap.put("VALUE_" + indx, BalanceValue);
												else
													AccMap.put("VALUE_" + indx, UTValueList[0]);
												if(UTValueList[1].equals("MIGDATE"))
													AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
												else
													AccMap.put("CLEARING_DATE_" + indx,"1");
											}
											else
											{
												AccMap.put("VALUE_" + indx, BalanceValue);
												AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
											}
											this.indx++;
										}																	
									});
								}
							}
							if(FinalGroupName.startsWith("E-") && FinalUAList.size() != 0)
							{
								long FinalBalance = 0;
								String AC_ID = "";
								String UA_Value = "";
								for(String item : FinalUAList)
								{
									AC_ID = item.split("\\|")[1].split(";")[0];
									UA_Value = item.split("\\|")[1].split(";")[1];
									FinalBalance += Long.parseLong(item.split("\\|")[0].split(";")[2]);	
								}
								
								if(AC_ID.length() > 1)
								{
									AccMap.put("ID_" + indx, AC_ID);
									if(!UA_Value.isEmpty())
									{
										String[] UTValueList = UA_Value.split("-");
										if(UTValueList[0].equals("BT_Value"))
											AccMap.put("VALUE_" + indx, String.valueOf(FinalBalance));
										else
											AccMap.put("VALUE_" + indx, UTValueList[0]);
										if(UTValueList[1].equals("MIGDATE"))
											AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
										else
											AccMap.put("CLEARING_DATE_" + indx,"1");
									}
									else
									{
										AccMap.put("VALUE_" + indx, String.valueOf(FinalBalance));
										AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
									}
									
									this.indx++;
								}										
							}
							if(FinalGroupName.startsWith("A-") && FinalUAList.size() != 0)
							{
								if(UAList.size() == CurrentGroupBalance.size())
								{
									ExtraAccumFlag = false;
									FinalUAList.forEach(item->{
										String AC_ID = item.split("\\|")[1].split(";")[0];
										String UA_Value = item.split("\\|")[1].split(";")[1];
										String BalanceValue = item.split("\\|")[0].split(";")[2];
										if(AC_ID.length() > 1)
										{
											AccMap.put("ID_" + indx, AC_ID);
											if(!UA_Value.isEmpty())
											{
												String[] UTValueList = UA_Value.split("-");
												if(UTValueList[0].equals("BT_Value"))
													AccMap.put("VALUE_" + indx, BalanceValue);
												else
													AccMap.put("VALUE_" + indx, UTValueList[0]);
												if(UTValueList[1].equals("MIGDATE"))
													AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
												else
													AccMap.put("CLEARING_DATE_" + indx,"1");
											}
											else
											{
												AccMap.put("VALUE_" + indx, BalanceValue);
												AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
											}
											this.indx++;
										}																	
									});
								}
								else
								{
									if(LoadSubscriberMapping.BalanceAVGroupLookup.get(FinalGroupName) != null)
									{
										//ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).count()
										if(UAList.size() >= 2 && UAList.stream().filter(item->item.startsWith("P")).count() >=1 && UAList.stream().filter(item->item.startsWith("V")).count() >=1){
											String TargetOffer =  UAList.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[1];
											
											FinalUAList.forEach(item->{
												String AC_ID = item.split("\\|")[1].split(";")[0];
												String UA_Value = item.split("\\|")[1].split(";")[1];
												String BalanceValue = item.split("\\|")[0].split(";")[2];
												if(AC_ID.length() > 1)
												{
													AccMap.put("ID_" + indx, AC_ID);
													if(!UA_Value.isEmpty())
													{
														String[] UTValueList = UA_Value.split("-");
														if(UTValueList[0].equals("BT_Value"))
															AccMap.put("VALUE_" + indx, BalanceValue);
														else
															AccMap.put("VALUE_" + indx, UTValueList[0]);
														if(UTValueList[1].equals("MIGDATE"))
															AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
														else
															AccMap.put("CLEARING_DATE_" + indx,"1");
													}
													else
													{
														AccMap.put("VALUE_" + indx, BalanceValue);
														AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
													}													
													
													this.indx++;
												}																	
											});
											ExtraAccumFlag = false;
										}
										//else if(ValidGroupBalanceOffer.size() >= 2 &&  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("V")).count() >=1){
										else if(UAList.size() >= 2 ){
											FinalUAList.forEach(item->{
												String AC_ID = item.split("\\|")[1].split(";")[0];
												String UA_Value = item.split("\\|")[1].split(";")[1];
												String BalanceValue = item.split("\\|")[0].split(";")[2];
												if(AC_ID.length() > 1)
												{
													AccMap.put("ID_" + indx, AC_ID);
													if(!UA_Value.isEmpty())
													{
														String[] UTValueList = UA_Value.split("-");
														if(UTValueList[0].equals("BT_Value"))
															AccMap.put("VALUE_" + indx, BalanceValue);
														else
															AccMap.put("VALUE_" + indx, UTValueList[0]);
														if(UTValueList[1].equals("MIGDATE"))
															AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
														else
															AccMap.put("CLEARING_DATE_" + indx,"1");
													}
													else
													{
														AccMap.put("VALUE_" + indx, BalanceValue);
														AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
													}	
													this.indx++;
												}																	
											});
											ExtraAccumFlag = false;
										}
										else if(UAList.size() == 1)
										{
											//BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private
											if(UAList.stream().filter(item->item.startsWith("P")).count() == 1 ){
												
											}												
											if((UAList.stream().filter(item->item.startsWith("V")).count() == 1)){
												//BalanceOfferList.addAll(PopulateMasterOffer(ValidGroupBalanceOffer,BEIDForProductID));
												for(String Str : FinalUAList)
												{
													String targetUA = Str.split("\\|")[0];
													String SourceUA = Str.split("\\|")[1];
													if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M") != null)
													{
														String AC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getUAID();
														String UA_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getUAValue();
														String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getSymbols();
														String AC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getBTValue();
														
														if(Symbol.equals(">") && Integer.parseInt(targetUA.split(";")[2]) > Integer.parseInt(AC_Value))
														{
															if(AC_ID.length() > 1)
															{
																AccMap.put("ID_" + indx, AC_ID);
																if(!UA_Value.isEmpty())
																{
																	String[] UTValueList = UA_Value.split("-");
																	if(UTValueList[0].equals("BT_Value"))
																		AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	else
																		AccMap.put("VALUE_" + indx, UTValueList[0]);
																	if(UTValueList[1].equals("MIGDATE"))
																		AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
																	else
																		AccMap.put("CLEARING_DATE_" + indx,"1");
																}
																else
																{
																	AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
																}
																
																this.indx++;
															}
														}
														else if(Symbol.equals(">=") && Integer.parseInt(targetUA.split(";")[2]) >= Integer.parseInt(AC_Value))
														{
															if(AC_ID.length() > 1)
															{
																AccMap.put("ID_" + indx, AC_ID);
																if(!UA_Value.isEmpty())
																{
																	String[] UTValueList = UA_Value.split("-");
																	if(UTValueList[0].equals("BT_Value"))
																		AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	else
																		AccMap.put("VALUE_" + indx, UTValueList[0]);
																	if(UTValueList[1].equals("MIGDATE"))
																		AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
																	else
																		AccMap.put("CLEARING_DATE_" + indx,"1");
																}
																else
																{
																	AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
																}
																this.indx++;
															}
														}
														else if(Symbol.equals("=") && Integer.parseInt(targetUA.split(";")[2]) == Integer.parseInt(AC_Value))
														{
															if(AC_ID.length() > 1)
															{
																AccMap.put("ID_" + indx, AC_ID);
																if(!UA_Value.isEmpty())
																{
																	String[] UTValueList = UA_Value.split("-");
																	if(UTValueList[0].equals("BT_Value"))
																		AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	else
																		AccMap.put("VALUE_" + indx, UTValueList[0]);
																	if(UTValueList[1].equals("MIGDATE"))
																		AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
																	else
																		AccMap.put("CLEARING_DATE_" + indx,"1");
																}
																else
																{
																	AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
																}
																this.indx++;
															}
														}
														else if(Symbol.equals("or"))
														{
															//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
															String[] values = AC_Value.split("#");
															
															if(Arrays.stream(values).anyMatch(targetUA.split(";")[2]::equals))
															{
																if(AC_ID.length() > 1)
																{
																	AccMap.put("ID_" + indx, AC_ID);
																	if(!UA_Value.isEmpty())
																	{
																		String[] UTValueList = UA_Value.split("-");
																		if(UTValueList[0].equals("BT_Value"))
																			AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																		else
																			AccMap.put("VALUE_" + indx, UTValueList[0]);
																		if(UTValueList[1].equals("MIGDATE"))
																			AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
																		else
																			AccMap.put("CLEARING_DATE_" + indx,"1");
																	}
																	else
																	{
																		AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																		AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
																	}
																	this.indx++;
																}
															}
														}
													}
												}
											}
											if((UAList.stream().filter(item->item.startsWith("M")).count() == 1)){
												//BalanceOfferList.addAll(PopulateMasterOffer(ValidGroupBalanceOffer,BEIDForProductID));
												for(String Str : FinalUAList)
												{
													String targetUA = Str.split("\\|")[0];
													String SourceUA = Str.split("\\|")[1];
													if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M") != null)
													{
														String AC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getUAID();
														String UA_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getUAValue();
														String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getSymbols();
														String AC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getBTValue();
														
														if(Symbol.equals(">") && Integer.parseInt(targetUA.split(";")[2]) > Integer.parseInt(AC_Value))
														{
															if(AC_ID.length() > 1)
															{
																AccMap.put("ID_" + indx, AC_ID);
																if(!UA_Value.isEmpty())
																{
																	String[] UTValueList = UA_Value.split("-");
																	if(UTValueList[0].equals("BT_Value"))
																		AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	else
																		AccMap.put("VALUE_" + indx, UTValueList[0]);
																	if(UTValueList[1].equals("MIGDATE"))
																		AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
																	else
																		AccMap.put("CLEARING_DATE_" + indx,"1");
																}
																else
																{
																	AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
																}
																
																this.indx++;
															}
														}
														else if(Symbol.equals(">=") && Integer.parseInt(targetUA.split(";")[2]) >= Integer.parseInt(AC_Value))
														{
															if(AC_ID.length() > 1)
															{
																AccMap.put("ID_" + indx, AC_ID);
																if(!UA_Value.isEmpty())
																{
																	String[] UTValueList = UA_Value.split("-");
																	if(UTValueList[0].equals("BT_Value"))
																		AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	else
																		AccMap.put("VALUE_" + indx, UTValueList[0]);
																	if(UTValueList[1].equals("MIGDATE"))
																		AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
																	else
																		AccMap.put("CLEARING_DATE_" + indx,"1");
																}
																else
																{
																	AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
																}
																this.indx++;
															}
														}
														else if(Symbol.equals("=") && Integer.parseInt(targetUA.split(";")[2]) == Integer.parseInt(AC_Value))
														{
															if(AC_ID.length() > 1)
															{
																AccMap.put("ID_" + indx, AC_ID);
																if(!UA_Value.isEmpty())
																{
																	String[] UTValueList = UA_Value.split("-");
																	if(UTValueList[0].equals("BT_Value"))
																		AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	else
																		AccMap.put("VALUE_" + indx, UTValueList[0]);
																	if(UTValueList[1].equals("MIGDATE"))
																		AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
																	else
																		AccMap.put("CLEARING_DATE_" + indx,"1");
																}
																else
																{
																	AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
																}
																this.indx++;
															}
														}
														else if(Symbol.equals("or"))
														{
															//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
															String[] values = AC_Value.split("#");
															
															if(Arrays.stream(values).anyMatch(targetUA.split(";")[2]::equals))
															{
																if(AC_ID.length() > 1)
																{
																	AccMap.put("ID_" + indx, AC_ID);
																	if(!UA_Value.isEmpty())
																	{
																		String[] UTValueList = UA_Value.split("-");
																		if(UTValueList[0].equals("BT_Value"))
																			AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																		else
																			AccMap.put("VALUE_" + indx, UTValueList[0]);
																		if(UTValueList[1].equals("MIGDATE"))
																			AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
																		else
																			AccMap.put("CLEARING_DATE_" + indx,"1");
																	}
																	else
																	{
																		AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																		AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
																	}
																	this.indx++;
																}
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
										if(UAList.size() >= 2 && UAList.stream().filter(item->item.startsWith("P")).count() >=1 && UAList.stream().filter(item->item.startsWith("M")).count() >=1){
											String TargetOffer =  UAList.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[1];
											ExtraAccumFlag = false;
											FinalUAList.forEach(item->{
												String AC_ID = item.split("\\|")[1].split(";")[0];
												String UA_Value = item.split("\\|")[1].split(";")[1];
												String BalanceValue = item.split("\\|")[0].split(";")[2];
												if(AC_ID.length() > 1)
												{
													AccMap.put("ID_" + indx, AC_ID);
													if(!UA_Value.isEmpty())
													{
														String[] UTValueList = UA_Value.split("-");
														if(UTValueList[0].equals("BT_Value"))
															AccMap.put("VALUE_" + indx, BalanceValue);
														else
															AccMap.put("VALUE_" + indx, UTValueList[0]);
														if(UTValueList[1].equals("MIGDATE"))
															AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
														else
															AccMap.put("CLEARING_DATE_" + indx,"1");
													}
													else
													{
														AccMap.put("VALUE_" + indx, BalanceValue);
														AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
													}
													this.indx++;
												}																	
											});
											
										}
										else if(UAList.size() >= 2 &&  UAList.stream().filter(item->item.startsWith("M")).count() >=1){
											//BalanceOfferList.addAll(PopulateMasterOffer(ValidGroupBalanceOffer,BEIDForProductID));
											for(String Str : FinalUAList)
											{
												String targetUA = Str.split("\\|")[0];
												String SourceUA = Str.split("\\|")[1];
												if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M") != null)
												{
													String AC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getUAID();
													String UA_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getUAValue();
													String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getSymbols();
													String AC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getBTValue();
													
													if(Symbol.equals(">") && Integer.parseInt(targetUA.split(";")[2]) > Integer.parseInt(AC_Value))
													{
														if(AC_ID.length() > 1)
														{
															AccMap.put("ID_" + indx, AC_ID);
															if(!UA_Value.isEmpty())
															{
																String[] UTValueList = UA_Value.split("-");
																if(UTValueList[0].equals("BT_Value"))
																	AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																else
																	AccMap.put("VALUE_" + indx, UTValueList[0]);
																if(UTValueList[1].equals("MIGDATE"))
																	AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
																else
																	AccMap.put("CLEARING_DATE_" + indx,"1");
															}
															else
															{
																AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
															}
															
															this.indx++;
														}
													}
													else if(Symbol.equals(">=") && Integer.parseInt(targetUA.split(";")[2]) >= Integer.parseInt(AC_Value))
													{
														if(AC_ID.length() > 1)
														{
															AccMap.put("ID_" + indx, AC_ID);
															if(!UA_Value.isEmpty())
															{
																String[] UTValueList = UA_Value.split("-");
																if(UTValueList[0].equals("BT_Value"))
																	AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																else
																	AccMap.put("VALUE_" + indx, UTValueList[0]);
																if(UTValueList[1].equals("MIGDATE"))
																	AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
																else
																	AccMap.put("CLEARING_DATE_" + indx,"1");
															}
															else
															{
																AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
															}
															this.indx++;
														}
													}
													else if(Symbol.equals("=") && Integer.parseInt(targetUA.split(";")[2]) == Integer.parseInt(AC_Value))
													{
														if(AC_ID.length() > 1)
														{
															AccMap.put("ID_" + indx, AC_ID);
															if(!UA_Value.isEmpty())
															{
																String[] UTValueList = UA_Value.split("-");
																if(UTValueList[0].equals("BT_Value"))
																	AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																else
																	AccMap.put("VALUE_" + indx, UTValueList[0]);
																if(UTValueList[1].equals("MIGDATE"))
																	AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
																else
																	AccMap.put("CLEARING_DATE_" + indx,"1");
															}
															else
															{
																AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
															}
															this.indx++;
														}
													}
													else if(Symbol.equals("or"))
													{
														//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
														String[] values = AC_Value.split("#");
														
														if(Arrays.stream(values).anyMatch(targetUA.split(";")[2]::equals))
														{
															if(AC_ID.length() > 1)
															{
																AccMap.put("ID_" + indx, AC_ID);
																if(!UA_Value.isEmpty())
																{
																	String[] UTValueList = UA_Value.split("-");
																	if(UTValueList[0].equals("BT_Value"))
																		AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	else
																		AccMap.put("VALUE_" + indx, UTValueList[0]);
																	if(UTValueList[1].equals("MIGDATE"))
																		AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
																	else
																		AccMap.put("CLEARING_DATE_" + indx,"1");
																}
																else
																{
																	AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
																}
																this.indx++;
															}
														}
													}
												}				
											}	
										}
										else if(UAList.size() == 1)
										{
											//BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private
											if(UAList.stream().filter(item->item.startsWith("P")).count() == 1 ){
												String TargetOffer =  UAList.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[1];
												// Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + Balance_StartDate + ";" + Balance_ExpiryDate  + ";" + Product_Private
												FinalUAList.forEach(item->{
													String AC_ID = item.split("\\|")[1].split(";")[0];
													String UA_Value = item.split("\\|")[1].split(";")[1];
													String BalanceValue = item.split("\\|")[0].split(";")[2];
													if(AC_ID.length() > 1)
													{
														AccMap.put("ID_" + indx, AC_ID);
														if(!UA_Value.isEmpty())
														{
															String[] UTValueList = UA_Value.split("-");
															if(UTValueList[0].equals("BT_Value"))
																AccMap.put("VALUE_" + indx, BalanceValue);
															else
																AccMap.put("VALUE_" + indx, UTValueList[0]);
															if(UTValueList[1].equals("MIGDATE"))
																AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
															else
																AccMap.put("CLEARING_DATE_" + indx,"1");
														}
														else
														{
															AccMap.put("VALUE_" + indx, BalanceValue);
															AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
														}
														AccMap.put("VALUE_" + indx, BalanceValue);
														AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
														
														this.indx++;
													}																	
												});
											}
											if(UAList.stream().filter(item->item.startsWith("M")).count() == 1){
												//BalanceOfferList.addAll(PopulateMasterOffer(ValidGroupBalanceOffer,BEIDForProductID));
												for(String Str : FinalUAList)
												{
													String targetUA = Str.split("\\|")[0];
													String SourceUA = Str.split("\\|")[1];
													if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M") != null)
													{
														String AC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getUAID();
														String UA_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getUAValue();
														String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getSymbols();
														String AC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getBTValue();
														
														if(Symbol.equals(">") && Integer.parseInt(targetUA.split(";")[2]) > Integer.parseInt(AC_Value))
														{
															if(AC_ID.length() > 1)
															{
																AccMap.put("ID_" + indx, AC_ID);
																if(!UA_Value.isEmpty())
																{
																	String[] UTValueList = UA_Value.split("-");
																	if(UTValueList[0].equals("BT_Value"))
																		AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	else
																		AccMap.put("VALUE_" + indx, UTValueList[0]);
																	if(UTValueList[1].equals("MIGDATE"))
																		AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
																	else
																		AccMap.put("CLEARING_DATE_" + indx,"1");
																}
																else
																{
																	AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
																}
																
																this.indx++;
															}
														}
														else if(Symbol.equals(">=") && Integer.parseInt(targetUA.split(";")[2]) >= Integer.parseInt(AC_Value))
														{
															if(AC_ID.length() > 1)
															{
																AccMap.put("ID_" + indx, AC_ID);
																if(!UA_Value.isEmpty())
																{
																	String[] UTValueList = UA_Value.split("-");
																	if(UTValueList[0].equals("BT_Value"))
																		AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	else
																		AccMap.put("VALUE_" + indx, UTValueList[0]);
																	if(UTValueList[1].equals("MIGDATE"))
																		AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
																	else
																		AccMap.put("CLEARING_DATE_" + indx,"1");
																}
																else
																{
																	AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
																}
																this.indx++;
															}
														}
														else if(Symbol.equals("=") && Integer.parseInt(targetUA.split(";")[2]) == Integer.parseInt(AC_Value))
														{
															if(AC_ID.length() > 1)
															{
																AccMap.put("ID_" + indx, AC_ID);
																if(!UA_Value.isEmpty())
																{
																	String[] UTValueList = UA_Value.split("-");
																	if(UTValueList[0].equals("BT_Value"))
																		AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	else
																		AccMap.put("VALUE_" + indx, UTValueList[0]);
																	if(UTValueList[1].equals("MIGDATE"))
																		AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
																	else
																		AccMap.put("CLEARING_DATE_" + indx,"1");
																}
																else
																{
																	AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																	AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
																}
																this.indx++;
															}
														}
														else if(Symbol.equals("or"))
														{
															//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
															String[] values = AC_Value.split("#");
															
															if(Arrays.stream(values).anyMatch(targetUA.split(";")[2]::equals))
															{
																if(AC_ID.length() > 1)
																{
																	AccMap.put("ID_" + indx, AC_ID);
																	if(!UA_Value.isEmpty())
																	{
																		String[] UTValueList = UA_Value.split("-");
																		if(UTValueList[0].equals("BT_Value"))
																			AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																		else
																			AccMap.put("VALUE_" + indx, UTValueList[0]);
																		if(UTValueList[1].equals("MIGDATE"))
																			AccMap.put("CLEARING_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString())));
																		else
																			AccMap.put("CLEARING_DATE_" + indx,"1");
																	}
																	else
																	{
																		AccMap.put("VALUE_" + indx, targetUA.split(";")[2]);
																		AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
																	}
																	this.indx++;
																}
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
							
							
							if(ExtraAccumFlag)
							{
								for(String s: UAList)
								{
									if(s.split("\\|")[1].split(";").length >= 5)
									{
										String ExtraAccmValue = s.split("\\|")[1].split(";")[4];
										if(!ExtraAccmValue.isEmpty())
										{
											String[] ListofAddedUA = ExtraAccmValue.split("#");
											
											for(int i = 0; i<ListofAddedUA.length; i++)
											{
												if(ListofAddedUA[i].length() > 1)
												{
													AccMap.put("ID_" + indx, ListofAddedUA[i].split("-")[0]);
													
													AccMap.put("VALUE_" + indx, ListofAddedUA[i].split("-")[1]);
													AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
													
													this.indx++;
													//trackLog.add("INC7003:Add_UC considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":UC_ID=" + UC_ID + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID()  + ":UC_VALUE=" + Balance +":ACTION=Logging");
												}											 
											}
										}
									}
								}								
							}
						}
						else
						{
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						}
					}
				}
			}
		}
		// TODO Auto-generated method stub
		return AccMap;
	}
	
	
	
	private Map<? extends String,?extends String> generateACMFromProfileTag()
	{
		Map<String,String> AccMap = new HashMap<>();
		Date currDate = new Date();
		SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
		if(subscriber.getProfiledumpInfoList().size() == 0)
			return AccMap;
		
		for(String itr : LoadSubscriberMapping.Profile_Tags_Mapping.keySet())
		{
			PROFILETAGINFO profileMappingValue = LoadSubscriberMapping.Profile_Tags_Mapping.get(itr);
			String Symbol = profileMappingValue.getSymbols();
			String TargetValue = profileMappingValue.getProfileTagValue();
			String IgnoreFlag =  profileMappingValue.getIgnoreFlag();
			if(IgnoreFlag.equals("N"))
			{				
				//if(itr.equals("MBB10GBWelcome") || itr.equals("MBB2GBWelcome") || itr.equals("MBBUnlimWelcome") || itr.equals("MBBWelcome"))
				if(LoadSubscriberMapping.ProfileTagName.contains(itr))
				{
					String Profile_Value = profileTag.GetProfileTagValue(itr);
					if(Profile_Value.isEmpty() || profileMappingValue.getUAID().isEmpty())
						continue;
					
					if(profileMappingValue.getSubState().isEmpty())
					{						
						if(Symbol.equals("=") && Profile_Value.equals(TargetValue))
						{
							String AC_ID = profileMappingValue.getUAID();
							String Balance = profileMappingValue.getUAValue();
							if(AC_ID.length() > 1)
							{
								AccMap.put("ID_" + indx, AC_ID);
								AccMap.put("VALUE_" + indx,String.valueOf(Balance));
								AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
								
								this.indx++;
							}
						}
					}
					else if(profileMappingValue.getSubState().equals(subscriber.getSubscriberInfoSERVICESTATE()))
					{
						if(Symbol.equals("=") && Profile_Value.equals(TargetValue))
						{
							String AC_ID = profileMappingValue.getUAID();
							String Balance = profileMappingValue.getUAValue();
							if(AC_ID.length() > 1)
							{
								AccMap.put("ID_" + indx, AC_ID);
								AccMap.put("VALUE_" + indx,String.valueOf(Balance));
								AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
								
								this.indx++;
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
						
			if(IgnoreFlag.equals("N"))
			{
								
				//***************logic for Dummy1, dummy2 and Dummy3
				String Profile_Value = profileTag.GetProfileTagValue(itr.split(",")[0]);
				
				if(Profile_Value.isEmpty() || profileMappingValue.getUAID().isEmpty())
					continue;
				
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
								if(profileTagSymbol.equals("!="))
								{
									String additionalProfileValue = profileTag.GetProfileTagValue(profileTagName);
									if(!additionalProfileValue.isEmpty())
									{
										if(!additionalProfileValue.equals("#"))
											ValidPTCheck.add(profileTagName);
										else
										{
											break;
										}
									}
									else
									{
										break;
									}
								}
							}
							if(PT_Values.size() == ValidPTCheck.size())
							{
								String AC_ID = profileMappingValue.getUAID();
								String Balance = profileTag.GetProfileTagValue(profileMappingValue.getUAValue().split("\\.")[0]);
								if(AC_ID.length() > 1)
								{
									AccMap.put("ID_" + indx, AC_ID);
									AccMap.put("VALUE_" + indx,String.valueOf(Balance));
									AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
									
									this.indx++;
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
										if(!additionalProfileValue.equals("#"))
											ValidPTCheck.add(profileTagName);
										else
										{
											break;
										}
									}
									else
									{
										break;
									}
								}
							}
							if(PT_Values.size() == ValidPTCheck.size())
							{
								String AC_ID = profileMappingValue.getUAID();
								
								String Balance = profileTag.GetProfileTagValue(profileMappingValue.getUAValue().split("\\.")[0]);
								if(AC_ID.length() > 1)
								{
									AccMap.put("ID_" + indx, AC_ID);
									AccMap.put("VALUE_" + indx,String.valueOf(Balance));
									AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
									
									this.indx++;
								}
							}
						}
					}					
				}				
				
				if(TargetName.equals("NewPPBundle") && Profile_Value.length() !=0)					
				{
					if(profileMappingValue.getSubState().equals(Profile_Value))
					{
						String[] values = {"Monthly1","Monthly2","Monthly3","Monthly4"};
						if(Symbol.equals("="))
						{	
							if(TargetValue.equals(Profile_Value))
							{
								String AC_ID = profileMappingValue.getUAID();
								String Balance = profileTag.GetProfileTagValue("BstrVINChngsAllwd");
								if(AC_ID.length() > 1)
								{
									AccMap.put("ID_" + indx, AC_ID);
									AccMap.put("VALUE_" + indx,String.valueOf(Balance));
									AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
									
									this.indx++;
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
		return AccMap;
	}
		
	private Map<String,Map<String,Map<String,String>>> populateAMGroupResult(String balance_ID,String balance_Value,String START_DATE, String EXPIRY_DATE, String bebucketid, Set<String> CompletedBT_ID) 
	{
		Map<String,String> DAValues = new HashMap<>();
		List<String> AMCompleted_ID = new ArrayList<>();
		Map<String,Map<String,Map<String,String>>> GroupOfferMap = new HashMap<>();
		
		Map<String,String> AMGroupBTs = new HashMap<>();
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo TempbalanceInput :  SortedBalanceInput){
			String TempBalance_ID = TempbalanceInput.getBALANCETYPE();
			String TempBalance_Value = TempbalanceInput.getBEBUCKETVALUE();
			String TempBalance_ExpiryDate = TempbalanceInput.getBEEXPIRY();
			//CurrentGroupBalanceID.put(TempBalance_ID + "," + TempBalance_Value + "," + TempBalance_ExpiryDate, TempbalanceInput.getBEBUCKETID());
			
			if(CompletedBT_ID.contains(TempbalanceInput.getBEBUCKETID()))
				continue;
			
			if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(TempBalance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(TempBalance_ID).equals("Y"))
			{
				CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
				continue;
			}
			
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
				List<String> ValidAMGroupBalanceAC = item.getValue();
				
				int i = 1;
				Set<String> CompletedAMBT = new HashSet<>();
				for(String Str : ValidAMGroupBalanceAC)
				{
					String SourceOffer = Str.split("\\|")[0];
					String TargetOffer = Str.split("\\|")[1];
					if(i <= UniqueBTID.size() && UniqueBTID.contains(SourceOffer.split(";")[2]) && !CompletedAMBT.contains(SourceOffer.split(";")[2]))
					{
						CompletedAMBT.add(SourceOffer.split(";")[2]);
						AMCompleted_ID.add(TargetOffer.split(";")[3]);	
					}
					else
					{
						/*if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceOffer.split(";")[2] + "|M") != null)
						{
							for(String item_value: UAList)
							{
								String SourceValue =  item_value.split("\\|")[0];
								String TargetValue = item_value.split("\\|")[1];
								
							}															
						}*/
						AMCompleted_ID.add(TargetOffer.split(";")[3]);
					}
				}													
			}
		}
		
		Map<String,Map<String,String>> AMOutputDetails = new HashMap<>();
		if(AMCompleted_ID.size() != 0)
		{
			Map<String, String> temp = new HashMap<>();
			temp.put("BTID",String.join(",",AMCompleted_ID));
			AMOutputDetails.put("CompletedBT",temp);
		}
		if(DAValues.size() != 0)
			AMOutputDetails.put("UA", DAValues);
		
		
		GroupOfferMap.put("AMOutputDetails",AMOutputDetails);
		
		return GroupOfferMap;
	}
	
	public Map<String,Map<String,Map<String,String>>>  ComputeASpecialGroup(String inputBalance_ID, Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub Map<String,List<String>>
		Map<String, String> AccMap = new HashMap<>();
		List<String> ASGroupName = new ArrayList<>();		
		List<String> UAList = new ArrayList<>();
		List<String> FinalUAList = new ArrayList<>();
		Set<String> ASBT_ID = new HashSet<>();
		boolean ExtraAccumFlag = false;
		boolean ASGroupFormed = false;
		List<String> GroupBalanceUA = new ArrayList<>();
		Map<String,List<String>> tempGroupBalanceOffer = new HashMap<>();
		
		//Map<String,Map<String,List<String>>> ASGroupOfferMap = new HashMap<>();
		Map<String,Map<String,Map<String,String>>> ASGroupUAMap = new HashMap<>();
		ASGroupName = (commonfunction.getASpecialGroupKey(LoadSubscriberMapping.BalanceOnlySpecialASGroupMap,inputBalance_ID));

		//CheckIf A-S-1 is present in the input
		//Map<String,Map<String,List<String>>> ASGroupOfferMap = new HashMap<>();
		/*ASGroupOfferMap = CheckifA_S_1Present(CompletedBT_ID);
		
		Map<String,List<String>> AS1_OutputDetails = new HashMap<>();
		AS1_OutputDetails = ASGroupOfferMap.get("ASOutputDetails");
		if(AS1_OutputDetails.containsKey("Offer"))
		{											
			FinalBalanceOffer.addAll(AS1_OutputDetails.get("Offer"));											
			CompletedBT_ID.addAll(AS1_OutputDetails.get("CompletedBT"));			
		}*/
		
		for(String GroupName : ASGroupName)
		{
			Set<String> GroupElements = new HashSet<>();
			if(!GroupName.startsWith("A-S"))
			{
				UAList.clear();
				ASBT_ID.clear();
				FinalUAList.clear();
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
							String AC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getUAID();
							String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getSymbols();
							String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getBTValue();
							String AC_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getUAType();
							String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getBTTYPE();
							
							String ExtraAccmValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getAddUA();
							
							if(!ExtraAccmValue.isEmpty())
							{
								ExtraAccumFlag = true;
							}
							else
							{
								ExtraAccmValue = "";
							}
							ASBT_ID.add(TempbalanceInput.getBEBUCKETID());
							if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
							{
								if(!GroupElements.contains(TempBalance_ID))
								{
									GroupElements.add(TempBalance_ID);
									UAList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + AC_ID + ";" + AC_Type + ";" + TempbalanceInput.getBEEXPIRY() + ";" + TempbalanceInput.getBEBUCKETID() + ";" + ExtraAccmValue);
									if(!AC_ID.isEmpty())
										FinalUAList.add(TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + AC_ID + ";" + AC_Type + ";" + TempbalanceInput.getBEEXPIRY() + ";" + TempbalanceInput.getBEBUCKETID() + ";" + ExtraAccmValue);
								}
							}
							else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
							{
								if(!GroupElements.contains(TempBalance_ID))
								{
									GroupElements.add(TempBalance_ID);
									UAList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + AC_ID + ";" + AC_Type + ";" + TempbalanceInput.getBEEXPIRY() + ";" + TempbalanceInput.getBEBUCKETID() + ";" + ExtraAccmValue);
									if(!AC_ID.isEmpty())
										FinalUAList.add(TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + AC_ID + ";" + AC_Type + ";" + TempbalanceInput.getBEEXPIRY() + ";" + TempbalanceInput.getBEBUCKETID() + ";" + ExtraAccmValue);
								}
							}
							else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
							{
								if(!GroupElements.contains(TempBalance_ID))
								{
									GroupElements.add(TempBalance_ID);
									UAList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + AC_ID + ";" + AC_Type + ";" + TempbalanceInput.getBEEXPIRY() + ";" + TempbalanceInput.getBEBUCKETID() + ";" + ExtraAccmValue);
									if(!AC_ID.isEmpty())
										FinalUAList.add(TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + AC_ID + ";" + AC_Type + ";" + TempbalanceInput.getBEEXPIRY() + ";" + TempbalanceInput.getBEBUCKETID() + ";" + ExtraAccmValue);
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
										GroupElements.add(TempBalance_ID);
										UAList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + AC_ID + ";" + AC_Type + ";" + TempbalanceInput.getBEEXPIRY() + ";" + TempbalanceInput.getBEBUCKETID() + ";" + ExtraAccmValue);
										if(!AC_ID.isEmpty())
											FinalUAList.add(TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + AC_ID + ";" + AC_Type + ";" + TempbalanceInput.getBEEXPIRY() + ";" + TempbalanceInput.getBEBUCKETID() + ";" + ExtraAccmValue);
									}
								}
							}														
						}							
					}
				}
				if(UAList.size() == GroupBTItems.size())
				{
					ASGroupFormed = true;
					break;					
				}
				else
				{
					List<String> temp = new ArrayList<>( UAList);
					tempGroupBalanceOffer.put(GroupName, temp);
				}
			}
		}
		if(ASGroupFormed)
		{
			FinalUAList.forEach(item->{
				String AC_ID = item.split("\\|")[1].split(";")[0];
				String BalanceValue = item.split("\\|")[0].split(";")[2];
				if(AC_ID.length() > 1)
				{
					AccMap.put("ID_" + indx, AC_ID);
					AccMap.put("VALUE_" + indx, BalanceValue);
					AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
					
					this.indx++;
				}																	
			});
		}
		else
		{
			List<String> ValidGroupBalanceUA = Collections.max(tempGroupBalanceOffer.entrySet(), (entry1, entry2) -> entry1.getValue().size() - entry2.getValue().size()).getValue();
			
			for(String Str : FinalUAList)
			{
				String targetUA = Str.split("\\|")[0];
				String SourceUA = Str.split("\\|")[1];
				if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M") != null)
				{
					String AC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getUAID();
					String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getSymbols();
					String AC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getBTValue();
					String AC_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getUAType();
					String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(targetUA.split(";")[1] + "|M").getBTTYPE();
					
					if(Symbol.equals(">") && Integer.parseInt(targetUA.split(";")[2]) > Integer.parseInt(AC_Value))
					{
						if(AC_ID.length() > 1)
						{
							AccMap.put("ID_" + indx, AC_ID);
							AccMap.put("VALUE_" + indx, AC_Value);
							AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
							
							this.indx++;
						}
					}
					else if(Symbol.equals(">=") && Integer.parseInt(targetUA.split(";")[2]) >= Integer.parseInt(AC_Value))
					{
						if(AC_ID.length() > 1)
						{
							AccMap.put("ID_" + indx, AC_ID);
							AccMap.put("VALUE_" + indx, AC_Value);
							AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
							
							this.indx++;
						}
					}
					else if(Symbol.equals("=") && Integer.parseInt(targetUA.split(";")[2]) == Integer.parseInt(AC_Value))
					{
						if(AC_ID.length() > 1)
						{
							AccMap.put("ID_" + indx, AC_ID);
							AccMap.put("VALUE_" + indx, AC_Value);
							AccMap.put("CLEARING_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_last_reset_Date"));
							
							this.indx++;
						}
					}
				}				
			}	
		}
		
		Map<String,Map<String,String>> AMOutputDetails = new HashMap<>();
		if(ASBT_ID.size() != 0)
		{
			Map<String, String> temp = new HashMap<>();
			temp.put("BTID",String.join(",",ASBT_ID));
			AMOutputDetails.put("CompletedBT",temp);
		}
		if(AccMap.size() != 0)
			AMOutputDetails.put("UA", AccMap);
		
		
		ASGroupUAMap.put("AMOutputDetails",AMOutputDetails);
		
		return ASGroupUAMap;
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
						i++;
						FinalGroupName = individualGroup;
						A_currentGroup.add(BT_ID);
						continue;
					}
					else if(Symbol.equals(">") && Integer.parseInt(BT_BALANCE) > Integer.parseInt(BT_Value))
					{
						i++;
						FinalGroupName = individualGroup;
						A_currentGroup.add(BT_ID);
						continue;
					}
					else if(Symbol.equals("=") && Integer.parseInt(BT_BALANCE) == Integer.parseInt(BT_Value))
					{
						i++;
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
							i++;
							FinalGroupName = individualGroup;
							A_currentGroup.add(BT_ID);
							continue;
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
	
		List<String> FinalUAList = new ArrayList<>();
		Map<String,List<String>> AMGroupDAMap = new HashMap<>();
		if(FinalGroupName.length() != 0)
		{	
			boolean ExtraOfferFlag = false;
			boolean ExtraDAFlag = false;
			for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
			{
				String TempBalance_ID = balanceInput.getBALANCETYPE();
				if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName) != null)
				{
					if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
						continue;
				
					if(AMBalanceBT.containsKey(balanceInput.getBEBUCKETID()))
					{
						String AC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getUAID();
						String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getSymbols();
						String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTValue();
						String AC_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getUAType();
						String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTTYPE();
						
						
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						FinalUAList.add(BT_Type + ";" +balanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + balanceInput.getBEBUCKETVALUE() +"|" + AC_ID + ";" + AC_Type + ";" + balanceInput.getBEEXPIRY() + ";" + balanceInput.getBEBUCKETID());
						
					}					
				}
			}			
			AMGroupDAMap.put(FinalGroupName + "|" + AMBTValue, FinalUAList);
		}
		
		return AMGroupDAMap;
	}
	
	public Map<String,List<String>>  ComputeASGroup(String inputBalance_ID, Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub Map<String,List<String>>
		
		String FinalGroupName ="";		
		List<String>AllAvailableGroup = new ArrayList<>();	
		List<String> FinalUAList = new ArrayList<>();
		Set<String> ASGroupItems = new HashSet<>();
		boolean ExtraOfferFlag = false;
		boolean ExtraDAFlag = false;
		Map<String,List<String>> ASGroupOfferMap = new HashMap<>();
		FinalGroupName = (commonfunction.getASGroupKey(LoadSubscriberMapping.BalanceOnlyASGroupMap,inputBalance_ID)).trim();

		//String temp1 = LoadSubscriberMapping.BalanceOnlyASGroupMap.get(FinalGroupName);
		ASGroupItems = Arrays.stream(LoadSubscriberMapping.BalanceOnlyASGroupMap.get(FinalGroupName).split(",")).collect(Collectors.toSet()); 
		
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo TempbalanceInput : SortedBalanceInput)
		{
			String TempBalance_ID = TempbalanceInput.getBALANCETYPE();
			String TempBalance_Name = TempbalanceInput.getBALANCETYPENAME();
			String TempBalance_Value = TempbalanceInput.getBEBUCKETVALUE();
			String TempBalance_StartDate = TempbalanceInput.getBEBUCKETSTARTDATE();
			String TempBalance_ExpiryDate = TempbalanceInput.getBEEXPIRY();
			//CurrentGroupBalanceID.put(TempBalance_ID + "," + TempBalance_Value + "," + TempBalance_ExpiryDate, TempbalanceInput.getBEBUCKETID());
			
			if(CompletedBT_ID.contains(TempbalanceInput.getBEBUCKETID()))
				continue;
			if(ASGroupItems.contains(TempBalance_ID))
			{
				if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName) != null)
				{							
					String AC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getUAID();
					String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getSymbols();
					String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTValue();
					String AC_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getUAType();
					String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTTYPE();
					
					
					
					CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
					
					if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
					{
						FinalUAList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + AC_ID + ";" + AC_Type + ";" + TempbalanceInput.getBEBUCKETID());
						continue;
					}
					else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
					{
						FinalUAList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + AC_ID + ";" + AC_Type + ";" + TempbalanceInput.getBEBUCKETID());						
						continue;
					}
					else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
					{
						FinalUAList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + AC_ID + ";" + AC_Type + ";" + TempbalanceInput.getBEBUCKETID());
						continue;
					}
					else if(Symbol.equals("or"))
					{
						//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
						String[] values = BT_Value.split("#");											
						if(Arrays.stream(values).anyMatch(TempBalance_Value::equals))
						{
							FinalUAList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + AC_ID + ";" + AC_Type + ";" + TempbalanceInput.getBEBUCKETID());
							continue;
						}																					
					}
				}
			}
		}
		String temp = String.join(",", ASGroupItems);
		ASGroupOfferMap.put(FinalGroupName +"|" + temp, FinalUAList);
		return ASGroupOfferMap;
	}
	
}
