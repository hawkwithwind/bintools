//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.extend;

import java.util.Random;

public class IntConvert {
    private static final int XOR1 = 422952728;
    private static final int XOR2 = 212464431;
    private static final int XOR3 = -923452239;
    private static final int NOT1 = 1400848159;
    private static final int NOT2 = 1847296445;
    private static final int NOT3 = -1788595985;
    private static final byte[] MARTIX_A = new byte[]{3, 4, 4, 5};
    private static final byte[] MARTIX_B = new byte[]{-5, 4, 4, -3};
    private static final int[] SORT1_A = new int[]{13, 29, 2, 16, 9, 17, 27, 1, 6, 5, 7, 28, 20, 25, 0, 23, 3, 18, 4, 11, 14, 31, 15, 12, 21, 30, 10, 8, 19, 22, 26, 24};
    private static final int[] SORT1_B = new int[]{10, 6, 30, 20, 25, 1, 18, 0, 16, 2, 7, 19, 3, 14, 26, 28, 9, 11, 31, 8, 12, 5, 27, 4, 21, 23, 22, 13, 15, 29, 24, 17};
    private static final int[] SORT2_A = new int[]{19, 23, 20, 16, 27, 30, 25, 8, 4, 18, 11, 29, 13, 2, 9, 28, 17, 5, 22, 14, 1, 10, 15, 3, 7, 12, 24, 21, 6, 31, 0, 26};
    private static final int[] SORT2_B = new int[]{2, 26, 20, 16, 27, 0, 25, 5, 30, 13, 4, 29, 31, 22, 15, 28, 9, 12, 19, 6, 21, 10, 17, 24, 7, 3, 14, 23, 8, 18, 11, 1};
    private static final int[] SORT3_A = new int[]{28, 8, 12, 10, 15, 1, 16, 27, 19, 17, 29, 0, 18, 2, 3, 23, 26, 11, 6, 21, 31, 30, 7, 20, 24, 14, 13, 4, 22, 9, 25, 5};
    private static final int[] SORT3_B = new int[]{11, 10, 21, 31, 24, 15, 1, 7, 16, 3, 12, 8, 23, 19, 22, 25, 27, 6, 5, 29, 14, 28, 2, 30, 9, 13, 0, 4, 17, 18, 26, 20};

    public IntConvert() {
    }

    public static int convert1(int num) {
        int num1 = notInt(sortInt(num, SORT1_A), 1400848159) ^ 422952728;
        int numx1 = bytes2int(multiBytes(int2bytes(num1), MARTIX_A));
        int num2 = notInt(sortInt(numx1, SORT2_A), 1847296445) ^ 212464431;
        int numx2 = bytes2int(multiBytes(int2bytes(num2), MARTIX_A));
        int num3 = notInt(sortInt(numx2, SORT3_A), -1788595985) ^ -923452239;
        int numx3 = bytes2int(multiBytes(int2bytes(num3), MARTIX_A));
        return numx3;
    }

    public static int convert2(int num) {
        int numx3 = bytes2int(multiBytes(int2bytes(num), MARTIX_B));
        int num3 = sortInt(notInt(numx3 ^ -923452239, -1788595985), SORT3_B);
        int numx2 = bytes2int(multiBytes(int2bytes(num3), MARTIX_B));
        int num2 = sortInt(notInt(numx2 ^ 212464431, 1847296445), SORT2_B);
        int numx1 = bytes2int(multiBytes(int2bytes(num2), MARTIX_B));
        int num1 = sortInt(notInt(numx1 ^ 422952728, 1400848159), SORT1_B);
        return num1;
    }

    protected static int notInt(int val, int param) {
        for (int i = 0; i < 32; ++i) {
            int mod = 1 << i;
            boolean not = (mod & param) != 0;
            if (not) {
                if ((val & mod) != 0) {
                    val ^= mod;
                } else {
                    val |= mod;
                }
            }
        }

        return val;
    }

    protected static int sortInt(int val, int[] params) {
        int newVal = 0;

        for (int i = 0; i < params.length; ++i) {
            newVal <<= 1;
            int mod = 1 << params[i];
            if ((val & mod) != 0) {
                newVal |= 1;
            }
        }

        return newVal;
    }

    protected static byte[] multiBytes(byte[] data1, byte[] data2) {
        byte[] result = new byte[]{(byte) (data1[0] * data2[0] + data1[1] * data2[2]), (byte) (data1[0] * data2[1] + data1[1] * data2[3]), (byte) (data1[2] * data2[0] + data1[3] * data2[2]), (byte) (data1[2] * data2[1] + data1[3] * data2[3])};
        return result;
    }

    protected static byte[] int2bytes(int val) {
        byte[] data = new byte[4];

        for (int i = 0; i < data.length; ++i) {
            data[i] = (byte) (val >> i * 8 & 255);
        }

        return data;
    }

    protected static int bytes2int(byte[] data) {
        int val = 0;

        for (int i = 0; i < 4; ++i) {
            val |= (data[i] & 255) << i * 8;
        }

        return val;
    }

    protected static int[] genSortA() {
        int length = 32;
        int[] sortA = new int[length];
        Random rand = new Random(System.currentTimeMillis());

        int index;
        for (int i = 0; i < 32; sortA[index] = i++) {
            index = rand.nextInt(i + 1);
            System.arraycopy(sortA, index, sortA, index + 1, i - index);
        }

        return sortA;
    }

    protected static int[] genSortB(int[] sortA) {
        int[] sortB = new int[sortA.length];

        for (int i = sortA.length - 1; i >= 0; --i) {
            int index = -1;

            for (int j = 0; j < sortA.length; ++j) {
                if (sortA[j] == i) {
                    index = j;
                    break;
                }
            }

            sortB[sortA.length - 1 - i] = sortA.length - 1 - index;
        }

        return sortB;
    }

    protected static void printSort(int[] sort) {
        for (int i = 0; i < sort.length; ++i) {
            if (i == 0) {
                System.out.print("[");
            } else {
                System.out.print(",");
            }

            System.out.print(Integer.toString(sort[i]));
        }

        System.out.println("]");
    }
}
