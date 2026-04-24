package bank.impl;

import bank.api.Bank;
import dto.BankCard;
import dto.BankCardType;
import dto.CreditBankCard;
import dto.DebitBankCard;
import dto.User;

import java.util.Random;
import java.util.function.BiFunction;

public class BankImpl implements Bank {

    private final Random random = new Random();
    private final String prefix;
    private final boolean useAbsoluteValue;

    public BankImpl() {
        this("", false);
    }

    protected BankImpl(String prefix) {
        this(prefix, true);
    }

    protected BankImpl(String prefix, boolean useAbsoluteValue) {
        this.prefix = prefix;
        this.useAbsoluteValue = useAbsoluteValue;
    }

    @Override
    public BankCard createBankCard(User user, BankCardType bankCardType) {
        var number = generateNumber();
        return cardFactory(bankCardType).apply(number, user);
    }

    private String generateNumber() {
        var value = random.nextLong();
        if (useAbsoluteValue) {
            value = Math.abs(value);
        }
        return prefix + value;
    }

    protected BiFunction<String, User, BankCard> cardFactory(BankCardType bankCardType) {
        return switch (bankCardType) {
            case CREDIT -> CreditBankCard::new;
            case DEBIT -> DebitBankCard::new;
        };
    }
}
