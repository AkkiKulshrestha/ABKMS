package com.ascend.www.abkms.webservices;

public class RestClient
	{
		private static API REST_CLIENT;

		public static String ROOT_URL = "http://royalsparsh.in/kulshrestha_app_api/";

		public static String URLProd = "http://www.mymfnow.com/api";

		public static String URLDev = "http://13.126.98.215:8081";

		public static String GenerateQrCode = "https://pierre2106j-qrcode.p.rapidapi.com/api?backcolor=ffffff&pixel=10&ecl=L+%7C+M%7C+Q+%7C+H&forecolor=000000&type=json+%7C+url+%7C+tel+%7C+sms+%7C+email&";



		public static String Development = "http://103.87.174.12/fubiqmfapp/api/User/";
		/*static
			{
				setupRestClient();
			}*/


		private RestClient()
			{
			}

		public static API get()
			{
				return REST_CLIENT;
			}

		/*private static void setupRestClient()
			{
				RestAdapter.Builder builder = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(ROOT).setClient(new OkClient(new OkHttpClient()));
				RestAdapter restAdapter = builder.build();
				REST_CLIENT = restAdapter.create(API.class);
			}*/
	}
