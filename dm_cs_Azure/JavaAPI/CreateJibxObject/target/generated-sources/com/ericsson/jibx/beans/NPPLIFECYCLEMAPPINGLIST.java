
package com.ericsson.jibx.beans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="NPPLIFECYCLE_MAPPING_LIST">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="NPPLIFECYCLE_MAPPING_INFO" minOccurs="0" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class NPPLIFECYCLEMAPPINGINFO -->
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class NPPLIFECYCLEMAPPINGLIST
{
    private List<NPPLIFECYCLEMAPPINGINFO> NPPLIFECYCLEMAPPINGINFOList = new ArrayList<NPPLIFECYCLEMAPPINGINFO>();

    /** 
     * Get the list of 'NPPLIFECYCLE_MAPPING_INFO' element items.
     * 
     * @return list
     */
    public List<NPPLIFECYCLEMAPPINGINFO> getNPPLIFECYCLEMAPPINGINFOList() {
        return NPPLIFECYCLEMAPPINGINFOList;
    }

    /** 
     * Set the list of 'NPPLIFECYCLE_MAPPING_INFO' element items.
     * 
     * @param list
     */
    public void setNPPLIFECYCLEMAPPINGINFOList(
            List<NPPLIFECYCLEMAPPINGINFO> list) {
        NPPLIFECYCLEMAPPINGINFOList = list;
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="NPPLIFECYCLE_MAPPING_INFO" minOccurs="0" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:sequence>
     *       &lt;xs:element type="xs:string" name="SerialNumber"/>
     *       &lt;xs:element type="xs:string" name="CCS_ACCT_TYPE_ID"/>
     *       &lt;xs:element type="xs:string" name="State"/>
     *       &lt;xs:element type="xs:string" name="Ignore_Flag"/>
     *       &lt;xs:element type="xs:string" name="SC_Target"/>
     *       &lt;xs:element type="xs:string" name="PBT_ID"/>
     *       &lt;xs:element type="xs:string" name="UA"/>
     *       &lt;xs:element type="xs:string" name="OFFER" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="SUP_EXPIRY_Date" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="SUF_DAYS" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="SUP_STATUS" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="SFEE_EXPIRY_Date" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="SFEE_STATUS" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="ACTIVATED" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="FIRST_IVR_CALL_DONE" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="FIRST_CALL_DONE" minOccurs="0"/>
     *       &lt;xs:element type="xs:string" name="SUBSCRIBER_STATUS" minOccurs="0"/>
     *     &lt;/xs:sequence>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class NPPLIFECYCLEMAPPINGINFO
    {
        private String serialNumber;
        private String CCSACCTTYPEID;
        private String state;
        private String ignoreFlag;
        private String SCTarget;
        private String PBTID;
        private String UA;
        private String OFFER;
        private String SUPEXPIRYDate;
        private String SUFDAYS;
        private String SUPSTATUS;
        private String SFEEEXPIRYDate;
        private String SFEESTATUS;
        private String ACTIVATED;
        private String FIRSTIVRCALLDONE;
        private String FIRSTCALLDONE;
        private String SUBSCRIBERSTATUS;

        /** 
         * Get the 'SerialNumber' element value.
         * 
         * @return value
         */
        public String getSerialNumber() {
            return serialNumber;
        }

        /** 
         * Set the 'SerialNumber' element value.
         * 
         * @param serialNumber
         */
        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        /** 
         * Get the 'CCS_ACCT_TYPE_ID' element value.
         * 
         * @return value
         */
        public String getCCSACCTTYPEID() {
            return CCSACCTTYPEID;
        }

        /** 
         * Set the 'CCS_ACCT_TYPE_ID' element value.
         * 
         * @param CCSACCTTYPEID
         */
        public void setCCSACCTTYPEID(String CCSACCTTYPEID) {
            this.CCSACCTTYPEID = CCSACCTTYPEID;
        }

        /** 
         * Get the 'State' element value.
         * 
         * @return value
         */
        public String getState() {
            return state;
        }

        /** 
         * Set the 'State' element value.
         * 
         * @param state
         */
        public void setState(String state) {
            this.state = state;
        }

        /** 
         * Get the 'Ignore_Flag' element value.
         * 
         * @return value
         */
        public String getIgnoreFlag() {
            return ignoreFlag;
        }

        /** 
         * Set the 'Ignore_Flag' element value.
         * 
         * @param ignoreFlag
         */
        public void setIgnoreFlag(String ignoreFlag) {
            this.ignoreFlag = ignoreFlag;
        }

        /** 
         * Get the 'SC_Target' element value.
         * 
         * @return value
         */
        public String getSCTarget() {
            return SCTarget;
        }

        /** 
         * Set the 'SC_Target' element value.
         * 
         * @param SCTarget
         */
        public void setSCTarget(String SCTarget) {
            this.SCTarget = SCTarget;
        }

        /** 
         * Get the 'PBT_ID' element value.
         * 
         * @return value
         */
        public String getPBTID() {
            return PBTID;
        }

        /** 
         * Set the 'PBT_ID' element value.
         * 
         * @param PBTID
         */
        public void setPBTID(String PBTID) {
            this.PBTID = PBTID;
        }

        /** 
         * Get the 'UA' element value.
         * 
         * @return value
         */
        public String getUA() {
            return UA;
        }

        /** 
         * Set the 'UA' element value.
         * 
         * @param UA
         */
        public void setUA(String UA) {
            this.UA = UA;
        }

        /** 
         * Get the 'OFFER' element value.
         * 
         * @return value
         */
        public String getOFFER() {
            return OFFER;
        }

        /** 
         * Set the 'OFFER' element value.
         * 
         * @param OFFER
         */
        public void setOFFER(String OFFER) {
            this.OFFER = OFFER;
        }

        /** 
         * Get the 'SUP_EXPIRY_Date' element value.
         * 
         * @return value
         */
        public String getSUPEXPIRYDate() {
            return SUPEXPIRYDate;
        }

        /** 
         * Set the 'SUP_EXPIRY_Date' element value.
         * 
         * @param SUPEXPIRYDate
         */
        public void setSUPEXPIRYDate(String SUPEXPIRYDate) {
            this.SUPEXPIRYDate = SUPEXPIRYDate;
        }

        /** 
         * Get the 'SUF_DAYS' element value.
         * 
         * @return value
         */
        public String getSUFDAYS() {
            return SUFDAYS;
        }

        /** 
         * Set the 'SUF_DAYS' element value.
         * 
         * @param SUFDAYS
         */
        public void setSUFDAYS(String SUFDAYS) {
            this.SUFDAYS = SUFDAYS;
        }

        /** 
         * Get the 'SUP_STATUS' element value.
         * 
         * @return value
         */
        public String getSUPSTATUS() {
            return SUPSTATUS;
        }

        /** 
         * Set the 'SUP_STATUS' element value.
         * 
         * @param SUPSTATUS
         */
        public void setSUPSTATUS(String SUPSTATUS) {
            this.SUPSTATUS = SUPSTATUS;
        }

        /** 
         * Get the 'SFEE_EXPIRY_Date' element value.
         * 
         * @return value
         */
        public String getSFEEEXPIRYDate() {
            return SFEEEXPIRYDate;
        }

        /** 
         * Set the 'SFEE_EXPIRY_Date' element value.
         * 
         * @param SFEEEXPIRYDate
         */
        public void setSFEEEXPIRYDate(String SFEEEXPIRYDate) {
            this.SFEEEXPIRYDate = SFEEEXPIRYDate;
        }

        /** 
         * Get the 'SFEE_STATUS' element value.
         * 
         * @return value
         */
        public String getSFEESTATUS() {
            return SFEESTATUS;
        }

        /** 
         * Set the 'SFEE_STATUS' element value.
         * 
         * @param SFEESTATUS
         */
        public void setSFEESTATUS(String SFEESTATUS) {
            this.SFEESTATUS = SFEESTATUS;
        }

        /** 
         * Get the 'ACTIVATED' element value.
         * 
         * @return value
         */
        public String getACTIVATED() {
            return ACTIVATED;
        }

        /** 
         * Set the 'ACTIVATED' element value.
         * 
         * @param ACTIVATED
         */
        public void setACTIVATED(String ACTIVATED) {
            this.ACTIVATED = ACTIVATED;
        }

        /** 
         * Get the 'FIRST_IVR_CALL_DONE' element value.
         * 
         * @return value
         */
        public String getFIRSTIVRCALLDONE() {
            return FIRSTIVRCALLDONE;
        }

        /** 
         * Set the 'FIRST_IVR_CALL_DONE' element value.
         * 
         * @param FIRSTIVRCALLDONE
         */
        public void setFIRSTIVRCALLDONE(String FIRSTIVRCALLDONE) {
            this.FIRSTIVRCALLDONE = FIRSTIVRCALLDONE;
        }

        /** 
         * Get the 'FIRST_CALL_DONE' element value.
         * 
         * @return value
         */
        public String getFIRSTCALLDONE() {
            return FIRSTCALLDONE;
        }

        /** 
         * Set the 'FIRST_CALL_DONE' element value.
         * 
         * @param FIRSTCALLDONE
         */
        public void setFIRSTCALLDONE(String FIRSTCALLDONE) {
            this.FIRSTCALLDONE = FIRSTCALLDONE;
        }

        /** 
         * Get the 'SUBSCRIBER_STATUS' element value.
         * 
         * @return value
         */
        public String getSUBSCRIBERSTATUS() {
            return SUBSCRIBERSTATUS;
        }

        /** 
         * Set the 'SUBSCRIBER_STATUS' element value.
         * 
         * @param SUBSCRIBERSTATUS
         */
        public void setSUBSCRIBERSTATUS(String SUBSCRIBERSTATUS) {
            this.SUBSCRIBERSTATUS = SUBSCRIBERSTATUS;
        }
    }
}
