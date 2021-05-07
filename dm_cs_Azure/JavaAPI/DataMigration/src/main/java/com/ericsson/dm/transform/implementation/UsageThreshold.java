package com.ericsson.dm.transform.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ericsson.jibx.beans.SubscriberXml;

public class UsageThreshold {
	SubscriberXml subscriber;
	List<String> rejectAndLog, discardAndLog, onlyLog;
	Set<String> validMsisdn;
	
	public UsageThreshold(SubscriberXml subscriber, List<String> rejectAndLog,
			List<String> discardAndLog, List<String> onlyLog, List<String> notMigratedLog) {
		this.subscriber = subscriber;
		this.rejectAndLog = rejectAndLog;
		this.discardAndLog = discardAndLog;
		this.onlyLog = onlyLog;
		// TODO Auto-generated constructor stub
	}
	
	public Map<String, List<String>> execute() {
		// TODO Auto-generated method stub
		Map<String, List<String>> map = new HashMap<>();
		map.put("UsageCounter", generateUsageThreshold());
		map.put("ProviderUsageCounter", generateProviderUsageThreshold());

		return map;
	}

	private List<String> generateProviderUsageThreshold() {
		// TODO Auto-generated method stub
		return null;
	}

	private List<String> generateUsageThreshold() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
