package software.amazon.awssdk.protocols.jsoncore;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.assertj.core.data.Offset;
import org.junit.Test;

public class JsonNodeTest {
    private static final JsonNodeParser PARSER = JsonNode.parser();

    @Test
    public void parseNull_givesCorrectType() {
        JsonNode node = PARSER.parse("null");

        assertThat(node.isNull()).isTrue();
        assertThat(node.isBoolean()).isFalse();
        assertThat(node.isNumber()).isFalse();
        assertThat(node.isString()).isFalse();
        assertThat(node.isArray()).isFalse();
        assertThat(node.isObject()).isFalse();
        assertThat(node.isEmbeddedObject()).isFalse();
    }

    @Test
    public void parseBoolean_givesCorrectType() {
        String[] options = { "true", "false" };
        for (String option : options) {
            JsonNode node = PARSER.parse(option);

            assertThat(node.isNull()).isFalse();
            assertThat(node.isBoolean()).isTrue();
            assertThat(node.isNumber()).isFalse();
            assertThat(node.isString()).isFalse();
            assertThat(node.isArray()).isFalse();
            assertThat(node.isObject()).isFalse();
            assertThat(node.isEmbeddedObject()).isFalse();
        }
    }

    @Test
    public void parseNumber_givesCorrectType() {
        String[] options = { "-1e100", "-1", "0", "1", "1e100" };
        for (String option : options) {
            JsonNode node = PARSER.parse(option);

            assertThat(node.isNull()).isFalse();
            assertThat(node.isBoolean()).isFalse();
            assertThat(node.isNumber()).isTrue();
            assertThat(node.isString()).isFalse();
            assertThat(node.isArray()).isFalse();
            assertThat(node.isObject()).isFalse();
            assertThat(node.isEmbeddedObject()).isFalse();
        }
    }

    @Test
    public void parseString_givesCorrectType() {
        String[] options = { "\"\"", "\"foo\"" };
        for (String option : options) {
            JsonNode node = PARSER.parse(option);

            assertThat(node.isNull()).isFalse();
            assertThat(node.isBoolean()).isFalse();
            assertThat(node.isNumber()).isFalse();
            assertThat(node.isString()).isTrue();
            assertThat(node.isArray()).isFalse();
            assertThat(node.isObject()).isFalse();
            assertThat(node.isEmbeddedObject()).isFalse();
        }
    }

    @Test
    public void parseArray_givesCorrectType() {
        String[] options = { "[]", "[null]" };
        for (String option : options) {
            JsonNode node = PARSER.parse(option);

            assertThat(node.isNull()).isFalse();
            assertThat(node.isBoolean()).isFalse();
            assertThat(node.isNumber()).isFalse();
            assertThat(node.isString()).isFalse();
            assertThat(node.isArray()).isTrue();
            assertThat(node.isObject()).isFalse();
            assertThat(node.isEmbeddedObject()).isFalse();
        }
    }

    @Test
    public void parseObject_givesCorrectType() {
        String[] options = { "{}", "{ \"foo\": null }" };
        for (String option : options) {
            JsonNode node = PARSER.parse(option);

            assertThat(node.isNull()).isFalse();
            assertThat(node.isBoolean()).isFalse();
            assertThat(node.isNumber()).isFalse();
            assertThat(node.isString()).isFalse();
            assertThat(node.isArray()).isFalse();
            assertThat(node.isObject()).isTrue();
            assertThat(node.isEmbeddedObject()).isFalse();
        }
    }

    @Test
    public void parseBoolean_givesCorrectValue() {
        assertThat(PARSER.parse("true").asBoolean()).isTrue();
        assertThat(PARSER.parse("false").asBoolean()).isFalse();
    }

    @Test
    public void parseNumber_floatingPoint_givesCorrectValue() {
        JsonNumber floatingPoint = PARSER.parse("5.9").asNumber();

        assertThat(floatingPoint.isFloatingPoint()).isTrue();
        assertThat(floatingPoint.isInteger()).isFalse();

        assertThat(floatingPoint.asBigInteger()).isEqualTo(new BigInteger("5"));
        assertThat(floatingPoint.asLong()).isEqualTo(5L);
        assertThat(floatingPoint.asInt()).isEqualTo(5);
        assertThat(floatingPoint.asBigDecimal()).isEqualTo(new BigDecimal("5.9"));
        assertThat(floatingPoint.asDouble()).isEqualTo(5.9, Offset.offset(0.1));
    }

    @Test
    public void parseNumber_integer_givesCorrectValue() {
        JsonNumber floatingPoint = PARSER.parse("5").asNumber();

        assertThat(floatingPoint.isFloatingPoint()).isFalse();
        assertThat(floatingPoint.isInteger()).isTrue();

        assertThat(floatingPoint.asBigInteger()).isEqualTo(new BigInteger("5"));
        assertThat(floatingPoint.asLong()).isEqualTo(5L);
        assertThat(floatingPoint.asInt()).isEqualTo(5);
        assertThat(floatingPoint.asBigDecimal()).isEqualTo(new BigDecimal("5"));
        assertThat(floatingPoint.asDouble()).isEqualTo(5, Offset.offset(0.1));
    }

    @Test
    public void parseNumber_veryNegative_givesCorrectValue() {
        BigInteger veryNegativeValue = new BigInteger(Long.toString(Long.MIN_VALUE));
        BigInteger evenMoreNegativeValue = veryNegativeValue.subtract(BigInteger.ONE);

        JsonNumber negativeInteger = PARSER.parse(evenMoreNegativeValue.toString()).asNumber();

        assertThat(negativeInteger.isFloatingPoint()).isFalse();
        assertThat(negativeInteger.isInteger()).isTrue();

        assertThat(negativeInteger.asBigInteger()).isEqualTo(evenMoreNegativeValue);
        assertThat(negativeInteger.asLong()).isEqualTo(Long.MIN_VALUE);
        assertThat(negativeInteger.asInt()).isEqualTo(Integer.MIN_VALUE);
        assertThat(negativeInteger.asBigDecimal()).isEqualTo(new BigDecimal(evenMoreNegativeValue.toString()));
        assertThat(negativeInteger.asDouble()).isEqualTo(evenMoreNegativeValue.doubleValue(), Offset.offset(1D));
    }

    @Test
    public void testToString() {
        // TODO
    }
}