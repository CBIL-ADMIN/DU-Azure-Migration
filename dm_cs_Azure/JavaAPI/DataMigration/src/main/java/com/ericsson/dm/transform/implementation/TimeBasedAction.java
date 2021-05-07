package com.ericsson.dm.transform.implementation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ericsson.dm.Utils.CommonUtilities;
import com.ericsson.dm.inititialization.LoadSubscriberMapping;
import com.ericsson.jibx.beans.SubscriberXml;
import com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo;

public class TimeBasedAction implements Comparator<SchemasubscriberbalancesdumpInfo> {

	SubscriberXml subscriber;
	String msisdn;
	String INITIAL_ACTIVATION_DATE;
	
	private Map<String, Set<String>> ProductIDLookUpMap;
	int productID;
	int CounterFor53;
	Set<String> onlyLog;
	Set<String> AMBTIDSet;
	
	ProfileTagProcessing profileTag;
	CommonFunctions commonfunction;
	
	public boolean Offer4805Created = false;
	
	public CopyOnWriteArrayList<SchemasubscriberbalancesdumpInfo> SortedBalanceInput;
	
	public TimeBasedAction()
	{
		
	}
	 
	public TimeBasedAction(SubscriberXml subscriber, Map<String, Set<String>> ProductIDLookUpMap, Set<String> onlyLog) {
		// TODO Auto-generated constructor stub
		this.subscriber=subscriber;
		this.onlyLog = onlyLog;
		SortedBalanceInput = new CopyOnWriteArrayList<>();
		commonfunction = new CommonFunctions(subscriber, ProductIDLookUpMap,this.onlyLog);
		this.AMBTIDSet = new HashSet<>();
		this.ProductIDLookUpMap = ProductIDLookUpMap;
		
	}
	
	public List<String> execute() {
		// TODO Auto-generated method stub
		msisdn = subscriber.getSubscriberInfoMSISDN();
		
		SortedBalanceInput.addAll(subscriber.getBalancesdumpInfoList());
		
		Collections.sort(SortedBalanceInput,new Offer());
		
		List<String> TimeBasedAction = new ArrayList<>();
		//offer creation from Balance Mapping sheet
		TimeBasedAction.addAll(TimeBasedActionFromBalanceMapping());
		
		SortedBalanceInput.clear();
		return TimeBasedAction;
	}
	
	private Collection<String> TimeBasedActionFromBalanceMapping() {
		Date currDate = new Date();
		SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<String> TimeBasedActionList =new ArrayList<>();
		Set<String> CompletedBT_ID = new HashSet<>();
		
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{			
			String Balance_ID = balanceInput.getBALANCETYPE();
			//System.out.println("Master Balance_ID: " + Balance_ID);
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			String Balance_StartDate = balanceInput.getBEBUCKETSTARTDATE();
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			boolean BTPA14BalanceDummy = false;
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
				CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				continue;
			}	
			else
			{	
				if(LoadSubscriberMapping.ExceptionBalances.contains(Balance_ID))
				{	
					if(Balance_ID.equals("3052") || Balance_ID.equals("1496"))
					{
						for(String dummyValue : LoadSubscriberMapping.TimeBasedBalanceDummy.keySet())
						{
							if(dummyValue.contains(Balance_ID))
							{
								String Symbol = LoadSubscriberMapping.TimeBasedBalanceDummy.get(dummyValue).getSymbols();
								String BT_Value = LoadSubscriberMapping.TimeBasedBalanceDummy.get(dummyValue).getBTValue();
								String BT_TYPE = LoadSubscriberMapping.TimeBasedBalanceDummy.get(dummyValue).getBTTYPE();
								String RP_ID = LoadSubscriberMapping.TimeBasedBalanceDummy.get(dummyValue).getRPID();
								String MappingExpiry = LoadSubscriberMapping.TimeBasedBalanceDummy.get(dummyValue).getOfferExpiryDate();
								//if(Balance_ID.equals("3052"))
								if(subscriber.getSubscriberInfoCCSACCTTYPEID().equals(RP_ID) && !BTPA14BalanceDummy)
								{
									if(Symbol.equals("or")) //&& Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
									{
										String[] values = BT_Value.split("#");											
										if(Arrays.stream(values).anyMatch(Balance_Value::equals))
										{
											String Offer_ID = LoadSubscriberMapping.TimeBasedBalanceDummy.get(dummyValue).getOfferID();
											String Offer_Type = LoadSubscriberMapping.TimeBasedBalanceDummy.get(dummyValue).getOfferType();
											boolean startFlag = LoadSubscriberMapping.TimeBasedBalanceDummy.get(dummyValue).getOfferStartDate().length() > 0 ? true:false;
											boolean expiryFalg = LoadSubscriberMapping.TimeBasedBalanceDummy.get(dummyValue).getOfferExpiryDate().length() > 0 ? true:false;
											if(!Offer_ID.isEmpty())
											{
												String Promotion_Day = String.valueOf(CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) - CommonUtilities.convertDateToEpoch(Balance_StartDate));
												long Epochtime = CommonUtilities.convertDateToEpochSeconds(Balance_ExpiryDate);
												int counter = 0;
												for(String Key : LoadSubscriberMapping.TBAMap.keySet())
												{
													if(Integer.parseInt(Key) > Integer.parseInt(Promotion_Day))
													{
														String TBA_Definition_ID = LoadSubscriberMapping.TBAMap.get(Key).split("\\|")[0];
														String TBA_TYPE = LoadSubscriberMapping.TBAMap.get(Key).split("\\|")[1];
														
														StringBuffer sb = new StringBuffer();
														sb.append(msisdn).append(",");
														sb.append(Offer_ID).append(",");
														String Product_ID = commonfunction.GetProductIDCreation(new HashSet<>(Arrays.asList( balanceInput.getBEBUCKETID())));
														
														if(!balanceInput.getBEBUCKETID().isEmpty())
														{
															if(Product_ID.length() != 0)
																sb.append(Product_ID).append(",");
															else
																sb.append("0").append(",");
														}
														else
														{	
															sb.append("0").append(",");
														}														
														sb.append(TBA_Definition_ID).append(",");
														sb.append(TBA_TYPE).append(",");
														sb.append(Epochtime + (counter * 3600 * 24));
														
														TimeBasedActionList.add(sb.toString());
														counter++;
													}
												}
												BTPA14BalanceDummy = true;														
											}
										}
										else
										{
											
										}
									}
								}
								CompletedBT_ID.add(balanceInput.getBEBUCKETID());
								continue;								
							}
						}
					}
				}
			}
		}
		
		return TimeBasedActionList;
	}

	@Override
	public int compare(SchemasubscriberbalancesdumpInfo o1, SchemasubscriberbalancesdumpInfo o2) {
		int value1 = (o2.getBEEXPIRY()).compareTo((o1.getBEEXPIRY()));
        if (value1 == 0) {
        	return  o2.getBEBUCKETID().compareTo(o1.getBEBUCKETID());
        }
        return value1;
	}
}
