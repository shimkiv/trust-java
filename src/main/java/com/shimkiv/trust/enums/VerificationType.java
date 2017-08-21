/*
 * All materials herein: Copyright (c) 2000-2017 Serhii Shymkiv. All Rights Reserved.
 *
 * These materials are owned by Serhii Shymkiv and are protected by copyright laws
 * and international copyright treaties, as well as other intellectual property laws
 * and treaties.
 *
 * All right, title and interest in the copyright, confidential information,
 * patents, design rights and all other intellectual property rights of
 * whatsoever nature in and to these materials are and shall remain the sole
 * and exclusive property of Serhii Shymkiv.
 */

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
