package team_f.domain.entities;

import team_f.domain.enums.AccountRole;
import team_f.domain.enums.EntityType;
import team_f.domain.enums.properties.AccountProperty;

public class Account extends BaseDomainEntity<AccountProperty> {
    private Integer _accountID;
    private String _username;
    private String _password;
    private AccountRole _accountRole;

    public Account() {
        super(EntityType.ACCOUNT);
    }

    public Integer getAccountID() {
        return _accountID;
    }

    public String getUsername() {
        return _username;
    }

    public String getPassword() {
        return _password;
    }

    public AccountRole getRole() {
        return _accountRole;
    }

    public void setAccountID(Integer accountID) {
        _accountID = accountID;
    }

    public void setUsername(String username) {
        _username = username;
    }

    public void setPassword(String password) {
        _password = password;
    }

    public void setRole(AccountRole accountRole) {
        _accountRole = accountRole;
    }

    @Override
    public int getID() {
        return getAccountID();
    }
}
