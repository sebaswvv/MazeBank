package w.mazebank.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IbanGeneratorTest {
    private String iban;

    @BeforeAll
    void setUp() {
        iban = IbanGenerator.generate();
    }

    @Test
    void ibanShouldBe18Chars() {
        assertEquals(18, iban.length());
    }

    @Test
    void ibanCountryCodeShouldContainCountryCode() {
        assertEquals("NL", iban.substring(0, 2));
    }

    @Test
    void ibanShouldContainBankCode() {
        assertEquals("INHO", iban.substring(4, 8));
    }

    @Test
    void ibanShouldContainAccountNumber() {
        assertEquals(10, iban.substring(8, 18).length());
    }

    @Test
    void accountNumberShouldContainOnlyNumbers() {
        assertTrue(iban.substring(8, 18).chars().allMatch(Character::isDigit));
    }

    @Test
    void accountNumberShouldBeGreaterThan1() {
        System.out.println(iban.substring(8, 18));
        assertEquals(1, iban.substring(8, 18).compareTo("0000000001"));
    }

    @Test
    void accountNumberShouldHavePaddedZeros() {
        assertTrue(iban.substring(8, 18).startsWith("0"));
    }
}