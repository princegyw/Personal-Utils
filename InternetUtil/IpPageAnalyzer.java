package com.example.locationfromip;

//this class is for internal resolving IP Webpage
class IpPageAnalyzer extends WebpageAnalyzer {
		
		private String IpAddr = null;
		private String geoInfo = null;
		private String owner = null;

		public IpPageAnalyzer(String url) {
			super(url);
		}
		
		public void resolveIp()
		{
			String details = getElementsByClass("well").first().toString();
			IpAddr = details.substring(details.indexOf("<code>") + 6, details.indexOf("</code>"));	
		}
		
		public void resolveDetails()
		{
			String details = getElementsByClass("well").first().toString();	
			geoInfo = details.substring(details.indexOf("GeoIP") + 7, nthOccurrence(details, "</p>", 2));	
			owner = details.substring(nthOccurrence(details, "<p>", 3) + 3, nthOccurrence(details, "</p>", 3));		
		}
		
		public String get_geoInfo()
		{
			
			if (geoInfo == null) {
				resolveDetails();
			}
			return geoInfo;
		}
		
		public String get_owner()
		{
			if (owner == null) {
				resolveDetails();
			}
			return owner;
		}
		
		public String get_ipAddr()
		{
			resolveIp();
			return IpAddr;
		}

	}