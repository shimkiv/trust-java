package com.shimkiv.trust.entities.verification;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.SystemUtils.LINE_SEPARATOR;

/**
 * Verification Entities
 *
 * @author Serhii Shymkiv
 */

public class VerificationEntities implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<VerificationEntity> verificationEntityList =
            new ArrayList<>();

    public List<VerificationEntity> getVerificationEntities() {
        return verificationEntityList;
    }

    public VerificationEntity getVerificationEntity(String verificationType) {
        for(VerificationEntity verificationEntity :
                getVerificationEntities()) {
            if(verificationEntity.
                    getVerificationType().
                    equalsIgnoreCase(
                            verificationType)) {
                return verificationEntity;
            }
        }

        return null;
    }

    public VerificationEntities setVerificationEntities(List<VerificationEntity>
                                                                verificationEntities) {
        this.verificationEntityList =
                verificationEntities;

        return this;
    }

    public VerificationEntities addVerificationEntity(VerificationEntity
                                                              verificationEntity) {
        this.verificationEntityList.
                add(verificationEntity);

        return this;
    }

    @Override
    public String toString() {
        return "Verification Entities [" +
                LINE_SEPARATOR +
                StringUtils.join(
                        getVerificationEntities(),
                        LINE_SEPARATOR) +
                LINE_SEPARATOR +
                "]";
    }

    public static class VerificationEntity implements Serializable {
        private static final long serialVersionUID = 1L;

        private String verificationType = null;
        private List<String> verificationRules =
                new ArrayList<>();

        public String getVerificationType() {
            return verificationType;
        }

        public VerificationEntity setVerificationType(String verificationType) {
            this.verificationType =
                    verificationType;

            return this;
        }

        public List<String> getVerificationRules() {
            return verificationRules;
        }

        public VerificationEntity setVerificationRules(List<String>
                                                               verificationRules) {
            this.verificationRules =
                    verificationRules;

            return this;
        }

        public VerificationEntity addVerificationRule(String verificationRule) {
            this.verificationRules.
                    add(verificationRule);

            return this;
        }

        @Override
        public String toString() {
            return "Verification Entity [" +
                    StringUtils.join(
                            new String[] {
                                    getVerificationType(),
                                    String.valueOf(
                                            getVerificationRules())
                            }, ',') +
                    "]";
        }
    }
}
