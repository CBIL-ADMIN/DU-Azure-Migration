package com.ericsson.dm.transform.implementation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
import com.ericsson.jibx.beans.SubscriberXml;
import com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo;

public class UsageCounter implements Comparator<SchemasubscriberbalancesdumpInfo>{
	SubscriberXml subscriber;
	String msisdn;
	Set<String> rejectAndLog;
	Set<String> onlyLog;
	Set<String> trackLog;
	Set<String> validMsisdn;
	Set<String> AMBTIDSet;
	private Map<String, Set<String>> ProductIDLookUpMap;
	private Map<Integer, String> ProductIDFor53;
	int CounterFor53;
	
	ProfileTagProcessing profileTag;
	CommonFunctions commonfunction;	
	public CopyOnWriteArrayList<SchemasubscriberbalancesdumpInfo> SortedBalanceInput;
	
	public UsageCounter()
	{
		
	}
	
	public UsageCounter(SubscriberXml subscriber,Set<String> rejectAndLog, Set<String> onlyLog, Set<String> trackLog,Map<String, Set<String>> ProductIDLookUpMap,Map<Integer, String> ProductIDFor53) {
		this.subscriber = subscriber;
		this.rejectAndLog=rejectAndLog;
		this.trackLog = trackLog;
		this.onlyLog=onlyLog;
		this.CounterFor53 = 1;
		this.ProductIDLookUpMap = ProductIDLookUpMap;
		this.ProductIDFor53 = ProductIDFor53;
		this.AMBTIDSet = new HashSet<>();
		
		SortedBalanceInput = new CopyOnWriteArrayList<>();
		
		commonfunction = new CommonFunctions(subscriber,ProductIDLookUpMap,onlyLog);
		profileTag = new ProfileTagProcessing(subscriber,this.onlyLog);
	}
	
	@Override
	public int compare(SchemasubscriberbalancesdumpInfo o1, SchemasubscriberbalancesdumpInfo o2) {
		int value1 = (o2.getBEEXPIRY()).compareTo((o1.getBEEXPIRY()));
        if (value1 == 0) {
        	return  o2.getBEBUCKETID().compareTo(o1.getBEBUCKETID());
        }
        return value1;
	}
	
	public List<String> execute() {
		// TODO Auto-generated method stub
		msisdn = subscriber.getSubscriberInfoMSISDN();
		
		SortedBalanceInput.addAll(subscriber.getBalancesdumpInfoList());
		
		Collections.sort(SortedBalanceInput,new Offer());
		
		List<String> UCList = new ArrayList<>();
		//offer creation from Balance Mapping sheet
		UCList.addAll(UsageCounterFromAMBalanceMapping());
		
		UCList.addAll(UsageCounterFromBalanceMapping());
		
		SortedBalanceInput.clear();
		return UCList;
	}
	
	private Collection<? extends String> UsageCounterFromBalanceMapping() {
		// TODO Auto-generated method stub
		
		Date currDate = new Date();
		SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<String> UsageCounterList =new ArrayList<>();
		Set<String> UCCreated = new HashSet<>();
		
		Set<String> BEIDForProductID = new HashSet<>();
		Set<String> CompletedBT_ID = new HashSet<>();
		CompletedBT_ID.addAll(AMBTIDSet);
		
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{			
			String Balance_ID = balanceInput.getBALANCETYPE();
			if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(Balance_ID))
				continue;
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			String Balance_StartDate = balanceInput.getBEBUCKETSTARTDATE();
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			String Balance_Msisdn = balanceInput.getMSISDN();
			BEIDForProductID.clear();
			boolean BTPA14BalanceDummy = false;
			boolean BT25UCCreated = false;
			/*if(CompletedBT_ID.contains(Balance_ID))
				continue;*/			
			
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
				Boolean AddUCFlag = false;
				String UC_ID = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getUCID();
				String Symbol = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getSymbols();
				String BT_Value = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getBTValue();
				String UC_Value = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getUTValue();
				String Resource = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getResource();
				String AddUC = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getAddUC();
				
				if(!UC_ID.isEmpty())
				{					
					if(Symbol.isEmpty() && BT_Value.isEmpty())
					{	
						//String Resourse, String UTVal, String Balance,String balance_ID,bucket_ID, String Target_ID
						long CalculatedBalance = CalculateBalance(Resource,UC_Value,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
						UsageCounterList.add(PopulateUsageCounter(UC_ID,Balance_Msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
					}
					else
					{
						if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
						{
							long CalculatedBalance = CalculateBalance(Resource,UC_Value,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
							UsageCounterList.add(PopulateUsageCounter(UC_ID,Balance_Msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
							AddUCFlag = true;
						}
						else if(Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_Value))
						{
							long CalculatedBalance = CalculateBalance(Resource,UC_Value,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
							UsageCounterList.add(PopulateUsageCounter(UC_ID,Balance_Msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
							AddUCFlag = true;
						}
						else if(Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value))
						{
							long CalculatedBalance = CalculateBalance(Resource,UC_Value,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
							UsageCounterList.add(PopulateUsageCounter(UC_ID,Balance_Msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
							AddUCFlag = true;
						}
						else if(Symbol.equals("or"))
						{
							//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
							String[] values = BT_Value.split("#");
							
							if(Arrays.stream(values).anyMatch(Balance_Value::equals))
							{
								long CalculatedBalance = CalculateBalance(Resource,UC_Value,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
								UsageCounterList.add(PopulateUsageCounter(UC_ID,Balance_Msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								AddUCFlag = true;
							}
						}
					}
				}
				if(!AddUC.isEmpty() && AddUCFlag)
				{
					String[] ListofAddedUC = AddUC.split("#");
					
					for(int i = 0; i<ListofAddedUC.length; i++)
					{
						if(ListofAddedUC[i].length() > 1)
						{
							//361-5368709120
							String AddUC_ID = ListofAddedUC[i].split("-")[0];
							String AddBalance = ListofAddedUC[i].split("-")[1];
							if(AddBalance.equals("BT_Value"))
							{
								long CalculatedBalance = CalculateBalance(Resource,UC_Value,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
								AddBalance = String.valueOf(CalculatedBalance);
							}
							UsageCounterList.add(PopulateUsageCounter(AddUC_ID,Balance_Msisdn,String.valueOf(AddBalance), new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
							//BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID()
							trackLog.add("INC7003:Add_UC considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID()  + ":UC_ID=" + UC_ID + ":UC_VALUE=" + AddBalance +":ACTION=Logging");
						}											 
					}
				
				}
			}	
			else
			{	
				if(LoadSubscriberMapping.ExceptionBalances.contains(Balance_ID))
				{	
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
										String UC_ID = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getUCID();
										String UC_Value = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getUTValue();
										String Resource = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getResource();
										
										if(UC_ID.length() != 0 )
										{
											if(!UCCreated.contains(UC_ID))
											{
												UCCreated.add(UC_ID);
												BTPA14BalanceDummy = false;
												long CalculatedBalance = CalculateBalance(Resource,UC_Value,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
												UsageCounterList.add(PopulateUsageCounter(UC_ID,Balance_Msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
											}
										}
									}
									else if(Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_Value) && !BTPA14BalanceDummy)
									{
										String UC_ID = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getUCID();
										String UC_Value = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getUTValue();
										String Resource = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getResource();
										
										if(UC_ID.length() != 0 )
										{
											if(!UCCreated.contains(UC_ID))
											{
												UCCreated.add(UC_ID);
												BTPA14BalanceDummy = false;
												long CalculatedBalance = CalculateBalance(Resource,UC_Value,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
												UsageCounterList.add(PopulateUsageCounter(UC_ID,Balance_Msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
											}
										}
									}
									CompletedBT_ID.add(balanceInput.getBEBUCKETID());
									continue;
								}
							}
						}
					}
					if(Balance_ID.equals("25") || Balance_ID.equals("240") || Balance_ID.equals("260"))
					{
						boolean DummyDoneFlag = false;
						for(String str : LoadSubscriberMapping.ProfileTagDummy)
						{							 
							if(LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str) != null && str.contains(Balance_ID))
							{
								String Symbol = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getSymbols();
								String BT_Value = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getBTValue();
								if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
								{
									List<String> PTMappingValue = Arrays.asList(LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getPTName().split("#"));
									List<String> UCOutput  = new ArrayList<>();
									Set<String> ValidUCTagValue= new HashSet();
									
									for(String PtValue : PTMappingValue)
									{
										String PT_Name = PtValue.split("-")[0];
										String PT_Symbol = PtValue.split("-")[1];
										String PT_Value = PtValue.split("-")[2];
																													
										String PT_InputValue = profileTag.GetProfileTagValue(PT_Name);
										if(PT_Symbol.equals("=") && PT_InputValue.equals(PT_Value))
										{
											String UC_ID = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getUCID();
											String UC_Value = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getUTValue();
											String Resource = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getResource();
											
											if(UC_ID.length() != 0 )
											{
												if(!BT25UCCreated)
												{													
													UCOutput.add(balanceInput.getBEBUCKETID());
													//long CalculatedBalance = CalculateBalance(Resource,UC_Value,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
													ValidUCTagValue.add(UC_ID + ";" + Balance_Msisdn + ";" + Resource + ";" +  balanceInput.getBEBUCKETID() + ";" + UC_Value);
												}
											}
										}
										if(PT_Symbol.equals("!=") && !PT_InputValue.equals(PT_Value))
										{
											String UC_ID = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getUCID();
											String UC_Value = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getUTValue();
											String Resource = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getResource();
											
											if(UC_ID.length() != 0 )
											{
												if(!BT25UCCreated)
												{													
													UCOutput.add(balanceInput.getBEBUCKETID());
													//long CalculatedBalance = CalculateBalance(Resource,UC_Value,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
													ValidUCTagValue.add(UC_ID + ";" + Balance_Msisdn + ";" + Resource + ";" +  balanceInput.getBEBUCKETID()+ ";" + UC_Value);
												}
											}
										}
									}
									if(UCOutput.size() == PTMappingValue.size() && !BT25UCCreated)
									{
										for(String s : ValidUCTagValue)
										{
											//[101311;139553567117;TOUC3;41380938;60]
											BT25UCCreated = true;
											DummyDoneFlag = true;
											long CalculatedBalance = CalculateBalance(s.split(";")[2],s.split(";")[4],Balance_Value,Balance_ID,s.split(";")[3],s.split(";")[0]);
											UsageCounterList.add(PopulateUsageCounter(s.split(";")[0],s.split(";")[1],String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(s.split(";")[3]))));
										}
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
											String UC_ID = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getUCID();
											String UC_Value = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getUTValue();
											String Resource = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getResource();
											
											if(UC_ID.length() != 0 )
											{
												if(!BT25UCCreated)
												{
													DummyDoneFlag = true;
													long CalculatedBalance = CalculateBalance(Resource,UC_Value,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
													UsageCounterList.add(PopulateUsageCounter(UC_ID,Balance_Msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
													//UCCreated.add(UC_ID);
													BT25UCCreated =  true;
													break;
												}
											}
										}		
									}															
								}*/	
							}
						}
						if(!DummyDoneFlag)
						{
							if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M") != null)
							{
								String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getUCID();
								String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getResource();
								String UTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getUTValue();
								
								String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getSymbols();
								String BTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getBTValue();
								
								if(UC_ID.length() > 1 && Resource.length() > 1 && Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BTValue))
								{
									long CalculatedBalance = CalculateBalance(Resource,UTValue,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
									UsageCounterList.add(PopulateUsageCounter(UC_ID,Balance_Msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								}
								if(UC_ID.length() > 1 && Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BTValue))
								{	
									long CalculatedBalance = CalculateBalance(Resource,UTValue,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
									UsageCounterList.add(PopulateUsageCounter(UC_ID,Balance_Msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								}
								if(UC_ID.length() > 1 && Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BTValue))
								{	
									long CalculatedBalance = CalculateBalance(Resource,UTValue,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
									UsageCounterList.add(PopulateUsageCounter(UC_ID,Balance_Msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								}
							}														
						}
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
					}
					if( Balance_ID.equals("759") || Balance_ID.equals("2072") || Balance_ID.equals("1772"))
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
										String UC_ID = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getUCID();
										String UC_Value = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getUTValue();
										String Resource = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getResource();
										
										if(UC_ID.length() != 0 )
										{
											if(!UCCreated.contains(UC_ID))
											{
												UCCreated.add(UC_ID);
												DummyDoneFlag = false;
												long CalculatedBalance = CalculateBalance(Resource,UC_Value,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
												UsageCounterList.add(PopulateUsageCounter(UC_ID,Balance_Msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
											}
										}
									}									
								}
							}
						}
						if(!DummyDoneFlag)
						{
							if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M") != null)
							{
								String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getUCID();
								String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getResource();
								String UTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getUTValue();
								
								String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getSymbols();
								String BTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getBTValue();
								
								if(UC_ID.length() > 1 && Resource.length() > 1 && Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BTValue))
								{
									long CalculatedBalance = CalculateBalance(Resource,UTValue,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
									UsageCounterList.add(PopulateUsageCounter(UC_ID,Balance_Msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								}
								if(UC_ID.length() > 1 && Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BTValue))
								{	
									long CalculatedBalance = CalculateBalance(Resource,UTValue,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
									UsageCounterList.add(PopulateUsageCounter(UC_ID,Balance_Msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));								
								}
								if(UC_ID.length() > 1 && Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BTValue))
								{	
									long CalculatedBalance = CalculateBalance(Resource,UTValue,Balance_Value,Balance_ID,balanceInput.getBEBUCKETID(),UC_ID);
									UsageCounterList.add(PopulateUsageCounter(UC_ID,Balance_Msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));								
								}
							}														
						}
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
					}
				}
				else if(LoadSubscriberMapping.BalanceOnlySpecialASGroupSet.contains(Balance_ID))
				{	
					Map<String,Map<String,List<String>>> ASGroupOfferMap = new HashMap<>();
					ASGroupOfferMap = ComputeASpecialGroup(Balance_ID,CompletedBT_ID);
					
					Map<String,List<String>> OutputDetails = new HashMap<>();
					OutputDetails = ASGroupOfferMap.get("ASOutputDetails");
					if(OutputDetails.containsKey("Counter"))
					{											
						UsageCounterList.addAll(OutputDetails.get("Counter"));											
						CompletedBT_ID.addAll(OutputDetails.get("CompletedBT"));
					}
				}
				else
				{
					String GroupName = "";
					List<String> CurrentGroupBalance = new ArrayList<>();
					List<String> FinalUCList = new ArrayList<>();
					Set<String> ValidGroupBT_ID = new HashSet<>();
					List<String> ValidGroupBalanceCounter = new ArrayList<>();
					//Map<String, String> CurrentGroupBalanceID = new ConcurrentHashMap<>(50, 0.75f, 30);
					boolean ExtraUCFlag = false;
					//Collections.sort(LoadSubscriberMapping.BalanceGroupingMap,new UsageCounter());
					for(Set<String> valueList : LoadSubscriberMapping.BalanceGroupingMap.values()) 
					{					
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
								GroupName =	commonfunction.ComputeAGroup(Balance_ID,GroupName,CompletedBT_ID);
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
							Set<String> PartialGroupList = new HashSet<>();
							for(String id : CurrentGroupBalance)
							{
								for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo TempbalanceInput :  SortedBalanceInput)
								{
									String TempBalance_ID = TempbalanceInput.getBALANCETYPE();
									String TempBalance_Name = TempbalanceInput.getBALANCETYPENAME();
									String TempBalance_Value = TempbalanceInput.getBEBUCKETVALUE();
									String TempBalance_StartDate = TempbalanceInput.getBEBUCKETSTARTDATE();
									String TempBalance_ExpiryDate = TempbalanceInput.getBEEXPIRY();
									String TempBalance_Msisdn = TempbalanceInput.getMSISDN();
									//CurrentGroupBalanceID.put(TempBalance_ID + "," + TempBalance_Value + "," + TempBalance_ExpiryDate, TempbalanceInput.getBEBUCKETID());
									
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
											String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getUCID();
											String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getSymbols();
											String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTValue();
											String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getProductPrivate();
											String UC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getUTValue();
											String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getResource();
											String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTTYPE();
											String PTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getPTName();
											String ExtraUCValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAddUC();
											if(!ExtraUCValue.isEmpty())
											{
												ExtraUCFlag = true;
											}
											else
											{
												ExtraUCValue = "";
											}
											
											if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
											{
												CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
												BEIDForProductID.add(TempbalanceInput.getBEBUCKETID());
												if(PTValue.isEmpty())
												{
													ValidGroupBT_ID.add(TempBalance_ID);
													ValidGroupBalanceCounter.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
													if(!UC_ID.isEmpty())
														FinalUCList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
													break;
												}
												else
												{
													List<String> PT_List = Arrays.asList(PTValue.split("#"));
													List<String> ValidPT = new ArrayList<>();
													for(String pt : PT_List)
													{
														String PT_Name = pt.split("-")[0];
														String PT_Symbol = pt.split("-")[1];
														String PT_Value = pt.split("-")[2];
														String PT_InputValue = profileTag.GetProfileTagValue(PT_Name);
														if(PT_Symbol.equals("=") && PT_InputValue.equals(PT_Value))
														{
															ValidPT.add(PT_Name);
														}
														else
														{
															//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + PT_Name + ":Profile_Tag_Value="+ PT_Value + ":ACTION=Logging");
															break;
														}
													}
													if(ValidPT.size() == PT_List.size())
													{
														ValidGroupBT_ID.add(TempBalance_ID);
														ValidGroupBalanceCounter.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" +  TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
														if(!UC_ID.isEmpty())
															FinalUCList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
														break;
													}													
												}
											}
											else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
											{
												CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
												BEIDForProductID.add(TempbalanceInput.getBEBUCKETID());
												if(PTValue.isEmpty())
												{
													ValidGroupBT_ID.add(TempBalance_ID);
													ValidGroupBalanceCounter.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";"  + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
													if(!UC_ID.isEmpty())
														FinalUCList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";"  + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
													break;
												}
												else
												{
													List<String> PT_List = Arrays.asList(PTValue.split("#"));
													List<String> ValidPT = new ArrayList<>();
													for(String pt : PT_List)
													{
														String PT_Name = pt.split("-")[0];
														String PT_Symbol = pt.split("-")[1];
														String PT_Value = pt.split("-")[2];
														String PT_InputValue = profileTag.GetProfileTagValue(PT_Name);
														if(PT_Symbol.equals("=") && PT_InputValue.equals(PT_Value))
														{
															ValidPT.add(PT_Name);
														}
														else
														{
															//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + PT_Name + ":Profile_Tag_Value="+ PT_Value + ":ACTION=Logging");
															break;
														}
													}
													if(ValidPT.size() == PT_List.size())
													{
														ValidGroupBT_ID.add(TempBalance_ID);
														ValidGroupBalanceCounter.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
														if(!UC_ID.isEmpty())
															FinalUCList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
														break;
													}													
												}
											}
											else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
											{
												CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
												BEIDForProductID.add(TempbalanceInput.getBEBUCKETID());
												if(PTValue.isEmpty())
												{
													ValidGroupBT_ID.add(TempBalance_ID);
													ValidGroupBalanceCounter.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
													if(!UC_ID.isEmpty())
														FinalUCList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";"  + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
													break;
												}
												else
												{
													List<String> PT_List = Arrays.asList(PTValue.split("#"));
													List<String> ValidPT = new ArrayList<>();
													for(String pt : PT_List)
													{
														String PT_Name = pt.split("-")[0];
														String PT_Symbol = pt.split("-")[1];
														String PT_Value = pt.split("-")[2];
														String PT_InputValue = profileTag.GetProfileTagValue(PT_Name);
														if(PT_Symbol.equals("=") && PT_InputValue.equals(PT_Value))
														{
															ValidPT.add(PT_Name);
														}
														else
														{
															//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + PT_Name + ":Profile_Tag_Value="+ PT_Value + ":ACTION=Logging");
															break;
														}
													}
													if(ValidPT.size() == PT_List.size())
													{
														ValidGroupBT_ID.add(TempBalance_ID);
														ValidGroupBalanceCounter.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
														if(!UC_ID.isEmpty())
															FinalUCList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" +  TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
														break;
													}													
												}
											}
											else if(Symbol.equals("or"))
											{
												BEIDForProductID.add(TempbalanceInput.getBEBUCKETID());
												String[] values = BT_Value.split("#");											
												if(Arrays.stream(values).anyMatch(TempBalance_Value::equals))
												{
													CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
													if(PTValue.isEmpty())
													{
														ValidGroupBT_ID.add(TempBalance_ID);
														ValidGroupBalanceCounter.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
														if(!UC_ID.isEmpty())
															FinalUCList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
														break;
													}
													else
													{
														List<String> PT_List = Arrays.asList(PTValue.split("#"));
														List<String> ValidPT = new ArrayList<>();
														for(String pt : PT_List)
														{
															String PT_Name = pt.split("-")[0];
															String PT_Symbol = pt.split("-")[1];
															String PT_Value = pt.split("-")[2];
															String PT_InputValue = profileTag.GetProfileTagValue(PT_Name);
															if(PT_Symbol.equals("=") && PT_InputValue.equals(PT_Value))
															{
																ValidPT.add(PT_Name);
															}
															else
															{
																//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + PT_Name + ":Profile_Tag_Value="+ PT_Value + ":ACTION=Logging");
																break;
															}
														}
														if(ValidPT.size() == PT_List.size())
														{
															ValidGroupBT_ID.add(TempBalance_ID);
															ValidGroupBalanceCounter.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID +  ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
															if(!UC_ID.isEmpty())
																FinalUCList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
															break;
														}													
													}
												}
											}
										}									
									}
								}								
							}
							//Find the BT_ID which is not part of the group for formation of partical group;PartialGroupList
							if(FinalGroupName.startsWith("A-") && ValidGroupBalanceCounter.size() > 0)
							{
								Set<String> outputBT = new HashSet<>();
								ValidGroupBalanceCounter.forEach(x->{outputBT.add(x.split("\\|")[0].split(";")[2]);});
								for(String s : CurrentGroupBalance)
								{
									if(!outputBT.contains(s))
									{
										PartialGroupList.add(s);
									}
								}
							}
							if(FinalGroupName.startsWith("F-") && ValidGroupBalanceCounter.size() != 0)
							{
								if(ValidGroupBalanceCounter.size() == CurrentGroupBalance.size())
								{
									ExtraUCFlag = false;
									//the reason for putting code in catch is some group doesn't have P so in that group check for M 
									//BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer
									for(String UCValue : FinalUCList)
									{
										String UCId = UCValue.split("\\|",-1)[1].split(";",-1)[0];
										if(!UCId.isEmpty())
										{
											String Resource = UCValue.split("\\|")[1].split(";")[2];
											if(Resource.length() > 1)
											{												
												//long CalculatedBalance = CalculateBalance(Resource,(UCValue.split("\\|")[1].split(";")[1]),(UCValue.split("\\|")[0].split(";")[2]),UCValue.split("\\|")[0].split(";")[1],UCValue.split("\\|")[1].split(";")[7],UCValue.split("\\|")[1].split(";")[0]);
												long CalculatedBalance = CalculateBalance(Resource,(UCValue.split("\\|")[1].split(";")[1]),(UCValue.split("\\|")[0].split(";")[3]),UCValue.split("\\|")[0].split(";")[2],UCValue.split("\\|")[1].split(";")[7],UCValue.split("\\|")[1].split(";")[0]);
												UsageCounterList.add(PopulateUsageCounter(UCValue.split("\\|")[1].split(";")[0],UCValue.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), BEIDForProductID));
											}
										}
									}										
								}
								else
								{
									String MPresent = ValidGroupBalanceCounter.stream().filter(item->item.startsWith("M")).findFirst().orElse(null);
									if(MPresent != null)
									{
										if(ValidGroupBalanceCounter.size() == 1 && FinalUCList.size() == 0)
										{
											String GroupLastChar = FinalGroupName.substring(FinalGroupName.lastIndexOf('-')+1,FinalGroupName.length());
											String MasterGroupName = FinalGroupName.replace(GroupLastChar, "M");
											String BT_ID = MPresent.split("\\|")[0].split(";")[2];
											String SourceBT_Value = MPresent.split("\\|")[0].split(";")[3];
											if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName) != null)
											{
												String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getUCID();
												String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getSymbols();
												String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getBTValue();
												
												String UC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getUTValue();
												String MasterResource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getResource();
												String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getBTTYPE();
												boolean bCreateUC = false;
												if(Symbol.equals(">=") && Integer.parseInt(SourceBT_Value) >= Integer.parseInt(BT_Value))
													bCreateUC = true;
												else if(Symbol.equals(">") && Integer.parseInt(SourceBT_Value) > Integer.parseInt(BT_Value))
													bCreateUC = true;
												else if(Symbol.equals("=") && Integer.parseInt(SourceBT_Value) == Integer.parseInt(BT_Value))
													bCreateUC = true;
												
												if(MasterResource.length() > 1 && bCreateUC)
												{
													//String Resourse, String UTVal, String Balance,String balance_ID
													long CalculatedBalance = CalculateBalance(MasterResource,UC_Value,SourceBT_Value,BT_ID,MPresent.split("\\|")[1].split(";")[7],UC_ID);
													UsageCounterList.add(PopulateUsageCounter(UC_ID,MPresent.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), BEIDForProductID));
												}
											}											
										}
										else
										{
											String PCPresent = ValidGroupBalanceCounter.stream().filter(item->item.startsWith("P")).findFirst().orElse(null);
											if(PCPresent == null)
											{
												String GroupLastChar = FinalGroupName.substring(FinalGroupName.lastIndexOf('-')+1,FinalGroupName.length());
												String MasterGroupName = FinalGroupName.replace(GroupLastChar, "M");
												String BT_ID = MPresent.split("\\|")[0].split(";")[2];
												String SourceBT_Value = MPresent.split("\\|")[0].split(";")[3];
												//String Expiry_Value = MPresent.split("\\|")[1].split(";")[5];
												if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName) != null)
												{
													String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getUCID();
													String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getSymbols();
													String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getBTValue();
													
													String UC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getUTValue();
													String MasterResource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getResource();
													String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getBTTYPE();
													boolean bCreateUC = false;
													if(Symbol.equals(">=") && Integer.parseInt(SourceBT_Value) >= Integer.parseInt(BT_Value))
														bCreateUC = true;
													else if(Symbol.equals(">") && Integer.parseInt(SourceBT_Value) > Integer.parseInt(BT_Value))
														bCreateUC = true;
													else if(Symbol.equals("=") && Integer.parseInt(SourceBT_Value) == Integer.parseInt(BT_Value))
														bCreateUC = true;
													
													if(MasterResource.length() > 1 && bCreateUC)
													{
														//String Resourse, String UTVal, String Balance,String balance_ID
														long CalculatedBalance = CalculateBalance(MasterResource,UC_Value,SourceBT_Value,BT_ID,MPresent.split("\\|")[1].split(";")[7],UC_ID);
														UsageCounterList.add(PopulateUsageCounter(UC_ID,MPresent.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), BEIDForProductID));
													}
												}
											}
											else
											{
												ExtraUCFlag = false;
												for(String UCValue : FinalUCList)
												{
													String UC_ID = UCValue.split("\\|",-1)[1].split(";",-1)[0];
													
													if(!UC_ID.isEmpty())
													{
														String TargetOffer = UCValue.split("\\|",-1)[1];
														String Resource = UCValue.split("\\|")[1].split(";")[2];
														if(Resource.length() > 1)
														{
															//String Resourse, String UTVal, String Balance,String balance_ID, String bucket_ID, String Target_ID)
															long CalculatedBalance = CalculateBalance(Resource,(UCValue.split("\\|")[1].split(";")[1]),(UCValue.split("\\|")[0].split(";")[3]),UCValue.split("\\|")[0].split(";")[2],MPresent.split("\\|")[1].split(";")[7],UC_ID);
															UsageCounterList.add(PopulateUsageCounter(UC_ID,UCValue.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), BEIDForProductID));
														}
													}
												}	
											}
										}											
									}	
									else
									{	
										String SPresent = ValidGroupBalanceCounter.stream().filter(item->item.startsWith("S")).findFirst().orElse(null);
										if(SPresent != null)
										{
											if(ValidGroupBalanceCounter.size() == 1 && FinalUCList.size() == 0)
											{
												String GroupLastChar = FinalGroupName.substring(FinalGroupName.lastIndexOf('-')+1,FinalGroupName.length());
												String MasterGroupName = FinalGroupName.replace(GroupLastChar, "M");
												String BT_ID = SPresent.split("\\|")[0].split(";")[2];
												String SourceBT_Value = SPresent.split("\\|")[0].split(";")[3];
												if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName) != null)
												{
													String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getUCID();
													String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getSymbols();
													String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getBTValue();
													
													String UC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getUTValue();
													String MasterResource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getResource();
													String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getBTTYPE();
													boolean bCreateUC = false;
													if(Symbol.equals(">=") && Integer.parseInt(SourceBT_Value) >= Integer.parseInt(BT_Value))
														bCreateUC = true;
													else if(Symbol.equals(">") && Integer.parseInt(SourceBT_Value) > Integer.parseInt(BT_Value))
														bCreateUC = true;
													else if(Symbol.equals("=") && Integer.parseInt(SourceBT_Value) == Integer.parseInt(BT_Value))
														bCreateUC = true;
													
													if(MasterResource.length() > 1 && bCreateUC)
													{
														//String Resourse, String UTVal, String Balance,String balance_ID
														long CalculatedBalance = CalculateBalance(MasterResource,UC_Value,SourceBT_Value,BT_ID,SPresent.split("\\|")[1].split(";")[7],UC_ID);
														UsageCounterList.add(PopulateUsageCounter(UC_ID,SPresent.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), BEIDForProductID));
													}
												}												
											}
										}
									}
								}							 
							}
							if(FinalGroupName.startsWith("A-") && ValidGroupBalanceCounter.size() != 0)
							{
								if(ValidGroupBalanceCounter.size() == CurrentGroupBalance.size())
								{
									ExtraUCFlag = false;
									FinalUCList.forEach(item->{
										String Resource = item.split("\\|")[1].split(";")[2];
										if(Resource.length() > 1)
										{
											long CalculatedBalance = CalculateBalance(Resource,(item.split("\\|")[1].split(";")[1]),(item.split("\\|")[0].split(";")[3]),item.split("\\|")[0].split(";")[2],item.split("\\|")[1].split(";")[7],item.split("\\|")[1].split(";")[0]);
											UsageCounterList.add(PopulateUsageCounter(item.split("\\|")[1].split(";")[0],item.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), BEIDForProductID));
										}										
									});
									//CompletedBT_ID.addAll(CurrentGroupBalance);
								}
								else
								{	
									if(FinalGroupName.equals("A-3") && ValidGroupBalanceCounter.size() == 3)
									{
										UsageCounterList.addAll(PopulateAS2Group(ValidGroupBalanceCounter, BEIDForProductID));
										continue;
									}
									if(LoadSubscriberMapping.BalanceAVGroupLookup.get(FinalGroupName) != null)
									{
										//ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).count()
										if(ValidGroupBalanceCounter.size() >= 2 && ValidGroupBalanceCounter.stream().filter(item->item.startsWith("P")).count() >=1 && ValidGroupBalanceCounter.stream().filter(item->item.startsWith("V")).count() >=1){
											String TargetOffer =  ValidGroupBalanceCounter.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0);
											// Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + Balance_StartDate + ";" + Balance_ExpiryDate  + ";" + Product_Private
											
											FinalUCList.forEach(item->{
												String Resource = item.split("\\|")[1].split(";")[2];
												if(Resource.length() > 1)
												{
													long CalculatedBalance = CalculateBalance(Resource,(item.split("\\|")[1].split(";")[1]),(item.split("\\|")[0].split(";")[3]),item.split("\\|")[0].split(";")[2],item.split("\\|")[1].split(";")[7],item.split("\\|")[1].split(";")[0]);
													UsageCounterList.add(PopulateUsageCounter(item.split("\\|")[1].split(";")[0],item.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), BEIDForProductID));
												}										
											});
											
											//Added code to handle partial product.
											/*if(PartialGroupList.size() > 0)
											{
												for(String BT_ID : PartialGroupList)
												{
													String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + FinalGroupName).getUCID();
													String Balance = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + FinalGroupName).getUTValue();
													UsageCounterList.add(PopulateUsageCounter(UC_ID,msisdn,Balance, BEIDForProductID));
												}
											}*/
											
											ExtraUCFlag = false;
										}
										else if(ValidGroupBalanceCounter.size() >= 2){
											UsageCounterList.addAll(CalculateMasterUsageCounter(ValidGroupBalanceCounter,BEIDForProductID));	
											ExtraUCFlag = false;
										}
										else if(ValidGroupBalanceCounter.size() == 1)
										{
											//BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private
											if(ValidGroupBalanceCounter.stream().filter(item->item.startsWith("P")).count() == 1 ){
												ExtraUCFlag = false;
											}
											if(ValidGroupBalanceCounter.stream().filter(item->item.startsWith("M")).count() == 1){
												UsageCounterList.addAll(CalculateMasterUsageCounter(ValidGroupBalanceCounter,BEIDForProductID));
											}
											if(ValidGroupBalanceCounter.stream().filter(item->item.startsWith("V")).count() == 1){
												UsageCounterList.addAll(CalculateMasterUsageCounter(ValidGroupBalanceCounter,BEIDForProductID));
											}
										}
									}
									else
									{	
										//ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).count()
										if(ValidGroupBalanceCounter.size() >= 2 && ValidGroupBalanceCounter.stream().filter(item->item.startsWith("P")).count() >=1 && ValidGroupBalanceCounter.stream().filter(item->item.startsWith("M")).count() >=1){
											String TargetOffer =  ValidGroupBalanceCounter.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[1];
											// Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + Balance_StartDate + ";" + Balance_ExpiryDate  + ";" + Product_Private
											
											FinalUCList.forEach(item->{
												String Resource = item.split("\\|")[1].split(";")[2];
												if(Resource.length() > 1)
												{
													long CalculatedBalance = CalculateBalance(Resource,(item.split("\\|")[1].split(";")[1]),(item.split("\\|")[0].split(";")[3]),item.split("\\|")[0].split(";")[2],item.split("\\|")[1].split(";")[7],item.split("\\|")[1].split(";")[0]);
													UsageCounterList.add(PopulateUsageCounter(item.split("\\|")[1].split(";")[0],item.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), BEIDForProductID));
												}										
											});
											
											//Added code to handle partial product.
											/*if(PartialGroupList.size() > 0)
											{
												for(String BT_ID : PartialGroupList)
												{
													String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + FinalGroupName).getUCID();
													String Balance = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + FinalGroupName).getUTValue();
													UsageCounterList.add(PopulateUsageCounter(UC_ID,msisdn,Balance, BEIDForProductID));
												}
											}*/
											
											ExtraUCFlag = false;
										}
										else if(ValidGroupBalanceCounter.size() >= 2 &&  ValidGroupBalanceCounter.stream().filter(item->item.startsWith("M")).count() >=1){
											UsageCounterList.addAll(CalculateMasterUsageCounter(ValidGroupBalanceCounter,BEIDForProductID));
										}
										else if(ValidGroupBalanceCounter.size() == 1)
										{
											//BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private
											if(ValidGroupBalanceCounter.stream().filter(item->item.startsWith("P")).count() == 1 ){
												FinalUCList.forEach(item->{
													String Resource = item.split("\\|")[1].split(";")[2];
													if(Resource.length() > 1)
													{
														long CalculatedBalance = CalculateBalance(Resource,(item.split("\\|")[1].split(";")[1]),(item.split("\\|")[0].split(";")[3]),item.split("\\|")[0].split(";")[2],item.split("\\|")[1].split(";")[7],item.split("\\|")[1].split(";")[0]);
														UsageCounterList.add(PopulateUsageCounter(item.split("\\|")[1].split(";")[0],item.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), BEIDForProductID));
													}										
												});
												
												//Added code to handle partial product.
												/*if(PartialGroupList.size() > 0)
												{
													for(String BT_ID : PartialGroupList)
													{
														String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + FinalGroupName).getUCID();
														String Balance = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + FinalGroupName).getUTValue();
														UsageCounterList.add(PopulateUsageCounter(UC_ID,msisdn,Balance, BEIDForProductID));
													}
												}*/
											}
											if(ValidGroupBalanceCounter.stream().filter(item->item.startsWith("M")).count() == 1){
												UsageCounterList.addAll(CalculateMasterUsageCounter(ValidGroupBalanceCounter,BEIDForProductID));
											}
											if(ValidGroupBalanceCounter.stream().filter(item->item.startsWith("X")).count() == 1){
												UsageCounterList.addAll(CalculateMasterUsageCounter(ValidGroupBalanceCounter,BEIDForProductID));
											}
										}
									}									
								}
							}
							if(FinalGroupName.startsWith("C-") || FinalGroupName.startsWith("D-") && ValidGroupBalanceCounter.size() != 0)
							{
								if(ValidGroupBalanceCounter.size() == CurrentGroupBalance.size())
								{
									ExtraUCFlag = false;
									FinalUCList.forEach(item->{
										String Resource = item.split("\\|")[1].split(";")[2];
										if(Resource.length() > 1)
										{
											long CalculatedBalance = CalculateBalance(Resource,(item.split("\\|")[1].split(";")[1]),(item.split("\\|")[0].split(";")[3]),item.split("\\|")[0].split(";")[2],item.split("\\|")[1].split(";")[7],item.split("\\|")[1].split(";")[0]);
											UsageCounterList.add(PopulateUsageCounter(item.split("\\|")[1].split(";")[0],item.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), BEIDForProductID));
										}										
									});
									//CompletedBT_ID.addAll(CurrentGroupBalance);
								}
								else
								{
									UsageCounterList.addAll(CalculateMasterUsageCounter(FinalUCList,BEIDForProductID));
									//CompletedBT_ID.addAll(CurrentGroupBalance);	
								}
							}
							if(ExtraUCFlag)
							{	
								ExtraUCFlag = false;
								for(String s: ValidGroupBalanceCounter)
								{
									if(s.split("\\|")[1].split(";")[6].length() > 0)
									{
										String AddedUC = s.split("\\|")[1].split(";")[6];
										if(!AddedUC.isEmpty())
										{
											String[] ListofAddedUC;
											/*if(AddedUC.contains("\\|"))
												ListofAddedUC = AddedUC.split("\\|");
											else
												ListofAddedUC = new String[]{AddedUC};*/
											ListofAddedUC = AddedUC.split("#");
											
											for(int i = 0; i<ListofAddedUC.length; i++)
											{
												if(ListofAddedUC[i].length() > 1)
												{
													//361-5368709120
													String UC_ID = ListofAddedUC[i].split("-")[0];
													String Balance = ListofAddedUC[i].split("-")[1];
													
													UsageCounterList.add(PopulateUsageCounter(UC_ID,msisdn,String.valueOf(Balance), new HashSet<>()));
													//BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID()
													trackLog.add("INC7003:Add_UC considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + s.split("\\|")[0].split(";")[2] + ":BE_BUCKET_ID="+ s.split("\\|")[1].split(";")[7] + ":UC_ID=" + UC_ID + ":UC_VALUE=" + Balance +":ACTION=Logging");
												}											 
											}
										}
									}
								}
								ExtraUCFlag = false;
							}							
							//Add code here
						}				
					}
				}
			}
		}
		
		//return UsageCounterList.stream().distinct().collect(Collectors.toList());
		return UsageCounterList;
	}
	
	private Collection<? extends String> PopulateAS2Group(List<String> validGroupBalanceCounter,Set<String> bEIDForProductID) 
	{
		List<String> UsageCounterList = new ArrayList<>();
		for(String item : validGroupBalanceCounter)
		{
			String TempBalance_ID = item.split("\\|")[0].split(";")[2];
			if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.containsKey(TempBalance_ID + "|A-S-2"))
			{
				String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-2").getUCID();
				String UC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-2").getUTValue();
				String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-2").getResource();
				
				
				if(!UC_ID.isEmpty())
				{
					long CalculatedBalance = CalculateBalance(Resource,(item.split("\\|")[1].split(";")[1]),(item.split("\\|")[0].split(";")[3]),item.split("\\|")[0].split(";")[2],item.split("\\|")[1].split(";")[7],item.split("\\|")[1].split(";")[0]);
					UsageCounterList.add(PopulateUsageCounter(item.split("\\|")[1].split(";")[0],item.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), bEIDForProductID));
				}
			}
		}
		return UsageCounterList;
	}

	private Collection<? extends String> UsageCounterFromAMBalanceMapping() {
		List<String> FinalBalanceUsageCounter = new ArrayList<>();
		
		//Check if BT 1633 is present this is only BT which comes with AM Group
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{
			String Balance_ID = balanceInput.getBALANCETYPE();	
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			String Balance_StartDate = balanceInput.getBEBUCKETSTARTDATE();
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
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
											ValidAMGroupBalanceOffer.forEach(item1->{GroupBTID.add(item1.split("\\|")[1].split(";")[7]);});
											for(String Str : ValidAMGroupBalanceOffer)
											{
												String SourceValue = Str.split("\\|")[0];
												String TargetValue = Str.split("\\|")[1];											
												if(TargetValue.split(";")[0].length() != 0)
												{
													long CalculatedBalance = CalculateBalance(TargetValue.split(";")[2],TargetValue.split(";")[1],SourceValue.split(";")[3],SourceValue.split(";")[2],TargetValue.split(";")[7],TargetValue.split(";")[0]);
													FinalBalanceUsageCounter.add(PopulateUsageCounter(TargetValue.split(";")[0],SourceValue.split(";")[4],String.valueOf(CalculatedBalance), GroupBTID));
													AMBTIDSet.addAll(GroupBTID);	
												}											
											}													
										}
										AM1633GroupBTs.clear();
										break;
									}
								}
								else
								{
									//Expiry not equal
									//gotSameExpiry = false;
									//AM1633GroupBTs.put(balanceInput.getBEBUCKETID(), Balance_ID + "|" + Balance_Value + "|" + Balance_StartDate + "|" + Balance_ExpiryDate);									
								}								
							}							
						}
						/*if(!gotSameExpiry)
						{
							for(Entry<String,String> item:  AM1633GroupBTs.entrySet())
							{
								String BT_ID = item.getValue().split("\\|")[0].trim();
								String BT_Value = item.getValue().split("\\|")[1].trim();
								String BE_BucketId = item.getKey();
								if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M") != null)
								{
									String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getUCID();
									String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getResource();
									String UTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getUTValue();
									
									String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getSymbols();
									String BTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getBTValue();
									
									if(UC_ID.isEmpty())
										continue;
									if(Symbol.equals(">") && Integer.parseInt(BT_Value) > Integer.parseInt(BTValue))
									{
										AMBTIDSet.add(BE_BucketId);
										long CalculatedBalance = CalculateBalance(Resource,UTValue,BT_Value,BT_ID,BE_BucketId,BT_ID);
										FinalBalanceUsageCounter.add(PopulateUsageCounter(UC_ID,msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(BE_BucketId))));
										continue;
									}
									else if(Symbol.equals(">=") && Integer.parseInt(BT_Value) >= Integer.parseInt(BTValue))
									{
										AMBTIDSet.add(BE_BucketId);
										long CalculatedBalance = CalculateBalance(Resource,UTValue,BT_Value,BT_ID,BE_BucketId,BT_ID);
										FinalBalanceUsageCounter.add(PopulateUsageCounter(UC_ID,msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(BE_BucketId))));
										continue;
									}
									else if(Symbol.equals("=") && Integer.parseInt(BT_Value) == Integer.parseInt(BTValue))
									{
										AMBTIDSet.add(BE_BucketId);
										long CalculatedBalance = CalculateBalance(Resource,UTValue,BT_Value,BT_ID,BE_BucketId,BT_ID);
										FinalBalanceUsageCounter.add(PopulateUsageCounter(UC_ID,msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(BE_BucketId))));
										continue;
									}
									else
									{
										AMBTIDSet.add(BE_BucketId);										
									}
								}								
							}
							break;						
						}*/
					}
				}
			}
		}
		
		//Now check for 1633 BT for its master as the group is not formed above.
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{
			String Balance_ID = balanceInput.getBALANCETYPE();	
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			String Balance_StartDate = balanceInput.getBEBUCKETSTARTDATE();
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			if(AMBTIDSet.contains(balanceInput.getBEBUCKETID()))
				continue;
			
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
						if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M") != null)
						{
							AMBTIDSet.add(balanceInput.getBEBUCKETID());							
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
											
											List<String> GroupBalanceUC = new ArrayList<>();
											List<String> AMGroupName = (commonfunction.getASpecialGroupKey(LoadSubscriberMapping.BalanceOnlySpecialAMGroupMap,TempBalance_ID1));
											for(String GroupName : AMGroupName)
											{
												GroupBalanceUC.clear();
												if(!GroupName.startsWith("A-M"))
												{													
													Set<String> GroupBTItems = LoadSubscriberMapping.BalanceOnlySpecialAMGroupMap.get(GroupName);
													for(Entry<String, String> item : BT_TypeValue.entrySet())
													{
														
														String SourceBTID = item.getKey();
														String SourceBucketID = item.getValue().split("\\|")[0];
														String SourceBTValue = item.getValue().split("\\|")[1];
														String SourceBTStart = item.getValue().split("\\|")[2];
														String SourceBTExpiry = item.getValue().split("\\|")[3];
														if(GroupBTItems.contains(SourceBTID))
														{
															if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName) != null)
															{							
																String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getUCID();
																String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getSymbols();
																String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTValue();
																String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getProductPrivate();
																String UC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getUTValue();
																String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getResource();
																String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTTYPE();
																String ExtraUCValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getAddUC();
																if(Symbol.equals(">=") && Integer.parseInt(SourceBTValue) >= Integer.parseInt(BT_Value))
																{	
																	GroupBalanceUC.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue + "|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + ExtraUCValue + ";" + SourceBucketID);
																	continue;
																}
																else if(Symbol.equals(">") && Integer.parseInt(SourceBTValue) > Integer.parseInt(BT_Value))
																{
																	GroupBalanceUC.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue + "|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + ExtraUCValue + ";" + SourceBucketID);
																	continue;
																}
																else if(Symbol.equals("=") && Integer.parseInt(SourceBTValue) == Integer.parseInt(BT_Value))
																{
																	GroupBalanceUC.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue + "|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + ExtraUCValue + ";" + SourceBucketID);
																	continue;
																}
																else if(Symbol.equals("or"))
																{
																	//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
																	String[] values = BT_Value.split("#");											
																	if(Arrays.stream(values).anyMatch(SourceBTValue::equals))
																	{
																		GroupBalanceUC.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue + "|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + ExtraUCValue + ";" + SourceBucketID);
																		continue;
																	}																	
																}																	
															}
														}
													}
													if(GroupBalanceUC.size() == GroupBTItems.size())
													{
														Set<String> GroupBTID = new HashSet<>();
														gotSameExpiry = true;
														GroupBalanceUC.forEach(item1->{GroupBTID.add(item1.split("\\|")[1].split(";")[7]);});
														for(String Str : GroupBalanceUC)
														{
															String SourceValue = Str.split("\\|")[0];
															String TargetValue = Str.split("\\|")[1];											
															if(TargetValue.split(";")[0].length() != 0)
															{
																long CalculatedBalance = CalculateBalance(TargetValue.split(";")[2],TargetValue.split(";")[1],SourceValue.split(";")[3],SourceValue.split(";")[2],TargetValue.split(";")[7],TargetValue.split(";")[0]);
																FinalBalanceUsageCounter.add(PopulateUsageCounter(TargetValue.split(";")[0],msisdn,String.valueOf(CalculatedBalance), GroupBTID));
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
												List<String> ValidAMGroupBalanceUC = item.getValue();
												Set<String> GroupBTID = new HashSet<>();
												ValidAMGroupBalanceUC.forEach(item1->{GroupBTID.add(item1.split("\\|")[1].split(";")[7]);});
												for(String Str : ValidAMGroupBalanceUC)
												{
													String SourceValue = Str.split("\\|")[0];
													String TargetValue = Str.split("\\|")[1];											
													if(TargetValue.split(";")[0].length() != 0)
													{
														long CalculatedBalance = CalculateBalance(TargetValue.split(";")[2],TargetValue.split(";")[1],SourceValue.split(";")[3],SourceValue.split(";")[2],TargetValue.split(";")[7],TargetValue.split(";")[0]);
														FinalBalanceUsageCounter.add(PopulateUsageCounter(TargetValue.split(";")[0],msisdn,String.valueOf(CalculatedBalance), GroupBTID));
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
								
								List<String> GroupBalanceUC = new ArrayList<>();
								List<String> AMGroupName = (commonfunction.getASpecialGroupKey(LoadSubscriberMapping.BalanceOnlySpecialAMGroupMap,TempBalance_ID1));
								for(String GroupName : AMGroupName)
								{
									GroupBalanceUC.clear();
									if(!GroupName.startsWith("A-M"))
									{													
										Set<String> GroupBTItems = LoadSubscriberMapping.BalanceOnlySpecialAMGroupMap.get(GroupName);
										for(Entry<String, String> item : BT_TypeValue.entrySet())
										{														
											String SourceBTID = item.getKey();
											String SourceBucketID = item.getValue().split("\\|")[0];
											String SourceBTValue = item.getValue().split("\\|")[1];
											String SourceBTStart = item.getValue().split("\\|")[2];
											String SourceBTExpiry = item.getValue().split("\\|")[3];
											boolean ExtraOfferFlag = false;
											if(GroupBTItems.contains(SourceBTID))
											{
												if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName) != null)
												{							
													String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getUCID();
													String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getSymbols();
													String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTValue();
													String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getProductPrivate();
													String UC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getUTValue();
													String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getResource();
													String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTTYPE();
													String ExtraUCValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getAddUC();
													if(Symbol.equals(">=") && Integer.parseInt(SourceBTValue) >= Integer.parseInt(BT_Value))
													{	
														GroupBalanceUC.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue + "|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + ExtraUCValue + ";" + SourceBucketID);
														continue;
													}
													else if(Symbol.equals(">") && Integer.parseInt(SourceBTValue) > Integer.parseInt(BT_Value))
													{
														GroupBalanceUC.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue + "|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + ExtraUCValue + ";" + SourceBucketID);
														continue;
													}
													else if(Symbol.equals("=") && Integer.parseInt(SourceBTValue) == Integer.parseInt(BT_Value))
													{
														GroupBalanceUC.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue + "|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + ExtraUCValue + ";" + SourceBucketID);
														continue;
													}
													else if(Symbol.equals("or"))
													{
														//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
														String[] values = BT_Value.split("#");											
														if(Arrays.stream(values).anyMatch(SourceBTValue::equals))
														{
															GroupBalanceUC.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue + "|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + ExtraUCValue + ";" + SourceBucketID);
															continue;
														}
														else
														{
															AMBTIDSet.add(SourceBucketID);
														}
													}
												}
											}
										}
										if(GroupBalanceUC.size() == 2)
										{
											Set<String> GroupBTID = new HashSet<>();
											GroupBalanceUC.forEach(item1->{GroupBTID.add(item1.split("\\|")[1].split(";")[7]);});
											for(String Str : GroupBalanceUC)
											{
												String SourceValue = Str.split("\\|")[0];
												String TargetValue = Str.split("\\|")[1];											
												if(TargetValue.split(";")[0].length() != 0)
												{
													long CalculatedBalance = CalculateBalance(TargetValue.split(";")[2],TargetValue.split(";")[1],SourceValue.split(";")[3],SourceValue.split(";")[2],TargetValue.split(";")[7],TargetValue.split(";")[0]);
													FinalBalanceUsageCounter.add(PopulateUsageCounter(TargetValue.split(";")[0],msisdn,String.valueOf(CalculatedBalance), GroupBTID));
														
												}
												AMBTIDSet.addAll(GroupBTID);
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
						if(!gotSameExpiry)
						{
							for(Entry<String,String> item:  BT_TypeValue.entrySet())
							{
								String BT_ID = item.getKey();
								String BT_Value = item.getValue().split("\\|")[1].trim();
								String BT_StartDate = item.getValue().split("\\|")[2].trim();
								String BT_ExpiryDate = item.getValue().split("\\|")[3].trim();
								String BE_BucketId = item.getValue().split("\\|")[0].trim();
								if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M") != null)
								{
									String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getUCID();
									String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getResource();
									String UTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getUTValue();
									
									String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getSymbols();
									String BTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getBTValue();
									
									if(UC_ID.isEmpty())
									{
										AMBTIDSet.add(BE_BucketId);
										continue;
									}
									if(Symbol.equals(">") && Integer.parseInt(BT_Value) > Integer.parseInt(BTValue))
									{
										AMBTIDSet.add(BE_BucketId);
										long CalculatedBalance = CalculateBalance(Resource,UTValue,BT_Value,BT_ID,BE_BucketId,BT_ID);
										FinalBalanceUsageCounter.add(PopulateUsageCounter(UC_ID,msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(BE_BucketId))));
										continue;
									}
									else if(Symbol.equals(">=") && Integer.parseInt(BT_Value) >= Integer.parseInt(BTValue))
									{
										AMBTIDSet.add(BE_BucketId);
										long CalculatedBalance = CalculateBalance(Resource,UTValue,BT_Value,BT_ID,BE_BucketId,BT_ID);
										FinalBalanceUsageCounter.add(PopulateUsageCounter(UC_ID,msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(BE_BucketId))));
										continue;
									}
									else if(Symbol.equals("=") && Integer.parseInt(BT_Value) == Integer.parseInt(BTValue))
									{
										AMBTIDSet.add(BE_BucketId);
										long CalculatedBalance = CalculateBalance(Resource,UTValue,BT_Value,BT_ID,BE_BucketId,BT_ID);
										FinalBalanceUsageCounter.add(PopulateUsageCounter(UC_ID,msisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(BE_BucketId))));
										continue;
									}
									else
									{
										AMBTIDSet.add(BE_BucketId);										
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
			String SourceBTID = balanceInput.getBALANCETYPE();
			String SourceBTValue = balanceInput.getBALANCETYPE();
			String BE_BucketId = balanceInput.getBEBUCKETID();
			if(AMBTIDSet.contains(balanceInput.getBEBUCKETID()))
				continue;
			if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(SourceBTID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(SourceBTID).equals("Y"))
			{
				AMBTIDSet.add(balanceInput.getBEBUCKETID());
				continue;
			}
		
			if(!balanceInput.getBEEXPIRY().equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(balanceInput.getBEEXPIRY()) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
			{
				//INC4001	Balance_Type expired	MSISDN,BALANCE_TYPE,BE_BUCKET_VALUE,BE_BUCKET_ID,BE_EXPIRY
				AMBTIDSet.add(balanceInput.getBEBUCKETID());
				continue;
			}
			if(LoadSubscriberMapping.ExceptionBalancesPCForAMGroup.contains(SourceBTID))
			{
				String GroupName = "";
				
				for(Set<String> valueList : LoadSubscriberMapping.BalanceGroupingMap.values()) 
				{					
					if(valueList.contains(SourceBTID)){
						GroupName = commonfunction.getKey(LoadSubscriberMapping.BalanceGroupingMap, valueList);
					}
				}
				
				if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName) != null)
				{
					String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getResource();
					String UTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getUTValue();
					String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getSymbols();
					String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTValue();
					String AddUC = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getAddUC();
					
					boolean CreateAddedUC = false;
					
					if(Symbol.equals(">=") && Integer.parseInt(SourceBTValue) >= Integer.parseInt(BT_Value))
					{	
						CreateAddedUC = true;
					}
					else if(Symbol.equals(">") && Integer.parseInt(SourceBTValue) > Integer.parseInt(BT_Value))
					{
						CreateAddedUC = true;
					}
					else if(Symbol.equals("=") && Integer.parseInt(SourceBTValue) == Integer.parseInt(BT_Value))
					{
						CreateAddedUC = true;
					}
					else if(Symbol.equals("or"))
					{
						//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
						String[] values = BT_Value.split("#");											
						if(Arrays.stream(values).anyMatch(SourceBTValue::equals))
						{
							CreateAddedUC = true;
						}																	
					}		
					
					if(!AddUC.isEmpty() && CreateAddedUC)
					{
						String[] ListofAddedUC = AddUC.split("#");
						
						for(int i = 0; i<ListofAddedUC.length; i++)
						{
							if(ListofAddedUC[i].length() > 1)
							{
								//361-5368709120
								String AddUC_ID = ListofAddedUC[i].split("-")[0];
								String AddBalance = ListofAddedUC[i].split("-")[1];
								if(AddBalance.equals("BT_Value"))
								{
									long CalculatedBalance = CalculateBalance(Resource,UTValue,BT_Value,SourceBTID,BE_BucketId,AddUC_ID);
									AddBalance = String.valueOf(CalculatedBalance);
								}
								FinalBalanceUsageCounter.add(PopulateUsageCounter(AddUC_ID,msisdn,String.valueOf(AddBalance), new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								//BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID()
								trackLog.add("INC7003:Add_UC considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceBTID + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID()  + ":UC_ID=" + AddUC_ID + ":UC_VALUE=" + AddBalance + ":ACTION=Logging");
							}
						}
						AMBTIDSet.add(BE_BucketId);										
					}
				}
				else
				{
					AMBTIDSet.add(BE_BucketId);					
				}
			}
		}
		
		return FinalBalanceUsageCounter;
	}
	
	public Map<String,Map<String,List<String>>>  ComputeASpecialGroup(String inputBalance_ID, Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub Map<String,List<String>>
		
		List<String> ASGroupName = new ArrayList<>();		
		List<String> ValidGroupBalanceCounter = new ArrayList<>();
		List<String> FinalBalanceUC = new ArrayList<>();
		Set<String> ASBT_ID = new HashSet<>();
		boolean ExtraUCFlag = false;
		boolean ASGroupFormed = false;
		Map<String,List<String>> tempGroupBalanceOffer = new HashMap<>();
		List<String> FinalUCList = new ArrayList<>();
		
		Map<String,Map<String,List<String>>> ASGroupOfferMap = new HashMap<>();
		ASGroupName = (commonfunction.getASpecialGroupKey(LoadSubscriberMapping.BalanceOnlySpecialASGroupMap,inputBalance_ID));

		//CheckIf A-S-1 is present in the input
		//Map<String,Map<String,List<String>>> ASGroupOfferMap = new HashMap<>();
		ASGroupOfferMap = CheckifA_S_1Present(CompletedBT_ID);
		
		Map<String,List<String>> AS1_OutputDetails = new HashMap<>();
		AS1_OutputDetails = ASGroupOfferMap.get("ASOutputDetails");
		if(AS1_OutputDetails.containsKey("Counter"))
		{											
			FinalBalanceUC.addAll(AS1_OutputDetails.get("Counter"));
		}
		if(AS1_OutputDetails.containsKey("CompletedBT"))
		{												
			CompletedBT_ID.addAll(AS1_OutputDetails.get("CompletedBT"));			
		}
		
		for(String GroupName : ASGroupName)
		{
			Set<String> GroupElements = new HashSet<>();
			if(!GroupName.startsWith("A-S"))
			{
				ValidGroupBalanceCounter.clear();
				ASBT_ID.clear();
				FinalUCList.clear();
				Set<String> GroupBTItems = LoadSubscriberMapping.BalanceOnlySpecialASGroupMap.get(GroupName);
				for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo TempbalanceInput : SortedBalanceInput)
				{
					String TempBalance_ID = TempbalanceInput.getBALANCETYPE();
					String TempBalance_Value = TempbalanceInput.getBEBUCKETVALUE();
					String TempBalance_StartDate = TempbalanceInput.getBEBUCKETSTARTDATE();
					String TempBalance_ExpiryDate = TempbalanceInput.getBEEXPIRY();
					if(CompletedBT_ID.contains(TempbalanceInput.getBEBUCKETID()))
						continue;
					
					if(GroupBTItems.contains(TempBalance_ID))
					{
						if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName) != null)
						{							
							String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getUCID();
							String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getSymbols();
							String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getBTValue();
							String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getProductPrivate();
							String UC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getUTValue();
							String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getResource();
							String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getBTTYPE();
							
							String ExtraUCValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getAddUC();
							if(!ExtraUCValue.isEmpty())
							{
								ExtraUCFlag = true;
							}
							else
							{
								ExtraUCValue = "";
							}
							
							if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
							{
								if(!GroupElements.contains(TempBalance_ID))
								{
									GroupElements.add(TempBalance_ID);
									ASBT_ID.add(TempbalanceInput.getBEBUCKETID());
									ValidGroupBalanceCounter.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempbalanceInput.getMSISDN() +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
									if(!UC_ID.isEmpty())
										FinalUCList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempbalanceInput.getMSISDN() +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
									continue;
								}
							}
							else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
							{
								if(!GroupElements.contains(TempBalance_ID))
								{
									GroupElements.add(TempBalance_ID);
									ASBT_ID.add(TempbalanceInput.getBEBUCKETID());
									ValidGroupBalanceCounter.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempbalanceInput.getMSISDN() +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
									if(!UC_ID.isEmpty())
										FinalUCList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempbalanceInput.getMSISDN() +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
									continue;
								}
							}
							else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
							{
								if(!GroupElements.contains(TempBalance_ID))
								{
									GroupElements.add(TempBalance_ID);
									ASBT_ID.add(TempbalanceInput.getBEBUCKETID());
									ValidGroupBalanceCounter.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempbalanceInput.getMSISDN() +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
									if(!UC_ID.isEmpty())
										FinalUCList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempbalanceInput.getMSISDN() +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
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
										GroupElements.add(TempBalance_ID);
										ASBT_ID.add(TempbalanceInput.getBEBUCKETID());
										ValidGroupBalanceCounter.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempbalanceInput.getMSISDN() +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
										if(!UC_ID.isEmpty())
											FinalUCList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempbalanceInput.getMSISDN() +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
										continue;
									}
								}
							}												
						}						
					}
				}
				if(ValidGroupBalanceCounter.size() == GroupBTItems.size())
				{
					ASGroupFormed = true;
					break;					
				}
				else
				{
					List<String> temp = new ArrayList<>( ValidGroupBalanceCounter);
					tempGroupBalanceOffer.put(GroupName, temp);
				}
			}
		}
		if(ASGroupFormed)
		{
			ExtraUCFlag = false;
			FinalUCList.forEach(item->{
				String Resource = item.split("\\|")[1].split(";")[2];
				if(Resource.length() > 1)
				{
					long CalculatedBalance = CalculateBalance(Resource,(item.split("\\|")[1].split(";")[1]),(item.split("\\|")[0].split(";")[3]),item.split("\\|")[0].split(";")[2],item.split("\\|")[1].split(";")[7],item.split("\\|")[1].split(";")[0]);
					FinalBalanceUC.add(PopulateUsageCounter(item.split("\\|")[1].split(";")[0],item.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), ASBT_ID));
				}										
			});
		}
		else
		{
			List<String> ValidGroupBalanceOffer = Collections.max(tempGroupBalanceOffer.entrySet(), (entry1, entry2) -> entry1.getValue().size() - entry2.getValue().size()).getValue();
			ASBT_ID.clear();
			for(String Str : ValidGroupBalanceOffer)
			{
				String TargetValue = Str.split("\\|")[0];
				String SourceValue = Str.split("\\|")[1];
		
				if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetValue.split(";")[2] + "|M") != null)
				{
					if(TargetValue.split(";")[2].equals("74"))
					{
						String ProfileTag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetValue.split(";")[2] + "|M").getPTName().trim();
						
						if(!ProfileTag.isEmpty())
						{
							List<String> PT_List = Arrays.asList(ProfileTag);
							List<String> ValidPT = new ArrayList<>();
							for(String pt : PT_List)
							{
								String PT_Name = pt.split("-")[0];
								String PT_Symbol = pt.split("-")[1];
								String PT_Value = pt.split("-")[2];
								String PT_InputValue = profileTag.GetProfileTagValue(PT_Name);
								if(PT_Symbol.equals("=") && PT_InputValue.equals(PT_Value))
								{
									ValidPT.add(PT_Name);
								}
							}
							if(ValidPT.size() == PT_List.size())
							{
								String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetValue.split(";")[2] + "|M").getUCID();
								String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetValue.split(";")[2] + "|M").getResource();
								String UTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetValue.split(";")[2] + "|M").getUTValue();
								
								String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetValue.split(";")[2] + "|M").getSymbols();
								String BTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetValue.split(";")[2] + "|M").getBTValue();
								
								if(UC_ID.length() > 1 && Resource.length() > 1 && Symbol.equals(">") && Integer.parseInt((Str.split("\\|")[0].split(";")[3])) > Integer.parseInt(BTValue))
								{
									long CalculatedBalance = CalculateBalance(Resource,UTValue,Str.split("\\|")[0].split(";")[3],Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[7],Str.split("\\|")[1].split(";")[0]);
									FinalBalanceUC.add(PopulateUsageCounter(UC_ID,Str.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(Str.split("\\|")[1].split(";")[7]))));
								}	
								if(UC_ID.length() > 1 && Resource.length() > 1 && Symbol.equals(">=") && Integer.parseInt((Str.split("\\|")[0].split(";")[3])) >= Integer.parseInt(BTValue))
								{
									long CalculatedBalance = CalculateBalance(Resource,UTValue,(Str.split("\\|")[0].split(";")[3]),Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[7],Str.split("\\|")[1].split(";")[0]);
									FinalBalanceUC.add(PopulateUsageCounter(UC_ID,Str.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(Str.split("\\|")[1].split(";")[7]))));
								}	
								if(UC_ID.length() > 1 && Resource.length() > 1 && Symbol.equals("=") && Integer.parseInt((Str.split("\\|")[0].split(";")[3])) == Integer.parseInt(BTValue))
								{
									long CalculatedBalance = CalculateBalance(Resource,UTValue,(Str.split("\\|")[0].split(";")[3]),Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[7],Str.split("\\|")[1].split(";")[0]);
									FinalBalanceUC.add(PopulateUsageCounter(UC_ID,Str.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(Str.split("\\|")[1].split(";")[7]))));
								}	
								ASBT_ID.add(Str.split("\\|")[1].split(";")[7]);
							}
						}
						else
						{						
							String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetValue.split(";")[2] + "|M").getUCID();
							String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetValue.split(";")[2] + "|M").getResource();
							String UTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetValue.split(";")[2] + "|M").getUTValue();
							
							String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetValue.split(";")[2] + "|M").getSymbols();
							String BTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetValue.split(";")[2] + "|M").getBTValue();
							
							if(UC_ID.length() > 1 && Resource.length() > 1 && Symbol.equals(">") && Integer.parseInt((Str.split("\\|")[0].split(";")[3])) > Integer.parseInt(BTValue))
							{
								long CalculatedBalance = CalculateBalance(Resource,UTValue,(Str.split("\\|")[0].split(";")[3]),Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[7],UC_ID);
								FinalBalanceUC.add(PopulateUsageCounter(UC_ID,Str.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(Str.split("\\|")[1].split(";")[7]))));
							}
							if(UC_ID.length() > 1 && Resource.length() > 1 && Symbol.equals(">=") && Integer.parseInt((Str.split("\\|")[0].split(";")[3])) >= Integer.parseInt(BTValue))
							{
								long CalculatedBalance = CalculateBalance(Resource,UTValue,(Str.split("\\|")[0].split(";")[3]),Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[7],UC_ID);
								FinalBalanceUC.add(PopulateUsageCounter(UC_ID,Str.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(Str.split("\\|")[1].split(";")[7]))));
							}
							if(UC_ID.length() > 1 && Resource.length() > 1 && Symbol.equals("=") && Integer.parseInt((Str.split("\\|")[0].split(";")[3])) == Integer.parseInt(BTValue))
							{
								long CalculatedBalance = CalculateBalance(Resource,UTValue,(Str.split("\\|")[0].split(";")[3]),Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[7],UC_ID);
								FinalBalanceUC.add(PopulateUsageCounter(UC_ID,Str.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(Str.split("\\|")[1].split(";")[7]))));
							}
							ASBT_ID.add(Str.split("\\|")[1].split(";")[7]);
						}
					}															
				}
			}
		}
		
		Map<String,List<String>> ASOutputDetails = new HashMap<>();
		if(ASBT_ID.size() != 0 || AS1_OutputDetails.containsKey("CompletedBT"))
		{
			if(AS1_OutputDetails.containsKey("CompletedBT"))
				ASBT_ID.addAll(AS1_OutputDetails.get("CompletedBT"));
			ASOutputDetails.put("CompletedBT", new ArrayList<String>(ASBT_ID));
		}
		if(FinalBalanceUC.size() != 0)
			ASOutputDetails.put("Counter", FinalBalanceUC);
	
		ASGroupOfferMap.put("ASOutputDetails", ASOutputDetails);
		
		return ASGroupOfferMap;
	}
	
	public Map<String,Map<String,List<String>>> CheckifA_S_1Present(Set<String> CompletedBT_ID)
	{
		boolean ExtraOfferFlag = false;
		List<String> ValidGroupBalanceCounter = new ArrayList<>();
		List<String> FinalBalanceUC = new ArrayList<>();
		List<String> FinalUCList = new ArrayList<>();
		Set<String> ASBT_ID = new HashSet<>();
		Map<String,Map<String,List<String>>> ASGroupOfferMap = new HashMap<>();
		
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{
			String TempBalance_ID = balanceInput.getBALANCETYPE();
			String TempBalance_Value = balanceInput.getBEBUCKETVALUE();
			String TempBalance_StartDate = balanceInput.getBEBUCKETSTARTDATE();
			String TempBalance_ExpiryDate = balanceInput.getBEEXPIRY();
			if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
				continue;
			
			if(TempBalance_ID.equals("1035") && TempBalance_Value.equals("2"))
			{
				String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getUCID();
				String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getSymbols();
				String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getBTValue();
				String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getProductPrivate();
				String UC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getUTValue();
				String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getResource();
				String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getBTTYPE();
				
				String ExtraUCValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getAddUC();
				
				ASBT_ID.add(balanceInput.getBEBUCKETID());
				ValidGroupBalanceCounter.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + balanceInput.getMSISDN() +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + balanceInput.getBEBUCKETID());
				if(!UC_ID.isEmpty())
					FinalUCList.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + balanceInput.getMSISDN() +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + balanceInput.getBEBUCKETID());
			}
			if(TempBalance_ID.equals("74") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt("-999999"))
			{
				
				String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getUCID();
				String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getSymbols();
				String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getBTValue();
				String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getProductPrivate();
				String UC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getUTValue();
				String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getResource();
				String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getBTTYPE();
				
				String ExtraUCValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getAddUC();
				
				ASBT_ID.add(balanceInput.getBEBUCKETID());

				ValidGroupBalanceCounter.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + balanceInput.getMSISDN() +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + balanceInput.getBEBUCKETID());
				if(!UC_ID.isEmpty())
					FinalUCList.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + balanceInput.getMSISDN() +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + balanceInput.getBEBUCKETID());	
			}
		}
		
		if(ValidGroupBalanceCounter.size() == 2)
		{
			if(ValidGroupBalanceCounter.stream().filter(x->x.contains("Blackberry Bundle")).count() == 1 && ValidGroupBalanceCounter.stream().filter(x->x.contains("Blackberry KB")).count() == 1)
			{
				for(String Str : FinalUCList)
				{
					
					//M;Blackberry Bundle;1035;2|6014;Timer;false;false;;1970-01-01 00:00:00;Yes;NULL;;752817472
					String TargetValue = Str.split("\\|")[0];
					String SourceValue = Str.split("\\|")[1];
					if(!SourceValue.split(";")[0].isEmpty())
					{
						long CalculatedBalance = CalculateBalance(Str.split("\\|")[1].split(";")[2],(Str.split("\\|")[1].split(";")[1]),(Str.split("\\|")[0].split(";")[3]),Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[7],Str.split("\\|")[1].split(";")[0]);
						String Product_ID = "0";
						FinalBalanceUC.add(PopulateUsageCounter(Str.split("\\|")[1].split(";")[0],Str.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(Str.split("\\|")[1].split(";")[7]))));
						//FinalBalanceUC.add(msisdn +","+ Str.split("\\|")[1].split(";")[0] +","+ Str.split("\\|")[0].split(";")[4] +","+ CalculatedBalance +","+ Product_ID +","+ CalculatedBalance +",0");
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
			if(ValidGroupBalanceCounter.size() == 1 && ValidGroupBalanceCounter.stream().filter(x->x.contains("Blackberry Bundle")).count() == 1)
			{
				
			}
			else
				ASBT_ID.clear();			
		}
		
		Map<String,List<String>> ASOutputDetails = new HashMap<>();
		if(ASBT_ID.size() != 0)
			ASOutputDetails.put("CompletedBT", new ArrayList<String>(ASBT_ID));
		if(FinalBalanceUC.size() != 0)
			ASOutputDetails.put("Counter", FinalBalanceUC);
	
		ASGroupOfferMap.put("ASOutputDetails", ASOutputDetails);
		
		return ASGroupOfferMap;
	}

	public Map<String,List<String>> ComputeAMGroup(Map<String, String> AMBalanceBT, Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub
	
		String FinalGroupName ="";
		String ComputedGroupName = "";
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
				ComputedGroupName = FinalGroupName; 
				break;
			}
		}
		
		
		List<String> ValidGroupBalanceCounter = new ArrayList<>();
		Map<String,List<String>> AMGroupDAMap = new HashMap<>();
		if(ComputedGroupName.length() != 0)
		{	
			boolean ExtraUCFlag = false;
			boolean ExtraDAFlag = false;
			for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
			{
				String TempBalance_ID = balanceInput.getBALANCETYPE();
				if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName) != null)
				{
					if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
						continue;
				
					if(AMBalanceBT.containsKey(balanceInput.getBEBUCKETID()))
					{
						String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getUCID();
						String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getSymbols();
						String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getBTValue();
						String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getProductPrivate();
						String UC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getUTValue();
						String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getResource();
						String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getBTTYPE();
						
						String ExtraUCValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getAddUC();
						if(!ExtraUCValue.isEmpty())
						{
							ExtraUCFlag = true;
						}
						else
						{
							ExtraUCValue = "";
						}
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						
						ValidGroupBalanceCounter.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + balanceInput.getBEBUCKETVALUE() + ";" + balanceInput.getMSISDN() +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + balanceInput.getBEBUCKETSTARTDATE() + ";" + balanceInput.getBEEXPIRY()  + ";" + Product_Private + ";" + ExtraUCValue + ";" + balanceInput.getBEBUCKETID());
					}					
				}
			}			
			AMGroupDAMap.put(FinalGroupName + "|" + AMBTValue, ValidGroupBalanceCounter);
		}
		
		return AMGroupDAMap;
	}
	
	private List<String> CalculateMasterUsageCounter(List<String> ValidGroupBalanceCounter, Set<String> BEIDForProductID)
	{
		List<String> UsageCounterList = new ArrayList<>();
		for(String Str : ValidGroupBalanceCounter)
		{
			String SourceValue =  Str.split("\\|")[0];
			String TargetValue = Str.split("\\|")[1];
			if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[2] + "|M") != null)
			{
				String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[2] + "|M").getUCID();
				String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[2] + "|M").getResource();
				String UTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[2] + "|M").getUTValue();
				
				String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[2] + "|M").getSymbols();
				String BTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[2] + "|M").getBTValue();
				
				if(UC_ID.length() > 1 && Resource.length() > 1 && Symbol.equals(">") && Integer.parseInt((Str.split("\\|")[0].split(";")[3])) > Integer.parseInt(BTValue))
				{
					long CalculatedBalance = CalculateBalance(Resource,UTValue,(Str.split("\\|")[0].split(";")[3]),Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[7],UC_ID);
					UsageCounterList.add(PopulateUsageCounter(UC_ID,Str.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(TargetValue.split(";")[7]))));
				}
				if(UC_ID.length() > 1 && Resource.length() > 1 && Symbol.equals(">=") && Integer.parseInt((Str.split("\\|")[0].split(";")[3])) >= Integer.parseInt(BTValue))
				{
					long CalculatedBalance = CalculateBalance(Resource,UTValue,(Str.split("\\|")[0].split(";")[3]),Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[7],UC_ID);
					UsageCounterList.add(PopulateUsageCounter(UC_ID,Str.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(TargetValue.split(";")[7]))));
				}
				if(UC_ID.length() > 1 && Resource.length() > 1 && Symbol.equals("=") && Integer.parseInt((Str.split("\\|")[0].split(";")[3])) == Integer.parseInt(BTValue))
				{
					long CalculatedBalance = CalculateBalance(Resource,UTValue,(Str.split("\\|")[0].split(";")[3]),Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[7],UC_ID);
					UsageCounterList.add(PopulateUsageCounter(UC_ID,Str.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(TargetValue.split(";")[7]))));
				}
				if(Symbol.equals("or"))
				{
					String[] values = BTValue.split("#");											
					if(Arrays.stream(values).anyMatch(TargetValue.split(";")[3]::equals))
					{
						long CalculatedBalance = CalculateBalance(Resource,UTValue,(Str.split("\\|")[0].split(";")[3]),Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[7],UC_ID);
						UsageCounterList.add(PopulateUsageCounter(UC_ID,Str.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(TargetValue.split(";")[7]))));
					}					
				}
			}
		}
		return UsageCounterList;
	}
	
	public long CalculateBalance(String Resource, String UTVal, String Balance,String balance_ID, String bucket_ID, String Target_ID)
	{
		String TotalBalance = "";
		if(Resource.equals("DATO2"))
		{
			return 0;
		}
		String tempBalance_Value = "";
		boolean NegativeBalance = false;
		
		if(Long.parseLong(Balance) < 0)
		{
			tempBalance_Value = Balance;
			NegativeBalance = true;
			Balance = "0";
		}
		
		long UTValue = Long.parseLong(UTVal);
		long Balance_Input = Long.parseLong(Balance);
		BigDecimal CalculatedValue = null;
		if(Resource.equals("TOUC1")){
			CalculatedValue = new BigDecimal(UTValue - (Balance_Input * 1024));
		}
		else if(Resource.equals("TOUC2")){
			CalculatedValue = new BigDecimal((UTValue - (Balance_Input/100.0)));
		}
		else if(Resource.equals("TOUC3")){
			CalculatedValue = new BigDecimal(UTValue - Balance_Input);
		}
		else if(Resource.equals("TOUC4")){
			CalculatedValue = new BigDecimal(UTValue - ((Balance_Input- 10000)*1048576));
		}
		else if(Resource.equals("UC")){
			CalculatedValue = new BigDecimal(UTValue - (Balance_Input/10000.0));
		}
		else if(Resource.equals("UCTO1")){
			CalculatedValue = new BigDecimal(Balance_Input * 100);
		}
		else if(Resource.equals("UCTO2")){
			CalculatedValue = new BigDecimal(Balance_Input);
		}
		else if(Resource.equals("TOUC")){
		}
		
		String RoundOff = LoadSubscriberMapping.ConversionLogicMap.get(Resource).split("\\|")[2];
		if(RoundOff.equals("Y"))
		{			
			if(LoadSubscriberMapping.CommonConfigMap.get("Enable_Logging_for_RoundOff").equals("Y"))
			{	
				long roundedBal = (Long.parseLong(CalculatedValue.setScale(0, RoundingMode.HALF_UP).toPlainString()));
				
				double diffValue = 0;
				
				diffValue = roundedBal - Double.valueOf((CalculatedValue.toPlainString()));
				if(diffValue != 0.0)
				{					
					trackLog.add("INC7008:Actual derived value post conversion:MSISDN=" + msisdn + ":BALANCE_TYPE=" + balance_ID + ":BE_BUCKET_VALUE=" + Balance + ":BE_BUCKET_ID="+ bucket_ID +  ":MIGRATED_VALUE="+ String.valueOf(roundedBal) + ":DERIVED_VALUE="+ String.format("%.4f", CalculatedValue) +":ACTION=Logging");
				}
				TotalBalance = String.valueOf(roundedBal);
			}
		}
		else
		{
			TotalBalance = String.valueOf(CalculatedValue);
		}
		
		if(NegativeBalance && Long.parseLong(tempBalance_Value) < 0)
		{
			trackLog.add("INC7006:Negative BT_Value Found will be migrated as Zero:MSISDN=" + msisdn + ":BALANCE_TYPE=" + balance_ID + ":BE_BUCKET_VALUE=" + tempBalance_Value + ":BE_BUCKET_ID="+ bucket_ID +
			":TARGET_ID=" + Target_ID + ":MIGRATED_VALUE="+ TotalBalance + ":ACTION=Logging");
		}
		
		if(Long.parseLong(TotalBalance) < 0)
		{			
			trackLog.add("INC7007:Derived DA_BALANCE or UC_VALUE is negative but migrated as zero:MSISDN=" + msisdn + ":BALANCE_TYPE=" + balance_ID + ":BE_BUCKET_VALUE=" + Balance_Input + ":BE_BUCKET_ID="+ bucket_ID + 
					":TARGET_ID="+ Target_ID + ":MIGRATED_VALUE="+ 0 + ":DERIVED_VALUE="+ CalculatedValue +":ACTION=Logging");
			TotalBalance = "0";
			//onlyLog.add("INC4010:Negative BT_Value found will be migrated as Zero:MSISDN=" + msisdn + ":BALANCE_TYPE_ID=" + balance_ID + ":BE_BUCKET_VALUE=" + Balance + ":ACTION=Logging");
		}
		
		return Long.parseLong(TotalBalance);
	}	
	
	public String PopulateUsageCounter(String UC_ID, String Balance_Msisdn, String CalculatedBalance, Set<String> BEBucketID)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(msisdn).append(",");
		sb.append(UC_ID).append(",");
		sb.append(Balance_Msisdn).append(",");
		sb.append(CalculatedBalance).append(",");
		String Product_ID = commonfunction.GetProductIDCreation(BEBucketID);
		/*if(UC_ID.equals("53"))
		{			
			sb.append(ProductIDFor53.get(CounterFor53)).append(",");
			CounterFor53 ++;
		}
		else
		{
			if(Product_ID.length() != 0)
				sb.append(Product_ID).append(",");
			else
				sb.append("0").append(",");
		}*/
	
		
		if(BEBucketID.size() != 0)
		{
			if(UC_ID.equals("53"))
				{			
					sb.append(ProductIDFor53.get(CounterFor53)).append(",");
					CounterFor53 ++;
				}
				else
				{
					if(Product_ID.length() != 0)
						sb.append(Product_ID).append(",");
					else
						sb.append("0").append(",");
				}
			}		
		else
		{	
			sb.append("0").append(",");
		}
		
		sb.append(CalculatedBalance).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
		
		return sb.toString();
	}

	/***********backup Code*******************/
	public Map<String,Map<String,List<String>>>  ComputeAMSpecialGroup(String balance_ID,String balance_Value,String START_DATE, String EXPIRY_DATE, String strMSISDN, String bebucketid, Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub Map<String,List<String>>
		
		List<String> AMGroupName = new ArrayList<>();		
		List<String> ValidGroupBalanceCounter = new ArrayList<>();
		List<String> FinalUCList = new ArrayList<>();
		List<String> UsageCounterList = new ArrayList<>();
		Set<String> AMBT_ID = new HashSet<>();
		boolean ExtraUCFlag = false;
		boolean AMGroupFormed = false;
		//Map<String,List<String>> tempGroupBalanceOffer = new HashMap<>();
		
		Map<String,Map<String,List<String>>> GroupUCMap = new HashMap<>();
		AMGroupName = (commonfunction.getASpecialGroupKey(LoadSubscriberMapping.BalanceOnlySpecialAMGroupMap,balance_ID));
		
		
		for(String GroupName : AMGroupName)
		{
			Set<String> GroupElements = new HashSet<>();
			Map<String, String> MapBTIDDateTime = new HashMap<>();
			if(!GroupName.startsWith("A-M"))
			{
				ValidGroupBalanceCounter.clear();
				AMBT_ID.clear();
				GroupElements.clear();
				FinalUCList.clear();
				Set<String> GroupBTItems = LoadSubscriberMapping.BalanceOnlySpecialAMGroupMap.get(GroupName);
				for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo TempbalanceInput : SortedBalanceInput)
				{
					String TempBalance_ID = TempbalanceInput.getBALANCETYPE();
					String TempBalance_Value = TempbalanceInput.getBEBUCKETVALUE();
					String TempBalance_Name = TempbalanceInput.getBALANCETYPENAME();
					String TempBalance_Msisdn = TempbalanceInput.getMSISDN();
					String TempBalance_StartDate = TempbalanceInput.getBEBUCKETSTARTDATE();
					String TempBalance_ExpiryDate = TempbalanceInput.getBEEXPIRY();
					if(CompletedBT_ID.contains(TempbalanceInput.getBEBUCKETID()))
						continue;
					
					if(GroupBTItems.contains(TempBalance_ID))
					{
						if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName) != null)
						{	
							String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getUCID();
							String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getSymbols();
							String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getBTValue();
							String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getProductPrivate();
							String UC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getUTValue();
							String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getResource();
							String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getBTTYPE();
							
							String ExtraUCValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getAddUC();
							if(!ExtraUCValue.isEmpty())
							{
								ExtraUCFlag = true;
							}
							else
							{
								ExtraUCValue = "";
							}
							if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
							{
								if(!GroupElements.contains(TempBalance_ID))
								{
									GroupElements.add(TempBalance_ID);
									if(BT_Type.equals("X") || BT_Type.equals("F"))
										MapBTIDDateTime.put(TempBalance_ID, TempbalanceInput.getBEEXPIRY());
									AMBT_ID.add(TempbalanceInput.getBEBUCKETID());
									ValidGroupBalanceCounter.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
									if(!UC_ID.isEmpty())
										FinalUCList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
									continue;
								}
							}
							else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
							{
								if(!GroupElements.contains(TempBalance_ID))
								{
									GroupElements.add(TempBalance_ID);
									if(BT_Type.equals("X") || BT_Type.equals("F"))
										MapBTIDDateTime.put(TempBalance_ID, TempbalanceInput.getBEEXPIRY());
									AMBT_ID.add(TempbalanceInput.getBEBUCKETID());
									ValidGroupBalanceCounter.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
									if(!UC_ID.isEmpty())
										FinalUCList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
									continue;
								}
							}
							else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
							{
								if(!GroupElements.contains(TempBalance_ID))
								{
									GroupElements.add(TempBalance_ID);
									AMBT_ID.add(TempbalanceInput.getBEBUCKETID());
									if(BT_Type.equals("X") || BT_Type.equals("F"))
										MapBTIDDateTime.put(TempBalance_ID, TempbalanceInput.getBEEXPIRY());
									ValidGroupBalanceCounter.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
									if(!UC_ID.isEmpty())
										FinalUCList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
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
										GroupElements.add(TempBalance_ID);
										AMBT_ID.add(TempbalanceInput.getBEBUCKETID());
										if(BT_Type.equals("X") || BT_Type.equals("F"))
											MapBTIDDateTime.put(TempBalance_ID, TempbalanceInput.getBEEXPIRY());
										ValidGroupBalanceCounter.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
										if(!UC_ID.isEmpty())
											FinalUCList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + ";" + TempBalance_Msisdn +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
										continue;
									}
								}
							}														
						}							
					}
				}
				if(ValidGroupBalanceCounter.size() == GroupBTItems.size())
				{
					boolean xBTCheck = false;
					String BT55Expiry = MapBTIDDateTime.get("55");
					for(Map.Entry<String, String> entry : MapBTIDDateTime.entrySet())
					{	if(!entry.getValue().equals(BT55Expiry))
						{
							AMGroupFormed = false;
							xBTCheck = true;
							break;
						}
					}
					if(!xBTCheck)
					{
						AMGroupFormed = true;
						break;
					}					
				}	
			}
		}
		if(AMGroupFormed)
		{
			ExtraUCFlag = false;
			FinalUCList.forEach(item->{
				String Resource = item.split("\\|")[1].split(";")[2];
				if(Resource.length() > 1)
				{
					long CalculatedBalance = CalculateBalance(Resource,(item.split("\\|")[1].split(";")[1]),(item.split("\\|")[0].split(";")[3]),item.split("\\|")[0].split(";")[2],item.split("\\|")[1].split(";")[7],item.split("\\|")[1].split(";")[0]);
					UsageCounterList.add(PopulateUsageCounter(item.split("\\|")[1].split(";")[0],item.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), AMBT_ID));
				}										
			});
		}
		else
		{
			//Group is not formed so check for A-M Group and then go to master
			Map<String,Map<String,List<String>>> AMOutputDetails = new HashMap<>();
			AMOutputDetails = populateAMGroupResult(balance_ID,balance_Value,START_DATE, EXPIRY_DATE, strMSISDN, bebucketid,CompletedBT_ID);
			return AMOutputDetails;
		}
		
		Map<String,List<String>> AMOutputDetails = new HashMap<>();
		if(AMBT_ID.size() != 0)
			AMOutputDetails.put("CompletedBT", AMBT_ID.stream().collect(Collectors.toList()));
		if(UsageCounterList.size() != 0)
			AMOutputDetails.put("UC", UsageCounterList);
		
		
		GroupUCMap.put("AMOutputDetails",AMOutputDetails);
		
		return GroupUCMap;
	}
	
	private Map<String,Map<String,List<String>>> populateAMGroupResult(String balance_ID,String balance_Value,String START_DATE, String EXPIRY_DATE, String newMsisdn, String bebucketid, Set<String> CompletedBT_ID) 
	{
		List<String> UsageCounterList = new ArrayList<>();
		List<String> AMCompleted_ID = new ArrayList<>();
		List<String> tempAMUniqueBts = new ArrayList<>(LoadSubscriberMapping.UniqueBalanceOnlyAMGroupMap);
		Map<String,Map<String,List<String>>> GroupUCMap = new HashMap<>();
		
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
				if(tempAMUniqueBts.contains(TempBalance_ID))
					AMGroupBTs.put(TempbalanceInput.getBEBUCKETID(), TempBalance_ID + "|" + TempBalance_Value);
				
				if(TempBalance_ID.contains("55"))
				{
					tempAMUniqueBts.remove(TempBalance_ID);
				}
				else if(TempBalance_ID.contains("1633") || TempBalance_ID.contains("1635"))
				{
					tempAMUniqueBts.remove("1633");
					tempAMUniqueBts.remove("1635");
				}
			}
		}
		
		
		Map<String,List<String>> AMGroupOfferMap = ComputeAMGroup(AMGroupBTs, CompletedBT_ID);
		
		//Now Populate the MGroup
		if(AMGroupOfferMap.size() != 0)
		{
			Set<String> AMGroupBEBucket = new HashSet<>();
			for(Entry<String, List<String>> item : AMGroupOfferMap.entrySet())
			{
				List<String> ValidAMGroupBalanceOffer = item.getValue();
				for(String Str : ValidAMGroupBalanceOffer)
				{
					AMGroupBEBucket.add(Str.split("\\|")[1].split(";")[7]);
				}
			}
			
			for(Entry<String, List<String>> item : AMGroupOfferMap.entrySet())
			{
				String AMGroupName = item.getKey().split("\\|")[0];
				List<String> UniqueBTID = Arrays.asList(item.getKey().split("\\|")[1].split(",")); 
				List<String> ValidAMGroupBalanceUC = item.getValue();
				
				int i = 1;
				Set<String> CompletedAMBT = new HashSet<>();
				for(String Str : ValidAMGroupBalanceUC)
				{
					String SourceValue = Str.split("\\|")[0];
					String TargetValue = Str.split("\\|")[1];
					if(i <= UniqueBTID.size() && UniqueBTID.contains(SourceValue.split(";")[2]) && !CompletedAMBT.contains(SourceValue.split(";")[2]))
					{
						i++;
						String Resource = Str.split("\\|")[1].split(";")[2];
						if(Resource.length() > 1 && Str.split("\\|")[1].split(";")[0].length() != 0)
						{
							long CalculatedBalance = CalculateBalance(Resource,(Str.split("\\|")[1].split(";")[1]),(Str.split("\\|")[0].split(";")[3]),Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[7],Str.split("\\|")[1].split(";")[0]);
							UsageCounterList.add(PopulateUsageCounter(Str.split("\\|")[1].split(";")[0],Str.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), AMGroupBEBucket));
						}													
						
						CompletedAMBT.add(SourceValue.split(";")[2]);
						AMCompleted_ID.add(TargetValue.split(";")[7]);	
					}
					else
					{
						if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[2] + "|M") != null)
						{
							String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[2] + "|M").getUCID();
							String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[2] + "|M").getResource();
							String UTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[2] + "|M").getUTValue();
							
							String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[2] + "|M").getSymbols();
							String BTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[2] + "|M").getBTValue();
							
							if(UC_ID.length() > 1 && Resource.length() > 1 && Symbol.equals(">") && Integer.parseInt((Str.split("\\|")[0].split(";")[3])) > Integer.parseInt(BTValue))
							{
								long CalculatedBalance = CalculateBalance(Resource,UTValue,(Str.split("\\|")[0].split(";")[3]),Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[7],UC_ID);
								UsageCounterList.add(PopulateUsageCounter(UC_ID,Str.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(Str.split("\\|")[1].split(";")[7]))));
							}
							if(UC_ID.length() > 1 && Resource.length() > 1 && Symbol.equals(">=") && Integer.parseInt((Str.split("\\|")[0].split(";")[3])) >= Integer.parseInt(BTValue))
							{
								long CalculatedBalance = CalculateBalance(Resource,UTValue,(Str.split("\\|")[0].split(";")[3]),Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[7],UC_ID);
								UsageCounterList.add(PopulateUsageCounter(UC_ID,Str.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(Str.split("\\|")[1].split(";")[7]))));
							}
							if(UC_ID.length() > 1 && Resource.length() > 1 && Symbol.equals("=") && Integer.parseInt((Str.split("\\|")[0].split(";")[3])) == Integer.parseInt(BTValue))
							{
								long CalculatedBalance = CalculateBalance(Resource,UTValue,(Str.split("\\|")[0].split(";")[3]),Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[7],UC_ID);
								UsageCounterList.add(PopulateUsageCounter(UC_ID,Str.split("\\|")[0].split(";")[4],String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(Str.split("\\|")[1].split(";")[7]))));
							}
						}
						AMCompleted_ID.add(TargetValue.split(";")[7]);
					}
				}													
			}
		}
		else
		{
			if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(balance_ID + "|M") != null)
			{
				String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(balance_ID + "|M").getUCID();
				String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(balance_ID + "|M").getResource();
				String UTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(balance_ID + "|M").getUTValue();
				
				String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(balance_ID + "|M").getSymbols();
				String BTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(balance_ID + "|M").getBTValue();
				
				if(UC_ID.length() > 1 && Symbol.equals(">") && Integer.parseInt(balance_Value) > Integer.parseInt(BTValue))
				{
					long CalculatedBalance = CalculateBalance(Resource,UTValue,(balance_Value),balance_ID,bebucketid,UC_ID);
					UsageCounterList.add(PopulateUsageCounter(UC_ID,newMsisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(bebucketid))));
				}
				if(UC_ID.length() > 1 && Symbol.equals(">=") && Integer.parseInt(balance_Value) >= Integer.parseInt(BTValue))
				{
					long CalculatedBalance = CalculateBalance(Resource,UTValue,(balance_Value),balance_ID,bebucketid,UC_ID);
					UsageCounterList.add(PopulateUsageCounter(UC_ID,newMsisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(bebucketid))));
				}
				if(UC_ID.length() > 1 && Symbol.equals("=") && Integer.parseInt(balance_Value) == Integer.parseInt(BTValue))
				{
					long CalculatedBalance = CalculateBalance(Resource,UTValue,balance_Value,balance_ID,bebucketid,UC_ID);
					UsageCounterList.add(PopulateUsageCounter(UC_ID,newMsisdn,String.valueOf(CalculatedBalance), new HashSet<>(Arrays.asList(bebucketid))));
				}
				AMCompleted_ID.add(bebucketid);
			}	
		
		}
		
		Map<String,List<String>> AMOutputDetails = new HashMap<>();
		if(AMCompleted_ID.size() != 0)
			AMOutputDetails.put("CompletedBT", AMCompleted_ID);
		if(UsageCounterList.size() != 0)
			AMOutputDetails.put("UC", UsageCounterList);
		
		
		GroupUCMap.put("AMOutputDetails",AMOutputDetails);
		
		return GroupUCMap;
	}

	public Map<String,List<String>>  ComputeASGroup(String inputBalance_ID, Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub Map<String,List<String>>
		
		String FinalGroupName ="";		
		List<String>AllAvailableGroup = new ArrayList<>();	
		List<String> ValidGroupBalanceCounter = new ArrayList<>();
		Set<String> ASGroupItems = new HashSet<>();
		boolean ExtraOfferFlag = false;
		boolean ExtraUCFlag = false;
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
					String UC_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getUCID();
					String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getSymbols();
					String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTValue();
					String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getProductPrivate();
					String UC_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getUTValue();
					String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getResource();
					String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTTYPE();
					
					String ExtraUCValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAddUC();
					if(!ExtraUCValue.isEmpty())
					{
						ExtraUCFlag = true;
					}
					else
					{
						ExtraUCValue = "";
					}
					CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
					if(Symbol.equals(">=") && Integer.parseInt(TempbalanceInput.getBEBUCKETVALUE()) >= Integer.parseInt(BT_Value))
					{
						ValidGroupBalanceCounter.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() + ";" + TempbalanceInput.getMSISDN() +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
						break;
					}
					else if(Symbol.equals(">") && Integer.parseInt(TempbalanceInput.getBEBUCKETVALUE()) > Integer.parseInt(BT_Value))
					{
						ValidGroupBalanceCounter.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +  ";" + TempbalanceInput.getMSISDN() +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
						break;
					}
					else if(Symbol.equals("=") && Integer.parseInt(TempbalanceInput.getBEBUCKETVALUE()) == Integer.parseInt(BT_Value))
					{
						ValidGroupBalanceCounter.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() + ";" + TempbalanceInput.getMSISDN() +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
						break;
					}
					else if(Symbol.equals("or"))
					{
						//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)												
						String[] values = BT_Value.split("#");											
						if(Arrays.stream(values).anyMatch(TempbalanceInput.getBEBUCKETVALUE()::equals))
						{
							ValidGroupBalanceCounter.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() + ";" + TempbalanceInput.getMSISDN() +"|" + UC_ID + ";" + UC_Value + ";" + Resource + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + Product_Private + ";" + ExtraUCValue + ";" + TempbalanceInput.getBEBUCKETID());
							break;
						}							
					}
				}	
			}
		}
		String temp = String.join(",", ASGroupItems);
		ASGroupOfferMap.put(FinalGroupName +"|" + temp, ValidGroupBalanceCounter);
		return ASGroupOfferMap;
	}
	
}
