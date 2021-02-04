package edu.unh.cs.cs619.bulletzone.datalayer;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class BankAccountRepositoryTest {
    static BulletZoneData db;
    static GameUser basicUser, otherUser;
    static BankAccount basicAccount, otherAccount;

    @BeforeClass
    static public void setup() {
        db = new BulletZoneData();
        db.rebuildData();
        basicUser = db.users.createUser("BasicUser", "BasicUser", "password");
        otherUser = db.users.createUser("OtherUser", "OtherUser", "password");
        basicAccount = db.accounts.create();
        db.permissions.setOwner(basicAccount, basicUser);
        otherAccount = db.accounts.create();
        db.permissions.setOwner(otherAccount, otherUser);
    }

    @Test
    public void testGetAccount() {
        assertEquals(db.accounts.getAccount(basicAccount.getId()), basicAccount);
        assertEquals(db.accounts.getAccount(otherAccount.getId()), otherAccount);
    }

    @Test
    public void testGetAccounts() {
        Collection<BankAccount> list = db.accounts.getAccounts();
        assertTrue(list.contains(basicAccount));
        assertTrue(list.contains(otherAccount));
        assertThat(list.size(), is(2));
    }

    @Test
    public void testModifyAccountBalance() {
        int amount = 500;
        double startAmount = basicAccount.getBalance();
        Collection<AccountTransferHistoryRecord> startList = db.accounts.getTransactions(basicAccount);
        db.accounts.modifyAccountBalance(basicAccount, amount);
        assertThat(basicAccount.getBalance(), is(startAmount + amount));
        db.accounts.modifyAccountBalance(basicAccount, -amount);
        assertThat(basicAccount.getBalance(), is(startAmount));
        Collection<AccountTransferHistoryRecord> endList = db.accounts.getTransactions(basicAccount);
        assertThat(endList.size(), is(startList.size() + 2));
    }
}