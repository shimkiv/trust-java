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

package com.shimkiv.trust.common;

import com.codeborne.selenide.SelenideElement;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static com.shimkiv.trust.config.TrustConfig.BODY_TAG_NAME;

/**
 * Common Utils
 *
 * @author Serhii Shymkiv
 */

public class CommonUtils {
    private static final Logger LOG =
            Logger.getLogger(
                    CommonUtils.class.
                            getName());

    // Prevents instantiation
    private CommonUtils() {}

    /**
     * Parses {@link Boolean} of provided {@link Object}
     *
     * @param object {@link Object} to parse
     * @return {@link Boolean} of provided {@link Object}
     */
    public static boolean parseBoolean(Object object) {
        return object != null &&
                (boolean) object;
    }

    /**
     * Checks whether the collection is empty
     *
     * @param collection Collection
     * @return Whether the collection is empty
     */
    public static <T> boolean collectionIsEmpty(T collection) {
        if(collection != null) {
            if(Map.class.isAssignableFrom(
                    collection.
                            getClass())) {
                return ((Map) collection).isEmpty();
            } else if(List.class.isAssignableFrom(
                    collection.
                            getClass())) {
                return ((List) collection).isEmpty();
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * Checks whether the collection is not empty
     *
     * @param collection Collection
     * @return Whether the collection is not empty
     */
    public static <T> boolean collectionIsNotEmpty(T collection) {
        return !collectionIsEmpty(collection);
    }

    /**
     * Confirms alert/confirm/prompt dialog quietly
     */
    public static void confirmAlertQuietly() {
        try {
            confirm(null);
        } catch (Exception e) {
            LOG.info("No alert is present. " +
                    "Expected behavior for the current method invocation !");
        }
    }

    /**
     * Checks whether the alert/confirm/prompt dialog is present on the page
     */
    public static boolean alertIsPresent() {
        try {
            switchTo().
                    alert();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets parent container of elements by label
     *
     * @param label Label of container
     * @return Parent container of elements
     */
    public static SelenideElement getParentContainer(String label) {
        if(StringUtils.isNotBlank(label)) {
            for(SelenideElement element :
                    $$(withText(label))) {
                if(element.isDisplayed()) {
                    return element.
                            parent();
                }
            }
        }

        return $(BODY_TAG_NAME);
    }
}
