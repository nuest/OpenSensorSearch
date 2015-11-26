/**
 * Copyright (C) 2013 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.oss.sir.api;

/**
 * @author Jan Schulte
 * 
 */
public class SirConstraint {

    /**
     * Types of Constraints
     * 
     * @author Jan Schulte
     * 
     */
    public enum ConsType {
        isBetween, isEqualTo, isGreaterThan, isGreaterThanOrEqualTo, isLessThan, isLessThanOrEqualTo, isNotEqualTo
    }

    /**
     * Value of Type
     * 
     * @author Jan Schulte
     * 
     */
    public enum ValueType {
        Boolean, Double, String
    }

    /**
     * Constraint type
     */
    private ConsType consType;

    /**
     * lower boundary for constraint isBetween
     */
    private double lowerBoundary;

    /**
     * upper boundary for constraint isBetween
     */
    private double upperBoundary;

    /**
     * Value if ValueType is boolean
     */
    private boolean valueBoolean;

    /**
     * Value if ValueType is Double
     */
    private double valueDouble;

    /**
     * Value it ValueType is String
     */
    private String valueString;

    /**
     * Value type
     */
    private ValueType valueType;

    /**
     * @return the consType
     */
    public ConsType getConsType() {
        return this.consType;
    }

    /**
     * @return the lowerBoundary
     */
    public double getLowerBoundary() {
        return this.lowerBoundary;
    }

    /**
     * @return the upperBoundary
     */
    public double getUpperBoundary() {
        return this.upperBoundary;
    }

    /**
     * @return the valueDouble
     */
    public double getValueDouble() {
        return this.valueDouble;
    }

    /**
     * @return the valueString
     */
    public String getValueString() {
        return this.valueString;
    }

    /**
     * @return the valueType
     */
    public ValueType getValueType() {
        return this.valueType;
    }

    /**
     * @return the valueBoolean
     */
    public boolean isValueBoolean() {
        return this.valueBoolean;
    }

    /**
     * @param consType
     *        the consType to set
     */
    public void setConsType(ConsType consType) {
        this.consType = consType;
    }

    /**
     * @param lowerBoundary
     *        the lowerBoundary to set
     */
    public void setLowerBoundary(double lowerBoundary) {
        this.lowerBoundary = lowerBoundary;
    }

    /**
     * @param upperBoundary
     *        the upperBoundary to set
     */
    public void setUpperBoundary(double upperBoundary) {
        this.upperBoundary = upperBoundary;
    }

    /**
     * @param valueBoolean
     *        the valueBoolean to set
     */
    public void setValueBoolean(boolean valueBoolean) {
        this.valueBoolean = valueBoolean;
    }

    /**
     * @param valueDouble
     *        the valueDouble to set
     */
    public void setValueDouble(double valueDouble) {
        this.valueDouble = valueDouble;
    }

    /**
     * @param valueString
     *        the valueString to set
     */
    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    /**
     * @param valueType
     *        the valueType to set
     */
    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (this.consType == ConsType.isEqualTo) {
            sb.append(" is equal to ");
            if (this.valueString != null) {
                sb.append(this.valueString);
            }
            if (this.valueDouble != Double.MAX_VALUE) {
                sb.append(this.valueDouble);
            }
            else {
                sb.append(this.valueBoolean);
            }
        }
        if (this.consType == ConsType.isNotEqualTo) {
            sb.append(" is not equal to ");
            if (this.valueString != null) {
                sb.append(this.valueString);
            }
            if (this.valueDouble != Double.MAX_VALUE) {
                sb.append(this.valueDouble);
            }
            else {
                sb.append(this.valueBoolean);
            }
        }
        if (this.consType == ConsType.isGreaterThan) {
            sb.append(" is greater than to " + this.valueDouble);
        }
        if (this.consType == ConsType.isLessThan) {
            sb.append(" is less than " + this.valueDouble);
        }
        if (this.consType == ConsType.isGreaterThanOrEqualTo) {
            sb.append("is greater than or equal to " + this.valueDouble);
        }
        if (this.consType == ConsType.isLessThanOrEqualTo) {
            sb.append(" is less than or equal to " + this.valueDouble);
        }
        if (this.consType == ConsType.isBetween) {
            sb.append(" is between " + this.lowerBoundary + " and " + this.upperBoundary);
        }
        return sb.toString();
    }
}
