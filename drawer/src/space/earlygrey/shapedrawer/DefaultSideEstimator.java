package space.earlygrey.shapedrawer;

public class DefaultSideEstimator implements ISideEstimator {

	/**
	 * Minimum value returned by {@link #estimateSidesRequired(float, float, float)}
	 */
	protected int minimumSides; // 
	/**
	 * Maximum value returned by {@link #estimateSidesRequired(float, float, float)}
	 */
	protected int maximumSides;
	/**
	 * Multiply the number of sides return by this value. {@link #minimumSides} and {@link #maximumSides} are not affected
	 */
	protected float sideMultiplicator;

	public DefaultSideEstimator() {
		this(20, 16000, 1f);
	}

	public DefaultSideEstimator(int minimumSides, int maximumSides, float sideMultiplicator) {
		this.minimumSides = minimumSides;
		this.maximumSides = maximumSides;
		this.sideMultiplicator = sideMultiplicator;
	}

	public int estimateSidesRequired(float pixelSize, float radiusX, float radiusY) {
		float circumference = (float) (ShapeUtils.PI2 * Math.sqrt((radiusX * radiusX + radiusY * radiusY) / 2f));
		int sides = (int) (circumference / (16 * pixelSize));
		float a = Math.min(radiusX, radiusY), b = Math.max(radiusX, radiusY);
		float eccentricity = (float) Math.sqrt(1 - ((a * a) / (b * b)));
		sides += (sides * eccentricity * sideMultiplicator) / 16;
		return Math.min(Math.max(sides, minimumSides), maximumSides);
	}
}
