package com.example.kri.chess;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ChessManager{

    private Pawn pawn;
    private Rook rook;
    private Bishop bishop;
    private Knight knight;
    private Queen queen;
    private King king;
    private ChessBoard board;
    private ChessSquare[] squares;
    private List<PieceInfo> pieces;
    public String[] indicesTable = {
            "70","71","72","73","74","75","76","77",
            "60","61","62","63","64","65","66","67",
            "50","51","52","53","54","55","56","57",
            "40","41","42","43","44","45","46","47",
            "30","31","32","33","34","35","36","37",
            "20","21","22","23","24","25","26","27",
            "10","11","12","13","14","15","16","17",
            "00","01","02","03","04","05","06","07"};

    public ChessManager(Context context){
        pieces = new ArrayList<>();
        squares = new ChessSquare[64];
        pawn = new Pawn(context);
        rook = new Rook(context);
        bishop = new Bishop(context);
        knight = new Knight(context);
        queen = new Queen(context);
        king = new King(context);
        board = new ChessBoard(context);
        Bitboards.initiateChess();
    }

    public ChessBoard getBoard(){return board;}
    public ChessSquare[] getSquares(){return squares;}
    public List<PieceInfo> getPieces(){return pieces;}
    public Rook getRook(){return rook;}
    public Knight getKnight(){return knight;}
    public Bishop getBishop(){return bishop;}
    public King getKing(){return king;}
    public Queen getQueen(){return queen;}
    public Pawn getPawn(){return pawn;}

    public void initSquaresCenters(){
        int counter = 0;
        float z, x;
        for(int i = 0; i < 8; i++){
            z = (i * 2) -7;
            for(int j = 0; j < 8; j++){
                x = (j * -2) + 7;
                squares[counter++] = new ChessSquare(x, z);
            }
        }
    }

    public void initSquaresCorners(){
        for(int i = 0; i < 64; i++){
            float x = squares[i].getX();
            float z = squares[i].getZ();
            squares[i].setUpLeft(x + 1, z + 1);
            squares[i].setUpRight(x - 1, z + 1);
            squares[i].setDownLeft(x + 1, z - 1);
            squares[i].setDownRight(x - 1, z - 1);
        }
    }

    public void initBoardSetup(){
        int counter = 0;
        pieces.add(new PieceInfo(counter++, 'R'));
        pieces.add(new PieceInfo(counter++, 'N'));
        pieces.add(new PieceInfo(counter++, 'B'));
        pieces.add(new PieceInfo(counter++, 'Q'));
        pieces.add(new PieceInfo(counter++, 'K'));
        pieces.add(new PieceInfo(counter++, 'B'));
        pieces.add(new PieceInfo(counter++, 'N'));
        pieces.add(new PieceInfo(counter++, 'R'));
        for(int i = 0; i < 8; i++){
            pieces.add(new PieceInfo(counter++, 'P'));
        }
        counter = 48;
        for(int i = 0; i < 8; i++){
            pieces.add(new PieceInfo(counter++, 'p'));
        }
        pieces.add(new PieceInfo(counter++, 'r'));
        pieces.add(new PieceInfo(counter++, 'n'));
        pieces.add(new PieceInfo(counter++, 'b'));
        pieces.add(new PieceInfo(counter++, 'q'));
        pieces.add(new PieceInfo(counter++, 'k'));
        pieces.add(new PieceInfo(counter++, 'b'));
        pieces.add(new PieceInfo(counter++, 'n'));
        pieces.add(new PieceInfo(counter++, 'r'));

        initPieceCoordSetup();
    }

    public void initPieceCoordSetup(){
        for(PieceInfo piece : pieces){
            piece.setCoordX(squares[piece.getIndex()].getX());
            piece.setCoordZ(squares[piece.getIndex()].getZ());
        }
    }

    public float[] getAvailibleSquaresMesh(int index, char type){

        List<Integer> matchedIndices = new ArrayList<>();
        String matcher = indicesTable[index];
        String moves = Moves.generateMovesForPiece(type);
        float[] mesh;

        for(int i = 0; i < moves.length()/4; i++){
            if(moves.substring(i * 4, i * 4 + 2).equals(matcher)){
                String tester = moves.substring(i * 4 + 2, i * 4 + 4);
                for(int j = 0; j < 64; j++){
                    if(indicesTable[j].equals(tester)){
                        matchedIndices.add(j);
                        break;
                    }
                }
            }
        }

        if(matchedIndices.size() > 0){
            ChessRenderer.setMatchedIndexesForAvailibleSquares(matchedIndices);
        }

        mesh = new float[matchedIndices.size() * 36];
        int counter = 0;

        for(int i = 0; i < matchedIndices.size(); i++){
            ChessSquare sq = squares[matchedIndices.get(i)];
            mesh[counter++] = sq.getX();
            mesh[counter++] = sq.getY();
            mesh[counter++] = sq.getZ();
            mesh[counter++] = sq.getDownLeftX();
            mesh[counter++] = sq.getY();
            mesh[counter++] = sq.getDownLeftZ();
            mesh[counter++] = sq.getDownRightX();
            mesh[counter++] = sq.getY();
            mesh[counter++] = sq.getDownRightZ();

            mesh[counter++] = sq.getX();
            mesh[counter++] = sq.getY();
            mesh[counter++] = sq.getZ();
            mesh[counter++] = sq.getDownRightX();
            mesh[counter++] = sq.getY();
            mesh[counter++] = sq.getDownRightZ();
            mesh[counter++] = sq.getUpRightX();
            mesh[counter++] = sq.getY();
            mesh[counter++] = sq.getUpRightZ();

            mesh[counter++] = sq.getX();
            mesh[counter++] = sq.getY();
            mesh[counter++] = sq.getZ();
            mesh[counter++] = sq.getUpRightX();
            mesh[counter++] = sq.getY();
            mesh[counter++] = sq.getUpRightZ();
            mesh[counter++] = sq.getUpLeftX();
            mesh[counter++] = sq.getY();
            mesh[counter++] = sq.getUpLeftZ();

            mesh[counter++] = sq.getX();
            mesh[counter++] = sq.getY();
            mesh[counter++] = sq.getZ();
            mesh[counter++] = sq.getUpLeftX();
            mesh[counter++] = sq.getY();
            mesh[counter++] = sq.getUpLeftZ();
            mesh[counter++] = sq.getDownLeftX();
            mesh[counter++] = sq.getY();
            mesh[counter++] = sq.getDownLeftZ();
        }

        return mesh;
    }

    public void makeMove(String move){
        Moves.makeMoveOnBitboards(move);
        String from = move.substring(0,2);
        String to = move.substring(2);
        int indexFrom = convertFromMoveToIndex(from);
        int indexTo = convertFromMoveToIndex(to);
        if(isPieceOnSquare(indexTo)){
            pieces.remove(findPiecePositionById(indexTo));
        }

        pieces.get(findPiecePositionById(indexFrom)).setCoordX(squares[indexTo].getX());
        pieces.get(findPiecePositionById(indexFrom)).setCoordZ(squares[indexTo].getZ());
        pieces.get(findPiecePositionById(indexFrom)).setIndex(indexTo);

    }

    private int findPiecePositionById(int id){
        for(int i = 0; i < pieces.size(); i++){
            if(pieces.get(i).getIndex() == id){
                return i;
            }
        }
        return  -1;
    }

    private boolean isPieceOnSquare(int sqId){
        for(PieceInfo piece : pieces){
            if(piece.getIndex() == sqId){
                return true;
            }
        }
        return false;
    }

    private int convertFromMoveToIndex(String move){
        for(int i = 0; i < 64; i++){
            if(move.equals(indicesTable[i])){
                return i;
            }
        }
        return -1;
    }

    public void makeComputerMove(){
        String computerMove = Moves.alphaBeta(Bitboards.WP, Bitboards.WN, Bitboards.WB, Bitboards.WR, Bitboards.WQ,
                Bitboards.WK, Bitboards.BP, Bitboards.BN, Bitboards.BB, Bitboards.BR, Bitboards.BQ, Bitboards.BK, 0,
                true, Moves.MAX, Moves.MIN, "", false).substring(0,4);
        makeMove(computerMove);
        ChessRenderer.playersTurn = !ChessRenderer.playersTurn;
    }
}
