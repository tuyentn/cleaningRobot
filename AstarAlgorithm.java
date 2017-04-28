/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpg;

import java.util.ArrayList;

/**
 *
 * @author VPC
 */

/*Giải thuật tìm kiếm đường A*
    Đầu vào: địa chỉ Xuất Phát, địa chỉ Đích, mảng ma trận map 2 chiều
     Đầu ra: List các location 


*/
class AstarAlgorithm {
     ArrayList<Location> path;
     Location loc_des;
     Location loc_start;
     int[][]  map_data;
     Node found;
     Node current;
     Node start;
     Node destination;
     ArrayList<Node> Open;
     ArrayList<Node> Close;
     ArrayList<Node> successors;
     
     //Constructor
     public AstarAlgorithm(Location XP, Location Dich, int[][] vitual_map){
          path = new ArrayList<>();
          this.map_data = vitual_map;
          System.out.println("Xuat phat = ("+XP.m+","+XP.n+")");
          System.out.println("Dich = ("+Dich.m+","+Dich.n+")");
          start = new Node(XP.m, XP.n);
          destination = new Node(Dich.m, Dich.n);
          found = start;
          Open = new ArrayList<>();
          Close = new ArrayList<>();
     }
    
     public ArrayList<Location> Looking_path(){
          Open.add(start);
          while(Open.size()>0){
               current = get_lowest();   //lấy phần tử heuristic trong hàng đợi Open
               System.out.println("Current = ("+current.location.m+","+current.location.n+")");
               if(current.isMatch(destination)){   //Den dich
                    found=current;
                    break;
               } else{
                    successors = get_children(current);   //Sinh các ô mới
                    for (Node successor : successors){
                         System.out.println("Successor = ("+successor.location.m+","+successor.location.n+")");
                         boolean exist = false;

                         for (Node o_node : Open){
                              if(successor.isMatch(o_node)){
                                   exist = true;
                                   break;
                              }
                         }
                         if(exist){continue;}  // Nếu succesor đã tồn tại trong Open thì bỏ qua

                         for(Node c_node : Close){
                              if(successor.isMatch(c_node)){
                                   exist = true;
                                   break;
                              }
                         }
                         if(exist){continue;}  // Nếu succesor đã tồn tại trong Close  thì bỏ qua

                         Open.add(successor);  //Thêm successor vào hàng đợi Open
                    }
                    Open.remove(current);     //Current đã xét -> bị xóa
                    Close.add(current);
               }
          }
          path.add(found.location);
          while((found.parent != null) && (! found.parent.isMatch(start))){    //Truy vet 
               path.add(found.parent.location);
               found = found.parent;
          }
          return path;
     }
     
     
     ArrayList<Node> get_children(Node curr){
          ArrayList<Node> temp = new ArrayList<>();
          int m = curr.location.m;
          int n = curr.location.n;
          if ((map_data[m-1][n] !=-1 && map_data[m-1][n] != Map.TUONG ) ){   //Nếu đi được hoặc là sạc pin
               temp.add(new Node(m-1,n,curr));
          }
          if ((map_data[m][n+1] !=-1 && map_data[m][n+1] != Map.TUONG ) ){
               temp.add(new Node(m,n+1,curr));
          }
          if ((map_data[m+1][n] !=-1 && map_data[m+1][n] != Map.TUONG ) ){
               temp.add(new Node(m+1,n,curr));
          }
          if ((map_data[m][n-1] !=-1 && map_data[m][n-1] != Map.TUONG ) ){
               temp.add(new Node(m,n-1,curr));
          }
          return temp;
     }
     Node get_lowest(){
     //Lấy node tối ưu ở trong Open
          Node temp = Open.get(Open.size()-1);
          for (Node Inode : Open) {
               if(Inode.heuristic < temp.heuristic){
                    temp = Inode;
               }
          }
          return temp;
     }
     class Node{
          Location location;
          Node parent;
          int cost;
          double heuristic;
          public Node(int tm, int tn){
               this.parent = null;
               this.location = new Location(tm,tn);
               this.cost = 0;
               this.heuristic = 99999;
          }
          public Node(int m, int n, Node tparent){
               this.parent = tparent;
               this.location = new Location(m,n);
               this.caculate_cost();
               this.caculate_heuris();
          }
          void caculate_cost(){
               this.cost = this.parent.cost + 1;
          }
          void caculate_heuris(){
               this.heuristic =  Math.sqrt(Math.pow((destination.location.m - location.m), 2) + Math.pow((destination.location.n - location.n), 2));
          }
          boolean isMatch(Node cmp_node){
               return ((this.location.m == cmp_node.location.m) &&  (this.location.n == cmp_node.location.n));
          }
     }
}
