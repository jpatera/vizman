package eu.japtor.vizman.backend.bean;

import eu.japtor.vizman.app.HasLogger;

import java.math.BigDecimal;

interface PruhAware {

    BigDecimal getD01();
    void setD01(BigDecimal d01);

    BigDecimal getD02();
    void setD02(BigDecimal d02);

    BigDecimal getD03();
    void setD03(BigDecimal d03);

    BigDecimal getD04();
    void setD04(BigDecimal d04);

    BigDecimal getD05();
    void setD05(BigDecimal d05);

    BigDecimal getD06();
    void setD06(BigDecimal d06);

    BigDecimal getD07();
    void setD07(BigDecimal d07);

    BigDecimal getD08();
    void setD08(BigDecimal d08);

    BigDecimal getD09();
    void setD09(BigDecimal d09);

    BigDecimal getD10();
    void setD10(BigDecimal d10);

    BigDecimal getD11();
    void setD11(BigDecimal d11);

    BigDecimal getD12();
    void setD12(BigDecimal d12);

    BigDecimal getD13();
    void setD13(BigDecimal d13);

    BigDecimal getD14();
    void setD14(BigDecimal d14);

    BigDecimal getD15();
    void setD15(BigDecimal d15);

    BigDecimal getD16();
    void setD16(BigDecimal d16);

    BigDecimal getD17();
    void setD17(BigDecimal d17);

    BigDecimal getD18();
    void setD18(BigDecimal d18);

    BigDecimal getD19();
    void setD19(BigDecimal d19);

    BigDecimal getD20();
    void setD20(BigDecimal d20);

    BigDecimal getD21();
    void setD21(BigDecimal d21);

    BigDecimal getD22();
    void setD22(BigDecimal d22);

    BigDecimal getD23();
    void setD23(BigDecimal d23);

    BigDecimal getD24();
    void setD24(BigDecimal d24);

    BigDecimal getD25();
    void setD25(BigDecimal d25);

    BigDecimal getD26();
    void setD26(BigDecimal d26);

    BigDecimal getD27();
    void setD27(BigDecimal d27);

    BigDecimal getD28();
    void setD28(BigDecimal d28);

    BigDecimal getD29();
    void setD29(BigDecimal d29);

    BigDecimal getD30();
    void setD30(BigDecimal d30);

    BigDecimal getD31();
    void setD31(BigDecimal d31);

    void setValueToDayField(int day, final BigDecimal value);

    BigDecimal getValueFromDayField(int day);
}
