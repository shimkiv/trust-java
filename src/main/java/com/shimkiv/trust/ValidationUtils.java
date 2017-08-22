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
