package com.ascend.www.abkms.model;

public class MatrimonyProfileListModel {


    private String ID;
    String profile_name;
    String father_name;
    String mother_name;
    String gender;
    String date_of_birth;
    String age;
    String gotra;
    String family_origin_place;
    String residing_city;
    String working_status;
    String working_as;
    String annunal_income;
    String contact_no;
    String whatsapp_no;
    String created_by;


    public MatrimonyProfileListModel() {
    }

    public MatrimonyProfileListModel(String ID, String profile_name, String father_name, String mother_name, String gender,
                                     String date_of_birth,String age, String gotra, String family_origin_place, String residing_city, String working_status,
                                     String working_as,String annunal_income, String contact_no, String whatsapp_no, String created_by) {
        this.ID = ID;
        this.profile_name = profile_name;
        this.father_name = father_name;
        this.mother_name = mother_name;
        this.gender = gender;
        this.date_of_birth = date_of_birth;
        this.age = age;
        this.gotra = gotra;
        this.family_origin_place = family_origin_place;
        this.residing_city = residing_city;
        this.working_status = working_status;
        this.working_as = working_as;
        this.annunal_income = annunal_income;
        this.contact_no = contact_no;
        this.whatsapp_no = whatsapp_no;
        this.created_by = created_by;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    public String getFather_name() {
        return father_name;
    }

    public void setFather_name(String father_name) {
        this.father_name = father_name;
    }

    public String getMother_name() {
        return mother_name;
    }

    public void setMother_name(String mother_name) {
        this.mother_name = mother_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGotra() {
        return gotra;
    }

    public void setGotra(String gotra) {
        this.gotra = gotra;
    }

    public String getFamily_origin_place() {
        return family_origin_place;
    }

    public void setFamily_origin_place(String family_origin_place) {
        this.family_origin_place = family_origin_place;
    }

    public String getResiding_city() {
        return residing_city;
    }

    public void setResiding_city(String residing_city) {
        this.residing_city = residing_city;
    }

    public String getWorking_status() {
        return working_status;
    }

    public void setWorking_status(String working_status) {
        this.working_status = working_status;
    }

    public String getWorking_as() {
        return working_as;
    }

    public void setWorking_as(String working_as) {
        this.working_as = working_as;
    }

    public String getAnnunal_income() {
        return annunal_income;
    }

    public void setAnnunal_income(String annunal_income) {
        this.annunal_income = annunal_income;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getWhatsapp_no() {
        return whatsapp_no;
    }

    public void setWhatsapp_no(String whatsapp_no) {
        this.whatsapp_no = whatsapp_no;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }


}
