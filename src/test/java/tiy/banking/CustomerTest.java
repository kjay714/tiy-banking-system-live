package tiy.banking;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by localdom on 5/6/2016.
 */
public class CustomerTest {

    public static final double AMOUNT_FOR_FIRST_ACCOUNT = 100.00;
    public static final double AMOUNT_FOR_SECOND_ACCOUNT = 300.00;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    // make sure the Customer() constructor calls its base class constructor
    @Test
    public void testDefaultConstructor() throws Exception {
        Customer testCustomer = new Customer();
        assertEquals(Customer.DEFAULT_FIRST_NAME, testCustomer.getFirstName());
        assertEquals(Customer.DEFAULT_LAST_NAME, testCustomer.getLastName());
        assertEquals(Customer.DEFAULT_EMAIL_ADDRESS, testCustomer.getEmailAddress());
    }

    @Test
    public void testCustomerConstructor() throws Exception {
        Customer testCustomer = new Customer("Test", "Tester", "tester@tsl.com");
        assertEquals("Test", testCustomer.getFirstName());
        assertEquals("Tester", testCustomer.getLastName());
        assertEquals("tester@tsl.com", testCustomer.getEmailAddress());
        Customer secCustomer = new Customer("JJ", "Fish", "jjfish@tsl.com");
        assertEquals("JJ", secCustomer.getFirstName());
        assertEquals("Fish", secCustomer.getLastName());
        assertEquals("jjfish@tsl.com", secCustomer.getEmailAddress());
    }

    @Test
    public void testAddBankAccount() throws Exception {
        Customer testCustomer = new Customer("Test", "Tester", "tester@tsl.com");
        String accountID = "TestAccount 1";
        double depositAmount = 100.00;
        // I know I can create a bank account with no issues because of the
        // BankAccountTest unit test class
        BankAccount testBankAccount = new BankAccount(accountID, depositAmount);

        testCustomer.addBankAccount(testBankAccount);

        // validate the customer now has exactly one bank account
        assertEquals(1, testCustomer.getBankAccounts().size());

        // validate that we can retrieve the bank account by name from the customer
        BankAccount retrievedAccount = testCustomer.getBankAccountByID(accountID);
        assertEquals(accountID, retrievedAccount.getAccountID());
        assertEquals(depositAmount, retrievedAccount.getAccountBalance(), 0.0);

        // validate that my total balance for the customer is correct after
        // adding just a single account
        double totalCustomerMoney = testCustomer.getTotalAccountBalance();
        assertEquals(depositAmount, totalCustomerMoney, 0.0);
    }

    @Test
    public void testTotalAccountBalance() throws Exception {
        Customer testCustomer = createCustomerWithTwoBankAccounts("Test", "Tester", "tester@tsl.com");

        double expectedTotal = AMOUNT_FOR_FIRST_ACCOUNT + AMOUNT_FOR_SECOND_ACCOUNT;

        assertEquals(expectedTotal, testCustomer.getTotalAccountBalance(), 0.0);
    }

    /**
     * Utility method that returns a Customer with two bank accounts
     * Note: this uses AMOUNT_FOR_FIRST_ACCOUNT and AMOUNT_FOR_SECOND_ACCOUNT
     * as the amounts to deposit on the first and second account, respectively
     *
     * @return
     */
    public static Customer createCustomerWithTwoBankAccounts(String firstName, String lastName, String emailAddress) {
        Customer testCustomer = new Customer(firstName, lastName, emailAddress);
        BankAccount testAccount1 = new BankAccount("Test Account 1", AMOUNT_FOR_FIRST_ACCOUNT);
        BankAccount testAccount2 = new BankAccount("Test Account 2", AMOUNT_FOR_SECOND_ACCOUNT);

        testCustomer.addBankAccount(testAccount1);
        testCustomer.addBankAccount(testAccount2);


        return testCustomer;
    }

    @Test
    public void testGetBankAccountByID() throws Exception {

    }
}