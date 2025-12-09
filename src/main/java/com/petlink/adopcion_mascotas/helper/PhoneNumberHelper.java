package com.petlink.adopcion_mascotas.helper;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class PhoneNumberHelper {

    private static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    @Getter
    public static class CountryPhone {
        private final String regionCode;
        private final int countryCode;
        private final String displayName;

        public CountryPhone(String regionCode, int countryCode) {
            this.regionCode = regionCode;
            this.countryCode = countryCode;
            this.displayName = regionCode + " (+" + countryCode + ")";
        }

        public String getPrefijo() {
            return "+" + countryCode;
        }
    }

    public static List<CountryPhone> getAllCountries() {
        Set<String> supportedRegions = phoneNumberUtil.getSupportedRegions();

        return supportedRegions.stream()
                .map(region -> {
                    int countryCode = phoneNumberUtil.getCountryCodeForRegion(region);
                    return new CountryPhone(region, countryCode);
                })
                .sorted(Comparator.comparing(CountryPhone::getDisplayName))
                .collect(Collectors.toList());
    }

    public static List<CountryPhone> getMainCountries() {
        String[] mainRegions = {
            "ES", "US", "MX", "AR", "BR", "CL", "CO", "PE", "VE", "EC",
            "UY", "PY", "BO", "CR", "PA", "GT", "HN", "SV", "NI", "CU",
            "DO", "PR", "FR", "DE", "IT", "GB", "PT", "CA", "CN", "JP",
            "KR", "IN", "AU", "NZ"
        };

        List<CountryPhone> countries = new ArrayList<>();
        for (String region : mainRegions) {
            int countryCode = phoneNumberUtil.getCountryCodeForRegion(region);
            countries.add(new CountryPhone(region, countryCode));
        }

        return countries;
    }
}
