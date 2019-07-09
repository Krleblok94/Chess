package com.example.kri.chess;

public class ChessSquare{

    private float translateX, translateZ;
    private float upLeftX, upLeftZ;
    private float upRightX, upRightZ;
    private float downLeftX, downLeftZ;
    private float downRightX, downRightZ;
    private float y;

    public ChessSquare(float x, float z){
        translateX = x;
        translateZ = z;
        y = 0.55f;
    }

    public float getX(){return translateX;}
    public float getY(){return y;}
    public float getZ(){return translateZ;}
    public void setUpLeft(float x, float z){upLeftX = x; upLeftZ = z;}
    public void setUpRight(float x, float z){upRightX = x; upRightZ = z;}
    public void setDownLeft(float x, float z){downLeftX = x; downLeftZ = z;}
    public void setDownRight(float x, float z){downRightX = x; downRightZ = z;}
    public float getUpLeftX(){return  upLeftX;}
    public float getUpLeftZ(){return  upLeftZ;}
    public float getUpRightX(){return  upRightX;}
    public float getUpRightZ(){return  upRightZ;}
    public float getDownLeftX(){return  downLeftX;}
    public float getDownLeftZ(){return  downLeftZ;}
    public float getDownRightX(){return  downRightX;}
    public float getDownRightZ(){return  downRightZ;}
}
