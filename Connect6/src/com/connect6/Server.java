package com.connect6;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

//2 - не установлен камень
//1 - установлен белый камень
//0 - установлен черный камень

public class Server implements GameLogic {
    private int lastX = -1, lastY = -1;
    private boolean id1 = false;
    private static int [][]board = new int[19][19];
    private static int []counter = new int[2];
    private static int whoWon = 2;

    public Server() {
        for(int i=0; i<18; i++)
            for(int j=0; j<18; j++)
                board[i][j] = 2;

        counter[0]=0;
        counter[1]=0;
    }

    private static boolean checkWin(int x, int y, int checkColor){
        int nowX = x;
        int nowY = y;

        //Проверка по горизонтали
        while(board[nowX][nowY]==(checkColor)) {
            counter[checkColor]++;
            nowX+=1;
        }
        nowX = x-1;
        while(board[nowX][nowY]==(checkColor)) {
            counter[checkColor]++;
            nowX-=1;
        }

        if(counter[checkColor]>=6)
            return true;
        else{
            //Проверка по вертикали
            counter[checkColor] = 0;
            nowX = x;
            nowY = y;
            while(board[nowX][nowY]==(checkColor)) {
                counter[checkColor]++;
                nowY+=1;
            }
            nowY = y-1;
            while(board[nowX][nowY]==(checkColor)) {
                counter[checkColor]++;
                nowY-=1;
            }

            if(counter[checkColor]>=6)
                return true;
            else{
                //Проверка по диагонали (возрастание)
                counter[checkColor] = 0;
                nowX = x;
                nowY = y;
                while(board[nowX][nowY]==(checkColor)) {
                    counter[checkColor]++;
                    nowX+=1;
                    nowY+=1;
                }
                nowX = x-1;
                nowY = y-1;
                while(board[nowX][nowY]==(checkColor)) {
                    counter[checkColor]++;
                    nowX-=1;
                    nowY-=1;
                }

                if(counter[checkColor]>=6)
                    return true;
                else{
                    //Проверка по диагонали (убывание)
                    counter[checkColor] = 0;
                    nowX = x;
                    nowY = y;
                    while(board[nowX][nowY]==(checkColor)) {
                        counter[checkColor]++;
                        nowX+=1;
                        nowY-=1;
                    }
                    nowX = x-1;
                    nowY = y+1;
                    while(board[nowX][nowY]==(checkColor)) {
                        counter[checkColor]++;
                        nowX-=1;
                        nowY+=1;
                    }
                    if(counter[checkColor]>=6)
                        return true;
                }
            }
        }
        counter[checkColor] = 0;
        return false;
    }

    public void setPoint(int x, int y, int color) {
        board[x][y] = color;
        lastX = x;
        lastY = y;

        if(checkWin(x,y,color)) {
            if(color==1)
                whoWon = 1;
            else
                whoWon = 0;
        }
    }

    public int whoWon(){
        return whoWon;
    }

    public int getColor(){
        if(!id1) {
            id1 = true;
            return 0;
        }
        else{
            return 1;
        }
    }

    public int[] getEnemyTurn(){
        int[] enemy = new int[2];
        enemy[0] = lastX;
        enemy[1] = lastY;
        lastX = -1;
        lastY = -1;
        return enemy;
    }

    public static void main(String args[]) {
        try {
            Server obj = new Server();
            GameLogic stub = (GameLogic) UnicastRemoteObject.exportObject(obj, 666);
            Registry registry = LocateRegistry.createRegistry(666);
            registry.rebind("Server", stub);
            System.out.println("Server is ready!");

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}