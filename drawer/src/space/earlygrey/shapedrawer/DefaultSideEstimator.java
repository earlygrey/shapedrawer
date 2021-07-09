package space.earlygrey.shapedrawer;

public class DefaultSideEstimator implements ISideEstimator {

	public int estimateSidesRequired(float pixelSize, float radiusX, float radiusY) {
		float circumference = (float) (ShapeUtils.PI2 * Math.sqrt((radiusX * radiusX + radiusY * radiusY) / 2f));
		int sides = (int) (circumference / (16 * pixelSize));
		float a = Math.min(radiusX, radiusY), b = Math.max(radiusX, radiusY);
		float eccentricity = (float) Math.sqrt(1 - ((a * a) / (b * b)));
		sides += (sides * eccentricity) / 16;
		return Math.max(sides, 20);
	}
}
