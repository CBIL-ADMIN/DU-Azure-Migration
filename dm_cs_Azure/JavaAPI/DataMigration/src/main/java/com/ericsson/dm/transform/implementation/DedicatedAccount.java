package com.ericsson.dm.transform.implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.ericsson.dm.Utils.CommonUtilities;
import com.ericsson.dm.inititialization.LoadSubscriberMapping;
import com.ericsson.jibx.beans.SubscriberXml;
import com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo;
import com.sun.xml.internal.ws.util.StringUtils;

public class DedicatedAccount implements Comparator<SchemasubscriberbalancesdumpInfo>{

	SubscriberXml subscriber;
	String msisdn;
	String INITIAL_ACTIVATION_DATE;
	String[] NegativeBalance = {"21","1439","235"};
	private Map<String, Set<String>> ProductIDLookUpMap;
	private int indx;	
	final static Logger LOG = Logger.getLogger(DedicatedAccount.class);
	// private Map<String, Map<String, String>> mapOfOfferId2ProductId;
    
	//private ReadWriteLock rwl = new ReentrantReadWriteLock();
	Set<String> rejectAndLog;
	Set<String> onlyLog;
	Set<String> RoundOffLog;
	Set<String> trackLog;
	Set<String> AMBTIDSet;
	
	CommonFunctions commonfunction;
	ProfileTagProcessing profileTag;
	public CopyOnWriteArrayList<SchemasubscriberbalancesdumpInfo> SortedBalanceInput;
	
	public DedicatedAccount()
	{
		
	}
	
	public DedicatedAccount(SubscriberXml subs, Set<String> rejectAndLog, Set<String> onlyLog, Set<String> trackLog, Set<String> RoundOffLog, String INITIAL_ACTIVATION_DATE, Map<String, Set<String>> ProductIDLookUpMap) {
		
		// TODO Auto-generated constructor stub
		this.subscriber=subs;
		this.indx = 1;
		this.rejectAndLog=rejectAndLog;
		this.onlyLog=onlyLog;
		this.trackLog = trackLog;
		this.RoundOffLog=RoundOffLog;
		this.ProductIDLookUpMap = ProductIDLookUpMap;
		this.INITIAL_ACTIVATION_DATE = INITIAL_ACTIVATION_DATE;
		this.AMBTIDSet = new HashSet<>();
		
		commonfunction = new CommonFunctions(subscriber,ProductIDLookUpMap,this.onlyLog);
		profileTag = new ProfileTagProcessing(subscriber,this.onlyLog);
		SortedBalanceInput = new CopyOnWriteArrayList<>();
	}

	public Map<String,String> execute() {
		// TODO Auto-generated method stub
		msisdn = subscriber.getSubscriberInfoMSISDN();
		
		SortedBalanceInput.addAll(subscriber.getBalancesdumpInfoList());
		Collections.sort(SortedBalanceInput,new Offer());
		//Map<String, Map<String, String>> DAmap = new HashMap<>();		
		Map<String, String> DAmap = new HashMap<>();
		
		DAmap.putAll(generateDAAMFromProductMapping());		
		DAmap.putAll(generateDAFromProductMapping());
		DAmap.putAll(generateDAFromDefaultSheet());
	
		SortedBalanceInput.clear();
		return DAmap;
	}
	
	@Override
	public int compare(SchemasubscriberbalancesdumpInfo o1, SchemasubscriberbalancesdumpInfo o2) {
		int value1 = (o2.getBEEXPIRY()).compareTo((o1.getBEEXPIRY()));
        if (value1 == 0) {
        	return  o2.getBEBUCKETID().compareTo(o1.getBEBUCKETID());
        }
        return value1;
	}
	
	private Map<String, String> generateDAFromDefaultSheet() {
		Map<String,String> DA = new HashMap<>();
		//System.out.println("Inside defaultSheet");
		LoadSubscriberMapping.DefaultServicesMap.forEach((k,v)->{
			if (v.split(",",-1)[1].trim().equals("N"))
			{
				//System.out.println(v);
				if (v.split(",",-1)[22] != "" && v.split(",",-1)[22].length() != 0)
				{
					String CCS_ID = v.split(",",-1)[24];
					//System.out.println(CCS_ID);
					//String SourceCCS_ID = subscriber.getSubscriberInfoCCSACCTTYPEID();
					//if(subscriber.getSubscriberInfoCCSACCTTYPEID().equals(CCS_ID))
					{
						DA.put("ID_" + indx, v.split(",",-1)[22]);
						if(v.split(",", -1)[26].equals("LAST_RECHARGE_AMOUNT.INFILE_Subscriber_Dump_USMS.csv"))
						{
							String LAST_RECHARGE_AMOUNT = subscriber.getUsmsdumpInfoList().get(0).getLASTRECHARGEAMOUNT();
							if(Long.parseLong(LAST_RECHARGE_AMOUNT) < 0)
							{
								trackLog.add("INC7009:Negative Balances found for DA in Default_Services:MSISDN=" + msisdn + ":SOURCE_FIELD=LAST_RECHARGE_AMOUNT" + ":SOURCE_VALUE=" + LAST_RECHARGE_AMOUNT  + ":DA_ID=" + v.split(",",-1)[22] + ":ACTION=Logging");
								LAST_RECHARGE_AMOUNT = "0";
							}
							DA.put("BALANCE_" + indx, CalculateBalance((LAST_RECHARGE_AMOUNT),v.split(",",-1)[21],"","",v.split(",",-1)[22]));
						}
						if(v.split(",", -1)[26].equals("LAST_BALANCE_BEFORE_RECHARGE.INFILE_Subscriber_Dump_USMS.csv"))
						{
							String LAST_BALANCE_BEFORE_RECHARGE = subscriber.getUsmsdumpInfoList().get(0).getLASTBALANCEBEFORERECHARGE();
							if(Long.parseLong(LAST_BALANCE_BEFORE_RECHARGE) < 0)
							{
								trackLog.add("INC7009:Negative Balances found for DA in Default_Services:MSISDN=" + msisdn + ":SOURCE_FIELD=LAST_BALANCE_BEFORE_RECHARGE" + ":SOURCE_VALUE=" + LAST_BALANCE_BEFORE_RECHARGE   + ":DA_ID=" + v.split(",",-1)[22] + ":ACTION=Logging");
								LAST_BALANCE_BEFORE_RECHARGE = "0";
							}
							DA.put("BALANCE_" + indx, CalculateBalance((LAST_BALANCE_BEFORE_RECHARGE),v.split(",",-1)[21],"","",v.split(",",-1)[22]));
						}
						DA.put("START_DATE_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_NULL"));
						DA.put("EXPIRY_DATE_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_NULL"));
						DA.put("PAM_SERVICE_ID_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
						DA.put("PRODUCT_ID_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
						
						this.indx++;
					}
				}
			}
		});				
		return DA;
	}
	
	private synchronized Map<String, String> generateDAFromProductMapping() {
		Map<String,String> DAValues = new ConcurrentHashMap<>(1000, 0.75f, 30);
		synchronized (DAValues)
		{	
			Set<String> CompletedBT_ID = new HashSet<>();
			Set<String> BEIDForProductID = new HashSet<>();
			CompletedBT_ID.addAll(AMBTIDSet);
			
			for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
			{			
				String Balance_ID = balanceInput.getBALANCETYPE();
				if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(Balance_ID))
					continue;
				String Balance_Value = balanceInput.getBEBUCKETVALUE();
				String Balance_StartDate = balanceInput.getBEBUCKETSTARTDATE();
				String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
				BEIDForProductID.clear();
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
					String DA_ID = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getDAID();
					String Symbol = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getSymbols();
					String BT_Value = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getBTValue();
					//String Product_Private = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMapForDA.get(Balance_ID + "|").getProductPrivate();
					String Resource = 	LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getResource();		
					//String DA_Type = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMapForDA.get(Balance_ID + "|" ).getDAType();
					
					if(!DA_ID.isEmpty())
					{					
						if(Symbol.isEmpty() && BT_Value.isEmpty())
						{	
							if(CheckIfDAAvailable(DA_ID,DAValues))
							{
								String CalculatedBalance = CalculateBalance((Balance_Value),Resource,Balance_ID,balanceInput.getBEBUCKETID(),DA_ID);
								DAValues.putAll(PopulateDedicatedAccount(DA_ID,CalculatedBalance,Balance_StartDate,Balance_ExpiryDate, new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							}
						}
						else
						{								
							boolean GenerateDA = false;
							if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
								GenerateDA = true;
							else if(Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_Value))
								GenerateDA = true;
							else if(Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value))
								GenerateDA = true;
							
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							
							if(GenerateDA)
							{	
								if(!DA_ID.isEmpty() && CheckIfDAAvailable(DA_ID,DAValues))
								{
									String CalculatedBalance = CalculateBalance((Balance_Value),Resource,Balance_ID,balanceInput.getBEBUCKETID(),DA_ID);
									DAValues.putAll(PopulateDedicatedAccount(DA_ID,CalculatedBalance,Balance_StartDate,Balance_ExpiryDate,new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
									CompletedBT_ID.add(balanceInput.getBEBUCKETID());
									
									//Create Extra DA if AddDA is not empty
									String AddedDA = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getAddDA();
									if(!AddedDA.isEmpty())
									{
										String[] ListofAddedDA;
										/*if(AddedDA.contains("\\|"))
											ListofAddedDA = AddedDA.split("\\|");
										else
											ListofAddedDA = new String[]{AddedDA};*/
										ListofAddedDA = AddedDA.split("\\|");
										
										for(int i = 0; i<ListofAddedDA.length; i++)
										{
											if(ListofAddedDA[i].length() > 1)
											{
												//361-5368709120
												String AddedDAD = ListofAddedDA[i].split("-")[0];
												String Balance = ListofAddedDA[i].split("-")[1];
												
												DAValues.put("ID_" + indx, AddedDAD);
												DAValues.put("BALANCE_" + indx, Balance);
												DAValues.put("START_DATE_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
												DAValues.put("EXPIRY_DATE_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
												DAValues.put("PAM_SERVICE_ID_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
												DAValues.put("PRODUCT_ID_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
												
												trackLog.add("INC7002:Add_DA considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID() + ":DA_ID=" + AddedDAD + ":DA_BALANCE=" + Balance  + ":ACTION=Logging");
												this.indx++;
											}											 
										}										
									}
								}
							}													
						}
					}	
				}
				else
				{
					if(LoadSubscriberMapping.ExceptionBalances.contains(Balance_ID))
					{	
						if(Balance_ID.equals("21"))
						{
							if(!subscriber.getSubscriberInfoCCSACCTTYPEID().equals("334"))
							{
								if(Double.parseDouble(Balance_Value) >= Double.parseDouble("0"))
								{
									String DA_ID = LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + ">=").getDAID();
									String Resource = 	LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + ">=").getResource();
									String CalculatedBalance = CalculateBalance((Balance_Value),Resource,Balance_ID,balanceInput.getBEBUCKETID(),DA_ID);
									
									String TotalBalance = ChecknAddGeneralCashBalance(DA_ID,CalculatedBalance,DAValues);									
									
									if(!TotalBalance.isEmpty())
									{
										Entry<String, String> result = DAValues.entrySet().stream().filter(e -> e.getKey().startsWith("ID_"))
												.filter(x->x.getValue().equals(DA_ID)).findFirst().orElse(null);
										
										if(result != null)
										{
											String BalanceID = "BALANCE_" + result.getKey().split("_")[1];
											DAValues.put(BalanceID, String.valueOf(TotalBalance));
										}
									}
									else
									{
										DAValues.putAll(PopulateDedicatedAccount(DA_ID,CalculatedBalance,Balance_StartDate,Balance_ExpiryDate,new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
									}
									CompletedBT_ID.add(balanceInput.getBEBUCKETID());
								}
								if(Double.parseDouble(Balance_Value) < Double.parseDouble("0"))
								{
									String DA_ID = LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + "<").getDAID();
									String Resource = 	LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + "<").getResource();
									String CalculatedBalance = CalculateBalance((Balance_Value),Resource,Balance_ID,balanceInput.getBEBUCKETID(),DA_ID);
									String TotalBalance = ChecknAddGeneralCashBalance(DA_ID,CalculatedBalance,DAValues);
									
									if(!TotalBalance.isEmpty())
									{
										Entry<String, String> result = DAValues.entrySet().stream().filter(e -> e.getKey().startsWith("ID_"))
												.filter(x->x.getValue().equals(DA_ID)).findFirst().orElse(null);
										
										if(result != null)
										{
											String BalanceID = "BALANCE_" + result.getKey().split("_")[1];
											DAValues.put(BalanceID, String.valueOf(TotalBalance));
										}
									}
									else
									{									
										DAValues.put("ID_" + indx, DA_ID);
										if(TotalBalance.isEmpty())
											DAValues.put("BALANCE_" + indx, String.valueOf(CalculatedBalance));
										else
											DAValues.put("BALANCE_" + indx, String.valueOf(TotalBalance));
										if(Balance_StartDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
											DAValues.put("START_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
										else
											DAValues.put("START_DATE_" + indx,String.valueOf(CommonUtilities.convertDateToEpoch(Balance_StartDate)));
										if(Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
											DAValues.put("EXPIRY_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
										else
											DAValues.put("EXPIRY_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(Balance_ExpiryDate)));
										DAValues.put("PAM_SERVICE_ID_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
										DAValues.put("PRODUCT_ID_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
									
										this.indx++;
									}
									CompletedBT_ID.add(balanceInput.getBEBUCKETID());
									
									
									String ExtraDA = LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + "<").getAddDA();
									if(!ExtraDA.isEmpty())
									{
										DAValues.put("ID_" + indx, ExtraDA.split("-")[0]);
										DAValues.put("BALANCE_" + indx, ExtraDA.split("-")[1]);
										DAValues.put("START_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
										DAValues.put("EXPIRY_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
										DAValues.put("PAM_SERVICE_ID_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
										DAValues.put("PRODUCT_ID_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
									
										this.indx++;
									}									
								}
							}
							else
							{
								String BalanceValue1 = LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + "<=").getBTValue();
								String BalanceValue2 = LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + ">").getBTValue();
								if(Double.parseDouble(Balance_Value) <= Double.parseDouble(BalanceValue1))
								{
									String DA_ID = LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + "<=").getDAID();
									String Resource = 	LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + "<=").getResource();
									String CalculatedBalance = CalculateBalance((Balance_Value),Resource,Balance_ID,balanceInput.getBEBUCKETID(),DA_ID);
									String TotalBalance = ChecknAddGeneralCashBalance(DA_ID,CalculatedBalance,DAValues);
									if(!TotalBalance.isEmpty())
									{
										Entry<String, String> result = DAValues.entrySet().stream().filter(e -> e.getKey().startsWith("ID_"))
												.filter(x->x.getValue().equals(DA_ID)).findFirst().orElse(null);
										
										if(result != null)
										{
											String BalanceID = "BALANCE_" + result.getKey().split("_")[1];
											DAValues.put(BalanceID, String.valueOf(TotalBalance));
										}
									}
									else
									{
										DAValues.put("ID_" + indx, DA_ID);
										if(TotalBalance.isEmpty())
											DAValues.put("BALANCE_" + indx, String.valueOf(CalculatedBalance));
										else
											DAValues.put("BALANCE_" + indx, String.valueOf(TotalBalance));
										if(Balance_StartDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
											DAValues.put("START_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
										else
											DAValues.put("START_DATE_" + indx,String.valueOf(CommonUtilities.convertDateToEpoch(Balance_StartDate)));
										if(Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
											DAValues.put("EXPIRY_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
										else
											DAValues.put("EXPIRY_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(Balance_ExpiryDate)));
										DAValues.put("PAM_SERVICE_ID_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
										DAValues.put("PRODUCT_ID_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
									
										this.indx++;
									}
									CompletedBT_ID.add(balanceInput.getBEBUCKETID());
								}
								if(Double.parseDouble(Balance_Value) > Double.parseDouble(BalanceValue2))
								{
									String DA_ID = LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + ">").getDAID();
									String Resource = 	LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + ">").getResource();
									String CalculatedBalance = CalculateBalance((Balance_Value),Resource,Balance_ID,balanceInput.getBEBUCKETID(),DA_ID);
									String TotalBalance = ChecknAddGeneralCashBalance(DA_ID,CalculatedBalance,DAValues);
									
									if(!TotalBalance.isEmpty())
									{
										Entry<String, String> result = DAValues.entrySet().stream().filter(e -> e.getKey().startsWith("ID_"))
												.filter(x->x.getValue().equals(DA_ID)).findFirst().orElse(null);
										
										if(result != null)
										{
											String BalanceID = "BALANCE_" + result.getKey().split("_")[1];
											DAValues.put(BalanceID, String.valueOf(TotalBalance));
										}
									}
									else
									{									
										DAValues.put("ID_" + indx, DA_ID);
										if(TotalBalance.isEmpty())
											DAValues.put("BALANCE_" + indx, String.valueOf(CalculatedBalance));
										else
											DAValues.put("BALANCE_" + indx, String.valueOf(TotalBalance));
										if(Balance_StartDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
											DAValues.put("START_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
										else
											DAValues.put("START_DATE_" + indx,String.valueOf(CommonUtilities.convertDateToEpoch(Balance_StartDate)));
										if(Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
											DAValues.put("EXPIRY_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
										else
											DAValues.put("EXPIRY_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(Balance_ExpiryDate)));
										DAValues.put("PAM_SERVICE_ID_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
										DAValues.put("PRODUCT_ID_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
									
										this.indx++;
									}
									CompletedBT_ID.add(balanceInput.getBEBUCKETID());																										
								}
							}
						}						
						if(Balance_ID.equals("1439"))
						{							
							if(Integer.parseInt(Balance_Value) >= Integer.parseInt("0"))
							{
								String DA_ID = LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + ">=").getDAID();
								String Resource = 	LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + ">=").getResource();								
								String CalculatedBalance = CalculateBalance((Balance_Value),Resource,Balance_ID,balanceInput.getBEBUCKETID(),DA_ID);
								String TotalBalance = ChecknAddGeneralCashBalance(DA_ID,CalculatedBalance,DAValues);
								
								if(!TotalBalance.isEmpty())
								{
									Entry<String, String> result = DAValues.entrySet().stream().filter(e -> e.getKey().startsWith("ID_"))
											.filter(x->x.getValue().equals(DA_ID)).findFirst().orElse(null);
									
									if(result != null)
									{
										String BalanceID = "BALANCE_" + result.getKey().split("_")[1];
										DAValues.put(BalanceID, String.valueOf(TotalBalance));
									}
								}
								else
								{
									DAValues.put("ID_" + indx, DA_ID);
									if(TotalBalance.isEmpty())
										DAValues.put("BALANCE_" + indx, String.valueOf(CalculatedBalance));
									else
										DAValues.put("BALANCE_" + indx, String.valueOf(TotalBalance));
									if(Balance_StartDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
										DAValues.put("START_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
									else
										DAValues.put("START_DATE_" + indx,String.valueOf(CommonUtilities.convertDateToEpoch(Balance_StartDate)));
									if(Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
										DAValues.put("EXPIRY_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
									else
										DAValues.put("EXPIRY_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(Balance_ExpiryDate)));
								
									DAValues.put("PAM_SERVICE_ID_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
									DAValues.put("PRODUCT_ID_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
								
									this.indx++;
								}
								CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							}
							else if(Integer.parseInt(Balance_Value) < Integer.parseInt("0"))
							{
								String DA_ID = LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + "<").getDAID();
								String Resource = 	LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + "<").getResource();
								String CalculatedBalance = CalculateBalance((Balance_Value),Resource,Balance_ID,balanceInput.getBEBUCKETID(),DA_ID);
								String TotalBalance = ChecknAddGeneralCashBalance(DA_ID,CalculatedBalance,DAValues);
								if(!TotalBalance.isEmpty())
								{
									Entry<String, String> result = DAValues.entrySet().stream().filter(e -> e.getKey().startsWith("ID_"))
											.filter(x->x.getValue().equals(DA_ID)).findFirst().orElse(null);
									
									if(result != null)
									{
										String BalanceID = "BALANCE_" + result.getKey().split("_")[1];
										DAValues.put(BalanceID, String.valueOf(TotalBalance));
									}
								}
								else
								{
									DAValues.put("ID_" + indx, DA_ID);
									if(TotalBalance.isEmpty())
										DAValues.put("BALANCE_" + indx, String.valueOf(CalculatedBalance));
									else
										DAValues.put("BALANCE_" + indx, String.valueOf(TotalBalance));
									if(Balance_StartDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
										DAValues.put("START_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
									else
										DAValues.put("START_DATE_" + indx,String.valueOf(CommonUtilities.convertDateToEpoch(Balance_StartDate)));
									if(Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
										DAValues.put("EXPIRY_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
									else
										DAValues.put("EXPIRY_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(Balance_ExpiryDate)));
									DAValues.put("PAM_SERVICE_ID_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
									DAValues.put("PRODUCT_ID_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
								
									this.indx++;
								}
								CompletedBT_ID.add(balanceInput.getBEBUCKETID());
								
								
								String ExtraDA = LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + "<").getAddDA();
								if(!ExtraDA.isEmpty())
								{
									DAValues.put("ID_" + indx, ExtraDA.split("-")[0]);
									DAValues.put("BALANCE_" + indx, ExtraDA.split("-")[1]);
									DAValues.put("START_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
									DAValues.put("EXPIRY_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
									DAValues.put("PAM_SERVICE_ID_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
									DAValues.put("PRODUCT_ID_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
								
									this.indx++;
								}							
							}
						}							
						if(Balance_ID.equals("235"))
						{
							if(!subscriber.getSubscriberInfoCCSACCTTYPEID().equals("334"))
							{
								if(Double.parseDouble(Balance_Value) >= Double.parseDouble("0"))
								{
									String DA_ID = LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + ">=").getDAID();
									String Resource = 	LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + ">=").getResource();
									String CalculatedBalance = CalculateBalance((Balance_Value),Resource,Balance_ID,balanceInput.getBEBUCKETID(),DA_ID);
									String TotalBalance = ChecknAddGeneralCashBalance(DA_ID,CalculatedBalance,DAValues);
									if(!TotalBalance.isEmpty())
									{
										Entry<String, String> result = DAValues.entrySet().stream().filter(e -> e.getKey().startsWith("ID_"))
												.filter(x->x.getValue().equals(DA_ID)).findFirst().orElse(null);
										
										if(result != null)
										{
											String BalanceID = "BALANCE_" + result.getKey().split("_")[1];
											DAValues.put(BalanceID, String.valueOf(TotalBalance));
										}
									}
									else
									{
										DAValues.put("ID_" + indx, DA_ID);
										DAValues.put("BALANCE_" + indx, String.valueOf(CalculatedBalance));
										if(Balance_StartDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
											DAValues.put("START_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
										else
											DAValues.put("START_DATE_" + indx,String.valueOf(CommonUtilities.convertDateToEpoch(Balance_StartDate)));
										if(Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
											DAValues.put("EXPIRY_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
										else
											DAValues.put("EXPIRY_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(Balance_ExpiryDate)));
										DAValues.put("PAM_SERVICE_ID_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
										DAValues.put("PRODUCT_ID_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
									
										this.indx++;
									}
									CompletedBT_ID.add(balanceInput.getBEBUCKETID());
								}
								if(Double.parseDouble(Balance_Value) < Double.parseDouble("0"))
								{
									String DA_ID = LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + "<").getDAID();
									String Resource = 	LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + "<").getResource();
									String CalculatedBalance = CalculateBalance((Balance_Value),Resource,Balance_ID,balanceInput.getBEBUCKETID(),DA_ID);
									DAValues.put("ID_" + indx, DA_ID);
									DAValues.put("BALANCE_" + indx, String.valueOf(CalculatedBalance));
									if(Balance_StartDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
										DAValues.put("START_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
									else
										DAValues.put("START_DATE_" + indx,String.valueOf(CommonUtilities.convertDateToEpoch(Balance_StartDate)));
									if(Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
										DAValues.put("EXPIRY_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
									else
										DAValues.put("EXPIRY_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(Balance_ExpiryDate)));
									DAValues.put("PAM_SERVICE_ID_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
									DAValues.put("PRODUCT_ID_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
								
									this.indx++;
									CompletedBT_ID.add(balanceInput.getBEBUCKETID());
									
									
									String ExtraDA = LoadSubscriberMapping.CashBalancesGroup.get(Balance_ID + "," + "<").getAddDA();
									if(!ExtraDA.isEmpty())
									{
										DAValues.put("ID_" + indx, ExtraDA.split("-")[0]);
										DAValues.put("BALANCE_" + indx, ExtraDA.split("-")[1]);
										DAValues.put("START_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
										DAValues.put("EXPIRY_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
										DAValues.put("PAM_SERVICE_ID_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
										DAValues.put("PRODUCT_ID_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
									
										this.indx++;
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
											String expiryDate = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferExpiryDate();
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
											DummyDoneFlag =  true;											
										}									
									}
								}
							}
							if(!DummyDoneFlag)
							{
								if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M") != null)
								{
									boolean MasterGroupFlag = false;
									String DA_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getDAID();
									String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getResource();
									String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getSymbols();
									String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getBTValue();
									
																					
									if(!DA_ID.isEmpty())
									{
										if(Resource.length() > 1 && Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_VALUE))
										{
											MasterGroupFlag = true;
										}
										if(Resource.length() > 1 && Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_VALUE))
										{
											MasterGroupFlag = true;
										}
										if(Resource.length() > 1 && Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_VALUE))
										{
											MasterGroupFlag = true;
										}
										if(MasterGroupFlag)
										{
											String CalculatedBalance = CalculateBalance(Balance_Value,Resource,Balance_ID,balanceInput.getBEBUCKETID(),DA_ID);
											DAValues.putAll(PopulateDedicatedAccount(DA_ID,CalculatedBalance,Balance_StartDate,Balance_ExpiryDate,new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));												
										}	
									}
								}
							}
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						}
					}
					else
					{
						String GroupName = "";
						List<String> DAList = new ArrayList<>();
						List<String> FinalDAList = new ArrayList<>();
						List<String> CurrentGroupBalance = new ArrayList<>();
						//Map<String, String> CurrentGroupBalanceID = new ConcurrentHashMap<>(50, 0.75f, 30);
						boolean ExtraOfferFlag = false;
						boolean ExtraDAFlag = false;
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
								Set<String> PartialGroupList = new HashSet<>();
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
												String DA_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getDAID();
												String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getSymbols();
												String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTValue();
												String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getProductPrivate();
												String DA_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getDAType();
												String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getResource();
												String ExtraOfferDA = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAddOffer();
												String ExtraDA = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAddDA();
												String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTTYPE();
												if(!ExtraOfferDA.isEmpty())
													ExtraOfferFlag = true;
												else
													ExtraOfferDA = "";
												if(!ExtraDA.isEmpty())
													ExtraDAFlag = true;
												else
													ExtraDA = "";
												
												if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
												{
													CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
													BEIDForProductID.add(TempbalanceInput.getBEBUCKETID());
													DAList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
													if(!DA_ID.isEmpty())
														FinalDAList.add(TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
													break;
												}
												else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
												{
													CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
													BEIDForProductID.add(TempbalanceInput.getBEBUCKETID());
													DAList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
													if(!DA_ID.isEmpty())
														FinalDAList.add(TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
													break;
												}
												else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
												{
													CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
													BEIDForProductID.add(TempbalanceInput.getBEBUCKETID());
													DAList.add(BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
													if(!DA_ID.isEmpty())
														FinalDAList.add( TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
													break;
												}
												else if(Symbol.equals("or"))
												{
													//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
													String[] values = BT_Value.split("#");											
													if(Arrays.stream(values).anyMatch(TempBalance_Value::equals))
													{
														CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
														BEIDForProductID.add(TempbalanceInput.getBEBUCKETID());
														DAList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
														if(!DA_ID.isEmpty())
															FinalDAList.add(TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
														break;
													}
												}
											}
										}
									}
								}
								
								//Find the BT_ID which is not part of the group for formation of partical group;PartialGroupList
								if(FinalGroupName.startsWith("A-") && DAList.size() > 0)
								{
									Set<String> outputBT = new HashSet<>();
									DAList.forEach(x->{outputBT.add(x.split("\\|")[0].split(";")[2]);});
									for(String s : CurrentGroupBalance)
									{
										if(!outputBT.contains(s))
										{
											PartialGroupList.add(s);
										}
									}
								}
								
								if(FinalGroupName.startsWith("A-") && DAList.size() != 0)
								{
									if(DAList.size() == CurrentGroupBalance.size())
									{
										ExtraDAFlag = false;
										ExtraOfferFlag = false;
										FinalDAList.forEach(item->{
											String Resource = item.split("\\|")[1].split(";")[2];
											if(Resource.length() > 1)
											{
												if(!item.split("\\|")[1].split(";")[0].isEmpty() && CheckIfDAAvailable(item.split("\\|")[1].split(";")[0],DAValues))
												{
													//String CalculatedBalance = CalculateBalance((item.split("\\|")[0].split(";")[2]),Resource,item.split("\\|")[0].split(";")[1],CurrentGroupBalanceID.get(item.split("\\|")[0].split(";")[1] +","+item.split("\\|")[0].split(";")[2]));
													String CalculatedBalance = CalculateBalance((item.split("\\|")[0].split(";")[2]),Resource,item.split("\\|")[0].split(";")[1],item.split("\\|")[1].split(";")[8],item.split("\\|")[1].split(";")[0]);
													DAValues.putAll(PopulateDedicatedAccount(item.split("\\|")[1].split(";")[0],CalculatedBalance,item.split("\\|")[1].split(";")[3],item.split("\\|")[1].split(";")[4],BEIDForProductID));
												}
											}										
										});
										//CompletedBT_ID.addAll(CurrentGroupBalance);
									}
									else
									{											
										if(LoadSubscriberMapping.BalanceAVGroupLookup.get(FinalGroupName) != null)
										{
											if(DAList.size() >= 2 && DAList.stream().filter(item->item.startsWith("P")).count() >=1 && DAList.stream().filter(item->item.startsWith("V")).count() >=1){
												for(String item : DAList)
												{
													String Resource = item.split("\\|")[1].split(";")[2];
													if(Resource.length() > 1)
													{
														if(!item.split("\\|")[1].split(";")[0].isEmpty() && CheckIfDAAvailable(item.split("\\|")[1].split(";")[0],DAValues))
														{
															String CalculatedBalance = CalculateBalance((item.split("\\|")[0].split(";")[3]),Resource,item.split("\\|")[0].split(";")[2],item.split("\\|")[1].split(";")[8],item.split("\\|")[1].split(";")[0]);
															DAValues.putAll(PopulateDedicatedAccount(item.split("\\|")[1].split(";")[0],String.valueOf(CalculatedBalance),item.split("\\|")[1].split(";")[3],item.split("\\|")[1].split(";")[4],BEIDForProductID));
														}
													}
												}
												
												//Added code to handle partial product.
												/*if(PartialGroupList.size() > 0)
												{
													for(String BT_ID : PartialGroupList)
													{
														String DA_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + FinalGroupName).getDAID();
														DAValues.putAll(PopulateDedicatedAccount(DA_ID,String.valueOf(0),LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString(),LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString(),BEIDForProductID));
													}
												}*/
												ExtraDAFlag = false;
											}
											else if(DAList.size() >= 2){													
												DAValues.putAll(PopulateDedicatedMasterAccount(FinalDAList,BEIDForProductID));																			
												ExtraDAFlag = false;
											}
											else if(DAList.size() == 1)
											{
												//BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private
												if(DAList.stream().filter(item->item.startsWith("P")).count() == 1 ){
													ExtraDAFlag = false;
												}
												if(DAList.stream().filter(item->item.startsWith("M")).count() == 1){
													DAValues.putAll(PopulateDedicatedMasterAccount(FinalDAList,BEIDForProductID));
												}
												if(DAList.stream().filter(item->item.startsWith("V")).count() == 1){
													DAValues.putAll(PopulateDedicatedMasterAccount(FinalDAList,BEIDForProductID));
												}
												//CompletedBT_ID.addAll(CurrentGroupBalance);
											}										
										}
										else
										{
											if(DAList.size() >= 2 && DAList.stream().filter(item->item.startsWith("P")).count() >=1 && DAList.stream().filter(item->item.startsWith("M")).count() >=1){
												for(String item : DAList)
												{
													String Resource = item.split("\\|")[1].split(";")[2];
													if(Resource.length() > 1)
													{
														if(!item.split("\\|")[1].split(";")[0].isEmpty() && CheckIfDAAvailable(item.split("\\|")[1].split(";")[0],DAValues))
														{
															String CalculatedBalance = CalculateBalance((item.split("\\|")[0].split(";")[3]),Resource,item.split("\\|")[0].split(";")[2],item.split("\\|")[1].split(";")[8],item.split("\\|")[1].split(";")[0]);
															DAValues.putAll(PopulateDedicatedAccount(item.split("\\|")[1].split(";")[0],String.valueOf(CalculatedBalance),item.split("\\|")[1].split(";")[3],item.split("\\|")[1].split(";")[4],BEIDForProductID));
														}
													}
												}
												//Added code to handle partial product.
												/*if(PartialGroupList.size() > 0)
												{
													for(String BT_ID : PartialGroupList)
													{
														String DA_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + FinalGroupName).getDAID();
														DAValues.putAll(PopulateDedicatedAccount(DA_ID,String.valueOf(0),LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString(),LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString(),BEIDForProductID));
													}
												}*/
											}
											else if(DAList.size() >= 2 &&  DAList.stream().filter(item->item.startsWith("M")).count() >=1){
												DAValues.putAll(PopulateDedicatedMasterAccount(FinalDAList,BEIDForProductID));																			
											}
											else if(DAList.size() == 1)
											{
												//BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private
												if(DAList.stream().filter(item->item.startsWith("P")).count() == 1 ){
													for(String item : DAList)
													{
														String Resource = item.split("\\|")[1].split(";")[2];
														if(Resource.length() > 1)
														{
															if(!item.split("\\|")[1].split(";")[0].isEmpty() && CheckIfDAAvailable(item.split("\\|")[1].split(";")[0],DAValues))
															{
																String CalculatedBalance = CalculateBalance((item.split("\\|")[0].split(";")[3]),Resource,item.split("\\|")[0].split(";")[2],item.split("\\|")[1].split(";")[8],item.split("\\|")[1].split(";")[0]);
																DAValues.putAll(PopulateDedicatedAccount(item.split("\\|")[1].split(";")[0],String.valueOf(CalculatedBalance),item.split("\\|")[1].split(";")[3],item.split("\\|")[1].split(";")[4],BEIDForProductID));
															}
														}
													}
													
													//Added code to handle partial product.
													/*if(PartialGroupList.size() > 0)
													{
														for(String BT_ID : PartialGroupList)
														{
															String DA_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + FinalGroupName).getDAID();
															DAValues.putAll(PopulateDedicatedAccount(DA_ID,String.valueOf(0),LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString(),LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString(),BEIDForProductID));
														}
													}*/
												}
												if(DAList.stream().filter(item->item.startsWith("M")).count() == 1){
													DAValues.putAll(PopulateDedicatedMasterAccount(FinalDAList,BEIDForProductID));															
												}
												//CompletedBT_ID.addAll(CurrentGroupBalance);
											}
										}
									}																	
								}
								if( FinalGroupName.startsWith("C-") && DAList.size() != 0)
								{
									if(DAList.size() == CurrentGroupBalance.size())
									{
										ExtraDAFlag = false;
										ExtraOfferFlag = false;
										FinalDAList.forEach(item->{
											String Resource = item.split("\\|")[1].split(";")[2];
											if(Resource.length() > 1)
											{
												if(!item.split("\\|")[1].split(";")[0].isEmpty() && CheckIfDAAvailable(item.split("\\|")[1].split(";")[0],DAValues))
												{
													String CalculatedBalance = CalculateBalance((item.split("\\|")[0].split(";")[2]),Resource,item.split("\\|")[0].split(";")[1],item.split("\\|")[1].split(";")[8],item.split("\\|")[1].split(";")[0]);
													DAValues.putAll(PopulateDedicatedAccount(item.split("\\|")[1].split(";")[0],String.valueOf(CalculatedBalance),item.split("\\|")[1].split(";")[3],item.split("\\|")[1].split(";")[4],BEIDForProductID));
												}
											}										
										});
										//CompletedBT_ID.addAll(CurrentGroupBalance);
									}
									else
									{
										boolean MasterGroupFlag = false;
										DAValues.putAll(PopulateDedicatedMasterAccount(FinalDAList,BEIDForProductID));															
										//CompletedBT_ID.addAll(CurrentGroupBalance);									
									}								
								}
								if(FinalGroupName.startsWith("B-") && DAList.size() != 0)
								{
									if(DAList.size() == CurrentGroupBalance.size())
									{
										ExtraOfferFlag = false;
										ExtraDAFlag = false;
										FinalDAList.forEach(item->{
											String Resource = item.split("\\|")[1].split(";")[2];
											if(Resource.length() > 1)
											{
												if(!item.split("\\|")[1].split(";")[0].isEmpty() && CheckIfDAAvailable(item.split("\\|")[1].split(";")[0],DAValues))
												{
													String CalculatedBalance = CalculateBalance((item.split("\\|")[0].split(";")[2]),Resource,item.split("\\|")[0].split(";")[1],item.split("\\|")[1].split(";")[8],item.split("\\|")[1].split(";")[0]);
													DAValues.putAll(PopulateDedicatedAccount(item.split("\\|")[1].split(";")[0],String.valueOf(CalculatedBalance),item.split("\\|")[1].split(";")[3],item.split("\\|")[1].split(";")[4],BEIDForProductID));
												}
											}										
										});
									}
								}
								if(ExtraOfferFlag)
								{	
									ExtraOfferFlag = false;
									for(String s: DAList)
									{
										if(s.split("\\|")[1].split(";").length > 8)
										{
											String AddedOfferDA= s.split("\\|")[1].split(";")[8];
											if(!AddedOfferDA.isEmpty() && CheckIfDAAvailable(AddedOfferDA,DAValues))
											{
												String[] ListofAddedOfferDA;
												if(AddedOfferDA.contains(":"))
													ListofAddedOfferDA = AddedOfferDA.split(":")[1].split("\\|");
												else
													break;
												
												for(int i = 0; i<ListofAddedOfferDA.length; i++)
												{
													if(ListofAddedOfferDA[i].length() > 1)
													{
														//361-5368709120
														String DA_ID = ListofAddedOfferDA[i].split("-")[0];
														String Balance = ListofAddedOfferDA[i].split("-")[1];
														
														DAValues.put("ID_" + indx, DA_ID);
														DAValues.put("BALANCE_" + indx, Balance);
														DAValues.put("START_DATE_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
														DAValues.put("EXPIRY_DATE_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
														DAValues.put("PAM_SERVICE_ID_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
														DAValues.put("PRODUCT_ID_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
													
														this.indx++;
													}											 
												}
											}
										}
									}
									ExtraOfferFlag = false;
								}
								
								if(ExtraDAFlag)
								{	
									ExtraDAFlag = false;
									for(String s: DAList)
									{
										if(s.split("\\|")[1].split(";")[7].length() > 0)
										{
											String AddedDA= s.split("\\|")[1].split(";")[7];
											if(!AddedDA.isEmpty())
											{
												String[] ListofAddedDA;
												if(AddedDA.contains("#"))
													ListofAddedDA = AddedDA.split("#");
												else
													ListofAddedDA = new String[]{AddedDA};
												
												for(int i = 0; i<ListofAddedDA.length; i++)
												{
													if(ListofAddedDA[i].length() > 1)
													{
														//361-5368709120
														String DA_ID = ListofAddedDA[i].split("-")[0];
														String Balance = ListofAddedDA[i].split("-")[1];
														
														DAValues.put("ID_" + indx, DA_ID);
														DAValues.put("BALANCE_" + indx, Balance);
														DAValues.put("START_DATE_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
														DAValues.put("EXPIRY_DATE_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
														DAValues.put("PAM_SERVICE_ID_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
														DAValues.put("PRODUCT_ID_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
													
														this.indx++;															
														
														//BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID()
														trackLog.add("INC7002:Add_DA considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + s.split("\\|")[0].split(";")[2] + ":BE_BUCKET_ID="+ s.split("\\|")[1].split(";")[8] + ":DA_ID=" + DA_ID + ":DA_BALANCE=" + Balance + ":ACTION=Logging");
													}											 
												}
											}
										}
									}
									ExtraDAFlag = false;
								}
								//Add Code here
							}
							else
							{
								CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							}
						}
					}
				}
				//System.out.println("End of Thread: " + Thread.currentThread().getName());
			}		
			//CompletedBT_ID.forEach((k->System.out.println(k)));
			return DAValues;
		}
	}

	private synchronized Map<String, String> generateDAAMFromProductMapping() {
		Map<String, String> FinalBalanceDA = new ConcurrentHashMap<>(1000, 0.75f, 30);
		
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
											List<String> ValidAMGroupBalanceDA = item.getValue();
											Set<String> GroupBTID = new HashSet<>();
											ValidAMGroupBalanceDA.forEach(item1->{GroupBTID.add(item1.split("\\|")[1].split(";")[8]);});
											for(String Str : ValidAMGroupBalanceDA)
											{
												String SourceValue = Str.split("\\|")[0];
												String TargetValue = Str.split("\\|")[1];											
												if(TargetValue.split(";")[0].length() != 0)
												{
													String Resource = TargetValue.split(";")[2];
													if(Resource.length() > 1)
													{
														if(!TargetValue.split(";")[0].isEmpty() )
														{
															//String CalculatedBalance = CalculateBalance((item.split("\\|")[0].split(";")[2]),Resource,item.split("\\|")[0].split(";")[1],CurrentGroupBalanceID.get(item.split("\\|")[0].split(";")[1] +","+item.split("\\|")[0].split(";")[2]));
															String CalculatedBalance = CalculateBalance((Str.split("\\|")[0].split(";")[3]),Resource,Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[8],Str.split("\\|")[1].split(";")[0]);
															FinalBalanceDA.putAll(PopulateDedicatedAccount(Str.split("\\|")[1].split(";")[0],CalculatedBalance,Str.split("\\|")[1].split(";")[3],Str.split("\\|")[1].split(";")[4],GroupBTID));
														}
													}	
												}
												AMBTIDSet.addAll(GroupBTID);
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
								String BT_StartDate = item.getValue().split("\\|")[2].trim();
								String BT_ExpiryDate = item.getValue().split("\\|")[3].trim();
								String BE_BucketId = item.getKey();
								if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M") != null)
								{
									boolean MasterGroupFlag = false;
									String DA_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getDAID();
									String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getResource();
									String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getSymbols();
									String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getBTValue();
									
																					
									if(!DA_ID.isEmpty())
									{
										if(Resource.length() > 1 && Symbol.equals(">") && Integer.parseInt(BT_Value) > Integer.parseInt(BT_VALUE))
										{
											MasterGroupFlag = true;
										}
										if(Resource.length() > 1 && Symbol.equals(">=") && Integer.parseInt(BT_Value) >= Integer.parseInt(BT_VALUE))
										{
											MasterGroupFlag = true;
										}
										if(Resource.length() > 1 && Symbol.equals("=") && Integer.parseInt(BT_Value) == Integer.parseInt(BT_VALUE))
										{
											MasterGroupFlag = true;
										}
										if(MasterGroupFlag)
										{
											String CalculatedBalance = CalculateBalance(BT_Value,Resource,BT_ID,BE_BucketId,DA_ID);
											FinalBalanceDA.putAll(PopulateDedicatedAccount(DA_ID,CalculatedBalance,BT_StartDate,BT_ExpiryDate,new HashSet<>(Arrays.asList(BE_BucketId))));
											AMBTIDSet.add(BE_BucketId);	
										}
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
							boolean MasterGroupFlag = false;
							String DA_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getDAID();
							String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getResource();
							String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getSymbols();
							String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getBTValue();
																			
							if(!DA_ID.isEmpty())
							{
								if(Resource.length() > 1 && Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_VALUE))
								{
									MasterGroupFlag = true;
								}
								if(Resource.length() > 1 && Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_VALUE))
								{
									MasterGroupFlag = true;
								}
								if(Resource.length() > 1 && Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_VALUE))
								{
									MasterGroupFlag = true;
								}
								if(MasterGroupFlag)
								{
									String CalculatedBalance = CalculateBalance(Balance_Value,Resource,Balance_ID,balanceInput.getBEBUCKETID(),DA_ID);
									FinalBalanceDA.putAll(PopulateDedicatedAccount(DA_ID,CalculatedBalance,Balance_StartDate,Balance_ExpiryDate,new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
									AMBTIDSet.add(balanceInput.getBEBUCKETID());	
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
											
											List<String> GroupBalanceDA = new ArrayList<>();
											List<String> AMGroupName = (commonfunction.getASpecialGroupKey(LoadSubscriberMapping.BalanceOnlySpecialAMGroupMap,TempBalance_ID1));
											for(String GroupName : AMGroupName)
											{
												GroupBalanceDA.clear();
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
														boolean ExtraDAFlag = false;
														boolean ExtraOfferFlag = false;
														if(GroupBTItems.contains(SourceBTID))
														{
															if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName) != null)
															{							
																String DA_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getDAID();
																String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getSymbols();
																String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTValue();
																String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getProductPrivate();
																String DA_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getDAType();
																String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getResource();
																String ExtraOfferDA = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getAddOffer();
																String ExtraDA = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getAddDA();
																String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTTYPE();
																if(!ExtraOfferDA.isEmpty())
																	ExtraOfferFlag = true;
																else
																	ExtraOfferDA = "";
																if(!ExtraDA.isEmpty())
																	ExtraDAFlag = true;
																else
																	ExtraDA = "";
																
																if(Symbol.equals(">=") && Integer.parseInt(SourceBTValue) >= Integer.parseInt(BT_Value))
																{	
																	GroupBalanceDA.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + SourceBucketID);
																	continue;
																}
																else if(Symbol.equals(">") && Integer.parseInt(SourceBTValue) > Integer.parseInt(BT_Value))
																{
																	GroupBalanceDA.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + SourceBucketID);
																	continue;
																}
																else if(Symbol.equals("=") && Integer.parseInt(SourceBTValue) == Integer.parseInt(BT_Value))
																{
																	GroupBalanceDA.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + SourceBucketID);
																	continue;
																}
																else if(Symbol.equals("or"))
																{
																	//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
																	String[] values = BT_Value.split("#");											
																	if(Arrays.stream(values).anyMatch(SourceBTValue::equals))
																	{
																		GroupBalanceDA.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + SourceBucketID);
																		continue;
																	}
																}
															}
														}
													}
													if(GroupBalanceDA.size() == GroupBTItems.size())
													{
														Set<String> GroupBTID = new HashSet<>();
														gotSameExpiry = true;
														GroupBalanceDA.forEach(item1->{GroupBTID.add(item1.split("\\|")[1].split(";")[8]);});
														for(String Str : GroupBalanceDA)
														{
															String TargetValue = Str.split("\\|")[1];											
															if(TargetValue.split(";")[0].length() != 0)
															{
																String Resource = TargetValue.split(";")[2];
																if(Resource.length() > 1)
																{
																	if(!TargetValue.split(";")[0].isEmpty() )
																	{
																		//String CalculatedBalance = CalculateBalance((item.split("\\|")[0].split(";")[2]),Resource,item.split("\\|")[0].split(";")[1],CurrentGroupBalanceID.get(item.split("\\|")[0].split(";")[1] +","+item.split("\\|")[0].split(";")[2]));
																		String CalculatedBalance = CalculateBalance((Str.split("\\|")[0].split(";")[3]),Resource,Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[8],Str.split("\\|")[1].split(";")[0]);
																		FinalBalanceDA.putAll(PopulateDedicatedAccount(Str.split("\\|")[1].split(";")[0],CalculatedBalance,Str.split("\\|")[1].split(";")[3],Str.split("\\|")[1].split(";")[4],GroupBTID));
																	}
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
												List<String> ValidAMGroupBalanceDA = item.getValue();
												Set<String> GroupBTID = new HashSet<>();
												ValidAMGroupBalanceDA.forEach(item1->{GroupBTID.add(item1.split("\\|")[1].split(";")[8]);});
												for(String Str : ValidAMGroupBalanceDA)
												{
													String TargetValue = Str.split("\\|")[1];											
													if(TargetValue.split(";")[0].length() != 0)
													{
														String Resource = TargetValue.split(";")[2];
														if(Resource.length() > 1)
														{
															if(!TargetValue.split(";")[0].isEmpty() )
															{
																//String CalculatedBalance = CalculateBalance((item.split("\\|")[0].split(";")[2]),Resource,item.split("\\|")[0].split(";")[1],CurrentGroupBalanceID.get(item.split("\\|")[0].split(";")[1] +","+item.split("\\|")[0].split(";")[2]));
																String CalculatedBalance = CalculateBalance((Str.split("\\|")[0].split(";")[3]),Resource,Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[8],Str.split("\\|")[1].split(";")[0]);
																FinalBalanceDA.putAll(PopulateDedicatedAccount(Str.split("\\|")[1].split(";")[0],CalculatedBalance,Str.split("\\|")[1].split(";")[3],Str.split("\\|")[1].split(";")[4],GroupBTID));
															}
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
								
								List<String> GroupBalanceDA = new ArrayList<>();
								List<String> AMGroupName = (commonfunction.getASpecialGroupKey(LoadSubscriberMapping.BalanceOnlySpecialAMGroupMap,TempBalance_ID1));
								for(String GroupName : AMGroupName)
								{
									GroupBalanceDA.clear();
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
											boolean ExtraDAFlag = false;
											if(GroupBTItems.contains(SourceBTID))
											{
												if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName) != null)
												{							
													String DA_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getDAID();
													String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getSymbols();
													String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTValue();
													String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getProductPrivate();
													String DA_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getDAType();
													String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getResource();
													String ExtraOfferDA = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getAddOffer();
													String ExtraDA = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getAddDA();
													String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTTYPE();
													if(!ExtraOfferDA.isEmpty())
														ExtraOfferFlag = true;
													else
														ExtraOfferDA = "";
													if(!ExtraDA.isEmpty())
														ExtraDAFlag = true;
													else
														ExtraDA = "";
													
													if(Symbol.equals(">=") && Integer.parseInt(SourceBTValue) >= Integer.parseInt(BT_Value))
													{	
														GroupBalanceDA.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + SourceBucketID);
														continue;
													}
													else if(Symbol.equals(">") && Integer.parseInt(SourceBTValue) > Integer.parseInt(BT_Value))
													{
														GroupBalanceDA.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + SourceBucketID);
														continue;
													}
													else if(Symbol.equals("=") && Integer.parseInt(SourceBTValue) == Integer.parseInt(BT_Value))
													{
														GroupBalanceDA.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + SourceBucketID);
														continue;
													}
													else if(Symbol.equals("or"))
													{
														//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
														String[] values = BT_Value.split("#");											
														if(Arrays.stream(values).anyMatch(SourceBTValue::equals))
														{
															GroupBalanceDA.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + SourceBucketID);
															continue;
														}
													}
												}
											}
										}
										if(GroupBalanceDA.size() == 2)
										{
											Set<String> GroupBTID = new HashSet<>();
											GroupBalanceDA.forEach(item1->{GroupBTID.add(item1.split("\\|")[1].split(";")[8]);});
											for(String Str : GroupBalanceDA)
											{
												String SourceValue = Str.split("\\|")[0];
												String TargetValue = Str.split("\\|")[1];											
												if(TargetValue.split(";")[0].length() != 0)
												{
													String Resource = TargetValue.split(";")[2];
													if(Resource.length() > 1)
													{
														if(!TargetValue.split(";")[0].isEmpty() )
														{
															//String CalculatedBalance = CalculateBalance((item.split("\\|")[0].split(";")[2]),Resource,item.split("\\|")[0].split(";")[1],CurrentGroupBalanceID.get(item.split("\\|")[0].split(";")[1] +","+item.split("\\|")[0].split(";")[2]));
															String CalculatedBalance = CalculateBalance((Str.split("\\|")[0].split(";")[3]),Resource,Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[8],Str.split("\\|")[1].split(";")[0]);
															FinalBalanceDA.putAll(PopulateDedicatedAccount(Str.split("\\|")[1].split(";")[0],CalculatedBalance,Str.split("\\|")[1].split(";")[3],Str.split("\\|")[1].split(";")[4],GroupBTID));
														}
													}	
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
									boolean MasterGroupFlag = false;
									String DA_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getDAID();
									String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getResource();
									String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getSymbols();
									String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M").getBTValue();
									
																					
									if(!DA_ID.isEmpty())
									{
										if(Resource.length() > 1 && Symbol.equals(">") && Integer.parseInt(BT_Value) > Integer.parseInt(BT_VALUE))
										{
											MasterGroupFlag = true;
										}
										if(Resource.length() > 1 && Symbol.equals(">=") && Integer.parseInt(BT_Value) >= Integer.parseInt(BT_VALUE))
										{
											MasterGroupFlag = true;
										}
										if(Resource.length() > 1 && Symbol.equals("=") && Integer.parseInt(BT_Value) == Integer.parseInt(BT_VALUE))
										{
											MasterGroupFlag = true;
										}
										if(MasterGroupFlag)
										{
											String CalculatedBalance = CalculateBalance(BT_Value,Resource,BT_ID,BE_BucketId,DA_ID);
											FinalBalanceDA.putAll(PopulateDedicatedAccount(DA_ID,CalculatedBalance,BT_StartDate,BT_ExpiryDate,new HashSet<>(Arrays.asList(BE_BucketId))));
												
											AMBTIDSet.add(BE_BucketId);	
										}
									}
									else
										AMBTIDSet.add(BE_BucketId);
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
					String AddedDA = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getAddDA();
					String DA_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getDAID();
					String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getSymbols();
					String BTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTValue();
					boolean CreateAddedDA = false;
					
					if(Symbol.equals(">=") && Integer.parseInt(SourceBTValue) >= Integer.parseInt(BTValue))
					{	
						CreateAddedDA = true;
					}
					else if(Symbol.equals(">") && Integer.parseInt(SourceBTValue) > Integer.parseInt(BTValue))
					{
						CreateAddedDA = true;
					}
					else if(Symbol.equals("=") && Integer.parseInt(SourceBTValue) == Integer.parseInt(BTValue))
					{
						CreateAddedDA = true;
					}
					else if(Symbol.equals("or"))
					{
						//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
						String[] values = BTValue.split("#");											
						if(Arrays.stream(values).anyMatch(SourceBTValue::equals))
						{
							CreateAddedDA = true;
						}
					}
					if(!AddedDA.isEmpty() && CreateAddedDA)
					{
						String[] ListofAddedDA;
						ListofAddedDA = AddedDA.split("\\|");
						
						for(int i = 0; i<ListofAddedDA.length; i++)
						{
							if(ListofAddedDA[i].length() > 1)
							{
								//361-5368709120
								String AddedDAD = ListofAddedDA[i].split("-")[0];
								String Balance = ListofAddedDA[i].split("-")[1];
								
								FinalBalanceDA.put("ID_" + indx, AddedDAD);
								FinalBalanceDA.put("BALANCE_" + indx, Balance);
								FinalBalanceDA.put("START_DATE_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
								FinalBalanceDA.put("EXPIRY_DATE_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
								FinalBalanceDA.put("PAM_SERVICE_ID_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
								FinalBalanceDA.put("PRODUCT_ID_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
								
								trackLog.add("INC7002:Add_DA considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceBTID + ":BE_BUCKET_ID="+ BE_BucketId + ":DA_ID=" + AddedDAD + ":DA_BALANCE=" + Balance +  ":ACTION=Logging");
								this.indx++;
							}											 
						}										
					}					
					AMBTIDSet.add(BE_BucketId);
				}
				else
				{
					AMBTIDSet.add(BE_BucketId);
					
				}
			}
		}
		
		return FinalBalanceDA;
	}
	
	private synchronized boolean CheckIfDAAvailable(String DA_ID, Map<String,String> DAValues) {
		synchronized (DAValues)
		{		
			boolean isDAPresent = true;		
				
			Entry<String, String> result = DAValues.entrySet().stream().filter(e -> e.getKey().startsWith("ID_"))
					.filter(x->x.getValue().equals(DA_ID)).findFirst().orElse(null);
			
			if(result != null)
			{
				if(!result.getValue().equals("1"))
				//if(!result.getValue().equals("1") || !result.getValue().equals("26") || !result.getValue().equals("255") || !result.getValue().equals("254"))			
					return true;
				else
					return false;
					
			}
			return isDAPresent;
		}		
	}
	
	private String ChecknAddGeneralCashBalance(String DA_ID, String Balance, Map<String,String> DAValues)
	{
		String FinalBalance = "";
		Entry<String, String> result = DAValues.entrySet().stream().filter(e -> e.getKey().startsWith("ID_"))
				.filter(x->x.getValue().equals(DA_ID)).findFirst().orElse(null);
		
		if(result != null)
		{
			String BalanceID = "BALANCE_" + result.getKey().split("_")[1];
 			String AvailabeBalance = DAValues.get(BalanceID);
			
			//Calculation for integer
			String regexInteger = "[+-]?[0-9][0-9]*"; 
	        Pattern pInteger = Pattern.compile(regexInteger); 
	        Matcher mInteger = pInteger.matcher(AvailabeBalance);          
	        
	        //Calculation for float
	        String regexFloat = "[+-]?[0-9]+(\\.[0-9]+)"; 
	        Pattern pFloat = Pattern.compile(regexFloat); 
	        Matcher mFloat = pFloat.matcher(AvailabeBalance); 
	          
	        // If match found and equal to input1 
	        
	        if(mInteger.find() && mInteger.group().equals(AvailabeBalance)) 
	        {        	
	        	FinalBalance = String.valueOf(Long.parseLong(AvailabeBalance) + Long.parseLong(Balance));
	        }
	        else if(mFloat.find() && mFloat.group().equals(AvailabeBalance))
	        {
	        	FinalBalance = String.valueOf(Double.parseDouble(AvailabeBalance) + Double.parseDouble(Balance)); 
	        }			
		}
		
		return FinalBalance;
	}
	
	public Map<String,Map<String,Map<String,String>>>  ComputeAMSpecialGroup(String balance_ID,String balance_Value,String START_DATE, String EXPIRY_DATE, String bebucketid, Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub Map<String,List<String>>
		Map<String,String> DAValues = new HashMap<>();
		List<String> AMGroupName = new ArrayList<>();		
		List<String> DAList = new ArrayList<>();
		List<String> FinalDAList = new ArrayList<>();
		Set<String> AMBT_ID = new HashSet<>();
		Set<String> PGroupBT_ID = new HashSet<>();
		boolean ExtraOfferFlag = false;
		boolean ExtraDAFlag = false;
		boolean PGroupBTConsidered = false;
		
		boolean AMGroupFormed = false;
		//Map<String,List<String>> tempGroupBalanceOffer = new HashMap<>();
		
		Map<String,Map<String,Map<String,String>>> GroupDAMap = new HashMap<>();
		AMGroupName = (commonfunction.getASpecialGroupKey(LoadSubscriberMapping.BalanceOnlySpecialAMGroupMap,balance_ID));
		
		
		for(String GroupName : AMGroupName)
		{
			Set<String> GroupElements = new HashSet<>();
			Map<String, String> MapBTIDDateTime = new HashMap<>();
			if(!GroupName.startsWith("A-M"))
			{
				DAList.clear();
				AMBT_ID.clear();
				GroupElements.clear();
				FinalDAList.clear();
				Set<String> GroupBTItems = LoadSubscriberMapping.BalanceOnlySpecialAMGroupMap.get(GroupName);
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
							String DA_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getDAID();
							String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getSymbols();
							String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getBTValue();
							String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getProductPrivate();
							String DA_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getDAType();
							String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getResource();
							String ExtraOfferDA = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getAddOffer();
							String ExtraDA = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getAddDA();
							String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getBTTYPE();
							if(!ExtraOfferDA.isEmpty())
								ExtraOfferFlag = true;
							else
								ExtraOfferDA = "";
							if(!ExtraDA.isEmpty())
								ExtraDAFlag = true;
							else
								ExtraDA = "";
			
							if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
							{
								if(!GroupElements.contains(TempBalance_ID))
								{
									AMBT_ID.add(TempbalanceInput.getBEBUCKETID());
									if(BT_Type.equals("X") || BT_Type.equals("F"))
										MapBTIDDateTime.put(TempBalance_ID, TempbalanceInput.getBEEXPIRY());
									DAList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
									GroupElements.add(TempBalance_ID);
									if(!DA_ID.isEmpty())
										FinalDAList.add(TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
									continue;
								}
							}
							else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
							{
								if(!GroupElements.contains(TempBalance_ID))
								{
									AMBT_ID.add(TempbalanceInput.getBEBUCKETID());
									if(BT_Type.equals("X") || BT_Type.equals("F"))
										MapBTIDDateTime.put(TempBalance_ID, TempbalanceInput.getBEEXPIRY());
									DAList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
									GroupElements.add(TempBalance_ID);
									if(!DA_ID.isEmpty())
										FinalDAList.add(TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
									continue;
								}
							}
							else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
							{
								if(!GroupElements.contains(TempBalance_ID))
								{
									AMBT_ID.add(TempbalanceInput.getBEBUCKETID());
									if(BT_Type.equals("X") || BT_Type.equals("F"))
										MapBTIDDateTime.put(TempBalance_ID, TempbalanceInput.getBEEXPIRY());
									DAList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
									GroupElements.add(TempBalance_ID);
									if(!DA_ID.isEmpty())
										FinalDAList.add(TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
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
										AMBT_ID.add(TempbalanceInput.getBEBUCKETID());
										if(BT_Type.equals("X") || BT_Type.equals("F"))
											MapBTIDDateTime.put(TempBalance_ID, TempbalanceInput.getBEEXPIRY());
										DAList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
										GroupElements.add(TempBalance_ID);
										if(!DA_ID.isEmpty())
											FinalDAList.add(TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());										
										continue;
									}
								}
							}
						}							
					}
				}
				if(DAList.size() == GroupBTItems.size())
				{
					boolean xBTCheck = false;
					String BT55Expiry = MapBTIDDateTime.get("55");
					for(Map.Entry<String, String> entry : MapBTIDDateTime.entrySet())
					{	if(!entry.getValue().equals(BT55Expiry))
						{
							AMGroupFormed = false;
							xBTCheck = true;
							/*//now group is not formed since F and X BT expiry is not same
							//but since P and F present we need to created Offer.
							for(String item : DAList)
							{
								String Resource = item.split("\\|")[1].split(";")[2];
								if(Resource.length() > 1 && item.split("\\|")[1].split(";")[0].length() > 0)
								{
									String CalculatedBalance = CalculateBalance((item.split("\\|")[0].split(";")[3]),Resource,item.split("\\|")[0].split(";")[2],item.split("\\|")[1].split(";")[8],item.split("\\|")[1].split(";")[0]);
									DAValues.putAll(PopulateDedicatedAccount(item.split("\\|")[1].split(";")[0],CalculatedBalance,item.split("\\|")[1].split(";")[3],item.split("\\|")[1].split(";")[4],AMBT_ID));
								}
								if(!item.split("\\|")[0].split(";")[0].equals("X"))
								{
									//Consider only F and P be bucket ID
									PGroupBT_ID.add(item.split("\\|")[1].split(";")[8]);
								}
							}
							PGroupBTConsidered = true;*/
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
			FinalDAList.forEach(item->{
				String Resource = item.split("\\|")[1].split(";")[2];
				if(Resource.length() > 1)
				{
					if(!item.split("\\|")[1].split(";")[0].isEmpty() && CheckIfDAAvailable(item.split("\\|")[1].split(";")[0],DAValues))
					{
						//String CalculatedBalance = CalculateBalance((item.split("\\|")[0].split(";")[2]),Resource,item.split("\\|")[0].split(";")[1],CurrentGroupBalanceID.get(item.split("\\|")[0].split(";")[1] +","+item.split("\\|")[0].split(";")[2]));
						String CalculatedBalance = CalculateBalance((item.split("\\|")[0].split(";")[2]),Resource,item.split("\\|")[0].split(";")[1],item.split("\\|")[1].split(";")[8],item.split("\\|")[1].split(";")[0]);
						DAValues.putAll(PopulateDedicatedAccount(item.split("\\|")[1].split(";")[0],CalculatedBalance,item.split("\\|")[1].split(";")[3],item.split("\\|")[1].split(";")[4],AMBT_ID));							
					}
				}										
			});
		}
		else
		{
			//Group is not formed so check for A-M Group and then go to master
			Map<String,Map<String,Map<String,String>>> AMOutputDetails = new HashMap<>();
			AMOutputDetails = populateAMGroupResult(balance_ID,balance_Value,START_DATE, EXPIRY_DATE,bebucketid,CompletedBT_ID);
			if(PGroupBTConsidered)
			{
				if(PGroupBT_ID.size() != 0 )
				{					
					Map<String,Map<String,String>> OutputDetails = new HashMap<>();
					Map<String,String> tempDAValues = new HashMap<>();
					OutputDetails = AMOutputDetails.get("AMOutputDetails");
					if(OutputDetails.containsKey("DA"))
					{
						tempDAValues.putAll(OutputDetails.get("DA"));
						tempDAValues.putAll(DAValues);						
					}
					else
						tempDAValues.putAll(DAValues);
					
					if(OutputDetails.containsKey("CompletedBT"))
					{
						Map<String,String> temp = new HashMap<>();
						temp = OutputDetails.get("CompletedBT");
						PGroupBT_ID.addAll(Arrays.asList(temp.get("BTID").split(",")));						
					}
					
					Map<String,Map<String,String>> tempAMOutputDetails = new HashMap<>();
					if(PGroupBT_ID.size() != 0)
					{
						Map<String, String> temp = new HashMap<>();
						temp.put("BTID",String.join(",",PGroupBT_ID));
						tempAMOutputDetails.put("CompletedBT",temp);
					}
					if(DAValues.size() != 0)
						tempAMOutputDetails.put("DA", tempDAValues);
					
					
					AMOutputDetails.put("AMOutputDetails",tempAMOutputDetails);
				}
			}
			
			
			return AMOutputDetails;
		}
		
		Map<String,Map<String,String>> AMOutputDetails = new HashMap<>();
		if(AMBT_ID.size() != 0)
		{
			Map<String, String> temp = new HashMap<>();
			temp.put("BTID",String.join(",",AMBT_ID));
			AMOutputDetails.put("CompletedBT",temp);
		}
		if(DAValues.size() != 0)
			AMOutputDetails.put("DA", DAValues);
		
		
		GroupDAMap.put("AMOutputDetails",AMOutputDetails);
		
		return GroupDAMap;
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
		
		List<String> FinalDAList = new ArrayList<>();
		Map<String,List<String>> AMGroupDAMap = new HashMap<>();
		if(ComputedGroupName.length() != 0)
		{	
			boolean ExtraOfferFlag = false;
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
						String DA_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getDAID();
						String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getSymbols();
						String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getBTValue();
						String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getProductPrivate();
						String DA_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getDAType();
						String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getResource();
						String ExtraOfferDA = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getAddOffer();
						String ExtraDA = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getAddDA();
						String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getBTTYPE();
						if(!ExtraOfferDA.isEmpty())
							ExtraOfferFlag = true;
						else
							ExtraOfferDA = "";
						
						if(!ExtraDA.isEmpty())
							ExtraDAFlag = true;
						else
							ExtraDA = "";
						
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						//if(!DA_ID.isEmpty())
						FinalDAList.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + balanceInput.getBEBUCKETVALUE() +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + balanceInput.getBEBUCKETSTARTDATE() + ";" + balanceInput.getBEEXPIRY()   + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + balanceInput.getBEBUCKETID());
					}					
				}
			}			
			AMGroupDAMap.put(FinalGroupName + "|" + AMBTValue, FinalDAList);
		}
		
		return AMGroupDAMap;
	}
		
	private String CalculateBalance(String balance_Value, String Resource, String balance_ID, String bucket_ID, String Target_ID) {
		
		String CalculatedValue = String.valueOf(CalculateBalanceForInteger(Long.parseLong(balance_Value), Resource, balance_ID, bucket_ID, Target_ID));
		
		/*//Calculation for integer
		String regexInteger = "[+-]?[0-9][0-9]*"; 
        Pattern pInteger = Pattern.compile(regexInteger); 
        Matcher mInteger = pInteger.matcher(balance_Value);          
        
        //Calculation for float
        String regexFloat = "[+-]?[0-9]+(\\.[0-9]+)"; 
        Pattern pFloat = Pattern.compile(regexFloat); 
        Matcher mFloat = pFloat.matcher(balance_Value); 
          
        // If match found and equal to input1 
        
        if(mInteger.find() && mInteger.group().equals(balance_Value)) 
        {        	
        	CalculatedValue = String.valueOf(CalculateBalanceForInteger(Long.parseLong(balance_Value), Resource, balance_ID, bucket_ID, Target_ID));
        }
        else if(mFloat.find() && mFloat.group().equals(balance_Value))
        {
        	CalculatedValue = (CalculateBalanceForFloat(Double.parseDouble(balance_Value), Resource, balance_ID, bucket_ID,Target_ID));
        }*/
		
		return CalculatedValue;
	}
	
	private String CalculateBalanceForInteger(Long balance_Value, String Resource, String balance_ID, String bucket_ID,String Target_ID) {
		// TODO Auto-generated method stub
		String TotalBalance = "";
		
		DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(4);
        
        if(balance_Value < 0 && !Arrays.stream(NegativeBalance).anyMatch(balance_ID::equals))
		{
        	trackLog.add("INC7006:Negative BT_Value Found will be migrated as Zero:MSISDN=" + msisdn + ":BALANCE_TYPE=" + balance_ID + ":BE_BUCKET_VALUE=" + balance_Value + ":BE_BUCKET_ID="+ bucket_ID +
        			":TARGET_ID=" + Target_ID + ":MIGRATED_VALUE="+ 0 +":ACTION=Logging");
			return "0";
		}
        
        String RoundOff = LoadSubscriberMapping.ConversionLogicMap.get(Resource).split("\\|")[2];
        BigDecimal bd = null;
		if(Resource.equals("DA") || Resource.equals("DATO6"))
		{			
			bd = new BigDecimal(((balance_Value) * 100));
			
		}
		else if(Resource.equals("TOUC1"))
		{
			bd = new BigDecimal("0");
		}
		else if(Resource.equals("DATO1"))
		{
			bd = new BigDecimal((((balance_Value/10000.0) / 0.1667) * 60) * 1000000);
		}
		else if(Resource.equals("DATO2"))
		{
			bd = new BigDecimal(((balance_Value) * 1024) * 1000000);
		}
		else if(Resource.equals("DATO3"))
		{
			bd = new BigDecimal((((balance_Value)/10000.0)*1048576) * 1000000);
		}
		else if(Resource.equals("DATO4"))
		{
			bd = new BigDecimal(((balance_Value)/100.0) * 1000000);
		}
		else if(Resource.equals("DATO5"))
		{
			bd = new BigDecimal(balance_Value * 1000000);
		}
		else if(Resource.equals("DATO6"))
		{
			bd = new BigDecimal((balance_Value)*100);
		}
		
		if(RoundOff.equals("Y"))
		{			
			if(LoadSubscriberMapping.CommonConfigMap.get("Enable_Logging_for_RoundOff").equals("Y"))
			{	
				long roundedBal = (Long.parseLong(bd.setScale(0, RoundingMode.HALF_UP).toPlainString()));
				
				double diffValue = 0;
				
				diffValue = roundedBal - Double.valueOf((bd.toPlainString()));
				if(diffValue != 0.0)
				{
					GenerateRoundOffDocument(balance_ID, String.valueOf(balance_Value), df.format(roundedBal), String.format("%.4f", diffValue));
					trackLog.add("INC7008:Actual derived value post conversion:MSISDN=" + msisdn + ":BALANCE_TYPE=" + balance_ID + ":BE_BUCKET_VALUE=" + balance_Value + ":BE_BUCKET_ID="+ bucket_ID + ":TARGET_ID="+ Target_ID + ":MIGRATED_VALUE="+ String.valueOf(roundedBal) + ":DERIVED_VALUE="+ String.format("%.4f", bd) +":ACTION=Logging");
				}
				TotalBalance = String.valueOf(roundedBal);
			}
		}
		else
		{
			TotalBalance = String.valueOf(bd);
		}
		
		//Arrays.stream(ExceptionBalances).anyMatch(Balance_ID::equals)
		if(Long.parseLong(TotalBalance) < 0 && !Arrays.stream(NegativeBalance).anyMatch(balance_ID::equals))
		{
			trackLog.add("INC7007:Derived DA_BALANCE or UC_VALUE is negative but migrated as zero:MSISDN=" + msisdn + ":BALANCE_TYPE=" + balance_ID + ":BE_BUCKET_VALUE=" + balance_Value + ":BE_BUCKET_ID="+ bucket_ID + ":TARGET_ID="+ Target_ID + ":MIGRATED_VALUE="+ 0 + ":DERIVED_VALUE="+ TotalBalance +":ACTION=Logging");
			TotalBalance = "0";			
		}
		return TotalBalance;
	}
	
	private Map<String, String> PopulateDedicatedMasterAccount(List<String> FinalDAList,Set<String> BEIDForProductID)
	{
		boolean MasterGroupFlag = false;
		Map<String, String> DAValues = new HashMap<>();
		for(String item: FinalDAList)
		{
			String SourceValue =  item.split("\\|")[0];
			String TargetValue = item.split("\\|")[1];
			if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M") != null)
			{
				String DA_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getDAID();
				String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getResource();
				String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getSymbols();
				String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceValue.split(";")[1] + "|M").getBTValue();
				
																
				if(!DA_ID.isEmpty() )
				{
					if(Resource.length() > 1 && Symbol.equals(">") && Integer.parseInt(item.split("\\|")[0].split(";")[2]) > Integer.parseInt(BT_VALUE))
					{
						MasterGroupFlag = true;																	
					}
					if(Resource.length() > 1 && Symbol.equals(">=") && Integer.parseInt(item.split("\\|")[0].split(";")[2]) >= Integer.parseInt(BT_VALUE))
					{
						MasterGroupFlag = true;																	
					}
					if(Resource.length() > 1 && Symbol.equals("=") && Integer.parseInt(item.split("\\|")[0].split(";")[2]) == Integer.parseInt(BT_VALUE))
					{
						MasterGroupFlag = true;																	
					}
					
					if(MasterGroupFlag)
					{
						String CalculatedBalance = CalculateBalance((item.split("\\|")[0].split(";")[2]),Resource,item.split("\\|")[0].split(";")[1],item.split("\\|")[1].split(";")[8],item.split("\\|")[1].split(";")[0]);
						DAValues.putAll(PopulateDedicatedAccount(item.split("\\|")[1].split(";")[0],String.valueOf(CalculatedBalance),item.split("\\|")[1].split(";")[3],item.split("\\|")[1].split(";")[4],BEIDForProductID));
					}
				}		
			}
		}		
		return DAValues;
	}
	
	private Map<String,String> PopulateDedicatedAccount(String DA_ID,String CalculatedBalance,String Balance_StartDate, String Balance_ExpiryDate,Set<String> BEBucketID )
	{
		Map<String, String> DAValues = new HashMap<>();
		
		DAValues.put("ID_" + indx, DA_ID);
		DAValues.put("BALANCE_" + indx, String.valueOf(CalculatedBalance));
		if(Balance_StartDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
			DAValues.put("START_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
		else
			DAValues.put("START_DATE_" + indx,String.valueOf(CommonUtilities.convertDateToEpoch(Balance_StartDate)));
		if(Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
			DAValues.put("EXPIRY_DATE_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString());
		else
			DAValues.put("EXPIRY_DATE_" + indx, String.valueOf(CommonUtilities.convertDateToEpoch(Balance_ExpiryDate)));
		
		DAValues.put("PAM_SERVICE_ID_" + indx,LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
		
		String Product_ID = commonfunction.GetProductIDCreation(BEBucketID);
		if(BEBucketID.size() != 0)
		{
			if(Product_ID.length() != 0)
				DAValues.put("PRODUCT_ID_" + indx, Product_ID);
			else
				DAValues.put("PRODUCT_ID_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));				
		}
		else
			DAValues.put("PRODUCT_ID_" + indx, LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));	
		
		this.indx++;
		
		return DAValues;
	}
	
	private void GenerateRoundOffDocument(String balance_ID, String balance_Value, String round_value, String Diff_Value) {
		
		Date currDate = new Date();
		InetAddress addr = null;
	    try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Hostname can not be resolved");
			e.printStackTrace();
		}
		SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyyMMddHHmmss");
		String RoundOffValue = addr.getHostName() + "," + "" + "," +"SequenceNumber"+ "," + "5" + "," + sdfDaily.format(currDate) + 
				"," + "" + "," + "" + "," + "" + "," + "" + "," + "" + "," + "" + "," + LoadSubscriberMapping.ProductMappingNameIDMap.get(balance_ID) +
				"," + "" + "," + "," + "S" + "," + sdfDaily.format(currDate) + "," + balance_ID + "," + balance_Value + "," + 
				round_value +  "," + subscriber.getSubscriberInfoCCSACCTTYPEID() +  "," + "Account" + "," + "" + "," + 
				Diff_Value + "," + "NULL" + "," + "NULL" + "," + "NULL" + "," + "NULL" + "," + "NULL" + "," + "NULL" + "" + "," + subscriber.getSubscriberInfoMSISDN();
				
		this.RoundOffLog.add(RoundOffValue);		
	}
	
	
	/*****************BackupCode***********************/
	
	public Map<String,List<String>>  ComputeASGroup(String inputBalance_ID, Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub Map<String,List<String>>
		
		String FinalGroupName ="";		
		List<String> FinalDAList = new ArrayList<>();
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
					String DA_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getDAID();
					String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getSymbols();
					String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTValue();
					String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getProductPrivate();
					String DA_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getDAType();
					String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getResource();
					String ExtraOfferDA = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAddOffer();
					String ExtraDA = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAddDA();
					String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTTYPE();
					if(!ExtraOfferDA.isEmpty())
						ExtraOfferFlag = true;
					else
						ExtraOfferDA = "";
					
					if(!ExtraDA.isEmpty())
						ExtraDAFlag = true;
					else
						ExtraDA = "";
					
					CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
					
					if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
					{
						FinalDAList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()   + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
						continue;
					}
					else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
					{
						FinalDAList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()   + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());						
						continue;
					}
					else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
					{
						FinalDAList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()   + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
						continue;
					}
					else if(Symbol.equals("or"))
					{
						//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
						String[] values = BT_Value.split("#");											
						if(Arrays.stream(values).anyMatch(TempBalance_Value::equals))
						{
							FinalDAList.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + DA_ID + ";" + DA_Type + ";" + Resource  + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()   + ";" + Product_Private + ";" + ExtraOfferDA + ";" + ExtraDA + ";" + TempbalanceInput.getBEBUCKETID());
							continue;
						}																					
					}																			
				}	
			}
		}
		String temp = String.join(",", ASGroupItems);
		ASGroupOfferMap.put(FinalGroupName +"|" + temp, FinalDAList);
		return ASGroupOfferMap;
	}

	private Map<String,Map<String,Map<String,String>>> populateAMGroupResult(String balance_ID,String balance_Value,String START_DATE, String EXPIRY_DATE, String bebucketid, Set<String> CompletedBT_ID) 
	{
		Map<String,String> DAValues = new HashMap<>();
		List<String> AMCompleted_ID = new ArrayList<>();
		Map<String,Map<String,Map<String,String>>> GroupDAMap = new HashMap<>();
		List<String> tempAMUniqueBts = new ArrayList<>(LoadSubscriberMapping.UniqueBalanceOnlyAMGroupMap);
		
		Map<String,String> AMGroupBTs = new HashMap<>();
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo TempbalanceInput :  SortedBalanceInput){
			String TempBalance_ID = TempbalanceInput.getBALANCETYPE();
			String TempBalance_Value = TempbalanceInput.getBEBUCKETVALUE();
			String TempBalance_ExpiryDate = TempbalanceInput.getBEEXPIRY();
			//CurrentGroupBalanceID.put(TempBalance_ID + "," + TempBalance_Value + "," + TempBalance_ExpiryDate, TempbalanceInput.getBEBUCKETID());
			
			if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(TempBalance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(TempBalance_ID).equals("Y"))
			{
				CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
				continue;
			}
			
			if(CompletedBT_ID.contains(TempbalanceInput.getBEBUCKETID()))
				continue;
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
					AMGroupBEBucket.add(Str.split("\\|")[1].split(";")[8]);
				}
			}
			for(Entry<String, List<String>> item : AMGroupOfferMap.entrySet())
			{
				String AMGroupName = item.getKey().split("\\|")[0];
				List<String> UniqueBTID = Arrays.asList(item.getKey().split("\\|")[1].split(",")); 
				List<String> ValidAMGroupBalanceDA = item.getValue();
				
				int i = 1;
				Set<String> CompletedAMBT = new HashSet<>();
				for(String Str : ValidAMGroupBalanceDA)
				{
					String SourceOffer = Str.split("\\|")[0];
					String TargetOffer = Str.split("\\|")[1];
					if(i <= UniqueBTID.size() && UniqueBTID.contains(SourceOffer.split(";")[2]) && !CompletedAMBT.contains(SourceOffer.split(";")[2]))
					{
						i++;
						String Resource = TargetOffer.split(";")[2];
						if(Resource.length() > 1)
						{
							if(!Str.split("\\|")[1].split(";")[0].isEmpty() && CheckIfDAAvailable(Str.split("\\|")[1].split(";")[0],DAValues))
							{
								//String CalculatedBalance = CalculateBalance((item.split("\\|")[0].split(";")[2]),Resource,item.split("\\|")[0].split(";")[1],CurrentGroupBalanceID.get(item.split("\\|")[0].split(";")[1] +","+item.split("\\|")[0].split(";")[2]));
								String CalculatedBalance = CalculateBalance((Str.split("\\|")[0].split(";")[3]),Resource,Str.split("\\|")[0].split(";")[2],Str.split("\\|")[1].split(";")[8],Str.split("\\|")[1].split(";")[0]);
								DAValues.putAll(PopulateDedicatedAccount(Str.split("\\|")[1].split(";")[0],CalculatedBalance,Str.split("\\|")[1].split(";")[3],Str.split("\\|")[1].split(";")[4],AMGroupBEBucket));
							}
						}														
						
						CompletedAMBT.add(SourceOffer.split(";")[2]);
						AMCompleted_ID.add(TargetOffer.split(";")[8]);	
					}					
				}													
			}
		}
		else
		{
			if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(balance_ID + "|M") != null)
			{
				boolean MasterGroupFlag = false;
				String DA_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(balance_ID + "|M").getDAID();
				String Resource = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(balance_ID + "|M").getResource();
				String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(balance_ID + "|M").getSymbols();
				String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(balance_ID + "|M").getBTValue();
				
																
				if(!DA_ID.isEmpty())
				{
					if(Resource.length() > 1 && Symbol.equals(">") && Integer.parseInt(balance_Value) > Integer.parseInt(BT_VALUE))
					{
						MasterGroupFlag = true;
					}
					if(Resource.length() > 1 && Symbol.equals(">=") && Integer.parseInt(balance_Value) >= Integer.parseInt(BT_VALUE))
					{
						MasterGroupFlag = true;
					}
					if(Resource.length() > 1 && Symbol.equals("=") && Integer.parseInt(balance_Value) == Integer.parseInt(BT_VALUE))
					{
						MasterGroupFlag = true;
					}
					if(MasterGroupFlag)
					{
						String CalculatedBalance = CalculateBalance(balance_Value,Resource,balance_ID,bebucketid,DA_ID);
						DAValues.putAll(PopulateDedicatedAccount(DA_ID,CalculatedBalance,START_DATE,EXPIRY_DATE,new HashSet<>(Arrays.asList(bebucketid))));
							
						AMCompleted_ID.add(bebucketid);	
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
			AMOutputDetails.put("DA", DAValues);
		
		
		GroupDAMap.put("AMOutputDetails",AMOutputDetails);
		
		return GroupDAMap;
	}
	
	/*
	
		*****Some lambda expression
		*Map<String,String> DAMapTemp = new HashMap<>();
		Map<String, String> DAMapFinal = new HashMap<>();
		Map<String, String> Temp = new HashMap<>();
		DAMapTemp.putAll(generateDAFromProductMapping());
		//Adding up the Common DA_ID Values
		List<String> DA_IDValue = DAMapTemp.entrySet().stream().filter(e -> e.getKey().startsWith("ID_"))
				.map(Map.Entry::getValue).collect(Collectors.toList());
		
		Set<String> DuplicateValue = DA_IDValue.stream().filter(i -> Collections.frequency(DA_IDValue, i) >1)
                .collect(Collectors.toSet());
		
		
		for(Map.Entry<String, String> entry : DAMapTemp.entrySet())
		{
			if(DuplicateValue.contains(entry.getValue()))
			{
				
				Temp.put(entry.getKey(), entry.getValue());
			}
		}
	*/
}
