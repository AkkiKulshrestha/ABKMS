package com.ascend.www.abkms.webservices;

public class RestClient
	{
		private static API REST_CLIENT;

		public static String ROOT_URL = "http://agrixilla.in/abkms_app/";



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
