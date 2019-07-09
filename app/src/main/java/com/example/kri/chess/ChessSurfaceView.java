package com.example.kri.chess;

import android.content.Context;
import android.opengl.GLSurfaceView;


public class ChessSurfaceView extends GLSurfaceView{
    public ChessSurfaceView(Context context){
        super(context);
        setEGLContextClientVersion(2);
    }
}
