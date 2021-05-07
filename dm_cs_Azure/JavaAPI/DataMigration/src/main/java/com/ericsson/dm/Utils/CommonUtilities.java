package com.ericsson.dm.Utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.xml.bind.DatatypeConverter;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;

import com.ericsson.dm.inititialization.LoadSubscriberMapping;

public class CommonUtilities {
	final static Logger LOG = Logger.getLogger(CommonUtilities.class);

	public static long convertDateToEpoch(String date) {
		try {
			if (date != null && !date.equals("null") && date.length() > 0) {
				if (date.startsWith("0")) {
					String temp[] = date.split("-");
					if (temp.length > 1) {
						int tempInt = 2000 + Integer.parseInt(temp[0]);
						date = tempInt + "-" + temp[1] + "-" + temp[2];
					} else {
						date = "2000-01-01 00:00:00";
					}
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				java.util.Date date1 = sdf.parse(date);
				java.util.Date date2 = sdf.parse("1970-01-01 00:00:00");

				String zone = "UTC";//Constants.DEFAULT_TIME_ZONE;
				zone = "".equals(zone) ? "UTC" : zone;

				// Here getDifferenceMillis will return negative or possitive
				// value depending on timezone.
				
				long days = ((date1.getTime() - getDifferenceMillis(date1, zone)) - date2.getTime())
						/ (1000 * 60 * 60 * 24);
			
				return days;
			} else {
				return 0;
			}
		} catch (Exception e) {

			e.printStackTrace();
			LOG.error(e);
		}
		return 0;

	}
	
	public static long convertDateToEpochSeconds(String date) {
		try {
			if (date != null && !date.equals("null") && date.length() > 0) {
				if (date.startsWith("0")) {
					String temp[] = date.split("-");
					if (temp.length > 1) {
						int tempInt = 2000 + Integer.parseInt(temp[0]);
						date = tempInt + "-" + temp[1] + "-" + temp[2];
					} else {
						date = "2000-01-01 00:00:00";
					}
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				java.util.Date date1 = sdf.parse(date);
				java.util.Date date2 = sdf.parse("1970-01-01 00:00:00");

				String zone = "UTC";//Constants.DEFAULT_TIME_ZONE;
				zone = "".equals(zone) ? "UTC" : zone;

				// Here getDifferenceMillis will return negative or possitive
				// value depending on timezone.
				
				long days = ((date1.getTime() - getDifferenceMillis(date1, zone)) - date2.getTime())
						/ (1000);
			
				return days;
			} else {
				return 0;
			}
		} catch (Exception e) {

			e.printStackTrace();
			LOG.error(e);
		}
		return 0;

	}

	public static String convertEpochDateToDate(int days) {
		String result = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date date2 = sdf.parse("1970-01-01 00:00:00");

			String zone = Constants.DEFAULT_TIME_ZONE;
			zone = "".equals(zone) ? "GMT" : zone;

			Calendar c = Calendar.getInstance();
			c.setTime(date2);

			c.add(Calendar.DATE, days);
			result = sdf.format(c.getTime());

		} catch (Exception e) {
			LOG.error(e);
		}
		return result;
	}

	public static Integer[] convertDateToTimerOfferDate(String date) {
		Integer[] data = new Integer[2];
		try {
			// String dateString = date;
			if (date != null && !date.equals("null") && date.length() > 0) {
				if (date.startsWith("0")) {
					String temp[] = date.split("-");
					if (temp.length > 1) {
						int tempInt = 2000 + Integer.parseInt(temp[0]);
						date = tempInt + "-" + temp[1] + "-" + temp[2];
					} else {
						date = "2000-01-01 00:00:00";
					}
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				java.util.Date date1 = sdf.parse(date);
				java.util.Date date2 = sdf.parse("1970-01-01 00:00:00");

				String zone = Constants.DEFAULT_TIME_ZONE;
				zone = "".equals(zone) ? "GMT" : zone;
				
				//System.out.println(getDifferenceMillis(date1, zone));

				// Here getDifferenceMillis will return negative or possitive
				// value
				// depending on timezone.
				long secs = ((date1.getTime() - getDifferenceMillis(date1, zone)) - date2.getTime()) / (1000);
				data = convertDate(secs);
				sdf = null;
				date1 = null;
				date2 = null;
				zone = null;
				return data;
			}
		} catch (Exception e) {

			e.printStackTrace();
			LOG.error(e);
			Integer[] res = { 0, 0 };
			return res;
		} finally {

		}

		return data;
	}

	public static long getDifferenceMillis(Date date, String zone) {
		long diffmilis = 0;
		// get offset milis
		TimeZone timeZone = TimeZone.getTimeZone(zone);
		int rawOffsetMillis = timeZone.getRawOffset();// It might be -ve or +ve
														// value
		// System.out.println("------------->>rawOffsetMillis :"+rawOffsetMillis
		// );

		// Check if the date provied is belongs to daylight saving if yes then
		// get saving duration
		boolean isDT = TimeZone.getTimeZone(zone).inDaylightTime(date);
		// System.out.println("------------->>isDT :"+isDT );
		int dstSavingMillis = 0;
		if (isDT) {
			dstSavingMillis = timeZone.getDSTSavings(); // for no. of milli
														// seconds
		}
		// System.out.println("------------->>dstSavingMillis :"+dstSavingMillis
		// );

		// time zone diff - Daylight saving
		diffmilis = rawOffsetMillis + dstSavingMillis;
		//System.out.println("DAYLIGHT::"+ dstSavingMillis+","+diffmilis);
		return diffmilis;
	}

	private static Integer[] convertDate(long date) {
		int constt = 32767;
		String bin = Long.toBinaryString(date);
		String value1 = bin.substring(0, bin.length() - 16);
		String value2 = bin.substring(bin.length() - 16);
		Integer[] output = new Integer[2];
		output[0] = Integer.parseInt(value1, 2);
		output[1] = value2.startsWith("0") ? Integer.parseInt(value2.substring(1), 2)
				: -1 * (constt - Integer.parseInt(value2.substring(1), 2) + 1);
		return output;
	}
	
	private static String returnActualDateFromTimerDate(long date , long secs) throws ParseException {
		int constt = 32767;
		String bin1 = Long.toBinaryString(date);
		String bin2 = "";
		if(secs<0){
			secs=secs*-1;
			String str = Integer.toBinaryString((int)secs);//.substring(1);
			secs = Long.parseLong(str, 2);
			secs = constt+ secs;
			bin2 = Long.toBinaryString(secs);
			long originalNum =Long.parseLong(bin2.substring(1), 2);
			bin2 =Integer.toBinaryString(~(int)originalNum);
			bin2 =bin2.substring(bin2.length()-16);
		}
		else{
			bin2 = Long.toBinaryString(secs);
			long originalNum =Long.parseLong(bin2.substring(0), 2);
			bin2 =Integer.toBinaryString((int)originalNum);
			//bin2 =bin2.substring(bin2.length()-16);
			int indx =bin2.length();
			while(indx<16){
				bin2="0"+bin2;
				indx++;
			}
		}
		
		bin1 = bin1+bin2;
		long output=Long.parseLong(bin1, 2);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date2 = sdf.parse("1970-01-01 00:00:00");
		//millisecs = date2.getTime();
		Calendar csl = Calendar.getInstance();
		csl.setTime(date2);
		csl.add(Calendar.SECOND, (int) output);
		//System.out.println(sdf.format(csl.getTime()));
		return sdf.format(csl.getTime());
		
	}

	public static String toHexadecimal(String text) throws UnsupportedEncodingException {
		byte[] myBytes = text.getBytes("UTF-8");

		return DatatypeConverter.printHexBinary(myBytes);
	}
	
	public static String toOfferAttrType3(String text) throws UnsupportedEncodingException
	{
		String hexValue = Long.toHexString(Long.parseLong(text));
		String PaddedValue = org.apache.commons.lang.StringUtils.leftPad(hexValue, 16, "0");
		
		return "01" + PaddedValue + "0000000000000064";
	}

	public static String toHexadecimal(int text) throws UnsupportedEncodingException {

		return String.format("%x", new BigInteger(String.valueOf(text)));
	}

	public static String getCurrentPamPeriod(String paramString, int day) {

		SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfMonthly = new SimpleDateFormat("yyyy_MMM");
		SimpleDateFormat sdfday = new SimpleDateFormat("dd");

		// SimpleDateFormat sdfWeekly = new SimpleDateFormat("yyyy");
		Date currDate = new Date();

		Calendar cal = Calendar.getInstance();
		cal.setTime(currDate);
		String str = sdfday.format(currDate);
		if (Integer.parseInt(str) > day) {
			cal.add(Calendar.MONTH, +1);
			currDate = cal.getTime();
		}
		// String week = String.valueOf(cal.get(Calendar.WEEK_OF_YEAR));
		// week = week.length()==1?"0"+week:week;

		String returnValue = null;
		switch (paramString) {
		case "Daily":
			returnValue = "Daily" + sdfDaily.format(currDate);
			break;
		case "Monthly":
			returnValue = sdfMonthly.format(currDate);
			break;
		// case "Weekly" :
		// returnValue=sdfWeekly.format(currDate)+"_W"+week;break;

		}
		sdfDaily = null;
		sdfMonthly = null;
		// sdfWeekly=null;
		currDate = null;// week=null;paramString=null;
		return returnValue;
	}

	public static boolean isDateExpirted(String inputValue,String refValue)
	{
		boolean bExpired = false;
		String DateReference = refValue;
		
		SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date refDate = new Date();
		Date inputDate = new Date();
		
		try {
			refDate = sdfDaily.parse(DateReference);
			inputDate = sdfDaily.parse(inputValue);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return inputDate.after(refDate);
	}
	
	public static long getCurrentPamPeriodInDays(String paramString) {
		long returnValue = 0;
		SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(currDate);
		switch (paramString) {
		case "Daily":
			
			
			cal.add(Calendar.DATE, -1);
			currDate = cal.getTime();
			returnValue = convertDateToEpoch(sdfDaily.format(currDate));
			break;
		case "Monthly":
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
			currDate = cal.getTime();
			
			returnValue = convertDateToEpoch(sdfDaily.format(currDate));
			break;
		}
		return returnValue;
	}

	
	/*
	 * private String mapZainEquipIdWithEoc(String equipId) {
	 * 
	 * Map<String, String> mapEquipIdWithItemCode =
	 * InitializationStep.catalogMainItem.get("ZAIN_MAP");
	 * 
	 * Map<String, Map<String, CatalogItem>> catalogItemData =
	 * InitializationStep.catalogItemData; Map<String, CatalogItem>
	 * catalogItemEoc = catalogItemData.get("EOC"); String result = null;
	 * Set<String> itemKeys = catalogItemEoc.keySet(); for (String itemKey :
	 * itemKeys) { if (itemKey != null && itemKey.startsWith(itemCode)) {
	 * CatalogItem catalogItem = catalogItemEoc.get(itemKey); if (catalogItem !=
	 * null && catalogItem.getItemcode().startsWith("CS_SC")) { result =
	 * catalogItem.getItemcode(); result = result.substring(6, result.length());
	 * break; } } } return result;
	 * 
	 * }
	 */

	public static void main(String args[]) throws ParseException, UnsupportedEncodingException {
	
		System.out.println("Number Of Days: "+convertDateToEpoch("2018-08-07 00:00:00"));;
		Integer [] res = convertDateToTimerOfferDate("2014-01-11 09:00:00");
		System.out.println("Date: "+res[0]+" Seconds: "+ res[1]);
		System.out.println("Actual Date from timer dates and secs: " + returnActualDateFromTimerDate(23345, -32256));
		
		System.out.println("Actual Date from number of days: "+convertEpochDateToDate(17807));
		System.out.println(toHexadecimal("30"));
		
		System.out.println(getCurrentPamPeriodInDays("Daily"));
		System.out.println(getCurrentPamPeriodInDays("Monthly"));
		
		
		System.out.println(isDateExpirted("2019-05-28 00:00:00","2019-05-27 23:59:59"));

	}
}