package ru.otus.java.dev.pro;

import java.util.Comparator;

public class CustomerComparator implements Comparator<Customer> {

    @Override
    public int compare(Customer o1, Customer o2) {
        return Long.compare(o1.getScores(), o2.getScores()); // NPE if o1\o2 is null
    }

}
