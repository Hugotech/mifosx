package org.mifosplatform.portfolio.billingproduct;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.exception.UnsupportedParameterException;
import org.mifosplatform.portfolio.address.command.AddressCommand;
import org.mifosplatform.portfolio.adjustment.commands.AdjustmentCommand;
import org.mifosplatform.portfolio.billingcycle.command.BillingCycleCommand;
import org.mifosplatform.portfolio.billingmaster.command.BillMasterCommand;
import org.mifosplatform.portfolio.charge.commands.ChargeCodeCommand;
import org.mifosplatform.portfolio.client.serialization.ClientCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.discountmaster.commands.DiscountMasterCommand;
import org.mifosplatform.portfolio.item.command.ItemCommand;
import org.mifosplatform.portfolio.onetimesale.command.OneTimeSaleCommand;
import org.mifosplatform.portfolio.order.command.OrdersCommand;
import org.mifosplatform.portfolio.payment.command.Paymentcommand;
import org.mifosplatform.portfolio.paymodes.commands.PaymodeCommand;
import org.mifosplatform.portfolio.payterms.commands.PaytermsCommand;
import org.mifosplatform.portfolio.plan.commands.PlansCommand;
import org.mifosplatform.portfolio.pricing.commands.PricingCommand;
import org.mifosplatform.portfolio.servicemaster.commands.ServiceMasterCommand;
import org.mifosplatform.portfolio.servicemaster.commands.ServicesCommand;
import org.mifosplatform.portfolio.subscription.commands.SubscriptionCommand;
import org.mifosplatform.portfolio.taxmaster.commands.TaxMappingRateCommand;
import org.mifosplatform.portfolio.taxmaster.commands.TaxMasterCommand;
import org.mifosplatform.portfolio.ticketmaster.command.TicketMasterCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.number.NumberFormatter;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

@Service
public class PortfolioApiBillingDataConversionServiceImpl implements
		PortfolioApiDataBillingConversionService {

	/**
	 * Google-gson class for converting to and from json.
	 */
	private final Gson gsonConverter;

	private final ClientCommandFromApiJsonDeserializer clientCommandFromApiJsonDeserializer;

	@Autowired
	public PortfolioApiBillingDataConversionServiceImpl(
			final ClientCommandFromApiJsonDeserializer clientCommandFromApiJsonDeserializer) {
		this.clientCommandFromApiJsonDeserializer = clientCommandFromApiJsonDeserializer;
		this.gsonConverter = new Gson();
	}

	private void checkForUnsupportedParameters(Map<String, ?> requestMap,
			Set<String> supportedParams) {
		List<String> unsupportedParameterList = new ArrayList<String>();
		for (String providedParameter : requestMap.keySet()) {
			if (!supportedParams.contains(providedParameter)) {
				unsupportedParameterList.add(providedParameter);
			}
		}

		if (!unsupportedParameterList.isEmpty()) {
			throw new UnsupportedParameterException(unsupportedParameterList);
		}
	}

	private String extractStringParameter(final String paramName,
			final Map<String, ?> requestMap,
			final Set<String> modifiedParameters) {
		String paramValue = null;
		if (requestMap.containsKey(paramName)) {
			paramValue = (String) requestMap.get(paramName);
			modifiedParameters.add(paramName);
		}

		if (paramValue != null) {
			paramValue = paramValue.trim();
		}

		return paramValue;
	}

	private Integer extractIntegerParameter(final String paramName,
			final Map<String, ?> requestMap,
			final Set<String> modifiedParameters) {
		Integer paramValue = null;
		if (requestMap.containsKey(paramName)) {
			String valueAsString = (String) requestMap.get(paramName);
			paramValue = convertToInteger(valueAsString, paramName,
					extractLocaleValue(requestMap));
			modifiedParameters.add(paramName);
		}
		return paramValue;
	}

	private BigDecimal extractBigDecimalParameter(final String paramName,
			final Map<String, ?> requestMap,
			final Set<String> modifiedParameters) {
		BigDecimal paramValue = null;
		if (requestMap.containsKey(paramName)) {
			String valueAsString = (String) requestMap.get(paramName);
			paramValue = convertFrom(valueAsString, paramName,
					extractLocaleValue(requestMap));
			modifiedParameters.add(paramName);
		}
		return paramValue;
	}

	private boolean extractBooleanParameter(final String paramName,
			final Map<String, ?> requestMap,
			final Set<String> modifiedParameters) {
		boolean paramValue = false;
		String paramValueAsString = null;
		if (requestMap.containsKey(paramName)) {
			paramValueAsString = (String) requestMap.get(paramName);

			if (paramValueAsString != null) {
				paramValueAsString = paramValueAsString.trim();
			}

			paramValue = Boolean.valueOf(paramValueAsString);
			modifiedParameters.add(paramName);
		}
		return paramValue;
	}

	private Long extractLongParameter(final String paramName,
			final Map<String, ?> requestMap,
			final Set<String> modifiedParameters) {
		Long paramValue = null;
		if (requestMap.containsKey(paramName)) {
			String valueAsString = (String) requestMap.get(paramName);
			if (StringUtils.isNotBlank(valueAsString)) {
				paramValue = Long.valueOf(Double.valueOf(valueAsString)
						.longValue());
			}
			modifiedParameters.add(paramName);
		}
		return paramValue;
	}

	private LocalDate extractLocalDateParameter(final String paramName,
			final Map<String, ?> requestMap,
			final Set<String> modifiedParameters) {
		LocalDate paramValue = null;
		if (requestMap.containsKey(paramName)) {
			String valueAsString = (String) requestMap.get(paramName);
			if (StringUtils.isNotBlank(valueAsString)) {
				final String dateFormat = (String) requestMap.get("dateFormat");
				final Locale locale = new Locale(
						(String) requestMap.get("locale"));
				paramValue = convertFrom(valueAsString, paramName, dateFormat,
						locale);
			}
			modifiedParameters.add(paramName);
		}
		return paramValue;
	}

	private Locale extractLocaleValue(Map<String, ?> requestMap) {
		Locale clientApplicationLocale = null;
		String locale = null;
		if (requestMap.containsKey("locale")) {
			locale = (String) requestMap.get("locale");
			clientApplicationLocale = localeFromString(locale);
		}
		return clientApplicationLocale;
	}

	private LocalDate convertFrom(final String dateAsString,
			final String parameterName, final String dateFormat,
			final Locale clientApplicationLocale) {

		if (StringUtils.isBlank(dateFormat) || clientApplicationLocale == null) {

			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			if (StringUtils.isBlank(dateFormat)) {
				String defaultMessage = new StringBuilder(
						"The parameter '"
								+ parameterName
								+ "' requires a 'dateFormat' parameter to be passed with it.")
						.toString();
				ApiParameterError error = ApiParameterError.parameterError(
						"validation.msg.missing.dateFormat.parameter",
						defaultMessage, parameterName);
				dataValidationErrors.add(error);
			}
			if (clientApplicationLocale == null) {
				String defaultMessage = new StringBuilder(
						"The parameter '"
								+ parameterName
								+ "' requires a 'locale' parameter to be passed with it.")
						.toString();
				ApiParameterError error = ApiParameterError.parameterError(
						"validation.msg.missing.locale.parameter",
						defaultMessage, parameterName);
				dataValidationErrors.add(error);
			}
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}

		LocalDate eventLocalDate = null;
		if (StringUtils.isNotBlank(dateAsString)) {
			try {
				// Locale locale = LocaleContextHolder.getLocale();
				eventLocalDate = DateTimeFormat
						.forPattern(dateFormat)
						.withLocale(clientApplicationLocale)
						.parseLocalDate(
								dateAsString
										.toLowerCase(clientApplicationLocale));
			} catch (IllegalArgumentException e) {
				List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
				ApiParameterError error = ApiParameterError.parameterError(
						"validation.msg.invalid.date.format", "The parameter "
								+ parameterName
								+ " is invalid based on the dateFormat: '"
								+ dateFormat + "' and locale: '"
								+ clientApplicationLocale + "' provided:",
						parameterName, dateAsString, dateFormat);
				dataValidationErrors.add(error);

				throw new PlatformApiDataValidationException(
						"validation.msg.validation.errors.exist",
						"Validation errors exist.", dataValidationErrors);
			}
		}

		return eventLocalDate;
	}

	private Integer convertToInteger(final String numericalValueFormatted,
			final String parameterName, final Locale clientApplicationLocale) {

		if (clientApplicationLocale == null) {

			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			String defaultMessage = new StringBuilder("The parameter '"
					+ parameterName
					+ "' requires a 'locale' parameter to be passed with it.")
					.toString();
			ApiParameterError error = ApiParameterError.parameterError(
					"validation.msg.missing.locale.parameter", defaultMessage,
					parameterName);
			dataValidationErrors.add(error);

			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}

		try {
			Integer number = null;

			if (StringUtils.isNotBlank(numericalValueFormatted)) {

				String source = numericalValueFormatted.trim();

				NumberFormat format = NumberFormat
						.getInstance(clientApplicationLocale);
				DecimalFormat df = (DecimalFormat) format;
				DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
				df.setParseBigDecimal(true);

				// http://bugs.sun.com/view_bug.do?bug_id=4510618
				char groupingSeparator = symbols.getGroupingSeparator();
				if (groupingSeparator == '\u00a0') {
					source = source.replaceAll(" ",
							Character.toString('\u00a0'));
				}

				Number parsedNumber = df.parse(source);

				double parsedNumberDouble = parsedNumber.doubleValue();
				int parsedNumberInteger = parsedNumber.intValue();

				if (source.contains(Character.toString(symbols
						.getDecimalSeparator()))) {
					throw new ParseException(source, 0);
				}

				if (!Double.valueOf(parsedNumberDouble).equals(
						Double.valueOf(Integer.valueOf(parsedNumberInteger)))) {
					throw new ParseException(source, 0);
				}

				number = parsedNumber.intValue();
			}

			return number;
		} catch (ParseException e) {

			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			ApiParameterError error = ApiParameterError
					.parameterError(
							"validation.msg.invalid.integer.format",
							"The parameter "
									+ parameterName
									+ " has value: "
									+ numericalValueFormatted
									+ " which is invalid integer value for provided locale of ["
									+ clientApplicationLocale.toString() + "].",
							parameterName, numericalValueFormatted,
							clientApplicationLocale);
			dataValidationErrors.add(error);

			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}

	private BigDecimal convertFrom(final String numericalValueFormatted,
			final String parameterName, final Locale clientApplicationLocale) {

		if (clientApplicationLocale == null) {

			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			String defaultMessage = new StringBuilder("The parameter '"
					+ parameterName
					+ "' requires a 'locale' parameter to be passed with it.")
					.toString();
			ApiParameterError error = ApiParameterError.parameterError(
					"validation.msg.missing.locale.parameter", defaultMessage,
					parameterName);
			dataValidationErrors.add(error);

			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}

		try {
			BigDecimal number = null;

			if (StringUtils.isNotBlank(numericalValueFormatted)) {

				String source = numericalValueFormatted.trim();

				NumberFormat format = NumberFormat
						.getNumberInstance(clientApplicationLocale);
				DecimalFormat df = (DecimalFormat) format;
				DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
				// http://bugs.sun.com/view_bug.do?bug_id=4510618
				char groupingSeparator = symbols.getGroupingSeparator();
				if (groupingSeparator == '\u00a0') {
					source = source.replaceAll(" ",
							Character.toString('\u00a0'));
				}

				NumberFormatter numberFormatter = new NumberFormatter();
				Number parsedNumber = numberFormatter.parse(source,
						clientApplicationLocale);
				number = BigDecimal.valueOf(Double.valueOf(parsedNumber
						.doubleValue()));
			}

			return number;
		} catch (ParseException e) {

			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			ApiParameterError error = ApiParameterError
					.parameterError(
							"validation.msg.invalid.decimal.format",
							"The parameter "
									+ parameterName
									+ " has value: "
									+ numericalValueFormatted
									+ " which is invalid decimal value for provided locale of ["
									+ clientApplicationLocale.toString() + "].",
							parameterName, numericalValueFormatted,
							clientApplicationLocale);
			dataValidationErrors.add(error);

			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}

	/*
	 * private LocalDate convertFrom(final String dateAsString, final String
	 * parameterName, final String dateFormat) {
	 *
	 * if (StringUtils.isBlank(dateFormat)) {
	 *
	 * List<ApiParameterError> dataValidationErrors = new
	 * ArrayList<ApiParameterError>(); String defaultMessage = new
	 * StringBuilder("The parameter '" + parameterName +
	 * "' requires a 'dateFormat' parameter to be passed with it.").toString();
	 * ApiParameterError error = ApiParameterError.parameterError(
	 * "validation.msg.missing.dateFormat.parameter", defaultMessage,
	 * parameterName); dataValidationErrors.add(error);
	 *
	 * throw new PlatformApiDataValidationException(
	 * "validation.msg.validation.errors.exist", "Validation errors exist.",
	 * dataValidationErrors); }
	 *
	 * LocalDate eventLocalDate = null; if
	 * (StringUtils.isNotBlank(dateAsString)) { try { Locale locale =
	 * LocaleContextHolder.getLocale(); eventLocalDate =
	 * DateTimeFormat.forPattern(dateFormat) .withLocale(locale)
	 * .parseLocalDate(dateAsString.toLowerCase(locale)); } catch
	 * (IllegalArgumentException e) { List<ApiParameterError>
	 * dataValidationErrors = new ArrayList<ApiParameterError>();
	 * ApiParameterError error = ApiParameterError .parameterError(
	 * "validation.msg.invalid.date.format", "The parameter " + parameterName +
	 * " is invalid based on the dateFormat provided:" + dateFormat,
	 * parameterName, dateAsString, dateFormat);
	 * dataValidationErrors.add(error);
	 *
	 * throw new PlatformApiDataValidationException(
	 * "validation.msg.validation.errors.exist", "Validation errors exist.",
	 * dataValidationErrors); } }
	 *
	 * return eventLocalDate; }
	 *
	 * private Integer convertToInteger(final String numericalValueFormatted,
	 * final String parameterName, final Locale clientApplicationLocale) {
	 *
	 * if (clientApplicationLocale == null) {
	 *
	 * List<ApiParameterError> dataValidationErrors = new
	 * ArrayList<ApiParameterError>(); String defaultMessage = new
	 * StringBuilder("The parameter '" + parameterName +
	 * "' requires a 'locale' parameter to be passed with it.").toString();
	 * ApiParameterError error =
	 * ApiParameterError.parameterError("validation.msg.missing.locale.parameter"
	 * , defaultMessage, parameterName); dataValidationErrors.add(error);
	 *
	 * throw new PlatformApiDataValidationException(
	 * "validation.msg.validation.errors.exist", "Validation errors exist.",
	 * dataValidationErrors); }
	 *
	 * try { Integer number = null;
	 *
	 * if (StringUtils.isNotBlank(numericalValueFormatted)) {
	 *
	 * String source = numericalValueFormatted.trim();
	 *
	 * NumberFormat format = NumberFormat.getInstance(clientApplicationLocale);
	 * DecimalFormat df = (DecimalFormat) format; DecimalFormatSymbols symbols =
	 * df.getDecimalFormatSymbols(); df.setParseBigDecimal(true);
	 *
	 * // http://bugs.sun.com/view_bug.do?bug_id=4510618 char groupingSeparator
	 * = symbols.getGroupingSeparator(); if (groupingSeparator == '\u00a0') {
	 * source = source.replaceAll(" ", Character.toString('\u00a0')); }
	 *
	 * Number parsedNumber = df.parse(source);
	 *
	 * double parsedNumberDouble = parsedNumber.doubleValue(); int
	 * parsedNumberInteger = parsedNumber.intValue();
	 *
	 * if (source.contains(Character.toString(symbols.getDecimalSeparator()))) {
	 * throw new ParseException(source, 0); }
	 *
	 * if
	 * (!Double.valueOf(parsedNumberDouble).equals(Double.valueOf(Integer.valueOf
	 * (parsedNumberInteger)))) { throw new ParseException(source, 0); }
	 *
	 * number = parsedNumber.intValue(); }
	 *
	 * return number; } catch (ParseException e) {
	 *
	 * List<ApiParameterError> dataValidationErrors = new
	 * ArrayList<ApiParameterError>(); ApiParameterError error =
	 * ApiParameterError.parameterError(
	 * "validation.msg.invalid.integer.format", "The parameter " + parameterName
	 * + " has value: " + numericalValueFormatted +
	 * " which is invalid integer value for provided locale of [" +
	 * clientApplicationLocale.toString() + "].", parameterName,
	 * numericalValueFormatted, clientApplicationLocale);
	 * dataValidationErrors.add(error);
	 *
	 * throw new PlatformApiDataValidationException(
	 * "validation.msg.validation.errors.exist", "Validation errors exist.",
	 * dataValidationErrors); } }
	 *
	 * private BigDecimal convertFrom(final String numericalValueFormatted,
	 * final String parameterName, final Locale clientApplicationLocale) {
	 *
	 * if (clientApplicationLocale == null) {
	 *
	 * List<ApiParameterError> dataValidationErrors = new
	 * ArrayList<ApiParameterError>(); String defaultMessage = new
	 * StringBuilder("The parameter '" + parameterName +
	 * "' requires a 'locale' parameter to be passed with it.").toString();
	 * ApiParameterError error =
	 * ApiParameterError.parameterError("validation.msg.missing.locale.parameter"
	 * , defaultMessage, parameterName); dataValidationErrors.add(error);
	 *
	 * throw new PlatformApiDataValidationException(
	 * "validation.msg.validation.errors.exist", "Validation errors exist.",
	 * dataValidationErrors); }
	 *
	 * try { BigDecimal number = null;
	 *
	 * if (StringUtils.isNotBlank(numericalValueFormatted)) {
	 *
	 * String source = numericalValueFormatted.trim();
	 *
	 * NumberFormat format =
	 * NumberFormat.getNumberInstance(clientApplicationLocale); DecimalFormat df
	 * = (DecimalFormat) format; DecimalFormatSymbols symbols =
	 * df.getDecimalFormatSymbols(); //
	 * http://bugs.sun.com/view_bug.do?bug_id=4510618 char groupingSeparator =
	 * symbols.getGroupingSeparator(); if (groupingSeparator == '\u00a0') {
	 * source = source.replaceAll(" ", Character.toString('\u00a0')); }
	 *
	 * NumberFormatter numberFormatter = new NumberFormatter(); Number
	 * parsedNumber = numberFormatter.parse(source, clientApplicationLocale);
	 * number = BigDecimal.valueOf(Double.valueOf(parsedNumber.doubleValue()));
	 * }
	 *
	 * return number; } catch (ParseException e) {
	 *
	 * List<ApiParameterError> dataValidationErrors = new
	 * ArrayList<ApiParameterError>(); ApiParameterError error =
	 * ApiParameterError.parameterError(
	 * "validation.msg.invalid.decimal.format", "The parameter " + parameterName
	 * + " has value: " + numericalValueFormatted +
	 * " which is invalid decimal value for provided locale of [" +
	 * clientApplicationLocale.toString() + "].", parameterName,
	 * numericalValueFormatted, clientApplicationLocale);
	 * dataValidationErrors.add(error);
	 *
	 * throw new PlatformApiDataValidationException(
	 * "validation.msg.validation.errors.exist", "Validation errors exist.",
	 * dataValidationErrors); } }
	 */
	private Locale localeFromString(final String localeAsString) {

		if (StringUtils.isBlank(localeAsString)) {
			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			ApiParameterError error = ApiParameterError.parameterError(
					"validation.msg.invalid.locale.format",
					"The parameter locale is invalid. It cannot be blank.",
					"locale");
			dataValidationErrors.add(error);

			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}

		String languageCode = "";
		String courntryCode = "";
		String variantCode = "";

		String[] localeParts = localeAsString.split("_");

		if (localeParts != null && localeParts.length == 1) {
			languageCode = localeParts[0];
		}

		if (localeParts != null && localeParts.length == 2) {
			languageCode = localeParts[0];
			courntryCode = localeParts[1];
		}

		if (localeParts != null && localeParts.length == 3) {
			languageCode = localeParts[0];
			courntryCode = localeParts[1];
			variantCode = localeParts[2];
		}

		return localeFrom(languageCode, courntryCode, variantCode);
	}

	private Locale localeFrom(final String languageCode,
			final String courntryCode, final String variantCode) {

		List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

		List<String> allowedLanguages = Arrays.asList(Locale.getISOLanguages());
		if (!allowedLanguages.contains(languageCode.toLowerCase())) {
			ApiParameterError error = ApiParameterError.parameterError(
					"validation.msg.invalid.locale.format",
					"The parameter locale has an invalid language value "
							+ languageCode + " .", "locale", languageCode);
			dataValidationErrors.add(error);
		}

		if (StringUtils.isNotBlank(courntryCode.toUpperCase())) {
			List<String> allowedCountries = Arrays.asList(Locale
					.getISOCountries());
			if (!allowedCountries.contains(courntryCode)) {
				ApiParameterError error = ApiParameterError.parameterError(
						"validation.msg.invalid.locale.format",
						"The parameter locale has an invalid country value "
								+ courntryCode + " .", "locale", courntryCode);
				dataValidationErrors.add(error);
			}
		}

		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}

		return new Locale(languageCode.toLowerCase(),
				courntryCode.toUpperCase(), variantCode);
	}

	@Override
	public AdjustmentCommand convertJsonToAdjustmentCommand(
			Long resourceIdentifier, String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		Type typeOfMap = new TypeToken<Map<String, String>>() {
		}.getType();
		Map<String, String> requestMap = gsonConverter
				.fromJson(json, typeOfMap);

		Set<String> supportedParams = new HashSet<String>(Arrays.asList(
				"adjustment_date", "adjustment_code", "adjustment_type",
				"amount_paid", "bill_id", "external_id", "Remarks", "locale",
				"dateFormat"));

		checkForUnsupportedParameters(requestMap, supportedParams);

		Set<String> modifiedParameters = new HashSet<String>();

		// Long client_id=extractLongParameter("client_id", requestMap,
		// modifiedParameters);
		LocalDate adjustment_date = extractLocalDateParameter(
				"adjustment_date", requestMap, modifiedParameters);
		String adjustment_code = extractStringParameter("adjustment_code",
				requestMap, modifiedParameters);
		String adjustment_type = extractStringParameter("adjustment_type",
				requestMap, modifiedParameters);
		BigDecimal amount_paid = extractBigDecimalParameter("amount_paid",
				requestMap, modifiedParameters);
		Long bill_id = extractLongParameter("bill_id", requestMap,
				modifiedParameters);
		Long external_id = extractLongParameter("external_id", requestMap,
				modifiedParameters);
		// boolean is_deleted=extractBooleanParameter("is_deleted", requestMap,
		// modifiedParameters);
		String Remarks = extractStringParameter("Remarks", requestMap,
				modifiedParameters);
		// Long createdby_id=extractLongParameter("createdby_id", requestMap,
		// modifiedParameters);
		// LocalDate created_date=extractLocalDateParameter("created_date",
		// requestMap, modifiedParameters);
		// LocalDate
		// lastmodified_date=extractLocalDateParameter("lastmodified_date",
		// requestMap, modifiedParameters);
		// Long lastmodifiedby_id=extractLongParameter("lastmodifiedby_id",
		// requestMap, modifiedParameters);

		return new AdjustmentCommand(adjustment_date, adjustment_code,
				adjustment_type, amount_paid, bill_id, external_id, Remarks);
	}

	@Override
	public SubscriptionCommand convertJsonToSubscriptionCommand(
			Long resourceIdentifier, String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		Set<String> modifiedParameters = new HashSet<String>();
		Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
		Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);
		Set<String> supportedParams = new HashSet<String>(
				Arrays.asList("id","subscription_period","units","day_name","subscriptionTypeId")
				);
		checkForUnsupportedParameters(requestMap, supportedParams);
		Long sub_id=extractLongParameter("id", requestMap, modifiedParameters);
		//String subscription_type = extractStringParameter("subscription_type", requestMap,modifiedParameters);
			String subscription_period=extractStringParameter("subscription_period",requestMap,modifiedParameters);
			Long units=extractLongParameter("units",requestMap,modifiedParameters);
			Long subscriptionTypeId=extractLongParameter("subscriptionTypeId",requestMap,modifiedParameters);
			String day_name=extractStringParameter("day_name",requestMap,modifiedParameters);
		 return new  SubscriptionCommand(modifiedParameters,sub_id,resourceIdentifier,subscription_period,units,day_name,subscriptionTypeId);
	}

	@Override
	public PaytermsCommand convertJsonToPaytermsCommand(
			Long resourceIdentifier, String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		Set<String> modifiedParameters = new HashSet<String>();
		Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
		Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);
		Set<String> supportedParams = new HashSet<String>(
				Arrays.asList("id","payterm_period","payterm_type","units")
				);
		checkForUnsupportedParameters(requestMap, supportedParams);
		Long id=extractLongParameter("id", requestMap, modifiedParameters);
		String payterm_type = extractStringParameter("payterm_type", requestMap,modifiedParameters);
			Long payterm_period=extractLongParameter("payterm_period",requestMap,modifiedParameters);
			Long units=extractLongParameter("units",requestMap,modifiedParameters);
		 return new  PaytermsCommand(id,payterm_period,payterm_type,units);
	}

	@Override
	public PaymodeCommand convertJsonToPaymodeCommand(Long resourceIdentifier,
			String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		Set<String> modifiedParameters = new HashSet<String>();
		Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
		Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);
		Set<String> supportedParams = new HashSet<String>(
				Arrays.asList("id","paymode","description","category")
				);
		checkForUnsupportedParameters(requestMap, supportedParams);
		Long id=extractLongParameter("id", requestMap, modifiedParameters);
		String paymode = extractStringParameter("paymode", requestMap,modifiedParameters);
	String description=extractStringParameter("description",requestMap,modifiedParameters);
	String category = extractStringParameter("category", requestMap,modifiedParameters);
		 return new  PaymodeCommand(modifiedParameters,id,paymode,description,category);
	}

	@Override
	public BillingCycleCommand convertJsonToBillingCycleCommand(
			Long resourceIdentifier, String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		Set<String> modifiedParameters = new HashSet<String>();
		Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);
		Set<String> supportedParams = new HashSet<String>(
				Arrays.asList("billing_code","description","frequency","every")
				);
		checkForUnsupportedParameters(requestMap, supportedParams);
		String billing_code=extractStringParameter("billing_code",requestMap,modifiedParameters);
		String description=extractStringParameter("description",requestMap,modifiedParameters);
		String frequency=extractStringParameter("frequency",requestMap,modifiedParameters);
		//String every=extractStringParameter("every",requestMap,modifiedParameters);


		final JsonParser parser = new JsonParser();

	    String[] services = null;
	    JsonElement element = parser.parse(json);
	    if (element.isJsonObject()) {
	        JsonObject object = element.getAsJsonObject();
	        if (object.has("every")) {
	            modifiedParameters.add("every");
	            JsonArray array = object.get("every").getAsJsonArray();
	            services = new String[array.size()];
	            for (int i = 0; i < array.size(); i++) {
			services[i] = array.get(i).getAsString();
	            }
	        }
	    }


		 return new  BillingCycleCommand(modifiedParameters,billing_code,description,frequency,services);
	}

	@Override
	public PlansCommand convertJsonToPlansCommand(Long resourceIdentifier,
			String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        final Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);
		Set<String> modifiedParameters = new HashSet<String>();
		Set<String> supportedParams = new HashSet<String>(
				Arrays.asList("planCode","planDescription","locale","dateFormat","startDate","endDate","status","chargeCode","roles","billRule","services")
				);
		checkForUnsupportedParameters(requestMap, supportedParams);
		//Long plan_id=extractLongParameter("id", requestMap, modifiedParameters);
		String planCode = extractStringParameter("planCode", requestMap,modifiedParameters);
		String planDescription=extractStringParameter("planDescription", requestMap, modifiedParameters);
	 LocalDate startDate = extractLocalDateParameter("startDate", requestMap, modifiedParameters);
	LocalDate endDate = extractLocalDateParameter("endDate", requestMap, modifiedParameters);
	Long status=extractLongParameter("status",requestMap,modifiedParameters);
	String contractPeriod=extractStringParameter("contractPeriod",requestMap,modifiedParameters);
	Long billRule=extractLongParameter("billRule", requestMap, modifiedParameters);

	String chargeCode=extractStringParameter("chargeCode", requestMap, modifiedParameters);

	final JsonParser parser = new JsonParser();

    String[] services = null;
    JsonElement element = parser.parse(json);
    if (element.isJsonObject()) {
        JsonObject object = element.getAsJsonObject();
        if (object.has("services")) {
            modifiedParameters.add("services");
            JsonArray array = object.get("services").getAsJsonArray();
            services = new String[array.size()];
            for (int i = 0; i < array.size(); i++) {
		services[i] = array.get(i).getAsString();
            }
        }
    }

		 return new  PlansCommand(modifiedParameters,planCode,planDescription,startDate,endDate,status,services,billRule,chargeCode,contractPeriod);
	}

	@Override
	public Paymentcommand convertJsonToPaymentCommand(Long resourceIdentifier,
			String json) {
		  if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

	        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
	        Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

	        // preClosureInterestRate
	        Set<String> supportedParams = new HashSet<String>(Arrays.asList("paymentId","clientId","paymentDate","paymentCode","amountPaid",
				"statmentId","externalId","remarks", "locale", "dateFormat"));
	        checkForUnsupportedParameters(requestMap, supportedParams);
	        Set<String> modifiedParameters = new HashSet<String>();

	        Long clientId = extractLongParameter("clientId", requestMap, modifiedParameters);
	        Long paymentId = extractLongParameter("paymentId", requestMap, modifiedParameters);
	        Long externalId = extractLongParameter("externalId", requestMap, modifiedParameters);
	        Long statmentId = extractLongParameter("statmentId", requestMap, modifiedParameters);
	        String paymentCode = extractStringParameter("paymentCode", requestMap, modifiedParameters);
	        String remarks = extractStringParameter("remarks", requestMap, modifiedParameters);
	        BigDecimal amountPaid = extractBigDecimalParameter("amountPaid", requestMap, modifiedParameters);
	        LocalDate paymentDate = extractLocalDateParameter("paymentDate", requestMap, modifiedParameters);


	        return new Paymentcommand(clientId, paymentId, externalId, statmentId, paymentCode,
				remarks, amountPaid, paymentDate);
	}

	@Override
	public PricingCommand convertJsonToPricingCommand(Long resourceIdentifier,
			String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		Set<String> modifiedParameters = new HashSet<String>();
		Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
		Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);
		Set<String> supportedParams = new HashSet<String>(
				Arrays.asList("id","planCode","locale","serviceCode","chargeCode","chargevariant","price","discountId")
				);
		checkForUnsupportedParameters(requestMap, supportedParams);
		Long id=extractLongParameter("id", requestMap, modifiedParameters);
		String planCode = extractStringParameter("planCode", requestMap,modifiedParameters);
			String serviceCode=extractStringParameter("serviceCode",requestMap,modifiedParameters);
			String chargeCode=extractStringParameter("chargeCode",requestMap,modifiedParameters);
			String charging_variant=extractStringParameter("chargevariant",requestMap,modifiedParameters);
	BigDecimal price=extractBigDecimalParameter("price",requestMap,modifiedParameters);
	 Long discountId=extractLongParameter("discountId", requestMap, modifiedParameters);
		 return new  PricingCommand(modifiedParameters,planCode,serviceCode,chargeCode,charging_variant,price,discountId);
	}

	@Override
	public OrdersCommand convertJsonToOrderCommand(Object resourceIdentifier,
			Long clientId, String jsonRequestBody) {
		 if (StringUtils.isBlank(jsonRequestBody)) {
			 throw new InvalidJsonException();
			 }

			 Set<String> modifiedParameters = new HashSet<String>();
			 Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
			 Map<String, String> requestMap = gsonConverter.fromJson(jsonRequestBody, typeOfMap);
			 Set<String> supportedParams = new HashSet<String>(
			 Arrays.asList("planCode","locale","dateFormat","start_date","paytermCode","contractPeriod","billAlign","price")
			 );
			 checkForUnsupportedParameters(requestMap, supportedParams);
			 Long plan_id=extractLongParameter("planCode", requestMap, modifiedParameters);
			  LocalDate start_date = extractLocalDateParameter("start_date", requestMap, modifiedParameters);
			 String paytermtype=extractStringParameter("paytermCode",requestMap,modifiedParameters);
			 Long contractPeriod=extractLongParameter("contractPeriod",requestMap,modifiedParameters);
			 boolean billAlign=extractBooleanParameter("billAlign", requestMap, modifiedParameters);
			 BigDecimal price=extractBigDecimalParameter("price",requestMap,modifiedParameters);
			     return new OrdersCommand(modifiedParameters,plan_id,plan_id,start_date,paytermtype,contractPeriod,clientId,billAlign,price);
	}

	@Override
	public DiscountMasterCommand convertJsonToDiscountMasterCommand(
			Long resourceIdentifier, String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
			}

			Type typeOfMap = new TypeToken<Map<String, String>>(){}.getType();
			Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

			Set<String> supportedParams = new HashSet<String>(Arrays.asList("discountCode","discountDescription","discounType","discountValue"));

			checkForUnsupportedParameters(requestMap, supportedParams);

			Set<String> modifiedParameters = new HashSet<String>();

			Long discountCode = extractLongParameter("discountCode", requestMap, modifiedParameters);
			String discountDescription = extractStringParameter("discountDescription", requestMap, modifiedParameters);
			String discounType = extractStringParameter("discounType", requestMap, modifiedParameters);
			Long discountValue=extractLongParameter("discountValue", requestMap, modifiedParameters);

			return new DiscountMasterCommand(discountCode,discountDescription,discounType,discountValue,modifiedParameters);

	}

	@Override
	public TaxMasterCommand convertJsonToTaxMasterCommand(
			Long resourceIdentifier, String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
			}

			Type typeOfMap = new TypeToken<Map<String, String>>(){}.getType();
			Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

			Set<String> supportedParams = new HashSet<String>(Arrays.asList("taxCode","taxType","taxDescription","locale"));

			checkForUnsupportedParameters(requestMap, supportedParams);

			Set<String> modifiedParameters = new HashSet<String>();

			String taxCode = extractStringParameter("taxCode", requestMap, modifiedParameters);
			String taxType = extractStringParameter("taxType", requestMap, modifiedParameters);
			String taxDescription = extractStringParameter("taxDescription", requestMap, modifiedParameters);
			return new TaxMasterCommand(taxCode,taxType,taxDescription);
	}

	@Override
	public TaxMappingRateCommand convertJsonToTaxMappingRateCommand(
			Long resourceIdentifier, String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
			}

			Type typeOfMap = new TypeToken<Map<String, String>>(){}.getType();
			Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

			Set<String> supportedParams = new HashSet<String>(Arrays.asList("chargeCode","taxCode","startdate","type","value","locale", "dateFormat"));

			checkForUnsupportedParameters(requestMap, supportedParams);

			Set<String> modifiedParameters = new HashSet<String>();

			String chargeCode = extractStringParameter("chargeCode", requestMap, modifiedParameters);
			String taxCode = extractStringParameter("taxCode", requestMap, modifiedParameters);
			LocalDate startdate = extractLocalDateParameter("startdate", requestMap, modifiedParameters);
			String type = extractStringParameter("type", requestMap, modifiedParameters);
			BigDecimal value = extractBigDecimalParameter("value", requestMap, modifiedParameters);

			return new TaxMappingRateCommand(chargeCode,taxCode,startdate,type,value);
	}

	@Override
	public ChargeCodeCommand convertJsonToChargeCodeCommand(
			Long resourceIdentifier, String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
			}

			Type typeOfMap = new TypeToken<Map<String, String>>(){}.getType();
			Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

			Set<String> supportedParams = new HashSet<String>(Arrays.asList("chargeCode","chargeDescription","chargeType","locale"));

			checkForUnsupportedParameters(requestMap, supportedParams);

			Set<String> modifiedParameters = new HashSet<String>();

			String chargeCode = extractStringParameter("chargeCode", requestMap, modifiedParameters);
			String chargeDescription = extractStringParameter("chargeDescription", requestMap, modifiedParameters);
			String chargeType = extractStringParameter("chargeType", requestMap, modifiedParameters);
			return new ChargeCodeCommand(chargeCode,chargeDescription,chargeType);
	}

	@Override
	public ServiceMasterCommand convertJsonToServiceMasterCommand(
			Long resourceIdentifier, String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
			}

			Type typeOfMap = new TypeToken<Map<String, String>>(){}.getType();
			Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

			Set<String> supportedParams = new HashSet<String>(Arrays.asList("serviceCode","serviceDescription","serviceType"));

			checkForUnsupportedParameters(requestMap, supportedParams);

			Set<String> modifiedParameters = new HashSet<String>();

			String serviceCode = extractStringParameter("serviceCode", requestMap, modifiedParameters);
			String serviceDescription = extractStringParameter("serviceDescription", requestMap, modifiedParameters);
			Long serviceType = extractLongParameter("serviceType", requestMap, modifiedParameters);

			return new ServiceMasterCommand(serviceCode,serviceDescription,serviceType,modifiedParameters);
	}

	@Override
	public ServicesCommand convertJsonToServiceCommand(Long resourceIdentifier,
			String json) {
		 if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

	        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
	        Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);

	        Set<String> supportedParams = new HashSet<String>(Arrays.asList("serviceCode", "serviceDescription", "serviceType"));

	        checkForUnsupportedParameters(requestMap, supportedParams);

	        Set<String> modifiedParameters = new HashSet<String>();

	        String serviceCode = extractStringParameter("serviceCode", requestMap, modifiedParameters);
	        String serviceDescription = extractStringParameter("serviceDescription", requestMap, modifiedParameters);
	        Long serviceType = extractLongParameter("serviceType", requestMap, modifiedParameters);

	        return new ServicesCommand(serviceCode, serviceDescription, serviceType);
	}

	@Override
public LocalDate convertJsonToBillingProductCommand(
Long resourceIdentifier, String jsonRequestBody) {


if (StringUtils.isBlank(jsonRequestBody)) { throw new InvalidJsonException(); }

Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
Map<String, String> requestMap = gsonConverter.fromJson(jsonRequestBody, typeOfMap);

Set<String> supportedParams = new HashSet<String>(Arrays.asList("locale", "systemDate","dateFormat"));

checkForUnsupportedParameters(requestMap, supportedParams);

Set<String> modifiedParameters = new HashSet<String>();


        checkForUnsupportedParameters(requestMap, supportedParams);
       LocalDate date = extractLocalDateParameter("systemDate", requestMap, modifiedParameters);

        return date;
}

	@Override
	public BillMasterCommand convertJsonToBillMasterCommand(Long resourceIdentifier,
			String json) {
		 if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

	        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
	        Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);

	        Set<String> supportedParams = new HashSet<String>(Arrays.asList("locale", "dueDate","dateFormat","message"));

	        checkForUnsupportedParameters(requestMap, supportedParams);

	        Set<String> modifiedParameters = new HashSet<String>();
	        LocalDate dueDate = extractLocalDateParameter("dueDate", requestMap, modifiedParameters);
	        String message = extractStringParameter("message", requestMap, modifiedParameters);

	        return new BillMasterCommand(dueDate, message);
	}

	@Override
	public TicketMasterCommand convertJsonToTicketMasterCommand(Object object,
			String jsonRequestBody) {
		  if (StringUtils.isBlank(jsonRequestBody)) { throw new InvalidJsonException(); }

	        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
	        Map<String, String> requestMap = gsonConverter.fromJson(jsonRequestBody, typeOfMap);

	        // preClosureInterestRate
	        Set<String> supportedParams = new HashSet<String>(Arrays.asList("locale","dateFormat","clientId","priority","ticketDate","problemCode",
	    	"description","status","resolutionDescription","assignedTo"));
	        checkForUnsupportedParameters(requestMap, supportedParams);
	        Set<String> modifiedParameters = new HashSet<String>();

	        Long clientId = extractLongParameter("clientId", requestMap, modifiedParameters);
	        String priority = extractStringParameter("priority", requestMap, modifiedParameters);
	        String status = extractStringParameter("status", requestMap, modifiedParameters);
	        String problemCode = extractStringParameter("problemCode", requestMap, modifiedParameters);
	        String description = extractStringParameter("description", requestMap, modifiedParameters);
	        String resolutionDescription = extractStringParameter("resolutionDescription", requestMap, modifiedParameters);
	        String assignedTo = extractStringParameter("assignedTo", requestMap, modifiedParameters);
	        LocalDate ticketDate = extractLocalDateParameter("ticketDate", requestMap, modifiedParameters);


	        return new TicketMasterCommand(clientId,priority, description, problemCode, status,
	        		resolutionDescription, assignedTo,ticketDate);
	}

	@Override
	public AddressCommand convertJsonToAddressCommand(Object object,
			Long clientId, String jsonRequestBody) {
		 if (StringUtils.isBlank(jsonRequestBody)) { throw new InvalidJsonException(); }

	        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
	        Map<String, String> requestMap = gsonConverter.fromJson(jsonRequestBody, typeOfMap);

	        // preClosureInterestRate
	        Set<String> supportedParams = new HashSet<String>(Arrays.asList("clientId","addressKey","addressNo","street","zip","city",
	    	"state","country"));
	        checkForUnsupportedParameters(requestMap, supportedParams);
	        Set<String> modifiedParameters = new HashSet<String>();

	        Long clientid = extractLongParameter("clientId", requestMap, modifiedParameters);
	        String addressKey = extractStringParameter("addressKey", requestMap, modifiedParameters);
	        String addressNo = extractStringParameter("addressNo", requestMap, modifiedParameters);
	        String street = extractStringParameter("street", requestMap, modifiedParameters);
	        String zip = extractStringParameter("zip", requestMap, modifiedParameters);
	        String city = extractStringParameter("city", requestMap, modifiedParameters);
	        String state = extractStringParameter("state", requestMap, modifiedParameters);
	        String country = extractStringParameter("country", requestMap, modifiedParameters);


	        return new AddressCommand(modifiedParameters,clientId,addressKey, addressNo, street, zip,
	        		city, state,country);
	}

	@Override
	public OneTimeSaleCommand convertJsonToSalesCommand(Object object,
			String jsonRequestBody) {
		 if (StringUtils.isBlank(jsonRequestBody)) { throw new InvalidJsonException(); }

	        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
	        Map<String, String> requestMap = gsonConverter.fromJson(jsonRequestBody, typeOfMap);

	        // preClosureInterestRate
	        Set<String> supportedParams = new HashSet<String>(Arrays.asList("itemId","locale","units","chargeCode","unitPrice","quantity","totalPrice"));
	        checkForUnsupportedParameters(requestMap, supportedParams);
	        Set<String> modifiedParameters = new HashSet<String>();

	        Long itemId = extractLongParameter("itemId", requestMap, modifiedParameters);
	        String units = extractStringParameter("units", requestMap, modifiedParameters);
	        String chargeCode = extractStringParameter("chargeCode", requestMap, modifiedParameters);
	        BigDecimal unitPrice = extractBigDecimalParameter("unitPrice", requestMap, modifiedParameters);
	        String quantity = extractStringParameter("quantity", requestMap, modifiedParameters);
	        BigDecimal totalPrice = extractBigDecimalParameter("totalPrice", requestMap, modifiedParameters);
	      


	        return new OneTimeSaleCommand(modifiedParameters,itemId,units, chargeCode, unitPrice, quantity,
	        		totalPrice);
	}

	@Override
	public ItemCommand convertJsonToItemCommand(Object object,
			String jsonRequestBody) {
		 if (StringUtils.isBlank(jsonRequestBody)) { throw new InvalidJsonException(); }

	        Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
	        Map<String, String> requestMap = gsonConverter.fromJson(jsonRequestBody, typeOfMap);

	        // preClosureInterestRate
	        Set<String> supportedParams = new HashSet<String>(Arrays.asList("itemCode","itemDescription","units","chargeCode","locale","unitPrice","warranty","itemClass"));
	        checkForUnsupportedParameters(requestMap, supportedParams);
	        Set<String> modifiedParameters = new HashSet<String>();

	        String itemCode = extractStringParameter("itemCode", requestMap, modifiedParameters);
	        String itemDescription = extractStringParameter("itemDescription", requestMap, modifiedParameters);
	        String units = extractStringParameter("units", requestMap, modifiedParameters);
	        String itemClass = extractStringParameter("itemClass", requestMap, modifiedParameters);
	        String chargeCode = extractStringParameter("chargeCode", requestMap, modifiedParameters);
	        BigDecimal unitPrice = extractBigDecimalParameter("unitPrice", requestMap, modifiedParameters);
	        Long warranty = extractLongParameter("warranty", requestMap, modifiedParameters);

	        return new ItemCommand(modifiedParameters,itemCode,itemDescription,itemClass, chargeCode, unitPrice, warranty,units);
	}
	}

