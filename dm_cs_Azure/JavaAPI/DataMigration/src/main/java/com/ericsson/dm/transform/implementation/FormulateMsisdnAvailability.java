package com.ericsson.dm.transform.implementation;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

public class FormulateMsisdnAvailability {

	//private String migToolPath;
	private String logPath;
	private String tmpPath;
	private Set<String> totalUniqueMsisdn;
	private Set<String> BeMsisdn;
	private Set<String> BalancesMsisdn;
	private Set<String> CUGMsisdn;
	private Set<String> USMSMsisdn;
	private Set<String> ProfileMsisdn;
	
	public FormulateMsisdnAvailability(String migToolPath)
	{
		//this.migToolPath = migToolPath;
		this.logPath = migToolPath + "logs/";
		this.tmpPath = migToolPath + "tmp/";
		totalUniqueMsisdn = Collections.synchronizedSet(new HashSet<String>());
		BeMsisdn = Collections.synchronizedSet(new HashSet<String>());
		BalancesMsisdn = Collections.synchronizedSet(new HashSet<String>());
		CUGMsisdn = Collections.synchronizedSet(new HashSet<String>());
		USMSMsisdn = Collections.synchronizedSet(new HashSet<String>());
		ProfileMsisdn = Collections.synchronizedSet(new HashSet<String>());
	}
	
	private void populateInput() throws IOException
	{
		//read the full list
		File uniqueFile = new File(tmpPath+ "unique_total.csv");
		File INC1003File = new File(tmpPath+ "inc1003_total.csv");
		totalUniqueMsisdn = FileUtils.readLines(uniqueFile).stream().collect(Collectors.toSet());
		
		uniqueFile = new File(tmpPath+ "unique_be.csv");		
		BeMsisdn = FileUtils.readLines(uniqueFile).stream().collect(Collectors.toSet());
		
		uniqueFile = new File(tmpPath+ "unique_balances.csv");		
		BalancesMsisdn = FileUtils.readLines(uniqueFile).stream().collect(Collectors.toSet());
	
		uniqueFile = new File(tmpPath+ "unique_cugcli.csv");		
		CUGMsisdn = FileUtils.readLines(uniqueFile).stream().collect(Collectors.toSet());
		
		uniqueFile = new File(tmpPath+ "unique_usms.csv");		
		USMSMsisdn = FileUtils.readLines(uniqueFile).stream().collect(Collectors.toSet());
		
		uniqueFile = new File(tmpPath+ "unique_profile.csv");		
		ProfileMsisdn = FileUtils.readLines(uniqueFile).stream().collect(Collectors.toSet());	
	}
	
	private void generateOutput()
	{
		Set<String> uniqueMsisdn = Collections.synchronizedSet(new HashSet<String>());
		Set<String> inc1003Msisdn = Collections.synchronizedSet(new HashSet<String>());
		File uniqueFile = new File(logPath + "unique_msisdn.csv");
		File inc1003File = new File(logPath + "INC1003.log");
		try 
		{
			for(String msisdn: totalUniqueMsisdn)
			{
				String isBEPresent = "N";
				String isProfilePresent = "N";
				String isUSMSPresent = "N";
				String isBalancePresent = "N";
				String isCUGPresent = "N";
				
				if(BalancesMsisdn.contains(msisdn))
					isBalancePresent = "Y";
				if(CUGMsisdn.contains(msisdn))
					isCUGPresent = "Y";
				if(BeMsisdn.contains(msisdn))
					isBEPresent = "Y";
				if(ProfileMsisdn.contains(msisdn))
					isProfilePresent = "Y";
				if(USMSMsisdn.contains(msisdn))
					isUSMSPresent = "Y";
		
				//uniqueMsisdn.add("MSISDN"  ",BE" ",USMS" + ",PROFILE" + ",BALANCE"  + ",CUG");
				if(!(isBalancePresent == "Y" && isCUGPresent == "Y" && isBEPresent == "Y" && isProfilePresent == "Y" && isUSMSPresent == "Y"))
					uniqueMsisdn.add(msisdn + "," + isBEPresent + "," + isUSMSPresent + "," + isProfilePresent + "," + isBalancePresent + "," + isCUGPresent);
			
				/*
				   BE	USMS	PROFILE	BALANCES	CUG	Logging
					N	N		Y		N			N	INC1003
					N	Y		N		N			N	INC1003
					N	Y		Y		N			N	INC1003
					N	Y		Y		N			Y	INC1003
					Y	N		N		Y			N	INC1003
					Y	Y		N		Y			N	INC1003
				 */
				
				/*if((isBEPresent == "N" && isUSMSPresent == "N" && isProfilePresent == "Y" && isBalancePresent == "N" && isCUGPresent == "N"))
					inc1003Msisdn.add(msisdn + "," + isBEPresent + "," + isUSMSPresent + "," + isProfilePresent + "," + isBalancePresent + "," + isCUGPresent);
				else if((isBEPresent == "N" && isUSMSPresent == "Y" && isProfilePresent == "N" && isBalancePresent == "N" && isCUGPresent == "N"))
					inc1003Msisdn.add(msisdn + "," + isBEPresent + "," + isUSMSPresent + "," + isProfilePresent + "," + isBalancePresent + "," + isCUGPresent);
				else if((isBEPresent == "N" && isUSMSPresent == "Y" && isProfilePresent == "Y" && isBalancePresent == "N" && isCUGPresent == "N"))
					inc1003Msisdn.add(msisdn + "," + isBEPresent + "," + isUSMSPresent + "," + isProfilePresent + "," + isBalancePresent + "," + isCUGPresent);
				else if((isBEPresent == "N" && isUSMSPresent == "Y" && isProfilePresent == "Y" && isBalancePresent == "N" && isCUGPresent == "Y"))
					inc1003Msisdn.add(msisdn + "," + isBEPresent + "," + isUSMSPresent + "," + isProfilePresent + "," + isBalancePresent + "," + isCUGPresent);
				else if((isBEPresent == "Y" && isUSMSPresent == "N" && isProfilePresent == "N" && isBalancePresent == "Y" && isCUGPresent == "N"))
					inc1003Msisdn.add(msisdn + "," + isBEPresent + "," + isUSMSPresent + "," + isProfilePresent + "," + isBalancePresent + "," + isCUGPresent);
				else if((isBEPresent == "Y" && isUSMSPresent == "Y" && isProfilePresent == "N" && isBalancePresent == "Y" && isCUGPresent == "N"))
					inc1003Msisdn.add(msisdn + "," + isBEPresent + "," + isUSMSPresent + "," + isProfilePresent + "," + isBalancePresent + "," + isCUGPresent);
				*/
				
				if(!(isBEPresent == "Y" && isUSMSPresent == "Y" && isProfilePresent == "Y"))
					inc1003Msisdn.add("INC1003:Subscriber relation missing:MSISDN=" + msisdn + ":BE=" + isBEPresent + ":USMS=" + isUSMSPresent + ":PROFILE=" + isProfilePresent +":ACTION:Discard and Log");
			}
		
			FileUtils.writeLines(uniqueFile, uniqueMsisdn, true);
			FileUtils.writeLines(inc1003File, inc1003Msisdn, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public void execute()
	{
		try {
			System.out.println("Start of preparation of unique msisdn file at: " + LocalTime.now());
			
			populateInput();
			generateOutput();
			
			System.out.println("Unique msisdn count: " + totalUniqueMsisdn.size());
			System.out.println("Finished preparation of unique msisdn file at: " + LocalTime.now());
			
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BigInteger bi = new BigInteger("97180020158002526667");
		
	
		
		String migToolPath = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\";
		
		FormulateMsisdnAvailability fma = new FormulateMsisdnAvailability(migToolPath);
		fma.execute();
		
		
	}

}
