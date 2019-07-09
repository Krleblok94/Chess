package com.example.kri.chess;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniform4f;
import static com.example.kri.chess.Constants.BLACK_PIECE;
import static com.example.kri.chess.Constants.WHITE_PIECE;

public class Rook{
    private List<float[]> rookMesh;
    private VertexArray rookData;
    public Rook(Context context){
        rookMesh = new ArrayList<>();
        rookMesh = ObjMtlParser.getMesh(context, "rook.obj");
        rookData = new VertexArray(rookMesh.get(0));
    }

    public void draw(ColorShaderProgram colorShaderProgram, int color){
        if(color == 0){
            glUniform4f(colorShaderProgram.getColorAttributeLocation(), WHITE_PIECE, WHITE_PIECE, WHITE_PIECE, 1.0f);
            colorShaderProgram.setAmbientAttribute(0.01f);
        }else{
            glUniform4f(colorShaderProgram.getColorAttributeLocation(), BLACK_PIECE, BLACK_PIECE, BLACK_PIECE, 1.0f);
            colorShaderProgram.setAmbientAttribute(0.1f);
        }
        rookData.setVertexAttribPointer(0, colorShaderProgram.getPositionAttributeLocation(), 3, 24);
        rookData.setVertexAttribPointer(12, colorShaderProgram.getNormalAttributeLocation(), 3, 24);
        glDrawArrays(GL_TRIANGLES, 0, rookMesh.get(0).length/6);
    }
}
