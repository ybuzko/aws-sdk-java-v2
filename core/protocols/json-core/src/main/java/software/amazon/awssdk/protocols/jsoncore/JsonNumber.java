package software.amazon.awssdk.protocols.jsoncore;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface JsonNumber {
    boolean isInteger();
    boolean isFloatingPoint();

    BigDecimal asBigDecimal();
    BigInteger asBigInteger();
    double asDouble();

    int asInt();
    long asLong();

    static JsonNumber of(Number number) {
        return null;
    }
}
