package com.shimkiv.trust.enums;

/**
 * Verification Types Enumeration
 *
 * @author Serhii Shymkiv
 */

public enum VerificationType {
    UI_ALERT,
    UI_ERROR,
    UI_COMMON,
    API_RESPONSE;

    /**
     * Gets enum object by name
     *
     * @param name Verification type name
     * @return Enum object
     */
    public static VerificationType getVerificationType(String name) {
        for(VerificationType verificationType : values()) {
            if(verificationType.
                    toString().
                    equalsIgnoreCase(name)) {
                return verificationType;
            }
        }

        return null;
    }
}
