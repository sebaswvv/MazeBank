package w.mazebank.utils;

import org.iban4j.CountryCode;
import org.iban4j.Iban;

import java.util.Random;

public class IbanGenerator {
    private static final String BANK_CODE = "INHO";
    private static final int ACCOUNT_NUMBER_LENGTH = 9;
    private static final int MIN_ACCOUNT_NUMBER = 2; // 1 is reserved for the bank itself

    private static final Random RANDOM = new Random();

    public static String generate() {
        int accountNumberBound = (int) Math.pow(10, ACCOUNT_NUMBER_LENGTH);
        int accountNumber = RANDOM.nextInt(MIN_ACCOUNT_NUMBER, accountNumberBound);
        Iban iban = new Iban.Builder()
            .countryCode(CountryCode.NL)
            .bankCode(BANK_CODE)
            .accountNumber(String.format("%010d", accountNumber))
            .build();
        return iban.toString();
    }
}
