package com.foober.foober.util;

import com.foober.foober.dto.LocalUser;
import com.foober.foober.dto.SocialProvider;
import com.foober.foober.dto.UserInfo;
import com.foober.foober.exception.UserIsNotActivatedException;
import com.foober.foober.model.Role;
import com.foober.foober.model.User;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GeneralUtils {

    public static final String TEMPLATE_IMAGE = "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";

    public static List<SimpleGrantedAuthority> buildSimpleGrantedAuthorities(final Set<Role> roles) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    public static SocialProvider toSocialProvider(String providerId) {
        for (SocialProvider socialProvider : SocialProvider.values()) {
            if (socialProvider.getProviderType().equals(providerId)) {
                return socialProvider;
            }
        }
        return SocialProvider.LOCAL;
    }

    public static UserInfo buildUserInfo(LocalUser localUser) {
        List<String> roles = localUser.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());
        roles.remove("ROLE_USER");
        User user = localUser.getUser();
        if (!user.isEnabled())
            throw new UserIsNotActivatedException();
        String image = TEMPLATE_IMAGE;
        if (user.getImage() != null)
            image = Base64.encodeBase64String(user.getImage().getData());
        return new UserInfo(image, user.getDisplayName(), user.getUsername(), user.getEmail(), roles.get(0), user.getPhoneNumber(), user.getCity());
    }

}