package com.dell.lockedme.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

import com.dell.lockedme.model.Users;
import com.dell.lockedme.model.UserCreds;

public class driver {

	// input data
	private static Scanner keyboard;

	// model to store loggedin user data.
	private static Users loggedInUser;

	private static List<Users> userList;
	private static List<UserCreds> usersCredList;
	private static String dbFilePath = "database/users/";
	private static String dbFileName = "users.txt";
	private static String userCredsPath = "database/userCreds/";

	public static void main(String[] args) {
		initApp();
		signInOptions();
	}
	
	public static void initApp() {
		try {
			File dbFile = new File(dbFilePath + dbFileName);
			File dbPath = new File(dbFilePath);
			File credsPath = new File(userCredsPath);
			if(!dbPath.exists()) {
				dbPath.mkdirs();
			}
			if (!dbFile.exists()) {
				dbFile.createNewFile();
			}
			if(!credsPath.exists()) {
				credsPath.mkdirs();
			}
			loggedInUser = new Users();
			// userCredentials = new UserCreds();

			// if data available in file, then deserialize else empty object
			try {
				FileInputStream file = new FileInputStream(dbFile);
				if (file.available() != 0) {
					ObjectInputStream in = new ObjectInputStream(file);
					ArrayList<Users> readObject = (ArrayList<Users>) in.readObject();
					userList = readObject;
					in.close();
					file.close();
				} else {
					userList = new ArrayList<Users>();
				}
			} catch (IOException e) {
				if (e.getClass().toString().equalsIgnoreCase("class java.io.StreamCorruptedException")) {
					System.out.println("---------");
					System.out.println("DB File corrupted");
					System.out.println("Deleting all existing users. Please register again");
					PrintWriter writer = new PrintWriter(dbFile);
					writer.print("");
					writer.close();
					System.out.println("---------");
				} else {
					System.out.println(e.getMessage());
					System.out.println("---------");
					e.printStackTrace();
					System.out.println("---------");
				}
			}
			usersCredList = new ArrayList<UserCreds>();
			// read data from keyboard
			keyboard = new Scanner(System.in);

		} catch (IOException e) {
			System.out.println("404 : File Not Found ");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void welcomeScreen() {
		System.out.println("********************************************");
		System.out.println("**                                        **");
		System.out.println("**        Welcome To LockedMe.com         **");
		System.out.println("**     Your Personal Digital Locker       **");
		System.out.println("**                                        **");
		System.out.println("********************************************\n");

	}

	public static void signInOptions() {
		try {
			do {
				welcomeScreen();
				keyboard = new Scanner(System.in);
				System.out.println("1 -> Registration ");
				System.out.println("2 -> Login ");
				System.out.println("3 -> Exit ");
				int input = keyboard.nextInt();
				switch (input) {
				case 1:
					registerUser();
					//signInOptions();
					break;
				case 2:
					loginUser();
					break;
				case 3:
					System.out.println("++  Thank you  ++");
					keyboard.close();
					System.exit(0);
					break;
				default:
					System.out.println("Please select 1, 2 or 3");
					break;
				}
			} while (true);
		} catch (InputMismatchException ex) {
			System.out.println("[Error][Only 1, 2 or 3 input allowed]");
		}
		// input.close();
	}

	public static void registerUser() {
		Users user = new Users();
		boolean userExist = true, acceptedPass = false;
		System.out.println("\n\n******************************************");
		System.out.println("-----------  Register New User  ----------");
		System.out.println("******************************************\n");
		keyboard = new Scanner(System.in);
		System.out.println("Enter your full name:");
		String fullName = keyboard.nextLine();
		user.setFullName(fullName);
		do {
			System.out.println("Enter Username (can be alphanumeric):");
			String username = keyboard.next();
			user.setUsername(username.toLowerCase());
			// check if username already exist, then prompt for another username or prompt
			// for pass
			userExist = userList.stream().anyMatch(obj -> obj.getUsername().equalsIgnoreCase(username));
			if (!username.matches("[a-zA-Z0-9]+")) {
				System.out.println("-- Only alphabets or numbers allowed in username--\n");
			} else if (userExist) {
				System.out.println("-- Username taken. Please type another username--\n");
			}
	
		} while (userExist);
	
		// user.setUsername(username);
		// minimum 6 char alphanumeric
		do {
			System.out.println("\nEnter alphanumeric Password. Conditions as follows:");
			System.out.println("\t + Minimum 1 digit required");
			System.out.println("\t + Minimum 6 & Max 12 char length");
			System.out.println("\t + Only @!_- special char allowed");
			System.out.println();
			String password = keyboard.next();
			acceptedPass = password.matches("^(?=.*[0-9])([a-zA-Z0-9@!_-]){6,12}$");
			user.setPassword(password);
			if (!acceptedPass) {
				System.out.println("-- Incorrect password format! Please try again-- \n");
			}
		} while (!acceptedPass);
		if (!userExist && acceptedPass) {
			userList.add(user);
		}
		// save userList to DB file
		try {
			FileOutputStream file = new FileOutputStream(dbFilePath + dbFileName);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(userList);
			out.close();
			file.close();
			System.out.println("++ User Registration Suscessful! ++\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loginUser() {
		System.out.println("\n\n******************************************");
		System.out.println("----------------  Login Page  ------------");
		System.out.println("******************************************\n");
	
		// Users user = new Users();
		if (Objects.isNull(userList) || !userList.isEmpty()) {
	
			boolean found = false, acceptedPass = false;
			do {
				System.out.println("Enter Username :");
				String inpUsername = keyboard.next();
				Optional<Users> user = userList.stream().filter(obj -> obj.getUsername().equalsIgnoreCase(inpUsername))
						.findFirst();
				if (user.isPresent()) {
					found = true;
					do {
						System.out.println("Enter Password :");
						String inpPassword = keyboard.next();
						Users selUser = user.get();
						if (selUser.getPassword().equals(inpPassword.toString())) {
							acceptedPass = true;
							System.out.println("\n++ Login Successful! ++");
							loggedInUser = selUser;
							// Call lockerOption
							userCredsLockerOptions();
						} else {
							System.out.println("-- Incorrect password provided! Try again. --\n");
						}
					} while (!acceptedPass);
				} else {
					System.out.println("-- User Not Found! Try again. --\n");
				}
			} while (!found);
		}
		else {
			System.out.println("-- No registered users found. Register yourself first! --\n");
		}
	}

	public static void userCredsLockerOptions() {
		keyboard = new Scanner(System.in);
		// deserialise the file into obj
		try {
			File usercreds = new File(userCredsPath + loggedInUser.getUsername().toLowerCase() + ".txt");
			if (usercreds.exists()) {
				FileInputStream file = new FileInputStream(usercreds);
				if (file.available() != 0) {
					ObjectInputStream in = new ObjectInputStream(file);
					ArrayList<UserCreds> readObject = (ArrayList<UserCreds>) in.readObject();
					usersCredList = readObject;
					in.close();
					file.close();
				} else {
					usersCredList = new ArrayList<UserCreds>();
				}
			} else {
				usercreds.createNewFile();
			}
			boolean goBack = false;
			do {
				goBack = false;
				System.out.println("\n\n********************************************");
				System.out.println();
				System.out.println("        Welcome To LockedMe.com         ");
				System.out.println("     Your Personal Digital Locaker      ");
				System.out.println("                                        ");
				System.out.println("                 Logged In As: " + loggedInUser.getUsername());
				System.out.println("********************************************\n");
				System.out.println("1 -> Fetch all stored creds ");
				System.out.println("2 -> Insert new credentials ");
				System.out.println("3 -> Delete credentials ");
				System.out.println("4 -> Go Back to login page ");
				System.out.println("5 -> Exit ");
				int option = keyboard.nextInt();
				switch (option) {
				case 1:
					fetchCredentials();
					break;
				case 2:
					storeCredentials();
					break;
				case 3:
					deleteCredentials();
					break;
				case 4:
					goBack = true;
					break;
				case 5:
					System.out.println("++  Thank you  ++");
					keyboard.close();
					System.exit(0);
					break;
				default:
					System.out.println("Please select 1, 2, 3 or 4");
					break;
				}
			} while (!goBack);
			// lockerInput.close();
		} catch (IOException e) {
			if (e.getClass().toString().equalsIgnoreCase("class java.io.StreamCorruptedException")) {
				System.out.println("---------");
				System.out.println("User Creds File corrupted");
				System.out.println("Deleting all creds. Please add creds again");
				System.out.println("---------");
			} else {
				System.out.println(e.getMessage());
				System.out.println("---------");
				e.printStackTrace();
				System.out.println("---------");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InputMismatchException ex) {
			System.out.println("[Error][Only following  1, 2, 3 or 4 is allowed");
		}
	}

	// fetch credentials
	public static void fetchCredentials() {
		System.out.println("\n\n******************************************");
		System.out.println("---------  User Stored Credentials -------");
		System.out.println("                 Logged In As: " + loggedInUser.getUsername());
		System.out.println("******************************************\n");
		// System.out.println(inpUsername);
		// Find if a file exist with users name. If yes, check content. if 0 no creds to
		// show, else show everything
		if (usersCredList.isEmpty()) {
			System.out.println("-- No Credentials stored yet! --");
		}
		// traverse the list and show all the creds
		else {
			System.out.println("[SiteName] \t[UserName] \t[Password]");
			for (UserCreds uc : usersCredList) {
				System.out.println(
						"[" + uc.getSiteName() + "]\t\t[" + uc.getUsername() + "]\t\t[" + uc.getPassword() + "]");
			}
			// System.out.println(usersCredList.toString());
		}
	
	}

	// store credentails
	public static void storeCredentials() {
		UserCreds uc = new UserCreds();
		System.out.println("\n\n******************************************");
		System.out.println("---------  User Stored Credentials -------");
		System.out.println("                 Logged In As: " + loggedInUser.getUsername());
		System.out.println("******************************************\n");

		// userCredentials.setLoggedInUser(loggedInUser);

		System.out.println("Enter Site Name :");
		String siteName = keyboard.next();
		uc.setSiteName(siteName);

		System.out.println("Enter Site Username :");
		String username = keyboard.next();
		uc.setUsername(username);

		System.out.println("Enter Password :");
		String password = keyboard.next();
		uc.setPassword(password);

		usersCredList.add(uc);

		// Serialise back in file
		saveUsersCredListToFile();
		System.out.println("Stored your credentials securely!");
	}

	public static void deleteCredentials() {
		// UserCreds uc = new UserCreds();
		System.out.println("\n\n******************************************");
		System.out.println("----- Delete User Stored Credentials -----");
		System.out.println("                 Logged In As: " + loggedInUser.getUsername());
		System.out.println("******************************************\n");
		if (!usersCredList.isEmpty()) {

			System.out.println("Select the site to be deleted\n");
			System.out.println("Sl.No. \t [SiteName] \t [UserName] \t [Password]");
			// userCredentials.setLoggedInUser(loggedInUser);
			int count = 0;
			ArrayList<String> options = new ArrayList<String>();
			for (UserCreds uc : usersCredList) {
				++count;
				System.out.println(
						count + "\t[" + uc.getSiteName() + "]\t[" + uc.getUsername() + "]\t[" + uc.getPassword() + "]");
				options.add(Integer.toString(count));
			}
			boolean deleted = false;
			do {
				System.out.println("Type the site serial number to be deleted");
				String siteNum = keyboard.next();
				System.out.println(siteNum);
				if (options.stream().anyMatch(obj -> obj.equalsIgnoreCase(siteNum))) {
					usersCredList.remove(Integer.parseInt(siteNum) - 1);
					deleted = true;
				} else {
					System.out.println("Incorrect input provided");
				}
			} while (!deleted);
			saveUsersCredListToFile();
		} else {
			System.out.println("-- No Credentials found to be deleted! --");
		}
		// Serialise back in file

	}

	private static void saveUsersCredListToFile() {
		//To save loggedin users CredList in file
		try {
			FileOutputStream file = new FileOutputStream(userCredsPath + loggedInUser.getUsername().toLowerCase() + ".txt");
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(usersCredList);
			out.close();
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
