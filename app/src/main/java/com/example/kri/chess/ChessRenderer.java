package com.example.kri.chess;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.kri.chess.Geometry.*;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CCW;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glFrontFace;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;
import static com.example.kri.chess.Constants.*;

import static java.lang.Math.*;

public class ChessRenderer implements GLSurfaceView.Renderer{

    private Context context;
    private float[] mProjectionMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mViewProjectionMatrix = new float[16];
    private float[] mModelViewProjectionMatrix = new float[16];
    private float[] mInvertedViewProjectionMatrix = new float[16];
    private float prevX, prevY;
    private float pitch = 52, yaw = -90;
    private Vector cameraPos;
    private float radius;

    private ColorShaderProgram program;
    private ChessManager manager;
    private VertexArray availibleSquaresMesh;
    private boolean activateAvailibleSquares;
    private int meshLength;
    private static List<Integer> matchedIndexesForAvailibleSquares;
    private PieceInfo pressedPiece;

    private String from, to;
    private String playerColor;
    public static boolean playersTurn;

    public ChessRenderer(Context context){
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config){
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_BUFFER_BIT | GL_DEPTH_TEST);
        glEnable (GL_BLEND);
        glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClearColor(0.5f, 0.5f, 0.5f, 1f);
        program = new ColorShaderProgram(context);
        manager = new ChessManager(context);
        manager.initSquaresCenters();
        manager.initSquaresCorners();
        manager.initBoardSetup();
        program.useProgram();
        radius = VIEW_SPHERE_RADIUS;
        cameraPos = EAngleToVector(pitch, yaw).scale(radius);
        activateAvailibleSquares = false;
        matchedIndexesForAvailibleSquares = new ArrayList<>();
        pressedPiece = null;
        playerColor = "white";
        playersTurn = true;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height){
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(mProjectionMatrix, 45, (float) width / (float) height, 0.1f, 100f);
        setLookAtM(mViewMatrix, 0, cameraPos.x, cameraPos.y, cameraPos.z, 0f, 0f, 0f, 0f, 1f, 0f);
        program.setVectorToLight(new Vector(0,1,0).normalize());
    }

    @Override
    public void onDrawFrame(GL10 gl){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glFrontFace(GL_CCW);
        multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        invertM(mInvertedViewProjectionMatrix, 0, mViewProjectionMatrix, 0);
        program.setUniforms(mViewProjectionMatrix); //only for the board use this matrix combination, model matrix not necessary

        program.setAmbientAttribute(0.5f);
        manager.getBoard().draw(program);
        program.setAmbientAttribute(0f);

        for(PieceInfo piece : manager.getPieces()){
            positionObject(piece);
            program.setUniforms(mModelViewProjectionMatrix);
            switch (piece.getType()){
                case 'P':
                    manager.getPawn().draw(program, 0);
                    break;
                case 'R':
                    manager.getRook().draw(program, 0);
                    break;
                case 'N':
                    manager.getKnight().draw(program, 0);
                    break;
                case 'B':
                    manager.getBishop().draw(program, 0);
                    break;
                case 'Q':
                    manager.getQueen().draw(program, 0);
                    break;
                case 'K':
                    manager.getKing().draw(program, 0);
                    break;
                case 'p':
                    manager.getPawn().draw(program, 1);
                    break;
                case 'r':
                    manager.getRook().draw(program, 1);
                    break;
                case 'n':
                    manager.getKnight().draw(program, 1);
                    break;
                case 'b':
                    manager.getBishop().draw(program, 1);
                    break;
                case 'q':
                    manager.getQueen().draw(program, 1);
                    break;
                case 'k':
                    manager.getKing().draw(program, 1);
                    break;
            }

        }

        drawAvailibleMesh();

        if(!playersTurn) {
            manager.makeComputerMove();
        }
    }

    private void positionObject(PieceInfo info){
        setIdentityM(mModelMatrix, 0);
        translateM(mModelMatrix, 0, info.getCoordX(), 0f, info.getCoordZ());
        if(info.getType() == 'P' || info.getType() == 'R' || info.getType() == 'N' || info.getType() == 'B' ||
                info.getType() == 'Q' || info.getType() == 'K'){
            rotateM(mModelMatrix, 0, 180f, 0, 1, 0);
        }
        multiplyMM(mModelViewProjectionMatrix, 0, mViewProjectionMatrix, 0, mModelMatrix, 0);
    }

    public void handleTouchEvent(float x, float y){
        if(playersTurn){
            Ray ray = convertNormalized2DPointToRay(x, y);
            for(PieceInfo piece : manager.getPieces()){
                ChessSquare sq = manager.getSquares()[piece.getIndex()];
                if(piece.getColor() == playerColor){
                    Sphere sphere = new Sphere(new Point(sq.getX(), piece.getHeight(), sq.getZ()), Constants.SQUARE_RADIUS);
                    if(Geometry.intersects(sphere, ray)){
                        pressedPiece = piece;
                        float[] mesh = manager.getAvailibleSquaresMesh(pressedPiece.getIndex(), pressedPiece.getType());
                        meshLength = mesh.length;
                        availibleSquaresMesh = new VertexArray(mesh);
                        activateAvailibleSquares = true;
                        from = manager.indicesTable[pressedPiece.getIndex()];
                        break;
                    }
                }
            }

            if(activateAvailibleSquares){
                if(matchedIndexesForAvailibleSquares.size() > 0){
                    for(int i : matchedIndexesForAvailibleSquares){
                        ChessSquare sq = manager.getSquares()[i];
                        Sphere sphere = new Sphere(new Point(sq.getX(), 0.5f, sq.getZ()), Constants.SQUARE_RADIUS);
                        if(Geometry.intersects(sphere, ray)){
                            to = manager.indicesTable[i];
                            manager.makeMove(from + to);
                            pressedPiece = null;
                            activateAvailibleSquares = false;
                            availibleSquaresMesh = null;
                            matchedIndexesForAvailibleSquares.clear();
                            playersTurn = !playersTurn;
                            meshLength = 0;
                            break;
                        }
                    }
                }
            }

            prevX = x;
            prevY = y;
        }
    }

    public static void setMatchedIndexesForAvailibleSquares(List<Integer> list){
        matchedIndexesForAvailibleSquares.clear();
        for(Integer i : list){
            matchedIndexesForAvailibleSquares.add(i);
        }
    }

    public void handleDragEvent(float x, float y){
        if(playersTurn){
            pressedPiece = null;
            activateAvailibleSquares = false;
            availibleSquaresMesh = null;
            matchedIndexesForAvailibleSquares.clear();
            meshLength = 0;

            float deltaX = (x - prevX);
            float deltaY = (y - prevY);

            pitch -= deltaY * 80;
            yaw += deltaX * 80;

            prevX = x;
            prevY = y;

            normalize();

            cameraPos = EAngleToVector(pitch, yaw).scale(radius);
            setLookAtM(mViewMatrix, 0, cameraPos.x, cameraPos.y, cameraPos.z, 0f, 0f, 0f, 0f, 1f, 0f);
        }
    }

    private void drawAvailibleMesh(){
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        if(activateAvailibleSquares){
            program.setUniforms(mViewProjectionMatrix);
            availibleSquaresMesh.setVertexAttribPointer(0, program.getPositionAttributeLocation(), 3, 0);
            glUniform4f(program.getColorAttributeLocation(), 0, 1, 0, 0.7f);
            program.setAmbientAttribute(0.5f);
            glDrawArrays(GL_TRIANGLES, 0, meshLength/3);
        }
        glDisable(GL_BLEND);
    }

    private Geometry.Ray convertNormalized2DPointToRay(float x, float y){
        final float[] nearPointNdc = {x, y, -1, 1};
        final float[] farPointNdc = {x, y, 1, 1};

        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        multiplyMV(nearPointWorld, 0, mInvertedViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(farPointWorld, 0, mInvertedViewProjectionMatrix, 0, farPointNdc, 0);

        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Point nearPointRay = new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Point farPointRay = new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
        return new Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
    }

    private void divideByW(float[] vector){
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

    private float toRadians(float angle){
        return angle * RADIAN_CONST;
    }

    private Vector EAngleToVector(float pitch, float yaw){
        float x = (float)(cos(toRadians(yaw))*cos(toRadians(pitch)));
        float y = (float)(sin(toRadians(pitch)));
        float z = (float)(sin(toRadians(yaw))*cos(toRadians(pitch)));
        return new Vector(x, y, z);
    }

    private void normalize(){
        if(pitch > 75){pitch = 75;}
        if(pitch < 15){pitch = 15;}
        while(yaw < -180){yaw += 360;}
        while(yaw > 180){yaw -= 360;}
    }
}