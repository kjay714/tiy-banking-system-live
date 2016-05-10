package tiy.webapp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tiy.banking.Bank;
import tiy.banking.BankAccount;
import tiy.banking.Customer;

import javax.servlet.http.HttpSession;

/**
 * Created by localdom on 5/8/2016.
 */
@Controller
public class BankSystemController {

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model) {
        setCommonAttributes(session, model);
        model.addAttribute("bankList", Bank.retrieveAllBanks());

        return "home";
    }

    @RequestMapping(path = "/customerList", method = RequestMethod.GET)
    public String getCustomerList(HttpSession session, Model model, String bankID) {
        setCommonAttributes(session, model);
        Bank bank = Bank.retrieve(bankID);
        model.addAttribute("bank", bank);
        System.out.println("There are " + bank.getBankCustomers().size() + " customers in the current bank");
        model.addAttribute("customerList", bank.getBankCustomers().values());
        return "customerList";
    }

    @RequestMapping(path = "/accountList", method = RequestMethod.GET)
    public String getAccountList(HttpSession session, Model model, String bankID, String customerEmailAddress) {
        setCommonAttributes(session, model);
        Bank bank = Bank.retrieve(bankID);
        Customer customer = bank.getBankCustomers().get(customerEmailAddress);
        model.addAttribute("bank", bank);
        model.addAttribute("accountList", customer.getBankAccounts().values());
        model.addAttribute("customer", customer);
        return "accountList";
    }

    @RequestMapping(path = "/accountDetails", method = RequestMethod.GET)
    public String getAccountDetails(HttpSession session, Model model, String bankID, String customerEmailAddress, String accountID) {
        setCommonAttributes(session, model);

        Bank bank = Bank.retrieve(bankID);
        Customer customer = bank.getBankCustomers().get(customerEmailAddress);
        BankAccount bankAccount = customer.getBankAccountByID(accountID);
        model.addAttribute("bankAccount", bankAccount);
        model.addAttribute("customer", customer);
        model.addAttribute("bank", bank);
        return "accountDetails";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(HttpSession session, String userName) {
        session.setAttribute("userName", userName);
        return "redirect:/";
    }

    @RequestMapping(path = "/create-account", method = RequestMethod.POST)
    public String createAccount(HttpSession session, Model model, String bankID, String accountID,
                                double initialDepositAmount, String customerEmailAddress) {
        System.out.println("createAccount()");
        setCommonAttributes(session, model);
        Bank bank = Bank.retrieve(bankID);
        System.out.println("Retrieved bank with ID = " + bankID);
        System.out.println("ID on the retrieved bank object = " + bank.getBankID());

        BankAccount bankAccount = new BankAccount(accountID, initialDepositAmount);
        Customer customer = bank.getBankCustomers().get(customerEmailAddress);
        bank.addBankAccountForCustomer(bankAccount, customer);

        System.out.println("Added bank account for customer");
        bank.save();

        return "redirect:/accountList?bankID=" + bankID + "&customerEmailAddress=" + customerEmailAddress;
    }

    @RequestMapping(path = "/create-customer", method = RequestMethod.POST)
    public String createCustomer(HttpSession session, Model model, String bankID, String firstName, String lastName,
                                String emailAddress) {
        System.out.println("createCustomer()");
        setCommonAttributes(session, model);
        Bank bank = Bank.retrieve(bankID);

        Customer customer = new Customer(firstName, lastName, emailAddress);
        bank.addCustomer(customer);

        bank.save();

        return "redirect:/customerList?bankID=" + bankID;
    }


    @RequestMapping(path = "/deposit", method = RequestMethod.POST)
    public String deposit(HttpSession session, Model model, String bankID, String accountID, String emailAddress,
                                 double amountToDeposit) {
        System.out.println("deposit()");
        setCommonAttributes(session, model);
        Bank bank = Bank.retrieve(bankID);

        Customer customer = bank.getBankCustomers().get(emailAddress);
        BankAccount account = customer.getBankAccountByID(accountID);

        account.deposit(amountToDeposit);

        bank.save();

        return "redirect:/accountDetails?bankID=" + bankID +
                "&customerEmailAddress=" + emailAddress +
                "&accountID=" + accountID;
    }

    @RequestMapping(path = "/withdraw", method = RequestMethod.POST)
    public String withdraw(HttpSession session, Model model, String bankID, String accountID, String emailAddress,
                          double amountToWithdraw) {
        System.out.println("withdraw()");
        setCommonAttributes(session, model);
        Bank bank = Bank.retrieve(bankID);

        Customer customer = bank.getBankCustomers().get(emailAddress);
        BankAccount account = customer.getBankAccountByID(accountID);

        account.withdraw(amountToWithdraw);

        bank.save();

        return "redirect:/accountDetails?bankID=" + bankID +
                "&customerEmailAddress=" + emailAddress +
                "&accountID=" + accountID;
    }

    @RequestMapping(path = "/create-bank", method = RequestMethod.POST)
    public String createBank(HttpSession session, Model model, String bankName, String bankAddress) {
        System.out.println("createBank()");
        setCommonAttributes(session, model);

        Bank newBank = new Bank(bankName, bankAddress);
        newBank.save();

        return "redirect:/";
    }

    // call this method at the beginning of every controller to make sure the
    // right items are set on the model ...
    public void setCommonAttributes(HttpSession session, Model model) {
        model.addAttribute("name", session.getAttribute("userName"));
    }

}
