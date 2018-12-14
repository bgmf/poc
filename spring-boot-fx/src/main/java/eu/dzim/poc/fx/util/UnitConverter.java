package eu.dzim.poc.fx.util;

public enum UnitConverter {
    METERS {
        @Override
        public double toMiles(final double meters) {
            return meters * 0.00062137D;
        }

        @Override
        public double toMeters(final double meters) {
            return meters;
        }
    },
    MILES {
        @Override
        public double toMiles(final double miles) {
            return miles;
        }

        @Override
        public double toMeters(final double miles) {
            return miles / 0.00062137D;
        }
    };

    public abstract double toMiles(double unit);

    public abstract double toMeters(double unit);
}