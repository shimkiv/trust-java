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

package com.shimkiv.trust.config;

import java.util.stream.Stream;

/**
 * TRUST Configuration
 *
 * @author Serhii Shymkiv
 */

public class TrustConfig {
    /*
     * TRUST constants section
     */
    public static final String LOAD_SCRIPT_TPL_EXPR =
            "load(\"{0}\");";
    public static final String ALERT_MSG_TPL = "ALERT_MSG";
    public static final String ERROR_MSG_TPL = "ERROR_MSG";
    public static final String TPL_START_SUBSTRING = "${";
    public static final String TPL_END_SUBSTRING = "}";
    public static final String BODY_TAG_NAME = "body";
    public static final String COLON_MARK = ";";
    public static final String XML_MARK = "xml";

    public static final int FIRST_ELEMENT = 0;
    public static final int SECOND_ELEMENT = 1;


    /*
     * TRUST settings section
     */
    public static final String SCRIPT_ENGINE_NAME =
            System.getProperty(
                    "trust.scriptEngine",
                    "nashorn");
    public static final Stream<String> SCRIPTS_TO_LOAD =
            Stream.of(
                    System.getProperty(
                            "trust.loadScripts",
                            "https://cdn.jsdelivr.net/lodash/4.17.4/lodash.min.js").
                            split(COLON_MARK));
    public static final String VERIFICATION_TYPES_DELIMITER =
            System.getProperty(
                    "trust.verificationTypesDelimiter",
                    "\\|\\&\\|");
    public static final String VERIFICATION_TYPE_DELIMITER =
            System.getProperty(
                    "trust.verificationTypeDelimiter",
                    ":\u0020\u0020");
    public static final String VERIFICATION_RULES_DELIMITER =
            System.getProperty(
                    "trust.verificationRulesDelimiter",
                    ";\u0020\u0020");
    public static final boolean ALERTS_AUTO_CONFIRM =
            Boolean.parseBoolean(
                    System.getProperty(
                            "trust.alertsAutoConfirm",
                            "true"));


    /*
     * TRUST messages section
     */
    public static final String SCRIPT_EVAL_ERROR_MESSAGE =
            "Impossible to evaluate the script ! " +
                    "Verification rules vocabulary will be limited !";
    public static final String COMMON_EVAL_ERROR_MESSAGE =
            "Impossible to evaluate the expression(s) !";

    // Prevents instantiation
    private TrustConfig() {}
}
