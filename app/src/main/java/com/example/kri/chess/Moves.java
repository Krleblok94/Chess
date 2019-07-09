package com.example.kri.chess;

import android.util.Log;

import java.util.Arrays;

public class Moves{

    private static final long FILE_A = 72340172838076673L;
    private static final long FILE_H = -9187201950435737472L;
    private static final long FILE_AB = 217020518514230019L;
    private static final long FILE_GH = -4557430888798830400L;
    private static final long RANK_1 = -72057594037927936L;
    private static final long RANK_4 = 1095216660480L;
    private static final long RANK_5 = 4278190080L;
    private static final long RANK_8 = 255L;
    private static final long CENTRE = 103481868288L;
    private static final long EXTENDED_CENTRE = 66229406269440L;
    private static final long KING_SIDE = -1085102592571150096L;
    private static final long QUEEN_SIDE = 1085102592571150095L;
    private static final long KING_SPAN = 460039L;
    private static final long KNIGHT_SPAN = 43234889994L;
    private static long NOT_MY_PIECES;
    private static long MY_PIECES;
    private static long OCCUPIED;
    private static long EMPTY;

    public static final int MAX = 1000000;
    public static final int MIN = -1000000;
    private static final int MAX_DEPTH = 2;
    private static final int MATE_SCORE = 1000;

    private static final long RankMasks8[] = { /*from rank1 to rank8*/
            0xFFL, 0xFF00L, 0xFF0000L, 0xFF000000L, 0xFF00000000L, 0xFF0000000000L,
            0xFF000000000000L, 0xFF00000000000000L
    };
    private static final long FileMasks8[] = { /*from fileA to FileH*/
            0x101010101010101L, 0x202020202020202L, 0x404040404040404L, 0x808080808080808L,
            0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L, 0x8080808080808080L
    };
    private static final long DiagonalMasks8[] = { /*from top left to bottom right*/
            0x1L, 0x102L, 0x10204L, 0x1020408L, 0x102040810L, 0x10204081020L, 0x1020408102040L,
            0x102040810204080L, 0x204081020408000L, 0x408102040800000L, 0x810204080000000L,
            0x1020408000000000L, 0x2040800000000000L, 0x4080000000000000L, 0x8000000000000000L
    };
    private static final long AntiDiagonalMasks8[] = { /*from top right to bottom left*/
            0x80L, 0x8040L, 0x804020L, 0x80402010L, 0x8040201008L, 0x804020100804L, 0x80402010080402L,
            0x8040201008040201L, 0x4020100804020100L, 0x2010080402010000L, 0x1008040201000000L,
            0x804020100000000L, 0x402010000000000L, 0x201000000000000L, 0x100000000000000L
    };

    private static long horizontalAndVerticalMoves(int s){
        long binary = 1L << s;
        long possibilitiesHorizontal = (OCCUPIED - 2 * binary) ^ Long.reverse(Long.reverse(OCCUPIED)
                - 2 * Long.reverse(binary));
        long possibilitiesVertical = ((OCCUPIED & FileMasks8[s % 8]) - (2 * binary)) ^
                Long.reverse(Long.reverse(OCCUPIED & FileMasks8[s % 8]) - (2 * Long.reverse(binary)));
        return (possibilitiesHorizontal & RankMasks8[s / 8]) | (possibilitiesVertical&FileMasks8[s % 8]);
    }

    private static long diagonalAndAntidiagonalMoves(int s){
        long binary = 1L << s;
        long possibilitiesDiagonal = ((OCCUPIED & DiagonalMasks8[(s / 8) + (s % 8)]) - (2 * binary)) ^
                Long.reverse(Long.reverse(OCCUPIED & DiagonalMasks8[(s / 8) + (s % 8)]) - (2 * Long.reverse(binary)));
        long possibilitiesAntiDiagonal = ((OCCUPIED & AntiDiagonalMasks8[(s / 8) + 7 - (s % 8)]) - (2 * binary)) ^
                Long.reverse(Long.reverse(OCCUPIED & AntiDiagonalMasks8[(s / 8) + 7 - (s % 8)]) - (2 * Long.reverse(binary)));
        return (possibilitiesDiagonal & DiagonalMasks8[(s / 8) + (s % 8)]) |
                (possibilitiesAntiDiagonal&AntiDiagonalMasks8[(s / 8) + 7 - (s % 8)]);
    }

    public static String possibleWhiteMovesAll(String history, long WP,long WN,long WB,long WR,long WQ,
                                               long WK,long BP,long BN,long BB,long BR,long BQ,long BK){
        NOT_MY_PIECES = ~ (WP|WN|WB|WR|WQ|WK|BK);
        MY_PIECES = BP|BN|BB|BR|BQ;
        OCCUPIED = WP|WN|WB|WR|WQ|WK|BP|BN|BB|BR|BQ|BK;
        EMPTY = ~ OCCUPIED;
        String list = possibleWhitePawnMoves(WP) +
                possibleN(WN) +
                possibleB(WB) +
                possibleR(WR) +
                possibleQ(WQ) +
                possibleK(WK);
        return list;
    }

    public static String possibleBlackMovesAll(String history, long WP,long WN,long WB,long WR,long WQ,
                                               long WK,long BP,long BN,long BB,long BR,long BQ,long BK){
        NOT_MY_PIECES = ~ (BP|BN|BB|BR|BQ|BK|WK);
        MY_PIECES = WP|WN|WB|WR|WQ;
        OCCUPIED = WP|WN|WB|WR|WQ|WK|BP|BN|BB|BR|BQ|BK;
        EMPTY = ~ OCCUPIED;
        String list = possibleBlackPawnMoves(BP) +
                possibleN(BN) +
                possibleB(BB) +
                possibleR(BR) +
                possibleQ(BQ) +
                possibleK(BK);
        return list;
    }

    private static String possibleWhitePawnMoves(long bitboardWP){
        String list = "";
        long pawnMoves;
        long possibility;

        pawnMoves = (bitboardWP >> 7) & MY_PIECES &~ RANK_8 &~ FILE_A; //capture right
        possibility = pawnMoves &~ (pawnMoves - 1);
        while(possibility != 0){
            int index = Long.numberOfTrailingZeros(possibility);
            list += "" + (index / 8 + 1) + (index % 8 - 1) + (index / 8) + (index % 8);
            pawnMoves &=~ possibility;
            possibility = pawnMoves &~ (pawnMoves - 1);
        }

        pawnMoves = (bitboardWP >> 9) & MY_PIECES &~ RANK_8 &~ FILE_H; //capture left
        possibility = pawnMoves &~ (pawnMoves - 1);
        while(possibility != 0){
            int index = Long.numberOfTrailingZeros(possibility);
            list += "" + (index / 8 + 1) + (index % 8 + 1) + (index / 8) + (index % 8);
            pawnMoves &=~ possibility;
            possibility = pawnMoves &~ (pawnMoves - 1);
        }

        pawnMoves = (bitboardWP >> 8) & EMPTY &~ RANK_8; //move 1 forward
        possibility = pawnMoves &~ (pawnMoves - 1);
        while(possibility != 0){
            int index = Long.numberOfTrailingZeros(possibility);
            list += "" + (index / 8 + 1) + (index % 8) + (index / 8) + (index % 8);
            pawnMoves &=~ possibility;
            possibility = pawnMoves &~ (pawnMoves - 1);
        }

        pawnMoves = (bitboardWP >> 16) & EMPTY & (EMPTY >> 8) & RANK_4; //move 2 forward
        possibility = pawnMoves &~ (pawnMoves - 1);
        while(possibility != 0){
            int index = Long.numberOfTrailingZeros(possibility);
            list += "" + (index / 8 + 2) + (index % 8) + (index / 8) + (index % 8);
            pawnMoves &=~ possibility;
            possibility = pawnMoves &~ (pawnMoves - 1);
        }

        pawnMoves = (bitboardWP >> 7) & MY_PIECES & RANK_8 &~ FILE_A; //pawn promotion by capture right
        possibility = pawnMoves &~ (pawnMoves - 1);
        while(possibility != 0){
            int index = Long.numberOfTrailingZeros(possibility);
            list += "" + (index % 8 - 1) + (index % 8) + "QP" + (index % 8 - 1) + (index % 8) + "RP" +
                    (index % 8 - 1) + (index % 8) + "BP" + (index % 8 - 1) + (index % 8) + "NP";
            pawnMoves &=~ possibility;
            possibility = pawnMoves &~ (pawnMoves - 1);
        }

        pawnMoves = (bitboardWP >> 9) & MY_PIECES & RANK_8 &~ FILE_H; //pawn promotion by capture left
        possibility = pawnMoves &~ (pawnMoves - 1);
        while(possibility != 0){
            int index = Long.numberOfTrailingZeros(possibility);
            list += "" + (index % 8 + 1) + (index % 8) + "QP" + (index % 8 + 1) + (index % 8) + "RP" +
                    (index % 8 + 1) + (index % 8) + "BP" + (index % 8 + 1 ) + (index % 8) + "NP";
            pawnMoves &=~ possibility;
            possibility = pawnMoves &~ (pawnMoves - 1);
        }

        pawnMoves = (bitboardWP >> 8) & EMPTY & RANK_8; //pawn promotion by 1 move forward
        possibility = pawnMoves &~ (pawnMoves - 1);
        while(possibility != 0){
            int index = Long.numberOfTrailingZeros(possibility);
            list += "" + (index % 8) + (index % 8) + "QP" + (index % 8) + (index % 8) + "RP" +
                    (index % 8) + (index % 8) + "BP" + (index % 8) + (index % 8) + "NP";
            pawnMoves &=~ possibility;
            possibility = pawnMoves &~ (pawnMoves - 1);
        }

        return list;
    }

    private static String possibleBlackPawnMoves(long bitboardBP){
        String list = "";
        long pawnMoves;
        long possibility;

        pawnMoves = (bitboardBP << 7) & MY_PIECES &~ RANK_1 &~ FILE_H; //capture right
        possibility = pawnMoves &~ (pawnMoves - 1);
        while(possibility != 0){
            int index = Long.numberOfTrailingZeros(possibility);
            list += "" + (index / 8 - 1) + (index % 8 + 1) + (index / 8) + (index % 8);
            pawnMoves &=~ possibility;
            possibility = pawnMoves &~ (pawnMoves - 1);
        }

        pawnMoves = (bitboardBP << 9) & MY_PIECES &~ RANK_1 &~ FILE_A; //capture left
        possibility = pawnMoves &~ (pawnMoves - 1);
        while(possibility != 0){
            int index = Long.numberOfTrailingZeros(possibility);
            list += "" + (index / 8 - 1) + (index % 8 - 1) + (index / 8) + (index % 8);
            pawnMoves &=~ possibility;
            possibility = pawnMoves &~ (pawnMoves - 1);
        }

        pawnMoves = (bitboardBP << 8) & EMPTY &~ RANK_1; //move 1 forward
        possibility = pawnMoves &~ (pawnMoves - 1);
        while(possibility != 0){
            int index = Long.numberOfTrailingZeros(possibility);
            list += "" + (index / 8 - 1) + (index % 8) + (index / 8) + (index % 8);
            pawnMoves &=~ possibility;
            possibility = pawnMoves &~ (pawnMoves - 1);
        }

        pawnMoves = (bitboardBP << 16) & EMPTY & (EMPTY << 8) & RANK_5; //move 2 forward
        possibility = pawnMoves &~ (pawnMoves - 1);
        while(possibility != 0){
            int index = Long.numberOfTrailingZeros(possibility);
            list += "" + (index / 8 - 2) + (index % 8) + (index / 8) + (index % 8);
            pawnMoves &=~ possibility;
            possibility = pawnMoves &~ (pawnMoves - 1);
        }

        pawnMoves = (bitboardBP << 7) & MY_PIECES & RANK_1 &~ FILE_H; //pawn promotion by capture right
        possibility = pawnMoves &~ (pawnMoves - 1);
        while(possibility != 0){
            int index = Long.numberOfTrailingZeros(possibility);
            list += "" + (index % 8 + 1) + (index % 8) + "qP" + (index % 8 + 1) + (index % 8) + "rP" +
                    (index % 8 + 1) + (index % 8) + "bP" + (index % 8 + 1) + (index % 8) + "nP";
            pawnMoves &=~ possibility;
            possibility = pawnMoves &~ (pawnMoves - 1);
        }

        pawnMoves = (bitboardBP << 9) & MY_PIECES & RANK_8 &~ FILE_H; //pawn promotion by capture left
        possibility = pawnMoves &~ (pawnMoves - 1);
        while(possibility != 0){
            int index = Long.numberOfTrailingZeros(possibility);
            list += "" + (index % 8 - 1) + (index % 8) + "qP" + (index % 8 - 1) + (index % 8) + "rP" +
                    (index % 8 - 1) + (index % 8) + "bP" + (index % 8 - 1 ) + (index % 8) + "nP";
            pawnMoves &=~ possibility;
            possibility = pawnMoves &~ (pawnMoves - 1);
        }

        pawnMoves = (bitboardBP << 8) & EMPTY & RANK_8; //pawn promotion by 1 move forward
        possibility = pawnMoves &~ (pawnMoves - 1);
        while(possibility != 0){
            int index = Long.numberOfTrailingZeros(possibility);
            list += "" + (index % 8) + (index % 8) + "qP" + (index % 8) + (index % 8) + "rP" +
                    (index % 8) + (index % 8) + "bP" + (index % 8) + (index % 8) + "nP";
            pawnMoves &=~ possibility;
            possibility = pawnMoves &~ (pawnMoves - 1);
        }

        return list;
    }

    private static String possibleN(long bitboardKnight){
        String list="";
        long i = bitboardKnight &~ (bitboardKnight - 1);
        long possibility;
        while(i != 0){
            int location = Long.numberOfTrailingZeros(i);
            if(location > 18){
                possibility = KNIGHT_SPAN << (location - 18);
            }else{
                possibility = KNIGHT_SPAN >> (18 - location);
            }

            if(location % 8 < 4){
                possibility &=~ FILE_GH & NOT_MY_PIECES;
            }else{
                possibility &=~ FILE_AB & NOT_MY_PIECES;
            }

            long j = possibility &~ (possibility - 1);
            while(j != 0){
                int index = Long.numberOfTrailingZeros(j);
                list += "" + (location / 8) + (location % 8) + (index / 8) + (index % 8);
                possibility &=~ j;
                j = possibility &~ (possibility - 1);
            }

            bitboardKnight &=~ i;
            i = bitboardKnight &~ (bitboardKnight - 1);
        }
        return list;
    }

    private static String possibleB(long bitboardBishop){
        String list="";
        long i = bitboardBishop &~ (bitboardBishop-1);
        long possibility;
        while(i != 0){
            int location = Long.numberOfTrailingZeros(i);
            possibility = diagonalAndAntidiagonalMoves(location) & NOT_MY_PIECES;
            long j = possibility &~ (possibility - 1);
            while(j != 0){
                int index = Long.numberOfTrailingZeros(j);
                list += "" + (location / 8) + (location % 8) + (index / 8) + (index % 8);
                possibility &=~ j;
                j = possibility &~ (possibility - 1);
            }
            bitboardBishop &=~ i;
            i = bitboardBishop &~ (bitboardBishop - 1);
        }
        return list;
    }

    private static String possibleR(long bitboardRook){
        String list = "";
        long i = bitboardRook &~ (bitboardRook - 1);
        long possibility;
        while(i != 0){
            int location = Long.numberOfTrailingZeros(i);
            possibility = horizontalAndVerticalMoves(location) & NOT_MY_PIECES;
            long j = possibility& ~ (possibility - 1);
            while(j != 0){
                int index = Long.numberOfTrailingZeros(j);
                list += "" + (location / 8) + (location % 8) + (index / 8) + (index % 8);
                possibility &=~ j;
                j = possibility &~ (possibility - 1);
            }
            bitboardRook &=~ i;
            i = bitboardRook &~ (bitboardRook - 1);
        }
        return list;
    }

    private static String possibleQ(long bitboardQueen){
        String list = "";
        long i = bitboardQueen &~ (bitboardQueen - 1);
        long possibility;
        while(i != 0){
            int location = Long.numberOfTrailingZeros(i);
            possibility = (horizontalAndVerticalMoves(location) | diagonalAndAntidiagonalMoves(location)) & NOT_MY_PIECES;
            long j = possibility &~ (possibility - 1);
            while(j != 0){
                int index = Long.numberOfTrailingZeros(j);
                list += "" + (location / 8) + (location % 8) + (index / 8) + (index % 8);
                possibility &=~ j;
                j = possibility &~ (possibility - 1);
            }
            bitboardQueen &=~ i;
            i = bitboardQueen &~ (bitboardQueen - 1);
        }
        return list;
    }

    private static String possibleK(long bitboardKing){
        String list = "";
        long possibility;
        int location = Long.numberOfTrailingZeros(bitboardKing);
        if(location > 9){
            possibility = KING_SPAN << (location - 9);
        }else{
            possibility = KING_SPAN >> (9 - location);
        }

        if(location % 8 < 4){
            possibility &=~ FILE_GH & NOT_MY_PIECES;
        }else{
            possibility &=~ FILE_AB & NOT_MY_PIECES;
        }

        long j = possibility &~ (possibility - 1);
        while(j != 0){
            int index = Long.numberOfTrailingZeros(j);
            list += "" + (location / 8) + (location % 8) + (index / 8) + (index % 8);
            possibility &=~ j;
            j = possibility &~ (possibility - 1);
        }
        return list;
    }

    public static String generateMovesForPiece(char type){
        String moves;

        if(type == 'p' || type == 'r' || type == 'n' || type == 'b' || type == 'q' || type == 'k'){
            moves = possibleBlackMovesAll("", Bitboards.WP, Bitboards.WN, Bitboards.WB, Bitboards.WR, Bitboards.WQ,
                    Bitboards.WK, Bitboards.BP, Bitboards.BN, Bitboards.BB, Bitboards.BR, Bitboards.BQ, Bitboards.BK);
        }else{
            moves = possibleWhiteMovesAll("", Bitboards.WP, Bitboards.WN, Bitboards.WB, Bitboards.WR, Bitboards.WQ,
                    Bitboards.WK, Bitboards.BP, Bitboards.BN, Bitboards.BB, Bitboards.BR, Bitboards.BQ, Bitboards.BK);
        }

        return moves;
    }

    private static void debugDraw(long bitboard){
        String chessBoard[][] = new String[8][8];
        for (int i = 0; i < 64; i++){chessBoard[i/8][i%8]=" ";}
        for (int i = 0; i < 64; i++){if(((bitboard >>> i) & 1) == 1){chessBoard[i/8][i%8]="X";}}
        for (int i = 0; i < 8; i++){System.out.println(Arrays.toString(chessBoard[i]));}
    }

    public static long makeMove(long board, String move, char type){
        if(Character.isDigit(move.charAt(3))){ //regular move
            int start = (Character.getNumericValue(move.charAt(0)) * 8)
                    + (Character.getNumericValue(move.charAt(1)));
            int end = (Character.getNumericValue(move.charAt(2)) * 8)
                    + (Character.getNumericValue(move.charAt(3)));
            if(((board >>> start) & 1) == 1){
                board &=~ (1L << start);
                board |= (1L << end);
            }else{
                board &=~ (1L << end);
            }
        }else if(move.charAt(3) == 'P'){ //pawn promotion
            int start, end;
            if(Character.isUpperCase(move.charAt(2))){
                start = Long.numberOfTrailingZeros(FileMasks8[move.charAt(0) - '0'] & RankMasks8[6]);
                end = Long.numberOfTrailingZeros(FileMasks8[move.charAt(1) - '0'] & RankMasks8[7]);
            }else{
                start = Long.numberOfTrailingZeros(FileMasks8[move.charAt(0) - '0'] & RankMasks8[1]);
                end = Long.numberOfTrailingZeros(FileMasks8[move.charAt(1) - '0'] & RankMasks8[0]);
            }

            if(type == move.charAt(2)){
                board &=~ (1L << start);
                board |= (1L << end);
            }else{
                board &=~ (1L << end);
            }
        }
        return board;
    }

    public static String alphaBeta(long WP, long WN, long WB, long WR, long WQ,
                                   long WK, long BP, long BN, long BB, long BR, long BQ, long BK, int depth,
                                   boolean maxPlayer, int alpha, int beta, String move, boolean isWhite){

        String bestMove;
        String possibleMoves;

        if(depth == MAX_DEPTH){
            if(isWhite){
                return move + (Evaluation.eval(WP, WN, WB, WR, WQ, WK, isWhite) -
                        Evaluation.eval(BP, BN, BB, BR, BQ, BK, !isWhite));
            }else{
                return move + (Evaluation.eval(BP, BN, BB, BR, BQ, BK, isWhite) -
                        Evaluation.eval(WP, WN, WB, WR, WQ, WK, !isWhite));
            }
        }

        if(maxPlayer){
            int bestValue = MIN;
            bestMove = move;
            if(isWhite){
                possibleMoves = possibleWhiteMovesAll("", WP, WN, WB, WR, WQ, WK,
                        BP, BN, BB, BR, BQ, BK);
                if(possibleMoves.length() == 0){return move + MATE_SCORE;};
                for(int i = 0; i < possibleMoves.length(); i += 4){
                    long WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt;
                    WPt = makeMove(WP, possibleMoves.substring(i, i + 4), 'P');
                    WNt = makeMove(WN, possibleMoves.substring(i, i + 4), 'N');
                    WBt = makeMove(WB, possibleMoves.substring(i, i + 4), 'B');
                    WRt = makeMove(WR, possibleMoves.substring(i, i + 4), 'R');
                    WQt = makeMove(WQ, possibleMoves.substring(i, i + 4), 'Q');
                    WKt = makeMove(WK, possibleMoves.substring(i, i + 4), 'K');

                    BPt = makeMove(BP, possibleMoves.substring(i, i + 4), 'p');
                    BNt = makeMove(BN, possibleMoves.substring(i, i + 4), 'n');
                    BBt = makeMove(BB, possibleMoves.substring(i, i + 4), 'b');
                    BRt = makeMove(BR, possibleMoves.substring(i, i + 4), 'r');
                    BQt = makeMove(BQ, possibleMoves.substring(i, i + 4), 'q');
                    BKt = makeMove(BK, possibleMoves.substring(i, i + 4), 'k');

                    int value = Integer.valueOf(alphaBeta(WPt, WNt, WBt, WRt, WQt, WKt,
                            BPt, BNt, BBt, BRt, BQt, BKt, depth + 1, !maxPlayer, alpha, beta,
                            possibleMoves.substring(i, i + 4), !isWhite).substring(0, 4));
                    if(bestValue < value){bestValue = value; bestMove = possibleMoves.substring(i, i + 4);}
                    if(alpha <= bestValue){alpha = bestValue;}
                    if(beta >= alpha){break;}
                }
                return bestMove + bestValue;
            }else{
                possibleMoves = possibleBlackMovesAll("", WP, WN, WB, WR, WQ, WK,
                        BP, BN, BB, BR, BQ, BK);
                if(possibleMoves.length() == 0){return move + MATE_SCORE;};
                for(int i = 0; i < possibleMoves.length(); i += 4){
                    long WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt;
                    WPt = makeMove(WP, possibleMoves.substring(i, i + 4), 'P');
                    WNt = makeMove(WN, possibleMoves.substring(i, i + 4), 'N');
                    WBt = makeMove(WB, possibleMoves.substring(i, i + 4), 'B');
                    WRt = makeMove(WR, possibleMoves.substring(i, i + 4), 'R');
                    WQt = makeMove(WQ, possibleMoves.substring(i, i + 4), 'Q');
                    WKt = makeMove(WK, possibleMoves.substring(i, i + 4), 'K');

                    BPt = makeMove(BP, possibleMoves.substring(i, i + 4), 'p');
                    BNt = makeMove(BN, possibleMoves.substring(i, i + 4), 'n');
                    BBt = makeMove(BB, possibleMoves.substring(i, i + 4), 'b');
                    BRt = makeMove(BR, possibleMoves.substring(i, i + 4), 'r');
                    BQt = makeMove(BQ, possibleMoves.substring(i, i + 4), 'q');
                    BKt = makeMove(BK, possibleMoves.substring(i, i + 4), 'k');

                    int value = Integer.valueOf(alphaBeta(WPt, WNt, WBt, WRt, WQt, WKt,
                            BPt, BNt, BBt, BRt, BQt, BKt, depth + 1, !maxPlayer, alpha, beta,
                            possibleMoves.substring(i, i + 4), !isWhite).substring(0, 4));
                    if(bestValue < value){bestValue = value; bestMove = possibleMoves.substring(i, i + 4);}
                    if(alpha <= bestValue){alpha = bestValue;}
                    if(beta >= alpha){break;}
                }
                return bestMove + bestValue;
            }
        }else{
            int bestValue = MAX;
            bestMove = move;
            if(isWhite){
                possibleMoves = possibleWhiteMovesAll("", WP, WN, WB, WR, WQ, WK,
                        BP, BN, BB, BR, BQ, BK);
                if(possibleMoves.length() == 0){return move + (MATE_SCORE * -1);};
                for(int i = 0; i < possibleMoves.length(); i += 4){
                    long WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt;
                    WPt = makeMove(WP, possibleMoves.substring(i, i + 4), 'P');
                    WNt = makeMove(WN, possibleMoves.substring(i, i + 4), 'N');
                    WBt = makeMove(WB, possibleMoves.substring(i, i + 4), 'B');
                    WRt = makeMove(WR, possibleMoves.substring(i, i + 4), 'R');
                    WQt = makeMove(WQ, possibleMoves.substring(i, i + 4), 'Q');
                    WKt = makeMove(WK, possibleMoves.substring(i, i + 4), 'K');

                    BPt = makeMove(BP, possibleMoves.substring(i, i + 4), 'p');
                    BNt = makeMove(BN, possibleMoves.substring(i, i + 4), 'n');
                    BBt = makeMove(BB, possibleMoves.substring(i, i + 4), 'b');
                    BRt = makeMove(BR, possibleMoves.substring(i, i + 4), 'r');
                    BQt = makeMove(BQ, possibleMoves.substring(i, i + 4), 'q');
                    BKt = makeMove(BK, possibleMoves.substring(i, i + 4), 'k');

                    int value = Integer.valueOf(alphaBeta(WPt, WNt, WBt, WRt, WQt, WKt,
                            BPt, BNt, BBt, BRt, BQt, BKt, depth + 1, !maxPlayer, alpha, beta,
                            possibleMoves.substring(i, i + 4), !isWhite).substring(0, 4));
                    if(bestValue > value){bestValue = value; bestMove = possibleMoves.substring(i, i + 4);}
                    if(beta >= bestValue){beta = bestValue;}
                    if(beta >= alpha){break;}
                }
                return bestMove + bestValue;
            }else{
                possibleMoves = possibleBlackMovesAll("", WP, WN, WB, WR, WQ, WK,
                        BP, BN, BB, BR, BQ, BK);
                if(possibleMoves.length() == 0){return move + (MATE_SCORE * -1);};
                for(int i = 0; i < possibleMoves.length(); i += 4){
                    long WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt;
                    WPt = makeMove(WP, possibleMoves.substring(i, i + 4), 'P');
                    WNt = makeMove(WN, possibleMoves.substring(i, i + 4), 'N');
                    WBt = makeMove(WB, possibleMoves.substring(i, i + 4), 'B');
                    WRt = makeMove(WR, possibleMoves.substring(i, i + 4), 'R');
                    WQt = makeMove(WQ, possibleMoves.substring(i, i + 4), 'Q');
                    WKt = makeMove(WK, possibleMoves.substring(i, i + 4), 'K');

                    BPt = makeMove(BP, possibleMoves.substring(i, i + 4), 'p');
                    BNt = makeMove(BN, possibleMoves.substring(i, i + 4), 'n');
                    BBt = makeMove(BB, possibleMoves.substring(i, i + 4), 'b');
                    BRt = makeMove(BR, possibleMoves.substring(i, i + 4), 'r');
                    BQt = makeMove(BQ, possibleMoves.substring(i, i + 4), 'q');
                    BKt = makeMove(BK, possibleMoves.substring(i, i + 4), 'k');

                    int value = Integer.valueOf(alphaBeta(WPt, WNt, WBt, WRt, WQt, WKt,
                            BPt, BNt, BBt, BRt, BQt, BKt, depth + 1, !maxPlayer, alpha, beta,
                            possibleMoves.substring(i, i + 4), !isWhite).substring(0, 4));
                    if(bestValue >= value){bestValue = value; bestMove = possibleMoves.substring(i, i + 4);}
                    if(beta >= bestValue){beta = bestValue;}
                    if(beta >= alpha){break;}
                }
                return bestMove + bestValue;
            }
        }
    }

    public static void makeMoveOnBitboards(String move){
        Bitboards.WP = makeMove(Bitboards.WP, move, 'P');
        Bitboards.WR = makeMove(Bitboards.WR, move, 'R');
        Bitboards.WN = makeMove(Bitboards.WN, move, 'N');
        Bitboards.WB = makeMove(Bitboards.WB, move, 'B');
        Bitboards.WQ = makeMove(Bitboards.WQ, move, 'Q');
        Bitboards.WK = makeMove(Bitboards.WK, move, 'K');

        Bitboards.BP = makeMove(Bitboards.BP, move, 'P');
        Bitboards.BR = makeMove(Bitboards.BR, move, 'R');
        Bitboards.BN = makeMove(Bitboards.BN, move, 'N');
        Bitboards.BB = makeMove(Bitboards.BB, move, 'B');
        Bitboards.BQ = makeMove(Bitboards.BQ, move, 'Q');
        Bitboards.BK = makeMove(Bitboards.BK, move, 'K');
    }
}
