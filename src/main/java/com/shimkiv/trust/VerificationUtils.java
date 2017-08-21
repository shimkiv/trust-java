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

package com.shimkiv.trust;

import com.codeborne.selenide.SelenideElement;
import com.shimkiv.trust.entities.verification.VerificationEntities;
import com.shimkiv.trust.enums.VerificationType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.assertj.core.api.AutoCloseableSoftAssertions;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.shimkiv.trust.common.CommonUtils.*;
import static com.shimkiv.trust.config.TrustConfig.*;
import static com.shimkiv.trust.enums.VerificationType.*;
import static com.shimkiv.trust.evaluation.EvaluationUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Verification Utils
 *
 * @author Serhii Shymkiv
 */

public class VerificationUtils {
    private static final Logger LOG =
            Logger.getLogger(
                    VerificationUtils.class.
                            getName());

    // Prevents instantiation
    private VerificationUtils() {}

    /**
     * Performs UI alert verification
     *
     * @param verificationEntities {@link VerificationEntities}
     */
    public static void performUiAlertVerification(VerificationEntities
                                                          verificationEntities) {
        LOG.info("About to verify the UI alert ...");

        assertThat(alertIsPresent()).
                isTrue();

        Map<String, String> testResults =
                new HashMap<>();

        testResults.put(
                ALERT_MSG_TPL,
                switchTo().
                        alert().
                        getText());

        verifyTestResults(
                testResults,
                verificationEntities.
                        getVerificationEntity(
                                UI_ALERT).
                        getVerificationRules());

        if(ALERTS_AUTO_CONFIRM) {
            confirmAlertQuietly();
        }
    }

    /**
     * Performs UI error verification
     *
     * @param verificationEntities {@link VerificationEntities}
     * @param errorContainerLabel The displayed label of the error container
     */
    public static void performUiErrorVerification(VerificationEntities
                                                          verificationEntities,
                                                  String errorContainerLabel) {
        LOG.info("About to verify the UI error ...");

        SelenideElement errorContainer =
                getParentContainer(
                        errorContainerLabel);
        Map<String, String> testResults =
                new HashMap<>();

        testResults.put(
                ERROR_MSG_TPL,
                errorContainer.
                        getText());

        errorContainer.
                shouldBe(visible);
        verifyTestResults(
                testResults,
                verificationEntities.
                        getVerificationEntity(
                                UI_ERROR).
                        getVerificationRules());
    }

    /**
     * Generates {@link VerificationEntities} by provided data
     *
     * @param verificationData Verification data to parse
     * @return {@link VerificationEntities}
     */
    public static VerificationEntities generateVerificationEntities(String verificationData) {
        VerificationEntities verificationEntities =
                new VerificationEntities();

        if(StringUtils.
                isNotBlank(
                        verificationData)) {
            for(String verificationType :
                    verificationData.
                            trim().split(
                            VERIFICATION_TYPES_DELIMITER)) {
                if(StringUtils.
                        isNotBlank(
                                verificationType)) {
                    updateVerificationEntities(
                            verificationEntities,
                            verificationType);
                }
            }
        }

        return verificationEntities;
    }

    /**
     * Performs test results verification
     *
     * @param verificationEntities {@link VerificationEntities}
     * @param testResults {@link Map} of test results
     * @param verificationType {@link VerificationType}
     */
    public static void performTestResultsVerification(VerificationEntities
                                                              verificationEntities,
                                                      Map<String, String>
                                                              testResults,
                                                      VerificationType
                                                              verificationType) {
        if(verificationEntities != null) {
            VerificationEntities.VerificationEntity verificationEntity =
                    verificationEntities.
                            getVerificationEntity(
                                    verificationType);

            if(verificationEntity != null) {
                verifyTestResults(
                        testResults,
                        verificationEntity.
                                getVerificationRules());
            }
        }
    }

    /**
     * Performs API response verification
     *
     * @param apiResponsePayload API response payload
     * @param apiResponseContentType API response content-type
     * @param verificationEntities {@link VerificationEntities}
     */
    public static void performApiResponseVerification(String apiResponsePayload,
                                                      String apiResponseContentType,
                                                      VerificationEntities
                                                              verificationEntities) {
        LOG.info("About to verify the API response ...");

        performTestResultsVerification(
                verificationEntities,
                generateApiTestResults(
                        apiResponsePayload,
                        apiResponseContentType,
                        getApiEvalExpressions(
                                verificationEntities)),
                API_RESPONSE);
    }

    private static void verifyTestResults(Map<String, String>
                                                  testResults,
                                          List<String>
                                                  evalExpressions) {
        try(AutoCloseableSoftAssertions softly =
                    new AutoCloseableSoftAssertions()) {
            evalExpressions.
                    forEach(evalExpression -> {
                        String updatedEvalExpression =
                                updateEvalExpression(
                                        testResults.
                                                entrySet().
                                                stream().
                                                collect(Collectors.toMap(
                                                        Map.Entry::getKey,
                                                        entry ->
                                                                StringUtils.
                                                                        normalizeSpace(
                                                                                entry.getValue()))),
                                        evalExpression);

                        softly.assertThat(
                                evaluateJsToBoolean(updatedEvalExpression)).
                                as(updatedEvalExpression).
                                isTrue();
                    });
        }
    }

    private static void updateVerificationEntities(VerificationEntities
                                                           verificationEntities,
                                                   String verificationType) {
        VerificationEntities.VerificationEntity verificationEntity =
                generateVerificationEntity(
                        verificationType.
                                trim().split(
                                        VERIFICATION_TYPE_DELIMITER));

        if(verificationEntity != null) {
            verificationEntities.
                    addVerificationEntity(
                            verificationEntity);
        }
    }

    private static VerificationEntities.VerificationEntity
            generateVerificationEntity(String[] verificationRules) {
        if(verificationRules != null &&
                verificationRules.length == 2 &&
                getVerificationType(
                        verificationRules[FIRST_ELEMENT]) != null &&
                StringUtils.isNotBlank(
                        verificationRules[SECOND_ELEMENT])) {
            VerificationEntities.VerificationEntity verificationEntity =
                    new VerificationEntities.
                            VerificationEntity().
                            setVerificationType(
                                    getVerificationType(
                                            verificationRules[FIRST_ELEMENT]));

            for(String verificationRule :
                    verificationRules[SECOND_ELEMENT].
                            trim().split(
                                    VERIFICATION_RULES_DELIMITER)) {
                verificationEntity.
                        addVerificationRule(
                                verificationRule.
                                        trim());
            }

            return verificationEntity;
        }

        return null;
    }

    private static String updateEvalExpression(Map<String, String> testResults,
                                               String evalExpression) {
        if(collectionIsNotEmpty(testResults) &&
                StringUtils.
                        isNotBlank(
                                evalExpression)) {
            return new StrSubstitutor(testResults).
                    replace(evalExpression);
        }

        return evalExpression;
    }

    private static Map<String, String> generateApiTestResults(String apiResponsePayload,
                                                              String apiResponseContentType,
                                                              List<String>
                                                                      evalExpressions) {
        Map<String, String> testResults =
                new HashMap<>();

        evalExpressions.
                forEach(evalExpression -> {
                    if(StringUtils.
                            containsIgnoreCase(
                                    apiResponseContentType,
                                    XML_MARK)) {
                        testResults.put(
                                evalExpression,
                                evaluateXPathToString(
                                        evalExpression,
                                        apiResponsePayload));
                    } else {
                        testResults.put(
                                evalExpression,
                                evaluateJsonPathToString(
                                        evalExpression,
                                        apiResponsePayload));
                    }
                });

        return testResults;
    }

    private static List<String> getApiEvalExpressions(VerificationEntities
                                                              verificationEntities) {
        List<String> evalExpressions =
                new ArrayList<>();
        VerificationEntities.VerificationEntity verificationEntity =
                verificationEntities.
                        getVerificationEntity(
                                API_RESPONSE);

        if(verificationEntity != null) {
            verificationEntity.
                    getVerificationRules().
                    forEach(evalExpression ->
                            evalExpressions.addAll(
                                    Arrays.asList(
                                            StringUtils.
                                                    substringsBetween(
                                                            evalExpression,
                                                            TPL_START_SUBSTRING,
                                                            TPL_END_SUBSTRING))));
        }

        return evalExpressions;
    }
}
