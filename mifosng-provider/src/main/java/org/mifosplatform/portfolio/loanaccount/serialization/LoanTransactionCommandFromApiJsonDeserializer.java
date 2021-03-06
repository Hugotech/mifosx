package org.mifosplatform.portfolio.loanaccount.serialization;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.serialization.AbstractFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.serialization.FromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.loanaccount.command.LoanTransactionCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Implementation of {@link FromApiJsonDeserializer} for
 * {@link LoanTransactionCommand}'s.
 */
@Component
public final class LoanTransactionCommandFromApiJsonDeserializer extends AbstractFromApiJsonDeserializer<LoanTransactionCommand> {

    /**
     * The parameters supported for this command.
     */
    final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("transactionDate", "transactionAmount", "note", "dateFormat",
            "locale" ,"status", "closedOnDate", "writtenOffOnDate", "rescheduledOnDate"));

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public LoanTransactionCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    @Override
    public LoanTransactionCommand commandFromApiJson(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final JsonElement element = fromApiJsonHelper.parse(json);
        final LocalDate transactionDate = fromApiJsonHelper.extractLocalDateNamed("transactionDate", element);
        final BigDecimal transactionAmount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("transactionAmount", element);
        final String note = fromApiJsonHelper.extractStringNamed("note", element);

        return new LoanTransactionCommand(transactionDate, transactionAmount, note);
    }
}