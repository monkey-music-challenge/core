# <höjd> <bredd> <flyttal: densitet väggar> <flyttal: densitet tracks>

#include <algorithm>
#include <iostream>
#include <cassert>
#include <cstdlib>
#include <string>
#include <vector>
#include <ctime>
#include <queue>
 
using namespace std;
 
vector<string> map;
int width, height;
 
int dr[4] = {0, 1, 0, -1};
int dc[4] = {1, 0, -1, 0};
 
void printMap() {
    return;
    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; ++j) {
            cout << map[i][j];
        }
        cout << endl;
    }
}
 
void connectSquares(int r1, int c1, int r2, int c2) {
    //cout << "connecting (" << r1 << "," << c1 << ") and (" << r2 << "," << c2 << ")" << endl;
    pair<int,int> leftPoint, rightPoint;
    if (c1 < c2) {
        leftPoint = make_pair(r1,c1);        
        rightPoint = make_pair(r2,c2);
    } else {
        leftPoint = make_pair(r2,c2);        
        rightPoint = make_pair(r1,c1);
    }
 
    int minRow = min(leftPoint.first, rightPoint.first);
    int maxRow = max(leftPoint.first, rightPoint.first);
 
    for (int curC = leftPoint.second; curC <= rightPoint.second; ++curC) {
        map[leftPoint.first][curC] = ' ';
    }
 
    for (int curR = minRow; curR <= maxRow; ++curR) {
        map[curR][rightPoint.second] = ' ';
    }
   
    printMap();
}
 
void makeSureThereIsAPathFrom0To1(vector<vector<int> > &ids) {
    vector<pair<int,int> > ones;
    vector<pair<int,int> > zeroes;
    for (int i = 0; i < ids.size(); ++i) {
        for (int j = 0; j < ids[0].size(); ++j) {
            if (map[i][j] == '#') continue;
            if (ids[i][j] == 0) zeroes.push_back(make_pair(i,j));
            else if (ids[i][j] == 1) ones.push_back(make_pair(i,j));
        }
    }
 
    int besti, bestj;
    int min = 1<<30;
 
    for (int i = 0; i < zeroes.size(); ++i) {
        for (int j = 0; j < ones.size(); ++j) {
            int dr = abs(zeroes[i].first-ones[j].first);
            int dc = abs(zeroes[i].second-ones[j].second);
 
            if (dr + dc < min) {
                besti = i;
                bestj = j;
                min = dr + dc;
            }
        }
    }
 
    connectSquares(zeroes[besti].first, zeroes[besti].second, ones[bestj].first, ones[bestj].second);
}
 
void dfs(vector<vector<int> > &vis, int r, int c, int component) {
    if (!(r >= 0 && r < height && c >= 0 && c < width))
        return;
 
    if (map[r][c] == '#') return;
    if (vis[r][c] != -1) return;
    vis[r][c] = component;
    for (int i = 0; i < 4; ++i) {
        dfs(vis, r + dr[i], c + dc[i], component);
    }
}
 
vector<vector<int> > partitionMap() {
    vector<vector<int> > vis(map.size(), vector<int>(map[0].size(), -1));
   
    int numComponents = 0;
    for (int i = 0; i < map.size(); ++i) {
        for (int j = 0; j < map[i].size(); ++j) {
            assert(map[i].size() == map[0].size() && "map sizes aren't equal");
            if (map[i][j] != '#' && -1 == vis[i][j]) {
                dfs(vis, i, j, numComponents++);
            }
        }
    }
 
    return vis;
}
 
int main() {
    srand(time(0));
 
    cin >> width >> height;
    double density;
    cin >> density;
    double densityTracks;
    cin >> densityTracks;
 
    map.resize(height);
   
    for (int i = 0; i < height; ++i) {
        string mapRow;
        for (int j = 0; j < width; ++j) {
            // wall or not?
            int r = rand();
            double rat = (r+0.0)/((1<<31)-1);
            if (rat < density) {
                // place wall (might be removed later)
                mapRow.push_back('#');
            } else {
                mapRow.push_back(' ');
            }
        }
        map[i] = mapRow;
    }
 
    // remove random box on right edge (to make mirroring connected)
    int rRow = rand() % height;
    map[rRow][width-2] = ' ';
    map[rRow][width-1] = ' ';
   
    rRow = rand() % height;
    map[rRow][width-1] = ' ';
 
    for (;;) {
        vector<vector<int> > mapPartition = partitionMap();
 
        int check = 0;
        for (int i = 0; i < mapPartition.size(); ++i) {
            for (int j = 0; j < mapPartition[i].size(); ++j) {
                check = max(mapPartition[i][j], check);
                //cout << mapPartition[i][j] << "\t";
            }
            //cout << endl;
        }
 
        if (check == 0) break;
 
        // make sure there is a path from 0 to 1
        makeSureThereIsAPathFrom0To1(mapPartition);
    }
   
    vector<int> uCandidates;
    for (int i = 0; i < height; ++i) {
        if (map[i][width-1] == ' ') {
            uCandidates.push_back(i);
        }
    }
 
    random_shuffle(uCandidates.begin(), uCandidates.end());
   
    map[uCandidates[0]][width-1] = 'U';
    map[uCandidates[0]][width-2] = '1';
 
    // place tracks
    vector<pair<int,int> > pCandidates;
    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width-1; ++j) {
            if (map[i][j] == ' ') {
                pCandidates.push_back(make_pair(i, j));
            }
        }
    }
 
    random_shuffle(pCandidates.begin(), pCandidates.end());
 
    int nTracks = int(densityTracks * pCandidates.size());
 
    for (int i = 0; i < nTracks; ++i) {
        char type = 'a' + (rand()%5);
        map[pCandidates[i].first][ pCandidates[i].second] = type;
    }
 
    for (int i = 0; i < height; ++i) {
        string rowcp = map[i];
        reverse(rowcp.begin(), rowcp.end());
        cout << map[i];
        for (int i = 1; i < rowcp.size(); ++i) {
            if (rowcp[i] == '1') cout << 2;
            else
            cout << rowcp[i];
        }
        cout << endl;
    }
}
