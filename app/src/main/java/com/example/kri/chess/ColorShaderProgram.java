package com.example.kri.chess;

import android.content.Context;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class ColorShaderProgram extends ShaderProgram{

    // Uniform locations
    private final int uMatrixLocation;
    // Attribute locations
    private final int aPositionLocation;
    private final int uColorLocation;
    private final int aNormalLocation;
    private final int uAmbientLocation;

    public ColorShaderProgram(Context context){
        super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);
        // Retrieve uniform locations for the shader program.
        uMatrixLocation = glGetUniformLocation(mProgram, U_MATRIX);
        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(mProgram, A_POSITION);
        uColorLocation = glGetUniformLocation(mProgram, "u_Color");
        aNormalLocation = glGetAttribLocation(mProgram, "a_Normals");
        uAmbientLocation = glGetUniformLocation(mProgram, "u_Ambient");
    }

    public void setUniforms(float[] matrix){glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);}
    public int getPositionAttributeLocation(){return aPositionLocation;}
    public int getColorAttributeLocation(){return uColorLocation;}
    public int getNormalAttributeLocation(){return aNormalLocation;}
    public void setVectorToLight(Geometry.Vector vectorToLight){
        glUniform3f(glGetUniformLocation(mProgram, "u_VectorToLight"),
                vectorToLight.x, vectorToLight.y, vectorToLight.z);
    }
    public void setAmbientAttribute(float ambient){
        glUniform1f(uAmbientLocation, ambient);
    }

}
