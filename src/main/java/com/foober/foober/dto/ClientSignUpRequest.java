package com.foober.foober.dto;

import com.foober.foober.validation.PasswordMatches;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@PasswordMatches
public class ClientSignUpRequest {
    @NotEmpty
    @Size(min = 2, max = 20)
    @Pattern(regexp = "[a-zA-Za-šA-Š\s]*")
    private String displayName;
    @NotEmpty
    @Size(min = 2, max = 20)
    @Pattern(regexp = "[a-zA-Za-šA-Š0-9]*")
    private String username;
    @NotEmpty
    @Size(min = 2, max = 64)
    @Email
    private String email;
    @NotEmpty
    @Size(min = 6, max = 20)
    @Pattern(regexp = "[a-zA-Za-šA-Š0-9]*")
    private String password;
    @NotEmpty
    @Size(min = 6, max = 20)
    @Pattern(regexp = "[a-zA-Za-šA-Š0-9]*")
    private String confirmPassword;
    private SocialProvider socialProvider;
    private String providerUserId;

    public ClientSignUpRequest(String providerUserId, String displayName, String email, String password, SocialProvider socialProvider) {
        this.providerUserId = providerUserId;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.socialProvider = socialProvider;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String providerUserID;
        private String displayName;
        private String email;
        private String password;
        private SocialProvider socialProvider;

        public Builder addProviderUserID(final String userID) {
            this.providerUserID = userID;
            return this;
        }

        public Builder addDisplayName(final String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder addEmail(final String email) {
            this.email = email;
            return this;
        }

        public Builder addPassword(final String password) {
            this.password = password;
            return this;
        }

        public Builder addSocialProvider(final SocialProvider socialProvider) {
            this.socialProvider = socialProvider;
            return this;
        }

        public ClientSignUpRequest build() {
            return new ClientSignUpRequest(providerUserID, displayName, email, password, socialProvider);
        }
    }
}
