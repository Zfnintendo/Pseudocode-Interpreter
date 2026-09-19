package parser;

public class InfixPowers {

    private final double firstPower;
    private final double secondPower;

    public InfixPowers(double firstPower, double secondPower) {
        this.firstPower = firstPower;
        this.secondPower = secondPower;
    }

    public double getFirstPower() {
        return this.firstPower;
    }

    public double getSecondPower() {
        return this.secondPower;
    }

}
