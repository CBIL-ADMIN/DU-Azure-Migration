package com.ericsson.dm.transform.implementation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

public class AddHeaderToCIS {

	private String OutputFilePath;
	private String NotificationFileName;
	private String ParkingFileName;
	private String OnceOffFileName;
	private String RenewalFileName;
	
	public AddHeaderToCIS()
	{
		
	}

	public AddHeaderToCIS(String OutputFilePath, String ParkingFileName, String OnceOffFileName, String RenewalFileName, String NotificationFileName)
	{
		this.OutputFilePath = OutputFilePath;
		this.NotificationFileName = NotificationFileName;
		this.ParkingFileName = ParkingFileName;
		this.OnceOffFileName = OnceOffFileName;
		this.RenewalFileName = RenewalFileName;
	}
	
	public void AppendNotificationHeader(String FileName)
	{
		String header = "id|msisdn|product_id|an_br_config|base_date|count|language_id|next_notification_date|segment_id|enable_notification";
		File File = new File(FileName);
		List<String> lines;
		try 
		{
			lines = FileUtils.readLines(File).stream().collect(Collectors.toList());
			lines.add(0, header);
			FileUtils.writeLines(File, lines);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void AppendParkingHeader(String FileName)
	{
		String header = "id|msisdn|product_id|circle_id|status|expiry_date|start_date|product_type|service_name|last_action_date|product_description|pay_src|ben_msisdn|product_cost|srcchannel|offer_id|product_category|bundle_name|base_bundle_name|product_purchase_type|language_id|pre_notif_status|post_notif_status|retry_limit|priority|processing_state|send_sms|segment_id|previous_status|pre_notification_count|post_notification_count|status_change_time|subscription_mode|extra_param";
		File File = new File(FileName);
		List<String> lines;
		try 
		{
			lines = FileUtils.readLines(File).stream().collect(Collectors.toList());
			lines.add(0, header);
			FileUtils.writeLines(File, lines);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void AppendOnceOffHeader(String FileName)
	{
		String header = "id|msisdn|product_id|status|pre_notification_count|post_notification_count|expiry_date|start_date|product_type|service_name|last_action_date|product_description|is_pam_product|pay_src|ben_msisdn|send_sms|split_no|product_cost|pam_id|enable_notification|renewal_value|srcchannel|offer_id|product_category|bundle_name|product_purchase_type|language_id|pre_notif_status|post_notif_status|segment_id|deprov_status|retry_limit|gifted_by|priority|previous_status|correlation_id|network_status|status_change_time|extra_param";
		File File = new File(FileName);
		List<String> lines;
		try 
		{
			lines = FileUtils.readLines(File).stream().collect(Collectors.toList());
			lines.add(0, header);
			FileUtils.writeLines(File, lines);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void AppendRenewalHeader(String FileName)
	{
		String header = "Msisdn|renewal_count|grace_date|last_action_date|last_renewal_date|renewal_date|status|processing_state|process_timestamp|activation_date|circle_id|product_id|id|product_description|split_action|renewal_value|expiry_notification_flag|product_type|pre_notification_count|post_notification_count|marketing_text|pre_marketing_text_enabled|post_marketing_text_enabled|retry_limit|is_pam_product|pay_src|ben_msisdn|send_sms|split_no|product_cost|pam_id|enable_notification|recurringgraceperiod|srcchannel|offer_id|product_category|bundle_name|product_purchase_type|language_id|renewal_status|pre_notif_status |post_notif_status |segment_id |deprov_retry_limit|deprov_status|base_bundle_name |gifted_by|priority |pre_grace_exp_notif_status |renewal_num |previous_status|correlation_id|network_status|status_change_time|is_grace_chargeable|extra_param|On_Grace_Network_Deact_Enabled";
		File File = new File(FileName);
		List<String> lines;
		try 
		{
			lines = FileUtils.readLines(File).stream().collect(Collectors.toList());
			lines.add(0, header);
			FileUtils.writeLines(File, lines);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void execute()
	{
		//Append all the files header
				
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String filepath = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\output\\";
		
		String file1 = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\output\\CIS_Parking_Products.csv";
		String file2 = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\output\\CIS_OnceOff_Bundles.csv";
		String file3 = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\output\\CIS_Renewal_Bundles.csv";
		String file4 = "C:\\Ericsson\\MyWorkingProject\\Charging_System\\dm_cs_2018_du_dubai\\dev\\src\\output\\CIS_Notification.csv";
		
		//AddHeaderToCIS ahc = new AddHeaderToCIS(filepath, "CIS_Parking_Products.csv", "CIS_OnceOff_Bundles.csv", "CIS_Renewal_Bundles.csv", "CIS_Notification.csv");
		AddHeaderToCIS ahc = new AddHeaderToCIS();
		
		//ahc.AppendNotificationHeader(ahc.OutputFilePath, ahc.NotificationFileName);
		ahc.AppendNotificationHeader(file4);
		ahc.AppendOnceOffHeader(file2);
		ahc.AppendParkingHeader(file1);
		ahc.AppendRenewalHeader(file3);
		
		file1.contains("asas");
		
	}

}
