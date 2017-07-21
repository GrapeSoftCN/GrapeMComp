package test;

import httpServer.booter;

public class testmcomp {
	public static void main(String[] args) {
//		String info = "{\"companyName\":\"test1\"}";
//		System.out.println(new MCompany().AddComp("test111", info));
		
		booter booter = new booter();
		System.out.println("GrapeM!");
		try {
			System.setProperty("AppName", "GrapeM");
			booter.start(6002);
		} catch (Exception e) {

		}
	}
}
