package ir.cafebazaar.bazaarpay.widget.loading;

import android.graphics.Path;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

/**
 * API 21+ implementation for path interpolator compatibility.
 */
class PathInterpolatorCompatApi21 {

    private PathInterpolatorCompatApi21() {
        // prevent instantiation
    }

    public static Interpolator create(Path path) {
        return new PathInterpolator(path);
    }

    public static Interpolator create(float controlX, float controlY) {
        return new PathInterpolator(controlX, controlY);
    }

    public static Interpolator create(
        float controlX1, float controlY1,
        float controlX2, float controlY2
    ) {
        return new PathInterpolator(controlX1, controlY1, controlX2, controlY2);
    }
}
