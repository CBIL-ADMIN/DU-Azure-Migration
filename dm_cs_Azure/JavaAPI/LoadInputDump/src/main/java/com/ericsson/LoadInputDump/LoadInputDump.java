package com.ericsson.LoadInputDump;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import com.google.common.collect.Lists;

public class LoadInputDump {	
	
	protected static DB mapDbDatabase;
	protected static String SDPName;
	private String sdpId;
	protected static Map<String, List<String>> entityColumnsMap;
	private String dumpFilePath;
	private String schemaFilePath;
	private String mapdbPath;
	
	
	private static HTreeMap<String, String> btree_balance01;
	private static HTreeMap<String, String> btree_balance02;
	private static HTreeMap<String, String> btree_balance03;
	private static HTreeMap<String, String> btree_balance04;
	private static HTreeMap<String, String> btree_cug;
	private static HTreeMap<String, String> btree_usms;
	private static HTreeMap<String, String> btree_profile;

		
	public LoadInputDump(String dumpFilePath, String schemaFilePath, String mapdbPath,String sdpId) {
		this.sdpId = sdpId;
		SDPName = sdpId;
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		// String currentDate = sdf.format(new Date());
		this.dumpFilePath = dumpFilePath;
		this.schemaFilePath = schemaFilePath;
		this.mapdbPath = mapdbPath;
		
		entityColumnsMap = parseSchemaCsvFiles();		
		mapDbDatabase = DBMaker.fileDB(new File(this.mapdbPath + "/db_" + this.sdpId)).fileMmapEnable().make();

	}
	
	public LoadInputDump() {
		// TODO Auto-generated constructor stub
	}

	public Map<String, List<String>> parseSchemaCsvFiles() {
		Map<String, List<String>> mapOfTablesAndColumns = new LinkedHashMap<String, List<String>>();
		//System.out.println(schemaFilePath);
		File dir = new File(this.schemaFilePath);
		
		//System.out.println(this.schemaFilePath);
		
		//List<File> list = Arrays.asList(dir.listFiles()); 
		
		
		
		List<File> list = (List<File>)Arrays.asList(dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("schema_") && name.endsWith(".csv"); // or
				// something
				// else
			}
		}));
		
		
		for (File file : list) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = "";
				List<String> columns = new ArrayList<String>();
				while ((line = br.readLine()) != null) {
					columns.add(line);
					/*if (line.startsWith("c;")) {
						String temp[] = line.split(";");
						
						if (file.getName().endsWith("subscriber.csv")) {
							columns.add(temp[1]);
						} else {
							if (!"time_stamp".equals(temp[1].trim())) {
								columns.add(temp[1]);
							}
						}
					}*/
				}
				br.close();
				String tableName = file.getName();
				//tableName = tableName.substring(tableName.indexOf('.', 16) + 1, tableName.length());
				mapOfTablesAndColumns.put(tableName.replaceAll("_", "").replaceAll(".csv", ""), columns);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// LOG.error(e);
			}
		}
       // System.out.println(mapOfTablesAndColumns.get("subscriber"));
		return mapOfTablesAndColumns;

	}
	
	//protected static void loadIntoMapDB(DB db, String entity, File fullFile) throws Exception {
	protected static void loadIntoMapDB(DB db, String entity, File fullFile, String tmpFilePath) throws Exception {
		// TODO Auto-generated method stub
		// System.out.println("INVOKED loadIntoMapDB");
		
		//HTreeMap<Object, Object> hTree = db.createHashMap(entity).keySerializer(Serializer.STRING)
		//		.valueSerializer(Serializer.STRING).make();
		
		long start = Calendar.getInstance().getTimeInMillis();
		System.out.println("starting File: " + fullFile + " at: " + LocalTime.now());
		HTreeMap<String, String> hTree = db.hashMap(entity).keySerializer(Serializer.STRING)
				.valueSerializer(Serializer.STRING).create();
		List<String> columnList = entityColumnsMap.get(entity);
		GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(fullFile));
		BufferedReader br = new BufferedReader(new InputStreamReader(gzip));
		
		
		//This code is special case and not needed in other project as i need to prepare Unique List of MSISDN for DU dubai
		File uniqueFile = new File(tmpFilePath + "unique" + fullFile.getName().toString().split("\\.")[0].replace(SDPName, "") + ".csv");
		Set<String> uniqueMsisdn = Collections.synchronizedSet(new HashSet<String>());
		//till here
		
		String line;
		while ((line = br.readLine()) != null) {
			String arr[] = line.split("\\|", -1);
						
			String msisdn = arr[0];
			//System.out.println(msisdn + "-------" + line);
			if (hTree.containsKey(msisdn)) {
				hTree.put(msisdn, hTree.get(msisdn) + formXml(columnList, arr, entity));				
			} else {
				uniqueMsisdn.add(msisdn);
				hTree.put(msisdn, formXml(columnList, arr, entity));
			}
		}
		br.close();
		// hTree.close();;
		
		//This code is special case and not needed in other project as i need to prepare Unique List of MSISDN for DU dubai
		if (!uniqueFile.exists()) {
			uniqueFile.createNewFile();
		}
		FileUtils.writeLines(uniqueFile, uniqueMsisdn, true);
		//till here
		
		
		System.out.println("Finished File: " + fullFile + "at: " + LocalTime.now());
		long totalTime = Calendar.getInstance().getTimeInMillis() - start;
		System.out.println("Total time for file :"+ fullFile +" is: "+ totalTime);
	}
	
	private static String formXml(List<String> columnList, String data[], String entity) throws Exception {		
	  	if (data.length != columnList.size()) {
			
			throw new Exception("Wrong format of file in sdp schema " + entity + " for msisdn " + data[0] +" with data length " + 
			data.length + " and coulmn length is " + columnList.size());
		}
	  	
	  	if(entity.startsWith("schemasubscriberbalancesdump"))
	  		entity = "schemasubscriberbalancesdump";
	  	
		StringBuffer sb = new StringBuffer("<").append(entity).append("_info").append(">");
		int index = 0;
		for (String column : columnList) {
			sb.append("<").append(column).append(">").append(data[index]).append("</").append(column).append(">");
			index++;
		}
		sb.append("</").append(entity).append("_info").append(">");
		String result = sb.toString();
		sb = null;
		return result;
	}


	public static void execute(String dumpFilePath, String schemaFilePath, String mapdbPath, String migToolPath) {
	
		//dumpFilePath = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\input";
		//schemaFilePath = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\config\\schema";
		//mapdbPath = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\working";
		//filesToBeInserted = "sdp01_subscriber_balances_dump.csv.gz,sdp01_subscriber_cugcli_dump.csv.gz,sdp01_subscriber_usms_dump.csv.gz";
		//sdpId = "sdp01";
	
		String filesToBeInserted = getInputFile(dumpFilePath);
		String sdpId = filesToBeInserted.split("_")[0];
	
		LoadInputDump obj = new LoadInputDump(dumpFilePath, schemaFilePath, mapdbPath, sdpId);
		
		File folder = new File(obj.dumpFilePath);
		ArrayList<String> listOfFilesToBeInserted = new ArrayList<String>(Arrays.asList(filesToBeInserted.split(",")));
	
		int cores = Runtime.getRuntime().availableProcessors();
		//int cores = 29;
		if (listOfFilesToBeInserted.size() < cores) {
			cores = listOfFilesToBeInserted.size();
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(cores - 1);
		List<File> listOfFiles = new ArrayList<File>(Arrays.asList(folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {	
				return name.endsWith(".gz"); 
				// or // something // else
			}
		})));
		
		List<List<String>> smallerLists = Lists.partition(listOfFilesToBeInserted,
				listOfFilesToBeInserted.size() / (cores - 1));
		/*
		 * System.out.println(smallerLists); System.out.println("Started :" +
		 * new java.util.Date() + " with cores = " + cores);
		 */ long start = Calendar.getInstance().getTimeInMillis();
		 
		 
		for (int i = 0; i < cores; i++) {
			Runnable worker = new WorkerThreadForMapdb(listOfFiles, smallerLists.get(i),sdpId,migToolPath);
			executor.execute(worker);
		}
		
		executor.shutdown();
		
		while (!executor.isTerminated()) {
		
		}
					
		
		listOfFilesToBeInserted.clear();
		listOfFiles.clear();
		smallerLists.clear();
		listOfFilesToBeInserted = null;
		listOfFiles = null;
		smallerLists = null;
		mapDbDatabase.commit();	
		System.out.println("Finished all threads");
		System.out.println("Ended :" + new java.util.Date());
		long totalTime = Calendar.getInstance().getTimeInMillis() - start;
		System.out.println("Total time :" + totalTime);
	
	}
	
	public static void InitializeBtree()
	{
		btree_balance01 = LoadInputDump.mapDbDatabase.get("schemasubscriberbalancesdump01");
		btree_balance02 = LoadInputDump.mapDbDatabase.get("schemasubscriberbalancesdump02");
		btree_balance03 = LoadInputDump.mapDbDatabase.get("schemasubscriberbalancesdump03");
		btree_balance04 = LoadInputDump.mapDbDatabase.get("schemasubscriberbalancesdump04");
		btree_cug = LoadInputDump.mapDbDatabase.get("schemasubscribercugclidump");
		btree_usms = LoadInputDump.mapDbDatabase.get("schemasubscriberusmsdump");
		btree_profile = LoadInputDump.mapDbDatabase.get("schemasubscriberprofiledump");
	}

	public String GetXML(String MSISDN)
	{			
		String xml_balance = "<balancesdump_info>" + btree_balance01.get(MSISDN) + btree_balance02.get(MSISDN) + btree_balance03.get(MSISDN) + btree_balance04.get(MSISDN) +"</balancesdump_info>";
		String xml_cug = "<cugclidump_info>" + btree_cug.get(MSISDN) + "</cugclidump_info>";
		String xml_usms = "<usmsdump_info>" + btree_usms.get(MSISDN) + "</usmsdump_info>";
		String xml_profile = "<profiledump_info>" + btree_profile.get(MSISDN) + "</profiledump_info>";
		
		return xml_balance + xml_cug + xml_usms + xml_profile;
	}
	

	public static void clearDB()
	{
		mapDbDatabase.close();		
		entityColumnsMap.clear();		
		entityColumnsMap = null;		
	}
	
	private static String getInputFile(String dumpFilePath) {
		File folder = new File(dumpFilePath);		
		File[] listOfFiles = folder.listFiles();
		String FileName = "";
		
		for (int i = 0; i < listOfFiles.length; i++) 
		{
		  if (listOfFiles[i].isFile()) 
		  {
			  System.out.println(listOfFiles[i].getName());
			  if (!listOfFiles[i].getName().contains("_subscriber_be_dump.csv.gz"))
			  {
				 FileName += listOfFiles[i].getName().trim() + ',';
			  }
			  //results.add(listOfFiles[i].getName().trim());
		  }
		}		
		return FileName.substring(0,FileName.length()-1).toString();
	}
	
	public void printBtree()
	{
		//LoadInputDump.mapDbDatabase.get
	}
}
