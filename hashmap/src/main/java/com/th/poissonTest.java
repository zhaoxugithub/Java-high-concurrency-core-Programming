package com.th;

import com.google.common.math.IntMath;

import java.math.BigDecimal;

/**
 * @ClassName: poissonTest
 * @Description:
 * @Author: 唐欢
 * @Date: 2023/4/24 15:15
 * @Version 1.0
 */
public class poissonTest {
    private static String poisson(int k) {
        //泊松分布 Java
        double value = Math.exp(-0.5) * Math.pow(0.5, k) / IntMath.factorial(k);
        //格式化参数，保留10位小数。
        return new BigDecimal(value+"").setScale(10,BigDecimal.ROUND_DOWN).toPlainString();
    }

    public static void main(String[] args) {
        System.out.println(poisson(8));
    }
}