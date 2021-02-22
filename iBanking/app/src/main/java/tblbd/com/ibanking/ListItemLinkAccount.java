package tblbd.com.ibanking;

public class ListItemLinkAccount {
    String AccountNumber;
    String AccounName;
    String AccountType;
    String Balance;

    public ListItemLinkAccount(String accountNumber, String accounName, String accountType) {
        AccountNumber = accountNumber;
        AccounName = accounName;
        AccountType = accountType;

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

}
