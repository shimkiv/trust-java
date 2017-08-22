package com.shimkiv.trust.evaluation;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.xml.sax.InputSource;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Logger;

import static com.jayway.jsonpath.Option.ALWAYS_RETURN_LIST;
import static com.jayway.jsonpath.Option.SUPPRESS_EXCEPTIONS;
import static com.shimkiv.trust.common.CommonUtils.collectionIsNotEmpty;
import static com.shimkiv.trust.common.CommonUtils.parseBoolean;
import static com.shimkiv.trust.config.TrustConfig.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Evaluation Utils
 *
 * @author Serhii Shymkiv
 */

public class EvaluationUtils {
    private static final Logger LOG =
            Logger.getLogger(
                    EvaluationUtils.class.
                            getName());

    private static final ScriptEngine SCRIPT_ENGINE =
            new ScriptEngineManager().
                    getEngineByName(
                            SCRIPT_ENGINE_NAME);

    static {
        SCRIPTS_TO_LOAD.
                forEach(
                        script -> {
                            try {
                                SCRIPT_ENGINE.eval(
                                        MessageFormat.format(
                                                LOAD_SCRIPT_TPL_EXPR,
                                                script));
                            } catch (Exception e) {
                                LOG.config(SCRIPT_EVAL_ERROR_MESSAGE);
                            }
                        });
    }

    // Prevents instantiation
    private EvaluationUtils() {}

    /**
     * Evaluates provided JS expression into the {@link Boolean}
     *
     * @param evalExpression Expression to evaluate
     * @return Evaluation result
     */
    public static boolean evaluateJsToBoolean(String evalExpression) {
        try {
            return parseBoolean(
                    SCRIPT_ENGINE.
                            eval(evalExpression.
                                    endsWith(COLON_MARK) ?
                                    evalExpression :
                                    evalExpression +
                                            COLON_MARK));
        } catch (Exception e) {
            LOG.warning(COMMON_EVAL_ERROR_MESSAGE);
        }

        return false;
    }

    /**
     * Evaluates provided XPath expression into the {@link String}
     *
     * @param evalExpression Expression to evaluate
     * @param xmlSource XML source
     * @return Evaluation result
     */
    public static String evaluateXPathToString(String evalExpression,
                                               String xmlSource) {
        try {
            return XPathFactory.
                    newInstance().
                    newXPath().
                    evaluate(
                            evalExpression,
                            DocumentBuilderFactory.
                                    newInstance().
                                    newDocumentBuilder().
                                    parse(new InputSource(
                                            new StringReader(
                                                    xmlSource))));
        } catch (Exception e) {
            LOG.warning(COMMON_EVAL_ERROR_MESSAGE);
        }

        return EMPTY;
    }

    /**
     * Evaluates provided JSONPath expression into the {@link String}
     *
     * @param evalExpression Expression to evaluate
     * @param jsonSource JSON source
     * @return Evaluation result
     */
    public static String evaluateJsonPathToString(String evalExpression,
                                                  String jsonSource) {
        try {
            List<Object> parsedData =
                    JsonPath.
                            using(Configuration.
                                    defaultConfiguration().
                                    addOptions(
                                            ALWAYS_RETURN_LIST,
                                            SUPPRESS_EXCEPTIONS)).
                            parse(jsonSource).
                            read(evalExpression);

            return collectionIsNotEmpty(parsedData) ?
                    String.valueOf(
                            parsedData.
                                    get(FIRST_ELEMENT)) :
                    EMPTY;
        } catch (Exception e) {
            LOG.warning(COMMON_EVAL_ERROR_MESSAGE);
        }

        return EMPTY;
    }
}
