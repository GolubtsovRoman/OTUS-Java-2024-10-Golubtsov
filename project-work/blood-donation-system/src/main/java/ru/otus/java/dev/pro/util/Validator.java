package ru.otus.java.dev.pro.util;

import ru.otus.java.dev.pro.exception.ValidateException;

import java.util.Objects;
import java.util.regex.Pattern;

public class Validator {

    private static final Pattern SNILS_PATTERN =
            Pattern.compile("^(\\d{3})-(\\d{3})-(\\d{3})-(\\d{2})$");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");


    public static void validateSnils(String snils) {
        if (Objects.isNull(snils)) {
            throw new ValidateException("snils", null, "value is NULL");
        }

        if (!SNILS_PATTERN.matcher(snils).matches()) {
            throw new ValidateException("snils", snils);
        }

        int[] snilsNumbers = new int[9];
        for (int i = 0; i < 9; i++) {
            snilsNumbers[i] = Character.getNumericValue(snils.charAt(i + (i / 3)));
        }

        // control number
        int controlNumber = (snilsNumbers[0] * 9 + snilsNumbers[1] * 8 + snilsNumbers[2] * 7 +
                snilsNumbers[3] * 6 + snilsNumbers[4] * 5 + snilsNumbers[5] * 4 +
                snilsNumbers[6] * 3 + snilsNumbers[7] * 2 + snilsNumbers[8]) % 101;
        if (controlNumber == 100) {
            controlNumber = 0;
        }

        // check
        if (controlNumber != Integer.parseInt(snils.substring(12, 14))) {
            throw new ValidateException("snils", snils, "control number does not match");
        }
    }


    public static void validateEmail(String email) {
        if (Objects.isNull(email)) {
            throw new ValidateException("email", email, "value is NULL");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidateException("email", email);
        }
    }

}
