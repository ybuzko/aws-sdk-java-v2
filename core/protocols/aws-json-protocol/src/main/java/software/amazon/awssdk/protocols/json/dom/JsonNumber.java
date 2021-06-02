package software.amazon.awssdk.protocols.json.dom;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface JsonNumber {
    Type type();

    boolean isBigDecimal();
    boolean isBigInteger();
    boolean isDouble();
    boolean isFloat();
    boolean isInt();
    boolean isLong();

    BigDecimal asBigDecimal();
    BigInteger asBigInteger();
    double asDouble();
    float asFloat();
    int asInt();
    long asLong();

    JsonNumber of(Number number);

    enum Type {
        BIG_DECIMAL,
        BIG_INTEGER,
        DOUBLE,
        FLOAT,
        INT,
        LONG
    }
}
