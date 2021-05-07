
package com.ericsson.jibx.beans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="subscriber_list">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="subscriber_info" minOccurs="0" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class SubscriberInfo -->
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class SubscriberList
{
    private List<SubscriberInfo> subscriberInfoList = new ArrayList<SubscriberInfo>();

    /** 
     * Get the list of 'subscriber_info' element items.
     * 
     * @return list
     */
    public List<SubscriberInfo> getSubscriberInfoList() {
        return subscriberInfoList;
    }

    /** 
     * Set the list of 'subscriber_info' element items.
     * 
     * @param list
     */
    public void setSubscriberInfoList(List<SubscriberInfo> list) {
        subscriberInfoList = list;
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="subscriber_info" minOccurs="0" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:all>
     *       &lt;xs:element type="xs:string" name="msisdn"/>
     *       &lt;xs:element type="xs:string" name="account_id"/>
     *       &lt;xs:element type="xs:string" name="subscriber_status"/>
     *       &lt;xs:element type="xs:string" name="refill_failed"/>
     *       &lt;xs:element type="xs:string" name="refill_bar_end"/>
     *       &lt;xs:element type="xs:string" name="first_ivr_call_done"/>
     *       &lt;xs:element type="xs:string" name="first_call_done"/>
     *       &lt;xs:element type="xs:string" name="language"/>
     *       &lt;xs:element type="xs:string" name="special_announc_played"/>
     *       &lt;xs:element type="xs:string" name="sfee_warn_played"/>
     *       &lt;xs:element type="xs:string" name="sup_warn_played"/>
     *       &lt;xs:element type="xs:string" name="low_level_warn_played"/>
     *       &lt;xs:element type="xs:string" name="wanted_block_status"/>
     *       &lt;xs:element type="xs:string" name="actual_block_status"/>
     *       &lt;xs:element type="xs:string" name="eoc_selection_id"/>
     *       &lt;xs:element type="xs:string" name="pin_code"/>
     *       &lt;xs:element type="xs:string" name="usage_statistic_flags"/>
     *     &lt;/xs:all>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class SubscriberInfo
    {
        private String msisdn;
        private String accountId;
        private String subscriberStatus;
        private String refillFailed;
        private String refillBarEnd;
        private String firstIvrCallDone;
        private String firstCallDone;
        private String language;
        private String specialAnnouncPlayed;
        private String sfeeWarnPlayed;
        private String supWarnPlayed;
        private String lowLevelWarnPlayed;
        private String wantedBlockStatus;
        private String actualBlockStatus;
        private String eocSelectionId;
        private String pinCode;
        private String usageStatisticFlags;

        /** 
         * Get the 'msisdn' element value.
         * 
         * @return value
         */
        public String getMsisdn() {
            return msisdn;
        }

        /** 
         * Set the 'msisdn' element value.
         * 
         * @param msisdn
         */
        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

        /** 
         * Get the 'account_id' element value.
         * 
         * @return value
         */
        public String getAccountId() {
            return accountId;
        }

        /** 
         * Set the 'account_id' element value.
         * 
         * @param accountId
         */
        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        /** 
         * Get the 'subscriber_status' element value.
         * 
         * @return value
         */
        public String getSubscriberStatus() {
            return subscriberStatus;
        }

        /** 
         * Set the 'subscriber_status' element value.
         * 
         * @param subscriberStatus
         */
        public void setSubscriberStatus(String subscriberStatus) {
            this.subscriberStatus = subscriberStatus;
        }

        /** 
         * Get the 'refill_failed' element value.
         * 
         * @return value
         */
        public String getRefillFailed() {
            return refillFailed;
        }

        /** 
         * Set the 'refill_failed' element value.
         * 
         * @param refillFailed
         */
        public void setRefillFailed(String refillFailed) {
            this.refillFailed = refillFailed;
        }

        /** 
         * Get the 'refill_bar_end' element value.
         * 
         * @return value
         */
        public String getRefillBarEnd() {
            return refillBarEnd;
        }

        /** 
         * Set the 'refill_bar_end' element value.
         * 
         * @param refillBarEnd
         */
        public void setRefillBarEnd(String refillBarEnd) {
            this.refillBarEnd = refillBarEnd;
        }

        /** 
         * Get the 'first_ivr_call_done' element value.
         * 
         * @return value
         */
        public String getFirstIvrCallDone() {
            return firstIvrCallDone;
        }

        /** 
         * Set the 'first_ivr_call_done' element value.
         * 
         * @param firstIvrCallDone
         */
        public void setFirstIvrCallDone(String firstIvrCallDone) {
            this.firstIvrCallDone = firstIvrCallDone;
        }

        /** 
         * Get the 'first_call_done' element value.
         * 
         * @return value
         */
        public String getFirstCallDone() {
            return firstCallDone;
        }

        /** 
         * Set the 'first_call_done' element value.
         * 
         * @param firstCallDone
         */
        public void setFirstCallDone(String firstCallDone) {
            this.firstCallDone = firstCallDone;
        }

        /** 
         * Get the 'language' element value.
         * 
         * @return value
         */
        public String getLanguage() {
            return language;
        }

        /** 
         * Set the 'language' element value.
         * 
         * @param language
         */
        public void setLanguage(String language) {
            this.language = language;
        }

        /** 
         * Get the 'special_announc_played' element value.
         * 
         * @return value
         */
        public String getSpecialAnnouncPlayed() {
            return specialAnnouncPlayed;
        }

        /** 
         * Set the 'special_announc_played' element value.
         * 
         * @param specialAnnouncPlayed
         */
        public void setSpecialAnnouncPlayed(String specialAnnouncPlayed) {
            this.specialAnnouncPlayed = specialAnnouncPlayed;
        }

        /** 
         * Get the 'sfee_warn_played' element value.
         * 
         * @return value
         */
        public String getSfeeWarnPlayed() {
            return sfeeWarnPlayed;
        }

        /** 
         * Set the 'sfee_warn_played' element value.
         * 
         * @param sfeeWarnPlayed
         */
        public void setSfeeWarnPlayed(String sfeeWarnPlayed) {
            this.sfeeWarnPlayed = sfeeWarnPlayed;
        }

        /** 
         * Get the 'sup_warn_played' element value.
         * 
         * @return value
         */
        public String getSupWarnPlayed() {
            return supWarnPlayed;
        }

        /** 
         * Set the 'sup_warn_played' element value.
         * 
         * @param supWarnPlayed
         */
        public void setSupWarnPlayed(String supWarnPlayed) {
            this.supWarnPlayed = supWarnPlayed;
        }

        /** 
         * Get the 'low_level_warn_played' element value.
         * 
         * @return value
         */
        public String getLowLevelWarnPlayed() {
            return lowLevelWarnPlayed;
        }

        /** 
         * Set the 'low_level_warn_played' element value.
         * 
         * @param lowLevelWarnPlayed
         */
        public void setLowLevelWarnPlayed(String lowLevelWarnPlayed) {
            this.lowLevelWarnPlayed = lowLevelWarnPlayed;
        }

        /** 
         * Get the 'wanted_block_status' element value.
         * 
         * @return value
         */
        public String getWantedBlockStatus() {
            return wantedBlockStatus;
        }

        /** 
         * Set the 'wanted_block_status' element value.
         * 
         * @param wantedBlockStatus
         */
        public void setWantedBlockStatus(String wantedBlockStatus) {
            this.wantedBlockStatus = wantedBlockStatus;
        }

        /** 
         * Get the 'actual_block_status' element value.
         * 
         * @return value
         */
        public String getActualBlockStatus() {
            return actualBlockStatus;
        }

        /** 
         * Set the 'actual_block_status' element value.
         * 
         * @param actualBlockStatus
         */
        public void setActualBlockStatus(String actualBlockStatus) {
            this.actualBlockStatus = actualBlockStatus;
        }

        /** 
         * Get the 'eoc_selection_id' element value.
         * 
         * @return value
         */
        public String getEocSelectionId() {
            return eocSelectionId;
        }

        /** 
         * Set the 'eoc_selection_id' element value.
         * 
         * @param eocSelectionId
         */
        public void setEocSelectionId(String eocSelectionId) {
            this.eocSelectionId = eocSelectionId;
        }

        /** 
         * Get the 'pin_code' element value.
         * 
         * @return value
         */
        public String getPinCode() {
            return pinCode;
        }

        /** 
         * Set the 'pin_code' element value.
         * 
         * @param pinCode
         */
        public void setPinCode(String pinCode) {
            this.pinCode = pinCode;
        }

        /** 
         * Get the 'usage_statistic_flags' element value.
         * 
         * @return value
         */
        public String getUsageStatisticFlags() {
            return usageStatisticFlags;
        }

        /** 
         * Set the 'usage_statistic_flags' element value.
         * 
         * @param usageStatisticFlags
         */
        public void setUsageStatisticFlags(String usageStatisticFlags) {
            this.usageStatisticFlags = usageStatisticFlags;
        }
    }
}
