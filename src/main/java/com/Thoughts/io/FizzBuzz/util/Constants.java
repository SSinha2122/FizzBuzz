package com.Thoughts.io.FizzBuzz.util;

import com.Thoughts.io.FizzBuzz.config.AppConfig;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
@AllArgsConstructor
public class Constants {
    AppConfig appConfig;

    public static final String ACTIVATION_EMAIL = appConfig.getAppUrl()+"/api/auth/accountVerification";
}
