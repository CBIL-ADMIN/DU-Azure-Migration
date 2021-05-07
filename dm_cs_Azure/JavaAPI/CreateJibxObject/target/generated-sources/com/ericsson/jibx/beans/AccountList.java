
package com.ericsson.jibx.beans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="account_list">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="account_info" minOccurs="0" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class AccountInfo -->
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class AccountList
{
    private List<AccountInfo> accountInfoList = new ArrayList<AccountInfo>();

    /** 
     * Get the list of 'account_info' element items.
     * 
     * @return list
     */
    public List<AccountInfo> getAccountInfoList() {
        return accountInfoList;
    }

    /** 
     * Set the list of 'account_info' element items.
     * 
     * @param list
     */
    public void setAccountInfoList(List<AccountInfo> list) {
        accountInfoList = list;
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="account_info" minOccurs="0" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:all>
     *       &lt;xs:element type="xs:string" name="id"/>
     *       &lt;xs:element type="xs:string" name="account_class"/>
     *       &lt;xs:element type="xs:string" name="orig_account_class"/>
     *       &lt;xs:element type="xs:string" name="account_class_expiry"/>
     *       &lt;xs:element type="xs:string" name="units"/>
     *       &lt;xs:element type="xs:string" name="activated"/>
     *       &lt;xs:element type="xs:string" name="sfee_expiry_date"/>
     *       &lt;xs:element type="xs:string" name="sup_expiry_date"/>
     *       &lt;xs:element type="xs:string" name="sfee_done_date"/>
     *       &lt;xs:element type="xs:string" name="previous_sfee_done_date"/>
     *       &lt;xs:element type="xs:string" name="sfee_status"/>
     *       &lt;xs:element type="xs:string" name="sup_status"/>
     *       &lt;xs:element type="xs:string" name="neg_balance_start"/>
     *       &lt;xs:element type="xs:string" name="neg_balance_barred"/>
     *       &lt;xs:element type="xs:string" name="account_disconnect"/>
     *       &lt;xs:element type="xs:string" name="account_status"/>
     *       &lt;xs:element type="xs:string" name="prom_notification"/>
     *       &lt;xs:element type="xs:string" name="service_offerings"/>
     *       &lt;xs:element type="xs:string" name="account_group_id"/>
     *       &lt;xs:element type="xs:string" name="community_id1"/>
     *       &lt;xs:element type="xs:string" name="community_id2"/>
     *       &lt;xs:element type="xs:string" name="community_id3"/>
     *       &lt;xs:element type="xs:string" name="account_home_region"/>
     *       &lt;xs:element type="xs:string" name="product_id_counter"/>
     *       &lt;xs:element type="xs:string" name="account_lock"/>
     *       &lt;xs:element type="xs:string" name="account_prepaid_empty_limit"/>
     *     &lt;/xs:all>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class AccountInfo
    {
        private String id;
        private String accountClass;
        private String origAccountClass;
        private String accountClassExpiry;
        private String units;
        private String activated;
        private String sfeeExpiryDate;
        private String supExpiryDate;
        private String sfeeDoneDate;
        private String previousSfeeDoneDate;
        private String sfeeStatus;
        private String supStatus;
        private String negBalanceStart;
        private String negBalanceBarred;
        private String accountDisconnect;
        private String accountStatus;
        private String promNotification;
        private String serviceOfferings;
        private String accountGroupId;
        private String communityId1;
        private String communityId2;
        private String communityId3;
        private String accountHomeRegion;
        private String productIdCounter;
        private String accountLock;
        private String accountPrepaidEmptyLimit;

        /** 
         * Get the 'id' element value.
         * 
         * @return value
         */
        public String getId() {
            return id;
        }

        /** 
         * Set the 'id' element value.
         * 
         * @param id
         */
        public void setId(String id) {
            this.id = id;
        }

        /** 
         * Get the 'account_class' element value.
         * 
         * @return value
         */
        public String getAccountClass() {
            return accountClass;
        }

        /** 
         * Set the 'account_class' element value.
         * 
         * @param accountClass
         */
        public void setAccountClass(String accountClass) {
            this.accountClass = accountClass;
        }

        /** 
         * Get the 'orig_account_class' element value.
         * 
         * @return value
         */
        public String getOrigAccountClass() {
            return origAccountClass;
        }

        /** 
         * Set the 'orig_account_class' element value.
         * 
         * @param origAccountClass
         */
        public void setOrigAccountClass(String origAccountClass) {
            this.origAccountClass = origAccountClass;
        }

        /** 
         * Get the 'account_class_expiry' element value.
         * 
         * @return value
         */
        public String getAccountClassExpiry() {
            return accountClassExpiry;
        }

        /** 
         * Set the 'account_class_expiry' element value.
         * 
         * @param accountClassExpiry
         */
        public void setAccountClassExpiry(String accountClassExpiry) {
            this.accountClassExpiry = accountClassExpiry;
        }

        /** 
         * Get the 'units' element value.
         * 
         * @return value
         */
        public String getUnits() {
            return units;
        }

        /** 
         * Set the 'units' element value.
         * 
         * @param units
         */
        public void setUnits(String units) {
            this.units = units;
        }

        /** 
         * Get the 'activated' element value.
         * 
         * @return value
         */
        public String getActivated() {
            return activated;
        }

        /** 
         * Set the 'activated' element value.
         * 
         * @param activated
         */
        public void setActivated(String activated) {
            this.activated = activated;
        }

        /** 
         * Get the 'sfee_expiry_date' element value.
         * 
         * @return value
         */
        public String getSfeeExpiryDate() {
            return sfeeExpiryDate;
        }

        /** 
         * Set the 'sfee_expiry_date' element value.
         * 
         * @param sfeeExpiryDate
         */
        public void setSfeeExpiryDate(String sfeeExpiryDate) {
            this.sfeeExpiryDate = sfeeExpiryDate;
        }

        /** 
         * Get the 'sup_expiry_date' element value.
         * 
         * @return value
         */
        public String getSupExpiryDate() {
            return supExpiryDate;
        }

        /** 
         * Set the 'sup_expiry_date' element value.
         * 
         * @param supExpiryDate
         */
        public void setSupExpiryDate(String supExpiryDate) {
            this.supExpiryDate = supExpiryDate;
        }

        /** 
         * Get the 'sfee_done_date' element value.
         * 
         * @return value
         */
        public String getSfeeDoneDate() {
            return sfeeDoneDate;
        }

        /** 
         * Set the 'sfee_done_date' element value.
         * 
         * @param sfeeDoneDate
         */
        public void setSfeeDoneDate(String sfeeDoneDate) {
            this.sfeeDoneDate = sfeeDoneDate;
        }

        /** 
         * Get the 'previous_sfee_done_date' element value.
         * 
         * @return value
         */
        public String getPreviousSfeeDoneDate() {
            return previousSfeeDoneDate;
        }

        /** 
         * Set the 'previous_sfee_done_date' element value.
         * 
         * @param previousSfeeDoneDate
         */
        public void setPreviousSfeeDoneDate(String previousSfeeDoneDate) {
            this.previousSfeeDoneDate = previousSfeeDoneDate;
        }

        /** 
         * Get the 'sfee_status' element value.
         * 
         * @return value
         */
        public String getSfeeStatus() {
            return sfeeStatus;
        }

        /** 
         * Set the 'sfee_status' element value.
         * 
         * @param sfeeStatus
         */
        public void setSfeeStatus(String sfeeStatus) {
            this.sfeeStatus = sfeeStatus;
        }

        /** 
         * Get the 'sup_status' element value.
         * 
         * @return value
         */
        public String getSupStatus() {
            return supStatus;
        }

        /** 
         * Set the 'sup_status' element value.
         * 
         * @param supStatus
         */
        public void setSupStatus(String supStatus) {
            this.supStatus = supStatus;
        }

        /** 
         * Get the 'neg_balance_start' element value.
         * 
         * @return value
         */
        public String getNegBalanceStart() {
            return negBalanceStart;
        }

        /** 
         * Set the 'neg_balance_start' element value.
         * 
         * @param negBalanceStart
         */
        public void setNegBalanceStart(String negBalanceStart) {
            this.negBalanceStart = negBalanceStart;
        }

        /** 
         * Get the 'neg_balance_barred' element value.
         * 
         * @return value
         */
        public String getNegBalanceBarred() {
            return negBalanceBarred;
        }

        /** 
         * Set the 'neg_balance_barred' element value.
         * 
         * @param negBalanceBarred
         */
        public void setNegBalanceBarred(String negBalanceBarred) {
            this.negBalanceBarred = negBalanceBarred;
        }

        /** 
         * Get the 'account_disconnect' element value.
         * 
         * @return value
         */
        public String getAccountDisconnect() {
            return accountDisconnect;
        }

        /** 
         * Set the 'account_disconnect' element value.
         * 
         * @param accountDisconnect
         */
        public void setAccountDisconnect(String accountDisconnect) {
            this.accountDisconnect = accountDisconnect;
        }

        /** 
         * Get the 'account_status' element value.
         * 
         * @return value
         */
        public String getAccountStatus() {
            return accountStatus;
        }

        /** 
         * Set the 'account_status' element value.
         * 
         * @param accountStatus
         */
        public void setAccountStatus(String accountStatus) {
            this.accountStatus = accountStatus;
        }

        /** 
         * Get the 'prom_notification' element value.
         * 
         * @return value
         */
        public String getPromNotification() {
            return promNotification;
        }

        /** 
         * Set the 'prom_notification' element value.
         * 
         * @param promNotification
         */
        public void setPromNotification(String promNotification) {
            this.promNotification = promNotification;
        }

        /** 
         * Get the 'service_offerings' element value.
         * 
         * @return value
         */
        public String getServiceOfferings() {
            return serviceOfferings;
        }

        /** 
         * Set the 'service_offerings' element value.
         * 
         * @param serviceOfferings
         */
        public void setServiceOfferings(String serviceOfferings) {
            this.serviceOfferings = serviceOfferings;
        }

        /** 
         * Get the 'account_group_id' element value.
         * 
         * @return value
         */
        public String getAccountGroupId() {
            return accountGroupId;
        }

        /** 
         * Set the 'account_group_id' element value.
         * 
         * @param accountGroupId
         */
        public void setAccountGroupId(String accountGroupId) {
            this.accountGroupId = accountGroupId;
        }

        /** 
         * Get the 'community_id1' element value.
         * 
         * @return value
         */
        public String getCommunityId1() {
            return communityId1;
        }

        /** 
         * Set the 'community_id1' element value.
         * 
         * @param communityId1
         */
        public void setCommunityId1(String communityId1) {
            this.communityId1 = communityId1;
        }

        /** 
         * Get the 'community_id2' element value.
         * 
         * @return value
         */
        public String getCommunityId2() {
            return communityId2;
        }

        /** 
         * Set the 'community_id2' element value.
         * 
         * @param communityId2
         */
        public void setCommunityId2(String communityId2) {
            this.communityId2 = communityId2;
        }

        /** 
         * Get the 'community_id3' element value.
         * 
         * @return value
         */
        public String getCommunityId3() {
            return communityId3;
        }

        /** 
         * Set the 'community_id3' element value.
         * 
         * @param communityId3
         */
        public void setCommunityId3(String communityId3) {
            this.communityId3 = communityId3;
        }

        /** 
         * Get the 'account_home_region' element value.
         * 
         * @return value
         */
        public String getAccountHomeRegion() {
            return accountHomeRegion;
        }

        /** 
         * Set the 'account_home_region' element value.
         * 
         * @param accountHomeRegion
         */
        public void setAccountHomeRegion(String accountHomeRegion) {
            this.accountHomeRegion = accountHomeRegion;
        }

        /** 
         * Get the 'product_id_counter' element value.
         * 
         * @return value
         */
        public String getProductIdCounter() {
            return productIdCounter;
        }

        /** 
         * Set the 'product_id_counter' element value.
         * 
         * @param productIdCounter
         */
        public void setProductIdCounter(String productIdCounter) {
            this.productIdCounter = productIdCounter;
        }

        /** 
         * Get the 'account_lock' element value.
         * 
         * @return value
         */
        public String getAccountLock() {
            return accountLock;
        }

        /** 
         * Set the 'account_lock' element value.
         * 
         * @param accountLock
         */
        public void setAccountLock(String accountLock) {
            this.accountLock = accountLock;
        }

        /** 
         * Get the 'account_prepaid_empty_limit' element value.
         * 
         * @return value
         */
        public String getAccountPrepaidEmptyLimit() {
            return accountPrepaidEmptyLimit;
        }

        /** 
         * Set the 'account_prepaid_empty_limit' element value.
         * 
         * @param accountPrepaidEmptyLimit
         */
        public void setAccountPrepaidEmptyLimit(String accountPrepaidEmptyLimit) {
            this.accountPrepaidEmptyLimit = accountPrepaidEmptyLimit;
        }
    }
}
