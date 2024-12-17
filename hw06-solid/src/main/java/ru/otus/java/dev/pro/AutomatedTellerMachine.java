package ru.otus.java.dev.pro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AutomatedTellerMachine {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // 1 номинал - 1 одна ячейка
    private final Map<Denomination, Cell> cellMap = Arrays.stream(Denomination.values())
            .collect(Collectors.toMap(
                    denomination -> denomination,
                    Cell::emptyCell
            ));


    public Long putBanknotes(List<Banknote> banknotes) {
        for (Banknote banknote: banknotes) {
            this.cellMap.get(banknote.denomination()).putBanknote(banknote);
        }
        Long sum = banknotes.stream().mapToLong(Banknote::getValue).sum();
        log.info("ATM filled: {} banknote(s) on sum {}", banknotes.size(), sum);
        return sum;
    }

    public List<Banknote> getSum(Long sum) {
        if (sum == null || sum < 1) {
            throw new IllegalArgumentException("Sum is NULL or less ONE. sum=[%s]".formatted(sum));
        }
        Set<Denomination> sortedDenomination = cellMap.keySet().stream()
                .sorted(Comparator.comparing(Denomination::getNum))
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .reversed();

        var banknotesToOut = new ArrayList<Banknote>();
        for (Denomination denomination: sortedDenomination) {
            if (sum == 0) {
                break;
            }
            if (cellMap.get(denomination).isEmpty()) {
                continue;
            }
            var banknotePower = denomination.getNum();
            var banknoteNeedCount = sum / banknotePower;
            if (banknoteNeedCount > 0) {
                List<Banknote> addedCache = cellMap.get(denomination).getBanknotesOrAll(banknoteNeedCount);
                banknotesToOut.addAll(addedCache);
                sum %= addedCache.stream().mapToLong(Banknote::getValue).sum();
            }
        }
        if (sum != 0) {
            throw new CacheOutException("Can't return all sum");
        }

        return banknotesToOut;
    }

    public Long getRemainder() {
        return this.cellMap.values().stream()
                .mapToLong(Cell::getSumMoney)
                .sum();
    }

}
