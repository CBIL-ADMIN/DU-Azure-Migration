package com.ericsson.dm.transform.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.ericsson.dm.Utils.CommonUtilities;
import com.ericsson.dm.inititialization.LoadSubscriberMapping;
import com.ericsson.jibx.beans.SubscriberXml;
import com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo;

public class CommonFunctions implements Comparator<SchemasubscriberbalancesdumpInfo> {
	SubscriberXml subscriber;
	Map<String, Set<String>> ProductIDLookUpMap;
	Set<String> onlyLog;
	
	public CopyOnWriteArrayList<SchemasubscriberbalancesdumpInfo> SortedBalanceInput;
	
	public CommonFunctions(SubscriberXml subscriber,  Map<String, Set<String>> ProductIDLookUpMap, Set<String> onlyLog)
	{
		this.subscriber = subscriber;
		SortedBalanceInput = new CopyOnWriteArrayList<>();		
		SortedBalanceInput.addAll(subscriber.getBalancesdumpInfoList());	
		this.ProductIDLookUpMap = ProductIDLookUpMap;
		this.onlyLog = onlyLog;
		Collections.sort(SortedBalanceInput,new Offer());
	}
	
	public CommonFunctions() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int compare(SchemasubscriberbalancesdumpInfo o1, SchemasubscriberbalancesdumpInfo o2) {
		int value1 = o2.getBEEXPIRY().compareTo(o1.getBEEXPIRY());
        if (value1 == 0) {
        	return o1.getBEBUCKETID().compareTo(o2.getBEBUCKETID());
        }
        return value1;
	}
	
	public String ComputeAGroup_New(String inputBalance_ID, String inputGroupName, Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub
		String FinalGroupName ="";
		
		List<String>AllAvailableGroup = new ArrayList<>();
		
		for(Set<String> valueList : LoadSubscriberMapping.BalanceGroupingMap.values()) {					
			if(valueList.contains(inputBalance_ID)){
				AllAvailableGroup.add(getKey(LoadSubscriberMapping.BalanceGroupingMap, valueList));	
			}
		}
		
		Map<String,Set<String>> BestMatch = new ConcurrentHashMap<>(1000, 0.75f, 30);
		for(String A_ID: AllAvailableGroup)
		{
			if(LoadSubscriberMapping.BalanceOnlyAGroupMap.containsKey(A_ID))
			{
				Set<String> A_Items = Arrays.stream(LoadSubscriberMapping.BalanceOnlyAGroupMap.get(A_ID).split(",")).collect(Collectors.toSet());
				int i =0;
				Set<String> A_currentGroup = new HashSet<>();
				for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
				{	
					if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
						continue;
					
					if(A_Items.contains(balanceInput.getBALANCETYPE()))
					{
						if(LoadSubscriberMapping.OnlyVBalancesValueSet.contains(balanceInput.getBEBUCKETID()))
						{
							if(LoadSubscriberMapping.OnlyVBalancesValueGroupMap.containsKey(A_ID))
							{
								String BTID = LoadSubscriberMapping.OnlyVBalancesValueGroupMap.get(A_ID).split("\\|")[0];
								List<String> BTVALUE = Arrays.asList(LoadSubscriberMapping.OnlyVBalancesValueGroupMap.get(A_ID).split("\\|")[1].split("#"));
								if(BTVALUE.contains(balanceInput.getBEBUCKETVALUE()))
								{
									i++;
									FinalGroupName = A_ID;
									A_currentGroup.add(balanceInput.getBALANCETYPE());
									continue;
								}
							}
						}
						else
						{							
							i++;
							FinalGroupName = A_ID;
							A_currentGroup.add(balanceInput.getBALANCETYPE());
							continue;
						}
					}
					/*if(A_Items.size() == i)
					{
						FinalGroupName = A_ID;
						break;
					}*/						
				}
				if(A_currentGroup.size() == A_Items.size() && A_Items.containsAll(A_currentGroup))
					return FinalGroupName;
				else
					BestMatch.put(FinalGroupName, A_currentGroup);
			}
		}
		return Collections.max(BestMatch.entrySet(), (entry1, entry2) -> entry1.getValue().size() - entry2.getValue().size()).getKey();
	}
	
	public String ComputeAGroup(String inputBalance_ID, String inputGroupName, Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub
		String FinalGroupName ="";
		
		List<String>AllAvailableGroup = new ArrayList<>();
		
		for(Set<String> valueList : LoadSubscriberMapping.BalanceGroupingMap.values()) {					
			if(valueList.contains(inputBalance_ID)){
				AllAvailableGroup.add(getKey(LoadSubscriberMapping.BalanceGroupingMap, valueList));	
			}
		}
		
		Map<String,Set<String>> BestMatch = new ConcurrentHashMap<>(1000, 0.75f, 30);
		for(String A_ID: AllAvailableGroup)
		{
			if(LoadSubscriberMapping.BalanceOnlyAGroupMap.containsKey(A_ID))
			{
				Set<String> A_Items = Arrays.stream(LoadSubscriberMapping.BalanceOnlyAGroupMap.get(A_ID).split(",")).collect(Collectors.toSet());
				int i =0;
				Set<String> A_currentGroup = new HashSet<>();
				for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
				{	
					if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
						continue;
					if(A_Items.contains(balanceInput.getBALANCETYPE()))
					{
						i++;
						FinalGroupName = A_ID;
						A_currentGroup.add(balanceInput.getBALANCETYPE());
						continue;
					}
					/*if(A_Items.size() == i)
					{
						FinalGroupName = A_ID;
						break;
					}*/						
				}
				if(A_currentGroup.size() == A_Items.size() && A_Items.containsAll(A_currentGroup))
					return FinalGroupName;
				else
					BestMatch.put(FinalGroupName, A_currentGroup);
			}
		}
		return Collections.max(BestMatch.entrySet(), (entry1, entry2) -> entry1.getValue().size() - entry2.getValue().size()).getKey();
	}
	
	public String ComputeCGroup(String inputBalance_ID, String inputGroupName , Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub
		String FinalGroupName ="";
		
		List<String>AllAvailableGroup = new ArrayList<>();
		
		for(Set<String> valueList : LoadSubscriberMapping.BalanceGroupingMap.values()) {					
			if(valueList.contains(inputBalance_ID)){
				AllAvailableGroup.add(getKey(LoadSubscriberMapping.BalanceGroupingMap, valueList));	
			}
		}
		
		Map<String,Set<String>> BestMatch = new ConcurrentHashMap<>(1000, 0.75f, 30);
		for(String A_ID: AllAvailableGroup)
		{
			if(LoadSubscriberMapping.BalanceOnlyCGroupMap.containsKey(A_ID))
			{
				Set<String> A_Items = Arrays.stream(LoadSubscriberMapping.BalanceOnlyCGroupMap.get(A_ID).split(",")).collect(Collectors.toSet());
				int i =0;
				Set<String> A_currentGroup = new HashSet<>();
				for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
				{	
					if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
						continue;
					if(A_Items.contains(balanceInput.getBALANCETYPE()))
					{
						i++;
						FinalGroupName = A_ID;
						A_currentGroup.add(balanceInput.getBALANCETYPE());
						continue;
					}
					/*if(A_Items.size() == i)
					{
						FinalGroupName = A_ID;
						break;
					}*/						
				}
				if(A_currentGroup.size() == A_Items.size() && A_Items.containsAll(A_currentGroup))
					return FinalGroupName;
				else
					BestMatch.put(FinalGroupName, A_currentGroup);
			}
		}
		return Collections.max(BestMatch.entrySet(), (entry1, entry2) -> entry1.getValue().size() - entry2.getValue().size()).getKey();

	}
	
	public String ComputeFGroup(String inputBalance_ID, String inputGroupName , Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub
		String FinalGroupName ="";
		
		List<String>AllAvailableGroup = new ArrayList<>();
		
		for(Set<String> valueList : LoadSubscriberMapping.BalanceGroupingMap.values()) {					
			if(valueList.contains(inputBalance_ID)){
				AllAvailableGroup.add(getKey(LoadSubscriberMapping.BalanceGroupingMap, valueList));	
			}
		}
		
		Map<String,Set<String>> BestMatch = new ConcurrentHashMap<>(1000, 0.75f, 30);
		for(String A_ID: AllAvailableGroup)
		{
			if(LoadSubscriberMapping.BalanceOnlyFGroupMap.containsKey(A_ID))
			{
				Set<String> A_Items = Arrays.stream(LoadSubscriberMapping.BalanceOnlyFGroupMap.get(A_ID).split(",")).collect(Collectors.toSet());
				int i =0;
				Set<String> A_currentGroup = new HashSet<>();
				for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
				{
					if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
						continue;
					if(A_Items.contains(balanceInput.getBALANCETYPE()))
					{
						i++;
						FinalGroupName = A_ID;
						A_currentGroup.add(balanceInput.getBALANCETYPE());
						continue;
					}
					/*if(A_Items.size() == i)
					{
						FinalGroupName = A_ID;
						break;
					}*/						
				}
				if(A_currentGroup.size() == A_Items.size() && A_Items.containsAll(A_currentGroup))
					return FinalGroupName;
				else
					BestMatch.put(FinalGroupName, A_currentGroup);
			}
		}
		return Collections.max(BestMatch.entrySet(), (entry1, entry2) -> entry1.getValue().size() - entry2.getValue().size()).getKey();
	}
	
	public String ComputeHGroup(String inputBalance_ID, String inputGroupName , Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub
		String FinalGroupName ="";
		
		List<String>AllAvailableGroup = new ArrayList<>();
		
		for(Set<String> valueList : LoadSubscriberMapping.BalanceGroupingMap.values()) {					
			if(valueList.contains(inputBalance_ID)){
				AllAvailableGroup.add(getKey(LoadSubscriberMapping.BalanceGroupingMap, valueList));	
			}
		}
		
		Map<String,Set<String>> BestMatch = new ConcurrentHashMap<>(1000, 0.75f, 30);
		for(String A_ID: AllAvailableGroup)
		{
			if(LoadSubscriberMapping.BalanceOnlyHGroupMap.containsKey(A_ID))
			{
				Set<String> A_Items = Arrays.stream(LoadSubscriberMapping.BalanceOnlyHGroupMap.get(A_ID).split(",")).collect(Collectors.toSet());
				int i =0;
				Set<String> A_currentGroup = new HashSet<>();
				for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
				{
					if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
						continue;
					if(A_Items.contains(balanceInput.getBALANCETYPE()))
					{
						i++;
						FinalGroupName = A_ID;
						A_currentGroup.add(balanceInput.getBALANCETYPE());
						continue;
					}
					/*if(A_Items.size() == i)
					{
						FinalGroupName = A_ID;
						break;
					}*/						
				}
				if(A_currentGroup.size() == A_Items.size() && A_Items.containsAll(A_currentGroup))
					return FinalGroupName;
				else
					BestMatch.put(FinalGroupName, A_currentGroup);
			}
		}
		return Collections.max(BestMatch.entrySet(), (entry1, entry2) -> entry1.getValue().size() - entry2.getValue().size()).getKey();
	}
	
	public String ComputeBGroup(String inputBalance_ID, String inputGroupName , Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub
		String FinalGroupName ="";
		
		List<String>AllAvailableGroup = new ArrayList<>();
		
		for(Set<String> valueList : LoadSubscriberMapping.BalanceGroupingMap.values()) {					
			if(valueList.contains(inputBalance_ID)){
				AllAvailableGroup.add(getKey(LoadSubscriberMapping.BalanceGroupingMap, valueList));	
			}
		}
		
		Map<String,Set<String>> BestMatch = new ConcurrentHashMap<>(1000, 0.75f, 30);
		for(String A_ID: AllAvailableGroup)
		{
			if(LoadSubscriberMapping.BalanceOnlyBGroupMap.containsKey(A_ID))
			{
				Set<String> A_Items = Arrays.stream(LoadSubscriberMapping.BalanceOnlyBGroupMap.get(A_ID).split(",")).collect(Collectors.toSet());
				int i =0;
				Set<String> A_currentGroup = new HashSet<>();
				for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
				{
					if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
						continue;
					if(A_Items.contains(balanceInput.getBALANCETYPE()))
					{
						i++;
						FinalGroupName = A_ID;
						A_currentGroup.add(balanceInput.getBALANCETYPE());
						continue;
					}
					/*if(A_Items.size() == i)
					{
						FinalGroupName = A_ID;
						break;
					}*/						
				}
				if(A_currentGroup.size() == A_Items.size() && A_Items.containsAll(A_currentGroup))
					return FinalGroupName;
				else
					BestMatch.put(FinalGroupName, A_currentGroup);
			}
		}
		return Collections.max(BestMatch.entrySet(), (entry1, entry2) -> entry1.getValue().size() - entry2.getValue().size()).getKey();
	}
	
	public String ComputeDGroup(String inputBalance_ID, String inputGroupName , Set<String> CompletedBT_ID) {
		// TODO Auto-generated method stub
		String FinalGroupName ="";
		
		List<String>AllAvailableGroup = new ArrayList<>();
		
		for(Set<String> valueList : LoadSubscriberMapping.BalanceGroupingMap.values()) {					
			if(valueList.contains(inputBalance_ID)){
				AllAvailableGroup.add(getKey(LoadSubscriberMapping.BalanceGroupingMap, valueList));	
			}
		}
		
		Map<String,Set<String>> BestMatch = new ConcurrentHashMap<>(1000, 0.75f, 30);
		for(String A_ID: AllAvailableGroup)
		{
			if(LoadSubscriberMapping.BalanceOnlyDGroupMap.containsKey(A_ID))
			{
				Set<String> A_Items = Arrays.stream(LoadSubscriberMapping.BalanceOnlyDGroupMap.get(A_ID).split(",")).collect(Collectors.toSet());
				int i =0;
				Set<String> A_currentGroup = new HashSet<>();
				for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
				{
					if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
						continue;
					if(A_Items.contains(balanceInput.getBALANCETYPE()))
					{
						i++;
						FinalGroupName = A_ID;
						A_currentGroup.add(balanceInput.getBALANCETYPE());
						continue;
					}
					/*if(A_Items.size() == i)
					{
						FinalGroupName = A_ID;
						break;
					}	*/					
				}
				if(A_currentGroup.size() == A_Items.size() && A_Items.containsAll(A_currentGroup))
					return FinalGroupName;
				else
					BestMatch.put(FinalGroupName, A_currentGroup);
			}
		}
		return Collections.max(BestMatch.entrySet(), (entry1, entry2) -> entry1.getValue().size() - entry2.getValue().size()).getKey();
	}
	
	public <K, V> K getKey(Map<K, V> map, V value) {
		return map.keySet()
						.stream()
						.filter(key -> value.equals(map.get(key)))
						.findFirst().get();
	}
	
	public List<String> getGraceGroupKey(Map<String, Set<String>> map, String value) {
		return map.entrySet()
				.stream()
				.filter(e-> e.getValue().equals(value))
				.map(Map.Entry::getKey)
		        .collect(Collectors.toList());		
	}
	
	public List<String> getAMGroupKey(Map<String, String> map, Set<Integer> InputList) {
		List<String> MGroupList = new ArrayList<>();
  		String MGroupItem = "";
		
		for(Map.Entry<String, String> entry : map.entrySet())
		{
			MGroupItem =  entry.getValue();
			Set<Integer> MappingRefList = new TreeSet<>();
			List<String> list = Arrays.asList(MGroupItem.split(","));
			Set<Integer> set = list.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toSet());
			MappingRefList.addAll(set);
			
			if(InputList.equals(MappingRefList))
				MGroupList.add(entry.getKey());
			
		}
		return MGroupList;
	}
	
	public String getASGroupKey(Map<String, String> map, String inputBalance_ID) {
		// TODO Auto-generated method stub
		return map.entrySet()
				.stream()
				.filter(e-> e.getValue().contains(inputBalance_ID))
				.map(Map.Entry::getKey)
		        .collect(Collectors.joining(","));
	}
	
	public List<String> getASpecialGroupKey(Map<String, Set<String>> map, String inputBalance_ID) {
		// TODO Auto-generated method stub
		
		return map.entrySet()
				.stream()
				.filter(e-> e.getValue().contains(inputBalance_ID))
				.map(Map.Entry::getKey)
		        .collect(Collectors.toList());
	}
	
	public String GetProductIDCreation(Set<String> BEBucketID) {
		// TODO Auto-generated method stub
		String ProductID = "";
		if(BEBucketID.size() == 0)
			return "";
		for(Map.Entry<String, Set<String>> entry : ProductIDLookUpMap.entrySet())
		{
			Set<String> MappingRefList = new TreeSet<>(entry.getValue());
			if(MappingRefList.containsAll(BEBucketID))
				ProductID = entry.getKey();
		}
		return ProductID;
	}
	
	public boolean FindAGroupPCitemMultipleGroup(String GroupName)
	{
		boolean multipleGroupName = false;
		
		if(LoadSubscriberMapping.PBTinMultipleGroup.entrySet().stream().filter(e-> e.getValue().contains(GroupName)).findFirst().isPresent())
		{
			multipleGroupName = true;
		}
		
		return multipleGroupName;
	}
	
	public String ComputeAGroupPCitemMultipleGroup_new(String groupName, Set<String> CompletedBT_ID) {
		
		String VBTGroupName = "";
		Map<String,Set<String>> BestMatch = new ConcurrentHashMap<>(1000, 0.75f, 30);
		for(Map.Entry<String, Set<String>> entry : LoadSubscriberMapping.PBTinMultipleGroup.entrySet())	
		{
			//List<String> SpecialGroupList = new ArrayList<>();
			//SpecialGroupList.addAll();
			if(entry.getValue().contains(groupName))
			{	
				for(String individualGroup : entry.getValue())
				{
					//String FinalGroupName = "";
					Set<String> A_currentGroup = new HashSet<>();
					for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput){
						String BT_ID = balanceInput.getBALANCETYPE();
						String BT_BALANCE = balanceInput.getBEBUCKETVALUE();
						if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
							continue;
						
						if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + individualGroup) != null)
						{							
							String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + individualGroup).getSymbols();
							String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + individualGroup).getBTValue();
							String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + individualGroup).getBTTYPE();
							if(Symbol.equals(">=") && Integer.parseInt(BT_BALANCE) >= Integer.parseInt(BT_Value))
							{
								//FinalGroupName = individualGroup;
								if(BT_Type.equals("V"))
									VBTGroupName = individualGroup;
								A_currentGroup.add(BT_ID);
								continue;
							}
							else if(Symbol.equals(">") && Integer.parseInt(BT_BALANCE) > Integer.parseInt(BT_Value))
							{
								//FinalGroupName = individualGroup;
								if(BT_Type.equals("V"))
									VBTGroupName = individualGroup;
								A_currentGroup.add(BT_ID);
								continue;
							}
							else if(Symbol.equals("=") && Integer.parseInt(BT_BALANCE) == Integer.parseInt(BT_Value))
							{
								//FinalGroupName = individualGroup;
								if(BT_Type.equals("V"))
									VBTGroupName = individualGroup;
								A_currentGroup.add(BT_ID);
								continue;
							}
							else if(Symbol.equals("or"))
							{
								//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
								String[] values = BT_Value.split("#");											
								if(Arrays.stream(values).anyMatch(BT_BALANCE::equals))
								{
									//FinalGroupName = individualGroup;
									if(BT_Type.equals("V"))
										VBTGroupName = individualGroup;
									A_currentGroup.add(BT_ID);
									continue;
								}																					
							}
						}
					}
					if(A_currentGroup.size() == LoadSubscriberMapping.BalanceGroupingMap.get(individualGroup).size())
					{
						return individualGroup;
					}
					else
						BestMatch.put(individualGroup, A_currentGroup);
					/*else if(!VBTGroupName.isEmpty())
						return VBTGroupName;*/
				}
			}
		}
		
		return Collections.max(BestMatch.entrySet(), (entry1, entry2) -> entry1.getValue().size() - entry2.getValue().size()).getKey();
	}
	
	public String ComputeAGroupPCitemMultipleGroup(String InputBT_ID,String groupName, Set<String> CompletedBT_ID) {
		
		String VBTGroupName = "";
		if(LoadSubscriberMapping.OnlyVBalancesValueSet.contains(InputBT_ID))
		{
			for(Map.Entry<String, Set<String>> entry : LoadSubscriberMapping.PBTinMultipleGroup.entrySet())	
			{
				//List<String> SpecialGroupList = new ArrayList<>();
				//SpecialGroupList.addAll();
				if(entry.getValue().contains(groupName))
				{	
					for(String individualGroup : entry.getValue())
					{
						//String FinalGroupName = "";
						Set<String> A_currentGroup = new HashSet<>();
						for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput){
							String BT_ID = balanceInput.getBALANCETYPE();
							String BT_BALANCE = balanceInput.getBEBUCKETVALUE();
							if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
								continue;
							
							if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + individualGroup) != null)
							{							
								String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + individualGroup).getSymbols();
								String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + individualGroup).getBTValue();
								String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + individualGroup).getBTTYPE();
								if(Symbol.equals(">=") && Integer.parseInt(BT_BALANCE) >= Integer.parseInt(BT_Value))
								{
									//FinalGroupName = individualGroup;
									if(BT_Type.equals("V"))
										VBTGroupName = individualGroup;
									A_currentGroup.add(BT_ID);
									continue;
								}
								else if(Symbol.equals(">") && Integer.parseInt(BT_BALANCE) > Integer.parseInt(BT_Value))
								{
									//FinalGroupName = individualGroup;
									if(BT_Type.equals("V"))
										VBTGroupName = individualGroup;
									A_currentGroup.add(BT_ID);
									continue;
								}
								else if(Symbol.equals("=") && Integer.parseInt(BT_BALANCE) == Integer.parseInt(BT_Value))
								{
									//FinalGroupName = individualGroup;
									if(BT_Type.equals("V"))
										VBTGroupName = individualGroup;
									A_currentGroup.add(BT_ID);
									continue;
								}
								else if(Symbol.equals("or"))
								{
									//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
									String[] values = BT_Value.split("#");											
									if(Arrays.stream(values).anyMatch(BT_BALANCE::equals))
									{
										//FinalGroupName = individualGroup;
										if(BT_Type.equals("V"))
											VBTGroupName = individualGroup;
										A_currentGroup.add(BT_ID);
										continue;
									}																					
								}
							}
						}
						if(A_currentGroup.size() == LoadSubscriberMapping.BalanceGroupingMap.get(individualGroup).size())
						{
							return individualGroup;
						}
						else if(!VBTGroupName.isEmpty())
							return VBTGroupName;
					}
				}
			}
		}
		else
		{
			for(Map.Entry<String, Set<String>> entry : LoadSubscriberMapping.PBTinMultipleGroup.entrySet())	
			{
				//List<String> SpecialGroupList = new ArrayList<>();
				//SpecialGroupList.addAll();
				if(entry.getValue().contains(groupName))
				{	
					for(String individualGroup : entry.getValue())
					{
						//String FinalGroupName = "";
						Set<String> A_currentGroup = new HashSet<>();
						for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput){
							String BT_ID = balanceInput.getBALANCETYPE();
							String BT_BALANCE = balanceInput.getBEBUCKETVALUE();
							if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
								continue;
							
							if(LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + individualGroup) != null)
							{							
								String Symbol = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + individualGroup).getSymbols();
								String BT_Value = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + individualGroup).getBTValue();
								String BT_Type = LoadSubscriberMapping.BalanceNonEmptyBTGroupIdentifierMap.get(BT_ID + "|" + individualGroup).getBTTYPE();
								if(Symbol.equals(">=") && Integer.parseInt(BT_BALANCE) >= Integer.parseInt(BT_Value))
								{
									//FinalGroupName = individualGroup;
									if(BT_Type.equals("V"))
										VBTGroupName = individualGroup;
									A_currentGroup.add(BT_ID);
									continue;
								}
								else if(Symbol.equals(">") && Integer.parseInt(BT_BALANCE) > Integer.parseInt(BT_Value))
								{
									//FinalGroupName = individualGroup;
									if(BT_Type.equals("V"))
										VBTGroupName = individualGroup;
									A_currentGroup.add(BT_ID);
									continue;
								}
								else if(Symbol.equals("=") && Integer.parseInt(BT_BALANCE) == Integer.parseInt(BT_Value))
								{
									//FinalGroupName = individualGroup;
									if(BT_Type.equals("V"))
										VBTGroupName = individualGroup;
									A_currentGroup.add(BT_ID);
									continue;
								}
								else if(Symbol.equals("or"))
								{
									//Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value)
									String[] values = BT_Value.split("#");											
									if(Arrays.stream(values).anyMatch(BT_BALANCE::equals))
									{
										//FinalGroupName = individualGroup;
										if(BT_Type.equals("V"))
											VBTGroupName = individualGroup;
										A_currentGroup.add(BT_ID);
										continue;
									}																					
								}
							}
						}
						if(A_currentGroup.size() == LoadSubscriberMapping.BalanceGroupingMap.get(individualGroup).size())
						{
							return individualGroup;
						}
						/*else if(!VBTGroupName.isEmpty())
							return VBTGroupName;*/
					}
				}
			}
		}
		return groupName;
	}

	public String FindNPPPBTItem(String CCS_ACT, String State)
	{
		String BTExpiryDate = "";
		String BTStartDate = "";
		String BTValue = "";
		String MSISDN = "";
		//for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
		
		for(String nppKey : LoadSubscriberMapping.NPPLifeCycleMap.keySet())
		{
			Set<String> nppBTValueMatched = new HashSet<>();
			com.ericsson.jibx.beans.NPPLIFECYCLEMAPPINGLIST.NPPLIFECYCLEMAPPINGINFO nppValue = LoadSubscriberMapping.NPPLifeCycleMap.get(nppKey);
			
			if(nppValue.getCCSACCTTYPEID().equals(CCS_ACT) && nppValue.getState().equals(State))
			{
				Set<String> CompletedBT_ID = new HashSet<>();
				List<String> PBT_Details = Arrays.asList(nppValue.getPBTID().split("#"));
				Map<String,String> PBT_Map = new HashMap();
				
				for(String s : PBT_Details)
					PBT_Map.put(s.split("-")[0],s);
				
				for(com.ericsson.jibx.beans.SubscriberXml.SchemasubscriberbalancesdumpInfo balanceInput : SortedBalanceInput)
				{
					String Balance_ID = balanceInput.getBALANCETYPE();
					MSISDN = balanceInput.getMSISDN();
					String Balance_Value = balanceInput.getBEBUCKETVALUE();
					String Balance_ExpiryDate = balanceInput.getBEEXPIRY();
					String Balance_StartDate = balanceInput.getBEBUCKETSTARTDATE();
					
					if(CompletedBT_ID.contains(balanceInput.getBEBUCKETID()))
						continue;
					
					/*if((LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID) != null) && LoadSubscriberMapping.ProductMappingIgnoreFlag.get(Balance_ID).equals("Y"))
					{
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						continue;
					}*/
					if(!Balance_ExpiryDate.equals(LoadSubscriberMapping.CommonConfigMap.get("Lifetime_validity_value").toString()) && CommonUtilities.convertDateToEpoch(Balance_ExpiryDate) < CommonUtilities.convertDateToEpoch(LoadSubscriberMapping.CommonConfigMap.get("migration_date").toString()))
					{
						CompletedBT_ID.add(balanceInput.getBEBUCKETID());
						continue;
					}
					if(PBT_Map.containsKey(Balance_ID))
					{
						//this code is to populate the logs if the condition is not matching 
						Set<String> Mappingbalances = new HashSet<>();
						Mappingbalances = LoadSubscriberMapping.NPPLifeCycleBTIDDetails.get(Balance_ID);
						
						if(Mappingbalances.size() > 0)
						{
							boolean MatchConditionFailed = false;
							for(String s : Mappingbalances)
							{
								String Symbol = s.split("\\|")[0];
								String Value = s.split("\\|")[1];
								boolean ValidValue = false;
								if(Symbol.equals("=") && Long.parseLong(Value) == Long.parseLong(Balance_Value))
									ValidValue = true;
								if(Symbol.equals(">=") && Long.parseLong(Value) >= Long.parseLong(Balance_Value))
									ValidValue = true;
								
								if(!ValidValue)
								{
									//INC1006	NPP_Lifecycle match condition failed	MSISDN,CCS_ACCT_TYPE_ID,SERVICE_STATE,BALANCE_TYPE,BE_BUCKET_VALUE,BE_BUCKET_ID	
									onlyLog.add("INC1006:NPP_Lifecycle match condition failed:MSISDN=" + MSISDN + ":BALANCE_TYPE=" + Balance_ID + ":BE_BUCKET_VALUE=" + Balance_Value + ":BE_BUCKET_ID=" + balanceInput.getBEBUCKETID() +":ACTION=Logging");
									MatchConditionFailed = true;
									break;
								}
							}
							
							if(MatchConditionFailed)
								continue;
						}
						
						String Symbol = PBT_Map.get(Balance_ID).split("-")[1];
						String BT_Value = PBT_Map.get(Balance_ID).split("-")[2];
						if(Symbol.equals(">=") && Integer.parseInt(Balance_Value) >= Integer.parseInt(BT_Value))
						{
							nppBTValueMatched.add(Balance_ExpiryDate);
							if(Balance_ID.equals(nppValue.getSUPEXPIRYDate()))
							{
								BTStartDate = Balance_StartDate;
								BTExpiryDate = Balance_ExpiryDate;
							}
						}
						else if(Symbol.equals(">") && Integer.parseInt(Balance_Value) > Integer.parseInt(BT_Value))
						{
							nppBTValueMatched.add(Balance_ExpiryDate);
							if(Balance_ID.equals(nppValue.getSUPEXPIRYDate()))
							{
								BTStartDate = Balance_StartDate;
								BTExpiryDate = Balance_ExpiryDate;
							}
						}
						else if(Symbol.equals("=") && Integer.parseInt(Balance_Value) == Integer.parseInt(BT_Value))
						{
							nppBTValueMatched.add(Balance_ExpiryDate);
							if(Balance_ID.equals(nppValue.getSUPEXPIRYDate()))
							{
								BTStartDate = Balance_StartDate;
								BTExpiryDate = Balance_ExpiryDate;
								BTValue = Balance_Value;
							}
						}
					}
				}
				if(PBT_Details.size() == nppBTValueMatched.size())
				{
					return nppKey + "|" + BTExpiryDate + "|" + BTStartDate + "|" + BTValue;
				}	
			}
		}
		if(BTValue.isEmpty() && BTStartDate.isEmpty() && BTExpiryDate.isEmpty())
		{
			//INC1005		MSISDN,CCS_ACCT_TYPE_ID,SERVICE_STATE
			onlyLog.add("INC1005:NPP_Lifecycle PBT_ID not found:MSISDN=" + MSISDN + ":CCS_ACCT_TYPE_ID=" + CCS_ACT + ":SERVICE_STATE=" + State + ":ACTION=Logging");
		}
		
		return "0|" +BTExpiryDate + "|" + BTStartDate + "|" + BTValue;
	}
}
