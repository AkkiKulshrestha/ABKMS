package com.ascend.www.abkms.model;

public class Member {

    private int SR_No;
    private String ID;
    private String NAME;
    private String MARITAL_STATUS;
    private String FATHER_NAME;
    private String SPOUSE_NAME;
    private String GENDER;
    private String AGE;
    private String DOB;
    private String FAMILY_MEMBER_COUNT;
    private String ADDRESS1;
    private String ADDRESS2;
    private String ADDRESS3;
    private String CITY;
    private String STATE;
    private String PINCODE;
    private String MOBILE_NO1;
    private String MOBILE_NO;
    private String WHATSAPP_NO;
    private  String EMAIL_ID;
    private String sub_inspec_date,registration_date;
    private String AFFLIATED_ABKMS;
    private String MOTHER_NAME;
    private String SPOUSE_OCCUPATION;
    private String SPOUSE_MOBILE;
    private String FAMILY_ORIGIN;
    private String OCCUPATION;
    private String WORKS_WITH;
    private String MONTHLY_FAMILY_INCOME;
    private String CONTRIBUTION_FOR_ABKMS;
    private String HAVE_CHILDREN;
    private String CHILDREN_INFO;

    public Member(int SR_No, String ID, String NAME,String MARITAL_STATUS, String FATHER_NAME, String SPOUSE_NAME, String GENDER,String DOB,
                           String AGE,String FAMILY_MEMBER_COUNT,String ADDRESS1,String ADDRESS2,String ADDRESS3, String CITY,
                           String STATE, String PINCODE, String MOBILE_NO, String MOBILE_NO1, String WHATSAPP_NO,String EMAIL_ID, String AFFLIATED_ABKMS,String MOTHER_NAME,
                           String SPOUSE_OCCUPATION,String WORKS_WITH,String SPOUSE_MOBILE,String FAMILY_ORIGIN, String OCCUPATION,String MONTHLY_FAMILY_INCOME,String CONTRIBUTION_FOR_ABKMS,
                           String HAVE_CHILDREN, String CHILDREN_INFO) {
        this.SR_No = SR_No;
        this.ID = ID;
        this.NAME = NAME;
        this.MARITAL_STATUS = MARITAL_STATUS;
        this.FATHER_NAME = FATHER_NAME;
        this.SPOUSE_NAME = SPOUSE_NAME;
        this.FAMILY_MEMBER_COUNT = FAMILY_MEMBER_COUNT;
        this.GENDER = GENDER;
        this.AGE = AGE;
        this.DOB = DOB;
        this.ADDRESS1 = ADDRESS1;
        this.ADDRESS2 = ADDRESS2;
        this.ADDRESS3 = ADDRESS3;
        this.CITY = CITY;
        this.STATE = STATE;
        this.PINCODE = PINCODE;
        this.MOBILE_NO = MOBILE_NO;
        this.MOBILE_NO1 = MOBILE_NO1;
        this.WHATSAPP_NO = WHATSAPP_NO;
        this.EMAIL_ID = EMAIL_ID;
        this.AFFLIATED_ABKMS = AFFLIATED_ABKMS;
        this.MOTHER_NAME = MOTHER_NAME ;
        this.SPOUSE_OCCUPATION = SPOUSE_OCCUPATION ;
        this.SPOUSE_MOBILE = SPOUSE_MOBILE ;
        this.FAMILY_ORIGIN = FAMILY_ORIGIN ;
        this.OCCUPATION = OCCUPATION ;
        this.WORKS_WITH = WORKS_WITH;
        this.MONTHLY_FAMILY_INCOME = MONTHLY_FAMILY_INCOME ;
        this.CONTRIBUTION_FOR_ABKMS = CONTRIBUTION_FOR_ABKMS ;
        this.HAVE_CHILDREN = HAVE_CHILDREN ;
        this.CHILDREN_INFO = CHILDREN_INFO ;

    }


    public  Member (){ }

    public int getSR_No() {
        return SR_No;
    }

    public void setSR_No(int SR_No) {
        this.SR_No = SR_No;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;

    }

    public String getMARITAL_STATUS() {
        return MARITAL_STATUS;
    }

    public void setMARITAL_STATUS(String MARITAL_STATUS) {
        this.MARITAL_STATUS = MARITAL_STATUS;
    }

    public String getFATHER_NAME() {
        return FATHER_NAME;
    }

    public void setFATHER_NAME(String FATHER_NAME) {
        this.FATHER_NAME = FATHER_NAME;
    }

    public String getSPOUSE_NAME() {
        return SPOUSE_NAME;
    }

    public void setSPOUSE_NAME(String SPOUSE_NAME) {
        this.SPOUSE_NAME = SPOUSE_NAME;
    }

    public String getGENDER() {
        return GENDER;
    }

    public void setGENDER(String GENDER) {
        this.GENDER = GENDER;
    }

    public String getAGE() {
        return AGE;
    }

    public void setAGE(String AGE) {
        this.AGE = AGE;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getFAMILY_MEMBER_COUNT() {
        return FAMILY_MEMBER_COUNT;
    }

    public void setFAMILY_MEMBER_COUNT(String FAMILY_MEMBER_COUNT) {
        this.FAMILY_MEMBER_COUNT = FAMILY_MEMBER_COUNT;
    }

    public String getADDRESS1() {
        return ADDRESS1;
    }

    public void setADDRESS1(String ADDRESS1) {
        this.ADDRESS1 = ADDRESS1;
    }

    public String getADDRESS2() {
        return ADDRESS2;
    }

    public void setADDRESS2(String ADDRESS2) {
        this.ADDRESS2 = ADDRESS2;
    }

    public String getADDRESS3() {
        return ADDRESS3;
    }

    public void setADDRESS3(String ADDRESS3) {
        this.ADDRESS3 = ADDRESS3;
    }

    public String getCITY() {
        return CITY;
    }

    public void setCITY(String CITY) {
        this.CITY = CITY;
    }

    public String getSTATE() {
        return STATE;
    }

    public void setSTATE(String STATE) {
        this.STATE = STATE;
    }

    public String getPINCODE() {
        return PINCODE;
    }

    public void setPINCODE(String PINCODE) {
        this.PINCODE = PINCODE;
    }

    public String getMOBILE_NO1() {
        return MOBILE_NO1;
    }

    public void setMOBILE_NO1(String MOBILE_NO1) {
        this.MOBILE_NO1 = MOBILE_NO1;
    }

    public String getMOBILE_NO() {
        return MOBILE_NO;
    }

    public void setMOBILE_NO(String MOBILE_NO) {
        this.MOBILE_NO = MOBILE_NO;
    }

    public String getWHATSAPP_NO() {
        return WHATSAPP_NO;
    }

    public void setWHATSAPP_NO(String WHATSAPP_NO) {
        this.WHATSAPP_NO = WHATSAPP_NO;
    }

    public String getEMAIL_ID() {
        return EMAIL_ID;
    }

    public void setEMAIL_ID(String EMAIL_ID) {
        this.EMAIL_ID = EMAIL_ID;
    }

    public String getSub_inspec_date() {
        return sub_inspec_date;
    }

    public void setSub_inspec_date(String sub_inspec_date) {
        this.sub_inspec_date = sub_inspec_date;
    }

    public String getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(String registration_date) {
        this.registration_date = registration_date;
    }

    public String getAFFLIATED_ABKMS() {
        return AFFLIATED_ABKMS;
    }

    public void setAFFLIATED_ABKMS(String AFFLIATED_ABKMS) {
        this.AFFLIATED_ABKMS = AFFLIATED_ABKMS;
    }

    public String getMOTHER_NAME() {
        return MOTHER_NAME;
    }

    public void setMOTHER_NAME(String MOTHER_NAME) {
        this.MOTHER_NAME = MOTHER_NAME;
    }

    public String getSPOUSE_OCCUPATION() {
        return SPOUSE_OCCUPATION;
    }

    public void setSPOUSE_OCCUPATION(String SPOUSE_OCCUPATION) {
        this.SPOUSE_OCCUPATION = SPOUSE_OCCUPATION;
    }

    public String getSPOUSE_MOBILE() {
        return SPOUSE_MOBILE;
    }

    public void setSPOUSE_MOBILE(String SPOUSE_MOBILE) {
        this.SPOUSE_MOBILE = SPOUSE_MOBILE;
    }

    public String getFAMILY_ORIGIN() {
        return FAMILY_ORIGIN;
    }

    public void setFAMILY_ORIGIN(String FAMILY_ORIGIN) {
        this.FAMILY_ORIGIN = FAMILY_ORIGIN;
    }

    public String getOCCUPATION() {
        return OCCUPATION;
    }

    public void setOCCUPATION(String OCCUPATION) {
        this.OCCUPATION = OCCUPATION;
    }

    public String getWORKS_WITH() {
        return WORKS_WITH;
    }

    public void setWORKS_WITH(String WORKS_WITH) {
        this.WORKS_WITH = WORKS_WITH;
    }

    public String getMONTHLY_FAMILY_INCOME() {
        return MONTHLY_FAMILY_INCOME;
    }

    public void setMONTHLY_FAMILY_INCOME(String MONTHLY_FAMILY_INCOME) {
        this.MONTHLY_FAMILY_INCOME = MONTHLY_FAMILY_INCOME;
    }

    public String getCONTRIBUTION_FOR_ABKMS() {
        return CONTRIBUTION_FOR_ABKMS;
    }

    public void setCONTRIBUTION_FOR_ABKMS(String CONTRIBUTION_FOR_ABKMS) {
        this.CONTRIBUTION_FOR_ABKMS = CONTRIBUTION_FOR_ABKMS;
    }

    public String getHAVE_CHILDREN() {
        return HAVE_CHILDREN;
    }

    public void setHAVE_CHILDREN(String HAVE_CHILDREN) {
        this.HAVE_CHILDREN = HAVE_CHILDREN;
    }

    public String getCHILDREN_INFO() {
        return CHILDREN_INFO;
    }

    public void setCHILDREN_INFO(String CHILDREN_INFO) {
        this.CHILDREN_INFO = CHILDREN_INFO;
    }
}
