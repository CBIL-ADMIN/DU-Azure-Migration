
package com.ericsson.LoadInputDump;

import java.io.File;
import java.util.Collections;
import java.util.List;


public class WorkerThreadForMapdb implements Runnable {
	private List<File> listOfFiles;
	private List<String> listOfFilesToBeInserted;
	private String sdpid;
	private String tmpFilePath;
	
//	final static Logger LOG = Logger.getLogger(SDPDumpTool.class);

	WorkerThreadForMapdb(List<File> listOfFiles, List<String> listOfFilesToBeInserted, String sdpid, String migToolPath) {
		this.listOfFiles = listOfFiles;
		this.listOfFilesToBeInserted = listOfFilesToBeInserted;
		this.sdpid = sdpid;
		this.tmpFilePath = migToolPath + "working/";
		//Sdp2Mapdb obj = new Sdp2Mapdb(dumpFilePath, schemaFilePath, mapdbPath, sdpId);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		for (File file : listOfFiles) {
			Collections.sort(listOfFilesToBeInserted);
			if (file.isFile() && file.length() > 0 && (Collections.binarySearch(listOfFilesToBeInserted, file.getName())>=0)) {
				//System.out.println(listOfFilesToBeInserted);
				//System.out.println( file.getName());
				System.out.println(listOfFilesToBeInserted);
				System.out.println(file.getName());
				//System.out.println(Collections.binarySearch(listOfFilesToBeInserted, file.getName()));
				
				if (LoadInputDump.entityColumnsMap.get(file.getName().toLowerCase().replaceAll(".csv.gz", "").replaceAll("_","").replaceAll(this.sdpid, "schema")) == null) {
                      System.out.println(file.getName());
                      System.out.println(LoadInputDump.entityColumnsMap.keySet());
					// Log error Details in the read me file does not exist,
					// Kindly load in manually
				} else {
					
					try {
						LoadInputDump.loadIntoMapDB(LoadInputDump.mapDbDatabase,file.getName().toLowerCase().replaceAll(".csv.gz", "").replaceAll("_","").replaceAll(this.sdpid, "schema"),file, this.tmpFilePath);
						LoadInputDump.mapDbDatabase.commit();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
	}
	public void run_actual() {
		// TODO Auto-generated method stub
		for (File file : listOfFiles) {
			Collections.sort(listOfFilesToBeInserted);
			if (file.isFile() && file.length() > 0 && (Collections.binarySearch(listOfFilesToBeInserted, file.getName())>=0)) {
				//System.out.println(listOfFilesToBeInserted);
				//System.out.println( file.getName());
				System.out.println(listOfFilesToBeInserted);
				System.out.println(file.getName());
				//System.out.println(Collections.binarySearch(listOfFilesToBeInserted, file.getName()));
				
				if (LoadInputDump.entityColumnsMap.get(file.getName().toLowerCase().replaceAll(".csv.gz", "").replaceAll("_","").replaceAll(this.sdpid, "schema")) == null) {
                      System.out.println(file.getName());
                      System.out.println(LoadInputDump.entityColumnsMap.keySet());
					// Log error Details in the read me file does not exist,
					// Kindly load in manually
				} else {
					
					try {
						LoadInputDump.loadIntoMapDB(LoadInputDump.mapDbDatabase,file.getName().toLowerCase().replaceAll(".csv.gz", "").replaceAll("_","").replaceAll(this.sdpid, "schema"),file, this.tmpFilePath);
						LoadInputDump.mapDbDatabase.commit();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
	}
}
