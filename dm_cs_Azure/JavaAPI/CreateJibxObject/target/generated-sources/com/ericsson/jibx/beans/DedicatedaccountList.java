
package com.ericsson.jibx.beans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="dedicatedaccount_list">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="dedicatedaccount_info" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class DedicatedaccountInfo -->
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class DedicatedaccountList
{
    private List<DedicatedaccountInfo> dedicatedaccountInfoList = new ArrayList<DedicatedaccountInfo>();

    /** 
     * Get the list of 'dedicatedaccount_info' element items.
     * 
     * @return list
     */
    public List<DedicatedaccountInfo> getDedicatedaccountInfoList() {
        return dedicatedaccountInfoList;
    }

    /** 
     * Set the list of 'dedicatedaccount_info' element items.
     * 
     * @param list
     */
    public void setDedicatedaccountInfoList(List<DedicatedaccountInfo> list) {
        dedicatedaccountInfoList = list;
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="dedicatedaccount_info" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:all>
     *       &lt;xs:element type="xs:string" name="account_id"/>
     *       &lt;xs:element type="xs:string" name="sequence_id"/>
     *       &lt;xs:element type="xs:string" name="id_1"/>
     *       &lt;xs:element type="xs:string" name="balance_1"/>
     *       &lt;xs:element type="xs:string" name="expiry_date_1"/>
     *       &lt;xs:element type="xs:string" name="id_2"/>
     *       &lt;xs:element type="xs:string" name="balance_2"/>
     *       &lt;xs:element type="xs:string" name="expiry_date_2"/>
     *       &lt;xs:element type="xs:string" name="id_3"/>
     *       &lt;xs:element type="xs:string" name="balance_3"/>
     *       &lt;xs:element type="xs:string" name="expiry_date_3"/>
     *       &lt;xs:element type="xs:string" name="id_4"/>
     *       &lt;xs:element type="xs:string" name="balance_4"/>
     *       &lt;xs:element type="xs:string" name="expiry_date_4"/>
     *       &lt;xs:element type="xs:string" name="id_5"/>
     *       &lt;xs:element type="xs:string" name="balance_5"/>
     *       &lt;xs:element type="xs:string" name="expiry_date_5"/>
     *       &lt;xs:element type="xs:string" name="id_6"/>
     *       &lt;xs:element type="xs:string" name="balance_6"/>
     *       &lt;xs:element type="xs:string" name="expiry_date_6"/>
     *       &lt;xs:element type="xs:string" name="id_7"/>
     *       &lt;xs:element type="xs:string" name="balance_7"/>
     *       &lt;xs:element type="xs:string" name="expiry_date_7"/>
     *       &lt;xs:element type="xs:string" name="id_8"/>
     *       &lt;xs:element type="xs:string" name="balance_8"/>
     *       &lt;xs:element type="xs:string" name="expiry_date_8"/>
     *       &lt;xs:element type="xs:string" name="id_9"/>
     *       &lt;xs:element type="xs:string" name="balance_9"/>
     *       &lt;xs:element type="xs:string" name="expiry_date_9"/>
     *       &lt;xs:element type="xs:string" name="id_10"/>
     *       &lt;xs:element type="xs:string" name="balance_10"/>
     *       &lt;xs:element type="xs:string" name="expiry_date_10"/>
     *       &lt;xs:element type="xs:string" name="start_date_1"/>
     *       &lt;xs:element type="xs:string" name="start_date_2"/>
     *       &lt;xs:element type="xs:string" name="start_date_3"/>
     *       &lt;xs:element type="xs:string" name="start_date_4"/>
     *       &lt;xs:element type="xs:string" name="start_date_5"/>
     *       &lt;xs:element type="xs:string" name="start_date_6"/>
     *       &lt;xs:element type="xs:string" name="start_date_7"/>
     *       &lt;xs:element type="xs:string" name="start_date_8"/>
     *       &lt;xs:element type="xs:string" name="pam_service_id_10"/>
     *       &lt;xs:element type="xs:string" name="start_date_9"/>
     *       &lt;xs:element type="xs:string" name="product_id_10"/>
     *       &lt;xs:element type="xs:string" name="product_id_1"/>
     *       &lt;xs:element type="xs:string" name="product_id_2"/>
     *       &lt;xs:element type="xs:string" name="product_id_3"/>
     *       &lt;xs:element type="xs:string" name="product_id_4"/>
     *       &lt;xs:element type="xs:string" name="product_id_5"/>
     *       &lt;xs:element type="xs:string" name="product_id_6"/>
     *       &lt;xs:element type="xs:string" name="product_id_7"/>
     *       &lt;xs:element type="xs:string" name="product_id_8"/>
     *       &lt;xs:element type="xs:string" name="product_id_9"/>
     *       &lt;xs:element type="xs:string" name="pam_service_id_1"/>
     *       &lt;xs:element type="xs:string" name="pam_service_id_2"/>
     *       &lt;xs:element type="xs:string" name="pam_service_id_3"/>
     *       &lt;xs:element type="xs:string" name="pam_service_id_4"/>
     *       &lt;xs:element type="xs:string" name="pam_service_id_5"/>
     *       &lt;xs:element type="xs:string" name="pam_service_id_6"/>
     *       &lt;xs:element type="xs:string" name="pam_service_id_7"/>
     *       &lt;xs:element type="xs:string" name="pam_service_id_8"/>
     *       &lt;xs:element type="xs:string" name="pam_service_id_9"/>
     *       &lt;xs:element type="xs:string" name="start_date_10"/>
     *     &lt;/xs:all>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class DedicatedaccountInfo
    {
        private String accountId;
        private String sequenceId;
        private String id1;
        private String balance1;
        private String expiryDate1;
        private String id2;
        private String balance2;
        private String expiryDate2;
        private String id3;
        private String balance3;
        private String expiryDate3;
        private String id4;
        private String balance4;
        private String expiryDate4;
        private String id5;
        private String balance5;
        private String expiryDate5;
        private String id6;
        private String balance6;
        private String expiryDate6;
        private String id7;
        private String balance7;
        private String expiryDate7;
        private String id8;
        private String balance8;
        private String expiryDate8;
        private String id9;
        private String balance9;
        private String expiryDate9;
        private String id10;
        private String balance10;
        private String expiryDate10;
        private String startDate1;
        private String startDate2;
        private String startDate3;
        private String startDate4;
        private String startDate5;
        private String startDate6;
        private String startDate7;
        private String startDate8;
        private String pamServiceId10;
        private String startDate9;
        private String productId10;
        private String productId1;
        private String productId2;
        private String productId3;
        private String productId4;
        private String productId5;
        private String productId6;
        private String productId7;
        private String productId8;
        private String productId9;
        private String pamServiceId1;
        private String pamServiceId2;
        private String pamServiceId3;
        private String pamServiceId4;
        private String pamServiceId5;
        private String pamServiceId6;
        private String pamServiceId7;
        private String pamServiceId8;
        private String pamServiceId9;
        private String startDate10;

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
         * Get the 'sequence_id' element value.
         * 
         * @return value
         */
        public String getSequenceId() {
            return sequenceId;
        }

        /** 
         * Set the 'sequence_id' element value.
         * 
         * @param sequenceId
         */
        public void setSequenceId(String sequenceId) {
            this.sequenceId = sequenceId;
        }

        /** 
         * Get the 'id_1' element value.
         * 
         * @return value
         */
        public String getId1() {
            return id1;
        }

        /** 
         * Set the 'id_1' element value.
         * 
         * @param id1
         */
        public void setId1(String id1) {
            this.id1 = id1;
        }

        /** 
         * Get the 'balance_1' element value.
         * 
         * @return value
         */
        public String getBalance1() {
            return balance1;
        }

        /** 
         * Set the 'balance_1' element value.
         * 
         * @param balance1
         */
        public void setBalance1(String balance1) {
            this.balance1 = balance1;
        }

        /** 
         * Get the 'expiry_date_1' element value.
         * 
         * @return value
         */
        public String getExpiryDate1() {
            return expiryDate1;
        }

        /** 
         * Set the 'expiry_date_1' element value.
         * 
         * @param expiryDate1
         */
        public void setExpiryDate1(String expiryDate1) {
            this.expiryDate1 = expiryDate1;
        }

        /** 
         * Get the 'id_2' element value.
         * 
         * @return value
         */
        public String getId2() {
            return id2;
        }

        /** 
         * Set the 'id_2' element value.
         * 
         * @param id2
         */
        public void setId2(String id2) {
            this.id2 = id2;
        }

        /** 
         * Get the 'balance_2' element value.
         * 
         * @return value
         */
        public String getBalance2() {
            return balance2;
        }

        /** 
         * Set the 'balance_2' element value.
         * 
         * @param balance2
         */
        public void setBalance2(String balance2) {
            this.balance2 = balance2;
        }

        /** 
         * Get the 'expiry_date_2' element value.
         * 
         * @return value
         */
        public String getExpiryDate2() {
            return expiryDate2;
        }

        /** 
         * Set the 'expiry_date_2' element value.
         * 
         * @param expiryDate2
         */
        public void setExpiryDate2(String expiryDate2) {
            this.expiryDate2 = expiryDate2;
        }

        /** 
         * Get the 'id_3' element value.
         * 
         * @return value
         */
        public String getId3() {
            return id3;
        }

        /** 
         * Set the 'id_3' element value.
         * 
         * @param id3
         */
        public void setId3(String id3) {
            this.id3 = id3;
        }

        /** 
         * Get the 'balance_3' element value.
         * 
         * @return value
         */
        public String getBalance3() {
            return balance3;
        }

        /** 
         * Set the 'balance_3' element value.
         * 
         * @param balance3
         */
        public void setBalance3(String balance3) {
            this.balance3 = balance3;
        }

        /** 
         * Get the 'expiry_date_3' element value.
         * 
         * @return value
         */
        public String getExpiryDate3() {
            return expiryDate3;
        }

        /** 
         * Set the 'expiry_date_3' element value.
         * 
         * @param expiryDate3
         */
        public void setExpiryDate3(String expiryDate3) {
            this.expiryDate3 = expiryDate3;
        }

        /** 
         * Get the 'id_4' element value.
         * 
         * @return value
         */
        public String getId4() {
            return id4;
        }

        /** 
         * Set the 'id_4' element value.
         * 
         * @param id4
         */
        public void setId4(String id4) {
            this.id4 = id4;
        }

        /** 
         * Get the 'balance_4' element value.
         * 
         * @return value
         */
        public String getBalance4() {
            return balance4;
        }

        /** 
         * Set the 'balance_4' element value.
         * 
         * @param balance4
         */
        public void setBalance4(String balance4) {
            this.balance4 = balance4;
        }

        /** 
         * Get the 'expiry_date_4' element value.
         * 
         * @return value
         */
        public String getExpiryDate4() {
            return expiryDate4;
        }

        /** 
         * Set the 'expiry_date_4' element value.
         * 
         * @param expiryDate4
         */
        public void setExpiryDate4(String expiryDate4) {
            this.expiryDate4 = expiryDate4;
        }

        /** 
         * Get the 'id_5' element value.
         * 
         * @return value
         */
        public String getId5() {
            return id5;
        }

        /** 
         * Set the 'id_5' element value.
         * 
         * @param id5
         */
        public void setId5(String id5) {
            this.id5 = id5;
        }

        /** 
         * Get the 'balance_5' element value.
         * 
         * @return value
         */
        public String getBalance5() {
            return balance5;
        }

        /** 
         * Set the 'balance_5' element value.
         * 
         * @param balance5
         */
        public void setBalance5(String balance5) {
            this.balance5 = balance5;
        }

        /** 
         * Get the 'expiry_date_5' element value.
         * 
         * @return value
         */
        public String getExpiryDate5() {
            return expiryDate5;
        }

        /** 
         * Set the 'expiry_date_5' element value.
         * 
         * @param expiryDate5
         */
        public void setExpiryDate5(String expiryDate5) {
            this.expiryDate5 = expiryDate5;
        }

        /** 
         * Get the 'id_6' element value.
         * 
         * @return value
         */
        public String getId6() {
            return id6;
        }

        /** 
         * Set the 'id_6' element value.
         * 
         * @param id6
         */
        public void setId6(String id6) {
            this.id6 = id6;
        }

        /** 
         * Get the 'balance_6' element value.
         * 
         * @return value
         */
        public String getBalance6() {
            return balance6;
        }

        /** 
         * Set the 'balance_6' element value.
         * 
         * @param balance6
         */
        public void setBalance6(String balance6) {
            this.balance6 = balance6;
        }

        /** 
         * Get the 'expiry_date_6' element value.
         * 
         * @return value
         */
        public String getExpiryDate6() {
            return expiryDate6;
        }

        /** 
         * Set the 'expiry_date_6' element value.
         * 
         * @param expiryDate6
         */
        public void setExpiryDate6(String expiryDate6) {
            this.expiryDate6 = expiryDate6;
        }

        /** 
         * Get the 'id_7' element value.
         * 
         * @return value
         */
        public String getId7() {
            return id7;
        }

        /** 
         * Set the 'id_7' element value.
         * 
         * @param id7
         */
        public void setId7(String id7) {
            this.id7 = id7;
        }

        /** 
         * Get the 'balance_7' element value.
         * 
         * @return value
         */
        public String getBalance7() {
            return balance7;
        }

        /** 
         * Set the 'balance_7' element value.
         * 
         * @param balance7
         */
        public void setBalance7(String balance7) {
            this.balance7 = balance7;
        }

        /** 
         * Get the 'expiry_date_7' element value.
         * 
         * @return value
         */
        public String getExpiryDate7() {
            return expiryDate7;
        }

        /** 
         * Set the 'expiry_date_7' element value.
         * 
         * @param expiryDate7
         */
        public void setExpiryDate7(String expiryDate7) {
            this.expiryDate7 = expiryDate7;
        }

        /** 
         * Get the 'id_8' element value.
         * 
         * @return value
         */
        public String getId8() {
            return id8;
        }

        /** 
         * Set the 'id_8' element value.
         * 
         * @param id8
         */
        public void setId8(String id8) {
            this.id8 = id8;
        }

        /** 
         * Get the 'balance_8' element value.
         * 
         * @return value
         */
        public String getBalance8() {
            return balance8;
        }

        /** 
         * Set the 'balance_8' element value.
         * 
         * @param balance8
         */
        public void setBalance8(String balance8) {
            this.balance8 = balance8;
        }

        /** 
         * Get the 'expiry_date_8' element value.
         * 
         * @return value
         */
        public String getExpiryDate8() {
            return expiryDate8;
        }

        /** 
         * Set the 'expiry_date_8' element value.
         * 
         * @param expiryDate8
         */
        public void setExpiryDate8(String expiryDate8) {
            this.expiryDate8 = expiryDate8;
        }

        /** 
         * Get the 'id_9' element value.
         * 
         * @return value
         */
        public String getId9() {
            return id9;
        }

        /** 
         * Set the 'id_9' element value.
         * 
         * @param id9
         */
        public void setId9(String id9) {
            this.id9 = id9;
        }

        /** 
         * Get the 'balance_9' element value.
         * 
         * @return value
         */
        public String getBalance9() {
            return balance9;
        }

        /** 
         * Set the 'balance_9' element value.
         * 
         * @param balance9
         */
        public void setBalance9(String balance9) {
            this.balance9 = balance9;
        }

        /** 
         * Get the 'expiry_date_9' element value.
         * 
         * @return value
         */
        public String getExpiryDate9() {
            return expiryDate9;
        }

        /** 
         * Set the 'expiry_date_9' element value.
         * 
         * @param expiryDate9
         */
        public void setExpiryDate9(String expiryDate9) {
            this.expiryDate9 = expiryDate9;
        }

        /** 
         * Get the 'id_10' element value.
         * 
         * @return value
         */
        public String getId10() {
            return id10;
        }

        /** 
         * Set the 'id_10' element value.
         * 
         * @param id10
         */
        public void setId10(String id10) {
            this.id10 = id10;
        }

        /** 
         * Get the 'balance_10' element value.
         * 
         * @return value
         */
        public String getBalance10() {
            return balance10;
        }

        /** 
         * Set the 'balance_10' element value.
         * 
         * @param balance10
         */
        public void setBalance10(String balance10) {
            this.balance10 = balance10;
        }

        /** 
         * Get the 'expiry_date_10' element value.
         * 
         * @return value
         */
        public String getExpiryDate10() {
            return expiryDate10;
        }

        /** 
         * Set the 'expiry_date_10' element value.
         * 
         * @param expiryDate10
         */
        public void setExpiryDate10(String expiryDate10) {
            this.expiryDate10 = expiryDate10;
        }

        /** 
         * Get the 'start_date_1' element value.
         * 
         * @return value
         */
        public String getStartDate1() {
            return startDate1;
        }

        /** 
         * Set the 'start_date_1' element value.
         * 
         * @param startDate1
         */
        public void setStartDate1(String startDate1) {
            this.startDate1 = startDate1;
        }

        /** 
         * Get the 'start_date_2' element value.
         * 
         * @return value
         */
        public String getStartDate2() {
            return startDate2;
        }

        /** 
         * Set the 'start_date_2' element value.
         * 
         * @param startDate2
         */
        public void setStartDate2(String startDate2) {
            this.startDate2 = startDate2;
        }

        /** 
         * Get the 'start_date_3' element value.
         * 
         * @return value
         */
        public String getStartDate3() {
            return startDate3;
        }

        /** 
         * Set the 'start_date_3' element value.
         * 
         * @param startDate3
         */
        public void setStartDate3(String startDate3) {
            this.startDate3 = startDate3;
        }

        /** 
         * Get the 'start_date_4' element value.
         * 
         * @return value
         */
        public String getStartDate4() {
            return startDate4;
        }

        /** 
         * Set the 'start_date_4' element value.
         * 
         * @param startDate4
         */
        public void setStartDate4(String startDate4) {
            this.startDate4 = startDate4;
        }

        /** 
         * Get the 'start_date_5' element value.
         * 
         * @return value
         */
        public String getStartDate5() {
            return startDate5;
        }

        /** 
         * Set the 'start_date_5' element value.
         * 
         * @param startDate5
         */
        public void setStartDate5(String startDate5) {
            this.startDate5 = startDate5;
        }

        /** 
         * Get the 'start_date_6' element value.
         * 
         * @return value
         */
        public String getStartDate6() {
            return startDate6;
        }

        /** 
         * Set the 'start_date_6' element value.
         * 
         * @param startDate6
         */
        public void setStartDate6(String startDate6) {
            this.startDate6 = startDate6;
        }

        /** 
         * Get the 'start_date_7' element value.
         * 
         * @return value
         */
        public String getStartDate7() {
            return startDate7;
        }

        /** 
         * Set the 'start_date_7' element value.
         * 
         * @param startDate7
         */
        public void setStartDate7(String startDate7) {
            this.startDate7 = startDate7;
        }

        /** 
         * Get the 'start_date_8' element value.
         * 
         * @return value
         */
        public String getStartDate8() {
            return startDate8;
        }

        /** 
         * Set the 'start_date_8' element value.
         * 
         * @param startDate8
         */
        public void setStartDate8(String startDate8) {
            this.startDate8 = startDate8;
        }

        /** 
         * Get the 'pam_service_id_10' element value.
         * 
         * @return value
         */
        public String getPamServiceId10() {
            return pamServiceId10;
        }

        /** 
         * Set the 'pam_service_id_10' element value.
         * 
         * @param pamServiceId10
         */
        public void setPamServiceId10(String pamServiceId10) {
            this.pamServiceId10 = pamServiceId10;
        }

        /** 
         * Get the 'start_date_9' element value.
         * 
         * @return value
         */
        public String getStartDate9() {
            return startDate9;
        }

        /** 
         * Set the 'start_date_9' element value.
         * 
         * @param startDate9
         */
        public void setStartDate9(String startDate9) {
            this.startDate9 = startDate9;
        }

        /** 
         * Get the 'product_id_10' element value.
         * 
         * @return value
         */
        public String getProductId10() {
            return productId10;
        }

        /** 
         * Set the 'product_id_10' element value.
         * 
         * @param productId10
         */
        public void setProductId10(String productId10) {
            this.productId10 = productId10;
        }

        /** 
         * Get the 'product_id_1' element value.
         * 
         * @return value
         */
        public String getProductId1() {
            return productId1;
        }

        /** 
         * Set the 'product_id_1' element value.
         * 
         * @param productId1
         */
        public void setProductId1(String productId1) {
            this.productId1 = productId1;
        }

        /** 
         * Get the 'product_id_2' element value.
         * 
         * @return value
         */
        public String getProductId2() {
            return productId2;
        }

        /** 
         * Set the 'product_id_2' element value.
         * 
         * @param productId2
         */
        public void setProductId2(String productId2) {
            this.productId2 = productId2;
        }

        /** 
         * Get the 'product_id_3' element value.
         * 
         * @return value
         */
        public String getProductId3() {
            return productId3;
        }

        /** 
         * Set the 'product_id_3' element value.
         * 
         * @param productId3
         */
        public void setProductId3(String productId3) {
            this.productId3 = productId3;
        }

        /** 
         * Get the 'product_id_4' element value.
         * 
         * @return value
         */
        public String getProductId4() {
            return productId4;
        }

        /** 
         * Set the 'product_id_4' element value.
         * 
         * @param productId4
         */
        public void setProductId4(String productId4) {
            this.productId4 = productId4;
        }

        /** 
         * Get the 'product_id_5' element value.
         * 
         * @return value
         */
        public String getProductId5() {
            return productId5;
        }

        /** 
         * Set the 'product_id_5' element value.
         * 
         * @param productId5
         */
        public void setProductId5(String productId5) {
            this.productId5 = productId5;
        }

        /** 
         * Get the 'product_id_6' element value.
         * 
         * @return value
         */
        public String getProductId6() {
            return productId6;
        }

        /** 
         * Set the 'product_id_6' element value.
         * 
         * @param productId6
         */
        public void setProductId6(String productId6) {
            this.productId6 = productId6;
        }

        /** 
         * Get the 'product_id_7' element value.
         * 
         * @return value
         */
        public String getProductId7() {
            return productId7;
        }

        /** 
         * Set the 'product_id_7' element value.
         * 
         * @param productId7
         */
        public void setProductId7(String productId7) {
            this.productId7 = productId7;
        }

        /** 
         * Get the 'product_id_8' element value.
         * 
         * @return value
         */
        public String getProductId8() {
            return productId8;
        }

        /** 
         * Set the 'product_id_8' element value.
         * 
         * @param productId8
         */
        public void setProductId8(String productId8) {
            this.productId8 = productId8;
        }

        /** 
         * Get the 'product_id_9' element value.
         * 
         * @return value
         */
        public String getProductId9() {
            return productId9;
        }

        /** 
         * Set the 'product_id_9' element value.
         * 
         * @param productId9
         */
        public void setProductId9(String productId9) {
            this.productId9 = productId9;
        }

        /** 
         * Get the 'pam_service_id_1' element value.
         * 
         * @return value
         */
        public String getPamServiceId1() {
            return pamServiceId1;
        }

        /** 
         * Set the 'pam_service_id_1' element value.
         * 
         * @param pamServiceId1
         */
        public void setPamServiceId1(String pamServiceId1) {
            this.pamServiceId1 = pamServiceId1;
        }

        /** 
         * Get the 'pam_service_id_2' element value.
         * 
         * @return value
         */
        public String getPamServiceId2() {
            return pamServiceId2;
        }

        /** 
         * Set the 'pam_service_id_2' element value.
         * 
         * @param pamServiceId2
         */
        public void setPamServiceId2(String pamServiceId2) {
            this.pamServiceId2 = pamServiceId2;
        }

        /** 
         * Get the 'pam_service_id_3' element value.
         * 
         * @return value
         */
        public String getPamServiceId3() {
            return pamServiceId3;
        }

        /** 
         * Set the 'pam_service_id_3' element value.
         * 
         * @param pamServiceId3
         */
        public void setPamServiceId3(String pamServiceId3) {
            this.pamServiceId3 = pamServiceId3;
        }

        /** 
         * Get the 'pam_service_id_4' element value.
         * 
         * @return value
         */
        public String getPamServiceId4() {
            return pamServiceId4;
        }

        /** 
         * Set the 'pam_service_id_4' element value.
         * 
         * @param pamServiceId4
         */
        public void setPamServiceId4(String pamServiceId4) {
            this.pamServiceId4 = pamServiceId4;
        }

        /** 
         * Get the 'pam_service_id_5' element value.
         * 
         * @return value
         */
        public String getPamServiceId5() {
            return pamServiceId5;
        }

        /** 
         * Set the 'pam_service_id_5' element value.
         * 
         * @param pamServiceId5
         */
        public void setPamServiceId5(String pamServiceId5) {
            this.pamServiceId5 = pamServiceId5;
        }

        /** 
         * Get the 'pam_service_id_6' element value.
         * 
         * @return value
         */
        public String getPamServiceId6() {
            return pamServiceId6;
        }

        /** 
         * Set the 'pam_service_id_6' element value.
         * 
         * @param pamServiceId6
         */
        public void setPamServiceId6(String pamServiceId6) {
            this.pamServiceId6 = pamServiceId6;
        }

        /** 
         * Get the 'pam_service_id_7' element value.
         * 
         * @return value
         */
        public String getPamServiceId7() {
            return pamServiceId7;
        }

        /** 
         * Set the 'pam_service_id_7' element value.
         * 
         * @param pamServiceId7
         */
        public void setPamServiceId7(String pamServiceId7) {
            this.pamServiceId7 = pamServiceId7;
        }

        /** 
         * Get the 'pam_service_id_8' element value.
         * 
         * @return value
         */
        public String getPamServiceId8() {
            return pamServiceId8;
        }

        /** 
         * Set the 'pam_service_id_8' element value.
         * 
         * @param pamServiceId8
         */
        public void setPamServiceId8(String pamServiceId8) {
            this.pamServiceId8 = pamServiceId8;
        }

        /** 
         * Get the 'pam_service_id_9' element value.
         * 
         * @return value
         */
        public String getPamServiceId9() {
            return pamServiceId9;
        }

        /** 
         * Set the 'pam_service_id_9' element value.
         * 
         * @param pamServiceId9
         */
        public void setPamServiceId9(String pamServiceId9) {
            this.pamServiceId9 = pamServiceId9;
        }

        /** 
         * Get the 'start_date_10' element value.
         * 
         * @return value
         */
        public String getStartDate10() {
            return startDate10;
        }

        /** 
         * Set the 'start_date_10' element value.
         * 
         * @param startDate10
         */
        public void setStartDate10(String startDate10) {
            this.startDate10 = startDate10;
        }
    }
}
