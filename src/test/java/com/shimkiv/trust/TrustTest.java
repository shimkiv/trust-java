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

import com.shimkiv.trust.entities.verification.VerificationEntities;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.shimkiv.trust.ValidationUtils.validateApiResponseAgainstXsd;
import static com.shimkiv.trust.VerificationUtils.*;
import static com.shimkiv.trust.enums.VerificationType.UI_ALERT;
import static com.shimkiv.trust.enums.VerificationType.UI_COMMON;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * TRUST Tests
 *
 * @author Serhii Shymkiv
 */

public class TrustTest {
    private static final Logger LOG =
            Logger.getLogger(
                    TrustTest.class.
                            getName());

    private static Schema myApiSchema =
            myApiSchemaInit();

    private static final String XML_CONTENT_TYPE =
            "application/xml";
    private static final String JSON_CONTENT_TYPE =
            "application/json";

    private static final String XML_RESPONSE_PAYLOAD =
                    "<Response>\n" +
                    "    <node1>1</node1>\n" +
                    "    <node2>2</node2>\n" +
                    "    <node3>3</node3>\n" +
                    "    <description>Valid response</description>\n" +
                    "    <node5>5</node5>\n" +
                    "</Response>";
    private static final String JSON_RESPONSE_PAYLOAD =
                    "{\n" +
                    "    \"node1\": \"1\",\n" +
                    "    \"node2\": \"2\",\n" +
                    "    \"node3\": \"3\",\n" +
                    "    \"description\": \"Valid response\",\n" +
                    "    \"node5\": \"5\"\n" +
                    "}";

    private static final String MY_API_XSD =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">\n" +
                    "  <xs:element name=\"Response\">\n" +
                    "    <xs:complexType>\n" +
                    "      <xs:sequence>\n" +
                    "        <xs:element name=\"node1\" type=\"xs:string\" />\n" +
                    "        <xs:element name=\"node2\" type=\"xs:string\" />\n" +
                    "        <xs:element name=\"node3\" type=\"xs:string\" />\n" +
                    "        <xs:element name=\"description\" type=\"xs:string\" />\n" +
                    "        <xs:element name=\"node5\" type=\"xs:string\" />\n" +
                    "      </xs:sequence>\n" +
                    "    </xs:complexType>\n" +
                    "  </xs:element>\n" +
                    "</xs:schema>";

    private static final String UI_COMMON_RULES =
            "UI_COMMON:  _.includes(\"${Status:}\", \"DONE\");  " +
                    "!_.includes(\"${SomeField:}\", \"DONE\")";
    private static final String UI_ALERT_RULES =
            "UI_ALERT:  _.includes(\"${ALERT_MSG}\", \"TestMePlease\")";
    private static final String XML_API_RESPONSE_SUCCESS_RULES =
            "API_RESPONSE:  _.includes(\"${/Response/description}\", \"Valid response\")";
    private static final String JSON_API_RESPONSE_SUCCESS_RULES =
            "API_RESPONSE:  _.includes(\"${$.description}\", \"Valid response\")";
    private static final String XML_API_RESPONSE_FAIL_RULES =
            "API_RESPONSE:  _.includes(\"${/Response/description}\", \"InValid response\")";
    private static final String JSON_API_RESPONSE_FAIL_RULES =
            "API_RESPONSE:  _.includes(\"${$.description}\", \"InValid response\")";

    @Test
    public void generateVerificationEntitiesTest() {
        VerificationEntities verificationEntities =
                generateVerificationEntities(null);

        assertThat(verificationEntities).
                isNotEqualTo(null);
        assertThat(verificationEntities.
                getVerificationEntities()).
                hasSize(0);
        assertThat(verificationEntities.
                getVerificationEntity(
                        UI_COMMON)).
                isEqualTo(null);

        verificationEntities =
                generateVerificationEntities(
                        UI_COMMON_RULES);

        assertThat(verificationEntities).
                isNotEqualTo(null);
        assertThat(verificationEntities.
                getVerificationEntities()).
                hasSize(1);
        assertThat(verificationEntities.
                getVerificationEntity(
                        UI_COMMON)).
                isNotEqualTo(null);
    }

    @Test
    public void performTestResultsVerificationSuccessTest() {
        Map<String, String> testResults =
                new HashMap<>();

        testResults.
                put("SomeField:",
                        "TestMe");
        testResults.
                put("Status:",
                        "DONE");

        performTestResultsVerification(
                generateVerificationEntities(
                        UI_COMMON_RULES),
                testResults,
                UI_COMMON);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void performTestResultsVerificationFailTest() {
        Map<String, String> testResults =
                new HashMap<>();

        testResults.
                put("SomeField:",
                        "TestMe");
        testResults.
                put("Status:",
                        "QWE");

        performTestResultsVerification(
                generateVerificationEntities(
                        UI_COMMON_RULES),
                testResults,
                UI_COMMON);
    }

    @Test
    public void performApiResponseVerificationSuccessTest() {
        performApiResponseVerification(
                XML_RESPONSE_PAYLOAD,
                XML_CONTENT_TYPE,
                generateVerificationEntities(
                        XML_API_RESPONSE_SUCCESS_RULES));

        performApiResponseVerification(
                JSON_RESPONSE_PAYLOAD,
                JSON_CONTENT_TYPE,
                generateVerificationEntities(
                        JSON_API_RESPONSE_SUCCESS_RULES));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void performApiResponseVerificationFailXmlTest() {
        performApiResponseVerification(
                XML_RESPONSE_PAYLOAD,
                XML_CONTENT_TYPE,
                generateVerificationEntities(
                        XML_API_RESPONSE_FAIL_RULES));
    }

    @Test(expectedExceptions = AssertionError.class)
    public void performApiResponseVerificationFailJsonTest() {
        performApiResponseVerification(
                JSON_RESPONSE_PAYLOAD,
                JSON_CONTENT_TYPE,
                generateVerificationEntities(
                        JSON_API_RESPONSE_FAIL_RULES));
    }

    @Test
    public void verifyTestResultsSuccessTest() {
        Map<String, String> testResults =
                new HashMap<>();

        testResults.put(
                "ALERT_MSG",
                "TestMePlease");

        verifyTestResults(
                testResults,
                generateVerificationEntities(
                        UI_ALERT_RULES).
                        getVerificationEntity(
                                UI_ALERT).
                        getVerificationRules());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void verifyTestResultsFailTest() {
        Map<String, String> testResults =
                new HashMap<>();

        testResults.put(
                "ALERT_MSG",
                "QWE");

        verifyTestResults(
                testResults,
                generateVerificationEntities(
                        UI_ALERT_RULES).
                        getVerificationEntity(
                                UI_ALERT).
                        getVerificationRules());
    }

    @Test
    public void validateApiResponseAgainstXsdSuccessTest() {
        try {
            validateApiResponseAgainstXsd(
                    myApiSchema,
                    XML_RESPONSE_PAYLOAD);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    @BeforeMethod
    private void actionsBeforeTest(Method method) {
        LOG.info(">>> @TEST \"" + method.getName() + "\" entry point");
    }

    @AfterMethod
    private void actionsAfterTest(Method method) {
        LOG.info("<<< @TEST \"" + method.getName() + "\" end point");
    }

    private static Schema myApiSchemaInit() {
        try {
            return SchemaFactory.
                    newInstance(W3C_XML_SCHEMA_NS_URI).
                    newSchema(
                            new StreamSource(
                                    new StringReader(
                                            MY_API_XSD)));
        } catch (Exception e) {
            LOG.warning("Impossible to initialize the Schema !");
        }

        return null;
    }
}
