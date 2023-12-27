import java.util.Objects;

enum TestEnum {
    E1,
    E2
}

record Point<T extends Number>(T x, T y) {
}

public class TestDTO {
    private int i;
    private Integer i2;
    private float f;
    private Float f2;
    private double d;
    private Double d2;
    private String s;
    private char c;
    private Character c2;
    private TestEnum testEnum;
    private Point<Integer> p;

    public TestDTO() {
        i = 10;
        i2 = 11;
        f = 12.1f;
        f2 = 13.2f;
        d = 14.3;
        d2 = 15.4;
        s = "TestStr";
        c = 'T';
        c2 = '2';
        testEnum = TestEnum.E2;
        p = new Point<>(1, 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestDTO testDTO = (TestDTO) o;
        return i == testDTO.i && Float.compare(f, testDTO.f) == 0 && Double.compare(d, testDTO.d) == 0 && c == testDTO.c && Objects.equals(i2, testDTO.i2) && Objects.equals(f2, testDTO.f2) && Objects.equals(d2, testDTO.d2) && Objects.equals(s, testDTO.s) && Objects.equals(c2, testDTO.c2) && testEnum == testDTO.testEnum && Objects.equals(p, testDTO.p);
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, i2, f, f2, d, d2, s, c, c2, testEnum, p);
    }

    public int getI() {
        return i;
    }

    public Integer getI2() {
        return i2;
    }

    public float getF() {
        return f;
    }

    public Float getF2() {
        return f2;
    }

    public double getD() {
        return d;
    }

    public Double getD2() {
        return d2;
    }

    public String getS() {
        return s;
    }

    public char getC() {
        return c;
    }

    public Character getC2() {
        return c2;
    }

    public TestEnum getTestEnum() {
        return testEnum;
    }

    public Point<Integer> getP() {
        return p;
    }
}
