package ru.otus.java.dev.pro;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("Банкомат должен")
class AutomatedTellerMachineTest {

    @Test
    @DisplayName("принимать банкноты разных номиналов (на каждый номинал должна быть своя ячейка)")
    void putBanknotes() {
        List<Banknote> randomBanknotes = generateBanknotes(1, 10);
        assertThat(randomBanknotes).isNotEmpty();

        int countOfDenomination = randomBanknotes.stream().map(Banknote::denomination).collect(Collectors.toSet()).size();
        assertThat(countOfDenomination)
                .isGreaterThan(1)
                .isEqualTo(Denomination.values().length); // используем все имеюшщиеся

        var moneySum = randomBanknotes.stream().mapToInt(Banknote::getValue).sum();
        assertThat(moneySum).isGreaterThan(0);

        var atm = new AutomatedTellerMachine();
        var addedMoney = atm.putBanknotes(randomBanknotes);
        assertThat(addedMoney).isGreaterThan(0);

        assertThat(addedMoney).isEqualTo(moneySum);
    }


    @Test
    @DisplayName("выдавать запрошенную сумму минимальным количеством банкнот")
    void getSum() {
        // генерируем много кэша, чтобы тест точно прошел
        List<Banknote> randomBanknotes = generateBanknotes(99, 101);
        assertThat(randomBanknotes).isNotEmpty();
        var moneySum = randomBanknotes.stream().mapToInt(Banknote::getValue).sum();

        var atm = new AutomatedTellerMachine();
        var addedMoney = atm.putBanknotes(randomBanknotes);
        assertThat(addedMoney).isEqualTo(moneySum).isGreaterThan(0);

        var requestSum = 10_230L;
        List<Banknote> resultBanknoteList = atm.getSum(requestSum);
        assertThat(resultBanknoteList).isNotEmpty();

        var resultMoneySum = resultBanknoteList.stream().mapToInt(Banknote::getValue).sum();
        assertThat(requestSum).isEqualTo(resultMoneySum);

        // бред с натуральной т.з., но математически правильно
        assertThat(randomBanknotes).containsAll(resultBanknoteList);
    }

    @Test
    @DisplayName("выдавать ошибку, если сумму нельзя выдать")
    void getSumException() {
        // генерируем много кэша, чтобы тест точно прошел
        List<Banknote> randomBanknotes = generateBanknotes(99, 101);
        assertThat(randomBanknotes).isNotEmpty();
        var moneySum = randomBanknotes.stream().mapToInt(Banknote::getValue).sum();

        var atm = new AutomatedTellerMachine();
        var addedMoney = atm.putBanknotes(randomBanknotes);
        assertThat(addedMoney).isEqualTo(moneySum).isGreaterThan(0);

        var requestSum = 10_232L;
        assertThatExceptionOfType(CacheOutException.class)
                .isThrownBy(() -> atm.getSum(requestSum))
                .withMessageMatching("Can't return all sum");
    }

    @Test
    @DisplayName("выдавать сумму остатка денежных средств")
    void getRemainder() {
        List<Banknote> randomBanknotes = generateBanknotes(49, 51);
        assertThat(randomBanknotes).isNotEmpty();
        var moneySum = randomBanknotes.stream().mapToInt(Banknote::getValue).sum();

        var atm = new AutomatedTellerMachine();
        var addedMoney = atm.putBanknotes(randomBanknotes);
        assertThat(addedMoney).isEqualTo(moneySum).isGreaterThan(0); // сколько положили, столько и есть

        var startRemainder = atm.getRemainder();
        assertThat(startRemainder).isEqualTo(addedMoney);

        var requestSum = 10_000L;
        List<Banknote> resultBanknoteList = atm.getSum(requestSum);
        assertThat(resultBanknoteList).isNotEmpty();

        var resultMoneySum = resultBanknoteList.stream().mapToInt(Banknote::getValue).sum();
        assertThat(requestSum).isEqualTo(resultMoneySum).isGreaterThan(0);

        var endRemainder = atm.getRemainder();
        assertThat(addedMoney - requestSum).isEqualTo(endRemainder);
    }


    private List<Banknote> generateBanknotes(int denominationOrigin, int denominationBound) {
        var generatedList = new ArrayList<Banknote>();
        Arrays.stream(Denomination.values()).forEach(denomination -> {
            var banknoteCount = RandomGenerator.getDefault().nextInt(denominationOrigin, denominationBound);
            for (int i = 0; i < banknoteCount; i++) {
                generatedList.add(new Banknote(denomination));
            }
        });
        return generatedList;
    }

}