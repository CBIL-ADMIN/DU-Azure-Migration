
package com.ericsson.jibx.beans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="COMMUNITY_MAPPING_LIST">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="COMMUNITY_MAPPING_INFO" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class COMMUNITYMAPPINGINFO -->
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class COMMUNITYMAPPINGLIST
{
    private List<COMMUNITYMAPPINGINFO> COMMUNITYMAPPINGINFOList = new ArrayList<COMMUNITYMAPPINGINFO>();

    /** 
     * Get the list of 'COMMUNITY_MAPPING_INFO' element items.
     * 
     * @return list
     */
    public List<COMMUNITYMAPPINGINFO> getCOMMUNITYMAPPINGINFOList() {
        return COMMUNITYMAPPINGINFOList;
    }

    /** 
     * Set the list of 'COMMUNITY_MAPPING_INFO' element items.
     * 
     * @param list
     */
    public void setCOMMUNITYMAPPINGINFOList(List<COMMUNITYMAPPINGINFO> list) {
        COMMUNITYMAPPINGINFOList = list;
    }
    /** 
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="COMMUNITY_MAPPING_INFO" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:all>
     *       &lt;xs:element type="xs:string" name="CUG_NAME"/>
     *       &lt;xs:element type="xs:string" name="Community_ID"/>
     *     &lt;/xs:all>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class COMMUNITYMAPPINGINFO
    {
        private String CUGNAME;
        private String communityID;

        /** 
         * Get the 'CUG_NAME' element value.
         * 
         * @return value
         */
        public String getCUGNAME() {
            return CUGNAME;
        }

        /** 
         * Set the 'CUG_NAME' element value.
         * 
         * @param CUGNAME
         */
        public void setCUGNAME(String CUGNAME) {
            this.CUGNAME = CUGNAME;
        }

        /** 
         * Get the 'Community_ID' element value.
         * 
         * @return value
         */
        public String getCommunityID() {
            return communityID;
        }

        /** 
         * Set the 'Community_ID' element value.
         * 
         * @param communityID
         */
        public void setCommunityID(String communityID) {
            this.communityID = communityID;
        }
    }
}
