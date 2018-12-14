package eu.dzim.shared.fx.util;

import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ProgressIndicator;
import javafx.util.Duration;

public class ProgressTranistion extends Transition {

    private final ObjectProperty<Duration> duration = new SimpleObjectProperty<>(this, "duration", Duration.millis(250.0));
    private final DoubleProperty from = new SimpleDoubleProperty(this, "from", 0.0);
    private final DoubleProperty to = new SimpleDoubleProperty(this, "to", 0.0);

    private final ProgressIndicator progress;

    private double diff = 0.0;

    public ProgressTranistion(final ProgressIndicator progress) {
        this.setCycleDuration(getDuration());
        this.progress = progress;
    }

    private final ObjectProperty<Duration> durationProperty() {
        return duration;
    }

    public final Duration getDuration() {
        return durationProperty().get();
    }

    public final void setDuration(Duration duration) {
        durationProperty().set(duration);
        this.setCycleDuration(getDuration());
    }

    public final DoubleProperty fromProperty() {
        return this.from;
    }

    public final double getFrom() {
        return this.fromProperty().get();
    }

    public final void setFrom(final double from) {
        this.fromProperty().set(from);
    }

    public final DoubleProperty toProperty() {
        return this.to;
    }

    public final double getTo() {
        return this.toProperty().get();
    }

    public final void setTo(final double to) {
        this.toProperty().set(to);
    }

    @Override
    protected void interpolate(double fraction) {
        if (diff == 0.0)
            diff = to.get() - from.get();
        progress.setProgress(from.get() + diff * fraction);
    }

}
