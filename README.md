[![Build Status](https://travis-ci.org/shimkiv/trust-java.svg?branch=master)](https://travis-ci.org/shimkiv/trust-java)
[![Coverage Status](https://coveralls.io/repos/github/shimkiv/trust-java/badge.svg?branch=master)](https://coveralls.io/github/shimkiv/trust-java?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.shimkiv/trust-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.shimkiv/trust-java)
[ ![Download](https://api.bintray.com/packages/shimkiv/maven/trust-java/images/download.svg) ](https://bintray.com/shimkiv/maven/trust-java/_latestVersion)
[![Gratipay User](https://img.shields.io/gratipay/user/shimkiv.svg)](https://gratipay.com/trust-java/)
[![MIT License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/shimkiv/trust-java/blob/master/LICENSE)
![Free](https://img.shields.io/badge/free-open--source-green.svg)

# TRUST - Test Results Verification library for Java

The `TRUST`'s primary goal is to provide the simple way of different test results verification.

## General description

Usually during or at the end of some test scenario you want to verify that the application under the tests works as expected. 
Sometimes it is enough to use simple assertions chain but sometimes you have to implement the complex verification logic in order to check the test scenario(s) results.  
And this is the exact that stage where the `TRUST` might be useful since it brings some benefits:
* Unified test results verification approach for different testing types (e.g. `UI`, `API`)
* No need in sources recompilation after the `verification rules` updates (unless you store these rules inside the compilation units)
* `XPath` and `JSONPath` selectors support (for extracting the `API` response nodes value and using them during the further verification stages)
* Simple yet powerful `verification rules` specification (anyone with the basic knowledge of the `XPath`, `JSONPath`, [JS comparison and logical operators](https://www.w3schools.com/js/js_comparisons.asp) can write these rules)

As it was mentioned above, the `TRUST` is based on the user defined `verification rules` which are the subject for the further processing by the built-in `JavaScript` engine (the `Nashorn` by default).  

The `verification rules` are represented by the `String` and they consist of the following:
* `Verification type` (can be any `String` value (please check the list of [predefined](https://github.com/shimkiv/trust-java/blob/master/src/main/java/com/shimkiv/trust/enums/VerificationType.java) types) or `API_RESPONSE`)
* ':&nbsp;&nbsp;' symbols (note: colon and two spaces (by default), without quotes)
* `JS` expression(s) which should be evaluated into the `Boolean`'s `TRUE`
    * `JS` expressions should be separated by using the ';&nbsp;&nbsp;' symbols (note: semicolon and two spaces (by default), without quotes)
    * `JS` expressions are able to utilize the [Lodash](https://lodash.com/) `JS` library (by default) extra functionality
    * `JS` expressions should contain placeholders/templates (depending on chosen Verification Type) which will be substituted with the test result values during the expressions evaluation
        * For the `UI` tests the placeholders/templates are represented by the entities on the page of the test results, value of which you want to use in the `JS` expression
        * For the `API` tests the placeholders/templates are represented by the [XPath](https://www.w3.org/TR/xpath/) / [JSONPath](http://goessner.net/articles/JsonPath/), [JSONPath](https://github.com/json-path/JsonPath) expressions
* Verification Types should be separated by using the `|&|` symbols
* Verification rules are processed [softly](https://joel-costigliola.github.io/assertj/assertj-core-features-highlight.html)


Please note that the substituted value(s) of the placeholders/templates will be [normalized](https://www.w3.org/TR/xpath/#function-normalize-space) before use.  


You can override the defaults by using corresponding system properties. Please refer to the [TrustConfig](https://github.com/shimkiv/trust-java/blob/master/src/main/java/com/shimkiv/trust/config/TrustConfig.java) file (the `TRUST settings` section).

### Examples

```javascript
//
// The UI verification rules examples
//

// Verifies that the "Status" field value of the test results page will contain "DONE" text;
UI_COMMON:  _.includes("${Status:}", "DONE")

// Verifies that the error message of the test results page will contain "System Error" or "Was not able to perform requested action" text;
UI_ERROR:  _.includes("${ERROR_MSG}", "System Error") || _.includes("${ERROR_MSG}", "Was not able to perform requested action")

// Verifies that the alert text will not contain "TestMePlease" text;
UI_ALERT:  !_.includes("${ALERT_MSG}", "TestMePlease")

// Verifies that the "Amount" field value of the test results page will be greater than 0 and less than 100
MY_RULE:  _.gt(${Amount:}, 0) && _.lt(${Amount:}, 100)

// Together
UI_COMMON:  _.includes("${Status:}", "DONE")|&|MY_RULE:  _.gt(${Amount:}, 0) && _.lt(${Amount:}, 100)

// 
// The API verification rules examples
//

// Verifies that the API response node of the executed request will contain "Valid response" text;
// ${$.description} or ${/Response/description} placeholders will be replaced with the real value of the <Response> -> <description> response node (JSON and XML content-type respectively);
API_RESPONSE:  _.includes("${$.description}", "Valid response")
API_RESPONSE:  _.includes("${/Response/description}", "Valid response")
```

#### UI test results verification

Suppose you have the next `HTML` table on the page as the results of some `UI` test activities:

| Field Name | Value |
| --- | --- |
| Field1: | data1 |
| Field2: | data2 |
| Description: | Description |
| Status: | DONE |

Suppose also you have the next `verification rule` for this particular test:  
`UI_COMMON:  _.includes("${Status:}", "DONE")`  

In this case your job will be to parse the `HTML` table into the `key-value` pairs (the `Map`) and invoke the `VerificationUtils.performTestResultsVerification(...)` method with this `Map` as the `testResults` parameter.  

During the `verification` procedure the `${Status:}` template will be substituted with the `DONE` value.    
After this activities the resulting expression to verify will be `_.includes("DONE", "DONE")` <=> [Lodash syntax](https://lodash.com/) + `JS` expression(s) evaluation <=> and the current test will be marked as passed since the "DONE" string includes/contains the "DONE" string.

#### API test results verification

Suppose you have the `API` test which will produce the next response payload:

```xml
<Response>
    <node1>1</node1>
    <node2>2</node2>
    <node3>3</node3>
    <description>Valid response</description>
    <node5>5</node5>
</Response>
```

Suppose also you have the next `verification rule` for this particular test:  
`API_RESPONSE:  _.includes("${/Response/description}", "Valid response")`  

So the `/Response/description` `XPath` expression will be evaluated into the `Valid response` value for you and will be substituted instead of the `${/Response/description}` template  
After this activities the resulting expression to verify will be `_.includes("Valid response", "Valid response")` <=> [Lodash syntax](https://lodash.com/) + `JS` expression(s) evaluation <=> and the current test will be marked as passed since the "Valid response" string includes/contains the "Valid response" string.

## Basic usage

Let's say that for some particular `API` test scenario you're using the [DataProvider](http://testng.org/doc/documentation-main.html#parameters-dataproviders) implemented as [Iterator](https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html) which consumes the test data from the `CSV` file.  

In this case you can add the new column into the `CSV` file which will hold the test results `verification rules`:  

| ... | VERIFICATION_RULES |
| :---: | ------------------ |
| ... | API_RESPONSE:  _.includes("${/Response/description}", "Valid response") |

And then you can invoke the corresponding method from your test:

```java
package my.test;

import static com.shimkiv.trust.ValidationUtils.*;
import static com.shimkiv.trust.VerificationUtils.*;

public class MyApiTests {
        @Test
        public void apiResponseTest(int testDataIndex,
                                    Map<String, String> testData) {
            String httpResponsePayload;
            String httpResponseContentType;
            
            try {
                // do some test activities 
                // in order to receive 
                // the API response payload and content-type
            } catch (Exception e) {
                // process exceptions if required
            } finally {
                // for the XML payloads only !
                validateApiResponseAgainstXsd(
                        myApiXsd, 
                        httpResponsePayload);
                
                performApiResponseVerification(
                        httpResponsePayload,
                        httpResponseContentType,
                        generateVerificationEntities(
                                testData.
                                    get(VERIFICATION_RULES_NODE_NAME)));
            }
        }
}
```

The [Schema](https://docs.oracle.com/javase/8/docs/api/javax/xml/validation/Schema.html) itself can be initialized (if required) something like this:
```java
package my.config;

// imports

public class MyApiConfig {
        private static Schema myApiXsd = null;
        
        static {
            LOG.info("API Schemas initialization ...");
            
            myApiSchemaInit();
        }
        
        // getters/setters/etc.
        
        private static void myApiSchemaInit() {
            try {
                myApiXsd =
                        SchemaFactory.
                                newInstance(W3C_XML_SCHEMA_NS_URI).
                                newSchema(
                                        new StreamSource(
                                                findFileInClassPath(
                                                        COMMON_CONFIG_DIR,
                                                        MY_API_XSD_FILE_NAME)));
            } catch (Exception e) {
                LOG.debug(COMMON_ERROR_MESSAGE, e);
            }
        }
}
```

## More examples

The similar approach can be used in order to write your own verification methods like:

```java
package my.utils;

import static com.shimkiv.trust.VerificationUtils.*;

public class MyVerificationUtils {
        private static final Logger LOG;
        
        /**
         * Performs UI alert message text verification
         * 
         * For verification rule like: 
         * UI_ALERT:  !_.includes("${ALERT_MSG}", "TestMePlease")
         * 
         * @param verificationEntities {@link VerificationEntities}
         */
        public static void performUiAlertTextVerification(VerificationEntities 
                                                                verificationEntities) {
            LOG.info("About to verify the UI alert text ...");
    
            assertThat(alertIsPresent()).
                    isTrue();
    
            Map<String, String> testResults =
                    new HashMap<>();
    
            testResults.put(
                    "ALERT_MSG",
                    switchTo().
                            alert().
                            getText());
    
            verifyTestResults(
                    testResults,                                                              
                    verificationEntities.
                            getVerificationEntity(
                                    UI_ALERT.
                                        name()).
                            getVerificationRules());
    
            if(ALERTS_AUTO_CONFIRM) {
                confirmAlertQuietly();
            }
        }
    
        /**
         * Performs UI error message text verification
         * 
         * For verification rule like: 
         * UI_ERROR:  _.includes("${ERROR_MSG}", "System Error") || _.includes("${ERROR_MSG}", "Was not able to perform requested action")
         *
         * @param verificationEntities {@link VerificationEntities}
         * @param errorContainerLabel The displayed label of the error container
         */
        public static void performUiErrorMessageVerification(VerificationEntities 
                                                                    verificationEntities,
                                                             String errorContainerLabel) {
            LOG.info("About to verify the UI error ...");
    
            SelenideElement errorContainer =
                    getParentContainer(
                            errorContainerLabel);
            Map<String, String> testResults =
                    new HashMap<>();
    
            testResults.put(
                    "ERROR_MSG",
                    errorContainer.
                            getText());
    
            errorContainer.
                    shouldBe(visible);
            verifyTestResults(
                    testResults,
                    verificationEntities.
                            getVerificationEntity(
                                    UI_ERROR.
                                        name()).
                            getVerificationRules());
        }
}
```

## Usage with Maven (Not ready yet)

You can import the dependency of the `TRUST` into your `pom.xml` from the [Maven Central](https://maven-badges.herokuapp.com/maven-central/com.shimkiv/trust-java) repository:

```xml
<dependency>
    <groupId>com.shimkiv</groupId>
    <artifactId>trust</artifactId>
    <version>LATEST</version>
</dependency>
```

* And then you can import the following methods into your code:  
    * `import static com.shimkiv.trust.VerificationUtils.*;`  
    * Optional (for the API responses validation against the XSD schema):  
        * `import static com.shimkiv.trust.ValidationUtils.*;`

## Changelog
[CHANGELOG](https://github.com/shimkiv/trust-java/blob/master/CHANGELOG)

## Build from sources

```bash
git clone https://github.com/shimkiv/trust-java.git
cd trust-java
mvn clean package
# or
mvn clean install
```

## Authors

`TRUST` was originally designed and developed by [Serhii Shymkiv](mailto:sergey@shimkiv.com) in 2017

## License
`TRUST` is open-source project and distributed under the [MIT](https://choosealicense.com/licenses/mit/) license
