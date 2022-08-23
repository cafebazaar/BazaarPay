package ir.cafebazaar.bazaarpay.widget.loading;

import android.view.animation.Interpolator;

public class Ease {
    public static Interpolator inOut() {
        return PathInterpolatorCompat.create(0.42f, 0f, 0.58f, 1f);
    }
}
