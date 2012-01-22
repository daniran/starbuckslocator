package com.dannyran.test.tools;

import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;

import com.dannyran.test.server.StoreLocationHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

public class RemoteApiHelper {
	public static void main(String[] args) throws IOException {
		String username = System.console().readLine("username: ");
		String password = new String(System.console().readPassword("password: "));
		RemoteApiOptions options = new RemoteApiOptions().server("starbuckslocator.appspot.com", 443).credentials(username, password)
				.remoteApiPath("/remote_api");
		RemoteApiInstaller installer = new RemoteApiInstaller();
		installer.install(options);
		try {
			readCSV();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			installer.uninstall();
		}
	}

	private static void readCSV() throws Exception {
		CSVReader reader = null;
		reader = new CSVReader(new FileReader("/Users/daniran/Documents/workspace-indigo/farmigo/USA-Starbucks.csv"));
		String[] nextLine;
		int counter = 0;
		while ((nextLine = reader.readNext()) != null) {
			counter++;
			System.out.println(counter + ": Creating Entity");
			Entity entity = StoreLocationHelper.createLocationEntity(nextLine[3], nextLine[2], Double.parseDouble(nextLine[0]),
					Double.parseDouble(nextLine[1]));
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			Thread.sleep(1000);
			System.out.println("Key of new entity is " + ds.put(entity));
		}

	}
}