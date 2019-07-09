package com.example.kri.chess;

import android.content.Context;

import static android.opengl.GLES20.*;

import java.util.ArrayList;
import java.util.List;
import static com.example.kri.chess.Constants.BLACK_CONSTANT;
import static com.example.kri.chess.Constants.WHITE_CONSTANT;

public class ChessBoard{

    private List<float[]> meshWhite;
    private List<float[]> meshBlack;
    private float[] arrayWhite;
    private float[] arrayBlack;
    private VertexArray dataWhite;
    private VertexArray dataBlack;
    private int counter;

    public ChessBoard(Context context){
        meshWhite = new ArrayList<>();
        meshBlack = new ArrayList<>();
        meshBlack = ObjMtlParser.getMesh(context, "board_black.obj");
        meshWhite = ObjMtlParser.getMesh(context, "board_white.obj");
        counter = 0;
        arrayWhite = new float[6912];
        arrayBlack = new float[6912];
        setUpArrayFromMeshBlack();
        setUpArrayFromMeshWhite();
        dataWhite = new VertexArray(arrayWhite);
        dataBlack = new VertexArray(arrayBlack);
    }

    public void draw(ColorShaderProgram colorShaderProgram){

        glUniform4f(colorShaderProgram.getColorAttributeLocation(), WHITE_CONSTANT, WHITE_CONSTANT, WHITE_CONSTANT, 1.0f);
        dataWhite.setVertexAttribPointer(0, colorShaderProgram.getPositionAttributeLocation(), 3, 24);
        dataWhite.setVertexAttribPointer(3, colorShaderProgram.getNormalAttributeLocation(), 3, 24);
        glDrawArrays(GL_TRIANGLES, 0, arrayWhite.length/6);
        colorShaderProgram.setAmbientAttribute(0.01f);

        glUniform4f(colorShaderProgram.getColorAttributeLocation(), BLACK_CONSTANT, BLACK_CONSTANT, BLACK_CONSTANT, 1.0f);
        dataBlack.setVertexAttribPointer(0, colorShaderProgram.getPositionAttributeLocation(), 3, 24);
        dataBlack.setVertexAttribPointer(3, colorShaderProgram.getNormalAttributeLocation(), 3, 24);
        glDrawArrays(GL_TRIANGLES, 0, arrayBlack.length/6);
        colorShaderProgram.setAmbientAttribute(0.01f);
    }

    private void setUpArrayFromMeshBlack(){
        counter = 0;
        for(int i = 0; i < meshBlack.size(); i++){
            for(int j = 0; j < meshBlack.get(i).length; j++){
                arrayBlack[counter++] = meshBlack.get(i)[j];
            }
        }
    }

    private void setUpArrayFromMeshWhite(){
        counter = 0;
        for(int i = 0; i < meshWhite.size(); i++){
            for(int j = 0; j < meshWhite.get(i).length; j++){
                arrayWhite[counter++] = meshWhite.get(i)[j];
            }
        }
    }
}
