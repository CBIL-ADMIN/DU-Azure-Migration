
package com.ericsson.jibx.beans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="CIS_PARKING_LIST">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="CIS_PARKING_INFO" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class CISPARKINGINFO -->
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class CISPARKINGLIST
{
    private List<CISPARKINGINFO> CISPARKINGINFOList = new ArrayList<CISPARKINGINFO>();

    /** 
     * Get the list of 'CIS_PARKING_INFO' element items.
     * 
     * @return list
     */
    public List<CISPARKINGINFO> getCISPARKINGINFOList() {
        return CISPARKINGINFOList;
    }

    /** 
     * Set the list of 'CIS_PARKING_INFO' element items.
     * 
     * @param list
     */
    public void setCISPARKINGINFOList(List<CISPARKINGINFO> list) {
        CISPARKINGINFOList = list;
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="CIS_PARKING_INFO" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:all>
     *       &lt;xs:element type="xs:string" name="Product_name"/>
     *       &lt;xs:element type="xs:string" name="BT_Name"/>
     *       &lt;xs:element type="xs:string" name="BT_ID"/>
     *       &lt;xs:element type="xs:string" name="NWPC_State"/>
     *       &lt;xs:element type="xs:string" name="id"/>
     *       &lt;xs:element type="xs:string" name="msisdn"/>
     *       &lt;xs:element type="xs:string" name="product_id"/>
     *       &lt;xs:element type="xs:string" name="circle_id"/>
     *       &lt;xs:element type="xs:string" name="status"/>
     *       &lt;xs:element type="xs:string" name="expiry_date"/>
     *       &lt;xs:element type="xs:string" name="start_date"/>
     *       &lt;xs:element type="xs:string" name="product_type"/>
     *       &lt;xs:element type="xs:string" name="service_name"/>
     *       &lt;xs:element type="xs:string" name="last_action_date"/>
     *       &lt;xs:element type="xs:string" name="product_description"/>
     *       &lt;xs:element type="xs:string" name="pay_src"/>
     *       &lt;xs:element type="xs:string" name="ben_msisdn"/>
     *       &lt;xs:element type="xs:string" name="product_cost"/>
     *       &lt;xs:element type="xs:string" name="srcchannel"/>
     *       &lt;xs:element type="xs:string" name="offer_id"/>
     *       &lt;xs:element type="xs:string" name="product_category"/>
     *       &lt;xs:element type="xs:string" name="bundle_name"/>
     *       &lt;xs:element type="xs:string" name="base_bundle_name"/>
     *       &lt;xs:element type="xs:string" name="product_purchase_type"/>
     *       &lt;xs:element type="xs:string" name="language_id"/>
     *       &lt;xs:element type="xs:string" name="pre_notif_status"/>
     *       &lt;xs:element type="xs:string" name="post_notif_status"/>
     *       &lt;xs:element type="xs:string" name="retry_limit"/>
     *       &lt;xs:element type="xs:string" name="priority"/>
     *       &lt;xs:element type="xs:string" name="processing_state"/>
     *       &lt;xs:element type="xs:string" name="send_sms"/>
     *       &lt;xs:element type="xs:string" name="segment_id"/>
     *       &lt;xs:element type="xs:string" name="previous_status"/>
     *       &lt;xs:element type="xs:string" name="correlation_id"/>
     *       &lt;xs:element type="xs:string" name="pre_notification_count"/>
     *       &lt;xs:element type="xs:string" name="post_notification_count"/>
     *       &lt;xs:element type="xs:string" name="status_change_time"/>
     *       &lt;xs:element type="xs:string" name="subscription_mode"/>
     *     &lt;/xs:all>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class CISPARKINGINFO
    {
        private String productName;
        private String BTName;
        private String BTID;
        private String NWPCState;
        private String id;
        private String msisdn;
        private String productId;
        private String circleId;
        private String status;
        private String expiryDate;
        private String startDate;
        private String productType;
        private String serviceName;
        private String lastActionDate;
        private String productDescription;
        private String paySrc;
        private String benMsisdn;
        private String productCost;
        private String srcchannel;
        private String offerId;
        private String productCategory;
        private String bundleName;
        private String baseBundleName;
        private String productPurchaseType;
        private String languageId;
        private String preNotifStatus;
        private String postNotifStatus;
        private String retryLimit;
        private String priority;
        private String processingState;
        private String sendSms;
        private String segmentId;
        private String previousStatus;
        private String correlationId;
        private String preNotificationCount;
        private String postNotificationCount;
        private String statusChangeTime;
        private String subscriptionMode;

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
         * Get the 'expiry_date' element value.
         * 
         * @return value
         */
        public String getExpiryDate() {
            return expiryDate;
        }

        /** 
         * Set the 'expiry_date' element value.
         * 
         * @param expiryDate
         */
        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        /** 
         * Get the 'start_date' element value.
         * 
         * @return value
         */
        public String getStartDate() {
            return startDate;
        }

        /** 
         * Set the 'start_date' element value.
         * 
         * @param startDate
         */
        public void setStartDate(String startDate) {
            this.startDate = startDate;
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
         * Get the 'service_name' element value.
         * 
         * @return value
         */
        public String getServiceName() {
            return serviceName;
        }

        /** 
         * Set the 'service_name' element value.
         * 
         * @param serviceName
         */
        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
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
         * Get the 'offer_id' element value.
         * 
         * @return value
         */
        public String getOfferId() {
            return offerId;
        }

        /** 
         * Set the 'offer_id' element value.
         * 
         * @param offerId
         */
        public void setOfferId(String offerId) {
            this.offerId = offerId;
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
         * Get the 'status_change_time' element value.
         * 
         * @return value
         */
        public String getStatusChangeTime() {
            return statusChangeTime;
        }

        /** 
         * Set the 'status_change_time' element value.
         * 
         * @param statusChangeTime
         */
        public void setStatusChangeTime(String statusChangeTime) {
            this.statusChangeTime = statusChangeTime;
        }

        /** 
         * Get the 'subscription_mode' element value.
         * 
         * @return value
         */
        public String getSubscriptionMode() {
            return subscriptionMode;
        }

        /** 
         * Set the 'subscription_mode' element value.
         * 
         * @param subscriptionMode
         */
        public void setSubscriptionMode(String subscriptionMode) {
            this.subscriptionMode = subscriptionMode;
        }
    }
}
