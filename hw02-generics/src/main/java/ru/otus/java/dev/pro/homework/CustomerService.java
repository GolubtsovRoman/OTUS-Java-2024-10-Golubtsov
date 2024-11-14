package ru.otus.java.dev.pro.homework;

import java.util.*;

public class CustomerService {

    private final TreeMap<Customer, String> customerMap = new TreeMap<>(new CustomerComparator());


    public Map.Entry<Customer, String> getSmallest() {
        return copyOf(customerMap.entrySet().stream().findFirst().orElseThrow(NoSuchElementException::new));

    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        return copyOf(customerMap.higherEntry(customer));
    }

    public void add(Customer customer, String data) {
        customerMap.put(customer, data);
    }


    private Map.Entry<Customer, String> copyOf(Map.Entry<Customer, String> entry) {
        if (entry == null) {
            return null;
        }
        return Map.entry(entry.getKey().copy(), String.copyValueOf(entry.getValue().toCharArray()));
    }

}
