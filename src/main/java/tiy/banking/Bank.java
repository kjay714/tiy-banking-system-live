package tiy.banking;

import jodd.json.JsonParser;
import jodd.json.JsonSerializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by localdom on 5/6/2016.
 */
public class Bank {
    private static int currentBankID = 120;

    private String bankID;
    public final static String BANKID_PREFIX = "TIYBANK";
    public static String testPrefix = null;
    // used to switch back and forth between the "live" bank prefix and the "test"
    // bank prefix
    private static String activePrefix = BANKID_PREFIX;
    private String bankName;
    private String bankStreetAddress;
    private HashMap<String, Customer> bankCustomers = new HashMap<String, Customer>();
    private static String fileName;

    public Bank() {
        System.out.println("Bank()");
        initBank();
    }

    public void initBank() {
        if (testPrefix == null) {
            activePrefix = BANKID_PREFIX;
        } else {
            activePrefix = testPrefix;
        }
        System.out.println("activePrefix = " + activePrefix);
        this.bankID = activePrefix + currentBankID;
        currentBankID++;
        fileName = bankID + ".json";
    }

    public Bank(String bankName, String bankAddress) {
        this.bankName = bankName;
        this.bankStreetAddress = bankAddress;
        initBank();;
    }

    public void save() {
        System.out.println("save()");

        try {
            JsonSerializer jsonSerializer = new JsonSerializer().deep(true);
            String jsonString = jsonSerializer.serialize(this);

            System.out.println("JSON = ");
            System.out.println(jsonString);

            File sampleFile = new File(fileName);
            FileWriter jsonWriter = new FileWriter(sampleFile);
            jsonWriter.write(jsonString);
            jsonWriter.close();
        } catch (Exception exception) {
            exception.printStackTrace();
            // TODO: add better exception handling here ...
        }
    }

    public void deleteFile() {
//        System.out.println("deleteFile()");
        try {
            System.out.println("delete " + bankID + ".json");
            File fileToDelete = new File(bankID + ".json");
            System.out.println("Absolute: " + fileToDelete.getAbsolutePath());
            boolean deleted = fileToDelete.delete();
            if (deleted) {
                System.out.println("\tsuccess");
            } else {
                System.out.println("\tunable to delete");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            // TODO better exception handling here
        }
    }

    public static ArrayList<Bank> retrieveAllBanks() {
        ArrayList<Bank> bankList = new ArrayList<Bank>();

        try {
            File currentFolder = new File(".");
            File[] allFiles = currentFolder.listFiles();
            for (File currentFile : allFiles) {
                if (currentFile.isFile()) {
                    String fileName = currentFile.getName();
                    if (fileName.endsWith(".json") && fileName.startsWith(activePrefix)) {
                        System.out.println("Retrieving bank from " + fileName);
                        String bankID = fileName.substring(0, fileName.lastIndexOf(".json"));
                        Bank currentBank = retrieve(bankID);
                        System.out.println("Retrieved bank with ID " + currentBank.getBankID());
                        bankList.add(currentBank);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            // TODO need better exception handling here ...
        }
        return bankList;
    }

    public static Bank retrieve(String bankID) {
        Scanner fileScanner = null;
        try {
            System.out.println("Retrieving bank with ID " + bankID);
            String fileToRetrieve = bankID + ".json";
            fileScanner = new Scanner(new File(fileToRetrieve));
            fileScanner.useDelimiter("\\Z"); // read the input until the "end of the input" delimiter
            String fileContents = fileScanner.next();
            JsonParser bankParser = new JsonParser();

            Bank newBank = bankParser.parse(fileContents, Bank.class);
            System.out.println("ID of the bank after it was restored: " + newBank.getBankID());
            return newBank;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            // TODO Implement better exception handling here
            return null;
        } finally {
            if (fileScanner != null) {
                fileScanner.close();
            }
        }
    }

    public void addCustomer(Customer customer) {
        System.out.println("addCustomer()");
        bankCustomers.put(customer.getEmailAddress(), customer);
    }

    public void addBankAccountForCustomer(BankAccount account, Customer customer) {
        System.out.println("addBankAccountForCustomer()");
        customer.addBankAccount(account);
    }

    public double getTotalMoneyAtBank() {
        double totalBalance = 0.0;
        for (Customer customer : bankCustomers.values()) {
            totalBalance += customer.getTotalAccountBalance();
        }

        return totalBalance;
    }

    public static int nextBankID() {
        return currentBankID++;
    }

    public HashMap<String, Customer> getBankCustomers() {
        return bankCustomers;
    }

    public void setBankCustomers(HashMap<String, Customer> bankCustomers) {
        this.bankCustomers = bankCustomers;
    }

    public static int getCurrentBankID() {
        return currentBankID;
    }

    public static void setCurrentBankID(int currentBankID) {
        Bank.currentBankID = currentBankID;
    }

    public String getBankID() {
        return bankID;
    }

    public void setBankID(String bankID) {
        this.bankID = bankID;
        // make sure we reset the file name we need to save to whenever
        // we reset the bankID, since the file name is derived from the bankID
        fileName = bankID + ".json";
    }

    public static String getBankidPrefix() {
        return activePrefix;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankStreetAddress() {
        return bankStreetAddress;
    }

    public void setBankStreetAddress(String bankStreetAddress) {
        this.bankStreetAddress = bankStreetAddress;
    }
}
