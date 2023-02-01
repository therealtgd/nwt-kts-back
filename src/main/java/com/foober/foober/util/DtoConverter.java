package com.foober.foober.util;

import com.foober.foober.dto.RideBriefDisplay;
import com.foober.foober.dto.UserBriefDisplay;
import com.foober.foober.model.Address;
import com.foober.foober.model.Client;
import com.foober.foober.model.Ride;
import com.foober.foober.model.User;
import org.apache.tomcat.util.codec.binary.Base64;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.foober.foober.util.GeneralUtils.TEMPLATE_IMAGE;

public class DtoConverter {
    public static RideBriefDisplay rideToBriefDisplay(Ride ride, Client client) {
        Set<UserBriefDisplay> clients = new HashSet<>();
        ride.getClients().forEach(c -> clients.add(userToBriefDisplay(c)));
        boolean favorite = false;
        for (Ride r : client.getFavorites()) {
            favorite = favorite || r.getId().equals(ride.getId());
        }
        return new RideBriefDisplay(
                ride.getId(),
                userToBriefDisplay(ride.getDriver()),
                clients,
                ride.getPrice(),
                ride.getDistance(),
                getAddressAtIndex(ride.getRoute(), 1),
                getAddressAtIndex(ride.getRoute(), ride.getRoute().size()),
                longToTime(ride.getStartTime()),
                longToTime(ride.getEndTime()),
                favorite
        );
    }
    public static RideBriefDisplay rideToBriefDisplay(Ride ride) {
        Set<UserBriefDisplay> clients = new HashSet<>();
        ride.getClients().forEach(client -> clients.add(userToBriefDisplay(client)));
        return new RideBriefDisplay(
                ride.getId(),
                userToBriefDisplay(ride.getDriver()),
                clients,
                ride.getPrice(),
                ride.getDistance(),
                getAddressAtIndex(ride.getRoute(), 1),
                getAddressAtIndex(ride.getRoute(), ride.getRoute().size()),
                longToTime(ride.getStartTime()),
                longToTime(ride.getEndTime()),
                false
        );
    }
    public static UserBriefDisplay userToBriefDisplay(User user) {
        String image = TEMPLATE_IMAGE;
        if (user.getImage() != null)
            image = Base64.encodeBase64String(user.getImage().getData());
        return new UserBriefDisplay(user.getDisplayName(), user.getUsername(), image);
    }
    public static String longToTime(Long num) {
        Date date = new Date(num);
        Format format = new SimpleDateFormat("HH:mm dd MMM yyyy");
        return format.format(date);
    }
    public static String getAddressAtIndex(Set<Address> addresses, int index) {
        for (Address address:addresses) {
            if (address.getStation() == index) {
                return address.getStreetAddress();
            }
        }
        return null;
    }
}
