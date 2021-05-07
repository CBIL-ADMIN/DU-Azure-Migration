package com.ericsson.LoadInputDump;

public class Main_Method {

	public static void main(String[] args) {
		
		String dumpFilePath = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\input";
		String schemaFilePath = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\config\\schema";
		String mapdbPath = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\working";
		String workingPath = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\";
		//String filesToBeInserted = "INFILE_Subscriber_Balances.csv.gz,INFILE_Subscriber_cug_cli.csv.gz,INFILE_Subscriber_USMS.csv.gz";
		//String filesToBeInserted = "sdp01_subscriber_balances_dump.csv.gz,sdp01_subscriber_cugcli_dump.csv.gz,sdp01_subscriber_usms_dump.csv.gz";
		String sdpId1 = "sdp01_subscriber_balances_dump.csv.gz,sdp01_subscriber_cugcli_dump.csv.gz,sdp01_subscriber_usms_dump.csv.gz,sdp01_subscriber_be_dump.csv.gz";
		// TODO Auto-generated method stub
		//LoadInputDump lid = new LoadInputDump(dumpFilePath, schemaFilePath , mapdbPath,sdpId);
		String sdpId = sdpId1.split("_")[0];
		LoadInputDump.execute(dumpFilePath, schemaFilePath,  mapdbPath,workingPath);
		LoadInputDump.InitializeBtree();
		

		System.out.println("Going to get value from schemasubscriberbalancesdump");
		//HTreeMap<String, String> btree_balance = LoadInputDump.mapDbDatabase.get("schemasubscriberbalancesdump");
		//HTreeMap<String, String> btree_cug = LoadInputDump.mapDbDatabase.get("schemasubscribercugclidump");
		//HTreeMap<String, String> btree_usms = LoadInputDump.mapDbDatabase.get("schemasubscriberusmsdump");
		//System.out.println(btreee);
		
		//String xml_balance = "<balancesdump_info>" + btree_balance.get("971529761294") + "</balancesdump_info>";
		//String xml_cug = "<cugclidump_info>" + btree_cug.get("971529761294") + "</cugclidump_info>";
		//String xml_usms = "<usmsdump_info>" + btree_usms.get("971529761294") + "</usmsdump_info>";
		//System.out.println(xml_balance + xml_cug + xml_usms);
		
		LoadInputDump lid = new LoadInputDump();
		//LoadInputDump.InitializeBtree();
		
		
		System.out.println(lid.GetXML("971779383469"));
		//System.out.println(lid.GetXML("971559541812"));
		//System.out.println(lid.GetXML("971551715856"));
		
		LoadInputDump.clearDB();
		
		
		
		//Map<String, List<String>> mapOfTablesAndColumns = lid.parseSchemaCsvFiles();
		
	}

}
