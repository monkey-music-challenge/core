// Copyright (C) 2013 by Henry Kroll
/* Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */

module.exports = function(rows, cols) {
  var maze="",a;
  function mazeInit(){
    for(i=0;i<2*rows+1;i++){
      for(j=0;j<2*cols+1;j++){
        maze+="#";
      } maze +="\n";
    }
    a=maze.split("\n");
    for(i in a)a[i]=a[i].split('');
  }
  function mazeStep(r,c){
    var i,vector=[[0,0],[0,0],[0,0]]; /* 3 possible directions */
    function R(val){
      if(typeof val=="undefined")return vector[i][0];
      vector[i][0]=val;
    }
    function C(val){
      if(typeof val=="undefined")return vector[i][1];
      vector[i][1]=val;
    }
    while(1){
      i=0; /* create a list of possible options */
      if(r>1            &&a[r-2][c]!==" "){R(r-2);C(c);i++;}
      if(r< rows*2-1 &&a[r+2][c]!==" "){R(r+2);C(c);i++;}
      if(c>1            &&a[r][c-2]!==" "){R(r);C(c-2);i++;}
      if(c< cols*2-1 &&a[r][c+2]!==" "){R(r);C(c+2);i++;}
      /* i is never > 3 because path behind is cleared */
      if(i==0)break; /* check for dead end */
      i=Math.floor((Math.random()*i))|0; /* random 0 to i-1 */
      a[(R()+r)/2|0][(C()+c)/2|0]=" "; /* knock out block */
      a[R()][C()]=" "; /* clear to it */
      mazeStep(R(),C());
    }
  }
  function mazeWalk(){
    var i,r,c;
    c=cols|1;
    a[0][c]=' ';a[2*rows|0][c]=' ';
    i=Math.floor((Math.random()*2));
    c=(i)?1|0:(2*cols-1);r=rows|1;a[r][c]=' ';
    mazeStep(r,c);
  }
  mazeInit();
  mazeWalk();
  for(i in a)a[i]=a[i].join('');
  maze=a.join('\n');
  return maze;
};
