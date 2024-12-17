package ru.otus.java.dev.pro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Cell {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final List<Banknote> banknotes = new LinkedList<>();

    private final Denomination denomination;


    public static Cell emptyCell(Denomination denomination) {
        return new Cell(denomination);
    }

    private Cell(Denomination denomination) {
        if (denomination == null) {
            throw new IllegalArgumentException("Denomination is NULL");
        }
        this.denomination = denomination;
    }


    /**
     * Отдает либо запрошенно е количество банкнот, либо все (если их меньше, чем запрошено)
     * @param requestCount - сколько нужно банкнот
     * @return Список банкнот
     */
    public List<Banknote> getBanknotesOrAll(long requestCount) {
        if (requestCount <= 0) {
            throw new IllegalArgumentException("Count less or equals ZERO");
        }
        if (requestCount > countOfBanknotes()) {
            throw new IllegalArgumentException("The cell does not have that many banknotes");
        }

        List<Banknote> banknotesToOut;
        if (requestCount > countOfBanknotes()) {
            banknotesToOut = new ArrayList<>(banknotes);
            banknotes.clear();
        } else {
            banknotesToOut = new ArrayList<>();
            for (int i = 0; i < requestCount; i++) {
                banknotesToOut.add(banknotes.removeFirst());
            }
        }
        log.info("Returns {} banknote(s), denomination: {}", banknotesToOut.size(), getDenomination().name());
        return banknotesToOut;
    }

    public void putBanknote(Banknote banknote) {
        if (getDenomination() != banknote.denomination()) {
            throw new IllegalArgumentException(("The denomination of the banknote [%s] does not match " +
                    "the denomination of the cell [%s]").formatted(banknote.denomination(), getDenomination()));
        }
        this.banknotes.add(banknote);
        log.trace("Put [{}] banknote", banknote.serialNumber());
    }

    public Long getSumMoney() {
        return this.banknotes.stream().mapToLong(Banknote::getValue).sum();
    }

    public long countOfBanknotes() {
        return banknotes.size();
    }

    public boolean isEmpty() {
        return countOfBanknotes() == 0;
    }

    public Denomination getDenomination() {
        return denomination;
    }

}
