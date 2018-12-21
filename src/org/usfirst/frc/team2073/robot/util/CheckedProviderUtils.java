package org.usfirst.frc.team2073.robot.util;

import com.google.inject.throwingproviders.CheckedProvider;

public class CheckedProviderUtils {
    public static <T> T getOrNull(CheckedProvider<T> provider) {
        try {
            return provider.get();
        } catch (Exception e) {
            return null;
        }
    }
}
