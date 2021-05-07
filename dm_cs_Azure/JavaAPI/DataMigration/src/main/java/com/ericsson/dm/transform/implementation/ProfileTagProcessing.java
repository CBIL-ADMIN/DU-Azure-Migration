package com.ericsson.dm.transform.implementation;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ericsson.jibx.beans.SubscriberXml;

public class ProfileTagProcessing {
	
	SubscriberXml subscriber;
	Set<String> onlyLog;
	
	public ProfileTagProcessing(SubscriberXml subscriber,Set<String> onlyLog) {
		// TODO Auto-generated constructor stub
		this.subscriber=subscriber;	
		this.onlyLog = onlyLog;
	}
	
	public String GetProfileTagValue(String Attr_name) {
		String Attr_value = "";	
		
		 //MSISDN
		 //Value
		 if(Attr_name.toUpperCase().equals("LANGUAGE ID")   && subscriber.getProfiledumpInfoList().get(0).getLanguageID().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getLanguageID();
		 if(Attr_name.toUpperCase().equals("IMEI")   && subscriber.getProfiledumpInfoList().get(0).getIMEI().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getIMEI();
		 if(Attr_name.toUpperCase().equals("IMSI")   && subscriber.getProfiledumpInfoList().get(0).getIMSI().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getIMSI();								
		 if(Attr_name.toUpperCase().equals("HLRADDRESS")   && subscriber.getProfiledumpInfoList().get(0).getHLRAddress().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getHLRAddress();
		 //Wallet Value
		 //Value
		 if(Attr_name.toUpperCase().equals("BSTRVCEINTNUMTREE")   && subscriber.getProfiledumpInfoList().get(0).getBstrVceIntNumTree().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVceIntNumTree();	
		 if(Attr_name.toUpperCase().equals("BSTRVCENATNUMTREE")   && subscriber.getProfiledumpInfoList().get(0).getBstrVceNatNumTree().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVceNatNumTree();	
		 if(Attr_name.toUpperCase().equals("PERIODICCHARGESTATUS")   && subscriber.getProfiledumpInfoList().get(0).getPeriodicChargeStatus().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getPeriodicChargeStatus();	
		 if(Attr_name.toUpperCase().equals("CSBAR")   && subscriber.getProfiledumpInfoList().get(0).getCSBAR().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCSBAR();
		 if(Attr_name.toUpperCase().equals("CRBTACTREQDATE")   && subscriber.getProfiledumpInfoList().get(0).getCrbtActReqDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCrbtActReqDate();
		 if(Attr_name.toUpperCase().equals("CRTCNFRMD")   && subscriber.getProfiledumpInfoList().get(0).getCrtCnfrmd().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCrtCnfrmd();
		 if(Attr_name.toUpperCase().equals("CRBTACTCONFIRMDATE")   && subscriber.getProfiledumpInfoList().get(0).getCrbtActConfirmDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCrbtActConfirmDate();
		 if(Attr_name.toUpperCase().equals("CRBTDEACT")   && subscriber.getProfiledumpInfoList().get(0).getCrbtDeact().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCrbtDeact();
		 if(Attr_name.toUpperCase().equals("CRBTDEACTREQDATE")   && subscriber.getProfiledumpInfoList().get(0).getCrbtDeActReqDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCrbtDeActReqDate();
		 if(Attr_name.toUpperCase().equals("CRBTDEACTCONFIRMDATE")   && subscriber.getProfiledumpInfoList().get(0).getCrbtDeActConfirmDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCrbtDeActConfirmDate();
		 if(Attr_name.toUpperCase().equals("CBACTCONFIRMDATE")   && subscriber.getProfiledumpInfoList().get(0).getCbActConfirmDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbActConfirmDate();
		 if(Attr_name.toUpperCase().equals("CBEXPDATE")   && subscriber.getProfiledumpInfoList().get(0).getCbExpDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbExpDate();
		 if(Attr_name.toUpperCase().equals("CBCNFDACT")   && subscriber.getProfiledumpInfoList().get(0).getCbCnfdAct().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbCnfdAct();
		 if(Attr_name.toUpperCase().equals("CBWARN1")   && subscriber.getProfiledumpInfoList().get(0).getCbWarn1().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbWarn1();
		 if(Attr_name.toUpperCase().equals("CBWARN2")   && subscriber.getProfiledumpInfoList().get(0).getCbWarn2().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbWarn2();
		 if(Attr_name.toUpperCase().equals("CBDEACTREQDATE")   && subscriber.getProfiledumpInfoList().get(0).getCbDeActReqDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbDeActReqDate();
		 if(Attr_name.toUpperCase().equals("CBDEACTCONFIRMDATE")   && subscriber.getProfiledumpInfoList().get(0).getCbDeActConfirmDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbDeActConfirmDate();
		 if(Attr_name.toUpperCase().equals("CFACTCONFIRMDATE")   && subscriber.getProfiledumpInfoList().get(0).getCfActConfirmDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfActConfirmDate();
		 if(Attr_name.toUpperCase().equals("CFCONFACTIVE")   && subscriber.getProfiledumpInfoList().get(0).getCfConfActive().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfConfActive();
		 if(Attr_name.toUpperCase().equals("CFDEACTCONFIRMDATE")   && subscriber.getProfiledumpInfoList().get(0).getCfDeActConfirmDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfDeActConfirmDate();
		 if(Attr_name.toUpperCase().equals("CFDEACTREQDATE")   && subscriber.getProfiledumpInfoList().get(0).getCfDeActReqDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfDeActReqDate();
		 if(Attr_name.toUpperCase().equals("CFLASTFAILEDDATE")   && subscriber.getProfiledumpInfoList().get(0).getCfLastFailedDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfLastFailedDate();
		 if(Attr_name.toUpperCase().equals("CFACTREQDATE")   && subscriber.getProfiledumpInfoList().get(0).getCfActReqDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfActReqDate();
		 if(Attr_name.toUpperCase().equals("AVMACTCONFIRMDATE")   && subscriber.getProfiledumpInfoList().get(0).getAvmActConfirmDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvmActConfirmDate();
		 if(Attr_name.toUpperCase().equals("AVMCNFRMD")   && subscriber.getProfiledumpInfoList().get(0).getAvmCnfrmd().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvmCnfrmd();
		 if(Attr_name.toUpperCase().equals("AVMDEACTREQDATE")   && subscriber.getProfiledumpInfoList().get(0).getAvmDeActReqDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvmDeActReqDate();
		 if(Attr_name.toUpperCase().equals("AVMDEACTCONFIRMDATE")   && subscriber.getProfiledumpInfoList().get(0).getAvmDeActConfirmDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvmDeActConfirmDate();
		 if(Attr_name.toUpperCase().equals("SPRSSNTFCTN")   && subscriber.getProfiledumpInfoList().get(0).getSprssNtfctn().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getSprssNtfctn();
		 if(Attr_name.toUpperCase().equals("AVM")   && subscriber.getProfiledumpInfoList().get(0).getAvm().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvm();
		 if(Attr_name.toUpperCase().equals("AVMACTREQDATE")   && subscriber.getProfiledumpInfoList().get(0).getAvmActReqDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvmActReqDate();
		 if(Attr_name.toUpperCase().equals("MSSDCALLNOT")   && subscriber.getProfiledumpInfoList().get(0).getMssdCallNot().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMssdCallNot();
		 if(Attr_name.toUpperCase().equals("VMCIMEI")   && subscriber.getProfiledumpInfoList().get(0).getVMCIMEI().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getVMCIMEI();
		 if(Attr_name.toUpperCase().equals("VMCNAME")   && subscriber.getProfiledumpInfoList().get(0).getVMCName().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getVMCName();
		 if(Attr_name.toUpperCase().equals("ACCTACTIVEMONTH")   && subscriber.getProfiledumpInfoList().get(0).getAcctActiveMonth().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getAcctActiveMonth();
		 if(Attr_name.toUpperCase().equals("ACCTACTIVEYEAR")   && subscriber.getProfiledumpInfoList().get(0).getAcctActiveYear().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getAcctActiveYear();
		 if(Attr_name.toUpperCase().equals("TOPXCOUNTR1")   && subscriber.getProfiledumpInfoList().get(0).getTopXCountr1().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getTopXCountr1();
		 if(Attr_name.toUpperCase().equals("TOPXCOUNTR2")   && subscriber.getProfiledumpInfoList().get(0).getTopXCountr2().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getTopXCountr2();
		 if(Attr_name.toUpperCase().equals("TOPXCOUNTR4")   && subscriber.getProfiledumpInfoList().get(0).getTopXCountr4().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getTopXCountr4();
		 if(Attr_name.toUpperCase().equals("TOPXCOUNTR3")   && subscriber.getProfiledumpInfoList().get(0).getTopXCountr3().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getTopXCountr3();
		 if(Attr_name.toUpperCase().equals("TOPXCOUNTR5")   && subscriber.getProfiledumpInfoList().get(0).getTopXCountr5().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getTopXCountr5();
		 if(Attr_name.toUpperCase().equals("PREPAID")   && subscriber.getProfiledumpInfoList().get(0).getPrepaid().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getPrepaid();
		 if(Attr_name.toUpperCase().equals("ENTBSNSSCRCLACTV")   && subscriber.getProfiledumpInfoList().get(0).getEntBsnssCrclActv().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getEntBsnssCrclActv();
		 if(Attr_name.toUpperCase().equals("BUSMOBPAYG50")   && subscriber.getProfiledumpInfoList().get(0).getBusMobPayg50().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBusMobPayg50();
		 if(Attr_name.toUpperCase().equals("BUSMOBTOPUP")   && subscriber.getProfiledumpInfoList().get(0).getBusMobTopUp().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBusMobTopUp();
		 if(Attr_name.toUpperCase().equals("MERCURYOPTINOUTDATE")   && subscriber.getProfiledumpInfoList().get(0).getMercuryOptInOutDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMercuryOptInOutDate();
		 if(Attr_name.toUpperCase().equals("HARDCAPLABEL")   && subscriber.getProfiledumpInfoList().get(0).getHardCapLabel().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getHardCapLabel();
		 if(Attr_name.toUpperCase().equals("HARDCAPADDONLABEL")   && subscriber.getProfiledumpInfoList().get(0).getHardCapAddonLabel().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getHardCapAddonLabel();
		 if(Attr_name.toUpperCase().equals("ACTIVE")   && subscriber.getProfiledumpInfoList().get(0).getActive().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getActive();
		 if(Attr_name.toUpperCase().equals("ABSHER")   && subscriber.getProfiledumpInfoList().get(0).getAbsher().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getAbsher();
		 if(Attr_name.toUpperCase().equals("AVMINFSUB")   && subscriber.getProfiledumpInfoList().get(0).getAvmInfSub().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvmInfSub();
		 if(Attr_name.toUpperCase().equals("AVMPIN")   && subscriber.getProfiledumpInfoList().get(0).getAvmPin().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvmPin();
		 if(Attr_name.toUpperCase().equals("AVMPROF")   && subscriber.getProfiledumpInfoList().get(0).getAvmProf().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getAvmProf();
		 if(Attr_name.toUpperCase().equals("BRONZEMPROMOACTDATE")   && subscriber.getProfiledumpInfoList().get(0).getBronzeMPromoActDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBronzeMPromoActDate();
		 if(Attr_name.toUpperCase().equals("BRONZEMPROMODEACTDATE")   && subscriber.getProfiledumpInfoList().get(0).getBronzeMPromoDeactDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBronzeMPromoDeactDate();
		 if(Attr_name.toUpperCase().equals("BSTRVCEINTNUMACT")   && subscriber.getProfiledumpInfoList().get(0).getBstrVceIntNumAct().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVceIntNumAct();		
		 if(Attr_name.toUpperCase().equals("BSTRVCENATNUMACT")   && subscriber.getProfiledumpInfoList().get(0).getBstrVceNatNumAct().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVceNatNumAct();	
		 if(Attr_name.toUpperCase().equals("BSTRVINACTREQDATE")   && subscriber.getProfiledumpInfoList().get(0).getBstrVINActReqDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVINActReqDate();
		 if(Attr_name.toUpperCase().equals("BSTRVINCONFIRMDATE")   && subscriber.getProfiledumpInfoList().get(0).getBstrVINConfirmDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVINConfirmDate();
		 if(Attr_name.toUpperCase().equals("BSTRVINDEACTCONFDATE")   && subscriber.getProfiledumpInfoList().get(0).getBstrVINDeActConfDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVINDeActConfDate();
		 if(Attr_name.toUpperCase().equals("BSTRVINDEACTREQDATE")   && subscriber.getProfiledumpInfoList().get(0).getBstrVINDeActReqDate().length() > 0)	
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVINDeActReqDate();
		 if(Attr_name.toUpperCase().equals("BSTRVINRECUR")   && subscriber.getProfiledumpInfoList().get(0).getBstrVINRecur().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVINRecur();		
		 if(Attr_name.toUpperCase().equals("BSTRVNNACTREQDATE")   && subscriber.getProfiledumpInfoList().get(0).getBstrVNNActReqDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVNNActReqDate();
		 if(Attr_name.toUpperCase().equals("BSTRVNNCONFIRMDATE")   && subscriber.getProfiledumpInfoList().get(0).getBstrVNNConfirmDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVNNConfirmDate();
		 if(Attr_name.toUpperCase().equals("BSTRVNNDEACTCONFDATE")   && subscriber.getProfiledumpInfoList().get(0).getBstrVNNDeActConfDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVNNDeActConfDate();
		 if(Attr_name.toUpperCase().equals("BSTRVNNDEACTREQDATE")   && subscriber.getProfiledumpInfoList().get(0).getBstrVNNDeActReqDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVNNDeActReqDate();
		 if(Attr_name.toUpperCase().equals("BSTRVNNRECUR")   && subscriber.getProfiledumpInfoList().get(0).getBstrVNNRecur().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVNNRecur();
		 if(Attr_name.toUpperCase().equals("CBACTREQDATE")   && subscriber.getProfiledumpInfoList().get(0).getCbActReqDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbActReqDate();
		 if(Attr_name.toUpperCase().equals("CBALL")   && subscriber.getProfiledumpInfoList().get(0).getCbAll().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbAll();
		 if(Attr_name.toUpperCase().equals("CBINCOM")   && subscriber.getProfiledumpInfoList().get(0).getCbIncom().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbIncom();
		 if(Attr_name.toUpperCase().equals("CBINT")   && subscriber.getProfiledumpInfoList().get(0).getCbInt().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbInt();
		 if(Attr_name.toUpperCase().equals("CBINTEXC")   && subscriber.getProfiledumpInfoList().get(0).getCbIntExc().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbIntExc();
		 if(Attr_name.toUpperCase().equals("CBWR")   && subscriber.getProfiledumpInfoList().get(0).getCbWR().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbWR();
		 if(Attr_name.toUpperCase().equals("CFBUSY")   && subscriber.getProfiledumpInfoList().get(0).getCfBusy().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfBusy();
		 if(Attr_name.toUpperCase().equals("CFDATA")   && subscriber.getProfiledumpInfoList().get(0).getCfData().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfData();
		 if(Attr_name.toUpperCase().equals("CFFAX")   && subscriber.getProfiledumpInfoList().get(0).getCfFax().length() > 0)							
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfFax();
		 if(Attr_name.toUpperCase().equals("CFINT")   && subscriber.getProfiledumpInfoList().get(0).getCfInt().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfInt();
		 if(Attr_name.toUpperCase().equals("CFNOREPLY")   && subscriber.getProfiledumpInfoList().get(0).getCfNoReply().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfNoReply();
		 if(Attr_name.toUpperCase().equals("CFNOTREACH")   && subscriber.getProfiledumpInfoList().get(0).getCfNotReach().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfNotReach();
		 if(Attr_name.toUpperCase().equals("CFVOICE")   && subscriber.getProfiledumpInfoList().get(0).getCfVoice().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCfVoice();
		 if(Attr_name.toUpperCase().equals("CLIR")   && subscriber.getProfiledumpInfoList().get(0).getClir().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getClir();
		 if(Attr_name.toUpperCase().equals("CLLRRNGTNE")   && subscriber.getProfiledumpInfoList().get(0).getCllrRngTne().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCllrRngTne();
		 if(Attr_name.toUpperCase().equals("CVM")   && subscriber.getProfiledumpInfoList().get(0).getCVM().length() > 0)							
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCVM();
		 if(Attr_name.toUpperCase().equals("CVMCOUNTER")   && subscriber.getProfiledumpInfoList().get(0).getCVMCounter().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCVMCounter();
		 if(Attr_name.toUpperCase().equals("DATAGENERIC")   && subscriber.getProfiledumpInfoList().get(0).getDataGeneric().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getDataGeneric();
		 if(Attr_name.toUpperCase().equals("DATE1")   && subscriber.getProfiledumpInfoList().get(0).getDate1().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getDate1();
		 if(Attr_name.toUpperCase().equals("DISABILITYACTIVATIONDATE")   && subscriber.getProfiledumpInfoList().get(0).getDisabilityActivationDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getDisabilityActivationDate();
		 if(Attr_name.toUpperCase().equals("DISABILITYDECTIVATIONDATE")   && subscriber.getProfiledumpInfoList().get(0).getDisabilityDectivationDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getDisabilityDectivationDate();
		 if(Attr_name.toUpperCase().equals("EMIRATIPLAN")   && subscriber.getProfiledumpInfoList().get(0).getEmiratiPlan().length() > 0)
			 	Attr_value = subscriber.getProfiledumpInfoList().get(0).getEmiratiPlan();
		 if(Attr_name.toUpperCase().equals("ENTCUST")   && subscriber.getProfiledumpInfoList().get(0).getEntCust().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getEntCust();
		 if(Attr_name.toUpperCase().equals("ENTMSSGINGACTV")   && subscriber.getProfiledumpInfoList().get(0).getEntMssgingActv().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getEntMssgingActv();
		 if(Attr_name.toUpperCase().equals("FAXGROUP3")   && subscriber.getProfiledumpInfoList().get(0).getFaxGroup3().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getFaxGroup3();
		 if(Attr_name.toUpperCase().equals("GLOBALZONEOPTIN")   && subscriber.getProfiledumpInfoList().get(0).getGlobalZoneOptIn().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGlobalZoneOptIn();
		 if(Attr_name.toUpperCase().equals("GLOBALZONEOPTOUT")   && subscriber.getProfiledumpInfoList().get(0).getGlobalZoneOptOut().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGlobalZoneOptOut();
		 if(Attr_name.toUpperCase().equals("GPRSPMAIL")   && subscriber.getProfiledumpInfoList().get(0).getGprsPmail().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGprsPmail();
		 if(Attr_name.toUpperCase().equals("IDD2ACT")   && subscriber.getProfiledumpInfoList().get(0).getIDD2Act().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getIDD2Act();
		 if(Attr_name.toUpperCase().equals("IDD2ACTDATE")   && subscriber.getProfiledumpInfoList().get(0).getIDD2ActDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getIDD2ActDate();
		 if(Attr_name.toUpperCase().equals("IDD2DEACTDATE")   && subscriber.getProfiledumpInfoList().get(0).getIDD2DeactDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getIDD2DeactDate();
		 if(Attr_name.toUpperCase().equals("IDDCUTRATEACT")   && subscriber.getProfiledumpInfoList().get(0).getIDDCutRateAct().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getIDDCutRateAct();
		 if(Attr_name.toUpperCase().equals("IDDCUTRATEACTDATE")   && subscriber.getProfiledumpInfoList().get(0).getIDDCutRateActDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getIDDCutRateActDate();
		 if(Attr_name.toUpperCase().equals("INTERNATIONALMIN")   && subscriber.getProfiledumpInfoList().get(0).getInternationalMin().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getInternationalMin();
		 if(Attr_name.toUpperCase().equals("LANGUAGE")   && subscriber.getProfiledumpInfoList().get(0).getLanguage().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getLanguage();
		 if(Attr_name.toUpperCase().equals("ODBALL")   && subscriber.getProfiledumpInfoList().get(0).getOdbAll().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getOdbAll();
		 if(Attr_name.toUpperCase().equals("ODBINCOM")   && subscriber.getProfiledumpInfoList().get(0).getOdbIncom().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getOdbIncom();
		 if(Attr_name.toUpperCase().equals("ODBINT")   && subscriber.getProfiledumpInfoList().get(0).getOdbInt().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getOdbInt();
		 if(Attr_name.toUpperCase().equals("ODBINTEXC")   && subscriber.getProfiledumpInfoList().get(0).getOdbIntExc().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getOdbIntExc();
		 if(Attr_name.toUpperCase().equals("ODBONLYVOICE")   && subscriber.getProfiledumpInfoList().get(0).getOdbOnlyVoice().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getOdbOnlyVoice();
		 if(Attr_name.toUpperCase().equals("ODBOUTVOICE")   && subscriber.getProfiledumpInfoList().get(0).getOdbOutVoice().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getOdbOutVoice();
		 if(Attr_name.toUpperCase().equals("ODBWR")   && subscriber.getProfiledumpInfoList().get(0).getOdbWR().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getOdbWR();
		 if(Attr_name.toUpperCase().equals("PAYGDATAFNL")   && subscriber.getProfiledumpInfoList().get(0).getPAYGDataFNL().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPAYGDataFNL();
		 if(Attr_name.toUpperCase().equals("PAYGMET")   && subscriber.getProfiledumpInfoList().get(0).getPAYGMet().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPAYGMet();
		 if(Attr_name.toUpperCase().equals("PCNGLOBALOPTIN")   && subscriber.getProfiledumpInfoList().get(0).getPCNGlobalOptin().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPCNGlobalOptin();
		 if(Attr_name.toUpperCase().equals("PCNGLOBALOPTOUT")   && subscriber.getProfiledumpInfoList().get(0).getPCNGlobalOptout().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPCNGlobalOptout();
		 if(Attr_name.toUpperCase().equals("PLAN")   && subscriber.getProfiledumpInfoList().get(0).getPlan().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPlan();
		 if(Attr_name.toUpperCase().equals("RECEIVEPOSTCALLSMS")   && subscriber.getProfiledumpInfoList().get(0).getReceivePostCallSMS().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getReceivePostCallSMS();
		 if(Attr_name.toUpperCase().equals("SOCIALBNDLACTCHANNEL")   && subscriber.getProfiledumpInfoList().get(0).getSocialBndlActChannel().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getSocialBndlActChannel();
		 if(Attr_name.toUpperCase().equals("SOCIALBNDLDEACTCHANNEL")   && subscriber.getProfiledumpInfoList().get(0).getSocialBndlDeActChannel().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getSocialBndlDeActChannel();
		 if(Attr_name.toUpperCase().equals("BSTRVCEINTNUMEXP")   && subscriber.getProfiledumpInfoList().get(0).getBstrVceIntNumExp().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVceIntNumExp();	
		 if(Attr_name.toUpperCase().equals("BSTRVCENATNUMEXP")   && subscriber.getProfiledumpInfoList().get(0).getBstrVceNatNumExp().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVceNatNumExp();	
		 if(Attr_name.toUpperCase().equals("BSTRVINCHNGSALLWD")   && subscriber.getProfiledumpInfoList().get(0).getBstrVINChngsAllwd().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVINChngsAllwd();	
		 if(Attr_name.toUpperCase().equals("BSTRVNNCHNGSALLWD")   && subscriber.getProfiledumpInfoList().get(0).getBstrVNNChngsAllwd().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBstrVNNChngsAllwd();	
		 if(Attr_name.toUpperCase().equals("TRANSLATEDNUMBER")   && subscriber.getProfiledumpInfoList().get(0).getTranslatedNumber().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getTranslatedNumber();	
		 if(Attr_name.toUpperCase().equals("TRANSNUMBER")   && subscriber.getProfiledumpInfoList().get(0).getTransNumber().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getTransNumber();	
		 if(Attr_name.toUpperCase().equals("SMSBNDL1RECUR")   && subscriber.getProfiledumpInfoList().get(0).getSmsBndl1Recur().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getSmsBndl1Recur();
		 if(Attr_name.toUpperCase().equals("SMSEXPDATE")   && subscriber.getProfiledumpInfoList().get(0).getSmsExpDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getSmsExpDate();
		 if(Attr_name.toUpperCase().equals("SMSBNDL2RECUR")   && subscriber.getProfiledumpInfoList().get(0).getSmsBndl2Recur().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getSmsBndl2Recur();
		 if(Attr_name.toUpperCase().equals("PRICESHOUT")   && subscriber.getProfiledumpInfoList().get(0).getPriceShout().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getPriceShout();		
		 if(Attr_name.toUpperCase().equals("BLCKBRRYACT")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryAct().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryAct();
		 if(Attr_name.toUpperCase().equals("BLCKBRRYCNFDACT")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryCnfdAct().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryCnfdAct();
		 if(Attr_name.toUpperCase().equals("BLCKBRRYACTCNFRMDATE")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryActCnfrmDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryActCnfrmDate();
		 if(Attr_name.toUpperCase().equals("BLCKBRRYEXPDATE")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryExpDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryExpDate();
		 if(Attr_name.toUpperCase().equals("SMSBNDL3RECUR")   && subscriber.getProfiledumpInfoList().get(0).getSmsBndl3Recur().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getSmsBndl3Recur();
		 if(Attr_name.toUpperCase().equals("BESPOKE")   && subscriber.getProfiledumpInfoList().get(0).getBespoke().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getBespoke();
		 if(Attr_name.toUpperCase().equals("MANRENDATELESS1Y")   && subscriber.getProfiledumpInfoList().get(0).getManRenDateLess1Y().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getManRenDateLess1Y();
		 if(Attr_name.toUpperCase().equals("DATABNDL1RECUR")   && subscriber.getProfiledumpInfoList().get(0).getDataBndl1Recur().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getDataBndl1Recur();
		 if(Attr_name.toUpperCase().equals("DATABNDLTYPE")   && subscriber.getProfiledumpInfoList().get(0).getDataBndlType().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getDataBndlType();
		 if(Attr_name.toUpperCase().equals("DATAEXPDATE")   && subscriber.getProfiledumpInfoList().get(0).getDataExpDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getDataExpDate();
		 if(Attr_name.toUpperCase().equals("DATAGRACEACT")   && subscriber.getProfiledumpInfoList().get(0).getDataGraceAct().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getDataGraceAct();
		 if(Attr_name.toUpperCase().equals("DATAGRACEEND")   && subscriber.getProfiledumpInfoList().get(0).getDataGraceEnd().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getDataGraceEnd();
		 if(Attr_name.toUpperCase().equals("DATABNDL2RECUR")   && subscriber.getProfiledumpInfoList().get(0).getDataBndl2Recur().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getDataBndl2Recur();
		 if(Attr_name.toUpperCase().equals("DATABNDL3RECUR")   && subscriber.getProfiledumpInfoList().get(0).getDataBndl3Recur().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getDataBndl3Recur();
		 if(Attr_name.toUpperCase().equals("DATABNDL4RECUR")   && subscriber.getProfiledumpInfoList().get(0).getDataBndl4Recur().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getDataBndl4Recur();
		 if(Attr_name.toUpperCase().equals("DATABNDL5RECUR")   && subscriber.getProfiledumpInfoList().get(0).getDataBndl5Recur().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getDataBndl5Recur();
		 if(Attr_name.toUpperCase().equals("DATABNDL6RECUR")   && subscriber.getProfiledumpInfoList().get(0).getDataBndl6Recur().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getDataBndl6Recur();
		 if(Attr_name.toUpperCase().equals("DATABNDL8RECUR")   && subscriber.getProfiledumpInfoList().get(0).getDataBndl8Recur().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getDataBndl8Recur();
		 if(Attr_name.toUpperCase().equals("DATABNDL9RECUR")   && subscriber.getProfiledumpInfoList().get(0).getDataBndl9Recur().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getDataBndl9Recur();
		 if(Attr_name.toUpperCase().equals("MBB")   && subscriber.getProfiledumpInfoList().get(0).getMBB().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBB();
		 if(Attr_name.toUpperCase().equals("MBBEXPDATE")   && subscriber.getProfiledumpInfoList().get(0).getMBBExpDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBBExpDate();
		 if(Attr_name.toUpperCase().equals("MBBUNLIMITED")   && subscriber.getProfiledumpInfoList().get(0).getMBBUnlimited().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBBUnlimited();
		 if(Attr_name.toUpperCase().equals("MBBUNLIMEXPDATE")   && subscriber.getProfiledumpInfoList().get(0).getMBBUnlimExpDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBBUnlimExpDate();
		 if(Attr_name.toUpperCase().equals("MBB2GB")   && subscriber.getProfiledumpInfoList().get(0).getMBB2GB().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBB2GB();
		 if(Attr_name.toUpperCase().equals("MBB2GBEXPDATE")   && subscriber.getProfiledumpInfoList().get(0).getMBB2GBExpDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBB2GBExpDate();
		 if(Attr_name.toUpperCase().equals("MBB10GB")   && subscriber.getProfiledumpInfoList().get(0).getMBB10GB().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBB10GB();
		 if(Attr_name.toUpperCase().equals("MBB10GBEXPDATE")   && subscriber.getProfiledumpInfoList().get(0).getMBB10GBExpDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBB10GBExpDate();
		 if(Attr_name.toUpperCase().equals("MBBGRACEACT")   && subscriber.getProfiledumpInfoList().get(0).getMBBGraceAct().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBBGraceAct();
		 if(Attr_name.toUpperCase().equals("MBBGRACEEND")   && subscriber.getProfiledumpInfoList().get(0).getMBBGraceEnd().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBBGraceEnd();
		 if(Attr_name.toUpperCase().equals("MBBOFFEREXPDATE")   && subscriber.getProfiledumpInfoList().get(0).getMBBOfferExpDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBBOfferExpDate();
		 if(Attr_name.toUpperCase().equals("TP_SOCIAL_DEACT_CONF")   && subscriber.getProfiledumpInfoList().get(0).getTPSocialDeactConf().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getTPSocialDeactConf();
		 if(Attr_name.toUpperCase().equals("CBACT")   && subscriber.getProfiledumpInfoList().get(0).getCbAct().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbAct();
		 if(Attr_name.toUpperCase().equals("POKESMSOPTOUTDATE")   && subscriber.getProfiledumpInfoList().get(0).getPokeSMSOptoutDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPokeSMSOptoutDate();
		 if(Attr_name.toUpperCase().equals("POKESMSOPTINDATE")   && subscriber.getProfiledumpInfoList().get(0).getPokeSMSOptinDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPokeSMSOptinDate();
		 if(Attr_name.toUpperCase().equals("BLCKBRRYACTREQDATE")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryActReqDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryActReqDate();
		 if(Attr_name.toUpperCase().equals("BLCKBRRYDEACTREQDATE")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryDeActReqDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryDeActReqDate();
		 if(Attr_name.toUpperCase().equals("BLCKBRRYDEACTRBKDATE")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryDeactRbkDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryDeactRbkDate();
		 if(Attr_name.toUpperCase().equals("BRONZEDEACTDATE")   && subscriber.getProfiledumpInfoList().get(0).getBronzeDeactDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBronzeDeactDate();
		 if(Attr_name.toUpperCase().equals("BRONZEACTDATE")   && subscriber.getProfiledumpInfoList().get(0).getBronzeActDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBronzeActDate();
		 if(Attr_name.toUpperCase().equals("UMSCNFRMD")   && subscriber.getProfiledumpInfoList().get(0).getUmsCnfrmd().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getUmsCnfrmd();
		 if(Attr_name.toUpperCase().equals("UMSINFSUB")   && subscriber.getProfiledumpInfoList().get(0).getUmsInfSub().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getUmsInfSub();
		 if(Attr_name.toUpperCase().equals("UMS")   && subscriber.getProfiledumpInfoList().get(0).getUms().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getUms();
		 if(Attr_name.toUpperCase().equals("UMSF2M")   && subscriber.getProfiledumpInfoList().get(0).getUmsF2M().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getUmsF2M();
		 if(Attr_name.toUpperCase().equals("UMSSMS2F")   && subscriber.getProfiledumpInfoList().get(0).getUmsSMS2F().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getUmsSMS2F();
		 if(Attr_name.toUpperCase().equals("UMSVM2MMS")   && subscriber.getProfiledumpInfoList().get(0).getUmsVm2MMS().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getUmsVm2MMS();
		 if(Attr_name.toUpperCase().equals("TRAVSUMOPTINDATE")   && subscriber.getProfiledumpInfoList().get(0).getTravSumOptInDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getTravSumOptInDate();
		 if(Attr_name.toUpperCase().equals("PASSOPTINDATE")   && subscriber.getProfiledumpInfoList().get(0).getPassOptInDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPassOptInDate();
		 if(Attr_name.toUpperCase().equals("PASSOPTOUTDATE")   && subscriber.getProfiledumpInfoList().get(0).getPassOptOutDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPassOptOutDate();
		 if(Attr_name.toUpperCase().equals("TWINSIMNUMBER")   && subscriber.getProfiledumpInfoList().get(0).getTwinSIMNumber().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getTwinSIMNumber();
		 if(Attr_name.toUpperCase().equals("FSTTIMEACTIVATION")   && subscriber.getProfiledumpInfoList().get(0).getFstTimeActivation().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getFstTimeActivation();
		 if(Attr_name.toUpperCase().equals("ANNIVERSARYTOPUPPROMO")   && subscriber.getProfiledumpInfoList().get(0).getAnniversarytopuppromo().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getAnniversarytopuppromo();
		 if(Attr_name.toUpperCase().equals("ANNIVERSARYTOPUPPROMOEND")   && subscriber.getProfiledumpInfoList().get(0).getAnniversarytopuppromoEnd().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getAnniversarytopuppromoEnd();
		 if(Attr_name.toUpperCase().equals("SMARTPHONEEXPIRY")   && subscriber.getProfiledumpInfoList().get(0).getSmartPhoneExpiry().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getSmartPhoneExpiry();
		 if(Attr_name.toUpperCase().equals("DSP_PROMO")   && subscriber.getProfiledumpInfoList().get(0).getDSPPromo().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getDSPPromo();
		 if(Attr_name.toUpperCase().equals("GUIBALADJDATE")   && subscriber.getProfiledumpInfoList().get(0).getGuiBalAdjDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGuiBalAdjDate();
		 if(Attr_name.toUpperCase().equals("GUIBALADJVAL")   && subscriber.getProfiledumpInfoList().get(0).getGuiBalAdjVal().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGuiBalAdjVal();
		 if(Attr_name.toUpperCase().equals("BBBUNDNAME")   && subscriber.getProfiledumpInfoList().get(0).getBBBundName().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBBBundName();
		 if(Attr_name.toUpperCase().equals("BBPROMO")   && subscriber.getProfiledumpInfoList().get(0).getBBPromo().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBBPromo();
		 if(Attr_name.toUpperCase().equals("BSCSACT")   && subscriber.getProfiledumpInfoList().get(0).getBSCSAct().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBSCSAct();
		 if(Attr_name.toUpperCase().equals("BBPROMOSUBDATE")   && subscriber.getProfiledumpInfoList().get(0).getBBPromoSubDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBBPromoSubDate();
		 if(Attr_name.toUpperCase().equals("BBPROMOENDDATE")   && subscriber.getProfiledumpInfoList().get(0).getBBPromoEndDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBBPromoEndDate();
		 if(Attr_name.toUpperCase().equals("BLCKBRRYSPRSSSMS")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrrySprssSMS().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrrySprssSMS();
		 if(Attr_name.toUpperCase().equals("BLCKBRRYDEACTCNFRMDT")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryDeActCnfrmDt().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryDeActCnfrmDt();
		 if(Attr_name.toUpperCase().equals("PAYG_LIFE_OPTIN")   && subscriber.getProfiledumpInfoList().get(0).getPaygLifeOptin().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPaygLifeOptin();
		 if(Attr_name.toUpperCase().equals("GPRSBASICSER")   && subscriber.getProfiledumpInfoList().get(0).getGprsBasicSer().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGprsBasicSer();
		 if(Attr_name.toUpperCase().equals("GPRSWAP")   && subscriber.getProfiledumpInfoList().get(0).getGprsWap().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGprsWap();
		 if(Attr_name.toUpperCase().equals("SMSMTPP")   && subscriber.getProfiledumpInfoList().get(0).getSmsMtPp().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getSmsMtPp();
		 if(Attr_name.toUpperCase().equals("GPRSMMS")   && subscriber.getProfiledumpInfoList().get(0).getGprsMms().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGprsMms();
		 if(Attr_name.toUpperCase().equals("SMSMOPP")   && subscriber.getProfiledumpInfoList().get(0).getSmsMoPp().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getSmsMoPp();
		 if(Attr_name.toUpperCase().equals("CALLHOLD")   && subscriber.getProfiledumpInfoList().get(0).getCallhold().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCallhold();
		 if(Attr_name.toUpperCase().equals("CALLWAIT")   && subscriber.getProfiledumpInfoList().get(0).getCallwait().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCallwait();
		 if(Attr_name.toUpperCase().equals("MULTIPARTY")   && subscriber.getProfiledumpInfoList().get(0).getMultiParty().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getMultiParty();
		 if(Attr_name.toUpperCase().equals("INTROAM")   && subscriber.getProfiledumpInfoList().get(0).getIntRoam().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getIntRoam();
		 if(Attr_name.toUpperCase().equals("CLIP")   && subscriber.getProfiledumpInfoList().get(0).getClip().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getClip();
		 if(Attr_name.toUpperCase().equals("VDIOCALL")   && subscriber.getProfiledumpInfoList().get(0).getVdioCall().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getVdioCall();
		 if(Attr_name.toUpperCase().equals("DUCS")   && subscriber.getProfiledumpInfoList().get(0).getDuCS().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getDuCS();
		 if(Attr_name.toUpperCase().equals("BSCS_ACCOUNT_NUM")   && subscriber.getProfiledumpInfoList().get(0).getBSCSAccountNum().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getBSCSAccountNum();
		 if(Attr_name.toUpperCase().equals("CS_NOTIFICATION_NUM")   && subscriber.getProfiledumpInfoList().get(0).getCSNotificationNum().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCSNotificationNum();
		 if(Attr_name.toUpperCase().equals("CBGRACEACT")   && subscriber.getProfiledumpInfoList().get(0).getCbGraceAct().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbGraceAct();
		 if(Attr_name.toUpperCase().equals("CBGRACEEND")   && subscriber.getProfiledumpInfoList().get(0).getCbGraceEnd().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbGraceEnd();
		 if(Attr_name.toUpperCase().equals("CBGRACERENEW")   && subscriber.getProfiledumpInfoList().get(0).getCbGraceRenew().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCbGraceRenew();
		 if(Attr_name.toUpperCase().equals("GPRSCARRIER")   && subscriber.getProfiledumpInfoList().get(0).getGprsCarrier().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getGprsCarrier();
		 if(Attr_name.toUpperCase().equals("PAYGDATALINEOFFER")   && subscriber.getProfiledumpInfoList().get(0).getPAYGDataLineOffer().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPAYGDataLineOffer();
		 if(Attr_name.toUpperCase().equals("PAYGDATAWHSP")   && subscriber.getProfiledumpInfoList().get(0).getPAYGDataWHSP().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPAYGDataWHSP();
		 if(Attr_name.toUpperCase().equals("PAYGDATAWH")   && subscriber.getProfiledumpInfoList().get(0).getPAYGDataWH().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getPAYGDataWH();
		 if(Attr_name.toUpperCase().equals("MSISDNCHGDATE")   && subscriber.getProfiledumpInfoList().get(0).getMsisdnChgDate().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getMsisdnChgDate();
		 if(Attr_name.toUpperCase().equals("MAINMSISDN")   && subscriber.getProfiledumpInfoList().get(0).getMainMSISDN().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getMainMSISDN();
		 if(Attr_name.toUpperCase().equals("CASHRETURN")   && subscriber.getProfiledumpInfoList().get(0).getCashReturn().length() > 0)
				Attr_value = subscriber.getProfiledumpInfoList().get(0).getCashReturn();
		 if(Attr_name.toUpperCase().equals("NEWTOPUPVALUE")   && subscriber.getProfiledumpInfoList().get(0).getNewTopUpValue().length() > 0)
			 	Attr_value = subscriber.getProfiledumpInfoList().get(0).getNewTopUpValue();
		 if(Attr_name.toUpperCase().equals("BDGTCNTRLTOPUP")   && subscriber.getProfiledumpInfoList().get(0).getBdgtCntrlTopUp().length() > 0)
			 	Attr_value = subscriber.getProfiledumpInfoList().get(0).getBdgtCntrlTopUp();
		 if(Attr_name.toUpperCase().equals("BLCKBRRYBUNDLE")   && subscriber.getProfiledumpInfoList().get(0).getBlckBrryBundle().length() > 0)
			 	Attr_value = subscriber.getProfiledumpInfoList().get(0).getBlckBrryBundle();
		 if(Attr_name.toUpperCase().equals("MBB10GBWELCOME")   && subscriber.getProfiledumpInfoList().get(0).getMBB10GBWelcome().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBB10GBWelcome();		
		 if(Attr_name.toUpperCase().equals("MBB2GBWELCOME")   && subscriber.getProfiledumpInfoList().get(0).getMBB2GBWelcome().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBB2GBWelcome();		
		 if(Attr_name.toUpperCase().equals("MBBUNLIMWELCOME")   && subscriber.getProfiledumpInfoList().get(0).getMBBUnlimWelcome().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBBUnlimWelcome();		
		 if(Attr_name.toUpperCase().equals("MBBWELCOME")   && subscriber.getProfiledumpInfoList().get(0).getMBBWelcome().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMBBWelcome();		
		 if(Attr_name.toUpperCase().equals("GUIBALADJCOUNT")   && subscriber.getProfiledumpInfoList().get(0).getGuiBalAdjCount().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getGuiBalAdjCount();
		 if(Attr_name.toUpperCase().equals("LASTCHANGEDATEEXPIRY")   && subscriber.getProfiledumpInfoList().get(0).getLastChangeDateExpiry().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getLastChangeDateExpiry();
		 if(Attr_name.toUpperCase().equals("NEWPPBUNDLE")   && subscriber.getProfiledumpInfoList().get(0).getNewPPBundle().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getNewPPBundle();
		 if(Attr_name.toUpperCase().equals("SMSBNDLTYPE")   && subscriber.getProfiledumpInfoList().get(0).getSmsBndlType().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getSmsBndlType();
		 if(Attr_name.toUpperCase().equals("PCN1STCALL")   && subscriber.getProfiledumpInfoList().get(0).getPCN1stCall().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getPCN1stCall();		
		 if(Attr_name.toUpperCase().equals("RLHACT_REQDATE")   && subscriber.getProfiledumpInfoList().get(0).getRLHActReqDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getRLHActReqDate();		
		 if(Attr_name.toUpperCase().equals("RLHDEACT_DATE")   && subscriber.getProfiledumpInfoList().get(0).getRLHDeactDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getRLHDeactDate();		
		 if(Attr_name.toUpperCase().equals("ENTPDINTNUM")   && subscriber.getProfiledumpInfoList().get(0).getEntPDIntNum().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getEntPDIntNum();		
		 if(Attr_name.toUpperCase().equals("ALORCHGBONUSOPTINDT")   && subscriber.getProfiledumpInfoList().get(0).getAloRchgBonusOptinDt().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getAloRchgBonusOptinDt();
	     if(Attr_name.toUpperCase().equals("CUSTOMER_ID")   && subscriber.getProfiledumpInfoList().get(0).getCustomerID().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCustomerID();	
		 if(Attr_name.toUpperCase().equals("IDDBUNDLEACTDATE")   && subscriber.getProfiledumpInfoList().get(0).getIDDBundleActDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getIDDBundleActDate();		
		 if(Attr_name.toUpperCase().equals("ICPBUNDLE")   && subscriber.getProfiledumpInfoList().get(0).getICPBundle().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getICPBundle();		
		 if(Attr_name.toUpperCase().equals("MERCURY2DOPTINOUTDATE")   && subscriber.getProfiledumpInfoList().get(0).getMercury2DOptinoutDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMercury2DOptinoutDate();		
		 if(Attr_name.toUpperCase().equals("MERCURY2MOPTINOUTDATE")   && subscriber.getProfiledumpInfoList().get(0).getMercury2MOptinoutDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMercury2MOptinoutDate();		
		 if(Attr_name.toUpperCase().equals("FAMSPONSR")   && subscriber.getProfiledumpInfoList().get(0).getFamSponsr().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getFamSponsr();
		 if(Attr_name.toUpperCase().equals("FAMSPONSORETISALAT")   && subscriber.getProfiledumpInfoList().get(0).getFamsponsorEtisalat().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getFamsponsorEtisalat();
		 if(Attr_name.toUpperCase().equals("NPPSOCBNDLDAPACT")   && subscriber.getProfiledumpInfoList().get(0).getNPPSocBndlDAPAct().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getNPPSocBndlDAPAct();
		 if(Attr_name.toUpperCase().equals("NPPBISDAPMOD")   && subscriber.getProfiledumpInfoList().get(0).getNPPBISDAPMod().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getNPPBISDAPMod();
		 if(Attr_name.toUpperCase().equals("NPPBBSOCIALDAPMOD")   && subscriber.getProfiledumpInfoList().get(0).getNPPBBSocialDAPMod().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getNPPBBSocialDAPMod();
		 if(Attr_name.toUpperCase().equals("NPPBBSOCIALDAPDEACT")   && subscriber.getProfiledumpInfoList().get(0).getNPPBBSocialDAPDeact().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getNPPBBSocialDAPDeact();
		 if(Attr_name.toUpperCase().equals("MERCURYDOPTINDATE")   && subscriber.getProfiledumpInfoList().get(0).getMercuryDOptinDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMercuryDOptinDate();			 
		 if(Attr_name.toUpperCase().equals("MERCURYDOPTOUTDATE")   && subscriber.getProfiledumpInfoList().get(0).getMercuryDOptoutDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getMercuryDOptoutDate();
		 if(Attr_name.toUpperCase().equals("CVMDEACTDATE")   && subscriber.getProfiledumpInfoList().get(0).getCVMDeactDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getCVMDeactDate();
		 if(Attr_name.toUpperCase().equals("IDDRATECUTTERSOPTINDATE")   && subscriber.getProfiledumpInfoList().get(0).getIDDRateCuttersOptinDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getIDDRateCuttersOptinDate();
		 if(Attr_name.toUpperCase().equals("IDDRATECUTTERSOPTOUTDATE")   && subscriber.getProfiledumpInfoList().get(0).getIDDRateCuttersOptoutDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getIDDRateCuttersOptoutDate();
		 if(Attr_name.toUpperCase().equals("NONSTOPSOCACTDATE")   && subscriber.getProfiledumpInfoList().get(0).getNonstopSocActDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getNonstopSocActDate();
		 if(Attr_name.toUpperCase().equals("NONSTOPSOCDEACTDATE")   && subscriber.getProfiledumpInfoList().get(0).getNonstopSocDeactDate().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getNonstopSocDeactDate();
		 if(Attr_name.toUpperCase().equals("OWFNFLABEL")   && subscriber.getProfiledumpInfoList().get(0).getOWFnFLabel().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getOWFnFLabel();
		 if(Attr_name.toUpperCase().equals("ONEWAYFNF")   && subscriber.getProfiledumpInfoList().get(0).getOneWayFnF().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getOneWayFnF();
		 if(Attr_name.toUpperCase().equals("ONEWAYFNFMODCOUNT")   && subscriber.getProfiledumpInfoList().get(0).getOneWayFnFModCount().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getOneWayFnFModCount();
		 if(Attr_name.toUpperCase().equals("AUTORECHARGEVALUE")   && subscriber.getProfiledumpInfoList().get(0).getAutoRechargeValue().length() > 0)
			Attr_value = subscriber.getProfiledumpInfoList().get(0).getAutoRechargeValue();
		 
		return Attr_value;		
	}
		
}
