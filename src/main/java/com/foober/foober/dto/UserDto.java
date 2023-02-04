package com.foober.foober.dto;

import com.foober.foober.model.Role;
import com.foober.foober.model.User;
import lombok.Data;
import org.apache.tomcat.util.codec.binary.Base64;

import java.util.stream.Collectors;

import static com.foober.foober.util.GeneralUtils.TEMPLATE_IMAGE;

@Data
public class UserDto {
    public Long id;
    public String email;
    public String username;
    public String displayName;
    public String phoneNumber;
    public String city;
    public String image;
    public boolean enabled;

    public UserDto (User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.phoneNumber = user.getPhoneNumber();
        this.city = user.getCity();
        String image = TEMPLATE_IMAGE;
        if (user.getImage() != null)
            image = Base64.encodeBase64String(user.getImage().getData());
        this.image = image;
        this.enabled = user.isEnabled();
    }
}
