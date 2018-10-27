//
//  main.cpp
//  FastFourierTransformPolMultiplication
//  Time Complexity: O(nlog(n))
//  Created by Aakash Kataria on 27/10/18.
//  Copyright © 2018 Aakash Kataria. All rights reserved.
//

#include <iostream>
#include <cmath>
#include <vector>
using namespace std;

class Complex {
public:
    double real;
    double imaginary;
    Complex() {
    }
    Complex(double real1, double imaginary1) {
        real = real1;
        imaginary = imaginary1;
    }
    double getReal() {
        return real;
    }
    void setReal(double real1) {
        real = real1;
    }
    double getImaginary() {
        return imaginary;
    }
    void setImaginary(double imaginary1) {
        imaginary = imaginary1;
    }
    Complex add(Complex number){
        return Complex(real + number.real, imaginary + number.imaginary);
    }
    Complex subtract(Complex number){
        return Complex(real - number.real, imaginary - number.imaginary);
    }
    Complex multiply(Complex number){
        return Complex(real * number.real - imaginary * number.imaginary, real * number.imaginary + imaginary * number.real);
    }
    Complex getConjugate(){
        return Complex(real, -1 * imaginary);
    }
    Complex scalerDivide(Complex number){
        return Complex(real/number.real, imaginary/number.real);
    }
    Complex divide(Complex number){
        return multiply(number.getConjugate()).scalerDivide(number.multiply(number.getConjugate()));
    }
};

void fixPolSize(vector<int> &v) {
    int size = (int) v.size();
    if (pow(2, floor(log2(size))) != size){
        int newSize = (int) pow(2, floor(log2(size)) + 1);
        for (int i = size; i < newSize; i++)
            v.push_back(0);
    }
}

void degreefyAndMakeSizePowerOf2(vector<int> &v) {
    int size = (int) v.size();
    for (int i = 0; i < size - 1; i++){
        v.push_back(0);
    }
    fixPolSize(v);
}

int reverseBits(int x, int log2n){
    int n = 0;
    for (int i = 0; i < log2n; i++){
        n <<= 1;
        n |= (x & 1);
        x >>= 1;
    }
    return n;
}

Complex* bit_reverse_vec(Complex *coefficients, int size){
    Complex *vec = new Complex[size];
    double log2Val = log2(size);
    for (int i = 0; i < size; i++) vec[reverseBits(i, log2Val)] = coefficients[i];
    return vec;
}

Complex* iterativeFFT(Complex *coefficients, int coefficientCount, bool inverse){
    coefficients = bit_reverse_vec(coefficients, coefficientCount);
    Complex omega, nthRoot, t, u;
    int m;
    for (int i = 1; i <= log2(coefficientCount); i++) {
        m = pow(2, i);
        nthRoot = Complex(cos((2.0*M_PI)/m), sin((2.0*M_PI)/m));
        for (int j = 0; j < coefficientCount; j += m) {
            omega = Complex(1,0);
            for (int k = 0; k < m/2; k++) {
                if (inverse) t = omega.getConjugate().multiply(coefficients[j + k + m/2]);
                else t = omega.multiply(coefficients[j + k + m/2]);
                u = coefficients[j + k];
                coefficients[j + k] = u.add(t);
                coefficients[j + k + m/2] = u.subtract(t);
                omega = omega.multiply(nthRoot);
            }
        }
    }
    return coefficients;
}

vector<int> multiply(vector<int> v1, vector<int> v2){
    degreefyAndMakeSizePowerOf2(v1);
    degreefyAndMakeSizePowerOf2(v2);
    int size = (int) v1.size();
    Complex *pol1Complex = new Complex[size], *pol2Complex = new Complex[size], *pol3Complex = new Complex[size];
    for (int i = 0; i < size; i++){
        pol1Complex[i] = Complex(v1[i], 0);
        pol2Complex[i] = Complex(v2[i], 0);
    }
    pol1Complex = iterativeFFT(pol1Complex, size, false);
    pol2Complex = iterativeFFT(pol2Complex, size, false);
    for (int i = 0; i < size; i++) pol3Complex[i] = pol1Complex[i].multiply(pol2Complex[i]);
    pol3Complex = iterativeFFT(pol3Complex, size, true);
    vector<int> finalPol(size);
    for (int i = 0; i < size; i++) finalPol[i] = (pol3Complex[i].getReal()/size) + 0.5;
    return finalPol;
}

int main(int argc, const char * argv[]) {
    int n;
    cin>>n;
    vector<int> p1(n), p2(n);
    for (int i = 0; i < n; i++)
        cin>>p1[i];
    for (int i = 0; i < n; i++)
        cin>>p2[i];
    vector<int> res = multiply(p1, p2);
    for (int i = 0; i < res.size(); i++) 
        cout<<res[i]<<" ";
    return 0;
}
