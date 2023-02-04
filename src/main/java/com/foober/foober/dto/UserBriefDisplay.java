package com.foober.foober.dto;

import com.foober.foober.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.codec.binary.Base64;

import static com.foober.foober.util.GeneralUtils.TEMPLATE_IMAGE;

@Getter
@Setter
@AllArgsConstructor
public class UserBriefDisplay {
    private String displayName;
    private String username;
    private String image;

    public UserBriefDisplay(User user) {
        this.displayName = user.getDisplayName();
        this.username = user.getUsername();
        this.image = user.getImage() != null ? Base64.encodeBase64String(user.getImage().getData()) : TEMPLATE_IMAGE;
    }
}
