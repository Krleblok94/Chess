package com.example.kri.chess;

public class Evaluation{

    private static final int pawnPosWhite[][] = {
            {0,  0,  0,  0,  0,  0,  0,  0},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5,  5, 10, 25, 25, 10,  5,  5},
            {0,  0,  0, 20, 20,  0,  0,  0},
            {5, -5,-10,  0,  0,-10, -5,  5},
            {5, 10, 10,-20,-20, 10, 10,  5},
            {0,  0,  0,  0,  0,  0,  0,  0}
    };

    private static final int pawnPosBlack[][] = {
            {0,  0,  0,  0,  0,  0,  0,  0},
            {5, 10, 10,-20,-20, 10, 10,  5},
            {5, -5,-10,  0,  0,-10, -5,  5},
            {0,  0,  0, 20, 20,  0,  0,  0},
            {5,  5, 10, 25, 25, 10,  5,  5},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {0,  0,  0,  0,  0,  0,  0,  0}
    };

    private static final int bishopPos[][] = {
            {-5, -5, -5, -5, -5, -5, -5, -5},
            {-5, 10,  5,  8,  8,  5, 10, -5},
            {-5,  5,  3,  8,  8,  3,  5, -5},
            {-5,  3, 10,  3,  3, 10,  3, -5},
            {-5,  3, 10,  3,  3, 10,  3, -5},
            {-5,  5,  3,  8,  8,  3,  5, -5},
            {-5, 10,  5,  8,  8,  5, 10, -5},
            {-5, -5, -5, -5, -5, -5, -5, -5}
    };

    private static int knightPos[][] = {
            {-10, -5, -5, -5, -5, -5, -5,-10},
            { -8,  0,  0,  3,  3,  0,  0, -8},
            { -8,  0, 10,  8,  8, 10,  0, -8},
            { -8,  0,  8, 10, 10,  8,  0, -8},
            { -8,  0,  8, 10, 10,  8,  0, -8},
            { -8,  0, 10,  8,  8, 10,  0, -8},
            { -8,  0,  0,  3,  3,  0,  0, -8},
            {-10, -5, -5, -5, -5, -5, -5,-10}
    };

    private static int evalMaterial(long pawnBoard, long knightBoard, long bishopBoard,
                                    long rookBoard, long queenBoard, long kingBoard){
        int value = 0;
        for(int i = 0; i < 64; i++){
            if(((pawnBoard >>> i) & 1) == 1){
                value += 100;
            }
            if(((knightBoard >>> i) & 1) == 1){
                value += 320;
            }
            if(((bishopBoard >>> i) & 1) == 1){
                value += 330;
            }
            if(((rookBoard >>> i) & 1) == 1){
                value += 500;
            }
            if(((queenBoard >>> i) & 1) == 1){
                value += 900;
            }
            if(((kingBoard >>> i) & 1) == 1){
                value += 20000;
            }
        }
        return value;
    }

    private static int evalPosition(long pawnBoard, long knightBoard, long bishopBoard,
                                    long rookBoard, long queenBoard, long kingBoard, boolean white){
        int value = 0;
        for(int i = 0; i < 64; i++){
            if(((pawnBoard >>> i) & 1) == 1){
                if(white){
                    value += pawnPosWhite[i / 8][i % 8];
                }else{
                    value += pawnPosBlack[i / 8][i % 8];
                }
            }
            if(((knightBoard >>> i) & 1) == 1){
                value += knightPos[i / 8][i % 8];
            }
            if(((bishopBoard >>> i) & 1) == 1){
                value += bishopPos[i / 8][i % 8];
            }
        }
        return value;
    }

    public static int eval(long pawnBoard, long knightBoard, long bishopBoard,
                           long rookBoard, long queenBoard, long kingBoard, boolean white){
        int value = 0;
        value += evalMaterial(pawnBoard, knightBoard, bishopBoard, rookBoard, queenBoard, kingBoard);
        value += evalPosition(pawnBoard, knightBoard, bishopBoard, rookBoard, queenBoard, kingBoard, white);
        return value;
    }

}
