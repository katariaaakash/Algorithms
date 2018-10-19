//  main.java
//  InverseFastFourierTransform
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

    private static Scanner scanner;
    private static int pointsCount;
    private static ArrayList<Complex> points;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        pointsCount = scanner.nextInt();
        points = new ArrayList<>(Collections.nCopies(pointsCount, new Complex(0,0)));
        for (int i = 0; i < pointsCount; i++){
            points.set(i, new Complex(scanner.nextDouble(), scanner.nextDouble()));
        }
        ArrayList<Complex> coefficients = ifft(points, pointsCount);
        for (int i = 0; i < pointsCount; i++){
            coefficients.set(i, coefficients.get(i).divide(new Complex(pointsCount, 0)));
        }

        for (int i = 0; i < pointsCount; i++){
            System.out.println("" + coefficients.get(i).getReal() + " " + coefficients.get(i).getImaginary() + "i");
        }
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
}