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
    public static final String TPL_START_SUBSTRING = "${";
    public static final String TPL_END_SUBSTRING = "}";
    public static final String COLON_MARK = ";";
    public static final String XML_MARK = "xml";

    public static final int FIRST_ELEMENT = 0;
    public static final int SECOND_ELEMENT = 1;


    /*
     * TRUST settings section
     */

    /**
     * Script engine name to use
     * Default: nashorn
     */
    public static final String SCRIPT_ENGINE_NAME =
            System.getProperty(
                    "trust.scriptEngine",
                    "nashorn");
    /**
     * Colon separated URLs of scripts to load
     * Default: https://cdn.jsdelivr.net/lodash/4.17.4/lodash.min.js
     */
    public static final Stream<String> SCRIPTS_TO_LOAD =
            Stream.of(
                    System.getProperty(
                            "trust.loadScripts",
                            "https://cdn.jsdelivr.net/lodash/4.17.4/lodash.min.js").
                            split(COLON_MARK));
    /**
     * Verification types delimiter
     * Default: |&|
     */
    public static final String VERIFICATION_TYPES_DELIMITER =
            System.getProperty(
                    "trust.verificationTypesDelimiter",
                    "\\|\\&\\|");
    /**
     * Verification type delimiter
     * Default: :  (two spaces)
     */
    public static final String VERIFICATION_TYPE_DELIMITER =
            System.getProperty(
                    "trust.verificationTypeDelimiter",
                    ":\u0020\u0020");
    /**
     * Verification rules delimiter
     * Default: ;  (two spaces)
     */
    public static final String VERIFICATION_RULES_DELIMITER =
            System.getProperty(
                    "trust.verificationRulesDelimiter",
                    ";\u0020\u0020");


    /*
     * TRUST messages section
     */
    public static final String SCRIPT_EVAL_ERROR_MESSAGE =
            "Impossible to evaluate provided script ! " +
                    "Verification rules vocabulary will be limited !";
    public static final String COMMON_EVAL_ERROR_MESSAGE =
            "Impossible to evaluate the expression(s) !";

    // Prevents instantiation
    private TrustConfig() {}
}
