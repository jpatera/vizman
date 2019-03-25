package eu.japtor.vizman.backend.bean;

import eu.japtor.vizman.app.HasLogger;

import java.math.BigDecimal;
import java.util.Map;

public abstract class AbstractPruh implements HasLogger, java.io.Serializable {
    private static final long serialVersionUID = 1L;

//    BigDecimal d01;
//    BigDecimal d02;
//    BigDecimal d03;
//    BigDecimal d04;
//    BigDecimal d05;
//    BigDecimal d06;
//    BigDecimal d07;
//    BigDecimal d08;
//    BigDecimal d09;
//    BigDecimal d10;
//    BigDecimal d11;
//    BigDecimal d12;
//    BigDecimal d13;
//    BigDecimal d14;
//    BigDecimal d15;
//    BigDecimal d16;
//    BigDecimal d17;
//    BigDecimal d18;
//    BigDecimal d19;
//    BigDecimal d20;
//    BigDecimal d21;
//    BigDecimal d22;
//    BigDecimal d23;
//    BigDecimal d24;
//    BigDecimal d25;
//    BigDecimal d26;
//    BigDecimal d27;
//    BigDecimal d28;
//    BigDecimal d29;
//    BigDecimal d30;
//    BigDecimal d31;

    Map<Integer, BigDecimal> hods;

    public BigDecimal getHod(int day) {
        return hods.get(day);
    }
    public void setHod(int day, BigDecimal hod) {
        hods.put(day, hod);
    }

//    public BigDecimal getD01() {
//        return d01;
//    }
//    public void setD01(BigDecimal d01) {
//        this.d01 = d01;
//    }
//
//    public BigDecimal getD02() {
//        return d02;
//    }
//    public void setD02(BigDecimal d02) {
//        this.d02 = d02;
//    }
//
//    public BigDecimal getD03() {
//        return d03;
//    }
//    public void setD03(BigDecimal d03) {
//        this.d03 = d03;
//    }
//
//    public BigDecimal getD04() {
//        return d04;
//    }
//    public void setD04(BigDecimal d04) {
//        this.d04 = d04;
//    }
//
//    public BigDecimal getD05() {
//        return d05;
//    }
//    public void setD05(BigDecimal d05) {
//        this.d05 = d05;
//    }
//
//    public BigDecimal getD06() {
//        return d06;
//    }
//    public void setD06(BigDecimal d06) {
//        this.d06 = d06;
//    }
//
//    public BigDecimal getD07() {
//        return d07;
//    }
//    public void setD07(BigDecimal d07) {
//        this.d07 = d07;
//    }
//
//    public BigDecimal getD08() {
//        return d08;
//    }
//    public void setD08(BigDecimal d08) {
//        this.d08 = d08;
//    }
//
//    public BigDecimal getD09() {
//        return d09;
//    }
//    public void setD09(BigDecimal d09) {
//        this.d09 = d09;
//    }
//
//    public BigDecimal getD10() {
//        return d10;
//    }
//    public void setD10(BigDecimal d10) {
//        this.d10 = d10;
//    }
//
//    public BigDecimal getD11() {
//        return d11;
//    }
//    public void setD11(BigDecimal d11) {
//        this.d11 = d11;
//    }
//
//    public BigDecimal getD12() {
//        return d12;
//    }
//    public void setD12(BigDecimal d12) {
//        this.d12 = d12;
//    }
//
//    public BigDecimal getD13() {
//        return d13;
//    }
//    public void setD13(BigDecimal d13) {
//        this.d13 = d13;
//    }
//
//    public BigDecimal getD14() {
//        return d14;
//    }
//    public void setD14(BigDecimal d14) {
//        this.d14 = d14;
//    }
//
//    public BigDecimal getD15() {
//        return d15;
//    }
//    public void setD15(BigDecimal d15) {
//        this.d15 = d15;
//    }
//
//    public BigDecimal getD16() {
//        return d16;
//    }
//    public void setD16(BigDecimal d16) {
//        this.d16 = d16;
//    }
//
//    public BigDecimal getD17() {
//        return d17;
//    }
//    public void setD17(BigDecimal d17) {
//        this.d17 = d17;
//    }
//
//    public BigDecimal getD18() {
//        return d18;
//    }
//    public void setD18(BigDecimal d18) {
//        this.d18 = d18;
//    }
//
//    public BigDecimal getD19() {
//        return d19;
//    }
//    public void setD19(BigDecimal d19) {
//        this.d19 = d19;
//    }
//
//    public BigDecimal getD20() {
//        return d20;
//    }
//    public void setD20(BigDecimal d20) {
//        this.d20 = d20;
//    }
//
//    public BigDecimal getD21() {
//        return d21;
//    }
//    public void setD21(BigDecimal d21) {
//        this.d21 = d21;
//    }
//
//    public BigDecimal getD22() {
//        return d22;
//    }
//    public void setD22(BigDecimal d22) {
//        this.d22 = d22;
//    }
//
//    public BigDecimal getD23() {
//        return d23;
//    }
//    public void setD23(BigDecimal d23) {
//        this.d23 = d23;
//    }
//
//    public BigDecimal getD24() {
//        return d24;
//    }
//    public void setD24(BigDecimal d24) {
//        this.d24 = d24;
//    }
//
//    public BigDecimal getD25() {
//        return d25;
//    }
//    public void setD25(BigDecimal d25) {
//        this.d25 = d25;
//    }
//
//    public BigDecimal getD26() {
//        return d26;
//    }
//    public void setD26(BigDecimal d26) {
//        this.d26 = d26;
//    }
//
//    public BigDecimal getD27() {
//        return d27;
//    }
//    public void setD27(BigDecimal d27) {
//        this.d27 = d27;
//    }
//
//    public BigDecimal getD28() {
//        return d28;
//    }
//    public void setD28(BigDecimal d28) {
//        this.d28 = d28;
//    }
//
//    public BigDecimal getD29() {
//        return d29;
//    }
//    public void setD29(BigDecimal d29) {
//        this.d29 = d29;
//    }
//
//    public BigDecimal getD30() {
//        return d30;
//    }
//    public void setD30(BigDecimal d30) {
//        this.d30 = d30;
//    }
//
//    public BigDecimal getD31() {
//        return d31;
//    }
//    public void setD31(BigDecimal d31) {
//        this.d31 = d31;
//    }


//    public void setValueToDayField(int day, final BigDecimal value) {
//        switch (day) {
//            case 1: d01 = value; break;
//            case 2: d02 = value; break;
//            case 3: d03 = value; break;
//            case 4: d04 = value; break;
//            case 5: d05 = value; break;
//            case 6: d06 = value; break;
//            case 7: d07 = value; break;
//            case 8: d08 = value; break;
//            case 9: d09 = value; break;
//            case 10: d11 = value; break;
//            case 11: d11 = value; break;
//            case 12: d12 = value; break;
//            case 13: d13 = value; break;
//            case 14: d14 = value; break;
//            case 15: d15 = value; break;
//            case 16: d16 = value; break;
//            case 17: d17 = value; break;
//            case 18: d18 = value; break;
//            case 19: d19 = value; break;
//            case 20: d20 = value; break;
//            case 21: d21 = value; break;
//            case 22: d22 = value; break;
//            case 23: d23 = value; break;
//            case 24: d24 = value; break;
//            case 25: d25 = value; break;
//            case 26: d26 = value; break;
//            case 27: d27 = value; break;
//            case 28: d28 = value; break;
//            case 29: d29 = value; break;
//            case 30: d30 = value; break;
//            case 31: d31 = value; break;
//        }
//    }
//
//    private BigDecimal getValueFromDayField(int day) {
//        switch (day) {
//            case 1: return d01;
//            case 2: return getD02();
//            case 3: return getD03();
//            case 4: return getD04();
//            case 5: return getD05();
//            case 6: return getD06();
//            case 7: return getD07();
//            case 8: return getD08();
//            case 9: return getD09();
//            case 10: return getD10();
//            case 11: return getD11();
//            case 12: return getD12();
//            case 13: return getD13();
//            case 14: return getD14();
//            case 15: return getD15();
//            case 16: return getD16();
//            case 17: return getD17();
//            case 18: return getD18();
//            case 19: return getD19();
//            case 20: return getD20();
//            case 21: return getD21();
//            case 22: return getD22();
//            case 23: return getD23();
//            case 24: return getD24();
//            case 25: return getD25();
//            case 26: return getD26();
//            case 27: return getD27();
//            case 28: return getD28();
//            case 29: return getD29();
//            case 30: return getD30();
//            case 31: return getD31();
//            default: return null;
//        }
//    }
}
