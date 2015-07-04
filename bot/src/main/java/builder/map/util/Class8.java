package builder.map.util;


import builder.map.cache.DataArchive;

public class Class8 {

    public Class8() {
    }

    public static final byte method6(AlgorithmData algorithmData) {
        return (byte) method12(8, algorithmData);
    }

    public static final void method7(AlgorithmData algorithmData) {
        int i = 0;
        int is[] = null;
        int is_0_[] = null;
        int is_1_[] = null;
        algorithmData.anInt31 = 1;
        if (AlgorithmData.anIntArray40 == null)
            AlgorithmData.anIntArray40 = new int[algorithmData.anInt31 * 0x186a0];
        boolean bool = true;
        while (bool) {
            byte i_2_ = method6(algorithmData);
            if (i_2_ == 23)
                break;
            i_2_ = method6(algorithmData);
            i_2_ = method6(algorithmData);
            i_2_ = method6(algorithmData);
            i_2_ = method6(algorithmData);
            i_2_ = method6(algorithmData);
            algorithmData.anInt32++;
            i_2_ = method6(algorithmData);
            i_2_ = method6(algorithmData);
            i_2_ = method6(algorithmData);
            i_2_ = method6(algorithmData);
            i_2_ = println(algorithmData);
            if (i_2_ != 0)
                algorithmData.aBool28 = true;
            else
                algorithmData.aBool28 = false;
            if (algorithmData.aBool28)
                System.out.println(DataArchive.getValue(1266));
            algorithmData.anInt33 = 0;
            int i_3_ = method6(algorithmData);
            algorithmData.anInt33 = algorithmData.anInt33 << 8 | i_3_ & 0xff;
            i_3_ = method6(algorithmData);
            algorithmData.anInt33 = algorithmData.anInt33 << 8 | i_3_ & 0xff;
            i_3_ = method6(algorithmData);
            algorithmData.anInt33 = algorithmData.anInt33 << 8 | i_3_ & 0xff;
            for (int i_4_ = 0; i_4_ < 16; i_4_++) {
                byte i_5_ = println(algorithmData);
                if (i_5_ == 1)
                    algorithmData.aBoolArray43[i_4_] = true;
                else
                    algorithmData.aBoolArray43[i_4_] = false;
            }

            for (int i_6_ = 0; i_6_ < 256; i_6_++)
                algorithmData.aBoolArray42[i_6_] = false;

            for (int i_7_ = 0; i_7_ < 16; i_7_++)
                if (algorithmData.aBoolArray43[i_7_]) {
                    for (int i_8_ = 0; i_8_ < 16; i_8_++) {
                        byte i_9_ = println(algorithmData);
                        if (i_9_ == 1)
                            algorithmData.aBoolArray42[i_7_ * 16 + i_8_] = true;
                    }

                }

            method9(algorithmData);
            int i_10_ = algorithmData.anInt41 + 2;
            int i_11_ = method12(3, algorithmData);
            int i_12_ = method12(15, algorithmData);
            for (int i_13_ = 0; i_13_ < i_12_; i_13_++) {
                int i_14_ = 0;
                do {
                    byte i_15_ = println(algorithmData);
                    if (i_15_ == 0)
                        break;
                    i_14_++;
                } while (true);
                algorithmData.aByteArray48[i_13_] = (byte) i_14_;
            }

            byte is_16_[] = new byte[6];
            for (byte i_17_ = 0; i_17_ < i_11_; i_17_++)
                is_16_[i_17_] = i_17_;

            for (int i_18_ = 0; i_18_ < i_12_; i_18_++) {
                byte i_19_ = algorithmData.aByteArray48[i_18_];
                byte i_20_ = is_16_[i_19_];
                for (; i_19_ > 0; i_19_--)
                    is_16_[i_19_] = is_16_[i_19_ - 1];

                is_16_[0] = i_20_;
                algorithmData.aByteArray47[i_18_] = i_20_;
            }

            for (int i_21_ = 0; i_21_ < i_11_; i_21_++) {
                int i_22_ = method12(5, algorithmData);
                for (int i_23_ = 0; i_23_ < i_10_; i_23_++) {
                    do {
                        byte i_24_ = println(algorithmData);
                        if (i_24_ == 0)
                            break;
                        i_24_ = println(algorithmData);
                        if (i_24_ == 0)
                            i_22_++;
                        else
                            i_22_--;
                    } while (true);
                    algorithmData.aByteArrayArray49[i_21_][i_23_] = (byte) i_22_;
                }

            }

            for (int i_25_ = 0; i_25_ < i_11_; i_25_++) {
                int i_26_ = 32;
                byte i_27_ = 0;
                for (int i_28_ = 0; i_28_ < i_10_; i_28_++) {
                    if (algorithmData.aByteArrayArray49[i_25_][i_28_] > i_27_)
                        i_27_ = algorithmData.aByteArrayArray49[i_25_][i_28_];
                    if (algorithmData.aByteArrayArray49[i_25_][i_28_] < i_26_)
                        i_26_ = algorithmData.aByteArrayArray49[i_25_][i_28_];
                }

                method10(algorithmData.anIntArrayArray50[i_25_], algorithmData.anIntArrayArray51[i_25_], algorithmData.anIntArrayArray52[i_25_], algorithmData.aByteArrayArray49[i_25_], i_26_, i_27_, i_10_);
                algorithmData.anIntArray53[i_25_] = i_26_;
            }

            int i_29_ = algorithmData.anInt41 + 1;
            int i_30_ = -1;
            int i_31_ = 0;
            for (int i_32_ = 0; i_32_ <= 255; i_32_++)
                algorithmData.anIntArray36[i_32_] = 0;

            int i_33_ = 4095;
            for (int i_34_ = 15; i_34_ >= 0; i_34_--) {
                for (int i_35_ = 15; i_35_ >= 0; i_35_--) {
                    algorithmData.aByteArray45[i_33_] = (byte) (i_34_ * 16 + i_35_);
                    i_33_--;
                }

                algorithmData.anIntArray46[i_34_] = i_33_ + 1;
            }

            int i_36_ = 0;
            if (i_31_ == 0) {
                i_30_++;
                i_31_ = 50;
                byte i_37_ = algorithmData.aByteArray47[i_30_];
                i = algorithmData.anIntArray53[i_37_];
                is = algorithmData.anIntArrayArray50[i_37_];
                is_1_ = algorithmData.anIntArrayArray52[i_37_];
                is_0_ = algorithmData.anIntArrayArray51[i_37_];
            }
            i_31_--;
            int i_38_ = i;
            int i_39_;
            int i_40_;
            for (i_40_ = method12(i_38_, algorithmData); i_40_ > is[i_38_]; i_40_ = i_40_ << 1 | i_39_) {
                i_38_++;
                i_39_ = println(algorithmData);
            }

            for (int i_41_ = is_1_[i_40_ - is_0_[i_38_]]; i_41_ != i_29_; )
                if (i_41_ == 0 || i_41_ == 1) {
                    int i_42_ = -1;
                    int i_43_ = 1;
                    do {
                        if (i_41_ == 0)
                            i_42_ += 1 * i_43_;
                        else if (i_41_ == 1)
                            i_42_ += 2 * i_43_;
                        i_43_ *= 2;
                        if (i_31_ == 0) {
                            i_30_++;
                            i_31_ = 50;
                            byte i_44_ = algorithmData.aByteArray47[i_30_];
                            i = algorithmData.anIntArray53[i_44_];
                            is = algorithmData.anIntArrayArray50[i_44_];
                            is_1_ = algorithmData.anIntArrayArray52[i_44_];
                            is_0_ = algorithmData.anIntArrayArray51[i_44_];
                        }
                        i_31_--;
                        int i_45_ = i;
                        int i_46_;
                        int i_47_;
                        for (i_47_ = method12(i_45_, algorithmData); i_47_ > is[i_45_]; i_47_ = i_47_ << 1 | i_46_) {
                            i_45_++;
                            i_46_ = println(algorithmData);
                        }

                        i_41_ = is_1_[i_47_ - is_0_[i_45_]];
                    } while (i_41_ == 0 || i_41_ == 1);
                    i_42_++;
                    int i_48_ = algorithmData.aByteArray44[algorithmData.aByteArray45[algorithmData.anIntArray46[0]] & 0xff];
                    algorithmData.anIntArray36[i_48_ & 0xff] += i_42_;
                    for (; i_42_ > 0; i_42_--) {
                        AlgorithmData.anIntArray40[i_36_] = i_48_ & 0xff;
                        i_36_++;
                    }

                } else {
                    int i_49_ = i_41_ - 1;
                    byte i_50_;
                    if (i_49_ < 16) {
                        int i_51_ = algorithmData.anIntArray46[0];
                        i_50_ = algorithmData.aByteArray45[i_51_ + i_49_];
                        for (; i_49_ > 3; i_49_ -= 4) {
                            int i_52_ = i_51_ + i_49_;
                            algorithmData.aByteArray45[i_52_] = algorithmData.aByteArray45[i_52_ - 1];
                            algorithmData.aByteArray45[i_52_ - 1] = algorithmData.aByteArray45[i_52_ - 2];
                            algorithmData.aByteArray45[i_52_ - 2] = algorithmData.aByteArray45[i_52_ - 3];
                            algorithmData.aByteArray45[i_52_ - 3] = algorithmData.aByteArray45[i_52_ - 4];
                        }

                        for (; i_49_ > 0; i_49_--)
                            algorithmData.aByteArray45[i_51_ + i_49_] = algorithmData.aByteArray45[(i_51_ + i_49_) - 1];

                        algorithmData.aByteArray45[i_51_] = i_50_;
                    } else {
                        int i_53_ = i_49_ / 16;
                        int i_54_ = i_49_ % 16;
                        int i_55_ = algorithmData.anIntArray46[i_53_] + i_54_;
                        i_50_ = algorithmData.aByteArray45[i_55_];
                        for (; i_55_ > algorithmData.anIntArray46[i_53_]; i_55_--)
                            algorithmData.aByteArray45[i_55_] = algorithmData.aByteArray45[i_55_ - 1];

                        algorithmData.anIntArray46[i_53_]++;
                        for (; i_53_ > 0; i_53_--) {
                            algorithmData.anIntArray46[i_53_]--;
                            algorithmData.aByteArray45[algorithmData.anIntArray46[i_53_]] = algorithmData.aByteArray45[(algorithmData.anIntArray46[i_53_ - 1] + 16) - 1];
                        }

                        algorithmData.anIntArray46[0]--;
                        algorithmData.aByteArray45[algorithmData.anIntArray46[0]] = i_50_;
                        if (algorithmData.anIntArray46[0] == 0) {
                            int i_56_ = 4095;
                            for (int i_57_ = 15; i_57_ >= 0; i_57_--) {
                                for (int i_58_ = 15; i_58_ >= 0; i_58_--) {
                                    algorithmData.aByteArray45[i_56_] = algorithmData.aByteArray45[algorithmData.anIntArray46[i_57_] + i_58_];
                                    i_56_--;
                                }

                                algorithmData.anIntArray46[i_57_] = i_56_ + 1;
                            }

                        }
                    }
                    algorithmData.anIntArray36[algorithmData.aByteArray44[i_50_ & 0xff] & 0xff]++;
                    AlgorithmData.anIntArray40[i_36_] = algorithmData.aByteArray44[i_50_ & 0xff] & 0xff;
                    i_36_++;
                    if (i_31_ == 0) {
                        i_30_++;
                        i_31_ = 50;
                        byte i_59_ = algorithmData.aByteArray47[i_30_];
                        i = algorithmData.anIntArray53[i_59_];
                        is = algorithmData.anIntArrayArray50[i_59_];
                        is_1_ = algorithmData.anIntArrayArray52[i_59_];
                        is_0_ = algorithmData.anIntArrayArray51[i_59_];
                    }
                    i_31_--;
                    int i_60_ = i;
                    int i_61_;
                    int i_62_;
                    for (i_62_ = method12(i_60_, algorithmData); i_62_ > is[i_60_]; i_62_ = i_62_ << 1 | i_61_) {
                        i_60_++;
                        i_61_ = println(algorithmData);
                    }

                    i_41_ = is_1_[i_62_ - is_0_[i_60_]];
                }

            algorithmData.anInt27 = 0;
            algorithmData.aByte26 = 0;
            algorithmData.anIntArray38[0] = 0;
            for (int i_41_ = 1; i_41_ <= 256; i_41_++)
                algorithmData.anIntArray38[i_41_] = algorithmData.anIntArray36[i_41_ - 1];

            for (int i_41_ = 1; i_41_ <= 256; i_41_++)
                algorithmData.anIntArray38[i_41_] += algorithmData.anIntArray38[i_41_ - 1];

            for (int i_41_ = 0; i_41_ < i_36_; i_41_++) {
                int i_63_ = (byte) (AlgorithmData.anIntArray40[i_41_] & 0xff);
                AlgorithmData.anIntArray40[algorithmData.anIntArray38[i_63_ & 0xff]] |= i_41_ << 8;
                algorithmData.anIntArray38[i_63_ & 0xff]++;
            }

            algorithmData.anInt34 = AlgorithmData.anIntArray40[algorithmData.anInt33] >> 8;
            algorithmData.anInt37 = 0;
            algorithmData.anInt34 = AlgorithmData.anIntArray40[algorithmData.anInt34];
            algorithmData.anInt35 = (byte) (algorithmData.anInt34 & 0xff);
            algorithmData.anInt34 >>= 8;
            algorithmData.anInt37++;
            algorithmData.anInt54 = i_36_;
            method8(algorithmData);
            if (algorithmData.anInt37 == algorithmData.anInt54 + 1 && algorithmData.anInt27 == 0)
                bool = true;
            else
                bool = false;
        }
    }

    public static final void method8(AlgorithmData algorithmData) {
        byte i = algorithmData.aByte26;
        int i_64_ = algorithmData.anInt27;
        int i_65_ = algorithmData.anInt37;
        int i_66_ = algorithmData.anInt35;
        int is[] = AlgorithmData.anIntArray40;
        int i_67_ = algorithmData.anInt34;
        byte is_68_[] = algorithmData.aByteArray21;
        int i_69_ = algorithmData.anInt22;
        int i_70_ = algorithmData.anInt23;
        int i_71_ = i_70_;
        int i_72_ = algorithmData.anInt54 + 1;
        label0:
        do {
            if (i_64_ > 0) {
                do {
                    if (i_70_ == 0)
                        break label0;
                    if (i_64_ == 1)
                        break;
                    is_68_[i_69_] = i;
                    i_64_--;
                    i_69_++;
                    i_70_--;
                } while (true);
                if (i_70_ == 0) {
                    i_64_ = 1;
                    break;
                }
                is_68_[i_69_] = i;
                i_69_++;
                i_70_--;
            }
            boolean bool = true;
            while (bool) {
                bool = false;
                if (i_65_ == i_72_) {
                    i_64_ = 0;
                    break label0;
                }
                i = (byte) i_66_;
                i_67_ = is[i_67_];
                int i_73_ = (byte) (i_67_ & 0xff);
                i_67_ >>= 8;
                i_65_++;
                if (i_73_ != i_66_) {
                    i_66_ = i_73_;
                    if (i_70_ == 0) {
                        i_64_ = 1;
                    } else {
                        is_68_[i_69_] = i;
                        i_69_++;
                        i_70_--;
                        bool = true;
                        continue;
                    }
                    break label0;
                }
                if (i_65_ != i_72_)
                    continue;
                if (i_70_ == 0) {
                    i_64_ = 1;
                    break label0;
                }
                is_68_[i_69_] = i;
                i_69_++;
                i_70_--;
                bool = true;
            }
            i_64_ = 2;
            i_67_ = is[i_67_];
            int i_74_ = (byte) (i_67_ & 0xff);
            i_67_ >>= 8;
            if (++i_65_ != i_72_)
                if (i_74_ != i_66_) {
                    i_66_ = i_74_;
                } else {
                    i_64_ = 3;
                    i_67_ = is[i_67_];
                    int i_75_ = (byte) (i_67_ & 0xff);
                    i_67_ >>= 8;
                    if (++i_65_ != i_72_)
                        if (i_75_ != i_66_) {
                            i_66_ = i_75_;
                        } else {
                            i_67_ = is[i_67_];
                            int i_76_ = (byte) (i_67_ & 0xff);
                            i_67_ >>= 8;
                            i_65_++;
                            i_64_ = (i_76_ & 0xff) + 4;
                            i_67_ = is[i_67_];
                            i_66_ = (byte) (i_67_ & 0xff);
                            i_67_ >>= 8;
                            i_65_++;
                        }
                }
        } while (true);
        int i_77_ = algorithmData.anInt24;
        algorithmData.anInt24 += i_71_ - i_70_;
        if (algorithmData.anInt24 < i_77_)
            algorithmData.anInt25++;
        algorithmData.aByte26 = i;
        algorithmData.anInt27 = i_64_;
        algorithmData.anInt37 = i_65_;
        algorithmData.anInt35 = i_66_;
        AlgorithmData.anIntArray40 = is;
        algorithmData.anInt34 = i_67_;
        algorithmData.aByteArray21 = is_68_;
        algorithmData.anInt22 = i_69_;
        algorithmData.anInt23 = i_70_;
    }

    public static final void method9(AlgorithmData algorithmData) {
        algorithmData.anInt41 = 0;
        for (int i = 0; i < 256; i++)
            if (algorithmData.aBoolArray42[i]) {
                algorithmData.aByteArray44[algorithmData.anInt41] = (byte) i;
                algorithmData.anInt41++;
            }

    }

    public static final byte println(AlgorithmData algorithmData) {
        return (byte) method12(1, algorithmData);
    }

    public static final void method10(int is[], int is_78_[], int is_79_[], byte is_80_[], int i, int i_81_, int i_82_) {
        int i_83_ = 0;
        for (int i_84_ = i; i_84_ <= i_81_; i_84_++) {
            for (int i_85_ = 0; i_85_ < i_82_; i_85_++)
                if (is_80_[i_85_] == i_84_) {
                    is_79_[i_83_] = i_85_;
                    i_83_++;
                }

        }

        for (int i_86_ = 0; i_86_ < 23; i_86_++)
            is_78_[i_86_] = 0;

        for (int i_87_ = 0; i_87_ < i_82_; i_87_++)
            is_78_[is_80_[i_87_] + 1]++;

        for (int i_88_ = 1; i_88_ < 23; i_88_++)
            is_78_[i_88_] += is_78_[i_88_ - 1];

        for (int i_89_ = 0; i_89_ < 23; i_89_++)
            is[i_89_] = 0;

        int i_90_ = 0;
        for (int i_91_ = i; i_91_ <= i_81_; i_91_++) {
            i_90_ += is_78_[i_91_ + 1] - is_78_[i_91_];
            is[i_91_] = i_90_ - 1;
            i_90_ <<= 1;
        }

        for (int i_92_ = i + 1; i_92_ <= i_81_; i_92_++)
            is_78_[i_92_] = (is[i_92_ - 1] + 1 << 1) - is_78_[i_92_];

    }

    public static final int method11(byte des[], int i, byte src[], int i_94_, int i_95_) {
        AlgorithmData algorithmData = aAlgorithmData_57;
        aAlgorithmData_57.aByteArray16 = src;
        aAlgorithmData_57.anInt17 = i_95_;
        aAlgorithmData_57.aByteArray21 = des;
        aAlgorithmData_57.anInt22 = 0;
        aAlgorithmData_57.anInt18 = i_94_;
        aAlgorithmData_57.anInt23 = i;
        aAlgorithmData_57.anInt30 = 0;
        aAlgorithmData_57.anInt29 = 0;
        aAlgorithmData_57.anInt19 = 0;
        aAlgorithmData_57.anInt20 = 0;
        aAlgorithmData_57.anInt24 = 0;
        aAlgorithmData_57.anInt25 = 0;
        aAlgorithmData_57.anInt32 = 0;
        method7(aAlgorithmData_57);
        i -= aAlgorithmData_57.anInt23;
        return i;
    }

    public static final int method12(int i, AlgorithmData algorithmData) {
        int i_98_;
        do {
            if (algorithmData.anInt30 >= i) {
                int i_99_ = algorithmData.anInt29 >> algorithmData.anInt30 - i & (1 << i) - 1;
                algorithmData.anInt30 -= i;
                i_98_ = i_99_;
                break;
            }
            algorithmData.anInt29 = algorithmData.anInt29 << 8 | algorithmData.aByteArray16[algorithmData.anInt17] & 0xff;
            algorithmData.anInt30 += 8;
            algorithmData.anInt17++;
            algorithmData.anInt18--;
            algorithmData.anInt19++;
            if (algorithmData.anInt19 == 0)
                algorithmData.anInt20++;
        } while (true);
        return i_98_;
    }

    public static AlgorithmData aAlgorithmData_57 = new AlgorithmData();

}
