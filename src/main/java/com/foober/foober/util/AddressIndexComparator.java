package com.foober.foober.util;

import com.foober.foober.model.Address;

import java.util.Comparator;

public class AddressIndexComparator implements Comparator<Address> {
    @Override
    public int compare(Address o1, Address o2) {
        return Integer.compare(o1.getStation(), o2.getStation());
    }

}
