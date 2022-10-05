package com.aeontronix.enhancedmule.propertiesprovider.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class UserConfigFile {
    @JsonProperty("default")
    private String defaultProfile;
    @JsonProperty("profiles")
    private Map<String, Profile> profiles;

    public String getDefaultProfile() {
        return defaultProfile;
    }

    public void setDefaultProfile(String defaultProfile) {
        this.defaultProfile = defaultProfile;
    }

    public Map<String, Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(Map<String, Profile> profiles) {
        this.profiles = profiles;
    }

    public static class Profile {
        @JsonProperty("cryptoKey")
        private String key;

        public Profile() {
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}
