/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.protocols.jsoncore;

import java.math.BigDecimal;
import java.math.BigInteger;
import software.amazon.awssdk.annotations.SdkProtectedApi;

/**
 * A value in a JSON number node.
 *
 * <p>Because JSON does not distinguish between floating point or integer numbers, but Java does, the two options can be
 * differentiated via {@link #isInteger()} and {@link #isFloatingPoint()}.
 *
 * <p>The numeric value can be extracted via "as" methods, like {@link #asBigDecimal()} and {@link #asBigInteger()}</p>.
 */
@SdkProtectedApi
public interface JsonNumber {
    /**
     * Returns true if this number is a whole number without a decimal component. The value can be extracted via {@link #asInt()},
     * {@link #asLong()} or {@link #asBigInteger()}.
     */
    boolean isInteger();

    /**
     * Returns true if this number is a floating point number with a decimal component. The value can be extracted via
     * {@link #asDouble()}, or {@link #asBigDecimal()}.
     */
    boolean isFloatingPoint();

    /**
     * Convert this number to a {@link BigDecimal}. This does not lose any precision, regardless of the type.
     */
    BigDecimal asBigDecimal();

    /**
     * Convert this number to a {@code double}. This will be an inexact conversion from the value sent on the wire. If exact
     * precision is required, use {@link #asBigDecimal()}.
     */
    double asDouble();

    /**
     * Convert this number to a {@link BigInteger}. If {@link #isFloatingPoint()} is true, the decimal component will be
     * removed via truncation (dropped). This does not lose any precision if {@link #isInteger()} is true.
     */
    BigInteger asBigInteger();

    /**
     * Convert this number to a {@code long}. If {@link #isFloatingPoint()} is true, the decimal component will be removed via
     * truncation (dropped). If the number is larger than can fit into an int, {@link Long#MAX_VALUE} is used. If the number
     * is smaller than can fit into an int, {@link Long#MIN_VALUE} is used.
     */
    long asLong();

    /**
     * Convert this number to an {@code int}. If {@link #isFloatingPoint()} is true, the decimal component will be removed via
     * truncation (dropped). If the number is larger than can fit into an int, {@link Integer#MAX_VALUE} is used. If the number
     * is smaller than can fit into an int, {@link Integer#MIN_VALUE} is used.
     */
    int asInt();
}
