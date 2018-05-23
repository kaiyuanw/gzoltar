package com.gzoltar.report.metrics;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import com.gzoltar.core.spectrum.ISpectrum;
import com.gzoltar.fl.IFormula;

public abstract class AbstractMetric implements IMetric {

  private IFormula formula = null;

  /**
   * {@inheritDoc}
   */
  public abstract double calculate(final ISpectrum spectrum);

  /**
   * {@inheritDoc}
   */
  public abstract String getName();

  /**
   * {@inheritDoc}
   */
  public boolean requireFormula() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public void setFormula(IFormula formula) {
    this.formula = formula;
  }

  /**
   * {@inheritDoc}
   */
  public IFormula getFormula() {
    return this.formula;
  }

  /**
   * 
   * @param spectrum
   * @return
   */
  protected boolean validMatrix(final ISpectrum spectrum) {
    if (spectrum != null) {
      return spectrum.getNumberOfNodes() > 0 && spectrum.getNumberOfTransactions() > 0;
    }
    return false;
  }

  /**
   * 
   * @param value
   * @return
   */
  protected double log2(final double value) {
    return Math.log(value) / Math.log(2);
  }

  /**
   * Normalizes a value.
   * 
   * @param min
   * @param max
   * @param value to normalize
   * @return normalized value
   */
  protected double normalize(final double min, final double max, final double value) {
    return (value - min) / (max - min);
  }

  /**
   * Normalize a value using Andrea's normalization function.
   * 
   * @param value a double.
   * @return a double.
   * @throws java.lang.IllegalArgumentException if any.
   */
  protected double normalize(final double value) throws IllegalArgumentException {
    if (value < 0d) {
      throw new IllegalArgumentException("Values to normalize cannot be negative");
    }
    if (Double.isInfinite(value)) {
      return 1.0;
    }
    return value / (1.0 + value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();
    builder.append(this.getName());
    return builder.toHashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof IMetric)) {
      return false;
    }

    AbstractMetric metric = (AbstractMetric) obj;

    EqualsBuilder builder = new EqualsBuilder();
    builder.append(this.getName(), metric.getName());

    return builder.isEquals();
  }
}
