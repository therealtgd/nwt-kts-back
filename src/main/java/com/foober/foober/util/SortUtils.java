package com.foober.foober.util;

import com.foober.foober.dto.RideBriefDisplay;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.foober.foober.util.SortUtils.stringToDate;

public class SortUtils {
    public static List<RideBriefDisplay> sort(List<RideBriefDisplay> unsorted, String criteria) {
        switch (criteria) {
            case "route-↓":
                unsorted.sort(new RouteAscComparator());
                break;
            case "route-↑":
                unsorted.sort(new RouteDescComparator());
                break;
            case "price-↓":
                unsorted.sort(new PriceAscComparator());
                break;
            case "price-↑":
                unsorted.sort(new PriceDescComparator());
                break;
            case "date departed-↓":
                unsorted.sort(new DateAscComparator());
                break;
            case "date departed-↑":
                unsorted.sort(new DateDescComparator());
                break;
        }
        return unsorted;
    }
    @SneakyThrows
    public static Date stringToDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd MMM yyyy");
        return sdf.parse(dateString);
    }
}

class RouteAscComparator implements Comparator<RideBriefDisplay> {
    @Override
    public int compare(RideBriefDisplay o1, RideBriefDisplay o2) {
        return o1.getStartLocation().compareTo(o2.getStartLocation());
    }
}
class RouteDescComparator implements Comparator<RideBriefDisplay> {
    @Override
    public int compare(RideBriefDisplay o1, RideBriefDisplay o2) {
        return o2.getStartLocation().compareTo(o1.getStartLocation());
    }
}
class PriceAscComparator implements Comparator<RideBriefDisplay> {
    @Override
    public int compare(RideBriefDisplay s1, RideBriefDisplay s2) {
        return Double.compare(s1.getPrice(), s2.getPrice());
    }
}
class PriceDescComparator implements Comparator<RideBriefDisplay> {
    @Override
    public int compare(RideBriefDisplay s1, RideBriefDisplay s2) {
        return Double.compare(s2.getPrice(), s1.getPrice());
    }
}
class DateAscComparator implements Comparator<RideBriefDisplay> {
    @Override
    public int compare(RideBriefDisplay s1, RideBriefDisplay s2) {
        return stringToDate(s1.getStartTime()).compareTo(stringToDate(s2.getStartTime()));
    }
}
class DateDescComparator implements Comparator<RideBriefDisplay> {
    @Override
    public int compare(RideBriefDisplay s1, RideBriefDisplay s2) {
        return stringToDate(s2.getStartTime()).compareTo(stringToDate(s1.getStartTime()));
    }
}
