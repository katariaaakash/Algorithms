//  main.java
//  PolyNomialMultiplicationInO(nlogn)
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

    public Complex getConjugate(){
        return new Complex(this.real, -1 * this.imaginary);
    }

    public Complex scalerDivide(Complex number){
        return new Complex(this.real/number.real, this.imaginary/number.real);
    }

    public Complex divide(Complex number){
        return this.multiply(number.getConjugate()).scalerDivide(number.multiply(number.getConjugate()));
    }
}

public class Main {
    private static int pol1Size, pol2Size, pol3Size;
    private static ArrayList<Integer> pol1, pol2, pol3;
    private static ArrayList<Complex> pol1Complex, pol2Complex, pol3Complex;
    private static Scanner scanner;
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        pol1Size = scanner.nextInt();
        pol2Size = scanner.nextInt();
        pol1 = new ArrayList<>();
        pol2 = new ArrayList<>();
        pol1Complex = new ArrayList<>();
        pol2Complex = new ArrayList<>();
        pol3Complex = new ArrayList<>();
        for (int i = 0; i < pol1Size; i++)
            pol1.add(scanner.nextInt());
        for (int i = 0; i < pol2Size; i++)
            pol2.add(scanner.nextInt());
        if (pol1Size > pol2Size){
            for (int i = pol2Size; i < pol1Size; i++)
                pol2.add(0);
        }
        else if(pol1Size < pol2Size){
            for (int i = pol1Size; i < pol2Size; i++)
                pol1.add(0);
        }
        pol1 = degreefyAndMakeSizePowerOf2(pol1);
        pol2 = degreefyAndMakeSizePowerOf2(pol2);
        for (int i = 0; i < pol1.size(); i++)
            pol1Complex.add(new Complex(pol1.get(i), 0));
        for (int i = 0; i < pol2.size(); i++){
            pol2Complex.add(new Complex(pol2.get(i), 0));
        }
        ArrayList<Complex> points1 = fft(pol1Complex, pol1Complex.size());
        ArrayList<Complex> points2 = fft(pol2Complex, pol2Complex.size());
        ArrayList<Complex> points3 = new ArrayList<>();

        for (int i = 0; i < points1.size(); i++){
            points3.add(points1.get(i).multiply(points2.get(i)));
        }

        pol3Complex = ifft(points3, points3.size());
        pol3Size = pol3Complex.size();

        pol3 = smoothenCoefficients(pol3Complex);

        for (Integer coefficient : pol3) {
            System.out.println(coefficient);
        }
    }

    private static ArrayList<Integer> smoothenCoefficients(ArrayList<Complex> pol3Complex) {
        ArrayList<Integer> finalPol = new ArrayList<>();
        for (int i = 0; i < pol3Complex.size(); i++){
            finalPol.add(((int) Math.round(pol3Complex.get(i).getReal()))/pol3Complex.size());
        }
        return finalPol;
    }

    private static ArrayList<Complex> ifft(ArrayList<Complex> points, int pointsCount) {
        ArrayList<Complex> res = new ArrayList<>(Collections.nCopies(pointsCount, new Complex(0, 0)));
        if (pointsCount == 1) {
            res.set(0, points.get(0));
            return res;
        }
        ArrayList<Complex> evenPoints = new ArrayList<>(pointsCount / 2);
        ArrayList<Complex> oddPoints = new ArrayList<>(pointsCount / 2);
        Complex omega = new Complex(1, 0);
        Complex nthRoot = new Complex(Math.cos((2 * Math.PI) / pointsCount), Math.sin((2 * Math.PI) / pointsCount));
        for (int i = 0; i < pointsCount / 2; i++) {
            evenPoints.add(i, points.get(2 * i));
            oddPoints.add(i, points.get(2 * i + 1));
        }

        ArrayList<Complex> a_e_x2 = ifft(evenPoints, pointsCount / 2);
        ArrayList<Complex> a_o_x2 = ifft(oddPoints, pointsCount / 2);

        for (int i = 0; i < (pointsCount / 2); i++) {
            Complex point1 = a_e_x2.get(i).add(a_o_x2.get(i).multiply(omega.getConjugate()));
            Complex point2 = a_e_x2.get(i).subtract(a_o_x2.get(i).multiply(omega.getConjugate()));
            res.set(i, point1);
            res.set(i + (pointsCount) / 2, point2);
            omega = omega.multiply(nthRoot);
        }

        return res;
    }

    private static ArrayList<Complex> fft(ArrayList<Complex> coefficients, int coefficientCount) {
        ArrayList<Complex> res = new ArrayList<>(Collections.nCopies(coefficientCount, new Complex(0, 0)));
        if (coefficientCount == 1){
            res.set(0, coefficients.get(0));
            return res;
        }
        ArrayList<Complex> evenCoefficients = new ArrayList<>(coefficientCount/2);
        ArrayList<Complex> oddCoefficients = new ArrayList<>(coefficientCount/2);
        Complex omega = new Complex(1, 0);
        Complex nthRoot = new Complex(Math.cos((2*Math.PI)/coefficientCount), Math.sin((2*Math.PI)/coefficientCount));
        for (int i = 0; i < coefficientCount/2; i++){
            evenCoefficients.add(coefficients.get(2*i));
            oddCoefficients.add(coefficients.get(2*i + 1));
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

    private static ArrayList<Integer> degreefyAndMakeSizePowerOf2(ArrayList<Integer> pol) {
        ArrayList<Integer> polynomial = new ArrayList<>();
        polynomial.addAll(pol);
        for (int i = 0; i < pol.size() - 1; i++){
            polynomial.add(0);
        }
        return fixPolSize(polynomial, polynomial.size());
    }

    public static double log2(int n) {
        return (Math.log(n) / Math.log(2));
    }

    private static ArrayList<Integer> fixPolSize(ArrayList<Integer> pol, int polSize) {
        ArrayList<Integer> polynomial = new ArrayList<>();
        polynomial.addAll(pol);
        if (Math.pow(2, Math.floor(log2(polSize))) != polSize){
            int newSize = (int) Math.pow(2, Math.floor(log2(polSize)) + 1);
            for (int i = polSize; i < newSize; i++)
                polynomial.add(0);
        }
        return polynomial;
    }
}