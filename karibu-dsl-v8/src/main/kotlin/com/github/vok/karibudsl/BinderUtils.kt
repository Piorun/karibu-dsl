package com.github.vok.karibudsl

import com.vaadin.data.*
import com.vaadin.data.converter.*
import com.vaadin.server.Page
import com.vaadin.ui.AbstractTextField
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.reflect.KProperty1

/**
 * Trims the user input string before storing it into the underlying property data source. Vital for mobile-oriented apps:
 * Android keyboard often adds whitespace to the end of the text when auto-completion occurs. Imagine storing a username ending with a space upon registration:
 * such person can no longer log in from his PC unless he explicitely types in the space.
 */
fun <BEAN> Binder.BindingBuilder<BEAN, String?>.trimmingConverter(): Binder.BindingBuilder<BEAN, String?> =
        withConverter(object : Converter<String?, String?> {
            override fun convertToModel(value: String?, context: ValueContext?): Result<String?> =
                    Result.ok(value?.trim())
            override fun convertToPresentation(value: String?, context: ValueContext?): String? = value
        })
fun <BEAN> Binder.BindingBuilder<BEAN, String?>.toInt(): Binder.BindingBuilder<BEAN, Int?> =
        withConverter(StringToIntegerConverter("Can't convert to integer"))
fun <BEAN> Binder.BindingBuilder<BEAN, String?>.toDouble(): Binder.BindingBuilder<BEAN, Double?> =
        withConverter(StringToDoubleConverter("Can't convert to decimal number"))
fun <BEAN> Binder.BindingBuilder<BEAN, String?>.toLong(): Binder.BindingBuilder<BEAN, Long?> =
        withConverter(StringToLongConverter("Can't convert to integer"))
fun <BEAN> Binder.BindingBuilder<BEAN, String?>.toBigDecimal(): Binder.BindingBuilder<BEAN, BigDecimal?> =
        withConverter(StringToBigDecimalConverter("Can't convert to decimal number"))
fun <BEAN> Binder.BindingBuilder<BEAN, String?>.toBigInteger(): Binder.BindingBuilder<BEAN, BigInteger?> =
        withConverter(StringToBigIntegerConverter("Can't convert to integer"))
fun <BEAN> Binder.BindingBuilder<BEAN, LocalDate?>.toDate(): Binder.BindingBuilder<BEAN, Date?> =
        withConverter(LocalDateToDateConverter(ZoneOffset.ofTotalSeconds(Page.getCurrent().webBrowser.timezoneOffset / 1000)))
@JvmName("localDateTimeToDate")
fun <BEAN> Binder.BindingBuilder<BEAN, LocalDateTime?>.toDate(): Binder.BindingBuilder<BEAN, Date?> =
        withConverter(LocalDateTimeToDateConverter(ZoneOffset.ofTotalSeconds(Page.getCurrent().webBrowser.timezoneOffset / 1000)))

/**
 * Allows you to create [Binder] like this: `Binder<Person>()` instead of `Binder(Person::class.java)`
 */
inline fun <reified T : Any> BeanValidationBinder(): BeanValidationBinder<T> = BeanValidationBinder(T::class.java)

/**
 * Allows you to bind the component directly in the component's definition. E.g.
 * ```
 * textField("Name:") {
 *   bind(binder).trimmingConverter().bind(Person::name)
 * }
 * ```
 */
fun <BEAN, FIELDVALUE> HasValue<FIELDVALUE>.bind(binder: Binder<BEAN>): Binder.BindingBuilder<BEAN, FIELDVALUE> {
    var builder = binder.forField(this)
    // workaround for https://github.com/vaadin/framework/issues/8664
    @Suppress("UNCHECKED_CAST")
    if (this is AbstractTextField) builder = builder.withNullRepresentation("" as FIELDVALUE)
    return builder
}

/**
 * A type-safe binding which binds only to a property of given type, found on given bean.
 * @param prop the bean property
 */
fun <BEAN, FIELDVALUE> Binder.BindingBuilder<BEAN, FIELDVALUE>.bind(prop: KProperty1<BEAN, FIELDVALUE?>): Binder.Binding<BEAN, FIELDVALUE> =
        bind(prop.name)
