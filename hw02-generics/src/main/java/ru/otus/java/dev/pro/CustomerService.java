package ru.otus.java.dev.pro;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class CustomerService {

    private final NavigableMap<Customer, String> customerMap = new TreeMap<>(new CustomerComparator());


    public Map.Entry<Customer, String> getSmallest() {
        return copyOf(customerMap.firstEntry());

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
