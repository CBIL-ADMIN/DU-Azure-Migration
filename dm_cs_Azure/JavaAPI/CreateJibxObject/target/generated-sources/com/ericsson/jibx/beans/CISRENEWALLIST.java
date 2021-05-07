
package com.ericsson.jibx.beans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="CIS_RENEWAL_LIST">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="CIS_RENEWAL_INFO" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class CISRENEWALINFO -->
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class CISRENEWALLIST
{
    private List<CISRENEWALINFO> CISRENEWALINFOList = new ArrayList<CISRENEWALINFO>();

    /** 
     * Get the list of 'CIS_RENEWAL_INFO' element items.
     * 
     * @return list
     */
    public List<CISRENEWALINFO> getCISRENEWALINFOList() {
        return CISRENEWALINFOList;
    }

    /** 
     * Set the list of 'CIS_RENEWAL_INFO' element items.
     * 
     * @param list
     */
    public void setCISRENEWALINFOList(List<CISRENEWALINFO> list) {
        CISRENEWALINFOList = list;
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="CIS_RENEWAL_INFO" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:all>
     *       &lt;xs:element type="xs:string" name="Product_name"/>
     *       &lt;xs:element type="xs:string" name="BT_Name"/>
     *       &lt;xs:element type="xs:string" name="BT_ID"/>
     *       &lt;xs:element type="xs:string" name="NWPC_State"/>
     *       &lt;xs:element type="xs:string" name="Offer_ID"/>
     *       &lt;xs:element type="xs:string" name="renewal_count"/>
     *       &lt;xs:element type="xs:string" name="grace_date"/>
     *       &lt;xs:element type="xs:string" name="last_action_date"/>
     *       &lt;xs:element type="xs:string" name="last_renewal_date"/>
     *       &lt;xs:element type="xs:string" name="renewal_date"/>
     *       &lt;xs:element type="xs:string" name="status"/>
     *       &lt;xs:element type="xs:string" name="processing_state"/>
     *       &lt;xs:element type="xs:string" name="process_timestamp"/>
     *       &lt;xs:element type="xs:string" name="activation_date"/>
     *       &lt;xs:element type="xs:string" name="circle_id"/>
     *       &lt;xs:element type="xs:string" name="product_id"/>
     *       &lt;xs:element type="xs:string" name="id"/>
     *       &lt;xs:element type="xs:string" name="product_description"/>
     *       &lt;xs:element type="xs:string" name="split_action"/>
     *       &lt;xs:element type="xs:string" name="renewal_value"/>
     *       &lt;xs:element type="xs:string" name="expiry_notification_flag"/>
     *       &lt;xs:element type="xs:string" name="product_type"/>
     *       &lt;xs:element type="xs:string" name="pre_notification_count"/>
     *       &lt;xs:element type="xs:string" name="post_notification_count"/>
     *       &lt;xs:element type="xs:string" name="marketing_text"/>
     *       &lt;xs:element type="xs:string" name="pre_marketing_text_enabled"/>
     *       &lt;xs:element type="xs:string" name="post_marketing_text_enabled"/>
     *       &lt;xs:element type="xs:string" name="retry_limit"/>
     *       &lt;xs:element type="xs:string" name="is_pam_product"/>
     *       &lt;xs:element type="xs:string" name="pay_src"/>
     *       &lt;xs:element type="xs:string" name="ben_msisdn"/>
     *       &lt;xs:element type="xs:string" name="send_sms"/>
     *       &lt;xs:element type="xs:string" name="split_no"/>
     *       &lt;xs:element type="xs:string" name="product_cost"/>
     *       &lt;xs:element type="xs:string" name="pam_id"/>
     *       &lt;xs:element type="xs:string" name="enable_notification"/>
     *       &lt;xs:element type="xs:string" name="recurringgraceperiod"/>
     *       &lt;xs:element type="xs:string" name="srcchannel"/>
     *       &lt;xs:element type="xs:string" name="product_category"/>
     *       &lt;xs:element type="xs:string" name="bundle_name"/>
     *       &lt;xs:element type="xs:string" name="product_purchase_type"/>
     *       &lt;xs:element type="xs:string" name="language_id"/>
     *       &lt;xs:element type="xs:string" name="renewal_status"/>
     *       &lt;xs:element type="xs:string" name="pre_notif_status"/>
     *       &lt;xs:element type="xs:string" name="post_notif_status"/>
     *       &lt;xs:element type="xs:string" name="segment_id"/>
     *       &lt;xs:element type="xs:string" name="deprov_retry_limit"/>
     *       &lt;xs:element type="xs:string" name="deprov_status"/>
     *       &lt;xs:element type="xs:string" name="base_bundle_name"/>
     *       &lt;xs:element type="xs:string" name="gifted_by"/>
     *       &lt;xs:element type="xs:string" name="priority"/>
     *       &lt;xs:element type="xs:string" name="pre_grace_exp_notif_status"/>
     *       &lt;xs:element type="xs:string" name="renewal_num"/>
     *       &lt;xs:element type="xs:string" name="previous_status"/>
     *       &lt;xs:element type="xs:string" name="correlation_id"/>
     *       &lt;xs:element type="xs:string" name="network_status"/>
     *       &lt;xs:element type="xs:string" name="is_grace_chargeable"/>
     *       &lt;xs:element type="xs:string" name="extra_param"/>
     *       &lt;xs:element type="xs:string" name="on_grace_network_deact_enabled"/>
     *     &lt;/xs:all>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class CISRENEWALINFO
    {
        private String productName;
        private String BTName;
        private String BTID;
        private String NWPCState;
        private String offerID;
        private String renewalCount;
        private String graceDate;
        private String lastActionDate;
        private String lastRenewalDate;
        private String renewalDate;
        private String status;
        private String processingState;
        private String processTimestamp;
        private String activationDate;
        private String circleId;
        private String productId;
        private String id;
        private String productDescription;
        private String splitAction;
        private String renewalValue;
        private String expiryNotificationFlag;
        private String productType;
        private String preNotificationCount;
        private String postNotificationCount;
        private String marketingText;
        private String preMarketingTextEnabled;
        private String postMarketingTextEnabled;
        private String retryLimit;
        private String isPamProduct;
        private String paySrc;
        private String benMsisdn;
        private String sendSms;
        private String splitNo;
        private String productCost;
        private String pamId;
        private String enableNotification;
        private String recurringgraceperiod;
        private String srcchannel;
        private String productCategory;
        private String bundleName;
        private String productPurchaseType;
        private String languageId;
        private String renewalStatus;
        private String preNotifStatus;
        private String postNotifStatus;
        private String segmentId;
        private String deprovRetryLimit;
        private String deprovStatus;
        private String baseBundleName;
        private String giftedBy;
        private String priority;
        private String preGraceExpNotifStatus;
        private String renewalNum;
        private String previousStatus;
        private String correlationId;
        private String networkStatus;
        private String isGraceChargeable;
        private String extraParam;
        private String onGraceNetworkDeactEnabled;

        /** 
         * Get the 'Product_name' element value.
         * 
         * @return value
         */
        public String getProductName() {
            return productName;
        }

        /** 
         * Set the 'Product_name' element value.
         * 
         * @param productName
         */
        public void setProductName(String productName) {
            this.productName = productName;
        }

        /** 
         * Get the 'BT_Name' element value.
         * 
         * @return value
         */
        public String getBTName() {
            return BTName;
        }

        /** 
         * Set the 'BT_Name' element value.
         * 
         * @param BTName
         */
        public void setBTName(String BTName) {
            this.BTName = BTName;
        }

        /** 
         * Get the 'BT_ID' element value.
         * 
         * @return value
         */
        public String getBTID() {
            return BTID;
        }

        /** 
         * Set the 'BT_ID' element value.
         * 
         * @param BTID
         */
        public void setBTID(String BTID) {
            this.BTID = BTID;
        }

        /** 
         * Get the 'NWPC_State' element value.
         * 
         * @return value
         */
        public String getNWPCState() {
            return NWPCState;
        }

        /** 
         * Set the 'NWPC_State' element value.
         * 
         * @param NWPCState
         */
        public void setNWPCState(String NWPCState) {
            this.NWPCState = NWPCState;
        }

        /** 
         * Get the 'Offer_ID' element value.
         * 
         * @return value
         */
        public String getOfferID() {
            return offerID;
        }

        /** 
         * Set the 'Offer_ID' element value.
         * 
         * @param offerID
         */
        public void setOfferID(String offerID) {
            this.offerID = offerID;
        }

        /** 
         * Get the 'renewal_count' element value.
         * 
         * @return value
         */
        public String getRenewalCount() {
            return renewalCount;
        }

        /** 
         * Set the 'renewal_count' element value.
         * 
         * @param renewalCount
         */
        public void setRenewalCount(String renewalCount) {
            this.renewalCount = renewalCount;
        }

        /** 
         * Get the 'grace_date' element value.
         * 
         * @return value
         */
        public String getGraceDate() {
            return graceDate;
        }

        /** 
         * Set the 'grace_date' element value.
         * 
         * @param graceDate
         */
        public void setGraceDate(String graceDate) {
            this.graceDate = graceDate;
        }

        /** 
         * Get the 'last_action_date' element value.
         * 
         * @return value
         */
        public String getLastActionDate() {
            return lastActionDate;
        }

        /** 
         * Set the 'last_action_date' element value.
         * 
         * @param lastActionDate
         */
        public void setLastActionDate(String lastActionDate) {
            this.lastActionDate = lastActionDate;
        }

        /** 
         * Get the 'last_renewal_date' element value.
         * 
         * @return value
         */
        public String getLastRenewalDate() {
            return lastRenewalDate;
        }

        /** 
         * Set the 'last_renewal_date' element value.
         * 
         * @param lastRenewalDate
         */
        public void setLastRenewalDate(String lastRenewalDate) {
            this.lastRenewalDate = lastRenewalDate;
        }

        /** 
         * Get the 'renewal_date' element value.
         * 
         * @return value
         */
        public String getRenewalDate() {
            return renewalDate;
        }

        /** 
         * Set the 'renewal_date' element value.
         * 
         * @param renewalDate
         */
        public void setRenewalDate(String renewalDate) {
            this.renewalDate = renewalDate;
        }

        /** 
         * Get the 'status' element value.
         * 
         * @return value
         */
        public String getStatus() {
            return status;
        }

        /** 
         * Set the 'status' element value.
         * 
         * @param status
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /** 
         * Get the 'processing_state' element value.
         * 
         * @return value
         */
        public String getProcessingState() {
            return processingState;
        }

        /** 
         * Set the 'processing_state' element value.
         * 
         * @param processingState
         */
        public void setProcessingState(String processingState) {
            this.processingState = processingState;
        }

        /** 
         * Get the 'process_timestamp' element value.
         * 
         * @return value
         */
        public String getProcessTimestamp() {
            return processTimestamp;
        }

        /** 
         * Set the 'process_timestamp' element value.
         * 
         * @param processTimestamp
         */
        public void setProcessTimestamp(String processTimestamp) {
            this.processTimestamp = processTimestamp;
        }

        /** 
         * Get the 'activation_date' element value.
         * 
         * @return value
         */
        public String getActivationDate() {
            return activationDate;
        }

        /** 
         * Set the 'activation_date' element value.
         * 
         * @param activationDate
         */
        public void setActivationDate(String activationDate) {
            this.activationDate = activationDate;
        }

        /** 
         * Get the 'circle_id' element value.
         * 
         * @return value
         */
        public String getCircleId() {
            return circleId;
        }

        /** 
         * Set the 'circle_id' element value.
         * 
         * @param circleId
         */
        public void setCircleId(String circleId) {
            this.circleId = circleId;
        }

        /** 
         * Get the 'product_id' element value.
         * 
         * @return value
         */
        public String getProductId() {
            return productId;
        }

        /** 
         * Set the 'product_id' element value.
         * 
         * @param productId
         */
        public void setProductId(String productId) {
            this.productId = productId;
        }

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
         * Get the 'product_description' element value.
         * 
         * @return value
         */
        public String getProductDescription() {
            return productDescription;
        }

        /** 
         * Set the 'product_description' element value.
         * 
         * @param productDescription
         */
        public void setProductDescription(String productDescription) {
            this.productDescription = productDescription;
        }

        /** 
         * Get the 'split_action' element value.
         * 
         * @return value
         */
        public String getSplitAction() {
            return splitAction;
        }

        /** 
         * Set the 'split_action' element value.
         * 
         * @param splitAction
         */
        public void setSplitAction(String splitAction) {
            this.splitAction = splitAction;
        }

        /** 
         * Get the 'renewal_value' element value.
         * 
         * @return value
         */
        public String getRenewalValue() {
            return renewalValue;
        }

        /** 
         * Set the 'renewal_value' element value.
         * 
         * @param renewalValue
         */
        public void setRenewalValue(String renewalValue) {
            this.renewalValue = renewalValue;
        }

        /** 
         * Get the 'expiry_notification_flag' element value.
         * 
         * @return value
         */
        public String getExpiryNotificationFlag() {
            return expiryNotificationFlag;
        }

        /** 
         * Set the 'expiry_notification_flag' element value.
         * 
         * @param expiryNotificationFlag
         */
        public void setExpiryNotificationFlag(String expiryNotificationFlag) {
            this.expiryNotificationFlag = expiryNotificationFlag;
        }

        /** 
         * Get the 'product_type' element value.
         * 
         * @return value
         */
        public String getProductType() {
            return productType;
        }

        /** 
         * Set the 'product_type' element value.
         * 
         * @param productType
         */
        public void setProductType(String productType) {
            this.productType = productType;
        }

        /** 
         * Get the 'pre_notification_count' element value.
         * 
         * @return value
         */
        public String getPreNotificationCount() {
            return preNotificationCount;
        }

        /** 
         * Set the 'pre_notification_count' element value.
         * 
         * @param preNotificationCount
         */
        public void setPreNotificationCount(String preNotificationCount) {
            this.preNotificationCount = preNotificationCount;
        }

        /** 
         * Get the 'post_notification_count' element value.
         * 
         * @return value
         */
        public String getPostNotificationCount() {
            return postNotificationCount;
        }

        /** 
         * Set the 'post_notification_count' element value.
         * 
         * @param postNotificationCount
         */
        public void setPostNotificationCount(String postNotificationCount) {
            this.postNotificationCount = postNotificationCount;
        }

        /** 
         * Get the 'marketing_text' element value.
         * 
         * @return value
         */
        public String getMarketingText() {
            return marketingText;
        }

        /** 
         * Set the 'marketing_text' element value.
         * 
         * @param marketingText
         */
        public void setMarketingText(String marketingText) {
            this.marketingText = marketingText;
        }

        /** 
         * Get the 'pre_marketing_text_enabled' element value.
         * 
         * @return value
         */
        public String getPreMarketingTextEnabled() {
            return preMarketingTextEnabled;
        }

        /** 
         * Set the 'pre_marketing_text_enabled' element value.
         * 
         * @param preMarketingTextEnabled
         */
        public void setPreMarketingTextEnabled(String preMarketingTextEnabled) {
            this.preMarketingTextEnabled = preMarketingTextEnabled;
        }

        /** 
         * Get the 'post_marketing_text_enabled' element value.
         * 
         * @return value
         */
        public String getPostMarketingTextEnabled() {
            return postMarketingTextEnabled;
        }

        /** 
         * Set the 'post_marketing_text_enabled' element value.
         * 
         * @param postMarketingTextEnabled
         */
        public void setPostMarketingTextEnabled(String postMarketingTextEnabled) {
            this.postMarketingTextEnabled = postMarketingTextEnabled;
        }

        /** 
         * Get the 'retry_limit' element value.
         * 
         * @return value
         */
        public String getRetryLimit() {
            return retryLimit;
        }

        /** 
         * Set the 'retry_limit' element value.
         * 
         * @param retryLimit
         */
        public void setRetryLimit(String retryLimit) {
            this.retryLimit = retryLimit;
        }

        /** 
         * Get the 'is_pam_product' element value.
         * 
         * @return value
         */
        public String getIsPamProduct() {
            return isPamProduct;
        }

        /** 
         * Set the 'is_pam_product' element value.
         * 
         * @param isPamProduct
         */
        public void setIsPamProduct(String isPamProduct) {
            this.isPamProduct = isPamProduct;
        }

        /** 
         * Get the 'pay_src' element value.
         * 
         * @return value
         */
        public String getPaySrc() {
            return paySrc;
        }

        /** 
         * Set the 'pay_src' element value.
         * 
         * @param paySrc
         */
        public void setPaySrc(String paySrc) {
            this.paySrc = paySrc;
        }

        /** 
         * Get the 'ben_msisdn' element value.
         * 
         * @return value
         */
        public String getBenMsisdn() {
            return benMsisdn;
        }

        /** 
         * Set the 'ben_msisdn' element value.
         * 
         * @param benMsisdn
         */
        public void setBenMsisdn(String benMsisdn) {
            this.benMsisdn = benMsisdn;
        }

        /** 
         * Get the 'send_sms' element value.
         * 
         * @return value
         */
        public String getSendSms() {
            return sendSms;
        }

        /** 
         * Set the 'send_sms' element value.
         * 
         * @param sendSms
         */
        public void setSendSms(String sendSms) {
            this.sendSms = sendSms;
        }

        /** 
         * Get the 'split_no' element value.
         * 
         * @return value
         */
        public String getSplitNo() {
            return splitNo;
        }

        /** 
         * Set the 'split_no' element value.
         * 
         * @param splitNo
         */
        public void setSplitNo(String splitNo) {
            this.splitNo = splitNo;
        }

        /** 
         * Get the 'product_cost' element value.
         * 
         * @return value
         */
        public String getProductCost() {
            return productCost;
        }

        /** 
         * Set the 'product_cost' element value.
         * 
         * @param productCost
         */
        public void setProductCost(String productCost) {
            this.productCost = productCost;
        }

        /** 
         * Get the 'pam_id' element value.
         * 
         * @return value
         */
        public String getPamId() {
            return pamId;
        }

        /** 
         * Set the 'pam_id' element value.
         * 
         * @param pamId
         */
        public void setPamId(String pamId) {
            this.pamId = pamId;
        }

        /** 
         * Get the 'enable_notification' element value.
         * 
         * @return value
         */
        public String getEnableNotification() {
            return enableNotification;
        }

        /** 
         * Set the 'enable_notification' element value.
         * 
         * @param enableNotification
         */
        public void setEnableNotification(String enableNotification) {
            this.enableNotification = enableNotification;
        }

        /** 
         * Get the 'recurringgraceperiod' element value.
         * 
         * @return value
         */
        public String getRecurringgraceperiod() {
            return recurringgraceperiod;
        }

        /** 
         * Set the 'recurringgraceperiod' element value.
         * 
         * @param recurringgraceperiod
         */
        public void setRecurringgraceperiod(String recurringgraceperiod) {
            this.recurringgraceperiod = recurringgraceperiod;
        }

        /** 
         * Get the 'srcchannel' element value.
         * 
         * @return value
         */
        public String getSrcchannel() {
            return srcchannel;
        }

        /** 
         * Set the 'srcchannel' element value.
         * 
         * @param srcchannel
         */
        public void setSrcchannel(String srcchannel) {
            this.srcchannel = srcchannel;
        }

        /** 
         * Get the 'product_category' element value.
         * 
         * @return value
         */
        public String getProductCategory() {
            return productCategory;
        }

        /** 
         * Set the 'product_category' element value.
         * 
         * @param productCategory
         */
        public void setProductCategory(String productCategory) {
            this.productCategory = productCategory;
        }

        /** 
         * Get the 'bundle_name' element value.
         * 
         * @return value
         */
        public String getBundleName() {
            return bundleName;
        }

        /** 
         * Set the 'bundle_name' element value.
         * 
         * @param bundleName
         */
        public void setBundleName(String bundleName) {
            this.bundleName = bundleName;
        }

        /** 
         * Get the 'product_purchase_type' element value.
         * 
         * @return value
         */
        public String getProductPurchaseType() {
            return productPurchaseType;
        }

        /** 
         * Set the 'product_purchase_type' element value.
         * 
         * @param productPurchaseType
         */
        public void setProductPurchaseType(String productPurchaseType) {
            this.productPurchaseType = productPurchaseType;
        }

        /** 
         * Get the 'language_id' element value.
         * 
         * @return value
         */
        public String getLanguageId() {
            return languageId;
        }

        /** 
         * Set the 'language_id' element value.
         * 
         * @param languageId
         */
        public void setLanguageId(String languageId) {
            this.languageId = languageId;
        }

        /** 
         * Get the 'renewal_status' element value.
         * 
         * @return value
         */
        public String getRenewalStatus() {
            return renewalStatus;
        }

        /** 
         * Set the 'renewal_status' element value.
         * 
         * @param renewalStatus
         */
        public void setRenewalStatus(String renewalStatus) {
            this.renewalStatus = renewalStatus;
        }

        /** 
         * Get the 'pre_notif_status' element value.
         * 
         * @return value
         */
        public String getPreNotifStatus() {
            return preNotifStatus;
        }

        /** 
         * Set the 'pre_notif_status' element value.
         * 
         * @param preNotifStatus
         */
        public void setPreNotifStatus(String preNotifStatus) {
            this.preNotifStatus = preNotifStatus;
        }

        /** 
         * Get the 'post_notif_status' element value.
         * 
         * @return value
         */
        public String getPostNotifStatus() {
            return postNotifStatus;
        }

        /** 
         * Set the 'post_notif_status' element value.
         * 
         * @param postNotifStatus
         */
        public void setPostNotifStatus(String postNotifStatus) {
            this.postNotifStatus = postNotifStatus;
        }

        /** 
         * Get the 'segment_id' element value.
         * 
         * @return value
         */
        public String getSegmentId() {
            return segmentId;
        }

        /** 
         * Set the 'segment_id' element value.
         * 
         * @param segmentId
         */
        public void setSegmentId(String segmentId) {
            this.segmentId = segmentId;
        }

        /** 
         * Get the 'deprov_retry_limit' element value.
         * 
         * @return value
         */
        public String getDeprovRetryLimit() {
            return deprovRetryLimit;
        }

        /** 
         * Set the 'deprov_retry_limit' element value.
         * 
         * @param deprovRetryLimit
         */
        public void setDeprovRetryLimit(String deprovRetryLimit) {
            this.deprovRetryLimit = deprovRetryLimit;
        }

        /** 
         * Get the 'deprov_status' element value.
         * 
         * @return value
         */
        public String getDeprovStatus() {
            return deprovStatus;
        }

        /** 
         * Set the 'deprov_status' element value.
         * 
         * @param deprovStatus
         */
        public void setDeprovStatus(String deprovStatus) {
            this.deprovStatus = deprovStatus;
        }

        /** 
         * Get the 'base_bundle_name' element value.
         * 
         * @return value
         */
        public String getBaseBundleName() {
            return baseBundleName;
        }

        /** 
         * Set the 'base_bundle_name' element value.
         * 
         * @param baseBundleName
         */
        public void setBaseBundleName(String baseBundleName) {
            this.baseBundleName = baseBundleName;
        }

        /** 
         * Get the 'gifted_by' element value.
         * 
         * @return value
         */
        public String getGiftedBy() {
            return giftedBy;
        }

        /** 
         * Set the 'gifted_by' element value.
         * 
         * @param giftedBy
         */
        public void setGiftedBy(String giftedBy) {
            this.giftedBy = giftedBy;
        }

        /** 
         * Get the 'priority' element value.
         * 
         * @return value
         */
        public String getPriority() {
            return priority;
        }

        /** 
         * Set the 'priority' element value.
         * 
         * @param priority
         */
        public void setPriority(String priority) {
            this.priority = priority;
        }

        /** 
         * Get the 'pre_grace_exp_notif_status' element value.
         * 
         * @return value
         */
        public String getPreGraceExpNotifStatus() {
            return preGraceExpNotifStatus;
        }

        /** 
         * Set the 'pre_grace_exp_notif_status' element value.
         * 
         * @param preGraceExpNotifStatus
         */
        public void setPreGraceExpNotifStatus(String preGraceExpNotifStatus) {
            this.preGraceExpNotifStatus = preGraceExpNotifStatus;
        }

        /** 
         * Get the 'renewal_num' element value.
         * 
         * @return value
         */
        public String getRenewalNum() {
            return renewalNum;
        }

        /** 
         * Set the 'renewal_num' element value.
         * 
         * @param renewalNum
         */
        public void setRenewalNum(String renewalNum) {
            this.renewalNum = renewalNum;
        }

        /** 
         * Get the 'previous_status' element value.
         * 
         * @return value
         */
        public String getPreviousStatus() {
            return previousStatus;
        }

        /** 
         * Set the 'previous_status' element value.
         * 
         * @param previousStatus
         */
        public void setPreviousStatus(String previousStatus) {
            this.previousStatus = previousStatus;
        }

        /** 
         * Get the 'correlation_id' element value.
         * 
         * @return value
         */
        public String getCorrelationId() {
            return correlationId;
        }

        /** 
         * Set the 'correlation_id' element value.
         * 
         * @param correlationId
         */
        public void setCorrelationId(String correlationId) {
            this.correlationId = correlationId;
        }

        /** 
         * Get the 'network_status' element value.
         * 
         * @return value
         */
        public String getNetworkStatus() {
            return networkStatus;
        }

        /** 
         * Set the 'network_status' element value.
         * 
         * @param networkStatus
         */
        public void setNetworkStatus(String networkStatus) {
            this.networkStatus = networkStatus;
        }

        /** 
         * Get the 'is_grace_chargeable' element value.
         * 
         * @return value
         */
        public String getIsGraceChargeable() {
            return isGraceChargeable;
        }

        /** 
         * Set the 'is_grace_chargeable' element value.
         * 
         * @param isGraceChargeable
         */
        public void setIsGraceChargeable(String isGraceChargeable) {
            this.isGraceChargeable = isGraceChargeable;
        }

        /** 
         * Get the 'extra_param' element value.
         * 
         * @return value
         */
        public String getExtraParam() {
            return extraParam;
        }

        /** 
         * Set the 'extra_param' element value.
         * 
         * @param extraParam
         */
        public void setExtraParam(String extraParam) {
            this.extraParam = extraParam;
        }

        /** 
         * Get the 'on_grace_network_deact_enabled' element value.
         * 
         * @return value
         */
        public String getOnGraceNetworkDeactEnabled() {
            return onGraceNetworkDeactEnabled;
        }

        /** 
         * Set the 'on_grace_network_deact_enabled' element value.
         * 
         * @param onGraceNetworkDeactEnabled
         */
        public void setOnGraceNetworkDeactEnabled(
                String onGraceNetworkDeactEnabled) {
            this.onGraceNetworkDeactEnabled = onGraceNetworkDeactEnabled;
        }
    }
}
