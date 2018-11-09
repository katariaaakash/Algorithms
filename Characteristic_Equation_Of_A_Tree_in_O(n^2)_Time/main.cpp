//
//  main.cpp
//  Characteristic Equation of a tree
//  Time Complexity O(n^2)
//  Created by Aakash Kataria on 07/11/18.
//  Copyright © 2018 Aakash Kataria. All rights reserved.
//

#include <iostream>
#include <cmath>
#include <vector>
#include <climits>
#include <map>
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
    Complex getNegative(){
        return Complex(-1*real, -1*imaginary);
    }
    Complex scalerDivide(Complex number){
        return Complex(real/number.real, imaginary/number.real);
    }
    Complex divide(Complex number){
        if ((number.getReal() == 0.0 && number.getImaginary() == 0.0)) {
            return Complex(0.0, 0.0);
        }
        else return multiply(number.getConjugate()).scalerDivide(number.multiply(number.getConjugate()));
    }
};

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


pair<Complex, Complex> dfs(vector<vector<int>> &tree, Complex x, int root, vector<bool> &vis){
    vis[root] = true;
    if (tree[root].size() == 1 && vis[tree[root][0]]) {
        return {x, Complex(1.0, 0.0)};
    }
    Complex fdash = Complex(1.0, 0.0), sum = Complex(0.0, 0.0);
    for (int i = 0; i < tree[root].size(); i++) {
        if (!vis[tree[root][i]]) {
            pair<Complex, Complex> pr = dfs(tree, x, tree[root][i], vis);
            fdash = fdash.multiply(pr.first);
            if (fdash.getReal() == 0.0 && fdash.getImaginary() == 0.0) return make_pair(Complex(0.0, 0.0), Complex(0.0, 0.0));
            sum = sum.add((pr.second).divide(pr.first));
        }
    }
    if (fdash.getReal() != 0.0 && fdash.getImaginary() != 0.0) return make_pair(fdash.multiply(x.subtract(sum)), fdash);
    else return make_pair(Complex(0.0, 0.0), Complex(0.0, 0.0));
}

int nearest2Power(int n){
    if (pow(2, floor(log2(n))) != n) {
        return pow(2, floor(log2(n)) + 1);
    }
    return n;
}
vector<int> getCharisticPolynomial(vector<vector<int>> &tree, int root){
    vector<bool> vis(tree.size(), false);
    int m = nearest2Power(tree.size() + 1);
    Complex *points = new Complex[m];
    Complex nthRoot = Complex(cos((2.0*M_PI)/m), sin((2.0*M_PI)/m));
    Complex omega = Complex(1.0, 0.0);
    for (int i = 0; i < m; i++) {
        points[i] = dfs(tree, omega, root, vis).first;
        vis.clear();
        vis = vector<bool>(tree.size(), false);
        omega = omega.multiply(nthRoot);
    }
    Complex *coefficients = iterativeFFT(points, m, true);
    vector<int> coeff;
    for (int i = 0; i < m; i++) {
        coeff.push_back(round(coefficients[i].getReal()/(1.0*m)));
    }
    while (!coeff.empty() && coeff.back() == 0) {
        coeff.pop_back();
    }
    return coeff;
}

int main(){
    int n, r, x, y;
    cin>>n>>r;
    vector<vector<int>> tree(n);
    vector<int> charPol;
    for (int i = 0; i < n - 1; i++) {
        cin>>x>>y;
        tree[x-1].push_back(y-1);
        tree[y-1].push_back(x-1);
    }
    charPol = getCharisticPolynomial(tree, r-1);
    for (int i = 0; i < charPol.size(); i++) {
        cout<<charPol[i]<<" ";
    }
    cout<<endl;
}
