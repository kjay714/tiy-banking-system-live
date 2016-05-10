package tiy.banking;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by localdom on 5/6/2016.
 */
public class BankTest {

    int originalBankID = Bank.getCurrentBankID();

    @Before
    public void setUp() throws Exception {
        Bank.testPrefix = "TESTBANK";
    }

    @After
    public void tearDown() throws Exception {
        ArrayList<Bank> allTestBanks = Bank.retrieveAllBanks();
        System.out.println("Retrieved " + allTestBanks.size() + " banks");
        for (Bank bank : allTestBanks) {
            bank.deleteFile();
        }
        Bank.testPrefix = null;
        Bank.setCurrentBankID(originalBankID);
    }

    @Test
    public void testDeleteFile() throws Exception {
        Bank testBank = new Bank();

        testBank.save();
        testBank.deleteFile();

        String fileName = testBank.getBankID() + ".json";
        try {
            File deletedFile = new File(fileName);
            boolean deleted = deletedFile.delete();
            if (deleted) {
                throw new Exception("Shouldn't have been able to delete a file that was already delete - Bank.deleteFile() isn't working properly");
            } else {
                System.out.println("Test bank file deleted successfully");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    public void testCreateBank() throws Exception {
        Bank testBank = new Bank();
        Bank secondBank = new Bank();

        assertNotEquals(testBank.getBankID(), secondBank.getBankID());
    }

    @Test
    public void testAddCustomer() throws Exception {
        Bank testBank = new Bank();
        Customer testCustomer = CustomerTest.createCustomerWithTwoBankAccounts("Test", "Tester", "test@tsl.com");

        testBank.addCustomer(testCustomer);

        // if the customer was added successfully, then the total money at the bank
        // should equal the balance for this customer
        // Note: I can use testCustomer.getTotalAccountBalance() as the expected
        // value because I have a unit test (in BankAccountTest) that shows that that method
        // works properly
        assertEquals(testCustomer.getTotalAccountBalance(), testBank.getTotalMoneyAtBank(), 0.0);
    }

    @Test
    public void testAddTwoCustomers() throws Exception {
        Bank testBank = new Bank();
        Customer firstCustomer = CustomerTest.createCustomerWithTwoBankAccounts("Test", "Tester", "test@tsl.com");
        Customer secondCustomer = CustomerTest.createCustomerWithTwoBankAccounts("Test2", "Tester2", "test2@tsl.com");
        testBank.addCustomer(firstCustomer);
        testBank.addCustomer(secondCustomer);

        double expectedBalance = firstCustomer.getTotalAccountBalance() + secondCustomer.getTotalAccountBalance();
        assertEquals(expectedBalance, testBank.getTotalMoneyAtBank(), 0.0);
    }

    @Test
    public void testSaveAndRetrieveBasicBank() throws Exception {
        Bank myBank = new Bank("Test Bank", "123 Spring St, Atlanta GA 30054");
        // do things to the bank ...
        myBank.save();
        String bankID = myBank.getBankID();

        Bank retrievedBank = Bank.retrieve(bankID);

        assertEquals(myBank.getBankID(), retrievedBank.getBankID());
        assertEquals(myBank.getBankName(), retrievedBank.getBankName());
    }

    @Test
    public void testSaveAndRetrieveTwoBanks() throws Exception {
        Bank myBank = new Bank("Test Bank", "123 Spring St, Atlanta GA 30054");
        // do things to the bank ...
        String myBankID = myBank.getBankID();
        myBank.save();
        Bank mySecondBank = new Bank("Test Bank 2", "233 Spring St, Atlanta GA 30054");
        String secondBankID = mySecondBank.getBankID();
        mySecondBank.save();

        assertNotEquals(myBankID, secondBankID);

        Bank retrievedBank1 = Bank.retrieve(myBankID);
        Bank retrievedBank2 = Bank.retrieve(secondBankID);

        assertNotEquals(retrievedBank1.getBankID(), retrievedBank2.getBankID()); 

        assertEquals(myBank.getBankID(), retrievedBank1.getBankID());
        assertEquals(myBank.getBankName(), retrievedBank1.getBankName());
        assertEquals(mySecondBank.getBankID(), retrievedBank2.getBankID());
        assertEquals(mySecondBank.getBankName(), retrievedBank2.getBankName());

        // also test that we can retrieve all banks here
        ArrayList<Bank> allBanks = Bank.retrieveAllBanks();
        assertNotNull(allBanks);
        // I should have at least the two banks I've been testing with
        // saved to file, so retrieveAllBanks should return at least 2 banks
        int numBanks = allBanks.size();
        // TODO find out how to compare less than or greater than
        assertNotEquals(0, numBanks);
        assertNotEquals(1, numBanks);
    }

    @Test
    public void testSaveAndRetrieveFullBank() throws Exception {
        Bank myBank = new Bank("Test Bank", "123 Spring St, Atlanta GA 30054");
        String customerID = "test@tsl.com";
        Customer testCustomer = CustomerTest.createCustomerWithTwoBankAccounts("Test", "Tester", customerID);
        myBank.addCustomer(testCustomer);

        myBank.save();

        Bank retrievedBank = Bank.retrieve(myBank.getBankID());

        assertEquals(myBank.getBankID(), retrievedBank.getBankID());
        assertEquals(myBank.getBankName(), retrievedBank.getBankName());
        assertEquals(myBank.getTotalMoneyAtBank(), retrievedBank.getTotalMoneyAtBank(), 0.0);
        assertEquals(myBank.getBankCustomers().size(), retrievedBank.getBankCustomers().size());
        for (Customer myBankCustomer : myBank.getBankCustomers().values()) {
            Customer retrievedCustomer = retrievedBank.getBankCustomers().get(myBankCustomer.getEmailAddress());
            assertEquals(myBankCustomer.getEmailAddress(), retrievedCustomer.getEmailAddress());
            assertEquals(myBankCustomer.getTotalAccountBalance(), retrievedCustomer.getTotalAccountBalance(), 0.0);
        }
    }

    @Test
    public void testAddBankAccountForCustomer() throws Exception {
        Bank testBank = new Bank();
        String customerID = "test@tsl.com";
        Customer testCustomer = CustomerTest.createCustomerWithTwoBankAccounts("Test", "Tester", customerID);
        testBank.addCustomer(testCustomer);

        double existingCustomerBalance = testCustomer.getTotalAccountBalance();
        double existingBankBalance = testBank.getTotalMoneyAtBank();
        // a bit redundant since we're testing the same thing elsewhere,
        // but easy to test here and validates our baseline from within this test
        assertEquals(existingBankBalance, existingCustomerBalance, 0.0);

        double newDeposit = 500.00;
        BankAccount testBankAccount = new BankAccount("Customer Account 2", newDeposit);
        testBank.addBankAccountForCustomer(testBankAccount, testCustomer);

        // if the bank account was added properly, then the customer's new total balance
        // should be what it was plus the amount of this new account
        // and same for the total balance at the bank
        assertEquals(existingCustomerBalance + newDeposit, testCustomer.getTotalAccountBalance(), 0.0);
        assertEquals(existingBankBalance + newDeposit, testBank.getTotalMoneyAtBank(), 0.0);
    }

    @Test
    public void testGetTotalMoneyAtBank() throws Exception {

    }

    @Test
    public void testNextBankID() throws Exception {

    }
}