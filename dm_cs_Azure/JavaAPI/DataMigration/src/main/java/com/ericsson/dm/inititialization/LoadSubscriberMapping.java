package com.ericsson.dm.inititialization;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.joda.time.LocalDate;

import com.ericsson.jibx.beans.BALANCEMAPPINGLIST;
import com.ericsson.jibx.beans.BALANCEMAPPINGLIST.BALANCEMAPPINGINFO;
import com.ericsson.jibx.beans.CISNWPCLIST;
import com.ericsson.jibx.beans.CISNWPCLIST.CISNWPCINFO;
import com.ericsson.jibx.beans.CISONCEOFFLIST;
import com.ericsson.jibx.beans.CISONCEOFFLIST.CISONCEOFFINFO;
import com.ericsson.jibx.beans.CISPARKINGLIST;
import com.ericsson.jibx.beans.CISPARKINGLIST.CISPARKINGINFO;
import com.ericsson.jibx.beans.CISRENEWALLIST;
import com.ericsson.jibx.beans.CISRENEWALLIST.CISRENEWALINFO;
import com.ericsson.jibx.beans.COMMUNITYMAPPINGLIST;
import com.ericsson.jibx.beans.COMMUNITYMAPPINGLIST.COMMUNITYMAPPINGINFO;
import com.ericsson.jibx.beans.NPPLIFECYCLEMAPPINGLIST;
import com.ericsson.jibx.beans.NPPLIFECYCLEMAPPINGLIST.NPPLIFECYCLEMAPPINGINFO;
import com.ericsson.jibx.beans.PROFILETAGLIST;
import com.ericsson.jibx.beans.PROFILETAGLIST.PROFILETAGINFO;

public class LoadSubscriberMapping {
	
	public static final Map<String, String> LanguageMap = new ConcurrentHashMap<>(50, 0.75f, 30);
	public static final Map<String, String> CommonConfigMap = new ConcurrentHashMap<>(100, 0.75f, 30);
	public static final Map<String, String> CommonFactorMap = new ConcurrentHashMap<>(100, 0.75f, 30);
	public static final Map<String, String> ConversionLogicMap = new ConcurrentHashMap<>(100, 0.75f, 30);
	public static final Map<String, String> LoggingMap = new ConcurrentHashMap<>(50, 0.75f, 30);
	public static final Map<String, String> OutputFilesMap = new ConcurrentHashMap<>(50, 0.75f, 30);
	public static final Map<String, String> sdp_ftp_ranges = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Map<String, String> TBAMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Map<String, String> sdp_distribution = new ConcurrentHashMap<>(100, 0.75f, 30);	
	public static final Map<String, String> ServiceClassMap = new ConcurrentHashMap<>(100, 0.75f, 30);
	public static final Map<String, String> PAMMap = new ConcurrentHashMap<>(100, 0.75f, 30);
	public static final Map<String, String> OfferAttrDefMap = new ConcurrentHashMap<>(100, 0.75f, 30);
	
	public static final Map<String, PROFILETAGINFO> Profile_Tags_Mapping = new ConcurrentHashMap<>(10000, 0.75f, 100);
	public static final Set<String> ProfileTagName = new HashSet<>();
	public static Set<String> ProfileInputList = new HashSet<>();
	public static final Map<String, PROFILETAGINFO> Profile_Tags_MappingWithGroup = new ConcurrentHashMap<>(10000, 0.75f, 100);
	
	public static final Map<String, Set<String>> ProfileGroupingMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Map<String, PROFILETAGINFO> ProfileSpecialGroup = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Set<String> ProfileBalancesID = new HashSet<>();
	public static final Set<String> AMasterBTValues = new HashSet<>();
	public static final Set<String> UniqueBalanceOnlyAMGroupMap = new HashSet<>();
	public static final Map<String, String> ProfileBalancesIDDetails = new ConcurrentHashMap<>(10000, 0.75f, 100);
	//public static final Map<String, String> OfferProductID = new ConcurrentHashMap<>(10000, 0.75f, 100);
	
	public static final Set<String> inc6001ProfileTag = new HashSet<>();
	
	public static final Map<String, Set<String>> BalanceGroupingMap = new ConcurrentHashMap<>(1000, 0.75f, 30);	
	public static final Map<String, String> BalanceOnlyAGroupMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Map<String, String> BalanceOnlyAMGroupMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Map<String, String> BalanceOnlyASGroupMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Map<String, String> BalanceOnlyBGroupMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Map<String, String> BalanceOnlyDGroupMap = new ConcurrentHashMap<>(1000, 0.75f, 30);	
	public static final Map<String, String> BalanceOnlyCGroupMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Map<String, String> BalanceOnlyFGroupMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Map<String, String> BalanceOnlyHGroupMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Map<String, String> BalanceOnlyGGroupMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	
	public static final Map<String, Set<String>> BalanceOnlySpecialASGroupMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Set<String> BalanceOnlySpecialASGroupSet = new HashSet<>();
	
	public static final Map<String, Set<String>> BalanceOnlySpecialAMGroupMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Set<String> BalanceOnlySpecialAMGroupSet = new HashSet<>(); 
	
	public static final Map<String, Set<String>> AMGroupMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	
	public static final Map<String, String> BalanceAVGroupLookup = new ConcurrentHashMap<>(1000, 0.75f, 30);
	
	public static final List<String> MainBalanceGroupingList = new ArrayList<>();	
	public static final Map<String, String> DefaultServicesMap = new ConcurrentHashMap<>(1000, 0.75f, 30);	
	public static final Map<String, String> LifeCycleMap = new ConcurrentHashMap<>(100, 0.75f, 30);	
	public static final Map<String, NPPLIFECYCLEMAPPINGINFO> NPPLifeCycleMap = new ConcurrentHashMap<>(100, 0.75f, 30);
	public static final Map<String,Set<String>> NPPLifeCycleBTIDDetails = new ConcurrentHashMap<>(100, 0.75f, 30);;
	public static final Map<String, String> CommunityMap = new ConcurrentHashMap<>(1000, 0.75f, 30);

	//public static final Set<String> DefaultOfferAttributes = new HashSet<>();
	
	public static final Map<String, String> ProductMappingIgnoreFlag = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Map<String, String> ProductMappingNameIDMap = new ConcurrentHashMap<>(1000, 0.75f, 30); 
		
	/* CIS Mapping variable for all possible outputs*/
	public static final Map<String, CISRENEWALINFO> CIS_Renewal_Mapping = new ConcurrentHashMap<>(10000, 0.75f, 100);
	public static final Map<String, CISONCEOFFINFO> CIS_OnceOff_Mapping = new ConcurrentHashMap<>(10000, 0.75f, 100);
	public static final Map<String, CISPARKINGINFO> CIS_Parking_Mapping = new ConcurrentHashMap<>(10000, 0.75f, 100);
	public static final Map<String, CISNWPCINFO> CIS_NWPC_Mapping = new ConcurrentHashMap<>(10000, 0.75f, 100);
	
	public static final Set<String> CIS_NWPC_BTID = new HashSet<>();
	
	public static List<String> CommonBTForDummyGroup = new ArrayList<>(Arrays.asList("3412","3503","3411","3011"));
	//public static List<String> ExceptionBalances = new HashSet<>(Arrays.asList("1832","2112","1387","1219","21","1512","2432","235","1439","436","1266",
	//		"25","240","260","759","2072","1772","3286","3231","3324","2928","1453","2017","2016","1760","2773","1244","3233", "3451", "3440", "3232","3399"));
	
	public static Set<String> ExceptionBalances = new HashSet<>();
	
	public static List<String> ExceptionBalancesForASGroup = new ArrayList<>(Arrays.asList("1035","74"));
	//public static List<String> ExceptionBalancesForAMGroup = new ArrayList<>(Arrays.asList("55","316","819","966","967","968","1633","1635","3217","3218","3219","3220","3221"));
	public static List<String> ExceptionBalancesForAMGroup = new ArrayList<>(Arrays.asList("316","819","966","967","1635","3217","3218","3219","3220","3221","1633"));
	public static List<String> ExceptionBalancesPCForAMGroup = new ArrayList<>(Arrays.asList("316","819","966","967","3217","3218","3219","3220","3221"));
	public static List<String> ExceptionBalancesForFSBTTyperoup = new ArrayList<>(Arrays.asList("1633","1635"));
	public static List<String> A9SeriesBT = new ArrayList<>(Arrays.asList("397","396","395","899"));
	public static Set<String> IgnoreGraceBTForINC7004Log = new HashSet<>(Arrays.asList("2193","2176","2319","2317"));
	
	public static List<String> BT_VALUE_3011 = new ArrayList<>();
	
	/* Balance Mapping variable for all possible outputs*/
	public static final Map<String, BALANCEMAPPINGINFO> CashBalancesGroup = new ConcurrentHashMap<>(10000, 0.75f, 100);
	
	public static final Map<String, Set<String>> PBTinMultipleGroup = new ConcurrentHashMap<>(10000, 0.75f, 100);
	
	public static final Map<String, BALANCEMAPPINGINFO> BalanceEmptyBTGroupIdentifierMap = new ConcurrentHashMap<>(10000, 0.75f, 100);
	public static final Map<String, BALANCEMAPPINGINFO> BalanceNonEmptyBTGroupIdentifierMap = new ConcurrentHashMap<>(10000, 0.75f, 100);
	
	public static final Map<String, BALANCEMAPPINGINFO> SpecialBalanceNonBTGroupIdentifierMap = new ConcurrentHashMap<>(10000, 0.75f, 100);
	public static final Map<String, BALANCEMAPPINGINFO> MainBalanceNonBTGroupIdentifierMap = new ConcurrentHashMap<>(10000, 0.75f, 100);
	
	public static final Map<String, BALANCEMAPPINGINFO> Special436BalanceDummy = new ConcurrentHashMap<>(10000, 0.75f, 100);
	public static final Map<String, BALANCEMAPPINGINFO> Special1266BalanceDummy = new ConcurrentHashMap<>(10000, 0.75f, 100);
	public static final Map<String, BALANCEMAPPINGINFO> SpecialPA14BalanceDummy = new ConcurrentHashMap<>(10000, 0.75f, 100);
	public static final Map<String, BALANCEMAPPINGINFO> TimeBasedBalanceDummy = new ConcurrentHashMap<>(10000, 0.75f, 100);
	public static final Map<String, BALANCEMAPPINGINFO> SpecialBalanceDummyS = new ConcurrentHashMap<>(10000, 0.75f, 100);
	
	public static final Map<String, BALANCEMAPPINGINFO> SpecialGraceBalance = new ConcurrentHashMap<>(10000, 0.75f, 100);
	public static final Set<String> SpecialGraceList = new HashSet<>();
	public static final Map<String, Set<String>> GraceBalanceGroupingMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Map<String, Set<String>> GraceFullBalanceGroupingMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	
	public static final Map<String, Set<String>> GGroupBalanceGroupingMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Map<String, Set<String>> GSGroupBalanceGroupingMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Map<String, Set<String>> GSBalanceGroupMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Set<String> SpecialGGroupList = new HashSet<>();
	public static final Map<String, BALANCEMAPPINGINFO> SpecialGGroupBalance = new ConcurrentHashMap<>(10000, 0.75f, 100);
	
	public static final Map<String, BALANCEMAPPINGINFO> AllBTBalancesValueMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Set<String> AllBTBalancesValueSet = new HashSet<>();
	
	public static final Map<String, String> OnlyVBalancesMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Map<String, String> OnlyVBalancesValueGroupMap  = new ConcurrentHashMap<>(1000, 0.75f, 30);
	
	public static final Map<String, Set<String>> OnlyVBalancesValueMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Set<String> OnlyVBalancesValueSet = new HashSet<>();
	
	public static final Map<String, Set<String>> OnlyGBalancesValueMap = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Set<String> OnlyGBalancesValueSet = new HashSet<>();
	
	public static final Map<String, Set<String>> CompleteMappingBalanceValues = new ConcurrentHashMap<>(1000, 0.75f, 30);
	public static final Map<String, String> CompleteMappingBalanceType = new ConcurrentHashMap<>(1000, 0.75f, 30);
	
	public static final Set<String> SNA_Rate_Plan_ID_Identifier = new HashSet<>();
	
	public static final Set<String> OnlyUAPresent = new HashSet<>();
	
	public static List<String> ProfileTagDummy = new ArrayList<>();
	public static final Map<String, BALANCEMAPPINGINFO> ProfileTagBalanceDummy = new ConcurrentHashMap<>(10000, 0.75f, 100);
	
	public static final Map<String, Map<String,List<String>>> BalanceProductIDMap = new ConcurrentHashMap<>(10000, 0.75f, 100);
	//public static final Map<String, Set<String>> ProductIDLookUpMap = new ConcurrentHashMap<>(10000, 0.75f, 100);
	//public static final Map<Integer, String> ProductIDFor53 = new ConcurrentHashMap<>(10000, 0.75f, 100);
	//public static final Set<String> ProductIDForOffer = ConcurrentHashMap.newKeySet(BalanceProductID.size());
	//public static final Set<String> ProductIDForDA = ConcurrentHashMap.newKeySet(BalanceProductID.size());
	//public static final Set<String> ProductIDForUC = ConcurrentHashMap.newKeySet(BalanceProductID.size());
	
	final static Logger LOG = Logger.getLogger(LoadSubscriberMapping.class);
	
	public static Random rand = new Random();
	
	private void LoadlanguageMap(String dataFolderPath) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(dataFolderPath + "/LanguageMapping.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String datas[] = line.split(",");
			LoadSubscriberMapping.LanguageMap.put(datas[0], datas[1]);
		}
		br.close();
	}
	
	private void LoadCommonConfigMap(String dataFolderPath) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(dataFolderPath + "/CommonConfigMapping.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String datas[] = line.split(",");
			LoadSubscriberMapping.CommonConfigMap.put(datas[0], datas[1]);
		}
		br.close();
	}
	
	private void LoadPamMap(String dataFolderPath) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(dataFolderPath + "/PAMMapping.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String datas[] = line.split(",",-1);
			LoadSubscriberMapping.PAMMap.put(datas[0], datas[1] + ";" + datas[2] + ";" + datas[3] + ";" + datas[4]);
		}
		br.close();
	}
	
	private void LoadConversionFactorMap(String dataFolderPath) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(dataFolderPath + "/CommonFactorMapping.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String datas[] = line.split(",");
			LoadSubscriberMapping.CommonFactorMap.put(datas[0], datas[2]);
		}
		br.close();
	}
		
	private void LoadConversionLogicMap(String dataFolderPath) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(dataFolderPath + "/ConversionLogicMapping.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String datas[] = line.split(",",-1);
			LoadSubscriberMapping.ConversionLogicMap.put(datas[0], datas[1] + '|' + datas[2] + '|' + datas[3]);
		}
		br.close();
	}
	
	private void LoadLoggingMap(String dataFolderPath) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(dataFolderPath + "/LoggingMapping.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String datas[] = line.split("\\|");
			LoadSubscriberMapping.LoggingMap.put(datas[0], datas[2] + '|' + datas[3] + '|' + datas[5]);
		}
		br.close();
	}
	
	private void LoadOutputFilesMap(String dataFolderPath) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(dataFolderPath + "/OutputFilesMapping.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String datas[] = line.split(",");
			LoadSubscriberMapping.OutputFilesMap.put(datas[0], datas[1] );
		}
		br.close();
	}
	
	private void LoadSdpDistributionMap(String dataFolderPath) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(dataFolderPath + "/sdp_distribution.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String datas[] = line.split(",");
			LoadSubscriberMapping.sdp_distribution.put(datas[0], datas[1] + '|' + datas[2] );
		}
		br.close();
	}
	private void LoadCommunityMap(String dataFolderPath) throws IOException {
		IBindingFactory bindingFactoryBalance = null;
		IUnmarshallingContext unmarshallingContextBalance;
		try {
			bindingFactoryBalance = BindingDirectory.getFactory(com.ericsson.jibx.beans.COMMUNITYMAPPINGLIST.class);
			unmarshallingContextBalance = bindingFactoryBalance.createUnmarshallingContext();
			COMMUNITYMAPPINGLIST CommunityList = (com.ericsson.jibx.beans.COMMUNITYMAPPINGLIST) unmarshallingContextBalance
					.unmarshalDocument(new ByteArrayInputStream(
							FileUtils.readFileToByteArray(new File(dataFolderPath + "/CommunityMapping.xml"))), null);
			
			for (COMMUNITYMAPPINGINFO communityInfo : CommunityList.getCOMMUNITYMAPPINGINFOList()) {
				LoadSubscriberMapping.CommunityMap.put(communityInfo.getCUGNAME(), communityInfo.getCommunityID());
			}
		}
		catch (JiBXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(dataFolderPath + "/sdp_distribution.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String datas[] = line.split(",");
			LoadSubscriberMapping.sdp_distribution.put(datas[0], datas[1] + '|' + datas[2] );
		}
		br.close();*/
	}
	
	private void LoadFTPRangesMap(String dataFolderPath) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(dataFolderPath + "/sdp_ftp_ranges.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String datas[] = line.split(",");
			LoadSubscriberMapping.sdp_ftp_ranges.put(datas[0], line );
		}
		br.close();
	}
	
	private void LoadTBAMappingMap(String dataFolderPath) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(dataFolderPath + "/TBA_Mapping.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String datas[] = line.split(",");
			LoadSubscriberMapping.TBAMap.put(datas[0], datas[1] + "|" + datas[2] );
		}
		br.close();
	}
	
	private void LoadProfileTagInput(String dataFolderPath) throws IOException {
		// TODO Auto-generated method stub
		File FileProfileInput = new File(dataFolderPath+ "/ProfileTagInput.txt");	
		ProfileInputList = FileUtils.readLines(FileProfileInput).stream().collect(Collectors.toSet());
	}
	
	/*private void LoadProfileTagMap(String dataFolderPath) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(dataFolderPath + "/ProfileTagMapping.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String datas[] = line.split("\\|");
			LoadSubscriberMapping.ProfileTagMap.put(datas[0] + ';' + datas[1] + ';' + datas[2],line);
		}
		br.close();
	}*/
	
	private void LoadServiceClassMap(String dataFolderPath) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(dataFolderPath + "/ServiceClassMapping.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String datas[] = line.split(",",-1);
			LoadSubscriberMapping.ServiceClassMap.put(datas[1], datas[2] +'|' + datas[3] +'|' + datas[4] + '|' + datas[5] +'|' + datas[6] +'|' + datas[7] +'|' + datas[8] );
		}
		br.close();
	}
	
	private void LoadLifeCycleMap(String dataFolderPath) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(dataFolderPath + "/LifeCycleMapping.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String datas[] = line.split(",");
			LoadSubscriberMapping.LifeCycleMap.put(datas[0] + '|' + datas[1] + '|' + datas[2], line );
		}
		br.close();
	}
	
	private void LoadNPPLifeCycleMap(String dataFolderPath) throws IOException {
		// TODO Auto-generated method stub
		
		IBindingFactory bindingFactoryNPP = null;
		IUnmarshallingContext unmarshallingContextNPP;
		
		try {
			bindingFactoryNPP = BindingDirectory.getFactory(com.ericsson.jibx.beans.NPPLIFECYCLEMAPPINGLIST.class);
			unmarshallingContextNPP = bindingFactoryNPP.createUnmarshallingContext();
			NPPLIFECYCLEMAPPINGLIST NPPLifeCycleList = (com.ericsson.jibx.beans.NPPLIFECYCLEMAPPINGLIST) unmarshallingContextNPP
					.unmarshalDocument(new ByteArrayInputStream(
							FileUtils.readFileToByteArray(new File(dataFolderPath + "/NPPLifeCycleMapping.xml"))), null);
			
			for ( NPPLIFECYCLEMAPPINGINFO  nppLifeCycleInfo : NPPLifeCycleList.getNPPLIFECYCLEMAPPINGINFOList()) {
				if(nppLifeCycleInfo.getSerialNumber().length() != 0)
				{
					NPPLifeCycleMap.put(nppLifeCycleInfo.getSerialNumber(), nppLifeCycleInfo);
					List<String> PBT_Details = Arrays.asList(nppLifeCycleInfo.getPBTID().split("#")); 
					
					for(String s : PBT_Details)
					{	
						String[] nppValueArr = s.split("#");
						for(String str : nppValueArr)
						{
							Set<String> nppValueSet = new HashSet<>();
							if(NPPLifeCycleBTIDDetails.containsKey(str.split("-")[0]))
							{
								nppValueSet = NPPLifeCycleBTIDDetails.get(str.split("-")[0]);
								nppValueSet.add(str.split("-")[1] + "|" + str.split("-")[2]);
								NPPLifeCycleBTIDDetails.put(str.split("-")[0],nppValueSet);
							}
							else
							{
								nppValueSet.add(str.split("-")[1] + "|" + str.split("-")[2]);
								NPPLifeCycleBTIDDetails.put(str.split("-")[0],nppValueSet);
							}
						}
					}					
				}	
			}
		} catch (JiBXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void LoadDefaultServicesMap(String dataFolderPath) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(dataFolderPath + "/DefaultServicesMapping.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String datas[] = line.split(",",-1);
			LoadSubscriberMapping.DefaultServicesMap.put(datas[0] + '|' + datas[1], line );
			
			/*if(datas[10].length() >1)
			{
				DefaultOfferAttributes.add(datas[10]);
			}*/
			if(datas[14].length() > 1)
				inc6001ProfileTag.add(datas[14].split("\\.")[0].toUpperCase());
		}
		br.close();
	}
	
	private void LoadOfferAttrDefMap(String dataFolderPath) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(dataFolderPath + "/Offer_Attribute_Defination.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String datas[] = line.split(",");
			LoadSubscriberMapping.OfferAttrDefMap.put(datas[0]+ ',' + datas[1], datas[2] );
		}
		br.close();
	}
	
	private void LoadCISRenewalMap(String dataFolderPath)
	{
		IBindingFactory bindingFactoryCIS = null;
		IUnmarshallingContext unmarshallingContextCIS;
		
		try {
			bindingFactoryCIS = BindingDirectory.getFactory(com.ericsson.jibx.beans.CISRENEWALLIST.class);
			unmarshallingContextCIS = bindingFactoryCIS.createUnmarshallingContext();
			CISRENEWALLIST CISRenewallist = (com.ericsson.jibx.beans.CISRENEWALLIST) unmarshallingContextCIS
					.unmarshalDocument(new ByteArrayInputStream(
							FileUtils.readFileToByteArray(new File(dataFolderPath + "/CIS_Renewal_Mapping.xml"))), null);
			
			for (CISRENEWALINFO cisRenewalInfo : CISRenewallist.getCISRENEWALINFOList()) {
				if(cisRenewalInfo.getProductId().length() != 0)
				{
					CIS_Renewal_Mapping.put(cisRenewalInfo.getProductId(), cisRenewalInfo);
				}	
			}
		} catch (JiBXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void LoadCISParkingMap(String dataFolderPath)
	{
		IBindingFactory bindingFactoryCIS = null;
		IUnmarshallingContext unmarshallingContextCIS;
		
		try {
			bindingFactoryCIS = BindingDirectory.getFactory(com.ericsson.jibx.beans.CISPARKINGLIST.class);
			unmarshallingContextCIS = bindingFactoryCIS.createUnmarshallingContext();
			CISPARKINGLIST CISParkinglist = (com.ericsson.jibx.beans.CISPARKINGLIST) unmarshallingContextCIS
					.unmarshalDocument(new ByteArrayInputStream(
							FileUtils.readFileToByteArray(new File(dataFolderPath + "/CIS_Parking_Mapping.xml"))), null);
			
			for (CISPARKINGINFO cisParkingInfo : CISParkinglist.getCISPARKINGINFOList()) {
				if(cisParkingInfo.getProductId().length() != 0)
				{
					CIS_Parking_Mapping.put(cisParkingInfo.getProductId(), cisParkingInfo);
				}			
			}
		} catch (JiBXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void LoadCISNWPCMap(String dataFolderPath)
	{
		IBindingFactory bindingFactoryCIS = null;
		IUnmarshallingContext unmarshallingContextCIS;
		
		try {
			bindingFactoryCIS = BindingDirectory.getFactory(com.ericsson.jibx.beans.CISNWPCLIST.class);
			unmarshallingContextCIS = bindingFactoryCIS.createUnmarshallingContext();
			CISNWPCLIST CISnwpclist = (com.ericsson.jibx.beans.CISNWPCLIST) unmarshallingContextCIS
					.unmarshalDocument(new ByteArrayInputStream(
							FileUtils.readFileToByteArray(new File(dataFolderPath + "/CIS_NWPC_Mapping.xml"))), null);
			
			for (CISNWPCINFO cisNwpcInfo : CISnwpclist.getCISNWPCINFOList()) {
				String BT_ID = cisNwpcInfo.getBTID();
				String BT_VALUE = cisNwpcInfo.getBTValue();
				if(BT_ID.length() != 0)
				{
					CIS_NWPC_Mapping.put(BT_ID + "," + BT_VALUE, cisNwpcInfo);
					CIS_NWPC_BTID.add(BT_ID);
				}			
			}
		} catch (JiBXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void LoadCISOnceOffMap(String dataFolderPath)
	{
		IBindingFactory bindingFactoryCIS = null;
		IUnmarshallingContext unmarshallingContextCIS;
		
		try {
			bindingFactoryCIS = BindingDirectory.getFactory(com.ericsson.jibx.beans.CISONCEOFFLIST.class);
			unmarshallingContextCIS = bindingFactoryCIS.createUnmarshallingContext();
			CISONCEOFFLIST CISOncelist = (com.ericsson.jibx.beans.CISONCEOFFLIST) unmarshallingContextCIS
					.unmarshalDocument(new ByteArrayInputStream(
							FileUtils.readFileToByteArray(new File(dataFolderPath + "/CIS_OnceOff_Mapping.xml"))), null);
			
			for (CISONCEOFFINFO cisOnceInfo : CISOncelist.getCISONCEOFFINFOList()) {
				if(cisOnceInfo.getProductId().length() != 0)
				{
					CIS_OnceOff_Mapping.put(cisOnceInfo.getProductId(), cisOnceInfo);
				}			
			}
		} catch (JiBXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void LoadProfileTagsMap(String dataFolderPath)
	{
		IBindingFactory bindingFactoryProfile = null;
		IUnmarshallingContext unmarshallingContextProfile;
		
		try {
			bindingFactoryProfile = BindingDirectory.getFactory(com.ericsson.jibx.beans.PROFILETAGLIST.class);
			unmarshallingContextProfile = bindingFactoryProfile.createUnmarshallingContext();
			PROFILETAGLIST ProfileList = (com.ericsson.jibx.beans.PROFILETAGLIST) unmarshallingContextProfile
					.unmarshalDocument(new ByteArrayInputStream(
							FileUtils.readFileToByteArray(new File(dataFolderPath + "/ProfileTagMapping.xml"))), null);
			
			for (PROFILETAGINFO profileInfo : ProfileList.getPROFILETAGINFOList()) {
				String Profile_Tag_Name =  profileInfo.getProfileTagName();
				String PT_Group_Identifier =  profileInfo.getPTGroupIdentifier();
				if(Profile_Tag_Name.length() != 0)
				{
					inc6001ProfileTag.add(Profile_Tag_Name.toUpperCase());
					if(profileInfo.getAdditionalPTCheck().length() != 0)
					{
						List<String> PT_Values = new ArrayList<>();
						PT_Values = Arrays.asList(profileInfo.getAdditionalPTCheck().split("#"));
						for(String pt : PT_Values)
						{
							inc6001ProfileTag.add(pt.split("-")[0].toUpperCase());
						}
					}
					if(profileInfo.getUAValue().length() > 1)
					{
						inc6001ProfileTag.add(profileInfo.getUAValue().split("\\.")[0].toUpperCase());
					}
					if(profileInfo.getFafCalledNumber().length() != 0)
					{
						inc6001ProfileTag.add(profileInfo.getFafCalledNumber().split("\\.")[0].toUpperCase());
					}
				}
				
				if(Profile_Tag_Name.length() != 0 && PT_Group_Identifier.length() != 0)
				{
					Profile_Tags_MappingWithGroup.put(Profile_Tag_Name + "," + PT_Group_Identifier, profileInfo);
					if(Profile_Tag_Name.equals("BusMobTopUp") && profileInfo.getRatePlanOperator().length() > 1)
					{
						ProfileSpecialGroup.put(Profile_Tag_Name + "," + profileInfo.getProfileTagValue(), profileInfo);
						ProfileBalancesID.add(profileInfo.getRatePlanOperator());
					}					
				}	
				else
				{
					Profile_Tags_Mapping.put(Profile_Tag_Name, profileInfo);
					ProfileTagName.add(Profile_Tag_Name);					
				}
				
				for (PROFILETAGINFO TempprofileInfo : ProfileList.getPROFILETAGINFOList()) {
					String TempPT_Group_Identifier =  profileInfo.getPTGroupIdentifier();
					String TempProfile_Tag_Name = profileInfo.getProfileTagName();
					if(TempPT_Group_Identifier.length() > 0)
					{
						if(TempPT_Group_Identifier.equals(PT_Group_Identifier))
						{
							String TempProfileTag = TempprofileInfo.getProfileTagName();
							if(ProfileGroupingMap.containsKey(TempPT_Group_Identifier))
							{									
								Set<String> result = new HashSet<>();
								result.addAll(ProfileGroupingMap.get(TempPT_Group_Identifier));
								result.add(Profile_Tag_Name);
								ProfileGroupingMap.put(TempPT_Group_Identifier, result);									
							}
							else
							{
								Set<String> result = new HashSet<>();
								result.add(Profile_Tag_Name);
								result.add(TempProfile_Tag_Name);
								ProfileGroupingMap.put(TempPT_Group_Identifier, result);
							}
						}	
					}
				}
			}
		} catch (JiBXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
	private void LoadxmlBalanceMappingMap(String dataFolderPath)
	{
		IBindingFactory bindingFactoryBalance = null;
		IUnmarshallingContext unmarshallingContextBalance;
		
		try {
			bindingFactoryBalance = BindingDirectory.getFactory(com.ericsson.jibx.beans.BALANCEMAPPINGLIST.class);
			unmarshallingContextBalance = bindingFactoryBalance.createUnmarshallingContext();
			BALANCEMAPPINGLIST balancelist = (com.ericsson.jibx.beans.BALANCEMAPPINGLIST) unmarshallingContextBalance
					.unmarshalDocument(new ByteArrayInputStream(
							FileUtils.readFileToByteArray(new File(dataFolderPath + "/BalanceMapping.xml"))), null);
			
			for (BALANCEMAPPINGINFO balanceInfo : balancelist.getBALANCEMAPPINGINFOList()) {
								
				String Balance_ID = balanceInfo.getBTID();
				
				//Code for Added Offer, UC, DA
								
				//Added code to check for Offer id it is ignore
				ProductMappingIgnoreFlag.put(Balance_ID,balanceInfo.getIgnoreFlag().trim());
				//Added code to relate BTID with its mapped name need for RoundOffsheet
				ProductMappingNameIDMap.put(Balance_ID, balanceInfo.getBalanceTypeName());
				
				/*Create a map for General Cash and NPP data*/
				if(Balance_ID.equals("21") || Balance_ID.equals("1439") || Balance_ID.equals("235"))
					CashBalancesGroup.put(Balance_ID + ',' + balanceInfo.getSymbols(), balanceInfo);
				
				/*collect only those Accumulator which are empty, it is only usefull in case of logging*/
				if(balanceInfo.getBTGroupIdentifier().isEmpty() && balanceInfo.getOfferID().isEmpty() && !balanceInfo.getUAID().isEmpty())
						OnlyUAPresent.add(Balance_ID);
				
				/*if(Balance_ID.equals("436"))
					System.out.println("Vipin");*/
				
				if(balanceInfo.getBTGroupIdentifier().toUpperCase().startsWith("DUMMY") && !CommonBTForDummyGroup.contains(Balance_ID))
					ExceptionBalances.add(Balance_ID);
				
				String Product_Private = balanceInfo.getProductPrivate().trim();
				
				
				if(Product_Private.equals("Yes"))
				{	
					Set<String> UCList = new HashSet<>();
					Set<String> DAList = new HashSet<>();
					Set<String> OffAttrList = new HashSet<>();
					
					String AddedDA = balanceInfo.getAddDA();
					String AddedUC = balanceInfo.getAddUC();
					
					if(!AddedDA.isEmpty())
					{
						String[] ListofAddedDA;
						/*if(AddedDA.contains("\\|"))
							ListofAddedDA = AddedDA.split("\\|");
						else
							ListofAddedDA = new String[]{AddedDA};*/						
						ListofAddedDA = AddedDA.split("#");
						
						for(int i = 0; i<ListofAddedDA.length; i++)
						{
							if(ListofAddedDA[i].length() > 1)
							{
								//361-5368709120
								DAList.add(ListofAddedDA[i].split("-")[0]);
							}
						}
					}
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
								if(!ListofAddedUC[i].split("-")[0].equals("53"))
								{
									UCList.add(ListofAddedUC[i].split("-")[0]);
								}
							}
						}
					}
					
					for (BALANCEMAPPINGINFO Temp:  balancelist.getBALANCEMAPPINGINFOList())
					{
						if (balanceInfo.getOfferID().equals(Temp.getOfferID()))
						{
							String BT_Identifier = balanceInfo.getBTGroupIdentifier();
							//Adding code to implment the Added OFfer, DA, UC
							
							if(balanceInfo.getBTGroupIdentifier().isEmpty())
							{
								if(!Temp.getUCID().isEmpty())
									UCList.add(Temp.getUCID());
								if(!Temp.getDAID().isEmpty())
									DAList.add(Temp.getDAID());
								if(!Temp.getAttrOfferId().isEmpty())
									OffAttrList.add(Temp.getAttrOfferId());
							}
							else
							{
								boolean MappingCreated = false;
								for (BALANCEMAPPINGINFO Temp2:  balancelist.getBALANCEMAPPINGINFOList())
								{
									if(BT_Identifier.equals(Temp2.getBTGroupIdentifier()))
									{	
										if(BT_Identifier.equals("M"))
										{
											if(!Temp2.getUCID().isEmpty() && Temp2.getUCID().startsWith(Temp.getOfferID()))
											{
												UCList.add(Temp2.getUCID());
												MappingCreated = true;
											}
											if(!Temp2.getDAID().isEmpty() && Temp2.getDAID().startsWith(Temp.getOfferID()))
											{
												DAList.add(Temp2.getDAID());
												MappingCreated = true;
											}
											if(!Temp2.getAttrOfferId().isEmpty() && Temp2.getAttrOfferId().startsWith(Temp.getOfferID()))
											{
												OffAttrList.add(Temp2.getAttrOfferId());
												MappingCreated = true;
											}
										}
										else
										{										
											if(!Temp2.getUCID().isEmpty())
											{
												UCList.add(Temp2.getUCID());
												MappingCreated = true;
											}
											if(!Temp2.getDAID().isEmpty())
											{
												DAList.add(Temp2.getDAID());
												MappingCreated = true;
											}
											if(!Temp2.getAttrOfferId().isEmpty())
											{
												OffAttrList.add(Temp2.getAttrOfferId());
												MappingCreated = true;
											}
											//break;
										}
									}								
								}
							}
							break;
						}						
					}
					
					if(!balanceInfo.getOfferID().isEmpty())
					{
						if(!BalanceProductIDMap.containsKey(balanceInfo.getOfferID()))
						{
							Map<String,List<String>> MapValue = new HashMap<>(); 
							MapValue.put("UCValue",new ArrayList<>(UCList));
							MapValue.put("DAValue",new ArrayList<>(DAList));
							MapValue.put("OffAttrValue",new ArrayList<>(OffAttrList));
							BalanceProductIDMap.put(balanceInfo.getOfferID(),MapValue);
						}
						else
						{
							Map<String,List<String>> TempMapValue = new HashMap<>(); 
							TempMapValue = BalanceProductIDMap.get(balanceInfo.getOfferID());
							UCList.addAll(TempMapValue.get("UCValue"));
							DAList.addAll(TempMapValue.get("DAValue"));
							OffAttrList.addAll(TempMapValue.get("OffAttrValue"));
							TempMapValue.put("UCValue",new ArrayList<>(UCList));
							TempMapValue.put("DAValue",new ArrayList<>(DAList));
							TempMapValue.put("OffAttrValue",new ArrayList<>(OffAttrList));
							BalanceProductIDMap.put(balanceInfo.getOfferID(),TempMapValue);
						}
					}
				}
				
				//Adding balance for Connecting offers
				String AddedOffer = balanceInfo.getAddOffer();
				if(!AddedOffer.isEmpty())
				{
					Set<String> UCList = new HashSet<>();
					Set<String> DAList = new HashSet<>();
					Set<String> OffAttrList = new HashSet<>();
					
					String AddedUC = balanceInfo.getAddUC();
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
								UCList.add(ListofAddedUC[i].split("-")[0]);
							}
						}
					}
					
					
					String[] ListofAddedOffer;
					if(AddedOffer.contains(":"))
						ListofAddedOffer = AddedOffer.split(":")[0].split("\\|");
					else
						ListofAddedOffer = AddedOffer.split("\\|");
					
					for(int i = 0; i<ListofAddedOffer.length; i++)
					{
						if(ListofAddedOffer[i].length() > 1 && ListofAddedOffer[i].split("-")[1].equals("Y"))
						{	
							Map<String,List<String>> MapValue = new HashMap<>(); 
							MapValue.put("UCValue",new ArrayList<>(UCList));
							MapValue.put("DAValue",new ArrayList<>(DAList));
							MapValue.put("OffAttrValue",new ArrayList<>(OffAttrList));
							if(!ListofAddedOffer[i].split("-")[2].isEmpty())
								BalanceProductIDMap.put(ListofAddedOffer[i].split("-")[2],MapValue);							
						}
					}
				}
				if (!Balance_ID.isEmpty())
				{					
					String BTGroupIdentifier = balanceInfo.getBTGroupIdentifier();
					String CCID = balanceInfo.getRPID();
					String BalanceKey = Balance_ID + '|' + BTGroupIdentifier ;
					
					//Populate all the BT_Value in map and can be used for logging if any BT is left out.
					if(!BTGroupIdentifier.equals("M") && (balanceInfo.getSymbols().equals("or") || balanceInfo.getSymbols().equals("=")) && !BTGroupIdentifier.isEmpty())
					{
						//CompleteMappingBalanceValues
						if(CompleteMappingBalanceValues.containsKey(Balance_ID))
						{
							Set<String> BTBalances = new HashSet<>();
							BTBalances = CompleteMappingBalanceValues.get(Balance_ID);
							Collections.addAll(BTBalances, (balanceInfo.getBTValue().split("#")));
							CompleteMappingBalanceValues.put(Balance_ID, BTBalances);
							CompleteMappingBalanceType.put(Balance_ID,balanceInfo.getBTTYPE());
						}
						else
						{
							Set<String> BTBalances = new HashSet<>();
							//BTBalances.add(balanceInfo.getBTValue());
							Collections.addAll(BTBalances, (balanceInfo.getBTValue().split("#")));
							CompleteMappingBalanceValues.put(Balance_ID, BTBalances);
							CompleteMappingBalanceType.put(Balance_ID,balanceInfo.getBTTYPE());
						}
					}
					
									
					//if(!BTGroupIdentifier.equals("M") && !BTGroupIdentifier.startsWith("G-") && !BTGroupIdentifier.startsWith("GRACE-"))
					if(!BTGroupIdentifier.equals("M") && !BTGroupIdentifier.equals("G") && !BTGroupIdentifier.startsWith("GRACE-") && !BTGroupIdentifier.toUpperCase().startsWith("DUMMY"))
					{	
						if(balanceInfo.getBTTYPE().equals("V"))
						{
							OnlyVBalancesValueGroupMap.put(balanceInfo.getBTGroupIdentifier(),Balance_ID + "|" + balanceInfo.getBTValue());
							OnlyVBalancesValueSet.add(Balance_ID);
							
							//used in find the empty values
							if(OnlyVBalancesValueMap.containsKey(Balance_ID))
							{
								Set<String> BTBalances = new HashSet<>();
								BTBalances = OnlyVBalancesValueMap.get(Balance_ID);
								Collections.addAll(BTBalances, (balanceInfo.getBTValue().split("#")));
								OnlyVBalancesValueMap.put(Balance_ID, BTBalances);
							}
							else
							{
								Set<String> BTBalances = new HashSet<>();
								//BTBalances.add(balanceInfo.getBTValue());
								Collections.addAll(BTBalances, (balanceInfo.getBTValue().split("#")));
								OnlyVBalancesValueMap.put(Balance_ID, BTBalances);
							}
							//OnlyVBalancesValueMap.put(Balance_ID + "|" + balanceInfo.getBTValue() , balanceInfo);
							
							//OnlyVBalancesMap.put(Balance_ID,balanceInfo.getBTValue());
						}
						if(balanceInfo.getBTTYPE().equals("P") || balanceInfo.getBTTYPE().equals("M"))
						{
							AllBTBalancesValueMap.put(Balance_ID, balanceInfo);
							AllBTBalancesValueSet.add(Balance_ID);
						}
					}
					
					if(!BTGroupIdentifier.equals("M") && (BTGroupIdentifier.equals("G") || BTGroupIdentifier.startsWith("GRACE-")) && !BTGroupIdentifier.toUpperCase().startsWith("DUMMY"))
					{	
						if(balanceInfo.getBTTYPE().equals("V"))
						{
							OnlyVBalancesValueGroupMap.put(balanceInfo.getBTGroupIdentifier(),Balance_ID + "|" + balanceInfo.getBTValue());
							OnlyVBalancesValueSet.add(Balance_ID);
							
							//used in find the empty values
							if(OnlyVBalancesValueMap.containsKey(Balance_ID))
							{
								Set<String> BTBalances = new HashSet<>();
								BTBalances = OnlyVBalancesValueMap.get(Balance_ID);
								Collections.addAll(BTBalances, (balanceInfo.getBTValue().split("#")));
								OnlyVBalancesValueMap.put(Balance_ID, BTBalances);
							}
							else
							{
								Set<String> BTBalances = new HashSet<>();
								//BTBalances.add(balanceInfo.getBTValue());
								Collections.addAll(BTBalances, (balanceInfo.getBTValue().split("#")));
								OnlyVBalancesValueMap.put(Balance_ID, BTBalances);
							}
							//OnlyVBalancesValueMap.put(Balance_ID + "|" + balanceInfo.getBTValue() , balanceInfo);
							
							//OnlyVBalancesMap.put(Balance_ID,balanceInfo.getBTValue());
						}
						if((BTGroupIdentifier.equals("G") && (balanceInfo.getBTTYPE().equals("V") || balanceInfo.getBTTYPE().equals("M"))))
						{
							
						}
					}
					
					if (BTGroupIdentifier.length() == 0)
					{		
						BalanceEmptyBTGroupIdentifierMap.put(BalanceKey, balanceInfo);				
					}
					else if(balanceInfo.getPTName().length() > 0)
					{
						ProfileTagDummy.add(balanceInfo.getBTGroupIdentifier());
						ProfileTagBalanceDummy.put(BalanceKey,balanceInfo);
						if(Balance_ID.equals("74") || Balance_ID.equals("1487") || Balance_ID.equals("1712"))
							BalanceNonEmptyBTGroupIdentifierMap.put(BalanceKey, balanceInfo);
					}
					else
					{						
						if(BTGroupIdentifier.startsWith("DUMMY") && CCID.length() == 0)
						{
							SpecialBalanceNonBTGroupIdentifierMap.put(Balance_ID + '|' + balanceInfo.getBTValue() , balanceInfo);
							if(Balance_ID.equals("3011"))
							{
								BT_VALUE_3011.add(balanceInfo.getBTValue());
							}							
						}
						else if(BTGroupIdentifier.startsWith("DUMMY") && CCID.length() != 0)
						{							
							MainBalanceGroupingList.add(BTGroupIdentifier);
							MainBalanceNonBTGroupIdentifierMap.put(Balance_ID + '|' + CCID + '|' + BTGroupIdentifier, balanceInfo);
															
						}
						if(BTGroupIdentifier.startsWith("DUMMY_S"))
						{
							SpecialBalanceDummyS.put(Balance_ID, balanceInfo);
						}
						//if(!Offer_Value.isEmpty())
						if(Balance_ID.equals("436"))
						{
							Special436BalanceDummy.put(Balance_ID + ':' + CCID, balanceInfo);
						}
						if(Balance_ID.equals("1266") )
						{
							Special1266BalanceDummy.put(Balance_ID + '|' + balanceInfo.getSymbols(), balanceInfo);
						}
						if(Balance_ID.equals("1383") || Balance_ID.equals("3233") || Balance_ID.equals("1239"))
						{
							SpecialPA14BalanceDummy.put(BTGroupIdentifier + '|' + balanceInfo.getSymbols(), balanceInfo);
						}	
						if(Balance_ID.equals("3052") || Balance_ID.equals("1496"))
						{
							TimeBasedBalanceDummy.put(BTGroupIdentifier + '|' + balanceInfo.getSymbols(), balanceInfo);
						}
						if(BTGroupIdentifier.startsWith("GRACE-"))
						{
							for (BALANCEMAPPINGINFO tempbalanceInfo : balancelist.getBALANCEMAPPINGINFOList()) {
								String BTGroupIdentifierTemp = tempbalanceInfo.getBTGroupIdentifier();
								if(BTGroupIdentifierTemp.startsWith("GRACE-"))
								{
									if(balanceInfo.getBTGroupIdentifier().equals(BTGroupIdentifierTemp))
									{
										String Balance_IDTemp = tempbalanceInfo.getBTID();
										if(GraceBalanceGroupingMap.containsKey(BTGroupIdentifierTemp))
										{									
											Set<String> result = new HashSet<>();
											result.addAll(GraceBalanceGroupingMap.get(BTGroupIdentifierTemp));
											result.add(Balance_IDTemp);
											GraceBalanceGroupingMap.put(BTGroupIdentifier, result);									
										}
										else
										{
											Set<String> result = new HashSet<>();
											result.add(Balance_ID);
											result.add(Balance_IDTemp);
											GraceBalanceGroupingMap.put(BTGroupIdentifier, result);
										}
									}
								}								
							}
							SpecialGraceBalance.put(Balance_ID + '|' + balanceInfo.getBTGroupIdentifier(), balanceInfo);
							SpecialGraceList.add(Balance_ID);
						}
						
						if(BTGroupIdentifier.equals("G"))
						{
							//if(balanceInfo.getBTTYPE().equals("V"))
							{
								//used in find the empty values
								if(OnlyGBalancesValueMap.containsKey(Balance_ID))
								{
									Set<String> BTBalances = new HashSet<>();
									BTBalances = OnlyGBalancesValueMap.get(Balance_ID);
									Collections.addAll(BTBalances, (balanceInfo.getBTValue().split("#")));
									OnlyGBalancesValueMap.put(Balance_ID, BTBalances);
								}
								else
								{
									Set<String> BTBalances = new HashSet<>();
									//BTBalances.add(balanceInfo.getBTValue());
									Collections.addAll(BTBalances, (balanceInfo.getBTValue().split("#")));
									OnlyGBalancesValueMap.put(Balance_ID, BTBalances);
								}
								//OnlyVBalancesValueMap.put(Balance_ID + "|" + balanceInfo.getBTValue() , balanceInfo);								
								//OnlyVBalancesMap.put(Balance_ID,balanceInfo.getBTValue());
							}
							for (BALANCEMAPPINGINFO tempbalanceInfo : balancelist.getBALANCEMAPPINGINFOList()) {
								String BTGroupIdentifierTemp = tempbalanceInfo.getBTGroupIdentifier();
								if(BTGroupIdentifierTemp.startsWith("G-S"))
								{	
									if(balanceInfo.getBTGroupIdentifier().equals(BTGroupIdentifierTemp))
									{
										String Balance_IDTemp = tempbalanceInfo.getBTID();
										if(GSGroupBalanceGroupingMap.containsKey(BTGroupIdentifierTemp))
										{									
											Set<String> result = new HashSet<>();
											result.addAll(GSGroupBalanceGroupingMap.get(BTGroupIdentifierTemp));
											result.add(Balance_IDTemp);
											GSGroupBalanceGroupingMap.put(BTGroupIdentifier, result);											
										}
										else
										{
											Set<String> result = new HashSet<>();
											result.add(Balance_ID);
											result.add(Balance_IDTemp);
											GSGroupBalanceGroupingMap.put(BTGroupIdentifier, result);											
										}
										if(GSBalanceGroupMap.containsKey(tempbalanceInfo.getBTID()+ '|' + tempbalanceInfo.getBTValue()))
										{	
											Set<String> result1 = new HashSet<>();
											result1.addAll(GSBalanceGroupMap.get(tempbalanceInfo.getBTID()+ '|' + tempbalanceInfo.getBTValue()));
											result1.add(tempbalanceInfo.getBTGroupIdentifier());
											GSBalanceGroupMap.put(tempbalanceInfo.getBTID()+ '|' + tempbalanceInfo.getBTValue(), result1);
										}
										else
										{											
											Set<String> result1 = new HashSet<>();
											result1.add(balanceInfo.getBTGroupIdentifier());
											result1.add(tempbalanceInfo.getBTGroupIdentifier());
											GSBalanceGroupMap.put(tempbalanceInfo.getBTID()+ '|' + tempbalanceInfo.getBTValue(),result1);
										}
										
									}
								}
								else if(BTGroupIdentifierTemp.equals("G"))
								{
									if(balanceInfo.getBTGroupIdentifier().equals(BTGroupIdentifierTemp))
									{
										String Balance_IDTemp = tempbalanceInfo.getBTID();
										if(GGroupBalanceGroupingMap.containsKey(BTGroupIdentifierTemp))
										{									
											Set<String> result = new HashSet<>();
											result.addAll(GGroupBalanceGroupingMap.get(BTGroupIdentifierTemp));
											result.add(Balance_IDTemp);
											GGroupBalanceGroupingMap.put(BTGroupIdentifier, result);									
										}
										else
										{
											Set<String> result = new HashSet<>();
											result.add(Balance_ID);
											result.add(Balance_IDTemp);
											GGroupBalanceGroupingMap.put(BTGroupIdentifier, result);
										}
									}
								}								
							}
							SpecialGGroupBalance.put(Balance_ID + '|' + balanceInfo.getBTGroupIdentifier(), balanceInfo);
							SpecialGGroupList.add(Balance_ID);
						}
						
						
						BalanceNonEmptyBTGroupIdentifierMap.put(BalanceKey, balanceInfo);
						//Create a List with a group V
						if(balanceInfo.getBTTYPE().equals("V"))
						{
							BalanceAVGroupLookup.put(BTGroupIdentifier,Balance_ID);
						}
						
						for (BALANCEMAPPINGINFO tempbalanceInfo : balancelist.getBALANCEMAPPINGINFOList()) {
							String BTGroupIdentifierTemp = tempbalanceInfo.getBTGroupIdentifier();
							if(BTGroupIdentifierTemp.equals(BTGroupIdentifier) && !BTGroupIdentifier.equals("M") && !BTGroupIdentifier.startsWith("GRACE-") && !BTGroupIdentifier.startsWith("A-M-") && !BTGroupIdentifier.equals("G"))
							{
								String Balance_IDTemp = tempbalanceInfo.getBTID();
								if(BalanceGroupingMap.containsKey(BTGroupIdentifierTemp))
								{									
									Set<String> result = new HashSet<>();
									result.addAll(BalanceGroupingMap.get(BTGroupIdentifierTemp));
									result.add(Balance_IDTemp);
									BalanceGroupingMap.put(BTGroupIdentifier, result);									
								}
								else
								{
									Set<String> result = new HashSet<>();
									result.add(Balance_ID);
									result.add(Balance_IDTemp);
									BalanceGroupingMap.put(BTGroupIdentifier, result);
								}
								if(ExceptionBalancesForAMGroup.contains(Balance_ID))
								{
									if(BalanceOnlySpecialAMGroupMap.containsKey(BTGroupIdentifierTemp))
									{									
										Set<String> result = new HashSet<>();
										result.addAll(BalanceOnlySpecialAMGroupMap.get(BTGroupIdentifierTemp));
										result.add(Balance_IDTemp);
										BalanceOnlySpecialAMGroupMap.put(BTGroupIdentifier, result);
										BalanceOnlySpecialAMGroupSet.add(Balance_IDTemp);
									}
									else
									{
										Set<String> result = new HashSet<>();
										result.add(Balance_ID);
										result.add(Balance_IDTemp);
										BalanceOnlySpecialAMGroupMap.put(BTGroupIdentifier, result);
										BalanceOnlySpecialAMGroupSet.add(Balance_IDTemp);
									}
								}
								
								if(ExceptionBalancesForASGroup.contains(Balance_ID))
								{
									if(BalanceOnlySpecialASGroupMap.containsKey(BTGroupIdentifierTemp))
									{									
										Set<String> result = new HashSet<>();
										result.addAll(BalanceOnlySpecialASGroupMap.get(BTGroupIdentifierTemp));
										result.add(Balance_IDTemp);
										BalanceOnlySpecialASGroupMap.put(BTGroupIdentifier, result);	
										BalanceOnlySpecialASGroupSet.add(Balance_IDTemp);
									}
									else
									{
										Set<String> result = new HashSet<>();
										result.add(Balance_ID);
										result.add(Balance_IDTemp);
										BalanceOnlySpecialASGroupMap.put(BTGroupIdentifier, result);
										BalanceOnlySpecialASGroupSet.add(Balance_IDTemp);
									}
								}
							}
							if(BTGroupIdentifierTemp.equals(BTGroupIdentifier) && BTGroupIdentifier.startsWith("A-M-"))
							{
								String Balance_IDTemp = tempbalanceInfo.getBTID();
								if(AMGroupMap.containsKey(BTGroupIdentifierTemp))
								{									
									Set<String> result = new HashSet<>();
									result.addAll(AMGroupMap.get(BTGroupIdentifierTemp));
									result.add(Balance_IDTemp);
									AMGroupMap.put(BTGroupIdentifier, result);									
								}
								else
								{
									Set<String> result = new HashSet<>();
									result.add(Balance_ID);
									result.add(Balance_IDTemp);
									AMGroupMap.put(BTGroupIdentifier, result);
								}
							}
						}
					}
				}
				
				//Preparing list for INC6001
				if(balanceInfo.getPTName().length() > 0)
				{
					List<String> PTMappingValue = Arrays.asList(balanceInfo.getPTName().split("#"));
					for(String PtValue : PTMappingValue)
						inc6001ProfileTag.add(PtValue.split("-")[0].toUpperCase());
				}
				if(balanceInfo.getOfferStartDate().toUpperCase().endsWith("INFILE_SUBSCRIBER_PROFILE.CSV"))
				{
					inc6001ProfileTag.add(balanceInfo.getOfferStartDate().split("\\.")[0].toUpperCase());
				}
				if(balanceInfo.getOfferExpiryDate().toUpperCase().endsWith("INFILE_SUBSCRIBER_PROFILE.CSV"))
				{
					inc6001ProfileTag.add(balanceInfo.getOfferExpiryDate().split("\\.")[0].toUpperCase());
				}
				
			}
			//This loop is to handle the grace 
			for (BALANCEMAPPINGINFO Temp:  balancelist.getBALANCEMAPPINGINFOList())
			{
				String Balance_ID = Temp.getBTID();
				if (SpecialGraceList.contains(Balance_ID))
				{	
					if(GraceFullBalanceGroupingMap.containsKey(Balance_ID))
					{									
						Set<String> BTBalances = new HashSet<>();
						BTBalances = GraceFullBalanceGroupingMap.get(Balance_ID);
						Collections.addAll(BTBalances, (Temp.getBTValue().split("#")));
						GraceFullBalanceGroupingMap.put(Balance_ID, BTBalances);									
					}					
					else
					{
						Set<String> BTBalances = new HashSet<>();
						//BTBalances.add(balanceInfo.getBTValue());
						Collections.addAll(BTBalances, (Temp.getBTValue().split("#")));
						GraceFullBalanceGroupingMap.put(Balance_ID, BTBalances);						
					}					
				}
			}
			
			//BalanceEmptyBTGroupIdentifierMap.forEach((k,v)->System.out.println(k));
			//System.out.println("------------------------");
			//BalanceNonEmptyBTGroupIdentifierMap.forEach((k,v)->System.out.println(k + "###"));
			
			//BalanceProductID.keySet().forEach(action->System.out.println(action));
			//System.out.println("------------------------");
			//System.out.println("Vipin");
			//AllBTBalancesValueMap.forEach((k,v)->System.out.println(k+"----"+v));
			//System.out.println("------------------------");
			//OnlyVBalancesValueMap.forEach((k,v)->System.out.println(k+"----"+v));
			//BalanceOnlySpecialAGroupSet.forEach(x->System.out.println(x));
			
		} catch (JiBXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void GetAGroupPCitemMultipleGroup(String dataFolderPath)
	{
		IBindingFactory bindingFactoryBalance = null;
		IUnmarshallingContext unmarshallingContextBalance;
		
		try {
			bindingFactoryBalance = BindingDirectory.getFactory(com.ericsson.jibx.beans.BALANCEMAPPINGLIST.class);
			unmarshallingContextBalance = bindingFactoryBalance.createUnmarshallingContext();
			BALANCEMAPPINGLIST balancelist = (com.ericsson.jibx.beans.BALANCEMAPPINGLIST) unmarshallingContextBalance
					.unmarshalDocument(new ByteArrayInputStream(
							FileUtils.readFileToByteArray(new File(dataFolderPath + "/BalanceMapping.xml"))), null);
			
			for (BALANCEMAPPINGINFO balanceInfo : balancelist.getBALANCEMAPPINGINFOList()) {
				
				String BT_Type = balanceInfo.getBTTYPE();
				if(BT_Type.equals("P"))
				{
					String BT_ID = balanceInfo.getBTID();
					
					Set<String> GroupName = new HashSet<>();
					for (BALANCEMAPPINGINFO tempbalanceInfo : balancelist.getBALANCEMAPPINGINFOList()) {
						if(BT_ID.equals(tempbalanceInfo.getBTID()))
						{
							if(!tempbalanceInfo.getBTGroupIdentifier().startsWith("G"))
							{
								GroupName.add(tempbalanceInfo.getBTGroupIdentifier());
							}							
						}
					}
					if(GroupName.size()>=2)
						PBTinMultipleGroup.put(balanceInfo.getBTID(),GroupName);
				}
				
			}
		
		//Collections.sort(PCSGroup);
		//PBTinMultipleGroup.forEach((k,v)->System.out.println(k + "----" + v));
		//PCSGroup.forEach(x->System.out.println(x));
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.error("Exception occured ", e);
		}
		
	}
	

	
	public LoadSubscriberMapping(String sdpid, String configPath, String dataFolderPath, String workingMode) {

		try {
			LoadLoggingMap(dataFolderPath);
			LoadSdpDistributionMap(dataFolderPath);
			LoadFTPRangesMap(dataFolderPath);
			LoadTBAMappingMap(dataFolderPath);
			LoadCommonConfigMap(dataFolderPath);
			LoadPamMap(dataFolderPath);
			LoadConversionLogicMap(dataFolderPath);
			LoadConversionFactorMap(dataFolderPath);
			LoadOutputFilesMap(dataFolderPath);
			LoadLifeCycleMap(dataFolderPath);
			LoadNPPLifeCycleMap(dataFolderPath);
			LoadServiceClassMap(dataFolderPath);
			LoadlanguageMap(dataFolderPath);
			LoadxmlBalanceMappingMap(dataFolderPath);
			LoadDefaultServicesMap(dataFolderPath);
			LoadCommunityMap(dataFolderPath);
			//LoadProfileTagMap(dataFolderPath);
			LoadOfferAttrDefMap(dataFolderPath);
			LoadProfileTagsMap(dataFolderPath);
			LoadCISOnceOffMap(dataFolderPath);
			LoadCISRenewalMap(dataFolderPath);
			LoadCISParkingMap(dataFolderPath);
			LoadCISNWPCMap(dataFolderPath);
			LoadProfileTagInput(dataFolderPath);
			
			//find the Group where PC is repeated, as this group differentiated by Value of VBT
			GetAGroupPCitemMultipleGroup(dataFolderPath);
			
			 for (String GroupName : BalanceGroupingMap.keySet())
			 {
				 if(GroupName.startsWith("D-"))
				 {
					 Set<String> tempSet = BalanceGroupingMap.get(GroupName);
					 String ResultString = String.join(",", tempSet);
					 BalanceOnlyDGroupMap.put(GroupName, ResultString);
				 }
			 }
			 
			 for (String GroupName : BalanceGroupingMap.keySet())
			 {
				 if(GroupName.startsWith("A-"))
				 {
					 if(!GroupName.startsWith("A-M") )
					 {
						 if(!GroupName.startsWith("A-S"))
						 {
							 Set<String> tempSet = BalanceGroupingMap.get(GroupName);
							 String ResultString = String.join(",", tempSet);
							 BalanceOnlyAGroupMap.put(GroupName, ResultString);
						 }
					 }
				 }
			 }
			 for (String GroupName : AMGroupMap.keySet())
			 {
				 if(GroupName.startsWith("A-M"))
				 {
					 Set<String> tempSet = AMGroupMap.get(GroupName);
					 String ResultString = String.join(",", tempSet);
					 BalanceOnlyAMGroupMap.put(GroupName, ResultString);
					 UniqueBalanceOnlyAMGroupMap.addAll(tempSet.stream().map(String::trim).collect(Collectors.toList()));
				 }
			 }
			 for (String GroupName : BalanceGroupingMap.keySet())
			 {
				 if(GroupName.equals("G"))
				 {
					 Set<String> tempSet = BalanceGroupingMap.get(GroupName);
					 String ResultString = String.join(",", tempSet);
					 BalanceOnlyGGroupMap.put(GroupName, ResultString);
					 //UniqueBalanceOnlyGGroupMap.addAll(tempSet.stream().map(String::trim).collect(Collectors.toList()));
				 }
			 }
			 for (String GroupName : BalanceGroupingMap.keySet())
			 {
				 if(GroupName.startsWith("B-"))
				 {
					 Set<String> tempSet = BalanceGroupingMap.get(GroupName);
					 String ResultString = String.join(",", tempSet);
					 BalanceOnlyBGroupMap.put(GroupName, ResultString);
				 }
			 }
			 
			 
			 
			/* for (String GroupName : BalanceGroupingMap.keySet())
			 {
				 if(GroupName.startsWith("A-"))
				 {
					 Set<String> tempSet = BalanceGroupingMap.get(GroupName);
					 String ResultString = String.join(",", tempSet);
					 BalanceOnlyAGroupMap.put(GroupName, ResultString);
				 }
			 }*/
			 
			 for (String GroupName : BalanceGroupingMap.keySet())
			 {
				 if(GroupName.startsWith("C-"))
				 {
					 Set<String> tempSet = BalanceGroupingMap.get(GroupName);
					 String ResultString = String.join(",", tempSet);
					 BalanceOnlyCGroupMap.put(GroupName, ResultString);
				 }
			 }
			 
			 for (String GroupName : BalanceGroupingMap.keySet())
			 {
				 if(GroupName.startsWith("F-"))
				 {
					 if(!GroupName.endsWith("M"))
					 {
						 Set<String> tempSet = BalanceGroupingMap.get(GroupName);
						 String ResultString = String.join(",", tempSet);
						 BalanceOnlyFGroupMap.put(GroupName, ResultString);
					 }
				 }
				 if(GroupName.startsWith("H-"))
				 {
					 if(!GroupName.endsWith("M"))
					 {
						 Set<String> tempSet = BalanceGroupingMap.get(GroupName);
						 String ResultString = String.join(",", tempSet);
						 BalanceOnlyHGroupMap.put(GroupName, ResultString);
					 }
				 }
				 
			 }
			
			//populate the set to check if i need to traverse to master group
			BalanceOnlySpecialAMGroupSet.add("1633");
			BalanceOnlySpecialAMGroupSet.removeAll(A9SeriesBT);
			
			AMasterBTValues.addAll(Arrays.stream(CommonConfigMap.get("Product_Mapping_AM_BT_Group_Identifier").split("\\|")).collect(Collectors.toSet()));
			
			SNA_Rate_Plan_ID_Identifier.addAll(Arrays.stream(CommonConfigMap.get("SNA_Rate_Plan_ID_Identifier").split("\\|")).collect(Collectors.toSet()));
			
			//ExceptionBalances.addAll(SpecialCISRECList);
			
			
			//inc6001ProfileTag.addAll(new HashSet<>(Arrays.asList("TRANSLATEDNUMBER","TRANSNUMBER")));
			
			System.out.println("----------Count of Mapping Data for Offer------------");
			System.out.println("Empty-BT-GroupIdentifier: " + BalanceEmptyBTGroupIdentifierMap.size());
			System.out.println("NonEmpty-BTGroupIdentifier: " + BalanceNonEmptyBTGroupIdentifierMap.size());
			System.out.println("Special NonEmpty-BTGroupIdentifier: " + SpecialBalanceNonBTGroupIdentifierMap.size());
			System.out.println("MainBalance NonEmpty-BTGroupIdentifier: " + MainBalanceNonBTGroupIdentifierMap.size());

			/*System.out.println("----------Count of Mapping Data for Dedicated Account------------");
			System.out.println("Empty-BT-GroupIdentifier for DA: " + BalanceEmptyBTGroupIdentifierMapForDA.size());
			System.out.println("NonEmpty-BTGroupIdentifier for DA: " + BalanceNonEmptyBTGroupIdentifierMapForDA.size());
			System.out.println("Special NonEmpty-BTGroupIdentifier for DA: " + SpecialBalanceNonBTGroupIdentifierMapForDA.size());
			System.out.println("MainBalance NonEmpty-BTGroupIdentifier for DA: " + MainBalanceNonBTGroupIdentifierMapForDA.size());
			
			System.out.println("----------Count of Mapping Data for Usage Counter------------");
			System.out.println("Empty-BT-GroupIdentifier for UC: " + BalanceNonEmptyBTGroupIdentifierMapForUC.size());
			
			System.out.println("----------Count of Mapping Data for Accumulator------------");
			System.out.println("Empty-BT-GroupIdentifier for Acc: " + BalanceEmptyBTGroupIdentifierMapForAcc.size());
			System.out.println("NonEmpty-BT-GroupIdentifier for Acc: " + BalanceNonEmptyBTGroupIdentifierMapForAcc.size());
			
			System.out.println("----------Count of Mapping Data for Usage Counter------------");
			System.out.println("Empty-BT-GroupIdentifier for UC: " + BalanceEmptyBTGroupIdentifierMapForOffAttr.size());
			System.out.println("NonEmpty-BT-GroupIdentifier for UC: " + BalanceNonEmptyBTGroupIdentifierMapForOffAttr.size());*/
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.error("Exception occured ", e);
		}
	}
	
	
	public void GetPCitemGroup(String dataFolderPath)
	{
		IBindingFactory bindingFactoryBalance = null;
		IUnmarshallingContext unmarshallingContextBalance;
		Map<String, Set<String>> PBTGroup = new HashMap<>();
		TreeSet<String> PCSGroup = new TreeSet<>();
		try {
			bindingFactoryBalance = BindingDirectory.getFactory(com.ericsson.jibx.beans.BALANCEMAPPINGLIST.class);
			unmarshallingContextBalance = bindingFactoryBalance.createUnmarshallingContext();
			BALANCEMAPPINGLIST balancelist = (com.ericsson.jibx.beans.BALANCEMAPPINGLIST) unmarshallingContextBalance
					.unmarshalDocument(new ByteArrayInputStream(
							FileUtils.readFileToByteArray(new File(dataFolderPath + "/BalanceMapping.xml"))), null);
			
			for (BALANCEMAPPINGINFO balanceInfo : balancelist.getBALANCEMAPPINGINFOList()) {
				
				String BT_Type = 		balanceInfo.getBTTYPE();
				if(BT_Type.equals("P"))
				{
					String BT_ID = balanceInfo.getBTID();
					Set<String> GroupName = new HashSet<>();
					for (BALANCEMAPPINGINFO tempbalanceInfo : balancelist.getBALANCEMAPPINGINFOList()) {
						if(BT_ID.equals(tempbalanceInfo.getBTID()))
						{
							GroupName.add(tempbalanceInfo.getBTGroupIdentifier());
							if(!tempbalanceInfo.getBTGroupIdentifier().startsWith("G"))
							{
								PCSGroup.add(tempbalanceInfo.getBTGroupIdentifier());
							}
						}
					}
					PBTGroup.put(balanceInfo.getBTID(),GroupName);
				}
				
			}
		
		//Collections.sort(PCSGroup);
		//PBTGroup.forEach((k,v)->System.out.println(k + "----" + v));
		//PCSGroup.forEach(x->System.out.println(x));
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.error("Exception occured ", e);
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String sdpid, configPath, dataFolderPath, workingMode = null;
		
		dataFolderPath = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\data";
		configPath = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\config\\config";
		workingMode = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\working";
		//String filesToBeInserted = "INFILE_Subscriber_Balances.csv.gz,INFILE_Subscriber_cug_cli.csv.gz,INFILE_Subscriber_USMS.csv.gz";
		//String filesToBeInserted = "sdp01_subscriber_balances_dump.csv.gz,sdp01_subscriber_cugcli_dump.csv.gz,sdp01_subscriber_usms_dump.csv.gz";
		sdpid = "LOAD_MAPPING";
		
		LoadSubscriberMapping lsm = new LoadSubscriberMapping(sdpid, configPath, dataFolderPath, workingMode);

		lsm.GetPCitemGroup(dataFolderPath);
		//lsm.GetPCitemAGroup(dataFolderPath);
		System.out.println("----------Count of Mapping Data for Offer------------");
		System.out.println("Empty-BT-GroupIdentifier: " + BalanceEmptyBTGroupIdentifierMap.size());
		System.out.println("NonEmpty-BTGroupIdentifier: " + BalanceNonEmptyBTGroupIdentifierMap.size());
		System.out.println("Special NonEmpty-BTGroupIdentifier: " + SpecialBalanceNonBTGroupIdentifierMap.size());
		System.out.println("MainBalance NonEmpty-BTGroupIdentifier: " + MainBalanceNonBTGroupIdentifierMap.size());
		System.out.println("MainBalance NonEmpty-BTGroupIdentifier: " + BalanceOnlyASGroupMap.size());
		System.out.println("MainBalance NonEmpty-BTGroupIdentifier: " + BalanceOnlyAMGroupMap.size());
		
		System.out.println("----------Count of ProfileInput------------");
		System.out.println("Count of ProfileInput: " + inc6001ProfileTag.size());
		//NPPLifeCycleMap.forEach((k,v)->System.out.println(k+"----"+v));
		CompleteMappingBalanceValues.forEach((k,v)->System.out.println(k+"----"+v));

		//SpecialGraceList.forEach(k->System.out.println(k));
		/*System.out.println("----------Count of Mapping Data for Dedicated Account------------");
		System.out.println("Empty-BT-GroupIdentifier for DA: " + BalanceEmptyBTGroupIdentifierMapForDA.size());
		System.out.println("NonEmpty-BTGroupIdentifier for DA: " + BalanceNonEmptyBTGroupIdentifierMapForDA.size());
		System.out.println("Special NonEmpty-BTGroupIdentifier for DA: " + SpecialBalanceNonBTGroupIdentifierMapForDA.size());
		System.out.println("MainBalance NonEmpty-BTGroupIdentifier for DA: " + MainBalanceNonBTGroupIdentifierMapForDA.size());
		
		System.out.println("----------Count of Mapping Data for Usage Counter------------");
		System.out.println("Empty-BT-GroupIdentifier for UC: " + BalanceNonEmptyBTGroupIdentifierMapForUC.size());
		
		System.out.println("----------Count of Mapping Data for Accumulator------------");
		System.out.println("Empty-BT-GroupIdentifier for Acc: " + BalanceEmptyBTGroupIdentifierMapForAcc.size());
		System.out.println("NonEmpty-BT-GroupIdentifier for Acc: " + BalanceNonEmptyBTGroupIdentifierMapForAcc.size());
		
		System.out.println("----------Count of Mapping Data for Usage Counter------------");
		System.out.println("Empty-BT-GroupIdentifier for UC: " + BalanceEmptyBTGroupIdentifierMapForOffAttr.size());
		System.out.println("NonEmpty-BT-GroupIdentifier for UC: " + BalanceNonEmptyBTGroupIdentifierMapForOffAttr.size());*/
		/*for(Set<String> valueList : LoadSubscriberMapping.BalanceGroupingMap.values()) {
			System.out.println(LoadSubscriberMapping.getKey(LoadSubscriberMapping.BalanceGroupingMap, valueList));
			System.out.println(valueList);

		}*/
		
		//BalanceProductID.forEach((k,v)->System.out.println(k+"----"+v));
		//Map<String,List<String>>DA_ProductID = new HashMap<>();
		//DA_ProductID.putAll(BalanceProductID.get("DAValue"));
		//System.out.println("8888888888888888888888" + DA_ProductID.size());
		//DA_ProductID.forEach((k,v)->System.out.println(k+"----"+v));
		
		/*OnlyVBalancesMap.forEach((k,v)->System.out.println(k+"----"+v));
		System.out.println("8888888888888888888888");*/
		//GraceFullBalanceGroupingMap.forEach((k,v)->System.out.println(k+"----"+v));
		//System.out.println("------------------");
		//OnlyVBalancesValueSet.forEach(k->System.out.println(k));
		//TreeMap<String, Map<String,List<String>>> sorted = new TreeMap<>(BalanceProductID); 
		/*GGroupBalanceGroupingMap.forEach((k,v)->System.out.println(k + "----" + v));
		GSGroupBalanceGroupingMap.forEach((k,v)->System.out.println(k + "****" + v));
		GSBalanceGroupMap.forEach((k,v)->System.out.println(k + "........." + v));
		SpecialBalanceNonBTGroupIdentifierMap.forEach((k,v)->System.out.println(k + "`````````" + v));
		OnlyGVBalancesValueMap.forEach((k,v)->System.out.println(k + "++++" + v));
		
		
		SpecialGGroupBalance.forEach((k,v)->System.out.println(k + "----" + v));
		SpecialGGroupList.forEach((k)->System.out.println(k ));
		
		BalanceGroupingMap.forEach((k,v)->System.out.println(k + "=====" + v));*/
		
		SpecialBalanceNonBTGroupIdentifierMap.forEach((k,v)->System.out.println(k + "`````````" + v));
	}
	
}
