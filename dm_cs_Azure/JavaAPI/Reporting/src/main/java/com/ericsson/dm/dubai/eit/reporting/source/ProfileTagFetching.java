package com.ericsson.dm.dubai.eit.reporting.source;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ericsson.jibx.beans.SubscriberXml;

public class ProfileTagFetching {
	
	SubscriberXml subscriber;
	
	public ProfileTagFetching(SubscriberXml subscriber) {
		// TODO Auto-generated constructor stub
		this.subscriber=subscriber;	
	}
	
	public String GetProfileTagValue(String Attr_name) {
		String Attr_value = "";	
		if( subscriber.getProfiledumpInfoList()!=null &&  subscriber.getProfiledumpInfoList().size()>0){
		if(Attr_name.equals("BstrVINChngsAllwd")   && subscriber.getProfiledumpInfoList().get(0).getBstrVINChngsAllwd().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVINChngsAllwd();	
		if(Attr_name.equals("BstrVNNChngsAllwd")   && subscriber.getProfiledumpInfoList().get(0).getBstrVNNChngsAllwd().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVNNChngsAllwd();	
		if(Attr_name.equals("BstrVceIntNumTree")   && subscriber.getProfiledumpInfoList().get(0).getBstrVceIntNumTree().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVceIntNumTree();	
		if(Attr_name.equals("BstrVceIntNumExp")   && subscriber.getProfiledumpInfoList().get(0).getBstrVceIntNumExp().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVceIntNumExp();	
		if(Attr_name.equals("BstrVceIntNumAct")   && subscriber.getProfiledumpInfoList().get(0).getBstrVceIntNumAct().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVceIntNumAct();		
		if(Attr_name.equals("BstrVceNatNumAct")   && subscriber.getProfiledumpInfoList().get(0).getBstrVceNatNumAct().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVceNatNumAct();	
		if(Attr_name.equals("BstrVceNatNumExp")   && subscriber.getProfiledumpInfoList().get(0).getBstrVceNatNumExp().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVceNatNumExp();	
		if(Attr_name.equals("BstrVceNatNumTree")   && subscriber.getProfiledumpInfoList().get(0).getBstrVceNatNumTree().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVceNatNumTree();	
		if(Attr_name.equals("BstrVINRecur")   && subscriber.getProfiledumpInfoList().get(0).getBstrVINRecur().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVINRecur();		
		if(Attr_name.equals("BstrVNNRecur")   && subscriber.getProfiledumpInfoList().get(0).getBstrVNNRecur().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVNNRecur();
		if(Attr_name.equals("MBBOfferExpDate")   && subscriber.getProfiledumpInfoList().get(0).getMBBOfferExpDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBBOfferExpDate();
		if(Attr_name.equals("MBBGraceAct")   && subscriber.getProfiledumpInfoList().get(0).getMBBGraceAct().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBBGraceAct();
		if(Attr_name.equals("LastChangeDateExpiry")   && subscriber.getProfiledumpInfoList().get(0).getLastChangeDateExpiry().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getLastChangeDateExpiry();
		if(Attr_name.equals("ManRenDateLess1Y")   && subscriber.getProfiledumpInfoList().get(0).getManRenDateLess1Y().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getManRenDateLess1Y();
		if(Attr_name.equals("BlckBrryBundle")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryBundle().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryBundle();
		if(Attr_name.equals("Plan")   && subscriber.getProfiledumpInfoList().get(0).getPlan().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getPlan();
		if(Attr_name.equals("TP_Social_Deact_Conf")   && subscriber.getProfiledumpInfoList().get(0).getTPSocialDeactConf().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getTPSocialDeactConf();
		if(Attr_name.equals("TopXCountr5")   && subscriber.getProfiledumpInfoList().get(0).getTopXCountr5().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getTopXCountr5();
		if(Attr_name.equals("TopXCountr1")   && subscriber.getProfiledumpInfoList().get(0).getTopXCountr1().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getTopXCountr1();
		if(Attr_name.equals("TopXCountr2")   && subscriber.getProfiledumpInfoList().get(0).getTopXCountr2().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getTopXCountr2();
		if(Attr_name.equals("TopXCountr4")   && subscriber.getProfiledumpInfoList().get(0).getTopXCountr4().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getTopXCountr4();
		if(Attr_name.equals("TopXCountr3")   && subscriber.getProfiledumpInfoList().get(0).getTopXCountr3().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getTopXCountr3();
		if(Attr_name.equals("Prepaid")   && subscriber.getProfiledumpInfoList().get(0).getPrepaid().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getPrepaid();
		if(Attr_name.equals("IDDCutRateAct")   && subscriber.getProfiledumpInfoList().get(0).getIDDCutRateAct().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getIDDCutRateAct();
		if(Attr_name.equals("entBsnssCrclActv")   && subscriber.getProfiledumpInfoList().get(0).getEntBsnssCrclActv().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getEntBsnssCrclActv();
		if(Attr_name.equals("EmiratiPlan")   && subscriber.getProfiledumpInfoList().get(0).getEmiratiPlan().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getEmiratiPlan();
		if(Attr_name.equals("DataGraceAct")   && subscriber.getProfiledumpInfoList().get(0).getDataGraceAct().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getDataGraceAct();
		if(Attr_name.equals("Bespoke")   && subscriber.getProfiledumpInfoList().get(0).getBespoke().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBespoke();
		if(Attr_name.equals("Absher")   && subscriber.getProfiledumpInfoList().get(0).getAbsher().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getAbsher();
		if(Attr_name.equals("NewTopUpValue")   && subscriber.getProfiledumpInfoList().get(0).getNewTopUpValue().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getNewTopUpValue();
		if(Attr_name.equals("bdgtCntrlTopUp")   && subscriber.getProfiledumpInfoList().get(0).getBdgtCntrlTopUp().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBdgtCntrlTopUp();
		if(Attr_name.equals("guiBalAdjCount")   && subscriber.getProfiledumpInfoList().get(0).getGuiBalAdjCount().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getGuiBalAdjCount();
		if(Attr_name.equals("CVMCounter")   && subscriber.getProfiledumpInfoList().get(0).getCVMCounter().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCVMCounter();
		if(Attr_name.equals("ManRenDateLess1Y")   && subscriber.getProfiledumpInfoList().get(0).getManRenDateLess1Y().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getManRenDateLess1Y();
		if(Attr_name.equals("NewPPBundle")   && subscriber.getProfiledumpInfoList().get(0).getNewPPBundle().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getNewPPBundle();
		if(Attr_name.equals("IMEI")   && subscriber.getProfiledumpInfoList().get(0).getIMEI().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getIMEI();
		if(Attr_name.equals("IMSI")   && subscriber.getProfiledumpInfoList().get(0).getIMSI().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getIMSI();								
		if(Attr_name.equals("HLRAddress")   && subscriber.getProfiledumpInfoList().get(0).getHLRAddress().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getHLRAddress();
		if(Attr_name.equals("CSBAR")   && subscriber.getProfiledumpInfoList().get(0).getCSBAR().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCSBAR();
		if(Attr_name.equals("crbtActReqDate")   && subscriber.getProfiledumpInfoList().get(0).getCrbtActReqDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCrbtActReqDate();
		if(Attr_name.equals("crtCnfrmd")   && subscriber.getProfiledumpInfoList().get(0).getCrtCnfrmd().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCrtCnfrmd();
		if(Attr_name.equals("crbtActConfirmDate")   && subscriber.getProfiledumpInfoList().get(0).getCrbtActConfirmDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCrbtActConfirmDate();
		if(Attr_name.equals("crbtDeact")   && subscriber.getProfiledumpInfoList().get(0).getCrbtDeact().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCrbtDeact();
		if(Attr_name.equals("crbtDeActReqDate")   && subscriber.getProfiledumpInfoList().get(0).getCrbtDeActReqDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCrbtDeActReqDate();
		if(Attr_name.equals("crbtDeActConfirmDate")   && subscriber.getProfiledumpInfoList().get(0).getCrbtDeActConfirmDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCrbtDeActConfirmDate();
		if(Attr_name.equals("cbActConfirmDate")   && subscriber.getProfiledumpInfoList().get(0).getCbActConfirmDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbActConfirmDate();
		if(Attr_name.equals("cbExpDate")   && subscriber.getProfiledumpInfoList().get(0).getCbExpDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbExpDate();
		if(Attr_name.equals("cbCnfdAct")   && subscriber.getProfiledumpInfoList().get(0).getCbCnfdAct().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbCnfdAct();
		if(Attr_name.equals("cbWarn1")   && subscriber.getProfiledumpInfoList().get(0).getCbWarn1().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbWarn1();
		if(Attr_name.equals("cbWarn2")   && subscriber.getProfiledumpInfoList().get(0).getCbWarn2().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbWarn2();
		if(Attr_name.equals("cbDeActReqDate")   && subscriber.getProfiledumpInfoList().get(0).getCbDeActReqDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbDeActReqDate();
		if(Attr_name.equals("cbDeActConfirmDate")   && subscriber.getProfiledumpInfoList().get(0).getCbDeActConfirmDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbDeActConfirmDate();
		if(Attr_name.equals("cfActConfirmDate")   && subscriber.getProfiledumpInfoList().get(0).getCfActConfirmDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfActConfirmDate();
		if(Attr_name.equals("cfConfActive")   && subscriber.getProfiledumpInfoList().get(0).getCfConfActive().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfConfActive();
		if(Attr_name.equals("cfDeActConfirmDate")   && subscriber.getProfiledumpInfoList().get(0).getCfDeActConfirmDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfDeActConfirmDate();
		if(Attr_name.equals("cfDeActReqDate")   && subscriber.getProfiledumpInfoList().get(0).getCfDeActReqDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfDeActReqDate();
		if(Attr_name.equals("avmActConfirmDate")   && subscriber.getProfiledumpInfoList().get(0).getAvmActConfirmDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvmActConfirmDate();
		if(Attr_name.equals("avmCnfrmd")   && subscriber.getProfiledumpInfoList().get(0).getAvmCnfrmd().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvmCnfrmd();
		if(Attr_name.equals("avmDeActReqDate")   && subscriber.getProfiledumpInfoList().get(0).getAvmDeActReqDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvmDeActReqDate();
		if(Attr_name.equals("avmDeActConfirmDate")   && subscriber.getProfiledumpInfoList().get(0).getAvmDeActConfirmDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvmDeActConfirmDate();
		if(Attr_name.equals("SprssNtfctn")   && subscriber.getProfiledumpInfoList().get(0).getSprssNtfctn().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getSprssNtfctn();
		if(Attr_name.equals("avm")   && subscriber.getProfiledumpInfoList().get(0).getAvm().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvm();
		if(Attr_name.equals("avmActReqDate")   && subscriber.getProfiledumpInfoList().get(0).getAvmActReqDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvmActReqDate();
		if(Attr_name.equals("MssdCallNot")   && subscriber.getProfiledumpInfoList().get(0).getMssdCallNot().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getMssdCallNot();
		if(Attr_name.equals("VMCIMEI")   && subscriber.getProfiledumpInfoList().get(0).getVMCIMEI().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getVMCIMEI();
		if(Attr_name.equals("VMCName")   && subscriber.getProfiledumpInfoList().get(0).getVMCName().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getVMCName();
		if(Attr_name.equals("AcctActiveMonth")   && subscriber.getProfiledumpInfoList().get(0).getAcctActiveMonth().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getAcctActiveMonth();
		if(Attr_name.equals("AcctActiveYear")   && subscriber.getProfiledumpInfoList().get(0).getAcctActiveYear().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getAcctActiveYear();
		if(Attr_name.equals("2Active")   && subscriber.getProfiledumpInfoList().get(0).getActive().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getActive();
		if(Attr_name.equals("BusMobPayg50")   && subscriber.getProfiledumpInfoList().get(0).getBusMobPayg50().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBusMobPayg50();
		if(Attr_name.equals("BusMobTopUp")   && subscriber.getProfiledumpInfoList().get(0).getBusMobTopUp().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBusMobTopUp();
		if(Attr_name.equals("Date1")   && subscriber.getProfiledumpInfoList().get(0).getDate1().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getDate1();
		if(Attr_name.equals("DisabilityActivationDate")   && subscriber.getProfiledumpInfoList().get(0).getDisabilityActivationDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getDisabilityActivationDate();
		if(Attr_name.equals("DisabilityDectivationDate")   && subscriber.getProfiledumpInfoList().get(0).getDisabilityDectivationDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getDisabilityDectivationDate();
		if(Attr_name.equals("EntCust")   && subscriber.getProfiledumpInfoList().get(0).getEntCust().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getEntCust();
		if(Attr_name.equals("GlobalZoneOptIn")   && subscriber.getProfiledumpInfoList().get(0).getGlobalZoneOptIn().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGlobalZoneOptIn();
		if(Attr_name.equals("GlobalZoneOptOut")   && subscriber.getProfiledumpInfoList().get(0).getGlobalZoneOptOut().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGlobalZoneOptOut();
		if(Attr_name.equals("IDD2Act")   && subscriber.getProfiledumpInfoList().get(0).getIDD2Act().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getIDD2Act();
		if(Attr_name.equals("IDD2ActDate")   && subscriber.getProfiledumpInfoList().get(0).getIDD2ActDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getIDD2ActDate();
		if(Attr_name.equals("InternationalMin")   && subscriber.getProfiledumpInfoList().get(0).getInternationalMin().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getInternationalMin();
		if(Attr_name.equals("Language")   && subscriber.getProfiledumpInfoList().get(0).getLanguage().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getLanguage();
		if(Attr_name.equals("MercuryOptInOutDate")   && subscriber.getProfiledumpInfoList().get(0).getMercuryOptInOutDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getMercuryOptInOutDate();
		if(Attr_name.equals("PAYGDataFNL")   && subscriber.getProfiledumpInfoList().get(0).getPAYGDataFNL().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPAYGDataFNL();
		if(Attr_name.equals("PAYGMet")   && subscriber.getProfiledumpInfoList().get(0).getPAYGMet().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPAYGMet();
		if(Attr_name.equals("PCNGlobalOptin")   && subscriber.getProfiledumpInfoList().get(0).getPCNGlobalOptin().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPCNGlobalOptin();
		if(Attr_name.equals("PCNGlobalOptout")   && subscriber.getProfiledumpInfoList().get(0).getPCNGlobalOptout().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPCNGlobalOptout();
		if(Attr_name.equals("ReceivePostCallSMS")   && subscriber.getProfiledumpInfoList().get(0).getReceivePostCallSMS().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getReceivePostCallSMS();
		if(Attr_name.equals("avmInfSub")   && subscriber.getProfiledumpInfoList().get(0).getAvmInfSub().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvmInfSub();
		if(Attr_name.equals("avmPin")   && subscriber.getProfiledumpInfoList().get(0).getAvmPin().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvmPin();
		if(Attr_name.equals("avmProf")   && subscriber.getProfiledumpInfoList().get(0).getAvmProf().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvmProf();
		if(Attr_name.equals("cbActReqDate")   && subscriber.getProfiledumpInfoList().get(0).getCbActReqDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbActReqDate();
		if(Attr_name.equals("cfActReqDate")   && subscriber.getProfiledumpInfoList().get(0).getCfActReqDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfActReqDate();
		if(Attr_name.equals("cfBusy")   && subscriber.getProfiledumpInfoList().get(0).getCfBusy().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfBusy();
		if(Attr_name.equals("cfData")   && subscriber.getProfiledumpInfoList().get(0).getCfData().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfData();
		if(Attr_name.equals("cfFax")   && subscriber.getProfiledumpInfoList().get(0).getCfFax().length() > 0)							
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfFax();
		if(Attr_name.equals("CVM")   && subscriber.getProfiledumpInfoList().get(0).getCVM().length() > 0)							
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCVM();
		if(Attr_name.equals("cfInt")   && subscriber.getProfiledumpInfoList().get(0).getCfInt().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfInt();
		if(Attr_name.equals("cfLastFaildDate")   && subscriber.getProfiledumpInfoList().get(0).getCfLastFailedDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfLastFailedDate();
		if(Attr_name.equals("cfNoReply")   && subscriber.getProfiledumpInfoList().get(0).getCfNoReply().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfNoReply();
		if(Attr_name.equals("cfNotReach")   && subscriber.getProfiledumpInfoList().get(0).getCfNotReach().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfNotReach();
		if(Attr_name.equals("cfVoice")   && subscriber.getProfiledumpInfoList().get(0).getCfVoice().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfVoice();
		if(Attr_name.equals("cllrRngTne")   && subscriber.getProfiledumpInfoList().get(0).getCllrRngTne().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCllrRngTne();
		if(Attr_name.equals("odbOutVoice")   && subscriber.getProfiledumpInfoList().get(0).getOdbOutVoice().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getOdbOutVoice();
		if(Attr_name.equals("IDD2DeactDate")   && subscriber.getProfiledumpInfoList().get(0).getIDD2DeactDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getIDD2DeactDate();
		if(Attr_name.equals("IDDCutRateActDate")   && subscriber.getProfiledumpInfoList().get(0).getIDDCutRateActDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getIDDCutRateActDate();
		if(Attr_name.equals("cbAll")   && subscriber.getProfiledumpInfoList().get(0).getCbAll().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbAll();
		if(Attr_name.equals("cbInt")   && subscriber.getProfiledumpInfoList().get(0).getCbInt().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbInt();
		if(Attr_name.equals("cbIntExc")   && subscriber.getProfiledumpInfoList().get(0).getCbIntExc().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbIntExc();
		if(Attr_name.equals("cbIncom")   && subscriber.getProfiledumpInfoList().get(0).getCbIncom().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbIncom();
		if(Attr_name.equals("cbWR")   && subscriber.getProfiledumpInfoList().get(0).getCbWR().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbWR();
		if(Attr_name.equals("clir")   && subscriber.getProfiledumpInfoList().get(0).getClir().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getClir();
		if(Attr_name.equals("faxGroup3")   && subscriber.getProfiledumpInfoList().get(0).getFaxGroup3().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getFaxGroup3();
		if(Attr_name.equals("dataGeneric")   && subscriber.getProfiledumpInfoList().get(0).getDataGeneric().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getDataGeneric();
		if(Attr_name.equals("odbIncom")   && subscriber.getProfiledumpInfoList().get(0).getOdbIncom().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getOdbIncom();
		if(Attr_name.equals("odbWR")   && subscriber.getProfiledumpInfoList().get(0).getOdbWR().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getOdbWR();
		if(Attr_name.equals("odbIntExc")   && subscriber.getProfiledumpInfoList().get(0).getOdbIntExc().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getOdbIntExc();
		if(Attr_name.equals("odbInt")   && subscriber.getProfiledumpInfoList().get(0).getOdbInt().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getOdbInt();
		if(Attr_name.equals("odbOnlyVoice")   && subscriber.getProfiledumpInfoList().get(0).getOdbOnlyVoice().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getOdbOnlyVoice();
		if(Attr_name.equals("entMssgingActv")   && subscriber.getProfiledumpInfoList().get(0).getEntMssgingActv().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getEntMssgingActv();
		if(Attr_name.equals("gprsPmail")   && subscriber.getProfiledumpInfoList().get(0).getGprsPmail().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGprsPmail();
		if(Attr_name.equals("BstrVINActReqDate")   && subscriber.getProfiledumpInfoList().get(0).getBstrVINActReqDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVINActReqDate();
		if(Attr_name.equals("BstrVINConfirmDate")   && subscriber.getProfiledumpInfoList().get(0).getBstrVINConfirmDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVINConfirmDate();
		if(Attr_name.equals("BstrVINDeActReqDate")   && subscriber.getProfiledumpInfoList().get(0).getBstrVINDeActReqDate().length() > 0)	
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVINDeActReqDate();
		if(Attr_name.equals("BstrVINDeActConfDate")   && subscriber.getProfiledumpInfoList().get(0).getBstrVINDeActConfDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVINDeActConfDate();
		if(Attr_name.equals("BronzeMPromoActDate")   && subscriber.getProfiledumpInfoList().get(0).getBronzeMPromoActDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBronzeMPromoActDate();
		if(Attr_name.equals("BronzeMPromoDeactDate")   && subscriber.getProfiledumpInfoList().get(0).getBronzeMPromoDeactDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBronzeMPromoDeactDate();
		if(Attr_name.equals("SocialBndlActChannel")   && subscriber.getProfiledumpInfoList().get(0).getSocialBndlActChannel().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getSocialBndlActChannel();
		if(Attr_name.equals("SocialBndlDeActChannel")   && subscriber.getProfiledumpInfoList().get(0).getSocialBndlDeActChannel().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getSocialBndlDeActChannel();
		if(Attr_name.equals("BstrVNNActReqDate")   && subscriber.getProfiledumpInfoList().get(0).getBstrVNNActReqDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVNNActReqDate();
		if(Attr_name.equals("BstrVNNConfirmDate")   && subscriber.getProfiledumpInfoList().get(0).getBstrVNNConfirmDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVNNConfirmDate();
		if(Attr_name.equals("BstrVNNDeActConfDate")   && subscriber.getProfiledumpInfoList().get(0).getBstrVNNDeActConfDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVNNDeActConfDate();
		if(Attr_name.equals("BstrVNNDeActReqDate")   && subscriber.getProfiledumpInfoList().get(0).getBstrVNNDeActReqDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVNNDeActReqDate();
		if(Attr_name.equals("odbAll")   && subscriber.getProfiledumpInfoList().get(0).getOdbAll().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getOdbAll();
		if(Attr_name.equals("cbAct")   && subscriber.getProfiledumpInfoList().get(0).getCbAct().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbAct();
		if(Attr_name.equals("PokeSMSOptoutDate")   && subscriber.getProfiledumpInfoList().get(0).getPokeSMSOptoutDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPokeSMSOptoutDate();
		if(Attr_name.equals("PokeSMSOptinDate")   && subscriber.getProfiledumpInfoList().get(0).getPokeSMSOptinDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPokeSMSOptinDate();
		if(Attr_name.equals("BlckBrryActReqDate")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryActReqDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryActReqDate();
		if(Attr_name.equals("BlckBrryActCnfrmDate")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryActCnfrmDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryActCnfrmDate();
		if(Attr_name.equals("BlckBrryDeactRbkDate")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryDeactRbkDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryDeactRbkDate();
		if(Attr_name.equals("BronzeDeactDate")   && subscriber.getProfiledumpInfoList().get(0).getBronzeDeactDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBronzeDeactDate();
		if(Attr_name.equals("BronzeActDate")   && subscriber.getProfiledumpInfoList().get(0).getBronzeActDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBronzeActDate();
		if(Attr_name.equals("umsCnfrmd")   && subscriber.getProfiledumpInfoList().get(0).getUmsCnfrmd().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getUmsCnfrmd();
		if(Attr_name.equals("umsInfSub")   && subscriber.getProfiledumpInfoList().get(0).getUmsInfSub().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getUmsInfSub();
		if(Attr_name.equals("ums")   && subscriber.getProfiledumpInfoList().get(0).getUms().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getUms();
		if(Attr_name.equals("umsF2M")   && subscriber.getProfiledumpInfoList().get(0).getUmsF2M().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getUmsF2M();
		if(Attr_name.equals("umsSMS2F")   && subscriber.getProfiledumpInfoList().get(0).getUmsSMS2F().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getUmsSMS2F();
		if(Attr_name.equals("umsVm2MMS")   && subscriber.getProfiledumpInfoList().get(0).getUmsVm2MMS().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getUmsVm2MMS();
		if(Attr_name.equals("TravSumOptInDate")   && subscriber.getProfiledumpInfoList().get(0).getTravSumOptInDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getTravSumOptInDate();
		if(Attr_name.equals("PassOptInDate")   && subscriber.getProfiledumpInfoList().get(0).getPassOptInDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPassOptInDate();
		if(Attr_name.equals("PassOptOutDate")   && subscriber.getProfiledumpInfoList().get(0).getPassOptOutDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPassOptOutDate();
		if(Attr_name.equals("TwinSIMNumber")   && subscriber.getProfiledumpInfoList().get(0).getTwinSIMNumber().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getTwinSIMNumber();
		if(Attr_name.equals("FstTimeActivation")   && subscriber.getProfiledumpInfoList().get(0).getFstTimeActivation().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getFstTimeActivation();
		if(Attr_name.equals("Anniversarytopuppromo")   && subscriber.getProfiledumpInfoList().get(0).getAnniversarytopuppromo().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getAnniversarytopuppromo();
		if(Attr_name.equals("AnniversarytopuppromoEnd")   && subscriber.getProfiledumpInfoList().get(0).getAnniversarytopuppromoEnd().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getAnniversarytopuppromoEnd();
		if(Attr_name.equals("SmartPhoneExpiry")   && subscriber.getProfiledumpInfoList().get(0).getSmartPhoneExpiry().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getSmartPhoneExpiry();
		if(Attr_name.equals("DSP_Promo")   && subscriber.getProfiledumpInfoList().get(0).getDSPPromo().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getDSPPromo();
		if(Attr_name.equals("guiBalAdjDate")   && subscriber.getProfiledumpInfoList().get(0).getGuiBalAdjDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGuiBalAdjDate();
		if(Attr_name.equals("guiBalAdjVal")   && subscriber.getProfiledumpInfoList().get(0).getGuiBalAdjVal().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGuiBalAdjVal();
		if(Attr_name.equals("MBBOfferExpDate")   && subscriber.getProfiledumpInfoList().get(0).getMBBOfferExpDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBBOfferExpDate();
		if(Attr_name.equals("BBBundName")   && subscriber.getProfiledumpInfoList().get(0).getBBBundName().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBBBundName();
		if(Attr_name.equals("BBPromo")   && subscriber.getProfiledumpInfoList().get(0).getBBPromo().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBBPromo();
		if(Attr_name.equals("BSCSAct")   && subscriber.getProfiledumpInfoList().get(0).getBSCSAct().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBSCSAct();
		if(Attr_name.equals("BBPromoSubDate")   && subscriber.getProfiledumpInfoList().get(0).getBBPromoSubDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBBPromoSubDate();
		if(Attr_name.equals("BBPromoEndDate")   && subscriber.getProfiledumpInfoList().get(0).getBBPromoEndDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBBPromoEndDate();
		if(Attr_name.equals("BlckBrryActReqDate")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryActReqDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryActReqDate();
		if(Attr_name.equals("BlckBrrySprssSMS")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrrySprssSMS().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrrySprssSMS();
		if(Attr_name.equals("BlckBrryExpDate")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryExpDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryExpDate();
		if(Attr_name.equals("BlckBrryDeActCnfrmDt")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryDeActCnfrmDt().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryDeActCnfrmDt();
		if(Attr_name.equals("SprssNtfctn")   && subscriber.getProfiledumpInfoList().get(0).getSprssNtfctn().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getSprssNtfctn();
		if(Attr_name.equals("Payg_Life_Optin")   && subscriber.getProfiledumpInfoList().get(0).getPaygLifeOptin().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPaygLifeOptin();
		if(Attr_name.equals("gprsBasicSer")   && subscriber.getProfiledumpInfoList().get(0).getGprsBasicSer().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGprsBasicSer();
		if(Attr_name.equals("GprsWap")   && subscriber.getProfiledumpInfoList().get(0).getGprsWap().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGprsWap();
		if(Attr_name.equals("SmsMtPp")   && subscriber.getProfiledumpInfoList().get(0).getSmsMtPp().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getSmsMtPp();
		if(Attr_name.equals("GprsMms")   && subscriber.getProfiledumpInfoList().get(0).getGprsMms().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGprsMms();
		if(Attr_name.equals("SmsMoPp")   && subscriber.getProfiledumpInfoList().get(0).getSmsMoPp().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getSmsMoPp();
		if(Attr_name.equals("Callhold")   && subscriber.getProfiledumpInfoList().get(0).getCallhold().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCallhold();
		if(Attr_name.equals("Callwait")   && subscriber.getProfiledumpInfoList().get(0).getCallwait().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCallwait();
		if(Attr_name.equals("MultiParty")   && subscriber.getProfiledumpInfoList().get(0).getMultiParty().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getMultiParty();
		if(Attr_name.equals("IntRoam")   && subscriber.getProfiledumpInfoList().get(0).getIntRoam().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getIntRoam();
		if(Attr_name.equals("Clip")   && subscriber.getProfiledumpInfoList().get(0).getClip().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getClip();
		if(Attr_name.equals("VdioCall")   && subscriber.getProfiledumpInfoList().get(0).getVdioCall().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getVdioCall();
		if(Attr_name.equals("DuCS")   && subscriber.getProfiledumpInfoList().get(0).getDuCS().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getDuCS();
		if(Attr_name.equals("BSCS_account_num")   && subscriber.getProfiledumpInfoList().get(0).getBSCSAccountNum().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBSCSAccountNum();
		if(Attr_name.equals("CS_notification_num")   && subscriber.getProfiledumpInfoList().get(0).getCSNotificationNum().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCSNotificationNum();
		if(Attr_name.equals("cbGraceAct")   && subscriber.getProfiledumpInfoList().get(0).getCbGraceAct().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbGraceAct();
		if(Attr_name.equals("cbGraceEnd")   && subscriber.getProfiledumpInfoList().get(0).getCbGraceEnd().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbGraceEnd();
		if(Attr_name.equals("cbGraceRenew")   && subscriber.getProfiledumpInfoList().get(0).getCbGraceRenew().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbGraceRenew();
		if(Attr_name.equals("gprsCarrier")   && subscriber.getProfiledumpInfoList().get(0).getGprsCarrier().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGprsCarrier();
		/*if(Attr_name.equals("MusicPromo")   && subscriber.getProfiledumpInfoList().get(0).getMusicPromo().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getMusicPromo();*/
		if(Attr_name.equals("PAYGDataLineOffer")   && subscriber.getProfiledumpInfoList().get(0).getPAYGDataLineOffer().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPAYGDataLineOffer();
		if(Attr_name.equals("PAYGDataWHSP")   && subscriber.getProfiledumpInfoList().get(0).getPAYGDataWHSP().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPAYGDataWHSP();
		if(Attr_name.equals("PAYGDataWH")   && subscriber.getProfiledumpInfoList().get(0).getPAYGDataWH().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPAYGDataWH();
		if(Attr_name.equals("msisdnChgDate")   && subscriber.getProfiledumpInfoList().get(0).getMsisdnChgDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getMsisdnChgDate();
		if(Attr_name.equals("mainMSISDN")   && subscriber.getProfiledumpInfoList().get(0).getMainMSISDN().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getMainMSISDN();
		if(Attr_name.equals("CashReturn")   && subscriber.getProfiledumpInfoList().get(0).getCashReturn().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCashReturn();
		/*********Profile tag for other outputs***********/
		if(Attr_name.equals("SmsBndl1Recur")   && subscriber.getProfiledumpInfoList().get(0).getSmsBndl1Recur().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getSmsBndl1Recur();
		if(Attr_name.equals("SmsBndl2Recur")   && subscriber.getProfiledumpInfoList().get(0).getSmsBndl2Recur().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getSmsBndl2Recur();
		if(Attr_name.equals("PAYGMet")   && subscriber.getProfiledumpInfoList().get(0).getPAYGMet().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getPAYGMet();
		if(Attr_name.equals("SmsExpDate")   && subscriber.getProfiledumpInfoList().get(0).getSmsExpDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getSmsExpDate();
		if(Attr_name.equals("BlckBrryExpDate")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryExpDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryExpDate();
		if(Attr_name.equals("MBBExpDate")   && subscriber.getProfiledumpInfoList().get(0).getMBBExpDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBBExpDate();
		if(Attr_name.equals("MBBUnlimExpDate")   && subscriber.getProfiledumpInfoList().get(0).getMBBUnlimExpDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBBUnlimExpDate();
		if(Attr_name.equals("MBB2GBExpDate")   && subscriber.getProfiledumpInfoList().get(0).getMBB2GBExpDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBB2GBExpDate();
		if(Attr_name.equals("MBB10GBExpDate")   && subscriber.getProfiledumpInfoList().get(0).getMBB10GBExpDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBB10GBExpDate();
		if(Attr_name.equals("MBB")   && subscriber.getProfiledumpInfoList().get(0).getMBB().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBB();
		if(Attr_name.equals("MBBUnlimited")   && subscriber.getProfiledumpInfoList().get(0).getMBBUnlimited().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBBUnlimited();
		if(Attr_name.equals("BlckBrryAct")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryAct().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryAct();
		if(Attr_name.equals("BlckBrryCnfdAct")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryCnfdAct().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryCnfdAct();
		if(Attr_name.equals("MBB2GB")   && subscriber.getProfiledumpInfoList().get(0).getMBB2GB().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBB2GB();
		if(Attr_name.equals("MBB10GB")   && subscriber.getProfiledumpInfoList().get(0).getMBB10GB().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBB10GB();
		if(Attr_name.equals("PAYGDataWH")   && subscriber.getProfiledumpInfoList().get(0).getPAYGDataWH().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getPAYGDataWH();
		if(Attr_name.equals("PAYGDataWHSP")   && subscriber.getProfiledumpInfoList().get(0).getPAYGDataWHSP().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getPAYGDataWHSP();
		if(Attr_name.equals("PAYGDataLineOffer")   && subscriber.getProfiledumpInfoList().get(0).getPAYGDataLineOffer().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getPAYGDataLineOffer();		
		}
		
		return Attr_value;		
	}

}
