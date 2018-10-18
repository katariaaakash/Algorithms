//  main.java
//  FastFourierTransform
//  Created by Aakash Kataria on 19/10/18.
//  Copyright © 2018 Aakash Kataria. All rights reserved.

package com.aakashkataria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

class Complex {
    double real;
    double imaginary;

    public Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public double getReal() {
        return real;
    }

    public void setReal(double real) {
        this.real = real;
    }

    public double getImaginary() {
        return imaginary;
    }

    public void setImaginary(double imaginary) {
        this.imaginary = imaginary;
    }

    public Complex add(Complex number){
        return new Complex(this.real + number.real, this.imaginary + number.imaginary);
    }

    public Complex subtract(Complex number){
        return new Complex(this.real - number.real, this.imaginary - number.imaginary);
    }

    public Complex multiply(Complex number){
        return new Complex(this.real * number.real - this.imaginary * number.imaginary,
                this.real * number.imaginary + this.imaginary * number.real);
    }
}

public class Main {

    private static Scanner scanner;
    private static int coefficientCount;
    private static ArrayList<Integer> coefficients;

    public static double log2(int n) {
        return (Math.log(n) / Math.log(2));
    }

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        coefficientCount = scanner.nextInt();
        coefficients = new ArrayList<>(Collections.nCopies(coefficientCount, 0));
        for (int i = 0; i < coefficientCount; i++) {
            coefficients.set(i, scanner.nextInt());
        }
        if (Math.pow(2, log2(coefficients.size())) != coefficients.size()){
            int newSize = (int) Math.pow(2, Math.floor(log2(coefficients.size())) + 1);
            for (int i = coefficientCount; i < newSize; i++){
                coefficients.add(0);
            }
            coefficientCount = coefficients.size();
        }
        ArrayList<Complex> points = fft(coefficients, coefficientCount);

        for (Complex point: points){
            System.out.println("" + point.getReal() + " i" + point.getImaginary());
        }
    }

    private static ArrayList<Complex> fft(ArrayList<Integer> coefficients, int coefficientCount) {
        ArrayList<Complex> res = new ArrayList<>(Collections.nCopies(coefficientCount, new Complex(0, 0)));
        if (coefficientCount == 1){
            res.set(0, new Complex(coefficients.get(0),0));
            return res;
        }
        ArrayList<Integer> evenCoefficients = new ArrayList<>();
        ArrayList<Integer> oddCoefficients = new ArrayList<>();
        Complex omega = new Complex(1, 0);
        Complex nthRoot = new Complex(Math.cos((2*Math.PI)/coefficientCount), Math.sin((2*Math.PI)/coefficientCount));
        for (int i = 0; i < coefficientCount; i += 2){
            evenCoefficients.add(coefficients.get(i));
            oddCoefficients.add(coefficients.get(i+1));
        }

        ArrayList<Complex> a_e_x2 = fft(evenCoefficients, coefficientCount/2);
        ArrayList<Complex> a_o_x2 = fft(oddCoefficients, coefficientCount/2);

        for (int i = 0; i < (coefficientCount/2); i++){
            Complex point1 = a_e_x2.get(i).add(a_o_x2.get(i).multiply(omega));
            Complex point2 = a_e_x2.get(i).subtract(a_o_x2.get(i).multiply(omega));
            res.set(i, point1);
            res.set(i + (coefficientCount)/2, point2);
            omega = omega.multiply(nthRoot);
        }

        return res;
    }
}