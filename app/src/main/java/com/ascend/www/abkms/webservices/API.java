package com.ascend.www.abkms.webservices;


public interface API {



	/*@Headers({ "Accept: application/json"})
	@POST("/summary/ezzyPlannerRequest")
	void EzzyPlannerRequest(@Body CreatePdfReport data, Callback<CreatePdfReport> callback);
*/

	/*@Headers({ "Accept: application/json" })
	@POST("/summary/addLauchInviteRequest")
	void AddLauchInviteRequest(@Body CreatePdfReport data, Callback<CreatePdfReport> callback);

*/
	/*@Headers({ "Accept: application/json"})
    @POST("/user/addUserTermsConditionId")
	void UpdateUserTermsCondition(@Body CreateUserSent data, Callback<CreateUserResponse> callback);
*/
   /* //@Headers({ "Accept: application/json" })
    @POST("/user/updateCurrentStepOn")
	void UpdateCurrentStepOn(@Body CreateProfileDataSent data, Callback<CreateProfileDataSent> callback);

	/*@Headers({ "Accept: application/json" })
	@POST("/user/resendVerificationEmail")
	void SendVerificationEmail(@Body CreateProfileDataSent data, Callback<CreateProfileDataSent> callback);

	@Headers({ "Accept: application/json" })
	@POST("/user/forgotPassword")
	void ForgotPassword(@Body CreateUserSent data, Callback<CreateUserSent> callback);


	@Headers({ "Accept: application/json" })
	@POST("/user/resetPassword")
	void ResetPassword(@Body CreateUserSent data, Callback<CreateUserResponse> callback);


	@Headers({ "Accept: application/json" })
	@POST("/user/insertNewDevice")
	void InsertNewDevice(@Body CreateUserSent data, Callback<CreateUserResponse> callback);

	@Headers({ "Accept: application/json" })
	@POST("/user/createUser")
	void CreateUser(@Body CreateUserSent data, Callback<CreateUserResponse> callback);


    @Headers({ "Accept: application/json" })
    @POST("/summary/createProfile")
    void CreateProfile(@Body CreateProfileDataSent data, Callback<CreateProfileDataSent> callback);



	@Headers({ "Accept: application/json" })
	@POST("/user/addLogLogoutActivity")
	void LogoutUser(@Body CreateUserSent data, Callback<CreateUserResponse> callback);



	@Headers({"Accept: application/json"})
	@POST("/summary/createScap")
	void CreateScap(@Body CreateScapDataSent data, Callback<CreateScapDataSent> callback);

	@Headers({ "Accept: application/json" })
	@POST("/summary/sendReport")
	void CreatePdfReport(@Body CreatePdfReport data, Callback<CreatePdfReport> callback);

	@Headers({ "Accept: application/json" })
	@POST("/user/checkUserMobileVerification")
	void CheckMobileRegistration(@Body CreatePdfReport data, Callback<CreatePdfReport> callback);

	@Headers({ "Accept: application/json" })
	@POST("/user/updateUserMobile")
	void UpdateUserMobile(@Body CreatePdfReport data, Callback<CreatePdfReport> callback);

	@Headers({ "Accept: application/json" })
	@POST("/user/resendMobileVerificationOtp")
	void ResendOTP(@Body CreatePdfReport data, Callback<CreatePdfReport> callback);

	@Headers({ "Accept: application/json" })
	@POST("/user/verifyUserMobile")
	void VerifyMobile_CheckOTP(@Body CreatePdfReport data, Callback<CreatePdfReport> callback);
*/



	/*Post Risk Questionnary*/

/*
	@Headers({ "Accept: application/json" })
	@POST("/invoice/createInvoice")
	void CreateInvoice(@Body CreateInvoice data, Callback<CreateInvoice> callback);


	@Headers({ "Accept: application/json" })
	@POST("/paymentgateway/payU")
	void PayU(@Body PauU data, Callback<PauU> callback);

	@Headers({ "Accept: application/json" })
	@POST("/paymentgateway/updatePaymentStatus")
	void UpdatePaymentStatus(@Body UpdatePaymentStatus data, Callback<UpdatePaymentStatus> callback);

*/

	/*@Headers({ "Accept: application/json" })
	@POST("/kycdocument/createKycDocument")
	void CreateKycDocument(@Body CreateKycDocument data, Callback<CreateKycDocument> callback);
*/
	/*Post Risk Questionnary*/
/*
	@Headers({ "Accept: application/json" })
	@POST("/risktolerance/addUserRiskTolerance")
	void AddUserRisktolerance(@Body CreateRiskToleranceData data, Callback<CreateRiskToleranceData> callback);
*/

	/*Post CheckList*//*
	@Headers({ "Accept: application/json" })
	@POST("/checklist/addUserCheckList")
	void AddUserCheckList(@Body CreateRiskToleranceData data, Callback<CreateRiskToleranceData> callback);
*/

	/*@Headers({ "Accept: application/json" })
	@POST("/planner/SavePlannerAudit")
	void SavePlannerAudit(@Body CreatePlannerAuditData data, Callback<CreatePlannerAuditData> callback);

*/
    /*@Headers({ "Accept: application/json" })
	@POST("/summary/addLauchInviteRequest")
	void SendInvitation(@Body CreatePdfReport data, Callback<CreatePdfReport> callback);*/

	/*XE - SC */

	/*@Headers({ "Accept: application/json" })
	@POST("/xe/createScBank")
	void CreateSCBank(@Body CreateSCBankData data, Callback<CreateSCBankData> callback);
*/

/*
	@Headers({ "Accept: application/json" })
	@POST("/xe/createScFixedDeposit")
	void CreateSCFixedDeposit(@Body CreateSCBankData data, Callback<CreateSCBankData> callback);


	@Headers({ "Accept: application/json" })
	@POST("/xe/createScRecurringDeposit")
	void CreateSCRecurringDeposit(@Body CreateSCBankData data, Callback<CreateSCBankData> callback);
*/
/*

	@Headers({ "Accept: application/json" })
	@POST("/xe/deleteScBank")
	void DeleteSCBank(@Body CreateSCBankData data, Callback<CreateSCBankData> callback);


	@Headers({ "Accept: application/json" })
	@POST("/xe/createScMutualFund")
	void CreateSCMutualFund(@Body CreateSCMutualFund data, Callback<CreateSCMutualFund> callback);



	@Headers({ "Accept: application/json" })
	@POST("/xe/deleteScMutualFund")
	void DeleteSCMutualFund(@Body CreateSCMutualFund data, Callback<CreateSCMutualFund> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/createScOthers")
	void CreateScOthers(@Body CreateScOthers data, Callback<CreateScOthers> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/deleteScOthers")
	void DeleteScOthers(@Body CreateScOthers data, Callback<CreateScOthers> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/createScSmallSavings")
	void CreateScSmallSavings(@Body CreateScSmallSavings data, Callback<CreateScSmallSavings> callback);

    @Headers({ "Accept: application/json" })
	@POST("/xe/deleteScSmallSavings")
	void DeleteSCSmallSavings(@Body CreateScSmallSavings data, Callback<CreateScSmallSavings> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/createScSalaryIncome")
	void CreateScSalaryIncome(@Body CreateScSalaryIncome data, Callback<CreateScSalaryIncome> callback);


	@Headers({ "Accept: application/json" })
	@POST("/xe/deleteScSalaryIncome")
	void DeleteScSalaryIncome(@Body CreateScSalaryIncome data, Callback<CreateScSalaryIncome> callback);

    @Headers({ "Accept: application/json" })
	@POST("/xe/createScOtherIncome")
	void CreateScOtherIncome(@Body CreateScOtherIncome data, Callback<CreateScOtherIncome> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/deleteScOtherIncome")
	void DeleteScOtherIncome(@Body CreateScOtherIncome data, Callback<CreateScOtherIncome> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/createScInheritance")
	void CreateSCInheritance(@Body CreateSCInheritanceData data, Callback<CreateSCInheritanceData> callback);

    @Headers({ "Accept: application/json" })
	@POST("/xe/deleteScInheritance")
	void DeleteSCInheritance(@Body CreateSCInheritanceData data, Callback<CreateSCInheritanceData> callback);

    @Headers({ "Accept: application/json" })
	@POST("/xe/createScRealEstate")
	void CreateScRealEstate(@Body CreateScRealEstate data, Callback<CreateScRealEstate> callback);

    @Headers({ "Accept: application/json" })
	@POST("/xe/deleteScRealEstate")
	void DeleteScRealEstate(@Body CreateScRealEstate data, Callback<CreateScRealEstate> callback);

    @Headers({ "Accept: application/json" })
	@POST("/xe/createScapEssentials")
	void CreateSCAPEssential(@Body CreateScapEssential data, Callback<CreateScapEssential> callback);


	@Headers({ "Accept: application/json" })
	@POST("/xe/deleteScapEssentials")
	void DeleteSCAPEssential(@Body CreateScapEssential data, Callback<CreateScapEssential> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/createScapLegacy")
	void CreateScapLegacy(@Body CreateXESCAPLegacy data, Callback<CreateXESCAPLegacy> callback);

    @Headers({ "Accept: application/json" })
	@POST("/xe/deleteScapLegacy")
	void DeleteScapLegacy(@Body CreateXESCAPLegacy data, Callback<CreateXESCAPLegacy> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/createScapCharity")
	void CreateScapCharity(@Body CreateXESCAPCharity data, Callback<CreateXESCAPCharity> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/deleteScapCharity")
	void DeleteScapCharity(@Body CreateXESCAPCharity data, Callback<CreateXESCAPCharity> callback);

    @Headers({ "Accept: application/json" })
	@POST("/xe/createScapLifeStyle")
	void CreateScapLifeStyle(@Body CreateXESCAPLifeStyle data, Callback<CreateXESCAPLifeStyle> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/deleteScapLifestyle")
	void DeleteScapLifeStyle(@Body CreateXESCAPLifeStyle data, Callback<CreateXESCAPLifeStyle> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/createScapResponsibility")
	void CreateScapResponsibility(@Body CreateXESCAPResponsibility data, Callback<CreateXESCAPResponsibility> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/deleteScapResponsibility")
	void DeleteScapResponsibility(@Body CreateXESCAPLifeStyle data, Callback<CreateXESCAPLifeStyle> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/createScapInterest")
	void CreateScapInterest(@Body CreateXESCAPInterest data, Callback<CreateXESCAPInterest> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/deleteScapInterest")
	void DeleteScapInterest(@Body CreateXESCAPInterest data, Callback<CreateXESCAPInterest> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/createScapRealEstate")
	void CreateScapRealEstate(@Body CreateXESCAPRealEstate data, Callback<CreateXESCAPRealEstate> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/deleteScapRealEstate")
	void DeleteScapRealEstate(@Body CreateXESCAPRealEstate data, Callback<CreateXESCAPRealEstate> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/createScapProtectionOnly")
	void CreateScapProtectionOnly(@Body CreateXESCAPProtectionOnly data, Callback<CreateXESCAPProtectionOnly> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/createScapProtectionInvestment")
	void CreateScapProtectionInvestment(@Body CreateXESCAPProtectionOnly data, Callback<CreateXESCAPProtectionOnly> callback);

    @Headers({ "Accept: application/json" })
	@POST("/xe/deleteScapProtection")
	void DeleteScapProtection(@Body CreateXESCAPProtectionOnly data, Callback<CreateXESCAPProtectionOnly> callback);

	@Headers({ "Accept: application/json" })
	@POST("/xe/createScapTaxExemptions")
	void CreateScapTaxExemptions(@Body CreateScapTaxExemptions data, Callback<CreateScapTaxExemptions> callback);

        // Spottr

    @Headers({ "Accept: application/json" })
	@POST("/spotter/addSpotterRetirementAge")
	void UpdateRetirementAge(@Body CreateSpottrPrelongRetirement data, Callback<CreateSpottrPrelongRetirement> callback);

    @Headers({ "Accept: application/json" })
	@POST("/spotter/runCashflowSummary")
	void RunCashflowSummary(@Body CreateCashFlowSummary data, Callback<CreateCashFlowSummary> callback);

	@Headers({ "Accept: application/json" })
	@POST("/spotter/runSpottrSummary")
	void RunSpottrSummary(@Body CreateCashFlowSummary data, Callback<CreateCashFlowSummary> callback);



	//Optimizer

    @Headers({ "Accept: application/json" })
    @POST("/optimizer/createOptimizerRealEstate")
    void CreateOptimizerRealEstate(@Body CreateOptimizerRealEstate data, Callback<CreateOptimizerRealEstate> callback);

	@Headers({ "Accept: application/json" })
	@POST("/optimizer/createOptimizerVehicle")
	void CreateOptimizerVehicle(@Body CreateOptimizerVehicle data, Callback<CreateOptimizerVehicle> callback);

    @Headers({ "Accept: application/json" })
	@POST("/optimizer/createOptimizerJewellery")
	void CreateOptimizerJewellery(@Body CreateOptimizerJewellery data, Callback<CreateOptimizerJewellery> callback);

	@Headers({ "Accept: application/json" })
	@POST("/optimizer/createOptimizerPrepayLoan")
	void CreateOptimizerPrepayLoan(@Body CreateOptimizerPrepayLoan data, Callback<CreateOptimizerPrepayLoan> callback);

	@Headers({ "Accept: application/json" })
	@POST("/spotter/createAssetOptimizer")
	void CreateAssetOptimizer(@Body CreateAssetOptimizer data, Callback<CreateAssetOptimizer> callback);

    @Headers({ "Accept: application/json" })
	@POST("/jotz/updateAcceptedJotzPdf")
	void UpdateAcceptedJotzPdf(@Body CreateJotzPdf data, Callback<CreateJotzPdf> callback);

	@Headers({ "Accept: application/json" })
	@POST("/spotter/createBreakSchedule")
	void CreateBreakSchedule(@Body CreateBreakSchedule data, Callback<CreateBreakSchedule> callback);

	@Headers({ "Accept: application/json" })
	@POST("/jotzpdf/sendReport")
	void CreateJotzPdf(@Body CreateJotzPdf data, Callback<CreateJotzPdf> callback);



*/

	/*@Headers({ "Accept: application/json" })
	@POST("/summary/deleteProfile")
	void DeleteProfile(@Body CreateProfileDataSent data, Callback<CreateProfileDataSent> callback);

*/



	/*XE---->SCAP*/



	//*Spottr*//*



/*
	@Headers({ "Accept: application/json" })
	@POST("/optimizer/createOptimizerRealEstateSCAPPrepayLoan")
	void CreateOptimizerRealEstateSCAPPrepayLoan(@Body CreateOptimizerPrepayLoan data, Callback<CreateOptimizerPrepayLoan> callback);
*/





	/*Jotz*/

/*
	*/
/*Question No-2*//*

	@Headers({ "Accept: application/json" })
	@POST("/jotz/createOtherIncomeRentalIncome")
	void CreateOtherIncomeRentalIncome(@Body CreateOtherIncomeQuestionTwo data, Callback<CreateOtherIncomeQuestionTwo> callback);

	@Headers({ "Accept: application/json" })
	@POST("/jotz/createOtherIncomeFamilyIncome")
	void CreateOtherIncomeFamilyIncome(@Body CreateOtherIncomeQuestionTwo data, Callback<CreateOtherIncomeQuestionTwo> callback);

	@Headers({ "Accept: application/json" })
	@POST("/jotz/createOtherIncomeOtherIncome")
	void CreateOtherIncomeOtherIncome(@Body CreateOtherIncomeQuestionTwo data, Callback<CreateOtherIncomeQuestionTwo> callback);

	@Headers({ "Accept: application/json" })
	@POST("/jotz/createOtherIncomeMiscellaneousIncome")
	void CreateOtherIncomeMiscellaneousIncome(@Body CreateOtherIncomeQuestionTwo data, Callback<CreateOtherIncomeQuestionTwo> callback);

    /*Question No-3*/
  /*

  	@Headers({ "Accept: application/json" })
	@POST("/jotz/createRealEstateJotz")
	void CreateRealEstateJotz(@Body CreateQuestionThreeRealEstate data, Callback<CreateQuestionThreeRealEstate> callback);

    @Headers({ "Accept: application/json" })
	@POST("/jotz/createInheritanceRealEstateJotz")
	void CreateInheritanceRealEstateJotz(@Body CreateQuestionThreeInheritance data, Callback<CreateQuestionThreeInheritance> callback);

	@Headers({ "Accept: application/json" })
	@POST("/jotz/createInheritanceCarJotz")
	void CreateInheritanceCarJotz(@Body CreateQuestionThreeInheritance data, Callback<CreateQuestionThreeInheritance> callback);

	@Headers({ "Accept: application/json" })
	@POST("/jotz/createInheritanceJewelleryJotz")
	void CreateInheritanceJewelleryJotz(@Body CreateQuestionThreeInheritance data, Callback<CreateQuestionThreeInheritance> callback);

	@Headers({ "Accept: application/json" })
	@POST("/jotz/createInheritanceOtherAssetsJotz")
	void CreateInheritanceOtherAssetsJotz(@Body CreateQuestionThreeInheritance data, Callback<CreateQuestionThreeInheritance> callback);



*/






}
