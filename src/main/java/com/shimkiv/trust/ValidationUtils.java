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

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Logger;

/**
 * Validation Utils
 *
 * @author Serhii Shymkiv
 */

public class ValidationUtils {
    private static final Logger LOG =
            Logger.getLogger(
                    ValidationUtils.class.
                            getName());

    // Prevents instantiation
    private ValidationUtils() {}

    /**
     * Validates provided XML API response against the XSD {@link Schema}
     *
     * @param schema W3C XML {@link Schema} instance
     * @param xmlApiResponsePayload XML API response payload
     */
    public static void validateApiResponseAgainstXsd(Schema schema,
                                                     String xmlApiResponsePayload)
            throws ParserConfigurationException, SAXException, IOException {
        if(StringUtils.
                isNotBlank(
                        xmlApiResponsePayload)) {
            LOG.info("About to validate the API response against the XSD Schema ...");

            DocumentBuilderFactory documentBuilderFactory =
                    DocumentBuilderFactory.
                            newInstance();
            documentBuilderFactory.
                    setNamespaceAware(true);

            schema.newValidator().
                    validate(
                            new DOMSource(
                                    documentBuilderFactory.
                                            newDocumentBuilder().
                                            parse(new InputSource(
                                                    new StringReader(
                                                            xmlApiResponsePayload)))));
        }
    }
}
