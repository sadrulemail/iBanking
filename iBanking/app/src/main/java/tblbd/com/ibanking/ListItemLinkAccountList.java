package tblbd.com.ibanking;

public class ListItemLinkAccountList {
    String AccountNumber;
    String AccounName;
    String AccountType;
    String Balance;

    public ListItemLinkAccountList(String accountNumber, String accounName, String accountType,String balance) {
        AccountNumber = accountNumber;
        AccounName = accounName;
        AccountType = accountType;
        Balance=balance;

    }

    public String getAccountNumber() {
        return AccountNumber;
    }

    public String getAccounName() {
        return AccounName;
    }

    public String getAccountType() {
        return AccountType;
    }

    public String getBalance() {
        return Balance;
    }

}
