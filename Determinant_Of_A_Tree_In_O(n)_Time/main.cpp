//
//  main.cpp
//  Determinant of a tree
//  Time Complexity: O(n)
//  Created by Aakash Kataria on 07/11/18.
//  Copyright © 2018 Aakash Kataria. All rights reserved.
//

#include <iostream>
#include <vector>
#include <map>
using namespace std;

double determinent(vector<vector<int>> &tree, vector<int> &order, double alpha, int root){
    vector<double> diag(tree.size(), alpha);
    vector<bool> children(tree.size(), false);
    map<pair<int, int>, bool> deleted;
    int zeroCount, zeroIndex = -1, parent;
    double reduction, det = 1.0;
    for (int i = 0; i < order.size(); i++) {
//        cout<<endl<<order[i]<<endl;
        children[order[i]] = true;
        zeroCount = 0;
        for (int j = 0; j < tree[order[i]].size(); j++)
            if (children[tree[order[i]][j]] && diag[tree[order[i]][j]] == 0 && deleted.find(make_pair(order[i], tree[order[i]][j])) == deleted.end()) zeroCount++;
//        cout<<zeroCount<<endl;
        if (tree[order[i]].size() <= 1 && order[i] != root) {}
        else if(zeroCount > 1){
            return 0.0;
        }
        else if(zeroCount == 1){
            diag[order[i]] = -1;
            zeroIndex = -1;
            for (int j = 0; j < tree[order[i]].size(); j++) {
                if (children[tree[order[i]][j]] && diag[tree[order[i]][j]] == 0 && deleted.find(make_pair(order[i], tree[order[i]][j])) == deleted.end()) {
                    zeroIndex = tree[order[i]][j];
                    break;
                }
            }
//            cout<<zeroIndex<<endl;
            diag[zeroIndex] = 1;
            parent = -1;
            for (int j = 0; j < tree[order[i]].size(); j++) {
                if (!children[tree[order[i]][j]] && deleted.find(make_pair(order[i], tree[order[i]][j])) == deleted.end()) {
                    parent = tree[order[i]][j];
                    break;
                }
            }
//            cout<<"parent "<<parent<<endl;
            deleted.insert(make_pair(make_pair(order[i], parent), true));
            deleted.insert(make_pair(make_pair(parent, order[i]), true));
        }
        else{
            reduction = 0.0;
            for (int j = 0; j < tree[order[i]].size(); j++) {
                if (children[tree[order[i]][j]] && deleted.find(make_pair(order[i], tree[order[i]][j])) == deleted.end()) {
                    reduction += (1.0/diag[tree[order[i]][j]]);
                }
            }
//            cout<<reduction<<endl;
            diag[order[i]] = alpha - reduction;
        }
//        cout<<diag[order[i]]<<endl;
    }
    for (int i = 0; i < order.size(); i++) {
        cout<<diag[order[i]]<<" ";
        det *= diag[order[i]];
    }
    cout<<endl;
    return det;
}

void postOrderTraversal(vector<vector<int>> &graph, vector<bool> &vis, vector<int> &order, int root){
    vis[root] = true;
    for (int i = 0; i < graph[root].size(); i++) {
        if (!vis[graph[root][i]]) {
            postOrderTraversal(graph, vis, order, graph[root][i]);
        }
    }
    order.push_back(root);
}
int main(int argc, const char *argv[]) {
    int n, x, y, r;
    cin>>n>>r;
    vector<vector<int>> tree(n);
    vector<int> order;
    vector<bool> vis(n, false);
    for (int i = 0; i < n-1; i++) {
        cin>>x>>y;
        tree[x-1].push_back(y-1);
        tree[y-1].push_back(x-1);
    }
    postOrderTraversal(tree, vis, order, r-1);
    for (int i = 0; i < order.size(); i++) {
        cout<<order[i]<<" ";
    }
    cout<<endl;
    double det = determinent(tree, order, 1.0, r-1);
    cout<<det;
    
    return 0;
}
