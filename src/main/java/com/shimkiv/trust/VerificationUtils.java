package com.shimkiv.trust;

import com.shimkiv.trust.entities.verification.VerificationEntities;
import com.shimkiv.trust.enums.VerificationType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.assertj.core.api.AutoCloseableSoftAssertions;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.shimkiv.trust.common.CommonUtils.collectionIsNotEmpty;
import static com.shimkiv.trust.config.TrustConfig.*;
import static com.shimkiv.trust.enums.VerificationType.API_RESPONSE;
import static com.shimkiv.trust.enums.VerificationType.getVerificationType;
import static com.shimkiv.trust.evaluation.EvaluationUtils.*;

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

    /**
     * Performs test results verification
     *
     * @param testResults {@link Map} of test results
     * @param evalExpressions {@link List} of evaluation expressions
     */
    public static void verifyTestResults(Map<String, String>
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
