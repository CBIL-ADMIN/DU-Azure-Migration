package com.ericsson.dm.transform.implementation;

import java.text.Format;
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
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.joda.time.LocalDate;

import com.ericsson.dm.Utils.CommonUtilities;
import com.ericsson.dm.inititialization.LoadSubscriberMapping;
import com.ericsson.dm.transformation.ExecuteTransformation;
import com.ericsson.jibx.beans.SubscriberXml;
import com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo;
import com.ericsson.jibx.beans.PROFILETAGLIST.PROFILETAGINFO;

public class Offer implements Comparator<SchemasubscriberbalancesdumpInfo> {
	
	SubscriberXml subscriber;
	String msisdn;
	String INITIAL_ACTIVATION_DATE;
	
	Set<String> rejectAndLog;
	Set<String> onlyLog;
	Set<String> trackLog;
	private Map<String, Set<String>> ProductIDLookUpMap;
	private Map<Integer, String> ProductIDFor53; 
	private Map<String, Integer> ProfileProductIDLookUpMap;
	int productID;
	int CounterFor53;
	Set<String> AMBTIDSet;
	Set<String> CommonBTIDSet;
	Set<String> CompletedBTIDSet;
	
	ProfileTagProcessing profileTag;
	CommonFunctions commonfunction;
	
	public boolean Offer4805Created = false;
	public boolean Offer1201Created = false;
	
	public CopyOnWriteArrayList<SchemasubscriberbalancesdumpInfo> SortedBalanceInput;
	
	public Offer()
	{
		
	}
	 
	public Offer(SubscriberXml subscriber,Set<String> rejectAndLog, Set<String> onlyLog, Set<String> trackLog, String INITIAL_ACTIVATION_DATE, Map<String, Set<String>> ProductIDLookUpMap, Map<Integer, String> ProductIDFor53, Map<String,Integer> ProfileProductIDLookUpMap) {
		// TODO Auto-generated constructor stub
		this.subscriber=subscriber;
		this.rejectAndLog = rejectAndLog;
		this.trackLog = trackLog;
		this.onlyLog = onlyLog;
		this.productID = 100;
		this.CounterFor53 = 1;
		this.INITIAL_ACTIVATION_DATE = INITIAL_ACTIVATION_DATE;
		SortedBalanceInput = new CopyOnWriteArrayList<>();
		profileTag = new ProfileTagProcessing(subscriber,this.onlyLog);
		commonfunction = new CommonFunctions(subscriber, ProductIDLookUpMap,this.onlyLog);
		this.AMBTIDSet = new HashSet<>();
		this.CompletedBTIDSet = new HashSet<>();
		this.CommonBTIDSet = new HashSet<>();
		this.ProductIDLookUpMap = ProductIDLookUpMap;
		this.ProductIDFor53 = ProductIDFor53;
		this.ProfileProductIDLookUpMap = ProfileProductIDLookUpMap;
	}
	
	public Map<String, List<String>> execute() {
		// TODO Auto-generated method stub
		msisdn = subscriber.getSubscriberInfoMSISDN();
		
		SortedBalanceInput.addAll(subscriber.getBalancesdumpInfoList());
		
		Collections.sort(SortedBalanceInput,new Offer());
		
		Map<String,List<String>> map = new HashMap<>();
		map.put("Offer", generateOffers());
		map.put("Pam", generatePam());
		
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
	
	private List<String> generateOffers(){
		
		List<String> OfferList = new ArrayList<>();	
		
		//offer creation from Balance Mapping sheet
		OfferList.addAll(offerFromAMBalanceMapping());
	
		//Offer from balance mapping for the BT which duplicate in standalone as well as in the Group level, handling the standalone 
		//seperatly.
		OfferList.addAll(offerCommonBTBalanceMapping());
		
		//offer creation from Balance Mapping sheet
		OfferList.addAll(offerFromBalanceMapping());
		
		//Offer from balance Mapping Grace-* BT_TYPE_INDENTIFIER 
		OfferList.addAll(OfferFromGraceBalanceMapping());
		
		//Offer from balance Mapping Grace-M BT_TYPE_INDENTIFIER 
		OfferList.addAll(OfferFromGraceMBalanceMapping());
		
		//offer creation from Default_Services sheet
		OfferList.addAll(offerFromDefaultService());
				
		//offer creation from Profile_Tags sheet
		OfferList.addAll(offerFromProfileTag());
		
		//offer creation from LifeCycle sheet
		OfferList.addAll(offerFromLifeCycle());
		
		//offer creation from NPPLifeCycle sheet
		OfferList.addAll(offerFromNPPLifeCycle());
				
		//Parse G Group Item and just log the incidence INC4012 as for network bundle logging is only needed.
		GenerationFromGGroupBalanceMapping();
		
		//All balance mapping processed now proceed with logging the left out balances.
		GenerateLoggingForRemaingBalances();
		
		return OfferList;
	}
	
	
	private List<String> offerCommonBTBalanceMapping()
	{
		Date currDate = new Date();
		SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<String> BalanceOfferList =new ArrayList<>();
		Set<String> CompletedBT_ID = new HashSet<>();
		
		CompletedBT_ID.addAll(AMBTIDSet);
		
		//SortedBalanceInput.forEach(x->System.out.println(x.getBALANCETYPE() + "=" + x.getBEBUCKETID()));
		
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{			
			String Balance_ID = balanceInput.getBALANCETYPE();
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			String Balance_StartDate = balanceInput.getBEBUCKETSTARTDATE();
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			
			if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
				continue;
			
			if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(Balance_ID))
				continue;
			
			if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID).equals("Y"))
			{
				//onlyLog.add("INC4003:Balance_Type Ignored:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
				CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				continue;
			}
					
			
			//Check for expiry Date, log it and proceed further
			if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
			{
				//INC4001	Balance_Type expired	MSISDN,BALANCE_TYPE,BE_BUCKET_VALUE,BE_BUCKET_ID,BE_EXPIRY
				//onlyLog.add("INC4001:Balance_Type expired:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
				CompletedBT_ID.add(balanceInput.getBEBUCKETID());
			}
			else
			{
				//Two BT 3503 and 3412 are exceptional as they belong to dummy and Group so just handling based on its value.
				if(LoadSubscriberMapping.CommonBTForDummyGroup.contains(Balance_ID))
				{
					if(Balance_ID.equals("3011"))
					{
						if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
						{
							String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
							String Offer_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferType();
							boolean startFlag = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferStartDate().length() > 0 ? true:false;
							boolean expiryFalg = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferExpiryDate().length() > 0 ? true:false;
							if(!Offer_ID.isEmpty())
							{
								BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							}
							continue;
						}
					}
					
					if(Balance_ID.equals("3503"))
					{
						if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
						{
							String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
							String Offer_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferType();
							boolean startFlag = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferStartDate().length() > 0 ? true:false;
							boolean expiryFalg = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferExpiryDate().length() > 0 ? true:false;
							
							BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							
							/*String StartDate = profileTag.GetProfileTagValue("RLHAct_ReqDate"); //subscriber.getProfiledumpInfoList().get(0).getPriceShout();
							String CurrectStartDate = "";
							String CurrectExpiryDate = "";
							
							if(!StartDate.isEmpty() && StartDate.length() == 14)
								CurrectStartDate = StartDate.substring(0,4) + "-" + StartDate.substring(4,6) + "-" + StartDate.substring(6,8) + " " + StartDate.substring(8,10) + ":" + StartDate.substring(10,12) + ":" + StartDate.substring(12,14);
							
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Calendar c = Calendar.getInstance();
							try 
							{
								c.setTime(sdf.parse(CurrectStartDate));
								c.add(Calendar.DAY_OF_MONTH, 366); 
								CurrectExpiryDate = sdf.format(c.getTime());
								BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,CurrectStartDate, CurrectExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
							continue;
						}
					}
					
					if(Balance_ID.equals("3412"))
					{
						if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
						{
							String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
							String Offer_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferType();
							boolean startFlag = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferStartDate().length() > 0 ? true:false;
							boolean expiryFalg = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferExpiryDate().length() > 0 ? true:false;
							if(!Offer_ID.isEmpty())
							{
								BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							}
							continue;
						}							
					}
					if(Balance_ID.equals("3411"))
					{
						if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
						{
							String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
							String Offer_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferType();
							boolean startFlag = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferStartDate().length() > 0 ? true:false;
							boolean expiryFalg = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferExpiryDate().length() > 0 ? true:false;
							if(!Offer_ID.isEmpty())
							{
								BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							}
							continue;
						}
					}
				}
			}
		}
		CommonBTIDSet.addAll(CompletedBT_ID);
		CompletedBTIDSet.addAll(CompletedBT_ID);
		return BalanceOfferList;
	}
	
	private List<String> offerFromBalanceMapping()
	{		
		Date currDate = new Date();
		SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<String> BalanceOfferList =new ArrayList<>();
		Set<String> OfferCreated = new HashSet<>();
		Set<String> CompletedOffer = new HashSet<>();
		Set<String> BEIDForProductID = new HashSet<>();
		Set<String> CompletedBT_ID = new HashSet<>();
		
		CompletedBT_ID.addAll(AMBTIDSet);
		CompletedBT_ID.addAll(CommonBTIDSet);
		//SortedBalanceInput.forEach(x->System.out.println(x.getBALANCETYPE() + "=" + x.getBEBUCKETID()));
		
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{			
			String Balance_ID = balanceInput.getBALANCETYPE();
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			String Balance_StartDate = balanceInput.getBEBUCKETSTARTDATE();
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			boolean BTPA14BalanceDummy = false;
			boolean BT25OfferCreated = false;
			boolean BT3216fferCreated = false;
			BEIDForProductID.clear();
			
			if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
				continue;
			
			if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(Balance_ID))
				continue;
			
			if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID).equals("Y"))
			{
				onlyLog.add("INC4003:Balance_Type Ignored:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
				CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				continue;
			}
					
			
			//Check for expiry Date, log it and proceed further
			if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
			{
				//INC4001	Balance_Type expired	MSISDN,BALANCE_TYPE,BE_BUCKET_VALUE,BE_BUCKET_ID,BE_EXPIRY
				onlyLog.add("INC4001:Balance_Type expired:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
				CompletedBT_ID.add(balanceInput.getBEBUCKETID());
			}
			else
			{
				//Two BT 3503 and 3412 are exceptional as they belong to dummy and Group so just handling based on its value.
				if(LoadSubscriberMapping.CommonBTForDummyGroup.contains(Balance_ID))
				{
					if(Balance_ID.equals("3011"))
					{
						if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
						{
							/*String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
							String Offer_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferType();
							boolean startFlag = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferStartDate().length() > 0 ? true:false;
							boolean expiryFalg = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferExpiryDate().length() > 0 ? true:false;
							if(!Offer_ID.isEmpty())
							{
								BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							}*/
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							continue;
						}
					}
					
					if(Balance_ID.equals("3503"))
					{
						if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
						{
							/*String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
							String Offer_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferType();
							boolean startFlag = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferStartDate().length() > 0 ? true:false;
							boolean expiryFalg = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferExpiryDate().length() > 0 ? true:false;
							
							BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));*/
							
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							
							/*String StartDate = profileTag.GetProfileTagValue("RLHAct_ReqDate"); //subscriber.getProfiledumpInfoList().get(0).getPriceShout();
							String CurrectStartDate = "";
							String CurrectExpiryDate = "";
							
							if(!StartDate.isEmpty() && StartDate.length() == 14)
								CurrectStartDate = StartDate.substring(0,4) + "-" + StartDate.substring(4,6) + "-" + StartDate.substring(6,8) + " " + StartDate.substring(8,10) + ":" + StartDate.substring(10,12) + ":" + StartDate.substring(12,14);
							
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Calendar c = Calendar.getInstance();
							try 
							{
								c.setTime(sdf.parse(CurrectStartDate));
								c.add(Calendar.DAY_OF_MONTH, 366); 
								CurrectExpiryDate = sdf.format(c.getTime());
								BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,CurrectStartDate, CurrectExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
							continue;
						}
					}
					
					if(Balance_ID.equals("3412"))
					{
						if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
						{
							/*String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
							String Offer_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferType();
							boolean startFlag = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferStartDate().length() > 0 ? true:false;
							boolean expiryFalg = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferExpiryDate().length() > 0 ? true:false;
							if(!Offer_ID.isEmpty())
							{
								BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								
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
							String Offer_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferType();
							boolean startFlag = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferStartDate().length() > 0 ? true:false;
							boolean expiryFalg = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferExpiryDate().length() > 0 ? true:false;
							if(!Offer_ID.isEmpty())
							{
								BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								CompletedBT_ID.add(balanceInput.getBEBUCKETID());
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
							if(BT_TYPE.toUpperCase().equals("P"))
							{
								if(BT_TYPE.toUpperCase().equals("P") && Integer.parseInt(Balance_Value) < 3)
									onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
								else
									onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
									
								CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							}
							else
							{
								if(LoadSubscriberMapping.OnlyGBalancesValueMap.containsKey(Balance_ID))
								{
									Set<String> BalanceValueKey = LoadSubscriberMapping.OnlyGBalancesValueMap.get(Balance_ID);
									if(!BalanceValueKey.contains(Balance_Value))
									{
										onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
										CompletedBT_ID.add(balanceInput.getBEBUCKETID());
										continue;
									}
								}
								else
								{
									CompletedBT_ID.add(balanceInput.getBEBUCKETID());
									onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
								}
							}
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
							onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							continue;
						}
					}
				}
				
				if(LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|") != null)
				{
					Boolean AddOfferFlag = false;
					String Offer_ID = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getOfferID();
					String Symbol = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getSymbols();
					String BT_Value = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getBTValue();
					String Product_Private = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getProductPrivate();
					String Offer_Type = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getOfferType();
					boolean startFlag = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getOfferStartDate().length() > 0 ? true:false;
					boolean expiryFalg = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getOfferExpiryDate().length() > 0 ? true:false;
					String AddedOffer = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getAddOffer();			
					String BT_TYPE = LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|").getBTTYPE();
					if(!Offer_ID.isEmpty())
					{	
						if(Symbol.isEmpty() && BT_Value.isEmpty())
						{	
							BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate, Product_Private,"",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						}
						else
						{
							if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
							{
								BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,Product_Private,"",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								AddOfferFlag = true;
							}
							else if(Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_Value))
							{
								BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,Product_Private,"",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								AddOfferFlag = true;
							}
							else if(Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value))
							{
								BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,Product_Private,"",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								AddOfferFlag = true;
							}
							else if(Symbol.equals("or"))
							{
								//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
								String[] values = BT_Value.split("#");
								
								if(Arrays.stream(values).anyMatch(Balance_Value::equals))
								{
									if(BT_TYPE.equals("P") && Offer_ID.equals("1201"))
										Offer1201Created = true;
									BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,Product_Private,"",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
									AddOfferFlag = true;
								}
								else
								{
									if(BT_TYPE.toUpperCase().equals("P"))
									{
										if(LoadSubscriberMapping.GraceFullBalanceGroupingMap.containsKey(Balance_ID))
										{
											Set<String> BTValues = new HashSet<String>();
											BTValues.addAll(LoadSubscriberMapping.GraceFullBalanceGroupingMap.get(Balance_ID));
											if(!BTValues.contains(Balance_Value))
											{
												if(BT_TYPE.toUpperCase().equals("P") && Integer.parseInt(Balance_Value) < 3)
													onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
												else
													onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
											}											
										}
										else
										{
											if(BT_TYPE.toUpperCase().equals("P") && Integer.parseInt(Balance_Value) < 3)
												onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
											else
												onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
										}
									}
									else
										onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
								}
							}
							else
							{
								if(BT_TYPE.toUpperCase().equals("P"))
								{
									if(LoadSubscriberMapping.GraceFullBalanceGroupingMap.containsKey(Balance_ID))
									{
										Set<String> BTValues = new HashSet<String>();
										BTValues.addAll(LoadSubscriberMapping.GraceFullBalanceGroupingMap.get(Balance_ID));
										if(!BTValues.contains(Balance_Value))
										{
											if(BT_TYPE.toUpperCase().equals("P") && Integer.parseInt(Balance_Value) < 3)
												onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
											else
												onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
										}
									}
									else
									{
										if(BT_TYPE.toUpperCase().equals("P") && Integer.parseInt(Balance_Value) < 3)
											onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
										else
											onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
									}
								}
								else
									onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
							}							
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						}
					}
					if(!AddedOffer.isEmpty() && AddOfferFlag)
					{
						if(!AddedOffer.isEmpty())
						{
							String[] ListofAddedOffer;
							if(AddedOffer.contains(":"))
								ListofAddedOffer = AddedOffer.split(":")[0].split("\\|");
							else
								ListofAddedOffer = AddedOffer.split("\\|");
							
							for(int i = 0; i<ListofAddedOffer.length; i++)
							{
								if(ListofAddedOffer[i].length() > 1)
								{
									String[] OfferValues = ListofAddedOffer[i].split("-");
									//String offer_ID,String Offer_Type, boolean startFlag, boolean expiryFlag, 
									//String Balance_StartDate, String Balance_ExpiryDate, String Product_Private, String flag)
									String Timer = OfferValues[0];
									//String Product_Private = OfferValues[1];
									String AddOffer_ID = OfferValues[2];
									String Start_Date = "";
									String End_Date = "";
									if(OfferValues[3].equals("MIGDATE"))
										Start_Date = sdfDaily.format(currDate);
									
									if(OfferValues[4].startsWith("MIGDATE"))
									{
										int hours2Add = Integer.parseInt(OfferValues[4].replace("MIGDATE+", ""));
										Date NewDate = new Date(currDate.getTime() + hours2Add *3600*1000);
										End_Date = sdfDaily.format(NewDate);
									}														
									BalanceOfferList.add(PopulateOffer(AddOffer_ID,Timer,true,true ,Start_Date, End_Date,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								}											 
							}
						}
					}
					else if(Balance_ID.equals("92") || Balance_ID.equals("239") || Balance_ID.equals("259"))
					{
						if(!AddedOffer.isEmpty())
						{
							String[] ListofAddedOffer;
							if(AddedOffer.contains(":"))
								ListofAddedOffer = AddedOffer.split(":")[0].split("\\|");
							else
								ListofAddedOffer = AddedOffer.split("\\|");
							
							for(int i = 0; i<ListofAddedOffer.length; i++)
							{
								if(ListofAddedOffer[i].length() > 1)
								{
									String[] OfferValues = ListofAddedOffer[i].split("-");
									//String offer_ID,String Offer_Type, boolean startFlag, boolean expiryFlag, 
									//String Balance_StartDate, String Balance_ExpiryDate, String Product_Private, String flag)
									String Timer = OfferValues[0];
									//String Product_Private = OfferValues[1];
									String AddOffer_ID = OfferValues[2];
									String Start_Date = "";
									String End_Date = "";
									if(OfferValues[3].equals("MIGDATE"))
										Start_Date = sdfDaily.format(currDate);
									
									if(OfferValues[4].startsWith("MIGDATE"))
									{
										int hours2Add = Integer.parseInt(OfferValues[4].replace("MIGDATE+", ""));
										Date NewDate = new Date(currDate.getTime() + hours2Add *3600*1000);
										End_Date = sdfDaily.format(NewDate);
									}														
									BalanceOfferList.add(PopulateOffer(AddOffer_ID,Timer,true,true ,Start_Date, End_Date,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								}											 
							}
						}
					}
				}	
				else
				{	
					//if(Arrays.stream(LoadSubscriberMapping.ExceptionBalances).anyMatch(Balance_ID::equals))
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
									{
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
														SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
														Calendar c = Calendar.getInstance();
														try 
														{
															String CurrectExpiryDate = "";
															c.setTime(sdf.parse(Balance_StartDate));
															c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(MappingExpiry.split("\\+")[1])); 
															CurrectExpiryDate = sdf.format(c.getTime());
															BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, CurrectExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
															
														} catch (ParseException e) {
															// TODO Auto-generated catch block
															e.printStackTrace();
														}
														BTPA14BalanceDummy = true;														
													}
												}
												else
												{
													if(BT_TYPE.toUpperCase().equals("P") && Integer.parseInt(Balance_Value) < 3)
														onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
													else
														onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
												}
											}
										}
										CompletedBT_ID.add(balanceInput.getBEBUCKETID());
										continue;
									}									
								}
							}
						}
						
						
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
														String Offer_Type = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferType();
														boolean startFlag = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferStartDate().length() > 0 ? true:false;
														boolean expiryFalg = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferExpiryDate().length() > 0 ? true:false;
														String StartDate = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferStartDate();
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
														//System.out.println("MSISDN------- " + msisdn );
														if(!BT3216fferCreated)
														{
															BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, expiryDate,"","", new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
															
															//In this case we are creating offerID as 101, because OfferAttribute is created as 101. 
															BalanceOfferList.add(PopulateOffer(LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getAttrOfferId(),Offer_Type,startFlag,expiryFalg,"", "","","", new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
															BT3216fferCreated = true;
															DummyDoneFlag = true;
															break;
														}
													}	
												}	
											}
											else
											{
												if(BT_TYPE.toUpperCase().equals("P") && Integer.parseInt(Balance_Value) < 3)
													onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
												else
													onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
											}
										}
									}
									else
										onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
								}
							}
						}
						
						if(Balance_ID.equals("1832") || Balance_ID.equals("1387") || Balance_ID.equals("2112") || Balance_ID.equals("2432"))
						{
							if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
							{
								String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
								String Offer_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferType();
								boolean startFlag = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferStartDate().length() > 0 ? true:false;
								boolean expiryFalg = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferExpiryDate().length() > 0 ? true:false;
								if(Balance_ID.equals("2112"))
									BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								else
									BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, "","","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
							}
							else
							{
								onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
							}
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						}
						if(Balance_ID.equals("21"))
						{							
							//LoadSubscriberMapping.MainBalanceGroupingMap.forEach(action->System.out.println(action));Balance_ID + '|' + CCID + '|' + BTGroupIdentifier
							//LoadSubscriberMapping.MainBalanceGroupingList.forEach(BTGrp->
							for (String BTGrp: LoadSubscriberMapping.MainBalanceGroupingList)
							{
								if(LoadSubscriberMapping.MainBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + subscriber.getSubscriberInfoCCSACCTTYPEID() + '|' + BTGrp) != null)
								{
									String Offer_ID = LoadSubscriberMapping.MainBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + subscriber.getSubscriberInfoCCSACCTTYPEID() + '|' + BTGrp).getOfferID();
									String Symbol = LoadSubscriberMapping.MainBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + subscriber.getSubscriberInfoCCSACCTTYPEID() + '|' + BTGrp).getSymbols();
									String Offer_Type = LoadSubscriberMapping.MainBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + subscriber.getSubscriberInfoCCSACCTTYPEID() + '|' + BTGrp).getOfferType();
									boolean startFlag = LoadSubscriberMapping.MainBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + subscriber.getSubscriberInfoCCSACCTTYPEID() + '|' + BTGrp).getOfferStartDate().length() > 0 ? true:false;
									boolean expiryFalg = LoadSubscriberMapping.MainBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + subscriber.getSubscriberInfoCCSACCTTYPEID() + '|' + BTGrp).getOfferExpiryDate().length() > 0 ? true:false;
									String BT_Value = LoadSubscriberMapping.MainBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + subscriber.getSubscriberInfoCCSACCTTYPEID() + '|' + BTGrp).getBTValue();
									CompletedBT_ID.add(balanceInput.getBEBUCKETID());
									if(!Offer_ID.isEmpty())
									{
										if(Symbol.equals("<") && Double.parseDouble(Balance_Value) < Double.parseDouble(BT_Value))
										{
											if(!CompletedOffer.contains(Offer_ID))
											{								
												CompletedOffer.add(Offer_ID);
												BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
											}
										}
										if(Symbol.equals(">") && Double.parseDouble(Balance_Value) > Double.parseDouble(BT_Value))
										{
											if(!CompletedOffer.contains(Offer_ID))
											{								
												CompletedOffer.add(Offer_ID);
												BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
											}
										}
										else if(Symbol.equals(">=") && Double.parseDouble(Balance_Value) > Double.parseDouble(BT_Value))
										{
											if(!CompletedOffer.contains(Offer_ID))
											{								
												CompletedOffer.add(Offer_ID);
												BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
											}
										}
										else if(Symbol.equals("<=") && Double.parseDouble(Balance_Value) <= Double.parseDouble(BT_Value))
										{
											if(!CompletedOffer.contains(Offer_ID))
											{	
												CompletedOffer.add(Offer_ID);
												BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
											}
										}
										else if(Symbol.equals("=") && Long.parseLong(Balance_Value) == Long.parseLong(BT_Value))
										{	
											if(!CompletedOffer.contains(Offer_ID))
											{
												CompletedOffer.add(Offer_ID);
												BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
											}
										}
										else if(Symbol.equals("or"))
										{
											//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
											String[] values = BT_Value.split("#");											
											if(Arrays.stream(values).anyMatch(Balance_Value::equals))
											{												
												if(!CompletedOffer.contains(Offer_ID))
												{
													CompletedOffer.add(Offer_ID);
													BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
												}
											}
											else
												onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
										}
										else
										{
											if(Double.parseDouble(Balance_Value) > 120.024 && Double.parseDouble(Balance_Value) < 320.064)
												onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
										}
									}
								}
							}
						}						
						if(Balance_ID.equals("1219"))
						{
							if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
							{
								String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
								String Offer_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferType();
								boolean startFlag = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferStartDate().length() > 0 ? true:false;
								boolean expiryFalg = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferExpiryDate().length() > 0 ? true:false;
								String StartDate = profileTag.GetProfileTagValue("PriceShout"); //subscriber.getProfiledumpInfoList().get(0).getPriceShout();
								String CurrectStartDate = "";
								
								if(!StartDate.isEmpty() && StartDate.length() == 14)
									CurrectStartDate = StartDate.substring(0,4) + "-" + StartDate.substring(4,6) + "-" + StartDate.substring(6,8) + " " + StartDate.substring(8,10) + ":" + StartDate.substring(10,12) + ":" + StartDate.substring(12,14);
								
								BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,CurrectStartDate, "","","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
							}
							else
							{
								onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
							}
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
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
									String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + BTValue3011).getOfferID();
									String Offer_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + BTValue3011).getOfferType();
									boolean startFlag = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + BTValue3011).getOfferStartDate().length() > 0 ? true:false;
									boolean expiryFalg = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + BTValue3011).getOfferExpiryDate().length() > 0 ? true:false;
									BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","", new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));									
								}
							}
							else
							{
								onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
							}
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						}
						if(Balance_ID.equals("1512"))
						{
							if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
							{
								String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
								String Offer_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferType();
								boolean startFlag = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferStartDate().length() > 0 ? true:false;
								boolean expiryFalg = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferExpiryDate().length() > 0 ? true:false;
								BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,INITIAL_ACTIVATION_DATE, "","","", new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
							}
							else
							{
								onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
							}
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
							continue;
						}
						if(Balance_ID.equals("1266"))
						{
							if(Balance_Value.equals("0"))
							{
								String Offer_ID = LoadSubscriberMapping.Special1266BalanceDummy.get(Balance_ID + "|=").getOfferID();
								String Offer_Type = LoadSubscriberMapping.Special1266BalanceDummy.get(Balance_ID + "|=").getOfferType();
								boolean startFlag = LoadSubscriberMapping.Special1266BalanceDummy.get(Balance_ID + "|=").getOfferStartDate().length() > 0 ? true:false;
								boolean expiryFalg = LoadSubscriberMapping.Special1266BalanceDummy.get(Balance_ID + "|=").getOfferExpiryDate().length() > 0 ? true:false;
								BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));								
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
						if(Balance_ID.equals("436"))
						{
							if(subscriber.getSubscriberInfoCCSACCTTYPEID().equals("255"))
							{
								String Offer_ID = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":" + subscriber.getSubscriberInfoCCSACCTTYPEID()).getOfferID();
								String RP_ID = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":" + subscriber.getSubscriberInfoCCSACCTTYPEID()).getRPID();
								String Symbol = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":" + subscriber.getSubscriberInfoCCSACCTTYPEID()).getSymbols();
								String BT_Value = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":" + subscriber.getSubscriberInfoCCSACCTTYPEID()).getBTValue();
								String Offer_Type = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":" + subscriber.getSubscriberInfoCCSACCTTYPEID()).getOfferType();
								boolean startFlag = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":" + subscriber.getSubscriberInfoCCSACCTTYPEID()).getOfferStartDate().length() > 0 ? true:false;
								boolean expiryFalg = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":" + subscriber.getSubscriberInfoCCSACCTTYPEID()).getOfferExpiryDate().length() > 0 ? true:false;
								if(!Offer_ID.isEmpty())
								{														
									if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
										BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
									else if(Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_Value))
										BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
									else if(Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value))
										BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
									else if(Symbol.equals("or"))
									{
										//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
										String[] values = BT_Value.split("#");
										
										if(Arrays.stream(values).anyMatch(Balance_Value::equals))
										{
											BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
										}
										else
											onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
									}
									else
										onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
										//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
								}
							}
							else if(subscriber.getSubscriberInfoCCSACCTTYPEID().equals("214") || subscriber.getSubscriberInfoCCSACCTTYPEID().equals("196"))
							{
								String Offer_ID = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":" + "214#196").getOfferID();
								String RP_ID = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":" + "214#196").getRPID();
								String Symbol = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":" + "214#196").getSymbols();
								String BT_Value = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":" + "214#196").getBTValue();
								String Offer_Type = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":" + "214#196").getOfferType();
								boolean startFlag = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":" + "214#196").getOfferStartDate().length() > 0 ? true:false;
								boolean expiryFalg = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":" + "214#196").getOfferExpiryDate().length() > 0 ? true:false;
								if(!Offer_ID.isEmpty())
								{														
									if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
										BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","", new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
									else if(Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_Value))
										BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","", new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
									else if(Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value))
										BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","", new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
									else if(Symbol.equals("or"))
									{
										//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
										String[] values = BT_Value.split("#");
										
										if(Arrays.stream(values).anyMatch(Balance_Value::equals))
										{
											BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
										}
										else
											onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
									}
									else
										onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
										//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
								}
							}
							else
							{
								String Offer_ID = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":").getOfferID();
								String RP_ID = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":").getRPID();
								String Symbol = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":").getSymbols();
								String BT_Value = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":").getBTValue();
								String Offer_Type = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":").getOfferType();
								boolean startFlag = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":").getOfferStartDate().length() > 0 ? true:false;
								boolean expiryFalg = LoadSubscriberMapping.Special436BalanceDummy.get(Balance_ID + ":").getOfferExpiryDate().length() > 0 ? true:false;
								
								if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
									BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","", new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								else if(Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_Value))
									BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","", new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								else if(Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value))
									BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","", new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								else if(Symbol.equals("or"))
								{
									//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
									String[] values = BT_Value.split("#");
									
									if(Arrays.stream(values).anyMatch(Balance_Value::equals))
									{
										BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
									}
									else
										onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
								}
								else
									onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
									//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
							}
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
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
										List<String> OfferOutput  = new ArrayList<>();
										Set<String> ValidOfferTagValue= new HashSet();
										for(String PtValue : PTMappingValue)
										{
											String PT_Name = PtValue.split("-")[0];
											String PT_Symbol = PtValue.split("-")[1];
											String PT_Value = PtValue.split("-")[2];
																														
											String PT_InputValue = profileTag.GetProfileTagValue(PT_Name);
											if(PT_Symbol.equals("=") && PT_InputValue.equals(PT_Value))
											{
												String Offer_ID = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferID();
												String Offer_Type = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferType();
												boolean startFlag = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferStartDate().length() > 0 ? true:false;
												boolean expiryFalg = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferExpiryDate().length() > 0 ? true:false;
												String StartDate = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferStartDate();
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
												//System.out.println("MSISDN------- " + msisdn );
												if(!BT25OfferCreated)
												{
													OfferOutput.add(balanceInput.getBEBUCKETID());
													ValidOfferTagValue.add(Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + Balance_StartDate + ";" + expiryDate + ";" + "" + ";" + "" + ";" + balanceInput.getBEBUCKETID());													
												}
											}
											else if(PT_Symbol.equals("!=") && !PT_InputValue.equals(PT_Value))
											{
												String Offer_ID = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferID();
												String Offer_Type = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferType();
												boolean startFlag = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferStartDate().length() > 0 ? true:false;
												boolean expiryFalg = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferExpiryDate().length() > 0 ? true:false;
												String StartDate = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferStartDate();
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
												//System.out.println("MSISDN------- " + msisdn );
												if(!BT25OfferCreated)
												{
													OfferOutput.add(balanceInput.getBEBUCKETID());
													ValidOfferTagValue.add(Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + Balance_StartDate + ";" + expiryDate + ";" + "" + ";" + "" + ";" + balanceInput.getBEBUCKETID());													
												}
											}
											else
											{
												
											}
										}
										if(OfferOutput.size() == PTMappingValue.size() && !BT25OfferCreated)
										{
											for(String s : ValidOfferTagValue)
											{
												BalanceOfferList.add(PopulateOffer(s.split(";")[0],s.split(";")[1],Boolean.parseBoolean(s.split(";")[2]),Boolean.parseBoolean(s.split(";")[3]),s.split(";")[4], s.split(";")[5],s.split(";")[6],s.split(";")[7], new HashSet<>(Arrays.asList(s.split(";")[8]))));
												BT25OfferCreated = true;
												DummyDoneFlag = true;
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
												String Offer_ID = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferID();
												String Offer_Type = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferType();
												boolean startFlag = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferStartDate().length() > 0 ? true:false;
												boolean expiryFalg = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferExpiryDate().length() > 0 ? true:false;
												String StartDate = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferStartDate();
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
												//System.out.println("MSISDN------- " + msisdn );
												if(!BT25OfferCreated)
												{
													BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, expiryDate,"","", new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
													BT25OfferCreated = true;
													DummyDoneFlag = true;
													break;
												}
											}	
										}
									}*/
								}								
							}
							
							/*if(matchConditionFlag)
							{
								onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
							}*/
							if(!DummyDoneFlag)
							{
								if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M") != null)
								{
									String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getOfferID();
									String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getOfferType();
									String Offer_flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getOfferFlag();
									boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getOfferStartDate().length() > 0 ? true:false;
									boolean expiryFalg = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getOfferExpiryDate().length() > 0 ? true:false;
									String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getSymbols();
									String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getBTValue();
									
									if(Offer_ID.length() > 0 && Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_VALUE))
									{	
										BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"",Offer_flag, new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
										trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID()  +":ACTION=Logging");
										continue;
									}
									if(Offer_ID.length() > 0 && Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_VALUE))
									{	
										BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"",Offer_flag, new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
										trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID()  +":ACTION=Logging");
										continue;
									}
									if(Offer_ID.length() > 0 && Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_VALUE))
									{	
										BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"",Offer_flag, new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
										trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID()  +":ACTION=Logging");
										continue;
									}
									else
										onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID()  +":ACTION=Logging");
								}
								else
								{
									onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID()  +":ACTION=Logging");
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
											String Offer_ID = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferID();
											String Offer_Type = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferType();
											boolean startFlag = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferStartDate().length() > 0 ? true:false;
											boolean expiryFalg = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferExpiryDate().length() > 0 ? true:false;
											String StartDate = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferStartDate();
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
											if(!OfferCreated.contains(Offer_ID))
											{
												OfferCreated.add(Offer_ID);
												DummyDoneFlag =  true;
												BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, expiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
											}
										}									
									}
								}
							}
							if(!DummyDoneFlag)
							{
								if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M") != null)
								{
									String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getOfferID();
									String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getOfferType();
									String Offer_flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getOfferFlag();
									boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getOfferStartDate().length() > 0 ? true:false;
									boolean expiryFalg = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getOfferExpiryDate().length() > 0 ? true:false;
									String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getSymbols();
									String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getBTValue();
									
									if(Offer_ID.length() > 0 && Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_VALUE))
									{	
										BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"",Offer_flag,new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
										trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID()  +":ACTION=Logging");
										continue;
									}
									if(Offer_ID.length() > 0 && Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_VALUE))
									{	
										BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"",Offer_flag,new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
										trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID()  +":ACTION=Logging");
										continue;
									}
									if(Offer_ID.length() > 0 && Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_VALUE))
									{	
										BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"",Offer_flag, new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
										trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID()  +":ACTION=Logging");
										continue;
									}
									else
										onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID()  +":ACTION=Logging");
								}
								else
								{
									onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID()  +":ACTION=Logging");
								}							
							}
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
										if(PT_Symbol.equals("=") && PT_InputValue.equals(PT_Value))
										{
											String Offer_ID = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferID();
											String Offer_Type = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferType();
											boolean startFlag = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferStartDate().length() > 0 ? true:false;
											boolean expiryFalg = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferExpiryDate().length() > 0 ? true:false;
											String StartDate = LoadSubscriberMapping.ProfileTagBalanceDummy.get(Balance_ID + "|" + str).getOfferStartDate();
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
											if(!OfferCreated.contains(Offer_ID))
											{
												OfferCreated.add(Offer_ID);
												BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, expiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
											}
										}									
									}
								}
							}
						}
						if(Balance_ID.equals("3286") || Balance_ID.equals("3231") || Balance_ID.equals("3324") || Balance_ID.equals("2928") || Balance_ID.equals("1453") || Balance_ID.equals("2017") || Balance_ID.equals("2016") || Balance_ID.equals("1760") || Balance_ID.equals("2773") || Balance_ID.equals("1244") || Balance_ID.equals("3451") || Balance_ID.equals("3440") || Balance_ID.equals("3399"))
						{
							String Offer_ID = LoadSubscriberMapping.SpecialBalanceDummyS.get(Balance_ID ).getOfferID();
							String Offer_Type = LoadSubscriberMapping.SpecialBalanceDummyS.get(Balance_ID).getOfferType();
							boolean startFlag = LoadSubscriberMapping.SpecialBalanceDummyS.get(Balance_ID).getOfferStartDate().length() > 0 ? true:false;
							boolean expiryFalg = LoadSubscriberMapping.SpecialBalanceDummyS.get(Balance_ID).getOfferExpiryDate().length() > 0 ? true:false;
							String BT_Value = LoadSubscriberMapping.SpecialBalanceDummyS.get(Balance_ID).getBTValue();
							String Symbol = LoadSubscriberMapping.SpecialBalanceDummyS.get(Balance_ID).getSymbols();
							if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
							{
								for(int i = 0; i < Integer.parseInt(Balance_Value); i++)
								{
									BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								}								
							}
							else
								onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						}
						if(Balance_ID.equals("3233") || Balance_ID.equals("1383") || Balance_ID.equals("1239"))
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
											String Offer_Type = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferType();
											boolean startFlag = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferStartDate().length() > 0 ? true:false;
											boolean expiryFalg = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferExpiryDate().length() > 0 ? true:false;
											if(!Offer_ID.isEmpty())
											{
												BTPA14BalanceDummy = true;
												BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
											}
										}
										else if(Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_Value) && !BTPA14BalanceDummy)
										{
											String Offer_ID = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferID();
											String Offer_Type = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferType();
											boolean startFlag = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferStartDate().length() > 0 ? true:false;
											boolean expiryFalg = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferExpiryDate().length() > 0 ? true:false;
											if(!Offer_ID.isEmpty())
											{
												BTPA14BalanceDummy = true;
												BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
											}
										}
										CompletedBT_ID.add(balanceInput.getBEBUCKETID());
										continue;
									}
									if(Balance_ID.equals("1239"))
									{
										if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value) && !BTPA14BalanceDummy)
										{
											String Offer_ID = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferID();
											String Offer_Type = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferType();
											boolean startFlag = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferStartDate().length() > 0 ? true:false;
											boolean expiryFalg = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferExpiryDate().length() > 0 ? true:false;
											if(!Offer_ID.isEmpty())
											{
												BTPA14BalanceDummy = true;
												BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
											}
										}
										else if(Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value) && !BTPA14BalanceDummy)
										{
											String Offer_ID = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferID();
											String Offer_Type = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferType();
											boolean startFlag = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferStartDate().length() > 0 ? true:false;
											boolean expiryFalg = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferExpiryDate().length() > 0 ? true:false;
											if(!Offer_ID.isEmpty())
											{												
												BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
											}
											BTPA14BalanceDummy = true;
										}										
										CompletedBT_ID.add(balanceInput.getBEBUCKETID());
										continue;
									}
									if(Balance_ID.equals("3233"))
									{
										if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value) && !BTPA14BalanceDummy)
										{
											String Offer_ID = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferID();
											String Offer_Type = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferType();
											boolean startFlag = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferStartDate().length() > 0 ? true:false;
											boolean expiryFalg = LoadSubscriberMapping.SpecialPA14BalanceDummy.get(dummyValue).getOfferExpiryDate().length() > 0 ? true:false;
											if(!Offer_ID.isEmpty())
											{
												BTPA14BalanceDummy = true;
												BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
											}
										}
										CompletedBT_ID.add(balanceInput.getBEBUCKETID());
										//break;
									}
								}
							}
							if(!BTPA14BalanceDummy)
								onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
						}
						if(Balance_ID.equals("3232"))
						{
							if(LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value) != null)
							{
								String Offer_ID = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferID();
								String Offer_Type = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferType();
								boolean startFlag = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferStartDate().length() > 0 ? true:false;
								boolean expiryFalg = LoadSubscriberMapping.SpecialBalanceNonBTGroupIdentifierMap.get(Balance_ID + "|" + Balance_Value).getOfferExpiryDate().length() > 0 ? true:false;
								
								BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"","",new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
							}
							else
							{
								onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
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
						if(OutputDetails.containsKey("Offer"))
							BalanceOfferList.addAll(OutputDetails.get("Offer"));
						
						if(OutputDetails.containsKey("CompletedBT"))
							CompletedBT_ID.addAll(OutputDetails.get("CompletedBT"));
						
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
						Set<String> CurrentGroupBalance = new HashSet<>();
						List<String> ValidGroupBalanceOffer = new ArrayList<>();
						Set<String> ValidGroupBT_ID = new HashSet<>();
						List<String> FinalOfferList = new ArrayList<>();
						Set<String> ExpiryDates = new TreeSet<>();
						boolean MaxDateFlag = false;
						
						//Map<String, String> CurrentGroupBalanceID = new ConcurrentHashMap<>(50, 0.75f, 30);
						boolean ExtraOfferFlag = false;
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
								if(GroupName.startsWith("H-"))
								{
									GroupName = commonfunction.ComputeHGroup(Balance_ID,GroupName,CompletedBT_ID);
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
										MaxDateFlag = false;
										//CurrentGroupBalanceID.put(TempBalance_ID + "," + TempBalance_Value + "," + TempBalance_ExpiryDate, TempbalanceInput.getBEBUCKETID());
										
										if(CompletedBT_ID.contains(TempbalanceInput.getBEBUCKETID()))
											continue;
										
										if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(TempBalance_ID))
											continue;
										
										if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(TempBalance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(TempBalance_ID).equals("Y"))
										{
											onlyLog.add("INC4003:Balance_Type Ignored:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value +  ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + TempbalanceInput.getBEEXPIRY() + ":ACTION=Logging");
											CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
											continue;
										}
										
										if(id.equals(TempBalance_ID))
										{
											if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(id + "|" + FinalGroupName) != null)
											{
												if(!TempBalance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(TempBalance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
												{													
													onlyLog.add("INC4001:Balance_Type expired:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value +  ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + TempbalanceInput.getBEEXPIRY() + ":ACTION=Logging");
													CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
													continue;
												}
												String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getOfferID();
												String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getSymbols();
												String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTValue();
												String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getProductPrivate();
												String Offer_Flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getOfferFlag();
												String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getOfferType();
												String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTTYPE();
												boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getOfferStartDate().length() > 0 ? true:false;
												boolean expiryFalg = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getOfferExpiryDate().length() > 0 ? true:false;
												String ExtraOffer = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAddOffer();
												String PTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getPTName();
												if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getOfferExpiryDate().equals("Max_Expiry_Date"))
													MaxDateFlag = true;													
												
												//ExpiryDates.add((CommonUtilities.convertDateToEpoch(TempBalance_ExpiryDate)));
												ExpiryDates.add(((TempBalance_ExpiryDate)));
												if(!ExtraOffer.isEmpty())
												{
													ExtraOfferFlag = true;
												}
												else
												{
													ExtraOffer = "";
												}
												
												if(Offer_Flag.isEmpty())
												{
													Offer_Flag = LoadSubscriberMapping.CommonConfigMap.get("default_NULL");
												}
												
												if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
												{
													CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
													BEIDForProductID.add(TempbalanceInput.getBEBUCKETID());
													if(PTValue.isEmpty())
													{
														ValidGroupBalanceOffer.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
														ValidGroupBT_ID.add(TempBalance_ID);
														if(!Offer_ID.isEmpty())
															FinalOfferList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
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
																onlyLog.add("INC4011:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
																/*if(BT_Type.toUpperCase().equals("P")  && Integer.parseInt(TempBalance_Value) < 3)
																	onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
																else
																	onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
																*/
																//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + PT_Name + ":Profile_Tag_Value="+ PT_Value + ":ACTION=Logging");
																break;
															}
														}
														if(ValidPT.size() == PT_List.size())
														{
															ValidGroupBalanceOffer.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
															ValidGroupBT_ID.add(TempBalance_ID);
															if(!Offer_ID.isEmpty())
																FinalOfferList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
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
														ValidGroupBalanceOffer.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
														ValidGroupBT_ID.add(TempBalance_ID);
														if(!Offer_ID.isEmpty())
															FinalOfferList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
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
																onlyLog.add("INC4011:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
																/*if(BT_Type.toUpperCase().equals("P")  && Integer.parseInt(TempBalance_Value) < 3)
																	onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
																else
																	onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
																*/
																//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + PT_Name + ":Profile_Tag_Value="+ PT_Value + ":ACTION=Logging");
																break;
															}
														}
														if(ValidPT.size() == PT_List.size())
														{
															ValidGroupBalanceOffer.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
															ValidGroupBT_ID.add(TempBalance_ID);
															if(!Offer_ID.isEmpty())
																FinalOfferList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
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
														ValidGroupBalanceOffer.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
														ValidGroupBT_ID.add(TempBalance_ID);
														if(!Offer_ID.isEmpty())
															FinalOfferList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
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
																onlyLog.add("INC4011:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
																/*if(BT_Type.toUpperCase().equals("P")  && Integer.parseInt(TempBalance_Value) < 3)
																	onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
																else
																	onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");*/
																
																//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + PT_Name + ":Profile_Tag_Value="+ PT_Value + ":ACTION=Logging");
																break;
															}
														}
														if(ValidPT.size() == PT_List.size())
														{
															ValidGroupBalanceOffer.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
															ValidGroupBT_ID.add(TempBalance_ID);
															if(!Offer_ID.isEmpty())
																FinalOfferList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
															break;
														}														
													}
												}
												else if(Symbol.equals("or"))
												{
													//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
													String[] values = BT_Value.split("#");											
													if(Arrays.stream(values).anyMatch(TempBalance_Value::equals))
													{
														CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
														BEIDForProductID.add(TempbalanceInput.getBEBUCKETID());
														if(PTValue.isEmpty())
														{
															ValidGroupBalanceOffer.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
															ValidGroupBT_ID.add(TempBalance_ID);
															if(!Offer_ID.isEmpty())
																FinalOfferList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
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
																	onlyLog.add("INC4011:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
																	//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + PT_Name + ":Profile_Tag_Value="+ PT_Value + ":ACTION=Logging");
																	break;
																}
															}
															if(ValidPT.size() == PT_List.size())
															{
																ValidGroupBalanceOffer.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
																ValidGroupBT_ID.add(TempBalance_ID);
																if(!Offer_ID.isEmpty())
																	FinalOfferList.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
																break;
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
																if(BT_Type.toUpperCase().equals("P")  && Integer.parseInt(TempBalance_Value) < 3)
																	onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
																else
																	onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
															}
														}
														else
														{
															if(BT_Type.toUpperCase().equals("P")  && Integer.parseInt(TempBalance_Value) < 3)
																onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
															else
																onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
														}
														break;
													}														
												}
												/*else
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
													else
													{
														if(BT_Type.toUpperCase().equals("P")  && Integer.parseInt(TempBalance_Value) < 3)
															onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
														else
															onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
													}													
												}*/													
											}
										}
									}
								}
								
								if(FinalGroupName.startsWith("A-") && ValidGroupBalanceOffer.size() != 0)
								{
									if(ValidGroupBalanceOffer.size() == CurrentGroupBalance.size())
									{
										String TargetOffer =  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[1];
										
										Set<String> GroupBTID = new HashSet<>();
										ValidGroupBalanceOffer.forEach(item->{GroupBTID.add(item.split("\\|")[1].split(";")[9]);});
										
										ExtraOfferFlag = false;
										if(MaxDateFlag)
										{
											String ExpiryDate = ExpiryDates.stream().reduce((first, second) -> second).orElse(null);
											BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
													Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], ExpiryDate,TargetOffer.split(";")[6],TargetOffer.split(";")[7],GroupBTID));
										}										
										else
											BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
													Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7],GroupBTID));
									
										CompletedBT_ID.addAll(GroupBTID);									
									}
									else
									{
										if(FinalGroupName.equals("A-3") && ValidGroupBalanceOffer.size() == 3)
										{
											Set<String> A_S_2 = new HashSet<>(Arrays.asList("2924","3034","2972"));
											Set<String> GroupBTID = new HashSet<>();
											ValidGroupBalanceOffer.forEach(item->{GroupBTID.add(item.split("\\|")[0].split(";")[2]);});
											
											
											if(A_S_2.containsAll(GroupBTID))
											{
												BalanceOfferList.addAll(PopulateAS2Group(ValidGroupBalanceOffer, BEIDForProductID));
												continue;
											}
										}
										
										//if(LoadSubscriberMapping.UniqueBalanceOnlyAMGroupMap.stream().allMatch(t -> ValidGroupBT_ID.stream().anyMatch(t::contains))) {
										if(LoadSubscriberMapping.BalanceAVGroupLookup.get(FinalGroupName) != null)
										{
											//ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).count()
											if(ValidGroupBalanceOffer.size() >= 2 && ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).count() >=1 && ValidGroupBalanceOffer.stream().filter(item->item.startsWith("V")).count() >=1){
												String TargetOffer =  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[1];
												
												BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
														Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7], BEIDForProductID));
												//ExtraOfferFlag = false;
											}
											//else if(ValidGroupBalanceOffer.size() >= 2 &&  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("V")).count() >=1){
											else if(ValidGroupBalanceOffer.size() >= 2 ){
												BalanceOfferList.addAll(PopulateMasterOffer(ValidGroupBalanceOffer,BEIDForProductID));
												ExtraOfferFlag = false;
											}
											else if(ValidGroupBalanceOffer.size() == 1)
											{
												//BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private
												if(ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).count() == 1 ){
													String TargetOffer =  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[1];
													String SourceOffer = ValidGroupBalanceOffer.get(0).split("\\|")[0];
													if(Integer.parseInt(SourceOffer.split(";")[3]) < 3)
														onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + SourceOffer.split(";")[3] + ":BE_BUCKET_ID=" + TargetOffer.split(";")[9] +":ACTION=Logging");
													else
														onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + SourceOffer.split(";")[3] + ":BE_BUCKET_ID=" + TargetOffer.split(";")[9] +":ACTION=Logging");
													ExtraOfferFlag = false;
												}												
												if((ValidGroupBalanceOffer.stream().filter(item->item.startsWith("V")).count() == 1)){
													//String TargetOffer = ValidGroupBalanceOffer.get(0).split("\\|")[1];
													//String SourceOffer = ValidGroupBalanceOffer.get(0).split("\\|")[0];
													//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + SourceOffer.split(";")[3] + ":BE_BUCKET_ID=" + TargetOffer.split(";")[9] +":ACTION=Logging");													
													BalanceOfferList.addAll(PopulateMasterOffer(ValidGroupBalanceOffer,BEIDForProductID));
												}
												if((ValidGroupBalanceOffer.stream().filter(item->item.startsWith("M")).count() == 1)){
													BalanceOfferList.addAll(PopulateMasterOffer(ValidGroupBalanceOffer,BEIDForProductID));
												}
												//CompletedBT_ID.addAll(CurrentGroupBalance);
											}
										}
										else
										{
											//ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).count()
											if(ValidGroupBalanceOffer.size() >= 2 && ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).count() >=1 && ValidGroupBalanceOffer.stream().filter(item->item.startsWith("M")).count() >=1){
												String TargetOffer =  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[1];
												ExtraOfferFlag = false;
												BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
														Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7],BEIDForProductID));
												
											}
											else if(ValidGroupBalanceOffer.size() >= 2 &&  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("M")).count() >=1){
												BalanceOfferList.addAll(PopulateMasterOffer(ValidGroupBalanceOffer,BEIDForProductID));										
											}
											else if(ValidGroupBalanceOffer.size() == 1)
											{
												//BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private
												if(ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).count() == 1 ){
													String TargetOffer =  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[1];
													// Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + Balance_StartDate + ";" + Balance_ExpiryDate  + ";" + Product_Private
													BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
															Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7],BEIDForProductID));
												}
												if(ValidGroupBalanceOffer.stream().filter(item->item.startsWith("M")).count() == 1){
													BalanceOfferList.addAll(PopulateMasterOffer(ValidGroupBalanceOffer,BEIDForProductID));
												}
												if(ValidGroupBalanceOffer.stream().filter(item->item.startsWith("X")).count() == 1){
													BalanceOfferList.addAll(PopulateMasterOffer(ValidGroupBalanceOffer,BEIDForProductID));
												}
												//CompletedBT_ID.addAll(CurrentGroupBalance);
											}
										}
									}						
								}
								if(FinalGroupName.startsWith("B-") && ValidGroupBalanceOffer.size() != 0)
								{
									if(ValidGroupBalanceOffer.size() == CurrentGroupBalance.size())
									{
										//String TargetOffer =  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("C")).collect(Collectors.toList()).get(0).split("\\|")[1];
										// Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + Balance_StartDate + ";" + Balance_ExpiryDate  + ";" + Product_Private
										Set<String> GroupBTID = new HashSet<>();
										ValidGroupBalanceOffer.forEach(item->{GroupBTID.add(item.split("\\|")[1].split(";")[9]);});
										
										ExtraOfferFlag = false;
										if(MaxDateFlag)
										{
											for (String item: ValidGroupBalanceOffer)
											{
												String TargetOffer = item.split("\\|")[1];
												if(TargetOffer.split(";")[0].length() > 0)
												{
													String ExpiryDate = ExpiryDates.stream().reduce((first, second) -> second).orElse(null);
													BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
														Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], ExpiryDate,TargetOffer.split(";")[6],TargetOffer.split(";")[7],GroupBTID));
												}
											}
										}										
										else
										{
											for (String item: ValidGroupBalanceOffer)
											{
												String TargetOffer = item.split("\\|")[1];
				
												if(TargetOffer.split(";")[0].length() > 0)
												{
													BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
														Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7],GroupBTID));
												}
											}
										}
											
										//CompletedBT_ID.addAll(CurrentGroupBalance);
									}
									else
									{
										for(String Str : ValidGroupBalanceOffer)
										{
											String TargetOffer = Str.split("\\|")[0];
											String SourceOffer = Str.split("\\|")[1]; 
											if(TargetOffer.split(";")[0].equals("P") && Integer.parseInt(TargetOffer.split(";")[3]) < 3)
												onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
											else
												onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
										}							
									}
								}
								
								/*if(FinalGroupName.startsWith("C-") && ValidGroupBalanceOffer.size() != 0)
								{
									if(ValidGroupBalanceOffer.size() == CurrentGroupBalance.size())
									{	
										ExtraOfferFlag = false;
										String TargetOffer =  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[1];
										Set<String> GroupBTID = new HashSet<>();
										ValidGroupBalanceOffer.forEach(item->{GroupBTID.add(item.split("\\|")[1].split(";")[9]);});
										
										if(MaxDateFlag)
										{
											String ExpiryDate = ExpiryDates.stream().reduce((first, second) -> second).orElse(null);
											BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
													Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], ExpiryDate,TargetOffer.split(";")[6],TargetOffer.split(";")[7],GroupBTID));
										}										
										else
											BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
												Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7],GroupBTID));
										//CompletedBT_ID.addAll(CurrentGroupBalance);
									}	
									else
									{
										//ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).count()
										if(ValidGroupBalanceOffer.size() >= 2 && ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).count() >=1 && ValidGroupBalanceOffer.stream().filter(item->item.startsWith("M")).count() >=1){
											String TargetOffer =  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[1];
											// Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + Balance_StartDate + ";" + Balance_ExpiryDate  + ";" + Product_Private
											
											BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
													Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7],BEIDForProductID));
											
											ValidGroupBalanceOffer.forEach(item->{
												CompletedBT_ID.add(item.split("\\|")[0].split(";")[2]);
											});
										}
										else if(ValidGroupBalanceOffer.size() >= 2 &&  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("M")).count() >=1){
										
											BalanceOfferList.addAll(PopulateMasterOffer(ValidGroupBalanceOffer,BEIDForProductID));										
										}
										else if(ValidGroupBalanceOffer.size() == 1)
										{
											//BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private
											if(ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).count() == 1 ){
												String TargetOffer =  ValidGroupBalanceOffer.get(0).split("\\|")[0];
												String SourceOffer =  ValidGroupBalanceOffer.get(0).split("\\|")[1];
												//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ CurrentGroupBalanceID.get(TargetOffer.split(";")[2] + "," + TargetOffer.split(";")[3] + "," + SourceOffer.split(";")[5])  +":ACTION=Logging");
												if(Integer.parseInt(TargetOffer.split(";")[3]) > 3)
													onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
												else
													onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
											}
											if(ValidGroupBalanceOffer.stream().filter(item->item.startsWith("M")).count() == 1){
												
												BalanceOfferList.addAll(PopulateMasterOffer(ValidGroupBalanceOffer,BEIDForProductID));
												
											}										
										}
										//CompletedBT_ID.addAll(CurrentGroupBalance);
									}								
								}*/
								if(FinalGroupName.startsWith("D-") && ValidGroupBalanceOffer.size() != 0)
								{
									if(ValidGroupBalanceOffer.size() == CurrentGroupBalance.size())
									{		
										ExtraOfferFlag = false;
										String TargetOffer =  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("C")).collect(Collectors.toList()).get(0).split("\\|")[1];
										Set<String> GroupBTID = new HashSet<>();
										ValidGroupBalanceOffer.forEach(item->{GroupBTID.add(item.split("\\|")[1].split(";")[9]);});
										
										if(MaxDateFlag)
										{
											String ExpiryDate = ExpiryDates.stream().reduce((first, second) -> second).orElse(null);
											BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
													Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], ExpiryDate,TargetOffer.split(";")[6],TargetOffer.split(";")[7],GroupBTID));
										}										
										else
											BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
												Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7],GroupBTID));
										//CompletedBT_ID.addAll(CurrentGroupBalance);
									}	
									else
									{
										BalanceOfferList.addAll(PopulateMasterOffer(ValidGroupBalanceOffer,BEIDForProductID));
									}
								}
								if(FinalGroupName.startsWith("H-") && ValidGroupBalanceOffer.size() != 0)
								{
									if(ValidGroupBalanceOffer.size() == CurrentGroupBalance.size())
									{	
										ExtraOfferFlag = false;										
										for(String OfferValue : ValidGroupBalanceOffer)
										{
											String OfferId = OfferValue.split("\\|",-1)[1].split(";",-1)[0];
											if(!OfferId.isEmpty())
											{
												String TargetOffer = OfferValue.split("\\|",-1)[1];
												
												String OfferCount =  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("S")).collect(Collectors.toList()).get(0).split("\\|")[0].split(";")[3];
												
												for(int i = 0; i < Integer.parseInt(OfferCount); i++)
												{
													if(MaxDateFlag)
													{
														String ExpiryDate = ExpiryDates.stream().reduce((first, second) -> second).orElse(null);
														BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
																Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], ExpiryDate,TargetOffer.split(";")[6],TargetOffer.split(";")[7],new HashSet<>(Arrays.asList(TargetOffer.split(";")[9]))));
													}										
													else
														BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
															Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7],BEIDForProductID));
													//CompletedBT_ID.addAll(CurrentGroupBalance);
												}
											}
										}										
									}
									else
									{		
										//This change came in 14_4, if SBT alone found without PBT then log it for specific group F-L and F-M
										for(String OfferValue : ValidGroupBalanceOffer)
										{
											String BT_ID = OfferValue.split("\\|")[0].split(";")[2];
											String BT_Value = OfferValue.split("\\|")[0].split(";")[3];
											String BucketID = OfferValue.split("\\|")[1].split(";")[9];
											onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID=" + BucketID +":ACTION=Logging");
										}
									}
								}
								
								if(FinalGroupName.startsWith("F-") && ValidGroupBalanceOffer.size() != 0)
								{
									if(ValidGroupBalanceOffer.size() == CurrentGroupBalance.size())
									{	
										ExtraOfferFlag = false;
										//the reason for putting code in catch is some group doesn't have P so in that group check for M 
										//BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer
										for(String OfferValue : FinalOfferList)
										{
											String OfferId = OfferValue.split("\\|",-1)[1].split(";",-1)[0];
											if(!OfferId.isEmpty())
											{
												String TargetOffer = OfferValue.split("\\|",-1)[1];
												if(OfferValue.split("\\|",-1)[0].split(";",-1)[0].equals("S"))
												{													
													for(int i = 0; i < Integer.parseInt(OfferValue.split("\\|",-1)[0].split(";",-1)[3]); i++)
													{
														if(MaxDateFlag)
														{
															String ExpiryDate = ExpiryDates.stream().reduce((first, second) -> second).orElse(null);
															BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
																	Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], ExpiryDate,TargetOffer.split(";")[6],TargetOffer.split(";")[7],new HashSet<>(Arrays.asList(TargetOffer.split(";")[9]))));
														}										
														else
															BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
																Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7],BEIDForProductID));
														//CompletedBT_ID.addAll(CurrentGroupBalance);
													}
												}
												else
												{
													BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
															Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7],BEIDForProductID));
												}
											}
										}										
									}
									else
									{	
										String MPresent = ValidGroupBalanceOffer.stream().filter(item->item.startsWith("M")).findFirst().orElse(null);
										if(MPresent != null)
										{
											if(ValidGroupBalanceOffer.size() == 1 && FinalOfferList.size() == 0)
											{
												String GroupLastChar = FinalGroupName.substring(FinalGroupName.lastIndexOf('-')+1,FinalGroupName.length());
												String MasterGroupName = FinalGroupName.replace(GroupLastChar, "M");
												String BT_ID = MPresent.split("\\|")[0].split(";")[2];
												String BT_Type = MPresent.split("\\|")[0].split(";")[0];
												String SourceBT_Value = MPresent.split("\\|")[0].split(";")[3];
												String BT_StartDate  = MPresent.split("\\|")[1].split(";")[4];
												String BT_ExpiryDate  = MPresent.split("\\|")[1].split(";")[5];
												String BucketID = MPresent.split("\\|")[1].split(";")[9];
												if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName) != null)
												{
													String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getOfferID();
													String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getOfferType();
													String Offer_flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getOfferFlag();
													boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getOfferStartDate().length() > 0 ? true:false;
													boolean expiryFalg = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getOfferExpiryDate().length() > 0 ? true:false;
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
														onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + SourceBT_Value + ":BE_BUCKET_ID=" + BucketID +":ACTION=Logging");
														break;
													}
													if(BT_Type.equals("M") && bCreateOffer)
													{														
														BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,BT_StartDate, BT_ExpiryDate,"",Offer_flag,BEIDForProductID));
														trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ BucketID  +":ACTION=Logging");
													}
													else if(BT_Type.equals("S") && bCreateOffer)
													{
														for(int i = 0; i < Integer.parseInt(SourceBT_Value); i++)
														{
															BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,BT_StartDate, BT_ExpiryDate,"",Offer_flag,BEIDForProductID));
															//trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ BucketID  +":ACTION=Logging");
														}
													}
												}
												else
												{
													if(BT_Type.equals("P") && Integer.parseInt(SourceBT_Value) < 3)
														onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + SourceBT_Value + ":BE_BUCKET_ID=" + BucketID +":ACTION=Logging");
													else
														onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + SourceBT_Value + ":BE_BUCKET_ID=" + BucketID +":ACTION=Logging");
												}												
											}
											else
											{
												String PCPresent = ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).findFirst().orElse(null);
												if(PCPresent == null)
												{
													String GroupLastChar = FinalGroupName.substring(FinalGroupName.lastIndexOf('-')+1,FinalGroupName.length());
													String MasterGroupName = FinalGroupName.replace(GroupLastChar, "M");
													for(String OfferValue : ValidGroupBalanceOffer)
													{
														String BT_ID = OfferValue.split("\\|")[0].split(";")[2];
														String BT_Type = OfferValue.split("\\|")[0].split(";")[0];
														String SourceBT_Value = OfferValue.split("\\|")[0].split(";")[3];
														String BT_StartDate  = OfferValue.split("\\|")[1].split(";")[4];
														String BT_ExpiryDate  = OfferValue.split("\\|")[1].split(";")[5];
														String BucketID = OfferValue.split("\\|")[1].split(";")[9];
														if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName) != null)
														{
															String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getOfferID();
															String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getOfferType();
															String Offer_flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getOfferFlag();
															boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getOfferStartDate().length() > 0 ? true:false;
															boolean expiryFalg = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getOfferExpiryDate().length() > 0 ? true:false;
															String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getSymbols();
															String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getBTValue();
															//String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getBTTYPE();
															boolean bCreateOffer = false;
															if(Symbol.equals(">=") && Integer.parseInt(SourceBT_Value) >= Integer.parseInt(BT_VALUE))
																bCreateOffer = true;
															else if(Symbol.equals(">") && Integer.parseInt(SourceBT_Value) > Integer.parseInt(BT_VALUE))
																bCreateOffer = true;
															else if(Symbol.equals("=") && Integer.parseInt(SourceBT_Value) == Integer.parseInt(BT_VALUE))
																bCreateOffer = true;
															else
															{
																//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + SourceBT_Value + ":BE_BUCKET_ID="+ CurrentGroupBalanceID.get(BT_ID + "," + SourceBT_Value + "," + BT_ExpiryDate)  +":ACTION=Logging");
																onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + SourceBT_Value + ":BE_BUCKET_ID="+ BucketID  +":ACTION=Logging");
																break;
															}
															if(BT_Type.equals("M") && bCreateOffer)
															{														
																BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,BT_StartDate, BT_ExpiryDate,"",Offer_flag, BEIDForProductID));
																trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":OFFER_ID=" + Offer_ID +  ":BE_BUCKET_ID="+ BucketID  +":ACTION=Logging");
															}
															else if(BT_Type.equals("S") && bCreateOffer)
															{
																for(int i = 0; i < Integer.parseInt(SourceBT_Value); i++)
																{
																	BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,BT_StartDate, BT_ExpiryDate,"",Offer_flag, BEIDForProductID));
																	//trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ BucketID  +":ACTION=Logging");
																}
															}
														}
														else
														{												
															onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + SourceBT_Value + ":BE_BUCKET_ID="+ BucketID  +":ACTION=Logging");
														}
													}
												}
												else
												{													
													ExtraOfferFlag = false;
													for(String OfferValue : FinalOfferList)
													{
														String OfferId = OfferValue.split("\\|",-1)[1].split(";",-1)[0];
														if(!OfferId.isEmpty())
														{
															String TargetOffer = OfferValue.split("\\|",-1)[1];
															if(OfferValue.split("\\|",-1)[0].split(";",-1)[0].equals("S"))
															{													
																for(int i = 0; i < Integer.parseInt(OfferValue.split("\\|",-1)[0].split(";",-1)[3]); i++)
																{
																	BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
																			Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7], BEIDForProductID));
																	//CompletedBT_ID.addAll(CurrentGroupBalance);
																}
															}
															else
															{
																BalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
																		Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7], BEIDForProductID));
															}
														}
													}	
												}
											}											
										}
										else
										{
											for(String OfferValue : FinalOfferList)
											{		
												//String TargetOffer = OfferValue.split("\\|",-1)[0];
												//String SourceOffer = OfferValue.split("\\|",-1)[1];
												String GroupLastChar = FinalGroupName.substring(FinalGroupName.lastIndexOf('-')+1,FinalGroupName.length());
												String MasterGroupName = FinalGroupName.replace(GroupLastChar, "M");
												
												String BT_ID = OfferValue.split("\\|")[0].split(";")[2];
												String BT_Type = OfferValue.split("\\|")[1].split(";")[0];
												String BT_Value = OfferValue.split("\\|")[0].split(";")[3];
												String SourceBT_Value = OfferValue.split("\\|")[0].split(";")[3];
												String BT_StartDate  = OfferValue.split("\\|")[1].split(";")[4];
												String BT_ExpiryDate  = OfferValue.split("\\|")[1].split(";")[5];
												String BucketID = OfferValue.split("\\|")[1].split(";")[9];
												if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName) != null)
												{
													String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getOfferID();
													String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getOfferType();
													String Offer_flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getOfferFlag();
													boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getOfferStartDate().length() > 0 ? true:false;
													boolean expiryFalg = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + MasterGroupName).getOfferExpiryDate().length() > 0 ? true:false;
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
														if(BT_Type.toUpperCase().equals("P") && Integer.parseInt(BT_Value) < 3)
															onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID=" + BucketID +":ACTION=Logging");
														else
															onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID=" + BucketID +":ACTION=Logging");
														break;
													}
													if(bCreateOffer)
													{
														for(int i = 0; i < Integer.parseInt(SourceBT_Value); i++)
														{
															BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,BT_StartDate, BT_ExpiryDate,"",Offer_flag, BEIDForProductID));
															if(!OfferValue.startsWith("S"))
																trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ BucketID  +":ACTION=Logging");
														}
													}
												}
												else
												{
													if(BT_Type.toUpperCase().equals("P") && Integer.parseInt(BT_Value) < 3)
														onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID=" + BucketID +":ACTION=Logging");
													else
														onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID=" + BucketID +":ACTION=Logging");
												}
												//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ SourceOffer.split(";")[0]  +":ACTION=Logging");
											}
										}
									}
								}
								if(ExtraOfferFlag)
								{	
									ExtraOfferFlag = false;
									for(String s: ValidGroupBalanceOffer)
									{
										if(s.split("\\|")[1].split(";").length > 8)
										{
											String AddedOffer= s.split("\\|")[1].split(";")[8];
											if(!AddedOffer.isEmpty())
											{
												String[] ListofAddedOffer;
												if(AddedOffer.contains(":"))
													ListofAddedOffer = AddedOffer.split(":")[0].split("\\|");
												else
													ListofAddedOffer = AddedOffer.split("\\|");
												
												for(int i = 0; i<ListofAddedOffer.length; i++)
												{
													if(ListofAddedOffer[i].length() > 1)
													{
														String[] OfferValues = ListofAddedOffer[i].split("-");
														//String offer_ID,String Offer_Type, boolean startFlag, boolean expiryFlag, 
														//String Balance_StartDate, String Balance_ExpiryDate, String Product_Private, String flag)
														String Timer = "";
														if(OfferValues[0].equals("Y"))
															Timer = "Timer";
														
														String Product_Private = OfferValues[1];
														String Offer_ID = OfferValues[2];
														String Start_Date = "";
														String End_Date = "";
														if(OfferValues[3].equals("MIGDATE"))
															Start_Date = sdfDaily.format(currDate);
														else
															Start_Date = s.split("\\|")[1].split(";")[4];
															
														if(OfferValues[4].startsWith("MIGDATE"))
														{
															int hours2Add = Integer.parseInt(OfferValues[4].replace("MIGDATE+", ""));
															Date NewDate = new Date(currDate.getTime() + hours2Add *3600*1000);
															End_Date = sdfDaily.format(NewDate);
														}
														else if(OfferValues[4].contains("+"))
														{
															SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
															Calendar c = Calendar.getInstance();
															try 
															{
																c.setTime(sdf.parse(s.split("\\|")[1].split(";")[5]));
																c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(OfferValues[4].split("\\+")[1])); 
																End_Date = sdf.format(c.getTime());
																
															} catch (ParseException e) {
																// TODO Auto-generated catch block
																e.printStackTrace();
															}
														}
														else
															End_Date = s.split("\\|")[1].split(";")[5];
														BalanceOfferList.add(PopulateOffer(Offer_ID,Timer,true,true ,Start_Date, End_Date,"","",new HashSet<>(Arrays.asList(s.split("\\|")[1].split(";")[9]))));
													}											 
												}
											}
										}
									}
									ExtraOfferFlag = false;
								}
								
							//need to put code till here
							}
							else
							{
								
								Set<String> BTFromGnGrace = new HashSet<>();
								BTFromGnGrace.addAll(LoadSubscriberMapping.SpecialGraceList);
								BTFromGnGrace.addAll(LoadSubscriberMapping.SpecialGGroupList);
								
								if(!BTFromGnGrace.contains(Balance_ID))
								{
									if(Balance_ID.contains("1633"))
										continue;
									CompletedBT_ID.add(balanceInput.getBEBUCKETID());
									onlyLog.add("INC4002:Balance_Type lookup failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
								}
							}						
						}
					}				
				}
			}
		}
		CompletedBTIDSet.addAll(CompletedBT_ID);
		return BalanceOfferList;
	}
	
	private List<String> OfferFromGraceBalanceMapping()
	{
		List<String> CISBalanceOfferList =new ArrayList<>();
				
		Set<String> CompletedBT_ID = new HashSet<>();
		Set<String> CompletedGroupBT_ID = new HashSet<>();
		Set<String> GraceBalanceForLogging = new HashSet<>();
		//System.out.println(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()));
		
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{			
			String Balance_ID = balanceInput.getBALANCETYPE();
			//System.out.println("Master Balance_ID: " + Balance_ID);
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			CompletedGroupBT_ID.clear();
			
			if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(Balance_ID))
				continue;
			
			if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
				continue;
			
			//Check if only V BT is valid for migration for group element
			/*if(LoadSubscriberMapping.OnlyVBalancesValueSet.contains(Balance_ID))
			{
				if(LoadSubscriberMapping.OnlyVBalancesValueMap.get(Balance_ID) != null)
				{
					Set<String> BalanceValueKey = LoadSubscriberMapping.OnlyVBalancesValueMap.get(Balance_ID);
					if(!BalanceValueKey.contains(balanceInput.getBEBUCKETVALUE()))
					{
						onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + balanceInput.getBEBUCKETVALUE() + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						continue;
					}
				}
			}*/
			
			if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) == "Y")
			{
				//onlyLog.add("INC4003:Balance_Type Ignored:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
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
					List<String> BalanceOfferList = new ArrayList<>();
					List<String> ValidBucketIDList = new ArrayList<>();
					for(String gracename : GroupNames)
					{
						if(gracename.equals("GRACE-M"))
							continue;
						Set<String> GraceBTs = LoadSubscriberMapping.GraceBalanceGroupingMap.get(gracename);
						BalanceOfferList.clear();
						ValidBucketIDList.clear();
						for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo tempbalanceInput : SortedBalanceInput)
						{	
							if(CompletedBT_ID.contains(tempbalanceInput.getBEBUCKETID()))
								continue;
							String TempBalance_ID = tempbalanceInput.getBALANCETYPE();
							String TempBalance_Value = tempbalanceInput.getBEBUCKETVALUE();
							String TempBalance_StartDate = tempbalanceInput.getBEBUCKETSTARTDATE();
							String TempBalance_ExpiryDate = tempbalanceInput.getBEEXPIRY();
							
							if(!TempBalance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(TempBalance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
							{													
								//onlyLog.add("INC4001:Balance_Type expired:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value +  ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + TempbalanceInput.getBEEXPIRY() + ":ACTION=Logging");
								CompletedBT_ID.add(tempbalanceInput.getBEBUCKETID());
								continue;
							}
							
							/*if(LoadSubscriberMapping.OnlyVBalancesValueSet.contains(TempBalance_ID))
							{
								if(LoadSubscriberMapping.OnlyVBalancesValueMap.get(TempBalance_ID) != null)
								{
									Set<String> BalanceValueKey = LoadSubscriberMapping.OnlyVBalancesValueMap.get(TempBalance_ID);
									if(!BalanceValueKey.contains(TempBalance_Value))
									{
										onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + tempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
										CompletedBT_ID.add(tempbalanceInput.getBEBUCKETID());
										continue;
									}
								}
							}*/
							
							if(GraceBTs.contains(TempBalance_ID))							
							{								
		 						if(LoadSubscriberMapping.SpecialGraceBalance.get(TempBalance_ID + '|' + gracename) != null)
								{
									String Offer_ID = LoadSubscriberMapping.SpecialGraceBalance.get(TempBalance_ID + '|' + gracename).getOfferID();
									String Symbol = LoadSubscriberMapping.SpecialGraceBalance.get(TempBalance_ID + '|' + gracename).getSymbols();
									String BT_Value = LoadSubscriberMapping.SpecialGraceBalance.get(TempBalance_ID + '|' + gracename).getBTValue();
									String Product_Private = LoadSubscriberMapping.SpecialGraceBalance.get(TempBalance_ID + '|' + gracename).getProductPrivate();
									String Offer_Type = LoadSubscriberMapping.SpecialGraceBalance.get(TempBalance_ID + '|' + gracename).getOfferType();
									boolean startFlag = LoadSubscriberMapping.SpecialGraceBalance.get(TempBalance_ID + '|' + gracename).getOfferStartDate().length() > 0 ? true:false;
									boolean expiryFalg = LoadSubscriberMapping.SpecialGraceBalance.get(TempBalance_ID + '|' + gracename).getOfferExpiryDate().length() > 0 ? true:false;
									String BT_TYPE = LoadSubscriberMapping.SpecialGraceBalance.get(TempBalance_ID + '|' + gracename).getBTTYPE();
									String CIS_Reference = 	LoadSubscriberMapping.SpecialGraceBalance.get(TempBalance_ID + '|' + gracename).getCISReference();
									if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
									{
										BalanceOfferList.add(BT_TYPE + ";" +tempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + "" + ";" + "" + ";" + tempbalanceInput.getBEBUCKETID());
										ValidBucketIDList.add(tempbalanceInput.getBEBUCKETID());
										GraceBalanceForLogging.add(BT_TYPE + ";" + CIS_Reference + ";" + TempBalance_ID + ";"+ TempBalance_Value + ";" + Offer_ID + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate + ";" + tempbalanceInput.getBEBUCKETID());
									}
									else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
									{
										BalanceOfferList.add(BT_TYPE + ";" +tempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + "" + ";" + "" + ";" + tempbalanceInput.getBEBUCKETID());
										ValidBucketIDList.add(tempbalanceInput.getBEBUCKETID());
										GraceBalanceForLogging.add(BT_TYPE + ";" + CIS_Reference + ";" + TempBalance_ID + ";"+ TempBalance_Value + ";" + Offer_ID + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate + ";" + tempbalanceInput.getBEBUCKETID());
									}
									else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
									{
										BalanceOfferList.add(BT_TYPE + ";" +tempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + "" + ";" + "" + ";" + tempbalanceInput.getBEBUCKETID());
										ValidBucketIDList.add(tempbalanceInput.getBEBUCKETID());
										GraceBalanceForLogging.add(BT_TYPE + ";" + CIS_Reference + ";" + TempBalance_ID + ";"+ TempBalance_Value + ";" + Offer_ID + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate + ";" + tempbalanceInput.getBEBUCKETID());
									}
									else if(Symbol.equals("or"))
									{
										//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
										String[] values = BT_Value.split("#");
										
										if(Arrays.stream(values).anyMatch(TempBalance_Value::equals))
										{
											BalanceOfferList.add(BT_TYPE + ";" +tempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + "" + ";" + "" + ";" + tempbalanceInput.getBEBUCKETID());
											ValidBucketIDList.add(tempbalanceInput.getBEBUCKETID());
											GraceBalanceForLogging.add(BT_TYPE + ";" + CIS_Reference + ";" + TempBalance_ID + ";"+ TempBalance_Value + ";" + Offer_ID + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate + ";" + tempbalanceInput.getBEBUCKETID());
										}
										else
										{
											if(LoadSubscriberMapping.GraceFullBalanceGroupingMap.containsKey(TempBalance_ID))
											{
												Set<String> BTValues = new HashSet<String>();
												BTValues.addAll(LoadSubscriberMapping.GraceFullBalanceGroupingMap.get(TempBalance_ID));
												if(!BTValues.contains(TempBalance_Value))
												{	//CompletedBT_ID.add(tempbalanceInput.getBEBUCKETID());
													if(BT_TYPE.equals("P") && Integer.parseInt(TempBalance_Value) < 3)
														onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + tempbalanceInput.getBEBUCKETID() +":ACTION=Logging");														
													else
														onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + tempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
													
													CompletedBT_ID.add(tempbalanceInput.getBEBUCKETID());
													break;
												}
											}
											//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
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
												if(BT_TYPE.equals("P") && Integer.parseInt(TempBalance_Value) < 3)
													onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + tempbalanceInput.getBEBUCKETID() +":ACTION=Logging");													
												else
													onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + tempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
												CompletedBT_ID.add(tempbalanceInput.getBEBUCKETID());
												//break;
											}
										}
									}
								}
							}
						}
						
						if(ValidBucketIDList.size() == GraceBTs.size())
						{
							for(String CISOffer : BalanceOfferList)
							{
								String OfferId = CISOffer.split("\\|",-1)[1].split(";",-1)[0];
								String TargetOffer = CISOffer.split("\\|",-1)[1];
								String SourceOffer = CISOffer.split("\\|",-1)[0];
								if(!OfferId.isEmpty())
								{
									CISBalanceOfferList.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
												Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7],new HashSet<>(Arrays.asList(TargetOffer.split(";")[9]))));
									if(!LoadSubscriberMapping.IgnoreGraceBTForINC7004Log.contains(SourceOffer.split(";")[2]))
										trackLog.add("INC7004:GRACE PC considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceOffer.split(";")[2] + ":BE_BUCKET_ID=" + TargetOffer.split(";")[9] + ":OFFER_ID=" + OfferId +":ACTION=Logging");
								}
							}								
							CompletedBT_ID.addAll(ValidBucketIDList);
							break;
						}					
					}
				}
				else
				{
					if(!LoadSubscriberMapping.SpecialGGroupList.contains(Balance_ID))
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				}
			}			
		}
		
		//now all formation is done, so now look for PC and log it if its missed in the group
		
		
		if(GraceBalanceForLogging.size() > 0)
		{
			for(String s : GraceBalanceForLogging)
			{
				//BT_TYPE + ";" + CIS_Reference + ";" + TempBalance_ID + ";"+ TempBalance_Value + ";" + Offer_ID + ";" + TempBalance_StartDate 
				//+ ";" + TempBalance_ExpiryDate + ";" + tempbalanceInput.getBEBUCKETID());
				String BT_Type = s.split(";")[0];
				String BT_ID = s.split(";")[2];
				String BT_Value = s.split(";")[3];
				String BT_Bucket = s.split(";")[7];
				if(CompletedBT_ID.contains(BT_Bucket))
					continue;
				if(LoadSubscriberMapping.GraceFullBalanceGroupingMap.containsKey(BT_ID))
				{
					if(BT_Type.equals("P"))
					{
						if( Integer.parseInt(BT_Value) < 3)
							onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID=" + BT_Bucket +":ACTION=Logging");														
						else
							onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID=" + BT_Bucket +":ACTION=Logging");
					}
					Set<String> BTValues = new HashSet<String>();
					BTValues.addAll(LoadSubscriberMapping.GraceFullBalanceGroupingMap.get(BT_ID));
					if(!BTValues.contains(BT_Value))
					{	//CompletedBT_ID.add(tempbalanceInput.getBEBUCKETID());
						if(BT_Type.equals("P") && Integer.parseInt(BT_Value) < 3)
							onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID=" + BT_Bucket +":ACTION=Logging");														
						else
							onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID=" + BT_Bucket +":ACTION=Logging");
						
						CompletedBT_ID.add(BT_Bucket);
						break;
					}
				}
			}
		}
		CompletedBTIDSet.addAll(CompletedBT_ID);
		return CISBalanceOfferList;
	}
	
	private List<String> OfferFromGraceMBalanceMapping()
	{
		List<String> CISBalanceOfferList =new ArrayList<>();
				
		Set<String> CompletedBT_ID = new HashSet<>();
		Set<String> CompletedGroupBT_ID = new HashSet<>();
		//System.out.println(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()));
		
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{			
			String Balance_ID = balanceInput.getBALANCETYPE();
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			String Balance_StartDate = balanceInput.getBEBUCKETSTARTDATE();
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			CompletedGroupBT_ID.clear();
			if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
				continue;
			
			if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(Balance_ID))
				continue;
			
			if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) == "Y")
			{
				//onlyLog.add("INC4003:Balance_Type Ignored:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
				CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				continue;
			}
			//Check for expiry Date, log it and proceed further
			if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
			{
				//onlyLog.add("INC4001:Balance_Type expired:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
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
								String Product_Private = LoadSubscriberMapping.SpecialGraceBalance.get(Balance_ID + '|' + gracename).getProductPrivate();
								String Offer_Type = LoadSubscriberMapping.SpecialGraceBalance.get(Balance_ID + '|' + gracename).getOfferType();
								String Offer_Flag = LoadSubscriberMapping.SpecialGraceBalance.get(Balance_ID + '|' + gracename).getOfferFlag();
								boolean startFlag = LoadSubscriberMapping.SpecialGraceBalance.get(Balance_ID + '|' + gracename).getOfferStartDate().length() > 0 ? true:false;
								boolean expiryFalg = LoadSubscriberMapping.SpecialGraceBalance.get(Balance_ID + '|' + gracename).getOfferExpiryDate().length() > 0 ? true:false;
								String BT_TYPE = LoadSubscriberMapping.SpecialGraceBalance.get(Balance_ID + '|' + gracename).getBTTYPE();
										
								if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
								{
									if(!Offer_ID.isEmpty())
									{
										CISBalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,Product_Private,Offer_Flag,new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
										if(!LoadSubscriberMapping.IgnoreGraceBTForINC7004Log.contains(Balance_ID))
											trackLog.add("INC7004:GRACE PC considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() + ":OFFER_ID=" + Offer_ID +":ACTION=Logging");
										CompletedBT_ID.add(balanceInput.getBEBUCKETID());
										
									}
								}
								else if(Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_Value))
								{
									if(!Offer_ID.isEmpty())
									{
										CISBalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,Product_Private,Offer_Flag,new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
										if(!LoadSubscriberMapping.IgnoreGraceBTForINC7004Log.contains(Balance_ID))
											trackLog.add("INC7004:GRACE PC considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() + ":OFFER_ID=" + Offer_ID +":ACTION=Logging");
										CompletedBT_ID.add(balanceInput.getBEBUCKETID());
									}
								}
								else if(Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value))
								{
									if(!Offer_ID.isEmpty())
									{
										CISBalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,Product_Private,Offer_Flag,new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
										if(!LoadSubscriberMapping.IgnoreGraceBTForINC7004Log.contains(Balance_ID))
											trackLog.add("INC7004:GRACE PC considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() + ":OFFER_ID=" + Offer_ID +":ACTION=Logging");
										CompletedBT_ID.add(balanceInput.getBEBUCKETID());
									}
								}
								else if(Symbol.equals("or"))
								{
									//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
									String[] values = BT_Value.split("#");
									
									if(Arrays.stream(values).anyMatch(Balance_Value::equals))
									{
										if(!Offer_ID.isEmpty())
										{
											CISBalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,Product_Private,Offer_Flag,new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
											if(!LoadSubscriberMapping.IgnoreGraceBTForINC7004Log.contains(Balance_ID))
												trackLog.add("INC7004:GRACE PC considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() + ":OFFER_ID=" + Offer_ID +":ACTION=Logging");
											CompletedBT_ID.add(balanceInput.getBEBUCKETID());
										}
									}
									else
									{
										if(LoadSubscriberMapping.GraceFullBalanceGroupingMap.containsKey(Balance_ID))
										{
											Set<String> BTValues = new HashSet<String>();
											BTValues.addAll(LoadSubscriberMapping.GraceFullBalanceGroupingMap.get(Balance_ID));
											if(!BTValues.contains(Balance_Value))
											{	//CompletedBT_ID.add(tempbalanceInput.getBEBUCKETID());
												if(BT_TYPE.equals("P") && Integer.parseInt(Balance_Value) < 3)
													onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");														
												else
													onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
												
												CompletedBT_ID.add(balanceInput.getBEBUCKETID());
												break;
											}
										}
										//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
									}
								}
								else
								{
									if(LoadSubscriberMapping.GraceFullBalanceGroupingMap.containsKey(Balance_ID))
									{
										Set<String> BTValues = new HashSet<String>();
										BTValues.addAll(LoadSubscriberMapping.GraceFullBalanceGroupingMap.get(Balance_ID));
										if(!BTValues.contains(Balance_Value))
										{
											if(BT_TYPE.equals("P") && Integer.parseInt(Balance_Value) < 3)
												onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");													
											else
												onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
											CompletedBT_ID.add(balanceInput.getBEBUCKETID());
											//break;
										}
									}
								}
							}
						}
					}
				}
				else
				{
					if(!LoadSubscriberMapping.SpecialGGroupList.contains(Balance_ID))
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
				}
			}
		}
		
		//now all formation is done, so now look for PC and log it if its missed in the group 
		
		CompletedBTIDSet.addAll(CompletedBT_ID);
		return CISBalanceOfferList;
	}
	
	private List<String> offerFromLifeCycle()
	{
		List<String> LifeCycleOfferList = new ArrayList<>();
		
		String serviceClass = subscriber.getSubscriberInfoSERVICESTATE();
		String INITIAL_ACTIVATION_DATE_FLAG;
				
		if(INITIAL_ACTIVATION_DATE.length() == 0)
			INITIAL_ACTIVATION_DATE_FLAG = "Y";
		else
			INITIAL_ACTIVATION_DATE_FLAG = "N";
		
		String Offer_ID = "";
		String Offer_Type = "";
				
		if( serviceClass.equals("S") && INITIAL_ACTIVATION_DATE_FLAG == "N")
		{
			Offer_ID = LoadSubscriberMapping.LifeCycleMap.get(serviceClass+"|"+INITIAL_ACTIVATION_DATE_FLAG +"|N").split(",")[11];
			Offer_Type = LoadSubscriberMapping.LifeCycleMap.get(serviceClass+"|"+INITIAL_ACTIVATION_DATE_FLAG +"|N").split(",")[12];
		}
		
		if( serviceClass.equals("F") && INITIAL_ACTIVATION_DATE_FLAG == "N")
		{
			Offer_ID = LoadSubscriberMapping.LifeCycleMap.get(serviceClass+"|"+INITIAL_ACTIVATION_DATE_FLAG +"|N").split(",")[11];
			Offer_Type = LoadSubscriberMapping.LifeCycleMap.get(serviceClass+"|"+INITIAL_ACTIVATION_DATE_FLAG +"|N").split(",")[12];
		}
		
		if( serviceClass.equals("D") && INITIAL_ACTIVATION_DATE_FLAG == "N")
		{
			Offer_ID = LoadSubscriberMapping.LifeCycleMap.get(serviceClass+"|"+INITIAL_ACTIVATION_DATE_FLAG +"|N").split(",")[11];
			Offer_Type = LoadSubscriberMapping.LifeCycleMap.get(serviceClass+"|"+INITIAL_ACTIVATION_DATE_FLAG +"|N").split(",")[12];
		}
		
		if(Offer_ID.length() > 0)
		{
			StringBuffer sb = new StringBuffer();
			sb.append(msisdn).append(",");
			sb.append(Offer_ID).append(",");
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
			sb.append("0");
			
			LifeCycleOfferList.add(sb.toString());
			sb =null;
		}
		
		return LifeCycleOfferList;
	}
	
	private List<String> offerFromProfileTag()
	{
		List<String> ProfileTagOfferList = new ArrayList<>();
		
		if(subscriber.getProfiledumpInfoList().size() == 0)
			return ProfileTagOfferList;
		
		for(String itr : LoadSubscriberMapping.Profile_Tags_Mapping.keySet())
		{
			PROFILETAGINFO profileMappingValue = LoadSubscriberMapping.Profile_Tags_Mapping.get(itr);
			String Symbol = profileMappingValue.getSymbols();
			String TargetValue = profileMappingValue.getProfileTagValue();
			String IgnoreFlag =  profileMappingValue.getIgnoreFlag();
			if(IgnoreFlag.equals("N"))
			{
				/*if(itr.equals("IDD2Act") || itr.equals("PAYGMet") || itr.equals("Absher") || itr.equals("Bespoke") || itr.equals("DataGraceAct") 
					|| itr.equals("EmiratiPlan") || itr.equals("entBsnssCrclActv") || itr.equals("IDDCutRateAct") || itr.equals("Prepaid")
					|| itr.equals("TopXCountr1") || itr.equals("TopXCountr2") || itr.equals("TopXCountr3") || itr.equals("TopXCountr4") 
					|| itr.equals("TopXCountr5") || itr.equals("TP_Social_Deact_Conf") || itr.equals("Plan") || itr.equals("MBBGraceAct"))
					*/
				if(LoadSubscriberMapping.ProfileTagName.contains(itr))
				{
					String Profile_Value = profileTag.GetProfileTagValue(itr);
					
					if(Profile_Value.isEmpty() || profileMappingValue.getOfferId().isEmpty())
						continue;
					
					if(profileMappingValue.getSubState().isEmpty())
					{						
						if(Symbol.equals("="))
						{
							if(TargetValue.equals(Profile_Value))
							{
								ProfileTagOfferList.add(populateProfileOffer(profileMappingValue.getOfferId(),"","",profileMappingValue.getProductPrivate(),itr,Profile_Value));
								if(profileMappingValue.getOfferId().equals("91"))
									trackLog.add("INC7005:GRACE PT considered:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":OFFER_ID="+ profileMappingValue.getOfferId() + ":ACTION=Logging");
								//MSISDN,PROFILE_TAG_NAME,OFFER_ID
							}
							else if(Profile_Value.length() >0)
								onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE="+ Profile_Value + ":ACTION=Logging");
						}
					}
					else if(profileMappingValue.getSubState().equals(subscriber.getSubscriberInfoSERVICESTATE()))
					{
						if(Symbol.equals("="))
						{
							if(TargetValue.equals(profileTag.GetProfileTagValue(itr)))
							{
								ProfileTagOfferList.add(populateProfileOffer(profileMappingValue.getOfferId(),"","",profileMappingValue.getProductPrivate(),itr,Profile_Value));
								if(profileMappingValue.getOfferId().equals("91"))
									onlyLog.add("INC7005:GRACE PT considered:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":OFFER_ID="+ profileMappingValue.getOfferId() + ":ACTION=Logging");
							}
							else if(Profile_Value.length() >0)
								onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE="+ Profile_Value + ":ACTION=Logging");
						}
					}
					else 
						onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE="+ Profile_Value + ":ACTION=Logging");
				}
				
				if(itr.equals("guiBalAdjCount") || itr.equals("PrjBlueCounter") || itr.equals("bdgtCntrlTopUp") || itr.equals("NewTopUpValue"))
				{	
					String Profile_Value = profileTag.GetProfileTagValue(itr);
					if(Symbol.equals(">=") && Profile_Value.length() > 0)
					{
						if(Double.parseDouble(Profile_Value) >= 0)
						{
							if(!profileMappingValue.getOfferId().isEmpty())
								ProfileTagOfferList.add(populateProfileOffer(profileMappingValue.getOfferId(),"","",profileMappingValue.getProductPrivate(),itr,Profile_Value));
						}
						else if(Profile_Value.length() >0)
							onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE="+ Profile_Value + ":ACTION=Logging");
							
					}
				}
				if(itr.equals("MBBOfferExpDate"))
				{
					String Profile_Value = profileTag.GetProfileTagValue(itr);
					String DateValue = "";
					if(Profile_Value.length() == 14)
					{
						DateValue = Profile_Value.substring(0,4) + "-" + Profile_Value.substring(4,6) + "-" + Profile_Value.substring(6,8) + " " + Profile_Value.substring(8,10) + ":" + Profile_Value.substring(10,12) + ":" + Profile_Value.substring(12,14);
					
						if(CommonUtilities.convertDateToEpoch(DateValue) <= CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
						{
							onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ACTION=Logging");
							break;
						}
						else
						{	
							ProfileTagOfferList.add(populateProfileOffer(profileMappingValue.getOfferId(),"",DateValue,profileMappingValue.getProductPrivate(),itr,Profile_Value));
						}
					}
				}
				
				if(itr.equals("CVMCounter"))
				{
					String Profile_Value = profileTag.GetProfileTagValue(itr);
					if(Symbol.equals(">") &&  Profile_Value.length() > 0)
					{	 
						if(Long.parseLong(Profile_Value) > Long.parseLong(TargetValue))
						{
							List<String> PT_Values = new ArrayList<>();
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
											onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
											break;
										}
										else
										{
											ProfileTagOfferList.add(populateProfileOffer(profileMappingValue.getOfferId(),"",DateValue,profileMappingValue.getProductPrivate(),itr,Profile_Value));
										}
									}
								}
							}
						}
						else
						{
							onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE=" + Profile_Value +":ACTION=Logging");
						}
					}
				}
				
				if(itr.equals("bdgtCntrlTopUp") || itr.equals("NewTopUpValue"))
				{
					if(!Offer1201Created)
					{
						String Profile_Value = profileTag.GetProfileTagValue(itr);
						if(Symbol.equals("!="))
						{
							if(Profile_Value.length() > 0)
							{
								if(!profileMappingValue.getOfferId().isEmpty())
									ProfileTagOfferList.add(populateProfileOffer(profileMappingValue.getOfferId(),"","",profileMappingValue.getProductPrivate(),itr,Profile_Value));
							}
							else if(Profile_Value.length() > 0)
								onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr + ":PROFILE_TAG_VALUE="+ Profile_Value + ":ACTION=Logging");
								
						}
					}						
				}
			}
			else
			{
				if(profileTag.GetProfileTagValue(itr).length() > 0)
					onlyLog.add("INC6002:Profile_Tags Mapping Ignored:MSISDN=" + msisdn + ":Profile_Tag_Name=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE="+ profileTag.GetProfileTagValue(itr) + ":ACTION=Logging");
				continue;
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
				if(Profile_Value.isEmpty() || profileMappingValue.getOfferId().isEmpty() )
					continue;
				if(TargetName.equals("CVM"))					
				{
					String[] values = {"1","2","3"};
					if(Symbol.equals("="))
					{	
						if(Integer.parseInt(TargetValue) == Integer.parseInt(Profile_Value))
							ProfileTagOfferList.add(populateProfileOffer(profileMappingValue.getOfferId(),"","",profileMappingValue.getProductPrivate(),itr,Profile_Value));
						else if(Profile_Value.length() >0 && !Arrays.stream(values).anyMatch(Profile_Value::equals))
							onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE="+ Profile_Value + ":ACTION=Logging");
							
					}
					continue;
				}
					
				if(TargetName.equals("BlckBrryBundle"))					
				{
					if(profileMappingValue.getSubState().equals(subscriber.getSubscriberInfoSERVICESTATE()))
					{
						String[] values = {"1","2"};
						if(Symbol.equals("="))
						{	
							if(Integer.parseInt(TargetValue) == Integer.parseInt(Profile_Value))
								ProfileTagOfferList.add(populateProfileOffer(profileMappingValue.getOfferId(),"","",profileMappingValue.getProductPrivate(),itr,Profile_Value));
							else if(Profile_Value.length() >0 && !Arrays.stream(values).anyMatch(Profile_Value::equals))
								onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE="+ Profile_Value + ":ACTION=Logging");								
						}						
					}
					else
					{
						onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE="+ Profile_Value + ":ACTION=Logging");
					}
					continue;
				}
				
				if(TargetName.equals("MBBGraceAct") || TargetName.equals("DataGraceAct"))					
				{
					{
						if(Symbol.equals("="))
						{	
							if(TargetValue.equals(Profile_Value))
							{
								if(TargetValue.equals(Profile_Value))
								{
									ProfileTagOfferList.add(populateProfileOffer(profileMappingValue.getOfferId(),"","",profileMappingValue.getProductPrivate(),itr,Profile_Value));
									trackLog.add("INC7005:GRACE PT considered:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":OFFER_ID="+ profileMappingValue.getOfferId() + ":ACTION=Logging");
								}
								else
									onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE="+ Profile_Value + ":ACTION=Logging");
								continue;
							}
							else
								onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE="+ Profile_Value + ":ACTION=Logging");
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
								ProfileTagOfferList.add(populateProfileOffer(profileMappingValue.getOfferId(),"","",profileMappingValue.getProductPrivate(),itr,Profile_Value));
								ProfileTagOfferList.add(populateProfileOffer(profileMappingValue.getAttr1OfferId(),"","",profileMappingValue.getProductPrivate(),itr,Profile_Value));
							}
							else if(Profile_Value.length() >0 && !Arrays.stream(values).anyMatch(Profile_Value::equals))
								onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE="+ Profile_Value + ":ACTION=Logging");
						}
					}
					else 
					{
						onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE="+ Profile_Value + ":ACTION=Logging");
					}
					continue;
				}
				
				if(TargetName.equals("ManRenDateLess1Y") && !Profile_Value.isEmpty())
				{
					String ManRenDate = "";
					if(Profile_Value.length() == 14)
					{
						ManRenDate = Profile_Value.substring(0,4) + "-" + Profile_Value.substring(4,6) + "-" + Profile_Value.substring(6,8) + " " + Profile_Value.substring(8,10) + ":" + Profile_Value.substring(10,12) + ":" + Profile_Value.substring(12,14);
					
						if(CommonUtilities.convertDateToEpoch(ManRenDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
						{
							onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE=" + Profile_Value +":ACTION=Logging");
						}
						else
						{
							if(profileMappingValue.getRatePlanOperator().equals("="))
							{
								if(subscriber.getSubscriberInfoCCSACCTTYPEID().equals(profileMappingValue.getRatePlanID()))
								{
									ProfileTagOfferList.add(populateProfileOffer(profileMappingValue.getOfferId(),"",ManRenDate,profileMappingValue.getProductPrivate(),itr,Profile_Value));
								}
								//else
								//	onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE="+ Profile_Value + ":ACTION=Logging");
							}
							if(profileMappingValue.getRatePlanOperator().equals("or"))
							{
								List<String> RatePlanID = Arrays.asList(profileMappingValue.getRatePlanID().split("#"));
								
								if(RatePlanID.contains(subscriber.getSubscriberInfoCCSACCTTYPEID()))
								{
									ProfileTagOfferList.add(populateProfileOffer(profileMappingValue.getOfferId(),"",ManRenDate,profileMappingValue.getProductPrivate(),itr,Profile_Value));
								}
								//else
									//onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE="+ Profile_Value + ":ACTION=Logging");
							}					
						}
					}
					else
						onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE="+ Profile_Value + ":ACTION=Logging");
					continue;
				}
				
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
											onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
											break;
										}
										else
										{
											ValidPTCheck.add(profileTagName);
										}
									}
									else
									{
										onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
										break;
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
										onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
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
										onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
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
											onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
											break;
										}
									}
									else
									{
										onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
										break;
									}
								}	
							}
							if(PT_Values.size() == ValidPTCheck.size())
							{
								String StartDate = profileTag.GetProfileTagValue("BstrVNNConfirmDate");
								String CurrectStartDate = "";
								if(!StartDate.isEmpty() && StartDate.length() == 14)
									CurrectStartDate = StartDate.substring(0,4) + "-" + StartDate.substring(4,6) + "-" + StartDate.substring(6,8) + " " + StartDate.substring(8,10) + ":" + StartDate.substring(10,12) + ":" + StartDate.substring(12,14);
								
								
								String EndDate = profileTag.GetProfileTagValue("BstrVceNatNumExp");
								String CurrectEndDate = "";
								if(!EndDate.isEmpty())
									CurrectEndDate = EndDate.substring(0,4) + "-" + EndDate.substring(4,6) + "-" + EndDate.substring(6,8) + " " + EndDate.substring(8,10) + ":" + EndDate.substring(10,12) + ":" + EndDate.substring(12,14);
								
								ProfileTagOfferList.add(populateProfileOffer(profileMappingValue.getOfferId(),CurrectStartDate,CurrectEndDate,profileMappingValue.getProductPrivate(),itr,Profile_Value));
							}
						}
						else
						{
							String[] values = {"T","F"};
							if(Profile_Value.length() >0 && !Arrays.stream(values).anyMatch(Profile_Value::equals))
								onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE=" + Profile_Value +":ACTION=Logging");
						}
					}
					continue;
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
											onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
											break;
										}
										else
										{
											ValidPTCheck.add(profileTagName);
										}
									}
									else
									{
										onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
										break;
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
										onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
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
											onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
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
											onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
											break;
										}
									}
									else
									{
										onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE=" + Profile_Value + ":ADD_PROFILE_TAG_NAME=" + profileTagName + ":ADD_PROFILE_TAG_VALUE=" + additionalProfileValue  +":ACTION=Logging");
										break;
									}
								}
							}
							if(PT_Values.size() == ValidPTCheck.size())
							{
								String StartDate = profileTag.GetProfileTagValue("BstrVNNConfirmDate");
								String CurrectStartDate = "";
								if(!StartDate.isEmpty() && StartDate.length() == 14)
									CurrectStartDate = StartDate.substring(0,4) + "-" + StartDate.substring(4,6) + "-" + StartDate.substring(6,8) + " " + StartDate.substring(8,10) + ":" + StartDate.substring(10,12) + ":" + StartDate.substring(12,14);
								
								
								String EndDate = profileTag.GetProfileTagValue("BstrVceNatNumExp");
								String CurrectEndDate = "";
								if(!EndDate.isEmpty())
									CurrectEndDate = EndDate.substring(0,4) + "-" + EndDate.substring(4,6) + "-" + EndDate.substring(6,8) + " " + EndDate.substring(8,10) + ":" + EndDate.substring(10,12) + ":" + EndDate.substring(12,14);
								
								ProfileTagOfferList.add(populateProfileOffer(profileMappingValue.getOfferId(),CurrectStartDate,CurrectEndDate,profileMappingValue.getProductPrivate(),itr,Profile_Value));
							}
						}
						else
						{
							String[] values = {"T","F"};
							if(Profile_Value.length() >0 && !Arrays.stream(values).anyMatch(Profile_Value::equals))
								onlyLog.add("INC6003:Profile Tag match condition Failed:MSISDN=" + msisdn + ":PROFILE_TAG_NAME=" + itr.split(",")[0] + ":PROFILE_TAG_VALUE=" + Profile_Value +":ACTION=Logging");
						}
					}
					continue;
				}
				if(Offer4805Created)
				{
					if(TargetName.equals("OWFnFLabel") || TargetName.equals("OneWayFnF") || TargetName.equals("OneWayFnFModCount"))					
					{
						if(!Profile_Value.isEmpty())
						{
							ProfileTagOfferList.add(populateProfileOffer(profileMappingValue.getOfferId(),"","",profileMappingValue.getProductPrivate(),itr,Profile_Value));
							Offer4805Created = false;
						}
					}
				}				
			}
			else
			{
				//log for ignore
			}
		}
		return ProfileTagOfferList;
	}
	
	private List<String> offerFromDefaultService()
	{
		List<String> defaultOfferList = new ArrayList<>();
		
		LoadSubscriberMapping.DefaultServicesMap.forEach((k,v)->{
			//System.out.println("Item : " + k + " Count : " + v);
			
			if (v.split(",",-1)[1].trim().equals("N"))
			{
				//System.out.println(v);
				if (v.split(",",-1)[6] != "" && v.split(",",-1)[6].length() != 0)
				{
					String Offer_ID = v.split(",",-1)[6];
					String Offer_Type = v.split(",",-1)[7];
					String Rule_ID = v.split(",",-1)[8];
					
					if(Rule_ID.equals("1"))
					{
						StringBuffer sb = new StringBuffer();
						sb.append(msisdn).append(",");
						sb.append(Offer_ID).append(",");
						sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
						sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
						sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
						
						sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
						sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
						sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
						sb.append("0");
						defaultOfferList.add(sb.toString());
						
						sb = null;
					}
					
					if(Rule_ID.equals("2") && subscriber.getUsmsdumpInfoList().get(0).getLASTRECHARGEDATE().length() > 0)
					{
						String LAST_Recharge_Date = subscriber.getUsmsdumpInfoList().get(0).getLASTRECHARGEDATE(); 
						
						StringBuffer sb = new StringBuffer();
						sb.append(msisdn).append(",");
						sb.append(Offer_ID).append(",");
						sb.append(CommonUtilities.convertDateToTimerOfferDate(LAST_Recharge_Date)[0].toString()).append(",");
						sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
						sb.append(CommonUtilities.convertDateToTimerOfferDate(LAST_Recharge_Date)[1].toString()).append(",");
						sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
						sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
						sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
						sb.append("0");
						defaultOfferList.add(sb.toString());
						
						sb = null;
					}
					
				}
			}			
		});
		
		return defaultOfferList;
	}
		
	private List<String> offerFromNPPLifeCycle() {
		List<String> NPPLifeCycleOfferList = new ArrayList<>();
		
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
					String OfferValue = LoadSubscriberMapping.NPPLifeCycleMap.get(SerialNumber).getOFFER();
					if(!OfferValue.isEmpty())
						//N-Y-4623-BT_Start_Date-BT_Expiry_date
						NPPLifeCycleOfferList.add(PopulateOffer(OfferValue.split("-")[2], "Timer", false, false, BTExpiryDate, BTStartDate, "", "", new HashSet<>(Arrays.asList(""))));
				}
			}			
		}		
		return NPPLifeCycleOfferList;
	}
	
	private void GenerationFromGGroupBalanceMapping()
	{
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
					if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.containsKey(Balance_ID + "|G"))
					{	
						if(LoadSubscriberMapping.OnlyGBalancesValueMap.containsKey(Balance_ID))
						{
							Set<String> BalanceValueKey = LoadSubscriberMapping.OnlyGBalancesValueMap.get(Balance_ID);
							if(BalanceValueKey.contains(Balance_Value))
							{
								onlyLog.add("INC4012:NetworkBundle specific BT ignored:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
								CompletedBT_ID.add(balanceInput.getBEBUCKETID());
								continue;
							}
							/*else
							{
								if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|G").getBTTYPE().equals("P"))
								{
									if(Integer.parseInt(Balance_Value) < 3)
										onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
									else
										onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID() +":ACTION=Logging");
								}
								//else
									//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID() +":ACTION=Logging");
							}
							CompletedBT_ID.add(balanceInput.getBEBUCKETID());*/
							
						}
					}
				}
			}
		}
		
		CompletedBTIDSet.addAll(CompletedBT_ID);
	}
	
	/******Code to formulate and prepareGroups from product mapping sheet********************/	
	
	public List<String> offerFromAMBalanceMapping()
	{
		List<String> FinalBalanceOffer = new ArrayList<>();
		
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
					onlyLog.add("INC4003:Balance_Type Ignored:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
					AMBTIDSet.add(balanceInput.getBEBUCKETID());
					break;
				}
				else
				{
					if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
					{
						//INC4001	Balance_Type expired	MSISDN,BALANCE_TYPE,BE_BUCKET_VALUE,BE_BUCKET_ID,BE_EXPIRY
						onlyLog.add("INC4001:Balance_Type expired:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
						AMBTIDSet.add(balanceInput.getBEBUCKETID());
						break;
					}
					else
					{
						boolean gotSameExpiry = false;
						Map<String,String> AM1633GroupBTs = new HashMap<>();
						Map<String,String> AM1633GroupMasterBTs = new HashMap<>();
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
									//INC4001	Balance_Type expired	MSISDN,BALANCE_TYPE,BE_BUCKET_VALUE,BE_BUCKET_ID,BE_EXPIRY
									onlyLog.add("INC4001:Balance_Type expired:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
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
											ValidAMGroupBalanceOffer.forEach(item1->{GroupBTID.add(item1.split("\\|")[1].split(";")[9]);});
											for(String Str : ValidAMGroupBalanceOffer)
											{
												String TargetOffer = Str.split("\\|")[1];											
												if(TargetOffer.split(";")[0].length() != 0)
												{
													trackLog.add("INC7010:BT mapped in A-M-* group:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Str.split("\\|")[0].split(";")[2] + ":BE_BUCKET_ID="+ TargetOffer.split(";")[9] + ":OFFER_ID=" + TargetOffer.split(";")[0] + ":ACTION=Logging");
													FinalBalanceOffer.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
															Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7],GroupBTID));
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
								String BT_StartDate = item.getValue().split("\\|")[2].trim();
								String BT_ExpiryDate = item.getValue().split("\\|")[3].trim();
								String BE_BucketId = item.getKey();
								if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|M") != null)
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
									if(Symbol.equals(">=") && Integer.parseInt(BT_Value) >= Integer.parseInt(BT_VALUE))
									{
										AMBTIDSet.add(BE_BucketId);
										FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,BT_StartDate, BT_ExpiryDate,"",Offer_flag,new HashSet<>(Arrays.asList(BE_BucketId))));
										trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ BE_BucketId  +":ACTION=Logging");
										continue;
									}
									if(Symbol.equals("=") && Integer.parseInt(BT_Value) == Integer.parseInt(BT_VALUE))
									{
										AMBTIDSet.add(BE_BucketId);
										FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,BT_StartDate, BT_ExpiryDate,"",Offer_flag,new HashSet<>(Arrays.asList(BE_BucketId))));
										trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ BE_BucketId  +":ACTION=Logging");
										continue;
									}
									else
									{
										AMBTIDSet.add(BE_BucketId);
										onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID="+ BE_BucketId +":ACTION=Logging");
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
					onlyLog.add("INC4003:Balance_Type Ignored:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
					AMBTIDSet.add(balanceInput.getBEBUCKETID());
					break;
				}
				else
				{
					if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
					{
						onlyLog.add("INC4001:Balance_Type expired:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
						AMBTIDSet.add(balanceInput.getBEBUCKETID());
						break;
					}
					else
					{
						if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M") != null)
						{
							String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getOfferID();
							String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getOfferType();
							String Offer_flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getOfferFlag();
							boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getOfferStartDate().length() > 0 ? true:false;
							boolean expiryFalg = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getOfferExpiryDate().length() > 0 ? true:false;
							String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getSymbols();
							String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(Balance_ID + "|M").getBTValue();
							
							if(Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_VALUE))
							{
								AMBTIDSet.add(balanceInput.getBEBUCKETID());
								FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"",Offer_flag,new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID()  +":ACTION=Logging");
								continue;
							}
							if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_VALUE))
							{
								AMBTIDSet.add(balanceInput.getBEBUCKETID());
								FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"",Offer_flag,new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID()  +":ACTION=Logging");
								continue;
							}
							if(Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_VALUE))
							{
								AMBTIDSet.add(balanceInput.getBEBUCKETID());
								FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Balance_StartDate, Balance_ExpiryDate,"",Offer_flag,new HashSet<>(Arrays.asList(balanceInput.getBEBUCKETID()))));
								trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID()  +":ACTION=Logging");
								continue;
							}
							
						}
						else
						{
							AMBTIDSet.add(balanceInput.getBEBUCKETID());
							onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID() +":ACTION=Logging");
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
			
			if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(Balance_ID))
				continue;
			
			Map<String,String> BT_TypeValue = new HashMap<>();
			if(Balance_ID.equals("1635"))
			{
				BT_TypeValue.clear();
				BT_TypeValue.put(Balance_ID, balanceInput.getBEBUCKETID() + "|" + Balance_Value + "|" + Balance_StartDate + "|" + Balance_ExpiryDate );
				if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID).equals("Y"))
				{
					onlyLog.add("INC4003:Balance_Type Ignored:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
					AMBTIDSet.add(balanceInput.getBEBUCKETID());
					break;
				}
				else
				{
					if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
					{
						//INC4001	Balance_Type expired	MSISDN,BALANCE_TYPE,BE_BUCKET_VALUE,BE_BUCKET_ID,BE_EXPIRY
						onlyLog.add("INC4001:Balance_Type expired:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
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
									//INC4001	Balance_Type expired	MSISDN,BALANCE_TYPE,BE_BUCKET_VALUE,BE_BUCKET_ID,BE_EXPIRY
									onlyLog.add("INC4001:Balance_Type expired:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
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
														String SourceBucketID = item.getValue().split("\\|")[0];
														String SourceBTValue = item.getValue().split("\\|")[1];
														String SourceBTStart = item.getValue().split("\\|")[2];
														String SourceBTExpiry = item.getValue().split("\\|")[3];
														boolean ExtraOfferFlag = false;
														if(GroupBTItems.contains(SourceBTID))
														{
															if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName) != null)
															{							
																String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getOfferID();
																String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getSymbols();
																String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTValue();
																String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getProductPrivate();
																String Offer_Flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getOfferFlag();
																String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getOfferType();
																String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTTYPE();
																boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getOfferStartDate().length() > 0 ? true:false;
																boolean expiryFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getOfferExpiryDate().length() > 0 ? true:false;
																String ExtraOffer = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getAddOffer();
																if(!ExtraOffer.isEmpty())
																{
																	ExtraOfferFlag = true;
																}
																else
																{
																	ExtraOffer = "";
																}
																if(Offer_Flag.isEmpty())
																{
																	Offer_Flag = LoadSubscriberMapping.CommonConfigMap.get("default_NULL");
																}
																
																if(Symbol.equals(">=") && Integer.parseInt(SourceBTValue) >= Integer.parseInt(BT_Value))
																{	
																	GroupBalanceOffer.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFlag + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + SourceBucketID);
																	continue;
																}
																else if(Symbol.equals(">") && Integer.parseInt(SourceBTValue) > Integer.parseInt(BT_Value))
																{
																	GroupBalanceOffer.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFlag + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + SourceBucketID);
																	continue;
																}
																else if(Symbol.equals("=") && Integer.parseInt(SourceBTValue) == Integer.parseInt(BT_Value))
																{
																	GroupBalanceOffer.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFlag + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + SourceBucketID);
																	continue;
																}
																else if(Symbol.equals("or"))
																{
																	//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
																	String[] values = BT_Value.split("#");											
																	if(Arrays.stream(values).anyMatch(SourceBTValue::equals))
																	{
																		GroupBalanceOffer.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFlag + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + SourceBucketID);
																		continue;
																	}
																	else
																	{
																		if(BT_Type.toUpperCase().equals("P"))
																		{
																			if(LoadSubscriberMapping.GraceFullBalanceGroupingMap.containsKey(SourceBTID))
																			{
																				Set<String> BTValues = new HashSet<String>();
																				BTValues.addAll(LoadSubscriberMapping.GraceFullBalanceGroupingMap.get(SourceBTID));
																				if(!BTValues.contains(SourceBTValue))
																				{
																					if(BT_Type.equals("P") && Integer.parseInt(SourceBTValue) < 3)
																						onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceBTID + ":BE_BUCKET_VALUE=" + SourceBTValue + ":BE_BUCKET_ID=" + SourceBucketID +":ACTION=Logging");
																					else
																						onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceBTID + ":BE_BUCKET_VALUE=" + SourceBTValue + ":BE_BUCKET_ID=" + SourceBucketID +":ACTION=Logging");
																				}
																			}
																			else
																			{
																				if(BT_Type.equals("P") && Integer.parseInt(SourceBTValue) < 3)
																					onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceBTID + ":BE_BUCKET_VALUE=" + SourceBTValue + ":BE_BUCKET_ID=" + SourceBucketID +":ACTION=Logging");
																				else
																					onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceBTID + ":BE_BUCKET_VALUE=" + SourceBTValue + ":BE_BUCKET_ID=" + SourceBucketID +":ACTION=Logging");
																			}
																		}
																		else
																			onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceBTID + ":BE_BUCKET_VALUE=" + SourceBTValue + ":BE_BUCKET_ID=" + SourceBucketID +":ACTION=Logging");		
																		
																	}
																}
																/*else
																{
																	if(BT_Type.equals("P"))
																		onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceBTID + ":BE_BUCKET_VALUE=" + SourceBTValue + ":BE_BUCKET_ID=" + SourceBucketID +":ACTION=Logging");
																	else
																		onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceBTID + ":BE_BUCKET_VALUE=" + SourceBTValue + ":BE_BUCKET_ID=" + SourceBucketID +":ACTION=Logging");
																}*/																	
															}
														}
													}
													if(GroupBalanceOffer.size() == GroupBTItems.size())
													{
														Set<String> GroupBTID = new HashSet<>();
														gotSameExpiry = true;
														GroupBalanceOffer.forEach(item1->{GroupBTID.add(item1.split("\\|")[1].split(";")[9]);});
														for(String Str : GroupBalanceOffer)
														{
															String TargetOffer = Str.split("\\|")[1];											
															if(TargetOffer.split(";")[0].length() != 0)
															{
																
																FinalBalanceOffer.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
																		Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7],GroupBTID));
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
												ValidAMGroupBalanceOffer.forEach(item1->{GroupBTID.add(item1.split("\\|")[1].split(";")[9]);});
												for(String Str : ValidAMGroupBalanceOffer)
												{
													String TargetOffer = Str.split("\\|")[1];											
													if(TargetOffer.split(";")[0].length() != 0)
													{
														FinalBalanceOffer.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
																Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7],GroupBTID));
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
			
			if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(Balance_ID))
				continue;
			
			Map<String,String> BT_TypeValue = new HashMap<>();
			if(Balance_ID.equals("1635"))
			{
				BT_TypeValue.clear();
				BT_TypeValue.put(Balance_ID, balanceInput.getBEBUCKETID() + "|" + Balance_Value + "|" + Balance_StartDate + "|" + Balance_ExpiryDate );
				if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID).equals("Y"))
				{
					onlyLog.add("INC4003:Balance_Type Ignored:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
					AMBTIDSet.add(balanceInput.getBEBUCKETID());
					break;
				}
				else
				{
					if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
					{
						//INC4001	Balance_Type expired	MSISDN,BALANCE_TYPE,BE_BUCKET_VALUE,BE_BUCKET_ID,BE_EXPIRY
						onlyLog.add("INC4001:Balance_Type expired:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
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
											String SourceBucketID = item.getValue().split("\\|")[0];
											String SourceBTValue = item.getValue().split("\\|")[1];
											String SourceBTStart = item.getValue().split("\\|")[2];
											String SourceBTExpiry = item.getValue().split("\\|")[3];
											boolean ExtraOfferFlag = false;
											if(GroupBTItems.contains(SourceBTID))
											{
												if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName) != null)
												{							
													String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getOfferID();
													String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getSymbols();
													String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTValue();
													String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getProductPrivate();
													String Offer_Flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getOfferFlag();
													String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getOfferType();
													String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getBTTYPE();
													boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getOfferStartDate().length() > 0 ? true:false;
													boolean expiryFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getOfferExpiryDate().length() > 0 ? true:false;
													String ExtraOffer = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(SourceBTID + "|" + GroupName).getAddOffer();
													if(!ExtraOffer.isEmpty())
													{
														ExtraOfferFlag = true;
													}
													else
													{
														ExtraOffer = "";
													}
													if(Offer_Flag.isEmpty())
													{
														Offer_Flag = LoadSubscriberMapping.CommonConfigMap.get("default_NULL");
													}
													
													if(Symbol.equals(">=") && Integer.parseInt(SourceBTValue) >= Integer.parseInt(BT_Value))
													{	
														GroupBalanceOffer.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFlag + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + SourceBucketID);
														continue;
													}
													else if(Symbol.equals(">") && Integer.parseInt(SourceBTValue) > Integer.parseInt(BT_Value))
													{
														GroupBalanceOffer.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFlag + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + SourceBucketID);
														continue;
													}
													else if(Symbol.equals("=") && Integer.parseInt(SourceBTValue) == Integer.parseInt(BT_Value))
													{
														GroupBalanceOffer.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFlag + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + SourceBucketID);
														continue;
													}
													else if(Symbol.equals("or"))
													{
														//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
														String[] values = BT_Value.split("#");											
														if(Arrays.stream(values).anyMatch(SourceBTValue::equals))
														{
															GroupBalanceOffer.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + SourceBTID + ";" + SourceBTValue +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFlag + ";" + SourceBTStart + ";" + SourceBTExpiry  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + SourceBucketID);
															continue;
														}
														else
														{
															if(BT_Type.toUpperCase().equals("P"))
															{
																if(LoadSubscriberMapping.GraceFullBalanceGroupingMap.containsKey(SourceBTID))
																{
																	Set<String> BTValues = new HashSet<String>();
																	BTValues.addAll(LoadSubscriberMapping.GraceFullBalanceGroupingMap.get(SourceBTID));
																	if(!BTValues.contains(SourceBTValue))
																	{
																		if(BT_Type.equals("P") && Integer.parseInt(SourceBTValue) < 3)
																			onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceBTID + ":BE_BUCKET_VALUE=" + SourceBTValue + ":BE_BUCKET_ID=" + SourceBucketID +":ACTION=Logging");
																		else
																			onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceBTID + ":BE_BUCKET_VALUE=" + SourceBTValue + ":BE_BUCKET_ID=" + SourceBucketID +":ACTION=Logging");
																	}
																}
																else
																{
																	if(BT_Type.equals("P") && Integer.parseInt(SourceBTValue) < 3)
																		onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceBTID + ":BE_BUCKET_VALUE=" + SourceBTValue + ":BE_BUCKET_ID=" + SourceBucketID +":ACTION=Logging");
																	else
																		onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceBTID + ":BE_BUCKET_VALUE=" + SourceBTValue + ":BE_BUCKET_ID=" + SourceBucketID +":ACTION=Logging");
																}
															}
															else
																onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceBTID + ":BE_BUCKET_VALUE=" + SourceBTValue + ":BE_BUCKET_ID=" + SourceBucketID +":ACTION=Logging");
															AMBTIDSet.add(SourceBucketID);
														}
													}
													/*else
													{
														if(BT_Type.equals("P"))
															onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceBTID + ":BE_BUCKET_VALUE=" + SourceBTValue + ":BE_BUCKET_ID=" + SourceBucketID +":ACTION=Logging");
														else
															onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourceBTID + ":BE_BUCKET_VALUE=" + SourceBTValue + ":BE_BUCKET_ID=" + SourceBucketID +":ACTION=Logging");
													}*/
												}
											}
										}
										if(GroupBalanceOffer.size() == 2)
										{
											Set<String> GroupBTID = new HashSet<>();
											GroupBalanceOffer.forEach(item1->{GroupBTID.add(item1.split("\\|")[1].split(";")[9]);});
											for(String Str : GroupBalanceOffer)
											{
												String TargetOffer = Str.split("\\|")[1];											
												if(TargetOffer.split(";")[0].length() != 0)
												{
													FinalBalanceOffer.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
															Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7],GroupBTID));
													AMBTIDSet.addAll(GroupBTID);													
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
										onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID="+ BE_BucketId +":ACTION=Logging");
									}
								}
								else
								{
									onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID="+ BE_BucketId +":ACTION=Logging");
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
			String BT_Value = balanceInput.getBEBUCKETVALUE();
			String BT_StartDate = balanceInput.getBEBUCKETSTARTDATE();
			String BT_ExpiryDate = balanceInput.getBEEXPIRY();
			String BE_BucketId = balanceInput.getBEBUCKETID();
			if(AMBTIDSet.contains(balanceInput.getBEBUCKETID()))
				continue;
			
			if(LoadSubscriberMapping.NPPLifeCycleBTIDDetails.containsKey(BT_ID))
				continue;
			
			if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(BT_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(BT_ID).equals("Y"))
			{
				onlyLog.add("INC4003:Balance_Type Ignored:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
				AMBTIDSet.add(balanceInput.getBEBUCKETID());
				continue;
			}
		
			if(!BT_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(BT_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
			{
				//INC4001	Balance_Type expired	MSISDN,BALANCE_TYPE,BE_BUCKET_VALUE,BE_BUCKET_ID,BE_EXPIRY
				onlyLog.add("INC4001:Balance_Type expired:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value +  ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +  ":BE_EXPIRY=" + balanceInput.getBEEXPIRY() + ":ACTION=Logging");
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
						String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + GroupName).getOfferID();
						String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + GroupName).getSymbols();
						String BTValue = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + GroupName).getBTValue();
						String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + GroupName).getProductPrivate();
						String Offer_Flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + GroupName).getOfferFlag();
						String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + GroupName).getOfferType();
						String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + GroupName).getBTTYPE();
						boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + GroupName).getOfferStartDate().length() > 0 ? true:false;
						boolean expiryFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + GroupName).getOfferExpiryDate().length() > 0 ? true:false;
						String ExtraOffer = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + GroupName).getAddOffer();
						if(!ExtraOffer.isEmpty())
						{
							ExtraOfferFlag = true;
						}
						else
						{
							ExtraOffer = "";
						}
						if(Offer_Flag.isEmpty())
						{
							Offer_Flag = LoadSubscriberMapping.CommonConfigMap.get("default_NULL");
						}
						
						if(Symbol.equals(">=") && Integer.parseInt(BT_Value) >= Integer.parseInt(BTValue))
						{	
							AMBTIDSet.add(balanceInput.getBEBUCKETID());
							FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFlag,BT_StartDate, BT_ExpiryDate,"",Offer_Flag,new HashSet<>(Arrays.asList(BE_BucketId))));
							break;
						}
						else if(Symbol.equals(">") && Integer.parseInt(BT_Value) > Integer.parseInt(BTValue))
						{
							AMBTIDSet.add(balanceInput.getBEBUCKETID());
							FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFlag,BT_StartDate, BT_ExpiryDate,"",Offer_Flag,new HashSet<>(Arrays.asList(BE_BucketId))));
							break;
						}
						else if(Symbol.equals("=") && Integer.parseInt(BT_Value) == Integer.parseInt(BTValue))
						{
							AMBTIDSet.add(balanceInput.getBEBUCKETID());
							FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFlag,BT_StartDate, BT_ExpiryDate,"",Offer_Flag,new HashSet<>(Arrays.asList(BE_BucketId))));
							break;
						}
						else if(Symbol.equals("or"))
						{
							//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
							String[] values = BTValue.split("#");											
							if(Arrays.stream(values).anyMatch(BT_Value::equals))
							{
								AMBTIDSet.add(balanceInput.getBEBUCKETID());
								FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFlag,BT_StartDate, BT_ExpiryDate,"",Offer_Flag,new HashSet<>(Arrays.asList(BE_BucketId))));
								break;
							}
							else
							{
								if(BT_Type.toUpperCase().equals("P"))
								{
									if(LoadSubscriberMapping.GraceFullBalanceGroupingMap.containsKey(BT_ID))
									{
										Set<String> BTValues = new HashSet<String>();
										BTValues.addAll(LoadSubscriberMapping.GraceFullBalanceGroupingMap.get(BT_ID));
										if(!BTValues.contains(BT_Value))
										{
											if(BT_Type.equals("P") && Integer.parseInt(BT_Value) < 3)
												onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
											else
												onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
										}
									}
									else
									{
										if(BT_Type.equals("P") && Integer.parseInt(BT_Value) < 3)
											onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
										else
											onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
									}
								}
								else
									onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + BT_ID + ":BE_BUCKET_VALUE=" + BT_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
								AMBTIDSet.add(balanceInput.getBEBUCKETID());
							}
						}
					}
				}
				
			}
		}		
		return FinalBalanceOffer;
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
		
		/*if(!BestMatchFound && BestMatch.size() > 1)
			FinalGroupName = Collections.max(BestMatch.entrySet(), (entry1, entry2) -> entry1.getValue().size() - entry2.getValue().size()).getKey();*/
		
		List<String> GroupBalanceOffer = new ArrayList<>();
		Map<String,List<String>> AMGroupOfferMap = new HashMap<>();
		Set<String> AMCompletedBT = new HashSet<>();
		if(ComputedGroupName.length() != 0)
		{	
			boolean ExtraOfferFlag = false;
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
						String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getProductPrivate();
						String Offer_Flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getOfferFlag();
						String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getOfferType();
						String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getBTTYPE();
						boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getOfferStartDate().length() > 0 ? true:false;
						boolean expiryFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getOfferExpiryDate().length() > 0 ? true:false;
						String ExtraOffer = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getAddOffer();
						if(!ExtraOffer.isEmpty())
						{
							ExtraOfferFlag = true;
						}
						else
						{
							ExtraOffer = "";
						}
						
						if(Offer_Flag.isEmpty())
						{
							Offer_Flag = LoadSubscriberMapping.CommonConfigMap.get("default_NULL");
						}
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						AMCompletedBT.add(TempBalance_ID);
						GroupBalanceOffer.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + balanceInput.getBEBUCKETVALUE() +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFlag + ";" + balanceInput.getBEBUCKETSTARTDATE() + ";" + balanceInput.getBEEXPIRY()  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + balanceInput.getBEBUCKETID());
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

	public Map<String,Map<String,List<String>>>  ComputeASpecialGroup(String inputBalance_ID, Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub Map<String,List<String>>
		
		List<String> ASGroupName = new ArrayList<>();		
		List<String> GroupBalanceOffer = new ArrayList<>();
		List<String> FinalBalanceOffer = new ArrayList<>();
		Set<String> ASBT_ID = new HashSet<>();
		boolean ExtraOfferFlag = false;
		boolean ASGroupFormed = false;
		Map<String,List<String>> tempGroupBalanceOffer = new HashMap<>();
		
		Map<String,Map<String,List<String>>> ASGroupOfferMap = new HashMap<>();
		ASGroupName = (commonfunction.getASpecialGroupKey(LoadSubscriberMapping.BalanceOnlySpecialASGroupMap,inputBalance_ID));

		//CheckIf A-S-1 is present in the input
		//Map<String,Map<String,List<String>>> ASGroupOfferMap = new HashMap<>();
		ASGroupOfferMap = CheckifA_S_1Present(CompletedBT_ID);
		
		Map<String,List<String>> AS1_OutputDetails = new HashMap<>();
		AS1_OutputDetails = ASGroupOfferMap.get("ASOutputDetails");
		if(AS1_OutputDetails.containsKey("Offer"))
		{											
			FinalBalanceOffer.addAll(AS1_OutputDetails.get("Offer"));			
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
							String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getProductPrivate();
							String Offer_Flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getOfferFlag();
							String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getOfferType();
							String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getBTTYPE();
							boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getOfferStartDate().length() > 0 ? true:false;
							boolean expiryFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getOfferExpiryDate().length() > 0 ? true:false;
							String ExtraOffer = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + GroupName).getAddOffer();
							if(!ExtraOffer.isEmpty())
							{
								ExtraOfferFlag = true;
							}
							else
							{
								ExtraOffer = "";
							}
							
							if(Offer_Flag.isEmpty())
							{
								Offer_Flag = LoadSubscriberMapping.CommonConfigMap.get("default_NULL");
							}
							
							
							if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
							{								
								if(!GroupElements.contains(TempBalance_ID))
								{
									ASBT_ID.add(TempbalanceInput.getBEBUCKETID());
									GroupElements.add(TempBalance_ID);
									GroupBalanceOffer.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFlag + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
									continue;
								}
							}
							else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
							{								
								if(!GroupElements.contains(TempBalance_ID))
								{
									ASBT_ID.add(TempbalanceInput.getBEBUCKETID());
									GroupElements.add(TempBalance_ID);
									GroupBalanceOffer.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFlag + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
									continue;
								}
							}
							else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
							{
								if(!GroupElements.contains(TempBalance_ID))
								{
									ASBT_ID.add(TempbalanceInput.getBEBUCKETID());
									GroupElements.add(TempBalance_ID);
									GroupBalanceOffer.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFlag + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
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
										GroupBalanceOffer.add(BT_Type + ";" + TempbalanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + TempbalanceInput.getBEBUCKETVALUE() +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFlag + ";" + TempbalanceInput.getBEBUCKETSTARTDATE() + ";" + TempbalanceInput.getBEEXPIRY()  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + TempbalanceInput.getBEBUCKETID());
										continue;
									}
								}
								else
								{
									if(BT_Type.toUpperCase().equals("P"))
									{
										if(LoadSubscriberMapping.GraceFullBalanceGroupingMap.containsKey(TempBalance_ID))
										{
											Set<String> BTValues = new HashSet<String>();
											BTValues.addAll(LoadSubscriberMapping.GraceFullBalanceGroupingMap.get(TempBalance_ID));
											if(!BTValues.contains(TempBalance_Value))
											{
												if(Integer.parseInt(TempBalance_Value) < 3)
													onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
												else
													onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
											}
										}
										else
										{
											if(Integer.parseInt(TempBalance_Value) < 3)
												onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
											else
												onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
										}
									}
									else
										onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
								}
							}
																					
						}
						/*else
						{
							if(Integer.parseInt(TempBalance_Value) < 3)
								onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
							else
								onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":BE_BUCKET_ID=" + TempbalanceInput.getBEBUCKETID() +":ACTION=Logging");
						}*/	
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
			String TargetOffer = GroupBalanceOffer.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[1];
			FinalBalanceOffer.add(PopulateOffer(TargetOffer.split(";")[0],TargetOffer.split(";")[1],Boolean.parseBoolean(TargetOffer.split(";")[2]),
					Boolean.parseBoolean(TargetOffer.split(";")[3]),TargetOffer.split(";")[4], TargetOffer.split(";")[5],TargetOffer.split(";")[6],TargetOffer.split(";")[7],ASBT_ID));
		}
		else
		{
			List<String> ValidGroupBalanceOffer = Collections.max(tempGroupBalanceOffer.entrySet(), (entry1, entry2) -> entry1.getValue().size() - entry2.getValue().size()).getValue();
			ASBT_ID.clear();
			for(String Str : ValidGroupBalanceOffer)
			{
				String TargetOffer = Str.split("\\|")[0];
				String SourceOffer = Str.split("\\|")[1];
				String BT_Type = TargetOffer.split(";")[0];
				if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M") != null)
				{
					if(TargetOffer.split(";")[2].equals("74"))
					{
						String ProfileTag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getPTName().trim();
						
						if(!ProfileTag.isEmpty())
						{
							List<String> PT_List = Arrays.asList(ProfileTag.split("#"));
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
								String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getOfferID();
								String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getOfferType();
								String Offer_flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getOfferFlag();
								boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getOfferStartDate().length() > 0 ? true:false;
								boolean expiryFalg = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getOfferExpiryDate().length() > 0 ? true:false;
								String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getSymbols();
								String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getBTValue();
								
								String Start_Date = "";
								String Expiry_Date = "";
								
								String BlckBrryActCnfrmDate = profileTag.GetProfileTagValue("BlckBrryActCnfrmDate");
								if(!BlckBrryActCnfrmDate.isEmpty() && BlckBrryActCnfrmDate.length() == 14)
									Start_Date = BlckBrryActCnfrmDate.substring(0,4) + "-" + BlckBrryActCnfrmDate.substring(4,6) + "-" + BlckBrryActCnfrmDate.substring(6,8) + " " + BlckBrryActCnfrmDate.substring(8,10) + ":" + BlckBrryActCnfrmDate.substring(10,12) + ":" + BlckBrryActCnfrmDate.substring(12,14);
								else
									Start_Date = "";
								
								String BlckBrryExpDate = profileTag.GetProfileTagValue("BlckBrryExpDate");
								if(!BlckBrryExpDate.isEmpty() && BlckBrryExpDate.length() == 14)
									Expiry_Date = BlckBrryExpDate.substring(0,4) + "-" + BlckBrryExpDate.substring(4,6) + "-" + BlckBrryExpDate.substring(6,8) + " " + BlckBrryExpDate.substring(8,10) + ":" + BlckBrryExpDate.substring(10,12) + ":" + BlckBrryExpDate.substring(12,14);
								else
									Expiry_Date = "";
								ASBT_ID.add(SourceOffer.split(";")[9]);
								if(Symbol.equals(">") && Integer.parseInt(TargetOffer.split(";")[3]) > Integer.parseInt(BT_VALUE))
								{
									FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Start_Date, Expiry_Date,"",Offer_flag,new HashSet<>(Arrays.asList(SourceOffer.split(";")[9]))));
									trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
								}
								else if(Symbol.equals(">=") && Integer.parseInt(TargetOffer.split(";")[3]) >= Integer.parseInt(BT_VALUE))
								{
									FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Start_Date, Expiry_Date,"",Offer_flag,new HashSet<>(Arrays.asList(SourceOffer.split(";")[9]))));
									trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
								}
								else if(Symbol.equals("=") && Integer.parseInt(TargetOffer.split(";")[3]) == Integer.parseInt(BT_VALUE))
								{
									FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,Start_Date, Expiry_Date,"",Offer_flag, new HashSet<>(Arrays.asList(SourceOffer.split(";")[9]))));
									trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
								}
								else
									//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ CurrentGroupBalanceID.get(TargetOffer.split(";")[2] + "," + TargetOffer.split(";")[3] + "," + SourceOffer.split(";")[5])  +":ACTION=Logging");
									onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
							}
							else
							{
								onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");								
							}
							ASBT_ID.add(SourceOffer.split(";")[9]);
						}
						else
						{						
							String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getOfferID();
							String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getOfferType();
							String Offer_flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getOfferFlag();
							boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getOfferStartDate().length() > 0 ? true:false;
							boolean expiryFalg = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getOfferExpiryDate().length() > 0 ? true:false;
							String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getSymbols();
							String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getBTValue();
							
							if(Symbol.equals(">") && Integer.parseInt(TargetOffer.split(";")[3]) > Integer.parseInt(BT_VALUE))
							{
								FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,SourceOffer.split(";")[4], SourceOffer.split(";")[5],"",Offer_flag,new HashSet<>(Arrays.asList(SourceOffer.split(";")[9]))));
								trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
							}
							else if(Symbol.equals(">=") && Integer.parseInt(TargetOffer.split(";")[3]) >= Integer.parseInt(BT_VALUE))
							{
								FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,SourceOffer.split(";")[4], SourceOffer.split(";")[5],"",Offer_flag,new HashSet<>(Arrays.asList(SourceOffer.split(";")[9]))));
								trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
							}
							else if(Symbol.equals("=") && Integer.parseInt(TargetOffer.split(";")[3]) == Integer.parseInt(BT_VALUE))
							{
								FinalBalanceOffer.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,SourceOffer.split(";")[4], SourceOffer.split(";")[5],"",Offer_flag,new HashSet<>(Arrays.asList(SourceOffer.split(";")[9]))));
								trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
							}
							else
								//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ CurrentGroupBalanceID.get(TargetOffer.split(";")[2] + "," + TargetOffer.split(";")[3] + "," + SourceOffer.split(";")[5])  +":ACTION=Logging");
								onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
							ASBT_ID.add(SourceOffer.split(";")[9]);
						}
					}					
				}
				else
				{
					if(BT_Type.toUpperCase().equals("P") && Integer.parseInt(TargetOffer.split(";")[3]) < 3)
						onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");						
					else
						onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
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
		if(FinalBalanceOffer.size() != 0)
			ASOutputDetails.put("Offer", FinalBalanceOffer);
	
		ASGroupOfferMap.put("ASOutputDetails", ASOutputDetails);
		
		return ASGroupOfferMap;
	}
	
	public Map<String,Map<String,List<String>>> CheckifA_S_1Present(Set<String> CompletedBT_ID)
	{
		List<String> GroupBalanceOffer = new ArrayList<>();
		List<String> FinalBalanceOffer = new ArrayList<>();
		Set<String> ASBT_ID = new HashSet<>();
		Map<String,Map<String,List<String>>> ASGroupOfferMap = new HashMap<>();
		boolean ExtraOfferFlag = false;
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{
			String TempBalance_ID = balanceInput.getBALANCETYPE();
			String TempBalance_Value = balanceInput.getBEBUCKETVALUE();
			if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
				continue;
			
			if(TempBalance_ID.equals("1035") && TempBalance_Value.equals("2"))
			{
				String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getOfferID();
				String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getProductPrivate();
				String Offer_Flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getOfferFlag();
				String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getOfferType();
				String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getBTTYPE();
				boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getOfferStartDate().length() > 0 ? true:false;
				boolean expiryFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getOfferExpiryDate().length() > 0 ? true:false;
				String ExtraOffer = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getAddOffer();
				if(!ExtraOffer.isEmpty())
				{
					ExtraOfferFlag = true;
				}
				else
				{
					ExtraOffer = "";
				}
				
				if(Offer_Flag.isEmpty())
				{
					Offer_Flag = LoadSubscriberMapping.CommonConfigMap.get("default_NULL");
				}
				ASBT_ID.add(balanceInput.getBEBUCKETID());
				GroupBalanceOffer.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + balanceInput.getBEBUCKETVALUE() +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFlag + ";" + balanceInput.getBEBUCKETSTARTDATE() + ";" + balanceInput.getBEEXPIRY()  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + balanceInput.getBEBUCKETID());				
			}
			if(TempBalance_ID.equals("74") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt("-999999"))
			{
				String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getOfferID();
				String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getSymbols();
				String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getBTValue();
				String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getProductPrivate();
				String Offer_Flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getOfferFlag();
				String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getOfferType();
				String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getBTTYPE();
				boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getOfferStartDate().length() > 0 ? true:false;
				boolean expiryFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getOfferExpiryDate().length() > 0 ? true:false;
				String ExtraOffer = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-1").getAddOffer();
				if(!ExtraOffer.isEmpty())
				{
					ExtraOfferFlag = true;
				}
				else
				{
					ExtraOffer = "";
				}
				
				if(Offer_Flag.isEmpty())
				{
					Offer_Flag = LoadSubscriberMapping.CommonConfigMap.get("default_NULL");
				}
				ASBT_ID.add(balanceInput.getBEBUCKETID());
				GroupBalanceOffer.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + balanceInput.getBEBUCKETVALUE() +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFlag + ";" + balanceInput.getBEBUCKETSTARTDATE() + ";" + balanceInput.getBEEXPIRY()  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + balanceInput.getBEBUCKETID());
				
			}
		}
		
		if(GroupBalanceOffer.size() == 2)
		{
			if(GroupBalanceOffer.stream().filter(x->x.contains("Blackberry Bundle")).count() == 1 && GroupBalanceOffer.stream().filter(x->x.contains("Blackberry KB")).count() == 1)
			{
				for(String Str : GroupBalanceOffer)
				{
					//M;Blackberry Bundle;1035;2|6014;Timer;false;false;;1970-01-01 00:00:00;Yes;NULL;;752817472
					String TargetOffer = Str.split("\\|")[0];
					String SourceOffer = Str.split("\\|")[1];
					
					if(!SourceOffer.split(";")[0].isEmpty())
					{
						String Start_Date = "";
						String Expiry_Date = "";
						String BlckBrryActCnfrmDate = profileTag.GetProfileTagValue("BlckBrryActCnfrmDate");
						if(!BlckBrryActCnfrmDate.isEmpty() && BlckBrryActCnfrmDate.length() == 14)
							Start_Date = BlckBrryActCnfrmDate.substring(0,4) + "-" + BlckBrryActCnfrmDate.substring(4,6) + "-" + BlckBrryActCnfrmDate.substring(6,8) + " " + BlckBrryActCnfrmDate.substring(8,10) + ":" + BlckBrryActCnfrmDate.substring(10,12) + ":" + BlckBrryActCnfrmDate.substring(12,14);
						else
							Start_Date = "";
						
						String BlckBrryExpDate = profileTag.GetProfileTagValue("BlckBrryExpDate");
						if(!BlckBrryExpDate.isEmpty() && BlckBrryExpDate.length() == 14)
							Expiry_Date = BlckBrryExpDate.substring(0,4) + "-" + BlckBrryExpDate.substring(4,6) + "-" + BlckBrryExpDate.substring(6,8) + " " + BlckBrryExpDate.substring(8,10) + ":" + BlckBrryExpDate.substring(10,12) + ":" + BlckBrryExpDate.substring(12,14);
						else
							Expiry_Date = "";
						
						FinalBalanceOffer.add(PopulateOffer(SourceOffer.split(";")[0],SourceOffer.split(";")[1],Boolean.parseBoolean(SourceOffer.split(";")[2]),Boolean.parseBoolean(SourceOffer.split(";")[3]),Start_Date, Expiry_Date,"","",ASBT_ID));
					}
				}
			}
			else
			{
				ASBT_ID.clear();
				/*for(String Str : GroupBalanceOffer)
				{
					String TargetOffer = Str.split("\\|")[0];
					String SourceOffer = Str.split("\\|")[1];
					onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
				}*/				
			}
		}
		else
		{
			if(GroupBalanceOffer.size() == 1 && GroupBalanceOffer.stream().filter(x->x.contains("Blackberry Bundle")).count() == 1)
			{
				for(String Str : GroupBalanceOffer)
				{
					//M;Blackberry Bundle;1035;2|6014;Timer;false;false;;1970-01-01 00:00:00;Yes;NULL;;752817472
					String TargetOffer = Str.split("\\|")[0];
					String SourceOffer = Str.split("\\|")[1];
					
					if(TargetOffer.split(";")[3].equals("2"))
					{
						onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
					}
				}
			}
			else
				ASBT_ID.clear();
		}
		
		Map<String,List<String>> ASOutputDetails = new HashMap<>();
		if(ASBT_ID.size() != 0)
			ASOutputDetails.put("CompletedBT", new ArrayList<String>(ASBT_ID));
		if(FinalBalanceOffer.size() != 0)
			ASOutputDetails.put("Offer", FinalBalanceOffer);
	
		ASGroupOfferMap.put("ASOutputDetails", ASOutputDetails);
		
		return ASGroupOfferMap;
	}
	
	private List<String> PopulateMasterOffer(List<String> ValidGroupBalanceOffer,Set<String> BEIDForProductID)
	{
		List<String> BalanceOfferList = new ArrayList<>();
		
		for(String Str : ValidGroupBalanceOffer)
		{
			String TargetOffer = Str.split("\\|")[0];
			String SourceOffer = Str.split("\\|")[1];
			
			if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M") != null)
			{
				String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getOfferID();
				String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getOfferType();
				String Offer_flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getOfferFlag();
				boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getOfferStartDate().length() > 0 ? true:false;
				boolean expiryFalg = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getOfferExpiryDate().length() > 0 ? true:false;
				String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getSymbols();
				String BT_VALUE = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TargetOffer.split(";")[2] + "|M").getBTValue();
				
				if(Symbol.equals(">") && Integer.parseInt(TargetOffer.split(";")[3]) > Integer.parseInt(BT_VALUE))
				{
					BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,SourceOffer.split(";")[4], SourceOffer.split(";")[5],"",Offer_flag, new HashSet<>(Arrays.asList(SourceOffer.split(";")[9]))));
					trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
					continue;
				}
				if(Symbol.equals(">=") && Integer.parseInt(TargetOffer.split(";")[3]) >= Integer.parseInt(BT_VALUE))
				{
					BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,SourceOffer.split(";")[4], SourceOffer.split(";")[5],"",Offer_flag, new HashSet<>(Arrays.asList(SourceOffer.split(";")[9]))));
					trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
					continue;
				}
				if(Symbol.equals("=") && Integer.parseInt(TargetOffer.split(";")[3]) == Integer.parseInt(BT_VALUE))
				{
					BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,SourceOffer.split(";")[4], SourceOffer.split(";")[5],"",Offer_flag, new HashSet<>(Arrays.asList(SourceOffer.split(";")[9]))));
					trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
					continue;
				}
				if(Symbol.equals("or"))
				{
					String[] values = BT_VALUE.split("#");											
					if(Arrays.stream(values).anyMatch(TargetOffer.split(";")[3]::equals))
					{
						//Special case of BT1454, where expiry date should be populated as migration date and expiry as migration_Date+30
						if(TargetOffer.split(";")[2].equals("1454"))
						{
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Calendar c = Calendar.getInstance();
							try 
							{
								String CurrectExpiryDate = "";
								c.setTime(sdf.parse(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()));
								c.add(Calendar.DAY_OF_MONTH, 30); 
								CurrectExpiryDate = sdf.format(c.getTime());
								BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString(), CurrectExpiryDate,"",Offer_flag, new HashSet<>(Arrays.asList(SourceOffer.split(";")[9]))));
								
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
						else
						{
							BalanceOfferList.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFalg,SourceOffer.split(";")[4], SourceOffer.split(";")[5],"",Offer_flag, new HashSet<>(Arrays.asList(SourceOffer.split(";")[9]))));
						}
						trackLog.add("INC7001:Master mapping considered:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":OFFER_ID=" + Offer_ID + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
						continue;
					}					
				}
				else
				{
					if(TargetOffer.split(";")[0].equals("P") && Integer.parseInt(TargetOffer.split(";")[3]) < 3)
					{
						onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");						
					}
					else
						onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
				}
			}
			else
			{
				if(TargetOffer.split(";")[0].equals("P") && Integer.parseInt(TargetOffer.split(";")[3]) < 3)
					onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
				else
					onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ SourceOffer.split(";")[9]  +":ACTION=Logging");
			}
		}	
		
		return BalanceOfferList;
	}
	
	private String PopulateOffer(String Offer_ID,String Offer_Type, boolean startFlag, boolean expiryFlag, String Balance_StartDate,
			String Balance_ExpiryDate, String Product_Private, String flag, Set<String> BucketIDList) {
		
			String Offer_Startdate = "";
			String Offer_StartSec= "";
			String Offer_Expirydate= "";
			String Offer_ExpirySec= "";
			
			if(Offer_ID.equals("4805"))
				this.Offer4805Created = true;
			
			
			if(Balance_StartDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
				Balance_StartDate = "";
			
			if(Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()))
				Balance_ExpiryDate = "";
			if(Offer_Type.toUpperCase().equals("TIMER"))
			{
				if(!Balance_StartDate.isEmpty() && startFlag)
				{
					Offer_Startdate = CommonUtilities.convertDateToTimerOfferDate(Balance_StartDate)[0].toString();
					Offer_StartSec = CommonUtilities.convertDateToTimerOfferDate(Balance_StartDate)[1].toString();								
				}
				else
				{
					Offer_Startdate = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();
					Offer_StartSec = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();
				}
				if(!Balance_ExpiryDate.isEmpty() && expiryFlag)
				{
					Offer_Expirydate = CommonUtilities.convertDateToTimerOfferDate(Balance_ExpiryDate)[0].toString(); 
					Offer_ExpirySec = CommonUtilities.convertDateToTimerOfferDate(Balance_ExpiryDate)[1].toString(); 
				}
				else
				{
					Offer_Expirydate = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();
					Offer_ExpirySec = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();
				}
			}
			else
			{
				if(!Balance_StartDate.isEmpty())
				{
					Offer_Startdate = CommonUtilities.convertDateToTimerOfferDate(Balance_StartDate)[0].toString();
					Offer_StartSec = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();								
				}
				else
				{
					Offer_Startdate = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();
					Offer_StartSec = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();
				}
				if(!Balance_ExpiryDate.isEmpty())
				{
					Offer_Expirydate = CommonUtilities.convertDateToTimerOfferDate(Balance_ExpiryDate)[0].toString(); 
					Offer_ExpirySec = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString(); 
				}
				else
				{
					Offer_Expirydate = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();
					Offer_ExpirySec = LoadSubscriberMapping.CommonConfigMap.get("default_NULL").toString();
				}
			}
			
			
			StringBuffer sb = new StringBuffer();
			sb.append(msisdn).append(",");
			sb.append(Offer_ID).append(",");
			sb.append(Offer_Startdate).append(",");
			sb.append(Offer_Expirydate).append(",");
			sb.append(Offer_StartSec).append(",");
			sb.append(Offer_ExpirySec).append(",");
			if(flag.length() == 1)
			{
				sb.append(flag).append(",");
			}
			else
				sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
			if(LoadSubscriberMapping.BalanceProductIDMap.containsKey(Offer_ID))
			{
				if(Offer_ID.equals("53"))
				{
					ProductIDFor53.put(CounterFor53, String.valueOf(productID)) ;
					sb.append(String.valueOf(productID));
					CounterFor53++;					
				}
				else
				{
					sb.append(String.valueOf(productID));
					//Now Populate it into Map to be used in offer and other places
					Set<String> temp = new HashSet<>();
					temp.addAll(BucketIDList);
					ProductIDLookUpMap.put(String.valueOf(productID), temp);					
				}
				productID++;
			}
			else
			{
				sb.append("0");
			}
		
		// TODO Auto-generated method stub
		return sb.toString();
	}	

	private List<String> PopulateAS2Group(List<String> validGroupBalanceOffer, Set<String> BEIDForProductID) {
		// TODO Auto-generated method stub
		List<String> ASGroupOutput = new ArrayList<>();
		
		for(String item: validGroupBalanceOffer)
		{
			String TempBalance_ID = item.split("\\|")[0].split(";")[2];
			String TempStart_Date = item.split("\\|")[1].split(";")[4];
			String TempExpiry_Date = item.split("\\|")[1].split(";")[5];
			boolean ExtraOfferFlag = false;
			
			String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-2").getOfferID();
			String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-2").getProductPrivate();
			String Offer_Flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-2").getOfferFlag();
			String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-2").getOfferType();
			boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-2").getOfferStartDate().length() > 0 ? true:false;
			boolean expiryFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-2").getOfferExpiryDate().length() > 0 ? true:false;
			String ExtraOffer = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|A-S-2").getAddOffer();
			if(!ExtraOffer.isEmpty())
			{
				ExtraOfferFlag = true;
			}
			else
			{
				ExtraOffer = "";
			}
			
			if(Offer_Flag.isEmpty())
			{
				Offer_Flag = LoadSubscriberMapping.CommonConfigMap.get("default_NULL");
			}
			if(!Offer_ID.isEmpty())
			{
				//Offer_ID,Offer_Type, boolean startFlag, boolean expiryFlag, String Balance_StartDate,String Balance_ExpiryDate, String Product_Private, String flag, Set<String> BucketIDList
				ASGroupOutput.add(PopulateOffer(Offer_ID,Offer_Type,startFlag,expiryFlag,TempStart_Date,TempExpiry_Date,Product_Private,Offer_Flag,BEIDForProductID)); //
				trackLog.add("INC7011:BT mapped in A-S-* group:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TempBalance_ID + ":BE_BUCKET_ID="+ item.split("\\|")[1].split(";")[9] + ":ACTION=Logging");
			}			
		}		
		return ASGroupOutput;
	}

	private String populateProfileOffer(String offerId,String Offer_start,String Offer_end, String Product_Private,String ProfileTagName, String ProfileTagValue) {
		StringBuffer sb = new StringBuffer();
		
		sb.append(msisdn).append(",");
		sb.append(offerId).append(",");
		if(Offer_start.length() > 1)
			sb.append(CommonUtilities.convertDateToTimerOfferDate(Offer_start)[0].toString()).append(",");
		else
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
		if(Offer_end.length() > 1)
			sb.append(CommonUtilities.convertDateToTimerOfferDate(Offer_end)[0].toString()).append(",");
		else
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
		
		if(Offer_start.length() > 1)
			sb.append(CommonUtilities.convertDateToTimerOfferDate(Offer_start)[1].toString()).append(",");
		else
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
		if(Offer_end.length() > 1)
			sb.append(CommonUtilities.convertDateToTimerOfferDate(Offer_end)[1].toString()).append(",");
		else
			sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
		
		//sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
		//sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
		sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_NULL")).append(",");
		if(Product_Private.toUpperCase().equals("NO"))
			sb.append("0");
		else if(Product_Private.toUpperCase().equals("YES"))
		{	
			sb.append(String.valueOf(productID));
			this.productID++;
			this.ProfileProductIDLookUpMap.put(ProfileTagName +"|" + ProfileTagValue, productID);
		}
		return sb.toString();
	}
	
	private void GenerateLoggingForRemaingBalances() {
		// TODO Auto-generated method stub
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{			
			String Balance_ID = balanceInput.getBALANCETYPE();
			String Balance_Value = balanceInput.getBEBUCKETVALUE();
			if(CompletedBTIDSet.contains(balanceInput.getBEBUCKETID()))
			{
				continue;
			}
			else
			{
				if(LoadSubscriberMapping.CompleteMappingBalanceValues.containsKey(Balance_ID))
				{
					Set<String> BalanceValueKey = LoadSubscriberMapping.CompleteMappingBalanceValues.get(Balance_ID);
					if(!BalanceValueKey.contains(Balance_Value))
					{
						if(LoadSubscriberMapping.CompleteMappingBalanceType.get(Balance_ID).equals("P"))
						{
							if(Integer.parseInt(Balance_Value) < 3)
								onlyLog.add("INC4005:Periodic Charge is not active:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
							else
								onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID() +":ACTION=Logging");
						}
						else
							onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID="+ balanceInput.getBEBUCKETID() +":ACTION=Logging");
					}
						
				}
			}			
		}	
	}
	
	/******Code to PAM creation****************/
	
	private List<String> generatePam(){
		List<String> PAM = new ArrayList<>();
		
		PAM.addAll(PAMFromDefaultService(msisdn));
		PAM.addAll(PAMFromProductMapping(msisdn));
		
		return PAM;
	}
	
	private Collection<? extends String> PAMFromProductMapping(String msisdn) {
		List<String> defaultPAMList = new ArrayList<>();
		Set<String> CompletedGroupBT_ID = new HashSet<>();	
		Set<String> CompletedBT_ID = new HashSet<>();
		
		//System.out.println(CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()));
		
		for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		{			
			String Balance_ID = balanceInput.getBALANCETYPE();
			String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
			CompletedGroupBT_ID.clear();
			if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
				continue;
			
			if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID).equals("Y"))
				continue;
			
			//Check for expiry Date, log it and proceed further
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
					String Symbol = LoadSubscriberMapping.AllBTBalancesValueMap.get(Balance_ID).getSymbols();
					String BT_Value = LoadSubscriberMapping.AllBTBalancesValueMap.get(Balance_ID).getBTValue();
					boolean ValidBT = false;
					if(Symbol.equals(">=") && Integer.parseInt(balanceInput.getBEBUCKETVALUE()) >= Integer.parseInt(BT_Value))
						ValidBT = true;
					else if(Symbol.equals(">") && Integer.parseInt(balanceInput.getBEBUCKETVALUE()) > Integer.parseInt(BT_Value))
						ValidBT = true;
					else if(Symbol.equals("=") && Integer.parseInt(balanceInput.getBEBUCKETVALUE()) == Integer.parseInt(BT_Value))
						ValidBT = true;
					else if(Symbol.equals("or"))
					{
						//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
						String[] values = BT_Value.split("#");
						
						if(Arrays.stream(values).anyMatch(balanceInput.getBEBUCKETVALUE()::equals))
						{
							ValidBT = true;
						}
					}
					if(!ValidBT)
					{
						//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
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
					if(!BalanceValueKey.contains(balanceInput.getBEBUCKETVALUE()))
					{
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						continue;
					}
				}
			}
			
			if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
			{
				continue;
			}
			else
			{
				if(LoadSubscriberMapping.BalanceEmptyBTGroupIdentifierMap.get(Balance_ID + "|") != null)
				{
					//no Action needed in case of PAM
				}	
				else
				{						
					if(LoadSubscriberMapping.ExceptionBalances.contains(Balance_ID))
					{			
						//no Action needed in case of PAM
					}
					else if(LoadSubscriberMapping.BalanceOnlySpecialAMGroupSet.contains(Balance_ID))
					{
						
					}
					else
					{
						String GroupName = "";
						Set<String> CurrentGroupBalance = new HashSet<>();
						List<String> ValidGroupBalanceOffer = new ArrayList<>();
						Set<String> ValidGroupBT_ID = new HashSet<>();
						boolean ExtraPAMFlag = false;
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
								if(GroupName.startsWith("H-"))
								{
									GroupName = commonfunction.ComputeHGroup(Balance_ID,GroupName,CompletedBT_ID);
									CurrentGroupBalance.addAll(LoadSubscriberMapping.BalanceGroupingMap.get(GroupName));
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
										
										if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(TempBalance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(TempBalance_ID).equals("Y"))
										{
											CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
											continue;
										}
										
										if(CompletedBT_ID.contains(TempbalanceInput.getBEBUCKETID()))
											continue;
										
										if(id.equals(TempBalance_ID))
										{
											if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(id + "|" + FinalGroupName) != null)
											{
												if(!TempBalance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(TempBalance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
												{													
													continue;
												}
												String Offer_ID = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getOfferID();
												String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getSymbols();
												String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTValue();
												String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getProductPrivate();
												String Offer_Flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getOfferFlag();
												String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getOfferType();
												String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getBTTYPE();
												boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getOfferStartDate().length() > 0 ? true:false;
												boolean expiryFalg = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getOfferExpiryDate().length() > 0 ? true:false;
												String ExtraPAM = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + FinalGroupName).getAddPAM();
												if(ExtraPAM.isEmpty())
												{
													ExtraPAM = "";
												}
												else
												{
													ExtraPAMFlag = true;
												}
												
												CompletedBT_ID.add(TempbalanceInput.getBEBUCKETID());
												CompletedGroupBT_ID.add(TempbalanceInput.getBEBUCKETID());
												if(Symbol.equals(">=") && Integer.parseInt(TempBalance_Value) >= Integer.parseInt(BT_Value))
												{
													ValidGroupBalanceOffer.add(BT_Type + ";" + TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraPAM + ";" + TempbalanceInput.getBEBUCKETID());
													ValidGroupBT_ID.add(TempBalance_ID);
													break;
												}
												else if(Symbol.equals(">") && Integer.parseInt(TempBalance_Value) > Integer.parseInt(BT_Value))
												{
													ValidGroupBalanceOffer.add(BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraPAM + ";" + TempbalanceInput.getBEBUCKETID());
													ValidGroupBT_ID.add(TempBalance_ID);
													break;
												}
												else if(Symbol.equals("=") && Integer.parseInt(TempBalance_Value) == Integer.parseInt(BT_Value))
												{
													ValidGroupBalanceOffer.add(BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraPAM + ";" + TempbalanceInput.getBEBUCKETID());
													ValidGroupBT_ID.add(TempBalance_ID);
													break;
												}
												else if(Symbol.equals("or"))
												{
													//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
													String[] values = BT_Value.split("#");											
													if(Arrays.stream(values).anyMatch(TempBalance_Value::equals))
													{
														ValidGroupBT_ID.add(TempBalance_ID);
														ValidGroupBalanceOffer.add(BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraPAM + ";" + TempbalanceInput.getBEBUCKETID());
														break;
													}																										
												}	
											}
											/*else
											{
												//System.out.println("Discarded Logs: " + id);
												onlyLog.add("INC4004:Balance_Type lookup failed in Product_Mapping:MSISDN=" + msisdn + ":BALANCE_TYPE_ID=" + TempBalance_ID + ":BE_BUCKET_VALUE=" + TempBalance_Value + ":ACTION=Logging");
											}*/
										}
									}
								}
								if(ExtraPAMFlag && FinalGroupName.startsWith("A-"))
								{
									ExtraPAMFlag = false;
									if(ValidGroupBalanceOffer.size() == CurrentGroupBalance.size())
									{
										String TargetPAM =  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[1];
										// Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + Balance_StartDate + ";" + Balance_ExpiryDate  + ";" + Product_Private
										String SourcePAM =  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[0];
										if(TargetPAM.split(";")[8].length() > 0)
										{
											String AddedPAM= TargetPAM.split(";")[8];
											if(!AddedPAM.isEmpty())
											{
												String ExpiryDay = TargetPAM.split(";")[5].split(" ")[0].split("-")[2];
												if(LoadSubscriberMapping.PAMMap.containsKey(ExpiryDay))
												{
													String PAMValue = LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[0];
													if(PAMValue.equals(AddedPAM))
													{
														Format formatter = new SimpleDateFormat("MMM");
													    String MonthName = formatter.format(new Date());
													    
													    LocalDate currentDate = LocalDate.now();
													    int doy = currentDate.getYear();
													    
													    Calendar c = Calendar.getInstance();
												        Date date = c.getTime();
												        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
												        String CurrentDate = dfDate.format(date);
													    
													    String Pam_Period = "";
													    if(LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[3].equals("DAILY"))
													    	Pam_Period = "Daily_" + String.valueOf(CurrentDate);
													    else 
													    	Pam_Period = "Monthly_" + String.valueOf(doy) + "_" + MonthName; 
													    
													    Long Last_Evaluation_Date = CommonUtilities.getCurrentPamPeriodInDays("Monthly");
														
														if(LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[3].equals("DAILY"))
															Last_Evaluation_Date = CommonUtilities.getCurrentPamPeriodInDays("Daily");
														else
															Last_Evaluation_Date =CommonUtilities.getCurrentPamPeriodInDays("Monthly");
														
													    StringBuffer sb = new StringBuffer();
														
														sb.append(msisdn).append(",");
														sb.append(LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[1]).append(",");
														sb.append(LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[0]).append(",");
														sb.append(LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[2]).append(",");
														sb.append(Pam_Period).append(",");
														sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO")).append(",");
														sb.append(Last_Evaluation_Date).append(",");
														sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
														
														defaultPAMList.add(sb.toString());
													}
													else
													{
														//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ CurrentGroupBalanceID.get(TargetOffer.split(";")[2] + "," + TargetOffer.split(";")[3])  +":ACTION=Logging");
														onlyLog.add("INC4008:BillCycle PC expiry not matching with PAM:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourcePAM.split(";")[2] + ":BE_BUCKET_VALUE=" + SourcePAM.split(";")[3] + ":BE_EXPIRY="+ TargetPAM.split(";")[5] +":ACTION=Logging");
													}
												}												
											}
										}										
									}									
									else
									{
										
										//ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).count()
										if(ValidGroupBalanceOffer.size() >= 2 && ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).count() >=1 && ValidGroupBalanceOffer.stream().filter(item->item.startsWith("M")).count() >=1){
											String TargetPAM =  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[1];
											// Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + Balance_StartDate + ";" + Balance_ExpiryDate  + ";" + Product_Private
											String SourcePAM =  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[0];
											
											if(TargetPAM.split(";")[8].length() > 0)
											{
												String AddedPAM= TargetPAM.split(";")[8];
												if(!AddedPAM.isEmpty())
												{
													String ExpiryDay = TargetPAM.split(";")[5].split(" ")[0].split("-")[2];
													if(LoadSubscriberMapping.PAMMap.containsKey(ExpiryDay))
													{
														String PAMValue = LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[0];
														if(PAMValue.equals(AddedPAM))
														{
															Format formatter = new SimpleDateFormat("MMM");
														    String MonthName = formatter.format(new Date());
														    
														    LocalDate currentDate = LocalDate.now();
														    int doy = currentDate.getYear();

														    Calendar c = Calendar.getInstance();
													        Date date = c.getTime();
													        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
													        String CurrentDate = dfDate.format(date);
														    
														    String Pam_Period = "";
														    if(LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[3].equals("DAILY"))
														    	Pam_Period = "Daily_" + String.valueOf(CurrentDate);
														    else 
														    	Pam_Period = "Monthly_" + String.valueOf(doy) + "_" + MonthName; 
														    
														    Long Last_Evaluation_Date = CommonUtilities.getCurrentPamPeriodInDays("Monthly");
															
															if(LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[3].equals("DAILY"))
																Last_Evaluation_Date = CommonUtilities.getCurrentPamPeriodInDays("Daily");
															else
																Last_Evaluation_Date =CommonUtilities.getCurrentPamPeriodInDays("Monthly");
															
														    
														    StringBuffer sb = new StringBuffer();
															
															sb.append(msisdn).append(",");
															sb.append(LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[1]).append(",");
															sb.append(LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[0]).append(",");
															sb.append(LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[2]).append(",");
															sb.append(Pam_Period).append(",");
															sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO")).append(",");
															sb.append(Last_Evaluation_Date).append(",");
															sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
															
															defaultPAMList.add(sb.toString());
														}
														else
														{
															//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ CurrentGroupBalanceID.get(TargetOffer.split(";")[2] + "," + TargetOffer.split(";")[3])  +":ACTION=Logging");
															onlyLog.add("INC4008:BillCycle PC expiry not matching with PAM:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourcePAM.split(";")[2] + ":BE_BUCKET_VALUE=" + SourcePAM.split(";")[3] + ":BE_EXPIRY="+ TargetPAM.split(";")[5] +":ACTION=Logging");
														}
													}	
												}
											}				
										}										
										else if(ValidGroupBalanceOffer.size() == 1)
										{
											//BT_Type + ";" +TempBalance_Name + ";" + TempBalance_ID + ";" + TempBalance_Value + "|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFalg + ";" + TempBalance_StartDate + ";" + TempBalance_ExpiryDate  + ";" + Product_Private
											if(ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).count() == 1 ){
												String TargetPAM =  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[1];
												String SourcePAM =  ValidGroupBalanceOffer.stream().filter(item->item.startsWith("P")).collect(Collectors.toList()).get(0).split("\\|")[0];
												
												if(TargetPAM.split(";")[8].length() > 0)
												{
													String AddedPAM= TargetPAM.split(";")[8];
													if(!AddedPAM.isEmpty())
													{
														String ExpiryDay = TargetPAM.split(";")[5].split(" ")[0].split("-")[2];
														if(LoadSubscriberMapping.PAMMap.containsKey(ExpiryDay))
														{
															String PAMValue = LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[0];
															if(PAMValue.equals(AddedPAM))
															{
																Format formatter = new SimpleDateFormat("MMM");
															    String MonthName = formatter.format(new Date());
															    
															    LocalDate currentDate = LocalDate.now();
															    int doy = currentDate.getYear();
															    

															    Calendar c = Calendar.getInstance();
														        Date date = c.getTime();
														        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
														        String CurrentDate = dfDate.format(date);
															    
															    String Pam_Period = "";
															    if(LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[3].equals("DAILY"))
															    	Pam_Period = "Daily_" + String.valueOf(CurrentDate);
															    else 
															    	Pam_Period = "Monthly_" + String.valueOf(doy) + "_" + MonthName; 
															    
															    Long Last_Evaluation_Date = CommonUtilities.getCurrentPamPeriodInDays("Monthly");
																
																if(LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[3].equals("DAILY"))
																	Last_Evaluation_Date = CommonUtilities.getCurrentPamPeriodInDays("Daily");
																else
																	Last_Evaluation_Date =CommonUtilities.getCurrentPamPeriodInDays("Monthly");
																
															    
															    StringBuffer sb = new StringBuffer();
																
																sb.append(msisdn).append(",");
																sb.append(LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[1]).append(",");
																sb.append(LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[0]).append(",");
																sb.append(LoadSubscriberMapping.PAMMap.get(ExpiryDay).split(";")[2]).append(",");
																sb.append(Pam_Period).append(",");
																sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO")).append(",");
																sb.append(Last_Evaluation_Date).append(",");
																sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
																
																defaultPAMList.add(sb.toString());
															}
															else
															{
																//onlyLog.add("INC4004:Balance_Type match condition failed:MSISDN=" + msisdn + ":BALANCE_TYPE=" + TargetOffer.split(";")[2] + ":BE_BUCKET_VALUE=" + TargetOffer.split(";")[3] + ":BE_BUCKET_ID="+ CurrentGroupBalanceID.get(TargetOffer.split(";")[2] + "," + TargetOffer.split(";")[3])  +":ACTION=Logging");
																onlyLog.add("INC4008:BillCycle PC expiry not matching with PAM:MSISDN=" + msisdn + ":BALANCE_TYPE=" + SourcePAM.split(";")[2] + ":BE_BUCKET_VALUE=" + SourcePAM.split(";")[3] + ":BE_EXPIRY="+ TargetPAM.split(";")[5] +":ACTION=Logging");
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
									//this else is only to handle the M and S group
									//if(LoadSubscriberMapping.UniqueBalanceOnlyAMGroupMap.stream().allMatch(t -> ValidGroupBT_ID.stream().anyMatch(t::contains))) {
									if(LoadSubscriberMapping.UniqueBalanceOnlyAMGroupMap.containsAll(ValidGroupBT_ID)) 
									{
										String EXPIRY_DATE = ""; 						     //ValidGroupBalanceOffer.stream().filter(item->item.startsWith("X")).collect(Collectors.toList()).get(0).split("\\|")[1].split(";")[5];
										List<String> BT_BUCKET_ID_LIST = new ArrayList<>();  //ValidGroupBalanceOffer.stream().filter(item->item.startsWith("X")).collect(Collectors.toList()).get(0).split("\\|")[1].split(";")[9];
									
										for(String item : ValidGroupBalanceOffer)
										{
											EXPIRY_DATE = item.split("\\|")[1].split(";")[5];
											BT_BUCKET_ID_LIST.add(item.split("\\|")[1].split(";")[9]);
										}
										
										BT_BUCKET_ID_LIST.forEach(item->CompletedBT_ID.remove(item));
										
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
												List<String> ValidAMGroupBalanceOffer = item.getValue();
												
												int i = 1;
												Set<String> CompletedAMBT = new HashSet<>();
												for(String Str : ValidAMGroupBalanceOffer)
												{
													String SourceOffer = Str.split("\\|")[0];
													String TargetOffer = Str.split("\\|")[1];
													CompletedBT_ID.add(TargetOffer.split(";")[9]);
													
												}													
											}
										}
										else
										{										
											for(String Str : ValidGroupBalanceOffer)
											{
												String TargetOffer = Str.split("\\|")[0];
												String SourceOffer = Str.split("\\|")[1];
												
												CompletedBT_ID.add(SourceOffer.split(";")[9]);
											}
										}
									}
								}														
							//need to put code till here
							}
							else
							{
								CompletedBT_ID.add(balanceInput.getBEBUCKETID());								
							}						
						}
					}				
				}
			}
		}
		return defaultPAMList;
	}

	private List<String> PAMFromDefaultService(String msisdn)
	{
		List<String> defaultPAMList = new ArrayList<>();
		
		/*for (String string : LoadSubscriberMapping.DefaultServicesMap.keySet()) 
		{
			
		}*/			
		LoadSubscriberMapping.DefaultServicesMap.forEach((k,v)->{
			//System.out.println("Item : " + k + " Count : " + v);
			//PAM_Class_ID	PAM_Service_ID	Schedule_ID	Priority
			//14	15	16	17

			if (v.split(",",-1)[1].trim().equals("N"))
			{
				//System.out.println(v);
				if (v.split(",",-1)[16] != "" && v.split(",",-1)[16].length() != 0)
				{
					String Pam_Class_ID = v.split(",",-1)[16];
					String Pam_Service_ID = v.split(",",-1)[17];
					String Schedule_ID = v.split(",",-1)[18];
					String Priority = v.split(",",-1)[19];
					String Current_PAM_Period = v.split(",",-1)[20];
					Format formatter = new SimpleDateFormat("MMM");
				    String MonthName = formatter.format(new Date());
				    			    
				    LocalDate currentDate = LocalDate.now();
				    int doy = currentDate.getYear();
				    
				    Calendar c = Calendar.getInstance();
			        Date date = c.getTime();
			        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
			        String CurrentDate = dfDate.format(date);
				    
				    String Pam_Period = "";
				    
				    if(Current_PAM_Period.toUpperCase().equals("DAILY"))
				    	Pam_Period = "Daily_" + String.valueOf(CurrentDate);
				    else 
				    	Pam_Period = "Monthly_" + String.valueOf(doy) + "_" + MonthName; 
					
					
					
					Long Last_Evaluation_Date ;
					
					if(Current_PAM_Period.toUpperCase().equals("DAILY"))
					{
						Last_Evaluation_Date = CommonUtilities.getCurrentPamPeriodInDays("Daily");
					}
					else
					{
						Last_Evaluation_Date =CommonUtilities.getCurrentPamPeriodInDays("Monthly");
					}
					
					StringBuffer sb = new StringBuffer();
					
					sb.append(msisdn).append(",");
					sb.append(Pam_Service_ID).append(",");
					sb.append(Pam_Class_ID).append(",");
					sb.append(Schedule_ID).append(",");
					sb.append(Pam_Period).append(",");
					sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO")).append(",");
					sb.append(Last_Evaluation_Date).append(",");
					sb.append(LoadSubscriberMapping.CommonConfigMap.get("default_ZERO"));
					
					defaultPAMList.add(sb.toString());
					
					sb = null;
				}
			}			
		});
		
		return defaultPAMList.stream().distinct().collect(Collectors.toList());
	}
	
	
	/*************Code Backup*****************/
	
	public Map<String,List<String>> ComputeAMGroup_Backup(Map<String, String> AMBalanceBT, Set<String> CompletedBT_ID) {
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
		
		/*if(!BestMatchFound && BestMatch.size() > 1)
			FinalGroupName = Collections.max(BestMatch.entrySet(), (entry1, entry2) -> entry1.getValue().size() - entry2.getValue().size()).getKey();*/
		
		List<String> GroupBalanceOffer = new ArrayList<>();
		Map<String,List<String>> AMGroupOfferMap = new HashMap<>();
		Set<String> AMCompletedBT = new HashSet<>();
		if(ComputedGroupName.length() != 0)
		{	
			boolean ExtraOfferFlag = false;
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
						String Product_Private = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getProductPrivate();
						String Offer_Flag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getOfferFlag();
						String Offer_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getOfferType();
						String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getBTTYPE();
						boolean startFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getOfferStartDate().length() > 0 ? true:false;
						boolean expiryFlag = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getOfferExpiryDate().length() > 0 ? true:false;
						String ExtraOffer = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(TempBalance_ID + "|" + ComputedGroupName).getAddOffer();
						if(!ExtraOffer.isEmpty())
						{
							ExtraOfferFlag = true;
						}
						else
						{
							ExtraOffer = "";
						}
						
						if(Offer_Flag.isEmpty())
						{
							Offer_Flag = LoadSubscriberMapping.CommonConfigMap.get("default_NULL");
						}
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						AMCompletedBT.add(TempBalance_ID);
						GroupBalanceOffer.add(BT_Type + ";" + balanceInput.getBALANCETYPENAME() + ";" + TempBalance_ID + ";" + balanceInput.getBEBUCKETVALUE() +"|" + Offer_ID + ";" + Offer_Type + ";" + startFlag + ";" + expiryFlag + ";" + balanceInput.getBEBUCKETSTARTDATE() + ";" + balanceInput.getBEEXPIRY()  + ";" + Product_Private + ";" + Offer_Flag + ";" + ExtraOffer + ";" + balanceInput.getBEBUCKETID());
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
}
