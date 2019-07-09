package com.example.kri.chess;

public class PieceInfo{

    private char type;
    private int squareIndex;
    private float pieceHeight;
    String color;
    private float coordX, coordZ;

    public PieceInfo(int index, char type){
        squareIndex = index;
        this.type = type;

        if(type == 'K' || type == 'k'){
            pieceHeight = 3.165f;
        }else if(type == 'Q' || type == 'q'){
            pieceHeight = 2.646f;
        }else if(type == 'B' || type == 'b'){
            pieceHeight = 2.273f;
        }else if(type == 'R' || type == 'r'){
            pieceHeight = 1.735f;
        }else if(type == 'N' || type == 'n'){
            pieceHeight = 1.884f;
        }else if(type == 'P' || type == 'p'){
            pieceHeight = 1.515f;
        }

        if(type == 'r' || type == 'n' || type == 'b' || type == 'q' || type == 'p' || type == 'k'){
            color = "black";
        }else{
            color = "white";
        }
    }

    public int getIndex(){
        return squareIndex;
    }

    public void setIndex(int i){
        squareIndex = i;
    }

    public char getType(){
        return type;
    }

    public float getHeight(){
        return pieceHeight;
    }

    public String getColor(){return color;}
    public float getCoordX(){return coordX;}
    public float getCoordZ(){return coordZ;}
    public void setCoordX(float x){coordX = x;}
    public void setCoordZ(float z){coordZ = z;}

}
